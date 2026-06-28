package io.github.rwx.ui

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.RoundRectGradientBackground
import de.fabmax.kool.modules.ui2.UiNode
import de.fabmax.kool.modules.ui2.UiRenderer
import de.fabmax.kool.util.Color

/**
 * A wrapper around Kool's [RoundRectGradientBackground]. The gradient starts at
 * [start] and, by default, expands towards the local bottom-right corner.
 */
class LinearGradientBackground(
    private val startColor: Color,
    private val endColor: Color,
    private val start: Vec2f = Vec2f.ZERO,
    private val end: Vec2f? = null,
    private val cornerRadius: Dp = Dp.ZERO,
) : UiRenderer<UiNode> {

    override fun renderUi(node: UiNode) {
        val gradientEnd = end ?: Vec2f(node.widthPx, node.heightPx)
        RoundRectGradientBackground(
            cornerRadius = cornerRadius,
            colorA = startColor,
            colorB = endColor,
            gradientCx = Dp.fromPx(start.x),
            gradientCy = Dp.fromPx(start.y),
            gradientRx = Dp.fromPx(gradientEnd.x - start.x),
            gradientRy = Dp.fromPx(gradientEnd.y - start.y),
        ).renderUi(node)
    }
}
