package io.github.rwx.mod.api

@JvmInline
value class ProjectileObserverId(val value: String) {
    init {
        require(value.matches(Regex("[a-z][a-z0-9_.-]*:[a-z0-9][a-z0-9_./-]*"))) {
            "Invalid namespace-qualified projectile observer id: $value"
        }
    }
}

data class ProjectileObserverBinding(
    val observerId: ProjectileObserverId,
    val variantId: RenderVariantId,
)

data class ProjectileInstanceId(
    val objectId: Long,
    val generation: Int,
)

enum class ProjectileLifecyclePhase {
    STARTED,
    UPDATED,
    ENDED,
}

data class ProjectileLifecycleContext(
    val instanceId: ProjectileInstanceId,
    val phase: ProjectileLifecyclePhase,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val ageTicks: Float,
    val previousAgeTicks: Float?,
    val remainingTicks: Float,
    val previousRemainingTicks: Float?,
    val variantId: RenderVariantId,
)

fun interface ProjectileObserver {
    fun onLifecycle(context: ProjectileLifecycleContext)
}
