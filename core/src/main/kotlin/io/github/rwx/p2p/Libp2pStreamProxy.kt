package io.github.rwx.p2p

import io.github.rwx.logger
import io.libp2p.core.Host
import io.libp2p.core.PeerId
import io.libp2p.core.Stream
import io.libp2p.core.multiformats.Multiaddr
import io.libp2p.core.multistream.StrictProtocolBinding
import io.libp2p.protocol.ProtocolHandler
import io.libp2p.protocol.ProtocolMessageHandler
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong


class Libp2pStreamProxy(private val config: ProxyConfig = ProxyConfig()) {

    companion object {
        const val GAME_TUNNEL_PROTOCOL = "/rwx/game-tunnel/1.0.0"
        private const val DEFAULT_BUFFER_SIZE = 65536
        private const val DEFAULT_READ_TIMEOUT_MS = 30000
        private const val DEFAULT_STREAM_OPEN_TIMEOUT_MS = 10000L
        private const val DEFAULT_CONNECT_TIMEOUT_MS = 5000
        private const val DEFAULT_IDLE_TIMEOUT_MS = 120000L
        private const val IDLE_CHECK_INTERVAL_MS = 15000L
        private const val SHUTDOWN_TIMEOUT_MS = 5000L
    }

    data class ProxyConfig(
        val bufferSize: Int = DEFAULT_BUFFER_SIZE,
        val readTimeoutMs: Int = DEFAULT_READ_TIMEOUT_MS,
        val streamOpenTimeoutMs: Long = DEFAULT_STREAM_OPEN_TIMEOUT_MS,
        val connectTimeoutMs: Int = DEFAULT_CONNECT_TIMEOUT_MS,
        val idleTimeoutMs: Long = DEFAULT_IDLE_TIMEOUT_MS,
        val tcpKeepAlive: Boolean = true,
        val tcpNoDelay: Boolean = true
    )

    data class ConnectionStats(
        val hostSideRunning: Boolean,
        val clientSideRunning: Boolean,
        val activeConnections: Int,
        val totalConnections: Long,
        val totalBytesSent: Long,
        val totalBytesReceived: Long
    )

    interface ConnectionListener {
        fun onConnectionOpened(connectionId: String, peerId: String?) {}
        fun onConnectionClosed(connectionId: String, peerId: String?) {}
        fun onConnectionError(connectionId: String, peerId: String?, error: String) {}
    }

    private var executor: ScheduledExecutorService = createExecutor()
    private val connections = CopyOnWriteArrayList<TunnelConnection>()
    private val connectionListeners = CopyOnWriteArrayList<ConnectionListener>()

    private val hostSideRunning = AtomicBoolean(false)
    private val clientSideRunning = AtomicBoolean(false)
    private val totalConnectionsCounter = AtomicLong(0)
    private val totalBytesSent = AtomicLong(0)
    private val totalBytesReceived = AtomicLong(0)

    private var clientSideServerSocket: ServerSocket? = null
    private var clientSideLibp2pHost: Host? = null
    private var clientSideHostPeerId: String? = null
    private var protocolBinding: StrictProtocolBinding<GameTunnelController>? = null
    private var hostInstance: Host? = null
    private var idleCheckHandle: ScheduledFuture<*>? = null

    private fun createExecutor(): ScheduledExecutorService {
        var threadCount = 0L
        return Executors.newScheduledThreadPool(8) { r ->
            Thread(r, "Libp2pStreamProxy-${threadCount++}").also {
                it.isDaemon = true
            }
        }
    }

    @Throws(IOException::class)
    fun startHostSide(libp2pHost: Host, localPort: Int) {
        synchronized(this) {
            if (hostSideRunning.get()) {
                throw IOException("Host side proxy already running")
            }

            runCatching {
                ensureExecutorAlive()

                hostInstance = libp2pHost

                val handler = HostSideProtocolHandler(localPort)

                val binding: StrictProtocolBinding<GameTunnelController> =
                    object : StrictProtocolBinding<GameTunnelController>(
                        GAME_TUNNEL_PROTOCOL,
                        handler
                    ) {}
                protocolBinding = binding

                libp2pHost.addProtocolHandler(binding)

                hostSideRunning.set(true)
                startIdleCheck()

                logger.info {
                    "Libp2pStreamProxy (host side) registered protocol handler for $GAME_TUNNEL_PROTOCOL" +
                            "\nGame server port: $localPort"
                }
            }.onFailure { e ->
                hostInstance = null
                throw IOException("Failed to register stream handler: ${e.message}", e)
            }
        }
    }

    @Throws(IOException::class)
    fun startClientSide(
        libp2pHost: Host,
        hostPeerId: String,
        hostAddresses: List<Multiaddr> = emptyList(),
        localPort: Int = 0
    ): Int {
        synchronized(this) {
            if (clientSideRunning.get()) {
                throw IOException("Client side proxy already running")
            }

            return runCatching {
                ensureExecutorAlive()

                clientSideLibp2pHost = libp2pHost
                clientSideHostPeerId = hostPeerId

                clientSideServerSocket = ServerSocket(localPort.let { if (it > 0) it else 0 })
                val boundPort = clientSideServerSocket!!.localPort

                executor.execute {
                    acceptClientConnections(libp2pHost, hostPeerId, hostAddresses)
                }

                clientSideRunning.set(true)
                startIdleCheck()
                logger.info {
                    "Libp2pStreamProxy (client side) listening on port $boundPort\n" +
                            "Will forward connections to host $hostPeerId via libp2p"
                }
                boundPort
            }.onFailure { e ->
                clientSideServerSocket?.close()
                clientSideServerSocket = null
                throw IOException("Failed to start client side proxy: ${e.message}", e)
            }.getOrThrow()
        }
    }

    fun stop() {
        synchronized(this) {
            hostSideRunning.set(false)
            clientSideRunning.set(false)

            idleCheckHandle?.cancel(false)
            idleCheckHandle = null

            connections.forEach { it.close() }
            connections.clear()

            clientSideServerSocket?.close()
            clientSideServerSocket = null

            protocolBinding?.let { binding ->
                hostInstance?.removeProtocolHandler(binding)
            }
            hostInstance = null
            protocolBinding = null

            clientSideLibp2pHost = null
            clientSideHostPeerId = null

            shutdownAndResetExecutor()

            logger.info { "Libp2pStreamProxy stopped" }
        }
    }

    fun isRunning(): Boolean = hostSideRunning.get() || clientSideRunning.get()

    fun isHostSideRunning(): Boolean = hostSideRunning.get()

    fun isClientSideRunning(): Boolean = clientSideRunning.get()

    fun getActiveConnectionCount(): Int = connections.size

    fun getConnectedPeerIds(): Set<String> =
        connections.mapNotNull { it.remotePeerId }.toSet()

    fun getStats(): ConnectionStats = ConnectionStats(
        hostSideRunning = hostSideRunning.get(),
        clientSideRunning = clientSideRunning.get(),
        activeConnections = connections.size,
        totalConnections = totalConnectionsCounter.get(),
        totalBytesSent = totalBytesSent.get(),
        totalBytesReceived = totalBytesReceived.get()
    )

    fun addConnectionListener(listener: ConnectionListener) {
        connectionListeners.add(listener)
    }

    fun removeConnectionListener(listener: ConnectionListener) {
        connectionListeners.remove(listener)
    }

    private fun ensureExecutorAlive() {
        if (executor.isShutdown) {
            executor = createExecutor()
        }
    }

    private fun shutdownAndResetExecutor() {
        val old = executor
        executor = createExecutor()
        old.shutdown()
        try {
            if (!old.awaitTermination(SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                val dropped = old.shutdownNow()
                if (dropped.isNotEmpty()) {
                    logger.warn { "Libp2pStreamProxy: Forced executor shutdown, ${dropped.size} tasks dropped" }
                }
            }
        } catch (e: InterruptedException) {
            old.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    private fun startIdleCheck() {
        if (idleCheckHandle != null) return
        idleCheckHandle = executor.scheduleAtFixedRate({
            checkIdleConnections()
        }, IDLE_CHECK_INTERVAL_MS, IDLE_CHECK_INTERVAL_MS, TimeUnit.MILLISECONDS)
    }

    private fun checkIdleConnections() {
        val now = System.currentTimeMillis()
        connections.forEach { conn ->
            if (conn.isIdleTooLong(now)) {
                logger.warn { "Libp2pStreamProxy: Closing idle connection ${conn.id} (idle ${now - conn.lastActivityAt}ms)" }
                conn.close()
            }
        }
    }

    private fun notifyConnectionOpened(connectionId: String, peerId: String?) {
        connectionListeners.forEach { listener ->
            runCatching { listener.onConnectionOpened(connectionId, peerId) }
        }
    }

    private fun notifyConnectionClosed(connectionId: String, peerId: String?) {
        connectionListeners.forEach { listener ->
            runCatching { listener.onConnectionClosed(connectionId, peerId) }
        }
    }

    private fun notifyConnectionError(connectionId: String, peerId: String?, error: String) {
        connectionListeners.forEach { listener ->
            runCatching { listener.onConnectionError(connectionId, peerId, error) }
        }
    }

    private fun configureSocket(socket: Socket) {
        socket.apply {
            soTimeout = config.readTimeoutMs
            keepAlive = config.tcpKeepAlive
            tcpNoDelay = config.tcpNoDelay
        }
    }

    private fun acceptClientConnections(libp2pHost: Host, hostPeerId: String, hostAddresses: List<Multiaddr>) {
        val serverSocket = clientSideServerSocket
        if (serverSocket == null) {
            logger.error { "Client side server socket is null" }
            return
        }

        runCatching {
            while (!Thread.currentThread().isInterrupted && !serverSocket.isClosed && clientSideRunning.get()) {
                val clientSocket = try {
                    serverSocket.accept()
                } catch (e: Exception) {
                    if (!serverSocket.isClosed && clientSideRunning.get()) {
                        logger.error(e) { "Error accepting client connection: ${e.message}" }
                    }
                    continue
                }

                executor.execute {
                    handleClientConnection(libp2pHost, hostPeerId, hostAddresses, clientSocket)
                }
            }
        }.onFailure { e ->
            if (!serverSocket.isClosed && clientSideRunning.get()) {
                logger.error(e) { "Client connection acceptor error: ${e.message}" }
            }
        }
    }

    private fun handleClientConnection(
        libp2pHost: Host,
        hostPeerId: String,
        hostAddresses: List<Multiaddr>,
        clientSocket: Socket
    ) {
        val connectionId = "conn-${totalConnectionsCounter.incrementAndGet()}"

        runCatching {
            logger.info { "Libp2pStreamProxy: Game client connected from ${clientSocket.inetAddress.hostAddress} [$connectionId]" }

            val peerId = try {
                PeerId.fromBase58(hostPeerId)
            } catch (e: Exception) {
                val msg = "Invalid host peer ID: $hostPeerId"
                logger.error(e) { "$msg - ${e.message}" }
                notifyConnectionError(connectionId, hostPeerId, msg)
                clientSocket.close()
                return
            }

            configureSocket(clientSocket)
            val handler = ClientSideProtocolHandler(connectionId, hostPeerId, clientSocket)
            val perConnBinding =
                object : StrictProtocolBinding<GameTunnelController>(
                    GAME_TUNNEL_PROTOCOL, handler
                ) {}
            val p2pConnection = try {
                libp2pHost.network.connect(peerId, *hostAddresses.toTypedArray())
                    .orTimeout(config.streamOpenTimeoutMs, TimeUnit.MILLISECONDS).get()
            } catch (e: Exception) {
                val msg = "Failed to establish libp2p connection to host"
                logger.error(e) { "$msg: ${e.message}" }
                notifyConnectionError(connectionId, hostPeerId, msg)
                runCatching { clientSocket.close() }
                return
            }
            val streamPromise = p2pConnection.muxerSession().createStream(
                listOf(perConnBinding.toInitiator(listOf(GAME_TUNNEL_PROTOCOL)))
            )

            try {
                streamPromise.controller
                    .orTimeout(config.streamOpenTimeoutMs, TimeUnit.MILLISECONDS).get()
            } catch (e: Exception) {
                val msg = "Failed to negotiate game tunnel stream with host"
                logger.error(e) { "$msg: ${e.message}" }
                notifyConnectionError(connectionId, hostPeerId, msg)
                // onStartInitiator may have already attached the connection, so let
                // it clean up via close()
                runCatching { clientSocket.close() }
                runCatching { streamPromise.stream.getNow(null)?.close() }
                return
            }

            // From here on, data flows in both directions via the TunnelConnection
            // created inside onStartInitiator. Nothing left to do here.
        }.onFailure { e ->
            logger.error(e) { "Error handling client connection [$connectionId]: ${e.message}" }
            notifyConnectionError(connectionId, hostPeerId, e.message ?: "Unknown error")
            runCatching { clientSocket.close() }
        }
    }

    /**
     * Synchronously wires a Stream and a Socket together as a TunnelConnection and starts
     * the socket → stream relay on a worker thread. Must be called from the libp2p Netty
     * event loop (within onStartInitiator/onStartResponder) so the pushHandler call lands
     * before any peer data is dispatched to the pipeline.
     */
    private fun setupTunnel(
        stream: Stream,
        socket: Socket,
        connectionId: String,
        remotePeerId: String?
    ): GameTunnelController {
        val connection = TunnelConnection(
            id = connectionId,
            stream = stream,
            socket = socket,
            remotePeerId = remotePeerId,
            cfg = config,
            bytesSentCounter = totalBytesSent,
            bytesReceivedCounter = totalBytesReceived
        )
        connections.add(connection)
        notifyConnectionOpened(connectionId, remotePeerId)

        executor.execute {
            try {
                connection.relaySocketToStream()
            } finally {
                connection.close()
                connections.remove(connection)
                notifyConnectionClosed(connectionId, remotePeerId)
            }
        }

        return GameTunnelController(stream, connection)
    }

    private inner class TunnelConnection(
        val id: String,
        private val stream: Stream,
        private val socket: Socket,
        val remotePeerId: String?,
        private val cfg: ProxyConfig,
        private val bytesSentCounter: AtomicLong,
        private val bytesReceivedCounter: AtomicLong
    ) {
        private val buffer = ByteArray(cfg.bufferSize)
        private val closed = AtomicBoolean(false)

        @Volatile
        var lastActivityAt: Long = System.currentTimeMillis()
            private set

        private val bytesSent = AtomicLong(0)
        private val bytesReceived = AtomicLong(0)

        private val streamHandler = TunnelStreamHandler(socket, this)

        init {
            // CRITICAL: must be called synchronously from the libp2p event loop
            // so peer data arriving immediately after multistream negotiation is
            // delivered to our handler instead of being dropped.
            stream.pushHandler(streamHandler)
        }

        fun relaySocketToStream() {
            runCatching {
                while (!closed.get() && !Thread.currentThread().isInterrupted) {
                    try {
                        val bytesRead = socket.inputStream.read(buffer)
                        if (bytesRead < 0) {
                            break
                        }
                        if (bytesRead == 0) {
                            continue
                        }

                        lastActivityAt = System.currentTimeMillis()
                        bytesSent.addAndGet(bytesRead.toLong())
                        bytesSentCounter.addAndGet(bytesRead.toLong())

                        // Unpooled.copiedBuffer copies the bytes so we can keep
                        // reusing `buffer` on the next iteration safely.
                        val data = Unpooled.copiedBuffer(buffer, 0, bytesRead)
                        try {
                            stream.writeAndFlush(data)
                        } catch (e: Exception) {
                            if (!closed.get()) {
                                logger.error(e) { "Stream write error [$id]: ${e.message}" }
                            }
                            break
                        }
                    } catch (e: SocketTimeoutException) {
                        if (closed.get()) break
                        continue
                    } catch (e: IOException) {
                        if (!closed.get() && !isSocketClosedException(e)) {
                            logger.error(e) { "Socket read error [$id]: ${e.message}" }
                        }
                        break
                    }
                }
            }.onFailure { e ->
                logger.error(e) { "Socket relay error [$id]: ${e.message}" }
            }.also {
                close()
            }
        }

        fun onStreamDataReceived(length: Int) {
            lastActivityAt = System.currentTimeMillis()
            bytesReceived.addAndGet(length.toLong())
            bytesReceivedCounter.addAndGet(length.toLong())
        }

        fun isIdleTooLong(now: Long): Boolean {
            return (now - lastActivityAt) > cfg.idleTimeoutMs && !closed.get()
        }

        fun close() {
            if (!closed.compareAndSet(false, true)) return

            try {
                stream.close()
            } catch (_: Exception) {
            }

            try {
                socket.close()
            } catch (_: Exception) {
            }
        }

        private fun isSocketClosedException(e: IOException): Boolean {
            val msg = e.message?.lowercase() ?: return false
            return msg.contains("closed") || msg.contains("reset") || msg.contains("broken pipe")
        }
    }

    private inner class TunnelStreamHandler(
        private val socket: Socket,
        private val connection: TunnelConnection
    ) : ProtocolMessageHandler<ByteBuf> {

        override fun onActivated(stream: Stream) {
            logger.info { "Stream activated [${connection.id}]" }
        }

        override fun onMessage(stream: Stream, msg: ByteBuf) {
            try {
                val length = msg.readableBytes()
                if (length <= 0) return
                val data = ByteArray(length)
                msg.readBytes(data)
                socket.outputStream.write(data)
                socket.outputStream.flush()
                connection.onStreamDataReceived(length)
            } catch (e: Exception) {
                logger.error(e) { "Socket write error [${connection.id}]: ${e.message}" }
                notifyConnectionError(
                    connection.id, connection.remotePeerId,
                    e.message ?: "Socket write error"
                )
                connection.close()
            }
        }

        override fun onClosed(stream: Stream) {
            logger.info { "Stream closed [${connection.id}]" }
            connection.close()
        }

        override fun onException(cause: Throwable?) {
            logger.error(cause) { "Stream exception [${connection.id}]: ${cause?.message}" }
            notifyConnectionError(
                connection.id, connection.remotePeerId,
                cause?.message ?: "Stream exception"
            )
            connection.close()
        }
    }

    /**
     * Server-side handler. Activated when a remote peer opens a tunnel stream to us.
     * Synchronously connects to the local game-server port and wires the tunnel up
     * before returning the controller so that no inbound peer data is missed.
     */
    private inner class HostSideProtocolHandler(
        private val gameServerPort: Int
    ) : ProtocolHandler<GameTunnelController>(Long.MAX_VALUE, Long.MAX_VALUE) {

        override fun onStartResponder(stream: Stream): CompletableFuture<GameTunnelController> {
            val connectionId = "conn-${totalConnectionsCounter.incrementAndGet()}"
            val peerId = runCatching { stream.remotePeerId().toBase58() }.getOrNull()

            logger.info { "Libp2pStreamProxy: Incoming stream from ${peerId ?: "unknown"} [$connectionId]" }

            val gameSocket = try {
                Socket().apply {
                    connect(InetSocketAddress("127.0.0.1", gameServerPort), config.connectTimeoutMs)
                    configureSocket(this)
                }
            } catch (e: Exception) {
                val msg = "Failed to connect to game server on port $gameServerPort"
                logger.error(e) { msg }
                notifyConnectionError(connectionId, peerId, msg)
                runCatching { stream.close() }
                return CompletableFuture<GameTunnelController>().apply {
                    completeExceptionally(IOException(msg, e))
                }
            }

            return try {
                val controller = setupTunnel(stream, gameSocket, connectionId, peerId)
                CompletableFuture.completedFuture(controller)
            } catch (e: Exception) {
                logger.error(e) { "Error setting up host-side tunnel [$connectionId]: ${e.message}" }
                notifyConnectionError(connectionId, peerId, e.message ?: "Setup error")
                runCatching { gameSocket.close() }
                runCatching { stream.close() }
                CompletableFuture<GameTunnelController>().apply { completeExceptionally(e) }
            }
        }
    }

    /**
     * Client-side handler. Each accepted local socket gets its own handler instance,
     * which it then drives via a per-connection binding fed into muxerSession.createStream.
     */
    private inner class ClientSideProtocolHandler(
        private val connectionId: String,
        private val hostPeerId: String,
        private val clientSocket: Socket
    ) : ProtocolHandler<GameTunnelController>(Long.MAX_VALUE, Long.MAX_VALUE) {

        override fun onStartInitiator(stream: Stream): CompletableFuture<GameTunnelController> {
            return try {
                val controller = setupTunnel(stream, clientSocket, connectionId, hostPeerId)
                CompletableFuture.completedFuture(controller)
            } catch (e: Exception) {
                logger.error(e) { "Error setting up client-side tunnel [$connectionId]: ${e.message}" }
                notifyConnectionError(connectionId, hostPeerId, e.message ?: "Setup error")
                runCatching { clientSocket.close() }
                runCatching { stream.close() }
                CompletableFuture<GameTunnelController>().apply { completeExceptionally(e) }
            }
        }
    }

    class GameTunnelController(
        val stream: Stream,
        private val connection: Any? = null
    ) {
        private val closed = AtomicBoolean(false)

        fun close() {
            if (closed.compareAndSet(false, true)) {
                runCatching { stream.close() }
            }
        }

        fun isClosed(): Boolean = closed.get()
    }
}
