package io.github.rwx.render

data class WorldCameraState(
    val viewpointXSnapped: Float,
    val viewpointYSnapped: Float,
    val zoom: Float,
    val viewportWidthPixels: Float,
    val viewportHeightPixels: Float,
) {
    init {
        require(isUsableCameraState()) {
            "WorldCameraState requires finite viewpoints, positive finite zoom, and positive finite viewport dimensions"
        }
    }

    fun toWorldPoint(viewportX: Float, viewportY: Float): WorldCameraPoint? {
        if (!viewportX.isFinite() || !viewportY.isFinite()) {
            return null
        }
        if (viewportX !in 0.0f..viewportWidthPixels) {
            return null
        }
        if (viewportY !in 0.0f..viewportHeightPixels) {
            return null
        }

        return WorldCameraPoint(
            x = viewportX / zoom + viewpointXSnapped,
            y = viewportY / zoom + viewpointYSnapped,
        )
    }

    private fun isUsableCameraState(): Boolean =
        viewpointXSnapped.isFinite() &&
                viewpointYSnapped.isFinite() &&
                zoom.isFinite() &&
                zoom > 0.0f &&
                viewportWidthPixels.isFinite() &&
                viewportWidthPixels > 0.0f &&
                viewportHeightPixels.isFinite() &&
                viewportHeightPixels > 0.0f

    companion object {
        fun orNull(
            viewpointXSnapped: Float,
            viewpointYSnapped: Float,
            zoom: Float,
            viewportWidthPixels: Float,
            viewportHeightPixels: Float,
        ): WorldCameraState? {
            if (!viewpointXSnapped.isFinite() || !viewpointYSnapped.isFinite()) {
                return null
            }
            if (!zoom.isFinite() || zoom <= 0.0f) {
                return null
            }
            if (!viewportWidthPixels.isFinite() || viewportWidthPixels <= 0.0f) {
                return null
            }
            if (!viewportHeightPixels.isFinite() || viewportHeightPixels <= 0.0f) {
                return null
            }

            return WorldCameraState(
                viewpointXSnapped = viewpointXSnapped,
                viewpointYSnapped = viewpointYSnapped,
                zoom = zoom,
                viewportWidthPixels = viewportWidthPixels,
                viewportHeightPixels = viewportHeightPixels,
            )
        }
    }
}

data class WorldCameraPoint(
    val x: Float,
    val y: Float,
)
