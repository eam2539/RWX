package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.librocket.scripts.ScriptEngine
import com.corrodinggames.rts.game.PlayerTeam
import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.GameModeType
import com.corrodinggames.rts.gameFramework.network.NetworkEngine
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.libp2p.core.Host
import io.libp2p.core.PeerInfo
import io.libp2p.core.dsl.HostBuilder
import io.libp2p.core.multiformats.Multiaddr
import io.libp2p.core.pubsub.MessageApi
import io.libp2p.core.pubsub.Topic
import io.libp2p.discovery.MDnsDiscovery
import io.libp2p.pubsub.gossip.Gossip
import io.libp2p.pubsub.gossip.builders.GossipRouterBuilder
import io.netty.buffer.Unpooled
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class P2PLobbyService private constructor() {
    companion object {
        val IPFS_BOOTSTRAP_PEERS = mutableListOf(
            "/dnsaddr/bootstrap.libp2p.io/ipfs/QmNnooDu7bfjPFoTZYxMNLWUQJyrVwtbZg5gBMjTezGAJN",
            "/dnsaddr/bootstrap.libp2p.io/ipfs/QmQCU2EcMqAqQPR2i9bChDtGNJchTbq5TbXJJ16u19uLTa",
            "/ip4/104.131.131.82/tcp/4001/ipfs/QmaCpDMGvV2BGHeYERUEnRQAwe3N8SzbUtfsmvsqQLuvuJ",
            "/ip4/104.236.179.241/tcp/4001/ipfs/QmSoLPppuBtQSGwKDZT2M73ULpjvfd3aZ6ha4oFGL1KrGM",
            "/ip4/128.199.219.111/tcp/4001/ipfs/QmSoLSafTMBsPKadTEgaXctDQVcqN88CNLHXMkTNwMKPnu"
        )

        private const val LOBBY_TOPIC = "rwx.p2p.lobby.v1"
        private const val ROOM_ANNOUNCE_INTERVAL_MS = 3000L
        private const val ROOM_TTL_MS = 15000L
        private val singleton: P2PLobbyService = P2PLobbyService()

        @JvmStatic
        fun getInstance(): P2PLobbyService = singleton
    }

    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val discoveredRooms = ConcurrentHashMap<String, P2PRoomAdvertisement>()
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val lobbyTopic = Topic(LOBBY_TOPIC)

    private var gossip: Gossip? = null
    private var host: Host? = null
    private var mDnsDiscovery: MDnsDiscovery? = null
    private var started = false
    private var bootstrapPeersText = ""
    private var hostedRoom: P2PRoomAdvertisement? = null
    private var joinedRoom: P2PRoomAdvertisement? = null
    private var libp2pProxy: Libp2pStreamProxy = Libp2pStreamProxy()

    @Synchronized
    @Throws(IOException::class)
    fun startIfNeeded(bootstrapPeers: String?) {
        if (!started) {
            val gossipProtocol = Gossip(GossipRouterBuilder().build())
            gossip = gossipProtocol
            host = HostBuilder().listen("/ip4/0.0.0.0/tcp/0").protocol(gossipProtocol).build().also {
                it.addConnectionHandler(gossipProtocol)
                runCatching {
                    it.start().get(15L, TimeUnit.SECONDS)
                }.onFailure { e ->
                    throw IOException("Failed to start libp2p host", e)
                }
            }
            gossipProtocol.subscribe(this::handleMessage, lobbyTopic)
            val currentHost = host ?: throw IOException("Libp2p host was not created")
            mDnsDiscovery =
                MDnsDiscovery(currentHost, MDnsDiscovery.ServiceTagLocal, MDnsDiscovery.QueryInterval, null).also {
                    it.addHandler(this::handleMdnsPeerFound)
                    it.start()
                }
            scheduler.scheduleAtFixedRate(::tick, 1000L, ROOM_ANNOUNCE_INTERVAL_MS, TimeUnit.MILLISECONDS)
            started = true
        }
        if (bootstrapPeers != null) {
            bootstrapPeersText = bootstrapPeers
            savePeerConfig(bootstrapPeersText)
        }
        connectBootstrapPeers(parseLibp2pPeers(bootstrapPeersText))
    }

    fun getSavedPeerConfig(): String {
        val gameEngine = GameEngine.getInstance()
        return gameEngine.settingsEngine.p2pLibp2pPeers ?: ""
    }

    fun savePeerConfig(libp2pPeers: String) {
        val gameEngine = GameEngine.getInstance()
        gameEngine.settingsEngine.p2pLibp2pPeers = libp2pPeers.trim()
        gameEngine.settingsEngine.save()
    }

    @Synchronized
    @Throws(IOException::class)
    fun hostCurrentServer(libp2pPeers: String?) {
        ensureStarted()
        val gameEngine = GameEngine.getInstance()
        val room = P2PRoomAdvertisement().apply {
            roomId = UUID.randomUUID().toString()
            hostPeerId = host!!.peerId.toBase58()
            createdBy = gameEngine.networkEngine.playerName
            libp2pBootstrapPeers = parseLibp2pPeers(libp2pPeers).toMutableList().also {
                it += IPFS_BOOTSTRAP_PEERS
            }
        }
        updateRoomFromNetworkState(room)

        GameEngine.log("Starting libp2p stream proxy for game on port ${gameEngine.settingsEngine.networkPort}")
        libp2pProxy.startHostSide(host!!, gameEngine.settingsEngine.networkPort)
        
        hostedRoom = room
        joinedRoom = null
        discoveredRooms[room.roomId!!] = room
        publishRoom(room)
        queueRoomListRefresh()
    }

    @Synchronized
    @Throws(IOException::class)
    fun prepareJoin(roomId: String): String {
        ensureStarted()
        val room = findRoom(roomId) ?: throw IOException("That P2P room is no longer available")

        GameEngine.log("Starting libp2p stream proxy to connect to host")
        val hostPeerId = room.hostPeerId ?: throw IOException("Missing host peer ID")

        val localPort = libp2pProxy.startClientSide(
            host!!,
            hostPeerId,
            localPort = 0  // 自动选择可用端口
        )
        
        joinedRoom = room

        val connectAddress = "127.0.0.1:$localPort"
        GameEngine.log("Game client should connect to: $connectAddress (libp2p tunnel)")

        return connectAddress
    }

    @Synchronized
    fun stopSession() {
        hostedRoom?.let { room ->
            publishRoom(
                P2PRoomAdvertisement(
                    magic = room.magic,
                    schema = room.schema,
                    type = "room_close",
                    roomId = room.roomId,
                    hostPeerId = room.hostPeerId,
                )
            )
            discoveredRooms.remove(room.roomId)
        }
        hostedRoom = null
        joinedRoom = null
        libp2pProxy.stop()
        queueRoomListRefresh()
    }

    @Synchronized
    @Throws(IOException::class)
    fun requestRefresh() {
        ensureStarted()
        connectBootstrapPeers(parseLibp2pPeers(bootstrapPeersText))
        queueRoomListRefresh()
    }

    fun getRooms(): ArrayList<P2PRoomAdvertisement> {
        val now = System.currentTimeMillis()
        return discoveredRooms.values
            .filterNot { it.isExpired(now) }
            .sortedWith(compareBy<P2PRoomAdvertisement> { it.gameState ?: "" }.thenBy { it.createdBy ?: "" })
            .toCollection(ArrayList())
    }

    @Synchronized
    fun findRoom(roomId: String): P2PRoomAdvertisement? = discoveredRooms[roomId]

    private fun ensureStarted() {
        if (!started) {
            throw IOException("P2P lobby has not been started")
        }
    }

    private fun tick() {
        try {
            pruneExpiredRooms()
            hostedRoom?.let {
                updateRoomFromNetworkState(it)
                publishRoom(it)
            }
        } catch (e: Exception) {
            GameEngine.log("P2P tick failed: ${e.message}")
        }
    }

    private fun pruneExpiredRooms() {
        val now = System.currentTimeMillis()
        var removed = false
        val iterator = discoveredRooms.entries.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().value.isExpired(now)) {
                iterator.remove()
                removed = true
            }
        }
        if (removed) {
            queueRoomListRefresh()
        }
    }

    private fun updateRoomFromNetworkState(room: P2PRoomAdvertisement) {
        val gameEngine = GameEngine.getInstance()
        val networkEngine: NetworkEngine = gameEngine.networkEngine
        room.apply {
            type = "room_announce"
            createdBy = networkEngine.playerName
            gameVersionCode = gameEngine.getVersionCode(true)
            gameVersionString = gameEngine.getVersionName()
            requiresPassword = networkEngine.n != null
            gamePort = gameEngine.settingsEngine.networkPort
            mapPath = networkEngine.roomSettings.mapPath
            gameMode = networkEngine.roomSettings.gameModeType?.name ?: GameModeType.values()[0].name
            gameState = when {
                networkEngine.v -> "chat"
                networkEngine.gameHasBeenStarted -> "ingame"
                networkEngine.roomSettings.roomLock -> "locked"
                else -> "battleroom"
            }
            currentPlayers = networkEngine.getChangeableRoomSettings()
            maxPlayers = PlayerTeam.TEAM_NEUTRAL
            hasMods = networkEngine.o
            modsRequired = if (networkEngine.o) "Active mods required" else ""
            seq += 1
            lastSeenTimeMs = System.currentTimeMillis()
            expiresAtMs = room.lastSeenTimeMs + ROOM_TTL_MS
        }
    }

    private fun publishRoom(room: P2PRoomAdvertisement) {
        if (!started) return
        try {
            gossip?.createPublisher(null, System.currentTimeMillis())
                ?.publish(Unpooled.wrappedBuffer(objectMapper.writeValueAsBytes(room)), lobbyTopic)
        } catch (e: Exception) {
            GameEngine.log("Failed to publish P2P room: ${e.message}")
        }
    }

    private fun handleMessage(messageApi: MessageApi) {
        try {
            val data = messageApi.data
            val bytes = ByteArray(data.readableBytes())
            data.getBytes(data.readerIndex(), bytes)
            val room = objectMapper.readValue(bytes, P2PRoomAdvertisement::class.java)
            if (!room.isValid()) return
            if (host?.peerId?.toBase58() == room.hostPeerId && hostedRoom?.roomId == room.roomId) return
            if (room.type == "room_close") {
                if (discoveredRooms.remove(room.roomId) != null) {
                    queueRoomListRefresh()
                }
                return
            }
            room.lastSeenTimeMs = System.currentTimeMillis()
            val previous = discoveredRooms[room.roomId]
            if (previous == null || previous.seq <= room.seq) {
                discoveredRooms[room.roomId!!] = room
                queueRoomListRefresh()
            }
        } catch (e: Exception) {
            GameEngine.log("Failed to decode P2P room: ${e.message}")
        }
    }

    private fun queueRoomListRefresh() {
        ScriptEngine.getInstance()?.addScriptToQueue("displayP2PServerList()")
    }

    private fun connectBootstrapPeers(peers: List<String>) {
        if (!started) return
        val currentHost = host ?: return
        peers.forEach { peer ->
            try {
                currentHost.network.connect(Multiaddr(peer))
            } catch (e: Exception) {
                GameEngine.log("Failed bootstrap connect: $peer - ${e.message}")
            }
        }
    }

    private fun handleMdnsPeerFound(peerInfo: PeerInfo) {
        try {
            val currentHost = host ?: return
            if (peerInfo.addresses.isNotEmpty()) {
                currentHost.addressBook.addAddrs(peerInfo.peerId, 30000L, *peerInfo.addresses.toTypedArray())
                currentHost.network.connect(peerInfo.peerId, *peerInfo.addresses.toTypedArray())
            }
        } catch (e: Exception) {
            GameEngine.log("Failed mDNS connect: ${e.message}")
        }
    }

    private fun parseList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(Regex("[\\n,; ]+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    }

    private fun parseLibp2pPeers(value: String?): List<String> {
        return parseList(value).filter { it.startsWith("/") }
    }

}
