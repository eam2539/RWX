package io.github.rwx.app

import io.github.rwx.session.GameSession
import io.github.rwx.ui.AppScreen

internal class ExternalGameController(
    private val gameSession: GameSession,
    private val refreshMainMenu: () -> Unit,
    private val navigateTo: (AppScreen) -> Unit,
) {
    private var externalGameWasReady = false

    fun drive(screen: AppScreen, isStartingMap: Boolean) {
        if (screen == AppScreen.InGame && !gameSession.rendersIntoKoolCanvas) {
            if (gameSession.isReadyForDisplay()) {
                externalGameWasReady = true
            }
            if (shouldReturnToMainMenuAfterExternalGameClosed(
                    isRwGameVisible = true,
                    rendersIntoKoolCanvas = false,
                    isStartingMap = isStartingMap,
                    externalGameWasReady = externalGameWasReady,
                    canResume = gameSession.canResume(),
                    mapLoadFailed = gameSession.mapLoadError() != null,
                )
            ) {
                externalGameWasReady = false
                refreshMainMenu()
                navigateTo(AppScreen.MainMenu)
            }
        } else {
            externalGameWasReady = false
        }
    }
}
