package io.github.rwx.mod

import io.github.rwx.render.ModRenderRegistry

object ModRegistry {
    private val mods = mutableListOf<Mod>()

    @JvmStatic
    fun register(mod: Mod) {
        mods.add(mod)
    }

    @JvmStatic
    fun unregister(mod: Mod) {
        mods.remove(mod)
        (mod as? JvmMod)?.let { jvmMod ->
            (runCatching { jvmMod.api }.getOrNull() as? ApiImpl)?.let { api ->
                ModRenderRegistry.unregister(api)
                ModProjectileObserverRegistry.unregister(api)
                ModPreFireObserverRegistry.unregister(api)
                ModAudioRegistry.unregister(api)
                ModTeamActionRegistry.unregister(api)
                ModUiRegistry.unregister(api)
            }
        }
    }

    @JvmStatic
    fun getAll(): List<Mod> = mods.toList()

    @JvmStatic
    fun getById(id: String): Mod? = mods.find { it.metadata.id == id }

    @JvmStatic
    fun clear() {
        mods.filterIsInstance<JvmMod>().forEach { mod ->
            (runCatching { mod.api }.getOrNull() as? ApiImpl)?.let { api ->
                ModRenderRegistry.unregister(api)
                ModProjectileObserverRegistry.unregister(api)
                ModPreFireObserverRegistry.unregister(api)
                ModAudioRegistry.unregister(api)
                ModTeamActionRegistry.unregister(api)
                ModUiRegistry.unregister(api)
            }
        }
        mods.clear()
        NativeCommandQueue.clear()
    }
}
