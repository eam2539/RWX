package io.github.rwx.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import io.libp2p.core.Host
import io.libp2p.core.PeerId
import io.libp2p.core.dsl.HostBuilder
import io.libp2p.core.multiformats.Multiaddr
import io.libp2p.protocol.circuit.CircuitHopProtocol
import io.libp2p.protocol.circuit.CircuitStopProtocol
import io.libp2p.protocol.circuit.RelayTransport
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.Executors

class Libp2pRelaySupport(private val config: P2PConfig.RelayConfig) {
    data class Components(
        val hopBinding: CircuitHopProtocol.Binding,
        val stopBinding: CircuitStopProtocol.Binding
    )

    fun applyTo(builder: HostBuilder): Components? {
        if (!config.enableCircuitRelayV2) return null

        val stop = CircuitStopProtocol.Binding(CircuitStopProtocol())
        val manager = LimitedRelayManager(config.maxRelayReservations, config.maxRelayReservationSeconds)
        val hop = CircuitHopProtocol.Binding(manager, stop)
        val runner = Executors.newSingleThreadScheduledExecutor { r ->
            Thread(r, "Libp2pRelayTransport").also { it.isDaemon = true }
        }

        builder.protocol(hop, stop)
        builder.transport({ upgrader ->
            RelayTransport(hop, stop, upgrader, { host -> candidateRelays(host) }, runner).also { transport ->
                transport.setRelayCount(config.relayReservationCount)
            }
        })

        if (config.enableDcutr) {
            GameEngine.log("DCUtR requested in p2p.toml, but jvm-libp2p 1.2.0 does not provide a DCUtR protocol implementation")
        }

        return Components(hop, stop)
    }

    private fun candidateRelays(host: Host): List<RelayTransport.CandidateRelay> {
        val peers = config.relayPeers.ifEmpty { config.bootstrapRelays }
        return peers.mapNotNull { address ->
            runCatching {
                val multiaddr = Multiaddr(address)
                val peerId = multiaddr.getPeerId() ?: return@runCatching null
                RelayTransport.CandidateRelay(peerId, listOf(multiaddr))
            }.onFailure {
                GameEngine.log("Invalid relay peer address: $address - ${it.message}")
            }.getOrNull()
        }.ifEmpty {
            host.network.connections.map { connection ->
                val peerId = connection.secureSession().remoteId
                RelayTransport.CandidateRelay(peerId, listOf(connection.remoteAddress()))
            }
        }
    }

    private class LimitedRelayManager(
        private val maxReservations: Int,
        private val reservationSeconds: Int
    ) : CircuitHopProtocol.RelayManager {
        private val reservations = LinkedHashMap<PeerId, CircuitHopProtocol.Reservation>()

        override fun hasReservation(peer: PeerId): Boolean {
            pruneExpired()
            return reservations.containsKey(peer)
        }

        override fun createReservation(peer: PeerId, addr: Multiaddr): Optional<CircuitHopProtocol.Reservation> {
            pruneExpired()
            if (!reservations.containsKey(peer) && reservations.size >= maxReservations) {
                return Optional.empty()
            }
            val expiry = LocalDateTime.now().plusSeconds(reservationSeconds.toLong())
            val reservation = CircuitHopProtocol.Reservation(
                expiry,
                reservationSeconds,
                0L,
                ByteArray(0),
                arrayOf(addr)
            )
            reservations[peer] = reservation
            return Optional.of(reservation)
        }

        override fun allowConnection(target: PeerId, initiator: PeerId): Optional<CircuitHopProtocol.Reservation> {
            pruneExpired()
            return Optional.ofNullable(reservations[target])
        }

        private fun pruneExpired() {
            val now = LocalDateTime.now()
            val iterator = reservations.entries.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().value.expiry.isBefore(now)) {
                    iterator.remove()
                }
            }
        }
    }
}
