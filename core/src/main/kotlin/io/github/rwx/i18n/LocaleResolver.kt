package io.github.rwx.i18n

import java.util.*

object LocaleResolver {
    enum class Source {
        USER_PREFERENCE,
        OS_ENVIRONMENT,
        SYSTEM_PROPERTIES,
        JVM_DEFAULT,
    }

    data class Resolution(
        val locale: Locale,
        val source: Source,
    )

    fun resolve(
        preferredLocale: Locale?,
        supportedLocales: List<Locale> = I18n.supportedLocales,
    ): Resolution {
        preferredLocale?.let {
            matchSupportedLocale(preferredLocale, supportedLocales)?.let { locale ->
                return Resolution(
                    locale = locale,
                    source = Source.USER_PREFERENCE
                )
            }
        }


        return resolveSystemLocale()
    }

    fun resolveSystemLocale(): Resolution {
        val supportedLocales = I18n.supportedLocales
        run {
            val env = System.getenv()
            val rawValue = env["LC_ALL"] ?: env["LANG"] ?: return@run
            val normalized = normalizeLocaleToken(rawValue) ?: return@run
            matchSupportedLocale(Locale.forLanguageTag(normalized), supportedLocales)?.let { locale ->
                return Resolution(
                    locale = locale,
                    source = Source.OS_ENVIRONMENT
                )
            }
        }

        propertyLocale(System.getProperty("user.language"), System.getProperty("user.country"))?.let { locale ->
            matchSupportedLocale(locale, supportedLocales)?.let { locale ->
                return Resolution(
                    locale = locale,
                    source = Source.SYSTEM_PROPERTIES
                )
            }
        }
        return Resolution(
            locale = matchSupportedLocale(Locale.getDefault(), supportedLocales) ?: Locale.ENGLISH,
            source = Source.JVM_DEFAULT
        )
    }

    internal fun matchSupportedLocale(
        locale: Locale,
        supportedLocales: List<Locale> = I18n.supportedLocales,
    ): Locale? {
        if (locale.language.isBlank()) {
            return null
        }

        val exactTag = locale.toLanguageTag()
        supportedLocales.firstOrNull { supported ->
            supported.toLanguageTag().equals(exactTag, ignoreCase = true)
        }?.let { return it }

        return supportedLocales.firstOrNull { supported ->
            supported.language.equals(locale.language, ignoreCase = true)
        }
    }

    private fun normalizeLocaleToken(rawToken: String): String? {
        val stripped = rawToken
            .substringBefore('.')
            .substringBefore('@')
            .trim()

        if (stripped.isBlank()) {
            return null
        }

        if (stripped.equals("C", ignoreCase = true) || stripped.equals("POSIX", ignoreCase = true)) {
            return Locale.ENGLISH.toLanguageTag()
        }

        return stripped.replace('_', '-')
    }

    private fun propertyLocale(language: String?, country: String?): Locale? {
        if (language.isNullOrBlank()) {
            return null
        }

        return runCatching {
            Locale.Builder()
                .setLanguage(language.trim())
                .apply {
                    if (!country.isNullOrBlank()) {
                        setRegion(country.trim())
                    }
                }
                .build()
        }.getOrNull()
    }

}
