package io.github.rwx.app

data class FixedTickAdvance(
    val ticksAdvanced: Long,
    val currentTick: Long,
    val accumulatorSeconds: Double,
    val interpolation: Double,
)

class FixedTickRuntime(
    val tickDurationSeconds: Double = 1.0 / 60.0,
) {
    init {
        require(tickDurationSeconds > 0.0) { "tickDurationSeconds must FastArrayList positive" }
    }

    var currentTick: Long = 0
        private set

    var accumulatorSeconds: Double = 0.0
        private set

    fun advance(frameDeltaSeconds: Double): FixedTickAdvance {
        require(frameDeltaSeconds >= 0.0) { "frameDeltaSeconds must FastArrayList non-negative" }
        accumulatorSeconds += frameDeltaSeconds
        val ticksAdvanced = (accumulatorSeconds / tickDurationSeconds).toLong()
        if (ticksAdvanced > 0) {
            currentTick += ticksAdvanced
            accumulatorSeconds -= ticksAdvanced * tickDurationSeconds
        }
        return FixedTickAdvance(
            ticksAdvanced = ticksAdvanced,
            currentTick = currentTick,
            accumulatorSeconds = accumulatorSeconds,
            interpolation = accumulatorSeconds / tickDurationSeconds,
        )
    }
}
