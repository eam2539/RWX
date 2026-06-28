package io.github.rwx

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import net.peanuuutz.tomlkt.Toml

class TomlPreferenceStorage(
    private val location: StorageLocation,
) : PreferenceStorage {
    private val preferences: MutableMap<String, MutableMap<String, String>> = mutableMapOf()
    private val toml = Toml { ignoreUnknownKeys = true }
    private val serializer = MapSerializer(String.serializer(), MapSerializer(String.serializer(), String.serializer()))

    init {
        if (location.file.exists()) {
            runCatching {
                val text = location.file.readText()
                if (text.isNotBlank()) {
                    preferences.putAll(toml.decodeFromString(serializer, text).mapValues { it.value.toMutableMap() })
                }
            }.onFailure { e ->
                logger.warn(e) { "TomlSettingsStorage: failed to read ${location.file.name}: ${e.message}" }
            }
        } else {
            location.file.parentFile?.mkdirs()
            location.file.createNewFile()
        }
    }

    override fun preference(name: String): Preference =
        TomlPreference(preferences.getOrPut(name) { mutableMapOf() })

    override fun flush() {
        location.file.parentFile?.mkdirs()
        runCatching {
            location.file.writeText(toml.encodeToString(serializer, preferences))
        }.onFailure { e ->
            logger.warn(e) { "TomlSettingsStorage: failed to flush: ${e.message}" }
        }
    }

    private class TomlPreference(
        private val values: MutableMap<String, String>,
    ) : Preference {
        override fun getBoolean(key: String, defValue: Boolean): Boolean =
            values[key]?.toBoolean() ?: defValue

        override fun getInt(key: String, defValue: Int): Int =
            values[key]?.toIntOrNull() ?: defValue

        override fun getLong(key: String, defValue: Long): Long =
            values[key]?.toLongOrNull() ?: defValue

        override fun getFloat(key: String, defValue: Float): Float =
            values[key]?.toFloatOrNull() ?: defValue

        override fun getString(key: String, defValue: String?): String? =
            values[key] ?: defValue

        override fun putBoolean(key: String, value: Boolean): Preference =
            apply { values[key] = value.toString() }

        override fun putInt(key: String, value: Int): Preference =
            apply { values[key] = value.toString() }

        override fun putLong(key: String, value: Long): Preference =
            apply { values[key] = value.toString() }

        override fun putFloat(key: String, value: Float): Preference =
            apply { values[key] = value.toString() }

        override fun putString(key: String, value: String?): Preference =
            apply {
                if (value == null) {
                    values.remove(key)
                } else {
                    values[key] = value
                }
            }

        override fun contains(key: String): Boolean = key in values

        override fun remove(key: String): String? = values.remove(key)

        override fun clear() = values.clear()
    }
}
