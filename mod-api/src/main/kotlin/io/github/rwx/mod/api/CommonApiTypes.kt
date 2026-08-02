package io.github.rwx.mod.api

import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.Map
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.plus
import kotlin.let
import kotlin.require
import kotlin.text.Regex
import kotlin.text.isNotBlank
import kotlin.text.isNotEmpty
import kotlin.text.lowercase
import kotlin.text.matches
import kotlin.text.orEmpty
import kotlin.text.replace
import kotlin.text.substringBefore
import kotlin.text.trim
import kotlin.to

@JvmInline
value class ResourceId(val value: String) {
    init {
        require(value.matches(RESOURCE_ID_PATTERN)) { "Invalid resource id: $value" }
    }
}

data class Cost(val resources: Map<ResourceId, Double>) {
    companion object {
        val ZERO = Cost(emptyMap())
    }
}

data class RgbaColor(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int = 255,
) {
    init {
        require(red in 0..255 && green in 0..255 && blue in 0..255 && alpha in 0..255) {
            "RGBA color channels must be between 0 and 255"
        }
    }

    companion object {
        fun rgb(red: Int, green: Int, blue: Int) = RgbaColor(red, green, blue)
    }
}

data class LocalizedText(
    val key: String? = null,
    val literal: String? = null,
    val translations: Map<String, String> = emptyMap(),
) {
    init {
        require(key == null || key.isNotBlank()) { "Localized text key cannot be blank" }
        require(literal == null || literal.isNotEmpty()) { "Localized text literal cannot be empty" }
        translations.keys.forEach { locale ->
            require(locale.matches(LOCALE_TAG_PATTERN)) { "Invalid localized text locale: $locale" }
        }
    }

    fun translated(locale: String, value: String): LocalizedText = copy(
        translations = translations + (normalizeLocale(locale) to value),
    )

    fun resolve(locale: String? = null): String {
        val normalized = locale?.let(::normalizeLocale)
        val exact = normalized?.let { translations[it] }
        val language = normalized?.substringBefore('-')?.let { languageTag ->
            translations.entries.firstOrNull { normalizeLocale(it.key).substringBefore('-') == languageTag }?.value
        }
        return exact ?: language ?: literal ?: key.orEmpty()
    }

    companion object {
        fun key(key: String) = LocalizedText(key = key)
        fun literal(value: String) = LocalizedText(literal = value)

        fun bilingual(english: String, chinese: String): LocalizedText = LocalizedText(
            literal = english,
            translations = mapOf("zh" to chinese),
        )

        private fun normalizeLocale(locale: String): String = locale.trim().lowercase().replace('_', '-')
    }
}

private val RESOURCE_ID_PATTERN = Regex("[A-Za-z0-9][A-Za-z0-9_.:/#-]*")
private val LOCALE_TAG_PATTERN = Regex("[A-Za-z][A-Za-z0-9_-]*")
