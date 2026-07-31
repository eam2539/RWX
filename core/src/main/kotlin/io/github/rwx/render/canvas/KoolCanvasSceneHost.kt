package io.github.rwx.render.canvas

import de.fabmax.kool.scene.OnRenderScene
import de.fabmax.kool.scene.Scene

class KoolCanvasSceneHost(
    private val frameRenderer: KoolCanvasFrameRenderer = KoolCanvasFrameRenderer(),
    private val sceneName: String = DEFAULT_SCENE_NAME,
) : KoolCanvasRenderer {
    private var activeScene: Scene? = null

    @Volatile
    private var latestFrame: KoolCanvasFrame = EmptyFrame

    val scene: Scene?
        get() = activeScene

    fun createScene(): Scene = Scene(sceneName).also(::configure)

    override fun render(frame: KoolCanvasFrame) {
        latestFrame = frame.copy(commands = frame.commands.toList())
    }

    fun currentFrame(): KoolCanvasFrame = latestFrame

    private fun configure(scene: Scene) {
        activeScene = scene
        scene.onRenderScene += OnRenderScene {
            frameRenderer.render(scene, latestFrame)
        }
    }

    companion object {
        const val DEFAULT_SCENE_NAME: String = "kool-canvas"
        val EmptyFrame: KoolCanvasFrame = KoolCanvasFrame(KoolCanvasViewport(0, 0), emptyList())
    }
}
