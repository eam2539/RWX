package io.github.rwx

import de.fabmax.kool.util.GeneratorGlyphSet
import de.fabmax.kool.util.MsdfFontGenerator
import de.fabmax.kool.util.MsdfMeta
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.awt.image.BufferedImage
import java.io.File
import java.nio.charset.Charset
import javax.imageio.ImageIO

/**
 * Offline MSDF baker for committed UI font atlases.
 *
 * The CJK atlas covers Latin plus common GB2312 Chinese for normal UI text. The title atlas is a
 * smaller display-font atlas for ASCII title text. Runtime code only loads the baked MSDF atlases.
 */
object MsdfFontBaker {
    private const val CJK_INPUT_TTF: String = "assets/font/NotoSansCJKsc-Regular.otf"
    private const val CJK_OUTPUT_PATH: String = "assets/font/NotoSansCJKsc-Regular"
    private const val CJK_FONT_NAME: String = "noto-cjk-sc"

    private const val TITLE_INPUT_TTF: String = "assets/font/ZenDots-Regular.ttf"
    private const val TITLE_OUTPUT_PATH: String = "assets/font/ZenDots-Regular"
    private const val TITLE_FONT_NAME: String = "zen-dots"

    private const val SOLID_MEDIAN_THRESHOLD: Int = 200
    private const val WHITESPACE_CLEAR_MARGIN: Int = 2

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        if (args.singleOrNull() == "title") {
            bakeTitleFont()
        } else {
            bakeCjkFont()
            bakeTitleFont()
        }
    }

    private suspend fun bakeCjkFont() {
        val glyphs = buildCjkGlyphSet()
        println("[MsdfFontBaker] baking ${glyphs.length} glyphs from $CJK_INPUT_TTF ...")
        MsdfFontGenerator.writeMsdfFont(
            inputTtfPath = CJK_INPUT_TTF,
            outputMsdfPath = CJK_OUTPUT_PATH,
            fontName = CJK_FONT_NAME,
            glyphsToGenerate = GeneratorGlyphSet.FromString(glyphs),
            msdfGenSize = 36,
            pxRange = 8.0,
            atlasDim = 2048,
        )
        correctAtlasSign(File("$CJK_OUTPUT_PATH.png"), File("$CJK_OUTPUT_PATH.json"))
        println("[MsdfFontBaker] wrote $CJK_OUTPUT_PATH.png and $CJK_OUTPUT_PATH.json")
    }

    private suspend fun bakeTitleFont() {
        val glyphs = buildTitleGlyphSet()
        println("[MsdfFontBaker] baking ${glyphs.length} glyphs from $TITLE_INPUT_TTF ...")
        MsdfFontGenerator.writeMsdfFont(
            inputTtfPath = TITLE_INPUT_TTF,
            outputMsdfPath = TITLE_OUTPUT_PATH,
            fontName = TITLE_FONT_NAME,
            glyphsToGenerate = GeneratorGlyphSet.FromString(glyphs),
            msdfGenSize = 72,
            pxRange = 8.0,
            atlasDim = 512,
        )
        println("[MsdfFontBaker] wrote $TITLE_OUTPUT_PATH.png and $TITLE_OUTPUT_PATH.json")
    }

    private fun correctAtlasSign(pngFile: File, metaFile: File) {
        val src = ImageIO.read(pngFile)
        val width = src.width
        val height = src.height
        val pixels = src.getRGB(0, 0, width, height, null, 0, width)
        for (i in pixels.indices) pixels[i] = pixels[i].inv()

        val meta = Json.decodeFromString<MsdfMeta>(metaFile.readText())
        meta.compactGlyphs.forEach { glyph ->
            val (left, bottom, right, top) = glyph.aB
            val rowStart = (height - top).coerceIn(0, height)
            val rowEnd = (height - bottom).coerceIn(0, height)
            val colStart = left.coerceIn(0, width)
            val colEnd = right.coerceIn(0, width)
            if (rowEnd > rowStart && colEnd > colStart &&
                isUniformlySolid(pixels, width, rowStart, rowEnd, colStart, colEnd)
            ) {
                clearWhitespaceCell(pixels, width, height, rowStart, rowEnd, colStart, colEnd)
            }
        }

        val corrected = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        corrected.setRGB(0, 0, width, height, pixels, 0, width)
        ImageIO.write(corrected, "png", pngFile)
    }

    private fun clearWhitespaceCell(
        pixels: IntArray,
        width: Int,
        height: Int,
        rowStart: Int,
        rowEnd: Int,
        colStart: Int,
        colEnd: Int,
    ) {
        val margin = WHITESPACE_CLEAR_MARGIN
        for (y in (rowStart - margin).coerceAtLeast(0) until (rowEnd + margin).coerceAtMost(height)) {
            for (x in (colStart - margin).coerceAtLeast(0) until (colEnd + margin).coerceAtMost(width)) {
                pixels[y * width + x] = 0
            }
        }
    }

    private fun isUniformlySolid(
        pixels: IntArray,
        width: Int,
        rowStart: Int,
        rowEnd: Int,
        colStart: Int,
        colEnd: Int,
    ): Boolean {
        for (y in rowStart until rowEnd) {
            for (x in colStart until colEnd) {
                val p = pixels[y * width + x]
                val r = (p ushr 16) and 0xFF
                val g = (p ushr 8) and 0xFF
                val b = p and 0xFF
                val median = maxOf(minOf(r, g), minOf(maxOf(r, g), b))
                if (median < SOLID_MEDIAN_THRESHOLD) return false
            }
        }
        return true
    }

    private fun buildCjkGlyphSet(): String = buildString {
        for (codePoint in 0x20..0x24F) append(codePoint.toChar())

        val gb2312 = Charset.forName("GB2312")
        for (lead in 0xA1..0xF7) {
            for (trail in 0xA1..0xFE) {
                val decoded = String(byteArrayOf(lead.toByte(), trail.toByte()), gb2312)
                val ch = decoded.singleOrNull() ?: continue
                if (ch.code > 0x80 && ch != '\uFFFD') append(ch)
            }
        }
    }.toSet().joinToString(separator = "")

    private fun buildTitleGlyphSet(): String = buildString {
        for (codePoint in 0x20..0x7E) append(codePoint.toChar())
    }
}