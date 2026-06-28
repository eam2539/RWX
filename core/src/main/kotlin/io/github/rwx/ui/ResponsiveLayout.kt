package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.UiScope

private val DefaultHorizontalMargin: Dp = Dp(96f)

internal fun UiScope.ResponsiveContentWidth(
    defaultWidth: Dp,
    minWidth: Dp,
    maxWidth: Dp,
    horizontalMargin: Dp = DefaultHorizontalMargin,
): Dp {
    val viewportWidthDp = Dp.fromPx(surface.viewportWidth.use()).value
    if (viewportWidthDp <= 0f) {
        return defaultWidth
    }
    return Dp((viewportWidthDp - horizontalMargin.value).coerceIn(minWidth.value, maxWidth.value))
}

internal fun UiScope.ResponsiveViewportHeight(
    defaultHeight: Dp,
    minHeight: Dp,
    maxHeight: Dp,
    verticalChrome: Dp,
): Dp {
    val viewportHeightDp = Dp.fromPx(surface.viewportHeight.use()).value
    if (viewportHeightDp <= 0f) {
        return defaultHeight
    }
    return Dp((viewportHeightDp - verticalChrome.value).coerceIn(minHeight.value, maxHeight.value))
}

internal fun Dp.remainingAfter(used: Dp, minWidth: Dp = Dp(120f)): Dp =
    Dp((value - used.value).coerceAtLeast(minWidth.value))

internal fun Dp.fraction(
    fraction: Float,
    minWidth: Dp,
    maxWidth: Dp,
): Dp = Dp((value * fraction).coerceIn(minWidth.value, maxWidth.value))

internal fun Dp.splitEvenly(
    count: Int,
    totalGap: Dp = Dp(0f),
    minWidth: Dp = Dp(96f),
    maxWidth: Dp = this,
): Dp {
    if (count <= 0) {
        return minWidth
    }
    return Dp(((value - totalGap.value) / count).coerceIn(minWidth.value, maxWidth.value))
}
