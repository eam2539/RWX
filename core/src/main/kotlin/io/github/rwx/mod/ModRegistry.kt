package io.github.rwx.mod

import io.github.rwx.render.RenderRegistry

object ModRegistry {
    private val mods = mutableListOf<Mod>()
    private val owned: List<OwnedRegistry> = listOf(
        RenderRegistry,
        ProjectileObserverRegistry,
        TurretFireCycleObserverRegistry,
        DamageRegistry,
        AudioRegistry,
        TeamActionRegistry,
        UiRegistry,
    )
    @JvmStatic
    fun register(mod: Mod) {
        mods.add(mod)
    }
    fun release(owner: ApiImpl) {
        owned.forEach { it.unregister(owner) }
    }
    @JvmStatic
    fun unregister(mod: Mod) {
        mods.remove(mod)
        mod.apiImplOrNull()?.let(::release)
    }

    @JvmStatic
    fun getAll(): List<Mod> = mods.toList()

    @JvmStatic
    fun getById(id: String): Mod? = mods.find { it.metadata.id == id }

    @JvmStatic
    fun clear() {
        mods.mapNotNull { it.apiImplOrNull() }.forEach(::release)
        mods.clear()
        CommandQueue.clear()
    }
}

interface OwnedRegistry {
    fun unregister(owner: ApiImpl)
}

internal fun Mod.apiImplOrNull(): ApiImpl? =
    (this as? JvmMod)?.let { it.api as? ApiImpl }
