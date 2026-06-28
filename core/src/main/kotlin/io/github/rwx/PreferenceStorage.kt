package io.github.rwx

const val PREFERENCE_NAME: String = "preferences"

interface PreferenceStorage {
    fun preference(name: String): Preference

    fun flush()
}

interface Preference {
    fun getBoolean(key: String, defValue: Boolean = false): Boolean
    fun getInt(key: String, defValue: Int = 0): Int
    fun getLong(key: String, defValue: Long = 0L): Long
    fun getFloat(key: String, defValue: Float = 0f): Float
    fun getString(key: String, defValue: String? = ""): String?

    fun putBoolean(key: String, value: Boolean): Preference
    fun putInt(key: String, value: Int): Preference
    fun putLong(key: String, value: Long): Preference
    fun putFloat(key: String, value: Float): Preference
    fun putString(key: String, value: String?): Preference

    fun contains(key: String): Boolean
    fun remove(key: String): String?
    fun clear()
}
