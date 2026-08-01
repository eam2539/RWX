package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Scene
import io.github.rwx.ui.component.MessageDialog
import io.github.rwx.ui.component.PanelStyle
import io.github.rwx.ui.component.addPanelSurface
import io.github.rwx.ui.model.Dialog
import io.github.rwx.ui.model.SettingsModel


class DialogSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onVisibilityChanged: (Boolean) -> Unit = {},
) {
    private val dialogState = mutableStateOf<Dialog?>(null)
    private var dialogSurface: UiSurface? = null

    fun show(dialog: Dialog) {
        dialogState.value = dialog
        syncSurfaceInput()
        onVisibilityChanged(true)
    }

    fun hide() {
        dialogState.value = null
        syncSurfaceInput()
        onVisibilityChanged(false)
    }

    fun createScene(): Scene = UiScene(ERROR_DIALOG_SCENE_NAME) {
        dialogSurface = addPanelSurface(PanelStyle.Dialog, "dialog-panel", model) { theme ->
            val dialog = dialogState.use() ?: return@addPanelSurface
            MessageDialog(dialog, theme) { hide() }
        }
        syncSurfaceInput()
    }

    private fun syncSurfaceInput() {
        dialogSurface?.inputMode = if (dialogState.value != null) {
            UiSurface.InputCaptureMode.CaptureInsideBounds
        } else {
            UiSurface.InputCaptureMode.CaptureDisabled
        }
    }

    companion object {
        const val ERROR_DIALOG_SCENE_NAME: String = "dialog"
    }
}
