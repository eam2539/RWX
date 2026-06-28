package io.github.rwx.app

import de.fabmax.kool.KoolContext
import io.github.rwx.render.canvas.KoolCanvasFrame
import io.github.rwx.render.canvas.KoolCanvasViewport
import io.github.rwx.ui.*
import io.github.rwx.ui.model.LevelSelectMode
import io.github.rwx.ui.model.MainMenuConditions
import io.github.rwx.ui.model.PauseMenuConditions
import io.github.rwx.ui.model.ResourceBrowserType

const val RWX_KOOL_READY_MARKER: String = "RWX_SCREENSHOT_READY"
const val RWX_KOOL_GAME_FRAME_READY_MARKER: String = "RWX_GAME_FRAME_READY"

class AppSession internal constructor(
    val navigator: ScreenNavigator,
    private val onQuit: () -> Unit,
    private val onBack: () -> Unit,
) {
    fun navigateBack() = onBack()

    fun isFinishLoading(): Boolean =
        navigator.current != AppScreen.Loading

    fun quit() = onQuit()
}

fun installApp(
    context: KoolContext,
    options: AppOptions = AppOptions(),
    onQuit: () -> Unit = { context.window.close() },
): AppSession {
    val bootstrap = createAppBootstrap(context, options)
    val platformBridge = bootstrap.platformBridge
    val appMetadata = bootstrap.appMetadata
    val gameSession = bootstrap.gameSession
    val menuBackgroundSession = bootstrap.menuBackgroundSession
    val menuBackgroundRenderer = bootstrap.menuBackgroundRenderer
    val modRepository = bootstrap.modRepository
    val resourceBrowserRepository = bootstrap.resourceBrowserRepository
    val updateRepository = bootstrap.updateRepository
    val settingsRepository = bootstrap.settingsRepository
    val actions = bootstrap.actions
    val settingsModel = bootstrap.settingsModel
    val koolCanvasSceneHost = bootstrap.koolCanvasSceneHost
    val koolCanvasScene = bootstrap.koolCanvasScene
    val loadingSceneHost = bootstrap.loadingSceneHost
    val mainMenuSceneHost = bootstrap.mainMenuSceneHost
    val pauseSceneHost = bootstrap.pauseSceneHost
    val levelSelectSceneHost = bootstrap.levelSelectSceneHost
    val levelSelectViewModelFactory = bootstrap.levelSelectViewModelFactory
    val replaySelectSceneHost = bootstrap.replaySelectSceneHost
    val settingsSceneHost = bootstrap.settingsSceneHost
    val multiplayerSceneHost = bootstrap.multiplayerSceneHost
    val modsSceneHost = bootstrap.modsSceneHost
    val resourceBrowserSceneHost = bootstrap.resourceBrowserSceneHost
    val loadingDialogSceneHost = bootstrap.loadingDialogSceneHost
    val battleRoomSceneHost = bootstrap.battleRoomSceneHost
    val modWindowSceneHost = bootstrap.modWindowSceneHost
    val dialogSceneHost = bootstrap.dialogSceneHost

    var lastExternalGameFrame: KoolCanvasFrame? = null
    val startupTargetScreen = if (options.initialScreen == AppScreen.Loading) {
        AppScreen.MainMenu
    } else {
        options.initialScreen
    }
    lateinit var navigator: ScreenNavigator
    lateinit var pendingStartController: PendingStartController

    fun rwGameViewport(): KoolCanvasViewport {
        val windowSize = context.window.size
        return KoolCanvasViewport(
            width = if (windowSize.x > 1) windowSize.x else 1280,
            height = if (windowSize.y > 1) windowSize.y else 720,
        )
    }

    val screenPresenter = ScreenPresenter(
        bootstrap = bootstrap,
        viewport = ::rwGameViewport,
    )

    val warmupController = WarmupController(
        gameSession = gameSession,
        menuBackgroundSession = menuBackgroundSession,
        menuBackgroundRenderer = menuBackgroundRenderer,
        loadingSceneHost = loadingSceneHost,
        navigateTo = { screen -> navigator.navigateTo(screen) },
        clearExternalFrame = { lastExternalGameFrame = null },
        isMenuBackgroundDemoEnabled = { settingsModel.showMainMenuBackgroundDemo.value },
    )

    lateinit var updateController: UpdateController
    val dialogController = DialogController(
        platformBridge = platformBridge,
        appMetadata = appMetadata,
        dialogSceneHost = dialogSceneHost,
        requestManualUpdateCheck = { updateController.request(manual = true) },
    )

    val battleRoomController = BattleRoomController(
        gameSession = gameSession,
        levelSelectViewModelFactory = levelSelectViewModelFactory,
        sceneHost = battleRoomSceneHost,
        initialMode = options.levelSelectMode ?: LevelSelectMode.Skirmish,
        showUnavailableDialog = dialogController::showUnavailable,
    )

    fun refreshMainMenu() {
        mainMenuSceneHost.updateItems(
            MainMenuConditions(
                canResume = gameSession.canResume(),
                isDesktop = options.isDesktop,
            )
        )
    }

    pendingStartController = PendingStartController(
        gameSession = gameSession,
        dialogSceneHost = dialogSceneHost,
        refreshMainMenu = ::refreshMainMenu,
        navigateTo = { screen -> navigator.navigateTo(screen) },
    )

    val gameLaunchController = GameLaunchController(
        gameSession = gameSession,
        battleRoomController = battleRoomController,
        warmupController = warmupController,
        pendingStartController = pendingStartController,
        dialogSceneHost = dialogSceneHost,
        viewport = ::rwGameViewport,
        currentScreen = { navigator.current },
        navigateTo = { screen -> navigator.navigateTo(screen) },
    )

    updateController = UpdateController(
        appMetadata = appMetadata,
        updateRepository = updateRepository,
        loadingDialogSceneHost = loadingDialogSceneHost,
        dialogSceneHost = dialogSceneHost,
        openLink = dialogController::openLink,
    )
    val multiplayerLobbyController = MultiplayerLobbyController(
        sceneHost = multiplayerSceneHost,
    )
    val inGameDialogController = InGameDialogController(
        gameSession = gameSession,
        koolCanvasScene = koolCanvasScene,
        dialogSceneHost = dialogSceneHost,
        currentScreen = { navigator.current },
        viewport = ::rwGameViewport,
        showUnavailableDialog = dialogController::showUnavailable
    )

    val mapController = MapController(
        gameSession = gameSession,
        koolCanvasScene = koolCanvasScene,
        storage = { platformBridge?.storage },
        viewport = ::rwGameViewport,
        pendingStartMapPath = pendingStartController::currentMapPath,
        setPendingStart = pendingStartController::set,
        navigateToInGame = { navigator.navigateTo(AppScreen.InGame) },
        currentBattleRoomSnapshotForJoin = battleRoomController::currentSnapshot,
        showUnavailableDialog = dialogController::showUnavailable,
        showDialogOverGame = inGameDialogController::showDialogOverGame,
        hideDialog = dialogSceneHost::hide,
    )

    val battleRoomJoinController = BattleRoomJoinController(
        gameSession = gameSession,
        loadingDialogSceneHost = loadingDialogSceneHost,
        onStarted = battleRoomController::markJoinedRoomStarted,
        onConnected = { snapshot ->
            battleRoomController.updateConnectedRoom(snapshot)
            navigator.navigateTo(AppScreen.BattleRoom)
        },
        onFailed = dialogController::showUnavailable,
    )
    val multiplayerConnectionController = MultiplayerConnectionController(
        gameSession = gameSession,
        lobbyController = multiplayerLobbyController,
        battleRoomJoinController = battleRoomJoinController,
        dialogSceneHost = dialogSceneHost,
        selectHostMap = battleRoomController::selectedOrDefaultMap,
        onHostPreparing = battleRoomController::prepareHostRoom,
        updateBattleRoomFromNetwork = battleRoomController::updateFromNetwork,
        navigateToBattleRoom = { navigator.navigateTo(AppScreen.BattleRoom) },
        showUnavailableDialog = dialogController::showUnavailable,
    )
    val battleRoomAdminController = BattleRoomAdminController(
        gameSession = gameSession,
        dialogSceneHost = dialogSceneHost,
        updateBattleRoomFromNetwork = battleRoomController::updateFromNetwork,
        showUnavailableDialog = dialogController::showUnavailable,
    )
    val battleRoomLaunchController = BattleRoomLaunchController(
        gameSession = gameSession,
        storage = { platformBridge?.storage },
        viewport = ::rwGameViewport,
        currentScreen = { navigator.current },
        draftLaunchConfig = battleRoomController::draftLaunchConfig,
        draftMapPath = battleRoomController::draftMapPath,
        showStartNewGameDialog = gameLaunchController::showStartNewGameDialog,
        enterRwGame = gameLaunchController::enterRwGame,
        clearPendingRwStartState = warmupController::clear,
        clearPendingStartState = pendingStartController::clear,
        setPendingStartState = pendingStartController::set,
        navigateToInGame = { navigator.navigateTo(AppScreen.InGame) },
        showUnavailableDialog = dialogController::showUnavailable,
    )

    val externalGameController = ExternalGameController(
        gameSession = gameSession,
        refreshMainMenu = ::refreshMainMenu,
        navigateTo = { screen -> navigator.navigateTo(screen) },
    )

    gameSession.setInGameMenuCallbacks(object : InGameMenuCallbacks {
        override fun requestSettings() {
            inGameDialogController.requestInGameKoolOverlay { CoreUiEventQueue.requestInGameSettings() }
        }

        override fun requestSave() {
            inGameDialogController.requestInGameKoolOverlay { CoreUiEventQueue.requestInGameSave() }
        }

        override fun requestExportMap() {
            inGameDialogController.requestInGameKoolOverlay { CoreUiEventQueue.requestInGameExportMap() }
        }

        override fun requestMapList() {
            inGameDialogController.requestInGameKoolOverlay { CoreUiEventQueue.requestInGameMapList() }
        }

        override fun shouldShowMapList(): Boolean =
            mapController.canShowMapList()

        override fun requestChat(teamOnly: Boolean) {
            inGameDialogController.requestInGameKoolOverlay { CoreUiEventQueue.requestInGameChat(teamOnly) }
        }

        override fun requestPlayerList() {
            inGameDialogController.requestInGameKoolOverlay { CoreUiEventQueue.requestInGamePlayerList() }
        }

        override fun requestSurrender() {
            inGameDialogController.requestInGameKoolOverlay { CoreUiEventQueue.requestInGameSurrender() }
        }

        override fun requestExit() {
            inGameDialogController.requestInGameKoolOverlay { CoreUiEventQueue.requestInGameExit() }
        }

        override fun requestBattleRoomRefresh() {
            CoreUiEventQueue.requestBattleRoomRefresh()
        }

        override fun requestReturnToBattleRoom() {
            CoreUiEventQueue.requestInGameReturnToBattleRoom()
        }
    })

    val modsController = ModsController(
        modRepository = modRepository,
        gameSession = gameSession,
        sceneHost = modsSceneHost,
        loadingDialogSceneHost = loadingDialogSceneHost,
        dialogSceneHost = dialogSceneHost,
        onModsReloaded = {
            levelSelectViewModelFactory.invalidateCaches()
        },
    )

    val sessionActions = SessionActions(
        gameSession = gameSession,
        warmupController = warmupController,
        pendingStartController = pendingStartController,
        settingsSceneHost = settingsSceneHost,
        settingsRepository = settingsRepository,
        settingsModel = settingsModel,
        modsController = modsController,
        currentScreen = { navigator.current },
        navigateTo = { screen -> navigator.navigateTo(screen) },
        refreshMainMenu = ::refreshMainMenu,
        onQuit = onQuit,
    )
    val gameReadyController = GameReadyController(
        battleRoomController = battleRoomController,
        gameLaunchController = gameLaunchController,
        exitRwGameToMainMenu = sessionActions::exitRwGameToMainMenu,
        autoReturnMainMenuAfterGameReady = options.autoReturnMainMenuAfterGameReady,
        autoStartSecondBattleRoom = options.autoStartBattleRoomTwice
    )

    val resourceBrowserController = ResourceBrowserController(
        repository = resourceBrowserRepository,
        sceneHost = resourceBrowserSceneHost,
        loadingDialogSceneHost = loadingDialogSceneHost,
        dialogSceneHost = dialogSceneHost,
        showUnavailableDialog = dialogController::showUnavailable,
        onDownloaded = { type ->
            when (type) {
                ResourceBrowserType.Mod -> {
                    modsController.reloadAvailableAndRefresh()
                    // A downloaded mod may bundle maps; drop cached built-in lists so they appear.
                    levelSelectViewModelFactory.invalidateCaches()
                }

                ResourceBrowserType.Map -> {
                    levelSelectSceneHost.updateMaps(LevelSelectMode.CustomMaps)
                }
            }
        },
    )

    val screenLifecycleController = ScreenLifecycleController(
        screenPresenter = screenPresenter,
        externalFrame = { lastExternalGameFrame },
        actions = actions,
        battleRoomController = battleRoomController,
        battleRoomLaunchController = battleRoomLaunchController,
        replaySelectSceneHost = replaySelectSceneHost,
        multiplayerLobbyController = multiplayerLobbyController,
        modsController = modsController,
        resourceBrowserController = resourceBrowserController,
        modWindowSceneHost = modWindowSceneHost,
        settingsRepository = settingsRepository,
        settingsModel = settingsModel,
        refreshMainMenu = ::refreshMainMenu,
        autoStartSinglePlayerFromUi = options.autoStartSinglePlayerFromUi,
        autoStartBattleRoom = options.autoStartBattleRoom,
    )
    navigator = ScreenNavigator(
        initialScreen = AppScreen.Loading,
        onScreenChanged = screenLifecycleController::onScreenChanged,
    )
    val session = AppSession(
        navigator = navigator,
        onQuit = onQuit,
        onBack = {
            when (backActionForScreen(navigator.current, gameSession.rendersIntoKoolCanvas)) {
                BackNavigationAction.Pause -> {
                    pauseSceneHost.updateItems(
                        PauseMenuConditions(
                            canSave = true,
                            isMultiplayer = gameSession.isNetworkMultiplayerActive(),
                        )
                    )
                    navigator.navigateTo(AppScreen.Paused)
                }

                BackNavigationAction.ShowExitDialog -> {
                    inGameDialogController.showExitGameDialog(sessionActions::exitRwGameToMainMenu)
                }

                BackNavigationAction.MainMenu -> {
                    sessionActions.saveCurrentScreenStateBeforeMainMenu()
                    navigator.navigateTo(AppScreen.MainMenu)
                }

                BackNavigationAction.InGame -> navigator.navigateTo(AppScreen.InGame)
            }
        },
    )
    StartupController(
        battleRoomController = battleRoomController,
        warmupController = warmupController,
    ).start(startupTargetScreen)

    ActionRouter(
        actions = actions,
        navigator = navigator,
        platformBridge = platformBridge,
        settingsRepository = settingsRepository,
        settingsModel = settingsModel,
        levelSelectSceneHost = levelSelectSceneHost,
        battleRoomController = battleRoomController,
        multiplayerLobbyController = multiplayerLobbyController,
        multiplayerConnectionController = multiplayerConnectionController,
        battleRoomAdminController = battleRoomAdminController,
        battleRoomLaunchController = battleRoomLaunchController,
        modsController = modsController,
        resourceBrowserController = resourceBrowserController,
        resumeRwGame = gameLaunchController::resumeRwGame,
        enterRwGame = gameLaunchController::enterRwGame,
        enterReplay = gameLaunchController::enterReplay,
        enterSavedGame = gameLaunchController::enterSavedGame,
        quitApp = sessionActions::quitApp,
        showAboutDialog = dialogController::showAboutDialog,
        clearPendingStartState = pendingStartController::clear,
        openInGameSettings = sessionActions::openInGameSettings,
        showSaveGameDialog = inGameDialogController::showSaveGameDialog,
        showExitGameDialog = inGameDialogController::showExitGameDialog,
        showInGameChatDialog = inGameDialogController::showInGameChatDialog,
        showMultiplayerPlayerList = inGameDialogController::showInGamePlayerListDialog,
        requestInGameSurrender = sessionActions::requestInGameSurrender,
        exitRwGameToMainMenu = sessionActions::exitRwGameToMainMenu,
        showUnavailableDialog = dialogController::showUnavailable,
    ).install()

    val inputController = InputController(
        gameSession = gameSession,
        currentScreen = { navigator.current },
        screenScale = { context.window.parentScreenScale },
        navigateBack = session::navigateBack,
    ).also { it.install() }
    screenPresenter.apply(navigator.current, lastExternalGameFrame)

    FrameLoopInstaller(
        context = context,
        gameSession = gameSession,
        menuBackgroundRenderer = menuBackgroundRenderer,
        screenPresenter = screenPresenter,
        warmupController = warmupController,
        koolCanvasSceneHost = koolCanvasSceneHost,
        currentScreen = { navigator.current },
        lastExternalFrame = { lastExternalGameFrame },
        setLastExternalFrame = { frame -> lastExternalGameFrame = frame },
        multiplayerLobbyController = multiplayerLobbyController,
        battleRoomController = battleRoomController,
        battleRoomLaunchController = battleRoomLaunchController,
        resourceBrowserController = resourceBrowserController,
        inGameDialogController = inGameDialogController,
        rwxMapController = mapController,
        sessionActions = sessionActions,
        updateController = updateController,
        battleRoomJoinController = battleRoomJoinController,
        pendingStartController = pendingStartController,
        externalGameController = externalGameController,
        modsController = modsController,
        inputController = inputController,
        gameReadyController = gameReadyController,
        refreshModWindow = modWindowSceneHost::refresh,
        onBattleRoomClosed = { reason, message ->
            battleRoomJoinController.handleBattleRoomClosed()
            BattleRoomUiBridge.startGamePending = false
            if (navigator.current == AppScreen.BattleRoom) {
                navigator.navigateTo(battleRoomController.closeRoom())
            } else {
                gameSession.leaveBattleRoom()
            }
            val text = listOfNotNull(reason, message).joinToString("\n").ifBlank { null }
            text?.let { dialogController.showUnavailable(it) }
        },
    ).install()

    StartupFinalizer(
        bootstrap = bootstrap,
        inputController = inputController,
        currentScreen = { navigator.current },
        multiplayerLobbyController = multiplayerLobbyController,
        resourceBrowserController = resourceBrowserController,
    ).finish()
    options.joinServer?.let { address ->
        multiplayerConnectionController.joinOriginalServer(address, roomLabel = address)
    }
    return session
}
