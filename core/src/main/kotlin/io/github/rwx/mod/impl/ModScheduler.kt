package io.github.rwx.mod.impl

import com.corrodinggames.rts.gameFramework.GameEngine
import io.github.rwx.logger
import io.github.rwx.mod.api.Api
import io.github.rwx.mod.api.ModTask
import io.github.rwx.mod.isJvmModSimulationAllowed

object ModScheduler {
    private val lock = Any()
    private val tasks = mutableListOf<ScheduledTask>()
    private var generation = 0L

    fun schedule(api: Api, delayTicks: Long, intervalTicks: Long?, task: ModTask): ScheduledTask {
        val engine = GameEngine.getInstance()
        val now = engine?.currentTick?.toLong() ?: 0L
        if (!isJvmModSimulationAllowed(engine)) {
            return ScheduledTask(api, task, now, intervalTicks, generation).also(ScheduledTask::cancel)
        }
        return synchronized(lock) {
            ScheduledTask(api, task, now + delayTicks.coerceAtLeast(0), intervalTicks, generation)
                .also(tasks::add)
        }
    }

    @JvmStatic
    fun clear() {
        synchronized(lock) {
            generation++
            tasks.forEach(ScheduledTask::cancel)
            tasks.clear()
        }
    }

    @JvmStatic
    fun tick() {
        val engine = GameEngine.getInstance()
        if (!isJvmModSimulationAllowed(engine)) return
        if (synchronized(lock) { tasks.isEmpty() }) return
        val now = engine?.currentTick?.toLong() ?: return
        val dueTasks = synchronized(lock) {
            if (tasks.isEmpty()) return
            val due = mutableListOf<ScheduledTask>()
            val iterator = tasks.iterator()
            while (iterator.hasNext()) {
                val task = iterator.next()
                if (task.cancelled || task.generation != generation) {
                    iterator.remove()
                } else if (task.nextTick <= now) {
                    iterator.remove()
                    due += task
                }
            }
            due
        }
        dueTasks.forEach { task ->
            val active = synchronized(lock) { !task.cancelled && task.generation == generation }
            if (!active) return@forEach
            runCatching { task.task.run(task.api) }
                .onFailure { logger.error(it) { "Scheduled JVM mod task failed" } }
            val interval = task.intervalTicks
            if (interval != null && !task.cancelled) {
                synchronized(lock) {
                    if (!task.cancelled && task.generation == generation) {
                        task.nextTick = now + interval.coerceAtLeast(1)
                        tasks += task
                    }
                }
            }
        }
    }

    class ScheduledTask(
        val api: Api,
        val task: ModTask,
        var nextTick: Long,
        val intervalTicks: Long?,
        val generation: Long,
    ) : io.github.rwx.mod.api.ScheduledTask {
        @Volatile
        override var cancelled: Boolean = false
            private set

        override fun cancel() {
            cancelled = true
        }
    }
}
