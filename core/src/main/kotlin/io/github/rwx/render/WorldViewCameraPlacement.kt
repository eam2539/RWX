package io.github.rwx.render

/**
 * Pure translation from the neutral [WorldViewState] to a concrete perspective-camera placement
 * (position + lookAt) for the Kool world scene. The camera looks at the view center on the ground
 * plane and sits at a fixed pitch behind/above it; higher zoom pulls the camera proportionally
 * closer to the ground. The base offset is calibrated so that the starter framing
 * `(centerX=20, centerY=25, zoom=1)` reproduces the previous hardcoded camera exactly.
 *
 * This is camera-agnostic float math with no Kool types, so it is fully unit-testable; the host
 * turns it into an actual `setupCamera(...)` call.
 */
data class WorldViewCameraPlacement(
    val positionX: Float,
    val positionY: Float,
    val positionZ: Float,
    val lookAtX: Float,
    val lookAtY: Float,
    val lookAtZ: Float,
) {
    companion object {
        private const val BASE_OFFSET_Y: Float = -95.0f
        private const val BASE_OFFSET_Z: Float = 65.0f

        fun fromView(view: WorldViewState): WorldViewCameraPlacement = WorldViewCameraPlacement(
            positionX = view.centerX,
            positionY = view.centerY + BASE_OFFSET_Y / view.zoom,
            positionZ = BASE_OFFSET_Z / view.zoom,
            lookAtX = view.centerX,
            lookAtY = view.centerY,
            lookAtZ = 0.0f,
        )
    }
}
