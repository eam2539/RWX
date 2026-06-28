package io.github.rwx.slick

import io.github.rwx.render.GameRenderBackend
import io.github.rwx.render.canvas.KoolCanvasFrame
import io.github.rwx.render.canvas.KoolCanvasViewport

class SlickEmbeddedGameBackend(
    private val gameSession: SlickGameSession,
) : GameRenderBackend {
    override val id: String = "slick"

    override fun updateFrame(
        viewport: KoolCanvasViewport,
        deltaSeconds: Float,
        drainVisibleLayerBuffers: Boolean,
    ): KoolCanvasFrame =
        gameSession.updateFrame(viewport, deltaSeconds, drainVisibleLayerBuffers)

    override fun currentFrame(): KoolCanvasFrame =
        gameSession.currentFrame()
}
