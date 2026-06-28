package io.github.rwx

import org.koin.mp.KoinPlatform.getKoin

object AppMetadataBridge {
    @JvmStatic
    fun packageName(): String = current().packageName

    @JvmStatic
    fun installerPackageName(): String = current().installerPackageName

    @JvmStatic
    fun versionName(): String = current().versionName

    @JvmStatic
    fun versionCode(): Int = current().versionCode

    @JvmStatic
    fun signature(): String? = current().signature

    private fun current(): AppMetadata {
        return runCatching { getKoin().get<AppMetadata>() }.getOrDefault(AppMetadata())
    }
}
