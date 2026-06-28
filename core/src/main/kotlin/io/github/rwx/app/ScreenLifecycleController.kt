package io.github.rwx.app

import io.github.rwx.render.canvas.KoolCanvasFrame
import io.github.rwx.settings.GameSettingsRepository
import io.github.rwx.ui.AppScreen
import io.github.rwx.ui.LevelSelectAction
import io.github.rwx.ui.MainMenuAction
import io.github.rwx.ui.SettingsModel

internal class ScreenLifecycleController(
    private val screenPresenter: ScreenPresenter,
    private val externalFrame: () -> KoolCanvasFrame?,
    private val actions: ActionHandlers,
    private val battleRoomController: BattleRoomController,
    private val battleRoomLaunchController: BattleRoomLaunchController,
    private val replaySelectSceneHost: io.github.rwx.ui.ReplaySelectSceneHost,
    private val multiplayerLobbyController: MultiplayerLobbyController,
    private val modsController: ModsController,
    private val resourceBrowserController: ResourceBrowserController,
    private val modWindowSceneHost: io.github.rwx.ui.ModWindowSceneHost,
    private val settingsRepository: GameSettingsRepository,
    private val settingsModel: SettingsModel,
    private val refreshMainMenu: () -> Unit,
    autoStartSinglePlayerFromUi: Boolean,
    autoStartBattleRoom: Boolean,
) {
    private var pendingAutoStartSinglePlayerFromUi = autoStartSinglePlayerFromUi
    private var pendingAutoSelectSinglePlayerMap = false
    private var pendingAutoStartBattleRoom = autoStartBattleRoom

    fun onScreenChanged(screen: AppScreen) {
        screenPresenter.apply(screen, externalFrame())
        if (screen == AppScreen.MainMenu) {
            refreshMainMenu()
            if (pendingAutoStartSinglePlayerFromUi) {
                pendingAutoStartSinglePlayerFromUi = false
                pendingAutoSelectSinglePlayerMap = true
                actions.menu(MainMenuAction.SinglePlayer)
            }
        }
        if (screen == AppScreen.LevelSelect && pendingAutoSelectSinglePlayerMap) {
            pendingAutoSelectSinglePlayerMap = false
            pendingAutoStartBattleRoom = true
            battleRoomController.selectDefaultMap()?.let { map ->
                actions.levelSelect(LevelSelectAction.SelectMap(map))
            }
        }
        if (screen == AppScreen.ReplaySelect) replaySelectSceneHost.refresh()
        if (screen == AppScreen.Multiplayer) multiplayerLobbyController.requestRefresh()
        if (screen == AppScreen.Mods) modsController.refresh()
        if (screen == AppScreen.ResourceBrowser) {
            resourceBrowserController.requestInitialSearchIfEmpty()
        }
        if (screen == AppScreen.Settings) settingsRepository.loadInto(settingsModel)
        if (screen == AppScreen.ModWindow) modWindowSceneHost.refresh()
        if (screen == AppScreen.BattleRoom && pendingAutoStartBattleRoom) {
            pendingAutoStartBattleRoom = false
            battleRoomLaunchController.startBattleRoomGame()
        }
    }

}
