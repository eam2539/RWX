package io.github.rwx.slick

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.PlatformCallbacks
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
import com.corrodinggames.rts.gameFramework.network.PasswordHandler
import io.github.rwx.PlatformStorage
import io.github.rwx.logger
import io.github.rwx.render.canvas.*
import io.github.rwx.session.BattleRoomLaunchConfig
import io.github.rwx.session.GameMemorySnapshot
import io.github.rwx.session.GameLoadingStatus
import io.github.rwx.session.GameSession
import io.github.rwx.session.GameSessionRendererProfile
import io.github.rwx.session.hasActiveStartedGameConnection
import io.github.rwx.platform.CoreGameView
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
    override val sessionLogName: String = "Slick"
    override val backgroundThreadPrefix: String = "RWX-slick"
    override val supportsMenuBackground: Boolean = true

    private val lock = Any()

    @Volatile
    private var slickGame: SlickGame? = null

    @Volatile
    private var slickContainer: EmbeddedSlickGameContainer? = null
    private val slickRunning = AtomicBoolean(false)
    private val slickStopRequested = AtomicBoolean(false)
    private val engineReadyLock = ReentrantLock()
    private val engineReadyCondition = engineReadyLock.newCondition()

    @Volatile
    private var rendererStartupError: Throwable? = null

    @Volatile
    private var renderThread: Thread? = null
    private var requestedMapPath: String? = null
    private var requestedReplayName: String? = null
    private var requestedBattleRoomConfig: BattleRoomLaunchConfig? = null
    private var requestedMemorySnapshot: GameMemorySnapshot? = null
    private var requestedStartedGame: Boolean = false
    private var requestedMenuBackground: Boolean = false
    private var requestedEnginePreparation: Boolean = false
    private var gameVisible: Boolean = false
    private var pausedBackground: Boolean = false
    private var launchedMapPath: String? = null
    private var launchedReplayName: String? = null
    private var launchedBattleRoomConfig: BattleRoomLaunchConfig? = null
    private var launchedMemorySnapshot: GameMemorySnapshot? = null
    private var launchedStartedGame: Boolean = false
    private var launchedMenuBackground: Boolean = false
    private var readyMapPath: String? = null
    private var lastSlickViewport: KoolCanvasViewport = KoolCanvasViewport(1280, 720)
    private var lastFrameSnapshotSequence: Long = Long.MIN_VALUE
    private var lastFrameSnapshotWidth: Int = 0
    private var lastFrameSnapshotHeight: Int = 0
    private var lastFrameSnapshotViewport: KoolCanvasViewport = lastSlickViewport
    private var lastFrameSnapshotFrame: KoolCanvasFrame = emptyFrameFor(lastSlickViewport)
    private val loadingStatusLock = Any()
    private val recentEngineLoadingSteps = ArrayDeque<String>()
    @Volatile
    private var latestEngineLoadingStatus: GameLoadingStatus? = null

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
                    stopRenderer()
                }, "RWX-slick-embedded-shutdown")
            )
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
                requestedEnginePreparation = false
            }
            signalEngineStartup()
        }.onFailure { error ->
            rendererStartupError = error
            signalEngineStartup()
        }.getOrThrow()

    override fun prepareMapAsync(mapPath: String?, viewport: KoolCanvasViewport) {
        val requestedMapPath = mapPath?.takeIf { it.isNotBlank() } ?: return
        if (isMapLoaded(requestedMapPath)) return
        synchronized(gameLock) {
            mapLoadGeneration += 1
            pendingMapPath = requestedMapPath
            pendingMemorySnapshot = null
            pendingRendererBattleRoomConfig = null
            activeRendererBattleRoomConfig = null
            asyncMapLoadPath = requestedMapPath
            asyncMapLoadInProgress = true
            asyncMapLoadError = null
            runningMapPath = null
            menuBackgroundActive = false
        }
        synchronized(lock) {
            prepareRendererRequestLocked(viewport)
            this.requestedMapPath = requestedMapPath
            requestedReplayName = null
            requestedBattleRoomConfig = null
            requestedMemorySnapshot = null
            requestedStartedGame = false
            requestedMenuBackground = false
        }
        startSlickGameIfNeeded()
    }

    override fun prepareMemorySnapshotAsync(snapshot: GameMemorySnapshot, viewport: KoolCanvasViewport) {
        if (isMapLoaded(snapshot.mapPath)) return
        synchronized(gameLock) {
            mapLoadGeneration += 1
            pendingMapPath = snapshot.mapPath
            pendingMemorySnapshot = snapshot
            pendingRendererBattleRoomConfig = snapshot.battleRoomConfig
            activeRendererBattleRoomConfig = null
            asyncMapLoadPath = snapshot.mapPath
            asyncMapLoadInProgress = true
            asyncMapLoadError = null
            runningMapPath = null
            menuBackgroundActive = false
        }
        synchronized(lock) {
            prepareRendererRequestLocked(viewport)
            requestedMapPath = snapshot.mapPath
            requestedReplayName = null
            requestedBattleRoomConfig = snapshot.battleRoomConfig
            requestedMemorySnapshot = snapshot
            requestedStartedGame = false
            requestedMenuBackground = false
        }
        startSlickGameIfNeeded()
    }

    override fun prepareBattleRoomAsync(config: BattleRoomLaunchConfig, viewport: KoolCanvasViewport) {
        val requestedMapPath = config.mapPath.takeIf { it.isNotBlank() } ?: return
        if (isMapLoaded(requestedMapPath) && activeRendererBattleRoomConfig() == config) return
        synchronized(gameLock) {
            mapLoadGeneration += 1
            pendingMapPath = requestedMapPath
            pendingMemorySnapshot = null
            pendingRendererBattleRoomConfig = config
            activeRendererBattleRoomConfig = null
            asyncMapLoadPath = requestedMapPath
            asyncMapLoadInProgress = true
            asyncMapLoadError = null
            runningMapPath = null
            menuBackgroundActive = false
        }
        synchronized(lock) {
            prepareRendererRequestLocked(viewport)
            this.requestedMapPath = requestedMapPath
            requestedReplayName = null
            requestedBattleRoomConfig = config
            requestedMemorySnapshot = null
            requestedStartedGame = false
            requestedMenuBackground = false
        }
        startSlickGameIfNeeded()
    }

    fun updateFrame(
        viewport: KoolCanvasViewport,
        deltaSeconds: Float,
        drainVisibleLayerBuffers: Boolean,
    ): KoolCanvasFrame {
        synchronized(lock) {
            lastSlickViewport = normalizedViewport(viewport)
            lastViewport = lastSlickViewport
            syncRequestedMapFromSessionLocked()
        }
        startSlickGameIfNeeded()
        pollSlickFrameSnapshot()
        pollSlickTextInputRequests()
        return currentFrame()
    }

    fun currentFrame(): KoolCanvasFrame = synchronized(lock) {
        if (lastFrameSnapshotSequence == Long.MIN_VALUE) {
            return@synchronized emptyFrameFor(lastSlickViewport)
        }
        if (lastFrameSnapshotViewport != lastSlickViewport) {
            lastFrameSnapshotViewport = lastSlickViewport
            lastFrameSnapshotFrame = frameForSnapshotTexture(
                viewport = lastSlickViewport,
                width = lastFrameSnapshotWidth,
                height = lastFrameSnapshotHeight,
            )
        }
        lastFrameSnapshotFrame
    }

    override fun loadPendingMapNow(): KoolCanvasFrame {
        synchronized(lock) {
            syncRequestedMapFromSessionLocked()
        }
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
            requestedEnginePreparation = true
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
                    engineReadyCondition.await(
                        remainingNanos.coerceAtMost(
                            TimeUnit.MILLISECONDS.toNanos(SLICK_ENGINE_START_POLL_MILLIS),
                        ),
                        TimeUnit.NANOSECONDS,
                    )
                }

            }

        } finally {
            if (exposedCanvasForStartup && synchronized(lock) { !gameVisible }) {
                SlickCanvasHost.setGameVisible(visible = false)
            }
        }
        return gameEngine!!
    }

    protected override fun applyViewport(engine: GameEngine, viewport: KoolCanvasViewport) {
        val requestedViewport = normalizedViewport(viewport)
        val activeGame = synchronized(lock) {
            lastSlickViewport = requestedViewport
            lastViewport = requestedViewport
            activeSlickGameLocked()
        }
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
        val currentStatus = super.loadingStatus()
        val recentSteps = latestEngineLoadingStatus?.recentSteps.orEmpty()
        return if (recentSteps.isEmpty()) currentStatus else currentStatus.copy(recentSteps = recentSteps)
    }

    override fun requestReloadMods(): Boolean {
        val request = synchronized(lock) {
            activeSlickGameLocked()?.requestReloadMods()
        } ?: return false
        runCatching { request.join() }.getOrElse { error ->
            throw (error.cause ?: error)
        }
        return true
    }


    private fun stopRenderer() {
        val containerToStop = synchronized(lock) {
            requestedMapPath = null
            requestedBattleRoomConfig = null
            requestedMemorySnapshot = null
            requestedStartedGame = false
            requestedMenuBackground = false
            requestedEnginePreparation = false
            rendererStartupError = IllegalStateException("Slick renderer stopped before startup completed")
            gameVisible = false
            pausedBackground = false
            launchedMapPath = null
            launchedBattleRoomConfig = null
            launchedMemorySnapshot = null
            launchedStartedGame = false
            launchedMenuBackground = false
            readyMapPath = null
            menuBackgroundActive = false
            clearFrameSnapshotLocked(unregisterTexture = true)
            slickGame = null
            requestedReplayName = null
            launchedReplayName = null
            slickStopRequested.set(true)
            slickContainer.also { slickContainer = null }
        }
        containerToStop?.requestExitFromAnyThread()
        signalEngineStartup()
    }

    fun stopRendererAndWait(timeoutMillis: Long = RENDER_THREAD_JOIN_TIMEOUT_MILLIS) {
        val thread = renderThread
        stopRenderer()
        if (thread == null || thread === Thread.currentThread() || !thread.isAlive) return
        runCatching { thread.join(timeoutMillis) }
        if (thread.isAlive) {
            logger.warn { "Slick render thread did not exit within ${timeoutMillis}ms of shutdown" }
        }
    }

    override fun setGameVisible(
        visible: Boolean,
        viewport: KoolCanvasViewport,
        koolOverlay: Boolean,
        pausedBackground: Boolean,
    ) {
        var shouldCaptureExitFrame = false
        val activeGame = synchronized(lock) {
            lastSlickViewport = normalizedViewport(viewport)
            lastViewport = lastSlickViewport
            shouldCaptureExitFrame = gameVisible && !visible
            gameVisible = visible
            this.pausedBackground = pausedBackground
            syncRequestedMapFromSessionLocked()
            activeSlickGameLocked()
        }

        if (shouldCaptureExitFrame && pausedBackground) {
            // Prime the pending snapshot with a world-only render. The regular game loop must
            // remain stopped while a paused/resume background is captured.
            activeGame?.setGameVisible(true, pausedBackground = true)
        }
        if (shouldCaptureExitFrame) {
            activeGame?.requestFrameSnapshot(EXIT_FRAME_SNAPSHOT_TIMEOUT_MILLIS)?.let { snapshot ->
                synchronized(lock) {
                    updateFrameSnapshotLocked(snapshot)
                }
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
        slickGame?.submitOverlayPointer(screenX, screenY, isDown, pointerId)
    }

    override fun movePointer(screenX: Float, screenY: Float) {
        slickGame?.moveOverlayPointer(screenX, screenY)
    }

    override fun submitMouseWheel(amount: Int) {
        if (amount != 0) slickGame?.submitOverlayMouseWheel(amount)
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
        val canLaunch = synchronized(lock) {
            activeSlickGameLocked() != null || SlickCanvasHost.gameCanvas() != null
        }
        if (!canLaunch) {
            logger.warn { "Unable to adopt started Slick game without an installed canvas: $mapPath" }
            return false
        }

        synchronized(lock) {
            lastSlickViewport = normalizedViewport
            lastViewport = normalizedViewport
            requestedMapPath = mapPath
            requestedReplayName = null
            requestedBattleRoomConfig = null
            requestedMemorySnapshot = null
            requestedStartedGame = true
            requestedMenuBackground = false
            requestedEnginePreparation = false
            launchedMenuBackground = false
            readyMapPath = null
            clearFrameSnapshotLocked(unregisterTexture = true)
        }
        synchronized(gameLock) {
            pendingMapPath = null
            pendingMemorySnapshot = null
            pendingRendererBattleRoomConfig = null
            asyncMapLoadPath = mapPath
            asyncMapLoadInProgress = true
            asyncMapLoadError = null
            runningMapPath = null
            menuBackgroundActive = false
        }
        logger.info { "Adopting started battle room in embedded Slick renderer: $mapPath" }
        startSlickGameIfNeeded()
        return true
    }

    override fun prepareMenuBackgroundAsync(viewport: KoolCanvasViewport) {
        synchronized(lock) {
            if (menuBackgroundActive || requestedMenuBackground) {
                return
            }
        }
        synchronized(gameLock) {
            mapLoadGeneration += 1
            pendingMapPath = null
            pendingMemorySnapshot = null
            pendingRendererBattleRoomConfig = null
            activeRendererBattleRoomConfig = null
            asyncMapLoadPath = MENU_BACKGROUND_REQUEST
            asyncMapLoadInProgress = true
            asyncMapLoadError = null
            runningMapPath = null
            menuBackgroundActive = false
        }
        synchronized(lock) {
            prepareRendererRequestLocked(viewport)
            requestedMapPath = null
            requestedReplayName = null
            requestedBattleRoomConfig = null
            requestedMemorySnapshot = null
            requestedStartedGame = false
            requestedMenuBackground = true
        }
        startSlickGameIfNeeded()
    }

    override fun isMenuBackgroundActive(): Boolean = synchronized(lock) {
        menuBackgroundActive
    }

    override fun prepareReplayAsync(replayName: String, viewport: KoolCanvasViewport) {
        val requestedReplayName = replayName.takeIf { it.isNotBlank() } ?: return
        synchronized(lock) {
            prepareRendererRequestLocked(viewport)
            requestedMapPath = null
            requestedBattleRoomConfig = null
            requestedMemorySnapshot = null
            requestedStartedGame = false
            requestedMenuBackground = false
            this.requestedReplayName = requestedReplayName
        }
        synchronized(gameLock) {
            mapLoadGeneration += 1
            pendingMapPath = null
            pendingMemorySnapshot = null
            pendingRendererBattleRoomConfig = null
            activeRendererBattleRoomConfig = null
            asyncMapLoadPath = requestedReplayName
            asyncMapLoadInProgress = true
            asyncMapLoadError = null
            runningMapPath = null
            menuBackgroundActive = false
        }
        startSlickGameIfNeeded()
    }

    private fun prepareRendererRequestLocked(viewport: KoolCanvasViewport) {
        lastSlickViewport = normalizedViewport(viewport)
        lastViewport = lastSlickViewport
        requestedEnginePreparation = false
        rendererStartupError = null
        launchedMapPath = null
        launchedReplayName = null
        launchedBattleRoomConfig = null
        launchedMemorySnapshot = null
        launchedStartedGame = false
        launchedMenuBackground = false
        readyMapPath = null
        menuBackgroundActive = false
        resetEngineLoadingStatus()
        clearFrameSnapshotLocked(unregisterTexture = true)
    }

    private fun signalEngineStartup() {
        engineReadyLock.withLock {
            engineReadyCondition.signalAll()
        }
    }

    private fun resizeFromCanvasHost(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        val activeGame: SlickGame?
        synchronized(lock) {
            lastSlickViewport = KoolCanvasViewport(width, height)
            lastViewport = lastSlickViewport
            activeGame = activeSlickGameLocked()
        }
        activeGame?.requestSize(width.coerceAtLeast(320), height.coerceAtLeast(240))
    }

    private fun syncRequestedMapFromSessionLocked() {
        val pendingMemorySnapshot = pendingMemorySnapshot()
        val pendingBattleRoomConfig = pendingRendererBattleRoomConfig()
        val pendingMapPath = pendingRendererMapPath()
        if (pendingMemorySnapshot == null && pendingMapPath == null) {
            if (requestedMenuBackground) {
                return
            }
            if (requestedReplayName != null) {
                return
            }
            if (requestedMapPath == null) {
                requestedMapPath = runningMapPath()
                    ?.takeIf { it.isNotBlank() && isMapLoaded(it) }
            }
            return
        }
        val sessionMapPath = pendingMemorySnapshot?.mapPath
            ?: pendingMapPath
            ?: return
        val battleRoomConfig = when {
            pendingMemorySnapshot != null -> pendingMemorySnapshot.battleRoomConfig
            pendingBattleRoomConfig != null -> pendingBattleRoomConfig
            pendingMapPath != null -> null
            else -> null
        }
        requestedReplayName = null
        requestedMenuBackground = false
        requestedStartedGame = false
        launchedMenuBackground = false
        menuBackgroundActive = false
        if (requestedMapPath == sessionMapPath &&
            requestedBattleRoomConfig == battleRoomConfig &&
            requestedMemorySnapshot == pendingMemorySnapshot
        ) return
        requestedMapPath = sessionMapPath
        requestedBattleRoomConfig = battleRoomConfig
        requestedMemorySnapshot = pendingMemorySnapshot
        clearFrameSnapshotLocked(unregisterTexture = true)
    }

    private fun pollSlickTextInputRequests() {
        val activeGame = synchronized(lock) {
            activeSlickGameLocked()
        } ?: return
        val requests = runCatching { activeGame.pollTextInputRequests() }
            .onFailure { error -> logger.warn(error) { "Unable to read Slick UI requests" } }
            .getOrDefault(emptyList())
        requests.forEach { request ->
            CoreUiEventQueue.requestPasswordDialog(
                SlickTextInputPasswordHandler(activeGame, request.id),
                request.title,
                request.prompt,
                request.confirmButtonLabel,
                request.cancelButtonLabel,
            )
        }
    }

    private fun pollSlickFrameSnapshot() {
        val snapshot = synchronized(lock) {
            activeSlickGameLocked()?.currentFrameSnapshot()
        } ?: return
        synchronized(lock) {
            updateFrameSnapshotLocked(snapshot)
        }
    }

    private fun updateFrameSnapshotLocked(snapshot: SlickFrameSnapshot) {
        if (snapshot.width <= 0 || snapshot.height <= 0 || snapshot.pixels.size < snapshot.width * snapshot.height) {
            return
        }
        val snapshotChanged = snapshot.sequence != lastFrameSnapshotSequence ||
                snapshot.width != lastFrameSnapshotWidth ||
                snapshot.height != lastFrameSnapshotHeight
        if (snapshotChanged) {
            KoolCanvasTextureRegistry.registerArgb(
                SLICK_CURRENT_FRAME_TEXTURE_ID,
                snapshot.width,
                snapshot.height,
                snapshot.pixels,
            )
            lastFrameSnapshotSequence = snapshot.sequence
            lastFrameSnapshotWidth = snapshot.width
            lastFrameSnapshotHeight = snapshot.height
        }
        if (snapshotChanged || lastFrameSnapshotViewport != lastSlickViewport) {
            lastFrameSnapshotViewport = lastSlickViewport
            lastFrameSnapshotFrame = frameForSnapshotTexture(
                viewport = lastSlickViewport,
                width = lastFrameSnapshotWidth,
                height = lastFrameSnapshotHeight,
            )
        }
    }

    private fun clearFrameSnapshotLocked(unregisterTexture: Boolean) {
        lastFrameSnapshotSequence = Long.MIN_VALUE
        lastFrameSnapshotWidth = 0
        lastFrameSnapshotHeight = 0
        lastFrameSnapshotViewport = lastSlickViewport
        lastFrameSnapshotFrame = emptyFrameFor(lastSlickViewport)
        if (unregisterTexture) {
            KoolCanvasTextureRegistry.unregister(SLICK_CURRENT_FRAME_TEXTURE_ID)
        }
    }

    private fun startSlickGameIfNeeded() {
        val mapPath: String?
        val replayName: String?
        val battleRoomConfig: BattleRoomLaunchConfig?
        val memorySnapshot: GameMemorySnapshot?
        val startedGame: Boolean
        val menuBackground: Boolean
        val enginePreparation: Boolean
        val shouldShowGame: Boolean
        val shouldPauseBackground: Boolean
        val viewport: KoolCanvasViewport
        val existingGame: SlickGame?
        var shouldStart = false
        var shouldRequestMap = false
        var shouldRequestReplay = false
        var shouldRequestBattleRoom = false
        var shouldRequestMemorySnapshot = false
        var shouldRequestStartedGame = false
        var shouldRequestMenuBackground = false
        synchronized(lock) {
            mapPath = requestedMapPath
            replayName = requestedReplayName
            battleRoomConfig = requestedBattleRoomConfig
            memorySnapshot = requestedMemorySnapshot
            startedGame = requestedStartedGame
            menuBackground = requestedMenuBackground
            enginePreparation = requestedEnginePreparation
            shouldShowGame = gameVisible
            shouldPauseBackground = pausedBackground
            viewport = lastSlickViewport
            if (mapPath == null && replayName == null && !menuBackground && !enginePreparation) return
            existingGame = activeSlickGameLocked()
            if (existingGame != null) {
                existingGame.requestSize(viewport.width.coerceAtLeast(320), viewport.height.coerceAtLeast(240))
                if (menuBackground) {
                    if (!launchedMenuBackground || launchedMapPath != null) {
                        launchedMapPath = null
                        launchedReplayName = null
                        launchedBattleRoomConfig = null
                        launchedMemorySnapshot = null
                        launchedStartedGame = false
                        launchedMenuBackground = true
                        readyMapPath = null
                        shouldRequestMenuBackground = true
                    }
                } else if (replayName != null) {
                    if (launchedReplayName != replayName ||
                        launchedMapPath != null ||
                        launchedBattleRoomConfig != null ||
                        launchedMemorySnapshot != null ||
                        launchedMenuBackground
                    ) {
                        launchedMapPath = null
                        launchedReplayName = replayName
                        launchedBattleRoomConfig = null
                        launchedMemorySnapshot = null
                        launchedStartedGame = false
                        launchedMenuBackground = false
                        readyMapPath = null
                        shouldRequestReplay = true
                    }
                } else if (launchedMapPath != mapPath ||
                    launchedReplayName != null ||
                    launchedBattleRoomConfig != battleRoomConfig ||
                    launchedMemorySnapshot != memorySnapshot ||
                    launchedStartedGame != startedGame ||
                    launchedMenuBackground
                ) {
                    launchedMapPath = mapPath
                    launchedReplayName = null
                    launchedBattleRoomConfig = battleRoomConfig
                    launchedMemorySnapshot = memorySnapshot
                    launchedStartedGame = startedGame
                    launchedMenuBackground = false
                    readyMapPath = null
                    if (startedGame) {
                        shouldRequestStartedGame = true
                    } else if (memorySnapshot != null) {
                        shouldRequestMemorySnapshot = true
                    } else if (battleRoomConfig != null) {
                        shouldRequestBattleRoom = true
                    } else {
                        shouldRequestMap = true
                    }
                }
            } else {
                shouldStart = true
            }
        }
        if (shouldRequestMenuBackground) {
            existingGame?.requestMenuBackground()
            return
        }
        if (shouldRequestReplay) {
            existingGame?.requestReplay(replayName!!)
            return
        }
        if (shouldRequestMemorySnapshot) {
            existingGame?.requestMemorySnapshot(memorySnapshot!!)
            return
        }
        if (shouldRequestStartedGame) {
            existingGame?.requestStartedGame(mapPath!!)
            return
        }
        if (shouldRequestBattleRoom) {
            existingGame?.requestBattleRoom(battleRoomConfig!!)
            return
        }
        if (shouldRequestMap) {
            existingGame?.requestMap(mapPath!!)
            return
        }
        if (!shouldStart) return

        val canvas = SlickCanvasHost.gameCanvas()
        if (canvas == null) {
            logger.warn { "Slick game canvas is not installed" }
            return
        }
        runCatching {
            SlickNativeRuntimeEnvironment.nativesDir()
            synchronized(lock) {
                launchedMapPath = mapPath
                launchedReplayName = replayName
                launchedBattleRoomConfig = battleRoomConfig
                launchedMemorySnapshot = memorySnapshot
                launchedStartedGame = startedGame
                launchedMenuBackground = menuBackground
                readyMapPath = null
            }
            val createdGame = startSlickGame(
                canvas = canvas,
                mapPath = mapPath,
                replayName = replayName,
                menuBackground = menuBackground,
                memorySnapshot = memorySnapshot,
                battleRoomConfig = battleRoomConfig,
                adoptStartedGame = startedGame,
                viewport = viewport,
                onReady = ::handleMapReady,
                onError = ::handleMapError,
                onLoadingStatus = ::handleLoadingStatus,
            )
            createdGame.setGameVisible(shouldShowGame, shouldPauseBackground)
        }.onFailure { error ->
            rendererStartupError = error
            signalEngineStartup()
            synchronized(lock) {
                slickGame = null
                launchedMapPath = null
                launchedReplayName = null
                launchedBattleRoomConfig = null
                launchedMemorySnapshot = null
                launchedStartedGame = false
                readyMapPath = null
            }
            logger.error(error) { "Failed to launch embedded Slick backend" }
        }
    }

    private fun startSlickGame(
        canvas: Canvas,
        mapPath: String?,
        replayName: String?,
        menuBackground: Boolean,
        memorySnapshot: GameMemorySnapshot?,
        battleRoomConfig: BattleRoomLaunchConfig?,
        adoptStartedGame: Boolean,
        viewport: KoolCanvasViewport,
        onReady: (String) -> Unit,
        onError: (String, Throwable) -> Unit,
        onLoadingStatus: (String) -> Unit,
    ): SlickGame {
        if (!slickRunning.compareAndSet(false, true)) {
            slickGame?.let { game ->
                if (menuBackground) {
                    game.requestMenuBackground()
                } else if (replayName != null) {
                    game.requestReplay(replayName)
                } else if (memorySnapshot != null) {
                    game.requestMemorySnapshot(memorySnapshot)
                } else {
                    mapPath?.let(game::requestMap)
                }
                return game
            }
            error("Slick game is marked running without an active game")
        }
        slickStopRequested.set(false)
        configureLegacyDesktopPlatform()
        storage.createDirectories()
        val width = viewport.width.coerceAtLeast(320)
        val height = viewport.height.coerceAtLeast(240)
        val graphicsEngine = SlickGraphicsEngine(storage)
        val createdGame = SlickGame(
            graphicsEngine = graphicsEngine,
            gameSession = this,
            initialWidth = width,
            initialHeight = height,
            initialMapPath = mapPath,
            initialReplayName = replayName,
            initialMenuBackground = menuBackground,
            initialBattleRoomConfig = battleRoomConfig,
            initialMemorySnapshot = memorySnapshot,
            initialAdoptStartedGame = adoptStartedGame,
            onTextInputRequest = ::requestKoolTextInput,
            onMapReady = onReady,
            onMapError = onError,
            onLoadingStatus = onLoadingStatus,
        )
        slickGame = createdGame

        Thread({
            var containerToDestroy: EmbeddedSlickGameContainer? = null
            try {
                configureLwjgl2Natives()
                waitForDisplayableCanvas(canvas)
                if (slickStopRequested.get()) return@Thread
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
                slickContainer = createdContainer
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
                if (slickStopRequested.get()) {
                    createdContainer.requestExitFromAnyThread()
                }
                createdContainer.start()
            } catch (error: Throwable) {
                rendererStartupError = error
                signalEngineStartup()
                onError(replayName ?: mapPath ?: slickGame?.runningMapPath().orEmpty(), error)
            } finally {
                // Linux JAWT surfaces retain their creator thread's JNIEnv, so release before this thread exits.
                runCatching { containerToDestroy?.destroy() }
                    .onFailure { error -> logger.warn(error) { "Failed to destroy the embedded Slick container" } }
                slickContainer = null
                slickGame = null
                slickStopRequested.set(false)
                slickRunning.set(false)
                renderThread = null
            }
        }, "RWX-slick-embedded").apply {
            isDaemon = true
            renderThread = this
            start()
        }

        return createdGame
    }

    private fun requestKoolTextInput(game: SlickGame, request: SlickTextInputRequest): Boolean {
        CoreUiEventQueue.requestPasswordDialog(
            SlickTextInputPasswordHandler(game, request.id),
            request.title,
            request.prompt,
            request.confirmButtonLabel,
            request.cancelButtonLabel,
        )
        return true
    }

    private fun configureLwjgl2Natives() {
        System.setProperty("org.lwjgl.opengl.contextAPI", "native")
        val nativesDir = System.getProperty("rwx.slick.nativesDir")
            ?: System.getenv("RWX_SLICK_NATIVES_DIR")
            ?: return
        System.setProperty("org.lwjgl.librarypath", nativesDir)
        System.setProperty("java.library.path", nativesDir)
    }

    private fun waitForDisplayableCanvas(canvas: Canvas) {
        val deadline = System.nanoTime() + 5_000_000_000L
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
            Thread.sleep(16L)
        }
        check(canvas.isDisplayable && canvas.width > 0 && canvas.height > 0) {
            "Slick parent canvas was not displayable"
        }
    }

    private fun handleMapReady(mapPath: String) {
        var menuBackgroundReady = false
        synchronized(lock) {
            if (launchedMenuBackground && launchedMapPath == null && launchedReplayName == null) {
                requestedMenuBackground = false
                readyMapPath = mapPath
                asyncMapLoadPath = null
                asyncMapLoadInProgress = false
                asyncMapLoadError = null
                menuBackgroundActive = true
                menuBackgroundReady = true
            } else if (launchedReplayName == mapPath) {
                readyMapPath = mapPath
            } else if (launchedMapPath == null) {
                launchedMapPath = mapPath
                readyMapPath = mapPath
            } else if (launchedMapPath == mapPath) {
                readyMapPath = mapPath
            }
        }
        if (menuBackgroundReady) {
            logger.info { "Embedded Slick menu background ready: $mapPath" }
            return
        }
        markRendererMapReady(mapPath, lastSlickViewport)
        println("RWX_SLICK_MAP_READY:$mapPath")
        System.out.flush()
    }

    private fun handleLoadingStatus(text: String) {
        val engine = com.corrodinggames.rts.gameFramework.GameEngine.getInstance()
        val loadingText = text.ifBlank { "Loading..." }
        synchronized(loadingStatusLock) {
            if (recentEngineLoadingSteps.lastOrNull() != loadingText) {
                recentEngineLoadingSteps.addLast(loadingText)
                while (recentEngineLoadingSteps.size > ENGINE_LOADING_HISTORY_LIMIT) {
                    recentEngineLoadingSteps.removeFirst()
                }
            }
            latestEngineLoadingStatus = GameLoadingStatus(
                text = loadingText,
                progress = engine?.getLoadingProgress()?.coerceIn(0.0f, 1.0f),
                recentSteps = recentEngineLoadingSteps.toList(),
            )
        }
    }

    private fun resetEngineLoadingStatus() {
        synchronized(loadingStatusLock) {
            recentEngineLoadingSteps.clear()
            latestEngineLoadingStatus = null
        }
    }

    private fun handleMapError(mapPath: String, error: Throwable) {
        val failedMapPath: String?
        var failedMenuBackground = false
        synchronized(lock) {
            failedMapPath = mapPath.takeIf { it.isNotBlank() } ?: launchedMapPath
            failedMenuBackground = launchedMenuBackground && launchedMapPath == null && launchedReplayName == null
            if (failedMenuBackground || failedMapPath == null || launchedMapPath == failedMapPath || launchedReplayName == failedMapPath) {
                requestedMapPath = null
                requestedReplayName = null
                requestedBattleRoomConfig = null
                requestedMemorySnapshot = null
                requestedStartedGame = false
                requestedMenuBackground = false
                launchedMapPath = null
                launchedReplayName = null
                launchedBattleRoomConfig = null
                launchedMemorySnapshot = null
                launchedStartedGame = false
                launchedMenuBackground = false
                readyMapPath = null
                menuBackgroundActive = false
            }
        }
        if (failedMapPath != null) {
            markRendererMapError(failedMapPath, error)
        } else if (failedMenuBackground) {
            markRendererMapError(MENU_BACKGROUND_REQUEST, error)
        }
        logger.error(error) { "Embedded Slick map load failed: ${mapPath.ifBlank { "<unknown>" }}" }
    }

    private fun isSlickGameRunningLocked(): Boolean {
        if (slickRunning.get() && slickGame != null) return true
        slickGame = null
        launchedMapPath = null
        launchedReplayName = null
        launchedBattleRoomConfig = null
        launchedMemorySnapshot = null
        launchedStartedGame = false
        readyMapPath = null
        return false
    }

    private fun activeSlickGameLocked(requireReady: Boolean = false): SlickGame? {
        if (!isSlickGameRunningLocked()) return null
        if (requireReady && readyMapPath == null) return null
        return slickGame
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

}

private val SLICK_CURRENT_FRAME_TEXTURE_ID = KoolCanvasTextureId("slick-current-frame")
private const val MENU_BACKGROUND_REQUEST = "<menu-background>"
private const val EXIT_FRAME_SNAPSHOT_TIMEOUT_MILLIS = 150L
private const val RENDER_THREAD_JOIN_TIMEOUT_MILLIS = 2_000L
private const val ENGINE_LOADING_HISTORY_LIMIT = 6
private const val SLICK_CANVAS_INSTALL_TIMEOUT_MILLIS = 15_000L
private const val SLICK_ENGINE_INITIALIZATION_TIMEOUT_MILLIS = 60_000L
private const val SLICK_ENGINE_START_POLL_MILLIS = 50L

private fun deadlineAfterMillis(timeoutMillis: Long): Long =
    System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis)

private class SlickTextInputPasswordHandler(
    private val game: SlickGame,
    private val requestId: Int,
) : PasswordHandler() {
    override fun submitPassword(str: String?) {
        game.submitTextInput(requestId, str.orEmpty())
    }

    override fun cancelPasswordEntry() {
        game.cancelTextInput(requestId)
    }
}

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
