package io.github.rwx.mod

import com.corrodinggames.rts.game.Projectile
import com.corrodinggames.rts.game.ProjectileTemplate
import com.corrodinggames.rts.game.units.custom.CustomProjectileTemplate
import io.github.rwx.logger
import io.github.rwx.mod.api.*

object ModProjectileObserverRegistry {
    private data class Registration(
        val owner: ApiImpl,
        val observer: ProjectileObserver,
    )

    private data class ActiveProjectile(
        val observerId: String,
        val variantId: String,
        val generation: Int,
        val lastAge: Float,
        val lastRemaining: Float,
    )

    private val registrations = linkedMapOf<String, Registration>()
    private val active = linkedMapOf<Long, ActiveProjectile>()
    private val reportedFailures = mutableSetOf<String>()

    fun register(owner: ApiImpl, id: ProjectileObserverId, observer: ProjectileObserver) {
        check(id.value !in registrations) { "Projectile observer is already registered: ${id.value}" }
        registrations[id.value] = Registration(owner, observer)
    }

    @JvmStatic
    fun update(projectile: Projectile, projectileTemplate: ProjectileTemplate) {
        val custom = projectileTemplate as? CustomProjectileTemplate ?: return
        val observerId = custom.projectileObserverId ?: return
        val variantId = custom.projectileObserverVariant ?: return
        val registration = registrations[observerId] ?: return
        val previous = active[projectile.objectId]
        val restarted = previous != null && (
                previous.observerId != observerId ||
                        previous.variantId != variantId ||
                        projectile.J + RESTART_EPSILON < previous.lastAge
                )
        if (restarted) {
            dispatch(projectile, previous, ProjectileLifecyclePhase.ENDED, registrationFor(previous.observerId))
        }
        val current = if (previous == null || restarted) {
            ActiveProjectile(
                observerId = observerId,
                variantId = variantId,
                generation = (previous?.generation ?: -1) + 1,
                lastAge = projectile.J,
                lastRemaining = projectile.h,
            )
        } else {
            previous.copy(lastAge = projectile.J, lastRemaining = projectile.h)
        }
        active[projectile.objectId] = current
        dispatch(
            projectile = projectile,
            state = current,
            phase = if (previous == null || restarted) {
                ProjectileLifecyclePhase.STARTED
            } else {
                ProjectileLifecyclePhase.UPDATED
            },
            registration = registration,
            previousAge = if (previous == null || restarted) null else previous.lastAge,
            previousRemaining = if (previous == null || restarted) null else previous.lastRemaining,
        )
    }

    @JvmStatic
    fun end(projectile: Projectile) {
        val state = active.remove(projectile.objectId) ?: return
        dispatch(projectile, state, ProjectileLifecyclePhase.ENDED, registrationFor(state.observerId))
    }

    fun unregister(owner: ApiImpl) {
        val removedIds = registrations.filterValues { it.owner === owner }.keys
        registrations.keys.removeAll(removedIds)
        active.entries.removeAll { it.value.observerId in removedIds }
    }

    private fun registrationFor(id: String): Registration? = registrations[id]

    private fun dispatch(
        projectile: Projectile,
        state: ActiveProjectile,
        phase: ProjectileLifecyclePhase,
        registration: Registration?,
        previousAge: Float? = state.lastAge,
        previousRemaining: Float? = state.lastRemaining,
    ) {
        registration ?: return
        val target = projectile.l
        val endX = target?.posX ?: projectile.n
        val endY = target?.posY ?: projectile.o
        runCatching {
            registration.observer.onLifecycle(
                ProjectileLifecycleContext(
                    instanceId = ProjectileInstanceId(projectile.objectId, state.generation),
                    phase = phase,
                    startX = projectile.posX,
                    startY = projectile.posY,
                    endX = endX,
                    endY = endY,
                    ageTicks = projectile.J.coerceAtLeast(0f),
                    previousAgeTicks = previousAge,
                    remainingTicks = projectile.h.coerceAtLeast(0f),
                    previousRemainingTicks = previousRemaining,
                    variantId = RenderVariantId(state.variantId),
                )
            )
        }.onFailure { error ->
            val key = "${state.observerId}:${error.javaClass.name}:${error.message}"
            if (reportedFailures.add(key)) {
                logger.error(error) { "Mod projectile observer failed: ${state.observerId}" }
            }
        }
    }

    private const val RESTART_EPSILON = 0.001f
}
