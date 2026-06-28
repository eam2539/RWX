package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Scene
import io.github.rwx.ui.model.Dialog
import io.github.rwx.ui.component.PanelStyle
import io.github.rwx.ui.model.SettingsModel
import io.github.rwx.ui.component.addPanelSurface
import io.github.rwx.ui.component.MessageDialog


class DialogSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onVisibilityChanged: (Boolean) -> Unit = {},
) {
    private val dialogState = mutableStateOf<Dialog?>(null)

    fun show(dialog: Dialog) {
        dialogState.value = dialog
        onVisibilityChanged(true)
    }

    fun hide() {
        dialogState.value = null
        onVisibilityChanged(false)
    }

    fun createScene(): Scene = UiScene(ERROR_DIALOG_SCENE_NAME) {
        addPanelSurface(PanelStyle.Dialog, "rwx-dialog-panel", model) { theme ->
            val dialog = dialogState.use() ?: return@addPanelSurface
            MessageDialog(dialog, theme) { hide() }
        }
    }

    companion object {
        const val ERROR_DIALOG_SCENE_NAME: String = "rwx-dialog"
    }
}
