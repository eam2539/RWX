package io.github.rwx.mod.api

import io.github.rwx.mod.api.specs.*

@DslMarker
annotation class UnitDsl

class UnitTemplate internal constructor(
    internal val configure: UnitBuilder.() -> kotlin.Unit,
)

fun unitTemplate(configure: UnitBuilder.() -> kotlin.Unit): UnitTemplate = UnitTemplate(configure)

fun Units.unit(
    id: UnitId,
    vararg templates: UnitTemplate,
    configure: UnitBuilder.() -> kotlin.Unit,
) {
    val builder = UnitBuilder(id)
    templates.forEach { template -> builder.apply(template.configure) }
    builder.configure()
    registerUnit(builder.buildDefinition())
}

@UnitDsl
class UnitBuilder internal constructor(private val id: UnitId) {
    private val extension = UnitExtensionBuilder()
    private val eventHandlers = linkedMapOf<UnitEventBindingId, UnitEventBinding>()
    private val ini = UnitIniBuilder()

    fun extension(configure: UnitExtensionBuilder.() -> kotlin.Unit) {
        extension.configure()
    }

    fun core(configure: CoreSpec.() -> kotlin.Unit) = ini.core(configure)
    fun graphics(configure: GraphicsSpec.() -> kotlin.Unit) = ini.graphics(configure)
    fun movement(configure: MovementSpec.() -> kotlin.Unit) = ini.movement(configure)
    fun attack(configure: AttackSpec.() -> kotlin.Unit) = ini.attack(configure)
    fun ai(configure: AiSpec.() -> kotlin.Unit) = ini.ai(configure)
    fun resource(name: String, configure: ResourceSpec.() -> kotlin.Unit) = ini.resource(name, configure)
    fun globalResource(name: String, configure: GlobalResourceSpec.() -> kotlin.Unit) =
        ini.globalResource(name, configure)

    fun action(name: String, configure: ActionSpec.() -> kotlin.Unit) = ini.action(name, configure)
    fun hiddenAction(name: String, configure: HiddenActionSpec.() -> kotlin.Unit) =
        ini.hiddenAction(name, configure)

    fun turret(name: String, configure: TurretSpec.() -> kotlin.Unit) = ini.turret(name, configure)
    fun projectile(name: String, configure: ProjectileSpec.() -> kotlin.Unit) = ini.projectile(name, configure)
    fun effect(name: String, configure: EffectSpec.() -> kotlin.Unit) = ini.effect(name, configure)
    fun effect(name: String, spec: EffectSpec) = ini.effect(name, spec)
    fun decal(name: String, configure: DecalSpec.() -> kotlin.Unit) = ini.decal(name, configure)
    fun attachment(name: String, configure: AttachmentSpec.() -> kotlin.Unit) = ini.attachment(name, configure)
    fun leg(name: String, configure: LegSpec.() -> kotlin.Unit) = ini.leg(name, configure)
    fun arm(name: String, configure: ArmSpec.() -> kotlin.Unit) = ini.arm(name, configure)
    fun placementRule(name: String, configure: PlacementRuleSpec.() -> kotlin.Unit) =
        ini.placementRule(name, configure)

    fun animation(name: String, configure: AnimationSpec.() -> kotlin.Unit) = ini.animation(name, configure)
    fun canBuild(name: String, configure: CanBuildSpec.() -> kotlin.Unit) = ini.canBuild(name, configure)
    fun template(name: String, configure: TemplateSpec.() -> kotlin.Unit) = ini.template(name, configure)

    fun on(
        id: UnitEventBindingId,
        event: UnitEvent,
        condition: UnitCondition = UnitCondition.Always,
        recursionLimit: Int = 1,
        configure: UnitEventActionBuilder.() -> kotlin.Unit,
    ) {
        val actions = UnitEventActionBuilder().apply(configure).build()
        eventHandlers[id] = UnitEventBinding(
            id = id,
            event = event,
            condition = condition,
            recursionLimit = recursionLimit,
            actions = actions,
        )
    }

    fun listen(
        id: UnitEventBindingId,
        event: UnitEvent,
        condition: UnitCondition = UnitCondition.Always,
        recursionLimit: Int = 1,
        handler: UnitEventHandler,
    ) {
        eventHandlers[id] = UnitEventBinding(
            id = id,
            event = event,
            condition = condition,
            recursionLimit = recursionLimit,
            listener = handler,
        )
    }

    fun removeEventHandler(id: UnitEventBindingId) {
        eventHandlers.remove(id)
    }

    internal fun buildDefinition(): UnitDefinition = UnitDefinition(
        id = id,
        extension = extension.buildDefinition(),
        eventHandlers = eventHandlers.values.toList(),
        iniSections = ini.build(),
    )
}

@UnitDsl
class UnitExtensionBuilder internal constructor() {
    var shaderId: String? = null
    var renderBinding: UnitRenderBinding? = null
    var damageAvoidChance: Float? = null
    var exposureOffsetX: Float? = null
    var exposureOffsetY: Float? = null
    var exposureWidth: Float? = null
    var exposureHeight: Float? = null
    private val projectiles = linkedMapOf<String, ProjectileExtensionBuilder>()
    private val turrets = linkedMapOf<String, TurretExtensionBuilder>()
    private val effects = linkedMapOf<String, EffectExtensionBuilder>()

    fun projectile(name: String, configure: ProjectileExtensionBuilder.() -> kotlin.Unit) {
        projectiles.getOrPut(requireExtensionSectionName(name), ::ProjectileExtensionBuilder).configure()
    }

    fun turret(name: String, configure: TurretExtensionBuilder.() -> kotlin.Unit) {
        turrets.getOrPut(requireExtensionSectionName(name), ::TurretExtensionBuilder).configure()
    }

    fun effect(name: String, configure: EffectExtensionBuilder.() -> kotlin.Unit) {
        effects.getOrPut(requireExtensionSectionName(name), ::EffectExtensionBuilder).configure()
    }

    internal fun buildDefinition() = UnitExtension(
        shaderId = shaderId,
        renderBinding = renderBinding,
        damageAvoidChance = damageAvoidChance,
        exposureOffsetX = exposureOffsetX,
        exposureOffsetY = exposureOffsetY,
        exposureWidth = exposureWidth,
        exposureHeight = exposureHeight,
        projectiles = projectiles.mapValues { it.value.buildDefinition() },
        turrets = turrets.mapValues { it.value.buildDefinition() },
        effects = effects.mapValues { it.value.buildDefinition() },
    )
}

@UnitDsl
class ProjectileExtensionBuilder internal constructor() {
    var renderBinding: ProjectileRenderBinding? = null
    var observerBinding: ProjectileObserverBinding? = null
    var directDamageAmount: Float? = null
    var areaDamageAmount: Float? = null
    var directDamageHitRateBonus: Float? = null
    var areaDamageHitRateBonus: Float? = null
    var areaDamageExcludeDirectHit: Boolean? = null
    var rayDamage: Boolean? = null
    var rayDamageRange: Float? = null
    var rayDamageWidth: Float? = null
    var rayDamageTargetWidthFactor: Float? = null
    var rayDamageHitEffectOffsetFactor: Float? = null
    var rayDamageSecondaryTargetTags: List<String>? = null
    var directDamageArmourIgnoreAmount: Float? = null
    var areaDamageArmourIgnoreAmount: Float? = null

    internal fun buildDefinition() = ProjectileExtension(
        renderBinding = renderBinding,
        observerBinding = observerBinding,
        directDamageAmount = directDamageAmount,
        areaDamageAmount = areaDamageAmount,
        directDamageHitRateBonus = directDamageHitRateBonus,
        areaDamageHitRateBonus = areaDamageHitRateBonus,
        areaDamageExcludeDirectHit = areaDamageExcludeDirectHit,
        rayDamage = rayDamage,
        rayDamageRange = rayDamageRange,
        rayDamageWidth = rayDamageWidth,
        rayDamageTargetWidthFactor = rayDamageTargetWidthFactor,
        rayDamageHitEffectOffsetFactor = rayDamageHitEffectOffsetFactor,
        rayDamageSecondaryTargetTags = rayDamageSecondaryTargetTags,
        directDamageArmourIgnoreAmount = directDamageArmourIgnoreAmount,
        areaDamageArmourIgnoreAmount = areaDamageArmourIgnoreAmount,
    )
}

@UnitDsl
class TurretExtensionBuilder internal constructor() {
    var preFireDuration: Ticks = Ticks.ZERO
    var renderBinding: PreFireRenderBinding? = null
    var observerBinding: PreFireObserverBinding? = null

    internal fun buildDefinition() = TurretExtension(preFireDuration, renderBinding, observerBinding)
}

@UnitDsl
class EffectExtensionBuilder internal constructor() {
    var renderBinding: EffectRenderBinding? = null

    internal fun buildDefinition() = EffectExtension(
        requireNotNull(renderBinding) { "Native effect extension must define renderBinding" },
    )
}

private fun requireExtensionSectionName(name: String): String {
    require(name.matches(Regex("[A-Za-z0-9_.-]+"))) { "Invalid native INI section name: $name" }
    return name
}
