package io.github.rwx.app

import io.github.rwx.mod.UiRegistry
import io.github.rwx.render.canvas.KoolCanvasFrame
import io.github.rwx.render.canvas.KoolCanvasViewport
import io.github.rwx.ui.AppScreen
import io.github.rwx.ui.AppScreenLayout

internal class ScreenPresenter(
    private val bootstrap: AppBootstrap,
    private val viewport: () -> KoolCanvasViewport,
) {
    fun shouldShowRwMenuBackground(screen: AppScreen): Boolean =
        screen == AppScreen.MainMenu &&
                bootstrap.settingsModel.showMainMenuBackgroundDemo.value &&
                bootstrap.menuBackgroundSession.isMenuBackgroundActive() &&
                !bootstrap.gameSession.canResume()

    fun apply(
        screen: AppScreen,
        lastExternalGameFrame: KoolCanvasFrame?,
    ) {
        val isMenuBackgroundPreparing = ensureMenuBackground(screen)
        val gameSession = bootstrap.gameSession
        val visibility = AppScreenLayout.visibilityFor(screen)
        val isRwMenuBackgroundVisible = shouldShowRwMenuBackground(screen)
        val isExternalModWindowOverlayVisible = screen == AppScreen.ModWindow && !gameSession.rendersIntoKoolCanvas
        val isExternalModHudOverlayVisible = shouldShowExternalModHudOverlay(
            hudVisible = visibility.hud,
            rendersIntoKoolCanvas = gameSession.rendersIntoKoolCanvas,
            hasActiveHudLayers = UiRegistry.hasActiveHudLayers(),
        )
        val isResumeBackgroundVisible = shouldShowResumeMenuBackground(screen, gameSession.canResume())
        val isExternalRwBackgroundVisible = shouldShowExternalRwBackgroundSurface(
            isRwMenuBackgroundVisible = isRwMenuBackgroundVisible,
            isResumeBackgroundVisible = isResumeBackgroundVisible,
            rendersIntoKoolCanvas = gameSession.rendersIntoKoolCanvas,
            usesNativeSurfaceForResumeBackground = gameSession.usesNativeSurfaceForResumeBackground,
        ) || (isMenuBackgroundPreparing && !gameSession.rendersIntoKoolCanvas)
        val isLastExternalFrameBackgroundVisible = screen == AppScreen.MainMenu &&
                !gameSession.rendersIntoKoolCanvas &&
                lastExternalGameFrame != null
        val isBattleBackgroundVisible = isRwMenuBackgroundVisible ||
                isResumeBackgroundVisible ||
                isLastExternalFrameBackgroundVisible

        bootstrap.mainMenuSceneHost.setBattleBackgroundVisible(isBattleBackgroundVisible)
        bootstrap.koolCanvasScene.isVisible = visibility.world
        bootstrap.modHudScene.isVisible = visibility.hud
        bootstrap.loadingScene.isVisible = visibility.loading

        bootstrap.mainMenuScene.isVisible = visibility.mainMenu
        bootstrap.levelSelectScene.isVisible = visibility.levelSelect
        bootstrap.replaySelectScene.isVisible = visibility.replaySelect
        bootstrap.settingsScene.isVisible = visibility.settings
        bootstrap.pauseScene.isVisible = visibility.paused
        bootstrap.multiplayerScene.isVisible = visibility.multiplayer
        bootstrap.modsScene.isVisible = visibility.mods
        bootstrap.resourceBrowserScene.isVisible = visibility.resourceBrowser
        bootstrap.battleRoomScene.isVisible = visibility.battleRoom
        bootstrap.modWindowScene.isVisible = visibility.modWindow
        gameSession.setGameVisible(
            shouldSetRwGameVisibleForScreen(screen) || isExternalRwBackgroundVisible,
            viewport(),
            koolOverlay = isExternalRwBackgroundVisible ||
                    isExternalModWindowOverlayVisible ||
                    isExternalModHudOverlayVisible,
            pausedBackground = shouldPauseRwGameForScreen(screen, isResumeBackgroundVisible),
        )
    }

    private fun ensureMenuBackground(screen: AppScreen): Boolean {
        if (!bootstrap.settingsModel.showMainMenuBackgroundDemo.value) return false
        if (screen != AppScreen.MainMenu) return false
        if (bootstrap.gameSession.canResume() || bootstrap.menuBackgroundSession.isMenuBackgroundActive()) return false
        bootstrap.menuBackgroundSession.prepareMenuBackgroundAsync(viewport())
        return true
    }
}

internal fun shouldShowExternalModHudOverlay(
    hudVisible: Boolean,
    rendersIntoKoolCanvas: Boolean,
    hasActiveHudLayers: Boolean,
): Boolean = hudVisible && !rendersIntoKoolCanvas && hasActiveHudLayers
