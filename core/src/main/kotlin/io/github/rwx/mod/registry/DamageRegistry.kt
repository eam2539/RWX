package io.github.rwx.mod.registry

import com.corrodinggames.rts.game.Projectile
import com.corrodinggames.rts.game.ProjectileTemplate
import com.corrodinggames.rts.game.units.BaseUnit
import com.corrodinggames.rts.game.units.custom.AnimationSet
import com.corrodinggames.rts.game.units.custom.AnimationTag
import com.corrodinggames.rts.game.units.custom.CustomUnit
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig
import com.corrodinggames.rts.gameFramework.Utility
import io.github.rwx.mod.impl.ApiImpl
import java.util.*


object DamageRegistry : OwnedRegistry {
    /** Damage extensions bound to one native projectile template. */
    data class ProjectileDamage(
        val directDamageAmount: Float? = null,
        val areaDamageAmount: Float? = null,
        val directDamageHitRateBonus: Float? = null,
        val areaDamageHitRateBonus: Float? = null,
        val areaDamageExcludeDirectHit: Boolean = false,
        val directDamageArmourIgnoreAmount: Float? = null,
        val areaDamageArmourIgnoreAmount: Float? = null,
        val rayDamage: Boolean = false,
        val rayDamageRange: Float = 0f,
        val rayDamageWidth: Float = 0f,
        val rayDamageTargetWidthFactor: Float = 0f,
        val rayDamageHitEffectOffsetFactor: Float? = null,
        val rayDamageSecondaryTargetTags: AnimationSet? = null,
    )

    /**
     * The rectangle a unit exposes to beams, in world units, relative to its position.
     *
     * A zero-sized box is meaningful, not missing data: it means the unit is only hit by a
     * beam passing exactly through its centre. Units with no bound rectangle fall back to
     * their collision radius.
     */
    data class UnitExposure(
        val offsetX: Float = 0f,
        val offsetY: Float = 0f,
        val width: Float,
        val height: Float,
    )

    /** Damage extensions bound to one native unit config. */
    data class UnitDamage(
        val avoidChance: Float = 0f,
        val exposure: UnitExposure? = null,
    )

    private val lock = Any()
    private val projectiles = RegistrationTable<ProjectileTemplate, ProjectileDamage>(
        label = "Projectile damage",
        lock = lock,
        entries = IdentityHashMap(),
    )
    private val units = RegistrationTable<CustomUnitConfig, UnitDamage>(
        label = "Unit damage",
        lock = lock,
        entries = IdentityHashMap(),
    )

    fun bindProjectile(owner: ApiImpl, template: ProjectileTemplate, damage: ProjectileDamage) {
        damage.directDamageHitRateBonus?.let {
            require(it.isFinite()) { "directDamageHitRateBonus must be finite" }
        }
        damage.areaDamageHitRateBonus?.let {
            require(it.isFinite()) { "areaDamageHitRateBonus must be finite" }
        }
        if (damage.rayDamage) {
            require(damage.rayDamageRange > 0f) { "rayDamageRange must be greater than zero" }
            require(damage.rayDamageWidth >= 0f && damage.rayDamageTargetWidthFactor >= 0f) {
                "ray damage width values cannot be negative"
            }
            damage.rayDamageHitEffectOffsetFactor?.let {
                require(it >= 0f) { "rayDamageHitEffectOffsetFactor cannot be negative" }
            }
        }
        projectiles.put(owner, template, damage)
    }

    fun bindUnit(owner: ApiImpl, config: CustomUnitConfig, damage: UnitDamage) {
        require(damage.avoidChance in 0f..1f) { "damageAvoidChance must be between 0 and 1" }
        damage.exposure?.let { exposure ->
            require(exposure.width >= 0f && exposure.height >= 0f) {
                "exposureWidth and exposureHeight cannot be negative"
            }
        }
        units.put(owner, config, damage)
    }

    override fun unregister(owner: ApiImpl) {
        synchronized(lock) {
            projectiles.removeOwned(owner)
            units.removeOwned(owner)
        }
    }

    private fun damageOf(template: ProjectileTemplate?): ProjectileDamage? =
        template?.let { projectiles[it] }

    private fun damageOf(unit: BaseUnit): UnitDamage? =
        (unit as? CustomUnit)?.unitConfig?.let { units[it] }

    /** Half-extents of [unit]'s exposure box, falling back to its collision radius. */
    private fun exposureBox(unit: BaseUnit): UnitExposure =
        damageOf(unit)?.exposure
            ?: UnitExposure(width = unit.radius * 2f, height = unit.radius * 2f)

    // ---------------------------------------------------------------- damage amounts

    /**
     * Direct damage for [template], preserving fractions the native `int` field would truncate.
     * Returns [fallback] (the native value) when no mod bound a fractional amount.
     */
    @JvmStatic
    fun directDamageAmount(template: ProjectileTemplate, fallback: Int): Float =
        damageOf(template)?.directDamageAmount ?: fallback.toFloat()

    /** Area damage for [template]; see [directDamageAmount]. */
    @JvmStatic
    fun areaDamageAmount(template: ProjectileTemplate, fallback: Int): Float =
        damageOf(template)?.areaDamageAmount ?: fallback.toFloat()

    /**
     * Armour a hit ignores, letting direct and area damage pierce independently.
     * Returns [fallback] (the projectile's native `armourIgnoreAmount`) when unbound.
     */
    @JvmStatic
    fun armourIgnoreAmount(projectile: Projectile?, areaDamage: Boolean, fallback: Float): Float {
        val damage = damageOf(projectile?.g) ?: return fallback
        val override = if (areaDamage) {
            damage.areaDamageArmourIgnoreAmount
        } else {
            damage.directDamageArmourIgnoreAmount
        }
        return override ?: fallback
    }

    /**
     * True when area damage must skip [target] because it already took this projectile's
     * direct hit, so a beam or proximity blast cannot damage its primary target twice.
     */
    @JvmStatic
    fun excludesAreaDamage(projectile: Projectile, target: BaseUnit): Boolean {
        val damage = damageOf(projectile.g) ?: return false
        return damage.areaDamageExcludeDirectHit && target === projectile.l
    }

    // ---------------------------------------------------------------- hit-rate rolls

    /**
     * Applies the target's dodge chance before [damage] reaches shields or health, returning
     * either the full damage or zero. Only projectiles that bound a hit-rate bonus can be
     * dodged, so weapons meant to always connect are unaffected.
     *
     * The roll is deterministic in the target's own random stream, and advances that stream by
     * bumping `unitFlags4` exactly as the engine's own rolls do — otherwise every dodge check
     * against a stationary unit on the same tick would return the same value.
     *
     * The decision must not depend on anything a peer can disagree about. In particular it is
     * taken before the caller's local-only invincibility toggle can zero the damage, so a
     * player with invincibility on still consumes the same rolls as everyone else.
     */
    @JvmStatic
    fun applyHitRate(
        target: BaseUnit?,
        damage: Float,
        projectile: Projectile?,
        areaDamage: Boolean,
    ): Float {
        if (target == null || projectile == null) return damage
        val extension = damageOf(projectile.g) ?: return damage
        val hitRateBonus = (
                if (areaDamage) extension.areaDamageHitRateBonus else extension.directDamageHitRateBonus
                ) ?: return damage
        val avoidChance = damageOf(target)?.avoidChance ?: 0f
        if (avoidChance <= 0f) return damage
        val roll = Utility.getDeterministicRandomFloat(target, 0f, 1f, AVOID_ROLL_SALT)
        target.unitFlags4++
        return if (roll < avoidChance - hitRateBonus) 0f else damage
    }

    // ---------------------------------------------------------------- ray damage

    /** True when [template] damages every enemy along its path rather than only its target. */
    @JvmStatic
    fun isRayDamage(template: ProjectileTemplate?): Boolean = damageOf(template)?.rayDamage == true

    /**
     * Where a beam's hit effect belongs on [target]: the point the beam crosses its exposure
     * box, pushed [ProjectileDamage.rayDamageHitEffectOffsetFactor] of the box width further
     * along so the effect sits on the surface rather than the edge.
     *
     * Returns `null` when the beam misses the box or the mod bound no offset, meaning the
     * caller should leave the impact point alone.
     */
    private fun hitEffectDistance(
        damage: ProjectileDamage,
        projectile: Projectile,
        target: BaseUnit,
        directionX: Float,
        directionY: Float,
    ): Float? {
        val offsetFactor = damage.rayDamageHitEffectOffsetFactor ?: return null
        val box = exposureBox(target)
        val distance = RayDamageGeometry.firstIntersectionDistance(
            projectile.posX,
            projectile.posY,
            directionX,
            directionY,
            target.posX + box.offsetX,
            target.posY + box.offsetY,
            box.width,
            box.height,
        )
        if (distance.isNaN()) return null
        return distance + box.width * offsetFactor
    }

    /**
     * The impact point to show for a beam's primary [target], or `null` to keep the engine's
     * own point. Returned rather than assigned because the engine's impact fields are
     * package-private and also drive area damage and save state.
     */
    @JvmStatic
    fun rayDamageImpactPoint(
        projectile: Projectile,
        target: BaseUnit?,
        directionX: Float,
        directionY: Float,
    ): FloatArray? {
        target ?: return null
        val damage = damageOf(projectile.g)?.takeIf { it.rayDamage } ?: return null
        val distance = hitEffectDistance(damage, projectile, target, directionX, directionY)
            ?: return null
        val length = length(directionX, directionY) ?: return null
        return floatArrayOf(
            projectile.posX + (directionX / length) * distance,
            projectile.posY + (directionY / length) * distance,
            target.posZ,
        )
    }

    /**
     * True when the mod bound a hit-effect offset but the beam misses [target]'s exposure box,
     * so the engine should suppress the primary impact effect entirely.
     */
    @JvmStatic
    fun suppressesPrimaryHitEffect(
        projectile: Projectile,
        target: BaseUnit?,
        directionX: Float,
        directionY: Float,
    ): Boolean {
        target ?: return false
        val damage = damageOf(projectile.g)?.takeIf { it.rayDamage } ?: return false
        if (damage.rayDamageHitEffectOffsetFactor == null) return false
        return hitEffectDistance(damage, projectile, target, directionX, directionY) == null
    }

    /** Applies this projectile's damage to one secondary beam target. */
    fun interface SecondaryDamage {
        fun apply(target: BaseUnit)
    }

    /** Shows this projectile's hit effect on a secondary beam target, at a world point. */
    fun interface SecondaryHitEffect {
        fun emit(target: BaseUnit, x: Float, y: Float, z: Float)
    }

    /**
     * Damages every enemy the beam passes through besides its primary target, in unit-list
     * order so all peers resolve the same hits in the same sequence.
     *
     * Damage is applied through [applyDamage] — the engine's own damage entry point — so
     * secondary hits share the primary path's armour, shield and event handling.
     */
    @JvmStatic
    fun applyRayDamageToSecondaryTargets(
        projectile: Projectile,
        directionX: Float,
        directionY: Float,
        applyDamage: SecondaryDamage,
        emitHitEffect: SecondaryHitEffect,
    ) {
        val damage = damageOf(projectile.g)?.takeIf { it.rayDamage } ?: return
        val source = projectile.j ?: return
        if (projectile.l == null) return
        val length = length(directionX, directionY) ?: return

        val units = BaseUnit.bE.a()
        val size = BaseUnit.bE.size
        for (index in 0 until size) {
            val target = units[index] ?: continue
            if (!isEligibleSecondaryTarget(damage, projectile, source, target)) continue

            val box = exposureBox(target)
            val centerX = target.posX + box.offsetX
            val centerY = target.posY + box.offsetY
            if (!RayDamageGeometry.isExposureWithinRange(
                    source.posX,
                    source.posY,
                    centerX,
                    centerY,
                    box.width,
                    box.height,
                    damage.rayDamageRange,
                )
            ) {
                continue
            }
            if (!RayDamageGeometry.intersectsRayOrWidth(
                    projectile.posX,
                    projectile.posY,
                    directionX,
                    directionY,
                    centerX,
                    centerY,
                    box.width,
                    box.height,
                    damage.rayDamageWidth,
                    damage.rayDamageTargetWidthFactor,
                )
            ) {
                continue
            }
            applyDamage.apply(target)
            hitEffectDistance(damage, projectile, target, directionX, directionY)?.let { distance ->
                emitHitEffect.emit(
                    target,
                    projectile.posX + (directionX / length) * distance,
                    projectile.posY + (directionY / length) * distance,
                    target.posZ,
                )
            }
        }
    }

    /**
     * Secondary beam hits reach live enemy units the mod opted in by tag. Untagged units are
     * eligible only when the mod bound no tag filter at all.
     */
    private fun isEligibleSecondaryTarget(
        damage: ProjectileDamage,
        projectile: Projectile,
        source: BaseUnit,
        target: BaseUnit,
    ): Boolean {
        if (target === projectile.l || target === source) return false
        if (target.isDead || !target.isAlive || target.unitTransportTarget != null) return false
        val sourceTeam = source.team ?: return false
        val targetTeam = target.team ?: return false
        if (!sourceTeam.c(targetTeam)) return false
        val tags = damage.rayDamageSecondaryTargetTags ?: return true
        return AnimationTag.a(tags, target.tags)
    }

    private fun length(directionX: Float, directionY: Float): Float? {
        val length = StrictMath.sqrt(
            (directionX * directionX + directionY * directionY).toDouble(),
        ).toFloat()
        return length.takeIf { it > DIRECTION_EPSILON }
    }

    /**
     * Salt for the dodge roll, distinct from other per-unit deterministic rolls so a dodge
     * check cannot correlate with another system reading the same unit's stream.
     */
    private const val AVOID_ROLL_SALT = 47
    private const val DIRECTION_EPSILON = 0.000001f
}

/**
 * Ray-versus-rectangle tests for beam weapons.
 *
 * Beams are checked against each unit's axis-aligned exposure box rather than its collision
 * circle, so long thin ships are hit along their length instead of within a radius.
 */
internal object RayDamageGeometry {
    private const val DIRECTION_EPSILON = 0.000001f

    /**
     * True when [range] reaches the exposure box, measured to the box's nearest edge rather
     * than its centre, so a large target is in range as soon as any part of it is.
     */
    @JvmStatic
    fun isExposureWithinRange(
        sourceX: Float,
        sourceY: Float,
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
        range: Float,
    ): Boolean {
        val halfWidth = width * 0.5f
        val halfHeight = height * 0.5f
        val dx = axisDistance(sourceX, centerX - halfWidth, centerX + halfWidth)
        val dy = axisDistance(sourceY, centerY - halfHeight, centerY + halfHeight)
        return (dx * dx) + (dy * dy) < range * range
    }

    /**
     * True when the beam hits the exposure box, either by crossing it or by passing within the
     * weapon's width of its centre.
     *
     * The width test compares a squared perpendicular distance against
     * `rayWidth² + width * targetWidthFactor`, whose second term is a plain length rather than
     * a squared one. That mismatch is deliberate and reproduces the original game's formula
     * exactly; callers compensate through [targetWidthFactor], which carries the world-unit
     * scale (16 for a rectangle authored in the source game's units). Making the units
     * consistent here would silently change every beam's effective width.
     *
     * Like the original, this ignores whether the target lies ahead of or behind the beam
     * origin — the caller has already limited candidates by range.
     */
    @JvmStatic
    fun intersectsRayOrWidth(
        originX: Float,
        originY: Float,
        directionX: Float,
        directionY: Float,
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
        rayWidth: Float,
        targetWidthFactor: Float,
    ): Boolean {
        val length = StrictMath.sqrt(
            (directionX * directionX + directionY * directionY).toDouble(),
        ).toFloat()
        if (length <= DIRECTION_EPSILON) return false
        val normalizedX = directionX / length
        val normalizedY = directionY / length
        if (!firstIntersectionDistance(
                originX,
                originY,
                normalizedX,
                normalizedY,
                centerX,
                centerY,
                width,
                height,
            ).isNaN()
        ) {
            return true
        }
        if (rayWidth <= 0f && targetWidthFactor <= 0f) return false
        val cross = (normalizedX * (centerY - originY)) - (normalizedY * (centerX - originX))
        return cross * cross <= (rayWidth * rayWidth) + (width * targetWidthFactor)
    }

    /**
     * Distance from the beam origin to where it first enters the exposure box, `0` when the
     * origin is already inside it, or `NaN` when the beam misses.
     *
     * A zero-sized box stays a point rather than falling back to a radius: that is the source
     * game's runtime behaviour for units that never had an exposure rectangle authored.
     */
    @JvmStatic
    fun firstIntersectionDistance(
        originX: Float,
        originY: Float,
        directionX: Float,
        directionY: Float,
        centerX: Float,
        centerY: Float,
        width: Float,
        height: Float,
    ): Float {
        val length = StrictMath.sqrt(
            (directionX * directionX + directionY * directionY).toDouble(),
        ).toFloat()
        if (length <= DIRECTION_EPSILON) return Float.NaN
        val dirX = directionX / length
        val dirY = directionY / length
        val halfWidth = width * 0.5f
        val halfHeight = height * 0.5f
        val minX = centerX - halfWidth
        val maxX = centerX + halfWidth
        val minY = centerY - halfHeight
        val maxY = centerY + halfHeight
        var near = Float.NEGATIVE_INFINITY
        var far = Float.POSITIVE_INFINITY

        // Slab test: clip the ray against each axis' pair of edges in turn.
        if (StrictMath.abs(dirX) <= DIRECTION_EPSILON) {
            if (originX !in minX..maxX) return Float.NaN
        } else {
            val first = (minX - originX) / dirX
            val second = (maxX - originX) / dirX
            near = maxOf(near, minOf(first, second))
            far = minOf(far, maxOf(first, second))
        }
        if (StrictMath.abs(dirY) <= DIRECTION_EPSILON) {
            if (originY !in minY..maxY) return Float.NaN
        } else {
            val first = (minY - originY) / dirY
            val second = (maxY - originY) / dirY
            near = maxOf(near, minOf(first, second))
            far = minOf(far, maxOf(first, second))
        }
        if (far < maxOf(near, 0f)) return Float.NaN
        return maxOf(near, 0f)
    }

    /** Distance from [point] to the `[minimum], [maximum]` interval, or `0` when inside it. */
    private fun axisDistance(point: Float, minimum: Float, maximum: Float): Float = when {
        point < minimum -> minimum - point
        point > maximum -> point - maximum
        else -> 0f
    }
}