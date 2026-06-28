package io.github.rwx.i18n

import io.github.rwx.PREFERENCE_NAME
import io.github.rwx.PreferenceStorage
import io.github.rwx.logger
import org.koin.mp.KoinPlatform.getKoin
import java.util.*

object LocaleSettings {
    private const val KEY_PREFERRED_LOCALE_TAG = "preferredLocaleTag"

    fun initialize(): LocaleResolver.Resolution {
        val resolution = LocaleResolver.resolve(
            preferredLocale = currentPreferenceLocale(),
        )
        applyLocale(resolution.locale)
        logger.info {
            "Initialized UI locale: $resolution"
        }
        return resolution
    }

    fun currentPreferenceLocale(): Locale? =
        getKoin().get<PreferenceStorage>().preference(PREFERENCE_NAME)
            .getString(KEY_PREFERRED_LOCALE_TAG, "")
            .orEmpty()
            .trim()
            .takeIf { it.isNotBlank() }
            ?.let {
                Locale.forLanguageTag(it)
            }

    fun updatePreferredLocale(preferredLocale: Locale): LocaleResolver.Resolution {
        val storage = getKoin().get<PreferenceStorage>()
        storage.preference(PREFERENCE_NAME)
            .putString(KEY_PREFERRED_LOCALE_TAG, preferredLocale.toLanguageTag())
        storage.flush()

        val resolution = LocaleResolver.resolve(
            preferredLocale = currentPreferenceLocale(),
        )
        applyLocale(resolution.locale)
        logger.info {
            "Updated UI locale: $resolution"
        }
        return resolution
    }

    fun applyLocale(locale: Locale) {
        Locale.setDefault(locale)
        I18n.setLocale(locale)
    }

}
