package io.github.rwx

import android.content.Context
import android.content.SharedPreferences

/**
 * Android [PreferenceStorage] backed by real Android [android.content.SharedPreferences].
 *
 * Each named table maps to a [android.content.SharedPreferences] file via
 * [android.content.Context.getSharedPreferences]. Writes are batched per table through
 * [android.content.SharedPreferences.Editor] and committed on [flush].
 */
class AndroidPreferenceStorage(
    private val context: Context
) : PreferenceStorage {

    private val preferences = LinkedHashMap<String, AndroidPreference>()
    private val pendingEditors = LinkedHashMap<String, SharedPreferences.Editor>()

    override fun preference(name: String): Preference {
        val existing = preferences[name]
        if (existing != null) return existing

        val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return AndroidPreference(name, prefs).also { preferences[name] = it }
    }

    override fun flush() {
        for ((_, editor) in pendingEditors) {
            editor.apply()
        }
        pendingEditors.clear()
    }

    private fun editor(name: String): SharedPreferences.Editor =
        pendingEditors.getOrPut(name) {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit()
        }

    /**
     * Android implementation of [Preference] wrapping one [SharedPreferences] + its editor.
     */
    private inner class AndroidPreference(
        private val tableName: String,
        private val prefs: SharedPreferences
    ) : Preference {

        override fun getBoolean(key: String, defValue: Boolean): Boolean =
            prefs.getBoolean(key, defValue)

        override fun getInt(key: String, defValue: Int): Int =
            prefs.getInt(key, defValue)

        override fun getLong(key: String, defValue: Long): Long =
            prefs.getLong(key, defValue)

        override fun getFloat(key: String, defValue: Float): Float =
            prefs.getFloat(key, defValue)

        override fun getString(key: String, defValue: String?): String? =
            prefs.getString(key, defValue) ?: defValue


        override fun putBoolean(key: String, value: Boolean): Preference {
            editor(tableName).putBoolean(key, value)
            return this
        }

        override fun putInt(key: String, value: Int): Preference {
            editor(tableName).putInt(key, value)
            return this
        }

        override fun putLong(key: String, value: Long): Preference {
            editor(tableName).putLong(key, value)
            return this
        }

        override fun putFloat(key: String, value: Float): Preference {
            editor(tableName).putFloat(key, value)
            return this
        }

        override fun putString(key: String, value: String?): Preference {
            editor(tableName).putString(key, value)
            return this
        }

        override fun contains(key: String): Boolean =
            prefs.contains(key)

        override fun remove(key: String): String? { //compatible with Java Interface
            editor(tableName).remove(key)
            return null
        }

        override fun clear() {
            editor(tableName).clear()
        }
    }
}
