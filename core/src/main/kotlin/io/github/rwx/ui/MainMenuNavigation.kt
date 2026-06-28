package io.github.rwx.ui

import io.github.rwx.ui.model.LevelSelectMode
import io.github.rwx.ui.model.MainMenuAction

/**
 * What pressing a main-menu button should do. Keeps the navigation decision out of the menu host
 * (which is presentation only) and out of the navigator (which only holds the current screen), so
 * the policy is one pure, exhaustively-tested function.
 */
sealed interface MainMenuOutcome {
    /** Switch to [screen]. */
    data class Navigate(val screen: AppScreen) : MainMenuOutcome

    /** Open the shared map browser; [mode] is null when the host should keep its current mode. */
    data class NavigateToLevelSelect(val mode: LevelSelectMode? = null) : MainMenuOutcome

    /** Quit the application. */
    data object QuitApp : MainMenuOutcome

    /** Show the application information dialog. */
    data object ShowAboutDialog : MainMenuOutcome

}

/** Decides what each [io.github.rwx.ui.model.MainMenuAction] does. Pure and platform-neutral. */
object MainMenuNavigation {
    fun outcomeFor(action: MainMenuAction): MainMenuOutcome = when (action) {
        MainMenuAction.Continue -> MainMenuOutcome.Navigate(AppScreen.InGame)
        MainMenuAction.SinglePlayer -> MainMenuOutcome.NavigateToLevelSelect()
        MainMenuAction.WatchReplay -> MainMenuOutcome.Navigate(AppScreen.ReplaySelect)
        MainMenuAction.Sandbox -> MainMenuOutcome.Navigate(AppScreen.BattleRoom)
        MainMenuAction.Multiplayer,
        MainMenuAction.P2PMultiplayer -> MainMenuOutcome.Navigate(AppScreen.Multiplayer)

        MainMenuAction.Settings -> MainMenuOutcome.Navigate(AppScreen.Settings)
        MainMenuAction.Mods -> MainMenuOutcome.Navigate(AppScreen.Mods)
        MainMenuAction.ResourceBrowser -> MainMenuOutcome.Navigate(AppScreen.ResourceBrowser)
        MainMenuAction.About -> MainMenuOutcome.ShowAboutDialog
        MainMenuAction.Exit -> MainMenuOutcome.QuitApp
    }
}
