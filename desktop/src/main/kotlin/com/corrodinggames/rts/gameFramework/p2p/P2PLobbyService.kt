package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.librocket.scripts.ScriptEngine
import com.corrodinggames.rts.game.PlayerTeam
import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.GameModeType
import com.corrodinggames.rts.gameFramework.network.NetworkEngine
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.libp2p.core.Host
import io.libp2p.core.PeerId
import io.libp2p.core.PeerInfo
import io.libp2p.core.dsl.HostBuilder
import io.libp2p.core.multiformats.Multiaddr
import io.libp2p.core.pubsub.MessageApi
import io.libp2p.core.pubsub.Topic
import io.libp2p.discovery.MDnsDiscovery
import io.libp2p.pubsub.gossip.Gossip
import io.libp2p.pubsub.gossip.builders.GossipRouterBuilder
import io.libp2p.transport.tcp.TcpTransport
import io.netty.buffer.Unpooled
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class P2PLobbyService private constructor() {
    companion object {
        private const val LOBBY_TOPIC = "rwx.p2p.lobby.v1"
        private val singleton: P2PLobbyService = P2PLobbyService()

        @JvmStatic
        fun getInstance(): P2PLobbyService = singleton
    }

    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val discoveredRooms = ConcurrentHashMap<String, P2PRoomAdvertisement>()
    private val mdnsPeers = ConcurrentHashMap<String, Long>()
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val lobbyTopic = Topic(LOBBY_TOPIC)
    private val webrtcTopic = Topic("$LOBBY_TOPIC.webrtc")

    private var gossip: Gossip? = null
    private var host: Host? = null
    private var mDnsDiscovery: MDnsDiscovery? = null
    private var started = false
    private var p2pConfig: P2PConfig = P2PConfigLoader.load()
    private var hostedRoom: P2PRoomAdvertisement? = null
    private var joinedRoom: P2PRoomAdvertisement? = null
    private var libp2pProxy: Libp2pStreamProxy = Libp2pStreamProxy(p2pConfig.libp2pProxy)
    private var libp2pPortMapping: NatPortMapper.Mapping? = null
    private val natPortMapper = NatPortMapper()
    private var webRtcProxy = WebRtcTunnelProxy(p2pConfig.webRtcProxy)
    private var relaySupport: Libp2pRelaySupport.Components? = null
    private var serviceDiscovery = P2PServiceDiscoveryProvider(p2pConfig.discovery.service, objectMapper)
    private var servicePublisher = P2PServicePublisher(
        p2pConfig.discovery.service.publish,
        p2pConfig.discovery.service.urls,
        objectMapper
    )
    private var dhtDiscovery = P2PDhtDiscoveryProvider(p2pConfig.discovery.dht)
    private var bootstrapNodeSource = P2PBootstrapNodeSource(p2pConfig.libp2pPeerSources, objectMapper)
    private var lastServiceDiscoveryMs = 0L
    private var lastDhtDiscoveryMs = 0L
    private var lastServicePublishMs = 0L

    @Synchronized
    @Throws(IOException::class)
    fun startIfNeeded() {
        p2pConfig = P2PConfigLoader.load()
        if (!started) {
            libp2pProxy = Libp2pStreamProxy(p2pConfig.libp2pProxy)
            webRtcProxy = WebRtcTunnelProxy(p2pConfig.webRtcProxy)
            serviceDiscovery = P2PServiceDiscoveryProvider(p2pConfig.discovery.service, objectMapper)
            servicePublisher = P2PServicePublisher(
                p2pConfig.discovery.service.publish,
                p2pConfig.discovery.service.urls,
                objectMapper
            )
            dhtDiscovery = P2PDhtDiscoveryProvider(p2pConfig.discovery.dht)
            bootstrapNodeSource = P2PBootstrapNodeSource(p2pConfig.libp2pPeerSources, objectMapper)
            val gossipProtocol = Gossip(GossipRouterBuilder().build())
            gossip = gossipProtocol
            val builtHost = runCatching {
                val hostBuilder = HostBuilder()
                    .transport(::TcpTransport)
                    .listen("/ip4/0.0.0.0/tcp/${p2pConfig.libp2pListenPort}")
                    .protocol(gossipProtocol)
                relaySupport = Libp2pRelaySupport(p2pConfig.relay).applyTo(hostBuilder)
                hostBuilder.build()
            }.getOrElse { e ->
                GameEngine.log("Failed to build libp2p host:\n${stackTraceToString(e)}")
                throw IOException("Failed to build libp2p host: ${rootCauseMessage(e)}", e)
            }
            host = builtHost.also {
                it.addConnectionHandler(gossipProtocol)
                runCatching {
                    it.start().get(15L, TimeUnit.SECONDS)
                }.onFailure { e ->
                    GameEngine.log("Failed to start libp2p host:\n${stackTraceToString(e)}")
                    throw IOException("Failed to start libp2p host: ${rootCauseMessage(e)}", e)
                }
            }
            val currentHost = host ?: throw IOException("Libp2p host was not created")
            GameEngine.log(
                "P2P libp2p host started peer=${currentHost.peerId.toBase58()} addrs=${
                    currentHost.listenAddresses().joinToString(",")
                }"
            )
            if (p2pConfig.discovery.enableGossipSub) {
                gossipProtocol.subscribe(this::handleMessage, lobbyTopic)
            }
            gossipProtocol.subscribe(this::handleWebRtcSignal, webrtcTopic)
            if (p2pConfig.discovery.enableMdns) {
                mDnsDiscovery =
                    MDnsDiscovery(currentHost, MDnsDiscovery.ServiceTagLocal, MDnsDiscovery.QueryInterval, null).also {
                        it.addHandler(this::handleMdnsPeerFound)
                        it.start()
                    }
            }
            scheduler.scheduleAtFixedRate(
                ::tick,
                1000L,
                p2pConfig.lobby.roomAnnounceIntervalMs,
                TimeUnit.MILLISECONDS
            )
            started = true
        }
        connectBootstrapPeers((p2pConfig.libp2pPeers + bootstrapNodeSource.fetchPeers()).distinct())
    }

    fun getSavedPeerConfig(): String {
        return "P2P config: ${P2PConfigLoader.configPath()}"
    }

    @Synchronized
    @Throws(IOException::class)
    fun hostCurrentServer() {
        ensureStarted()
        val gameEngine = GameEngine.getInstance()
        val room = P2PRoomAdvertisement().apply {
            roomId = UUID.randomUUID().toString()
            hostPeerId = host!!.peerId.toBase58()
            createdBy = gameEngine.networkEngine.playerName
            libp2pBootstrapPeers = p2pConfig.libp2pPeers.toMutableList()
            libp2pDirectAddresses = getHostDirectAddresses(host!!).toMutableList()
            libp2pMappedAddresses = getMappedAddresses().toMutableList()
            webrtcIceServers = p2pConfig.webrtcIceServers.toMutableList()
        }
        updateRoomFromNetworkState(room)

        GameEngine.log("Starting WebRTC DataChannel proxy for game on port ${gameEngine.settingsEngine.networkPort}")
        libp2pProxy.startHostSide(host!!, gameEngine.settingsEngine.networkPort)
        ensureLibp2pPortMapping()
        updateRoomFromNetworkState(room)
        webRtcProxy.startHostSide(
            room.roomId!!,
            host!!.peerId.toBase58(),
            gameEngine.settingsEngine.networkPort,
            room.webrtcIceServers,
            ::publishWebRtcSignal
        )
        
        hostedRoom = room
        joinedRoom = null
        mergeDiscoveredRoom(P2PDiscoveryResult(room, P2PDiscoverySource.MANUAL, trustScore = 100))
        GameEngine.log(
            "P2P hosted room id=${room.roomId} host=${room.hostPeerId} direct=${
                room.libp2pDirectAddresses.joinToString(
                    ","
                )
            } mapped=${room.libp2pMappedAddresses.joinToString(",")}"
        )
        publishRoom(room)
        servicePublisher.publishRoom(room)
        lastServicePublishMs = System.currentTimeMillis()
        dhtDiscovery.publishRoom(room)
        queueRoomListRefresh()
    }

    @Synchronized
    @Throws(IOException::class)
    fun prepareJoin(roomId: String): String {
        ensureStarted()
        val room = findRoom(roomId) ?: throw IOException("That P2P room is no longer available")

        GameEngine.log("Starting WebRTC DataChannel proxy to connect to host")
        val hostPeerId = room.hostPeerId ?: throw IOException("Missing host peer ID")
        val hostAddresses = parseMultiaddrs(room.allLibp2pAddresses())
        val localPort = if (room.webrtcSignaling == "gossip") {
            webRtcProxy.startClientSide(
                room.roomId!!,
                host!!.peerId.toBase58(),
                hostPeerId,
                room.webrtcIceServers,
                ::publishWebRtcSignal
            )
        } else if (hostAddresses.isNotEmpty()) {
            libp2pProxy.startClientSide(host!!, hostPeerId, hostAddresses, localPort = 0)
        } else {
            throw IOException("P2P room has no WebRTC fallback and no direct libp2p address")
        }
        
        joinedRoom = room

        val connectAddress = "127.0.0.1:$localPort"
        GameEngine.log("Game client should connect to: $connectAddress (WebRTC DataChannel tunnel)")

        return connectAddress
    }

    @Synchronized
    fun stopSession() {
        hostedRoom?.let { room ->
            servicePublisher.closeRoom(room)
            publishRoom(
                P2PRoomAdvertisement(
                    magic = room.magic,
                    schema = room.schema,
                    type = "room_close",
                    roomId = room.roomId,
                    hostPeerId = room.hostPeerId,
                    libp2pDirectAddresses = room.libp2pDirectAddresses,
                    libp2pMappedAddresses = room.libp2pMappedAddresses,
                    webrtcSignaling = room.webrtcSignaling,
                    webrtcIceServers = room.webrtcIceServers,
                )
            )
            discoveredRooms.remove(room.roomId)
        }
        libp2pPortMapping?.let { natPortMapper.deleteMapping(it) }
        libp2pPortMapping = null
        hostedRoom = null
        joinedRoom = null
        libp2pProxy.stop()
        webRtcProxy.stop()
        queueRoomListRefresh()
    }

    @Synchronized
    @Throws(IOException::class)
    fun requestRefresh() {
        ensureStarted()
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
                publishExternalRoom(it)
                dhtDiscovery.publishRoom(it)
            }
            refreshExternalDiscovery()
        } catch (e: Exception) {
            GameEngine.log("P2P tick failed: ${e.message}")
        }
    }

    private fun refreshExternalDiscovery() {
        val now = System.currentTimeMillis()
        if (p2pConfig.discovery.service.enable && now - lastServiceDiscoveryMs >= p2pConfig.discovery.service.refreshIntervalMs) {
            lastServiceDiscoveryMs = now
            serviceDiscovery.fetchRooms().forEach { mergeDiscoveredRoom(it) }
        }
        if (p2pConfig.discovery.dht.enable && now - lastDhtDiscoveryMs >= p2pConfig.discovery.dht.queryIntervalMs) {
            lastDhtDiscoveryMs = now
            dhtDiscovery.fetchRooms().forEach { mergeDiscoveredRoom(it) }
        }
    }

    private fun publishExternalRoom(room: P2PRoomAdvertisement) {
        val now = System.currentTimeMillis()
        if (now - lastServicePublishMs >= p2pConfig.discovery.service.publish.updateIntervalMs) {
            lastServicePublishMs = now
            servicePublisher.publishRoom(room)
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
            libp2pDirectAddresses = getHostDirectAddresses(host).toMutableList()
            libp2pMappedAddresses = getMappedAddresses().toMutableList()
            webrtcSignaling = "gossip"
            webrtcIceServers = p2pConfig.webrtcIceServers.toMutableList()
            seq += 1
            lastSeenTimeMs = System.currentTimeMillis()
            expiresAtMs = room.lastSeenTimeMs + p2pConfig.lobby.roomTtlMs
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

    private fun publishWebRtcSignal(signal: WebRtcTunnelProxy.Signal) {
        if (!started) return
        try {
            GameEngine.log("Publishing WebRTC signal type=${signal.type} session=${signal.sessionId} from=${signal.fromPeerId} to=${signal.toPeerId}")
            gossip?.createPublisher(null, System.currentTimeMillis())
                ?.publish(Unpooled.wrappedBuffer(objectMapper.writeValueAsBytes(signal)), webrtcTopic)
        } catch (e: Exception) {
            GameEngine.log("Failed to publish WebRTC signal: ${e.message}")
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
            val now = System.currentTimeMillis()
            val fromPeerId = runCatching { messageApi.from?.let { PeerId(it) }?.toBase58() }.getOrNull()
            val mdnsTtlMs = p2pConfig.lobby.mdnsPeerAddressTtlMs.coerceAtLeast(1000L)
            val isMdnsPeer = fromPeerId?.let { mdnsPeers[it] }?.let { now - it <= mdnsTtlMs } == true
            val source = if (isMdnsPeer) P2PDiscoverySource.MDNS else P2PDiscoverySource.GOSSIPSUB
            val trustScore = if (isMdnsPeer) 70 else 60
            mergeDiscoveredRoom(P2PDiscoveryResult(room, source, trustScore = trustScore, receivedAtMs = now))
        } catch (e: Exception) {
            GameEngine.log("Failed to decode P2P room: ${e.message}")
        }
    }

    private fun mergeDiscoveredRoom(result: P2PDiscoveryResult) {
        val room = result.room
        if (!room.isValid()) return
        val roomId = room.roomId ?: return
        val now = result.receivedAtMs
        if (room.isExpired(now)) return
        room.lastSeenTimeMs = now
        if (!room.discoverySources.contains(result.source.id)) {
            room.discoverySources.add(result.source.id)
        }
        val previous = discoveredRooms[roomId]
        if (previous == null) {
            discoveredRooms[roomId] = room
            queueRoomListRefresh()
            return
        }
        previous.discoverySources.forEach {
            if (!room.discoverySources.contains(it)) room.discoverySources.add(it)
        }
        if (previous.seq <= room.seq) {
            discoveredRooms[roomId] = room
            queueRoomListRefresh()
        } else if (!previous.discoverySources.contains(result.source.id)) {
            previous.discoverySources.add(result.source.id)
            queueRoomListRefresh()
        }
    }

    private fun handleWebRtcSignal(messageApi: MessageApi) {
        try {
            val data = messageApi.data
            val bytes = ByteArray(data.readableBytes())
            data.getBytes(data.readerIndex(), bytes)
            val signal = objectMapper.readValue(bytes, WebRtcTunnelProxy.Signal::class.java)
            val localPeerId = host?.peerId?.toBase58()
            GameEngine.log("Received WebRTC signal type=${signal.type} session=${signal.sessionId} from=${signal.fromPeerId} to=${signal.toPeerId} local=$localPeerId")
            if (signal.fromPeerId == localPeerId) return
            if (signal.toPeerId != localPeerId) return
            webRtcProxy.handleSignal(signal)
        } catch (e: Exception) {
            GameEngine.log("Failed to decode WebRTC signal: ${e.message}")
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
                    .orTimeout(p2pConfig.lobby.bootstrapConnectTimeoutMs, TimeUnit.MILLISECONDS)
                    .whenComplete { _, error ->
                        if (error == null) {
                            GameEngine.log("P2P bootstrap connected: $peer")
                        } else {
                            GameEngine.log("P2P bootstrap connect failed: $peer - ${rootCauseMessage(error)}")
                        }
                    }
                GameEngine.log("P2P bootstrap connect started: $peer")
            } catch (e: Exception) {
                GameEngine.log("Failed bootstrap connect: $peer - ${e.message}")
            }
        }
    }

    private fun getHostDirectAddresses(currentHost: Host?): List<String> {
        if (currentHost == null) return emptyList()
        return currentHost.listenAddresses()
            .map { it.toString() }
            .filterNot { it.contains("/ip4/0.0.0.0/") || it.contains("/ip6/::/") }
            .distinct()
    }

    private fun ensureLibp2pPortMapping() {
        if (!p2pConfig.nat.enablePcp && !p2pConfig.nat.enableUpnp) return
        if (libp2pPortMapping != null) return
        val localPort = getHostTcpListenPort(host) ?: return
        GameEngine.log("Trying NAT port mapping for libp2p TCP port $localPort")
        libp2pPortMapping = runCatching {
            natPortMapper.mapTcpPort(
                localPort,
                p2pConfig.nat.mappingDescription,
                enablePcp = p2pConfig.nat.enablePcp,
                enableUpnp = p2pConfig.nat.enableUpnp,
                pcpLifetimeSeconds = p2pConfig.nat.pcpLifetimeSeconds,
                pcpTimeoutMs = p2pConfig.nat.pcpTimeoutMs
            )
        }.onFailure { e ->
            GameEngine.log("NAT port mapping failed: ${e.message}")
        }.getOrNull()

        libp2pPortMapping?.let {
            GameEngine.log("${it.method} mapped libp2p TCP ${it.internalPort} -> ${it.externalIp}:${it.externalPort}")
        }
    }

    private fun getMappedAddresses(): List<String> {
        val mapping = libp2pPortMapping ?: return emptyList()
        val peerId = host?.peerId ?: return emptyList()
        return listOf("/ip4/${mapping.externalIp}/tcp/${mapping.externalPort}/p2p/${peerId.toBase58()}")
    }

    private fun getHostTcpListenPort(currentHost: Host?): Int? {
        return currentHost?.listenAddresses()
            ?.map { it.toString() }
            ?.firstNotNullOfOrNull { address ->
                Regex("/tcp/(\\d+)").find(address)?.groupValues?.get(1)?.toIntOrNull()
            }
    }

    private fun P2PRoomAdvertisement.allLibp2pAddresses(): List<String> {
        return (libp2pMappedAddresses + libp2pDirectAddresses).distinct()
    }

    private fun parseMultiaddrs(values: List<String>?): List<Multiaddr> {
        return values.orEmpty().mapNotNull { value ->
            runCatching { Multiaddr(value) }.getOrNull()
        }.distinct()
    }

    private fun handleMdnsPeerFound(peerInfo: PeerInfo) {
        try {
            val currentHost = host ?: return
            mdnsPeers[peerInfo.peerId.toBase58()] = System.currentTimeMillis()
            if (peerInfo.addresses.isNotEmpty()) {
                currentHost.addressBook.addAddrs(
                    peerInfo.peerId,
                    p2pConfig.lobby.mdnsPeerAddressTtlMs,
                    *peerInfo.addresses.toTypedArray()
                )
                currentHost.network.connect(peerInfo.peerId, *peerInfo.addresses.toTypedArray())
            }
        } catch (e: Exception) {
            GameEngine.log("Failed mDNS connect: ${e.message}")
        }
    }

    private fun stackTraceToString(throwable: Throwable): String {
        val writer = StringWriter()
        throwable.printStackTrace(PrintWriter(writer))
        return writer.toString()
    }

    private fun rootCauseMessage(throwable: Throwable): String {
        var current = throwable
        while (current.cause != null) current = current.cause!!
        return current.message ?: current.javaClass.name
    }

}
