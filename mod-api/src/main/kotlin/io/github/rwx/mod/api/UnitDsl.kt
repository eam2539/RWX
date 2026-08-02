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
    private val projectiles = linkedMapOf<String, ProjectileExtensionBuilder>()
    private val turrets = linkedMapOf<String, TurretExtensionBuilder>()
    private val effects = linkedMapOf<String, NativeEffectExtensionBuilder>()

    fun projectile(name: String, configure: ProjectileExtensionBuilder.() -> kotlin.Unit) {
        projectiles.getOrPut(requireExtensionSectionName(name), ::ProjectileExtensionBuilder).configure()
    }

    fun turret(name: String, configure: TurretExtensionBuilder.() -> kotlin.Unit) {
        turrets.getOrPut(requireExtensionSectionName(name), ::TurretExtensionBuilder).configure()
    }

    fun effect(name: String, configure: NativeEffectExtensionBuilder.() -> kotlin.Unit) {
        effects.getOrPut(requireExtensionSectionName(name), ::NativeEffectExtensionBuilder).configure()
    }

    internal fun buildDefinition() = UnitExtension(
        shaderId = shaderId,
        renderBinding = renderBinding,
        projectiles = projectiles.mapValues { it.value.buildDefinition() },
        turrets = turrets.mapValues { it.value.buildDefinition() },
        effects = effects.mapValues { it.value.buildDefinition() },
    )
}

@UnitDsl
class ProjectileExtensionBuilder internal constructor() {
    var renderBinding: ProjectileRenderBinding? = null
    var observerBinding: ProjectileObserverBinding? = null

    internal fun buildDefinition() = ProjectileExtension(renderBinding, observerBinding)
}

@UnitDsl
class TurretExtensionBuilder internal constructor() {
    var preFireDuration: Ticks = Ticks.ZERO
    var renderBinding: PreFireRenderBinding? = null
    var observerBinding: PreFireObserverBinding? = null

    internal fun buildDefinition() = TurretExtension(preFireDuration, renderBinding, observerBinding)
}

@UnitDsl
class NativeEffectExtensionBuilder internal constructor() {
    var renderBinding: EffectRenderBinding? = null

    internal fun buildDefinition() = NativeEffectExtension(
        requireNotNull(renderBinding) { "Native effect extension must define renderBinding" },
    )
}

private fun requireExtensionSectionName(name: String): String {
    require(name.matches(Regex("[A-Za-z0-9_.-]+"))) { "Invalid native INI section name: $name" }
    return name
}
