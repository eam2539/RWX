package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import net.peanuuutz.tomlkt.Toml
import net.peanuuutz.tomlkt.TomlArray
import net.peanuuutz.tomlkt.TomlElement
import net.peanuuutz.tomlkt.TomlLiteral
import net.peanuuutz.tomlkt.TomlTable
import net.peanuuutz.tomlkt.asTomlArray
import net.peanuuutz.tomlkt.asTomlLiteral
import net.peanuuutz.tomlkt.asTomlTable
import net.peanuuutz.tomlkt.toBooleanOrNull
import net.peanuuutz.tomlkt.toIntOrNull
import net.peanuuutz.tomlkt.toLongOrNull
import java.io.File

data class P2PConfig(
    val libp2pPeers: List<String>,
    val webrtcIceServers: List<String>,
    val libp2pProxy: Libp2pStreamProxy.ProxyConfig,
    val webRtcProxy: WebRtcTunnelProxy.ProxyConfig,
    val nat: NatConfig,
    val lobby: LobbyConfig
) {
    data class NatConfig(
        val enableUpnp: Boolean = true,
        val mappingDescription: String = "Rusted Warfare P2P"
    )

    data class LobbyConfig(
        val roomAnnounceIntervalMs: Long = 3000L,
        val roomTtlMs: Long = 15000L,
        val bootstrapConnectTimeoutMs: Long = 10000L,
        val mdnsPeerAddressTtlMs: Long = 30000L
    )
}

object P2PConfigLoader {
    private const val CONFIG_FILE_NAME = "p2p.toml"
    private val toml = Toml { ignoreUnknownKeys = true }

    val defaultLibp2pPeers = listOf(
        "/dnsaddr/bootstrap.libp2p.io/ipfs/QmNnooDu7bfjPFoTZYxMNLWUQJyrVwtbZg5gBMjTezGAJN",
        "/dnsaddr/bootstrap.libp2p.io/ipfs/QmQCU2EcMqAqQPR2i9bChDtGNJchTbq5TbXJJ16u19uLTa",
        "/ip4/104.131.131.82/tcp/4001/ipfs/QmaCpDMGvV2BGHeYERUEnRQAwe3N8SzbUtfsmvsqQLuvuJ",
        "/ip4/104.236.179.241/tcp/4001/ipfs/QmSoLPppuBtQSGwKDZT2M73ULpjvfd3aZ6ha4oFGL1KrGM",
        "/ip4/128.199.219.111/tcp/4001/ipfs/QmSoLSafTMBsPKadTEgaXctDQVcqN88CNLHXMkTNwMKPnu"
    )

    fun load(): P2PConfig {
        val file = File(CONFIG_FILE_NAME)
        if (!file.exists()) {
            runCatching { file.writeText(defaultConfigText(), Charsets.UTF_8) }
                .onFailure { GameEngine.log("Failed to create $CONFIG_FILE_NAME: ${it.message}") }
        }

        val table = runCatching { toml.parseToTomlTable(file.readText(Charsets.UTF_8)) }
            .onFailure { GameEngine.log("Failed to parse $CONFIG_FILE_NAME: ${it.message}") }
            .getOrNull()

        return P2PConfig(
            libp2pPeers = table.stringList("libp2p", "peers")
                .filter { it.startsWith("/") }
                .ifEmpty { defaultLibp2pPeers },
            webrtcIceServers = table.stringList("webrtc", "iceServers")
                .filter { isIceServer(it) }
                .ifEmpty { WebRtcTunnelProxy.DEFAULT_ICE_SERVERS },
            libp2pProxy = Libp2pStreamProxy.ProxyConfig(
                bufferSize = table.int("libp2p", "proxy", "bufferSize") ?: 65536,
                readTimeoutMs = table.int("libp2p", "proxy", "readTimeoutMs") ?: 30000,
                streamOpenTimeoutMs = table.long("libp2p", "proxy", "streamOpenTimeoutMs") ?: 10000L,
                connectTimeoutMs = table.int("libp2p", "proxy", "connectTimeoutMs") ?: 5000,
                idleTimeoutMs = table.long("libp2p", "proxy", "idleTimeoutMs") ?: 120000L,
                tcpKeepAlive = table.boolean("libp2p", "proxy", "tcpKeepAlive") ?: true,
                tcpNoDelay = table.boolean("libp2p", "proxy", "tcpNoDelay") ?: true
            ),
            webRtcProxy = WebRtcTunnelProxy.ProxyConfig(
                bufferSize = table.int("webrtc", "proxy", "bufferSize") ?: 65536,
                openTimeoutMs = table.long("webrtc", "proxy", "openTimeoutMs") ?: 20000L,
                socketConnectTimeoutMs = table.int("webrtc", "proxy", "socketConnectTimeoutMs") ?: 5000,
                socketReadTimeoutMs = table.int("webrtc", "proxy", "socketReadTimeoutMs") ?: 30000,
                executorThreads = table.int("webrtc", "proxy", "executorThreads") ?: 4
            ),
            nat = P2PConfig.NatConfig(
                enableUpnp = table.boolean("nat", "enableUpnp") ?: true,
                mappingDescription = table.string("nat", "mappingDescription") ?: "Rusted Warfare P2P"
            ),
            lobby = P2PConfig.LobbyConfig(
                roomAnnounceIntervalMs = table.long("lobby", "roomAnnounceIntervalMs") ?: 3000L,
                roomTtlMs = table.long("lobby", "roomTtlMs") ?: 15000L,
                bootstrapConnectTimeoutMs = table.long("lobby", "bootstrapConnectTimeoutMs") ?: 10000L,
                mdnsPeerAddressTtlMs = table.long("lobby", "mdnsPeerAddressTtlMs") ?: 30000L
            )
        )
    }

    fun configPath(): String = File(CONFIG_FILE_NAME).absolutePath

    private fun TomlTable?.stringList(vararg path: String): List<String> {
        val array = this.elementAt(*path)?.asArrayOrNull() ?: return emptyList()
        return array.mapNotNull { it.asLiteralOrNull()?.content?.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    }

    private fun TomlTable?.string(vararg path: String): String? {
        return this.elementAt(*path)?.asLiteralOrNull()?.content?.trim()?.takeIf { it.isNotEmpty() }
    }

    private fun TomlTable?.int(vararg path: String): Int? {
        return this.elementAt(*path)?.asLiteralOrNull()?.toIntOrNull()
    }

    private fun TomlTable?.long(vararg path: String): Long? {
        return this.elementAt(*path)?.asLiteralOrNull()?.toLongOrNull()
    }

    private fun TomlTable?.boolean(vararg path: String): Boolean? {
        return this.elementAt(*path)?.asLiteralOrNull()?.toBooleanOrNull()
    }

    private fun TomlTable?.elementAt(vararg path: String): TomlElement? {
        var element: TomlElement = this ?: return null
        for (part in path) {
            val table = element.asTableOrNull() ?: return null
            element = table[part] ?: return null
        }
        return element
    }

    private fun TomlElement.asTableOrNull(): TomlTable? = runCatching { asTomlTable() }.getOrNull()

    private fun TomlElement.asArrayOrNull(): TomlArray? = runCatching { asTomlArray() }.getOrNull()

    private fun TomlElement.asLiteralOrNull(): TomlLiteral? = runCatching { asTomlLiteral() }.getOrNull()

    private fun isIceServer(value: String): Boolean {
        return value.startsWith("stun:") || value.startsWith("turn:") || value.startsWith("turns:")
    }

    private fun defaultConfigText(): String {
        return """
            # Rusted Warfare P2P configuration
            # Edit this file locally; the P2P UI does not store network settings.

            [libp2p]
            # Used for lobby discovery and WebRTC signaling over GossipSub.
            peers = [
              "/dnsaddr/bootstrap.libp2p.io/ipfs/QmNnooDu7bfjPFoTZYxMNLWUQJyrVwtbZg5gBMjTezGAJN",
              "/dnsaddr/bootstrap.libp2p.io/ipfs/QmQCU2EcMqAqQPR2i9bChDtGNJchTbq5TbXJJ16u19uLTa",
              "/ip4/104.131.131.82/tcp/4001/ipfs/QmaCpDMGvV2BGHeYERUEnRQAwe3N8SzbUtfsmvsqQLuvuJ",
              "/ip4/104.236.179.241/tcp/4001/ipfs/QmSoLPppuBtQSGwKDZT2M73ULpjvfd3aZ6ha4oFGL1KrGM",
              "/ip4/128.199.219.111/tcp/4001/ipfs/QmSoLSafTMBsPKadTEgaXctDQVcqN88CNLHXMkTNwMKPnu"
            ]

            [libp2p.proxy]
            bufferSize = 65536
            readTimeoutMs = 30000
            streamOpenTimeoutMs = 10000
            connectTimeoutMs = 5000
            idleTimeoutMs = 120000
            tcpKeepAlive = true
            tcpNoDelay = true

            [webrtc]
            # WebRTC DataChannel ICE servers. TURN entries may use turn:user:pass@host:port.
            iceServers = [
              "stun:stun.l.google.com:19302",
              "stun:stun1.l.google.com:19302"
            ]

            [webrtc.proxy]
            bufferSize = 65536
            openTimeoutMs = 20000
            socketConnectTimeoutMs = 5000
            socketReadTimeoutMs = 30000
            executorThreads = 4

            [nat]
            enableUpnp = true
            mappingDescription = "Rusted Warfare P2P"

            [lobby]
            roomAnnounceIntervalMs = 3000
            roomTtlMs = 15000
            bootstrapConnectTimeoutMs = 10000
            mdnsPeerAddressTtlMs = 30000
        """.trimIndent() + "\n"
    }
}
