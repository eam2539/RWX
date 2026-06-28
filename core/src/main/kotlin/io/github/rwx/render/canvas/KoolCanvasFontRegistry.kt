package io.github.rwx.render.canvas

import de.fabmax.kool.util.MsdfFont
import de.fabmax.kool.util.TextMetrics

object KoolCanvasFontRegistry {
    @Volatile
    private var installedBase: MsdfFont? = null

    val base: MsdfFont
        get() = installedBase ?: defaultFont()

    private val keyedFonts = mutableMapOf<String, MsdfFont>()

    fun installBaseFont(font: MsdfFont) {
        installedBase = font
    }

    fun installFont(key: String, font: MsdfFont) {
        keyedFonts[key] = font
    }

    fun clearInstalledFonts() {
        keyedFonts.clear()
    }

    fun font(sizePts: Float, key: String? = null): MsdfFont =
        (key?.let(keyedFonts::get) ?: installedBase ?: defaultFont()).derive(sizePts.coerceAtLeast(1f))

    fun lineHeight(sizePts: Float, key: String? = null): Float =
        resolvedFont(sizePts, key)
            ?.lineHeight
            ?.coerceAtLeast(sizePts.coerceAtLeast(1f))
            ?: sizePts.coerceAtLeast(1f)

    fun textWidth(text: String, sizePts: Float, key: String? = null): Float {
        if (text.isEmpty()) {
            return 0f
        }
        val font = resolvedFont(sizePts, key) ?: return text.length * sizePts.coerceAtLeast(1f) * FALLBACK_GLYPH_WIDTH
        val metrics = font.textDimensions(text, TextMetrics())
        return metrics.baselineWidth.takeIf { it > 0f }
            ?: (text.length * sizePts.coerceAtLeast(1f))
    }

    private fun resolvedFont(sizePts: Float, key: String?): MsdfFont? =
        (key?.let(keyedFonts::get) ?: installedBase ?: runCatching { MsdfFont.DEFAULT_FONT }.getOrNull())
            ?.derive(sizePts.coerceAtLeast(1f))

    private fun defaultFont(): MsdfFont =
        runCatching { MsdfFont.DEFAULT_FONT }
            .getOrElse { error("Kool default font is unavailable before Kool runtime initialization") }

    private const val FALLBACK_GLYPH_WIDTH = 0.6f
}
