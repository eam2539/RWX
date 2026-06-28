package io.github.rwx.render.canvas

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ui2.MsdfUiShader
import de.fabmax.kool.modules.ui2.UiTextVertexLayout
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MsdfFont
import de.fabmax.kool.util.MsdfFontData
import de.fabmax.kool.util.TextMetrics
import kotlin.math.*
import de.fabmax.kool.pipeline.BlendMode as KoolBlendMode


class KoolCanvasFrameRenderer(
    private val textureStore: KoolCanvasTextureStore = KoolCanvasTextureRegistry,
    private val renderingFrameTextureIds: MutableSet<KoolCanvasTextureId> = mutableSetOf(),
) {
    private var attachedScene: Scene? = null
    private val primitiveMeshes = linkedMapOf<PrimitiveMeshKey, Mesh<VertexLayouts.PositionNormalColor>>()
    private val usedPrimitiveMeshKeys = mutableSetOf<PrimitiveMeshKey>()
    private val textureMeshes = linkedMapOf<TextureMeshKey, TextureMeshEntry>()
    private val usedTextureMeshKeys = mutableSetOf<TextureMeshKey>()
    private val instancedTextureMeshes = linkedMapOf<TextureMeshKey, InstancedTextureMeshEntry>()
    private val usedInstancedTextureMeshKeys = mutableSetOf<TextureMeshKey>()
    private val affineInstancedTextureMeshes = linkedMapOf<TextureMeshKey, AffineInstancedTextureMeshEntry>()
    private val usedAffineInstancedTextureMeshKeys = mutableSetOf<TextureMeshKey>()
    private val teamColorTextureMeshes = linkedMapOf<TeamColorTextureMeshKey, TeamColorTextureMeshEntry>()
    private val usedTeamColorTextureMeshKeys = mutableSetOf<TeamColorTextureMeshKey>()
    private val displacementTextureMeshes = linkedMapOf<DisplacementTextureMeshKey, DisplacementTextureMeshEntry>()
    private val usedDisplacementTextureMeshKeys = mutableSetOf<DisplacementTextureMeshKey>()
    private val textMeshes = linkedMapOf<TextMeshKey, TextMeshEntry>()
    private val usedTextMeshKeys = mutableSetOf<TextMeshKey>()
    private var activeBatchIndex = -1
    private var activeBatchKey: CanvasBatchKey? = null
    private var activeOrderingSegment = 0
    private var pendingOrderingSegmentAdvance = false
    private var projectedOrderingSegmentAdvances = 0
    private var frameProjectionDepth = 0
    private var projectedFrameTextureExpansions = 0
    private var nextFrameProjectionBatchId = 0
    private var activeFrameProjectionBatchId: Int? = null
    private var lastPrimitiveMeshKey: PrimitiveMeshKey? = null
    private var lastPrimitiveMesh: Mesh<VertexLayouts.PositionNormalColor>? = null
    private var lastTextureMeshKey: TextureMeshKey? = null
    private var lastTextureMeshEntry: TextureMeshEntry? = null
    private var lastInstancedTextureMeshKey: TextureMeshKey? = null
    private var lastInstancedTextureMeshEntry: InstancedTextureMeshEntry? = null
    private var alternateInstancedTextureMeshKey: TextureMeshKey? = null
    private var alternateInstancedTextureMeshEntry: InstancedTextureMeshEntry? = null
    private var lastAffineInstancedTextureMeshKey: TextureMeshKey? = null
    private var lastAffineInstancedTextureMeshEntry: AffineInstancedTextureMeshEntry? = null
    private var alternateAffineInstancedTextureMeshKey: TextureMeshKey? = null
    private var alternateAffineInstancedTextureMeshEntry: AffineInstancedTextureMeshEntry? = null
    private var lastResolvedTextureRef: KoolCanvasTextureRef? = null
    private var lastResolvedTextureFilter: KoolCanvasTextureFilter? = null
    private var lastResolvedTexture: Texture2d? = null
    private var alternateResolvedTextureRef: KoolCanvasTextureRef? = null
    private var alternateResolvedTextureFilter: KoolCanvasTextureFilter? = null
    private var alternateResolvedTexture: Texture2d? = null
    private val textureRevisionStore = textureStore as? KoolCanvasTextureRevisionStore
    private var currentTextureRevision = Int.MIN_VALUE
    private val resolvedTextureCacheRefs = arrayOfNulls<KoolCanvasTextureRef>(RESOLVED_TEXTURE_CACHE_SIZE)
    private val resolvedTextureCacheFilters = arrayOfNulls<KoolCanvasTextureFilter>(RESOLVED_TEXTURE_CACHE_SIZE)
    private val resolvedTextureCacheTextures = arrayOfNulls<Texture2d>(RESOLVED_TEXTURE_CACHE_SIZE)
    private var nextResolvedTextureCacheSlot = 0
    private var resolvedTextureCacheCount = 0
    private var resolvedTextureCacheRevision = Int.MIN_VALUE
    private val frameTextureRevisionStore = textureStore as? KoolCanvasFrameTextureRevisionStore
    private var currentFrameTextureRevision = Int.MIN_VALUE
    private val frameTextureCacheIds = arrayOfNulls<KoolCanvasTextureId>(FRAME_TEXTURE_CACHE_SIZE)
    private val frameTextureCacheFrames = arrayOfNulls<KoolCanvasFrame>(FRAME_TEXTURE_CACHE_SIZE)
    private var nextFrameTextureCacheSlot = 0
    private val missingFrameTextureCacheIds = arrayOfNulls<KoolCanvasTextureId>(FRAME_TEXTURE_MISS_CACHE_SIZE)
    private var nextMissingFrameTextureCacheSlot = 0
    private var missingFrameTextureCacheCount = 0
    private var missingFrameTextureCacheRevision = Int.MIN_VALUE
    private var lastMissingFrameTextureId: KoolCanvasTextureId? = null
    private var alternateMissingFrameTextureId: KoolCanvasTextureId? = null
    private var lastFrameTextureId: KoolCanvasTextureId? = null
    private var lastFrameTexture: KoolCanvasFrame? = null
    private var lastUsedPrimitiveMeshKey: PrimitiveMeshKey? = null
    private var lastUsedTextureMeshKey: TextureMeshKey? = null
    private var lastUsedInstancedTextureMeshKey: TextureMeshKey? = null
    private var lastUsedAffineInstancedTextureMeshKey: TextureMeshKey? = null
    private var lastUsedTeamColorTextureMeshKey: TeamColorTextureMeshKey? = null
    private var lastUsedDisplacementTextureMeshKey: DisplacementTextureMeshKey? = null
    private var lastUsedTextMeshKey: TextMeshKey? = null
    private val renderColorCachePaints = arrayOfNulls<KoolCanvasPaint>(RENDER_COLOR_CACHE_SIZE)
    private val renderColorCacheColors = arrayOfNulls<Color>(RENDER_COLOR_CACHE_SIZE)
    private var nextRenderColorCacheSlot = 0
    private var lastRenderColorPaint: KoolCanvasPaint? = null
    private var lastRenderColor: Color? = null
    private var currentViewportHalfWidth = 0f
    private var currentViewportHalfHeight = 0f
    private var meshPruneRequired = false
    private val textureRunTextureIds = arrayOfNulls<KoolCanvasTextureId>(TEXTURE_RUN_CACHE_SIZE)
    private val textureRunFilters = arrayOfNulls<KoolCanvasTextureFilter>(TEXTURE_RUN_CACHE_SIZE)
    private val textureRunEntries = arrayOfNulls<InstancedTextureMeshEntry>(TEXTURE_RUN_CACHE_SIZE)

    private fun createPrimitiveShader(renderBlend: CanvasRenderBlend): KslUnlitShader = KslUnlitShader {
        color { vertexColor() }
        pipeline {
            blendMode = renderBlend.primitiveBlendMode
            cullMethod = CullMethod.NO_CULLING
            depthTest = DepthCompareOp.ALWAYS
            isWriteDepth = false
        }
    }

    private fun createTextureShader(
        texture: Texture2d,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): KoolCanvasTextureShader =
        KoolCanvasTextureShader(
            PipelineConfig(
                blendMode = renderBlend.textureBlendMode(premultipliedAlpha),
                cullMethod = CullMethod.NO_CULLING,
                depthTest = DepthCompareOp.ALWAYS,
                isWriteDepth = false,
            ),
            premultipliedAlpha = premultipliedAlpha,
            multipliesRgbByAlpha = renderBlend == CanvasRenderBlend.Additive,
        ).apply {
            colorMap = texture
        }

    private fun createInstancedTextureShader(
        texture: Texture2d,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): KoolCanvasInstancedTextureShader =
        KoolCanvasInstancedTextureShader(
            PipelineConfig(
                blendMode = renderBlend.textureBlendMode(premultipliedAlpha),
                cullMethod = CullMethod.NO_CULLING,
                depthTest = DepthCompareOp.ALWAYS,
                isWriteDepth = false,
            ),
            premultipliedAlpha = premultipliedAlpha,
            multipliesRgbByAlpha = renderBlend == CanvasRenderBlend.Additive,
        ).apply {
            colorMap = texture
        }

    private fun createAffineInstancedTextureShader(
        texture: Texture2d,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): KoolCanvasAffineInstancedTextureShader =
        KoolCanvasAffineInstancedTextureShader(
            PipelineConfig(
                blendMode = renderBlend.textureBlendMode(premultipliedAlpha),
                cullMethod = CullMethod.NO_CULLING,
                depthTest = DepthCompareOp.ALWAYS,
                isWriteDepth = false,
            ),
            premultipliedAlpha = premultipliedAlpha,
            multipliesRgbByAlpha = renderBlend == CanvasRenderBlend.Additive,
        ).apply {
            colorMap = texture
        }

    private data class TextureMeshEntry(
        var texture: Texture2d,
        val mesh: Mesh<VertexLayouts.PositionNormalTexCoordColor>,
        val shader: KoolCanvasTextureShader,
    )

    private data class InstancedTextureMeshEntry(
        var texture: Texture2d,
        val mesh: Mesh<VertexLayouts.Position>,
        val shader: KoolCanvasInstancedTextureShader,
        val batchKey: CanvasBatchKey.Texture,
        val instances: MeshInstanceList<KoolCanvasTextureInstanceLayout>,
    )

    private data class AffineInstancedTextureMeshEntry(
        var texture: Texture2d,
        val mesh: Mesh<VertexLayouts.Position>,
        val shader: KoolCanvasAffineInstancedTextureShader,
        val batchKey: CanvasBatchKey.AffineTexture,
        val instances: MeshInstanceList<KoolCanvasAffineTextureInstanceLayout>,
    )

    private data class TeamColorTextureMeshEntry(
        var texture: Texture2d,
        val mesh: Mesh<VertexLayouts.PositionNormalTexCoordColor>,
        val shader: KoolTeamColorShader,
    )

    private data class DisplacementTextureMeshEntry(
        var displacementMap: Texture2d,
        var screenBase: Texture2d,
        val mesh: Mesh<VertexLayouts.PositionNormalTexCoordColor>,
        val shader: KoolDisplacementShader,
    )

    private data class PrimitiveMeshKey(
        val batchIndex: Int?,
        val orderingSegment: Int?,
        val projectionBatchId: Int?,
        val renderBlend: CanvasRenderBlend,
        val layer: PrimitiveMeshLayer,
    )

    private data class TextureMeshKey(
        val batchIndex: Int?,
        val orderingSegment: Int?,
        val projectionBatchId: Int?,
        val textureId: KoolCanvasTextureId,
        val textureFilter: KoolCanvasTextureFilter,
        val renderBlend: CanvasRenderBlend,
        val premultipliedAlpha: Boolean,
    )

    private data class TeamColorTextureMeshKey(
        val batchIndex: Int?,
        val orderingSegment: Int?,
        val projectionBatchId: Int?,
        val textureId: KoolCanvasTextureId,
        val textureFilter: KoolCanvasTextureFilter,
        val renderBlend: CanvasRenderBlend,
        val effect: KoolCanvasTextureEffect.TeamColor,
    )

    private data class DisplacementTextureMeshKey(
        val batchIndex: Int?,
        val orderingSegment: Int?,
        val projectionBatchId: Int?,
        val textureId: KoolCanvasTextureId,
        val textureFilter: KoolCanvasTextureFilter,
        val renderBlend: CanvasRenderBlend,
        val effect: KoolCanvasTextureEffect.Displacement,
    )

    private data class TextMeshEntry(
        val fontData: MsdfFontData,
        val renderBlend: CanvasRenderBlend,
        val mesh: Mesh<UiTextVertexLayout>,
        val shader: MsdfUiShader,
        val builder: MeshBuilder<UiTextVertexLayout>,
    )

    private data class TextMeshKey(
        val batchIndex: Int?,
        val orderingSegment: Int?,
        val projectionBatchId: Int?,
        val fontData: MsdfFontData,
        val renderBlend: CanvasRenderBlend,
        val typefaceKey: String?,
    )

    private sealed interface CanvasBatchKey {
        data class Primitive(
            val renderBlend: CanvasRenderBlend,
            val layer: PrimitiveMeshLayer,
        ) : CanvasBatchKey

        data class Texture(
            val textureId: KoolCanvasTextureId,
            val textureFilter: KoolCanvasTextureFilter,
            val renderBlend: CanvasRenderBlend,
            val premultipliedAlpha: Boolean,
        ) : CanvasBatchKey

        data class AffineTexture(
            val textureId: KoolCanvasTextureId,
            val textureFilter: KoolCanvasTextureFilter,
            val renderBlend: CanvasRenderBlend,
            val premultipliedAlpha: Boolean,
        ) : CanvasBatchKey

        data class TeamColorTexture(
            val textureId: KoolCanvasTextureId,
            val textureFilter: KoolCanvasTextureFilter,
            val renderBlend: CanvasRenderBlend,
            val effect: KoolCanvasTextureEffect.TeamColor,
        ) : CanvasBatchKey

        data class DisplacementTexture(
            val textureId: KoolCanvasTextureId,
            val textureFilter: KoolCanvasTextureFilter,
            val renderBlend: CanvasRenderBlend,
            val effect: KoolCanvasTextureEffect.Displacement,
        ) : CanvasBatchKey

        data class Text(
            val fontData: MsdfFontData,
            val renderBlend: CanvasRenderBlend,
            val typefaceKey: String?,
        ) : CanvasBatchKey
    }

    fun render(scene: Scene, frame: KoolCanvasFrame) {
        resetSceneCachesIfNeeded(scene)
        configureCamera(scene, frame.viewport)
        scene.clearColor = ClearColorFill(KoolCanvasColor.Transparent.toKoolColor())
        currentTextureRevision = textureRevisionStore?.textureRevision ?: Int.MIN_VALUE
        currentFrameTextureRevision = frameTextureRevisionStore?.frameTextureRevision ?: Int.MIN_VALUE
        activeBatchIndex = -1
        activeBatchKey = null
        activeOrderingSegment = 0
        pendingOrderingSegmentAdvance = false
        projectedOrderingSegmentAdvances = 0
        projectedFrameTextureExpansions = 0
        nextFrameProjectionBatchId = 0
        activeFrameProjectionBatchId = null
        meshPruneRequired = false
        lastPrimitiveMeshKey = null
        lastPrimitiveMesh = null
        lastTextureMeshKey = null
        lastTextureMeshEntry = null
        lastInstancedTextureMeshKey = null
        lastInstancedTextureMeshEntry = null
        alternateInstancedTextureMeshKey = null
        alternateInstancedTextureMeshEntry = null
        lastAffineInstancedTextureMeshKey = null
        lastAffineInstancedTextureMeshEntry = null
        alternateAffineInstancedTextureMeshKey = null
        alternateAffineInstancedTextureMeshEntry = null
        if (textureRevisionStore == null) {
            clearResolvedTextureCache()
        }
        clearFrameTextureCache()
        lastUsedPrimitiveMeshKey = null
        lastUsedTextureMeshKey = null
        lastUsedInstancedTextureMeshKey = null
        lastUsedAffineInstancedTextureMeshKey = null
        lastUsedTeamColorTextureMeshKey = null
        lastUsedDisplacementTextureMeshKey = null
        lastUsedTextMeshKey = null
        usedPrimitiveMeshKeys.clear()
        usedTextureMeshKeys.clear()
        usedInstancedTextureMeshKeys.clear()
        usedAffineInstancedTextureMeshKeys.clear()
        usedTeamColorTextureMeshKeys.clear()
        usedDisplacementTextureMeshKeys.clear()
        usedTextMeshKeys.clear()

        for (mesh in primitiveMeshes.values) {
            mesh.geometry.clear()
            mesh.isVisible = false
        }
        for (entry in textureMeshes.values) {
            entry.mesh.geometry.clear()
            entry.mesh.isVisible = false
        }
        for (entry in instancedTextureMeshes.values) {
            entry.instances.clear()
            entry.mesh.isVisible = false
        }
        for (entry in affineInstancedTextureMeshes.values) {
            entry.instances.clear()
            entry.mesh.isVisible = false
        }
        for (entry in teamColorTextureMeshes.values) {
            entry.mesh.geometry.clear()
            entry.mesh.isVisible = false
        }
        for (entry in displacementTextureMeshes.values) {
            entry.mesh.geometry.clear()
            entry.mesh.isVisible = false
        }
        for (entry in textMeshes.values) {
            entry.builder.clear()
            entry.mesh.isVisible = false
        }

        val viewport = frame.viewport
        currentViewportHalfWidth = viewport.width * 0.5f
        currentViewportHalfHeight = viewport.height * 0.5f
        val commands = frame.commands
        var commandIndex = 0
        while (commandIndex < commands.size) {
            val command = commands[commandIndex]
            when (command) {
                is KoolCanvasCommand.DrawTexture -> if (command.state.renderTarget == null) {
                    val textureRunLength = addUnorderedDefaultInstancedTextureRun(
                        scene = scene,
                        viewport = viewport,
                        commands = commands,
                        startIndex = commandIndex,
                        firstCommand = command,
                    )
                    if (textureRunLength > 0) {
                        commandIndex += textureRunLength
                        continue
                    } else {
                        val framePrimitiveGeometry = addFrameTexture(scene, viewport, command)
                        if (framePrimitiveGeometry == null) {
                            addTexture(scene, viewport, command)
                        }
                    }
                }

                is KoolCanvasCommand.DrawRect -> if (command.state.renderTarget == null) {
                    val rectRunLength = addFilledIdentityPrimitiveRectRun(
                        scene = scene,
                        viewport = viewport,
                        commands = commands,
                        startIndex = commandIndex,
                        firstCommand = command,
                    )
                    if (rectRunLength > 0) {
                        commandIndex += rectRunLength
                        continue
                    } else {
                        addRect(scene, viewport, command)
                    }
                }

                is KoolCanvasCommand.DrawLine -> if (command.state.renderTarget == null) {
                    addLine(scene, viewport, command)
                }

                is KoolCanvasCommand.DrawCircle -> if (command.state.renderTarget == null) {
                    addCircle(scene, viewport, command)
                }

                is KoolCanvasCommand.DrawText -> if (command.state.renderTarget == null) {
                    addText(scene, viewport, command)
                }

                is KoolCanvasCommand.Clear -> if (command.renderTarget == null) {
                    val clearColor = ClearColorFill(command.toClearColor())
                    scene.clearColor = clearColor
                }
            }
            commandIndex++
        }
        for (mesh in primitiveMeshes.values) {
            mesh.isVisible = !mesh.geometry.isEmpty()
        }
        for (entry in textureMeshes.values) {
            entry.mesh.isVisible = !entry.mesh.geometry.isEmpty()
        }
        for (entry in instancedTextureMeshes.values) {
            val instances = entry.instances
            val hasInstances = instances.numInstances != 0
            if (hasInstances) {
                instances.incrementModCount()
            }
            entry.mesh.isVisible = hasInstances
        }
        for (entry in affineInstancedTextureMeshes.values) {
            val instances = entry.instances
            val hasInstances = instances.numInstances != 0
            if (hasInstances) {
                instances.incrementModCount()
            }
            entry.mesh.isVisible = hasInstances
        }
        for (entry in teamColorTextureMeshes.values) {
            entry.mesh.isVisible = !entry.mesh.geometry.isEmpty()
        }
        for (entry in displacementTextureMeshes.values) {
            entry.mesh.isVisible = !entry.mesh.geometry.isEmpty()
        }
        for (entry in textMeshes.values) {
            entry.mesh.isVisible = !entry.mesh.geometry.isEmpty()
        }
        pruneUnusedMeshes(scene)
        (textureStore as? KoolCanvasRetiredTextureReleaser)?.releaseRetiredTextures()
    }

    private fun resetSceneCachesIfNeeded(scene: Scene) {
        if (attachedScene !== scene) {
            primitiveMeshes.clear()
            usedPrimitiveMeshKeys.clear()
            textureMeshes.clear()
            usedTextureMeshKeys.clear()
            instancedTextureMeshes.clear()
            usedInstancedTextureMeshKeys.clear()
            affineInstancedTextureMeshes.clear()
            usedAffineInstancedTextureMeshKeys.clear()
            teamColorTextureMeshes.clear()
            usedTeamColorTextureMeshKeys.clear()
            displacementTextureMeshes.clear()
            usedDisplacementTextureMeshKeys.clear()
            textMeshes.clear()
            usedTextMeshKeys.clear()
            lastPrimitiveMeshKey = null
            lastPrimitiveMesh = null
            lastTextureMeshKey = null
            lastTextureMeshEntry = null
            lastInstancedTextureMeshKey = null
            lastInstancedTextureMeshEntry = null
            alternateInstancedTextureMeshKey = null
            alternateInstancedTextureMeshEntry = null
            lastAffineInstancedTextureMeshKey = null
            lastAffineInstancedTextureMeshEntry = null
            alternateAffineInstancedTextureMeshKey = null
            alternateAffineInstancedTextureMeshEntry = null
            clearResolvedTextureCache()
            clearFrameTextureCache()
            lastUsedPrimitiveMeshKey = null
            lastUsedTextureMeshKey = null
            lastUsedInstancedTextureMeshKey = null
            lastUsedAffineInstancedTextureMeshKey = null
            lastUsedTeamColorTextureMeshKey = null
            lastUsedDisplacementTextureMeshKey = null
            lastUsedTextMeshKey = null
            attachedScene = scene
        }
    }

    private fun pruneUnusedMeshes(scene: Scene) {
        pruneUnusedMeshMap(scene, primitiveMeshes, usedPrimitiveMeshKeys) { it }
        pruneUnusedMeshMap(scene, textureMeshes, usedTextureMeshKeys) { it.mesh }
        pruneUnusedMeshMap(scene, instancedTextureMeshes, usedInstancedTextureMeshKeys) { it.mesh }
        pruneUnusedMeshMap(scene, affineInstancedTextureMeshes, usedAffineInstancedTextureMeshKeys) { it.mesh }
        pruneUnusedMeshMap(scene, teamColorTextureMeshes, usedTeamColorTextureMeshKeys) { it.mesh }
        pruneUnusedMeshMap(scene, displacementTextureMeshes, usedDisplacementTextureMeshKeys) { it.mesh }
        pruneUnusedMeshMap(scene, textMeshes, usedTextMeshKeys) { it.mesh }
    }

    private fun markPrimitiveMeshKeyUsed(key: PrimitiveMeshKey) {
        if (lastUsedPrimitiveMeshKey != key) {
            usedPrimitiveMeshKeys += key
            lastUsedPrimitiveMeshKey = key
        }
    }

    private fun markTextureMeshKeyUsed(key: TextureMeshKey) {
        if (lastUsedTextureMeshKey != key) {
            usedTextureMeshKeys += key
            lastUsedTextureMeshKey = key
        }
    }

    private fun markInstancedTextureMeshKeyUsed(key: TextureMeshKey) {
        if (lastUsedInstancedTextureMeshKey != key) {
            usedInstancedTextureMeshKeys += key
            lastUsedInstancedTextureMeshKey = key
        }
    }

    private fun markAffineInstancedTextureMeshKeyUsed(key: TextureMeshKey) {
        if (lastUsedAffineInstancedTextureMeshKey != key) {
            usedAffineInstancedTextureMeshKeys += key
            lastUsedAffineInstancedTextureMeshKey = key
        }
    }

    private fun markTeamColorTextureMeshKeyUsed(key: TeamColorTextureMeshKey) {
        if (lastUsedTeamColorTextureMeshKey != key) {
            usedTeamColorTextureMeshKeys += key
            lastUsedTeamColorTextureMeshKey = key
        }
    }

    private fun markDisplacementTextureMeshKeyUsed(key: DisplacementTextureMeshKey) {
        if (lastUsedDisplacementTextureMeshKey != key) {
            usedDisplacementTextureMeshKeys += key
            lastUsedDisplacementTextureMeshKey = key
        }
    }

    private fun markTextMeshKeyUsed(key: TextMeshKey) {
        if (lastUsedTextMeshKey != key) {
            usedTextMeshKeys += key
            lastUsedTextMeshKey = key
        }
    }

    private fun <K, V> pruneUnusedMeshMap(
        scene: Scene,
        meshes: MutableMap<K, V>,
        usedKeys: Set<K>,
        meshOf: (V) -> Mesh<*>,
    ) {
        if (!meshPruneRequired && meshes.size == usedKeys.size) {
            return
        }
        val iterator = meshes.iterator()
        while (iterator.hasNext()) {
            val (key, entry) = iterator.next()
            if (key !in usedKeys) {
                val mesh = meshOf(entry)
                scene.removeNode(mesh)
                mesh.release()
                iterator.remove()
            }
        }
    }

    private fun ensurePrimitiveMesh(
        scene: Scene,
        renderBlend: CanvasRenderBlend,
        layer: PrimitiveMeshLayer,
        ordered: Boolean,
    ): Mesh<VertexLayouts.PositionNormalColor> {
        val batchIndex = batchIndexForPrimitive(renderBlend, layer)
        val keyBatchIndex = batchIndex.takeIf { ordered }
        val keyOrderingSegment = activeOrderingSegment.takeIf { !ordered }
        val keyProjectionBatchId = activeFrameProjectionBatchId.takeIf { !ordered }
        val cachedKey = lastPrimitiveMeshKey
        if (cachedKey != null &&
            cachedKey.batchIndex == keyBatchIndex &&
            cachedKey.orderingSegment == keyOrderingSegment &&
            cachedKey.projectionBatchId == keyProjectionBatchId &&
            cachedKey.renderBlend == renderBlend &&
            cachedKey.layer == layer
        ) {
            markPrimitiveMeshKeyUsed(cachedKey)
            val cachedMesh = lastPrimitiveMesh!!
            placeBatchNodeIfFirstUse(scene, cachedMesh, batchIndex)
            return cachedMesh
        }
        val key = PrimitiveMeshKey(
            batchIndex = keyBatchIndex,
            orderingSegment = keyOrderingSegment,
            projectionBatchId = keyProjectionBatchId,
            renderBlend = renderBlend,
            layer = layer,
        )
        markPrimitiveMeshKeyUsed(key)
        val existing = primitiveMeshes[key]
        if (existing != null) {
            lastPrimitiveMeshKey = key
            lastPrimitiveMesh = existing
            placeBatchNodeIfFirstUse(scene, existing, batchIndex)
            return existing
        }

        val mesh = Mesh(
            geometry = IndexedVertexList(VertexLayouts.PositionNormalColor, usage = Usage.DYNAMIC),
            name = "rwx-kool-canvas-primitives${renderBlend.meshNameSuffix}${layer.meshNameSuffix}",
        ).apply {
            shader = createPrimitiveShader(renderBlend)
            isOpaque = false
            isVisible = false
        }
        placeBatchNode(scene, mesh, batchIndex)
        lastPrimitiveMeshKey = key
        lastPrimitiveMesh = mesh
        meshPruneRequired = true
        return mesh.also { primitiveMeshes[key] = it }
    }

    private fun ensureTextureMesh(
        scene: Scene,
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        texture: Texture2d,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
        ordered: Boolean,
    ): TextureMeshEntry {
        val batchIndex = batchIndexForTexture(textureId, textureFilter, renderBlend, premultipliedAlpha)
        val keyBatchIndex = batchIndex.takeIf { ordered }
        val keyOrderingSegment = activeOrderingSegment.takeIf { !ordered }
        val keyProjectionBatchId = activeFrameProjectionBatchId.takeIf { !ordered }
        val cachedKey = lastTextureMeshKey
        if (cachedKey != null &&
            cachedKey.batchIndex == keyBatchIndex &&
            cachedKey.orderingSegment == keyOrderingSegment &&
            cachedKey.projectionBatchId == keyProjectionBatchId &&
            cachedKey.textureId == textureId &&
            cachedKey.textureFilter == textureFilter &&
            cachedKey.renderBlend == renderBlend &&
            cachedKey.premultipliedAlpha == premultipliedAlpha
        ) {
            markTextureMeshKeyUsed(cachedKey)
            val cachedEntry = lastTextureMeshEntry!!
            if (cachedEntry.texture !== texture) {
                cachedEntry.texture = texture
                cachedEntry.shader.colorMap = texture
            }
            placeBatchNodeIfFirstUse(scene, cachedEntry.mesh, batchIndex)
            return cachedEntry
        }
        val key = TextureMeshKey(
            batchIndex = keyBatchIndex,
            orderingSegment = keyOrderingSegment,
            projectionBatchId = keyProjectionBatchId,
            textureId = textureId,
            textureFilter = textureFilter,
            renderBlend = renderBlend,
            premultipliedAlpha = premultipliedAlpha,
        )
        markTextureMeshKeyUsed(key)
        val existing = textureMeshes[key]
        if (existing != null) {
            if (existing.texture !== texture) {
                existing.texture = texture
                existing.shader.colorMap = texture
            }
            lastTextureMeshKey = key
            lastTextureMeshEntry = existing
            placeBatchNodeIfFirstUse(scene, existing.mesh, batchIndex)
            return existing
        }

        val shader = createTextureShader(texture, renderBlend, premultipliedAlpha)
        val mesh = Mesh(
            geometry = IndexedVertexList(VertexLayouts.PositionNormalTexCoordColor, usage = Usage.DYNAMIC),
            name = "rwx-kool-canvas-texture-${textureId.value}${renderBlend.meshNameSuffix}",
        ).apply {
            this.shader = shader
            isOpaque = false
            isVisible = false
        }
        placeBatchNode(scene, mesh, batchIndex)
        lastTextureMeshKey = key
        lastTextureMeshEntry = TextureMeshEntry(texture, mesh, shader)
        meshPruneRequired = true
        return lastTextureMeshEntry!!.also {
            textureMeshes[key] = it
        }
    }

    private fun ensureInstancedTextureMesh(
        scene: Scene,
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        texture: Texture2d,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
        ordered: Boolean,
    ): InstancedTextureMeshEntry {
        if (!ordered) {
            return ensureUnorderedInstancedTextureMesh(
                scene,
                textureId,
                textureFilter,
                texture,
                renderBlend,
                premultipliedAlpha,
            )
        }
        val batchIndex = batchIndexForTexture(textureId, textureFilter, renderBlend, premultipliedAlpha)
        val keyBatchIndex = batchIndex.takeIf { ordered }
        val keyOrderingSegment = activeOrderingSegment.takeIf { !ordered }
        val keyProjectionBatchId = activeFrameProjectionBatchId.takeIf { !ordered }
        val cachedKey = lastInstancedTextureMeshKey
        if (cachedKey != null &&
            cachedKey.batchIndex == keyBatchIndex &&
            cachedKey.orderingSegment == keyOrderingSegment &&
            cachedKey.projectionBatchId == keyProjectionBatchId &&
            cachedKey.textureId == textureId &&
            cachedKey.textureFilter == textureFilter &&
            cachedKey.renderBlend == renderBlend &&
            cachedKey.premultipliedAlpha == premultipliedAlpha
        ) {
            markInstancedTextureMeshKeyUsed(cachedKey)
            val cachedEntry = lastInstancedTextureMeshEntry!!
            if (cachedEntry.texture !== texture) {
                cachedEntry.texture = texture
                cachedEntry.shader.colorMap = texture
            }
            placeInstancedBatchNodeIfFirstUse(scene, cachedEntry.mesh, batchIndex)
            return cachedEntry
        }
        val key = TextureMeshKey(
            batchIndex = keyBatchIndex,
            orderingSegment = keyOrderingSegment,
            projectionBatchId = keyProjectionBatchId,
            textureId = textureId,
            textureFilter = textureFilter,
            renderBlend = renderBlend,
            premultipliedAlpha = premultipliedAlpha,
        )
        markInstancedTextureMeshKeyUsed(key)
        val existing = instancedTextureMeshes[key]
        if (existing != null) {
            if (existing.texture !== texture) {
                existing.texture = texture
                existing.shader.colorMap = texture
            }
            lastInstancedTextureMeshKey = key
            lastInstancedTextureMeshEntry = existing
            placeInstancedBatchNodeIfFirstUse(scene, existing.mesh, batchIndex)
            return existing
        }

        val batchKey = CanvasBatchKey.Texture(textureId, textureFilter, renderBlend, premultipliedAlpha)
        val shader = createInstancedTextureShader(texture, renderBlend, premultipliedAlpha)
        val instances =
            MeshInstanceList(KoolCanvasTextureInstanceLayout, initialSize = ORDERED_TEXTURE_INSTANCE_INITIAL_SIZE)
        val mesh = createInstancedTextureMesh(
            textureId,
            renderBlend,
            shader,
            instances,
        )
        placeBatchNode(scene, mesh, batchIndex)
        lastInstancedTextureMeshKey = key
        lastInstancedTextureMeshEntry = InstancedTextureMeshEntry(texture, mesh, shader, batchKey, instances)
        meshPruneRequired = true
        return lastInstancedTextureMeshEntry!!.also {
            instancedTextureMeshes[key] = it
        }
    }

    private fun ensureUnorderedInstancedTextureMesh(
        scene: Scene,
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        texture: Texture2d,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): InstancedTextureMeshEntry {
        if (pendingOrderingSegmentAdvance) {
            val matchesActiveBatch =
                activeTextureBatchMatches(textureId, textureFilter, renderBlend, premultipliedAlpha)
            if (!matchesActiveBatch) {
                advanceOrderingSegmentIfAllowed()
            }
        }
        val keyOrderingSegment = activeOrderingSegment
        val keyProjectionBatchId = activeFrameProjectionBatchId
        val cachedKey = lastInstancedTextureMeshKey
        if (cachedKey.matchesUnorderedTexture(
                keyOrderingSegment,
                keyProjectionBatchId,
                textureId,
                textureFilter,
                renderBlend,
                premultipliedAlpha,
            )
        ) {
            val cachedEntry = lastInstancedTextureMeshEntry!!
            val batchIndex = activateTextureBatch(cachedEntry.batchKey)
            markInstancedTextureMeshKeyUsed(cachedKey!!)
            updateInstancedTextureEntry(cachedEntry, texture)
            placeInstancedBatchNodeIfFirstUse(scene, cachedEntry.mesh, batchIndex)
            return cachedEntry
        }
        val alternateKey = alternateInstancedTextureMeshKey
        if (alternateKey.matchesUnorderedTexture(
                keyOrderingSegment,
                keyProjectionBatchId,
                textureId,
                textureFilter,
                renderBlend,
                premultipliedAlpha,
            )
        ) {
            val alternateEntry = alternateInstancedTextureMeshEntry!!
            alternateInstancedTextureMeshKey = lastInstancedTextureMeshKey
            alternateInstancedTextureMeshEntry = lastInstancedTextureMeshEntry
            lastInstancedTextureMeshKey = alternateKey
            lastInstancedTextureMeshEntry = alternateEntry
            val batchIndex = activateTextureBatch(alternateEntry.batchKey)
            markInstancedTextureMeshKeyUsed(alternateKey!!)
            updateInstancedTextureEntry(alternateEntry, texture)
            placeInstancedBatchNodeIfFirstUse(scene, alternateEntry.mesh, batchIndex)
            return alternateEntry
        }

        val key = TextureMeshKey(
            batchIndex = null,
            orderingSegment = keyOrderingSegment,
            projectionBatchId = keyProjectionBatchId,
            textureId = textureId,
            textureFilter = textureFilter,
            renderBlend = renderBlend,
            premultipliedAlpha = premultipliedAlpha,
        )
        val existing = instancedTextureMeshes[key]
        if (existing != null) {
            alternateInstancedTextureMeshKey = lastInstancedTextureMeshKey
            alternateInstancedTextureMeshEntry = lastInstancedTextureMeshEntry
            lastInstancedTextureMeshKey = key
            lastInstancedTextureMeshEntry = existing
            val batchIndex = activateTextureBatch(existing.batchKey)
            markInstancedTextureMeshKeyUsed(key)
            updateInstancedTextureEntry(existing, texture)
            placeInstancedBatchNodeIfFirstUse(scene, existing.mesh, batchIndex)
            return existing
        }

        val batchKey = CanvasBatchKey.Texture(textureId, textureFilter, renderBlend, premultipliedAlpha)
        val batchIndex = activateTextureBatch(batchKey)
        val shader = createInstancedTextureShader(texture, renderBlend, premultipliedAlpha)
        val instances =
            MeshInstanceList(KoolCanvasTextureInstanceLayout, initialSize = UNORDERED_TEXTURE_INSTANCE_INITIAL_SIZE)
        val mesh = createInstancedTextureMesh(
            textureId,
            renderBlend,
            shader,
            instances,
        )
        placeBatchNode(scene, mesh, batchIndex)
        val entry = InstancedTextureMeshEntry(texture, mesh, shader, batchKey, instances)
        alternateInstancedTextureMeshKey = lastInstancedTextureMeshKey
        alternateInstancedTextureMeshEntry = lastInstancedTextureMeshEntry
        lastInstancedTextureMeshKey = key
        lastInstancedTextureMeshEntry = entry
        markInstancedTextureMeshKeyUsed(key)
        meshPruneRequired = true
        instancedTextureMeshes[key] = entry
        return entry
    }

    private fun TextureMeshKey?.matchesUnorderedTexture(
        orderingSegment: Int,
        projectionBatchId: Int?,
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): Boolean =
        this != null &&
                batchIndex == null &&
                this.orderingSegment == orderingSegment &&
                this.projectionBatchId == projectionBatchId &&
                this.textureId == textureId &&
                this.textureFilter == textureFilter &&
                this.renderBlend == renderBlend &&
                this.premultipliedAlpha == premultipliedAlpha

    private fun activeTextureBatchMatches(
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): Boolean {
        val current = activeBatchKey as? CanvasBatchKey.Texture ?: return false
        return current.textureId == textureId &&
                current.textureFilter == textureFilter &&
                current.renderBlend == renderBlend &&
                current.premultipliedAlpha == premultipliedAlpha
    }

    private fun activateTextureBatch(batchKey: CanvasBatchKey.Texture): Int {
        if (activeBatchKey !== batchKey && activeBatchKey != batchKey) {
            activeBatchIndex++
            activeBatchKey = batchKey
        }
        return activeBatchIndex
    }

    private fun updateInstancedTextureEntry(entry: InstancedTextureMeshEntry, texture: Texture2d) {
        if (entry.texture !== texture) {
            entry.texture = texture
            entry.shader.colorMap = texture
        }
    }

    private fun createInstancedTextureMesh(
        textureId: KoolCanvasTextureId,
        renderBlend: CanvasRenderBlend,
        shader: KoolCanvasInstancedTextureShader,
        instances: MeshInstanceList<KoolCanvasTextureInstanceLayout>,
    ): Mesh<VertexLayouts.Position> =
        Mesh(
            geometry = IndexedVertexList(VertexLayouts.Position, usage = Usage.STATIC),
            instances = instances,
            name = "rwx-kool-canvas-texture-${textureId.value}${renderBlend.meshNameSuffix}",
        ).apply {
            this.shader = shader
            isOpaque = false
            isVisible = false
            geometry.addVertex { layout ->
                set(layout.position, 0f, 0f, 0f)
            }
            geometry.addVertex { layout ->
                set(layout.position, 1f, 0f, 0f)
            }
            geometry.addVertex { layout ->
                set(layout.position, 1f, 1f, 0f)
            }
            geometry.addVertex { layout ->
                set(layout.position, 0f, 1f, 0f)
            }
            geometry.addTriIndices(0, 1, 2)
            geometry.addTriIndices(0, 2, 3)
        }

    private fun ensureAffineInstancedTextureMesh(
        scene: Scene,
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        texture: Texture2d,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
        ordered: Boolean,
    ): AffineInstancedTextureMeshEntry {
        if (!ordered) {
            return ensureUnorderedAffineInstancedTextureMesh(
                scene,
                textureId,
                textureFilter,
                texture,
                renderBlend,
                premultipliedAlpha,
            )
        }
        val batchIndex = batchIndexForAffineTexture(textureId, textureFilter, renderBlend, premultipliedAlpha)
        val keyBatchIndex = batchIndex.takeIf { ordered }
        val keyOrderingSegment = activeOrderingSegment.takeIf { !ordered }
        val keyProjectionBatchId = activeFrameProjectionBatchId.takeIf { !ordered }
        val cachedKey = lastAffineInstancedTextureMeshKey
        if (cachedKey != null &&
            cachedKey.batchIndex == keyBatchIndex &&
            cachedKey.orderingSegment == keyOrderingSegment &&
            cachedKey.projectionBatchId == keyProjectionBatchId &&
            cachedKey.textureId == textureId &&
            cachedKey.textureFilter == textureFilter &&
            cachedKey.renderBlend == renderBlend &&
            cachedKey.premultipliedAlpha == premultipliedAlpha
        ) {
            markAffineInstancedTextureMeshKeyUsed(cachedKey)
            val cachedEntry = lastAffineInstancedTextureMeshEntry!!
            updateAffineInstancedTextureEntry(cachedEntry, texture)
            placeInstancedBatchNodeIfFirstUse(scene, cachedEntry.mesh, batchIndex)
            return cachedEntry
        }
        val key = TextureMeshKey(
            batchIndex = keyBatchIndex,
            orderingSegment = keyOrderingSegment,
            projectionBatchId = keyProjectionBatchId,
            textureId = textureId,
            textureFilter = textureFilter,
            renderBlend = renderBlend,
            premultipliedAlpha = premultipliedAlpha,
        )
        markAffineInstancedTextureMeshKeyUsed(key)
        val existing = affineInstancedTextureMeshes[key]
        if (existing != null) {
            updateAffineInstancedTextureEntry(existing, texture)
            lastAffineInstancedTextureMeshKey = key
            lastAffineInstancedTextureMeshEntry = existing
            placeInstancedBatchNodeIfFirstUse(scene, existing.mesh, batchIndex)
            return existing
        }

        val batchKey = CanvasBatchKey.AffineTexture(textureId, textureFilter, renderBlend, premultipliedAlpha)
        val shader = createAffineInstancedTextureShader(texture, renderBlend, premultipliedAlpha)
        val instances =
            MeshInstanceList(
                KoolCanvasAffineTextureInstanceLayout,
                initialSize = UNORDERED_TEXTURE_INSTANCE_INITIAL_SIZE
            )
        val mesh = createAffineInstancedTextureMesh(
            textureId,
            renderBlend,
            shader,
            instances,
        )
        placeBatchNode(scene, mesh, batchIndex)
        lastAffineInstancedTextureMeshKey = key
        lastAffineInstancedTextureMeshEntry =
            AffineInstancedTextureMeshEntry(texture, mesh, shader, batchKey, instances)
        meshPruneRequired = true
        return lastAffineInstancedTextureMeshEntry!!.also {
            affineInstancedTextureMeshes[key] = it
        }
    }

    private fun ensureUnorderedAffineInstancedTextureMesh(
        scene: Scene,
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        texture: Texture2d,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): AffineInstancedTextureMeshEntry {
        if (pendingOrderingSegmentAdvance) {
            val matchesActiveBatch =
                activeAffineTextureBatchMatches(textureId, textureFilter, renderBlend, premultipliedAlpha)
            if (!matchesActiveBatch) {
                advanceOrderingSegmentIfAllowed()
            }
        }
        val keyOrderingSegment = activeOrderingSegment
        val keyProjectionBatchId = activeFrameProjectionBatchId
        val cachedKey = lastAffineInstancedTextureMeshKey
        if (cachedKey.matchesUnorderedTexture(
                keyOrderingSegment,
                keyProjectionBatchId,
                textureId,
                textureFilter,
                renderBlend,
                premultipliedAlpha,
            )
        ) {
            val cachedEntry = lastAffineInstancedTextureMeshEntry!!
            val batchIndex = activateAffineTextureBatch(cachedEntry.batchKey)
            markAffineInstancedTextureMeshKeyUsed(cachedKey!!)
            updateAffineInstancedTextureEntry(cachedEntry, texture)
            placeInstancedBatchNodeIfFirstUse(scene, cachedEntry.mesh, batchIndex)
            return cachedEntry
        }
        val alternateKey = alternateAffineInstancedTextureMeshKey
        if (alternateKey.matchesUnorderedTexture(
                keyOrderingSegment,
                keyProjectionBatchId,
                textureId,
                textureFilter,
                renderBlend,
                premultipliedAlpha,
            )
        ) {
            val alternateEntry = alternateAffineInstancedTextureMeshEntry!!
            alternateAffineInstancedTextureMeshKey = lastAffineInstancedTextureMeshKey
            alternateAffineInstancedTextureMeshEntry = lastAffineInstancedTextureMeshEntry
            lastAffineInstancedTextureMeshKey = alternateKey
            lastAffineInstancedTextureMeshEntry = alternateEntry
            val batchIndex = activateAffineTextureBatch(alternateEntry.batchKey)
            markAffineInstancedTextureMeshKeyUsed(alternateKey!!)
            updateAffineInstancedTextureEntry(alternateEntry, texture)
            placeInstancedBatchNodeIfFirstUse(scene, alternateEntry.mesh, batchIndex)
            return alternateEntry
        }

        val key = TextureMeshKey(
            batchIndex = null,
            orderingSegment = keyOrderingSegment,
            projectionBatchId = keyProjectionBatchId,
            textureId = textureId,
            textureFilter = textureFilter,
            renderBlend = renderBlend,
            premultipliedAlpha = premultipliedAlpha,
        )
        val existing = affineInstancedTextureMeshes[key]
        if (existing != null) {
            alternateAffineInstancedTextureMeshKey = lastAffineInstancedTextureMeshKey
            alternateAffineInstancedTextureMeshEntry = lastAffineInstancedTextureMeshEntry
            lastAffineInstancedTextureMeshKey = key
            lastAffineInstancedTextureMeshEntry = existing
            val batchIndex = activateAffineTextureBatch(existing.batchKey)
            markAffineInstancedTextureMeshKeyUsed(key)
            updateAffineInstancedTextureEntry(existing, texture)
            placeInstancedBatchNodeIfFirstUse(scene, existing.mesh, batchIndex)
            return existing
        }

        val batchKey = CanvasBatchKey.AffineTexture(textureId, textureFilter, renderBlend, premultipliedAlpha)
        val batchIndex = activateAffineTextureBatch(batchKey)
        val shader = createAffineInstancedTextureShader(texture, renderBlend, premultipliedAlpha)
        val instances =
            MeshInstanceList(KoolCanvasAffineTextureInstanceLayout, initialSize = ORDERED_TEXTURE_INSTANCE_INITIAL_SIZE)
        val mesh = createAffineInstancedTextureMesh(
            textureId,
            renderBlend,
            shader,
            instances,
        )
        placeBatchNode(scene, mesh, batchIndex)
        val entry = AffineInstancedTextureMeshEntry(texture, mesh, shader, batchKey, instances)
        alternateAffineInstancedTextureMeshKey = lastAffineInstancedTextureMeshKey
        alternateAffineInstancedTextureMeshEntry = lastAffineInstancedTextureMeshEntry
        lastAffineInstancedTextureMeshKey = key
        lastAffineInstancedTextureMeshEntry = entry
        markAffineInstancedTextureMeshKeyUsed(key)
        meshPruneRequired = true
        affineInstancedTextureMeshes[key] = entry
        return entry
    }

    private fun activeAffineTextureBatchMatches(
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): Boolean {
        val current = activeBatchKey as? CanvasBatchKey.AffineTexture ?: return false
        return current.textureId == textureId &&
                current.textureFilter == textureFilter &&
                current.renderBlend == renderBlend &&
                current.premultipliedAlpha == premultipliedAlpha
    }

    private fun activateAffineTextureBatch(batchKey: CanvasBatchKey.AffineTexture): Int {
        if (activeBatchKey !== batchKey && activeBatchKey != batchKey) {
            activeBatchIndex++
            activeBatchKey = batchKey
        }
        return activeBatchIndex
    }

    private fun updateAffineInstancedTextureEntry(entry: AffineInstancedTextureMeshEntry, texture: Texture2d) {
        if (entry.texture !== texture) {
            entry.texture = texture
            entry.shader.colorMap = texture
        }
    }

    private fun createAffineInstancedTextureMesh(
        textureId: KoolCanvasTextureId,
        renderBlend: CanvasRenderBlend,
        shader: KoolCanvasAffineInstancedTextureShader,
        instances: MeshInstanceList<KoolCanvasAffineTextureInstanceLayout>,
    ): Mesh<VertexLayouts.Position> =
        Mesh(
            geometry = IndexedVertexList(VertexLayouts.Position, usage = Usage.STATIC),
            instances = instances,
            name = "rwx-kool-canvas-texture-${textureId.value}${renderBlend.meshNameSuffix}",
        ).apply {
            this.shader = shader
            isOpaque = false
            isVisible = false
            geometry.addVertex { layout ->
                set(layout.position, 0f, 0f, 0f)
            }
            geometry.addVertex { layout ->
                set(layout.position, 1f, 0f, 0f)
            }
            geometry.addVertex { layout ->
                set(layout.position, 1f, 1f, 0f)
            }
            geometry.addVertex { layout ->
                set(layout.position, 0f, 1f, 0f)
            }
            geometry.addTriIndices(0, 1, 2)
            geometry.addTriIndices(0, 2, 3)
        }

    private fun ensureTeamColorTextureMesh(
        scene: Scene,
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        texture: Texture2d,
        effect: KoolCanvasTextureEffect.TeamColor,
        renderBlend: CanvasRenderBlend,
        ordered: Boolean,
    ): TeamColorTextureMeshEntry {
        val batchIndex = batchIndexFor(CanvasBatchKey.TeamColorTexture(textureId, textureFilter, renderBlend, effect))
        val key = TeamColorTextureMeshKey(
            batchIndex = batchIndex.takeIf { ordered },
            orderingSegment = activeOrderingSegment.takeIf { !ordered },
            projectionBatchId = activeFrameProjectionBatchId.takeIf { !ordered },
            textureId = textureId,
            textureFilter = textureFilter,
            renderBlend = renderBlend,
            effect = effect,
        )
        markTeamColorTextureMeshKeyUsed(key)
        val existing = teamColorTextureMeshes[key]
        if (existing != null) {
            if (existing.texture !== texture) {
                existing.texture = texture
                existing.shader.colorMap = texture
            }
            placeBatchNodeIfFirstUse(scene, existing.mesh, batchIndex)
            return existing
        }

        val shader = createTeamColorShader(effect, renderBlend).apply {
            colorMap = texture
        }
        val mesh = Mesh(
            geometry = IndexedVertexList(VertexLayouts.PositionNormalTexCoordColor, usage = Usage.DYNAMIC),
            name = "rwx-kool-canvas-team-color-${textureId.value}-${effect.mode}${renderBlend.meshNameSuffix}",
        ).apply {
            this.shader = shader
            isOpaque = false
            isVisible = false
        }
        placeBatchNode(scene, mesh, batchIndex)
        meshPruneRequired = true
        return TeamColorTextureMeshEntry(texture, mesh, shader).also {
            teamColorTextureMeshes[key] = it
        }
    }

    private fun ensureDisplacementTextureMesh(
        scene: Scene,
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        displacementMap: Texture2d,
        screenBase: Texture2d,
        effect: KoolCanvasTextureEffect.Displacement,
        renderBlend: CanvasRenderBlend,
        viewport: KoolCanvasViewport,
        ordered: Boolean,
    ): DisplacementTextureMeshEntry {
        val batchIndex =
            batchIndexFor(CanvasBatchKey.DisplacementTexture(textureId, textureFilter, renderBlend, effect))
        val key = DisplacementTextureMeshKey(
            batchIndex = batchIndex.takeIf { ordered },
            orderingSegment = activeOrderingSegment.takeIf { !ordered },
            projectionBatchId = activeFrameProjectionBatchId.takeIf { !ordered },
            textureId = textureId,
            textureFilter = textureFilter,
            renderBlend = renderBlend,
            effect = effect,
        )
        markDisplacementTextureMeshKeyUsed(key)
        val existing = displacementTextureMeshes[key]
        if (existing != null) {
            if (existing.displacementMap !== displacementMap) {
                existing.displacementMap = displacementMap
                existing.shader.displacementMap = displacementMap
            }
            if (existing.screenBase !== screenBase) {
                existing.screenBase = screenBase
                existing.shader.screenBase = screenBase
            }
            existing.shader.viewportSize = Vec2f(viewport.width.toFloat(), viewport.height.toFloat())
            placeBatchNodeIfFirstUse(scene, existing.mesh, batchIndex)
            return existing
        }

        val shader = createDisplacementShader(effect, renderBlend, viewport).apply {
            this.displacementMap = displacementMap
            this.screenBase = screenBase
        }
        val mesh = Mesh(
            geometry = IndexedVertexList(VertexLayouts.PositionNormalTexCoordColor, usage = Usage.DYNAMIC),
            name = "rwx-kool-canvas-displacement-${textureId.value}${renderBlend.meshNameSuffix}",
        ).apply {
            this.shader = shader
            isOpaque = false
            isVisible = false
        }
        placeBatchNode(scene, mesh, batchIndex)
        meshPruneRequired = true
        return DisplacementTextureMeshEntry(displacementMap, screenBase, mesh, shader).also {
            displacementTextureMeshes[key] = it
        }
    }

    private fun createDisplacementShader(
        effect: KoolCanvasTextureEffect.Displacement,
        renderBlend: CanvasRenderBlend,
        viewport: KoolCanvasViewport,
    ): KoolDisplacementShader {
        val pipeline = PipelineConfig(
            blendMode = renderBlend.textureBlendMode,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = DepthCompareOp.ALWAYS,
            isWriteDepth = false,
        )
        return KoolDisplacementShader(pipeline).apply {
            offsetBy = effect.offsetBy
            viewportSize = Vec2f(viewport.width.toFloat(), viewport.height.toFloat())
        }
    }

    private fun createTeamColorShader(
        effect: KoolCanvasTextureEffect.TeamColor,
        renderBlend: CanvasRenderBlend,
    ): KoolTeamColorShader {
        val pipeline = PipelineConfig(
            blendMode = renderBlend.textureBlendMode,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = DepthCompareOp.ALWAYS,
            isWriteDepth = false,
        )
        return KoolTeamColorShader(
            mode = effect.mode,
            pipelineConfig = pipeline,
            multipliesRgbByAlpha = renderBlend == CanvasRenderBlend.Additive,
        ).apply {
            teamColor = effect.color.toVec4f()
            teamColorAmount = effect.amount
        }
    }

    private fun ensureTextMesh(
        scene: Scene,
        font: MsdfFont,
        renderBlend: CanvasRenderBlend,
        typefaceKey: String?,
        ordered: Boolean,
    ): TextMeshEntry {
        val batchIndex = batchIndexFor(CanvasBatchKey.Text(font.data, renderBlend, typefaceKey))
        val key = TextMeshKey(
            batchIndex = batchIndex.takeIf { ordered },
            orderingSegment = activeOrderingSegment.takeIf { !ordered },
            projectionBatchId = activeFrameProjectionBatchId.takeIf { !ordered },
            fontData = font.data,
            renderBlend = renderBlend,
            typefaceKey = typefaceKey,
        )
        markTextMeshKeyUsed(key)
        val existing = textMeshes[key]
        if (existing != null) {
            placeBatchNodeIfFirstUse(scene, existing.mesh, batchIndex)
            return existing
        }

        val shader = MsdfUiShader(
            pipelineCfg = PipelineConfig(
                cullMethod = CullMethod.NO_CULLING,
                blendMode = renderBlend.textBlendMode,
            ),
        ).apply {
            fontMap = font.data.map
        }
        val mesh = Mesh(
            geometry = IndexedVertexList(UiTextVertexLayout, usage = Usage.DYNAMIC),
            name = "rwx-kool-canvas-text-${font.data.meta.name}-${typefaceKey ?: "default"}${renderBlend.meshNameSuffix}",
        ).apply {
            this.shader = shader
            isOpaque = false
            isVisible = false
            isCastingShadow = false
        }
        val builder = MeshBuilder(mesh.geometry).apply {
            isInvertFaceOrientation = true
        }
        placeBatchNode(scene, mesh, batchIndex)
        meshPruneRequired = true
        return TextMeshEntry(font.data, renderBlend, mesh, shader, builder).also {
            textMeshes[key] = it
        }
    }

    private fun batchIndexFor(key: CanvasBatchKey): Int {
        if (pendingOrderingSegmentAdvance && activeBatchKey != key) {
            if (frameProjectionDepth == 0 || projectedOrderingSegmentAdvances < MaxProjectedOrderingSegmentAdvances) {
                activeOrderingSegment++
                if (frameProjectionDepth > 0) {
                    projectedOrderingSegmentAdvances++
                }
            }
            pendingOrderingSegmentAdvance = false
        }
        if (activeBatchKey != key) {
            activeBatchIndex++
            activeBatchKey = key
        }
        return activeBatchIndex
    }

    private fun batchIndexForPrimitive(renderBlend: CanvasRenderBlend, layer: PrimitiveMeshLayer): Int {
        val current = activeBatchKey as? CanvasBatchKey.Primitive
        val matchesCurrent = current != null && current.renderBlend == renderBlend && current.layer == layer
        if (pendingOrderingSegmentAdvance && !matchesCurrent) {
            advanceOrderingSegmentIfAllowed()
        }
        if (!matchesCurrent) {
            activeBatchIndex++
            activeBatchKey = CanvasBatchKey.Primitive(renderBlend, layer)
        }
        return activeBatchIndex
    }

    private fun batchIndexForTexture(
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): Int {
        val current = activeBatchKey as? CanvasBatchKey.Texture
        val matchesCurrent = current != null &&
                current.textureId == textureId &&
                current.textureFilter == textureFilter &&
                current.renderBlend == renderBlend &&
                current.premultipliedAlpha == premultipliedAlpha
        if (pendingOrderingSegmentAdvance && !matchesCurrent) {
            advanceOrderingSegmentIfAllowed()
        }
        if (!matchesCurrent) {
            activeBatchIndex++
            activeBatchKey = CanvasBatchKey.Texture(textureId, textureFilter, renderBlend, premultipliedAlpha)
        }
        return activeBatchIndex
    }

    private fun batchIndexForAffineTexture(
        textureId: KoolCanvasTextureId,
        textureFilter: KoolCanvasTextureFilter,
        renderBlend: CanvasRenderBlend,
        premultipliedAlpha: Boolean,
    ): Int {
        val current = activeBatchKey as? CanvasBatchKey.AffineTexture
        val matchesCurrent = current != null &&
                current.textureId == textureId &&
                current.textureFilter == textureFilter &&
                current.renderBlend == renderBlend &&
                current.premultipliedAlpha == premultipliedAlpha
        if (pendingOrderingSegmentAdvance && !matchesCurrent) {
            advanceOrderingSegmentIfAllowed()
        }
        if (!matchesCurrent) {
            activeBatchIndex++
            activeBatchKey = CanvasBatchKey.AffineTexture(textureId, textureFilter, renderBlend, premultipliedAlpha)
        }
        return activeBatchIndex
    }

    private fun advanceOrderingSegmentIfAllowed() {
        if (frameProjectionDepth == 0 || projectedOrderingSegmentAdvances < MaxProjectedOrderingSegmentAdvances) {
            activeOrderingSegment++
            if (frameProjectionDepth > 0) {
                projectedOrderingSegmentAdvances++
            }
        }
        pendingOrderingSegmentAdvance = false
    }

    private fun placeBatchNodeIfFirstUse(scene: Scene, mesh: Mesh<*>, batchIndex: Int) {
        if (!mesh.isVisible && mesh.geometry.isEmpty()) {
            placeBatchNode(scene, mesh, batchIndex)
        }
    }

    private fun placeInstancedBatchNodeIfFirstUse(scene: Scene, mesh: Mesh<*>, batchIndex: Int) {
        if (!mesh.isVisible && mesh.instances?.numInstances == 0) {
            placeBatchNode(scene, mesh, batchIndex)
        }
    }

    private fun placeBatchNode(scene: Scene, mesh: Mesh<*>, batchIndex: Int) {
        val targetIndex = batchIndex.coerceIn(0, scene.children.size)
        if (mesh.parent === scene) {
            if (targetIndex < scene.children.size && scene.children[targetIndex] === mesh) {
                return
            }
            val currentIndex = scene.children.indexOf(mesh)
            if (currentIndex == targetIndex) {
                return
            }
            scene.removeNode(mesh)
            scene.addNode(mesh, targetIndex.coerceIn(0, scene.children.size))
        } else {
            scene.addNode(mesh, targetIndex)
        }
    }

    private fun markOrderingBarrier() {
        pendingOrderingSegmentAdvance = true
    }

    private fun configureCamera(scene: Scene, viewport: KoolCanvasViewport) {
        val camera = (scene.camera as? OrthographicCamera) ?: OrthographicCamera("rwx-kool-canvas-camera").also {
            scene.camera = it
        }
        val halfWidth = viewport.width * 0.5f
        val halfHeight = viewport.height * 0.5f
        camera.left = -halfWidth
        camera.right = halfWidth
        camera.bottom = -halfHeight
        camera.top = halfHeight
        camera.isKeepAspectRatio = false
        camera.isClipToViewport = false
        camera.setupCamera(
            position = Vec3f(0f, 0f, 100f),
            lookAt = Vec3f(0f, 0f, 0f),
        )
    }

    private fun addTexture(
        scene: Scene,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawTexture,
    ): Boolean {
        val paint = command.paint
        if (paint.isRenderNoOp) return false
        if (command.source.hasZeroArea || command.destination.hasZeroArea) return false
        val textureRef = command.texture
        val textureFilter = paint.textureFilter
        val defaultTexturePaint = paint.isDefaultTexturePaint
        if (command.canUseUnorderedDefaultInstancedTextureQuad(defaultTexturePaint)) {
            val texture = resolveTexture(textureRef, textureFilter)
            val entry = ensureInstancedTextureMesh(
                scene,
                textureRef.id,
                textureFilter,
                texture,
                CanvasRenderBlend.Alpha,
                premultipliedAlpha = false,
                ordered = false,
            )
            return addInstancedTextureQuad(entry.instances, viewport, command)
        }
        val texture = resolveTexture(textureRef, textureFilter)
        val effect = paint.textureEffect
        val renderBlend = paint.renderBlend
        val orderingBarrier = paint.requiresTextureOrderingBarrier(textureRef)
        val ordered = frameProjectionDepth == 0 && orderingBarrier
        if (effect == null && command.canUseInstancedTextureQuad(defaultTexturePaint)) {
            val entry = ensureInstancedTextureMesh(
                scene,
                textureRef.id,
                textureFilter,
                texture,
                renderBlend,
                textureRef.premultipliedAlpha,
                ordered,
            )
            return addInstancedTextureQuad(entry.instances, viewport, command).also { added ->
                if (added && orderingBarrier) {
                    markOrderingBarrier()
                }
            }
        }
        if (effect == null && command.canUseAffineInstancedTextureQuad(defaultTexturePaint)) {
            val entry = ensureAffineInstancedTextureMesh(
                scene,
                textureRef.id,
                textureFilter,
                texture,
                renderBlend,
                textureRef.premultipliedAlpha,
                ordered,
            )
            return addAffineInstancedTextureQuad(entry.instances, viewport, command).also { added ->
                if (added && orderingBarrier) {
                    markOrderingBarrier()
                }
            }
        }
        val geometry = when (effect) {
            is KoolCanvasTextureEffect.TeamColor ->
                ensureTeamColorTextureMesh(
                    scene,
                    textureRef.id,
                    textureFilter,
                    texture,
                    effect,
                    renderBlend,
                    ordered,
                ).mesh.geometry

            is KoolCanvasTextureEffect.Displacement -> {
                val screenBase = resolveShaderTexture(scene, effect.screenBase, textureFilter)
                ensureDisplacementTextureMesh(
                    scene = scene,
                    textureId = textureRef.id,
                    textureFilter = textureFilter,
                    displacementMap = texture,
                    screenBase = screenBase,
                    effect = effect,
                    renderBlend = renderBlend,
                    viewport = viewport,
                    ordered = ordered,
                ).mesh.geometry
            }

            null -> ensureTextureMesh(
                scene,
                textureRef.id,
                textureFilter,
                texture,
                renderBlend,
                textureRef.premultipliedAlpha,
                ordered,
            ).mesh.geometry
        }
        return addTextureQuad(geometry, viewport, command).also { added ->
            if (added && orderingBarrier) {
                markOrderingBarrier()
            }
        }
    }

    private fun addUnorderedDefaultInstancedTextureRun(
        scene: Scene,
        viewport: KoolCanvasViewport,
        commands: List<KoolCanvasCommand>,
        startIndex: Int,
        firstCommand: KoolCanvasCommand.DrawTexture,
    ): Int {
        if (!firstCommand.canUseUnorderedDefaultInstancedTextureRun()) return 0
        var runEntryCount = 0
        var commandIndex = startIndex
        while (commandIndex < commands.size) {
            val command = commands[commandIndex] as? KoolCanvasCommand.DrawTexture ?: break
            if (!command.canUseUnorderedDefaultInstancedTextureRun()) break

            val textureRef = command.texture
            val textureId = textureRef.id
            val textureFilter = command.paint.textureFilter
            var entry: InstancedTextureMeshEntry? = null
            for (entryIndex in 0 until runEntryCount) {
                if (textureRunTextureIds[entryIndex] == textureId && textureRunFilters[entryIndex] == textureFilter) {
                    entry = textureRunEntries[entryIndex]
                    break
                }
            }
            if (entry == null) {
                if (runEntryCount >= TEXTURE_RUN_CACHE_SIZE) break
                if (resolveFrameTexture(textureId) != null) break
                val texture = resolveTexture(textureRef, textureFilter)
                entry = ensureInstancedTextureMesh(
                    scene,
                    textureId,
                    textureFilter,
                    texture,
                    CanvasRenderBlend.Alpha,
                    premultipliedAlpha = false,
                    ordered = false,
                )
                textureRunTextureIds[runEntryCount] = textureId
                textureRunFilters[runEntryCount] = textureFilter
                textureRunEntries[runEntryCount] = entry
                runEntryCount++
            }
            addInstancedTextureQuad(entry.instances, viewport, command)
            commandIndex++
        }
        for (entryIndex in 0 until runEntryCount) {
            textureRunTextureIds[entryIndex] = null
            textureRunFilters[entryIndex] = null
            textureRunEntries[entryIndex] = null
        }
        return commandIndex - startIndex
    }

    private fun KoolCanvasCommand.DrawTexture.canUseUnorderedDefaultInstancedTextureRun(): Boolean {
        val paint = paint
        val texture = texture
        return paint.isCanonicalDefaultTexturePaint &&
                state.renderTarget == null &&
                state.clip == null &&
                state.transform === KoolCanvasTransform.Identity &&
                !source.hasZeroArea &&
                !destination.hasZeroArea &&
                !texture.hasAlpha &&
                !texture.requiresOrderedAlpha &&
                !texture.premultipliedAlpha
    }

    private fun KoolCanvasCommand.DrawTexture.canUseInstancedTextureQuad(defaultTexturePaint: Boolean): Boolean =
        defaultTexturePaint &&
                state.clip == null &&
                state.transform === KoolCanvasTransform.Identity

    private fun KoolCanvasCommand.DrawTexture.canUseAffineInstancedTextureQuad(defaultTexturePaint: Boolean): Boolean =
        defaultTexturePaint &&
                state.clip == null &&
                state.transform !== KoolCanvasTransform.Identity

    private fun KoolCanvasCommand.DrawTexture.canUseUnorderedDefaultInstancedTextureQuad(
        defaultTexturePaint: Boolean,
    ): Boolean =
        defaultTexturePaint &&
                state.clip == null &&
                state.transform === KoolCanvasTransform.Identity &&
                !texture.hasAlpha &&
                !texture.requiresOrderedAlpha &&
                !texture.premultipliedAlpha

    private val KoolCanvasPaint.isDefaultTexturePaint: Boolean
        get() =
            this === KoolCanvasPaint.Default ||
                    this === KoolCanvasPaint.DefaultNearest ||
                    this == KoolCanvasPaint.Default ||
                    this == KoolCanvasPaint.DefaultNearest

    private val KoolCanvasPaint.isCanonicalDefaultTexturePaint: Boolean
        get() =
            this === KoolCanvasPaint.Default ||
                    this === KoolCanvasPaint.DefaultNearest

    private fun addInstancedTextureQuad(
        instances: MeshInstanceList<KoolCanvasTextureInstanceLayout>,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawTexture,
    ): Boolean {
        val destination = command.destination
        val viewportHalfWidth = currentViewportHalfWidth
        val viewportHalfHeight = currentViewportHalfHeight
        val left = destination.left - viewportHalfWidth
        val top = viewportHalfHeight - destination.top
        val right = destination.right - viewportHalfWidth
        val bottom = viewportHalfHeight - destination.bottom
        if (command.sourceIsFullTexture) {
            instances.addDefaultTextureInstance(left, top, right, bottom)
        } else {
            instances.addTextureInstance(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                u0 = command.sourceU0,
                v0 = command.sourceV0,
                u1 = command.sourceU1,
                v1 = command.sourceV1,
            )
        }
        return true
    }

    private fun addAffineInstancedTextureQuad(
        instances: MeshInstanceList<KoolCanvasAffineTextureInstanceLayout>,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawTexture,
    ): Boolean {
        val destination = command.destination

        val u0: Float
        val v0: Float
        val u1: Float
        val v1: Float
        if (command.sourceIsFullTexture) {
            u0 = 0f
            v0 = 0f
            u1 = 1f
            v1 = 1f
        } else {
            u0 = command.sourceU0
            v0 = command.sourceV0
            u1 = command.sourceU1
            v1 = command.sourceV1
        }

        val viewportHalfWidth = currentViewportHalfWidth
        val viewportHalfHeight = currentViewportHalfHeight
        val transform = command.state.transform
        val p0x = transform.scaleX * destination.left + transform.skewX * destination.top + transform.translateX
        val p0y = transform.skewY * destination.left + transform.scaleY * destination.top + transform.translateY
        val p1x = transform.scaleX * destination.right + transform.skewX * destination.top + transform.translateX
        val p1y = transform.skewY * destination.right + transform.scaleY * destination.top + transform.translateY
        val p3x = transform.scaleX * destination.left + transform.skewX * destination.bottom + transform.translateX
        val p3y = transform.skewY * destination.left + transform.scaleY * destination.bottom + transform.translateY

        instances.addAffineTextureInstance(
            originX = p0x - viewportHalfWidth,
            originY = viewportHalfHeight - p0y,
            axisXx = p1x - p0x,
            axisXy = -(p1y - p0y),
            axisYx = p3x - p0x,
            axisYy = -(p3y - p0y),
            u0 = u0,
            v0 = v0,
            u1 = u1,
            v1 = v1,
        )
        return true
    }

    private fun MeshInstanceList<KoolCanvasTextureInstanceLayout>.addDefaultTextureInstance(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ) {
        val data = instanceData
        if (data.limit >= data.capacity) {
            addInstances(1) { buffer ->
                buffer.writeTextureInstance(left, top, right, bottom, 0f, 0f, 1f, 1f)
            }
            return
        }
        data.writeTextureInstance(left, top, right, bottom, 0f, 0f, 1f, 1f)
    }

    private fun MeshInstanceList<KoolCanvasTextureInstanceLayout>.addTextureInstance(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        u0: Float,
        v0: Float,
        u1: Float,
        v1: Float,
    ) {
        val data = instanceData
        if (data.limit >= data.capacity) {
            addInstances(1) { buffer ->
                buffer.writeTextureInstance(left, top, right, bottom, u0, v0, u1, v1)
            }
            return
        }
        data.writeTextureInstance(left, top, right, bottom, u0, v0, u1, v1)
    }

    private fun de.fabmax.kool.util.StructBuffer<KoolCanvasTextureInstanceLayout>.writeTextureInstance(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        u0: Float,
        v0: Float,
        u1: Float,
        v1: Float,
    ) {
        val index = limit
        limit = index + 1
        val offset = index * strideBytes
        val rectOffset = offset + KoolCanvasTextureInstanceLayout.rect.byteOffset
        buffer.setFloat32(rectOffset, left)
        buffer.setFloat32(rectOffset + 4, top)
        buffer.setFloat32(rectOffset + 8, right)
        buffer.setFloat32(rectOffset + 12, bottom)
        val uvOffset = offset + KoolCanvasTextureInstanceLayout.uv.byteOffset
        buffer.setFloat32(uvOffset, u0)
        buffer.setFloat32(uvOffset + 4, v0)
        buffer.setFloat32(uvOffset + 8, u1)
        buffer.setFloat32(uvOffset + 12, v1)
    }

    private fun MeshInstanceList<KoolCanvasAffineTextureInstanceLayout>.addAffineTextureInstance(
        originX: Float,
        originY: Float,
        axisXx: Float,
        axisXy: Float,
        axisYx: Float,
        axisYy: Float,
        u0: Float,
        v0: Float,
        u1: Float,
        v1: Float,
    ) {
        val data = instanceData
        if (data.limit >= data.capacity) {
            addInstances(1) { buffer ->
                buffer.writeAffineTextureInstance(originX, originY, axisXx, axisXy, axisYx, axisYy, u0, v0, u1, v1)
            }
            return
        }
        data.writeAffineTextureInstance(originX, originY, axisXx, axisXy, axisYx, axisYy, u0, v0, u1, v1)
    }

    private fun de.fabmax.kool.util.StructBuffer<KoolCanvasAffineTextureInstanceLayout>.writeAffineTextureInstance(
        originX: Float,
        originY: Float,
        axisXx: Float,
        axisXy: Float,
        axisYx: Float,
        axisYy: Float,
        u0: Float,
        v0: Float,
        u1: Float,
        v1: Float,
    ) {
        val index = limit
        limit = index + 1
        val offset = index * strideBytes
        val originAxisXOffset = offset + KoolCanvasAffineTextureInstanceLayout.originAxisX.byteOffset
        buffer.setFloat32(originAxisXOffset, originX)
        buffer.setFloat32(originAxisXOffset + 4, originY)
        buffer.setFloat32(originAxisXOffset + 8, axisXx)
        buffer.setFloat32(originAxisXOffset + 12, axisXy)
        val axisYOffset = offset + KoolCanvasAffineTextureInstanceLayout.axisY.byteOffset
        buffer.setFloat32(axisYOffset, axisYx)
        buffer.setFloat32(axisYOffset + 4, axisYy)
        buffer.setFloat32(axisYOffset + 8, 0f)
        buffer.setFloat32(axisYOffset + 12, 0f)
        val uvOffset = offset + KoolCanvasAffineTextureInstanceLayout.uv.byteOffset
        buffer.setFloat32(uvOffset, u0)
        buffer.setFloat32(uvOffset + 4, v0)
        buffer.setFloat32(uvOffset + 8, u1)
        buffer.setFloat32(uvOffset + 12, v1)
    }

    private fun resolveShaderTexture(
        scene: Scene,
        texture: KoolCanvasTextureRef,
        filter: KoolCanvasTextureFilter,
    ): Texture2d {
        return resolveTexture(texture, filter)
    }

    private fun resolveTexture(
        texture: KoolCanvasTextureRef,
        filter: KoolCanvasTextureFilter,
    ): Texture2d {
        val revisionStore = textureRevisionStore
        if (revisionStore != null) {
            val revision = currentTextureRevision
            if (resolvedTextureCacheRevision != revision) {
                clearResolvedTextureCache(revision)
            }
        }
        val cachedTexture = lastResolvedTexture
        if (cachedTexture != null &&
            lastResolvedTextureRef == texture &&
            lastResolvedTextureFilter == filter
        ) {
            return cachedTexture
        }
        val alternateTexture = alternateResolvedTexture
        if (alternateTexture != null &&
            alternateResolvedTextureRef == texture &&
            alternateResolvedTextureFilter == filter
        ) {
            alternateResolvedTextureRef = lastResolvedTextureRef
            alternateResolvedTextureFilter = lastResolvedTextureFilter
            alternateResolvedTexture = cachedTexture
            lastResolvedTextureRef = texture
            lastResolvedTextureFilter = filter
            lastResolvedTexture = alternateTexture
            return alternateTexture
        }

        for (index in 0 until resolvedTextureCacheCount) {
            if (resolvedTextureCacheRefs[index] == texture &&
                resolvedTextureCacheFilters[index] == filter
            ) {
                return resolvedTextureCacheTextures[index]!!.also { resolved ->
                    alternateResolvedTextureRef = lastResolvedTextureRef
                    alternateResolvedTextureFilter = lastResolvedTextureFilter
                    alternateResolvedTexture = cachedTexture
                    lastResolvedTextureRef = texture
                    lastResolvedTextureFilter = filter
                    lastResolvedTexture = resolved
                }
            }
        }

        val resolved = textureStore.resolve(texture, filter)
        val cacheSlot = nextResolvedTextureCacheSlot
        resolvedTextureCacheRefs[cacheSlot] = texture
        resolvedTextureCacheFilters[cacheSlot] = filter
        resolvedTextureCacheTextures[cacheSlot] = resolved
        if (resolvedTextureCacheCount < RESOLVED_TEXTURE_CACHE_SIZE) {
            resolvedTextureCacheCount++
        }
        nextResolvedTextureCacheSlot = (cacheSlot + 1) % RESOLVED_TEXTURE_CACHE_SIZE
        alternateResolvedTextureRef = lastResolvedTextureRef
        alternateResolvedTextureFilter = lastResolvedTextureFilter
        alternateResolvedTexture = cachedTexture
        lastResolvedTextureRef = texture
        lastResolvedTextureFilter = filter
        lastResolvedTexture = resolved
        return resolved
    }

    private fun clearResolvedTextureCache(revision: Int = resolvedTextureCacheRevision) {
        for (index in 0 until RESOLVED_TEXTURE_CACHE_SIZE) {
            resolvedTextureCacheRefs[index] = null
            resolvedTextureCacheFilters[index] = null
            resolvedTextureCacheTextures[index] = null
        }
        nextResolvedTextureCacheSlot = 0
        resolvedTextureCacheCount = 0
        resolvedTextureCacheRevision = revision
        lastResolvedTextureRef = null
        lastResolvedTextureFilter = null
        lastResolvedTexture = null
        alternateResolvedTextureRef = null
        alternateResolvedTextureFilter = null
        alternateResolvedTexture = null
    }

    private fun resolveFrameTexture(textureId: KoolCanvasTextureId): KoolCanvasFrame? {
        if (lastFrameTextureId == textureId) {
            return lastFrameTexture
        }
        val revisionStore = frameTextureRevisionStore
        if (revisionStore != null) {
            val revision = currentFrameTextureRevision
            if (missingFrameTextureCacheRevision != revision) {
                clearMissingFrameTextureCache(revision)
            } else if (isKnownMissingFrameTexture(textureId)) {
                lastFrameTextureId = textureId
                lastFrameTexture = null
                return null
            }
        }
        for (index in 0 until FRAME_TEXTURE_CACHE_SIZE) {
            if (frameTextureCacheIds[index] == textureId) {
                return frameTextureCacheFrames[index].also { frame ->
                    lastFrameTextureId = textureId
                    lastFrameTexture = frame
                }
            }
        }
        val frame = textureStore.frame(textureId)
        val cacheSlot = nextFrameTextureCacheSlot
        frameTextureCacheIds[cacheSlot] = textureId
        frameTextureCacheFrames[cacheSlot] = frame
        nextFrameTextureCacheSlot = (cacheSlot + 1) % FRAME_TEXTURE_CACHE_SIZE
        lastFrameTextureId = textureId
        lastFrameTexture = frame
        if (frame == null && revisionStore != null) {
            rememberMissingFrameTexture(textureId)
        }
        return frame
    }

    private fun isKnownMissingFrameTexture(textureId: KoolCanvasTextureId): Boolean {
        val lastMissing = lastMissingFrameTextureId
        if (lastMissing == textureId) {
            return true
        }
        val alternateMissing = alternateMissingFrameTextureId
        if (alternateMissing == textureId) {
            alternateMissingFrameTextureId = lastMissing
            lastMissingFrameTextureId = textureId
            return true
        }
        for (index in 0 until missingFrameTextureCacheCount) {
            if (missingFrameTextureCacheIds[index] == textureId) {
                alternateMissingFrameTextureId = lastMissing
                lastMissingFrameTextureId = textureId
                return true
            }
        }
        return false
    }

    private fun rememberMissingFrameTexture(textureId: KoolCanvasTextureId) {
        if (lastMissingFrameTextureId == textureId) {
            return
        }
        alternateMissingFrameTextureId = lastMissingFrameTextureId
        lastMissingFrameTextureId = textureId
        val cacheSlot = nextMissingFrameTextureCacheSlot
        missingFrameTextureCacheIds[cacheSlot] = textureId
        if (missingFrameTextureCacheCount < FRAME_TEXTURE_MISS_CACHE_SIZE) {
            missingFrameTextureCacheCount++
        }
        nextMissingFrameTextureCacheSlot = (cacheSlot + 1) % FRAME_TEXTURE_MISS_CACHE_SIZE
    }

    private fun clearMissingFrameTextureCache(revision: Int) {
        for (index in 0 until FRAME_TEXTURE_MISS_CACHE_SIZE) {
            missingFrameTextureCacheIds[index] = null
        }
        nextMissingFrameTextureCacheSlot = 0
        missingFrameTextureCacheCount = 0
        missingFrameTextureCacheRevision = revision
        lastMissingFrameTextureId = null
        alternateMissingFrameTextureId = null
    }

    private fun clearFrameTextureCache() {
        for (index in 0 until FRAME_TEXTURE_CACHE_SIZE) {
            frameTextureCacheIds[index] = null
            frameTextureCacheFrames[index] = null
        }
        nextFrameTextureCacheSlot = 0
        lastFrameTextureId = null
        lastFrameTexture = null
    }

    private fun addFrameTexture(
        scene: Scene,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawTexture,
    ): Boolean? {
        if (activeFrameProjectionBatchId != null &&
            projectedFrameTextureExpansions >= MAX_PROJECTED_FRAME_TEXTURE_EXPANSIONS
        ) {
            return false
        }
        val sourceFrame = resolveFrameTexture(command.texture.id) ?: return null
        if (command.paint.isRenderNoOp || command.source.hasZeroArea || command.destination.hasZeroArea) {
            return false
        }
        if (frameProjectionDepth >= MAX_FRAME_TEXTURE_PROJECTION_DEPTH) {
            return false
        }
        if (command.texture.id in renderingFrameTextureIds) {
            return false
        }

        if (!renderingFrameTextureIds.add(command.texture.id)) {
            return false
        }
        val previousProjectionBatchId = activeFrameProjectionBatchId
        val ownsProjectionBatch = previousProjectionBatchId == null
        if (ownsProjectionBatch) {
            activeFrameProjectionBatchId = nextFrameProjectionBatchId++
        }
        val previousProjectedOrderingSegmentAdvances = projectedOrderingSegmentAdvances
        val previousProjectedFrameTextureExpansions = projectedFrameTextureExpansions
        if (ownsProjectionBatch) {
            projectedOrderingSegmentAdvances = 0
            projectedFrameTextureExpansions = 0
        } else {
            projectedFrameTextureExpansions++
        }
        try {
            frameProjectionDepth++
            return addProjectedFrameTextureCommands(scene, viewport, command, sourceFrame)
        } finally {
            frameProjectionDepth--
            if (ownsProjectionBatch) {
                activeFrameProjectionBatchId = previousProjectionBatchId
                projectedOrderingSegmentAdvances = previousProjectedOrderingSegmentAdvances
                projectedFrameTextureExpansions = previousProjectedFrameTextureExpansions
            }
            renderingFrameTextureIds.remove(command.texture.id)
        }
    }

    private fun addProjectedFrameTextureCommands(
        scene: Scene,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawTexture,
        sourceFrame: KoolCanvasFrame,
    ): Boolean {
        val frameClip = command.frameSourceClip(sourceFrame) ?: return false
        val clippedDestination = command.source.mapSubRectTo(command.destination, frameClip)
        val placementTransform = command.source.toDestinationTransform(command.destination)
        var addedPrimitiveGeometry = false
        sourceFrame.commands.forEach { nestedCommand ->
            when (nestedCommand) {
                is KoolCanvasCommand.Clear -> {
                    if (nestedCommand.renderTarget != null) return@forEach
                    addedPrimitiveGeometry = addRect(
                        scene,
                        viewport,
                        KoolCanvasCommand.DrawRect(
                            rect = clippedDestination,
                            paint = KoolCanvasPaint(
                                color = nestedCommand.color,
                                blendMode = nestedCommand.blendMode,
                            ).withOuterPaint(command.paint),
                            state = command.state,
                        ),
                    ) || addedPrimitiveGeometry
                }

                is KoolCanvasCommand.DrawTexture -> {
                    if (nestedCommand.state.renderTarget != null) return@forEach
                    val projectedState =
                        nestedCommand.state.projected(command.state, placementTransform, frameClip) ?: return@forEach
                    val projected = nestedCommand.copy(
                        state = projectedState,
                        paint = nestedCommand.paint.withOuterPaint(command.paint),
                    )
                    val framePrimitiveGeometry = addFrameTexture(scene, viewport, projected)
                    if (framePrimitiveGeometry == null) {
                        addedPrimitiveGeometry = addTexture(scene, viewport, projected) || addedPrimitiveGeometry
                    } else {
                        addedPrimitiveGeometry = framePrimitiveGeometry || addedPrimitiveGeometry
                    }
                }

                is KoolCanvasCommand.DrawRect -> {
                    if (nestedCommand.state.renderTarget != null) return@forEach
                    val projectedState =
                        nestedCommand.state.projected(command.state, placementTransform, frameClip) ?: return@forEach
                    addedPrimitiveGeometry = addRect(
                        scene,
                        viewport,
                        nestedCommand.copy(
                            state = projectedState,
                            paint = nestedCommand.paint.withOuterPaint(command.paint),
                        ),
                    ) || addedPrimitiveGeometry
                }

                is KoolCanvasCommand.DrawLine -> {
                    if (nestedCommand.state.renderTarget != null) return@forEach
                    val projectedState =
                        nestedCommand.state.projected(command.state, placementTransform, frameClip) ?: return@forEach
                    addedPrimitiveGeometry = addLine(
                        scene,
                        viewport,
                        nestedCommand.copy(
                            state = projectedState,
                            paint = nestedCommand.paint.withOuterPaint(command.paint),
                        ),
                    ) || addedPrimitiveGeometry
                }

                is KoolCanvasCommand.DrawCircle -> {
                    if (nestedCommand.state.renderTarget != null) return@forEach
                    val projectedState =
                        nestedCommand.state.projected(command.state, placementTransform, frameClip) ?: return@forEach
                    addedPrimitiveGeometry = addCircle(
                        scene,
                        viewport,
                        nestedCommand.copy(
                            state = projectedState,
                            paint = nestedCommand.paint.withOuterPaint(command.paint),
                        ),
                    ) || addedPrimitiveGeometry
                }

                is KoolCanvasCommand.DrawText -> {
                    if (nestedCommand.state.renderTarget != null) return@forEach
                    val projectedState =
                        nestedCommand.state.projected(command.state, placementTransform, frameClip) ?: return@forEach
                    addText(
                        scene,
                        viewport,
                        nestedCommand.copy(
                            state = projectedState,
                            paint = nestedCommand.paint.withOuterPaint(command.paint),
                        ),
                    )
                    addedPrimitiveGeometry = true
                }
            }
        }
        return addedPrimitiveGeometry
    }

    private fun addTextureQuad(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalTexCoordColor>,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawTexture,
    ): Boolean {
        val destination = command.destination
        val source = command.source
        if (destination.hasZeroArea || source.hasZeroArea) return false

        var destinationLeft = destination.left
        var destinationTop = destination.top
        var destinationRight = destination.right
        var destinationBottom = destination.bottom
        var sourceLeft = source.left
        var sourceTop = source.top
        var sourceRight = source.right
        var sourceBottom = source.bottom
        val clip = command.state.clipForGeometry()
        if (clip != null) {
            val clippedBoundsLeft = maxOf(destination.boundsLeft, clip.boundsLeft)
            val clippedBoundsTop = maxOf(destination.boundsTop, clip.boundsTop)
            val clippedBoundsRight = minOf(destination.boundsRight, clip.boundsRight)
            val clippedBoundsBottom = minOf(destination.boundsBottom, clip.boundsBottom)
            if (clippedBoundsRight <= clippedBoundsLeft || clippedBoundsBottom <= clippedBoundsTop) {
                return false
            }

            destinationLeft = if (destination.width >= 0f) clippedBoundsLeft else clippedBoundsRight
            destinationTop = if (destination.height >= 0f) clippedBoundsTop else clippedBoundsBottom
            destinationRight = if (destination.width >= 0f) clippedBoundsRight else clippedBoundsLeft
            destinationBottom = if (destination.height >= 0f) clippedBoundsBottom else clippedBoundsTop

            val leftRatio = (destinationLeft - destination.left) / destination.width
            val topRatio = (destinationTop - destination.top) / destination.height
            val rightRatio = (destinationRight - destination.left) / destination.width
            val bottomRatio = (destinationBottom - destination.top) / destination.height
            sourceLeft = source.left + source.width * leftRatio
            sourceTop = source.top + source.height * topRatio
            sourceRight = source.left + source.width * rightRatio
            sourceBottom = source.top + source.height * bottomRatio
        }

        val u0: Float
        val v0: Float
        val u1: Float
        val v1: Float
        if (sourceLeft == 0f &&
            sourceTop == 0f &&
            sourceRight == command.texture.widthFloat &&
            sourceBottom == command.texture.heightFloat
        ) {
            u0 = 0f
            v0 = 0f
            u1 = 1f
            v1 = 1f
        } else {
            u0 = sourceLeft * command.texture.inverseSafeWidth
            v0 = sourceTop * command.texture.inverseSafeHeight
            u1 = sourceRight * command.texture.inverseSafeWidth
            v1 = sourceBottom * command.texture.inverseSafeHeight
        }
        val color = command.paint.toRenderColor()
        val viewportHalfWidth = currentViewportHalfWidth
        val viewportHalfHeight = currentViewportHalfHeight
        val transform = command.state.transform
        val transformIsIdentity = transform === KoolCanvasTransform.Identity

        val i0 = geometry.addCanvasTextureVertex(
            x = destinationLeft,
            y = destinationTop,
            u = u0,
            v = v0,
            viewportHalfWidth = viewportHalfWidth,
            viewportHalfHeight = viewportHalfHeight,
            transform = transform,
            transformIsIdentity = transformIsIdentity,
            color = color,
        )
        val i1 = geometry.addCanvasTextureVertex(
            x = destinationRight,
            y = destinationTop,
            u = u1,
            v = v0,
            viewportHalfWidth = viewportHalfWidth,
            viewportHalfHeight = viewportHalfHeight,
            transform = transform,
            transformIsIdentity = transformIsIdentity,
            color = color,
        )
        val i2 = geometry.addCanvasTextureVertex(
            x = destinationRight,
            y = destinationBottom,
            u = u1,
            v = v1,
            viewportHalfWidth = viewportHalfWidth,
            viewportHalfHeight = viewportHalfHeight,
            transform = transform,
            transformIsIdentity = transformIsIdentity,
            color = color,
        )
        val i3 = geometry.addCanvasTextureVertex(
            x = destinationLeft,
            y = destinationBottom,
            u = u0,
            v = v1,
            viewportHalfWidth = viewportHalfWidth,
            viewportHalfHeight = viewportHalfHeight,
            transform = transform,
            transformIsIdentity = transformIsIdentity,
            color = color,
        )
        geometry.addTriIndices(i0, i1, i2)
        geometry.addTriIndices(i0, i2, i3)
        return true
    }

    private fun addText(
        scene: Scene,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawText,
    ): Boolean {
        if (command.text.isEmpty() || command.paint.isRenderNoOp) {
            return false
        }
        val ordered = command.paint.requiresOrderedTextBatch()
        val orderingBarrier = command.paint.requiresTextOrderingBarrier()

        val font = KoolCanvasFontRegistry.font(command.paint.textSize, command.paint.typefaceKey)
        val entry = ensureTextMesh(
            scene,
            font,
            command.paint.renderBlend,
            command.paint.typefaceKey,
            ordered,
        )
        val metrics = font.textDimensions(command.text, TextMetrics())
        val origin = alignedTextOrigin(command, metrics)
        if (!command.state.intersectsClip(textBounds(origin, metrics))) {
            return false
        }

        val builder = entry.builder
        val previousColor = builder.color
        val previousCustomizer = builder.vertexCustomizer
        val clipBounds = command.state.clip.toWorldClip(viewport)
        val vertexPosition = MutableVec3f()

        builder.color = command.paint.toRenderColor()
        builder.vertexCustomizer = { layout ->
            get(layout.position, vertexPosition)
            val mapped = command.state.transform.map(KoolCanvasPoint(vertexPosition.x, vertexPosition.y))
            set(
                layout.position,
                mapped.x - currentViewportHalfWidth,
                currentViewportHalfHeight - mapped.y,
                vertexPosition.z,
            )
            set(layout.clip, clipBounds)
        }

        return try {
            builder.text(
                TextProps(font).apply {
                    text = command.text
                    this.origin.set(origin.x, origin.y, 0f)
                    isYAxisUp = false
                },
            )
            if (orderingBarrier) {
                markOrderingBarrier()
            }
            true
        } finally {
            builder.vertexCustomizer = previousCustomizer
            builder.color = previousColor
        }
    }

    private fun alignedTextOrigin(
        command: KoolCanvasCommand.DrawText,
        metrics: TextMetrics,
    ): KoolCanvasPoint {
        val width = metrics.baselineWidth
        val x = when (command.paint.textAlign) {
            KoolCanvasTextAlign.Center -> command.baseline.x - width * 0.5f
            KoolCanvasTextAlign.Right -> command.baseline.x - width
            KoolCanvasTextAlign.Left -> command.baseline.x
        }
        return KoolCanvasPoint(x, command.baseline.y)
    }

    private fun textBounds(origin: KoolCanvasPoint, metrics: TextMetrics): KoolCanvasRect =
        KoolCanvasRect(
            left = origin.x + metrics.paddingStart,
            top = origin.y - metrics.ascentPx,
            right = origin.x + metrics.baselineWidth + metrics.paddingEnd,
            bottom = origin.y - metrics.descentPx,
        )

    private fun addRect(
        scene: Scene,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawRect,
    ): Boolean {
        if (command.paint.isRenderNoOp) return false
        val ordered = command.paint.requiresOrderedPrimitiveBatch()
        val orderingBarrier = command.paint.requiresPrimitiveOrderingBarrier()
        return addRect(
            ensurePrimitiveMesh(
                scene,
                command.paint.renderBlend,
                command.paint.primitiveMeshLayer,
                ordered,
            ).geometry,
            viewport,
            command,
        ).also { added ->
            if (added && orderingBarrier) {
                markOrderingBarrier()
            }
        }
    }

    private fun addFilledIdentityPrimitiveRectRun(
        scene: Scene,
        viewport: KoolCanvasViewport,
        commands: List<KoolCanvasCommand>,
        startIndex: Int,
        firstCommand: KoolCanvasCommand.DrawRect,
    ): Int {
        if (!firstCommand.canUseFilledIdentityPrimitiveRectRun()) return 0
        val geometry = ensurePrimitiveMesh(
            scene,
            CanvasRenderBlend.Alpha,
            PrimitiveMeshLayer.Default,
            ordered = false,
        ).geometry
        var hasOrderingBarrier = false
        var commandIndex = startIndex
        while (commandIndex < commands.size) {
            val command = commands[commandIndex] as? KoolCanvasCommand.DrawRect ?: break
            if (!command.canUseFilledIdentityPrimitiveRectRun()) break
            geometry.addFilledRectIdentityUnchecked(command.rect, command.paint)
            hasOrderingBarrier = hasOrderingBarrier || command.paint.requiresPrimitiveOrderingBarrier()
            commandIndex++
        }
        if (hasOrderingBarrier) {
            markOrderingBarrier()
        }
        return commandIndex - startIndex
    }

    private fun KoolCanvasCommand.DrawRect.canUseFilledIdentityPrimitiveRectRun(): Boolean {
        val paint = paint
        return !paint.isRenderNoOp &&
                paint.style == KoolCanvasPaintStyle.Fill &&
                paint.renderBlend == CanvasRenderBlend.Alpha &&
                state.renderTarget == null &&
                state.clip == null &&
                state.transform === KoolCanvasTransform.Identity &&
                !rect.isEmpty
    }

    private fun addRect(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalColor>,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawRect,
    ): Boolean {
        var added = false
        if (command.paint.style != KoolCanvasPaintStyle.Stroke) {
            added = addFilledRect(geometry, viewport, command.rect, command.paint, command.state) || added
        }
        if (command.paint.style != KoolCanvasPaintStyle.Fill) {
            added = addStrokeRect(geometry, viewport, command.rect, command.paint, command.state) || added
        }
        return added
    }

    private fun addFilledRect(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalColor>,
        viewport: KoolCanvasViewport,
        rect: KoolCanvasRect,
        paint: KoolCanvasPaint,
        state: KoolCanvasState,
    ): Boolean {
        if (state.clip == null && state.transform === KoolCanvasTransform.Identity) {
            return addFilledRectIdentity(geometry, viewport, rect, paint)
        }
        val clippedRect = state.clipForGeometry()?.let { rect.intersect(it) } ?: rect
        if (clippedRect.isEmpty) return false

        val color = paint.toRenderColor()
        val viewportHalfWidth = currentViewportHalfWidth
        val viewportHalfHeight = currentViewportHalfHeight
        val transform = state.transform
        val transformIsIdentity = transform === KoolCanvasTransform.Identity
        val i0 = geometry.addCanvasVertex(
            clippedRect.left,
            clippedRect.top,
            viewportHalfWidth,
            viewportHalfHeight,
            transform,
            transformIsIdentity,
            color,
        )
        val i1 = geometry.addCanvasVertex(
            clippedRect.right,
            clippedRect.top,
            viewportHalfWidth,
            viewportHalfHeight,
            transform,
            transformIsIdentity,
            color,
        )
        val i2 = geometry.addCanvasVertex(
            clippedRect.right,
            clippedRect.bottom,
            viewportHalfWidth,
            viewportHalfHeight,
            transform,
            transformIsIdentity,
            color,
        )
        val i3 = geometry.addCanvasVertex(
            clippedRect.left,
            clippedRect.bottom,
            viewportHalfWidth,
            viewportHalfHeight,
            transform,
            transformIsIdentity,
            color,
        )
        geometry.addTriIndices(i0, i1, i2)
        geometry.addTriIndices(i0, i2, i3)
        return true
    }

    private fun addFilledRectIdentity(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalColor>,
        viewport: KoolCanvasViewport,
        rect: KoolCanvasRect,
        paint: KoolCanvasPaint,
    ): Boolean {
        if (rect.isEmpty) return false

        val color = paint.toRenderColor()
        val viewportHalfWidth = currentViewportHalfWidth
        val viewportHalfHeight = currentViewportHalfHeight
        val i0 = geometry.addCanvasVertexIdentity(rect.left, rect.top, viewportHalfWidth, viewportHalfHeight, color)
        val i1 = geometry.addCanvasVertexIdentity(rect.right, rect.top, viewportHalfWidth, viewportHalfHeight, color)
        val i2 = geometry.addCanvasVertexIdentity(rect.right, rect.bottom, viewportHalfWidth, viewportHalfHeight, color)
        val i3 = geometry.addCanvasVertexIdentity(rect.left, rect.bottom, viewportHalfWidth, viewportHalfHeight, color)
        geometry.addTriIndices(i0, i1, i2)
        geometry.addTriIndices(i0, i2, i3)
        return true
    }

    private fun IndexedVertexList<VertexLayouts.PositionNormalColor>.addFilledRectIdentityUnchecked(
        rect: KoolCanvasRect,
        paint: KoolCanvasPaint,
    ) {
        val color = paint.toRenderColor()
        val viewportHalfWidth = currentViewportHalfWidth
        val viewportHalfHeight = currentViewportHalfHeight
        val i0 = addCanvasVertexIdentity(rect.left, rect.top, viewportHalfWidth, viewportHalfHeight, color)
        val i1 = addCanvasVertexIdentity(rect.right, rect.top, viewportHalfWidth, viewportHalfHeight, color)
        val i2 = addCanvasVertexIdentity(rect.right, rect.bottom, viewportHalfWidth, viewportHalfHeight, color)
        val i3 = addCanvasVertexIdentity(rect.left, rect.bottom, viewportHalfWidth, viewportHalfHeight, color)
        addTriIndices(i0, i1, i2)
        addTriIndices(i0, i2, i3)
    }

    private fun addStrokeRect(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalColor>,
        viewport: KoolCanvasViewport,
        rect: KoolCanvasRect,
        paint: KoolCanvasPaint,
        state: KoolCanvasState,
    ): Boolean {
        if (rect.isEmpty || !state.intersectsClip(rect.grow(paint.strokeWidth * 0.5f))) return false

        val top = addThickLine(
            geometry = geometry,
            viewport = viewport,
            start = KoolCanvasPoint(rect.left, rect.top),
            end = KoolCanvasPoint(rect.right, rect.top),
            paint = paint,
            state = state,
        )
        val right = addThickLine(
            geometry = geometry,
            viewport = viewport,
            start = KoolCanvasPoint(rect.right, rect.top),
            end = KoolCanvasPoint(rect.right, rect.bottom),
            paint = paint,
            state = state,
        )
        val bottom = addThickLine(
            geometry = geometry,
            viewport = viewport,
            start = KoolCanvasPoint(rect.right, rect.bottom),
            end = KoolCanvasPoint(rect.left, rect.bottom),
            paint = paint,
            state = state,
        )
        val left = addThickLine(
            geometry = geometry,
            viewport = viewport,
            start = KoolCanvasPoint(rect.left, rect.bottom),
            end = KoolCanvasPoint(rect.left, rect.top),
            paint = paint,
            state = state,
        )
        return top || right || bottom || left
    }

    private fun addLine(
        scene: Scene,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawLine,
    ): Boolean {
        if (command.paint.isRenderNoOp) return false
        val ordered = command.paint.requiresOrderedPrimitiveBatch()
        val orderingBarrier = command.paint.requiresPrimitiveOrderingBarrier()
        return addLine(
            ensurePrimitiveMesh(
                scene,
                command.paint.renderBlend,
                command.paint.primitiveMeshLayer,
                ordered,
            ).geometry,
            viewport,
            command,
        ).also { added ->
            if (added && orderingBarrier) {
                markOrderingBarrier()
            }
        }
    }

    private fun addLine(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalColor>,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawLine,
    ): Boolean {
        if (command.state.clip != null) {
            val halfWidth = command.paint.strokeWidth.coerceAtLeast(1f) * 0.5f
            val bounds = KoolCanvasRect(
                left = minOf(command.start.x, command.end.x) - halfWidth,
                top = minOf(command.start.y, command.end.y) - halfWidth,
                right = maxOf(command.start.x, command.end.x) + halfWidth,
                bottom = maxOf(command.start.y, command.end.y) + halfWidth,
            )
            if (!command.state.intersectsClip(bounds)) return false
        }
        return addThickLine(geometry, viewport, command.start, command.end, command.paint, command.state)
    }

    private fun addCircle(
        scene: Scene,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawCircle,
    ): Boolean {
        if (command.paint.isRenderNoOp) return false
        val ordered = command.paint.requiresOrderedPrimitiveBatch()
        val orderingBarrier = command.paint.requiresPrimitiveOrderingBarrier()
        return addCircle(
            ensurePrimitiveMesh(
                scene,
                command.paint.renderBlend,
                command.paint.primitiveMeshLayer,
                ordered,
            ).geometry,
            viewport,
            command,
        ).also { added ->
            if (added && orderingBarrier) {
                markOrderingBarrier()
            }
        }
    }

    private fun addCircle(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalColor>,
        viewport: KoolCanvasViewport,
        command: KoolCanvasCommand.DrawCircle,
    ): Boolean {
        if (command.state.clip != null) {
            val strokeHalfWidth = command.paint.strokeWidth.coerceAtLeast(1f) * 0.5f
            val bounds = KoolCanvasRect(
                left = command.center.x - command.radius - strokeHalfWidth,
                top = command.center.y - command.radius - strokeHalfWidth,
                right = command.center.x + command.radius + strokeHalfWidth,
                bottom = command.center.y + command.radius + strokeHalfWidth,
            )
            if (!command.state.intersectsClip(bounds)) return false
        }

        var added = false
        if (command.paint.style != KoolCanvasPaintStyle.Stroke && command.radius > 0f) {
            added = addFilledCircle(
                geometry = geometry,
                viewport = viewport,
                center = command.center,
                radius = command.radius,
                paint = command.paint,
                state = command.state,
            ) || added
        }
        if (command.paint.style != KoolCanvasPaintStyle.Fill) {
            added = addCircleStroke(
                geometry = geometry,
                viewport = viewport,
                center = command.center,
                radius = command.radius,
                paint = command.paint,
                state = command.state,
            ) || added
        }
        return added
    }

    private fun addThickLine(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalColor>,
        viewport: KoolCanvasViewport,
        start: KoolCanvasPoint,
        end: KoolCanvasPoint,
        paint: KoolCanvasPaint,
        state: KoolCanvasState,
    ): Boolean {
        val transform = state.transform
        val transformIsIdentity = transform === KoolCanvasTransform.Identity
        val mappedStart = if (transformIsIdentity) {
            start
        } else {
            transform.map(start)
        }
        val mappedEnd = if (transformIsIdentity) {
            end
        } else {
            transform.map(end)
        }
        val dx = mappedEnd.x - mappedStart.x
        val dy = mappedEnd.y - mappedStart.y
        val length = sqrt(dx * dx + dy * dy)
        val halfWidth = paint.strokeWidth.coerceAtLeast(1f) * 0.5f
        if (length == 0f) {
            return addFilledCircle(
                geometry,
                viewport,
                mappedStart,
                halfWidth,
                paint,
                state.copy(transform = KoolCanvasTransform.Identity)
            )
        }

        val normalX = -dy / length * halfWidth
        val normalY = dx / length * halfWidth
        val color = paint.toRenderColor()
        val viewportHalfWidth = currentViewportHalfWidth
        val viewportHalfHeight = currentViewportHalfHeight
        val i0 = geometry.addMappedCanvasVertex(
            mappedStart.x + normalX,
            mappedStart.y + normalY,
            viewportHalfWidth,
            viewportHalfHeight,
            color,
        )
        val i1 = geometry.addMappedCanvasVertex(
            mappedEnd.x + normalX,
            mappedEnd.y + normalY,
            viewportHalfWidth,
            viewportHalfHeight,
            color,
        )
        val i2 = geometry.addMappedCanvasVertex(
            mappedEnd.x - normalX,
            mappedEnd.y - normalY,
            viewportHalfWidth,
            viewportHalfHeight,
            color,
        )
        val i3 = geometry.addMappedCanvasVertex(
            mappedStart.x - normalX,
            mappedStart.y - normalY,
            viewportHalfWidth,
            viewportHalfHeight,
            color,
        )
        geometry.addTriIndices(i0, i1, i2)
        geometry.addTriIndices(i0, i2, i3)
        return true
    }

    private fun addFilledCircle(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalColor>,
        viewport: KoolCanvasViewport,
        center: KoolCanvasPoint,
        radius: Float,
        paint: KoolCanvasPaint,
        state: KoolCanvasState,
    ): Boolean {
        if (radius <= 0f) return false

        val color = paint.toRenderColor()
        val viewportHalfWidth = currentViewportHalfWidth
        val viewportHalfHeight = currentViewportHalfHeight
        val transform = state.transform
        val transformIsIdentity = transform === KoolCanvasTransform.Identity
        val centerIndex = geometry.addCanvasVertex(
            center.x,
            center.y,
            viewportHalfWidth,
            viewportHalfHeight,
            transform,
            transformIsIdentity,
            color,
        )
        val segments = circleSegmentCount(radius)
        var previous = geometry.addCircleVertex(
            center,
            radius,
            0,
            segments,
            viewportHalfWidth,
            viewportHalfHeight,
            transform,
            transformIsIdentity,
            color,
        )
        for (segment in 1..segments) {
            val next = geometry.addCircleVertex(
                center,
                radius,
                segment,
                segments,
                viewportHalfWidth,
                viewportHalfHeight,
                transform,
                transformIsIdentity,
                color,
            )
            geometry.addTriIndices(centerIndex, previous, next)
            previous = next
        }
        return true
    }

    private fun addCircleStroke(
        geometry: IndexedVertexList<VertexLayouts.PositionNormalColor>,
        viewport: KoolCanvasViewport,
        center: KoolCanvasPoint,
        radius: Float,
        paint: KoolCanvasPaint,
        state: KoolCanvasState,
    ): Boolean {
        val halfWidth = paint.strokeWidth.coerceAtLeast(1f) * 0.5f
        val outerRadius = radius + halfWidth
        val innerRadius = max(0f, radius - halfWidth)
        if (innerRadius == 0f) {
            return addFilledCircle(geometry, viewport, center, outerRadius, paint, state)
        }

        val color = paint.toRenderColor()
        val segments = circleSegmentCount(outerRadius)
        val viewportHalfWidth = currentViewportHalfWidth
        val viewportHalfHeight = currentViewportHalfHeight
        val transform = state.transform
        val transformIsIdentity = transform === KoolCanvasTransform.Identity
        val mappedCenter = if (transformIsIdentity) center else transform.map(center)
        var previousOuter = geometry.addCircleStrokeVertex(
            center,
            mappedCenter,
            radius,
            halfWidth,
            0,
            segments,
            viewportHalfWidth,
            viewportHalfHeight,
            transform,
            transformIsIdentity,
            color,
            outer = true,
        )
        var previousInner = geometry.addCircleStrokeVertex(
            center,
            mappedCenter,
            radius,
            halfWidth,
            0,
            segments,
            viewportHalfWidth,
            viewportHalfHeight,
            transform,
            transformIsIdentity,
            color,
            outer = false,
        )
        for (segment in 1..segments) {
            val nextOuter =
                geometry.addCircleStrokeVertex(
                    center,
                    mappedCenter,
                    radius,
                    halfWidth,
                    segment,
                    segments,
                    viewportHalfWidth,
                    viewportHalfHeight,
                    transform,
                    transformIsIdentity,
                    color,
                    outer = true,
                )
            val nextInner =
                geometry.addCircleStrokeVertex(
                    center,
                    mappedCenter,
                    radius,
                    halfWidth,
                    segment,
                    segments,
                    viewportHalfWidth,
                    viewportHalfHeight,
                    transform,
                    transformIsIdentity,
                    color,
                    outer = false,
                )
            geometry.addTriIndices(previousOuter, nextOuter, nextInner)
            geometry.addTriIndices(previousOuter, nextInner, previousInner)
            previousOuter = nextOuter
            previousInner = nextInner
        }
        return true
    }

    private fun circleSegmentCount(radius: Float): Int =
        ceil(radius / 6f).toInt().coerceIn(16, 96)

    private fun IndexedVertexList<VertexLayouts.PositionNormalColor>.addCircleVertex(
        center: KoolCanvasPoint,
        radius: Float,
        segment: Int,
        segmentCount: Int,
        viewportHalfWidth: Float,
        viewportHalfHeight: Float,
        transform: KoolCanvasTransform,
        transformIsIdentity: Boolean,
        color: Color,
    ): Int {
        val unit = circleUnitCache(segmentCount)
        return addCanvasVertex(
            x = center.x + unit.first[segment] * radius,
            y = center.y + unit.second[segment] * radius,
            viewportHalfWidth = viewportHalfWidth,
            viewportHalfHeight = viewportHalfHeight,
            transform = transform,
            transformIsIdentity = transformIsIdentity,
            color = color,
        )
    }

    private fun IndexedVertexList<VertexLayouts.PositionNormalColor>.addCircleStrokeVertex(
        center: KoolCanvasPoint,
        mappedCenter: KoolCanvasPoint,
        radius: Float,
        halfWidth: Float,
        segment: Int,
        segmentCount: Int,
        viewportHalfWidth: Float,
        viewportHalfHeight: Float,
        transform: KoolCanvasTransform,
        transformIsIdentity: Boolean,
        color: Color,
        outer: Boolean,
    ): Int {
        val unit = circleUnitCache(segmentCount)
        val localX = center.x + unit.first[segment] * radius
        val localY = center.y + unit.second[segment] * radius
        val mappedX: Float
        val mappedY: Float
        if (transformIsIdentity) {
            mappedX = localX
            mappedY = localY
        } else {
            mappedX = transform.scaleX * localX + transform.skewX * localY + transform.translateX
            mappedY = transform.skewY * localX + transform.scaleY * localY + transform.translateY
        }
        var normalX = mappedX - mappedCenter.x
        var normalY = mappedY - mappedCenter.y
        val normalLength = sqrt(normalX * normalX + normalY * normalY)
        if (normalLength > 0f) {
            normalX /= normalLength
            normalY /= normalLength
        } else {
            normalX = unit.first[segment]
            normalY = unit.second[segment]
        }
        val width = if (outer) halfWidth else -halfWidth
        return addMappedCanvasVertex(
            x = mappedX + normalX * width,
            y = mappedY + normalY * width,
            viewportHalfWidth = viewportHalfWidth,
            viewportHalfHeight = viewportHalfHeight,
            color = color,
        )
    }

    private fun circleUnitCache(segmentCount: Int): Pair<FloatArray, FloatArray> {
        val cached = CIRCLE_UNIT_CACHE[segmentCount]
        if (cached != null) {
            return cached
        }
        val xs = FloatArray(segmentCount + 1)
        val ys = FloatArray(segmentCount + 1)
        for (segment in 0..segmentCount) {
            val angle = (segment.toFloat() / segmentCount) * (PI.toFloat() * 2f)
            xs[segment] = cos(angle)
            ys[segment] = sin(angle)
        }
        return (xs to ys).also { CIRCLE_UNIT_CACHE[segmentCount] = it }
    }

    private fun IndexedVertexList<VertexLayouts.PositionNormalColor>.addCanvasVertex(
        x: Float,
        y: Float,
        viewport: KoolCanvasViewport,
        transform: KoolCanvasTransform,
        color: Color,
    ): Int =
        addCanvasVertex(
            x = x,
            y = y,
            viewportHalfWidth = currentViewportHalfWidth,
            viewportHalfHeight = currentViewportHalfHeight,
            transform = transform,
            transformIsIdentity = transform === KoolCanvasTransform.Identity,
            color = color,
        )

    private fun IndexedVertexList<VertexLayouts.PositionNormalColor>.addCanvasVertex(
        x: Float,
        y: Float,
        viewportHalfWidth: Float,
        viewportHalfHeight: Float,
        transform: KoolCanvasTransform,
        transformIsIdentity: Boolean,
        color: Color,
    ): Int {
        val mappedX: Float
        val mappedY: Float
        if (transformIsIdentity) {
            mappedX = x
            mappedY = y
        } else {
            mappedX = transform.scaleX * x + transform.skewX * y + transform.translateX
            mappedY = transform.skewY * x + transform.scaleY * y + transform.translateY
        }
        return addVertex { layout ->
            set(layout.position, mappedX - viewportHalfWidth, viewportHalfHeight - mappedY, 0f)
            set(layout.normal, 0f, 0f, 1f)
            set(layout.color, color.r, color.g, color.b, color.a)
        }
    }

    private fun IndexedVertexList<VertexLayouts.PositionNormalColor>.addCanvasVertexIdentity(
        x: Float,
        y: Float,
        viewportHalfWidth: Float,
        viewportHalfHeight: Float,
        color: Color,
    ): Int =
        addMappedCanvasVertex(x, y, viewportHalfWidth, viewportHalfHeight, color)

    private fun IndexedVertexList<VertexLayouts.PositionNormalColor>.addMappedCanvasVertex(
        x: Float,
        y: Float,
        viewportHalfWidth: Float,
        viewportHalfHeight: Float,
        color: Color,
    ): Int =
        addVertex { layout ->
            set(layout.position, x - viewportHalfWidth, viewportHalfHeight - y, 0f)
            set(layout.normal, 0f, 0f, 1f)
            set(layout.color, color.r, color.g, color.b, color.a)
        }

    private fun IndexedVertexList<VertexLayouts.PositionNormalTexCoordColor>.addCanvasTextureVertex(
        x: Float,
        y: Float,
        u: Float,
        v: Float,
        viewportHalfWidth: Float,
        viewportHalfHeight: Float,
        transform: KoolCanvasTransform,
        transformIsIdentity: Boolean,
        color: Color,
    ): Int {
        val mappedX: Float
        val mappedY: Float
        if (transformIsIdentity) {
            mappedX = x
            mappedY = y
        } else {
            mappedX = transform.scaleX * x + transform.skewX * y + transform.translateX
            mappedY = transform.skewY * x + transform.scaleY * y + transform.translateY
        }
        return addVertex { layout ->
            set(layout.position, mappedX - viewportHalfWidth, viewportHalfHeight - mappedY, 0f)
            set(layout.normal, 0f, 0f, 1f)
            set(layout.texCoord, u, v)
            set(layout.color, color.r, color.g, color.b, color.a)
        }
    }

    private fun KoolCanvasState.intersectsClip(bounds: KoolCanvasRect): Boolean =
        clipForGeometry()?.let { bounds.boundsIntersect(it) != null } ?: true

    private fun KoolCanvasState.clipForGeometry(): KoolCanvasRect? {
        val currentClip = clip ?: return null
        val currentTransform = transform
        if (frameProjectionDepth > 0 || currentTransform === KoolCanvasTransform.Identity) {
            return currentClip
        }
        return currentTransform.inverted()?.mapRect(currentClip) ?: currentClip
    }

    private fun KoolCanvasState.projected(
        outerState: KoolCanvasState,
        placementTransform: KoolCanvasTransform,
        frameClip: KoolCanvasRect,
    ): KoolCanvasState? {
        val localFrameClip = transform.inverted()?.mapRect(frameClip) ?: return null
        val projectedClip = clip?.boundsIntersect(localFrameClip) ?: localFrameClip
        return copy(
            transform = outerState.transform
                .multiply(placementTransform)
                .multiply(transform),
            clip = projectedClip,
        )
    }

    private fun KoolCanvasRect.toDestinationTransform(destination: KoolCanvasRect): KoolCanvasTransform =
        KoolCanvasTransform.Identity
            .translate(destination.left, destination.top)
            .scale(destination.width / width, destination.height / height)
            .translate(-left, -top)

    private fun KoolCanvasCommand.DrawTexture.frameSourceClip(frame: KoolCanvasFrame): KoolCanvasRect? {
        val frameBounds = KoolCanvasRect.fromSize(frame.viewport.width.toFloat(), frame.viewport.height.toFloat())
        var frameClip = source.boundsIntersect(frameBounds) ?: return null
        state.clip?.let { clip ->
            val clippedDestinationBounds = destination.boundsIntersect(clip) ?: return null
            val clippedDestination = destination.orientBounds(clippedDestinationBounds)
            frameClip = frameClip.boundsIntersect(destination.mapSubRectTo(source, clippedDestination)) ?: return null
        }
        return frameClip
    }

    private fun KoolCanvasRect.mapSubRectTo(target: KoolCanvasRect, subRect: KoolCanvasRect): KoolCanvasRect {
        val scaleX = target.width / width
        val scaleY = target.height / height
        return KoolCanvasRect(
            left = target.left + (subRect.left - left) * scaleX,
            top = target.top + (subRect.top - top) * scaleY,
            right = target.left + (subRect.right - left) * scaleX,
            bottom = target.top + (subRect.bottom - top) * scaleY,
        )
    }

    private fun KoolCanvasTransform.inverted(): KoolCanvasTransform? {
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

    private fun KoolCanvasTransform.mapRect(rect: KoolCanvasRect): KoolCanvasRect {
        val p0 = map(KoolCanvasPoint(rect.left, rect.top))
        val p1 = map(KoolCanvasPoint(rect.right, rect.top))
        val p2 = map(KoolCanvasPoint(rect.right, rect.bottom))
        val p3 = map(KoolCanvasPoint(rect.left, rect.bottom))
        return KoolCanvasRect(
            left = minOf(p0.x, p1.x, p2.x, p3.x),
            top = minOf(p0.y, p1.y, p2.y, p3.y),
            right = maxOf(p0.x, p1.x, p2.x, p3.x),
            bottom = maxOf(p0.y, p1.y, p2.y, p3.y),
        )
    }

    private fun KoolCanvasRect?.toWorldClip(viewport: KoolCanvasViewport): Vec4f {
        val halfWidth = viewport.width * 0.5f
        val halfHeight = viewport.height * 0.5f
        val rect = this ?: KoolCanvasRect(0f, 0f, viewport.width.toFloat(), viewport.height.toFloat())
        return Vec4f(
            rect.left - halfWidth,
            halfHeight - rect.bottom,
            rect.right - halfWidth,
            halfHeight - rect.top,
        )
    }

    private fun KoolCanvasRect.grow(amount: Float): KoolCanvasRect = KoolCanvasRect(
        left = left - amount,
        top = top - amount,
        right = right + amount,
        bottom = bottom + amount,
    )

    private val KoolCanvasPaint.renderBlend: CanvasRenderBlend
        get() = when (blendMode) {
            KoolCanvasBlendMode.Add -> CanvasRenderBlend.Additive
            KoolCanvasBlendMode.Clear,
            KoolCanvasBlendMode.ClearAlpha,
            KoolCanvasBlendMode.Source -> CanvasRenderBlend.Source

            else -> CanvasRenderBlend.Alpha
        }

    private val KoolCanvasPaint.primitiveMeshLayer: PrimitiveMeshLayer
        get() = PrimitiveMeshLayer.Default

    private val KoolCanvasPaint.isRenderNoOp: Boolean
        get() = blendMode == KoolCanvasBlendMode.Destination || alphaMultiplier <= 0f

    private val KoolCanvasPaint.effectiveAlpha: Float
        get() = (color.alpha / 255f) * alphaMultiplier

    private fun KoolCanvasPaint.requiresOrderedPrimitiveBatch(): Boolean =
        frameProjectionDepth == 0 && renderBlend != CanvasRenderBlend.Alpha

    private fun KoolCanvasPaint.requiresOrderedTextBatch(): Boolean =
        frameProjectionDepth == 0 && requiresTextOrderingBarrier()

    private fun KoolCanvasPaint.requiresPrimitiveOrderingBarrier(): Boolean =
        renderBlend != CanvasRenderBlend.Alpha || effectiveAlpha < 0.999f

    private fun KoolCanvasPaint.requiresTextureOrderingBarrier(texture: KoolCanvasTextureRef): Boolean =
        renderBlend != CanvasRenderBlend.Alpha ||
                effectiveAlpha < 0.999f ||
                texture.hasAlpha ||
                texture.requiresOrderedAlpha ||
                texture.premultipliedAlpha

    private fun KoolCanvasPaint.requiresTextOrderingBarrier(): Boolean =
        renderBlend != CanvasRenderBlend.Alpha || effectiveAlpha < 0.999f || frameProjectionDepth == 0

    private fun KoolCanvasPaint.toRenderColor(): Color {
        val directCachedPaint = lastRenderColorPaint
        if (directCachedPaint === this) {
            return lastRenderColor!!
        }

        for (index in 0 until RENDER_COLOR_CACHE_SIZE) {
            if (renderColorCachePaints[index] == this) {
                val cachedColor = renderColorCacheColors[index]!!
                lastRenderColorPaint = this
                lastRenderColor = cachedColor
                return cachedColor
            }
        }

        val color = if (blendMode == KoolCanvasBlendMode.Clear || blendMode == KoolCanvasBlendMode.ClearAlpha) {
            KoolCanvasColor.Transparent.toKoolColor()
        } else {
            color.toKoolColor(alphaMultiplier)
        }
        val cacheSlot = nextRenderColorCacheSlot
        renderColorCachePaints[cacheSlot] = this
        renderColorCacheColors[cacheSlot] = color
        nextRenderColorCacheSlot = (cacheSlot + 1) % RENDER_COLOR_CACHE_SIZE
        lastRenderColorPaint = this
        lastRenderColor = color
        return color
    }

    private fun KoolCanvasCommand.Clear.toClearColor(): Color =
        if (blendMode == KoolCanvasBlendMode.Clear || blendMode == KoolCanvasBlendMode.ClearAlpha) {
            KoolCanvasColor.Transparent.toKoolColor()
        } else {
            color.toKoolColor()
        }

    private fun KoolCanvasColor.toVec4f(): Vec4f =
        Vec4f(red / 255f, green / 255f, blue / 255f, alpha / 255f)

    private fun KoolCanvasPaint.withOuterPaint(outer: KoolCanvasPaint): KoolCanvasPaint =
        copy(
            color = color.multipliedBy(outer.color),
            alphaMultiplier = (alphaMultiplier * outer.alphaMultiplier).coerceIn(0f, 1f),
            blendMode = projectedBlendMode(outer),
            textureEffect = textureEffect ?: outer.textureEffect,
        )

    private fun KoolCanvasPaint.projectedBlendMode(outer: KoolCanvasPaint): KoolCanvasBlendMode =
        when {
            outer.blendMode != KoolCanvasBlendMode.SourceOver -> outer.blendMode
            blendMode == KoolCanvasBlendMode.Source ||
                    blendMode == KoolCanvasBlendMode.Clear ||
                    blendMode == KoolCanvasBlendMode.ClearAlpha ->
                KoolCanvasBlendMode.SourceOver

            else -> blendMode
        }

    private fun KoolCanvasColor.multipliedBy(outer: KoolCanvasColor): KoolCanvasColor =
        KoolCanvasColor(
            ((multiplyChannel(alpha, outer.alpha) and 0xff) shl 24) or
                    ((multiplyChannel(red, outer.red) and 0xff) shl 16) or
                    ((multiplyChannel(green, outer.green) and 0xff) shl 8) or
                    (multiplyChannel(blue, outer.blue) and 0xff),
        )

    private fun multiplyChannel(source: Int, multiplier: Int): Int = (source * multiplier) / 255

    private fun CanvasRenderBlend.textureBlendMode(premultipliedAlpha: Boolean): KoolBlendMode =
        if (this == CanvasRenderBlend.Alpha && premultipliedAlpha) {
            KoolBlendMode.BLEND_PREMULTIPLIED_ALPHA
        } else {
            textureBlendMode
        }

    private enum class CanvasRenderBlend(
        val meshNameSuffix: String,
        val primitiveBlendMode: KoolBlendMode,
        val textureBlendMode: KoolBlendMode,
        val textBlendMode: KoolBlendMode,
    ) {
        Alpha(
            meshNameSuffix = "",
            primitiveBlendMode = KoolBlendMode.BLEND_MULTIPLY_ALPHA,
            textureBlendMode = KoolBlendMode.BLEND_MULTIPLY_ALPHA,
            textBlendMode = KoolBlendMode.BLEND_PREMULTIPLIED_ALPHA,
        ),
        Additive(
            meshNameSuffix = "-additive",
            primitiveBlendMode = KoolBlendMode.BLEND_ADDITIVE,
            textureBlendMode = KoolBlendMode.BLEND_ADDITIVE,
            textBlendMode = KoolBlendMode.BLEND_ADDITIVE,
        ),
        Source(
            meshNameSuffix = "-source",
            primitiveBlendMode = KoolBlendMode.DISABLED,
            textureBlendMode = KoolBlendMode.DISABLED,
            textBlendMode = KoolBlendMode.DISABLED,
        ),
    }

    private enum class PrimitiveMeshLayer(val meshNameSuffix: String) {
        Default(""),
    }

    private companion object {
        const val MaxProjectedOrderingSegmentAdvances = 128
        const val MAX_FRAME_TEXTURE_PROJECTION_DEPTH: Int = 8
        const val MAX_PROJECTED_FRAME_TEXTURE_EXPANSIONS: Int = 192
        const val RESOLVED_TEXTURE_CACHE_SIZE: Int = 16
        const val FRAME_TEXTURE_CACHE_SIZE: Int = 4
        const val FRAME_TEXTURE_MISS_CACHE_SIZE: Int = 64
        const val RENDER_COLOR_CACHE_SIZE: Int = 8
        const val TEXTURE_RUN_CACHE_SIZE: Int = 16
        const val ORDERED_TEXTURE_INSTANCE_INITIAL_SIZE: Int = 128
        const val UNORDERED_TEXTURE_INSTANCE_INITIAL_SIZE: Int = 8192
        val CIRCLE_UNIT_CACHE: Array<Pair<FloatArray, FloatArray>?> = arrayOfNulls(97)
    }
}
