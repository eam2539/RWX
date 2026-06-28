package io.github.rwx.app

import de.fabmax.kool.scene.Scene
import io.github.rwx.logger
import io.github.rwx.ui.AppScreen

internal class StartupFinalizer(
    private val bootstrap: AppBootstrap,
    private val inputController: InputController,
    private val currentScreen: () -> AppScreen,
    private val multiplayerLobbyController: MultiplayerLobbyController,
    private val resourceBrowserController: ResourceBrowserController,
) {
    fun finish() {
        logPreparedComponents()
        refreshInitialScreenData()
    }

    private fun logPreparedComponents() {
        logger.info { "Starting RWX" }
        preparedScenes().forEach { scene ->
            logger.info { "Prepared ${scene.name}" }
        }
        logger.info { "Prepared ${inputController.preparedComponentName}" }
    }

    private fun preparedScenes(): List<Scene> = listOf(
        bootstrap.koolCanvasScene,
        bootstrap.loadingScene,
        bootstrap.mainMenuScene,
        bootstrap.levelSelectScene,
        bootstrap.settingsScene,
        bootstrap.pauseScene,
        bootstrap.multiplayerScene,
        bootstrap.modsScene,
        bootstrap.loadingDialogScene,
        bootstrap.battleRoomScene,
        bootstrap.snackbarScene,
        bootstrap.dialogScene,
    )

    private fun refreshInitialScreenData() {
        when (currentScreen()) {
            AppScreen.Multiplayer -> multiplayerLobbyController.requestRefresh()
            AppScreen.ResourceBrowser -> resourceBrowserController.requestSearch(append = false)
            else -> Unit
        }
    }
}
