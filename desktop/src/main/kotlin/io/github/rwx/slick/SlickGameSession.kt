package io.github.rwx.slick

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.PlatformCallbacks
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
import io.github.rwx.DesktopRendererMode
import io.github.rwx.PlatformStorage
import io.github.rwx.logger
import io.github.rwx.platform.CoreGameView
import io.github.rwx.render.RendererMode
import io.github.rwx.render.canvas.*
import io.github.rwx.session.*
import io.github.rwx.ui.CoreUiEventQueue
import java.awt.Canvas
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import javax.swing.SwingUtilities
import kotlin.concurrent.withLock

class SlickGameSession(
    private val storage: PlatformStorage,
    registerShutdownHook: Boolean = true,
) : GameSession() {
    override val rendererMode: RendererMode= DesktopRendererMode.Slick
    private val lock = Any()
    private val frameLock = Any()
    private val running = AtomicBoolean(false)
    private val requestState = SlickSessionRequestState()
    private val stopRequested = AtomicBoolean(false)

    private val loadingStatusTracker = SlickLoadingStatusTracker()
    private val textInputBridge = SlickTextInputBridge()
    private val engineReadyLock = ReentrantLock()
    private val engineReadyCondition = engineReadyLock.newCondition()
    @Volatile
    private var game: SlickGame? = null
    @Volatile
    private var container: EmbeddedSlickGameContainer? = null
    @Volatile
    private var renderThread: Thread? = null

    fun activeGame(): SlickGame? = if (running.get()) game else null

    @Volatile
    private var lastSlickViewport: KoolCanvasViewport = KoolCanvasViewport(1280, 720)

    @Volatile
    private var frameSnapshotState: SlickFrameSnapshotState = SlickFrameSnapshotState.empty(lastSlickViewport)
    @Volatile
    private var rendererStartupError: Throwable? = null

    @Volatile
    private var gameVisible: Boolean = false

    @Volatile
    private var pausedBackground: Boolean = false

    init {
        SlickCanvasHost.setResizeController(::resizeFromCanvasHost)
        SlickCanvasHost.setRendererShutdown(::stopRendererAndWait)
        configureRendererProfile(
            GameSessionRendererProfile(
                rendersIntoKoolCanvas = false,
                acceptsKoolInput = true,
                canStartNewSessionInPlace = true,
                usesNativeSurfaceForResumeBackground = false,
            )
        )
        if (registerShutdownHook) {
            Runtime.getRuntime().addShutdownHook(
                Thread({
                    stop()
                }, "RWX-slick-embedded-shutdown")
            )
        }
    }

    fun start(
        canvas: Canvas,
        request: SlickSessionRequest,
        viewport: KoolCanvasViewport,
        visible: Boolean,
        pausedBackground: Boolean,
    ): SlickGame {
        val width = viewport.width.coerceAtLeast(320)
        val height = viewport.height.coerceAtLeast(240)
        val createdGame = synchronized(lock) {
            if (running.get()) {
                return game ?: error("Slick runtime is stopping")
            }
            stopRequested.set(false)
            configureLegacyDesktopPlatform()
            storage.createDirectories()
            createGame(request, width, height).also { newGame ->
                game = newGame
                running.set(true)
            }
        }

        try {
            Thread({
                var containerToDestroy: EmbeddedSlickGameContainer? = null
                try {
                    configureLwjgl2Natives()
                    waitForDisplayableCanvas(canvas)
                    if (stopRequested.get()) return@Thread
                    val displayWidth = canvas.width.takeIf { it > 0 } ?: width
                    val displayHeight = canvas.height.takeIf { it > 0 } ?: height
                    createdGame.requestSize(displayWidth, displayHeight)
                    val createdContainer = EmbeddedSlickGameContainer(
                        createdGame,
                        displayWidth.coerceAtLeast(320),
                        displayHeight.coerceAtLeast(240),
                        false,
                        parentCanvas = canvas,
                        onParentCanvasResize = createdGame::requestSize,
                    )
                    containerToDestroy = createdContainer
                    synchronized(lock) {
                        container = createdContainer
                    }
                    createdContainer.setDisplayMode(
                        displayWidth.coerceAtLeast(320),
                        displayHeight.coerceAtLeast(240),
                        false,
                    )
                    createdContainer.setForceExit(false)
                    createdContainer.setAlwaysRender(true)
                    createdContainer.setUpdateOnlyWhenVisible(false)
                    createdContainer.setShowFPS(false)
                    createdContainer.setTargetFrameRate(300)
                    SlickCanvasHost.requestGameFocus()
                    if (stopRequested.get()) {
                        createdContainer.requestExitFromAnyThread()
                    }
                    createdContainer.start()
                } catch (error: Throwable) {
                    handleRendererStartupError(error)
                    handleMapError(request.loadingPath.orEmpty(), error)
                } finally {
                    // Linux JAWT surfaces retain their creator thread's JNIEnv, so release before this thread exits.
                    runCatching { containerToDestroy?.destroy() }
                        .onFailure { error -> logger.warn(error) { "Failed to destroy the embedded Slick container" } }
                    synchronized(lock) {
                        container = null
                        game = null
                        renderThread = null
                    }
                    stopRequested.set(false)
                    running.set(false)
                    handleRendererStopped()
                }
            }, RENDER_THREAD_NAME).apply {
                isDaemon = true
                synchronized(lock) {
                    renderThread = this
                }
                start()
            }

            createdGame.setGameVisible(visible, pausedBackground)
            return createdGame
        } catch (error: Throwable) {
            synchronized(lock) {
                game = null
                container = null
                renderThread = null
            }
            stopRequested.set(false)
            running.set(false)
            throw error
        }
    }

    fun stop() {
        val containerToStop = synchronized(lock) {
            stopRequested.set(true)
            game = null
            container.also { container = null }
        }
        containerToStop?.requestExitFromAnyThread()
    }
    fun stopAndWait(timeoutMillis: Long) {
        val thread = synchronized(lock) { renderThread }
        stop()
        if (thread == null || thread === Thread.currentThread() || !thread.isAlive) return
        runCatching { thread.join(timeoutMillis) }
        if (thread.isAlive) {
            logger.warn { "Slick render thread did not exit within ${timeoutMillis}ms of shutdown" }
        }
    }
    private fun createGame(request: SlickSessionRequest, width: Int, height: Int): SlickGame =
        SlickGame(
            graphicsEngine = SlickGraphicsEngine(storage),
            gameSession = this,
            initialWidth = width,
            initialHeight = height,
            initialRequest = request,
            onTextInputRequest = textInputBridge::deliver,
            onMapReady = ::handleMapReady,
            onMapError = ::handleMapError,
            onLoadingStatus = ::handleLoadingStatus,
        )
    private fun configureLwjgl2Natives() {
        System.setProperty("org.lwjgl.opengl.contextAPI", "native")
        val nativesDir = System.getProperty("rwx.slick.nativesDir")
            ?: System.getenv("RWX_SLICK_NATIVES_DIR")
            ?: return
        System.setProperty("org.lwjgl.librarypath", nativesDir)
        System.setProperty("java.library.path", nativesDir)
    }

    private fun waitForDisplayableCanvas(canvas: Canvas) {
        val deadline = System.nanoTime() + CANVAS_DISPLAY_TIMEOUT_NANOS
        while (System.nanoTime() < deadline) {
            var ready = false
            runCatching {
                if (SwingUtilities.isEventDispatchThread()) {
                    ready = canvas.isDisplayable && canvas.isShowing && canvas.width > 0 && canvas.height > 0
                } else {
                    SwingUtilities.invokeAndWait {
                        canvas.addNotify()
                        canvas.requestFocusInWindow()
                        ready = canvas.isDisplayable && canvas.isShowing && canvas.width > 0 && canvas.height > 0
                    }
                }
            }
            if (ready) return
            Thread.sleep(CANVAS_RETRY_MILLIS)
        }
        check(canvas.isDisplayable && canvas.width > 0 && canvas.height > 0) {
            "Slick parent canvas was not displayable"
        }
    }
    override fun ensureRendererEngine(
        viewport: KoolCanvasViewport,
        graphicsEngine: GraphicsEngine,
        view: CoreGameView,
        platformCallbacks: PlatformCallbacks?,
    ): GameEngine =
        runCatching {
            super.ensureRendererEngine(viewport, graphicsEngine, view, platformCallbacks)
        }.onSuccess {
            rendererStartupError = null
            synchronized(lock) {
                if (requestState.desired is SlickSessionRequest.EnginePreparation) {
                    requestState.clearRequest()
                }
            }
            signalEngineStartup()
        }.onFailure { error ->
            rendererStartupError = error
            signalEngineStartup()
        }.getOrThrow()

    override fun prepareMapAsync(mapPath: String?, viewport: KoolCanvasViewport) {
        val requestedMapPath = mapPath?.takeIf { it.isNotBlank() } ?: return
        if (isMapLoaded(requestedMapPath)) return
        beginRendererMapPreparation(requestedMapPath)
        prepareRendererRequest(
            viewport,
            SlickSessionRequest.Map(requestedMapPath),
        )
        startSlickGameIfNeeded()
    }

    override fun prepareMemorySnapshotAsync(snapshot: GameMemorySnapshot, viewport: KoolCanvasViewport) {
        if (isMapLoaded(snapshot.mapPath)) return
        beginRendererMapPreparation(
            mapPath = snapshot.mapPath,
            memorySnapshot = snapshot,
            battleRoomConfig = snapshot.battleRoomConfig,
        )
        prepareRendererRequest(
            viewport,
            SlickSessionRequest.MemorySnapshot(snapshot),
        )
        startSlickGameIfNeeded()
    }

    override fun prepareBattleRoomAsync(config: BattleRoomLaunchConfig, viewport: KoolCanvasViewport) {
        val requestedMapPath = config.mapPath.takeIf { it.isNotBlank() } ?: return
        if (isMapLoaded(requestedMapPath) && activeRendererBattleRoomConfig() == config) return
        beginRendererMapPreparation(
            mapPath = requestedMapPath,
            battleRoomConfig = config,
        )
        prepareRendererRequest(
            viewport,
            SlickSessionRequest.BattleRoom(config),
        )
        startSlickGameIfNeeded()
    }

   override fun updateFrame(
        viewport: KoolCanvasViewport,
        deltaSeconds: Float,
        drainVisibleLayerBuffers: Boolean,
    ): KoolCanvasFrame {
       lastSlickViewport = normalizedViewport(viewport)
       lastViewport = lastSlickViewport
       syncRequestedMapFromSession()
        startSlickGameIfNeeded()
        pollSlickFrameSnapshot()
        activeGame()?.let(textInputBridge::poll)
        return currentFrame()
    }

    override fun currentFrame(): KoolCanvasFrame {
        val viewport = lastSlickViewport
        val snapshotState = frameSnapshotState
        if (!snapshotState.isAvailable) {
            return emptyFrameFor(viewport)
        }
        val frame = snapshotState.frame
        return if (frame.viewport == viewport) {
            frame
        } else {
            frameForSnapshotTexture(viewport, snapshotState.width, snapshotState.height)
        }
    }

    override fun loadPendingMapNow(): KoolCanvasFrame {
        syncRequestedMapFromSession()
        startSlickGameIfNeeded()
        pollSlickFrameSnapshot()
        return currentFrame()
    }

    protected override fun ensureStarted(viewport: KoolCanvasViewport): GameEngine {
        synchronized(gameLock) {
            activeEngineLocked()?.let { return it }
        }
        val requestedViewport = normalizedViewport(viewport)
        synchronized(lock) {
            lastSlickViewport = requestedViewport
            lastViewport = requestedViewport
            if (requestState.desired == null) {
                requestState.replace(SlickSessionRequest.EnginePreparation)
            }
            rendererStartupError = null
        }

        val canvasDeadline = deadlineAfterMillis(SLICK_CANVAS_INSTALL_TIMEOUT_MILLIS)
        while (SlickCanvasHost.gameCanvas() == null && System.nanoTime() < canvasDeadline) {
            Thread.sleep(SLICK_ENGINE_START_POLL_MILLIS)
        }
        val canvas = checkNotNull(SlickCanvasHost.gameCanvas()) {
            "Slick game canvas was not installed within ${SLICK_CANVAS_INSTALL_TIMEOUT_MILLIS}ms"
        }
        val exposedCanvasForStartup = !canvas.isShowing
        if (exposedCanvasForStartup) {
            // AWTGLCanvas cannot create its GL context while hidden. Keep the Kool loading window
            // above it so engine preloading can initialize the native renderer without a UI flash.
            SlickCanvasHost.setGameVisible(visible = true, koolOverlay = true)
        }

        try {
            startSlickGameIfNeeded()
            val engineDeadline = deadlineAfterMillis(SLICK_ENGINE_INITIALIZATION_TIMEOUT_MILLIS)
            engineReadyLock.withLock {
                while (true) {
                    synchronized(gameLock) {
                        activeEngineLocked()?.let { return it }
                    }
                    rendererStartupError?.let { error ->
                        throw IllegalStateException("Unable to start the Slick renderer", error)
                    }
                    val remainingNanos = engineDeadline - System.nanoTime()
                    check(remainingNanos > 0L) {
                        "Slick renderer did not initialize within ${SLICK_ENGINE_INITIALIZATION_TIMEOUT_MILLIS}ms"
                    }
                    engineReadyCondition.await(remainingNanos, TimeUnit.NANOSECONDS)
                }

            }

        } finally {
            if (exposedCanvasForStartup && !gameVisible) {
                SlickCanvasHost.setGameVisible(visible = false)
            }
        }
        return gameEngine!!
    }

    protected override fun applyViewport(engine: GameEngine, viewport: KoolCanvasViewport) {
        val requestedViewport = normalizedViewport(viewport)
        lastSlickViewport = requestedViewport
        lastViewport = requestedViewport
        val activeGame = activeGame()
        activeGame?.requestSize(
            requestedViewport.width.coerceAtLeast(320),
            requestedViewport.height.coerceAtLeast(240),
        )
    }

    override fun discardRunningGame() {
        super.discardRunningGame()
        stopRenderer()
    }

    override fun loadingStatus(): GameLoadingStatus {
        return loadingStatusTracker.mergeWith(super.loadingStatus())
    }

    override suspend fun requestReloadMods(): Boolean {
        val activeCanvas = SlickCanvasHost.gameCanvas()
        val exposedCanvasForReload = activeCanvas != null && !activeCanvas.isShowing
        val activeGame = activeGame() ?: return false
        val request = activeGame.modReloadQueue.request()
        if (exposedCanvasForReload) {
            SlickCanvasHost.setGameVisible(visible = true, koolOverlay = true)
        }
        try {
            request.await()
        } finally {
            if (!request.isCompleted) {
                activeGame.modReloadQueue.cancelPending(request)
            }
            if (exposedCanvasForReload && !gameVisible) {
                SlickCanvasHost.setGameVisible(visible = false)
            }
        }
        return true
    }

    // The Slick render thread owns the engine while it is alive and does not take `gameLock`, so
    // the base implementations would mutate the engine concurrently with `gameLoop()`. Hand these
    // to the render thread instead, and only fall back to the direct path when it is not running.

    override fun requestSaveGame(name: String) {
        val game = activeGame()
        if (game != null) game.requestSaveGame(name) else super.requestSaveGame(name)
    }

    override fun requestExportMap(name: String) {
        val game = activeGame()
        if (game != null) game.requestExportMap(name) else super.requestExportMap(name)
    }

    override fun requestSurrender() {
        val game = activeGame()
        if (game != null) game.requestSurrender() else super.requestSurrender()
    }

    override fun requestChatMessage(message: String, teamOnly: Boolean) {
        val game = activeGame()
        if (game != null) game.requestChatMessage(message, teamOnly) else super.requestChatMessage(message, teamOnly)
    }


    private fun stopRenderer() {
        synchronized(lock) {
            requestState.clearRequest()
            rendererStartupError = IllegalStateException("Slick renderer stopped before startup completed")
            gameVisible = false
            pausedBackground = false
        }
        clearFrameSnapshot(unregisterTexture = true)
        markRendererSurfaceStopped()
        stop()
        signalEngineStartup()
    }

    fun stopRendererAndWait(timeoutMillis: Long = RENDER_THREAD_JOIN_TIMEOUT_MILLIS) {
        synchronized(lock) {
            requestState.clearRequest()
            rendererStartupError = IllegalStateException("Slick renderer stopped before startup completed")
            gameVisible = false
            pausedBackground = false
        }
        clearFrameSnapshot(unregisterTexture = true)
        markRendererSurfaceStopped()
        signalEngineStartup()
        stopAndWait(timeoutMillis)
    }

    override fun setGameVisible(
        visible: Boolean,
        viewport: KoolCanvasViewport,
        koolOverlay: Boolean,
        pausedBackground: Boolean,
    ) {
        val shouldCaptureExitFrame = gameVisible && !visible
        lastSlickViewport = normalizedViewport(viewport)
        lastViewport = lastSlickViewport
        gameVisible = visible
        this.pausedBackground = pausedBackground
        syncRequestedMapFromSession()
        val activeGame = activeGame()

        if (shouldCaptureExitFrame && pausedBackground) {
            // Prime the pending snapshot with a world-only render. The regular game loop must
            // remain stopped while a paused/resume background is captured.
            activeGame?.setGameVisible(true, pausedBackground = true)
        }
        if (shouldCaptureExitFrame) {
            activeGame?.requestFrameSnapshot(EXIT_FRAME_SNAPSHOT_TIMEOUT_MILLIS)?.let { snapshot ->
                updateFrameSnapshot(snapshot)
            }
        }
        SlickCanvasHost.setGameVisible(visible, koolOverlay)
        activeGame?.setGameVisible(visible, pausedBackground)
        if (visible && !koolOverlay) {
            SlickCanvasHost.requestGameFocus()
        }
        if (visible) {
            startSlickGameIfNeeded()
        }
    }

    override fun submitPointer(
        screenX: Float,
        screenY: Float,
        isDown: Boolean,
        pointerId: Int,
    ) {
        activeGame()?.submitOverlayPointer(screenX, screenY, isDown, pointerId)
    }

    override fun movePointer(screenX: Float, screenY: Float) {
        activeGame()?.moveOverlayPointer(screenX, screenY)
    }

    override fun submitMouseWheel(amount: Int) {
        if (amount != 0) activeGame()?.submitOverlayMouseWheel(amount)
    }

    override fun adoptStartedGameFromEngine(viewport: KoolCanvasViewport): Boolean {
        val mapPath = synchronized(gameLock) {
            val engine = activeEngineLocked() ?: return@synchronized null
            val networkEngine = engine.networkEngine ?: return@synchronized null
            if (!networkEngine.hasActiveStartedGameConnection()) return@synchronized null
            networkEngine.selectedMapPath?.takeIf { it.isNotBlank() }
                ?: engine.currentMapPath?.takeIf { it.isNotBlank() }
        } ?: return false
        val normalizedViewport = normalizedViewport(viewport)
        val canLaunch = activeGame() != null || SlickCanvasHost.gameCanvas() != null
        if (!canLaunch) {
            logger.warn { "Unable to adopt started Slick game without an installed canvas: $mapPath" }
            return false
        }

        synchronized(lock) {
            lastSlickViewport = normalizedViewport
            lastViewport = normalizedViewport
            requestState.replace(SlickSessionRequest.StartedGame(mapPath))
        }
        clearFrameSnapshot(unregisterTexture = true)
        beginRendererStartedGamePreparation(mapPath)
        logger.info { "Adopting started battle room in embedded Slick renderer: $mapPath" }
        startSlickGameIfNeeded()
        return true
    }

    override fun prepareMenuBackgroundAsync(viewport: KoolCanvasViewport) {
        synchronized(lock) {
            if (menuBackgroundActive || requestState.desired is SlickSessionRequest.MenuBackground) {
                return
            }
        }
        beginRendererStandalonePreparation(MENU_BACKGROUND_REQUEST)
        prepareRendererRequest(viewport, SlickSessionRequest.MenuBackground)
        startSlickGameIfNeeded()
    }

    override fun isMenuBackgroundActive(): Boolean = menuBackgroundActive

    override fun prepareReplayAsync(replayName: String, viewport: KoolCanvasViewport) {
        val requestedReplayName = replayName.takeIf { it.isNotBlank() } ?: return
        prepareRendererRequest(
            viewport,
            SlickSessionRequest.Replay(requestedReplayName),
        )
        beginRendererStandalonePreparation(requestedReplayName)
        startSlickGameIfNeeded()
    }

    private fun prepareRendererRequest(
        viewport: KoolCanvasViewport,
        request: SlickSessionRequest,
    ) {
        val requestedViewport = normalizedViewport(viewport)
        synchronized(lock) {
            lastSlickViewport = requestedViewport
            lastViewport = requestedViewport
            requestState.replace(request)
            rendererStartupError = null
        }
        loadingStatusTracker.reset()
        clearFrameSnapshot(unregisterTexture = true)
    }

    private fun signalEngineStartup() {
        engineReadyLock.withLock {
            engineReadyCondition.signalAll()
        }
    }

    private fun resizeFromCanvasHost(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        lastSlickViewport = KoolCanvasViewport(width, height)
        lastViewport = lastSlickViewport
        val activeGame = activeGame()
        activeGame?.requestSize(width.coerceAtLeast(320), height.coerceAtLeast(240))
    }

    private fun syncRequestedMapFromSession() {
        val preparation = rendererPreparationSnapshot()
        val pendingMemorySnapshot = preparation.memorySnapshot
        val pendingBattleRoomConfig = preparation.battleRoomConfig
        val pendingMapPath = preparation.mapPath
        if (pendingMemorySnapshot == null && pendingMapPath == null) {
            val preservesStandaloneRequest = synchronized(lock) {
                requestState.desired is SlickSessionRequest.MenuBackground ||
                        requestState.desired is SlickSessionRequest.Replay ||
                        requestState.desired is SlickSessionRequest.StartedGame
            }
            if (preservesStandaloneRequest) {
                return
            }
            val activeMapPath = runningMapPath()
                ?.takeIf { it.isNotBlank() && isMapLoaded(it) }
                ?: return
            synchronized(lock) {
                requestState.synchronize(SlickSessionRequest.Map(activeMapPath))
            }
            return
        }
        val sessionMapPath = pendingMemorySnapshot?.mapPath
            ?: pendingMapPath
            ?: return
        val request = when {
            pendingMemorySnapshot != null -> SlickSessionRequest.MemorySnapshot(pendingMemorySnapshot)
            pendingBattleRoomConfig != null -> SlickSessionRequest.BattleRoom(pendingBattleRoomConfig)
            else -> SlickSessionRequest.Map(sessionMapPath)
        }
        val requestChanged = synchronized(lock) {
            if (requestState.desired == request) {
                false
            } else {
                requestState.synchronize(request)
                true
            }
        }
        if (requestChanged) {
            clearFrameSnapshot(unregisterTexture = true)
        }
    }

    private fun pollSlickFrameSnapshot() {
        val snapshot = activeGame()?.currentFrameSnapshot() ?: return
        updateFrameSnapshot(snapshot)
    }

    private fun startSlickGameIfNeeded() {
        val request: SlickSessionRequest
        val viewport: KoolCanvasViewport
        val existingGame: SlickGame?
        val requestToDispatch: SlickSessionRequest?
        val shouldShowGame: Boolean
        val shouldPauseBackground: Boolean
        synchronized(lock) {
            request = requestState.desired ?: return
            shouldShowGame = gameVisible
            shouldPauseBackground = pausedBackground
            viewport = lastSlickViewport
            existingGame = activeGame()
            requestToDispatch = if (existingGame != null || !running.get()) {
                requestState.nextDispatch()
            } else {
                null
            }
        }
        if (existingGame != null) {
            existingGame.requestSize(viewport.width.coerceAtLeast(320), viewport.height.coerceAtLeast(240))
            requestToDispatch?.let(existingGame::submit)
            return
        }
        if (running.get()) {
            return
        }

        val canvas = SlickCanvasHost.gameCanvas()
        if (canvas == null) {
            synchronized(lock) {
                requestState.runtimeStopped()
            }
            logger.warn { "Slick game canvas is not installed" }
            return
        }
        runCatching {
            SlickNativeRuntimeEnvironment.nativesDir()
            start(
                canvas = canvas,
                request = request,
                viewport = viewport,
                visible = shouldShowGame,
                pausedBackground = shouldPauseBackground,
            )
        }.onFailure { error ->
            synchronized(lock) {
                requestState.runtimeStopped()
            }
            handleRendererStartupError(error)
            logger.error(error) { "Failed to launch embedded Slick backend" }
        }
    }

    private fun handleMapReady(mapPath: String) {
        var menuBackgroundReady = false
        val viewport: KoolCanvasViewport
        synchronized(lock) {
            val dispatchedRequest = requestState.dispatched
            viewport = lastSlickViewport
            if (dispatchedRequest is SlickSessionRequest.MenuBackground) {
                menuBackgroundReady = true
            }
        }
        if (menuBackgroundReady) {
            markRendererMenuBackgroundReady()
            CoreUiEventQueue.requestMenuBackgroundReady()
            logger.info { "Embedded Slick menu background ready: $mapPath" }
            return
        }
        markRendererMapReady(mapPath, viewport)
        println("RWX_SLICK_MAP_READY:$mapPath")
        System.out.flush()
    }

    private fun handleLoadingStatus(text: String) {
        loadingStatusTracker.update(text)
    }

    private fun handleMapError(mapPath: String, error: Throwable) {
        val failedMapPath: String?
        val failedMenuBackground: Boolean
        synchronized(lock) {
            val dispatchedRequest = requestState.dispatched
            failedMapPath = mapPath.takeIf { it.isNotBlank() } ?: dispatchedRequest?.loadingPath
            failedMenuBackground = dispatchedRequest is SlickSessionRequest.MenuBackground
            if (failedMenuBackground || failedMapPath == null || dispatchedRequest?.loadingPath == failedMapPath) {
                requestState.clearRequest()
            }
        }
        if (failedMenuBackground) {
            markRendererMenuBackgroundError(MENU_BACKGROUND_REQUEST, error)
        } else if (failedMapPath != null) {
            markRendererMapError(failedMapPath, error)
        }
        logger.error(error) { "Embedded Slick map load failed: ${mapPath.ifBlank { "<unknown>" }}" }
    }

    private fun handleRendererStartupError(error: Throwable) {
        rendererStartupError = error
        signalEngineStartup()
    }

    private fun handleRendererStopped() {
        if (activeGame() == null) {
            synchronized(lock) {
                requestState.runtimeStopped()
            }
        }
    }

    private fun normalizedViewport(viewport: KoolCanvasViewport): KoolCanvasViewport =
        SlickCanvasHost.gameCanvasSize()
            ?.takeIf { it.width > 0 && it.height > 0 }
            ?.let { size ->
                KoolCanvasViewport(
                    width = size.width,
                    height = size.height,
                )
            }
            ?: KoolCanvasViewport(
                width = viewport.width.takeIf { it > 0 } ?: 1280,
                height = viewport.height.takeIf { it > 0 } ?: 720,
            )
    private fun updateFrameSnapshot(snapshot: SlickFrameSnapshot) {
        synchronized(frameLock) {
            if (snapshot.width <= 0 || snapshot.height <= 0 || snapshot.pixels.size < snapshot.width * snapshot.height) {
                return
            }
            val previousState = frameSnapshotState
            val snapshotChanged = snapshot.sequence != previousState.sequence ||
                    snapshot.width != previousState.width ||
                    snapshot.height != previousState.height
            if (snapshotChanged) {
                KoolCanvasTextureRegistry.registerArgb(
                    SLICK_CURRENT_FRAME_TEXTURE_ID,
                    snapshot.width,
                    snapshot.height,
                    snapshot.pixels,
                )
            }
            val viewport = lastSlickViewport
            if (!snapshotChanged && previousState.frame.viewport == viewport) {
                return
            }
            val width = if (snapshotChanged) snapshot.width else previousState.width
            val height = if (snapshotChanged) snapshot.height else previousState.height
            frameSnapshotState = SlickFrameSnapshotState(
                sequence = if (snapshotChanged) snapshot.sequence else previousState.sequence,
                width = width,
                height = height,
                frame = frameForSnapshotTexture(viewport, width, height),
            )
        }
    }

    private fun clearFrameSnapshot(unregisterTexture: Boolean) {
        synchronized(frameLock) {
            frameSnapshotState = SlickFrameSnapshotState.empty(lastSlickViewport)
            if (unregisterTexture) {
                KoolCanvasTextureRegistry.unregister(SLICK_CURRENT_FRAME_TEXTURE_ID)
            }
        }
    }
    companion object{
        const val RENDER_THREAD_NAME = "RWX-slick-embedded"
        const val CANVAS_DISPLAY_TIMEOUT_NANOS = 5_000_000_000L
        const val CANVAS_RETRY_MILLIS = 16L
    }

}

private const val MENU_BACKGROUND_REQUEST = "<menu-background>"
private const val EXIT_FRAME_SNAPSHOT_TIMEOUT_MILLIS = 150L
private const val RENDER_THREAD_JOIN_TIMEOUT_MILLIS = 2_000L
private const val SLICK_CANVAS_INSTALL_TIMEOUT_MILLIS = 15_000L
private const val SLICK_ENGINE_INITIALIZATION_TIMEOUT_MILLIS = 60_000L
private const val SLICK_ENGINE_START_POLL_MILLIS = 50L
private val SLICK_CURRENT_FRAME_TEXTURE_ID = KoolCanvasTextureId("slick-current-frame")

private data class SlickFrameSnapshotState(
    val sequence: Long,
    val width: Int,
    val height: Int,
    val frame: KoolCanvasFrame,
) {
    val isAvailable: Boolean
        get() = sequence != Long.MIN_VALUE

    companion object {
        fun empty(viewport: KoolCanvasViewport): SlickFrameSnapshotState = SlickFrameSnapshotState(
            sequence = Long.MIN_VALUE,
            width = 0,
            height = 0,
            frame = emptyFrameFor(viewport),
        )
    }
}

private fun deadlineAfterMillis(timeoutMillis: Long): Long =
    System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis)


private fun emptyFrameFor(viewport: KoolCanvasViewport): KoolCanvasFrame =
    KoolCanvasFrame(viewport, emptyList())


private fun frameForSnapshotTexture(
    viewport: KoolCanvasViewport,
    width: Int,
    height: Int,
): KoolCanvasFrame {
    if (viewport.width <= 0 || viewport.height <= 0 || width <= 0 || height <= 0) {
        return emptyFrameFor(viewport)
    }
    val texture = KoolCanvasTextureRef(
        id = SLICK_CURRENT_FRAME_TEXTURE_ID,
        width = width,
        height = height,
        hasAlpha = false,
    )
    return KoolCanvasFrame(
        viewport = viewport,
        commands = listOf(
            KoolCanvasCommand.Clear(
                color = KoolCanvasColor(0xff000000.toInt()),
                blendMode = KoolCanvasBlendMode.Source,
            ),
            KoolCanvasCommand.DrawTexture(
                texture = texture,
                source = texture.fullRect,
                destination = KoolCanvasRect.fromSize(viewport.width.toFloat(), viewport.height.toFloat()),
                paint = KoolCanvasPaint(blendMode = KoolCanvasBlendMode.SourceOver),
                state = KoolCanvasState(),
            ),
        ),
    )
}
