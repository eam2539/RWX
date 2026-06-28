package io.github.rwx

import io.github.rwx.render.GameRenderBackend
import io.github.rwx.render.canvas.KoolCanvasFrame
import io.github.rwx.render.canvas.KoolCanvasViewport

internal class AndroidExternalGameRenderBackend(
    private val gameSession: AndroidGameSession,
) : GameRenderBackend {
    override val id: String
        get() = gameSession.backendId

    override fun updateFrame(
        viewport: KoolCanvasViewport,
        deltaSeconds: Float,
        drainVisibleLayerBuffers: Boolean,
    ): KoolCanvasFrame =
        gameSession.updateFrame(viewport, deltaSeconds, drainVisibleLayerBuffers)

    override fun currentFrame(): KoolCanvasFrame =
        gameSession.currentFrame()
}
