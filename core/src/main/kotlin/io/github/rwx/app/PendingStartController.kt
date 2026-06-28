package io.github.rwx.app

import io.github.rwx.i18n.I18n
import io.github.rwx.logger
import io.github.rwx.session.GameSession
import io.github.rwx.ui.AppScreen
import io.github.rwx.ui.Dialog
import io.github.rwx.ui.DialogButton
import io.github.rwx.ui.DialogSceneHost

internal class PendingStartController(
    private val gameSession: GameSession,
    private val dialogSceneHost: DialogSceneHost,
    private val refreshMainMenu: () -> Unit,
    private val navigateTo: (AppScreen) -> Unit,
) {
    private var mapPath: String? = null
    private var failureReturnScreen: AppScreen? = null

    val isPending: Boolean
        get() = mapPath != null

    fun currentMapPath(): String? = mapPath

    fun set(mapPath: String, failureReturnScreen: AppScreen) {
        this.mapPath = mapPath
        this.failureReturnScreen = failureReturnScreen
    }

    fun clear() {
        mapPath = null
        failureReturnScreen = null
    }

    fun drive(onLoaded: () -> Unit) {
        val pendingMapPath = mapPath ?: return
        val loadError = gameSession.mapLoadError(pendingMapPath)
        if (loadError != null) {
            val returnScreen = failureReturnScreen ?: AppScreen.MainMenu
            clear()
            logger.error(loadError) { "Unable to enter RW game for map: $pendingMapPath" }
            dialogSceneHost.show(
                Dialog(
                    title = "Map Load Error",
                    message = "Unable to load the selected map.",
                    buttons = listOf(DialogButton(I18n.common.ok())),
                ),
            )
            if (returnScreen == AppScreen.MainMenu) {
                refreshMainMenu()
            }
            navigateTo(returnScreen)
            return
        }
        if (gameSession.isMapLoaded(pendingMapPath)) {
            clear()
            onLoaded()
        }
    }
}
