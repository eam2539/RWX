package io.github.rwx.mod

import io.github.rwx.mod.api.*

internal data class UnitIniCompilation(
    val fileName: String,
    val content: String,
    val warnings: List<String>,
    val extensionBindings: UnitExtensionBindings,
)

internal data class UnitExtensionBindings(
    val projectiles: Map<String, ProjectileExtensionBinding>,
    val turrets: Map<String, TurretExtensionBinding>,
    val effects: Map<String, EffectExtensionBinding>,
)

internal data class ProjectileExtensionBinding(
    val renderer: ProjectileRenderBinding?,
    val observer: ProjectileObserverBinding?,
)

internal data class TurretExtensionBinding(
    val preFireDuration: Int,
    val renderer: PreFireRenderBinding?,
    val observer: PreFireObserverBinding?,
)

internal data class EffectExtensionBinding(val renderer: EffectRenderBinding)

private class MutableUnitExtensionBindings {
    val projectiles = linkedMapOf<String, ProjectileExtensionBinding>()
    val turrets = linkedMapOf<String, TurretExtensionBinding>()
    val effects = linkedMapOf<String, EffectExtensionBinding>()

    fun freeze() = UnitExtensionBindings(projectiles.toMap(), turrets.toMap(), effects.toMap())
}

internal object UnitIniCompiler {
    fun compile(
        definition: UnitDefinition,
        effects: Map<EffectId, EffectDefinition> = emptyMap(),
    ): UnitIniCompilation {
        val document = IniDocumentWriter()
        val warnings = mutableListOf<String>()
        val extensionBindings = MutableUnitExtensionBindings()
        writeCore(document, definition)
        writeGraphics(document)
        writeMovement(document)
        writeEffects(document, definition, effects, extensionBindings)
        writeNativeExtensionBindings(definition, extensionBindings)
        definition.iniSections.forEach { section ->
            applyIniSpec(document.section(section.name), section.values)
        }
        collectSpecWarnings(definition, effects, warnings)
        return UnitIniCompilation(
            fileName = "${engineName(definition.id)}.ini",
            content = document.render(),
            warnings = warnings,
            extensionBindings = extensionBindings.freeze(),
        )
    }

    fun engineName(id: UnitId): String = id.nativeName

    fun engineEffectName(id: EffectId): String = id.nativeName

    private fun writeCore(
        document: IniDocumentWriter,
        definition: UnitDefinition,
    ) {
        val core = document.section("core")
        core["name"] = engineName(definition.id)
        core["class"] = "CustomUnitMetadata"
        core["displayText"] = definition.id.value.substringAfter(':')
        core["price"] = "0"
        core["maxHp"] = "1"
        core["mass"] = "1"
        core["radius"] = "1"
        core["displayRadius"] = "1"
        core["buildSpeed"] = "1"
        core["armour"] = "0"
        core["fogOfWarSightRange"] = "0"
        core["isBuilding"] = "false"
        core["tags"] = ""
        core["explodeOnDeath"] = "true"
        core["hideScorchMark"] = "false"
    }

    private fun writeGraphics(document: IniDocumentWriter) {
        val graphics = document.section("graphics")
        graphics["total_frames"] = "1"
        graphics["image"] = "ROOT:units/missing_unit/missing.png"
        graphics["teamColoringMode"] = "disabled"
        graphics["imageSmoothing"] = "false"
        graphics["imageSmoothingWhenZoomedIn"] = "false"
        graphics["imageScale"] = "1.0"
    }

    private fun writeMovement(document: IniDocumentWriter) {
        val movement = document.section("movement")
        movement["movementType"] = "NONE"
        movement["moveSpeed"] = "1.0"
        movement["moveAccelerationSpeed"] = "0.0"
        movement["moveDecelerationSpeed"] = "0.0"
        movement["maxTurnSpeed"] = "4.0"
        movement["reverseSpeedPercentage"] = "0.6"

        val attack = document.section("attack")
        attack["canAttack"] = "false"
        attack["attackMovement"] = "normal"
    }

    private fun writeEffects(
        document: IniDocumentWriter,
        definition: UnitDefinition,
        effects: Map<EffectId, EffectDefinition>,
        extensionBindings: MutableUnitExtensionBindings,
    ) {
        val directlyReferenced = referencedEffectsFromIni(definition, effects) +
                definition.eventHandlers.flatMap { binding ->
                    binding.actions.map { action ->
                        when (action) {
                            is UnitEventAction.SpawnEffect -> action.effectId
                        }
                    }
                }

        val referenced = linkedMapOf<EffectId, EffectDefinition>()
        fun collect(id: EffectId) {
            if (id in referenced) return
            val effect = requireNotNull(effects[id]) {
                "Unit ${definition.id.value} references unregistered effect ${id.value}"
            }
            referenced[id] = effect
            (effect.emittedEffects + effect.emittedEffectsOnDeath).forEach(::collect)
        }
        directlyReferenced.sortedBy { it.value }.forEach(::collect)

        referenced.toSortedMap(compareBy { it.value }).forEach { (id, effect) ->
            val section = document.section("effect_${id.toIniLocalName()}")
            effect.image?.let { section["image"] = it.toIniPath() }
            section["life"] = effect.life.value.toString()
            if (effect.lifeRandom.value > 0) section["lifeRandom"] = effect.lifeRandom.value.toString()
            if (effect.spawnChance != 1f) section["spawnChance"] = effect.spawnChance.toString()
            section["color"] = effect.color.toIniColor()
            section["scaleFrom"] = effect.scaleFrom.toString()
            section["scaleTo"] = effect.scaleTo.toString()
            if (effect.scale.xFrom != effect.scaleFrom || effect.scale.yFrom != effect.scaleFrom ||
                effect.scale.xTo != effect.scaleTo || effect.scale.yTo != effect.scaleTo
            ) {
                section["scaleXFrom"] = effect.scale.xFrom.toString()
                section["scaleYFrom"] = effect.scale.yFrom.toString()
                section["scaleXTo"] = effect.scale.xTo.toString()
                section["scaleYTo"] = effect.scale.yTo.toString()
            }
            section["alpha"] = effect.alpha.toString()
            if (effect.fadeIn.value > 0) section["fadeInTime"] = effect.fadeIn.value.toString()
            section["fadeOut"] = effect.fadeOut.toString()
            if (effect.delay.value > 0) section["delayedStartTimer"] = effect.delay.value.toString()
            if (effect.delayRandom.value > 0) {
                section["delayedStartTimerRandom"] = effect.delayRandom.value.toString()
            }
            effect.offset.run {
                if (relativeX != 0f) section["xOffsetRelative"] = relativeX.toString()
                if (relativeY != 0f) section["yOffsetRelative"] = relativeY.toString()
                if (height != 0f) section["hOffset"] = height.toString()
                if (absoluteX != 0f) section["xOffsetAbsolute"] = absoluteX.toString()
                if (absoluteY != 0f) section["yOffsetAbsolute"] = absoluteY.toString()
            }
            effect.motion.run {
                if (relativeXPerTick != 0f) section["xSpeedRelative"] = relativeXPerTick.toString()
                if (relativeYPerTick != 0f) section["ySpeedRelative"] = relativeYPerTick.toString()
                if (heightPerTick != 0f) section["hSpeed"] = heightPerTick.toString()
                if (absoluteXPerTick != 0f) section["xSpeedAbsolute"] = absoluteXPerTick.toString()
                if (absoluteYPerTick != 0f) section["ySpeedAbsolute"] = absoluteYPerTick.toString()
                if (rotationDegreesPerTick != 0f) section["dirSpeed"] = rotationDegreesPerTick.toString()
            }
            if (effect.directionOffsetDegrees != 0f) {
                section["dirOffset"] = effect.directionOffsetDegrees.toString()
            }
            if (effect.directionOffsetRandomDegrees != 0f) {
                section["dirOffsetRandom"] = effect.directionOffsetRandomDegrees.toString()
            }
            if (effect.alwaysStartDirectionAtZero) section["alwaysStartDirAtZero"] = "true"
            effect.animation?.let { animation ->
                section["total_frames"] = animation.totalFrames.toString()
                animation.frameWidth?.let { section["frame_width"] = it.toString() }
                animation.frameHeight?.let { section["frame_height"] = it.toString() }
                section["animateFrameStart"] = animation.startFrame.toString()
                section["animateFrameEnd"] = animation.endFrame.toString()
                if (animation.startFrameRandomAdd > 0) {
                    section["animateFrameStartRandomAdd"] = animation.startFrameRandomAdd.toString()
                }
                section["animateFramePingPong"] = animation.pingPong.toString()
                section["animateFrameLooping"] = animation.looping.toString()
                section["animateFrameSpeed"] = animation.frameDuration.value.toString()
                if (animation.frameDurationRandom.value > 0) {
                    section["animateFrameSpeedRandom"] = animation.frameDurationRandom.value.toString()
                }
            }
            effect.renderBinding?.let { binding ->
                extensionBindings.effects[id.toIniLocalName()] = EffectExtensionBinding(binding)
            }
            section["attachedToUnit"] = effect.attachedToSource.toString()
            section["liveAfterAttachedDies"] = effect.liveAfterSourceDies.toString()
            section["drawUnderUnits"] = effect.drawUnderUnits.toString()
            if (effect.blendMode != io.github.rwx.mod.api.EffectBlendMode.ALPHA) {
                section["blendMode"] = effect.blendMode.name.lowercase()
            }
            if (effect.imageAnchor != io.github.rwx.mod.api.EffectImageAnchor.CENTER) {
                section["imageAnchor"] = effect.imageAnchor.name.lowercase()
            }
            section["priority"] = effect.priority.toIniValue()
            if (effect.emittedEffects.isNotEmpty()) {
                section["alsoEmitEffects"] = effect.emittedEffects.toIniEffectList()
            }
            if (effect.emittedEffectsOnDeath.isNotEmpty()) {
                section["alsoEmitEffectsOnDeath"] = effect.emittedEffectsOnDeath.toIniEffectList()
            }
            applyIniSpec(section, effect.ini)
        }
    }

    private fun referencedEffectsFromIni(
        definition: UnitDefinition,
        effects: Map<EffectId, EffectDefinition>,
    ): Set<EffectId> {
        val effectsByNativeName = effects.keys.groupBy(::engineEffectName)
        return definition.iniSections.asSequence()
            .flatMap { section -> section.values.values.values.asSequence() }
            .flatMap { it.nestedStrings() }
            .flatMap { value -> CUSTOM_EFFECT_REFERENCE.findAll(value).map { it.groupValues[1] } }
            .flatMap { nativeName -> effectsByNativeName[nativeName].orEmpty().asSequence() }
            .toSet()
    }

    private fun writeNativeExtensionBindings(
        definition: UnitDefinition,
        bindings: MutableUnitExtensionBindings,
    ) {
        val sectionNames = definition.iniSections.mapTo(hashSetOf()) { it.name }
        definition.extension.projectiles.forEach { (name, extension) ->
            require("projectile_$name" in sectionNames) {
                "Unit ${definition.id.value} extends missing native projectile section '$name'"
            }
            bindings.projectiles[name] = ProjectileExtensionBinding(
                renderer = extension.renderBinding,
                observer = extension.observerBinding,
            )
        }
        definition.extension.turrets.forEach { (name, extension) ->
            require("turret_$name" in sectionNames) {
                "Unit ${definition.id.value} extends missing native turret section '$name'"
            }
            bindings.turrets[name] = TurretExtensionBinding(
                preFireDuration = extension.preFireDuration.value,
                renderer = extension.renderBinding,
                observer = extension.observerBinding,
            )
        }
        definition.extension.effects.forEach { (name, extension) ->
            require("effect_$name" in sectionNames) {
                "Unit ${definition.id.value} extends missing native effect section '$name'"
            }
            bindings.effects[name] = EffectExtensionBinding(extension.renderBinding)
        }
    }

    private fun applyIniSpec(section: MutableMap<String, String>, spec: IniSpecValues) {
        spec.values.forEach { (key, value) -> section[key] = value.toIniValue() }
    }

    private fun collectSpecWarnings(
        definition: UnitDefinition,
        effects: Map<EffectId, EffectDefinition>,
        warnings: MutableList<String>,
    ) {
        val collected = mutableListOf<String>()
        definition.iniSections.forEach { section ->
            section.values.warnings.forEach { collected += "[${section.name}] $it" }
        }
        effects.values.forEach { effect ->
            effect.ini.warnings.forEach { collected += "effect ${effect.id.value}: $it" }
        }
        warnings += collected.distinct()
    }

    private fun ResourcePath.toIniPath(): String = nativePath

    private fun EffectId.toIniLocalName(): String = engineEffectName(this)

    private fun EffectId.toIniEffectReference(): String = "CUSTOM:${toIniLocalName()}"

    private fun Iterable<EffectId>.toIniEffectList(): String = joinToString(",") { it.toIniEffectReference() }

    private fun RgbaColor.toIniColor(): String = if (alpha == 255) {
        "#%02x%02x%02x".format(red, green, blue)
    } else {
        "#%02x%02x%02x%02x".format(alpha, red, green, blue)
    }

    private fun EffectPriority.toIniValue(): String = when (this) {
        EffectPriority.VERY_LOW -> "verylow"
        EffectPriority.LOW -> "low"
        EffectPriority.HIGH -> "high"
        EffectPriority.VERY_HIGH -> "veryhigh"
        EffectPriority.CRITICAL -> "critical"
    }

    private fun Any?.toIniValue(): String = when (this) {
        null -> ""
        is Boolean, is Number -> toString()
        is ResourcePath -> toIniPath()
        is Enum<*> -> name
        is Iterable<*> -> joinToString(",") { it.toIniValue() }
        else -> toString()
    }

    private fun Any?.nestedStrings(): Sequence<String> = when (this) {
        is String -> sequenceOf(this)
        is Iterable<*> -> asSequence().flatMap { it.nestedStrings() }
        is Map<*, *> -> values.asSequence().flatMap { it.nestedStrings() }
        else -> emptySequence()
    }

    private val CUSTOM_EFFECT_REFERENCE = Regex("CUSTOM:([A-Za-z0-9_.-]+)", RegexOption.IGNORE_CASE)

}

private class IniDocumentWriter {
    private val sections = linkedMapOf<String, LinkedHashMap<String, String>>()

    fun section(name: String): LinkedHashMap<String, String> =
        sections.getOrPut(name) { linkedMapOf() }

    fun render(): String = buildString {
        sections.forEach { (name, values) ->
            append('[').append(name).append("]\n")
            values.forEach { (key, value) ->
                append(key).append(": ").append(value).append('\n')
            }
            append('\n')
        }
    }
}
