package io.github.rwx.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.NetworkEngine
import io.github.rwx.map.PortalTransferMessage
import io.libp2p.core.Host
import io.libp2p.core.PeerId
import io.libp2p.core.Stream
import io.libp2p.core.multiformats.Multiaddr
import io.libp2p.core.multistream.StrictProtocolBinding
import io.libp2p.protocol.ProtocolHandler
import io.libp2p.protocol.ProtocolMessageHandler
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.serialization.Serializable
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

object FeatureIds {
    const val FEATURE_CHANNEL = "rwxFeatureChannel"
    const val AREA_CONTROL = "areaControl"
    const val MAP_LINKS = "mapLinks"

    fun currentClientFeatures(): List<String> = listOf(
        FEATURE_CHANNEL,
        AREA_CONTROL,
        MAP_LINKS,
    )
}

@Serializable
data class FeatureMessage(
    var magic: String = "rwx-feature-channel",
    var schema: Int = 1,
    var roomId: String? = null,
    var fromPeerId: String? = null,
    var toPeerId: String? = null,
    var type: String = "hello",
    var gameVersionCode: Int = 0,
    var gameVersionString: String? = null,
    var features: List<String> = emptyList(),
    var mapPath: String? = null,
    var requiredFeatures: List<String> = emptyList(),
    var multiMapAssignments: MultiMapAssignments? = null,
    var portalTransfer: PortalTransferMessage? = null,
) {
    fun isValid(): Boolean =
        magic == "rwx-feature-channel" &&
                schema == 1 &&
                !roomId.isNullOrBlank() &&
                !fromPeerId.isNullOrBlank() &&
                !type.isBlank()
}

class FeatureChannel {
    companion object {
        const val PROTOCOL_ID = "/rwx/feature-channel/1.0.0"
        private const val STREAM_OPEN_TIMEOUT_MS = 10000L
    }

    private val running = AtomicBoolean(false)
    private val controllersByPeer = ConcurrentHashMap<String, FeatureController>()
    private val peerFeatures = ConcurrentHashMap<String, Set<String>>()
    private val pendingPortalTransfers = ConcurrentLinkedQueue<PortalTransferMessage>()

    private var protocolBinding: StrictProtocolBinding<FeatureController>? = null
    private var hostInstance: Host? = null
    private var roomId: String? = null
    private var localPeerId: String? = null
    private var localFeatures: List<String> = emptyList()
    private var hostSide = false
    private var currentMapPath: String? = null
    private var currentRequiredFeatures: List<String> = emptyList()
    private var currentMultiMapAssignments: MultiMapAssignments? = null

    @Synchronized
    fun startHostSide(host: Host, roomId: String, localPeerId: String, features: List<String>) {
        stop()
        this.hostInstance = host
        this.roomId = roomId
        this.localPeerId = localPeerId
        this.localFeatures = features.distinct()
        this.hostSide = true

        val binding = object : StrictProtocolBinding<FeatureController>(
            PROTOCOL_ID,
            HostProtocolHandler(),
        ) {}
        protocolBinding = binding
        host.addProtocolHandler(binding)
        running.set(true)
        GameEngine.log("RWX feature channel host started protocol=$PROTOCOL_ID room=$roomId")
    }

    @Synchronized
    @Throws(IOException::class)
    fun startClientSide(
        host: Host,
        roomId: String,
        localPeerId: String,
        hostPeerId: String,
        hostAddresses: List<Multiaddr>,
        features: List<String>,
    ) {
        stop()
        this.hostInstance = host
        this.roomId = roomId
        this.localPeerId = localPeerId
        this.localFeatures = features.distinct()
        this.hostSide = false

        val peerId = try {
            PeerId.fromBase58(hostPeerId)
        } catch (e: Exception) {
            throw IOException("Invalid RWX feature host peer id: $hostPeerId", e)
        }

        val handler = ClientProtocolHandler(hostPeerId)
        val binding = object : StrictProtocolBinding<FeatureController>(
            PROTOCOL_ID,
            handler,
        ) {}
        val connection = try {
            host.network.connect(peerId, *hostAddresses.toTypedArray())
                .orTimeout(STREAM_OPEN_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .get()
        } catch (e: Exception) {
            throw IOException("Failed to connect RWX feature channel to host", e)
        }
        val streamPromise = connection.muxerSession().createStream(
            listOf(binding.toInitiator(listOf(PROTOCOL_ID)))
        )
        val controller = try {
            streamPromise.controller
                .orTimeout(STREAM_OPEN_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .get()
        } catch (e: Exception) {
            runCatching { streamPromise.stream.getNow(null)?.close() }
            throw IOException("Failed to negotiate RWX feature channel", e)
        }
        controllersByPeer[hostPeerId] = controller
        running.set(true)
        controller.send(buildMessage(type = "hello", toPeerId = hostPeerId))
        GameEngine.log("RWX feature channel client connected host=$hostPeerId room=$roomId")
    }

    @Synchronized
    fun stop() {
        running.set(false)
        controllersByPeer.values.forEach { it.close() }
        controllersByPeer.clear()
        peerFeatures.clear()
        pendingPortalTransfers.clear()
        protocolBinding?.let { binding ->
            hostInstance?.removeProtocolHandler(binding)
        }
        protocolBinding = null
        hostInstance = null
        roomId = null
        localPeerId = null
        localFeatures = emptyList()
        hostSide = false
        currentMapPath = null
        currentRequiredFeatures = emptyList()
        currentMultiMapAssignments = null
    }

    fun isRunning(): Boolean = running.get()

    fun getPeerFeatures(peerId: String): Set<String> = peerFeatures[peerId] ?: emptySet()

    fun getConnectedPeerIds(): Set<String> = controllersByPeer.keys

    fun peerFeatureSnapshot(): Map<String, Set<String>> = peerFeatures.toMap()

    @Synchronized
    fun currentMultiMapAssignments(): MultiMapAssignments? = currentMultiMapAssignments

    fun drainPortalTransfers(): List<PortalTransferMessage> {
        val drained = mutableListOf<PortalTransferMessage>()
        while (true) {
            drained += pendingPortalTransfers.poll() ?: break
        }
        return drained
    }

    fun sendToHost(type: String, features: List<String> = localFeatures) {
        if (hostSide) return
        val hostPeerId = controllersByPeer.keys.firstOrNull() ?: return
        controllersByPeer[hostPeerId]?.send(buildMessage(type = type, toPeerId = hostPeerId, features = features))
    }

    fun broadcastMapFeatures(mapPath: String?, requiredFeatures: List<String>) {
        if (!hostSide) return
        currentMapPath = mapPath
        currentRequiredFeatures = requiredFeatures.distinct()
        controllersByPeer.forEach { (peerId, controller) ->
            controller.send(
                buildMessage(
                    type = "mapFeatures",
                    toPeerId = peerId,
                    mapPath = mapPath,
                    requiredFeatures = currentRequiredFeatures,
                )
            )
            logMissingPeerRequirements(peerId, currentRequiredFeatures)
        }
    }

    fun broadcastMultiMapAssignments(assignments: MultiMapAssignments) {
        if (!hostSide) return
        currentMultiMapAssignments = assignments
        controllersByPeer.forEach { (peerId, controller) ->
            controller.send(
                buildMessage(
                    type = "multiMapAssignments",
                    toPeerId = peerId,
                    multiMapAssignments = assignments,
                )
            )
        }
        GameEngine.log(
            "RWX multi-map assignments broadcast root=${assignments.rootMapPath}" +
                    " instances=${assignments.instances.size}"
        )
    }

    fun broadcastPortalTransfer(transfer: PortalTransferMessage) {
        pendingPortalTransfers.add(transfer)
        if (!hostSide) {
            val hostPeerId = controllersByPeer.keys.firstOrNull() ?: return
            controllersByPeer[hostPeerId]?.send(
                buildMessage(
                    type = "portalTransfer",
                    toPeerId = hostPeerId,
                    portalTransfer = transfer,
                )
            )
            return
        }
        sendPortalTransferToPeers(transfer, excludePeerId = null)
    }

    private fun sendPortalTransferToPeers(transfer: PortalTransferMessage, excludePeerId: String?) {
        controllersByPeer.forEach { (peerId, controller) ->
            if (peerId == excludePeerId) {
                return@forEach
            }
            controller.send(
                buildMessage(
                    type = "portalTransfer",
                    toPeerId = peerId,
                    portalTransfer = transfer,
                )
            )
        }
    }

    private fun onMessage(controller: FeatureController, message: FeatureMessage) {
        if (!message.isValid()) {
            return
        }
        val expectedRoomId = roomId ?: return
        if (message.roomId != expectedRoomId) {
            return
        }
        val remotePeerId = message.fromPeerId ?: return
        if (message.toPeerId != null && message.toPeerId != localPeerId) {
            return
        }
        controllersByPeer[remotePeerId] = controller
        peerFeatures[remotePeerId] = message.features.toSet()
        when (message.type) {
            "hello" -> {
                GameEngine.log("RWX feature hello from=$remotePeerId features=${message.features.joinToString(",")}")
                if (hostSide) {
                    controller.send(buildMessage(type = "welcome", toPeerId = remotePeerId))
                    if (currentMapPath != null || currentRequiredFeatures.isNotEmpty()) {
                        controller.send(
                            buildMessage(
                                type = "mapFeatures",
                                toPeerId = remotePeerId,
                                mapPath = currentMapPath,
                                requiredFeatures = currentRequiredFeatures,
                            )
                        )
                    }
                    currentMultiMapAssignments?.let { assignments ->
                        controller.send(
                            buildMessage(
                                type = "multiMapAssignments",
                                toPeerId = remotePeerId,
                                multiMapAssignments = assignments,
                            )
                        )
                    }
                    logMissingPeerRequirements(remotePeerId, currentRequiredFeatures)
                }
            }

            "welcome" -> {
                GameEngine.log("RWX feature welcome from=$remotePeerId features=${message.features.joinToString(",")}")
            }

            "mapFeatures" -> {
                handleMapFeatures(message)
            }

            "multiMapAssignments" -> {
                handleMultiMapAssignments(message)
            }

            "portalTransfer" -> {
                handlePortalTransfer(message)
            }

            else -> {
                GameEngine.log("RWX feature message type=${message.type} from=$remotePeerId")
            }
        }
    }

    private fun buildMessage(
        type: String,
        toPeerId: String?,
        features: List<String> = localFeatures,
        mapPath: String? = null,
        requiredFeatures: List<String> = emptyList(),
        multiMapAssignments: MultiMapAssignments? = null,
        portalTransfer: PortalTransferMessage? = null,
    ): FeatureMessage {
        val gameEngine = GameEngine.getInstance()
        return FeatureMessage(
            roomId = roomId,
            fromPeerId = localPeerId,
            toPeerId = toPeerId,
            type = type,
            gameVersionCode = gameEngine.getVersionCode(true),
            gameVersionString = gameEngine.getVersionName(),
            features = features.distinct(),
            mapPath = mapPath,
            requiredFeatures = requiredFeatures.distinct(),
            multiMapAssignments = multiMapAssignments,
            portalTransfer = portalTransfer,
        )
    }

    private fun handleMapFeatures(message: FeatureMessage) {
        val requiredFeatures = message.requiredFeatures.distinct()
        val missing = missingLocalFeatures(requiredFeatures)
        if (missing.isEmpty()) {
            GameEngine.log("RWX map features ok map=${message.mapPath} required=${requiredFeatures.joinToString(",")}")
        } else {
            val text = "RWX map requires unsupported features: ${missing.joinToString(",")}"
            GameEngine.log(text)
            NetworkEngine.reportDesync(text)
        }
    }

    private fun handleMultiMapAssignments(message: FeatureMessage) {
        val assignments = message.multiMapAssignments ?: return
        currentMultiMapAssignments = assignments
        GameEngine.log(
            "RWX multi-map assignments received root=${assignments.rootMapPath}" +
                    " instances=${assignments.instances.size}"
        )
    }

    private fun handlePortalTransfer(message: FeatureMessage) {
        val transfer = message.portalTransfer ?: return
        pendingPortalTransfers.add(transfer)
        if (hostSide) {
            sendPortalTransferToPeers(transfer, excludePeerId = message.fromPeerId)
        }
        GameEngine.log(
            "RWX portal transfer received target=${transfer.targetMapId}" +
                    " portal=${transfer.targetPortalId ?: ""} units=${transfer.units.size}"
        )
    }

    private fun logMissingPeerRequirements(peerId: String, requiredFeatures: List<String>) {
        val peerSupportedFeatures = peerFeatures[peerId] ?: return
        val missing = requiredFeatures.filterNot { peerSupportedFeatures.contains(it) }
        if (missing.isNotEmpty()) {
            GameEngine.log("RWX peer $peerId missing required features: ${missing.joinToString(",")}")
        }
    }

    private fun missingLocalFeatures(requiredFeatures: List<String>): List<String> {
        val supported = localFeatures.toSet()
        return requiredFeatures.filterNot { supported.contains(it) }
    }

    private inner class HostProtocolHandler : ProtocolHandler<FeatureController>(Long.MAX_VALUE, Long.MAX_VALUE) {
        override fun onStartResponder(stream: Stream): CompletableFuture<FeatureController> {
            val peerId = runCatching { stream.remotePeerId().toBase58() }.getOrNull()
            val controller = FeatureController(stream, peerId)
            if (peerId != null) {
                controllersByPeer[peerId] = controller
            }
            return CompletableFuture.completedFuture(controller)
        }
    }

    private inner class ClientProtocolHandler(private val hostPeerId: String) :
        ProtocolHandler<FeatureController>(Long.MAX_VALUE, Long.MAX_VALUE) {
        override fun onStartInitiator(stream: Stream): CompletableFuture<FeatureController> {
            val controller = FeatureController(stream, hostPeerId)
            controllersByPeer[hostPeerId] = controller
            return CompletableFuture.completedFuture(controller)
        }
    }

    private inner class FeatureController(
        private val stream: Stream,
        private val remotePeerId: String?,
    ) : ProtocolMessageHandler<ByteBuf> {
        private val closed = AtomicBoolean(false)
        private val receiveBuffer = StringBuilder()

        init {
            stream.pushHandler(this)
        }

        fun send(message: FeatureMessage) {
            if (closed.get()) {
                return
            }
            val encoded = P2PJson.encodeToString(message) + "\n"
            stream.writeAndFlush(Unpooled.copiedBuffer(encoded.toByteArray(Charsets.UTF_8)))
        }

        fun close() {
            if (!closed.compareAndSet(false, true)) {
                return
            }
            runCatching { stream.close() }
        }

        override fun onActivated(stream: Stream) {
        }

        override fun onMessage(stream: Stream, msg: ByteBuf) {
            val data = ByteArray(msg.readableBytes())
            msg.readBytes(data)
            receiveBuffer.append(data.toString(Charsets.UTF_8))
            while (true) {
                val endIndex = receiveBuffer.indexOf("\n")
                if (endIndex == -1) {
                    return
                }
                val line = receiveBuffer.substring(0, endIndex).trim()
                receiveBuffer.delete(0, endIndex + 1)
                if (line.isEmpty()) {
                    continue
                }
                runCatching {
                    this@FeatureChannel.onMessage(this, P2PJson.decodeFromString<FeatureMessage>(line))
                }.onFailure { error ->
                    GameEngine.log("Failed to decode RWX feature message: ${error.message}")
                }
            }
        }

        override fun onClosed(stream: Stream) {
            close()
            remotePeerId?.let {
                controllersByPeer.remove(it, this)
                peerFeatures.remove(it)
            }
        }

        override fun onException(cause: Throwable?) {
            GameEngine.log("RWX feature channel stream error: ${cause?.message}")
            close()
        }
    }
}
