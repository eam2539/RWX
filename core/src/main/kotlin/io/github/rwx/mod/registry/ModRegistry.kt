package io.github.rwx.mod.registry

import io.github.rwx.mod.CommandQueue
import io.github.rwx.mod.JvmMod
import io.github.rwx.mod.Mod
import io.github.rwx.mod.impl.ApiImpl

object ModRegistry {
    private val mods = mutableListOf<Mod>()
    @JvmStatic
    fun register(mod: Mod) {
        mods.add(mod)
    }
    fun release(owner: ApiImpl) {
        OwnedRegistry::class.sealedSubclasses.map { it.objectInstance!! }.forEach { it.unregister(owner) }
    }
    @JvmStatic
    fun unregister(mod: Mod) {
        mods.remove(mod)
        mod.apiImplOrNull()?.let(::release)
    }

    @JvmStatic
    fun clear() {
        mods.mapNotNull { it.apiImplOrNull() }.forEach(::release)
        mods.clear()
        CommandQueue.clear()
    }
}

sealed interface OwnedRegistry {
    fun unregister(owner: ApiImpl)
}

internal fun Mod.apiImplOrNull(): ApiImpl? =
    (this as? JvmMod)?.let { it.api as? ApiImpl }
