package io.github.rwx.render

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene

/**
 * Neutral seam for registering a [Scene] with whatever owns the Kool render loop. Desktop and
 * Android provide their own registrar implementations so [WorldSceneHost] stays platform-free.
 */
fun interface SceneRegistrar {
    fun addScene(name: String, configure: Scene.() -> Unit): Scene
}

class WorldSceneHost(
    private val cameraFactory: () -> PerspectiveCamera = { PerspectiveCamera(WORLD_CAMERA_NAME) },
    private val renderCommandBridge: WorldRenderCommandBridge = WorldRenderCommandBridge(),
) {
    private var activeScene: Scene? = null

    val unitAnchors: List<WorldSceneUnitAnchor>
        get() = renderCommandBridge.unitAnchors

    var cameraState: WorldCameraState? = null
        private set

    var viewState: WorldViewState = STARTER_VIEW
        private set

    fun createScene(): Scene = Scene(WORLD_SCENE_NAME).also(::configure)

    fun registerWith(registrar: SceneRegistrar): Scene = registrar.addScene(WORLD_SCENE_NAME) {
        configure(this)
    }

    fun applyRenderCommands(commands: List<WorldRenderCommand>): List<WorldSceneUnitAnchor> {
        val anchors = renderCommandBridge.applyCommands(commands)
        return anchors
    }

    fun applyCameraState(cameraState: WorldCameraState?) {
        this.cameraState = cameraState
    }

    /** Drives the live Kool world camera from the neutral view model. */
    fun applyViewState(viewState: WorldViewState) {
        this.viewState = viewState
        activeScene?.camera?.let { placeCamera(it) }
    }

    private fun configure(scene: Scene) {
        activeScene = scene
        scene.camera = cameraFactory().also(::placeCamera)
    }

    private fun placeCamera(camera: Camera) {
        val placement = WorldViewCameraPlacement.fromView(viewState)
        camera.setupCamera(
            position = Vec3f(placement.positionX, placement.positionY, placement.positionZ),
            lookAt = Vec3f(placement.lookAtX, placement.lookAtY, placement.lookAtZ),
        )
    }

    companion object {
        const val WORLD_SCENE_NAME: String = "rwx-world"
        const val WORLD_CAMERA_NAME: String = "rwx-world-camera"
        val STARTER_VIEW: WorldViewState = WorldViewState(centerX = 20.0f, centerY = 25.0f, zoom = 1.0f)
    }
}
