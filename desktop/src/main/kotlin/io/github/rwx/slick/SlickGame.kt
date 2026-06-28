package io.github.rwx.slick

import com.corrodinggames.rts.R
import com.corrodinggames.rts.game.GameLogic
import com.corrodinggames.rts.game.GameTeam
import com.corrodinggames.rts.game.PlayerTeam
import com.corrodinggames.rts.game.map.LayerBufferManager
import com.corrodinggames.rts.game.map.TileMap
import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.GameMode
import com.corrodinggames.rts.gameFramework.InputController
import com.corrodinggames.rts.gameFramework.PlatformCallbacks
import com.corrodinggames.rts.gameFramework.network.*
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes
import io.github.rwx.DesktopInputHandler
import io.github.rwx.ensureDesktopOpenAlMusicFactory
import io.github.rwx.geometry.Point
import io.github.rwx.input.MultiTouchPointerState
import io.github.rwx.net.CoreUiNetworkCallbacks
import io.github.rwx.platform.CoreGameView
import io.github.rwx.render.canvas.KoolCanvasViewport
import io.github.rwx.session.*
import io.github.rwx.session.BattleRoomTeamLayout.*
import io.github.rwx.ui.InGameMenuController
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.newdawn.slick.BasicGame
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

internal fun configureLegacyDesktopPlatform() {
    GameEngine.isMenuBackgroundDisabled = true
    GameEngine.isNonAndroidVersion = true
    GameEngine.isDesktopInitialized = true
    GameEngine.isJavaDesktopVersion = true
    GameEngine.isPCOrIOSVersion = true
    InputController.b = DesktopInputHandler()
    ensureDesktopOpenAlMusicFactory()
}

internal class SlickLoadingCallbacks(
    private val onLoadingStatus: (String) -> Unit,
) : PlatformCallbacks() {
    override fun a(str: String, i: Int) = Unit

    override fun a(str: String, str2: String) = Unit

    override fun a(str: String, z: Boolean) {
        onLoadingStatus(str)
    }

    override fun a(th: Throwable) = Unit
}

object SlickGraphicsBinding {
    @Volatile
    private var currentGraphicsEngine: SlickGraphicsEngine? = null

    @JvmStatic
    fun current(): SlickGraphicsEngine? = currentGraphicsEngine

    fun bind(graphicsEngine: SlickGraphicsEngine) {
        currentGraphicsEngine = graphicsEngine
    }
}

private const val SLICK_MAP_READY_MARKER = "RWX_SLICK_MAP_READY:"
private const val SLICK_MAP_ERROR_MARKER = "RWX_SLICK_MAP_ERROR:"
private const val SLICK_LAYER_REDRAW_BUDGET_MS = 2
private const val SLICK_MENU_BACKGROUND_TARGET_FPS = 30
private val SLICK_TARGET_FPS_OVERRIDE: Int? =
    System.getenv("RWX_SLICK_TARGET_FPS")?.toIntOrNull()?.takeIf { it > 0 }

internal fun shouldDriveSlickGameLoop(
    hasPendingLoad: Boolean,
    hasLoadedLevel: Boolean,
    hasRunningMap: Boolean,
    gameVisible: Boolean,
    runningMenuBackground: Boolean,
    networkGameStarted: Boolean,
): Boolean = hasPendingLoad ||
        (hasLoadedLevel &&
            (runningMenuBackground || networkGameStarted || (gameVisible && hasRunningMap)))

internal fun shouldRenderPausedSlickFrame(
    pausedBackground: Boolean,
    gameSpeed: Float,
    hasLoadedLevel: Boolean,
    gameVisible: Boolean,
    runningMenuBackground: Boolean,
    networkMultiplayerActive: Boolean,
    enginePaused: Boolean = false,
): Boolean = hasLoadedLevel &&
        gameVisible &&
        !runningMenuBackground &&
        !networkMultiplayerActive &&
        (pausedBackground || gameSpeed == 0f || enginePaused)

internal fun shouldCountSlickReadyFrame(
    hasLoadedLevel: Boolean,
    runningMapPath: String?,
    readyNotifiedMapPath: String?,
): Boolean = hasLoadedLevel &&
        runningMapPath != null &&
        readyNotifiedMapPath != runningMapPath

internal fun shouldAbortStartedGameAfterDisconnect(
    setupComplete: Boolean,
    hasLoadedLevel: Boolean,
    hasActiveConnection: Boolean,
    renderedFrames: Int,
): Boolean = setupComplete &&
        !hasLoadedLevel &&
        !hasActiveConnection &&
        renderedFrames == 0

internal fun shouldRenderSlickBackgroundLayerRedraw(gameLoopDriven: Boolean): Boolean =
    !gameLoopDriven

internal class SlickFrameDelta {
    private var pendingMillis: Int = 0

    fun update(deltaMillis: Int) {
        pendingMillis = deltaMillis.coerceAtLeast(0)
    }

    fun consumeMillis(): Int = pendingMillis.also { pendingMillis = 0 }
}

internal class SlickModReloadQueue {
    private val pendingRequest = AtomicReference<CompletableFuture<Unit>?>(null)

    fun request(): CompletableFuture<Unit> {
        while (true) {
            pendingRequest.get()?.let { return it }
            val request = CompletableFuture<Unit>()
            if (pendingRequest.compareAndSet(null, request)) {
                return request
            }
        }
    }

    fun executePending(reload: () -> Unit) {
        val request = pendingRequest.getAndSet(null) ?: return
        runCatching(reload)
            .onSuccess { request.complete(Unit) }
            .onFailure(request::completeExceptionally)
    }
}

internal fun legacySlickDeltaSpeed(deltaMillis: Int): Float = deltaMillis * 0.060000002f

data class SlickTextInputRequest(
    val id: Int,
    val title: String,
    val prompt: String,
    val confirmButtonLabel: String,
    val cancelButtonLabel: String,
)

internal class SlickGame(
    private val graphicsEngine: SlickGraphicsEngine,
    private val gameSession: GameSession,
    private val initialWidth: Int,
    private val initialHeight: Int,
    private val initialMapPath: String?,
    private val initialReplayName: String? = null,
    private val initialMenuBackground: Boolean = false,
    private val initialBattleRoomConfig: BattleRoomLaunchConfig? = null,
    private val initialMemorySnapshot: GameMemorySnapshot? = null,
    private val initialAdoptStartedGame: Boolean = false,
    private val noFog: Boolean = System.getenv("RWX_SLICK_NO_FOG") == "1",
    private val onTextInputRequest: (SlickGame, SlickTextInputRequest) -> Boolean = { _, _ -> false },
    private val onMapReady: (String) -> Unit = { mapPath ->
        println("$SLICK_MAP_READY_MARKER$mapPath")
        System.out.flush()
    },
    private val onMapError: (String, Throwable) -> Unit = { mapPath, error ->
        println("$SLICK_MAP_ERROR_MARKER$mapPath\t${error.javaClass.simpleName}: ${error.message.orEmpty()}")
        System.out.flush()
    },
    private val onLoadingStatus: (String) -> Unit = {},
) : BasicGame("RWX") {
    private val view = SlickCoreGameView(gameSession.inGameMenuController)
    private val debugSlickMenuInput = System.getenv("RWX_DEBUG_SLICK_MENU") == "1"
    private val keyStates = BooleanArray(SLICK_KEY_COUNT)
    private var engine: GameEngine? = null

    @Volatile
    private var pendingMapPath: String? = if (initialAdoptStartedGame) {
        null
    } else {
        initialMemorySnapshot?.mapPath ?: initialBattleRoomConfig?.mapPath ?: initialMapPath
    }

    @Volatile
    private var pendingReplayName: String? = initialReplayName

    @Volatile
    private var pendingBattleRoomConfig: BattleRoomLaunchConfig? = initialBattleRoomConfig

    @Volatile
    private var pendingMemorySnapshot: GameMemorySnapshot? = initialMemorySnapshot

    @Volatile
    private var pendingStartedGameMapPath: String? = initialMapPath.takeIf { initialAdoptStartedGame }

    @Volatile
    private var pendingMenuBackground: Boolean = initialMenuBackground

    @Volatile
    private var runningMapPath: String? = null

    @Volatile
    private var runningMenuBackground: Boolean = false

    @Volatile
    private var startedBattleRoomGameSetupComplete: Boolean = false

    @Volatile
    private var gameVisible: Boolean = true

    @Volatile
    private var pausedBackground: Boolean = false
    private var readyNotifiedMapPath: String? = null
    private var renderedFramesForRunningMap: Int = 0

    @Volatile
    private var pendingWidth: Int = initialWidth

    @Volatile
    private var pendingHeight: Int = initialHeight

    @Volatile
    private var pendingSaveName: String? = null

    @Volatile
    private var pendingExportMapName: String? = null

    @Volatile
    private var pendingSurrender: Boolean = false

    private val modReloadQueue = SlickModReloadQueue()
    private val pendingChatMessages = ConcurrentLinkedQueue<PendingChatMessage>()
    private val pendingTextInputRequests = ConcurrentLinkedQueue<SlickTextInputRequest>()
    private val pendingTextInputResponses = ConcurrentLinkedQueue<PendingTextInputResponse>()
    private val pendingTextInputHandlers = ConcurrentHashMap<Int, PasswordHandler>()
    private val pendingInputEvents = ConcurrentLinkedQueue<SlickInputEvent>()
    private val pendingInputEventCount = AtomicInteger(0)
    private val nextTextInputRequestId = AtomicInteger(1)
    private val frameDelta = SlickFrameDelta()
    private var lastAppliedWidth: Int = 0
    private var lastAppliedHeight: Int = 0
    private var lastAppliedTargetFrameRate: Int = -1
    private var lastAppliedVsync: Boolean? = null
    private var lastAppliedSmoothDeltas: Boolean? = null
    private var pointerCursorApplied: Boolean = false

    @Volatile
    private var latestFrameSnapshot: SlickFrameSnapshot? = null
    private var latestFrameSnapshotSequence: Long = 0L
    private val pendingFrameSnapshotRequest = AtomicReference<CompletableFuture<SlickFrameSnapshot?>?>(null)

    override fun init(container: GameContainer) {
        GameEngine.screenSize = Point(initialWidth, initialHeight)
        SlickGraphicsBinding.bind(graphicsEngine)
        GameEngine.graphicsEngine = graphicsEngine
        GameEngine.externalGameLoopDriver = true
        val activeEngine = gameSession.ensureRendererEngine(
            viewport = KoolCanvasViewport(container.width, container.height),
            graphicsEngine = graphicsEngine,
            view = view,
            platformCallbacks = SlickLoadingCallbacks(onLoadingStatus),
        )
        resetSlickRenderCaches(activeEngine)
        if (activeEngine is GameLogic) {
            activeEngine.worldFrameRenderedListener = Runnable {
                captureFrameSnapshotAfterWorldRender(container)
            }
        }
        installNetworkCallbacks(activeEngine)
        activeEngine.colorizeLogMessage(view, false)
        view.onResume()
        activeEngine.updateWindowResolution(container.width, container.height)
        lastAppliedWidth = container.width
        lastAppliedHeight = container.height
        activeEngine.settingsEngine.numIncompleteLoadAttempts = 0
        activeEngine.settingsEngine.numLoadsSinceRunningGameOrNormalExit = 0
        engine = activeEngine
    }

    private fun applyPointerCursor(container: GameContainer): Boolean =
        runCatching {
            val pointerImage = (graphicsEngine.a(R.drawable.pointer, true) as? SlickTexture)?.image()
            if (pointerImage != null) {
                container.setMouseCursor(pointerImage, 0, 0)
            }
            pointerImage != null
        }.onFailure { error ->
            GameEngine.log("Failed to apply Slick mouse cursor", error)
        }.getOrDefault(false)

    private fun resetSlickRenderCaches(activeEngine: GameEngine) {
        graphicsEngine.resetBackendState()
        activeEngine.minimap?.release()
        TileMap.layerBufferManager.releaseLayerBuffers()
        TileMap.layerBufferManager = LayerBufferManager()
        TileMap.layerBufferManager.bindGraphicsBackend(graphicsEngine)
        TileMap.buildFogSmoothAtlas(graphicsEngine)
    }

    private fun installNetworkCallbacks(activeEngine: GameEngine) {
        activeEngine.networkEngine?.callbacks = object : CoreUiNetworkCallbacks() {
            override fun onPasswordPrompt(passwordHandler: PasswordHandler): Boolean {
                queueTextInputRequest(passwordHandler)
                return true
            }
        }
    }

    private fun queueTextInputRequest(passwordHandler: PasswordHandler) {
        val requestId = nextTextInputRequestId.getAndIncrement()
        pendingTextInputHandlers[requestId] = passwordHandler
        val request = SlickTextInputRequest(
            id = requestId,
            title = passwordHandler.dialogTitle?.takeIf { it.isNotBlank() } ?: "Password Required",
            prompt = passwordHandler.promptMessage?.takeIf { it.isNotBlank() }
                ?: "This server requires a password to join",
            confirmButtonLabel = passwordHandler.confirmButtonLabel?.takeIf { it.isNotBlank() } ?: "OK",
            cancelButtonLabel = passwordHandler.cancelButtonLabel?.takeIf { it.isNotBlank() } ?: "Cancel",
        )
        val delivered = runCatching {
            onTextInputRequest(this, request)
        }.onFailure { error ->
            GameEngine.log("Failed to deliver Slick text input request", error)
        }.getOrDefault(false)
        logSlickMenuInput(
            "DesktopSlickGame.queueTextInputRequest" +
                    " id=${request.id}" +
                    " title=\"${request.title}\"" +
                    " delivered=$delivered" +
                    " thread=${Thread.currentThread().name}"
        )
        if (!delivered) {
            pendingTextInputRequests.add(request)
        }
    }

    override fun update(container: GameContainer, delta: Int) {
        frameDelta.update(delta)
    }

    override fun render(container: GameContainer, graphics: Graphics) {
        val activeEngine = engine ?: return
        graphicsEngine.setGraphics(graphics, container.width, container.height)
        activeEngine.renderGraphicsEngine = graphicsEngine
        applyContainerRuntimeSettings(activeEngine, container)
        applyPendingInputEvents(activeEngine)
        applyPendingSize(activeEngine, container)
        adoptPendingStartedGame(activeEngine)
        loadPendingReplay(activeEngine)
        loadPendingMap(activeEngine)
        loadPendingMenuBackground(activeEngine)
        savePendingGame(activeEngine)
        exportPendingMap(activeEngine)
        reloadPendingMods(activeEngine)
        applyPendingInGameCommands(activeEngine)
        applyPendingTextInputResponses()
        updatePointerCursor(container, activeEngine)
        abortStartedGameAfterDisconnect(activeEngine)
        val deltaMillis = frameDelta.consumeMillis()
        val pausedFrame = shouldRenderPausedFrame(activeEngine)
        val gameLoopDriven = !pausedFrame && shouldDriveGameLoop(activeEngine)
        if (pausedFrame) {
            (activeEngine as GameLogic).drawWorldOnlyThreadSafe(0f)
        } else if (gameLoopDriven) {
            activeEngine.gameLoop(legacySlickDeltaSpeed(deltaMillis), deltaMillis)
        }
        if (shouldRenderSlickBackgroundLayerRedraw(gameLoopDriven)) {
            renderPendingLayerRedraws(activeEngine)
        }
        notifyReadyAfterVisibleFrames(activeEngine)
    }

    private fun renderPendingLayerRedraws(activeEngine: GameEngine) {
        synchronized(activeEngine.gameStateLock) {
            if (!activeEngine.hasLoadedLevel || activeEngine.tileMap == null) return
            TileMap.layerBufferManager.renderPendingRedraws(SLICK_LAYER_REDRAW_BUDGET_MS)
        }
    }

    private fun shouldDriveGameLoop(activeEngine: GameEngine): Boolean {
        val hasPendingLoad = pendingStartedGameMapPath != null || pendingReplayName != null ||
                pendingMapPath != null || pendingMenuBackground
        val networkEngine = activeEngine.networkEngine
        return shouldDriveSlickGameLoop(
            hasPendingLoad = hasPendingLoad,
            hasLoadedLevel = activeEngine.hasLoadedLevel,
            hasRunningMap = runningMapPath != null,
            gameVisible = gameVisible,
            runningMenuBackground = runningMenuBackground,
            networkGameStarted = activeEngine.isNetworkGameActive() &&
                networkEngine?.gameHasBeenStarted == true,
        )
    }

    private fun shouldRenderPausedFrame(activeEngine: GameEngine): Boolean =
        shouldRenderPausedSlickFrame(
            pausedBackground = pausedBackground,
            gameSpeed = activeEngine.gameSpeed,
            hasLoadedLevel = activeEngine.hasLoadedLevel,
            gameVisible = gameVisible,
            runningMenuBackground = runningMenuBackground,
            networkMultiplayerActive = activeEngine.isNetworkGameActive(),
            enginePaused = activeEngine.isPaused || activeEngine.isStopped,
        )

    private fun applyContainerRuntimeSettings(activeEngine: GameEngine, container: GameContainer) {
        val vsync = activeEngine.settingsEngine.renderVsync
        val targetFrameRate = targetFrameRateFor(activeEngine, container)
        if (lastAppliedTargetFrameRate != targetFrameRate) {
            container.setTargetFrameRate(targetFrameRate)
            lastAppliedTargetFrameRate = targetFrameRate
        }
        if (lastAppliedVsync != vsync) {
            container.setVSync(vsync)
            lastAppliedVsync = vsync
        }
        val smoothDeltas = activeEngine.settingsEngine.renderSmoothDelta
        if (lastAppliedSmoothDeltas != smoothDeltas) {
            container.setSmoothDeltas(smoothDeltas)
            lastAppliedSmoothDeltas = smoothDeltas
        }
    }

    private fun targetFrameRateFor(activeEngine: GameEngine, container: GameContainer): Int {
        SLICK_TARGET_FPS_OVERRIDE?.let { return it }
        if (runningMenuBackground) {
            return SLICK_MENU_BACKGROUND_TARGET_FPS
        }
        val highRefreshRate = activeEngine.settingsEngine.highRefreshRate
        return (container as? EmbeddedSlickGameContainer)
            ?.recommendedTargetFrameRate(highRefreshRate)
            ?: if (highRefreshRate) 300 else 120
    }

    private fun applyPendingSize(activeEngine: GameEngine, container: GameContainer) {
        val width = pendingWidth.coerceAtLeast(320)
        val height = pendingHeight.coerceAtLeast(240)
        if (container.width != width || container.height != height) {
            runCatching {
                if (container is EmbeddedSlickGameContainer) {
                    container.setDisplayMode(width, height, false)
                }
            }
        }
        if (lastAppliedWidth != container.width || lastAppliedHeight != container.height) {
            lastAppliedWidth = container.width
            lastAppliedHeight = container.height
            GameEngine.screenSize = Point(container.width, container.height)
            activeEngine.updateWindowResolution(container.width, container.height)
        }
    }

    private fun loadPendingMap(activeEngine: GameEngine) {
        if (pendingReplayName != null) return
        val mapPath = pendingMapPath ?: return
        val memorySnapshot = pendingMemorySnapshot?.takeIf { it.mapPath == mapPath }
        val battleRoomConfig = pendingBattleRoomConfig?.takeIf { it.mapPath == mapPath }
        try {
            if (memorySnapshot != null) {
                loadMemorySnapshot(activeEngine, memorySnapshot)
            } else if (battleRoomConfig != null) {
                loadBattleRoomMap(activeEngine, battleRoomConfig)
            } else {
                loadDirectMap(activeEngine, mapPath)
            }
        } catch (error: Throwable) {
            onMapError(mapPath, error)
            throw error
        }
        applyNoFogOverride(activeEngine)
        runningMapPath = mapPath
        readyNotifiedMapPath = null
        renderedFramesForRunningMap = 0
        pendingMapPath = null
        pendingBattleRoomConfig = null
        pendingMemorySnapshot = null
    }

    private fun adoptPendingStartedGame(activeEngine: GameEngine) {
        val requestedMapPath = pendingStartedGameMapPath ?: return
        try {
            val networkEngine = activeEngine.networkEngine
                ?: error("Started battle room has no network engine")
            check(networkEngine.hasActiveStartedGameConnection()) {
                "Battle room network connection is no longer active"
            }
            if (activeEngine.hasLoadedLevel && !activeEngine.isMenuBackgroundMap) {
                startedBattleRoomGameSetupComplete = true
                runningMenuBackground = false
                runningMapPath = networkEngine.selectedMapPath
                    ?: activeEngine.currentMapPath
                            ?: requestedMapPath
                readyNotifiedMapPath = null
                renderedFramesForRunningMap = 0
            } else {
                completeStartedBattleRoomGame(activeEngine)
            }
            check(networkEngine.hasActiveStartedGameConnection()) {
                "Battle room network connection closed while loading the map"
            }
            check(activeEngine.hasLoadedLevel) { "Started battle room did not load a level" }
            pendingStartedGameMapPath = null
        } catch (error: Throwable) {
            failStartedBattleRoomGame(activeEngine, requestedMapPath, error)
        }
    }

    private fun abortStartedGameAfterDisconnect(activeEngine: GameEngine) {
        val networkEngine = activeEngine.networkEngine ?: return
        if (!shouldAbortStartedGameAfterDisconnect(
                setupComplete = startedBattleRoomGameSetupComplete,
                hasLoadedLevel = activeEngine.hasLoadedLevel,
                hasActiveConnection = networkEngine.hasActiveStartedGameConnection(),
                renderedFrames = renderedFramesForRunningMap,
            )
        ) {
            return
        }
        val mapPath = runningMapPath ?: pendingStartedGameMapPath ?: return
        failStartedBattleRoomGame(
            activeEngine,
            mapPath,
            IllegalStateException("Battle room network connection closed before the first frame"),
        )
    }

    private fun failStartedBattleRoomGame(
        activeEngine: GameEngine,
        mapPath: String,
        error: Throwable,
    ) {
        pendingStartedGameMapPath = null
        startedBattleRoomGameSetupComplete = false
        runningMapPath = null
        readyNotifiedMapPath = null
        renderedFramesForRunningMap = 0
        activeEngine.networkEngine
            ?.takeIf { it.gameHasBeenStarted }
            ?.onStartGameFailed()
        onMapError(mapPath, error)
    }

    private fun loadPendingReplay(activeEngine: GameEngine) {
        val replayName = pendingReplayName ?: return
        try {
            loadReplay(activeEngine, replayName)
        } catch (error: Throwable) {
            onMapError(replayName, error)
            throw error
        }
        runningMapPath = replayName
        readyNotifiedMapPath = null
        renderedFramesForRunningMap = 0
        pendingReplayName = null
    }

    private fun loadReplay(activeEngine: GameEngine, replayName: String) {
        runningMenuBackground = false
        activeEngine.isGameStarted = false
        activeEngine.isStopped = false
        activeEngine.isPaused = false
        check(activeEngine.replayEngine.loadReplay(replayName)) { "Unable to load replay: $replayName" }
    }

    private fun loadDirectMap(activeEngine: GameEngine, mapPath: String) {
        prepareActiveGameLoad(activeEngine)
        val saveName = savedGameNameFromRequestPath(mapPath)
        if (saveName != null) {
            check(activeEngine.gameSaver.performAutosave(saveName, true)) {
                "Unable to load saved game: $saveName"
            }
        } else {
            activeEngine.currentMapPath = mapPath
            if (noFog) {
                activeEngine.networkEngine.roomSettings.fogMode = 0
                activeEngine.networkEngine.roomSettings.revealedMap = true
            }
            activeEngine.loadGame(true, GameMode.normal)
        }
    }

    private fun loadMemorySnapshot(activeEngine: GameEngine, snapshot: GameMemorySnapshot) {
        runningMenuBackground = false
        activeEngine.isStopped = false
        activeEngine.isPaused = false
        check(activeEngine.gameSaver.writeSaveToStream(GameInputStream(snapshot.saveBytes), false, false, false)) {
            "Unable to restore in-memory save: ${snapshot.mapPath}"
        }
    }

    private fun prepareSinglePlayerLocalTeam(activeEngine: GameEngine, playerName: String): GameTeam {
        PlayerTeam.resetTeamRegistry()
        val localPlayerTeam = GameTeam(0)
        localPlayerTeam.teamName = playerName.ifBlank { "Player" }
        localPlayerTeam.isTeamSpectator = false
        localPlayerTeam.isTeamControlledByAI = false
        localPlayerTeam.isTeamObserver = false
        localPlayerTeam.isTeamReady = true
        localPlayerTeam.teamPingTime = 0
        activeEngine.playerTeam = localPlayerTeam
        activeEngine.networkEngine?.localPlayerTeam = localPlayerTeam
        return localPlayerTeam
    }

    private fun loadBattleRoomMap(activeEngine: GameEngine, config: BattleRoomLaunchConfig) {
        prepareActiveGameLoad(activeEngine)
        activeEngine.currentMapPath = config.mapPath
        val networkEngine = activeEngine.networkEngine ?: run {
            loadDirectMap(
                activeEngine,
                if (config.savedGame) savedGameRequestPath(config.mapPath) else config.mapPath,
            )
            return
        }

        networkEngine.selectedMapPath = config.mapPath
        val playerName = networkEngine.playerName
            ?: activeEngine.settingsEngine.lastNetworkPlayerName
            ?: "Player"
        networkEngine.playerName = playerName
        networkEngine.requireActiveMods = true
        val localPlayerTeam = prepareSinglePlayerLocalTeam(activeEngine, playerName)

        val started =
            if (config.sandbox) networkEngine.startSandboxServer() else networkEngine.startSingleplayerServer()
        check(started) { "Unable to start Slick battle room" }
        check(networkEngine.localPlayerTeam === localPlayerTeam) {
            "Unable to start Slick battle room: local player team was not registered"
        }

        val settings = networkEngine.getEditableRoomSettings() ?: networkEngine.roomSettings
        settings.gameModeType = if (config.savedGame) GameModeType.savedGame else activeEngine.getGameModeType()
        settings.mapPath = if (config.savedGame) config.mapPath else activeEngine.getCurrentMapFilename()
        settings.applyBattleRoomOptions(
            if (config.sandbox) config.options.copy(fogMode = 0, revealedMap = true) else config.options
        )
        networkEngine.a(settings)

        repeat(config.aiPlayerCount.coerceAtLeast(0)) {
            networkEngine.addAIToGame()
        }
        config.teamLayout?.let { layout ->
            val slickLayout = layout.toSlickTeamLayout()
            if (slickLayout != null) {
                networkEngine.a(slickLayout)
            } else {
                applySlickCustomTeamMode(networkEngine, layout)
            }
        }

        startedBattleRoomGameSetupComplete = false
        check(networkEngine.startBattleRoomGame()) { "Unable to start Slick battle room game" }
        completeStartedBattleRoomGame(activeEngine)
    }

    private fun loadPendingMenuBackground(activeEngine: GameEngine) {
        if (!pendingMenuBackground || pendingMapPath != null) return
        pendingMenuBackground = false
        try {
            activeEngine.isStopped = true
            activeEngine.isPaused = true
            activeEngine.loadMenuBackground()
        } catch (error: Throwable) {
            onMapError(activeEngine.currentMapPath ?: "", error)
            throw error
        }
        runningMapPath = activeEngine.currentMapPath
        runningMenuBackground = true
        readyNotifiedMapPath = null
        renderedFramesForRunningMap = 0
    }

    private fun setupBattleRoomGame(activeEngine: GameEngine) {
        val networkEngine = activeEngine.networkEngine
        prepareActiveGameLoad(activeEngine)
        activeEngine.remoteMapStream = null
        when (networkEngine.roomSettings.gameModeType) {
            GameModeType.savedGame -> {
                if (!networkEngine.isServer) {
                    activeEngine.gameSaver.writeSaveToStream(networkEngine.receivedSaveGameStream, true, false, false)
                    activeEngine.gameUI?.messageManager?.addMessage(null, "Note: Game was started from a saved game.")
                } else {
                    activeEngine.gameSaver.performAutosave(networkEngine.roomSettings.mapPath, true)
                }
            }

            GameModeType.customMap -> {
                if (!networkEngine.isServer) {
                    activeEngine.currentMapPath = ""
                    activeEngine.remoteMapStream = networkEngine.receivedCustomMapStream
                    activeEngine.loadGame(true, GameMode.normal)
                    activeEngine.gameUI?.messageManager?.addMessage(
                        null,
                        "Note: Game was started from a custom map on server.",
                    )
                } else {
                    activeEngine.currentMapPath = networkEngine.selectedMapPath
                    activeEngine.loadGame(true, GameMode.normal)
                }
            }

            else -> {
                activeEngine.currentMapPath = networkEngine.selectedMapPath
                activeEngine.loadGame(true, GameMode.normal)
            }
        }
    }

    private fun completeStartedBattleRoomGame(activeEngine: GameEngine) {
        if (startedBattleRoomGameSetupComplete) return
        val networkEngine = activeEngine.networkEngine ?: return
        startedBattleRoomGameSetupComplete = true
        runCatching {
            setupBattleRoomGame(activeEngine)
            applyNoFogOverride(activeEngine)
            runningMenuBackground = false
            runningMapPath = networkEngine.selectedMapPath ?: activeEngine.currentMapPath
            readyNotifiedMapPath = null
            renderedFramesForRunningMap = 0
        }.onFailure { error ->
            startedBattleRoomGameSetupComplete = false
            throw error
        }
    }

    private fun applyNoFogOverride(activeEngine: GameEngine) {
        if (!noFog) return
        activeEngine.tileMap?.fogEnabled = false
        activeEngine.tileMap?.fogPeriodicMaintenanceEnabled = false
        activeEngine.tileMap?.fogRenderActive = true
    }

    private fun prepareActiveGameLoad(activeEngine: GameEngine) {
        runningMenuBackground = false
        activeEngine.isStopped = false
        activeEngine.isPaused = false
        activeEngine.minimap?.release()
    }

    private fun notifyReadyAfterVisibleFrames(activeEngine: GameEngine) {
        val mapPath = runningMapPath ?: return
        if (!shouldCountSlickReadyFrame(
                hasLoadedLevel = activeEngine.hasLoadedLevel,
                runningMapPath = mapPath,
                readyNotifiedMapPath = readyNotifiedMapPath,
            )
        ) {
            return
        }
        renderedFramesForRunningMap += 1
        if (renderedFramesForRunningMap >= READY_FRAME_COUNT) {
            readyNotifiedMapPath = mapPath
            onMapReady(mapPath)
        }
    }

    private fun updatePointerCursor(container: GameContainer, activeEngine: GameEngine) {
        val shouldApply = gameVisible &&
                activeEngine.hasLoadedLevel &&
                !activeEngine.isMenuBackgroundMap &&
                activeEngine.tileMap?.isCursorActive == true &&
                runningMapPath != null &&
                !runningMenuBackground
        if (shouldApply == pointerCursorApplied) return
        pointerCursorApplied = if (shouldApply) {
            applyPointerCursor(container)
        } else {
            runCatching { container.setDefaultMouseCursor() }
            false
        }
    }

    fun requestMap(mapPath: String) {
        resetFrameSnapshot()
        pendingMenuBackground = false
        runningMenuBackground = false
        startedBattleRoomGameSetupComplete = false
        pendingReplayName = null
        pendingBattleRoomConfig = null
        pendingMemorySnapshot = null
        pendingStartedGameMapPath = null
        pendingMapPath = mapPath
    }

    fun requestReplay(replayName: String) {
        resetFrameSnapshot()
        pendingMenuBackground = false
        runningMenuBackground = false
        startedBattleRoomGameSetupComplete = false
        pendingBattleRoomConfig = null
        pendingMapPath = null
        pendingMemorySnapshot = null
        pendingStartedGameMapPath = null
        pendingReplayName = replayName
    }

    fun requestBattleRoom(config: BattleRoomLaunchConfig) {
        resetFrameSnapshot()
        pendingMenuBackground = false
        runningMenuBackground = false
        startedBattleRoomGameSetupComplete = false
        pendingReplayName = null
        pendingBattleRoomConfig = config
        pendingMapPath = config.mapPath
        pendingMemorySnapshot = null
        pendingStartedGameMapPath = null
    }

    fun requestMemorySnapshot(snapshot: GameMemorySnapshot) {
        resetFrameSnapshot()
        pendingMenuBackground = false
        runningMenuBackground = false
        startedBattleRoomGameSetupComplete = false
        pendingReplayName = null
        pendingBattleRoomConfig = snapshot.battleRoomConfig
        pendingMemorySnapshot = snapshot
        pendingMapPath = snapshot.mapPath
        pendingStartedGameMapPath = null
    }

    fun requestStartedGame(mapPath: String) {
        resetFrameSnapshot()
        pendingMenuBackground = false
        runningMenuBackground = false
        startedBattleRoomGameSetupComplete = false
        pendingReplayName = null
        pendingBattleRoomConfig = null
        pendingMemorySnapshot = null
        pendingMapPath = null
        pendingStartedGameMapPath = mapPath
    }

    fun requestMenuBackground() {
        resetFrameSnapshot()
        pendingReplayName = null
        pendingBattleRoomConfig = null
        pendingMapPath = null
        pendingMemorySnapshot = null
        pendingStartedGameMapPath = null
        pendingMenuBackground = true
        startedBattleRoomGameSetupComplete = false
    }

    fun runningMapPath(): String? = runningMapPath

    fun currentFrameSnapshot(): SlickFrameSnapshot? = latestFrameSnapshot

    fun requestFrameSnapshot(timeoutMillis: Long): SlickFrameSnapshot? {
        val request = CompletableFuture<SlickFrameSnapshot?>()
        pendingFrameSnapshotRequest.getAndSet(request)?.complete(latestFrameSnapshot)
        return runCatching {
            request.get(timeoutMillis, TimeUnit.MILLISECONDS)
        }.getOrElse {
            pendingFrameSnapshotRequest.compareAndSet(request, null)
            latestFrameSnapshot
        }
    }

    fun setGameVisible(visible: Boolean, pausedBackground: Boolean = false) {
        gameVisible = visible
        this.pausedBackground = pausedBackground
    }

    fun requestSize(width: Int, height: Int) {
        pendingWidth = width
        pendingHeight = height
    }

    fun requestSaveGame(name: String) {
        pendingSaveName = name.toRwSaveName()
    }

    fun requestExportMap(name: String) {
        pendingExportMapName = name.toRwExportMapName()
    }

    fun requestReloadMods(): CompletableFuture<Unit> = modReloadQueue.request()

    fun requestSurrender() {
        pendingSurrender = true
    }

    fun requestChatMessage(message: String, teamOnly: Boolean) {
        pendingChatMessages.add(PendingChatMessage(message.trim(), teamOnly))
    }

    fun pollTextInputRequests(): List<SlickTextInputRequest> {
        val requests = mutableListOf<SlickTextInputRequest>()
        while (true) {
            requests += pendingTextInputRequests.poll() ?: break
        }
        return requests
    }

    fun submitTextInput(requestId: Int, text: String) {
        pendingTextInputResponses.add(PendingTextInputResponse(requestId, text, cancel = false))
    }

    fun cancelTextInput(requestId: Int) {
        pendingTextInputResponses.add(PendingTextInputResponse(requestId, null, cancel = true))
    }

    private fun savePendingGame(activeEngine: GameEngine) {
        val saveName = pendingSaveName ?: return
        pendingSaveName = null
        runCatching {
            activeEngine.gameSaver.updateAutosave(saveName, false)
            activeEngine.gameUI?.showMediumPriorityMessage("Game saved")
        }.onFailure { error ->
            GameEngine.log("Failed to save game: $saveName", error)
            activeEngine.gameUI?.showHighPriorityMessage("Save failed: ${error.message ?: error.javaClass.simpleName}")
        }
    }

    private fun exportPendingMap(activeEngine: GameEngine) {
        val exportName = pendingExportMapName ?: return
        pendingExportMapName = null
        val sourceMap = activeEngine.currentMapPath?.takeIf { it.isNotBlank() }
            ?: runningMapPath?.takeIf { it.isNotBlank() }
            ?: return
        val exportPath = "/SD/rustedWarfare/maps/$exportName.tmx"
        runCatching {
            activeEngine.tileMap.exportMapToPath(sourceMap, exportPath)
            activeEngine.gameUI?.showMediumPriorityMessage("Map exported: $exportName")
        }.onFailure { error ->
            GameEngine.log("Failed to export map: $exportPath", error)
            activeEngine.gameUI?.showHighPriorityMessage("Export failed: ${error.message ?: error.javaClass.simpleName}")
        }
    }

    private fun reloadPendingMods(activeEngine: GameEngine) {
        modReloadQueue.executePending {
            reloadMods(activeEngine)
        }
    }

    private fun reloadMods(activeEngine: GameEngine) {
        try {
            activeEngine.beginLoadingStatus("Loading custom unit data", 12)
            activeEngine.loadLevel("Loading custom unit data")
            activeEngine.modManager.saveModSelection()
            activeEngine.modManager.applyAndSaveMods()
            activeEngine.markLoadingStatusComplete("Mods reloaded")
            activeEngine.settingsEngine.save()
            activeEngine.gameUI?.showMediumPriorityMessage("Mods reloaded")
        } catch (error: Throwable) {
            GameEngine.log("Failed to reload mods", error)
            activeEngine.gameUI?.showHighPriorityMessage("Mod reload failed: ${error.message ?: error.javaClass.simpleName}")
            throw error
        }
    }

    fun loadingStatus(): GameLoadingStatus {
        val activeEngine = engine ?: GameEngine.getInstance()
        ?: return GameLoadingStatus("Starting renderer", 0.05f)
        return GameLoadingStatus(
            text = activeEngine.getLoadingText(),
            progress = activeEngine.getLoadingProgress().coerceIn(0.0f, 1.0f),
        )
    }

    private fun applyPendingInGameCommands(activeEngine: GameEngine) {
        if (pendingSurrender) {
            pendingSurrender = false
            activeEngine.networkEngine?.sendChatMessage("-surrender")
        }
        while (true) {
            val chat = pendingChatMessages.poll() ?: break
            if (chat.message.isBlank()) continue
            activeEngine.networkEngine?.sendChatMessage(if (chat.teamOnly) "-t ${chat.message}" else chat.message)
        }
    }

    private fun applyPendingTextInputResponses() {
        while (true) {
            val response = pendingTextInputResponses.poll() ?: break
            val handler = pendingTextInputHandlers.remove(response.requestId) ?: continue
            runCatching {
                if (response.cancel) {
                    handler.cancelPasswordEntry()
                } else {
                    handler.submitPassword(response.text.orEmpty())
                }
            }.onFailure { error ->
                GameEngine.log("Failed to apply Slick text input response", error)
            }
        }
    }

    private fun captureFrameSnapshotAfterWorldRender(container: GameContainer) {
        val request = pendingFrameSnapshotRequest.get()
        if (request == null && !runningMenuBackground) {
            return
        }
        if (runningMapPath == null || (!runningMenuBackground && !gameVisible)) {
            request?.let { completeFrameSnapshotRequest(it, latestFrameSnapshot) }
            return
        }
        val snapshot = captureFrameSnapshot(container) ?: latestFrameSnapshot
        request?.let { completeFrameSnapshotRequest(it, snapshot) }
    }

    private fun resetFrameSnapshot() {
        latestFrameSnapshot = null
    }

    private fun captureFrameSnapshot(container: GameContainer): SlickFrameSnapshot? {
        val width = container.width.takeIf { it > 0 }
        val height = container.height.takeIf { it > 0 }
        if (width == null || height == null) {
            return null
        }
        return runCatching {
            readBackBufferSnapshot(width, height, ++latestFrameSnapshotSequence).also { latestFrameSnapshot = it }
        }.onFailure { error ->
            GameEngine.log("Failed to capture Slick frame snapshot", error)
        }.getOrNull()
    }

    private fun completeFrameSnapshotRequest(
        request: CompletableFuture<SlickFrameSnapshot?>,
        snapshot: SlickFrameSnapshot?,
    ) {
        if (pendingFrameSnapshotRequest.compareAndSet(request, null)) {
            request.complete(snapshot)
        }
    }

    private fun readBackBufferSnapshot(width: Int, height: Int, sequence: Long): SlickFrameSnapshot {
        val buffer = BufferUtils.createByteBuffer(width * height * 4)
        GL11.glReadBuffer(GL11.GL_BACK)
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)

        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val sourceRow = height - 1 - y
            for (x in 0 until width) {
                val sourceOffset = (sourceRow * width + x) * 4
                val red = buffer.get(sourceOffset).toInt() and 0xff
                val green = buffer.get(sourceOffset + 1).toInt() and 0xff
                val blue = buffer.get(sourceOffset + 2).toInt() and 0xff
                pixels[y * width + x] = 0xff000000.toInt() or (red shl 16) or (green shl 8) or blue
            }
        }
        return SlickFrameSnapshot(width, height, pixels, sequence)
    }

    private fun applyPendingInputEvents(activeEngine: GameEngine) {
        var pendingMotion: SlickInputEvent.Motion? = null
        while (true) {
            val event = pendingInputEvents.poll() ?: break
            pendingInputEventCount.decrementAndGet()
            if (event is SlickInputEvent.Motion) {
                pendingMotion = event
                continue
            }
            pendingMotion?.let {
                applyInputEvent(activeEngine, it)
            }
            pendingMotion = null
            applyInputEvent(activeEngine, event)
        }
        pendingMotion?.let {
            applyInputEvent(activeEngine, it)
        }
    }

    private fun applyInputEvent(activeEngine: GameEngine, event: SlickInputEvent) {
        when (event) {
            is SlickInputEvent.PointerButton -> {
                view.pointerState.processEvent(event.x, event.y, event.down, event.pointerId)
            }

            is SlickInputEvent.Motion -> {
                view.pointerState.setStart(event.x, event.y)
            }

            is SlickInputEvent.Wheel -> {
                activeEngine.queueMouseWheelDelta(event.change)
            }

            is SlickInputEvent.Key -> {
                setKeyState(activeEngine, event.slickKey, event.down)
            }
        }
    }

    override fun mousePressed(button: Int, x: Int, y: Int) {
        val pointerId = pointerIdForButton(button)
        if (pointerId != -1) {
            enqueueInputEvent(SlickInputEvent.PointerButton(x.toFloat(), y.toFloat(), true, pointerId))
        }
        logSlickMenuInput("slickMousePressed button=$button pointerId=$pointerId xy=$x,$y ${engineTouchState()}")
    }

    override fun mouseReleased(button: Int, x: Int, y: Int) {
        val pointerId = pointerIdForButton(button)
        if (pointerId != -1) {
            enqueueInputEvent(SlickInputEvent.PointerButton(x.toFloat(), y.toFloat(), false, pointerId))
        }
        logSlickMenuInput("slickMouseReleased button=$button pointerId=$pointerId xy=$x,$y ${engineTouchState()}")
    }

    override fun mouseDragged(oldx: Int, oldy: Int, newx: Int, newy: Int) {
        enqueueInputEvent(SlickInputEvent.Motion(newx.toFloat(), newy.toFloat()))
    }

    override fun mouseMoved(oldx: Int, oldy: Int, newx: Int, newy: Int) {
        enqueueInputEvent(SlickInputEvent.Motion(newx.toFloat(), newy.toFloat()))
    }

    override fun mouseWheelMoved(change: Int) {
        enqueueInputEvent(SlickInputEvent.Wheel(change))
    }

    fun submitOverlayPointer(x: Float, y: Float, down: Boolean, pointerId: Int) {
        enqueueInputEvent(SlickInputEvent.PointerButton(x, y, down, pointerId))
    }

    fun moveOverlayPointer(x: Float, y: Float) {
        enqueueInputEvent(SlickInputEvent.Motion(x, y))
    }

    fun submitOverlayMouseWheel(change: Int) {
        enqueueInputEvent(SlickInputEvent.Wheel(change))
    }

    override fun keyPressed(key: Int, c: Char) {
        enqueueInputEvent(SlickInputEvent.Key(key, true))
    }

    override fun keyReleased(key: Int, c: Char) {
        enqueueInputEvent(SlickInputEvent.Key(key, false))
    }

    private fun enqueueInputEvent(event: SlickInputEvent) {
        pendingInputEventCount.incrementAndGet()
        pendingInputEvents.add(event)
    }

    private fun setKeyState(activeEngine: GameEngine, slickKey: Int, down: Boolean) {
        if (slickKey !in keyStates.indices || keyStates[slickKey] == down) return
        keyStates[slickKey] = down
        val androidKey = SlickToAndroidKeycodes.initializeGdxToAndroidMapping(slickKey)
        if (androidKey != 0) {
            activeEngine.setKeyState(androidKey, down)
        }
    }

    private fun pointerIdForButton(button: Int): Int = when (button) {
        Input.MOUSE_LEFT_BUTTON -> 1
        Input.MOUSE_RIGHT_BUTTON -> 2
        Input.MOUSE_MIDDLE_BUTTON -> 3
        else -> -1
    }

    private fun logSlickMenuInput(message: String) {
        if (debugSlickMenuInput) {
            GameEngine.log("RWX_DEBUG_SLICK_MENU $message")
        }
    }

    private fun engineTouchState(): String {
        val activeEngine = engine ?: return "engine=null"
        return runCatching {
            val pointerCount = activeEngine.getTouchPointerCount()
            val pointerId0 = if (pointerCount > 0) activeEngine.getTouchPointerId(0) else -1
            "touch=${activeEngine.getTouchX()},${activeEngine.getTouchY()} touchDown=${activeEngine.isTouchDown()} pointerCount=$pointerCount pointerId0=$pointerId0 menuDragging=${activeEngine.gameUI?.isDraggingSelection}"
        }.getOrElse { error ->
            "engineTouchStateError=${error.javaClass.simpleName}:${error.message.orEmpty()}"
        }
    }

    private class SlickCoreGameView(
        private val menuController: InGameMenuController,
    ) : CoreGameView {
        val pointerState = MultiTouchPointerState()
        private var rendering = true

        override fun pause() {
            rendering = false
        }

        override fun isPaused(): Boolean = true

        override fun isContinuousRendering(): Boolean = true

        override fun isRendering(): Boolean = rendering

        override fun getInGameMenuController(): InGameMenuController = menuController

        override fun onResume() {
            rendering = true
        }

        override fun getSettings(): MultiTouchPointerState = pointerState

        override fun onSizeChanged() = Unit

        override fun stopRender() {
            rendering = false
        }
    }

    private sealed interface SlickInputEvent {
        data class PointerButton(
            val x: Float,
            val y: Float,
            val down: Boolean,
            val pointerId: Int,
        ) : SlickInputEvent

        data class Motion(
            val x: Float,
            val y: Float,
        ) : SlickInputEvent

        data class Wheel(
            val change: Int,
        ) : SlickInputEvent

        data class Key(
            val slickKey: Int,
            val down: Boolean,
        ) : SlickInputEvent
    }

    private companion object {
        const val SLICK_KEY_COUNT = 224
        const val READY_FRAME_COUNT = 1
    }
}

private fun String.toRwSaveName(): String =
    trim()
        .ifBlank { "rwx_save" }
        .replace(".", "_")
        .replace("/", "_")
        .replace("\\", "_")

private fun String.toRwExportMapName(): String =
    trim()
        .ifBlank { "rwx_map" }
        .replace(".", "_")
        .replace("/", "_")
        .replace("\\", "_")
        .replace("|", "_")
        .replace("?", "_")
        .replace(":", "_")
        .replace("*", "_")
        .replace("\"", "_")
        .replace("<", "_")
        .replace(">", "_")

private data class PendingChatMessage(
    val message: String,
    val teamOnly: Boolean,
)

private data class PendingTextInputResponse(
    val requestId: Int,
    val text: String?,
    val cancel: Boolean,
)

private data class PendingPlayerConfig(
    val playerId: String,
    val spawn: Int?,
    val team: Int?,
)

private fun BattleRoomTeamLayout.toSlickTeamLayout(): TeamLayoutType? =
    when (this) {
        TwoSides -> TeamLayoutType.layout_2sides
        ThreeSides -> TeamLayoutType.layout_3sides
        Ffa -> TeamLayoutType.layout_ffa
        Spectators -> TeamLayoutType.layout_spectators
        Random, AllVsAi, AllVs2 -> null
    }

private fun applySlickCustomTeamMode(networkEngine: NetworkEngine, layout: BattleRoomTeamLayout) {
    if (!networkEngine.isServer) return
    val teams = PlayerTeam.getTeams().filter { !it.isSpectatorTeamColor() }
    when (layout) {
        AllVsAi -> teams.forEach { it.teamColorId = if (it.isTeamControlledByAI) 1 else 0 }
        AllVs2 -> teams.forEach { it.teamColorId = if (it.teamId == 0) 1 else 0 }
        Random -> {
            val shuffled = teams.shuffled()
            val teamCount = when (shuffled.size) {
                in 0..8 -> 2
                in 9..21 -> 3
                in 22..32 -> 4
                else -> 5
            }
            var leftCount = teamCount
            var currentTeam = 0
            shuffled.forEach { team ->
                team.teamColorId = currentTeam
                leftCount--
                if (leftCount == 0) {
                    leftCount = teamCount
                    currentTeam++
                }
            }
        }

        else -> return
    }
}

data class SlickFrameSnapshot(
    val width: Int,
    val height: Int,
    val pixels: IntArray,
    val sequence: Long,
)
