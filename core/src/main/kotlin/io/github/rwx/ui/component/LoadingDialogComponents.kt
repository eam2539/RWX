package io.github.rwx.ui.component

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import io.github.rwx.ui.ColorSchemeDefinition
import io.github.rwx.ui.UiTheme
import kotlin.math.*

fun UiScope.ProgressLoadingDialog(
    title: String,
    message: String,
    progress: Float,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.dialogMessageWidth,
) {
    LoadingDialogTitle(title, theme, contentWidth)
    LoadingDialogMessage(message, theme, contentWidth)
    LoadingProgressBar(
        progress = progress,
        width = contentWidth,
        theme = theme,
    )
}

fun UiScope.CircularLoadingDialog(
    title: String,
    message: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.dialogMessageWidth,
) {
    LoadingDialogTitle(title, theme, contentWidth)
    CircularLoadingIndicator(
        size = LoadingDialogSpinnerSize,
        strokeWidth = LoadingDialogSpinnerStroke,
        theme = theme,
    ).modifier
        .alignX(AlignmentX.Center)
        .margin(bottom = UiTheme.Spacing.lg)
    LoadingDialogMessage(message, theme, contentWidth)
}

fun UiScope.CircularLoadingIndicator(
    size: Dp,
    strokeWidth: Dp,
    theme: ColorSchemeDefinition,
): UiScope {
    surface.onEachFrame {
        surface.triggerUpdate()
    }
    return Box(width = size, height = size) {
        modifier.background(
            CircularLoadingIndicatorRenderer(
                activeColor = theme.palette.primary,
                trackColor = theme.palette.borderSubtle.withAlpha(LoadingSpinnerTrackAlpha),
                strokeWidth = strokeWidth,
            )
        )
    }
}

fun UiScope.LoadingProgressBar(
    progress: Float,
    width: Dp,
    theme: ColorSchemeDefinition,
) {
    val trackPadding = UiTheme.Spacing.xs
    val fillWidth = Dp(
        ((width.value - trackPadding.value * 2f) * progress.coerceIn(0.0f, 1.0f))
            .coerceAtLeast(LoadingProgressInnerHeight.value)
    )

    Box(width = width, height = LoadingProgressHeight) {
        modifier
            .alignX(AlignmentX.Center)
            .background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.xs))
            .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.xs, Dp(1f)))
            .padding(trackPadding)

        Box(width = fillWidth, height = LoadingProgressInnerHeight) {
            modifier
                .align(AlignmentX.Start, AlignmentY.Center)
                .background(RoundRectBackground(theme.palette.primary, Dp(3f)))
        }
    }
}

private fun UiScope.LoadingDialogTitle(
    title: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
) {
    Text(title) {
        modifier
            .width(contentWidth)
            .margin(bottom = UiTheme.Spacing.md)
            .font(UiTheme.Fonts.headingMedium)
            .textAlign(AlignmentX.Center, AlignmentY.Center)
            .textColor(theme.palette.textPrimary)
    }
}

private fun UiScope.LoadingDialogMessage(
    message: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
) {
    Text(message) {
        modifier
            .width(contentWidth)
            .margin(bottom = UiTheme.Spacing.lg)
            .font(UiTheme.Fonts.bodySmall)
            .textAlign(AlignmentX.Center, AlignmentY.Center)
            .isWrapText(true)
            .textColor(theme.palette.textSecondary)
    }
}

private class CircularLoadingIndicatorRenderer(
    private val activeColor: Color,
    private val trackColor: Color,
    private val strokeWidth: Dp,
) : UiRenderer<UiNode> {
    override fun renderUi(node: UiNode) {
        node.apply {
            val diameter = min(widthPx, heightPx)
            if (diameter <= 0f) {
                return
            }
            val stroke = strokeWidth.px.coerceIn(1f, diameter * 0.45f)
            val radius = diameter * 0.5f - stroke * 0.5f
            val centerX = widthPx * 0.5f
            val centerY = heightPx * 0.5f
            val rotation = ((Time.gameTime * LoadingSpinnerRotationsPerSecond) % 1.0).toFloat() * TAU

            getPlainBuilder().configured(clipped = true) {
                ringArc(
                    centerX = centerX,
                    centerY = centerY,
                    radius = radius,
                    strokeWidth = stroke,
                    startAngle = 0f,
                    sweepAngle = TAU,
                    startColor = trackColor,
                    endColor = trackColor,
                )
                ringArc(
                    centerX = centerX,
                    centerY = centerY,
                    radius = radius,
                    strokeWidth = stroke,
                    startAngle = -HALF_PI + rotation,
                    sweepAngle = LoadingSpinnerSweepRadians,
                    startColor = activeColor.withAlpha(LoadingSpinnerTailAlpha),
                    endColor = activeColor,
                )
            }
        }
    }
}

private fun MeshBuilder<UiVertexLayout>.ringArc(
    centerX: Float,
    centerY: Float,
    radius: Float,
    strokeWidth: Float,
    startAngle: Float,
    sweepAngle: Float,
    startColor: Color,
    endColor: Color,
) {
    val steps = max(4, (LoadingSpinnerSegments * (sweepAngle / TAU)).roundToInt())
    for (step in 0 until steps) {
        val t0 = step / steps.toFloat()
        val t1 = (step + 1) / steps.toFloat()
        addRingSegment(
            centerX = centerX,
            centerY = centerY,
            innerRadius = radius - strokeWidth * 0.5f,
            outerRadius = radius + strokeWidth * 0.5f,
            angle0 = startAngle + sweepAngle * t0,
            angle1 = startAngle + sweepAngle * t1,
            color0 = startColor.mix(endColor, t0),
            color1 = startColor.mix(endColor, t1),
        )
    }
}

private fun MeshBuilder<UiVertexLayout>.addRingSegment(
    centerX: Float,
    centerY: Float,
    innerRadius: Float,
    outerRadius: Float,
    angle0: Float,
    angle1: Float,
    color0: Color,
    color1: Color,
) {
    val outer0 = pointOnCircle(centerX, centerY, outerRadius, angle0)
    val outer1 = pointOnCircle(centerX, centerY, outerRadius, angle1)
    val inner0 = pointOnCircle(centerX, centerY, innerRadius, angle0)
    val inner1 = pointOnCircle(centerX, centerY, innerRadius, angle1)

    val i0 = vertex {
        set(it.position, outer0.x, outer0.y, 0f)
        set(it.color, color0)
    }
    val i1 = vertex {
        set(it.position, outer1.x, outer1.y, 0f)
        set(it.color, color1)
    }
    val i2 = vertex {
        set(it.position, inner1.x, inner1.y, 0f)
        set(it.color, color1)
    }
    val i3 = vertex {
        set(it.position, inner0.x, inner0.y, 0f)
        set(it.color, color0)
    }
    addTriIndices(i0, i1, i2)
    addTriIndices(i0, i2, i3)
}

private fun pointOnCircle(centerX: Float, centerY: Float, radius: Float, angle: Float): LoadingSpinnerPoint =
    LoadingSpinnerPoint(
        x = centerX + cos(angle) * radius,
        y = centerY + sin(angle) * radius,
    )

private data class LoadingSpinnerPoint(
    val x: Float,
    val y: Float,
)

private const val LoadingSpinnerSegments: Int = 48
private const val LoadingSpinnerRotationsPerSecond: Double = 0.85
private const val LoadingSpinnerTrackAlpha: Float = 0.24f
private const val LoadingSpinnerTailAlpha: Float = 0.18f
private const val TAU: Float = (PI * 2.0).toFloat()
private const val HALF_PI: Float = (PI * 0.5).toFloat()
private val LoadingSpinnerSweepRadians: Float = (PI * 1.42).toFloat()
private val LoadingDialogSpinnerSize: Dp = Dp(44f)
private val LoadingDialogSpinnerStroke: Dp = Dp(4f)
private val LoadingProgressHeight: Dp = Dp(16f)
private val LoadingProgressInnerHeight: Dp = Dp(12f)
