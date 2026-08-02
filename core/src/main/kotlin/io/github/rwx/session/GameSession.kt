package io.github.rwx.session

import com.corrodinggames.rts.game.PlayerTeam
import com.corrodinggames.rts.game.map.TileMap
import com.corrodinggames.rts.game.units.UnitTypeEnum
import com.corrodinggames.rts.gameFramework.*
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
import com.corrodinggames.rts.gameFramework.network.*
import com.corrodinggames.rts.gameFramework.network.ChatMessage
import io.github.rwx.app.launchOnIO
import io.github.rwx.applyBattleRoomPlayerConfig
import io.github.rwx.battleRoomPlayerDisplayName
import io.github.rwx.battleRoomPlayerPingLabel
import io.github.rwx.geometry.Point
import io.github.rwx.logger
import io.github.rwx.map.MapMetadata
import io.github.rwx.map.TransferredUnit
import io.github.rwx.net.CoreUiNetworkCallbacks
import io.github.rwx.platform.CoreGameView
import io.github.rwx.render.RendererMode
import io.github.rwx.render.canvas.KoolCanvasFrame
import io.github.rwx.render.canvas.KoolCanvasViewport
import io.github.rwx.ui.BattleRoomUiBridge
import io.github.rwx.ui.InGameMenuCallbacks
import io.github.rwx.ui.InGameMenuController
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

private const val SAVED_GAME_REQUEST_PREFIX: String = "rwx-save://"

fun savedGameRequestPath(saveName: String): String = SAVED_GAME_REQUEST_PREFIX + saveName

fun savedGameNameFromRequestPath(path: String): String? =
    path.takeIf { it.startsWith(SAVED_GAME_REQUEST_PREFIX) }
        ?.removePrefix(SAVED_GAME_REQUEST_PREFIX)
        ?.takeIf { it.isNotBlank() }

data class RunningMultiplayerExitInfo(
    val isHost: Boolean,
)

fun NetworkEngine.hasActiveStartedGameConnection(): Boolean =
    gameHasBeenStarted &&
            (singleplayerServer || (
                    networkGameActive &&
                            (isServer || getActiveServerConnection()?.isConnected == true)
                    ))

internal fun battleRoomPlayerTeams(): List<PlayerTeam> = PlayerTeam.getSortedTeams(true)

abstract class GameSession {
    protected val gameLock = Any()
    private val engineStartLock = Any()
    val inGameMenuController = InGameMenuController()
    protected val sessionLogName: String
        get() = rendererMode.id.uppercase().replace('-',' ')+" RW"
    protected abstract val rendererMode: RendererMode
    protected open val defaultPreloadViewport: KoolCanvasViewport = KoolCanvasViewport(1280, 720)

    @Volatile
    private var rendererProfile: GameSessionRendererProfile = GameSessionRendererProfile()
    open val rendersIntoKoolCanvas: Boolean
        get() = rendererProfile.rendersIntoKoolCanvas
    open val acceptsKoolInput: Boolean
        get() = rendererProfile.acceptsKoolInput
    open val canStartNewSessionInPlace: Boolean
        get() = rendererProfile.canStartNewSessionInPlace
    open val usesNativeSurfaceForResumeBackground: Boolean
        get() = rendererProfile.usesNativeSurfaceForResumeBackground

    @Volatile
    protected var gameEngine: GameEngine? = null

    /**
     * Every pending-request / load-progress value the session tracks, as one immutable state.
     *
     * All mutation goes through [updateLoadState] — a lock-free CAS loop — so writers on any
     * thread never block, and readers always observe a *consistent combination* of these fields
     * instead of the half-updated views the previous individual `@Volatile` fields allowed.
     * [gameLock] now guards engine access only, never this state.
     */
    protected data class SessionLoadState(
        val pendingMapPath: String? = null,
        val pendingMemorySnapshot: GameMemorySnapshot? = null,
        val pendingRendererBattleRoomConfig: BattleRoomLaunchConfig? = null,
        val activeRendererBattleRoomConfig: BattleRoomLaunchConfig? = null,
        val runningMapPath: String? = null,
        val asyncMapLoadPath: String? = null,
        val asyncMapLoadInProgress: Boolean = false,
        val asyncMapLoadError: Throwable? = null,
        val mapLoadGeneration: Long = 0L,
        val menuBackgroundActive: Boolean = false,
    )

    private val loadStateRef = AtomicReference(SessionLoadState())

    /** Consistent snapshot of the session's pending-request / load-progress state. */
    protected val loadState: SessionLoadState
        get() = loadStateRef.get()

    /**
     * Atomically applies [transform] and returns the state it installed (or the unchanged current
     * state when the transform was a no-op). [transform] may run more than once under contention,
     * so it must be pure.
     */
    protected fun updateLoadState(transform: (SessionLoadState) -> SessionLoadState): SessionLoadState {
        while (true) {
            val current = loadStateRef.get()
            val next = transform(current)
            if (next == current) return current
            if (loadStateRef.compareAndSet(current, next)) return next
        }
    }

    @Volatile
    protected var lastViewport: KoolCanvasViewport = KoolCanvasViewport(0, 0)

    @Volatile
    protected var lastFrame: KoolCanvasFrame = KoolCanvasFrame(lastViewport, emptyList())

    private val asyncEnginePreloadInProgress = AtomicBoolean(false)

    @Volatile
    protected var asyncEnginePreloadError: Throwable? = null

    /**
     * True while a mod reload owns the engine (and typically [gameLock]) for seconds. UI-facing
     * reads consult this *before* touching [gameLock] so the frame loop never parks behind the
     * reload — set it before acquiring the lock, clear it after releasing.
     */
    @Volatile
    protected var modReloadInProgress: Boolean = false

    /**
     * True while some background worker holds the engine for a long stretch (map load, engine
     * preload, mod reload). UI-facing reads return a fast fallback instead of blocking on
     * [gameLock] behind that work.
     */
    protected fun isEngineBusyForUiReads(): Boolean =
        modReloadInProgress || asyncEnginePreloadInProgress.get() || loadState.asyncMapLoadInProgress

    // Last successfully built engine-backed UI views. Rebuilds happen on battle-room UI events and
    // the app layer's 500ms network poll; while the engine is busy, readers get these instead of
    // parking on gameLock behind a multi-second load.
    private val publishedBattleRoom = AtomicReference<BattleRoomSnapshot?>(null)
    private val publishedPlayerList = AtomicReference<List<BattleRoomPlayerSnapshot>>(emptyList())
    private val publishedChatHistory = AtomicReference<List<MultiplayerChatSnapshot>>(emptyList())

    @Volatile
    private var activeBattleRoomJoinConnector: SocketConnector? = null

    @Volatile
    private var cachedBattleRoomRequiredModsSummary: String? = null

    @Volatile
    private var cachedBattleRoomNetworkStatusText: String? = null

    @Volatile
    var latestBattleRoomJoinError: String? = null

    /**
     * Runs [command] against the live engine on whichever thread owns it and returns its result,
     * or null when no engine is available or the owner could not complete it in time.
     *
     * Base behavior executes inline under [gameLock]; that is correct wherever the engine's owner
     * itself serializes on [gameLock] (Android's tick, the background loaders). A backend whose
     * owner thread never takes [gameLock] — the embedded Slick render thread — overrides this to
     * hand the command to that thread, so UI-triggered engine access stops racing the game loop.
     */
    protected open fun <T> runEngineCommand(label: String, command: (GameEngine) -> T?): T? =
        synchronized(gameLock) {
            val engine = activeEngineLocked() ?: return null
            runCatching { command(engine) }
                .onFailure { error -> logger.warn(error) { "Engine command failed: $label" } }
                .getOrNull()
        }

    /** Fire-and-forget form of [runEngineCommand]: the caller never waits for the result. */
    protected open fun postEngineCommand(label: String, command: (GameEngine) -> Unit) {
        runEngineCommand(label, command)
    }

    open fun configureRendererProfile(profile: GameSessionRendererProfile) {
        rendererProfile = profile
    }

    open fun updateFrame(
        viewport: KoolCanvasViewport,
        deltaSeconds: Float,
        drainVisibleLayerBuffers: Boolean = false,
    ): KoolCanvasFrame = lastFrame

    open fun currentFrame(): KoolCanvasFrame = lastFrame

    open fun setGameVisible(
        visible: Boolean,
        viewport: KoolCanvasViewport,
        koolOverlay: Boolean = false,
        pausedBackground: Boolean = false,
    ) = Unit

    open fun submitPointer(
        screenX: Float,
        screenY: Float,
        isDown: Boolean,
        pointerId: Int,
    ) = Unit

    open fun movePointer(screenX: Float, screenY: Float) = Unit

    open fun submitKey(androidKeyCode: Int, isDown: Boolean) {
        // Input during a long engine hold is meaningless (nothing interactive is running) and must
        // not park the UI thread on gameLock — drop it.
        if (isEngineBusyForUiReads()) return
        postEngineCommand("key state") { engine ->
            engine.setKeyState(androidKeyCode, isDown)
        }
    }

    open fun submitMouseWheel(amount: Int) {
        if (amount == 0) return
        if (isEngineBusyForUiReads()) return
        postEngineCommand("mouse wheel") { engine ->
            engine.queueMouseWheelDelta(amount)
        }
    }

    open fun setInGameMenuCallbacks(callbacks: InGameMenuCallbacks?) {
        inGameMenuController.setCallbacks(callbacks)
    }

    open fun preload(viewport: KoolCanvasViewport): GameEngine {
        lastViewport = viewport
        val engine = ensureStartedOutsideGameLock(viewport)
        synchronized(gameLock) {
            applyViewport(engine, viewport)
        }
        return engine
    }

    open fun ensureRendererEngine(
        viewport: KoolCanvasViewport,
        graphicsEngine: GraphicsEngine,
        view: CoreGameView,
        platformCallbacks: PlatformCallbacks? = null,
    ): GameEngine {
        synchronized(gameLock) {
            lastViewport = viewport
            val width = viewport.width.coerceAtLeast(1)
            val height = viewport.height.coerceAtLeast(1)
            GameEngine.screenSize = Point(width, height)
            GameEngine.graphicsEngine = graphicsEngine
            GameEngine.externalGameLoopDriver = true
            val engine = ensureSessionEngineLocked {
                GameEngine.createGameEngine(platformCallbacks)
            }
            engine.renderGraphicsEngine = graphicsEngine
            engine.minimap?.bindGraphicsBackend(graphicsEngine)
            TileMap.layerBufferManager.bindGraphicsBackend(graphicsEngine)
            TileMap.bindGraphicsBackend(graphicsEngine)
            engine.colorizeLogMessage(view, false)
            view.onResume()
            engine.updateWindowResolution(width, height)
            asyncEnginePreloadInProgress.set(false)
            asyncEnginePreloadError = null
            return engine
        }
    }

    open fun prepareEngineAsync(viewport: KoolCanvasViewport) {
        if (gameEngine != null) {
            return
        }
        if (!asyncEnginePreloadInProgress.compareAndSet(false, true)) {
            return
        }
        asyncEnginePreloadError = null
        lastViewport = viewport
        logger.info { "Preparing $sessionLogName engine asynchronously" }
        launchOnIO("${rendererMode.id}-engine-preloader") {
            preloadEngineInBackground(viewport)
        }
    }

    open fun isPreparingEngine(): Boolean = asyncEnginePreloadInProgress.get()

    open fun requestMap(mapPath: String?) {
        updateLoadState {
            it.copy(
                menuBackgroundActive = false,
                pendingRendererBattleRoomConfig = null,
                activeRendererBattleRoomConfig = null,
                pendingMemorySnapshot = null,
                pendingMapPath = mapPath?.takeIf { path -> path.isNotBlank() },
            )
        }
    }

    open fun requestMemorySnapshot(snapshot: GameMemorySnapshot) {
        updateLoadState {
            it.copy(
                menuBackgroundActive = false,
                pendingRendererBattleRoomConfig = snapshot.battleRoomConfig,
                pendingMemorySnapshot = snapshot,
                pendingMapPath = snapshot.mapPath,
            )
        }
    }

    open fun prepareMapAsync(mapPath: String?, viewport: KoolCanvasViewport) {
        val requestedMapPath = mapPath?.takeIf { it.isNotBlank() } ?: return
        if (isMapLoaded(requestedMapPath)) {
            return
        }
        val generation = tryBeginAsyncLoad(
            requestKey = requestedMapPath,
            viewport = viewport,
            resetLastFrame = { it.runningMapPath != requestedMapPath },
            mutate = { it.copy(pendingRendererBattleRoomConfig = null, activeRendererBattleRoomConfig = null) },
        ) ?: return
        logger.info { "Preparing $sessionLogName map asynchronously: $requestedMapPath" }
        launchOnIO("${rendererMode.id}-map-loader") {
            loadMapInBackground(requestedMapPath, viewport, generation)
        }
    }

    open fun prepareMemorySnapshotAsync(snapshot: GameMemorySnapshot, viewport: KoolCanvasViewport) {
        if (isMapLoaded(snapshot.mapPath)) {
            return
        }
        val generation = tryBeginAsyncLoad(
            requestKey = snapshot.mapPath,
            viewport = viewport,
            resetLastFrame = { true },
            mutate = {
                it.copy(
                    pendingMapPath = snapshot.mapPath,
                    pendingMemorySnapshot = snapshot,
                    pendingRendererBattleRoomConfig = snapshot.battleRoomConfig,
                )
            },
        ) ?: return
        logger.info { "Preparing $sessionLogName memory snapshot asynchronously: ${snapshot.mapPath}" }
        launchOnIO("${rendererMode.id}-memory-snapshot-loader") {
            loadMemorySnapshotInBackground(snapshot, viewport, generation)
        }
    }

    open fun prepareReplayAsync(replayName: String, viewport: KoolCanvasViewport) {
        val requestedReplayName = replayName.takeIf { it.isNotBlank() } ?: return
        val generation = tryBeginAsyncLoad(
            requestKey = requestedReplayName,
            viewport = viewport,
            resetLastFrame = { true },
        ) ?: return
        logger.info { "Preparing $sessionLogName replay asynchronously: $requestedReplayName" }
        launchOnIO("${rendererMode.id}-replay-loader") {
            loadReplayInBackground(requestedReplayName, viewport, generation)
        }
    }

    open fun prepareBattleRoomAsync(config: BattleRoomLaunchConfig, viewport: KoolCanvasViewport) {
        val requestedMapPath = config.mapPath.takeIf { it.isNotBlank() } ?: return
        if (isMapLoaded(requestedMapPath) && loadState.activeRendererBattleRoomConfig == config) {
            return
        }
        val generation = tryBeginAsyncLoad(
            requestKey = requestedMapPath,
            viewport = viewport,
            resetLastFrame = { it.runningMapPath != requestedMapPath },
            mutate = {
                it.copy(
                    pendingMapPath = requestedMapPath,
                    pendingMemorySnapshot = null,
                    pendingRendererBattleRoomConfig = config,
                )
            },
        ) ?: return
        logger.info { "Preparing $sessionLogName battle room map asynchronously: $requestedMapPath" }
        launchOnIO("${rendererMode.id}-battleroom-map-loader") {
            loadMapInBackground(requestedMapPath, viewport, generation)
        }
    }

    /**
     * Registers a new asynchronous load and returns its [SessionLoadState.mapLoadGeneration]
     * ticket, or null when an identical request is already in flight. [mutate] stages the
     * request-specific pending fields inside the same atomic update.
     *
     * The ticket is what makes a superseded load harmless: the loader compares it against the
     * current generation before it publishes a frame or records an error.
     */
    private fun tryBeginAsyncLoad(
        requestKey: String,
        viewport: KoolCanvasViewport,
        resetLastFrame: (SessionLoadState) -> Boolean,
        mutate: (SessionLoadState) -> SessionLoadState = { it },
    ): Long? {
        var ticket: Long? = null
        var resetFrame = false
        updateLoadState { current ->
            if (current.asyncMapLoadInProgress && current.asyncMapLoadPath == requestKey) {
                ticket = null
                current
            } else {
                resetFrame = resetLastFrame(current)
                mutate(current).copy(
                    mapLoadGeneration = current.mapLoadGeneration + 1,
                    asyncMapLoadPath = requestKey,
                    asyncMapLoadInProgress = true,
                    asyncMapLoadError = null,
                    menuBackgroundActive = false,
                ).also { ticket = it.mapLoadGeneration }
            }
        }
        val issuedTicket = ticket ?: return null
        if (resetFrame) {
            lastFrame = KoolCanvasFrame(viewport, emptyList())
        }
        return issuedTicket
    }

    open fun adoptStartedGameFromEngine(viewport: KoolCanvasViewport): Boolean = false

    /**
     * Queues [config] as the next level to load, launching straight into the game.
     *
     * The room is only a staging area here: the very next frame loads it. Use
     * [openLocalBattleRoomDraft] instead when the host should stay in the room and keep editing it.
     */
    open fun prepareLocalBattleRoom(config: BattleRoomLaunchConfig): Boolean {
        updateLoadState {
            it.copy(
                menuBackgroundActive = false,
                pendingMapPath = config.mapPath.takeIf { path -> path.isNotBlank() },
                pendingRendererBattleRoomConfig = config,
            )
        }
        disconnectUnstartedNetworking("starting local battleroom")
        lastFrame = KoolCanvasFrame(lastViewport, emptyList())
        return true
    }

    /**
     * Opens [config] as an editable draft room without loading its level.
     *
     * The draft is what [currentBattleRoom] reports and what the `*BattleRoom*` mutators edit until
     * the host presses "Start game". Unlike [prepareLocalBattleRoom] this queues no load, so the
     * room stays open instead of being consumed by the next frame.
     */
    open fun openLocalBattleRoomDraft(config: BattleRoomLaunchConfig): Boolean {
        updateLoadState {
            it.copy(
                menuBackgroundActive = false,
                pendingRendererBattleRoomConfig = config,
            )
        }
        disconnectUnstartedNetworking("starting local battleroom")
        lastFrame = KoolCanvasFrame(lastViewport, emptyList())
        return true
    }

    /** Drops a network room that was opened but never started. */
    private fun disconnectUnstartedNetworking(reason: String) {
        synchronized(gameLock) {
            gameEngine?.networkEngine?.takeIf { it.networkGameActive && !it.gameHasBeenStarted }
                ?.disconnectNetworking(reason)
        }
    }

    open fun enterLocalBattleRoomLive(config: BattleRoomLaunchConfig): Boolean =
        runBattleRoomCommand("enter local battle room") { engine, networkEngine ->
            if (networkEngine.networkGameActive && !networkEngine.gameHasBeenStarted) {
                networkEngine.disconnectNetworking("starting local battleroom")
            }
            initBattleRoomMap(engine, config.mapPath, force = true, savedGame = config.savedGame)
            engine.configureLocalBattleRoom(config, "$sessionLogName local")

            // Keep the config around so an in-game map switch / memory snapshot can rebuild the room,
            // but do NOT mark the level running: the level loads only at "Start game".
            updateLoadState { it.copy(activeRendererBattleRoomConfig = config) }
            BattleRoomUiBridge.updateUI()
            true
        }

    /** True when a live engine-backed battle room is open (host or client), started or not. */
    open fun isBattleRoomLive(): Boolean {
        if (gameEngine == null && GameEngine.getInstance() == null) return false
        if (isEngineBusyForUiReads()) return publishedBattleRoom.get() != null
        return synchronized(gameLock) { currentBattleRoomFromEngineLocked() != null }
    }

    open fun prepareMenuBackgroundAsync(viewport: KoolCanvasViewport) = Unit

    open fun isMenuBackgroundActive(): Boolean = loadState.menuBackgroundActive

    open fun hostBattleRoom(config: BattleRoomHostConfig): Boolean =
        runBattleRoomCommand("host battle room") { engine, networkEngine ->
            if (networkEngine.networkGameActive) {
                networkEngine.disconnectNetworking("starting new host")
            }
            engine.currentMapPath = config.mapPath
            networkEngine.roomPassword = config.password
            networkEngine.publishToMasterServer = config.isPublic
            networkEngine.requireActiveMods = config.useMods
            initBattleRoomMap(engine, config.mapPath, force = true, savedGame = config.savedGame)
            check(networkEngine.startServerHosting(false)) { "Unable to host battle room" }
            networkEngine.p2pSession = config.rwxP2PSession
            true
        }

    open fun joinBattleRoom(address: String, serverId: String?, p2pSession: Boolean): Boolean {
        val trimmedAddress = address.trim()
        if (trimmedAddress.isBlank()) {
            latestBattleRoomJoinError = "Missing server address"
            return false
        }
        return runBattleRoomCommand("join battle room") { engine, networkEngine ->
            activeBattleRoomJoinConnector?.a()
            activeBattleRoomJoinConnector = null
            latestBattleRoomJoinError = null
            networkEngine.serverAddress = serverId
            initBattleRoomMap(engine, networkEngine.selectedMapPath, force = false)
            lateinit var connector: SocketConnector
            connector = SocketConnector(
                trimmedAddress,
                true,
                Runnable {
                    if (activeBattleRoomJoinConnector !== connector) {
                        return@Runnable
                    }
                    val errorMessage = connector.errorMessage
                    if (errorMessage != null) {
                        latestBattleRoomJoinError = "Connection failed: $errorMessage"
                        activeBattleRoomJoinConnector = null
                        return@Runnable
                    }
                    val socket = connector.connectedSocket
                    if (socket == null) {
                        latestBattleRoomJoinError = "Connection failed: no socket returned"
                        activeBattleRoomJoinConnector = null
                        return@Runnable
                    }
                    runCatching {
                        networkEngine.disconnectNetworking("starting new")
                        networkEngine.a(socket)
                        networkEngine.p2pSession = p2pSession
                        BattleRoomUiBridge.updateUI()
                    }.onFailure { error ->
                        latestBattleRoomJoinError = "Connection failed: ${error.message ?: error.javaClass.simpleName}"
                    }
                    activeBattleRoomJoinConnector = null
                },
            )
            activeBattleRoomJoinConnector = connector
            connector.b()
            true
        }
    }

    val isJoiningBattleRoom: Boolean
        get() = activeBattleRoomJoinConnector != null

    open fun cancelBattleRoomJoin() {
        activeBattleRoomJoinConnector?.a()
        activeBattleRoomJoinConnector = null
    }

    open fun leaveBattleRoom() {
        activeBattleRoomJoinConnector?.a()
        activeBattleRoomJoinConnector = null
        latestBattleRoomJoinError = null
        publishedBattleRoom.set(null)
        publishedPlayerList.set(emptyList())
        publishedChatHistory.set(emptyList())
        updateLoadState {
            it.copy(
                pendingRendererBattleRoomConfig = null,
                pendingMapPath = null,
            )
        }
        synchronized(gameLock) {
            val engine = gameEngine ?: GameEngine.getInstance() ?: return
            engine.networkEngine?.takeIf { it.networkGameActive && !it.gameHasBeenStarted }
                ?.disconnectNetworking("left battleroom")
        }
    }

    open fun currentBattleRoom(refreshNetworkStatus: Boolean = true): BattleRoomSnapshot? {
        loadState.pendingRendererBattleRoomConfig?.toBattleRoomSnapshot()?.let { return it }
        if (gameEngine == null && GameEngine.getInstance() == null) {
            return null
        }
        // While a loader/reload owns the engine, serve the last published room instead of parking
        // the UI thread on gameLock for the duration of that work.
        if (isEngineBusyForUiReads()) {
            return publishedBattleRoom.get()
        }
        return synchronized(gameLock) {
            currentBattleRoomFromEngineLocked(refreshNetworkStatus)
        }.also(publishedBattleRoom::set)
    }

    /**
     * Applies a battle room change to whichever room is real right now.
     *
     * Until the host presses "Start game" the room exists only as a draft config inside
     * [loadState]: a local staging area with no engine or network state, so an edit rewrites that
     * config via one atomic state update. Once the engine owns a live room the same edit becomes a
     * network command instead.
     *
     * @param editDraft returns the replacement state for a draft edit, or null to reject it.
     */
    private inline fun editBattleRoom(
        label: String,
        crossinline editDraft: (SessionLoadState, BattleRoomLaunchConfig) -> SessionLoadState?,
        noinline commandLiveRoom: (GameEngine, NetworkEngine) -> Boolean,
    ): Boolean {
        val hasLiveRoom = synchronized(gameLock) { currentBattleRoomFromEngineLocked() != null }
        if (!hasLiveRoom) {
            var applied = false
            updateLoadState { current ->
                val draft = current.pendingRendererBattleRoomConfig
                val edited = draft?.let { editDraft(current, it) }
                if (edited == null) {
                    applied = false
                    current
                } else {
                    applied = true
                    edited
                }
            }
            return applied
        }
        return runBattleRoomCommand(label, commandLiveRoom)
    }

    open fun setBattleRoomMap(mapPath: String, savedGame: Boolean = false): Boolean {
        val requestedMapPath = mapPath.takeIf { it.isNotBlank() } ?: return false
        return editBattleRoom(
            "set battle room map",
            editDraft = { state, draft ->
                state.copy(
                    pendingMapPath = requestedMapPath,
                    pendingRendererBattleRoomConfig = draft.copy(mapPath = requestedMapPath, savedGame = savedGame),
                )
            },
        ) { engine, networkEngine ->
            initBattleRoomMap(engine, requestedMapPath, force = true, savedGame = savedGame)
            networkEngine.getEditableRoomSettings()?.let { settings ->
                settings.gameModeType = if (savedGame) GameModeType.savedGame else engine.getGameModeType()
                settings.mapPath = if (savedGame) requestedMapPath else engine.getCurrentMapFilename()
                networkEngine.a(settings)
            }
            BattleRoomUiBridge.updateUI()
            true
        }
    }

    open fun addBattleRoomAi(count: Int): Boolean {
        val addCount = count.coerceAtLeast(0)
        if (addCount == 0) return true
        return editBattleRoom(
            "add battle room AI",
            editDraft = { state, draft ->
                state.copy(pendingRendererBattleRoomConfig = draft.copy(aiPlayerCount = draft.aiPlayerCount + addCount))
            },
        ) { _, networkEngine ->
            repeat(addCount) {
                networkEngine.addAIToGame()
            }
            BattleRoomUiBridge.updateUI()
            true
        }
    }

    open fun setBattleRoomMaxPlayers(maxPlayers: Int): Boolean {
        val target = maxPlayers.coerceIn(BATTLE_ROOM_MIN_MAX_PLAYERS, BATTLE_ROOM_MAX_MAX_PLAYERS)
        // Team slots are an engine-global, so there is no draft equivalent to edit.
        return runBattleRoomCommand("set battle room max players") { _, networkEngine ->
            if (!networkEngine.isServer) return@runBattleRoomCommand false
            if (PlayerTeam.TEAM_NEUTRAL == target) return@runBattleRoomCommand true
            PlayerTeam.setMaxTeamId(target, true)
            BattleRoomUiBridge.updateUI()
            true
        }
    }

    open fun applyBattleRoomTeamLayout(layout: BattleRoomTeamLayout): Boolean =
        editBattleRoom(
            "apply battle room team layout",
            editDraft = { state, draft ->
                state.copy(pendingRendererBattleRoomConfig = draft.copy(teamLayout = layout))
            },
        ) { _, networkEngine ->
            networkEngine.applyBattleRoomTeamLayout(layout)
            BattleRoomUiBridge.updateUI()
            true
        }

    open fun applyBattleRoomOptions(options: BattleRoomOptions): Boolean =
        editBattleRoom(
            "apply battle room options",
            editDraft = { state, draft ->
                state.copy(pendingRendererBattleRoomConfig = draft.copy(options = options))
            },
        ) { _, networkEngine ->
            val settings = networkEngine.getEditableRoomSettings() ?: networkEngine.roomSettings
            settings.applyBattleRoomOptions(options)
            networkEngine.a(settings)
            BattleRoomUiBridge.updateUI()
            true
        }

    open fun configureBattleRoomPlayer(
        playerId: String,
        spawn: Int?,
        team: Int?,
        startingUnits: Int? = null,
        aiDifficulty: Int? = null,
    ): Boolean =
        editBattleRoom(
            "configure battle room player",
            // A draft slot carries no per-player overrides yet, so this only reports whether the
            // player exists and returns the state unchanged.
            editDraft = { state, draft ->
                state.takeIf { resolveBattleRoomPlayerSnapshot(playerId, draft.pendingBattleRoomPlayers()) != null }
            },
        ) { engine, networkEngine ->
            val player = resolveBattleRoomPlayer(playerId, engine, networkEngine)
                ?: return@editBattleRoom false
            applyBattleRoomPlayerConfig(networkEngine, player, spawn, team, startingUnits, aiDifficulty)
        }

    open fun kickBattleRoomPlayer(playerId: String): Boolean =
        editBattleRoom(
            "kick battle room player",
            // Local draft room: drop the AI slot from the pending config.
            editDraft = { state, draft ->
                val index = draftAiPlayerIndex(playerId)
                if (index == null || index <= 0 || index > draft.aiPlayerCount) {
                    null
                } else {
                    state.copy(pendingRendererBattleRoomConfig = draft.copy(aiPlayerCount = draft.aiPlayerCount - 1))
                }
            },
        ) { engine, networkEngine ->
            if (!(networkEngine.isServer || networkEngine.isProxyController)) {
                return@editBattleRoom false
            }
            val player = resolveBattleRoomPlayer(playerId, engine, networkEngine)
                ?: return@editBattleRoom false
            if (player == networkEngine.localPlayerTeam || player == engine.playerTeam) {
                return@editBattleRoom false
            }
            networkEngine.e(player)
            BattleRoomUiBridge.updateUI()
            true
        }

    open fun startBattleRoom(): Boolean =
        runBattleRoomCommand("start battle room") { _, networkEngine ->
            check(networkEngine.startBattleRoomGame()) { "Unable to start battle room game" }
            true
        }

    open fun sendBattleRoomMessage(message: String): Boolean {
        if (message.isBlank()) return false
        val hasLiveRoom = synchronized(gameLock) { currentBattleRoomFromEngineLocked() != null }
        if (!hasLiveRoom && loadState.pendingRendererBattleRoomConfig != null) {
            return true
        }
        return runBattleRoomCommand("send battle room message") { _, networkEngine ->
            networkEngine.sendChatMessage(message)
            true
        }
    }

    open fun sendBattleRoomSystemMessage(message: String): Boolean {
        return message.isNotBlank() && runBattleRoomCommand("send battle room system message") { _, networkEngine ->
            if (!networkEngine.isServer) return@runBattleRoomCommand false
            networkEngine.j(message)
            true
        }
    }


    open fun sendBattleRoomQuickCommand(command: String): Boolean {
        return command.isNotBlank() && runBattleRoomCommand("send battle room quick command") { _, networkEngine ->
            networkEngine.k(command)
            true
        }
    }

    open fun isPreparingMap(mapPath: String? = null): Boolean {
        val state = loadState
        if (!state.asyncMapLoadInProgress) {
            return false
        }
        return mapPath == null || state.asyncMapLoadPath == mapPath
    }

    open fun mapLoadError(mapPath: String? = null): Throwable? {
        val state = loadState
        val error = state.asyncMapLoadError ?: return null
        return if (mapPath == null || state.asyncMapLoadPath == mapPath) error else null
    }

    open fun loadingStatus(): GameLoadingStatus {
        asyncEnginePreloadError?.let { error ->
            return GameLoadingStatus(
                text = "Loading failed: ${error.message ?: error.javaClass.simpleName}",
                progress = 1.0f,
            )
        }
        val state = loadState
        val engine = gameEngine ?: GameEngine.getInstance()
        val fallbackText = when {
            state.asyncMapLoadInProgress -> "Loading map data"
            else -> "Loading..."
        }
        val text = engine?.getLoadingText()?.takeIf { it.isNotBlank() } ?: fallbackText
        val progress = engine?.getLoadingProgress()?.coerceIn(0.0f, 1.0f)
            ?: when {
                state.asyncMapLoadInProgress -> 0.15f
                asyncEnginePreloadInProgress.get() -> 0.05f
                else -> null
            }
        return GameLoadingStatus(text = text, progress = progress)
    }

    open fun canResume(): Boolean {
        val state = loadState
        if (state.menuBackgroundActive || state.asyncMapLoadInProgress) return false
        // Never park the frame loop behind a long engine hold: no engine or a busy engine simply
        // means "nothing to resume right now".
        val engine = gameEngine ?: return false
        if (isEngineBusyForUiReads()) return false
        return synchronized(gameLock) {
            engine.hasLoadedLevel && !engine.isMenuBackgroundMap && activeRunningMapPath(engine) != null
        }
    }

    open fun runningMapPath(): String? = loadState.runningMapPath

    open fun currentMapDisplayName(): String? {
        if (gameEngine == null || isEngineBusyForUiReads()) return null
        return synchronized(gameLock) {
            activeEngineLocked()
                ?.getCurrentMapName()
                ?.takeIf { it.isNotBlank() }
        }
    }

    open fun pendingRendererMapPath(): String? {
        val state = loadState
        return state.pendingRendererBattleRoomConfig?.mapPath?.takeIf { it.isNotBlank() }
            ?: state.pendingMapPath?.takeIf { it.isNotBlank() }
    }

    open fun pendingRendererBattleRoomConfig(): BattleRoomLaunchConfig? =
        loadState.pendingRendererBattleRoomConfig

    open fun pendingMemorySnapshot(): GameMemorySnapshot? =
        loadState.pendingMemorySnapshot

    open fun activeRendererBattleRoomConfig(): BattleRoomLaunchConfig? {
        val state = loadState
        return state.activeRendererBattleRoomConfig ?: state.pendingRendererBattleRoomConfig
    }

    protected data class RendererPreparationSnapshot(
        val mapPath: String?,
        val memorySnapshot: GameMemorySnapshot?,
        val battleRoomConfig: BattleRoomLaunchConfig?,
    )

    protected fun rendererPreparationSnapshot(): RendererPreparationSnapshot {
        val state = loadState
        return RendererPreparationSnapshot(
            mapPath = state.pendingRendererBattleRoomConfig?.mapPath?.takeIf { it.isNotBlank() }
                ?: state.pendingMapPath?.takeIf { it.isNotBlank() },
            memorySnapshot = state.pendingMemorySnapshot,
            battleRoomConfig = state.pendingRendererBattleRoomConfig,
        )
    }

    protected fun beginRendererMapPreparation(
        mapPath: String,
        memorySnapshot: GameMemorySnapshot? = null,
        battleRoomConfig: BattleRoomLaunchConfig? = null,
    ) {
        require(memorySnapshot == null || memorySnapshot.mapPath == mapPath) {
            "Memory snapshot path does not match renderer map path"
        }
        require(battleRoomConfig == null || battleRoomConfig.mapPath == mapPath) {
            "Battle room path does not match renderer map path"
        }
        updateLoadState {
            it.copy(
                mapLoadGeneration = it.mapLoadGeneration + 1,
                pendingMapPath = mapPath,
                pendingMemorySnapshot = memorySnapshot,
                pendingRendererBattleRoomConfig = battleRoomConfig,
                activeRendererBattleRoomConfig = null,
                asyncMapLoadPath = mapPath,
                asyncMapLoadInProgress = true,
                asyncMapLoadError = null,
                runningMapPath = null,
                menuBackgroundActive = false,
            )
        }
    }

    protected fun beginRendererStandalonePreparation(requestPath: String) {
        updateLoadState {
            it.copy(
                mapLoadGeneration = it.mapLoadGeneration + 1,
                pendingMapPath = null,
                pendingMemorySnapshot = null,
                pendingRendererBattleRoomConfig = null,
                activeRendererBattleRoomConfig = null,
                asyncMapLoadPath = requestPath,
                asyncMapLoadInProgress = true,
                asyncMapLoadError = null,
                runningMapPath = null,
                menuBackgroundActive = false,
            )
        }
    }

    protected fun beginRendererStartedGamePreparation(mapPath: String) {
        updateLoadState {
            it.copy(
                pendingMapPath = null,
                pendingMemorySnapshot = null,
                pendingRendererBattleRoomConfig = null,
                asyncMapLoadPath = mapPath,
                asyncMapLoadInProgress = true,
                asyncMapLoadError = null,
                runningMapPath = null,
                menuBackgroundActive = false,
            )
        }
    }

    protected fun markRendererMenuBackgroundReady() {
        updateLoadState {
            it.copy(
                asyncMapLoadPath = null,
                asyncMapLoadInProgress = false,
                asyncMapLoadError = null,
                menuBackgroundActive = true,
            )
        }
    }

    protected fun markRendererMenuBackgroundError(requestPath: String, error: Throwable) {
        updateLoadState {
            it.copy(
                asyncMapLoadPath = requestPath,
                asyncMapLoadInProgress = false,
                asyncMapLoadError = error,
                menuBackgroundActive = false,
            )
        }
    }

    protected fun markRendererSurfaceStopped() {
        updateLoadState {
            it.copy(menuBackgroundActive = false)
        }
    }

    open fun markRendererMapReady(mapPath: String, viewport: KoolCanvasViewport) {
        lastViewport = viewport
        updateLoadState { current ->
            val promotesBattleRoomConfig = current.pendingRendererBattleRoomConfig?.mapPath == mapPath
            current.copy(
                pendingMapPath = current.pendingMapPath.takeUnless { it == mapPath },
                pendingMemorySnapshot = current.pendingMemorySnapshot?.takeUnless { it.mapPath == mapPath },
                pendingRendererBattleRoomConfig = if (promotesBattleRoomConfig) {
                    null
                } else {
                    current.pendingRendererBattleRoomConfig
                },
                activeRendererBattleRoomConfig = if (promotesBattleRoomConfig) {
                    current.pendingRendererBattleRoomConfig
                } else {
                    current.activeRendererBattleRoomConfig
                },
                asyncMapLoadPath = null,
                asyncMapLoadInProgress = false,
                asyncMapLoadError = null,
                menuBackgroundActive = false,
                runningMapPath = mapPath,
            )
        }
    }

    open fun markRendererMapError(mapPath: String, error: Throwable) {
        updateLoadState { current ->
            current.copy(
                pendingMapPath = current.pendingMapPath.takeUnless { it == mapPath },
                pendingRendererBattleRoomConfig = current.pendingRendererBattleRoomConfig
                    ?.takeUnless { it.mapPath == mapPath },
                asyncMapLoadPath = mapPath,
                asyncMapLoadInProgress = false,
                asyncMapLoadError = error,
            )
        }
    }

    open fun discardRunningGame() {
        publishedBattleRoom.set(null)
        publishedPlayerList.set(emptyList())
        publishedChatHistory.set(emptyList())
        updateLoadState {
            it.copy(
                mapLoadGeneration = it.mapLoadGeneration + 1,
                pendingMapPath = null,
                pendingMemorySnapshot = null,
                pendingRendererBattleRoomConfig = null,
                activeRendererBattleRoomConfig = null,
                asyncMapLoadPath = null,
                asyncMapLoadInProgress = false,
                asyncMapLoadError = null,
                runningMapPath = null,
                menuBackgroundActive = false,
            )
        }
        asyncEnginePreloadInProgress.set(false)
        asyncEnginePreloadError = null
        lastFrame = KoolCanvasFrame(lastViewport, emptyList())
        clearInputState()
        synchronized(gameLock) {
            gameEngine?.let { engine ->
                runCatching { engine.networkEngine?.disconnectNetworking("exited") }
                engine.hasLoadedLevel = false
                engine.isMenuBackgroundMap = false
                engine.isGameStarted = false
                engine.isStopped = true
                engine.isPaused = true
            }
        }
    }

    open fun requestSaveGame(name: String) {
        val saveName = name.toRwSaveName()
        postEngineCommand("save game") { engine ->
            runCatching {
                engine.gameSaver.saveGame(saveName, false)
                engine.gameUI?.showMediumPriorityMessage("Game saved")
            }.onFailure { error ->
                logger.error(error) { "Failed to save game: $saveName" }
                engine.gameUI?.showHighPriorityMessage("Save failed: ${error.message ?: error.javaClass.simpleName}")
            }
        }
    }

    open fun captureMemorySnapshot(): GameMemorySnapshot? =
        runEngineCommand("capture memory snapshot") { engine ->
            if (engine.isNetworkGameActive() || !engine.hasLoadedLevel) {
                return@runEngineCommand null
            }
            val mapPath = activeRunningMapPath(engine)?.takeIf { it.isNotBlank() }
                ?: return@runEngineCommand null
            val state = loadState
            val battleRoomConfig = state.activeRendererBattleRoomConfig
                ?.copy(mapPath = mapPath)
                ?: state.pendingRendererBattleRoomConfig?.copy(mapPath = mapPath)
            runCatching {
                val output = GameOutputStream()
                engine.gameSaver.writeSaveToStream(output)
                GameMemorySnapshot(mapPath, output.toByteArray(), battleRoomConfig)
            }.onFailure { error ->
                logger.error(error) { "Failed to capture in-memory RW save for $mapPath" }
                engine.gameUI?.showHighPriorityMessage("Map snapshot failed: ${error.message ?: error.javaClass.simpleName}")
            }.getOrNull()
        }

    open fun requestExportMap(name: String) {
        val exportName = name.toRwExportMapName()
        postEngineCommand("export map") { engine ->
            val sourceMap = engine.currentMapPath?.takeIf { it.isNotBlank() }
                ?: loadState.runningMapPath?.takeIf { it.isNotBlank() }
                ?: return@postEngineCommand
            val exportPath = "/SD/rustedWarfare/maps/$exportName.tmx"
            runCatching {
                engine.tileMap.exportMapToPath(sourceMap, exportPath)
                engine.gameUI?.showMediumPriorityMessage("Map exported: $exportName")
            }.onFailure { error ->
                logger.error(error) { "Failed to export map: $exportPath" }
                engine.gameUI?.showHighPriorityMessage("Export failed: ${error.message ?: error.javaClass.simpleName}")
            }
        }
    }

    open suspend fun requestReloadMods(): Boolean {
        // Flag first, lock second: readers check the flag before touching gameLock, so the frame
        // loop keeps rendering (and the reload dialog stays clickable) for the whole reload.
        modReloadInProgress = true
        try {
            synchronized(gameLock) {
                val engine = gameEngine ?: ensureStarted(lastViewport)
                runCatching {
                    engine.beginLoadingStatus("Loading custom unit data", 12)
                    engine.loadLevel("Loading custom unit data")
                    engine.modManager.saveModSelection()
                    engine.modManager.applyAndSaveMods()
                    engine.markLoadingStatusComplete("Mods reloaded")
                    engine.settingsEngine?.save()
                }.onFailure { error ->
                    logger.error(error) { "Failed to reload mods" }
                    engine.gameUI?.showHighPriorityMessage("Mod reload failed: ${error.message ?: error.javaClass.simpleName}")
                    throw error
                }
            }
        } finally {
            modReloadInProgress = false
        }
        return true
    }

    open fun requestSurrender() {
        postEngineCommand("surrender") { engine ->
            engine.networkEngine?.sendChatMessage("-surrender")
        }
    }

    open fun requestChatMessage(message: String, teamOnly: Boolean = false) {
        val trimmed = message.trim()
        if (trimmed.isBlank()) return
        postEngineCommand("chat message") { engine ->
            engine.networkEngine?.sendChatMessage(if (teamOnly) "-t $trimmed" else trimmed)
        }
    }

    open fun isNetworkMultiplayerActive(): Boolean {
        if (gameEngine == null || isEngineBusyForUiReads()) return false
        return synchronized(gameLock) {
            activeEngineLocked()?.isNetworkGameActive() == true
        }
    }

    open fun multiplayerPlayerList(): List<BattleRoomPlayerSnapshot> {
        if (isEngineBusyForUiReads()) return publishedPlayerList.get()
        return synchronized(gameLock) {
            val engine = activeEngineLocked() ?: return@synchronized emptyList()
            val networkEngine = engine.networkEngine ?: return@synchronized emptyList()
            if (!engine.isNetworkGameActive()) return@synchronized emptyList<BattleRoomPlayerSnapshot>()
            battleRoomPlayerTeams().map { team ->
                team.toBattleRoomSnapshotPlayer(
                    isLocal = team == networkEngine.localPlayerTeam || team == engine.playerTeam,
                )
            }
        }.also(publishedPlayerList::set)
    }

    open fun multiplayerChatHistory(): List<MultiplayerChatSnapshot> {
        if (isEngineBusyForUiReads()) return publishedChatHistory.get()
        return synchronized(gameLock) {
            val engine = activeEngineLocked() ?: return@synchronized emptyList()
            val networkEngine = engine.networkEngine ?: return@synchronized emptyList()
            if (!engine.isNetworkGameActive()) return@synchronized emptyList<MultiplayerChatSnapshot>()
            networkEngine.chatLog.b().mapNotNull { entry ->
                val message = entry as? ChatMessage ?: return@mapNotNull null
                MultiplayerChatSnapshot(
                    text = message.displayText,
                    teamColorIndex = message.teamColorIndex,
                )
            }
        }.also(publishedChatHistory::set)
    }

    open fun runningMultiplayerExitInfo(): RunningMultiplayerExitInfo? {
        if (gameEngine == null || isEngineBusyForUiReads()) return null
        return synchronized(gameLock) {
            val engine = activeEngineLocked() ?: return@synchronized null
            val networkEngine = engine.networkEngine ?: return@synchronized null
            if (!engine.isNetworkGameActive() || !networkEngine.gameHasBeenStarted) return@synchronized null
            RunningMultiplayerExitInfo(isHost = networkEngine.isServer)
        }
    }

    open fun disconnectRunningMultiplayer() {
        postEngineCommand("disconnect multiplayer") { engine ->
            engine.networkEngine?.disconnectNetworking("exited")
        }
    }

    open fun scheduleReturnToBattleRoom(): Boolean =
        runEngineCommand("schedule return to battle room") { engine ->
            val networkEngine = engine.networkEngine ?: return@runEngineCommand false
            if (!networkEngine.networkGameActive || !networkEngine.gameHasBeenStarted || !networkEngine.isServer) {
                return@runEngineCommand false
            }
            networkEngine.scheduleDefaultReturnToBattleroom()
            engine.gameUI?.isDraggingSelection = false
            true
        } ?: false

    open fun currentMultiplayerPlayerName(): String {
        if (isEngineBusyForUiReads()) {
            return SettingsEngine.getInstance()?.lastNetworkPlayerName ?: "Player"
        }
        return synchronized(gameLock) {
            val engine = activeEngineLocked()
            engine?.networkEngine?.playerName
                ?: engine?.settingsEngine?.lastNetworkPlayerName
                ?: SettingsEngine.getInstance()?.lastNetworkPlayerName
                ?: "Player"
        }
    }

    open fun updateMultiplayerPlayerName(name: String): Boolean {
        val normalizedName = name.trim().replace(' ', '_')
        if (normalizedName.isBlank()) return false
        runEngineCommand("update multiplayer player name") { engine ->
            engine.networkEngine?.playerName = normalizedName
            val settings = engine.settingsEngine ?: SettingsEngine.getInstance() ?: return@runEngineCommand false
            settings.lastNetworkPlayerName = normalizedName
            settings.save()
            true
        }?.let { return it }
        // No engine yet: persist to settings alone, like before.
        val settings = SettingsEngine.getInstance() ?: return false
        settings.lastNetworkPlayerName = normalizedName
        settings.save()
        return true
    }

    open fun injectTransferredUnits(units: List<TransferredUnit>, targetPortalId: String?): Int {
        if (units.isEmpty()) return 0
        return runEngineCommand("inject transferred units") { engine ->
            if (!engine.hasLoadedLevel) return@runEngineCommand 0
            val center = engine.missionEngine?.getMapPortalCenter(targetPortalId)
            val spawnX = center?.getOrNull(0) ?: (engine.viewpointX + engine.halfVisibleWorldWidth)
            val spawnY = center?.getOrNull(1) ?: (engine.viewpointY + engine.halfVisibleWorldHeight)
            var createdCount = 0
            units.forEachIndexed { index, transfer ->
                val unitType = runCatching { UnitTypeEnum.valueOf(transfer.unitTypeId) }.getOrNull()
                    ?: return@forEachIndexed
                val team = PlayerTeam.k(transfer.teamId) ?: return@forEachIndexed
                val unit = unitType.a()
                unit.posX = spawnX + ((index % 4) - 1.5f) * 28.0f
                unit.posY = spawnY + ((index / 4) - 0.5f) * 28.0f
                unit.h(transfer.direction)
                unit.setUnitTeam(team)
                unit.currentHealth = (unit.maxHealth * transfer.healthFraction.coerceIn(0.01f, 1.0f))
                    .coerceIn(1.0f, unit.maxHealth)
                unit.isActive = true
                unit.n()
                PlayerTeam.c(unit)
                GameObject.dL()
                engine.unitSpatialIndex?.a(unit)
                createdCount += 1
            }
            if (createdCount > 0) {
                engine.gameUI?.showMediumPriorityMessage("Transferred $createdCount unit(s)")
            }
            createdCount
        } ?: 0
    }

    open fun isMapLoaded(mapPath: String? = runningMapPath()): Boolean {
        if (loadState.asyncMapLoadInProgress) {
            return false
        }
        // Fast, lock-free exits for the per-frame callers: no engine or an engine owned by a
        // long-running worker cannot have the requested map ready.
        val engine = gameEngine ?: return false
        if (isEngineBusyForUiReads()) return false
        synchronized(gameLock) {
            val activeMapPath = activeRunningMapPath(engine)
            if (mapPath != null && activeMapPath != mapPath) {
                return false
            }
            return engine.hasLoadedLevel
        }
    }

    open fun isReadyForDisplay(mapPath: String? = runningMapPath()): Boolean {
        if (!isMapLoaded(mapPath)) {
            return false
        }
        return lastFrame.commands.isNotEmpty() || !TileMap.layerBufferManager.hasVisiblePendingRedraws()
    }

    abstract fun loadPendingMapNow(): KoolCanvasFrame

    protected abstract fun ensureStarted(viewport: KoolCanvasViewport): GameEngine

    protected abstract fun applyViewport(engine: GameEngine, viewport: KoolCanvasViewport)

    protected open fun clearInputState() = Unit

    protected open fun activeRunningMapPath(engine: GameEngine): String? =
        loadState.runningMapPath
            ?: engine.networkEngine?.selectedMapPath?.takeIf { it.isNotBlank() }
            ?: engine.currentMapPath?.takeIf { it.isNotBlank() }

    protected fun activeEngineLocked(): GameEngine? {
        val sessionEngine = gameEngine
        val singletonEngine = GameEngine.getInstance()
        return when {
            sessionEngine == null -> singletonEngine?.also { gameEngine = it }
            singletonEngine == null || singletonEngine === sessionEngine -> sessionEngine
            else -> {
                logger.warn {
                    "GameSession engine reference differed from GameEngine singleton; adopting singleton"
                }
                gameEngine = singletonEngine
                singletonEngine
            }
        }
    }

    protected fun ensureSessionEngineLocked(createEngine: () -> GameEngine): GameEngine {
        activeEngineLocked()?.let { return it }
        val created = createEngine()
        val singletonEngine = GameEngine.getInstance()
        check(singletonEngine === created) {
            "GameEngine.createGameEngine returned a non-singleton engine"
        }
        gameEngine = created
        return created
    }

    private fun ensureStartedOutsideGameLock(viewport: KoolCanvasViewport): GameEngine =
        synchronized(engineStartLock) engineStart@{
            synchronized(gameLock) {
                activeEngineLocked()
            }?.let { return@engineStart it }
            ensureStarted(viewport)
        }

    protected fun preloadEngineInBackground(requestedViewport: KoolCanvasViewport) {
        val startedAt = System.nanoTime()
        runCatching {
            val viewport = requestedViewport.takeIf { it.width > 0 && it.height > 0 } ?: defaultPreloadViewport
            lastViewport = viewport
            val engine = ensureStartedOutsideGameLock(viewport)
            synchronized(gameLock) {
                applyViewport(engine, viewport)
            }
            logger.info { "Prepared $sessionLogName engine asynchronously: ${elapsedMs(startedAt)}ms" }
        }.onFailure { error ->
            asyncEnginePreloadError = error
            logger.error(error) { "$sessionLogName async engine preload failed" }
        }.also {
            asyncEnginePreloadInProgress.set(false)
        }
    }

    private fun runBattleRoomCommand(
        label: String,
        command: (GameEngine, NetworkEngine) -> Boolean,
    ): Boolean =
        runCatching {
            val viewport = lastViewport.takeIf { it.width > 0 && it.height > 0 } ?: defaultPreloadViewport
            ensureStartedOutsideGameLock(viewport)
            runEngineCommand(label) { engine ->
                val networkEngine = engine.networkEngine ?: return@runEngineCommand false
                prepareActiveBattleRoomEngine(engine)
                command(engine, networkEngine)
            } ?: false
        }.onFailure { error ->
            logger.warn(error) { "Unable to $label" }
        }.getOrDefault(false)

    private fun currentBattleRoomFromEngineLocked(refreshNetworkStatus: Boolean = true): BattleRoomSnapshot? {
        val engine = gameEngine ?: GameEngine.getInstance() ?: return null
        val networkEngine = engine.networkEngine ?: return null
        if (!networkEngine.networkGameActive && !networkEngine.isServer && !networkEngine.isProxyController) return null
        val settings = networkEngine.roomSettings
        val players = battleRoomPlayerTeams().map { team ->
            team.toBattleRoomSnapshotPlayer(
                isLocal = team == networkEngine.localPlayerTeam || team == engine.playerTeam,
            )
        }
        return BattleRoomSnapshot(
            mapPath = networkEngine.selectedMapPath ?: engine.currentMapPath,
            mapDisplayName = settings.mapPath?.let(MapMetadata::getMapName) ?: "Unknown map",
            mapTypeLabel = settings.gameModeType?.a() ?: "Multiplayer",
            options = settings.toBattleRoomOptions(),
            players = players,
            isHost = networkEngine.isServer || networkEngine.isProxyController,
            isNetworkMultiplayer = engine.isNetworkGameActive(),
            savedGame = settings.gameModeType == GameModeType.savedGame,
            rwxP2PSession = networkEngine.p2pSession,
            requiredModsSummary = if (refreshNetworkStatus || cachedBattleRoomRequiredModsSummary == null) {
                runCatching { networkEngine.requiredModsSummary }.getOrNull()
                    .also { cachedBattleRoomRequiredModsSummary = it }
            } else {
                cachedBattleRoomRequiredModsSummary
            },
            networkStatusText = if (refreshNetworkStatus || cachedBattleRoomNetworkStatusText == null) {
                runCatching { networkEngine.getPublicIpStatusText() }.getOrNull()
                    .also { cachedBattleRoomNetworkStatusText = it }
            } else {
                cachedBattleRoomNetworkStatusText
            },
            maxPlayers = PlayerTeam.TEAM_NEUTRAL,
        )
    }

    private fun BattleRoomLaunchConfig.toBattleRoomSnapshot(): BattleRoomSnapshot {
        val players = pendingBattleRoomPlayers()
        return BattleRoomSnapshot(
            mapPath = mapPath,
            mapDisplayName = MapMetadata.getMapName(mapPath),
            mapTypeLabel = when {
                sandbox -> "Sandbox"
                savedGame -> "Saved Game"
                else -> "Skirmish"
            },
            options = options,
            players = players,
            isHost = true,
            savedGame = savedGame,
        )
    }

    private fun BattleRoomLaunchConfig.pendingBattleRoomPlayers(): List<BattleRoomPlayerSnapshot> {
        val totalPlayers = 1 + aiPlayerCount.coerceAtLeast(0)
        return (0 until totalPlayers).map { index ->
            val isLocal = index == 0
            val teamIndex = when (teamLayout) {
                BattleRoomTeamLayout.TwoSides -> index % 2
                BattleRoomTeamLayout.ThreeSides -> index % 3
                BattleRoomTeamLayout.Ffa,
                null -> index

                BattleRoomTeamLayout.Spectators -> -1
                BattleRoomTeamLayout.AllVsAi,
                BattleRoomTeamLayout.AllVs2 -> if (index == 0) 0 else 1

                BattleRoomTeamLayout.Random -> index % 2
            }
            val isSpectator = teamIndex < 0
            BattleRoomPlayerSnapshot(
                id = if (isLocal) DRAFT_LOCAL_PLAYER_ID else "$DRAFT_AI_PLAYER_ID_PREFIX$index",
                name = if (isLocal) {
                    "Player"
                } else {
                    battleRoomPlayerDisplayName("AI", isAi = true, slotIndex = index)
                },
                spawnLabel = if (isSpectator) "Spec" else (index + 1).toString(),
                teamLabel = if (isSpectator) "-" else teamLabelFor(teamIndex),
                nameColorIndex = teamIndex,
                spawnColorIndex = if (isSpectator) -1 else index,
                teamColorIndex = teamIndex,
                isSpectator = isSpectator,
                isAI = !isLocal,
                isLocal = isLocal,
            )
        }
    }

    private fun prepareActiveBattleRoomEngine(engine: GameEngine) {
        updateLoadState {
            it.copy(
                menuBackgroundActive = false,
                pendingRendererBattleRoomConfig = null,
                pendingMapPath = null,
            )
        }
        engine.isStopped = false
        engine.isPaused = false
        installCoreNetworkCallbacksIfMissing(engine)
    }

    private fun installCoreNetworkCallbacksIfMissing(engine: GameEngine) {
        val networkEngine = engine.networkEngine ?: return
        if (networkEngine.callbacks !is CoreUiNetworkCallbacks) {
            networkEngine.callbacks = CoreUiNetworkCallbacks()
        }
    }

    private fun initBattleRoomMap(
        engine: GameEngine,
        mapPath: String?,
        force: Boolean,
        savedGame: Boolean = false,
    ) {
        val networkEngine = engine.networkEngine ?: return
        val resolvedPath = mapPath?.takeIf { it.isNotBlank() }
            ?: networkEngine.selectedMapPath?.takeIf { it.isNotBlank() }
            ?: "maps/skirmish/[z;p10]Crossing Large (10p).tmx"
        engine.currentMapPath = resolvedPath
        networkEngine.selectedMapPath = resolvedPath
        val settings = networkEngine.roomSettings
        if (settings.gameModeType == null || force) {
            settings.gameModeType = if (savedGame) GameModeType.savedGame else engine.getGameModeType()
        }
        if (settings.mapPath == null || force) {
            settings.mapPath = if (savedGame) resolvedPath else engine.currentMapFilename
        }
        if (networkEngine.playerName == null) {
            networkEngine.playerName = engine.settingsEngine.lastNetworkPlayerName ?: "Player"
        }
    }

    private fun resolveBattleRoomPlayer(
        playerId: String,
        engine: GameEngine,
        networkEngine: NetworkEngine,
    ): PlayerTeam? {
        val teams = battleRoomPlayerTeams()
        teams.firstOrNull { it.teamId.toString() == playerId }?.let { return it }
        if (playerId == DRAFT_LOCAL_PLAYER_ID) {
            return networkEngine.localPlayerTeam ?: engine.playerTeam
        }
        val draftIndex = draftAiPlayerIndex(playerId) ?: return null
        return teams.firstOrNull { it.teamId == draftIndex }
            ?: teams.getOrNull(draftIndex)
    }

    private fun PlayerTeam.toBattleRoomSnapshotPlayer(isLocal: Boolean): BattleRoomPlayerSnapshot {
        val isSpectator = isSpectatorTeamColor()
        val isAi = isTeamSpectator || isTeamControlledByAI
        val teamIndex = teamColorId.takeIf { it >= 0 } ?: -1
        return BattleRoomPlayerSnapshot(
            id = teamId.toString(),
            name = battleRoomPlayerDisplayName(teamName, isAi, teamId),
            spawnLabel = if (isSpectator) "Spec" else (teamId + 1).toString(),
            teamLabel = if (isSpectator) "-" else teamLabelFor(teamColorId),
            pingLabel = battleRoomPlayerPingLabel(this),
            nameColorIndex = getTeamColorIndex(),
            spawnColorIndex = teamId.takeIf { it >= 0 } ?: -1,
            teamColorIndex = teamIndex,
            isSpectator = isSpectator,
            isReady = isTeamReady,
            isAI = isAi,
            isLocal = isLocal,
        )
    }

    /**
     * Loads a level on the calling thread while holding [gameLock], then publishes its first frame.
     *
     * [generation] is the load's ticket: a newer request bumps [mapLoadGeneration], so a load that
     * finds its generation stale is discarded instead of overwriting the current frame or reporting
     * an error for a request nobody is waiting on any more.
     *
     * @param kind noun used in log lines, e.g. "map", "replay", "memory snapshot".
     * @param requestKey what was asked for, logged alongside [kind].
     */
    private fun loadLevelInBackground(
        kind: String,
        requestKey: String,
        requestedViewport: KoolCanvasViewport,
        generation: Long,
        load: (GameEngine) -> Unit,
    ) {
        var loadMs = 0L
        var firstFrameMs = 0L
        val startedAt = System.nanoTime()
        var loadedCurrentRequest = false
        runCatching {
            synchronized(gameLock) {
                if (generation != loadState.mapLoadGeneration) {
                    return@synchronized
                }
                val viewport = requestedViewport.takeIf { it.width > 0 && it.height > 0 } ?: defaultPreloadViewport
                lastViewport = viewport
                val engine = ensureStarted(viewport)
                applyViewport(engine, viewport)
                val loadStartedAt = System.nanoTime()
                load(engine)
                loadMs = elapsedMs(loadStartedAt)
                val firstFrameStartedAt = System.nanoTime()
                val preparedFrame = prepareFrameAfterBackgroundLoad(engine, viewport)
                firstFrameMs = elapsedMs(firstFrameStartedAt)
                if (generation == loadState.mapLoadGeneration) {
                    lastFrame = preparedFrame
                    loadedCurrentRequest = true
                }
            }
            if (!loadedCurrentRequest) {
                logger.info { "Discarded stale $sessionLogName $kind preparation: $requestKey" }
                return@runCatching
            }
            logger.info {
                "Prepared $sessionLogName $kind asynchronously: $requestKey " +
                        "(total=${elapsedMs(startedAt)}ms, load=${loadMs}ms, firstFrame=${firstFrameMs}ms)"
            }
        }.onFailure { error ->
            if (loadState.mapLoadGeneration == generation) {
                updateLoadState { if (it.mapLoadGeneration == generation) it.copy(asyncMapLoadError = error) else it }
                logger.error(error) { "$sessionLogName async $kind load failed: $requestKey" }
            } else {
                logger.info { "Ignored stale $sessionLogName $kind load failure for: $requestKey" }
            }
        }.also {
            updateLoadState { if (it.mapLoadGeneration == generation) it.copy(asyncMapLoadInProgress = false) else it }
        }
    }

    private fun loadMapInBackground(mapPath: String, requestedViewport: KoolCanvasViewport, generation: Long) =
        loadLevelInBackground("map", mapPath, requestedViewport, generation) { engine ->
            loadMap(engine, mapPath)
        }

    private fun loadReplayInBackground(replayName: String, requestedViewport: KoolCanvasViewport, generation: Long) =
        loadLevelInBackground("replay", replayName, requestedViewport, generation) { engine ->
            loadReplay(engine, replayName)
        }

    private fun loadMemorySnapshotInBackground(
        snapshot: GameMemorySnapshot,
        requestedViewport: KoolCanvasViewport,
        generation: Long,
    ) = loadLevelInBackground("memory snapshot", snapshot.mapPath, requestedViewport, generation) { engine ->
        loadMemorySnapshot(engine, snapshot)
    }

    protected open fun prepareFrameAfterBackgroundLoad(
        engine: GameEngine,
        viewport: KoolCanvasViewport,
    ): KoolCanvasFrame = KoolCanvasFrame(viewport, emptyList())

    protected fun loadPendingMap(engine: GameEngine) {
        val staged = loadState
        val mapPath = staged.pendingMapPath ?: return
        val snapshot = staged.pendingMemorySnapshot?.takeIf { it.mapPath == mapPath }
        val battleRoomConfig = staged.pendingRendererBattleRoomConfig?.takeIf { it.mapPath == mapPath }
        // Consume only the values read above: a request staged concurrently by another thread must
        // survive this clear instead of being wiped along with the one being handled.
        updateLoadState { current ->
            current.copy(
                pendingMapPath = current.pendingMapPath.takeUnless { it == mapPath },
                pendingMemorySnapshot = current.pendingMemorySnapshot
                    ?.takeUnless { it === staged.pendingMemorySnapshot },
                pendingRendererBattleRoomConfig = if (staged.runningMapPath == mapPath && engine.hasLoadedLevel) {
                    current.pendingRendererBattleRoomConfig?.takeUnless { it === staged.pendingRendererBattleRoomConfig }
                } else {
                    current.pendingRendererBattleRoomConfig
                },
            )
        }
        if (staged.runningMapPath == mapPath && engine.hasLoadedLevel) {
            return
        }
        runCatching {
            if (snapshot != null) {
                loadMemorySnapshot(engine, snapshot)
            } else if (battleRoomConfig != null) {
                loadBattleRoomMap(engine, battleRoomConfig)
            } else {
                loadMap(engine, mapPath)
            }
        }.onFailure { error ->
            // A successful load clears the draft itself. On failure it must be dropped here too:
            // currentBattleRoom() reads the draft before the engine, so a leaked one would keep
            // showing a room that was never created.
            if (battleRoomConfig != null) {
                updateLoadState { current ->
                    current.copy(
                        pendingRendererBattleRoomConfig = current.pendingRendererBattleRoomConfig
                            ?.takeUnless { it == battleRoomConfig },
                    )
                }
            }
            logger.error(error) { "$sessionLogName map load failed: $mapPath" }
        }
    }

    protected open fun loadMap(engine: GameEngine, mapPath: String) {
        updateLoadState {
            it.copy(
                menuBackgroundActive = false,
                activeRendererBattleRoomConfig = null,
                runningMapPath = mapPath,
            )
        }
        engine.isStopped = false
        engine.isPaused = false
        val saveName = savedGameNameFromRequestPath(mapPath)
        if (saveName != null) {
            check(engine.gameSaver.loadSaveFile(saveName, true)) {
                "Unable to load saved game: $saveName"
            }
        } else {
            engine.currentMapPath = mapPath
            engine.loadGame(true, GameMode.normal)
        }
    }

    protected open fun loadBattleRoomMap(engine: GameEngine, config: BattleRoomLaunchConfig) {
        updateLoadState {
            it.copy(
                menuBackgroundActive = false,
                runningMapPath = config.mapPath,
            )
        }
        engine.currentMapPath = config.mapPath
        engine.isStopped = false
        engine.isPaused = false
        engine.minimap?.release()
        val networkEngine = engine.configureLocalBattleRoom(config, sessionLogName) ?: run {
            loadMap(engine, if (config.savedGame) savedGameRequestPath(config.mapPath) else config.mapPath)
            return
        }

        check(networkEngine.startBattleRoomGame()) { "Unable to start $sessionLogName battle room game" }
        BattleRoomUiBridge.setupGame()
        val resolvedMapPath = activeRunningMapPath(engine) ?: config.mapPath
        updateLoadState {
            it.copy(
                activeRendererBattleRoomConfig = config,
                pendingRendererBattleRoomConfig = null,
                runningMapPath = resolvedMapPath,
            )
        }
    }

    protected open fun loadMemorySnapshot(engine: GameEngine, snapshot: GameMemorySnapshot) {
        updateLoadState {
            it.copy(
                menuBackgroundActive = false,
                runningMapPath = snapshot.mapPath,
                pendingRendererBattleRoomConfig = null,
                activeRendererBattleRoomConfig = snapshot.battleRoomConfig,
            )
        }
        engine.isStopped = false
        engine.isPaused = false
        check(engine.gameSaver.readSaveFromStream(GameInputStream(snapshot.saveBytes), false, false, false)) {
            "Unable to restore in-memory save: ${snapshot.mapPath}"
        }
        val resolvedMapPath = activeRunningMapPath(engine) ?: snapshot.mapPath
        updateLoadState {
            it.copy(
                runningMapPath = resolvedMapPath,
                activeRendererBattleRoomConfig = it.activeRendererBattleRoomConfig?.copy(mapPath = resolvedMapPath),
            )
        }
    }

    protected open fun loadReplay(engine: GameEngine, replayName: String) {
        updateLoadState {
            it.copy(
                menuBackgroundActive = false,
                runningMapPath = replayName,
            )
        }
        engine.isGameStarted = false
        engine.isStopped = false
        engine.isPaused = false
        check(engine.replayEngine.loadReplay(replayName)) { "Unable to load replay: $replayName" }
    }

    protected fun elapsedMs(startedAt: Long): Long =
        (System.nanoTime() - startedAt) / 1_000_000L

    protected fun Float.toGameSpeedDelta(): Float =
        (this * 60f).coerceIn(0f, 3f)
}

// Engine team-slot bounds: PlayerTeam.setMaxTeamId rejects < 10, and TEAM_ALLIES is the hard cap.
private const val BATTLE_ROOM_MIN_MAX_PLAYERS: Int = 10
private const val BATTLE_ROOM_MAX_MAX_PLAYERS: Int = 100
