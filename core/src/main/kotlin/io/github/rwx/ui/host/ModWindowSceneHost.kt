package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Scene
import io.github.rwx.i18n.I18n
import io.github.rwx.mod.registry.UiRegistry
import io.github.rwx.ui.CoreUiEventQueue
import io.github.rwx.ui.ResponsiveContentWidth
import io.github.rwx.ui.component.*
import io.github.rwx.ui.model.SettingsModel

class ModWindowSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onBack: () -> Unit = { CoreUiEventQueue.requestInGameModWindowBack() },
) {
    private var revision = mutableStateOf(0)

    fun refresh() {
        revision.value++
    }

    fun createScene(): Scene = UiScene(SCENE_NAME) {
        addPanelSurface(PanelStyle.Menu, "mod-window-panel", model) { theme ->
            revision.use()
            val active = UiRegistry.activeWindow()
            val contentWidth = ResponsiveContentWidth(
                defaultWidth = Dp(860f),
                minWidth = Dp(420f),
                maxWidth = Dp(1180f),
            )
            if (active == null) {
                ScreenTitle(I18n.modWindow.emptyTitle(), theme, contentWidth)
                BodyText(I18n.modWindow.emptyMessage(), theme, contentWidth)
            } else {
                ScreenTitle(
                    active.title.resolve(I18n.currentLocale.toLanguageTag()),
                    theme,
                    contentWidth,
                )
                active.content(this, active.context, contentWidth)
            }
            BackButton(I18n.modWindow.back(), contentWidth, theme) {
                active?.context?.close() ?: onBack()
            }
        }
    }

    companion object {
        const val SCENE_NAME = "mod-window"
    }
}
