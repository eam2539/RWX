package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import io.github.rwx.mod.ModUiRegistry

class ModHudSceneHost {
    fun createScene(): Scene = UiScene(SCENE_NAME) {
        var observedRevision = ModUiRegistry.hudRevision()
        lateinit var surface: UiSurface
        surface = addPanelSurface(
            name = SURFACE_NAME,
            backgroundColor = { null },
            layout = CellLayout,
            width = Grow.Std,
            height = Grow.Std,
        ) {
            ModUiRegistry.activeHudLayers().forEach { layer ->
                layer.content(this)
            }
        }
        surface.inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
        onUpdate {
            val revision = ModUiRegistry.hudRevision()
            if (revision != observedRevision) {
                observedRevision = revision
                surface.triggerUpdate()
            }
        }
    }

    companion object {
        const val SCENE_NAME = "mod-hud"
        const val SURFACE_NAME = "mod-hud-surface"
    }
}
