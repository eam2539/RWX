package io.github.rwx.app

import io.github.rwx.ui.AppScreen
import io.github.rwx.ui.AppScreen.*
import io.github.rwx.ui.AppScreenInput

internal fun shouldShowResumeMenuBackground(screen: AppScreen, canResume: Boolean): Boolean =
    screen == MainMenu && canResume

internal fun shouldSetRwGameVisibleForScreen(screen: AppScreen): Boolean =
    screen == InGame

internal fun shouldPauseRwGameForScreen(
    screen: AppScreen,
    isResumeBackgroundVisible: Boolean,
): Boolean = screen == Paused || screen == ModWindow || isResumeBackgroundVisible

internal fun shouldShowExternalRwBackgroundSurface(
    isRwMenuBackgroundVisible: Boolean,
    isResumeBackgroundVisible: Boolean,
    rendersIntoKoolCanvas: Boolean,
    usesNativeSurfaceForResumeBackground: Boolean = false,
): Boolean =
    !rendersIntoKoolCanvas &&
            (isRwMenuBackgroundVisible ||
                    (isResumeBackgroundVisible && usesNativeSurfaceForResumeBackground))

internal fun shouldForwardKoolInputForScreen(
    screen: AppScreen,
    acceptsKoolInput: Boolean,
): Boolean =
    acceptsKoolInput && AppScreenInput.policyFor(screen).worldInteraction

internal fun shouldHandleBattleRoomAction(screen: AppScreen): Boolean =
    screen == BattleRoom


internal fun shouldStartLiveBattleRoomInPlace(
    hasLaunchConfig: Boolean,
    isHost: Boolean,
    isNetworkMultiplayer: Boolean,
    isBattleRoomLive: Boolean,
): Boolean =
    !hasLaunchConfig && isHost && (isNetworkMultiplayer || isBattleRoomLive)

internal fun shouldUseRwCanvasFrameForFrame(
    isRwGameVisible: Boolean,
    isRwMenuBackgroundVisible: Boolean,
    isResumeBackgroundVisible: Boolean,
    isRwGameLoading: Boolean,
    isLastExternalFrameBackgroundVisible: Boolean,
    rendersIntoKoolCanvas: Boolean,
): Boolean = isRwMenuBackgroundVisible ||
        isResumeBackgroundVisible ||
        isLastExternalFrameBackgroundVisible ||
        (rendersIntoKoolCanvas && (isRwGameVisible || isRwGameLoading))

internal fun shouldReturnToMainMenuAfterExternalGameClosed(
    isRwGameVisible: Boolean,
    rendersIntoKoolCanvas: Boolean,
    isStartingMap: Boolean,
    externalGameWasReady: Boolean,
    canResume: Boolean,
    mapLoadFailed: Boolean = false,
): Boolean =
    isRwGameVisible &&
            !rendersIntoKoolCanvas &&
            !isStartingMap &&
            (externalGameWasReady || mapLoadFailed) &&
            !canResume

internal fun shouldDiscardExistingGameForStart(
    startNew: Boolean,
    hasLaunchConfig: Boolean,
    rendersIntoKoolCanvas: Boolean,
    canResume: Boolean,
    canStartNewSessionInPlace: Boolean,
): Boolean =
    startNew &&
            !canStartNewSessionInPlace &&
            (!hasLaunchConfig || rendersIntoKoolCanvas || canResume)

internal enum class BattleRoomGameStartedAction {
    Ignore,
    LoadKoolGame,
    ShowExternalGame,
}

internal fun battleRoomGameStartedAction(
    currentScreen: AppScreen,
    rendersIntoKoolCanvas: Boolean,
    inProcessNetworkGameStarted: Boolean,
): BattleRoomGameStartedAction =
    when {
        currentScreen == InGame -> BattleRoomGameStartedAction.Ignore
        !inProcessNetworkGameStarted -> BattleRoomGameStartedAction.Ignore
        !rendersIntoKoolCanvas -> BattleRoomGameStartedAction.ShowExternalGame
        else -> BattleRoomGameStartedAction.LoadKoolGame
    }

internal fun mapStartFailureReturnScreen(requestedReturnScreen: AppScreen?): AppScreen =
    when (requestedReturnScreen) {
        MainMenu,
        LevelSelect,
        ReplaySelect,
        Settings,
        Multiplayer,
        Mods,
        ModWindow,
        ResourceBrowser,
        BattleRoom -> requestedReturnScreen

        Loading,
        Paused,
        InGame,
        null -> MainMenu
    }

internal enum class BackNavigationAction {
    Pause,
    ShowExitDialog,
    MainMenu,
    InGame,
}

internal fun backActionForScreen(
    screen: AppScreen,
    rendersIntoKoolCanvas: Boolean,
): BackNavigationAction =
    when (screen) {
        AppScreen.InGame -> if (rendersIntoKoolCanvas) {
            BackNavigationAction.Pause
        } else {
            BackNavigationAction.ShowExitDialog
        }

        AppScreen.Paused -> BackNavigationAction.ShowExitDialog
        else -> BackNavigationAction.MainMenu
    }
