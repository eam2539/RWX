package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.Row
import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.scene.Scene
import io.github.rwx.ui.component.PanelStyle
import io.github.rwx.ui.model.PauseMenuAction
import io.github.rwx.ui.model.PauseMenuConditions
import io.github.rwx.ui.model.PauseMenuItem
import io.github.rwx.ui.model.PauseMenuViewModel
import io.github.rwx.ui.model.SettingsModel
import io.github.rwx.ui.UiTheme
import io.github.rwx.ui.component.addPanelSurface
import io.github.rwx.ui.component.Icon
import io.github.rwx.ui.component.IconButton
import io.github.rwx.ui.component.TextIconButton
import io.github.rwx.ui.remainingAfter

/**
 * Renders the pause menu in pure Kool DSL: a centered button stack (Resume, Save?, Settings,
 * Surrender, Exit Game) matching the classic in-game menu layout from menus.ingame.* keys.
 * Cross-platform — kool-core common APIs only, valid on desktop and Android.
 */
class PauseMenuSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onAction: (PauseMenuAction) -> Unit = {},
) {
    var items: List<PauseMenuItem> = emptyList()
        private set

    private val menuItems = mutableStateListOf<PauseMenuItem>()

    fun updateItems(conditions: PauseMenuConditions) {
        val next = PauseMenuViewModel.items(conditions)
        if (next != items) {
            items = next
            menuItems.atomic {
                clear()
                addAll(next)
            }
        }
    }

    fun dispatch(action: PauseMenuAction) = onAction(action)

    fun createScene(): Scene = UiScene(PAUSE_SCENE_NAME) {
        addPanelSurface(PanelStyle.Pause, "rwx-pause-panel", model) { theme ->
            val actions = menuItems.use().filterNot { it.action == PauseMenuAction.Resume }
            actions.firstOrNull()?.let { item ->
                Row(width = UiTheme.Layout.menuButtonWidth, height = UiTheme.Layout.menuButtonHeight) {
                    IconButton(Icon.Back, theme) {
                        dispatch(PauseMenuAction.Resume)
                    }
                    TextIconButton(
                        label = item.label,
                        icon = item.action.pauseIcon,
                        width = UiTheme.Layout.menuButtonWidth.remainingAfter(UiTheme.Layout.iconButtonSize),
                        theme = theme,
                    ) {
                        dispatch(item.action)
                    }
                }
            }
            actions.drop(1).forEach { item ->
                TextIconButton(item.label, item.action.pauseIcon, UiTheme.Layout.menuButtonWidth, theme) {
                    dispatch(item.action)
                }
            }
        }
    }

    companion object {
        const val PAUSE_SCENE_NAME: String = "rwx-pause"
    }
}

private val PauseMenuAction.pauseIcon: Icon
    get() = when (this) {
        PauseMenuAction.Resume -> Icon.Continue
        PauseMenuAction.Save -> Icon.Save
        PauseMenuAction.Settings -> Icon.Settings
        PauseMenuAction.Chat -> Icon.Send
        PauseMenuAction.Players -> Icon.Multiplayer
        PauseMenuAction.Surrender -> Icon.Surrender
        PauseMenuAction.ExitGame -> Icon.Exit
    }
