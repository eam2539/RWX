package io.github.rwx.mod

import com.corrodinggames.rts.game.units.custom.CustomUnit
import io.github.rwx.mod.api.*

object PreFireObserverRegistry : OwnedRegistry {
    private data class InstanceKey(val sourceObjectId: Long, val turretIndex: Int)

    private data class Snapshot(
        val observerId: String,
        val variantId: String,
        val generation: Int,
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float,
        val age: Float,
        val remaining: Float,
        val duration: Float,
    )

    private val observers = RegistrationTable<String, PreFireObserver>("Pre-fire observer")
    private val active = linkedMapOf<InstanceKey, Snapshot>()
    private val generations = mutableMapOf<InstanceKey, Int>()
    private val failures = ModFailureLog("Mod pre-fire observer")

    fun register(owner: ApiImpl, id: PreFireObserverId, observer: PreFireObserver) {
        observers.register(owner, id.value, observer)
    }

    @JvmStatic
    fun update(unit: CustomUnit) {
        unit.unitConfig.turrets.forEachIndexed { turretIndex, turret ->
            val observerId = turret?.preFireObserverId ?: return@forEachIndexed
            val variantId = turret.preFireObserverVariant ?: return@forEachIndexed
            val key = InstanceKey(unit.objectId, turretIndex)
            val movement = unit.movementLevels[turretIndex]
            val target = movement.targetUnit
            val phaseStart = (turret.warmup - turret.preFireDuration).coerceAtLeast(0f)
            val age = movement.speed - phaseStart
            val isActive = !unit.isDestroyed && target != null && turret.preFireDuration > 0f && age > 0f
            val previous = active[key]

            if (!isActive) {
                if (previous != null) {
                    active.remove(key)
                    dispatch(key, previous, previous, PreFireLifecyclePhase.ENDED)
                }
                return@forEachIndexed
            }

            val muzzle = unit.D(turretIndex)
            val restarted = previous != null && (
                    previous.observerId != observerId ||
                            previous.variantId != variantId ||
                            age + RESTART_EPSILON < previous.age
                    )
            if (restarted) {
                dispatch(key, previous, previous, PreFireLifecyclePhase.ENDED)
            }
            val current = Snapshot(
                observerId = observerId,
                variantId = variantId,
                generation = if (previous == null || restarted) {
                    (generations[key] ?: -1) + 1
                } else {
                    previous.generation
                },
                startX = muzzle.a,
                startY = muzzle.b,
                endX = target.posX,
                endY = target.posY,
                age = age.coerceIn(0f, turret.preFireDuration),
                remaining = (turret.preFireDuration - age).coerceAtLeast(0f),
                duration = turret.preFireDuration,
            )
            generations[key] = current.generation
            active[key] = current
            dispatch(
                key = key,
                current = current,
                previous = if (restarted) null else previous,
                phase = if (previous == null || restarted) {
                    PreFireLifecyclePhase.STARTED
                } else {
                    PreFireLifecyclePhase.UPDATED
                },
            )
        }
    }

    override fun unregister(owner: ApiImpl) {
        val removedIds = observers.removeOwned(owner).keys
        active.entries.removeAll { it.value.observerId in removedIds }
    }

    private fun dispatch(
        key: InstanceKey,
        current: Snapshot,
        previous: Snapshot?,
        phase: PreFireLifecyclePhase,
    ) {
        val observer = observers[current.observerId] ?: return
        failures.runSafely(current.observerId) {
            observer.onLifecycle(
                PreFireLifecycleContext(
                    instanceId = PreFireInstanceId(key.sourceObjectId, key.turretIndex, current.generation),
                    phase = phase,
                    startX = current.startX,
                    startY = current.startY,
                    endX = current.endX,
                    endY = current.endY,
                    ageTicks = current.age,
                    previousAgeTicks = previous?.age,
                    remainingTicks = current.remaining,
                    previousRemainingTicks = previous?.remaining,
                    durationTicks = current.duration,
                    variantId = RenderVariantId(current.variantId),
                )
            )
        }
    }

    private const val RESTART_EPSILON = 0.001f
}
