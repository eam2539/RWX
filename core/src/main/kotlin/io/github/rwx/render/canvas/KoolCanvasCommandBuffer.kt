package io.github.rwx.render.canvas

class KoolCanvasCommandBuffer(
    viewport: KoolCanvasViewport = KoolCanvasViewport(0, 0),
) {
    private val commands = mutableListOf<KoolCanvasCommand>()
    private val stateStack = ArrayDeque<KoolCanvasState>()

    var viewport: KoolCanvasViewport = viewport
        private set

    var state: KoolCanvasState = KoolCanvasState.Default
        private set

    fun beginFrame(viewport: KoolCanvasViewport) {
        this.viewport = viewport
        commands.clear()
        stateStack.clear()
        state = KoolCanvasState.Default
    }

    fun setRenderTarget(renderTarget: KoolCanvasRenderTargetRef?) {
        val renderTargetId = renderTarget?.id
        if (state.renderTarget == renderTargetId) {
            return
        }
        state = state.copy(renderTarget = renderTargetId)
    }

    fun save() {
        stateStack.addLast(state)
    }

    fun restore() {
        state = stateStack.removeLastOrNull() ?: KoolCanvasState.Default
    }

    fun clip(rect: KoolCanvasRect) {
        val transformedClip = state.transform.mapRectBounds(rect)
        val nextClip = state.clip?.intersect(transformedClip) ?: transformedClip
        if (state.clip == nextClip) {
            return
        }
        state = state.copy(clip = nextClip)
    }

    fun translate(dx: Float, dy: Float) {
        if (dx == 0f && dy == 0f) {
            return
        }
        state = state.copy(transform = state.transform.translate(dx, dy))
    }

    fun scale(sx: Float, sy: Float, pivotX: Float = 0f, pivotY: Float = 0f) {
        if (sx == 1f && sy == 1f) {
            return
        }
        state = state.copy(transform = state.transform.scale(sx, sy, pivotX, pivotY))
    }

    fun rotate(degrees: Float, pivotX: Float = 0f, pivotY: Float = 0f) {
        if (degrees == 0f) {
            return
        }
        state = state.copy(transform = state.transform.rotate(degrees, pivotX, pivotY))
    }

    fun clear(color: KoolCanvasColor, blendMode: KoolCanvasBlendMode = KoolCanvasBlendMode.SourceOver) {
        val renderTarget = state.renderTarget
        if (blendMode != KoolCanvasBlendMode.ClearAlpha) {
            commands.removeAll { it.renderTarget == renderTarget }
        }
        commands += KoolCanvasCommand.Clear(
            color = color,
            blendMode = blendMode,
            renderTarget = renderTarget,
        )
    }

    fun drawTexture(
        texture: KoolCanvasTextureRef,
        destination: KoolCanvasRect,
        paint: KoolCanvasPaint = KoolCanvasPaint.Default,
        source: KoolCanvasRect = texture.fullRect,
    ) {
        commands += KoolCanvasCommand.DrawTexture(
            texture = texture,
            source = source,
            destination = destination,
            paint = paint,
            state = state,
        )
    }

    fun drawRect(rect: KoolCanvasRect, paint: KoolCanvasPaint) {
        commands += KoolCanvasCommand.DrawRect(rect = rect, paint = paint, state = state)
    }

    fun drawLine(start: KoolCanvasPoint, end: KoolCanvasPoint, paint: KoolCanvasPaint) {
        commands += KoolCanvasCommand.DrawLine(start = start, end = end, paint = paint, state = state)
    }

    fun drawCircle(center: KoolCanvasPoint, radius: Float, paint: KoolCanvasPaint) {
        commands += KoolCanvasCommand.DrawCircle(center = center, radius = radius, paint = paint, state = state)
    }

    fun drawText(text: String, baseline: KoolCanvasPoint, paint: KoolCanvasPaint) {
        commands += KoolCanvasCommand.DrawText(text = text, baseline = baseline, paint = paint, state = state)
    }

    fun snapshot(): KoolCanvasFrame = KoolCanvasFrame(
        viewport = viewport,
        commands = commands.toList(),
    )

    private val KoolCanvasCommand.renderTarget: KoolCanvasRenderTargetId?
        get() = when (this) {
            is KoolCanvasCommand.Clear -> renderTarget
            is KoolCanvasCommand.DrawTexture -> state.renderTarget
            is KoolCanvasCommand.DrawRect -> state.renderTarget
            is KoolCanvasCommand.DrawLine -> state.renderTarget
            is KoolCanvasCommand.DrawCircle -> state.renderTarget
            is KoolCanvasCommand.DrawText -> state.renderTarget
        }
}
