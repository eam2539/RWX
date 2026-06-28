package io.github.rwx.render

import com.corrodinggames.rts.game.Projectile
import com.corrodinggames.rts.game.ProjectileTemplate
import com.corrodinggames.rts.game.units.OrderableUnit
import com.corrodinggames.rts.game.units.custom.CustomProjectileTemplate
import com.corrodinggames.rts.game.units.custom.CustomUnit
import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.effects.Effect
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
import com.corrodinggames.rts.gameFramework.graphics.Texture
import io.github.rwx.geometry.Rect
import io.github.rwx.geometry.RectF
import io.github.rwx.logger
import io.github.rwx.mod.ApiImpl
import io.github.rwx.mod.api.*
import io.github.rwx.render.canvas.KoolCanvasBlendMode
import io.github.rwx.render.canvas.KoolPaint
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

object ModRenderRegistry {
    private data class RegisteredTexture(
        val owner: ApiImpl,
        val path: ResourcePath,
        val options: TextureOptions,
        var texture: Texture? = null,
    )

    private data class RegisteredProjectileRenderer(
        val owner: ApiImpl,
        val renderer: ProjectileRenderer,
    )

    private data class RegisteredPreFireRenderer(
        val owner: ApiImpl,
        val renderer: PreFireRenderer,
    )

    private data class RegisteredEffectRenderer(
        val owner: ApiImpl,
        val renderer: EffectRenderer,
    )

    private data class RegisteredUnitRenderer(
        val owner: ApiImpl,
        val renderer: UnitRenderer,
    )

    private data class BoundUnitRenderer(
        val owner: ApiImpl,
        val definitionId: UnitId,
        val binding: UnitRenderBinding,
    )

    private val lock = Any()
    private val textures = linkedMapOf<String, RegisteredTexture>()
    private val projectileRenderers = linkedMapOf<String, RegisteredProjectileRenderer>()
    private val preFireRenderers = linkedMapOf<String, RegisteredPreFireRenderer>()
    private val effectRenderers = linkedMapOf<String, RegisteredEffectRenderer>()
    private val unitRenderers = linkedMapOf<String, RegisteredUnitRenderer>()
    private val unitBindings =
        IdentityHashMap<com.corrodinggames.rts.game.units.custom.CustomUnitConfig, BoundUnitRenderer>()
    private val reportedFailures = mutableSetOf<String>()

    fun registerTexture(owner: ApiImpl, id: TextureId, path: ResourcePath, options: TextureOptions) {
        synchronized(lock) {
            check(id.value !in textures) { "Texture is already registered: ${id.value}" }
            textures[id.value] = RegisteredTexture(owner, path, options)
        }
    }

    fun registerProjectileRenderer(owner: ApiImpl, id: RendererId, renderer: ProjectileRenderer) {
        synchronized(lock) {
            check(id.value !in projectileRenderers) { "Projectile renderer is already registered: ${id.value}" }
            projectileRenderers[id.value] = RegisteredProjectileRenderer(owner, renderer)
        }
    }

    fun registerPreFireRenderer(owner: ApiImpl, id: RendererId, renderer: PreFireRenderer) {
        synchronized(lock) {
            check(id.value !in preFireRenderers) { "Pre-fire renderer is already registered: ${id.value}" }
            preFireRenderers[id.value] = RegisteredPreFireRenderer(owner, renderer)
        }
    }

    fun registerEffectRenderer(owner: ApiImpl, id: RendererId, renderer: EffectRenderer) {
        synchronized(lock) {
            check(id.value !in effectRenderers) { "Effect renderer is already registered: ${id.value}" }
            effectRenderers[id.value] = RegisteredEffectRenderer(owner, renderer)
        }
    }

    fun registerUnitRenderer(owner: ApiImpl, id: RendererId, renderer: UnitRenderer) {
        synchronized(lock) {
            check(id.value !in unitRenderers) { "Unit renderer is already registered: ${id.value}" }
            unitRenderers[id.value] = RegisteredUnitRenderer(owner, renderer)
        }
    }

    fun activateUnitBindings(
        owner: ApiImpl,
        nativeUnits: Map<UnitId, com.corrodinggames.rts.game.units.custom.CustomUnitConfig>,
        definitions: List<UnitDefinition>,
    ): Int = synchronized(lock) {
        unitBindings.entries.removeAll { it.value.owner === owner }
        var count = 0
        definitions.forEach { definition ->
            val binding = definition.extension.renderBinding ?: return@forEach
            val renderer = checkNotNull(unitRenderers[binding.rendererId.value]) {
                "Unit renderer is not registered: ${binding.rendererId.value}"
            }
            check(renderer.owner === owner) {
                "Unit renderer ${binding.rendererId.value} belongs to another mod"
            }
            val nativeUnit = checkNotNull(nativeUnits[definition.id]) {
                "Native unit is not active: ${definition.id.value}"
            }
            unitBindings[nativeUnit] = BoundUnitRenderer(owner, definition.id, binding)
            count++
        }
        count
    }

    fun unregister(owner: ApiImpl) {
        synchronized(lock) {
            textures.entries.removeAll { it.value.owner === owner }
            projectileRenderers.entries.removeAll { it.value.owner === owner }
            preFireRenderers.entries.removeAll { it.value.owner === owner }
            effectRenderers.entries.removeAll { it.value.owner === owner }
            unitRenderers.entries.removeAll { it.value.owner === owner }
            unitBindings.entries.removeAll { it.value.owner === owner }
        }
    }

    @JvmStatic
    fun drawUnit(unit: CustomUnit, gameEngine: GameEngine, layer: UnitRenderLayer) {
        val bound = synchronized(lock) { unitBindings[unit.unitConfig] } ?: return
        val registration = synchronized(lock) { unitRenderers[bound.binding.rendererId.value] } ?: return
        val unitOffset = unit.getRenderOffset()
        val centerX = unit.posX + unitOffset.x - gameEngine.viewpointXSnapped
        val centerY = unit.posY + unitOffset.y - unit.posZ - gameEngine.viewpointYSnapped
        val bounds = unit.getUnitBounds()
        val imageScale = unit.getRenderScale()
        val width = (abs(bounds.c - bounds.a).takeIf { it > 0f } ?: unit.radius * 2f) * imageScale
        val height = (abs(bounds.d - bounds.b).takeIf { it > 0f } ?: unit.radius * 2f) * imageScale
        val canvas = EngineRenderCanvas(gameEngine.renderGraphicsEngine)
        try {
            renderSafely("unit:${bound.binding.rendererId.value}") {
                registration.renderer.render(
                    UnitRenderContext(
                        instanceId = UnitInstanceId(unit.objectId),
                        definitionId = bound.definitionId,
                        teamId = unit.team?.teamId ?: -1,
                        centerX = centerX,
                        centerY = centerY,
                        drawWidth = width,
                        drawHeight = height,
                        collisionRadius = unit.radius,
                        rotationDegrees = unit.rotationSpeed,
                        destroyed = unit.isDestroyed,
                        layer = layer,
                        variantId = bound.binding.variantId,
                    ),
                    canvas,
                )
            }
        } finally {
            canvas.finish()
        }
    }

    @JvmStatic
    fun drawPreFire(unit: CustomUnit, gameEngine: GameEngine) {
        var canvas: EngineRenderCanvas? = null
        try {
            unit.unitConfig.turrets.forEachIndexed { turretIndex, turret ->
                val rendererId = turret?.preFireRendererId ?: return@forEachIndexed
                val variantId = turret.preFireRendererVariant ?: return@forEachIndexed
                val registration = synchronized(lock) { preFireRenderers[rendererId] }
                    ?: return@forEachIndexed
                val movement = unit.movementLevels[turretIndex]
                val target = movement.targetUnit ?: return@forEachIndexed
                val phaseStart = (turret.warmup - turret.preFireDuration).coerceAtLeast(0f)
                val age = movement.speed - phaseStart
                if (unit.isDestroyed || turret.preFireDuration <= 0f || age <= 0f) return@forEachIndexed

                val muzzle = unit.D(turretIndex)
                val activeCanvas = canvas ?: EngineRenderCanvas(gameEngine.renderGraphicsEngine).also { canvas = it }
                renderSafely("pre-fire:$rendererId") {
                    registration.renderer.render(
                        PreFireRenderContext(
                            startX = muzzle.a - gameEngine.viewpointXSnapped,
                            startY = muzzle.b - unit.posZ - muzzle.c - gameEngine.viewpointYSnapped,
                            endX = target.posX - gameEngine.viewpointXSnapped,
                            endY = target.posY - target.posZ - gameEngine.viewpointYSnapped,
                            ageTicks = age.coerceIn(0f, turret.preFireDuration),
                            remainingTicks = (turret.preFireDuration - age).coerceAtLeast(0f),
                            durationTicks = turret.preFireDuration,
                            variantId = RenderVariantId(variantId),
                        ),
                        activeCanvas,
                    )
                }
            }
        } finally {
            canvas?.finish()
        }
    }

    @JvmStatic
    fun drawProjectile(
        projectile: Projectile,
        projectileTemplate: ProjectileTemplate,
        gameEngine: GameEngine,
        targetX: Float,
        targetY: Float,
        targetHeight: Float,
    ): Boolean {
        val custom = projectileTemplate as? CustomProjectileTemplate ?: return false
        val rendererId = custom.renderExtensionId ?: return false
        val variantId = custom.renderExtensionVariant ?: return false
        val registration = synchronized(lock) { projectileRenderers[rendererId] } ?: return false
        val startX = projectile.posX - gameEngine.viewpointXSnapped
        val startY = projectile.posY - gameEngine.viewpointYSnapped - projectile.posZ
        val endX = targetX - gameEngine.viewpointXSnapped + projectile.K
        val endY = targetY - targetHeight - gameEngine.viewpointYSnapped + projectile.L
        val canvas = EngineRenderCanvas(gameEngine.renderGraphicsEngine)
        try {
            renderSafely("projectile:$rendererId") {
                registration.renderer.render(
                    ProjectileRenderContext(
                        startX = startX,
                        startY = startY,
                        endX = endX,
                        endY = endY,
                        ageTicks = projectile.J.coerceAtLeast(0f),
                        remainingTicks = projectile.h.coerceAtLeast(0f),
                        drawSize = projectileTemplate.drawSize,
                        variantId = RenderVariantId(variantId),
                    ),
                    canvas,
                )
            }
        } finally {
            canvas.finish()
        }
        return true
    }

    @JvmStatic
    fun drawEffect(effect: Effect, gameEngine: GameEngine, shadowPass: Boolean): Boolean {
        val rendererId = effect.a.renderExtensionId ?: return false
        val variantId = effect.a.renderExtensionVariant ?: return false
        val registration = synchronized(lock) { effectRenderers[rendererId] } ?: return false

        var worldX = effect.I
        var worldY = effect.J
        var height = effect.K
        effect.b?.let { source ->
            worldX += source.posX
            worldY += source.posY
            height += source.posZ
        }
        if (!shadowPass && !effect.e && !effect.f) {
            if (!gameEngine.tileMap.isWorldPointVisibleForTeam(worldX, worldY, gameEngine.playerTeam)) return true
            effect.f = true
        }
        val age = (effect.W - effect.V).coerceIn(0f, effect.W.coerceAtLeast(0f))
        val progress = if (effect.W <= 0f) 1f else age / effect.W
        val scaleX = if (effect.scaleXFrom.isNaN()) {
            effect.G + (effect.F - effect.G) * progress
        } else {
            effect.scaleXFrom + (effect.scaleXTo - effect.scaleXFrom) * progress
        }
        val scaleY = if (effect.scaleYFrom.isNaN()) {
            effect.G + (effect.F - effect.G) * progress
        } else {
            effect.scaleYFrom + (effect.scaleYTo - effect.scaleYFrom) * progress
        }
        val canvas = EngineRenderCanvas(gameEngine.renderGraphicsEngine)
        val sourceSpeedFraction = (effect.b as? OrderableUnit)?.let { source ->
            planarSpeedFraction(source.velocityX, source.velocityY, source.getMoveSpeed())
        }
        try {
            renderSafely("effect:$rendererId") {
                registration.renderer.render(
                    EffectRenderContext(
                        originX = worldX - gameEngine.viewpointXSnapped,
                        originY = worldY - (if (shadowPass) 0f else height) - gameEngine.viewpointYSnapped,
                        ageTicks = age,
                        lifetimeTicks = effect.W,
                        rotationDegrees = effect.Y,
                        alpha = effect.E,
                        scaleX = scaleX,
                        scaleY = scaleY,
                        shadowPass = shadowPass,
                        variantId = RenderVariantId(variantId),
                        sourceSpeedFraction = sourceSpeedFraction,
                    ),
                    canvas,
                )
            }
        } finally {
            canvas.finish()
        }
        return true
    }

    private inline fun renderSafely(key: String, render: () -> Unit) {
        try {
            render()
        } catch (error: Throwable) {
            if (synchronized(lock) { reportedFailures.add(key) }) {
                logger.error(error) { "Mod renderer failed: $key" }
            }
        }
    }

    private fun resolveTexture(id: TextureId): Pair<Texture, TextureOptions> {
        val registration = synchronized(lock) {
            checkNotNull(textures[id.value]) { "Texture is not registered: ${id.value}" }
        }
        val texture = registration.texture ?: synchronized(registration) {
            registration.texture ?: registration.owner.loadRenderTexture(registration.path, registration.options)
                .also { registration.texture = it }
        }
        return texture to registration.options
    }

    private class EngineRenderCanvas(private val graphics: GraphicsEngine) : RenderCanvas {
        private val source = Rect()
        private val destination = RectF()
        private val paint = KoolPaint()
        private var saveDepth = 0

        override fun save() {
            graphics.k()
            saveDepth++
        }

        override fun restore() {
            check(saveDepth > 0) { "RenderCanvas.restore() called without save()" }
            graphics.l()
            saveDepth--
        }

        override fun rotate(degrees: Float, pivotX: Float, pivotY: Float) {
            graphics.a(degrees, pivotX, pivotY)
        }

        override fun scale(scaleX: Float, scaleY: Float, pivotX: Float, pivotY: Float) {
            require(scaleX.isFinite() && scaleY.isFinite()) { "Canvas scale must FastArrayList finite" }
            graphics.a(scaleX, scaleY, pivotX, pivotY)
        }

        override fun drawTexture(
            textureId: TextureId,
            centerX: Float,
            centerY: Float,
            width: Float,
            height: Float,
            tint: RgbaColor,
            opacity: Float,
            blendMode: RenderBlendMode,
        ) {
            require(width > 0f && height > 0f) { "Texture dimensions must FastArrayList positive: $textureId" }
            val (texture, options) = resolveTexture(textureId)
            paint.a(options.filter == TextureFilter.LINEAR)
            paint.a(
                (tint.alpha * opacity.coerceIn(0f, 1f)).roundToInt(),
                tint.red,
                tint.green,
                tint.blue,
            )
            paint.a(
                when (blendMode) {
                    RenderBlendMode.ALPHA -> KoolCanvasBlendMode.SourceOver
                    RenderBlendMode.ADDITIVE -> KoolCanvasBlendMode.Add
                }
            )
            source.a(0, 0, texture.width(), texture.height())
            destination.a(
                centerX - width * 0.5f,
                centerY - height * 0.5f,
                centerX + width * 0.5f,
                centerY + height * 0.5f,
            )
            graphics.a(texture, source, destination, paint)
        }

        fun finish() {
            while (saveDepth > 0) {
                graphics.l()
                saveDepth--
            }
        }
    }
}

internal fun planarSpeedFraction(velocityX: Float, velocityY: Float, maximumSpeed: Float): Float =
    if (maximumSpeed > 0f) sqrt(velocityX * velocityX + velocityY * velocityY) / maximumSpeed else 0f
