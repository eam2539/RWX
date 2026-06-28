package io.github.rwx.i18n

import java.text.MessageFormat
import java.util.*

class I18nText(
    val key: String,
    val translations: Map<String, String>,
    private val localeProvider: () -> Locale,
    private val fallbackLocaleTag: String,
) {
    fun text(locale: Locale = localeProvider()): String = resolve(locale)

    fun format(locale: Locale = localeProvider(), vararg args: Any?): String {
        val pattern = resolve(locale)
        return if (args.isEmpty()) {
            pattern
        } else {
            MessageFormat(pattern, locale).format(args)
        }
    }

    operator fun invoke(vararg args: Any?): String = format(localeProvider(), *args)

    override fun toString(): String = text()

    private fun resolve(locale: Locale): String {
        val exactTag = locale.toLanguageTag()
        translations[exactTag]?.let { return it }

        val normalizedExactTag = exactTag.lowercase()
        translations.entries.firstOrNull { it.key.lowercase() == normalizedExactTag }?.value?.let { return it }

        val language = locale.language
        if (language.isNotBlank()) {
            translations[language]?.let { return it }
            translations.entries.firstOrNull {
                it.key.equals(language, ignoreCase = true) || it.key.startsWith("$language-", ignoreCase = true)
            }?.value?.let { return it }
        }

        translations[fallbackLocaleTag]?.let { return it }
        return translations.values.firstOrNull().orEmpty()
    }
}

open class I18nNode(
    private val entry: I18nText? = null,
) {
    val key: String?
        get() = entry?.key

    val translations: Map<String, String>
        get() = entry?.translations.orEmpty()

    fun text(locale: Locale? = null): String =
        locale?.let { entry?.text(it) } ?: entry?.text().orEmpty()

    fun format(locale: Locale? = null, vararg args: Any?): String =
        locale?.let { entry?.format(it, *args) } ?: entry?.invoke(*args).orEmpty()

    operator fun invoke(vararg args: Any?): String = entry?.invoke(*args).orEmpty()

    override fun toString(): String = entry?.toString().orEmpty()
}
