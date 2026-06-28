package io.github.rwx.mod.api

val UnitId.nativeName: String
    get() = value.replace(':', '_').replace(INVALID_NATIVE_NAME_CHARACTER, "_")

val WeaponId.nativeName: String
    get() = value.substringAfter(':').replace(INVALID_NATIVE_NAME_CHARACTER, "_")

val EffectId.nativeName: String
    get() = value.substringAfter(':').replace(INVALID_NATIVE_NAME_CHARACTER, "_")

val ResourcePath.nativePath: String
    get() = when {
        value.startsWith("ROOT:") || value.startsWith("SHARED:") -> value
        else -> "ROOT:$value"
    }

private val INVALID_NATIVE_NAME_CHARACTER = Regex("[^A-Za-z0-9_.-]")
