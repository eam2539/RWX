package io.github.rwx.mod

import io.github.rwx.mod.api.Api

abstract class JvmMod : Mod {
    final override lateinit var metadata: ModMetadata
    final override val type: ModType = ModType.JVM

    lateinit var api: Api
    lateinit var classLoader: ClassLoader
}
