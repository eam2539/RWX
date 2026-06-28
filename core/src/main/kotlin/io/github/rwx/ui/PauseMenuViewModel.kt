package io.github.rwx.ui

import io.github.rwx.i18n.I18n
import io.github.rwx.i18n.I18nText


enum class PauseMenuAction(val text: I18nText) {
    Resume(I18n.pausemenu.resume),
    Save(I18n.pausemenu.save),
    Settings(I18n.pausemenu.settings),
    Chat(I18n.pausemenu.chat),
    Players(I18n.pausemenu.players),
    Surrender(I18n.pausemenu.surrender),
    ExitGame(I18n.pausemenu.exitGame),
}

/** Conditions that drive which pause-menu buttons are shown. */
data class PauseMenuConditions(
    val canSave: Boolean = false,
    val isMultiplayer: Boolean = false,
)

/** One rendered pause-menu entry. */
data class PauseMenuItem(
    val action: PauseMenuAction,
    val label: String,
)

object PauseMenuViewModel {
    fun items(conditions: PauseMenuConditions): List<PauseMenuItem> = buildList {
        add(PauseMenuAction.Resume)
        if (conditions.canSave) add(PauseMenuAction.Save)
        add(PauseMenuAction.Settings)
        if (conditions.isMultiplayer) {
            add(PauseMenuAction.Chat)
            add(PauseMenuAction.Players)
        }
        add(PauseMenuAction.Surrender)
        add(PauseMenuAction.ExitGame)
    }.map { action -> PauseMenuItem(action = action, label = action.text()) }
}

sealed interface PauseMenuOutcome {
    data class Navigate(val screen: AppScreen) : PauseMenuOutcome
    data object SaveGame : PauseMenuOutcome
    data object OpenSettings : PauseMenuOutcome
    data object Chat : PauseMenuOutcome
    data object Players : PauseMenuOutcome
    data object Surrender : PauseMenuOutcome
    data object ExitGame : PauseMenuOutcome
}

object PauseMenuNavigation {
    fun outcomeFor(action: PauseMenuAction): PauseMenuOutcome = when (action) {
        PauseMenuAction.Resume -> PauseMenuOutcome.Navigate(AppScreen.InGame)
        PauseMenuAction.Save -> PauseMenuOutcome.SaveGame
        PauseMenuAction.Settings -> PauseMenuOutcome.OpenSettings
        PauseMenuAction.Chat -> PauseMenuOutcome.Chat
        PauseMenuAction.Players -> PauseMenuOutcome.Players
        PauseMenuAction.Surrender -> PauseMenuOutcome.Surrender
        PauseMenuAction.ExitGame -> PauseMenuOutcome.ExitGame
    }
}
