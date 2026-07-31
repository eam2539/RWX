package io.github.rwx.render.canvas

import com.corrodinggames.rts.R
import com.corrodinggames.rts.game.ColorMode
import com.corrodinggames.rts.gameFramework.graphics.*
import com.corrodinggames.rts.gameFramework.graphics.opengl.TextureAtlas
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream
import de.fabmax.kool.Assets
import de.fabmax.kool.MimeType
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.Uint8Buffer
import io.github.rwx.geometry.Rect
import io.github.rwx.geometry.RectF
import io.github.rwx.trimAssetPath
import kotlinx.coroutines.runBlocking
import java.io.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.Lock
import java.util.zip.CRC32
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Core-side GraphicsEngine implementation that converts RW draw calls into the shared Kool canvas
 * command stream.
 */
class KoolGraphicsEngine private constructor(
    private val commandBuffer: KoolCanvasCommandBuffer = KoolCanvasCommandBuffer(),
    private val targetTexture: Texture? = null,
    private val targetMode: RenderTargetMode = RenderTargetMode.DEFAULT,
    private val textureStore: KoolCanvasTextureStore = KoolCanvasTextureRegistry,
    private val targetStates: MutableMap<Texture, KoolTargetState> = IdentityHashMap(),
    private val assetBytes: (String) -> ByteArray? = ::readAssetBytesFromFileSystem,
    private val enableTextureAtlas: Boolean = false,
) : GraphicsEngine {
    override fun backendCapabilities(): GraphicsBackendCapabilities = BACKEND_CAPABILITIES

    /**
     * Kool has a separate team-color texture path, but does not execute the
     * legacy [ShaderProgram] post-processing contract. Keeping these
     * capabilities separate prevents the core from routing the Kool backend
     * through the original framebuffer/shader pipeline.
     */
    override fun supportsShaderEffects(): Boolean = false

    override fun supportsTeamShaders(): Boolean = true

    constructor(
        textureStore: KoolCanvasTextureStore = KoolCanvasTextureRegistry,
        assetBytes: (String) -> ByteArray? = ::readAssetBytesFromFileSystem,
        enableTextureAtlas: Boolean = false,
    ) : this(
        textureStore = textureStore,
        targetStates = IdentityHashMap(),
        assetBytes = assetBytes,
        enableTextureAtlas = enableTextureAtlas,
    )

    private var viewport = KoolCanvasViewport(0, 0)
    private val fallbackTexture = Texture().apply {
        p = 1
        q = 1
        updateCenter()
    }
    private var textureAtlas: TextureAtlas? = null
    private var generatedTextureSerial = 0
    private var lastCommittedFrameSnapshot: FrameSnapshot? = null
    private val pendingFrameDependencySnapshotIds = linkedSetOf<KoolCanvasTextureId>()
    private val pendingFrameDependencySnapshots = mutableMapOf<KoolCanvasTextureId, ImmediateFrameSnapshot>()
    private val pendingPixelDependencySnapshots = mutableMapOf<KoolCanvasTextureId, ImmediatePixelSnapshot>()
    private val registeredTexturePixelRevisions = mutableMapOf<KoolCanvasTextureId, TexturePixelRegistration>()
    private var directBlitActive = false

    fun beginFrame(width: Int, height: Int) {
        releasePendingImmediateFrameSnapshots()
        viewport = KoolCanvasViewport(width.coerceAtLeast(0), height.coerceAtLeast(0))
        commandBuffer.beginFrame(viewport)
    }

    fun snapshot(): KoolCanvasFrame = commandBuffer.snapshot()

    override fun b(texture: Texture?): GraphicsEngine = b(texture, RenderTargetMode.DEFAULT)

    override fun b(texture: Texture?, mode: RenderTargetMode): GraphicsEngine {
        if (texture == null) {
            return KoolGraphicsEngine(
                textureStore = textureStore,
                targetStates = targetStates,
                assetBytes = assetBytes,
                enableTextureAtlas = enableTextureAtlas,
            )
        }
        val offscreen = KoolGraphicsEngine(
            commandBuffer = KoolCanvasCommandBuffer(),
            targetTexture = texture,
            targetMode = mode,
            textureStore = textureStore,
            targetStates = targetStates,
            assetBytes = assetBytes,
            enableTextureAtlas = false,
        )
        targetStates.remove(texture)?.releaseAuxiliaryTextures(textureStore)
        targetStates[texture] = KoolTargetState(offscreen, mode)
        return offscreen.apply {
            beginFrame(texture.width().coerceAtLeast(1), texture.height().coerceAtLeast(1))
        }
    }

    override fun a(lock: Lock?) {
        lock?.lock()
    }

    override fun b(lock: Lock?) {
        lock?.unlock()
    }

    override fun a(z: Boolean) {
        directBlitActive = z
    }

    override fun f() {
        directBlitActive = false
    }

    override fun a(i: Int, i2: Int) {
        viewport = KoolCanvasViewport(i.coerceAtLeast(0), i2.coerceAtLeast(0))
    }

    override fun m(): Int = viewport.width

    override fun n(): Int = viewport.height

    override fun r(): Texture = fallbackTexture

    override fun a(i: Int): Texture = a(i, true)

    override fun a(i: Int, z: Boolean): Texture {
        val drawableName = drawableNamesById[i]
        val assetPath = drawableName?.let(::drawableAssetPath)
        val imageBytes = assetPath?.let(::readAssetBytes)
        val decodedImage = imageBytes?.let { bytes -> decodeImage(bytes, assetPath) }
        val size = decodedImage?.let { it.width to it.height }
            ?: imageBytes
                ?.inputStream()
                ?.let(::readImageSize)
            ?: DEFAULT_TEXTURE_SIZE
        return createLegacyTexture(
            width = size.first,
            height = size.second,
            sourceKey = drawableName?.let { "drawable/$it" } ?: "drawable-id/$i",
            assetPath = assetPath,
            argbPixels = decodedImage?.argbPixels,
        )
    }

    override fun a(inputStream: InputStream?, z: Boolean): Texture {
        val path = (inputStream as? AssetInputStream)?.getPath()
        val assetPath = path?.let(::assetPathForImagePath)
        val imageBytes = assetPath?.let(::readAssetBytes) ?: inputStream?.readBytes()
        val decodedImage = imageBytes?.let { bytes -> decodeImage(bytes, assetPath ?: path.orEmpty()) }
        val size = decodedImage?.let { it.width to it.height }
            ?: imageBytes?.inputStream()?.let(::readImageSize)
            ?: DEFAULT_TEXTURE_SIZE
        return createLegacyTexture(
            width = size.first,
            height = size.second,
            sourceKey = path ?: "stream/${generatedTextureSerial++}",
            assetPath = assetPath,
            argbPixels = decodedImage?.argbPixels,
        )
    }

    override fun a(i: Int, i2: Int, z: Boolean): Texture = b(i, i2, z)

    override fun b(i: Int, i2: Int, z: Boolean): Texture =
        createLegacyTexture(
            width = i,
            height = i2,
            sourceKey = "generated/${i}x$i2/${generatedTextureSerial++}",
            assetPath = null,
            hasAlpha = z,
        )

    override fun o() {
        commandBuffer.clear(KoolCanvasColor.Transparent, KoolCanvasBlendMode.ClearAlpha)
    }

    override fun p() {
        commitTargetTextureFrame()
    }

    override fun q() {
        targetTexture?.let { texture ->
            targetStates[texture]
                ?.takeIf { it.engine === this }
                ?.commitEnabled = false
        }
    }

    override fun a(shaderProgram: ShaderProgram?) = Unit

    override fun b(i: Int) {
        commandBuffer.clear(KoolCanvasColor(i))
    }

    override fun a(i: Int, mode: KoolCanvasBlendMode?) {
        commandBuffer.clear(KoolCanvasColor(i), mode ?: KoolCanvasBlendMode.SourceOver)
    }

    override fun a(rect: Rect?, paint: KoolPaint?) {
        rect?.let { commandBuffer.drawRect(it.toCanvasRect(), paint.toCanvasPaint().withDirectBlitRectBlend()) }
    }

    override fun b(rect: Rect?, paint: KoolPaint?) {
        a(rect, paint)
    }

    override fun c(rect: Rect?, paint: KoolPaint?) {
        rect?.let {
            commandBuffer.drawRect(
                KoolCanvasRect(
                    left = it.a.toFloat(),
                    top = it.b.toFloat(),
                    right = (it.a + it.c).toFloat(),
                    bottom = (it.b + it.d).toFloat(),
                ),
                paint.toCanvasPaint().withDirectBlitRectBlend(),
            )
        }
    }

    override fun a(rectF: RectF?, paint: KoolPaint?) {
        rectF?.let { commandBuffer.drawRect(it.toCanvasRect(), paint.toCanvasPaint().withDirectBlitRectBlend()) }
    }

    override fun a(rect: Rect?) {
        if (rect == null) {
            return
        }
        commandBuffer.clip(rect.toCanvasRect())
    }

    override fun a(rectF: RectF?) {
        if (rectF == null) {
            return
        }
        commandBuffer.clip(rectF.toCanvasRect())
    }

    override fun a(f: Float, f2: Float, f3: Float, paint: KoolPaint?) {
        commandBuffer.drawCircle(KoolCanvasPoint(f, f2), f3, paint.toCanvasPaint())
    }

    override fun b(f: Float, f2: Float, f3: Float, paint: KoolPaint?) {
        a(f, f2, f3, paint)
    }

    override fun a(f: Float, f2: Float, f3: Float, f4: Float, paint: KoolPaint?) {
        commandBuffer.drawLine(KoolCanvasPoint(f, f2), KoolCanvasPoint(f3, f4), paint.toCanvasPaint())
    }

    override fun a(fArr: FloatArray?, i: Int, i2: Int, paint: KoolPaint?) {
        if (fArr == null || i2 <= 0) {
            return
        }
        val end = (i + i2).coerceAtMost(fArr.size - 1)
        var index = i.coerceAtLeast(0)
        val pointPaint = paint.toCanvasPaint().copy(style = KoolCanvasPaintStyle.Fill)
        val size = pointPaint.strokeWidth.coerceAtLeast(1f)
        while (index + 1 <= end) {
            val x = fArr[index]
            val y = fArr[index + 1]
            commandBuffer.drawRect(
                KoolCanvasRect(x, y, x + size, y + size),
                pointPaint,
            )
            index += 2
        }
    }

    override fun i() {
        commandBuffer.save()
    }

    override fun j() {
        commandBuffer.restore()
    }

    override fun k() {
        commandBuffer.save()
    }

    override fun l() {
        commandBuffer.restore()
    }

    override fun a(f: Float, f2: Float, f3: Float) {
        commandBuffer.rotate(f, f2, f3)
    }

    override fun a(f: Float, f2: Float) {
        commandBuffer.scale(f, f2)
    }

    override fun a(f: Float, f2: Float, f3: Float, f4: Float) {
        commandBuffer.scale(f, f2, f3, f4)
    }

    override fun b(f: Float, f2: Float) {
        commandBuffer.translate(f, f2)
    }

    override fun a(str: String?, f: Float, f2: Float, paint: KoolPaint?) {
        commandBuffer.drawText(str.orEmpty(), KoolCanvasPoint(f, f2), paint.toCanvasPaint())
    }

    override fun a(str: String?, f: Float, f2: Float, paint: KoolPaint?, paint2: KoolPaint?, f3: Float) {
        val text = str.orEmpty()
        val width = b(text, paint)
        val height = a(text, paint)
        val left = when (paint?.textAlign()) {
            KoolPaint.Align.CENTER -> f - (width * 0.5f) - f3
            KoolPaint.Align.RIGHT -> f - width - f3
            else -> f - f3
        }
        val top = f2 - f3
        commandBuffer.drawRect(
            KoolCanvasRect(left, top, left + width + (f3 * 2f), f2 + height + f3),
            paint2.toCanvasPaint(),
        )
        a(text, f, f2 + height, paint)
    }

    override fun a(str: String?, paint: KoolPaint?): Int =
        KoolCanvasFontRegistry.lineHeight(
            sizePts = paint?.textSize()?.coerceAtLeast(1f) ?: DEFAULT_TEXT_SIZE,
            key = paint.typefaceKey(),
        ).roundToInt().coerceAtLeast(1)

    override fun b(str: String?, paint: KoolPaint?): Int =
        KoolCanvasFontRegistry.textWidth(
            text = str.orEmpty(),
            sizePts = paint?.textSize()?.coerceAtLeast(1f) ?: DEFAULT_TEXT_SIZE,
            key = paint.typefaceKey(),
        ).roundToInt().coerceAtLeast(if (str.isNullOrEmpty()) 0 else 1)

    override fun a(texture: Texture?, f: Float, f2: Float, f3: Float, paint: KoolPaint?) {
        if (texture == null) return
        k()
        a(f3 + 90f, f, f2)
        val left = f - texture.r
        val top = f2 - texture.s
        drawTexture(
            texture = texture,
            destination = KoolCanvasRect(
                left = left,
                top = top,
                right = left + texture.width(),
                bottom = top + texture.height(),
            ),
            source = texture.fullSourceRect(),
            paint = paint,
        )
        l()
    }

    override fun a(texture: Texture?, rect: Rect?, f: Float, f2: Float, f3: Float, paint: KoolPaint?) {
        if (texture == null || rect == null) return
        k()
        a(f3, f, f2)
        val halfWidth = rect.width() * 0.5f
        val halfHeight = rect.height() * 0.5f
        drawTexture(
            texture = texture,
            destination = KoolCanvasRect(
                left = f - halfWidth,
                top = f2 - halfHeight,
                right = f + halfWidth,
                bottom = f2 + halfHeight,
            ),
            source = rect.toCanvasRect(),
            paint = paint,
        )
        l()
    }

    override fun a(texture: Texture?, rect: Rect?, rect2: Rect?, paint: KoolPaint?) {
        if (texture == null || rect == null || rect2 == null) return
        drawTexture(texture, rect2.toCanvasRect(), rect.toCanvasRect(), paint)
    }

    override fun b(texture: Texture?, rect: Rect?, rect2: Rect?, paint: KoolPaint?) {
        a(texture, rect, rect2, paint)
    }

    override fun a(texture: Texture?, rect: Rect?, rectF: RectF?, paint: KoolPaint?) {
        if (texture == null || rect == null || rectF == null) return
        drawTexture(texture, rectF.toCanvasRect(), rect.toCanvasRect(), paint)
    }

    override fun a(texture: Texture?, f: Float, f2: Float, paint: KoolPaint?) {
        if (texture == null) return
        drawTexture(
            texture = texture,
            destination = KoolCanvasRect(
                left = f - texture.t,
                top = f2 - texture.u,
                right = f + texture.t,
                bottom = f2 + texture.u,
            ),
            source = texture.fullSourceRect(),
            paint = paint,
        )
    }

    override fun a(texture: Texture?, f: Float, f2: Float, paint: KoolPaint?, f3: Float, f4: Float) {
        if (texture == null) return
        k()
        b(f, f2)
        a(f4, f4)
        a(f3, f, f2)
        drawTexture(
            texture = texture,
            destination = KoolCanvasRect.fromSize(texture.width().toFloat(), texture.height().toFloat()),
            source = texture.fullSourceRect(),
            paint = paint,
        )
        l()
    }

    override fun b(texture: Texture?, f: Float, f2: Float, paint: KoolPaint?) {
        if (texture == null) return
        drawTexture(
            texture = texture,
            destination = KoolCanvasRect(f, f2, f + texture.width(), f2 + texture.height()),
            source = texture.fullSourceRect(),
            paint = paint,
        )
    }

    override fun a(texture: Texture?, rect: Rect?, paint: KoolPaint?) {
        if (texture == null || rect == null) return
        drawTiledTexture(
            texture = texture,
            destination = rect.toCanvasRect(),
            offsetX = 0f,
            offsetY = 0f,
            repeatInsetX = 0,
            repeatInsetY = 0,
            paint = paint,
        )
    }

    override fun a(texture: Texture?, rect: Rect?, paint: KoolPaint?, i: Int, i2: Int, i3: Int, i4: Int) {
        if (texture == null || rect == null) return
        drawTiledTexture(
            texture = texture,
            destination = rect.toCanvasRect(),
            offsetX = i.toFloat(),
            offsetY = i2.toFloat(),
            repeatInsetX = i3,
            repeatInsetY = i4,
            paint = paint,
        )
    }

    override fun a(texture: Texture?, rectF: RectF?, paint: KoolPaint?, f: Float, f2: Float, i: Int, i2: Int) {
        if (texture == null || rectF == null) return
        drawTiledTexture(
            texture = texture,
            destination = rectF.toCanvasRect(),
            offsetX = f,
            offsetY = f2,
            repeatInsetX = i,
            repeatInsetY = i2,
            paint = paint,
        )
    }

    override fun a(texture: Texture?, file: File?) {
        if (texture == null || file == null) return
        val resolvedTexture = texture.resolveForKool()
        val width = resolvedTexture.width().coerceAtLeast(1)
        val height = resolvedTexture.height().coerceAtLeast(1)
        val pixels = resolvedTexture.argbPixelsCopy
            ?: IntArray(width * height)
        file.parentFile?.mkdirs()
        file.writeBytes(encodePng(width, height, pixels))
    }

    private fun createLegacyTexture(
        width: Int,
        height: Int,
        sourceKey: String,
        assetPath: String?,
        argbPixels: IntArray? = null,
        hasAlpha: Boolean = false,
    ): Texture {
        val texture = KoolBackendTexture(::releaseKoolTexture).apply {
            p = width.coerceAtLeast(1)
            q = height.coerceAtLeast(1)
            m = hasAlpha || argbPixels?.any { (it ushr 24) != 0xff } == true
            setSourceName(sourceKey)
            updateCenter()
        }
        if (argbPixels != null) {
            val pixels = argbPixels.copyOf()
            texture.j = pixels
            textureStore.registerArgb(texture.toCanvasTextureId(), texture.width(), texture.height(), pixels)
        } else if (assetPath != null) {
            textureStore.registerAsset(texture.toCanvasTextureId(), assetPath)
        }
        return texture
    }

    private fun readAssetBytes(assetPath: String): ByteArray? =
        assetBytes(assetPath.trimAssetPath()) ?: readAssetBytesFromFileSystem(assetPath)

    private fun decodeImage(bytes: ByteArray, sourceName: String): DecodedImage? =
        readPngImage(bytes) ?: readPlatformImage(bytes, sourceName)

    private fun flushTargetTextureFrame(): TargetTextureCommit? {
        return targetTexture?.let { texture ->
            val id = texture.toCanvasTextureId()
            val frame = snapshot()
            if (frame.commands.isEmpty()) {
                releasePendingImmediateFrameSnapshots()
                val previous = lastCommittedFrameSnapshot ?: return@let null
                textureStore.registerFrame(id, previous.frame)
                return@let TargetTextureCommit(id, previous.auxiliaryIds)
            }
            val pixels = if (targetMode == RenderTargetMode.IMMEDIATE) {
                rasterizeTargetFrame(texture, frame, visiting = setOf(id))
            } else {
                null
            }
            if (pixels != null) {
                texture.setCommittedArgbPixels(pixels)
                texture.setPremultipliedAlpha(false)
                textureStore.registerArgb(
                    id = id,
                    width = texture.width().coerceAtLeast(1),
                    height = texture.height().coerceAtLeast(1),
                    argbPixels = pixels,
                )
                registeredTexturePixelRevisions[id] = texture.pixelRegistration()
                commandBuffer.beginFrame(frame.viewport)
                releasePendingImmediateFrameSnapshots()
                TargetTextureCommit(id, emptyList())
            } else {
                val committedFrame = frame.withPersistedTargetContents(lastCommittedFrameSnapshot?.frame)
                val snapshot = snapshotFrameDependencies(committedFrame)
                val commitSnapshot = FrameSnapshot(
                    frame = snapshot.frame,
                    auxiliaryIds = (snapshot.auxiliaryIds + pendingFrameDependencySnapshotIds).distinct(),
                )
                textureStore.registerFrame(id, commitSnapshot.frame)
                lastCommittedFrameSnapshot = commitSnapshot
                pendingFrameDependencySnapshotIds.clear()
                pendingFrameDependencySnapshots.clear()
                pendingPixelDependencySnapshots.clear()
                commandBuffer.beginFrame(frame.viewport)
                TargetTextureCommit(id, commitSnapshot.auxiliaryIds)
            }
        }
    }

    private fun KoolCanvasFrame.withPersistedTargetContents(previousFrame: KoolCanvasFrame?): KoolCanvasFrame {
        if (previousFrame == null || replacesTargetContents()) {
            return this
        }
        return copy(commands = previousFrame.commands + commands)
    }

    private fun KoolCanvasFrame.replacesTargetContents(): Boolean {
        val firstCommand = commands.firstOrNull() as? KoolCanvasCommand.Clear ?: return false
        return firstCommand.renderTarget == null && firstCommand.blendMode != KoolCanvasBlendMode.ClearAlpha
    }

    private fun snapshotFrameDependencies(
        frame: KoolCanvasFrame,
        visiting: Set<KoolCanvasTextureId> = emptySet(),
        snapshotsByTexture: MutableMap<KoolCanvasTextureId, FrameDependencySnapshot> = mutableMapOf(),
        cloneExistingSnapshots: Boolean = false,
    ): FrameSnapshot {
        val auxiliaryIds = linkedSetOf<KoolCanvasTextureId>()
        val frozenCommands = frame.commands.map { command ->
            if (command !is KoolCanvasCommand.DrawTexture || command.texture.id in visiting) {
                command
            } else {
                val nestedFrame = textureStore.frame(command.texture.id) ?: return@map command
                if (command.texture.id.isFrameSnapshotId && !cloneExistingSnapshots) {
                    auxiliaryIds += command.texture.id
                    auxiliaryIds += collectFrameSnapshotIds(
                        frame = nestedFrame,
                        visiting = visiting + command.texture.id,
                        collected = linkedSetOf(command.texture.id),
                    )
                    command
                } else {
                    val dependencySnapshot = snapshotsByTexture.getOrPut(command.texture.id) {
                        val nestedSnapshot = snapshotFrameDependencies(
                            frame = nestedFrame,
                            visiting = visiting + command.texture.id,
                            snapshotsByTexture = snapshotsByTexture,
                            cloneExistingSnapshots = cloneExistingSnapshots,
                        )
                        val snapshotId = command.texture.id.snapshotId()
                        textureStore.registerFrame(snapshotId, nestedSnapshot.frame)
                        FrameDependencySnapshot(
                            textureId = snapshotId,
                            auxiliaryIds = nestedSnapshot.auxiliaryIds + snapshotId,
                        )
                    }
                    auxiliaryIds += dependencySnapshot.auxiliaryIds
                    command.copy(texture = command.texture.copy(id = dependencySnapshot.textureId))
                }
            }
        }
        return FrameSnapshot(
            frame = frame.copy(commands = frozenCommands),
            auxiliaryIds = auxiliaryIds.toList(),
        )
    }

    private fun collectFrameSnapshotIds(
        frame: KoolCanvasFrame,
        visiting: Set<KoolCanvasTextureId>,
        collected: LinkedHashSet<KoolCanvasTextureId> = linkedSetOf(),
        remainingDepth: Int = MAX_FRAME_SNAPSHOT_DEPENDENCY_DEPTH,
    ): List<KoolCanvasTextureId> {
        if (remainingDepth <= 0) {
            return emptyList()
        }
        frame.commands.forEach { command ->
            if (command !is KoolCanvasCommand.DrawTexture || command.texture.id in visiting) {
                return@forEach
            }
            val nestedFrame = textureStore.frame(command.texture.id) ?: return@forEach
            if (command.texture.id.isFrameSnapshotId && collected.add(command.texture.id)) {
                collectFrameSnapshotIds(
                    frame = nestedFrame,
                    visiting = visiting + command.texture.id,
                    collected = collected,
                    remainingDepth = remainingDepth - 1,
                )
            }
        }
        return collected.toList()
    }

    private fun rasterizeTargetFrame(
        texture: Texture,
        frame: KoolCanvasFrame,
        visiting: Set<KoolCanvasTextureId> = emptySet(),
    ): IntArray? {
        val width = texture.width().coerceAtLeast(1)
        val height = texture.height().coerceAtLeast(1)
        val pixels = texture.argbPixelsCopy?.copyOf(width * height) ?: IntArray(width * height)
        val profile = CpuTargetProfile.createIfEnabled(texture, frame, width, height)
        val startedAt = if (profile != null) System.nanoTime() else 0L
        return rasterizeFrameCommands(frame, pixels, width, height, visiting, profile).also {
            profile?.log(System.nanoTime() - startedAt, it != null)
        }
    }

    private fun rasterizeFrameToImage(
        id: KoolCanvasTextureId,
        frame: KoolCanvasFrame,
        width: Int,
        height: Int,
        visiting: Set<KoolCanvasTextureId>,
    ): KoolCanvasArgbImage? {
        if (id in visiting) {
            return textureStore.argbImageView(id)
        }
        val pixels = IntArray(width.coerceAtLeast(1) * height.coerceAtLeast(1))
        return rasterizeFrameCommands(
            frame = frame,
            pixels = pixels,
            width = width.coerceAtLeast(1),
            height = height.coerceAtLeast(1),
            visiting = visiting + id,
            profile = null,
        )?.let {
            KoolCanvasArgbImage(
                width = width.coerceAtLeast(1),
                height = height.coerceAtLeast(1),
                pixels = it,
                premultipliedAlpha = false,
            )
        }
    }

    private fun rasterizeFrameCommands(
        frame: KoolCanvasFrame,
        pixels: IntArray,
        width: Int,
        height: Int,
        visiting: Set<KoolCanvasTextureId>,
        profile: CpuTargetProfile? = null,
    ): IntArray? {
        profile?.totalCommands = frame.commands.size
        for (command in frame.commands) {
            when (command) {
                is KoolCanvasCommand.Clear -> {
                    profile?.let { it.clearCommands += 1 }
                    if (command.renderTarget != null) return null
                    when (command.blendMode) {
                        KoolCanvasBlendMode.Clear -> fillRasterPixels(pixels, 0)
                        KoolCanvasBlendMode.ClearAlpha -> {
                            processRasterRows(0, height, width) { rowStart, rowEnd ->
                                var index = rowStart * width
                                val end = rowEnd * width
                                while (index < end) {
                                    pixels[index] = pixels[index] and 0x00ffffff
                                    index += 1
                                }
                            }
                        }

                        else -> fillRasterPixels(pixels, command.color.argb)
                    }
                }

                is KoolCanvasCommand.DrawTexture -> {
                    if (!rasterizeTextureCommand(command, pixels, width, height, visiting, profile)) {
                        return null
                    }
                }

                is KoolCanvasCommand.DrawRect -> {
                    profile?.let { it.rectCommands += 1 }
                    if (!rasterizeRectCommand(command, pixels, width, height)) {
                        return null
                    }
                }

                is KoolCanvasCommand.DrawLine,
                is KoolCanvasCommand.DrawCircle,
                is KoolCanvasCommand.DrawText -> {
                    return null
                }
            }
        }
        return pixels
    }

    private fun rasterizeTextureCommand(
        command: KoolCanvasCommand.DrawTexture,
        targetPixels: IntArray,
        targetWidth: Int,
        targetHeight: Int,
        visiting: Set<KoolCanvasTextureId>,
        profile: CpuTargetProfile? = null,
    ): Boolean {
        if (command.state.renderTarget != null || command.paint.textureEffect != null) {
            return false
        }
        profile?.recordTextureCommand(command)
        val sourceImage = textureStore.argbImageView(command.texture.id)
            ?: textureStore.frame(command.texture.id)?.let { frame ->
                rasterizeFrameToImage(
                    id = command.texture.id,
                    frame = frame,
                    width = command.texture.width,
                    height = command.texture.height,
                    visiting = visiting,
                )
            }
            ?: return false
        val textureQuad =
            clipTextureQuad(command.source, command.destination, command.state.clipForGeometry()) ?: return true
        val source = textureQuad.source
        val destination = textureQuad.destination
        val transform = command.state.transform
        val targetBounds = transform.mapRectBounds(destination)
        val left = floor(targetBounds.boundsLeft).toInt().coerceIn(0, targetWidth)
        val top = floor(targetBounds.boundsTop).toInt().coerceIn(0, targetHeight)
        val right = ceil(targetBounds.boundsRight).toInt().coerceIn(0, targetWidth)
        val bottom = ceil(targetBounds.boundsBottom).toInt().coerceIn(0, targetHeight)
        if (left >= right || top >= bottom) return true

        if (transform === KoolCanvasTransform.Identity && copyTextureRowsIfPossible(
                command = command,
                sourceImage = sourceImage,
                source = source,
                destination = destination,
                targetPixels = targetPixels,
                targetWidth = targetWidth,
                left = left,
                top = top,
                right = right,
                bottom = bottom,
            )
        ) {
            profile?.let { it.fastTextureCopies += 1 }
            return true
        }

        profile?.let {
            it.sampledTextureDraws += 1
            it.sampledPixels += ((right - left) * (bottom - top)).toLong()
        }
        if (transform === KoolCanvasTransform.Identity) {
            rasterizeAxisAlignedTextureCommand(
                command = command,
                sourceImage = sourceImage,
                source = source,
                destination = destination,
                targetPixels = targetPixels,
                targetWidth = targetWidth,
                left = left,
                top = top,
                right = right,
                bottom = bottom,
            )
            return true
        }
        val inverseTransform = transform.inverted() ?: return false
        processRasterRows(top, bottom, right - left) { rowStart, rowEnd ->
            for (y in rowStart until rowEnd) {
                for (x in left until right) {
                    val localPoint = inverseTransform.map(KoolCanvasPoint(x + 0.5f, y + 0.5f))
                    if (!destination.contains(localPoint.x, localPoint.y)) {
                        continue
                    }
                    val sourceColor =
                        sampleSourceColor(command, sourceImage, source, destination, localPoint.x, localPoint.y)
                    blendRenderTargetPixel(
                        pixels = targetPixels,
                        index = x + (y * targetWidth),
                        sourceColor = tintColor(sourceColor, command.paint, sourceImage.premultipliedAlpha),
                        blendMode = command.paint.blendMode,
                        sourcePremultipliedAlpha = sourceImage.premultipliedAlpha,
                    )
                }
            }
        }
        return true
    }

    private fun rasterizeAxisAlignedTextureCommand(
        command: KoolCanvasCommand.DrawTexture,
        sourceImage: KoolCanvasArgbImage,
        source: KoolCanvasRect,
        destination: KoolCanvasRect,
        targetPixels: IntArray,
        targetWidth: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        when (command.paint.textureFilter) {
            KoolCanvasTextureFilter.Nearest -> rasterizeAxisAlignedNearestTextureCommand(
                command = command,
                sourceImage = sourceImage,
                source = source,
                destination = destination,
                targetPixels = targetPixels,
                targetWidth = targetWidth,
                left = left,
                top = top,
                right = right,
                bottom = bottom,
            )

            KoolCanvasTextureFilter.Linear -> rasterizeAxisAlignedLinearTextureCommand(
                command = command,
                sourceImage = sourceImage,
                source = source,
                destination = destination,
                targetPixels = targetPixels,
                targetWidth = targetWidth,
                left = left,
                top = top,
                right = right,
                bottom = bottom,
            )
        }
    }

    private fun rasterizeAxisAlignedNearestTextureCommand(
        command: KoolCanvasCommand.DrawTexture,
        sourceImage: KoolCanvasArgbImage,
        source: KoolCanvasRect,
        destination: KoolCanvasRect,
        targetPixels: IntArray,
        targetWidth: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val width = right - left
        val xSamples = IntArray(width)
        val xEnabled = BooleanArray(width)
        for (offset in 0 until width) {
            val pixelCenter = left + offset + 0.5f
            if (pixelCenter >= destination.boundsLeft && pixelCenter < destination.boundsRight) {
                xEnabled[offset] = true
                xSamples[offset] = sampleNearestCoordinate(
                    pixelCenter = pixelCenter,
                    destinationStart = destination.left,
                    destinationSize = destination.width,
                    sourceStart = source.left,
                    sourceSize = source.width,
                ).coerceIn(0, sourceImage.width - 1)
            }
        }
        processRasterRows(top, bottom, width) { rowStart, rowEnd ->
            for (y in rowStart until rowEnd) {
                val yCenter = y + 0.5f
                if (yCenter < destination.boundsTop || yCenter >= destination.boundsBottom) {
                    continue
                }
                val sourceY = sampleNearestCoordinate(
                    pixelCenter = yCenter,
                    destinationStart = destination.top,
                    destinationSize = destination.height,
                    sourceStart = source.top,
                    sourceSize = source.height,
                ).coerceIn(0, sourceImage.height - 1)
                val sourceRow = sourceY * sourceImage.width
                var targetIndex = left + (y * targetWidth)
                for (offset in 0 until width) {
                    if (xEnabled[offset]) {
                        val sourceColor = sourceImage.pixels[sourceRow + xSamples[offset]]
                        blendRenderTargetPixel(
                            pixels = targetPixels,
                            index = targetIndex,
                            sourceColor = tintColor(sourceColor, command.paint, sourceImage.premultipliedAlpha),
                            blendMode = command.paint.blendMode,
                            sourcePremultipliedAlpha = sourceImage.premultipliedAlpha,
                        )
                    }
                    targetIndex += 1
                }
            }
        }
    }

    private fun rasterizeAxisAlignedLinearTextureCommand(
        command: KoolCanvasCommand.DrawTexture,
        sourceImage: KoolCanvasArgbImage,
        source: KoolCanvasRect,
        destination: KoolCanvasRect,
        targetPixels: IntArray,
        targetWidth: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val width = right - left
        val x0Samples = IntArray(width)
        val x1Samples = IntArray(width)
        val xAmounts = FloatArray(width)
        val xEnabled = BooleanArray(width)
        for (offset in 0 until width) {
            val pixelCenter = left + offset + 0.5f
            if (pixelCenter >= destination.boundsLeft && pixelCenter < destination.boundsRight) {
                xEnabled[offset] = true
                val sourceX = sampleLinearCoordinate(
                    pixelCenter = pixelCenter,
                    destinationStart = destination.left,
                    destinationSize = destination.width,
                    sourceStart = source.left,
                    sourceSize = source.width,
                )
                val x0 = floor(sourceX).toInt()
                x0Samples[offset] = x0.coerceIn(0, sourceImage.width - 1)
                x1Samples[offset] = (x0 + 1).coerceIn(0, sourceImage.width - 1)
                xAmounts[offset] = sourceX - x0
            }
        }
        processRasterRows(top, bottom, width) { rowStart, rowEnd ->
            for (y in rowStart until rowEnd) {
                val yCenter = y + 0.5f
                if (yCenter < destination.boundsTop || yCenter >= destination.boundsBottom) {
                    continue
                }
                val sourceY = sampleLinearCoordinate(
                    pixelCenter = yCenter,
                    destinationStart = destination.top,
                    destinationSize = destination.height,
                    sourceStart = source.top,
                    sourceSize = source.height,
                )
                val y0 = floor(sourceY).toInt()
                val y1 = y0 + 1
                val y0Row = y0.coerceIn(0, sourceImage.height - 1) * sourceImage.width
                val y1Row = y1.coerceIn(0, sourceImage.height - 1) * sourceImage.width
                val yAmount = sourceY - y0
                var targetIndex = left + (y * targetWidth)
                for (offset in 0 until width) {
                    if (xEnabled[offset]) {
                        val c00 = sourceImage.pixels[y0Row + x0Samples[offset]]
                        val c10 = sourceImage.pixels[y0Row + x1Samples[offset]]
                        val c01 = sourceImage.pixels[y1Row + x0Samples[offset]]
                        val c11 = sourceImage.pixels[y1Row + x1Samples[offset]]
                        val topColor = interpolateColor(c00, c10, xAmounts[offset])
                        val bottomColor = interpolateColor(c01, c11, xAmounts[offset])
                        val sourceColor = interpolateColor(topColor, bottomColor, yAmount)
                        blendRenderTargetPixel(
                            pixels = targetPixels,
                            index = targetIndex,
                            sourceColor = tintColor(sourceColor, command.paint, sourceImage.premultipliedAlpha),
                            blendMode = command.paint.blendMode,
                            sourcePremultipliedAlpha = sourceImage.premultipliedAlpha,
                        )
                    }
                    targetIndex += 1
                }
            }
        }
    }

    private fun copyTextureRowsIfPossible(
        command: KoolCanvasCommand.DrawTexture,
        sourceImage: KoolCanvasArgbImage,
        source: KoolCanvasRect,
        destination: KoolCanvasRect,
        targetPixels: IntArray,
        targetWidth: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ): Boolean {
        if (command.paint.textureFilter != KoolCanvasTextureFilter.Nearest ||
            command.paint.color.argb != -0x1 ||
            command.paint.alphaMultiplier != 1f ||
            source.width <= 0f ||
            source.height <= 0f ||
            destination.width <= 0f ||
            destination.height <= 0f ||
            !source.left.isWholePixel() ||
            !source.top.isWholePixel() ||
            !source.right.isWholePixel() ||
            !source.bottom.isWholePixel() ||
            !destination.left.isWholePixel() ||
            !destination.top.isWholePixel() ||
            !destination.right.isWholePixel() ||
            !destination.bottom.isWholePixel() ||
            source.width != destination.width ||
            source.height != destination.height
        ) {
            return false
        }

        val sourceLeft = source.left.toInt() + (left - destination.left.toInt())
        val sourceTop = source.top.toInt() + (top - destination.top.toInt())
        val copyWidth = right - left
        val copyHeight = bottom - top
        if (sourceLeft < 0 ||
            sourceTop < 0 ||
            sourceLeft + copyWidth > sourceImage.width ||
            sourceTop + copyHeight > sourceImage.height
        ) {
            return false
        }

        when (command.paint.blendMode) {
            KoolCanvasBlendMode.Source -> {
                copyTextureRows(
                    sourceImage,
                    targetPixels,
                    targetWidth,
                    sourceLeft,
                    sourceTop,
                    left,
                    top,
                    copyWidth,
                    copyHeight
                )
            }

            KoolCanvasBlendMode.SourceOver -> {
                if (sourceImage.isOpaque(sourceLeft, sourceTop, copyWidth, copyHeight)) {
                    copyTextureRows(
                        sourceImage,
                        targetPixels,
                        targetWidth,
                        sourceLeft,
                        sourceTop,
                        left,
                        top,
                        copyWidth,
                        copyHeight
                    )
                } else {
                    blendSourceOverTextureRows(
                        sourceImage = sourceImage,
                        targetPixels = targetPixels,
                        targetWidth = targetWidth,
                        sourceLeft = sourceLeft,
                        sourceTop = sourceTop,
                        targetLeft = left,
                        targetTop = top,
                        width = copyWidth,
                        height = copyHeight,
                    )
                }
            }

            else -> return false
        }

        return true
    }

    private fun copyTextureRows(
        sourceImage: KoolCanvasArgbImage,
        targetPixels: IntArray,
        targetWidth: Int,
        sourceLeft: Int,
        sourceTop: Int,
        targetLeft: Int,
        targetTop: Int,
        width: Int,
        height: Int,
    ) {
        processRasterRows(0, height, width) { rowStart, rowEnd ->
            for (row in rowStart until rowEnd) {
                sourceImage.pixels.copyInto(
                    destination = targetPixels,
                    destinationOffset = targetLeft + ((targetTop + row) * targetWidth),
                    startIndex = sourceLeft + ((sourceTop + row) * sourceImage.width),
                    endIndex = sourceLeft + ((sourceTop + row) * sourceImage.width) + width,
                )
            }
        }
    }

    private fun blendSourceOverTextureRows(
        sourceImage: KoolCanvasArgbImage,
        targetPixels: IntArray,
        targetWidth: Int,
        sourceLeft: Int,
        sourceTop: Int,
        targetLeft: Int,
        targetTop: Int,
        width: Int,
        height: Int,
    ) {
        processRasterRows(0, height, width) { rowStart, rowEnd ->
            for (row in rowStart until rowEnd) {
                var sourceIndex = sourceLeft + ((sourceTop + row) * sourceImage.width)
                var targetIndex = targetLeft + ((targetTop + row) * targetWidth)
                val sourceEnd = sourceIndex + width
                while (sourceIndex < sourceEnd) {
                    val sourceColor = sourceImage.pixels[sourceIndex]
                    val sourceAlpha = sourceColor.alphaComponent
                    if (sourceAlpha == 255) {
                        targetPixels[targetIndex] = sourceColor
                    } else if (sourceAlpha != 0) {
                        targetPixels[targetIndex] = if (sourceImage.premultipliedAlpha) {
                            renderTargetPremultipliedSourceOver(targetPixels[targetIndex], sourceColor)
                        } else {
                            renderTargetSourceOver(targetPixels[targetIndex], sourceColor)
                        }
                    }
                    sourceIndex += 1
                    targetIndex += 1
                }
            }
        }
    }

    private fun clipTextureQuad(
        source: KoolCanvasRect,
        destination: KoolCanvasRect,
        clip: KoolCanvasRect?,
    ): TextureQuad? {
        if (source.hasZeroArea || destination.hasZeroArea) return null
        val clippedDestination = if (clip != null) {
            val clippedBounds = destination.boundsIntersect(clip) ?: return null
            destination.orientBounds(clippedBounds)
        } else {
            destination
        }
        if (clippedDestination.hasZeroArea) return null

        val leftRatio = (clippedDestination.left - destination.left) / destination.width
        val topRatio = (clippedDestination.top - destination.top) / destination.height
        val rightRatio = (clippedDestination.right - destination.left) / destination.width
        val bottomRatio = (clippedDestination.bottom - destination.top) / destination.height
        val clippedSource = KoolCanvasRect(
            left = source.left + source.width * leftRatio,
            top = source.top + source.height * topRatio,
            right = source.left + source.width * rightRatio,
            bottom = source.top + source.height * bottomRatio,
        )
        return TextureQuad(clippedSource, clippedDestination)
    }

    private fun sampleSourceColor(
        command: KoolCanvasCommand.DrawTexture,
        sourceImage: KoolCanvasArgbImage,
        source: KoolCanvasRect,
        destination: KoolCanvasRect,
        x: Float,
        y: Float,
    ): Int =
        when (command.paint.textureFilter) {
            KoolCanvasTextureFilter.Nearest -> {
                val sourceX = sampleNearestCoordinate(
                    pixelCenter = x,
                    destinationStart = destination.left,
                    destinationSize = destination.width,
                    sourceStart = source.left,
                    sourceSize = source.width,
                ).coerceIn(0, sourceImage.width - 1)
                val sourceY = sampleNearestCoordinate(
                    pixelCenter = y,
                    destinationStart = destination.top,
                    destinationSize = destination.height,
                    sourceStart = source.top,
                    sourceSize = source.height,
                ).coerceIn(0, sourceImage.height - 1)
                sourceImage.pixels[sourceX + (sourceY * sourceImage.width)]
            }

            KoolCanvasTextureFilter.Linear -> sampleLinearColor(command, sourceImage, source, destination, x, y)
        }

    private fun sampleLinearColor(
        command: KoolCanvasCommand.DrawTexture,
        sourceImage: KoolCanvasArgbImage,
        source: KoolCanvasRect,
        destination: KoolCanvasRect,
        x: Float,
        y: Float,
    ): Int {
        val sourceX = sampleLinearCoordinate(
            pixelCenter = x,
            destinationStart = destination.left,
            destinationSize = destination.width,
            sourceStart = source.left,
            sourceSize = source.width,
        )
        val sourceY = sampleLinearCoordinate(
            pixelCenter = y,
            destinationStart = destination.top,
            destinationSize = destination.height,
            sourceStart = source.top,
            sourceSize = source.height,
        )
        val x0 = floor(sourceX).toInt()
        val y0 = floor(sourceY).toInt()
        val x1 = x0 + 1
        val y1 = y0 + 1
        val tx = sourceX - x0
        val ty = sourceY - y0
        val c00 = sourceImage.pixelClamped(x0, y0)
        val c10 = sourceImage.pixelClamped(x1, y0)
        val c01 = sourceImage.pixelClamped(x0, y1)
        val c11 = sourceImage.pixelClamped(x1, y1)
        val top = interpolateColor(c00, c10, tx)
        val bottom = interpolateColor(c01, c11, tx)
        return interpolateColor(top, bottom, ty)
    }

    private fun rasterizeRectCommand(
        command: KoolCanvasCommand.DrawRect,
        targetPixels: IntArray,
        targetWidth: Int,
        targetHeight: Int,
    ): Boolean {
        if (command.state.renderTarget != null ||
            command.paint.style == KoolCanvasPaintStyle.Stroke
        ) {
            return false
        }
        val destination = command.state.clipForGeometry()?.let { command.rect.intersect(it) } ?: command.rect
        if (destination.isEmpty) return true
        val transform = command.state.transform
        val targetBounds = transform.mapRectBounds(destination)
        val left = floor(targetBounds.boundsLeft).toInt().coerceIn(0, targetWidth)
        val top = floor(targetBounds.boundsTop).toInt().coerceIn(0, targetHeight)
        val right = ceil(targetBounds.boundsRight).toInt().coerceIn(0, targetWidth)
        val bottom = ceil(targetBounds.boundsBottom).toInt().coerceIn(0, targetHeight)
        if (left >= right || top >= bottom) return true
        val color = paintColor(command.paint)
        val inverseTransform = transform.inverted() ?: return false
        processRasterRows(top, bottom, right - left) { rowStart, rowEnd ->
            for (y in rowStart until rowEnd) {
                for (x in left until right) {
                    val localPoint = inverseTransform.map(KoolCanvasPoint(x + 0.5f, y + 0.5f))
                    if (!destination.contains(localPoint.x, localPoint.y)) {
                        continue
                    }
                    blendRenderTargetPixel(targetPixels, x + (y * targetWidth), color, command.paint.blendMode)
                }
            }
        }
        return true
    }

    private fun fillRasterPixels(pixels: IntArray, color: Int) {
        processRasterRows(0, pixels.size, 1) { start, end ->
            pixels.fill(color, start, end)
        }
    }

    private fun processRasterRows(
        top: Int,
        bottom: Int,
        rowWidth: Int,
        block: (Int, Int) -> Unit,
    ) {
        val rowCount = bottom - top
        if (rowCount <= 0 || rowWidth <= 0) {
            return
        }
        val workerCount = RasterWorkerPool.workerCount
        val pixelCount = rowCount * rowWidth
        if (workerCount <= 1 ||
            rowCount < workerCount * PARALLEL_RASTER_MIN_ROWS_PER_WORKER ||
            pixelCount < PARALLEL_RASTER_MIN_PIXELS
        ) {
            block(top, bottom)
            return
        }

        val taskCount = minOf(workerCount, rowCount)
        val rowsPerTask = (rowCount + taskCount - 1) / taskCount
        val latch = CountDownLatch(taskCount)
        val failure = AtomicReference<Throwable?>()
        for (taskIndex in 0 until taskCount) {
            val rowStart = top + taskIndex * rowsPerTask
            val rowEnd = minOf(bottom, rowStart + rowsPerTask)
            RasterWorkerPool.executor.execute {
                try {
                    if (failure.get() == null) {
                        block(rowStart, rowEnd)
                    }
                } catch (throwable: Throwable) {
                    failure.compareAndSet(null, throwable)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()
        failure.get()?.let { throw it }
    }

    private fun sampleNearestCoordinate(
        pixelCenter: Float,
        destinationStart: Float,
        destinationSize: Float,
        sourceStart: Float,
        sourceSize: Float,
    ): Int {
        if (destinationSize == 0f) return sourceStart.toInt()
        val t = (pixelCenter - destinationStart) / destinationSize
        return floor(sourceStart + (t * sourceSize)).toInt()
    }

    private fun sampleLinearCoordinate(
        pixelCenter: Float,
        destinationStart: Float,
        destinationSize: Float,
        sourceStart: Float,
        sourceSize: Float,
    ): Float {
        if (destinationSize == 0f) return sourceStart
        val t = (pixelCenter - destinationStart) / destinationSize
        return sourceStart + (t * sourceSize) - 0.5f
    }

    private fun KoolCanvasRect.contains(x: Float, y: Float): Boolean =
        x >= boundsLeft && x < boundsRight && y >= boundsTop && y < boundsBottom

    private fun KoolCanvasTransform.inverted(): KoolCanvasTransform? {
        if (this === KoolCanvasTransform.Identity) return this
        val determinant = (scaleX * scaleY) - (skewX * skewY)
        if (abs(determinant) < 0.000001f) {
            return null
        }
        val inverseScaleX = scaleY / determinant
        val inverseSkewX = -skewX / determinant
        val inverseSkewY = -skewY / determinant
        val inverseScaleY = scaleX / determinant
        return KoolCanvasTransform(
            scaleX = inverseScaleX,
            skewY = inverseSkewY,
            skewX = inverseSkewX,
            scaleY = inverseScaleY,
            translateX = -(inverseScaleX * translateX + inverseSkewX * translateY),
            translateY = -(inverseSkewY * translateX + inverseScaleY * translateY),
        )
    }

    private fun KoolCanvasState.clipForGeometry(): KoolCanvasRect? {
        val currentClip = clip ?: return null
        val currentTransform = transform
        if (currentTransform === KoolCanvasTransform.Identity) {
            return currentClip
        }
        return currentTransform.inverted()?.mapRectBounds(currentClip) ?: currentClip
    }

    private fun KoolCanvasArgbImage.pixelClamped(x: Int, y: Int): Int {
        val clampedX = x.coerceIn(0, width - 1)
        val clampedY = y.coerceIn(0, height - 1)
        return pixels[clampedX + (clampedY * width)]
    }

    private fun KoolCanvasArgbImage.isOpaque(left: Int, top: Int, width: Int, height: Int): Boolean {
        for (row in 0 until height) {
            val rowStart = left + ((top + row) * this.width)
            for (index in rowStart until rowStart + width) {
                if (pixels[index].alphaComponent != 255) {
                    return false
                }
            }
        }
        return true
    }

    private fun interpolateColor(left: Int, right: Int, amount: Float): Int {
        val t = amount.coerceIn(0f, 1f)
        val alpha = interpolateChannelFloat(left.alphaComponent, right.alphaComponent, t)
        if (alpha <= 0f) {
            return 0
        }
        return argb(
            alpha = alpha.roundToInt().coerceIn(0, 255),
            red = interpolatePremultipliedChannel(
                left = left.redComponent,
                leftAlpha = left.alphaComponent,
                right = right.redComponent,
                rightAlpha = right.alphaComponent,
                amount = t,
                alpha = alpha,
            ),
            green = interpolatePremultipliedChannel(
                left = left.greenComponent,
                leftAlpha = left.alphaComponent,
                right = right.greenComponent,
                rightAlpha = right.alphaComponent,
                amount = t,
                alpha = alpha,
            ),
            blue = interpolatePremultipliedChannel(
                left = left.blueComponent,
                leftAlpha = left.alphaComponent,
                right = right.blueComponent,
                rightAlpha = right.alphaComponent,
                amount = t,
                alpha = alpha,
            ),
        )
    }

    private fun interpolatePremultipliedChannel(
        left: Int,
        leftAlpha: Int,
        right: Int,
        rightAlpha: Int,
        amount: Float,
        alpha: Float,
    ): Int {
        val premultipliedLeft = left * leftAlpha
        val premultipliedRight = right * rightAlpha
        val premultiplied = premultipliedLeft + ((premultipliedRight - premultipliedLeft) * amount)
        return (premultiplied / alpha).roundToInt().coerceIn(0, 255)
    }

    private fun interpolateChannelFloat(left: Int, right: Int, amount: Float): Float =
        left + ((right - left) * amount)

    private fun tintColor(sourceColor: Int, paint: KoolCanvasPaint, premultipliedAlpha: Boolean = false): Int {
        val tint = paint.color.argb
        val tintAlpha = multiplyChannel(tint.alphaComponent, (paint.alphaMultiplier * 255f).roundToInt())
        val alpha = multiplyChannel(sourceColor.alphaComponent, tintAlpha)
        if (premultipliedAlpha) {
            return argb(
                alpha = alpha,
                red = multiplyChannel(multiplyChannel(sourceColor.redComponent, tint.redComponent), tintAlpha),
                green = multiplyChannel(multiplyChannel(sourceColor.greenComponent, tint.greenComponent), tintAlpha),
                blue = multiplyChannel(multiplyChannel(sourceColor.blueComponent, tint.blueComponent), tintAlpha),
            )
        }
        return argb(
            alpha = alpha,
            red = multiplyChannel(sourceColor.redComponent, tint.redComponent),
            green = multiplyChannel(sourceColor.greenComponent, tint.greenComponent),
            blue = multiplyChannel(sourceColor.blueComponent, tint.blueComponent),
        )
    }

    private fun paintColor(paint: KoolCanvasPaint): Int {
        val color = paint.color.argb
        return argb(
            alpha = multiplyChannel(color.alphaComponent, (paint.alphaMultiplier * 255f).roundToInt()),
            red = color.redComponent,
            green = color.greenComponent,
            blue = color.blueComponent,
        )
    }

    private fun blendRenderTargetPixel(
        pixels: IntArray,
        index: Int,
        sourceColor: Int,
        blendMode: KoolCanvasBlendMode,
        sourcePremultipliedAlpha: Boolean = false,
    ) {
        pixels[index] = when (blendMode) {
            KoolCanvasBlendMode.Clear -> 0
            KoolCanvasBlendMode.ClearAlpha -> pixels[index] and 0x00ffffff
            KoolCanvasBlendMode.Source -> sourceColor
            KoolCanvasBlendMode.Destination -> pixels[index]
            KoolCanvasBlendMode.Add -> addColors(pixels[index], sourceColor, sourcePremultipliedAlpha)
            else -> if (sourcePremultipliedAlpha) {
                renderTargetPremultipliedSourceOver(pixels[index], sourceColor)
            } else {
                renderTargetSourceOver(pixels[index], sourceColor)
            }
        }
    }

    private fun renderTargetPremultipliedSourceOver(destinationColor: Int, sourceColor: Int): Int {
        val sourceAlpha = sourceColor.alphaComponent
        if (sourceAlpha == 255) return sourceColor
        if (sourceAlpha == 0) return destinationColor
        val destinationAlpha = destinationColor.alphaComponent
        if (destinationAlpha == 0) {
            return argb(
                alpha = sourceAlpha,
                red = unpremultiplyChannel(sourceColor.redComponent, sourceAlpha),
                green = unpremultiplyChannel(sourceColor.greenComponent, sourceAlpha),
                blue = unpremultiplyChannel(sourceColor.blueComponent, sourceAlpha),
            )
        }
        val inverseAlpha = 255 - sourceAlpha
        val destinationContributionAlpha = multiplyChannelRounded(destinationAlpha, inverseAlpha)
        val outAlpha = (sourceAlpha + destinationContributionAlpha).coerceAtMost(255)
        if (outAlpha == 0) return 0
        return argb(
            alpha = outAlpha,
            red = unpremultiplyChannel(
                (sourceColor.redComponent + multiplyChannel(
                    destinationColor.redComponent,
                    destinationContributionAlpha
                ))
                    .coerceAtMost(255),
                outAlpha,
            ),
            green = unpremultiplyChannel(
                (sourceColor.greenComponent + multiplyChannel(
                    destinationColor.greenComponent,
                    destinationContributionAlpha
                ))
                    .coerceAtMost(255),
                outAlpha,
            ),
            blue = unpremultiplyChannel(
                (sourceColor.blueComponent + multiplyChannel(
                    destinationColor.blueComponent,
                    destinationContributionAlpha
                ))
                    .coerceAtMost(255),
                outAlpha,
            ),
        )
    }

    private fun renderTargetSourceOver(destinationColor: Int, sourceColor: Int): Int {
        val sourceAlpha = sourceColor.alphaComponent
        if (sourceAlpha == 255) return sourceColor
        if (sourceAlpha == 0) return destinationColor
        val destinationAlpha = destinationColor.alphaComponent
        if (destinationAlpha == 0) return sourceColor
        val inverseAlpha = 255 - sourceAlpha
        val destinationContributionAlpha = multiplyChannelRounded(destinationAlpha, inverseAlpha)
        val outAlpha = (sourceAlpha + destinationContributionAlpha).coerceAtMost(255)
        if (outAlpha == 0) return 0
        return argb(
            alpha = outAlpha,
            red = unpremultiplyChannel(
                multiplyChannel(sourceColor.redComponent, sourceAlpha) +
                        multiplyChannel(destinationColor.redComponent, destinationContributionAlpha),
                outAlpha,
            ),
            green = unpremultiplyChannel(
                multiplyChannel(sourceColor.greenComponent, sourceAlpha) +
                        multiplyChannel(destinationColor.greenComponent, destinationContributionAlpha),
                outAlpha,
            ),
            blue = unpremultiplyChannel(
                multiplyChannel(sourceColor.blueComponent, sourceAlpha) +
                        multiplyChannel(destinationColor.blueComponent, destinationContributionAlpha),
                outAlpha,
            ),
        )
    }

    private fun unpremultiplyChannel(premultiplied: Int, alpha: Int): Int =
        if (alpha <= 0) {
            0
        } else {
            ((premultiplied * 255) + (alpha / 2)) / alpha
        }.coerceIn(0, 255)

    private fun multiplyChannelRounded(source: Int, multiplier: Int): Int =
        ((source * multiplier) + 127) / 255

    private fun addColors(destinationColor: Int, sourceColor: Int, sourcePremultipliedAlpha: Boolean): Int {
        val sourceAlpha = sourceColor.alphaComponent
        val sourceRed = sourceColor.redComponent.premultiplyForAdd(sourceAlpha, sourcePremultipliedAlpha)
        val sourceGreen = sourceColor.greenComponent.premultiplyForAdd(sourceAlpha, sourcePremultipliedAlpha)
        val sourceBlue = sourceColor.blueComponent.premultiplyForAdd(sourceAlpha, sourcePremultipliedAlpha)
        return argb(
            alpha = (destinationColor.alphaComponent + sourceAlpha).coerceAtMost(255),
            red = (destinationColor.redComponent + sourceRed).coerceAtMost(255),
            green = (destinationColor.greenComponent + sourceGreen).coerceAtMost(255),
            blue = (destinationColor.blueComponent + sourceBlue).coerceAtMost(255),
        )
    }

    private fun Int.premultiplyForAdd(alpha: Int, premultipliedAlpha: Boolean): Int =
        if (premultipliedAlpha) this else multiplyChannel(this, alpha)

    private fun commitTargetTextureFrame() {
        if (targetTexture == null) {
            flushTargetTextureFrame()
        } else {
            flushPendingTarget(targetTexture)
        }
    }

    private fun flushPendingTarget(texture: Texture) {
        val state = targetStates[texture] ?: return
        if (!state.commitEnabled || state.committing) {
            return
        }
        state.committing = true
        try {
            texture.e++
            val commit = state.engine.flushTargetTextureFrame() ?: return
            val nextAuxiliaryIds = commit.auxiliaryIds.toSet()
            state.activeAuxiliaryIds
                .filterNot(nextAuxiliaryIds::contains)
                .forEach(textureStore::unregister)
            state.activeAuxiliaryIds.clear()
            state.activeAuxiliaryIds += commit.auxiliaryIds
        } finally {
            state.committing = false
        }
    }

    private fun releaseKoolTexture(texture: Texture) {
        targetStates.remove(texture)?.releaseAuxiliaryTextures(textureStore)
        textureStore.unregister(texture.toCanvasTextureId())
    }

    private fun drawTexture(
        texture: Texture,
        destination: KoolCanvasRect,
        source: KoolCanvasRect,
        paint: KoolPaint?,
    ) {
        flushPendingTarget(texture)
        val atlasDraw = texture.toAtlasDraw(destination, source)
        val drawTexture = atlasDraw?.texture ?: texture
        val drawDestination = atlasDraw?.destination ?: destination
        val drawSource = atlasDraw?.source ?: source
        val teamColorEffect = (drawTexture as? TeamColorTexture)?.toCanvasTeamColorEffect()
        val resolvedTexture = if (teamColorEffect != null) {
            drawTexture.sourceTexture()
        } else {
            drawTexture.resolveForKool()
        }
        registerTexturePixels(resolvedTexture)
        val textureRef = freezeTextureForOffscreenDraw(resolvedTexture, resolvedTexture.toCanvasTextureRef())
        val canvasPaint = paint.toCanvasPaint()
        val texturePaint = canvasPaint
            .copy(textureEffect = teamColorEffect ?: canvasPaint.textureEffect)
            .withDirectBlitTextureBlend(resolvedTexture)
            .asCanonicalTexturePaint()
        commandBuffer.drawTexture(
            texture = textureRef,
            destination = drawDestination,
            paint = texturePaint,
            source = drawSource,
        )
    }

    private fun Texture.toAtlasDraw(
        destination: KoolCanvasRect,
        source: KoolCanvasRect,
    ): AtlasDraw? {
        val atlas = textureAtlasFor(this) ?: return null
        val region = atlas.a(this) ?: return null
        var drawLeft = destination.left
        var drawTop = destination.top
        var drawRight = destination.right
        var drawBottom = destination.bottom
        var sourceLeft = source.left
        var sourceTop = source.top
        var sourceRight = source.right
        var sourceBottom = source.bottom

        if (sourceLeft < 0f) {
            drawLeft += -sourceLeft
            sourceLeft = 0f
        }
        if (sourceTop < 0f) {
            drawTop += -sourceTop
            sourceTop = 0f
        }
        if (sourceRight > region.d) {
            drawRight += -(region.d - sourceRight)
            sourceRight = region.d
        }
        if (sourceBottom > region.e) {
            drawBottom += -(region.e - sourceBottom)
            sourceBottom = region.e
        }

        return AtlasDraw(
            texture = region.a ?: return null,
            destination = KoolCanvasRect(drawLeft, drawTop, drawRight, drawBottom),
            source = KoolCanvasRect(
                sourceLeft + region.b,
                sourceTop + region.c,
                sourceRight + region.b,
                sourceBottom + region.c,
            ),
        )
    }

    private fun textureAtlasFor(texture: Texture): TextureAtlas? {
        if (!enableTextureAtlas || targetTexture != null || texture is TeamColorTexture) {
            return null
        }
        if (texture.m() >= ATLAS_MAX_TEXTURE_WIDTH || texture.l() >= ATLAS_MAX_TEXTURE_HEIGHT) {
            return null
        }
        return textureAtlas ?: TextureAtlas(ATLAS_WIDTH, ATLAS_HEIGHT, this).also {
            textureAtlas = it
        }
    }

    private fun drawTiledTexture(
        texture: Texture,
        destination: KoolCanvasRect,
        offsetX: Float,
        offsetY: Float,
        repeatInsetX: Int,
        repeatInsetY: Int,
        paint: KoolPaint?,
    ) {
        if (destination.isEmpty) return
        val textureWidth = texture.width()
        val textureHeight = texture.height()
        if (textureWidth <= 0 || textureHeight <= 0) return

        val stepX = textureWidth - repeatInsetX
        val stepY = textureHeight - repeatInsetY
        if (stepX <= 0 || stepY <= 0) return

        val normalizedOffsetX = offsetX.modPositive(textureWidth.toFloat())
        val normalizedOffsetY = offsetY.modPositive(textureHeight.toFloat())
        val teamColorEffect = (texture as? TeamColorTexture)?.toCanvasTeamColorEffect()
        val resolvedTexture = if (teamColorEffect != null) {
            texture.sourceTexture()
        } else {
            texture.resolveForKool()
        }
        registerTexturePixels(resolvedTexture)
        val textureRef = freezeTextureForOffscreenDraw(resolvedTexture, resolvedTexture.toCanvasTextureRef())
        val basePaint = paint.toCanvasPaint()
        val canvasPaint = basePaint
            .copy(textureEffect = teamColorEffect ?: basePaint.textureEffect)
            .withDirectBlitTextureBlend(resolvedTexture)
            .asCanonicalTexturePaint()
        var tileLeft = destination.left - normalizedOffsetX
        val firstTileTop = destination.top - normalizedOffsetY
        var tileCount = 0

        while (tileLeft < destination.right) {
            var tileTop = firstTileTop
            while (tileTop < destination.bottom) {
                if (++tileCount > TILE_IMAGE_LIMIT) return
                var tileWidth = (destination.right - tileLeft).coerceAtMost(textureWidth.toFloat())
                var tileHeight = (destination.bottom - tileTop).coerceAtMost(textureHeight.toFloat())
                if (tileWidth <= 0f || tileHeight <= 0f) break

                var sourceLeft = 0f
                var sourceTop = 0f
                var drawLeft = tileLeft
                var drawTop = tileTop
                val drawRight = tileLeft + tileWidth
                val drawBottom = tileTop + tileHeight

                if (drawLeft < destination.left) {
                    val delta = destination.left - drawLeft
                    sourceLeft += delta
                    drawLeft += delta
                }
                if (drawTop < destination.top) {
                    val delta = destination.top - drawTop
                    sourceTop += delta
                    drawTop += delta
                }

                tileWidth -= sourceLeft
                tileHeight -= sourceTop
                if (tileWidth > 0f && tileHeight > 0f) {
                    commandBuffer.drawTexture(
                        texture = textureRef,
                        destination = KoolCanvasRect(drawLeft, drawTop, drawRight, drawBottom),
                        paint = canvasPaint,
                        source = KoolCanvasRect(sourceLeft, sourceTop, sourceLeft + tileWidth, sourceTop + tileHeight),
                    )
                }
                tileTop += stepY
            }
            tileLeft += stepX
        }
    }

    private fun freezeTextureForOffscreenDraw(
        texture: Texture,
        textureRef: KoolCanvasTextureRef
    ): KoolCanvasTextureRef {
        val targetId = targetTexture?.toCanvasTextureId() ?: return textureRef
        val sourceId = textureRef.id
        if (sourceId == targetId) return textureRef

        val sourceFrame = textureStore.frame(sourceId)
        if (sourceFrame != null) {
            pendingFrameDependencySnapshots[sourceId]?.takeIf { it.frame === sourceFrame }?.let { snapshot ->
                return textureRef.copy(id = snapshot.textureId)
            }
            retainExistingFrameSnapshotDependencies(sourceFrame, visiting = setOf(sourceId))
            val snapshot = snapshotFrameDependencies(sourceFrame, visiting = setOf(sourceId))
            val snapshotId = sourceId.snapshotId()
            textureStore.registerFrame(snapshotId, snapshot.frame)
            pendingFrameDependencySnapshotIds += snapshot.auxiliaryIds
            pendingFrameDependencySnapshotIds += snapshotId
            pendingFrameDependencySnapshots[sourceId] = ImmediateFrameSnapshot(sourceFrame, snapshotId)
            return textureRef.copy(id = snapshotId)
        }

        if (targetStates[texture]?.mode == null || targetStates[texture]?.mode == RenderTargetMode.DEFAULT) {
            return textureRef
        }
        val registration = texture.pixelRegistration()
        pendingPixelDependencySnapshots[sourceId]?.takeIf { it.registration == registration }?.let { snapshot ->
            return textureRef.copy(id = snapshot.textureId)
        }
        val pixels = texture.argbPixelsCopy ?: textureStore.argbImageView(sourceId)?.pixels ?: return textureRef
        val snapshotId = sourceId.snapshotId()
        if (texture.usesPremultipliedAlpha()) {
            textureStore.registerPremultipliedArgb(snapshotId, registration.width, registration.height, pixels)
        } else {
            textureStore.registerArgb(snapshotId, registration.width, registration.height, pixels)
        }
        pendingFrameDependencySnapshotIds += snapshotId
        pendingPixelDependencySnapshots[sourceId] = ImmediatePixelSnapshot(registration, snapshotId)
        return textureRef.copy(id = snapshotId)
    }

    private fun retainExistingFrameSnapshotDependencies(
        frame: KoolCanvasFrame,
        visiting: Set<KoolCanvasTextureId>,
        remainingDepth: Int = MAX_FRAME_SNAPSHOT_DEPENDENCY_DEPTH,
    ) {
        if (remainingDepth <= 0) {
            return
        }
        val retainer = textureStore as? KoolCanvasFrameSnapshotRetainer ?: return
        frame.commands.forEach { command ->
            if (command !is KoolCanvasCommand.DrawTexture || command.texture.id in visiting) {
                return@forEach
            }
            val nestedFrame = textureStore.frame(command.texture.id) ?: return@forEach
            if (command.texture.id.isFrameSnapshotId) {
                retainer.retainFrameSnapshot(command.texture.id)
            }
            retainExistingFrameSnapshotDependencies(
                frame = nestedFrame,
                visiting = visiting + command.texture.id,
                remainingDepth = remainingDepth - 1,
            )
        }
    }

    private fun releasePendingImmediateFrameSnapshots() {
        pendingFrameDependencySnapshotIds.forEach(textureStore::unregister)
        pendingFrameDependencySnapshotIds.clear()
        pendingFrameDependencySnapshots.clear()
        pendingPixelDependencySnapshots.clear()
    }

    private fun registerTexturePixels(texture: Texture) {
        val id = texture.toCanvasTextureId()
        val width = texture.width().coerceAtLeast(1)
        val height = texture.height().coerceAtLeast(1)
        val registration = texture.pixelRegistration(width, height)
        if (registeredTexturePixelRevisions[id] == registration) {
            return
        }
        val pixels = texture.argbPixelsCopy ?: return
        if (texture.usesPremultipliedAlpha()) {
            textureStore.registerPremultipliedArgb(id, width, height, pixels)
        } else {
            textureStore.registerArgb(id, width, height, pixels)
        }
        registeredTexturePixelRevisions[id] = registration
    }

    private fun Texture.resolveForKool(): Texture =
        (this as? TeamColorTexture)?.resolveSourceTexture() ?: this

    private fun TeamColorTexture.toCanvasTeamColorEffect(): KoolCanvasTextureEffect.TeamColor =
        KoolCanvasTextureEffect.TeamColor(
            mode = when (colorMode) {
                ColorMode.hueAdd -> KoolCanvasTeamColorMode.HueAdd
                ColorMode.hueShift -> KoolCanvasTeamColorMode.HueShift
                else -> KoolCanvasTeamColorMode.PureGreen
            },
            color = KoolCanvasColor(teamColor),
            amount = teamColorAmount,
        )

    private fun KoolDisplacementEffect.toCanvasDisplacementEffect(): KoolCanvasTextureEffect.Displacement? {
        val baseTexture = screenBase ?: return null
        registerTexturePixels(baseTexture.resolveForKool())
        return KoolCanvasTextureEffect.Displacement(
            screenBase = baseTexture.resolveForKool().toCanvasTextureRef(),
            offsetBy = offsetBy,
        )
    }

    private fun Texture.toCanvasTextureRef(): KoolCanvasTextureRef =
        KoolCanvasTextureRef(
            id = toCanvasTextureId(),
            width = width().coerceAtLeast(0),
            height = height().coerceAtLeast(0),
            hasAlpha = f(),
            requiresOrderedAlpha = requiresOrderedAlpha(),
            premultipliedAlpha = usesPremultipliedAlpha(),
        )

    private fun KoolCanvasPaint.withDirectBlitTextureBlend(texture: Texture): KoolCanvasPaint {
        if (!directBlitActive ||
            blendMode != KoolCanvasBlendMode.SourceOver ||
            color.alpha != 255 ||
            texture.f()
        ) {
            return this
        }
        return copy(blendMode = KoolCanvasBlendMode.Source)
    }

    private fun KoolCanvasPaint.asCanonicalTexturePaint(): KoolCanvasPaint {
        if (color != KoolCanvasColor.White ||
            alphaMultiplier != 1f ||
            blendMode != KoolCanvasBlendMode.SourceOver ||
            textureEffect != null
        ) {
            return this
        }
        return when (textureFilter) {
            KoolCanvasTextureFilter.Nearest -> KoolCanvasPaint.DefaultNearest
            KoolCanvasTextureFilter.Linear -> KoolCanvasPaint.Default
        }
    }

    private fun KoolCanvasPaint.withDirectBlitRectBlend(): KoolCanvasPaint {
        if (!directBlitActive ||
            blendMode != KoolCanvasBlendMode.SourceOver ||
            color.alpha != 255
        ) {
            return this
        }
        return copy(blendMode = KoolCanvasBlendMode.Source)
    }

    private fun Texture.toCanvasTextureId(): KoolCanvasTextureId = KoolCanvasTextureId("legacy-texture-$d")

    private fun Texture.pixelRegistration(
        width: Int = width().coerceAtLeast(1),
        height: Int = height().coerceAtLeast(1),
    ): TexturePixelRegistration =
        TexturePixelRegistration(
            width = width,
            height = height,
            pixelRevision = (e * 31) + getPixelRevision(),
            premultipliedAlpha = usesPremultipliedAlpha(),
        )

    private fun Texture.fullSourceRect(): KoolCanvasRect =
        KoolCanvasRect.fromSize(width().toFloat(), height().toFloat())

    private fun Rect.toCanvasRect(): KoolCanvasRect =
        KoolCanvasRect(a.toFloat(), b.toFloat(), c.toFloat(), d.toFloat())

    private fun RectF.toCanvasRect(): KoolCanvasRect =
        KoolCanvasRect(a, b, c, d)

    private fun KoolPaint?.toCanvasPaint(): KoolCanvasPaint {
        if (this == null) {
            return KoolCanvasPaint.DefaultNearest
        }
        val colorFilter = colorFilter()
        val color = color().applyKoolColorFilter(colorFilter)
        val textureEffect = (this as? GamePaint)?.displacementEffect()?.toCanvasDisplacementEffect()
        val textureFilter = if (legacyTextureFilteringEnabled()) {
            KoolCanvasTextureFilter.Linear
        } else {
            KoolCanvasTextureFilter.Nearest
        }
        return KoolCanvasPaint(
            color = KoolCanvasColor(color),
            alphaMultiplier = 1f,
            style = when (style()) {
                KoolPaint.Style.STROKE -> KoolCanvasPaintStyle.Stroke
                KoolPaint.Style.FILL_AND_STROKE -> KoolCanvasPaintStyle.FillAndStroke
                else -> KoolCanvasPaintStyle.Fill
            },
            strokeWidth = strokeWidth().coerceAtLeast(1f),
            textureFilter = textureFilter,
            blendMode = getBlendMode() ?: colorFilter.legacyKoolBlendMode() ?: KoolCanvasBlendMode.SourceOver,
            textSize = textSize().coerceAtLeast(1f),
            textAlign = when (textAlign()) {
                KoolPaint.Align.CENTER -> KoolCanvasTextAlign.Center
                KoolPaint.Align.RIGHT -> KoolCanvasTextAlign.Right
                else -> KoolCanvasTextAlign.Left
            },
            typefaceKey = typefaceKey(),
            textureEffect = textureEffect,
        )
    }

    private fun KoolPaint.legacyTextureFilteringEnabled(): Boolean =
        c()

    private fun KoolPaint?.typefaceKey(): String? = this?.typeface()?.koolKey

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

    private fun Float.modPositive(divisor: Float): Float {
        if (this == 0f) return 0f
        var value = this % divisor
        if (value < 0f) {
            value += divisor
        }
        return value
    }

    private fun Float.isWholePixel(): Boolean = this == roundToInt().toFloat()

    private data class AtlasDraw(
        val texture: Texture,
        val destination: KoolCanvasRect,
        val source: KoolCanvasRect,
    )

    private data class TextureQuad(
        val source: KoolCanvasRect,
        val destination: KoolCanvasRect,
    )

    private data class FrameSnapshot(
        val frame: KoolCanvasFrame,
        val auxiliaryIds: List<KoolCanvasTextureId>,
    )

    private data class FrameDependencySnapshot(
        val textureId: KoolCanvasTextureId,
        val auxiliaryIds: List<KoolCanvasTextureId>,
    )

    private data class ImmediateFrameSnapshot(
        val frame: KoolCanvasFrame,
        val textureId: KoolCanvasTextureId,
    )

    private data class ImmediatePixelSnapshot(
        val registration: TexturePixelRegistration,
        val textureId: KoolCanvasTextureId,
    )

    private data class TargetTextureCommit(
        val textureId: KoolCanvasTextureId,
        val auxiliaryIds: List<KoolCanvasTextureId>,
    )

    private data class TexturePixelRegistration(
        val width: Int,
        val height: Int,
        val pixelRevision: Int,
        val premultipliedAlpha: Boolean,
    )

    private class CpuTargetProfile(
        private val texture: Texture,
        private val width: Int,
        private val height: Int,
    ) {
        var totalCommands: Int = 0
        var clearCommands: Int = 0
        var textureCommands: Int = 0
        var rectCommands: Int = 0
        var linearTextureCommands: Int = 0
        var nearestTextureCommands: Int = 0
        var fastTextureCopies: Int = 0
        var sampledTextureDraws: Int = 0
        var sampledPixels: Long = 0L
        private var transformedTextureCommands: Int = 0
        private var tintedTextureCommands: Int = 0
        private var sourceBlendTextureCommands: Int = 0
        private var sourceOverTextureCommands: Int = 0

        fun recordTextureCommand(command: KoolCanvasCommand.DrawTexture) {
            textureCommands += 1
            when (command.paint.textureFilter) {
                KoolCanvasTextureFilter.Linear -> linearTextureCommands += 1
                KoolCanvasTextureFilter.Nearest -> nearestTextureCommands += 1
            }
            if (command.state.transform !== KoolCanvasTransform.Identity) {
                transformedTextureCommands += 1
            }
            if (command.paint.color.argb != -0x1 || command.paint.alphaMultiplier != 1f) {
                tintedTextureCommands += 1
            }
            when (command.paint.blendMode) {
                KoolCanvasBlendMode.Source -> sourceBlendTextureCommands += 1
                KoolCanvasBlendMode.SourceOver -> sourceOverTextureCommands += 1
                else -> Unit
            }
        }

        fun log(elapsedNanos: Long, success: Boolean) {
            val elapsedMillis = java.lang.String.format(java.util.Locale.US, "%.3f", elapsedNanos / 1_000_000.0)
            println(
                "RWX_KOOL_CPU_TARGET_PROFILE:" +
                        " texture=${texture.sourceName() ?: texture.toString()}" +
                        " size=${width}x${height}" +
                        " success=$success" +
                        " elapsedMs=$elapsedMillis" +
                        " commands=$totalCommands" +
                        " clear=$clearCommands" +
                        " texture=$textureCommands" +
                        " rect=$rectCommands" +
                        " linearTexture=$linearTextureCommands" +
                        " nearestTexture=$nearestTextureCommands" +
                        " fastCopy=$fastTextureCopies" +
                        " sampled=$sampledTextureDraws" +
                        " sampledPixels=$sampledPixels" +
                        " transformedTexture=$transformedTextureCommands" +
                        " tintedTexture=$tintedTextureCommands" +
                        " sourceBlend=$sourceBlendTextureCommands" +
                        " sourceOverBlend=$sourceOverTextureCommands",
            )
        }

        companion object {
            fun createIfEnabled(texture: Texture, frame: KoolCanvasFrame, width: Int, height: Int): CpuTargetProfile? {
                if (!java.lang.Boolean.getBoolean(CPU_TARGET_PROFILE_PROPERTY) || frame.commands.isEmpty()) {
                    return null
                }
                return CpuTargetProfile(texture, width, height)
            }
        }
    }

    private companion object {
        val BACKEND_CAPABILITIES = GraphicsBackendCapabilities(
            fixedLayerBufferPixelSize = 512,
            clearLayerBuffersBeforeCopy = true,
            supportsLayerBufferPreRendering = false,
            supportsSmoothFogLayerBuffers = false,
            requiresFogAtlasLock = false,
            requiresImageTintColorFilter = false,
        )
        val FRAME_SNAPSHOT_SERIAL = AtomicInteger()
        const val CPU_TARGET_PROFILE_PROPERTY: String = "rwx.kool.cpuTargetProfile"
        const val DEFAULT_TEXT_SIZE: Float = 16f
        const val IMAGE_HEADER_READ_LIMIT: Int = 64 * 1024
        const val TILE_IMAGE_LIMIT: Int = 2000
        const val ATLAS_WIDTH: Int = 1024
        const val ATLAS_HEIGHT: Int = 1024
        const val ATLAS_MAX_TEXTURE_WIDTH: Int = 450
        const val ATLAS_MAX_TEXTURE_HEIGHT: Int = 100
        const val MAX_FRAME_SNAPSHOT_DEPENDENCY_DEPTH: Int = 64
        const val PARALLEL_RASTER_MIN_PIXELS: Int = 64 * 1024
        const val PARALLEL_RASTER_MIN_ROWS_PER_WORKER: Int = 16
        const val PNG_COLOR_GRAYSCALE: Int = 0
        const val PNG_COLOR_RGB: Int = 2
        const val PNG_COLOR_INDEXED: Int = 3
        const val PNG_COLOR_GRAYSCALE_ALPHA: Int = 4
        const val PNG_COLOR_RGBA: Int = 6
        val DEFAULT_TEXTURE_SIZE: Pair<Int, Int> = 64 to 64
        val PNG_SIGNATURE: ByteArray = byteArrayOf(
            0x89.toByte(),
            0x50,
            0x4e,
            0x47,
            0x0d,
            0x0a,
            0x1a,
            0x0a,
        )

        object RasterWorkerPool {
            private val workerSerial = AtomicInteger()
            val workerCount: Int = System.getProperty("rwx.koolRasterThreads")
                ?.toIntOrNull()
                ?.coerceIn(1, 8)
                ?: (Runtime.getRuntime().availableProcessors() - 1).coerceIn(1, 4)
            val executor: ExecutorService = Executors.newFixedThreadPool(workerCount) { runnable ->
                Thread(runnable, "rwx-kool-raster-${workerSerial.incrementAndGet()}").apply {
                    isDaemon = true
                }
            }
        }

        fun KoolCanvasTextureId.snapshotId(): KoolCanvasTextureId =
            KoolCanvasTextureId("$value/snapshot-${FRAME_SNAPSHOT_SERIAL.incrementAndGet()}")

        val KoolCanvasTextureId.isFrameSnapshotId: Boolean
            get() = value.contains("/snapshot-")

        data class DecodedImage(
            val width: Int,
            val height: Int,
            val argbPixels: IntArray,
        )

        val drawableNamesById: Map<Int, String> by lazy {
            runCatching {
                R.drawable::class.java.fields.associate { field ->
                    field.getInt(null) to field.name
                }
            }.getOrDefault(emptyMap())
        }

        fun drawableAssetPath(drawableName: String): String? =
            imageFileNames(drawableName)
                .map { fileName -> "drawable/$fileName" }
                .firstOrNull(::assetExists)

        fun assetPathForImagePath(path: String): String? {
            val normalized = path.replace('\\', '/')
            val candidates = mutableListOf<String>()
            if (normalized.startsWith("assets/")) {
                candidates += normalized.removePrefix("assets/")
            }
            if (!normalized.startsWith("/")) {
                candidates += normalized
            }

            val file = File(normalized)
            if (file.isFile) {
                val assetsDir = File("assets").canonicalFile
                val canonicalFile = file.canonicalFile
                if (canonicalFile.path.startsWith(assetsDir.path + File.separator)) {
                    candidates += canonicalFile.relativeTo(assetsDir).invariantSeparatorsPath
                }
            }

            val fileName = normalized.substringAfterLast('/')
            candidates += "drawable/$fileName"
            return candidates.firstOrNull(::assetExists)
        }

        fun assetExists(assetPath: String): Boolean = assetFile(assetPath) != null

        fun readAssetBytesFromFileSystem(assetPath: String): ByteArray? =
            assetFile(assetPath)?.readBytes()

        fun assetFile(assetPath: String): File? {
            val normalized = assetPath.removePrefix("assets/").replace('\\', '/')
            return assetRootCandidates()
                .map { root -> File(root, normalized) }
                .firstOrNull(File::isFile)
        }

        fun assetRootCandidates(): Sequence<File> {
            val cwd = File(System.getProperty("user.dir")).absoluteFile
            return generateSequence(cwd) { it.parentFile }
                .map { File(it, "assets") }
                .filter(File::isDirectory)
        }

        fun encodePng(width: Int, height: Int, argbPixels: IntArray): ByteArray {
            val safeWidth = width.coerceAtLeast(1)
            val safeHeight = height.coerceAtLeast(1)
            val raw = ByteArrayOutputStream((safeWidth * 4 + 1) * safeHeight)
            for (y in 0 until safeHeight) {
                raw.write(0)
                for (x in 0 until safeWidth) {
                    val argb = argbPixels.getOrElse(x + (y * safeWidth)) { 0 }
                    raw.write((argb ushr 16) and 0xff)
                    raw.write((argb ushr 8) and 0xff)
                    raw.write(argb and 0xff)
                    raw.write((argb ushr 24) and 0xff)
                }
            }

            val compressed = ByteArrayOutputStream()
            DeflaterOutputStream(compressed).use { output ->
                output.write(raw.toByteArray())
            }

            return ByteArrayOutputStream().apply {
                write(PNG_SIGNATURE)
                writePngChunk(
                    type = "IHDR",
                    data = ByteArrayOutputStream().apply {
                        writeIntBigEndian(safeWidth)
                        writeIntBigEndian(safeHeight)
                        write(8)
                        write(PNG_COLOR_RGBA)
                        write(0)
                        write(0)
                        write(0)
                    }.toByteArray(),
                )
                writePngChunk("IDAT", compressed.toByteArray())
                writePngChunk("IEND", byteArrayOf())
            }.toByteArray()
        }

        fun readPngImage(bytes: ByteArray): DecodedImage? {
            if (!bytes.isPngHeader()) return null
            var offset = 8
            var width = 0
            var height = 0
            var bitDepth = 0
            var colorType = -1
            var interlaceMethod = 0
            var palette: IntArray? = null
            var paletteAlpha: IntArray? = null
            var transparentGray: Int? = null
            var transparentRgb: IntArray? = null
            val idat = ByteArrayOutputStream()

            while (offset + 8 <= bytes.size) {
                val length = readIntBigEndian(bytes, offset)
                if (length < 0 || offset + 12L + length > bytes.size) return null
                val chunkDataOffset = offset + 8
                val chunkType = bytes.decodeToString(offset + 4, offset + 8)
                when (chunkType) {
                    "IHDR" -> {
                        if (length < 13) return null
                        width = readIntBigEndian(bytes, chunkDataOffset)
                        height = readIntBigEndian(bytes, chunkDataOffset + 4)
                        bitDepth = bytes[chunkDataOffset + 8].toUnsignedInt()
                        colorType = bytes[chunkDataOffset + 9].toUnsignedInt()
                        val compressionMethod = bytes[chunkDataOffset + 10].toUnsignedInt()
                        val filterMethod = bytes[chunkDataOffset + 11].toUnsignedInt()
                        interlaceMethod = bytes[chunkDataOffset + 12].toUnsignedInt()
                        if (
                            width <= 0 ||
                            height <= 0 ||
                            compressionMethod != 0 ||
                            filterMethod != 0 ||
                            interlaceMethod != 0
                        ) {
                            return null
                        }
                    }

                    "PLTE" -> {
                        if (length % 3 != 0) return null
                        palette = IntArray(length / 3) { index ->
                            val base = chunkDataOffset + (index * 3)
                            argb(
                                alpha = 255,
                                red = bytes[base].toUnsignedInt(),
                                green = bytes[base + 1].toUnsignedInt(),
                                blue = bytes[base + 2].toUnsignedInt(),
                            )
                        }
                    }

                    "tRNS" -> {
                        when (colorType) {
                            PNG_COLOR_GRAYSCALE -> if (length >= 2) {
                                transparentGray = readUnsignedShortBigEndian(bytes, chunkDataOffset) and 0xff
                            }

                            PNG_COLOR_RGB -> if (length >= 6) {
                                transparentRgb = intArrayOf(
                                    readUnsignedShortBigEndian(bytes, chunkDataOffset) and 0xff,
                                    readUnsignedShortBigEndian(bytes, chunkDataOffset + 2) and 0xff,
                                    readUnsignedShortBigEndian(bytes, chunkDataOffset + 4) and 0xff,
                                )
                            }

                            PNG_COLOR_INDEXED -> {
                                paletteAlpha = IntArray(length) { index ->
                                    bytes[chunkDataOffset + index].toUnsignedInt()
                                }
                            }
                        }
                    }

                    "IDAT" -> idat.write(bytes, chunkDataOffset, length)
                    "IEND" -> break
                }
                offset += 12 + length
            }

            if (bitDepth != 8) return null
            val channels = pngChannelCount(colorType) ?: return null
            if (width > Int.MAX_VALUE / channels || width * channels > Int.MAX_VALUE / height) return null
            val rowSize = width * channels
            val inflated = inflatePngData(idat.toByteArray(), expectedSize = (rowSize + 1) * height) ?: return null
            return decodePngRows(
                inflated = inflated,
                width = width,
                height = height,
                channels = channels,
                colorType = colorType,
                palette = palette,
                paletteAlpha = paletteAlpha,
                transparentGray = transparentGray,
                transparentRgb = transparentRgb,
            )
        }

        /**
         * Keep the CPU source used by IMMEDIATE targets in step with the platform texture loader.
         * The hand-written PNG path is intentionally kept above for the common case; platform
         * decoders cover JPEG, progressive JPEG and PNG variants outside that subset.
         */
        fun readPlatformImage(bytes: ByteArray, sourceName: String): DecodedImage? =
            readAndroidBitmap(bytes)
                ?: readJvmImage(bytes)
                ?: runCatching {
                    val encoded = Uint8Buffer(bytes.size)
                    bytes.forEachIndexed { index, value -> encoded[index] = value.toUByte() }
                    val image = runBlocking {
                        Assets.loadImageFromBuffer(
                            texData = encoded,
                            mimeType = MimeType.forFileName(sourceName),
                            format = TexFormat.RGBA,
                        )
                    } as? BufferedImageData2d ?: return@runCatching null
                    val rgba = image.data as? Uint8Buffer ?: return@runCatching null
                    val pixelCount = image.width * image.height
                    if (image.width <= 0 || image.height <= 0 || rgba.capacity < pixelCount * 4) {
                        return@runCatching null
                    }
                    val argbPixels = IntArray(pixelCount)
                    for (index in 0 until pixelCount) {
                        val offset = index * 4
                        argbPixels[index] = argb(
                            alpha = rgba[offset + 3].toInt() and 0xff,
                            red = rgba[offset].toInt() and 0xff,
                            green = rgba[offset + 1].toInt() and 0xff,
                            blue = rgba[offset + 2].toInt() and 0xff,
                        )
                    }
                    DecodedImage(image.width, image.height, argbPixels)
                }.getOrNull()

        /**
         * Match the legacy Android loader for formats outside the hand-written PNG path. Reflection
         * keeps this common module free of an Android compile dependency.
         */
        private fun readAndroidBitmap(bytes: ByteArray): DecodedImage? = runCatching {
            val bitmapFactory = Class.forName("android.graphics.BitmapFactory")
            val bitmap = bitmapFactory
                .getMethod(
                    "decodeByteArray",
                    ByteArray::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                .invoke(null, bytes, 0, bytes.size)
                ?: return@runCatching null
            val bitmapClass = bitmap.javaClass
            val width = bitmapClass.getMethod("getWidth").invoke(bitmap) as Int
            val height = bitmapClass.getMethod("getHeight").invoke(bitmap) as Int
            if (width <= 0 || height <= 0) return@runCatching null
            val pixels = IntArray(width * height)
            bitmapClass.getMethod(
                "getPixels",
                IntArray::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
            ).invoke(bitmap, pixels, 0, width, 0, 0, width, height)
            runCatching { bitmapClass.getMethod("recycle").invoke(bitmap) }
            DecodedImage(width, height, pixels)
        }.getOrNull()

        private fun readJvmImage(bytes: ByteArray): DecodedImage? = runCatching {
            val imageIo = Class.forName("javax.imageio.ImageIO")
            val image = imageIo
                .getMethod("read", InputStream::class.java)
                .invoke(null, ByteArrayInputStream(bytes))
                ?: return@runCatching null
            val imageClass = image.javaClass
            val width = imageClass.getMethod("getWidth").invoke(image) as Int
            val height = imageClass.getMethod("getHeight").invoke(image) as Int
            if (width <= 0 || height <= 0) return@runCatching null
            val pixels = IntArray(width * height)
            imageClass.getMethod(
                "getRGB",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                IntArray::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
            ).invoke(image, 0, 0, width, height, pixels, 0, width)
            DecodedImage(width, height, pixels)
        }.getOrNull()

        fun readImageSize(inputStream: InputStream): Pair<Int, Int>? {
            val stream = if (inputStream.markSupported()) inputStream else BufferedInputStream(inputStream)
            stream.mark(IMAGE_HEADER_READ_LIMIT)
            return try {
                val header = ByteArray(24)
                if (stream.readFully(header) < header.size) {
                    null
                } else if (header.isPngHeader()) {
                    readIntBigEndian(header, 16) to readIntBigEndian(header, 20)
                } else {
                    stream.reset()
                    if (stream.read() == 0xff && stream.read() == 0xd8) {
                        readJpegSize(stream)
                    } else {
                        null
                    }
                }
            } finally {
                runCatching { stream.reset() }
            }
        }

        fun imageFileNames(nameWithoutExtension: String): List<String> =
            listOf(
                "$nameWithoutExtension.png",
                "$nameWithoutExtension.9.png",
                "$nameWithoutExtension.jpg",
                "$nameWithoutExtension.jpeg",
            )

        fun readJpegSize(inputStream: InputStream): Pair<Int, Int>? {
            while (true) {
                val prefix = inputStream.read()
                if (prefix < 0) return null
                if (prefix != 0xff) continue

                var marker = inputStream.read()
                while (marker == 0xff) {
                    marker = inputStream.read()
                }
                if (marker < 0) return null
                if (marker == 0xd9 || marker == 0xda) return null
                if (marker == 0x01 || marker in 0xd0..0xd7) continue

                val length = inputStream.readUnsignedShort()
                if (length < 2) return null
                if (isJpegStartOfFrame(marker)) {
                    if (length < 7 || inputStream.read() < 0) return null
                    val height = inputStream.readUnsignedShort()
                    val width = inputStream.readUnsignedShort()
                    return if (width > 0 && height > 0) width to height else null
                }
                if (!inputStream.skipFully(length - 2)) return null
            }
        }

        fun isJpegStartOfFrame(marker: Int): Boolean =
            marker in 0xc0..0xcf && marker != 0xc4 && marker != 0xc8 && marker != 0xcc

        fun inflatePngData(compressed: ByteArray, expectedSize: Int): ByteArray? {
            val inflater = Inflater()
            return try {
                inflater.setInput(compressed)
                val output = ByteArray(expectedSize)
                var total = 0
                while (!inflater.finished() && total < expectedSize) {
                    val count = inflater.inflate(output, total, expectedSize - total)
                    if (count == 0) {
                        if (inflater.needsInput() || inflater.needsDictionary()) break
                    } else {
                        total += count
                    }
                }
                if (total == expectedSize) output else null
            } catch (_: RuntimeException) {
                null
            } finally {
                inflater.end()
            }
        }

        fun decodePngRows(
            inflated: ByteArray,
            width: Int,
            height: Int,
            channels: Int,
            colorType: Int,
            palette: IntArray?,
            paletteAlpha: IntArray?,
            transparentGray: Int?,
            transparentRgb: IntArray?,
        ): DecodedImage? {
            val rowSize = width * channels
            val pixels = IntArray(width * height)
            var inputOffset = 0
            var previous = ByteArray(rowSize)
            var current = ByteArray(rowSize)
            for (y in 0 until height) {
                if (inputOffset >= inflated.size) return null
                val filter = inflated[inputOffset++].toUnsignedInt()
                if (inputOffset + rowSize > inflated.size) return null
                for (x in 0 until rowSize) {
                    val raw = inflated[inputOffset + x].toUnsignedInt()
                    val left = if (x >= channels) current[x - channels].toUnsignedInt() else 0
                    val up = previous[x].toUnsignedInt()
                    val upLeft = if (x >= channels) previous[x - channels].toUnsignedInt() else 0
                    val value = when (filter) {
                        0 -> raw
                        1 -> raw + left
                        2 -> raw + up
                        3 -> raw + ((left + up) / 2)
                        4 -> raw + paethPredictor(left, up, upLeft)
                        else -> return null
                    }
                    current[x] = (value and 0xff).toByte()
                }
                inputOffset += rowSize
                for (x in 0 until width) {
                    pixels[x + (y * width)] = pngPixelToArgb(
                        row = current,
                        x = x,
                        colorType = colorType,
                        palette = palette,
                        paletteAlpha = paletteAlpha,
                        transparentGray = transparentGray,
                        transparentRgb = transparentRgb,
                    ) ?: return null
                }
                val swap = previous
                previous = current
                current = swap
            }
            return DecodedImage(width, height, pixels)
        }

        fun pngPixelToArgb(
            row: ByteArray,
            x: Int,
            colorType: Int,
            palette: IntArray?,
            paletteAlpha: IntArray?,
            transparentGray: Int?,
            transparentRgb: IntArray?,
        ): Int? =
            when (colorType) {
                PNG_COLOR_GRAYSCALE -> {
                    val gray = row[x].toUnsignedInt()
                    argb(
                        alpha = if (transparentGray == gray) 0 else 255,
                        red = gray,
                        green = gray,
                        blue = gray,
                    )
                }

                PNG_COLOR_RGB -> {
                    val base = x * 3
                    val red = row[base].toUnsignedInt()
                    val green = row[base + 1].toUnsignedInt()
                    val blue = row[base + 2].toUnsignedInt()
                    val alpha = if (
                        transparentRgb != null &&
                        transparentRgb[0] == red &&
                        transparentRgb[1] == green &&
                        transparentRgb[2] == blue
                    ) {
                        0
                    } else {
                        255
                    }
                    argb(alpha, red, green, blue)
                }

                PNG_COLOR_INDEXED -> {
                    val index = row[x].toUnsignedInt()
                    val rgb = palette?.getOrNull(index) ?: return null
                    val alpha = paletteAlpha?.getOrNull(index) ?: 255
                    (rgb and 0x00ffffff) or ((alpha and 0xff) shl 24)
                }

                PNG_COLOR_GRAYSCALE_ALPHA -> {
                    val base = x * 2
                    val gray = row[base].toUnsignedInt()
                    argb(
                        alpha = row[base + 1].toUnsignedInt(),
                        red = gray,
                        green = gray,
                        blue = gray,
                    )
                }

                PNG_COLOR_RGBA -> {
                    val base = x * 4
                    argb(
                        alpha = row[base + 3].toUnsignedInt(),
                        red = row[base].toUnsignedInt(),
                        green = row[base + 1].toUnsignedInt(),
                        blue = row[base + 2].toUnsignedInt(),
                    )
                }

                else -> null
            }

        fun pngChannelCount(colorType: Int): Int? =
            when (colorType) {
                PNG_COLOR_GRAYSCALE -> 1
                PNG_COLOR_RGB -> 3
                PNG_COLOR_INDEXED -> 1
                PNG_COLOR_GRAYSCALE_ALPHA -> 2
                PNG_COLOR_RGBA -> 4
                else -> null
            }

        fun paethPredictor(left: Int, up: Int, upLeft: Int): Int {
            val estimate = left + up - upLeft
            val leftDistance = kotlin.math.abs(estimate - left)
            val upDistance = kotlin.math.abs(estimate - up)
            val upLeftDistance = kotlin.math.abs(estimate - upLeft)
            return when {
                leftDistance <= upDistance && leftDistance <= upLeftDistance -> left
                upDistance <= upLeftDistance -> up
                else -> upLeft
            }
        }

        fun ByteArray.isPngHeader(): Boolean =
            size >= 24 &&
                    (this[0].toInt() and 0xff) == 0x89 &&
                    (this[1].toInt() and 0xff) == 0x50 &&
                    (this[2].toInt() and 0xff) == 0x4e &&
                    (this[3].toInt() and 0xff) == 0x47 &&
                    (this[4].toInt() and 0xff) == 0x0d &&
                    (this[5].toInt() and 0xff) == 0x0a &&
                    (this[6].toInt() and 0xff) == 0x1a &&
                    (this[7].toInt() and 0xff) == 0x0a

        fun readIntBigEndian(bytes: ByteArray, offset: Int): Int =
            ((bytes[offset].toInt() and 0xff) shl 24) or
                    ((bytes[offset + 1].toInt() and 0xff) shl 16) or
                    ((bytes[offset + 2].toInt() and 0xff) shl 8) or
                    (bytes[offset + 3].toInt() and 0xff)

        fun readUnsignedShortBigEndian(bytes: ByteArray, offset: Int): Int =
            ((bytes[offset].toInt() and 0xff) shl 8) or
                    (bytes[offset + 1].toInt() and 0xff)

        fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int =
            ((alpha and 0xff) shl 24) or
                    ((red and 0xff) shl 16) or
                    ((green and 0xff) shl 8) or
                    (blue and 0xff)

        fun Byte.toUnsignedInt(): Int = toInt() and 0xff

        fun InputStream.readFully(buffer: ByteArray): Int {
            var total = 0
            while (total < buffer.size) {
                val read = read(buffer, total, buffer.size - total)
                if (read < 0) break
                total += read
            }
            return total
        }

        fun InputStream.readUnsignedShort(): Int {
            val high = read()
            val low = read()
            return if (high < 0 || low < 0) {
                -1
            } else {
                (high shl 8) or low
            }
        }

        fun InputStream.skipFully(byteCount: Int): Boolean {
            var remaining = byteCount.toLong()
            while (remaining > 0L) {
                val skipped = skip(remaining)
                if (skipped > 0L) {
                    remaining -= skipped
                } else if (read() < 0) {
                    return false
                } else {
                    remaining--
                }
            }
            return true
        }

        fun ByteArrayOutputStream.writePngChunk(type: String, data: ByteArray) {
            val typeBytes = type.encodeToByteArray()
            writeIntBigEndian(data.size)
            write(typeBytes)
            write(data)
            val crc = CRC32()
            crc.update(typeBytes)
            crc.update(data)
            writeIntBigEndian(crc.value.toInt())
        }

        fun ByteArrayOutputStream.writeIntBigEndian(value: Int) {
            write((value ushr 24) and 0xff)
            write((value ushr 16) and 0xff)
            write((value ushr 8) and 0xff)
            write(value and 0xff)
        }
    }
}

private class KoolTargetState(
    val engine: KoolGraphicsEngine,
    val mode: RenderTargetMode,
) {
    val activeAuxiliaryIds = linkedSetOf<KoolCanvasTextureId>()
    var commitEnabled: Boolean = true
    var committing: Boolean = false

    fun releaseAuxiliaryTextures(textureStore: KoolCanvasTextureStore) {
        activeAuxiliaryIds.forEach(textureStore::unregister)
        activeAuxiliaryIds.clear()
    }
}

private class KoolBackendTexture(
    private val releaseAction: (Texture) -> Unit,
) : Texture() {
    private var released = false

    override fun o() {
        if (!released) {
            released = true
            releaseAction(this)
        }
        super.o()
    }

    override fun clone(): Texture =
        KoolBackendTexture(releaseAction).also { texture ->
            copyTextureSettingsTo(texture)
            argbPixelsCopy?.let { pixels ->
                texture.j = pixels
            }
        }

    override fun a(width: Int, height: Int, copyPixels: Boolean): Texture =
        KoolBackendTexture(releaseAction).also { texture ->
            copyTextureSettingsTo(texture)
            texture.p = width
            texture.q = height
            texture.updateCenter()
            if (copyPixels) {
                val pixels = IntArray(width * height)
                val copyWidth = minOf(width, p)
                val copyHeight = minOf(height, q)
                for (x in 0 until copyWidth) {
                    for (y in 0 until copyHeight) {
                        pixels[x + (y * width)] = a(x, y)
                    }
                }
                texture.j = pixels
                texture.m = pixels.any { (it ushr 24) != 0xff }
            }
        }
}
