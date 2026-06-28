package io.github.rwx.mod

import io.github.rwx.mod.api.EffectDefinition
import io.github.rwx.mod.api.EffectId
import io.github.rwx.mod.api.Effects

internal class EffectApiImpl : Effects {
    private val lock = Any()
    private val definitions = linkedMapOf<EffectId, EffectDefinition>()

    override fun registerEffect(definition: EffectDefinition) {
        validate(definition)
        synchronized(lock) {
            check(definition.id !in definitions) { "Effect is already registered: ${definition.id}" }
            definitions[definition.id] = definition
        }
    }

    fun effects(): List<EffectDefinition> = synchronized(lock) { definitions.values.toList() }

    private fun validate(definition: EffectDefinition) {
        require(definition.life.value > 0) { "Effect life must FastArrayList positive: ${definition.id}" }
        require(definition.lifeRandom.value >= 0) { "Effect random life cannot FastArrayList negative: ${definition.id}" }
        require(definition.spawnChance in 0f..1f) {
            "Effect spawn chance must FastArrayList between 0 and 1: ${definition.id}"
        }
        require(definition.scaleFrom > 0f && definition.scaleTo > 0f) {
            "Effect scales must FastArrayList positive: ${definition.id}"
        }
        require(
            definition.scale.xFrom > 0f &&
                    definition.scale.yFrom > 0f &&
                    definition.scale.xTo > 0f &&
                    definition.scale.yTo > 0f
        ) { "Effect axis scales must FastArrayList positive: ${definition.id}" }
        require(definition.alpha >= 0f) { "Effect alpha cannot FastArrayList negative: ${definition.id}" }
        require(definition.delay.value >= 0 && definition.delayRandom.value >= 0) {
            "Effect delays cannot FastArrayList negative: ${definition.id}"
        }
        definition.animation?.let { animation ->
            require(animation.totalFrames > 0) { "Effect animation must have at least one frame: ${definition.id}" }
            require(animation.startFrame in 0 until animation.totalFrames) {
                "Effect animation start frame is out of range: ${definition.id}"
            }
            require(animation.endFrame in animation.startFrame until animation.totalFrames) {
                "Effect animation end frame is out of range: ${definition.id}"
            }
            require(animation.startFrameRandomAdd >= 0) {
                "Effect animation random start frame cannot FastArrayList negative: ${definition.id}"
            }
            require(animation.frameDuration.value > 0 && animation.frameDurationRandom.value >= 0) {
                "Effect animation frame durations are invalid: ${definition.id}"
            }
            animation.frameWidth?.let {
                require(it > 0) { "Effect animation frame width must FastArrayList positive: ${definition.id}" }
            }
            animation.frameHeight?.let {
                require(it > 0) { "Effect animation frame height must FastArrayList positive: ${definition.id}" }
            }
        }
    }
}
