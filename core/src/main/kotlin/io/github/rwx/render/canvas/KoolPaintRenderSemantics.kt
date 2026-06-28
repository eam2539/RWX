package io.github.rwx.render.canvas

import com.corrodinggames.rts.gameFramework.graphics.BlendMode
import com.corrodinggames.rts.gameFramework.graphics.TeamColorFilter

fun Int.applyKoolColorFilter(colorFilter: KoolColorFilter?): Int =
    when (colorFilter) {
        is KoolMultiplyAddColorFilter ->
            if (colorFilter.usesLegacyAdditiveBlend()) {
                applyLegacyLightingMultiply(colorFilter)
            } else {
                applyMultiplyAddColorFilter(colorFilter)
            }

        is KoolBlendColorFilter -> applyKoolBlendColorFilter(colorFilter)
        else -> this
    }

fun KoolColorFilter?.legacyKoolBlendMode(): KoolCanvasBlendMode? =
    when {
        this is KoolMultiplyAddColorFilter && usesLegacyAdditiveBlend() -> KoolCanvasBlendMode.Add
        this is TeamColorFilter && a == BlendMode.copy -> KoolCanvasBlendMode.Source
        this is TeamColorFilter && a == BlendMode.additive -> KoolCanvasBlendMode.Add
        else -> null
    }

fun KoolPaint?.resolvedKoolPaintColor(defaultColor: Int = -1): Int {
    val color = this?.color() ?: defaultColor
    return color.applyKoolColorFilter(this?.colorFilter())
}

fun KoolPaint?.resolvedKoolBlendMode(defaultBlendMode: KoolCanvasBlendMode = KoolCanvasBlendMode.SourceOver): KoolCanvasBlendMode =
    this?.getBlendMode() ?: this?.colorFilter().legacyKoolBlendMode() ?: defaultBlendMode

private fun Int.applyLegacyLightingMultiply(colorFilter: KoolMultiplyAddColorFilter): Int =
    argb(
        alpha = multiplyChannel(alphaComponent, colorFilter.multiplyColor.alphaComponent),
        red = multiplyChannel(redComponent, colorFilter.multiplyColor.redComponent),
        green = multiplyChannel(greenComponent, colorFilter.multiplyColor.greenComponent),
        blue = multiplyChannel(blueComponent, colorFilter.multiplyColor.blueComponent),
    )

private fun Int.applyMultiplyAddColorFilter(colorFilter: KoolMultiplyAddColorFilter): Int =
    argb(
        alpha = alphaComponent,
        red = filteredLightingChannel(
            redComponent,
            colorFilter.multiplyColor.redComponent,
            colorFilter.addColor.redComponent
        ),
        green = filteredLightingChannel(
            greenComponent,
            colorFilter.multiplyColor.greenComponent,
            colorFilter.addColor.greenComponent
        ),
        blue = filteredLightingChannel(
            blueComponent,
            colorFilter.multiplyColor.blueComponent,
            colorFilter.addColor.blueComponent
        ),
    )

private fun Int.applyKoolBlendColorFilter(colorFilter: KoolBlendColorFilter): Int =
    when (colorFilter.blendMode) {
        KoolCanvasBlendMode.Multiply -> argb(
            alpha = alphaComponent,
            red = multiplyChannel(redComponent, colorFilter.color.redComponent),
            green = multiplyChannel(greenComponent, colorFilter.color.greenComponent),
            blue = multiplyChannel(blueComponent, colorFilter.color.blueComponent),
        )

        else -> this
    }

private fun filteredLightingChannel(source: Int, multiply: Int, add: Int): Int =
    (multiplyChannel(source, multiply) + add).coerceIn(0, 255)

private fun multiplyChannel(source: Int, multiply: Int): Int = (source * multiply) / 255

private val Int.alphaComponent: Int get() = (this ushr 24) and 0xff

private val Int.redComponent: Int get() = (this ushr 16) and 0xff

private val Int.greenComponent: Int get() = (this ushr 8) and 0xff

private val Int.blueComponent: Int get() = this and 0xff

private fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int =
    ((alpha and 0xff) shl 24) or
            ((red and 0xff) shl 16) or
            ((green and 0xff) shl 8) or
            (blue and 0xff)
