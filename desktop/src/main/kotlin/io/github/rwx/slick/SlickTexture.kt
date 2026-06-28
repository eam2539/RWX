package io.github.rwx.slick

import com.corrodinggames.rts.gameFramework.graphics.Texture
import org.newdawn.slick.Image
import org.newdawn.slick.ImageBuffer
import org.newdawn.slick.SlickException

class SlickTexture(
    private var image: Image?,
    private val sourceKey: String? = null,
) : Texture() {
    private var uploadedPixelRevision = getPixelRevision()

    init {
        p = image?.width ?: 1
        q = image?.height ?: 1
        updateCenter()
        sourceKey?.let(::setSourceName)
        setArgbPixelLoader(::readImagePixels)
    }

    fun image(): Image? = image

    override fun a(): String? = sourceKey ?: super.a()

    override fun a(width: Int, height: Int, copyPixels: Boolean): Texture {
        val texture = try {
            SlickTexture(Image(width.coerceAtLeast(1), height.coerceAtLeast(1)), sourceKey = null)
        } catch (error: SlickException) {
            throw RuntimeException(error)
        }
        if (copyPixels) {
            texture.j = IntArray(texture.p * texture.q)
            val copyWidth = minOf(width, p)
            val copyHeight = minOf(height, q)
            for (y in 0 until copyHeight) {
                for (x in 0 until copyWidth) {
                    texture.a(x, y, a(x, y))
                }
            }
            texture.p()
        }
        return texture
    }

    override fun p() {
        super.p()
        val pixelRevision = getPixelRevision()
        if (pixelRevision == uploadedPixelRevision) {
            return
        }
        val pixels = argbPixelsCopy ?: return
        val replacement = imageFromArgbPixels(p, q, pixels, image?.filter ?: Image.FILTER_LINEAR)
        runCatching { image?.destroy() }
        image = replacement
        uploadedPixelRevision = pixelRevision
    }

    override fun o() {
        runCatching { image?.destroy() }
        image = null
        super.o()
    }

    override fun clone(): Texture = a(p, q, true).also(::copyTextureSettingsTo)

    // The core team-coloring path needs an ARGB snapshot, while Slick stores the image on the GPU.
    private fun readImagePixels(): IntArray? {
        val sourceImage = image ?: return null
        val sourceTexture = sourceImage.texture ?: return null
        val bytesPerPixel = if (sourceTexture.hasAlpha()) 4 else 3
        val textureWidth = sourceTexture.textureWidth
        val offsetX = (sourceImage.textureOffsetX * textureWidth).toInt()
        val offsetY = (sourceImage.textureOffsetY * sourceTexture.textureHeight).toInt()
        val stepX = if (sourceImage.textureWidth < 0f) -1 else 1
        val stepY = if (sourceImage.textureHeight < 0f) -1 else 1
        val texturePixels = sourceTexture.textureData
        val pixels = IntArray(p * q)
        for (y in 0 until q) {
            val textureY = offsetY + (y * stepY)
            for (x in 0 until p) {
                val textureX = offsetX + (x * stepX)
                val offset = (textureX + textureY * textureWidth) * bytesPerPixel
                val red = texturePixels[offset].toInt() and 0xff
                val green = texturePixels[offset + 1].toInt() and 0xff
                val blue = texturePixels[offset + 2].toInt() and 0xff
                val alpha = if (bytesPerPixel == 4) texturePixels[offset + 3].toInt() and 0xff else 0xff
                pixels[x + y * p] = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
            }
        }
        return pixels
    }

    private fun imageFromArgbPixels(width: Int, height: Int, pixels: IntArray, filter: Int): Image {
        val buffer = ImageBuffer(width.coerceAtLeast(1), height.coerceAtLeast(1))
        for (y in 0 until height) {
            for (x in 0 until width) {
                val argb = pixels[x + y * width]
                buffer.setRGBA(
                    x,
                    y,
                    (argb ushr 16) and 0xff,
                    (argb ushr 8) and 0xff,
                    argb and 0xff,
                    (argb ushr 24) and 0xff,
                )
            }
        }
        return Image(buffer).also { replacement ->
            if (replacement.filter != filter) {
                replacement.setFilter(filter)
            }
        }
    }
}
