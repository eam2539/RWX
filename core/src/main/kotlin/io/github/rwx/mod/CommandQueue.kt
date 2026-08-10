package io.github.rwx.mod

import io.github.rwx.logger

object CommandQueue {
    private data class Entry(
        val owner: Any,
        val command: () -> Unit,
    )

    private val lock = Any()
    private var pending = mutableListOf<Entry>()

    internal fun enqueue(owner: Any, command: () -> Unit) {
        if (!isJvmModSimulationAllowed()) return
        synchronized(lock) {
            pending += Entry(owner, command)
        }
    }

    internal fun cancel(owner: Any) {
        synchronized(lock) {
            pending.removeAll { it.owner === owner }
        }
    }

    @JvmStatic
    fun drain() {
        if (!isJvmModSimulationAllowed()) return
        val batch = synchronized(lock) {
            if (pending.isEmpty()) return
            pending.also { pending = mutableListOf() }
        }
        batch.forEach { entry ->
            runCatching(entry.command)
                .onFailure { logger.error(it) { "Queued JVM mod native command failed" } }
        }
    }

    @JvmStatic
    fun clear() {
        synchronized(lock) {
            pending.clear()
        }
    }

    internal fun size(): Int = synchronized(lock) { pending.size }
}
