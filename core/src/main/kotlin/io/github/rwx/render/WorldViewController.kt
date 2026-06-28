package io.github.rwx.render

import kotlin.math.pow

/**
 * Pure camera pan/zoom controller: maps normalized per-frame input intent into the next
 * [WorldViewState]. It owns the feel of the camera (pan speed, zoom step) but no devices — the
 * platform layer reads keyboard/scroll and passes normalized fractions here, keeping the mapping
 * testable and identical across desktop and Android.
 *
 * Pan distance scales inversely with zoom so the on-screen pan speed stays steady as the player
 * zooms in/out. Zoom multiplies by [zoomStepPerTick] per scroll tick (fractional ticks via pow).
 * Invalid or zero intent returns the same view instance so per-frame churn stays allocation-free.
 *
 * Carries no simulation rules: it never moves units, issues commands, or reads pointer devices.
 */
class WorldViewController(
    private val panSpeedWorldUnitsPerSecond: Float = 60.0f,
    private val zoomStepPerTick: Float = 1.25f,
) {
    fun advance(
        view: WorldViewState,
        panRightFraction: Float,
        panUpFraction: Float,
        zoomTicks: Float,
        deltaSeconds: Float,
    ): WorldViewState {
        if (!panRightFraction.isFinite() || !panUpFraction.isFinite() ||
            !zoomTicks.isFinite() || !deltaSeconds.isFinite() || deltaSeconds <= 0.0f
        ) {
            return view
        }

        var next = view
        if (zoomTicks != 0.0f) {
            next = next.zoomBy(zoomStepPerTick.pow(zoomTicks))
        }
        if (panRightFraction != 0.0f || panUpFraction != 0.0f) {
            val panDistance = panSpeedWorldUnitsPerSecond * deltaSeconds / next.zoom
            next = next.panBy(deltaX = panRightFraction * panDistance, deltaY = panUpFraction * panDistance)
        }
        return next
    }
}
