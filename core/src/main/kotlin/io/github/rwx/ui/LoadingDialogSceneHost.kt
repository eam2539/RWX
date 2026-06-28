package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Scene

class LoadingDialogSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onVisibilityChanged: (Boolean) -> Unit = {},
) {
    private val dialogState = mutableStateOf<LoadingDialogState?>(null)

    fun showProgress(title: String, message: String, progress: Float) {
        dialogState.value = LoadingDialogState(title, message, progress.coerceIn(0.0f, 1.0f))
        onVisibilityChanged(true)
    }

    fun showCircular(title: String, message: String) {
        dialogState.value = LoadingDialogState(title, message, progress = null)
        onVisibilityChanged(true)
    }

    fun updateProgress(message: String, progress: Float?) {
        val current = dialogState.value ?: return
        dialogState.value = current.copy(
            message = message.ifBlank { current.message },
            progress = progress?.coerceIn(0.0f, 1.0f),
        )
    }

    fun hide() {
        dialogState.value = null
        onVisibilityChanged(false)
    }

    fun createScene(): Scene = UiScene(LOADING_DIALOG_SCENE_NAME) {
        addPanelSurface(PanelStyle.Dialog, "rwx-loading-dialog-panel", model) { theme ->
            val dialog = dialogState.use() ?: return@addPanelSurface
            val progress = dialog.progress
            if (progress == null) {
                CircularLoadingDialog(dialog.title, dialog.message, theme)
            } else {
                ProgressLoadingDialog(dialog.title, dialog.message, progress, theme)
            }
        }
    }

    companion object {
        const val LOADING_DIALOG_SCENE_NAME: String = "rwx-loading-dialog"
    }
}

private data class LoadingDialogState(
    val title: String,
    val message: String,
    val progress: Float?,
)
