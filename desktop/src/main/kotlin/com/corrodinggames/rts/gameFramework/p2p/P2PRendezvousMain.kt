package com.corrodinggames.rts.gameFramework.p2p

import io.libp2p.core.dsl.HostBuilder
import io.libp2p.core.crypto.KeyType
import io.libp2p.core.crypto.PrivKey
import io.libp2p.core.crypto.generateKeyPair
import io.libp2p.core.crypto.marshalPrivateKey
import io.libp2p.core.crypto.unmarshalPrivateKey
import io.libp2p.core.pubsub.MessageApi
import io.libp2p.core.pubsub.Topic
import io.libp2p.pubsub.gossip.Gossip
import io.libp2p.pubsub.gossip.builders.GossipRouterBuilder
import io.libp2p.transport.tcp.TcpTransport
import java.io.File
import java.util.Base64
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

object P2PRendezvousMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val port = args.firstOrNull()?.toIntOrNull() ?: 4001
        val keyFile = args.getOrNull(1)?.let { File(it) }
        val gossip = Gossip(GossipRouterBuilder().build())
        val hostBuilder = HostBuilder()
            .builderModifier { builder ->
                val key = loadOrCreatePrivateKey(keyFile)
                if (key != null) {
                    builder.identity.factory = { key }
                } else {
                    builder.identity.random(KeyType.SECP256K1)
                }
            }
            .transport(::TcpTransport)
            .listen("/ip4/0.0.0.0/tcp/$port")
            .protocol(gossip)
        Libp2pRelaySupport(P2PConfig.RelayConfig()).applyTo(hostBuilder)
        val host = hostBuilder.build()
        host.addConnectionHandler(gossip)
        host.start().get(15, TimeUnit.SECONDS)
        gossip.subscribe(Consumer<MessageApi> { message ->
            println("P2P_RENDEZVOUS topic message bytes=${message.data.readableBytes()}")
        }, Topic("rwx.p2p.lobby.v1"))
        gossip.subscribe(Consumer<MessageApi> { message ->
            println("P2P_RENDEZVOUS webrtc signal bytes=${message.data.readableBytes()}")
        }, Topic("rwx.p2p.lobby.v1.webrtc"))
        println("P2P_RENDEZVOUS peer=${host.peerId.toBase58()}")
        host.listenAddresses().forEach { address ->
            println("P2P_RENDEZVOUS addr=$address/p2p/${host.peerId.toBase58()}")
        }
        while (true) Thread.sleep(60000L)
    }

    private fun loadOrCreatePrivateKey(file: File?): PrivKey? {
        if (file == null) return null
        if (file.exists()) {
            val bytes = Base64.getDecoder().decode(file.readText(Charsets.UTF_8).trim())
            return unmarshalPrivateKey(bytes)
        }
        file.parentFile?.mkdirs()
        val key = generateKeyPair(KeyType.SECP256K1).first
        file.writeText(Base64.getEncoder().encodeToString(marshalPrivateKey(key)), Charsets.UTF_8)
        file.setReadable(false, false)
        file.setReadable(true, true)
        file.setWritable(false, false)
        file.setWritable(true, true)
        return key
    }
}
