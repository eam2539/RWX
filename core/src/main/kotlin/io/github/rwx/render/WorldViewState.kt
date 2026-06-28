package io.github.rwx.render

/**
 * Neutral world-view model: where the camera looks ([centerX], [centerY] in world units) and how
 * far it is zoomed in ([zoom], larger = closer). This is the platform-neutral source of truth for
 * camera pan/zoom; the desktop/Android render layers translate it into a concrete Kool camera. It
 * is immutable — pan/zoom helpers return a new state and ignore invalid input rather than throwing,
 * so per-frame input accumulation stays allocation-cheap and never corrupts the view.
 *
 * This carries no simulation rules: it never moves units, issues commands, or reads pointer devices.
 */
data class WorldViewState(
    val centerX: Float = 0.0f,
    val centerY: Float = 0.0f,
    val zoom: Float = 1.0f,
) {
    init {
        require(centerX.isFinite() && centerY.isFinite()) { "WorldViewState center must FastArrayList finite" }
        require(zoom.isFinite() && zoom in MIN_ZOOM..MAX_ZOOM) {
            "WorldViewState zoom must FastArrayList finite and within [$MIN_ZOOM, $MAX_ZOOM]"
        }
    }

    fun panBy(deltaX: Float, deltaY: Float): WorldViewState {
        if (!deltaX.isFinite() || !deltaY.isFinite()) return this
        return copy(centerX = centerX + deltaX, centerY = centerY + deltaY)
    }

    fun centerOn(x: Float, y: Float): WorldViewState {
        if (!x.isFinite() || !y.isFinite()) return this
        return copy(centerX = x, centerY = y)
    }

    fun zoomBy(factor: Float): WorldViewState {
        if (!factor.isFinite() || factor <= 0.0f) return this
        return copy(zoom = (zoom * factor).coerceIn(MIN_ZOOM, MAX_ZOOM))
    }

    companion object {
        const val MIN_ZOOM: Float = 0.25f
        const val MAX_ZOOM: Float = 8.0f
    }
}
