package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Scene
import io.github.rwx.ui.component.CircularLoadingDialog
import io.github.rwx.ui.component.PanelStyle
import io.github.rwx.ui.component.ProgressLoadingDialog
import io.github.rwx.ui.component.addPanelSurface
import io.github.rwx.ui.model.SettingsModel

class LoadingDialogSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onVisibilityChanged: (Boolean) -> Unit = {},
) {
    private val dialogState = mutableStateOf<LoadingDialogState?>(null)
    private var dialogSurface: UiSurface? = null
    private var suspendedState: LoadingDialogState? = null

    fun showProgress(title: String, message: String, progress: Float, onDismiss: () -> Unit = { hide() }) {
        suspendedState = null
        dialogState.value = LoadingDialogState(title, message, progress.coerceIn(0.0f, 1.0f), onDismiss)
        setVisible(true)
    }

    fun showCircular(title: String, message: String, onDismiss: () -> Unit = { hide() }) {
        suspendedState = null
        dialogState.value = LoadingDialogState(title, message, progress = null, onDismiss)
        setVisible(true)
    }

    fun updateProgress(message: String, progress: Float?) {
        val current = dialogState.value ?: return
        dialogState.value = current.copy(
            message = message.ifBlank { current.message },
            progress = progress?.coerceIn(0.0f, 1.0f),
        )
    }

    fun hide() {
        suspendedState = null
        dialogState.value = null
        setVisible(false)
    }

    /**
     * Temporarily hides the loading dialog while preserving its state so that it can be
     * restored with [restoreFromTemporaryHide] once the blocking dialog is dismissed.
     *
     * Returns `true` if a loading dialog was visible and got suspended, `false` if there
     * was nothing to suspend.
     */
    fun temporarilyHide(): Boolean {
        val current = dialogState.value ?: return false
        suspendedState = current
        dialogState.value = null
        setVisible(false)
        return true
    }

    /**
     * Restores a loading dialog previously suspended with [temporarilyHide]. Does nothing
     * if no dialog is suspended, or if it was hidden/replaced while suspended.
     */
    fun restoreFromTemporaryHide() {
        val state = suspendedState ?: return
        suspendedState = null
        dialogState.value = state
        setVisible(true)
    }

    fun createScene(): Scene = UiScene(LOADING_DIALOG_SCENE_NAME) {
        dialogSurface = addPanelSurface(PanelStyle.Dialog, "loading-dialog-panel", model) { theme ->
            val dialog = dialogState.use() ?: return@addPanelSurface
            val progress = dialog.progress
            if (progress == null) {
                CircularLoadingDialog(dialog.title, dialog.message, theme, onCancel = dialog.onDismiss)
            } else {
                ProgressLoadingDialog(dialog.title, dialog.message, progress, theme, onCancel = dialog.onDismiss)
            }
        }
        syncSurfaceInput()
    }

    private fun setVisible(visible: Boolean) {
        syncSurfaceInput()
        onVisibilityChanged(visible)
    }

    private fun syncSurfaceInput() {
        dialogSurface?.inputMode = if (dialogState.value != null) {
            UiSurface.InputCaptureMode.CaptureInsideBounds
        } else {
            UiSurface.InputCaptureMode.CaptureDisabled
        }
    }

    companion object {

        const val LOADING_DIALOG_SCENE_NAME: String = "loading-dialog"
    }
}

private data class LoadingDialogState(
    val title: String,
    val message: String,
    val progress: Float?,
    val onDismiss: () -> Unit
)
