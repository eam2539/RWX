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
    protected var pendingMapPath: String? = null
    protected var pendingMemorySnapshot: GameMemorySnapshot? = null
    protected var pendingRendererBattleRoomConfig: BattleRoomLaunchConfig? = null
    protected var activeRendererBattleRoomConfig: BattleRoomLaunchConfig? = null
    protected var runningMapPath: String? = null
    protected var lastViewport: KoolCanvasViewport = KoolCanvasViewport(0, 0)

    @Volatile
    protected var lastFrame: KoolCanvasFrame = KoolCanvasFrame(lastViewport, emptyList())

    @Volatile
    protected var menuBackgroundActive: Boolean = false

    @Volatile
    protected var asyncEnginePreloadInProgress: Boolean = false

    @Volatile
    protected var asyncEnginePreloadError: Throwable? = null

    @Volatile
    protected var asyncMapLoadPath: String? = null

    @Volatile
    protected var asyncMapLoadInProgress: Boolean = false

    @Volatile
    protected var asyncMapLoadError: Throwable? = null

    @Volatile
    protected var mapLoadGeneration: Long = 0L

    @Volatile
    private var activeBattleRoomJoinConnector: SocketConnector? = null

    @Volatile
    private var cachedBattleRoomRequiredModsSummary: String? = null

    @Volatile
    private var cachedBattleRoomNetworkStatusText: String? = null

    @Volatile
    var latestBattleRoomJoinError: String? = null

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
        synchronized(gameLock) {
            gameEngine?.setKeyState(androidKeyCode, isDown)
        }
    }

    open fun submitMouseWheel(amount: Int) {
        if (amount == 0) return
        synchronized(gameLock) {
            gameEngine?.queueMouseWheelDelta(amount)
        }
    }

    open fun setInGameMenuCallbacks(callbacks: InGameMenuCallbacks?) {
        inGameMenuController.setCallbacks(callbacks)
    }

    open fun preload(viewport: KoolCanvasViewport): GameEngine {
        synchronized(gameLock) {
            lastViewport = viewport
        }
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
            asyncEnginePreloadInProgress = false
            asyncEnginePreloadError = null
            return engine
        }
    }

    open fun prepareEngineAsync(viewport: KoolCanvasViewport) {
        synchronized(gameLock) {
            if (gameEngine != null || asyncEnginePreloadInProgress) {
                return
            }
            asyncEnginePreloadInProgress = true
            asyncEnginePreloadError = null
            lastViewport = viewport
        }
        logger.info { "Preparing $sessionLogName engine asynchronously" }
        launchOnIO("${rendererMode.id}-engine-preloader") {
            preloadEngineInBackground(viewport)
        }
    }

    open fun isPreparingEngine(): Boolean = asyncEnginePreloadInProgress

    open fun requestMap(mapPath: String?) {
        synchronized(gameLock) {
            menuBackgroundActive = false
            pendingRendererBattleRoomConfig = null
            activeRendererBattleRoomConfig = null
            pendingMemorySnapshot = null
            pendingMapPath = mapPath?.takeIf { it.isNotBlank() }
        }
    }

    open fun requestMemorySnapshot(snapshot: GameMemorySnapshot) {
        synchronized(gameLock) {
            menuBackgroundActive = false
            pendingRendererBattleRoomConfig = snapshot.battleRoomConfig
            pendingMemorySnapshot = snapshot
            pendingMapPath = snapshot.mapPath
        }
    }

    open fun prepareMapAsync(mapPath: String?, viewport: KoolCanvasViewport) {
        val requestedMapPath = mapPath?.takeIf { it.isNotBlank() } ?: return
        if (isMapLoaded(requestedMapPath)) {
            return
        }
        val generation = synchronized(gameLock) {
            val ticket = beginAsyncLoadLocked(
                requestKey = requestedMapPath,
                viewport = viewport,
                resetLastFrame = runningMapPath != requestedMapPath,
            ) ?: return
            pendingRendererBattleRoomConfig = null
            activeRendererBattleRoomConfig = null
            ticket
        }
        logger.info { "Preparing $sessionLogName map asynchronously: $requestedMapPath" }
        launchOnIO("${rendererMode.id}-map-loader") {
            loadMapInBackground(requestedMapPath, viewport, generation)
        }
    }

    open fun prepareMemorySnapshotAsync(snapshot: GameMemorySnapshot, viewport: KoolCanvasViewport) {
        if (isMapLoaded(snapshot.mapPath)) {
            return
        }
        val generation = synchronized(gameLock) {
            val ticket = beginAsyncLoadLocked(
                requestKey = snapshot.mapPath,
                viewport = viewport,
                resetLastFrame = true,
            ) ?: return
            pendingMapPath = snapshot.mapPath
            pendingMemorySnapshot = snapshot
            pendingRendererBattleRoomConfig = snapshot.battleRoomConfig
            ticket
        }
        logger.info { "Preparing $sessionLogName memory snapshot asynchronously: ${snapshot.mapPath}" }
        launchOnIO("${rendererMode.id}-memory-snapshot-loader") {
            loadMemorySnapshotInBackground(snapshot, viewport, generation)
        }
    }

    open fun prepareReplayAsync(replayName: String, viewport: KoolCanvasViewport) {
        val requestedReplayName = replayName.takeIf { it.isNotBlank() } ?: return
        val generation = synchronized(gameLock) {
            beginAsyncLoadLocked(
                requestKey = requestedReplayName,
                viewport = viewport,
                resetLastFrame = true,
            ) ?: return
        }
        logger.info { "Preparing $sessionLogName replay asynchronously: $requestedReplayName" }
        launchOnIO("${rendererMode.id}-replay-loader") {
            loadReplayInBackground(requestedReplayName, viewport, generation)
        }
    }

    open fun prepareBattleRoomAsync(config: BattleRoomLaunchConfig, viewport: KoolCanvasViewport) {
        val requestedMapPath = config.mapPath.takeIf { it.isNotBlank() } ?: return
        if (isMapLoaded(requestedMapPath) && activeRendererBattleRoomConfig == config) {
            return
        }
        val generation = synchronized(gameLock) {
            val ticket = beginAsyncLoadLocked(
                requestKey = requestedMapPath,
                viewport = viewport,
                resetLastFrame = runningMapPath != requestedMapPath,
            ) ?: return
            pendingMapPath = requestedMapPath
            pendingMemorySnapshot = null
            pendingRendererBattleRoomConfig = config
            ticket
        }
        logger.info { "Preparing $sessionLogName battle room map asynchronously: $requestedMapPath" }
        launchOnIO("${rendererMode.id}-battleroom-map-loader") {
            loadMapInBackground(requestedMapPath, viewport, generation)
        }
    }

    /**
     * Registers a new asynchronous load and returns its [mapLoadGeneration] ticket, or null when an
     * identical request is already in flight. Must be called while holding [gameLock].
     *
     * The ticket is what makes a superseded load harmless: the loader compares it against the
     * current generation before it publishes a frame or records an error.
     */
    private fun beginAsyncLoadLocked(
        requestKey: String,
        viewport: KoolCanvasViewport,
        resetLastFrame: Boolean,
    ): Long? {
        if (asyncMapLoadInProgress && asyncMapLoadPath == requestKey) {
            return null
        }
        mapLoadGeneration += 1
        if (resetLastFrame) {
            lastFrame = KoolCanvasFrame(viewport, emptyList())
        }
        asyncMapLoadPath = requestKey
        asyncMapLoadInProgress = true
        asyncMapLoadError = null
        menuBackgroundActive = false
        return mapLoadGeneration
    }

    open fun adoptStartedGameFromEngine(viewport: KoolCanvasViewport): Boolean = false

    /**
     * Queues [config] as the next level to load, launching straight into the game.
     *
     * The room is only a staging area here: the very next frame loads it. Use
     * [openLocalBattleRoomDraft] instead when the host should stay in the room and keep editing it.
     */
    open fun prepareLocalBattleRoom(config: BattleRoomLaunchConfig): Boolean {
        synchronized(gameLock) {
            disconnectUnstartedNetworkingLocked()
            pendingMapPath = config.mapPath.takeIf { it.isNotBlank() }
            pendingRendererBattleRoomConfig = config
            lastFrame = KoolCanvasFrame(lastViewport, emptyList())
        }
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
        synchronized(gameLock) {
            disconnectUnstartedNetworkingLocked()
            pendingRendererBattleRoomConfig = config
            lastFrame = KoolCanvasFrame(lastViewport, emptyList())
        }
        return true
    }

    /** Drops a network room that was opened but never started. Must hold [gameLock]. */
    private fun disconnectUnstartedNetworkingLocked() {
        menuBackgroundActive = false
        gameEngine?.networkEngine?.takeIf { it.networkGameActive && !it.gameHasBeenStarted }
            ?.disconnectNetworking("starting local battleroom")
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
            activeRendererBattleRoomConfig = config
            BattleRoomUiBridge.updateUI()
            true
        }

    /** True when a live engine-backed battle room is open (host or client), started or not. */
    open fun isBattleRoomLive(): Boolean =
        synchronized(gameLock) { currentBattleRoomFromEngineLocked() != null }

    open fun prepareMenuBackgroundAsync(viewport: KoolCanvasViewport) = Unit

    open fun isMenuBackgroundActive(): Boolean = menuBackgroundActive

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
            networkEngine.rwxP2PSession = config.rwxP2PSession
            true
        }

    open fun joinBattleRoom(address: String, serverId: String?, rwxP2PSession: Boolean): Boolean {
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
                        networkEngine.rwxP2PSession = rwxP2PSession
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
        synchronized(gameLock) {
            activeBattleRoomJoinConnector?.a()
            activeBattleRoomJoinConnector = null
            latestBattleRoomJoinError = null
            pendingRendererBattleRoomConfig = null
            pendingMapPath = null
            val engine = gameEngine ?: GameEngine.getInstance() ?: return
            engine.networkEngine?.takeIf { it.networkGameActive && !it.gameHasBeenStarted }
                ?.disconnectNetworking("left battleroom")
        }
    }

    open fun currentBattleRoom(refreshNetworkStatus: Boolean = true): BattleRoomSnapshot? {
        synchronized(gameLock) {
            pendingRendererBattleRoomConfig?.toBattleRoomSnapshot()?.let { return it }
            return currentBattleRoomFromEngineLocked(refreshNetworkStatus)
        }
    }

    /**
     * Applies a battle room change to whichever room is real right now.
     *
     * Until the host presses "Start game" the room exists only as [pendingRendererBattleRoomConfig]:
     * a local draft with no engine or network state, so an edit rewrites that config. Once the engine
     * owns a live room the same edit becomes a network command instead.
     *
     * @param editDraft the replacement draft, or null to reject the change.
     */
    private inline fun editBattleRoom(
        label: String,
        editDraft: (BattleRoomLaunchConfig) -> BattleRoomLaunchConfig?,
        noinline commandLiveRoom: (GameEngine, NetworkEngine) -> Boolean,
    ): Boolean {
        synchronized(gameLock) {
            if (currentBattleRoomFromEngineLocked() == null) {
                val draft = pendingRendererBattleRoomConfig ?: return false
                pendingRendererBattleRoomConfig = editDraft(draft) ?: return false
                return true
            }
        }
        return runBattleRoomCommand(label, commandLiveRoom)
    }

    open fun setBattleRoomMap(mapPath: String, savedGame: Boolean = false): Boolean {
        val requestedMapPath = mapPath.takeIf { it.isNotBlank() } ?: return false
        return editBattleRoom(
            "set battle room map",
            editDraft = { draft ->
                pendingMapPath = requestedMapPath
                draft.copy(mapPath = requestedMapPath, savedGame = savedGame)
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
            editDraft = { draft -> draft.copy(aiPlayerCount = draft.aiPlayerCount + addCount) },
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
            editDraft = { draft -> draft.copy(teamLayout = layout) },
        ) { _, networkEngine ->
            networkEngine.applyBattleRoomTeamLayout(layout)
            BattleRoomUiBridge.updateUI()
            true
        }

    open fun applyBattleRoomOptions(options: BattleRoomOptions): Boolean =
        editBattleRoom(
            "apply battle room options",
            editDraft = { draft -> draft.copy(options = options) },
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
            // player exists and returns the draft unchanged.
            editDraft = { draft ->
                draft.takeIf { resolveBattleRoomPlayerSnapshot(playerId, it.pendingBattleRoomPlayers()) != null }
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
            editDraft = { draft ->
                val index = draftAiPlayerIndex(playerId)
                if (index == null || index <= 0 || index > draft.aiPlayerCount) {
                    null
                } else {
                    draft.copy(aiPlayerCount = draft.aiPlayerCount - 1)
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
        synchronized(gameLock) {
            if (currentBattleRoomFromEngineLocked() == null && pendingRendererBattleRoomConfig != null) {
                return true
            }
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
        if (!asyncMapLoadInProgress) {
            return false
        }
        return mapPath == null || asyncMapLoadPath == mapPath
    }

    open fun mapLoadError(mapPath: String? = null): Throwable? {
        val error = asyncMapLoadError ?: return null
        return if (mapPath == null || asyncMapLoadPath == mapPath) error else null
    }

    open fun loadingStatus(): GameLoadingStatus {
        asyncEnginePreloadError?.let { error ->
            return GameLoadingStatus(
                text = "Loading failed: ${error.message ?: error.javaClass.simpleName}",
                progress = 1.0f,
            )
        }
        val engine = gameEngine ?: GameEngine.getInstance()
        val fallbackText = when {
            asyncMapLoadInProgress -> "Loading map data"
            asyncEnginePreloadInProgress -> "Loading..."
            else -> "Loading..."
        }
        val text = engine?.getLoadingText()?.takeIf { it.isNotBlank() } ?: fallbackText
        val progress = engine?.getLoadingProgress()?.coerceIn(0.0f, 1.0f)
            ?: when {
                asyncMapLoadInProgress -> 0.15f
                asyncEnginePreloadInProgress -> 0.05f
                else -> null
            }
        return GameLoadingStatus(text = text, progress = progress)
    }

    open fun canResume(): Boolean =
        synchronized(gameLock) {
            val engine = gameEngine ?: return@synchronized false
            engine.hasLoadedLevel && !engine.isMenuBackgroundMap && activeRunningMapPath(engine) != null && !menuBackgroundActive
        }

    open fun runningMapPath(): String? = synchronized(gameLock) { runningMapPath }

    open fun currentMapDisplayName(): String? =
        synchronized(gameLock) {
            activeEngineLocked()
                ?.getCurrentMapName()
                ?.takeIf { it.isNotBlank() }
        }

    open fun pendingRendererMapPath(): String? =
        synchronized(gameLock) {
            pendingRendererBattleRoomConfig?.mapPath?.takeIf { it.isNotBlank() }
                ?: pendingMapPath?.takeIf { it.isNotBlank() }
        }

    open fun pendingRendererBattleRoomConfig(): BattleRoomLaunchConfig? =
        synchronized(gameLock) { pendingRendererBattleRoomConfig }

    open fun pendingMemorySnapshot(): GameMemorySnapshot? =
        synchronized(gameLock) { pendingMemorySnapshot }

    open fun activeRendererBattleRoomConfig(): BattleRoomLaunchConfig? =
        synchronized(gameLock) { activeRendererBattleRoomConfig ?: pendingRendererBattleRoomConfig }

    protected data class RendererPreparationSnapshot(
        val mapPath: String?,
        val memorySnapshot: GameMemorySnapshot?,
        val battleRoomConfig: BattleRoomLaunchConfig?,
    )

    protected fun rendererPreparationSnapshot(): RendererPreparationSnapshot =
        synchronized(gameLock) {
            RendererPreparationSnapshot(
                mapPath = pendingRendererBattleRoomConfig?.mapPath?.takeIf { it.isNotBlank() }
                    ?: pendingMapPath?.takeIf { it.isNotBlank() },
                memorySnapshot = pendingMemorySnapshot,
                battleRoomConfig = pendingRendererBattleRoomConfig,
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
        synchronized(gameLock) {
            mapLoadGeneration += 1
            pendingMapPath = mapPath
            pendingMemorySnapshot = memorySnapshot
            pendingRendererBattleRoomConfig = battleRoomConfig
            activeRendererBattleRoomConfig = null
            asyncMapLoadPath = mapPath
            asyncMapLoadInProgress = true
            asyncMapLoadError = null
            runningMapPath = null
            menuBackgroundActive = false
        }
    }

    protected fun beginRendererStandalonePreparation(requestPath: String) {
        synchronized(gameLock) {
            mapLoadGeneration += 1
            pendingMapPath = null
            pendingMemorySnapshot = null
            pendingRendererBattleRoomConfig = null
            activeRendererBattleRoomConfig = null
            asyncMapLoadPath = requestPath
            asyncMapLoadInProgress = true
            asyncMapLoadError = null
            runningMapPath = null
            menuBackgroundActive = false
        }
    }

    protected fun beginRendererStartedGamePreparation(mapPath: String) {
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
    }

    protected fun markRendererMenuBackgroundReady() {
        synchronized(gameLock) {
            asyncMapLoadPath = null
            asyncMapLoadInProgress = false
            asyncMapLoadError = null
            menuBackgroundActive = true
        }
    }

    protected fun markRendererMenuBackgroundError(requestPath: String, error: Throwable) {
        synchronized(gameLock) {
            asyncMapLoadPath = requestPath
            asyncMapLoadInProgress = false
            asyncMapLoadError = error
            menuBackgroundActive = false
        }
    }

    protected fun markRendererSurfaceStopped() {
        synchronized(gameLock) {
            menuBackgroundActive = false
        }
    }

    open fun markRendererMapReady(mapPath: String, viewport: KoolCanvasViewport) {
        synchronized(gameLock) {
            lastViewport = viewport
            if (pendingMapPath == mapPath) {
                pendingMapPath = null
            }
            if (pendingMemorySnapshot?.mapPath == mapPath) {
                pendingMemorySnapshot = null
            }
            if (pendingRendererBattleRoomConfig?.mapPath == mapPath) {
                activeRendererBattleRoomConfig = pendingRendererBattleRoomConfig
                pendingRendererBattleRoomConfig = null
            }
            asyncMapLoadPath = null
            asyncMapLoadInProgress = false
            asyncMapLoadError = null
            menuBackgroundActive = false
            runningMapPath = mapPath
        }
    }

    open fun markRendererMapError(mapPath: String, error: Throwable) {
        synchronized(gameLock) {
            if (pendingMapPath == mapPath) {
                pendingMapPath = null
            }
            if (pendingRendererBattleRoomConfig?.mapPath == mapPath) {
                pendingRendererBattleRoomConfig = null
            }
            asyncMapLoadPath = mapPath
            asyncMapLoadInProgress = false
            asyncMapLoadError = error
        }
    }

    open fun discardRunningGame() {
        synchronized(gameLock) {
            mapLoadGeneration += 1
            pendingMapPath = null
            pendingMemorySnapshot = null
            pendingRendererBattleRoomConfig = null
            activeRendererBattleRoomConfig = null
            asyncEnginePreloadInProgress = false
            asyncEnginePreloadError = null
            asyncMapLoadPath = null
            asyncMapLoadInProgress = false
            asyncMapLoadError = null
            runningMapPath = null
            menuBackgroundActive = false
            lastFrame = KoolCanvasFrame(lastViewport, emptyList())
            clearInputState()
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
        synchronized(gameLock) {
            val engine = activeEngineLocked() ?: return
            val saveName = name.toRwSaveName()
            runCatching {
                engine.gameSaver.saveGame(saveName, false)
                engine.gameUI?.showMediumPriorityMessage("Game saved")
            }.onFailure { error ->
                logger.error(error) { "Failed to save game: $saveName" }
                engine.gameUI?.showHighPriorityMessage("Save failed: ${error.message ?: error.javaClass.simpleName}")
            }
        }
    }

    open fun captureMemorySnapshot(): GameMemorySnapshot? {
        synchronized(gameLock) {
            val engine = activeEngineLocked() ?: return null
            if (engine.isNetworkGameActive() || !engine.hasLoadedLevel) {
                return null
            }
            val mapPath = activeRunningMapPath(engine)?.takeIf { it.isNotBlank() } ?: return null
            val battleRoomConfig = activeRendererBattleRoomConfig
                ?.copy(mapPath = mapPath)
                ?: pendingRendererBattleRoomConfig?.copy(mapPath = mapPath)
            return runCatching {
                val output = GameOutputStream()
                engine.gameSaver.writeSaveToStream(output)
                GameMemorySnapshot(mapPath, output.toByteArray(), battleRoomConfig)
            }.onFailure { error ->
                logger.error(error) { "Failed to capture in-memory RW save for $mapPath" }
                engine.gameUI?.showHighPriorityMessage("Map snapshot failed: ${error.message ?: error.javaClass.simpleName}")
            }.getOrNull()
        }
    }

    open fun requestExportMap(name: String) {
        synchronized(gameLock) {
            val engine = activeEngineLocked() ?: return
            val sourceMap = engine.currentMapPath?.takeIf { it.isNotBlank() }
                ?: runningMapPath?.takeIf { it.isNotBlank() }
                ?: return
            val exportName = name.toRwExportMapName()
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
        return true
    }

    open fun requestSurrender() {
        synchronized(gameLock) {
            activeEngineLocked()?.networkEngine?.sendChatMessage("-surrender")
        }
    }

    open fun requestChatMessage(message: String, teamOnly: Boolean = false) {
        if (message.isBlank()) return
        synchronized(gameLock) {
            activeEngineLocked()?.networkEngine?.sendChatMessage(if (teamOnly) "-t $message" else message)
        }
    }

    open fun isNetworkMultiplayerActive(): Boolean = synchronized(gameLock) {
        activeEngineLocked()?.isNetworkGameActive() == true
    }

    open fun multiplayerPlayerList(): List<BattleRoomPlayerSnapshot> = synchronized(gameLock) {
        val engine = activeEngineLocked() ?: return@synchronized emptyList()
        val networkEngine = engine.networkEngine ?: return@synchronized emptyList()
        if (!engine.isNetworkGameActive()) return@synchronized emptyList()
        battleRoomPlayerTeams().map { team ->
            team.toBattleRoomSnapshotPlayer(
                isLocal = team == networkEngine.localPlayerTeam || team == engine.playerTeam,
            )
        }
    }

    open fun multiplayerChatHistory(): List<MultiplayerChatSnapshot> = synchronized(gameLock) {
        val engine = activeEngineLocked() ?: return@synchronized emptyList()
        val networkEngine = engine.networkEngine ?: return@synchronized emptyList()
        if (!engine.isNetworkGameActive()) return@synchronized emptyList()
        networkEngine.chatLog.b().mapNotNull { entry ->
            val message = entry as? ChatMessage ?: return@mapNotNull null
            MultiplayerChatSnapshot(
                text = message.displayText,
                teamColorIndex = message.teamColorIndex,
            )
        }
    }

    open fun runningMultiplayerExitInfo(): RunningMultiplayerExitInfo? = synchronized(gameLock) {
        val engine = activeEngineLocked() ?: return@synchronized null
        val networkEngine = engine.networkEngine ?: return@synchronized null
        if (!engine.isNetworkGameActive() || !networkEngine.gameHasBeenStarted) return@synchronized null
        RunningMultiplayerExitInfo(isHost = networkEngine.isServer)
    }

    open fun disconnectRunningMultiplayer() {
        synchronized(gameLock) {
            activeEngineLocked()?.networkEngine?.disconnectNetworking("exited")
        }
    }

    open fun scheduleReturnToBattleRoom(): Boolean = synchronized(gameLock) {
        val engine = activeEngineLocked() ?: return@synchronized false
        val networkEngine = engine.networkEngine ?: return@synchronized false
        if (!networkEngine.networkGameActive || !networkEngine.gameHasBeenStarted || !networkEngine.isServer) {
            return@synchronized false
        }
        networkEngine.scheduleDefaultReturnToBattleroom()
        engine.gameUI?.isDraggingSelection = false
        true
    }

    open fun currentMultiplayerPlayerName(): String = synchronized(gameLock) {
        val engine = activeEngineLocked()
        engine?.networkEngine?.playerName
            ?: engine?.settingsEngine?.lastNetworkPlayerName
            ?: SettingsEngine.getInstance()?.lastNetworkPlayerName
            ?: "Player"
    }

    open fun updateMultiplayerPlayerName(name: String): Boolean {
        val normalizedName = name.trim().replace(' ', '_')
        if (normalizedName.isBlank()) return false
        synchronized(gameLock) {
            val engine = activeEngineLocked()
            engine?.networkEngine?.playerName = normalizedName
            val settings = engine?.settingsEngine ?: SettingsEngine.getInstance() ?: return false
            settings.lastNetworkPlayerName = normalizedName
            settings.save()
        }
        return true
    }

    open fun injectTransferredUnits(units: List<TransferredUnit>, targetPortalId: String?): Int {
        if (units.isEmpty()) return 0
        synchronized(gameLock) {
            val engine = activeEngineLocked() ?: return 0
            if (!engine.hasLoadedLevel) return 0
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
            return createdCount
        }
    }

    open fun isMapLoaded(mapPath: String? = runningMapPath()): Boolean {
        if (asyncMapLoadInProgress) {
            return false
        }
        synchronized(gameLock) {
            val engine = gameEngine ?: return false
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
        return synchronized(gameLock) {
            lastFrame.commands.isNotEmpty() || !TileMap.layerBufferManager.hasVisiblePendingRedraws()
        }
    }

    abstract fun loadPendingMapNow(): KoolCanvasFrame

    protected abstract fun ensureStarted(viewport: KoolCanvasViewport): GameEngine

    protected abstract fun applyViewport(engine: GameEngine, viewport: KoolCanvasViewport)

    protected open fun clearInputState() = Unit

    protected open fun activeRunningMapPath(engine: GameEngine): String? =
        runningMapPath
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
            synchronized(gameLock) {
                lastViewport = viewport
            }
            val engine = ensureStartedOutsideGameLock(viewport)
            synchronized(gameLock) {
                applyViewport(engine, viewport)
            }
            logger.info { "Prepared $sessionLogName engine asynchronously: ${elapsedMs(startedAt)}ms" }
        }.onFailure { error ->
            asyncEnginePreloadError = error
            logger.error(error) { "$sessionLogName async engine preload failed" }
        }.also {
            asyncEnginePreloadInProgress = false
        }
    }

    private fun runBattleRoomCommand(
        label: String,
        command: (GameEngine, NetworkEngine) -> Boolean,
    ): Boolean =
        runCatching {
            val viewport = synchronized(gameLock) {
                lastViewport.takeIf { it.width > 0 && it.height > 0 } ?: defaultPreloadViewport
            }
            ensureStartedOutsideGameLock(viewport)
            synchronized(gameLock) {
                val engine = activeEngineLocked() ?: return@synchronized false
                val networkEngine = engine.networkEngine ?: return@synchronized false
                prepareActiveBattleRoomEngine(engine)
                command(engine, networkEngine)
            }
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
            rwxP2PSession = networkEngine.rwxP2PSession,
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
        menuBackgroundActive = false
        pendingRendererBattleRoomConfig = null
        pendingMapPath = null
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
                if (generation != mapLoadGeneration) {
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
                if (generation == mapLoadGeneration) {
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
            if (generation == mapLoadGeneration) {
                asyncMapLoadError = error
                logger.error(error) { "$sessionLogName async $kind load failed: $requestKey" }
            } else {
                logger.info { "Ignored stale $sessionLogName $kind load failure for: $requestKey" }
            }
        }.also {
            if (generation == mapLoadGeneration) {
                asyncMapLoadInProgress = false
            }
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
        val mapPath = pendingMapPath ?: return
        val snapshot = pendingMemorySnapshot?.takeIf { it.mapPath == mapPath }
        val battleRoomConfig = pendingRendererBattleRoomConfig?.takeIf { it.mapPath == mapPath }
        if (runningMapPath == mapPath && engine.hasLoadedLevel) {
            pendingMapPath = null
            pendingMemorySnapshot = null
            pendingRendererBattleRoomConfig = null
            return
        }
        pendingMapPath = null
        pendingMemorySnapshot = null
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
            if (battleRoomConfig != null && pendingRendererBattleRoomConfig == battleRoomConfig) {
                pendingRendererBattleRoomConfig = null
            }
            logger.error(error) { "$sessionLogName map load failed: $mapPath" }
        }
    }

    protected open fun loadMap(engine: GameEngine, mapPath: String) {
        menuBackgroundActive = false
        activeRendererBattleRoomConfig = null
        runningMapPath = mapPath
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
        menuBackgroundActive = false
        runningMapPath = config.mapPath
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
        activeRendererBattleRoomConfig = config
        pendingRendererBattleRoomConfig = null
        runningMapPath = activeRunningMapPath(engine) ?: config.mapPath
    }

    protected open fun loadMemorySnapshot(engine: GameEngine, snapshot: GameMemorySnapshot) {
        menuBackgroundActive = false
        runningMapPath = snapshot.mapPath
        pendingRendererBattleRoomConfig = null
        activeRendererBattleRoomConfig = snapshot.battleRoomConfig
        engine.isStopped = false
        engine.isPaused = false
        check(engine.gameSaver.readSaveFromStream(GameInputStream(snapshot.saveBytes), false, false, false)) {
            "Unable to restore in-memory save: ${snapshot.mapPath}"
        }
        runningMapPath = activeRunningMapPath(engine) ?: snapshot.mapPath
        activeRendererBattleRoomConfig =
            activeRendererBattleRoomConfig?.copy(mapPath = runningMapPath ?: snapshot.mapPath)
    }

    protected open fun loadReplay(engine: GameEngine, replayName: String) {
        menuBackgroundActive = false
        runningMapPath = replayName
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
