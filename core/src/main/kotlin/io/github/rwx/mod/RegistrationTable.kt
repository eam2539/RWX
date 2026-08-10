package io.github.rwx.mod

import io.github.rwx.logger

/**
 * A keyed table of registrations owned by mods.
 *
 * Every mod-facing registry keeps the same thing: entries tagged with the [ApiImpl] that
 * registered them, rejected on duplicate keys, and dropped wholesale when that mod goes away.
 * This holds that logic once so registries only describe what they store.
 *
 * [lock] is supplied by the owning registry rather than created here: registries that keep
 * several tables consistent with each other (see [io.github.rwx.render.RenderRegistry])
 * must guard them all with one monitor, which per-table locks would silently break.
 *
 * Pass an [IdentityHashMap][java.util.IdentityHashMap] as [entries] to key by native object
 * identity instead of by id.
 */
class RegistrationTable<K : Any, V : Any>(
    private val label: String,
    private val lock: Any = Any(),
    private val entries: MutableMap<K, Owned<V>> = LinkedHashMap(),
    private val describeKey: (K) -> String = { it.toString() },
) : OwnedRegistry {

    /** A [value] together with the mod that registered it. */
    class Owned<V>(val owner: ApiImpl, val value: V)

    /** Adds [value] under [key], rejecting a key that is already taken. */
    fun register(owner: ApiImpl, key: K, value: V) {
        synchronized(lock) {
            check(key !in entries) { "$label is already registered: ${describeKey(key)}" }
            entries[key] = Owned(owner, value)
        }
    }

    /** Adds [value] under [key], replacing any existing entry. For rebindable bindings. */
    fun put(owner: ApiImpl, key: K, value: V) {
        synchronized(lock) { entries[key] = Owned(owner, value) }
    }

    operator fun get(key: K): V? = synchronized(lock) { entries[key]?.value }

    /** The entry under [key] along with its owner, or null. */
    fun owned(key: K): Owned<V>? = synchronized(lock) { entries[key] }

    /** The entry under [key], failing if absent. */
    fun required(key: K): V = synchronized(lock) {
        checkNotNull(entries[key]) { "$label is not registered: ${describeKey(key)}" }.value
    }

    operator fun contains(key: K): Boolean = synchronized(lock) { key in entries }

    fun isEmpty(): Boolean = synchronized(lock) { entries.isEmpty() }

    fun remove(key: K): V? = synchronized(lock) { entries.remove(key)?.value }

    /** Every entry, in registration order, detached from the table. */
    fun snapshot(): Map<K, V> = synchronized(lock) {
        entries.entries.associateTo(LinkedHashMap()) { it.key to it.value.value }
    }

    /** Values registered by [owner], in registration order. */
    fun ownedBy(owner: ApiImpl): List<V> = synchronized(lock) {
        entries.values.filter { it.owner === owner }.map { it.value }
    }

    /** Drops everything [owner] registered and returns it, so callers can run teardown. */
    fun removeOwned(owner: ApiImpl): Map<K, V> = synchronized(lock) {
        val removed = LinkedHashMap<K, V>()
        entries.entries.removeAll { (key, owned) ->
            (owned.owner === owner).also { if (it) removed[key] = owned.value }
        }
        removed
    }

    override fun unregister(owner: ApiImpl) {
        removeOwned(owner)
    }

    fun clear() = synchronized(lock) { entries.clear() }
}

/**
 * Rate-limited error reporting for mod callbacks.
 *
 * Mod code runs per-tick and per-frame, so an exception that happens once happens every
 * frame. Each distinct failure is logged the first time and swallowed afterwards; the
 * caller keeps running so one broken mod cannot stall the loop.
 */
class ModFailureLog(@PublishedApi internal val subject: String) {
    @PublishedApi
    internal val reported = mutableSetOf<String>()

    /** Runs [block], logging the first occurrence of each distinct error per [id]. */
    inline fun runSafely(id: String, block: () -> Unit) {
        try {
            block()
        } catch (error: Throwable) {
            report("$id:${error.javaClass.name}:${error.message}", id, error)
        }
    }

    /**
     * Runs [block], logging at most one failure per [id] whatever the error.
     *
     * For per-frame paths, where an error message carrying changing values (coordinates,
     * sizes) would defeat [runSafely]'s per-error key and flood the log.
     */
    inline fun runSafelyOncePerId(id: String, block: () -> Unit) {
        try {
            block()
        } catch (error: Throwable) {
            report(id, id, error)
        }
    }

    @PublishedApi
    internal fun report(dedupKey: String, id: String, error: Throwable) {
        if (synchronized(reported) { reported.add(dedupKey) }) {
            logger.error(error) { "$subject failed: $id" }
        }
    }

    fun clear() = synchronized(reported) { reported.clear() }
}
