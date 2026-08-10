package io.github.rwx.mod.api

import kotlin.collections.Map

/**
 * Registers deterministic unit and weapon declarations during [io.github.rwx.mod.Mod.init].
 * Durations use simulation ticks and distances use RWX world units.
 */
interface Units {
    fun registerUnit(definition: UnitDefinition)
}

@JvmInline
value class UnitId(val value: String) {
    init {
        requireContentId(value, "Unit")
    }
}

@JvmInline
value class WeaponId(val value: String) {
    init {
        requireContentId(value, "Weapon")
    }
}

@JvmInline
value class UnitActionId(val value: String) {
    init {
        require(value.matches(ACTION_ID_PATTERN)) { "Invalid unit action id: $value" }
    }
}

@JvmInline
value class Tag(val value: String) {
    init {
        requireLocalId(value, "Tag")
    }
}

@JvmInline
value class EffectId(val value: String) {
    init {
        requireContentId(value, "Effect")
    }
}

@JvmInline
value class Ticks(val value: Int) {
    init {
        require(value >= 0) { "Ticks cannot be negative" }
    }

    companion object {
        val ZERO = Ticks(0)
    }
}

val Int.ticks: Ticks get() = Ticks(this)

data class UnitDefinition(
    val id: UnitId,
    val extension: UnitExtension = UnitExtension(),
    val eventHandlers: List<UnitEventBinding> = emptyList(),
    val iniSections: List<IniSpecSection> = emptyList(),
)

@UnitDsl
data class UnitExtension(
    var shaderId: String? = null,
    var renderBinding: UnitRenderBinding? = null,
    var damageAvoidChance: Float? = null,
    var exposureOffsetX: Float? = null,
    var exposureOffsetY: Float? = null,
    var exposureWidth: Float? = null,
    var exposureHeight: Float? = null,
    var projectiles: Map<String, ProjectileExtension> = linkedMapOf(),
    var turrets: Map<String, TurretExtension> = linkedMapOf(),
    var effects: Map<String, EffectExtension> = linkedMapOf(),
) {
    fun projectile(name: String, configure: ProjectileExtension.() -> kotlin.Unit) {
        projectiles.asMutable().getOrPut(requireExtensionSectionName(name), ::ProjectileExtension).configure()
    }

    fun turret(name: String, configure: TurretExtension.() -> kotlin.Unit) {
        turrets.asMutable().getOrPut(requireExtensionSectionName(name), ::TurretExtension).configure()
    }

    fun effect(name: String, configure: EffectExtension.() -> kotlin.Unit) {
        effects.asMutable().getOrPut(requireExtensionSectionName(name), ::EffectExtension).configure()
    }
}

@UnitDsl
data class ProjectileExtension(
    var renderBinding: ProjectileRenderBinding? = null,
    var observerBinding: ProjectileObserverBinding? = null,
    var directDamageAmount: Float? = null,
    var areaDamageAmount: Float? = null,
    var directDamageHitRateBonus: Float? = null,
    var areaDamageHitRateBonus: Float? = null,
    var areaDamageExcludeDirectHit: Boolean? = null,
    var rayDamage: Boolean? = null,
    var rayDamageRange: Float? = null,
    var rayDamageWidth: Float? = null,
    var rayDamageTargetWidthFactor: Float? = null,
    var rayDamageHitEffectOffsetFactor: Float? = null,
    var rayDamageSecondaryTargetTags: List<String>? = null,
    var directDamageArmourIgnoreAmount: Float? = null,
    var areaDamageArmourIgnoreAmount: Float? = null,
)

@UnitDsl
data class TurretExtension(
    var preFireDuration: Ticks = Ticks.ZERO,
    var renderBinding: PreFireRenderBinding? = null,
    var observerBinding: PreFireObserverBinding? = null,
)

@UnitDsl
data class EffectExtension(
    var renderBinding: EffectRenderBinding? = null,
)

private fun requireExtensionSectionName(name: String): String {
    require(name.matches(Regex("[A-Za-z0-9_.-]+"))) { "Invalid native INI section name: $name" }
    return name
}

private fun <V> Map<String, V>.asMutable(): MutableMap<String, V> =
    this as? MutableMap<String, V> ?: toMutableMap()

private fun requireContentId(value: String, kind: String) {
    require(value.matches(CONTENT_ID_PATTERN)) { "Invalid namespace-qualified $kind id: $value" }
}

private fun requireLocalId(value: String, kind: String) {
    require(value.matches(LOCAL_ID_PATTERN)) { "Invalid $kind id: $value" }
}

private val CONTENT_ID_PATTERN = Regex("[a-z][a-z0-9_.-]*:[a-z0-9][a-z0-9_./-]*")
private val LOCAL_ID_PATTERN = Regex("[A-Za-z0-9][A-Za-z0-9_.:/#-]*")
private val ACTION_ID_PATTERN = Regex("[A-Za-z0-9][A-Za-z0-9_.-]*")
