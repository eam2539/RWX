package io.github.rwx.mod.api

@JvmInline
value class PreFireObserverId(val value: String) {
    init {
        require(value.matches(Regex("[a-z][a-z0-9_.-]*:[a-z0-9][a-z0-9_./-]*"))) {
            "Invalid namespace-qualified pre-fire observer id: $value"
        }
    }
}

data class PreFireObserverBinding(
    val observerId: PreFireObserverId,
    val variantId: RenderVariantId,
)

data class PreFireInstanceId(
    val sourceObjectId: Long,
    val turretIndex: Int,
    val generation: Int,
)

enum class PreFireLifecyclePhase {
    STARTED,
    UPDATED,
    ENDED,
}

data class PreFireLifecycleContext(
    val instanceId: PreFireInstanceId,
    val phase: PreFireLifecyclePhase,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val ageTicks: Float,
    val previousAgeTicks: Float?,
    val remainingTicks: Float,
    val previousRemainingTicks: Float?,
    val durationTicks: Float,
    val variantId: RenderVariantId,
)

fun interface PreFireObserver {
    fun onLifecycle(context: PreFireLifecycleContext)
}
