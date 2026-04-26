package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.librocket.scripts.ScriptEngine
import com.corrodinggames.rts.game.PlayerTeam
import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.file.FileHelper
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
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class P2PLobbyService private constructor() {
    companion object {
        private const val LOBBY_TOPIC = "rwx.p2p.lobby.v1"
        private const val ROOM_ANNOUNCE_INTERVAL_MS = 3000L
        private const val ROOM_TTL_MS = 15000L
        private val singleton: P2PLobbyService = P2PLobbyService()

        @JvmStatic
        fun getInstance(): P2PLobbyService = singleton
    }

    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val easyTierController = EasyTierController()
    private val discoveredRooms = ConcurrentHashMap<String, P2PRoomAdvertisement>()
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val lobbyTopic = Topic(LOBBY_TOPIC)

    private var gossip: Gossip? = null
    private var host: Host? = null
    private var mDnsDiscovery: MDnsDiscovery? = null
    private var started = false
    private var bootstrapPeersText = ""
    private var easyTierPeersText = ""
    private var hostedRoom: P2PRoomAdvertisement? = null
    private var joinedRoom: P2PRoomAdvertisement? = null

    fun getEasyTierStatusText(): String {
        return if (java.lang.Boolean.getBoolean("rwx.easytier.no_tun")) {
            "EasyTier: no-tun mode enabled. Discovery works, but virtual IP game traffic will not."
        } else {
            "EasyTier: requires TUN permissions. On Linux, grant CAP_NET_ADMIN to easytier-core."
        }
    }

    @Synchronized
    @Throws(IOException::class)
    fun startIfNeeded(bootstrapPeers: String?) {
        if (!started) {
            val gossipProtocol = Gossip(GossipRouterBuilder().build())
            gossip = gossipProtocol
            host = HostBuilder().listen("/ip4/0.0.0.0/tcp/0").protocol(gossipProtocol).build().also {
                it.addConnectionHandler(gossipProtocol)
                try {
                    it.start().get(15L, TimeUnit.SECONDS)
                } catch (e: Exception) {
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
            bootstrapPeersText = bootstrapPeers.trim()
            saveConfig(bootstrapPeersText, easyTierPeersText)
        }
        connectBootstrapPeers(parseLibp2pPeers(bootstrapPeersText))
    }

    fun getSavedPeerConfig(): Array<String> {
        val (libp2pPeers, easyTierPeers) = loadConfig()
        return arrayOf(libp2pPeers, easyTierPeers)
    }

    fun savePeerConfig(libp2pPeers: String, easyTierPeers: String) {
        bootstrapPeersText = libp2pPeers.trim()
        easyTierPeersText = easyTierPeers.trim()
        saveConfig(bootstrapPeersText, easyTierPeersText)
    }

    @Synchronized
    @Throws(IOException::class)
    fun hostCurrentServer(libp2pPeers: String?, easyTierPeers: String?) {
        ensureStarted()
        val gameEngine = GameEngine.getInstance()
        val room = P2PRoomAdvertisement().apply {
            roomId = UUID.randomUUID().toString()
            hostPeerId = host!!.peerId.toBase58()
            createdBy = gameEngine.networkEngine.playerName
            networkName = "rwx-${roomId!!.substring(0, 8)}"
            networkSecret = UUID.randomUUID().toString().replace("-", "")
            hostVirtualIp = generateVirtualIp("$roomId-host")
            libp2pBootstrapPeers = parseLibp2pPeers(libp2pPeers).toMutableList()
            this.easyTierPeers = parseEasyTierPeers(easyTierPeers).toMutableList()
        }
        updateRoomFromNetworkState(room)
        try {
            easyTierController.start(room, room.hostVirtualIp!!)
        } catch (e: IOException) {
            throw IOException("Failed to start EasyTier: ${e.message}", e)
        }
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
        val clientId = ensureClientId()
        try {
            easyTierController.start(room, generateVirtualIp("${room.roomId}-$clientId"))
        } catch (e: IOException) {
            throw IOException("Failed to start EasyTier: ${e.message}", e)
        }
        if (!easyTierController.waitForHost(
                room.hostVirtualIp ?: throw IOException("Missing host virtual IP"),
                room.gamePort,
                30000
            )
        ) {
            val message = easyTierController.lastLogLine()?.let { "Timed out waiting for EasyTier route to host\n$it" }
                ?: "Timed out waiting for EasyTier route to host"
            easyTierController.stop()
            throw IOException(message)
        }
        joinedRoom = room
        return room.getConnectAddress()
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
        easyTierController.stop()
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
            GameEngine.isInSpace("P2P tick failed: ${e.message}")
        }
    }

    private fun pruneExpiredRooms() {
        val now = System.currentTimeMillis()
        val removed = discoveredRooms.entries.removeIf { it.value.isExpired(now) }
        if (removed) {
            queueRoomListRefresh()
        }
    }

    private fun updateRoomFromNetworkState(room: P2PRoomAdvertisement) {
        val gameEngine = GameEngine.getInstance()
        val networkEngine: NetworkEngine = gameEngine.networkEngine
        room.type = "room_announce"
        room.createdBy = networkEngine.playerName
        room.gameVersionCode = gameEngine.getVersionCode(true)
        room.gameVersionString = gameEngine.getVersionName()
        room.requiresPassword = networkEngine.n != null
        room.gamePort = gameEngine.settingsEngine.networkPort
        room.mapPath = networkEngine.roomSettings.mapPath
        room.gameMode = networkEngine.roomSettings.gameModeType?.name ?: GameModeType.values()[0].name
        room.gameState = when {
            networkEngine.v -> "chat"
            networkEngine.gameHasBeenStarted -> "ingame"
            networkEngine.roomSettings.roomLock -> "locked"
            else -> "battleroom"
        }
        room.currentPlayers = networkEngine.getChangeableRoomSettings()
        room.maxPlayers = PlayerTeam.TEAM_NEUTRAL
        room.hasMods = networkEngine.o
        room.modsRequired = if (networkEngine.o) "Active mods required" else ""
        room.seq += 1
        room.lastSeenTimeMs = System.currentTimeMillis()
        room.expiresAtMs = room.lastSeenTimeMs + ROOM_TTL_MS
    }

    private fun publishRoom(room: P2PRoomAdvertisement) {
        if (!started) return
        try {
            gossip?.createPublisher(null, System.currentTimeMillis())
                ?.publish(Unpooled.wrappedBuffer(objectMapper.writeValueAsBytes(room)), lobbyTopic)
        } catch (e: Exception) {
            GameEngine.isInSpace("Failed to publish P2P room: ${e.message}")
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
            GameEngine.isInSpace("Failed to decode P2P room: ${e.message}")
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
                GameEngine.isInSpace("Failed bootstrap connect: $peer - ${e.message}")
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
            GameEngine.isInSpace("Failed mDNS connect: ${e.message}")
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

    private fun parseEasyTierPeers(value: String?): List<String> {
        return parseList(value).filter { it.contains("://") && !it.startsWith("/") }
    }

    private fun ensureClientId(): String {
        val gameEngine = GameEngine.getInstance()
        if (gameEngine.settingsEngine.networkClientId.isNullOrBlank()) {
            gameEngine.settingsEngine.networkClientId = UUID.randomUUID().toString()
            gameEngine.settingsEngine.save()
        }
        return gameEngine.settingsEngine.networkClientId
    }

    private fun generateVirtualIp(seed: String): String {
        val hash = kotlin.math.abs(seed.hashCode())
        val third = ((hash / 251) % 250) + 1
        val fourth = (hash % 250) + 1
        return "10.144.$third.$fourth"
    }

    private fun configPath(): String = FileHelper.convertAbstractPath("/SD/rustedWarfare/p2p_lobby.conf")

    private fun saveConfig(libp2pPeers: String, easyTierPeers: String) {
        try {
            File(configPath()).writeText("libp2p=$libp2pPeers\neasytier=$easyTierPeers\n")
        } catch (e: IOException) {
            GameEngine.isInSpace("Failed to save P2P config: ${e.message}")
        }
    }

    private fun loadConfig(): Pair<String, String> {
        return try {
            val file = File(configPath())
            if (!file.exists()) {
                "" to ""
            } else {
                var libp2pPeers = ""
                var easyTierPeers = ""
                file.readLines().forEach { line ->
                    when {
                        line.startsWith("libp2p=") -> libp2pPeers = line.removePrefix("libp2p=").trim()
                        line.startsWith("easytier=") -> easyTierPeers = line.removePrefix("easytier=").trim()
                        libp2pPeers.isEmpty() && !line.contains("=") -> libp2pPeers = line.trim()
                    }
                }
                libp2pPeers to easyTierPeers
            }
        } catch (e: IOException) {
            GameEngine.isInSpace("Failed to load P2P config: ${e.message}")
            "" to ""
        }
    }
}
