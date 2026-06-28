package io.github.rwx.render

import io.github.rwx.render.canvas.KoolCanvasFrame
import io.github.rwx.render.canvas.KoolCanvasViewport

interface GameRenderBackend {
    val id: String

    fun updateFrame(
        viewport: KoolCanvasViewport,
        deltaSeconds: Float,
        drainVisibleLayerBuffers: Boolean = false,
    ): KoolCanvasFrame

    fun currentFrame(): KoolCanvasFrame

    fun currentBackgroundFrame(): KoolCanvasFrame = currentFrame()
}
