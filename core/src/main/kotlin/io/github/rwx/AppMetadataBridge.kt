package io.github.rwx

import org.koin.mp.KoinPlatform.getKoin

object AppMetadataBridge {
    @JvmStatic
    fun packageName(): String = current().packageName

    @JvmStatic
    fun installerPackageName(): String = current().installerPackageName

    @JvmStatic
    fun compatibleCoreVersionCode(): Int = current().compatibleCoreVersionCode

    @JvmStatic
    fun signature(): String? = current().signature

    private fun current(): AppMetadata {
        return runCatching { getKoin().get<AppMetadata>() }.getOrDefault(AppMetadata())
    }
}
