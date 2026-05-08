package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import kotlinx.serialization.Serializable
import net.peanuuutz.tomlkt.Toml
import net.peanuuutz.tomlkt.TomlComment
import net.peanuuutz.tomlkt.decodeFromString
import java.io.File

@Serializable
data class P2PConfig(
    val libp2p: Libp2pConfig = Libp2pConfig(),
    val webrtc: WebRtcConfig = WebRtcConfig(),
    val nat: NatConfig = NatConfig(),
    val relay: RelayConfig = RelayConfig(),
    val discovery: DiscoveryConfig = DiscoveryConfig(),
    val lobby: LobbyConfig = LobbyConfig()
) {
    val libp2pPeers: List<String>
        get() = libp2p.peers

    val libp2pPeerSources: List<String>
        get() = libp2p.peerSources

    val libp2pListenPort: Int
        get() = libp2p.listenPort

    val webrtcIceServers: List<String>
        get() = webrtc.iceServers

    val libp2pProxy: Libp2pStreamProxy.ProxyConfig
        get() = Libp2pStreamProxy.ProxyConfig(
            bufferSize = libp2p.proxy.bufferSize,
            readTimeoutMs = libp2p.proxy.readTimeoutMs,
            streamOpenTimeoutMs = libp2p.proxy.streamOpenTimeoutMs,
            connectTimeoutMs = libp2p.proxy.connectTimeoutMs,
            idleTimeoutMs = libp2p.proxy.idleTimeoutMs,
            tcpKeepAlive = libp2p.proxy.tcpKeepAlive,
            tcpNoDelay = libp2p.proxy.tcpNoDelay
        )

    val webRtcProxy: WebRtcTunnelProxy.ProxyConfig
        get() = WebRtcTunnelProxy.ProxyConfig(
            bufferSize = webrtc.proxy.bufferSize,
            openTimeoutMs = webrtc.proxy.openTimeoutMs,
            socketConnectTimeoutMs = webrtc.proxy.socketConnectTimeoutMs,
            socketReadTimeoutMs = webrtc.proxy.socketReadTimeoutMs,
            executorThreads = webrtc.proxy.executorThreads
        )

    @Serializable
    data class Libp2pConfig(
        @TomlComment("Used for lobby discovery and WebRTC signaling over GossipSub. 0 means random ephemeral TCP port.")
        val listenPort: Int = 0,
        val peers: List<String> = DEFAULT_LIBP2P_PEERS,
        @TomlComment("Optional JSON node lists. Useful for unstable/testing rendezvous-bootstrap nodes.")
        val peerSources: List<String> = emptyList(),
        val proxy: Libp2pProxyConfig = Libp2pProxyConfig()
    )

    @Serializable
    data class Libp2pProxyConfig(
        val bufferSize: Int = 65536,
        val readTimeoutMs: Int = 30000,
        val streamOpenTimeoutMs: Long = 10000L,
        val connectTimeoutMs: Int = 5000,
        val idleTimeoutMs: Long = 120000L,
        val tcpKeepAlive: Boolean = true,
        val tcpNoDelay: Boolean = true
    )

    @Serializable
    data class WebRtcConfig(
        @TomlComment("WebRTC DataChannel ICE servers. TURN entries may use turn:user:pass@host:port.")
        val iceServers: List<String> = WebRtcTunnelProxy.DEFAULT_ICE_SERVERS,
        val proxy: WebRtcProxyConfig = WebRtcProxyConfig()
    )

    @Serializable
    data class WebRtcProxyConfig(
        val bufferSize: Int = 65536,
        val openTimeoutMs: Long = 20000L,
        val socketConnectTimeoutMs: Int = 5000,
        val socketReadTimeoutMs: Int = 30000,
        val executorThreads: Int = 4
    )

    @Serializable
    data class NatConfig(
        val enableUpnp: Boolean = true,
        val enablePcp: Boolean = true,
        val pcpLifetimeSeconds: Int = 3600,
        val pcpTimeoutMs: Int = 3000,
        val mappingDescription: String = "RWX P2P"
    )

    @Serializable
    data class RelayConfig(
        @TomlComment("jvm-libp2p 1.2.0 provides relay hop/stop support. DCUtR is reserved for future library support.")
        val enableCircuitRelayV2: Boolean = true,
        val enableDcutr: Boolean = false,
        val relayReservationCount: Int = 1,
        val maxRelayReservations: Int = 16,
        val maxRelayReservationSeconds: Int = 3600,
        val relayPeers: List<String> = emptyList(),
        val bootstrapRelays: List<String> = emptyList()
    )

    @Serializable
    data class DiscoveryConfig(
        val enableGossipSub: Boolean = true,
        val enableMdns: Boolean = true,
        val github: GithubDiscoveryConfig = GithubDiscoveryConfig(),
        val dht: DhtDiscoveryConfig = DhtDiscoveryConfig()
    )

    @Serializable
    data class GithubDiscoveryConfig(
        @TomlComment("Read-only static room indexes. The default project workflow publishes rooms.json to the p2p-dist branch.")
        val enable: Boolean = false,
        val urls: List<String> = emptyList(),
        val refreshIntervalMs: Long = 15000L,
        val timeoutMs: Int = 5000,
        val maxBytes: Int = 262144,
        val maxRoomsPerUrl: Int = 200,
        val publish: GithubGistPublishConfig = GithubGistPublishConfig()
    )

    @Serializable
    data class GithubGistPublishConfig(
        @TomlComment("Optional: publish this client's hosted room request to the user's own Gist. Prefer tokenEnv over putting a token in this file.")
        val enable: Boolean = false,
        val token: String = "",
        val gistId: String = "",
        val gistIdFile: String = "p2p-github-gist.id",
        val fileName: String = "rwx-p2p-room.json",
        val updateIntervalMs: Long = 15000L,
        val deleteOnClose: Boolean = true,
        val publicGist: Boolean = false
    )

    @Serializable
    data class DhtDiscoveryConfig(
        @TomlComment("Reserved for future DHT provider discovery. jvm-libp2p 1.2.0 does not expose Kad-DHT provider APIs.")
        val enable: Boolean = false,
        val namespace: String = "/rwx/lobby/v1",
        val provideIntervalMs: Long = 30000L,
        val queryIntervalMs: Long = 15000L,
        val queryTimeoutMs: Long = 10000L,
        val maxProviders: Int = 50
    )

    @Serializable
    data class LobbyConfig(
        val roomAnnounceIntervalMs: Long = 3000L,
        val roomTtlMs: Long = 15000L,
        val bootstrapConnectTimeoutMs: Long = 10000L,
        val mdnsPeerAddressTtlMs: Long = 30000L
    )

    fun normalized(): P2PConfig {
        return copy(
            libp2p = libp2p.copy(
                listenPort = libp2p.listenPort.coerceIn(0, 65535),
                peers = libp2p.peers.filter { it.startsWith("/") }.distinct().ifEmpty { DEFAULT_LIBP2P_PEERS },
                peerSources = libp2p.peerSources
                    .filter {
                        it.startsWith("https://api.github.com/gists/") || it.startsWith("https://gist.github.com/") || it.startsWith(
                            "https://gist.githubusercontent.com/"
                        )
                    }
                    .distinct()
            ),
            webrtc = webrtc.copy(
                iceServers = webrtc.iceServers.filter { isIceServer(it) }.distinct()
                    .ifEmpty { WebRtcTunnelProxy.DEFAULT_ICE_SERVERS }
            ),
            relay = relay.copy(
                relayPeers = relay.relayPeers.filter { it.startsWith("/") }.distinct(),
                bootstrapRelays = relay.bootstrapRelays.filter { it.startsWith("/") }.distinct()
            ),
            discovery = discovery.copy(
                github = discovery.github.copy(
                    urls = discovery.github.urls
                        .filter {
                            it.startsWith("https://raw.githubusercontent.com/") || it.startsWith("https://gist.githubusercontent.com/") || it.startsWith(
                                "https://github.com/"
                            )
                        }
                        .distinct()
                )
            )
        )
    }

    companion object {
        val DEFAULT_LIBP2P_PEERS = listOf(
            "/dnsaddr/bootstrap.libp2p.io/ipfs/QmNnooDu7bfjPFoTZYxMNLWUQJyrVwtbZg5gBMjTezGAJN",
            "/dnsaddr/bootstrap.libp2p.io/ipfs/QmQCU2EcMqAqQPR2i9bChDtGNJchTbq5TbXJJ16u19uLTa",
            "/ip4/104.131.131.82/tcp/4001/ipfs/QmaCpDMGvV2BGHeYERUEnRQAwe3N8SzbUtfsmvsqQLuvuJ",
            "/ip4/104.236.179.241/tcp/4001/ipfs/QmSoLPppuBtQSGwKDZT2M73ULpjvfd3aZ6ha4oFGL1KrGM",
            "/ip4/128.199.219.111/tcp/4001/ipfs/QmSoLSafTMBsPKadTEgaXctDQVcqN88CNLHXMkTNwMKPnu"
        )

        private fun isIceServer(value: String): Boolean {
            return value.startsWith("stun:") || value.startsWith("turn:") || value.startsWith("turns:")
        }
    }
}

object P2PConfigLoader {
    private const val CONFIG_FILE_NAME = "p2p.toml"
    private val toml = Toml { ignoreUnknownKeys = true }

    fun load(): P2PConfig {
        val file = File(CONFIG_FILE_NAME)
        if (!file.exists()) {
            runCatching { file.writeText(defaultConfigText(), Charsets.UTF_8) }
                .onFailure { GameEngine.log("Failed to create $CONFIG_FILE_NAME: ${it.message}") }
        }

        return runCatching { toml.decodeFromString<P2PConfig>(file.readText(Charsets.UTF_8)).normalized() }
            .onFailure { GameEngine.log("Failed to parse $CONFIG_FILE_NAME: ${it.message}") }
            .getOrElse { P2PConfig().normalized() }
    }

    fun configPath(): String = File(CONFIG_FILE_NAME).absolutePath

    private fun defaultConfigText(): String {
        return "# RWX P2P configuration\n# Edit this file locally.\n\n" +
                toml.encodeToString(P2PConfig.serializer(), P2PConfig())
    }
}
