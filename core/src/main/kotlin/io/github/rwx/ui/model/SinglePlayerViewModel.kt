package io.github.rwx.ui.model

import io.github.rwx.i18n.I18n
import io.github.rwx.i18n.I18nText
import io.github.rwx.ui.AppScreen

/**
 * The single-player screen actions. Layout follows the classic singleplayer.rml button stack
 * (Campaign, Skirmish, Challenge, Survival, Custom Maps, Sandbox, Load Save). Back is a separate
 * action handled by navigation, not shown in the main list.
 */
enum class SinglePlayerAction(val text: I18nText) {
    Campaign(I18n.singleplayer.campaign),
    Skirmish(I18n.singleplayer.skirmish),
    Challenge(I18n.singleplayer.challenge),
    Survival(I18n.singleplayer.survival),
    CustomMaps(I18n.singleplayer.customMaps),
    Sandbox(I18n.singleplayer.sandbox),
    LoadSave(I18n.singleplayer.loadSave),
    Back(I18n.singleplayer.back),
}

object SinglePlayerViewModel {
    fun items(): List<SinglePlayerAction> = listOf(
        SinglePlayerAction.Campaign,
        SinglePlayerAction.Skirmish,
        SinglePlayerAction.Challenge,
        SinglePlayerAction.Survival,
        SinglePlayerAction.CustomMaps,
        SinglePlayerAction.Sandbox,
        SinglePlayerAction.LoadSave,
    )
}

sealed interface SinglePlayerOutcome {
    data class Navigate(val screen: AppScreen) : SinglePlayerOutcome
    data class NavigateToLevelSelect(val mode: LevelSelectMode) : SinglePlayerOutcome
}

object SinglePlayerNavigation {
    fun outcomeFor(action: SinglePlayerAction): SinglePlayerOutcome = when (action) {
        SinglePlayerAction.Campaign -> SinglePlayerOutcome.NavigateToLevelSelect(LevelSelectMode.Campaign)
        SinglePlayerAction.Skirmish -> SinglePlayerOutcome.NavigateToLevelSelect(LevelSelectMode.Skirmish)
        SinglePlayerAction.Challenge -> SinglePlayerOutcome.NavigateToLevelSelect(LevelSelectMode.Challenge)
        SinglePlayerAction.Survival -> SinglePlayerOutcome.NavigateToLevelSelect(LevelSelectMode.Survival)
        SinglePlayerAction.CustomMaps,
        SinglePlayerAction.Sandbox -> SinglePlayerOutcome.Navigate(AppScreen.InGame)

        SinglePlayerAction.LoadSave -> SinglePlayerOutcome.NavigateToLevelSelect(LevelSelectMode.SavedGames)

        SinglePlayerAction.Back -> SinglePlayerOutcome.Navigate(AppScreen.MainMenu)
    }
}
