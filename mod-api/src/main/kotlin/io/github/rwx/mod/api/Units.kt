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

data class UnitExtension(
    val shaderId: String? = null,
    val renderBinding: UnitRenderBinding? = null,
    val projectiles: Map<String, ProjectileExtension> = emptyMap(),
    val turrets: Map<String, TurretExtension> = emptyMap(),
    val effects: Map<String, NativeEffectExtension> = emptyMap(),
)

data class ProjectileExtension(
    val renderBinding: ProjectileRenderBinding? = null,
    val observerBinding: ProjectileObserverBinding? = null,
)

data class TurretExtension(
    val preFireDuration: Ticks = Ticks.ZERO,
    val renderBinding: PreFireRenderBinding? = null,
    val observerBinding: PreFireObserverBinding? = null,
)

data class NativeEffectExtension(
    val renderBinding: EffectRenderBinding,
)

private fun requireContentId(value: String, kind: String) {
    require(value.matches(CONTENT_ID_PATTERN)) { "Invalid namespace-qualified $kind id: $value" }
}

private fun requireLocalId(value: String, kind: String) {
    require(value.matches(LOCAL_ID_PATTERN)) { "Invalid $kind id: $value" }
}

private val CONTENT_ID_PATTERN = Regex("[a-z][a-z0-9_.-]*:[a-z0-9][a-z0-9_./-]*")
private val LOCAL_ID_PATTERN = Regex("[A-Za-z0-9][A-Za-z0-9_.:/#-]*")
private val ACTION_ID_PATTERN = Regex("[A-Za-z0-9][A-Za-z0-9_.-]*")
