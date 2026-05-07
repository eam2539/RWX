package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import dev.onvoid.webrtc.CreateSessionDescriptionObserver
import dev.onvoid.webrtc.PeerConnectionFactory
import dev.onvoid.webrtc.PeerConnectionObserver
import dev.onvoid.webrtc.RTCAnswerOptions
import dev.onvoid.webrtc.RTCConfiguration
import dev.onvoid.webrtc.RTCDataChannel
import dev.onvoid.webrtc.RTCDataChannelBuffer
import dev.onvoid.webrtc.RTCDataChannelInit
import dev.onvoid.webrtc.RTCDataChannelObserver
import dev.onvoid.webrtc.RTCDataChannelState
import dev.onvoid.webrtc.RTCIceCandidate
import dev.onvoid.webrtc.RTCIceServer
import dev.onvoid.webrtc.RTCOfferOptions
import dev.onvoid.webrtc.RTCPeerConnection
import dev.onvoid.webrtc.RTCPeerConnectionState
import dev.onvoid.webrtc.RTCSessionDescription
import dev.onvoid.webrtc.RTCSdpType
import dev.onvoid.webrtc.SetSessionDescriptionObserver
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class WebRtcTunnelProxy(private val config: ProxyConfig = ProxyConfig()) {
    data class ProxyConfig(
        val bufferSize: Int = 65536,
        val openTimeoutMs: Long = 20000L,
        val socketConnectTimeoutMs: Int = 5000,
        val socketReadTimeoutMs: Int = 30000,
        val executorThreads: Int = 4
    )

    data class Signal(
        var magic: String = "rwx-p2p-webrtc",
        var schema: Int = 1,
        var roomId: String? = null,
        var sessionId: String? = null,
        var fromPeerId: String? = null,
        var toPeerId: String? = null,
        var type: String? = null,
        var sdpType: String? = null,
        var sdp: String? = null,
        var candidateSdpMid: String? = null,
        var candidateSdpMLineIndex: Int = 0,
        var candidateSdp: String? = null,
    ) {
        fun isValid(): Boolean {
            return magic == "rwx-p2p-webrtc" && schema == 1 &&
                    !roomId.isNullOrBlank() && !sessionId.isNullOrBlank() &&
                    !fromPeerId.isNullOrBlank() && !toPeerId.isNullOrBlank() && !type.isNullOrBlank()
        }
    }

    private data class ClientSession(
        val roomId: String,
        val sessionId: String,
        val localPeerId: String,
        val remotePeerId: String,
        val peerConnection: RTCPeerConnection,
        var dataChannel: RTCDataChannel? = null,
        var socket: Socket? = null,
        val openFuture: CompletableFuture<Unit> = CompletableFuture(),
        val pendingCandidates: MutableList<RTCIceCandidate> = CopyOnWriteArrayList(),
        val closed: AtomicBoolean = AtomicBoolean(false)
    )

    private var factory: PeerConnectionFactory? = null
    private var executor: ScheduledExecutorService = createExecutor()
    private val sessions = ConcurrentHashMap<String, ClientSession>()
    private var hostRoomId: String? = null
    private var hostPeerId: String? = null
    private var hostGamePort: Int = 0
    private var signalSender: ((Signal) -> Unit)? = null
    private var iceServerUrls: List<String> = DEFAULT_ICE_SERVERS
    private val pendingIceSignals = ConcurrentHashMap<String, MutableList<Signal>>()
    private var clientServerSocket: ServerSocket? = null
    private var clientRunning = AtomicBoolean(false)

    companion object {
        val DEFAULT_ICE_SERVERS = listOf(
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302"
        )
    }

    private fun createExecutor(): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(config.executorThreads.coerceAtLeast(1)) { r ->
            Thread(r, "WebRtcTunnelProxy").also { it.isDaemon = true }
        }
    }

    fun startHostSide(
        roomId: String,
        localPeerId: String,
        gamePort: Int,
        iceServers: List<String>,
        sendSignal: (Signal) -> Unit
    ) {
        ensureFactory()
        hostRoomId = roomId
        hostPeerId = localPeerId
        hostGamePort = gamePort
        iceServerUrls = normalizeIceServers(iceServers)
        signalSender = sendSignal
    }

    fun startClientSide(
        roomId: String,
        localPeerId: String,
        hostPeerId: String,
        iceServers: List<String>,
        sendSignal: (Signal) -> Unit
    ): Int {
        ensureFactory()
        iceServerUrls = normalizeIceServers(iceServers)
        signalSender = sendSignal
        clientServerSocket = ServerSocket(0)
        val port = clientServerSocket!!.localPort
        clientRunning.set(true)
        executor.execute {
            acceptClientSockets(roomId, localPeerId, hostPeerId)
        }
        return port
    }

    fun stop() {
        clientRunning.set(false)
        runCatching { clientServerSocket?.close() }
        clientServerSocket = null
        sessions.values.forEach { closeSession(it) }
        sessions.clear()
        hostRoomId = null
        hostPeerId = null
        hostGamePort = 0
    }

    fun handleSignal(signal: Signal) {
        if (!signal.isValid()) return
        val localPeerId = hostPeerId
        if (localPeerId != null && signal.toPeerId == localPeerId && signal.type == "offer") {
            handleHostOffer(signal, localPeerId)
            return
        }
        val sessionId = signal.sessionId ?: return
        val session = sessions[sessionId]
        if (session == null) {
            if (signal.type == "ice") {
                var signals = pendingIceSignals[sessionId]
                if (signals == null) {
                    val newSignals = CopyOnWriteArrayList<Signal>()
                    signals = pendingIceSignals.putIfAbsent(sessionId, newSignals) ?: newSignals
                }
                signals.add(signal)
            }
            return
        }
        when (signal.type) {
            "answer" -> handleClientAnswer(session, signal)
            "ice" -> addRemoteIce(session, signal)
        }
    }

    private fun acceptClientSockets(roomId: String, localPeerId: String, remotePeerId: String) {
        val serverSocket = clientServerSocket ?: return
        while (clientRunning.get() && !serverSocket.isClosed) {
            val socket = try {
                serverSocket.accept()
            } catch (_: Exception) {
                break
            }
            executor.execute {
                startClientSession(roomId, localPeerId, remotePeerId, socket)
            }
        }
    }

    private fun startClientSession(roomId: String, localPeerId: String, remotePeerId: String, socket: Socket) {
        val sessionId = UUID.randomUUID().toString()
        val session = createSession(roomId, sessionId, localPeerId, remotePeerId)
        session.socket = socket
        sessions[sessionId] = session
        applyPendingIceSignals(session)

        val dataChannel = session.peerConnection.createDataChannel("rwx-game", RTCDataChannelInit().apply {
            ordered = true
        })
        session.dataChannel = dataChannel
        registerDataChannel(session, dataChannel)

        session.peerConnection.createOffer(RTCOfferOptions(), object : CreateSessionDescriptionObserver {
            override fun onSuccess(description: RTCSessionDescription) {
                session.peerConnection.setLocalDescription(description, object : SetSessionDescriptionObserver {
                    override fun onSuccess() {
                        sendSessionDescription(session, "offer", description)
                    }

                    override fun onFailure(error: String) {
                        closeSession(session)
                    }
                })
            }

            override fun onFailure(error: String) {
                closeSession(session)
            }
        })

        try {
            session.openFuture.get(config.openTimeoutMs, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            GameEngine.log("WebRTC tunnel failed: ${e.message}")
            closeSession(session)
        }
    }

    private fun handleHostOffer(signal: Signal, localPeerId: String) {
        if (signal.roomId != hostRoomId || signal.sdp.isNullOrBlank() || signal.sdpType.isNullOrBlank()) return
        val remotePeerId = signal.fromPeerId ?: return
        val sessionId = signal.sessionId ?: return
        val session = createSession(signal.roomId!!, sessionId, localPeerId, remotePeerId)
        sessions[sessionId] = session
        applyPendingIceSignals(session)

        val remoteDescription = RTCSessionDescription(RTCSdpType.valueOf(signal.sdpType!!), signal.sdp!!)
        session.peerConnection.setRemoteDescription(remoteDescription, object : SetSessionDescriptionObserver {
            override fun onSuccess() {
                flushPendingCandidates(session)
                session.peerConnection.createAnswer(RTCAnswerOptions(), object : CreateSessionDescriptionObserver {
                    override fun onSuccess(description: RTCSessionDescription) {
                        session.peerConnection.setLocalDescription(description, object : SetSessionDescriptionObserver {
                            override fun onSuccess() {
                                sendSessionDescription(session, "answer", description)
                            }

                            override fun onFailure(error: String) {
                                closeSession(session)
                            }
                        })
                    }

                    override fun onFailure(error: String) {
                        closeSession(session)
                    }
                })
            }

            override fun onFailure(error: String) {
                closeSession(session)
            }
        })
    }

    private fun handleClientAnswer(session: ClientSession, signal: Signal) {
        if (signal.sdp.isNullOrBlank() || signal.sdpType.isNullOrBlank()) return
        val description = RTCSessionDescription(RTCSdpType.valueOf(signal.sdpType!!), signal.sdp!!)
        session.peerConnection.setRemoteDescription(description, object : SetSessionDescriptionObserver {
            override fun onSuccess() {
                flushPendingCandidates(session)
            }

            override fun onFailure(error: String) {
                closeSession(session)
            }
        })
    }

    private fun addRemoteIce(session: ClientSession, signal: Signal) {
        val sdp = signal.candidateSdp ?: return
        val candidate = RTCIceCandidate(signal.candidateSdpMid, signal.candidateSdpMLineIndex, sdp)
        if (session.peerConnection.getRemoteDescription() == null) {
            session.pendingCandidates += candidate
        } else {
            session.peerConnection.addIceCandidate(candidate)
        }
    }

    private fun applyPendingIceSignals(session: ClientSession) {
        pendingIceSignals.remove(session.sessionId)?.forEach { addRemoteIce(session, it) }
    }

    private fun flushPendingCandidates(session: ClientSession) {
        session.pendingCandidates.forEach { session.peerConnection.addIceCandidate(it) }
        session.pendingCandidates.clear()
    }

    private fun createSession(
        roomId: String,
        sessionId: String,
        localPeerId: String,
        remotePeerId: String
    ): ClientSession {
        val peerConnection = factory!!.createPeerConnection(createRtcConfig(), object : PeerConnectionObserver {
            override fun onIceCandidate(candidate: RTCIceCandidate) {
                signalSender?.invoke(
                    Signal(
                        roomId = roomId,
                        sessionId = sessionId,
                        fromPeerId = localPeerId,
                        toPeerId = remotePeerId,
                        type = "ice",
                        candidateSdpMid = candidate.sdpMid,
                        candidateSdpMLineIndex = candidate.sdpMLineIndex,
                        candidateSdp = candidate.sdp
                    )
                )
            }

            override fun onDataChannel(dataChannel: RTCDataChannel) {
                val session = sessions[sessionId] ?: return
                session.dataChannel = dataChannel
                registerDataChannel(session, dataChannel)
            }

            override fun onConnectionChange(state: RTCPeerConnectionState) {
                if (state == RTCPeerConnectionState.FAILED || state == RTCPeerConnectionState.CLOSED) {
                    sessions[sessionId]?.let { closeSession(it) }
                }
            }
        })
        return ClientSession(roomId, sessionId, localPeerId, remotePeerId, peerConnection)
    }

    private fun registerDataChannel(session: ClientSession, dataChannel: RTCDataChannel) {
        dataChannel.registerObserver(object : RTCDataChannelObserver {
            override fun onBufferedAmountChange(previousAmount: Long) {}

            override fun onStateChange() {
                if (dataChannel.getState() == RTCDataChannelState.OPEN) {
                    if (session.socket == null && hostGamePort > 0) {
                        session.socket = Socket().apply {
                            connect(InetSocketAddress("127.0.0.1", hostGamePort), config.socketConnectTimeoutMs)
                            soTimeout = config.socketReadTimeoutMs
                            tcpNoDelay = true
                        }
                    }
                    session.openFuture.complete(Unit)
                    session.socket?.let {
                        executor.execute { relaySocketToDataChannel(session) }
                    }
                }
            }

            override fun onMessage(buffer: RTCDataChannelBuffer) {
                val socket = session.socket ?: return
                val data = ByteArray(buffer.data.remaining())
                buffer.data.get(data)
                try {
                    socket.outputStream.write(data)
                    socket.outputStream.flush()
                } catch (_: Exception) {
                    closeSession(session)
                }
            }
        })
    }

    private fun relaySocketToDataChannel(session: ClientSession) {
        val socket = session.socket ?: return
        val dataChannel = session.dataChannel ?: return
        val buffer = ByteArray(config.bufferSize)
        try {
            while (!session.closed.get()) {
                val length = try {
                    socket.inputStream.read(buffer)
                } catch (e: SocketTimeoutException) {
                    continue
                }
                if (length < 0) break
                if (length == 0) continue
                dataChannel.send(RTCDataChannelBuffer(ByteBuffer.wrap(buffer.copyOf(length)), true))
            }
        } catch (_: Exception) {
        } finally {
            closeSession(session)
        }
    }

    private fun sendSessionDescription(session: ClientSession, type: String, description: RTCSessionDescription) {
        signalSender?.invoke(
            Signal(
                roomId = session.roomId,
                sessionId = session.sessionId,
                fromPeerId = session.localPeerId,
                toPeerId = session.remotePeerId,
                type = type,
                sdpType = description.sdpType.name,
                sdp = description.sdp
            )
        )
    }

    private fun closeSession(session: ClientSession) {
        if (!session.closed.compareAndSet(false, true)) return
        sessions.remove(session.sessionId)
        pendingIceSignals.remove(session.sessionId)
        runCatching { session.socket?.close() }
        runCatching { session.dataChannel?.close() }
        runCatching { session.dataChannel?.dispose() }
        runCatching { session.peerConnection.close() }
    }

    private fun ensureFactory() {
        if (factory == null) {
            factory = try {
                PeerConnectionFactory()
            } catch (e: Throwable) {
                throw IOException("Failed to initialize WebRTC native library: ${e.message}", e)
            }
        }
        if (executor.isShutdown) {
            executor = createExecutor()
        }
    }

    private fun createRtcConfig(): RTCConfiguration {
        return RTCConfiguration().apply {
            normalizeIceServers(iceServerUrls).forEach { url ->
                iceServers.add(createIceServer(url))
            }
        }
    }

    private fun createIceServer(value: String): RTCIceServer {
        val normalized = normalizeTurnCredentialUrl(value)
        val server = RTCIceServer()
        server.urls.add(normalized.url)
        if (normalized.username != null) server.username = normalized.username
        if (normalized.password != null) server.password = normalized.password
        return server
    }

    private data class NormalizedIceServer(
        val url: String,
        val username: String? = null,
        val password: String? = null
    )

    private fun normalizeTurnCredentialUrl(value: String): NormalizedIceServer {
        if (!value.startsWith("turn:") && !value.startsWith("turns:")) return NormalizedIceServer(value)
        return runCatching {
            val scheme = value.substringBefore(':')
            val rest = value.substringAfter(':')
            val userInfo = rest.substringBefore('@')
            if (userInfo == rest) return@runCatching NormalizedIceServer(value)
            val hostPart = rest.substringAfter('@')
            val username = URI("x://$userInfo").userInfo?.substringBefore(':') ?: userInfo.substringBefore(':')
            val password = URI("x://$userInfo").userInfo?.substringAfter(':', "") ?: userInfo.substringAfter(':', "")
            NormalizedIceServer("$scheme:$hostPart", username, password)
        }.getOrElse {
            NormalizedIceServer(value)
        }
    }

    private fun normalizeIceServers(values: List<String>): List<String> {
        val servers = values.map { it.trim() }
            .filter { it.startsWith("stun:") || it.startsWith("turn:") || it.startsWith("turns:") }
            .distinct()
        return servers.ifEmpty { DEFAULT_ICE_SERVERS }
    }
}
