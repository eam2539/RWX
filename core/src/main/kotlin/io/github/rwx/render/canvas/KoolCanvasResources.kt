package io.github.rwx.render.canvas

import de.fabmax.kool.Assets
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Uint8Buffer

@JvmInline
value class KoolCanvasTextureId(val value: String)

@JvmInline
value class KoolCanvasRenderTargetId(val value: String)

fun interface KoolCanvasTextureResolver {
    fun resolve(texture: KoolCanvasTextureRef, filter: KoolCanvasTextureFilter): Texture2d
}

interface KoolCanvasTextureStore : KoolCanvasTextureResolver {
    fun register(id: KoolCanvasTextureId, texture: Texture2d)
    fun registerArgb(id: KoolCanvasTextureId, width: Int, height: Int, argbPixels: IntArray)
    fun registerPremultipliedArgb(id: KoolCanvasTextureId, width: Int, height: Int, argbPixels: IntArray) {
        registerArgb(id, width, height, argbPixels)
    }

    fun registerAsset(id: KoolCanvasTextureId, assetPath: String)
    fun registerFrame(id: KoolCanvasTextureId, frame: KoolCanvasFrame)
    fun unregister(id: KoolCanvasTextureId)
    fun frame(id: KoolCanvasTextureId): KoolCanvasFrame?
    fun argbImage(id: KoolCanvasTextureId): KoolCanvasArgbImage? = null
    fun argbImageView(id: KoolCanvasTextureId): KoolCanvasArgbImage? = argbImage(id)
}

interface KoolCanvasRetiredTextureReleaser {
    fun releaseRetiredTextures()
}

interface KoolCanvasContextResourceInvalidator {
    fun invalidateContextResources()
}

interface KoolCanvasFrameTextureRevisionStore {
    val frameTextureRevision: Int
}

interface KoolCanvasTextureRevisionStore {
    val textureRevision: Int
}

interface KoolCanvasFrameSnapshotRetainer {
    fun retainFrameSnapshot(id: KoolCanvasTextureId)
}

object KoolCanvasTextureRegistry :
    KoolCanvasTextureStore,
    KoolCanvasRetiredTextureReleaser,
    KoolCanvasContextResourceInvalidator,
    KoolCanvasFrameTextureRevisionStore,
    KoolCanvasTextureRevisionStore,
    KoolCanvasFrameSnapshotRetainer {
    private val registeredTextures = mutableMapOf<KoolCanvasTextureId, Texture2d>()
    private val registeredArgbImages = mutableMapOf<KoolCanvasTextureId, KoolCanvasArgbImage>()
    private val registeredAssets = mutableMapOf<KoolCanvasTextureId, String>()
    private val registeredFrames = mutableMapOf<KoolCanvasTextureId, KoolCanvasFrame>()
    private val argbTextures = mutableMapOf<Pair<KoolCanvasTextureId, KoolCanvasTextureFilter>, Texture2d>()
    private val argbTextureUploadSlots = mutableMapOf<Pair<KoolCanvasTextureId, KoolCanvasTextureFilter>, Int>()
    private val argbTextureOwnerSerials = mutableMapOf<Pair<KoolCanvasTextureId, KoolCanvasTextureFilter>, Long>()
    private val assetTextures = mutableMapOf<Pair<KoolCanvasTextureId, KoolCanvasTextureFilter>, Texture2d>()
    private val placeholderTextures = mutableMapOf<KoolCanvasTextureFilter, Texture2d>()
    private val retiringTextures = mutableListOf<Texture2d>()
    private val releasableRetiredTextures = mutableListOf<Texture2d>()
    private val retainedFrameSnapshots = mutableMapOf<KoolCanvasTextureId, Int>()
    private var frameTextureRevisionValue = 0
    private var nextArgbTextureOwnerSerial = 0L
    private var nextAssetTextureOwnerSerial = 0L

    override val frameTextureRevision: Int
        @Synchronized get() = frameTextureRevisionValue

    override val textureRevision: Int
        @Synchronized get() = frameTextureRevisionValue

    @Synchronized
    override fun register(id: KoolCanvasTextureId, texture: Texture2d) {
        registeredTextures.put(id, texture)?.takeIf { it !== texture }?.retireTexture()
        registeredArgbImages.remove(id)
        registeredAssets.remove(id)
        registeredFrames.remove(id)
        releaseCachedTextures(id)
        frameTextureRevisionValue++
    }

    @Synchronized
    override fun registerArgb(id: KoolCanvasTextureId, width: Int, height: Int, argbPixels: IntArray) {
        if (width <= 0 || height <= 0) {
            unregister(id)
            return
        }
        val uploadPixels = bleedTransparentRgb(width, height, argbPixels)
        val image = KoolCanvasArgbImage(width, height, uploadPixels, premultipliedAlpha = false)
        registeredArgbImages[id] = image
        registeredTextures.remove(id)?.retireTexture()
        registeredAssets.remove(id)
        registeredFrames.remove(id)
        releaseCachedAssetTextures(id)
        refreshCachedArgbTextures(id, image)
        frameTextureRevisionValue++
    }

    @Synchronized
    override fun registerPremultipliedArgb(id: KoolCanvasTextureId, width: Int, height: Int, argbPixels: IntArray) {
        if (width <= 0 || height <= 0) {
            unregister(id)
            return
        }
        val image = KoolCanvasArgbImage(
            width = width,
            height = height,
            pixels = sanitizePremultipliedRgb(width, height, argbPixels),
            premultipliedAlpha = true,
        )
        registeredArgbImages[id] = image
        registeredTextures.remove(id)?.retireTexture()
        registeredAssets.remove(id)
        registeredFrames.remove(id)
        releaseCachedAssetTextures(id)
        refreshCachedArgbTextures(id, image)
        frameTextureRevisionValue++
    }

    @Synchronized
    override fun registerAsset(id: KoolCanvasTextureId, assetPath: String) {
        val normalizedPath = assetPath.removePrefix("assets/").replace('\\', '/')
        if (registeredAssets[id] != normalizedPath) {
            registeredAssets[id] = normalizedPath
            registeredTextures.remove(id)?.retireTexture()
            registeredArgbImages.remove(id)
            registeredFrames.remove(id)
            releaseCachedTextures(id)
            frameTextureRevisionValue++
        }
    }

    @Synchronized
    override fun registerFrame(id: KoolCanvasTextureId, frame: KoolCanvasFrame) {
        registeredFrames[id] = frame
        registeredTextures.remove(id)?.retireTexture()
        registeredArgbImages.remove(id)
        registeredAssets.remove(id)
        releaseCachedTextures(id)
        frameTextureRevisionValue++
    }

    @Synchronized
    override fun unregister(id: KoolCanvasTextureId) {
        val retainedCount = retainedFrameSnapshots[id]
        if (retainedCount != null) {
            if (retainedCount <= 1) {
                retainedFrameSnapshots.remove(id)
            } else {
                retainedFrameSnapshots[id] = retainedCount - 1
            }
            return
        }
        registeredTextures.remove(id)?.retireTexture()
        registeredArgbImages.remove(id)
        registeredAssets.remove(id)
        registeredFrames.remove(id)
        releaseCachedTextures(id)
        frameTextureRevisionValue++
    }

    @Synchronized
    override fun retainFrameSnapshot(id: KoolCanvasTextureId) {
        if (id in registeredFrames) {
            retainedFrameSnapshots[id] = (retainedFrameSnapshots[id] ?: 0) + 1
        }
    }

    @Synchronized
    override fun releaseRetiredTextures() {
        releasableRetiredTextures.forEach { it.release() }
        releasableRetiredTextures.clear()
        releasableRetiredTextures += retiringTextures
        retiringTextures.clear()
    }

    /**
     * Drops Texture2d instances owned by the current Kool context while retaining their logical
     * sources. The context owns the old GPU handles; scheduling delayed texture releases here can
     * otherwise run after a replacement EGL context has already been created.
     */
    @Synchronized
    override fun invalidateContextResources() {
        registeredTextures.clear()
        argbTextures.clear()
        argbTextureUploadSlots.clear()
        argbTextureOwnerSerials.clear()
        assetTextures.clear()
        placeholderTextures.clear()
        retiringTextures.clear()
        releasableRetiredTextures.clear()
        frameTextureRevisionValue++
    }

    @Synchronized
    override fun frame(id: KoolCanvasTextureId): KoolCanvasFrame? = registeredFrames[id]

    @Synchronized
    override fun argbImage(id: KoolCanvasTextureId): KoolCanvasArgbImage? =
        registeredArgbImages[id]?.copyPixels()

    @Synchronized
    override fun argbImageView(id: KoolCanvasTextureId): KoolCanvasArgbImage? =
        registeredArgbImages[id]

    @Synchronized
    override fun resolve(texture: KoolCanvasTextureRef, filter: KoolCanvasTextureFilter): Texture2d =
        registeredTextures[texture.id]
            ?: registeredArgbImages[texture.id]?.let { argbTexture(texture.id, it, filter) }
            ?: registeredAssets[texture.id]?.let { assetTexture(texture.id, it, filter) }
            ?: placeholderTexture(filter)

    private fun argbTexture(
        id: KoolCanvasTextureId,
        image: KoolCanvasArgbImage,
        filter: KoolCanvasTextureFilter,
    ): Texture2d {
        val key = id to filter
        return argbTextures.getOrPut(key) {
            val ownerSerial = ++nextArgbTextureOwnerSerial
            argbTextureUploadSlots[key] = 0
            argbTextureOwnerSerials[key] = ownerSerial
            Texture2d(
                data = argbImageData(id, filter, ownerSerial, 0, image.width, image.height, image.pixels),
                mipMapping = MipMapping.Off,
                samplerSettings = samplerSettings(filter),
                name = "rwx-canvas-argb-${id.value}-$filter-$ownerSerial",
            )
        }
    }

    private fun assetTexture(
        id: KoolCanvasTextureId,
        assetPath: String,
        filter: KoolCanvasTextureFilter,
    ): Texture2d {
        val key = id to filter
        return assetTextures.getOrPut(key) {
            val ownerSerial = ++nextAssetTextureOwnerSerial
            val ownerName = "rwx-canvas-asset-${id.value}-$filter-$ownerSerial"
            Texture2d(
                format = TexFormat.RGBA,
                mipMapping = MipMapping.Off,
                samplerSettings = samplerSettings(filter),
                name = ownerName,
            ) {
                val image = Assets.defaultLoader.loadBufferedImage2d(assetPath, TexFormat.RGBA)
                    .getOrElse { placeholderImageData("$ownerName-missing") }
                BufferedImageData2d(
                    data = image.data,
                    width = image.width,
                    height = image.height,
                    format = image.format,
                    // The GL loader owns textures by ImageData.id. Asset pixels are content-keyed
                    // by default, but registry Texture2d owners have independent lifetimes.
                    id = ownerName,
                )
            }
        }
    }

    private fun placeholderTexture(filter: KoolCanvasTextureFilter): Texture2d =
        placeholderTextures.getOrPut(filter) {
            Texture2d(
                data = placeholderImageData("rwx-canvas-placeholder-$filter"),
                mipMapping = MipMapping.Off,
                samplerSettings = samplerSettings(filter),
                name = "rwx-canvas-placeholder-$filter",
            )
        }

    private fun releaseCachedTextures(id: KoolCanvasTextureId) {
        releaseCachedArgbTextures(id)
        releaseCachedAssetTextures(id)
    }

    private fun releaseCachedArgbTextures(id: KoolCanvasTextureId) {
        releaseCachedTextures(argbTextures, id)
        argbTextureUploadSlots.keys.removeAll { it.first == id }
        argbTextureOwnerSerials.keys.removeAll { it.first == id }
    }

    private fun releaseCachedAssetTextures(id: KoolCanvasTextureId) {
        releaseCachedTextures(assetTextures, id)
    }

    private fun releaseCachedTextures(
        textures: MutableMap<Pair<KoolCanvasTextureId, KoolCanvasTextureFilter>, Texture2d>,
        id: KoolCanvasTextureId,
    ) {
        val iterator = textures.iterator()
        while (iterator.hasNext()) {
            val (key, texture) = iterator.next()
            if (key.first == id) {
                texture.retireTexture()
                iterator.remove()
            }
        }
    }

    private fun refreshCachedArgbTextures(id: KoolCanvasTextureId, image: KoolCanvasArgbImage) {
        argbTextures.forEach { (key, texture) ->
            if (key.first == id && !texture.isReleased) {
                val currentSlot = argbTextureUploadSlots[key] ?: 0
                val uploadSlot = if (texture.uploadData == null) 1 - currentSlot else currentSlot
                val ownerSerial = checkNotNull(argbTextureOwnerSerials[key])
                argbTextureUploadSlots[key] = uploadSlot
                texture.uploadLazy(
                    argbImageData(id, key.second, ownerSerial, uploadSlot, image.width, image.height, image.pixels)
                )
            }
        }
    }

    private fun Texture2d.retireTexture() {
        if (!isReleased && this !in retiringTextures && this !in releasableRetiredTextures) {
            retiringTextures += this
        }
    }

    private fun samplerSettings(filter: KoolCanvasTextureFilter): SamplerSettings =
        when (filter) {
            KoolCanvasTextureFilter.Nearest -> SamplerSettings().clamped().nearest().noAnisotropy()
            KoolCanvasTextureFilter.Linear -> SamplerSettings().clamped().linear().noAnisotropy()
        }

    private fun placeholderImageData(id: String): BufferedImageData2d {
        val pixels = Uint8Buffer(16)
        val colors = intArrayOf(
            0xffff00ff.toInt(),
            0xff202020.toInt(),
            0xff202020.toInt(),
            0xffff00ff.toInt(),
        )
        colors.forEachIndexed { index, argb ->
            val offset = index * 4
            pixels[offset] = ((argb ushr 16) and 0xff).toUByte()
            pixels[offset + 1] = ((argb ushr 8) and 0xff).toUByte()
            pixels[offset + 2] = (argb and 0xff).toUByte()
            pixels[offset + 3] = ((argb ushr 24) and 0xff).toUByte()
        }
        return BufferedImageData2d(
            data = pixels,
            width = 2,
            height = 2,
            format = TexFormat.RGBA,
            id = id,
        )
    }

    private fun argbImageData(
        id: KoolCanvasTextureId,
        filter: KoolCanvasTextureFilter,
        ownerSerial: Long,
        uploadSlot: Int,
        width: Int,
        height: Int,
        argbPixels: IntArray,
    ): ImageData2d {
        val pixelCount = width * height
        val pixels = Uint8Buffer(pixelCount * 4)
        for (index in 0 until pixelCount) {
            val argb = argbPixels.getOrElse(index) { 0 }
            val alpha = (argb ushr 24) and 0xff
            val offset = index * 4
            pixels[offset] = ((argb ushr 16) and 0xff).toUByte()
            pixels[offset + 1] = ((argb ushr 8) and 0xff).toUByte()
            pixels[offset + 2] = (argb and 0xff).toUByte()
            pixels[offset + 3] = alpha.toUByte()
        }
        return BufferedImageData2d(
            data = pixels,
            width = width,
            height = height,
            format = TexFormat.RGBA,
            // Kool's GL loader caches GPU textures by ImageData.id. Each Texture2d owner and
            // filter needs its own key; alternating slots then bound updates for that owner.
            id = "rwx-canvas-argb-${id.value}-$filter-$ownerSerial-$uploadSlot",
        )
    }

    private fun bleedTransparentRgb(width: Int, height: Int, argbPixels: IntArray): IntArray {
        val pixelCount = width * height
        val result = argbPixels.copyOf(pixelCount)
        val hasBleedRgb = BooleanArray(pixelCount)
        val queue = IntArray(pixelCount)
        var readIndex = 0
        var writeIndex = 0

        for (index in 0 until pixelCount) {
            val argb = result[index]
            if ((argb ushr 24) != 0) {
                hasBleedRgb[index] = true
                queue[writeIndex++] = index
            }
        }

        while (readIndex < writeIndex) {
            val index = queue[readIndex++]
            val x = index % width
            val y = index / width
            val rgb = result[index] and 0x00ffffff

            for (dy in -1..1) {
                val sampleY = y + dy
                if (sampleY !in 0 until height) continue
                for (dx in -1..1) {
                    if (dx == 0 && dy == 0) continue
                    val sampleX = x + dx
                    if (sampleX !in 0 until width) continue

                    val sampleIndex = sampleX + (sampleY * width)
                    if (hasBleedRgb[sampleIndex] || (result[sampleIndex] ushr 24) != 0) {
                        continue
                    }

                    result[sampleIndex] = rgb
                    hasBleedRgb[sampleIndex] = true
                    queue[writeIndex++] = sampleIndex
                }
            }
        }
        return result
    }

    private fun sanitizePremultipliedRgb(width: Int, height: Int, argbPixels: IntArray): IntArray {
        val pixelCount = width * height
        val result = argbPixels.copyOf(pixelCount)
        for (index in 0 until pixelCount) {
            if ((result[index] ushr 24) == 0) {
                result[index] = 0
            }
        }
        return result
    }
}

data class KoolCanvasArgbImage(
    val width: Int,
    val height: Int,
    val pixels: IntArray,
    val premultipliedAlpha: Boolean = false,
) {
    fun copyPixels(): KoolCanvasArgbImage = copy(pixels = pixels.copyOf())
}

data class KoolCanvasTextureRef(
    val id: KoolCanvasTextureId,
    val width: Int,
    val height: Int,
    val hasAlpha: Boolean = false,
    val requiresOrderedAlpha: Boolean = false,
    val premultipliedAlpha: Boolean = false,
) {
    init {
        require(width >= 0) { "Texture width must be non-negative" }
        require(height >= 0) { "Texture height must be non-negative" }
    }

    val widthFloat: Float = width.toFloat()
    val heightFloat: Float = height.toFloat()
    val safeWidthFloat: Float = width.coerceAtLeast(1).toFloat()
    val safeHeightFloat: Float = height.coerceAtLeast(1).toFloat()
    val inverseSafeWidth: Float = 1f / safeWidthFloat
    val inverseSafeHeight: Float = 1f / safeHeightFloat
    val fullRect: KoolCanvasRect = KoolCanvasRect.fromSize(widthFloat, heightFloat)
}

data class KoolCanvasRenderTargetRef(
    val id: KoolCanvasRenderTargetId,
    val width: Int,
    val height: Int,
) {
    init {
        require(width >= 0) { "Render target width must be non-negative" }
        require(height >= 0) { "Render target height must be non-negative" }
    }
}
