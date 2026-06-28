package io.github.rwx.app

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Scene
import io.github.rwx.AppMetadata
import io.github.rwx.PlatformBridge
import io.github.rwx.i18n.I18n
import io.github.rwx.mod.ModRepository
import io.github.rwx.net.ResourceBrowserRepository
import io.github.rwx.net.UpdateRepository
import io.github.rwx.render.GameRenderBackend
import io.github.rwx.render.canvas.KoolCanvasSceneHost
import io.github.rwx.session.GameSession
import io.github.rwx.settings.GameSettingsRepository
import io.github.rwx.ui.*
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

internal class ActionHandlers {
    var menu: (MainMenuAction) -> Unit = {}
    var pause: (PauseMenuAction) -> Unit = {}
    var levelSelect: (LevelSelectAction) -> Unit = {}
    var replaySelect: (ReplaySelectAction) -> Unit = {}
    var settings: (SettingsAction) -> Unit = {}
    var multiplayer: (MultiplayerAction) -> Unit = {}
    var mods: (ModsAction) -> Unit = {}
    var resourceBrowser: (ResourceBrowserAction) -> Unit = {}
    var battleRoom: (BattleRoomAction) -> Unit = {}
}

internal data class AppBootstrap(
    val platformBridge: PlatformBridge?,
    val appMetadata: AppMetadata,
    val gameSession: GameSession,
    val gameRenderBackend: GameRenderBackend,
    val menuBackgroundSession: GameSession,
    val menuBackgroundRenderer: GameRenderBackend,
    val modRepository: ModRepository,
    val resourceBrowserRepository: ResourceBrowserRepository,
    val updateRepository: UpdateRepository,
    val settingsRepository: GameSettingsRepository,
    val actions: ActionHandlers,
    val settingsModel: SettingsModel,
    val koolCanvasSceneHost: KoolCanvasSceneHost,
    val koolCanvasScene: Scene,
    val modHudSceneHost: ModHudSceneHost,
    val modHudScene: Scene,
    val loadingSceneHost: LoadingSceneHost,
    val loadingScene: Scene,
    val mainMenuSceneHost: MainMenuSceneHost,
    val mainMenuScene: Scene,
    val pauseSceneHost: PauseMenuSceneHost,
    val pauseScene: Scene,
    val levelSelectSceneHost: LevelSelectSceneHost,
    val levelSelectViewModelFactory: LevelSelectViewModelFactory,
    val levelSelectScene: Scene,
    val replaySelectSceneHost: ReplaySelectSceneHost,
    val replaySelectScene: Scene,
    val settingsSceneHost: SettingsSceneHost,
    val settingsScene: Scene,
    val multiplayerSceneHost: MultiplayerSceneHost,
    val multiplayerScene: Scene,
    val modsSceneHost: ModsSceneHost,
    val modsScene: Scene,
    val resourceBrowserSceneHost: ResourceBrowserSceneHost,
    val resourceBrowserScene: Scene,
    val loadingDialogSceneHost: LoadingDialogSceneHost,
    val loadingDialogScene: Scene,
    val battleRoomSceneHost: BattleRoomSceneHost,
    val battleRoomScene: Scene,
    val modWindowSceneHost: ModWindowSceneHost,
    val modWindowScene: Scene,
    val snackbarSceneHost: SnackbarSceneHost,
    val snackbarScene: Scene,
    val dialogSceneHost: DialogSceneHost,
    val dialogScene: Scene,
)

internal fun createAppBootstrap(
    context: KoolContext,
    options: AppOptions,
): AppBootstrap {
    val koin = getKoin()
    val platformBridge = runCatching { koin.get<PlatformBridge>() }.getOrNull()
    val appMetadata = platformBridge?.appMetadata ?: runCatching { koin.get<AppMetadata>() }.getOrDefault(AppMetadata())

    val gameRenderBackend = koin.get<GameRenderBackend>()
    val gameSession = koin.get<GameSession>()
    gameSession.registerFrameRenderer(gameRenderBackend)

    val menuBackgroundSession = gameSession
    val menuBackgroundRenderer = gameRenderBackend
    val modRepository = koin.get<ModRepository>()
    val resourceBrowserRepository = koin.get<ResourceBrowserRepository>()
    val updateRepository = koin.get<UpdateRepository>()
    val settingsRepository = koin.get<GameSettingsRepository>()
    val actions = ActionHandlers()

    val koolCanvasSceneHost = koin.get<KoolCanvasSceneHost> {
        parametersOf("rwx-kool-canvas")
    }
    val koolCanvasScene = koolCanvasSceneHost.createScene()
    context.addScene(koolCanvasScene)

    val modHudSceneHost = koin.get<ModHudSceneHost>()
    val modHudScene = modHudSceneHost.createScene()
    modHudScene.isVisible = false
    context.addScene(modHudScene)

    val settingsModel = SettingsModel()
    settingsRepository.loadInto(settingsModel)
    if (options.colorSchemeId != ColorSchemeRegistry.defaultSchemeId) {
        settingsModel.selectedColorSchemeId.value = options.colorSchemeId
    }

    val loadingSceneHost = LoadingSceneHost(settingsModel)
    val loadingScene = loadingSceneHost.createScene(LoadingSceneHost.GAME_LOADING_SCENE_NAME)
    context.addScene(loadingScene)

    val mainMenuSceneHost = koin.get<MainMenuSceneHost> {
        parametersOf(settingsModel, { action: MainMenuAction -> actions.menu(action) })
    }
    mainMenuSceneHost.updateItems(MainMenuConditions(isDesktop = options.isDesktop))
    val mainMenuScene = mainMenuSceneHost.createScene()
    context.addScene(mainMenuScene)

    val pauseSceneHost = koin.get<PauseMenuSceneHost> {
        parametersOf(settingsModel, { action: PauseMenuAction -> actions.pause(action) })
    }
    pauseSceneHost.updateItems(PauseMenuConditions(canSave = true))
    val pauseScene = pauseSceneHost.createScene()
    context.addScene(pauseScene)

    val levelSelectSceneHost = koin.get<LevelSelectSceneHost> {
        parametersOf(
            settingsModel,
            LevelSelectActionHandler { actions.levelSelect(it) },
        )
    }
    val levelSelectViewModelFactory = koin.get<LevelSelectViewModelFactory>()
    val levelSelectScene = levelSelectSceneHost.createScene()
    context.addScene(levelSelectScene)

    val replaySelectSceneHost = koin.get<ReplaySelectSceneHost> {
        parametersOf(
            settingsModel,
            ReplaySelectActionHandler { actions.replaySelect(it) },
        )
    }
    val replaySelectScene = replaySelectSceneHost.createScene()
    context.addScene(replaySelectScene)

    val settingsSceneHost = koin.get<SettingsSceneHost> {
        parametersOf(settingsModel, { action: SettingsAction -> actions.settings(action) })
    }
    val settingsScene = settingsSceneHost.createScene()
    context.addScene(settingsScene)

    val multiplayerSceneHost = koin.get<MultiplayerSceneHost> {
        parametersOf(settingsModel, { action: MultiplayerAction -> actions.multiplayer(action) })
    }
    val multiplayerScene = multiplayerSceneHost.createScene()
    context.addScene(multiplayerScene)

    val modsSceneHost = koin.get<ModsSceneHost> {
        parametersOf(settingsModel, { action: ModsAction -> actions.mods(action) })
    }
    modsSceneHost.updateMods(modRepository.listMods())
    val modsScene = modsSceneHost.createScene()
    context.addScene(modsScene)

    val resourceBrowserSceneHost = koin.get<ResourceBrowserSceneHost> {
        parametersOf(settingsModel, { action: ResourceBrowserAction -> actions.resourceBrowser(action) })
    }
    val resourceBrowserScene = resourceBrowserSceneHost.createScene()
    context.addScene(resourceBrowserScene)

    lateinit var loadingDialogScene: Scene
    val loadingDialogSceneHost = koin.get<LoadingDialogSceneHost> {
        parametersOf(settingsModel, { visible: Boolean -> loadingDialogScene.isVisible = visible })
    }
    loadingDialogScene = loadingDialogSceneHost.createScene()
    loadingDialogScene.isVisible = false
    context.addScene(loadingDialogScene)

    val battleRoomSceneHost = koin.get<BattleRoomSceneHost> {
        parametersOf(settingsModel, { action: BattleRoomAction -> actions.battleRoom(action) })
    }
    val battleRoomScene = battleRoomSceneHost.createScene()
    context.addScene(battleRoomScene)

    val modWindowSceneHost = koin.get<ModWindowSceneHost> {
        parametersOf(settingsModel, { CoreUiEventQueue.requestInGameModWindowBack() })
    }
    modWindowSceneHost.refresh()
    val modWindowScene = modWindowSceneHost.createScene()
    context.addScene(modWindowScene)

    lateinit var snackbarScene: Scene
    val snackbarSceneHost = koin.get<SnackbarSceneHost> {
        parametersOf(settingsModel, { visible: Boolean -> snackbarScene.isVisible = visible })
    }
    snackbarScene = snackbarSceneHost.createScene()
    snackbarScene.isVisible = false
    context.addScene(snackbarScene)

    lateinit var errorDialogScene: Scene
    val dialogSceneHost = koin.get<DialogSceneHost> {
        parametersOf(settingsModel, { visible: Boolean -> errorDialogScene.isVisible = visible })
    }
    errorDialogScene = dialogSceneHost.createScene()
    errorDialogScene.isVisible = false
    context.addScene(errorDialogScene)

    options.levelSelectMode?.let(levelSelectSceneHost::updateMaps)
    options.settingsPage?.let(settingsSceneHost::showPage)
    if (options.showDemoDialog) {
        dialogSceneHost.show(
            Dialog(
                title = "Connection Error",
                message = "Unable to reach the server.\nPlease check your connection and try again.",
                buttons = listOf(DialogButton(I18n.common.ok())),
            ),
        )
    }

    return AppBootstrap(
        platformBridge = platformBridge,
        appMetadata = appMetadata,
        gameSession = gameSession,
        gameRenderBackend = gameRenderBackend,
        menuBackgroundSession = menuBackgroundSession,
        menuBackgroundRenderer = menuBackgroundRenderer,
        modRepository = modRepository,
        resourceBrowserRepository = resourceBrowserRepository,
        updateRepository = updateRepository,
        settingsRepository = settingsRepository,
        actions = actions,
        settingsModel = settingsModel,
        koolCanvasSceneHost = koolCanvasSceneHost,
        koolCanvasScene = koolCanvasScene,
        modHudSceneHost = modHudSceneHost,
        modHudScene = modHudScene,
        loadingSceneHost = loadingSceneHost,
        loadingScene = loadingScene,
        mainMenuSceneHost = mainMenuSceneHost,
        mainMenuScene = mainMenuScene,
        pauseSceneHost = pauseSceneHost,
        pauseScene = pauseScene,
        levelSelectSceneHost = levelSelectSceneHost,
        levelSelectViewModelFactory = levelSelectViewModelFactory,
        levelSelectScene = levelSelectScene,
        replaySelectSceneHost = replaySelectSceneHost,
        replaySelectScene = replaySelectScene,
        settingsSceneHost = settingsSceneHost,
        settingsScene = settingsScene,
        multiplayerSceneHost = multiplayerSceneHost,
        multiplayerScene = multiplayerScene,
        modsSceneHost = modsSceneHost,
        modsScene = modsScene,
        resourceBrowserSceneHost = resourceBrowserSceneHost,
        resourceBrowserScene = resourceBrowserScene,
        loadingDialogSceneHost = loadingDialogSceneHost,
        loadingDialogScene = loadingDialogScene,
        battleRoomSceneHost = battleRoomSceneHost,
        battleRoomScene = battleRoomScene,
        modWindowSceneHost = modWindowSceneHost,
        modWindowScene = modWindowScene,
        snackbarSceneHost = snackbarSceneHost,
        snackbarScene = snackbarScene,
        dialogSceneHost = dialogSceneHost,
        dialogScene = errorDialogScene,
    )
}
