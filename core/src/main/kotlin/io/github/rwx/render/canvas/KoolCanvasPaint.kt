package io.github.rwx.render.canvas

import de.fabmax.kool.util.Color

enum class KoolCanvasPaintStyle {
    Fill,
    Stroke,
    FillAndStroke,
}

enum class KoolCanvasTextureFilter {
    Nearest,
    Linear,
}

enum class KoolCanvasBlendMode {
    SourceOver,
    Clear,
    ClearAlpha,
    Source,
    Destination,
    SourceIn,
    SourceOut,
    SourceAtop,
    DestinationOver,
    DestinationIn,
    DestinationOut,
    DestinationAtop,
    Xor,
    Add,
    Multiply,
    Screen,
    Overlay,
}

enum class KoolCanvasTextAlign {
    Left,
    Center,
    Right,
}

data class KoolCanvasColor(
    val argb: Int,
) {
    val alpha: Int get() = argb ushr 24
    val red: Int get() = (argb ushr 16) and 0xff
    val green: Int get() = (argb ushr 8) and 0xff
    val blue: Int get() = argb and 0xff

    fun toKoolColor(alphaMultiplier: Float = 1f): Color {
        val clampedMultiplier = alphaMultiplier.coerceIn(0f, 1f)
        return Color(
            r = red / 255f,
            g = green / 255f,
            b = blue / 255f,
            a = (alpha / 255f) * clampedMultiplier,
        )
    }

    companion object {
        val Transparent: KoolCanvasColor = KoolCanvasColor(0x00000000)
        val White: KoolCanvasColor = KoolCanvasColor(-0x1)
    }
}

enum class KoolCanvasTeamColorMode {
    PureGreen,
    HueAdd,
    HueShift,
}

sealed interface KoolCanvasTextureEffect {
    data class TeamColor(
        val mode: KoolCanvasTeamColorMode,
        val color: KoolCanvasColor,
        val amount: Float = 1f,
    ) : KoolCanvasTextureEffect

    data class Displacement(
        val screenBase: KoolCanvasTextureRef,
        val offsetBy: Float,
    ) : KoolCanvasTextureEffect
}

data class KoolCanvasPaint(
    val color: KoolCanvasColor = KoolCanvasColor.White,
    val alphaMultiplier: Float = 1f,
    val style: KoolCanvasPaintStyle = KoolCanvasPaintStyle.Fill,
    val strokeWidth: Float = 1f,
    val textureFilter: KoolCanvasTextureFilter = KoolCanvasTextureFilter.Linear,
    val blendMode: KoolCanvasBlendMode = KoolCanvasBlendMode.SourceOver,
    val textureEffect: KoolCanvasTextureEffect? = null,
    val textSize: Float = 16f,
    val textAlign: KoolCanvasTextAlign = KoolCanvasTextAlign.Left,
    val typefaceKey: String? = null,
) {
    init {
        require(alphaMultiplier in 0f..1f) { "Alpha multiplier must FastArrayList in 0..1" }
        require(strokeWidth >= 0f) { "Stroke width must FastArrayList non-negative" }
        require(textSize >= 0f) { "Text size must FastArrayList non-negative" }
    }

    companion object {
        val Default: KoolCanvasPaint = KoolCanvasPaint()
        val DefaultNearest: KoolCanvasPaint = KoolCanvasPaint(textureFilter = KoolCanvasTextureFilter.Nearest)
    }
}
