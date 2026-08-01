package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.scene.Scene
import io.github.rwx.ui.component.PanelStyle
import io.github.rwx.ui.component.Snackbar
import io.github.rwx.ui.component.addPanelSurface
import io.github.rwx.ui.model.*

class SnackbarSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onVisibilityChanged: (Boolean) -> Unit = {},
) {
    val hostState: SnackbarHostState = SnackbarHostState()
    private var snackbarSurface: UiSurface? = null

    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = if (actionLabel == null) {
            SnackbarDuration.Short
        } else {
            SnackbarDuration.Indefinite
        },
        onResult: ((SnackbarResult) -> Unit)? = null,
    ): SnackbarHandle {
        val handle = hostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration,
            onResult = onResult,
        )
        syncVisibility()
        return handle
    }

    fun showSnackbar(
        visuals: SnackbarVisuals,
        onResult: ((SnackbarResult) -> Unit)? = null,
    ): SnackbarHandle {
        val handle = hostState.showSnackbar(visuals, onResult)
        syncVisibility()
        return handle
    }

    fun dismissCurrent(): Boolean {
        val dismissed = hostState.dismissCurrent()
        syncVisibility()
        return dismissed
    }

    fun clear() {
        hostState.clear()
        syncVisibility()
    }

    fun createScene(): Scene = UiScene(SNACKBAR_SCENE_NAME) {
        onUpdate {
            if (hostState.tick()) {
                syncVisibility()
            }
        }

        snackbarSurface = addPanelSurface(
            PanelStyle.Snackbar,
            "rwx-snackbar-panel",
            model,
            backgroundColor = { null },
        ) { theme ->
            val data = hostState.currentSnackbarData.use() ?: return@addPanelSurface
            Snackbar(
                data = data,
                theme = theme,
                onAction = {
                    hostState.performAction(data)
                    syncVisibility()
                },
                onDismiss = {
                    hostState.dismiss(data)
                    syncVisibility()
                },
            )
        }
        syncSurfaceInput()
    }

    private fun syncVisibility() {
        syncSurfaceInput()
        onVisibilityChanged(hostState.isVisible)
    }

    private fun syncSurfaceInput() {
        snackbarSurface?.inputMode = if (hostState.isVisible) {
            UiSurface.InputCaptureMode.CaptureOverBackground
        } else {
            UiSurface.InputCaptureMode.CaptureDisabled
        }
    }

    companion object {
        const val SNACKBAR_SCENE_NAME: String = "rwx-snackbar"
    }
}
