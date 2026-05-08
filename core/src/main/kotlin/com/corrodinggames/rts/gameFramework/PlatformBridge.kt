package com.corrodinggames.rts.gameFramework

import java.io.File
import java.nio.charset.Charset

interface PlatformBridge {
    val storage: PlatformStorage

    fun isMobilePlatform(): Boolean

    /**
     * Create an isolated ClassLoader for a mod JAR file.
     * Desktop: URLClassLoader over JVM .class files.
     * Android: DexClassLoader over classes.dex inside the JAR.
     */
    fun createModClassLoader(modFile: File, parent: ClassLoader): ClassLoader
}

interface PlatformStorage {
    val rootDir: StorageLocation
    val localDir: StorageLocation
    val cacheDir: StorageLocation
    val modsDir: StorageLocation
    val unitsDir: StorageLocation
    val mapsDir: StorageLocation
    val savesDir: StorageLocation
    val replaysDir: StorageLocation
    val screenshotsDir: StorageLocation
    val crashReportsFile: StorageLocation

    fun location(kind: StorageKind): StorageLocation
    fun resolveVirtualPath(virtualPath: String): PlatformFile
    fun createDirectories()
}

interface PlatformFile {
    val path: String
    val name: String
    val parent: PlatformFile?

    fun exists(): Boolean
    fun isDirectory(): Boolean
    fun mkdirs(): Boolean
    fun resolve(relativePath: String): PlatformFile
    fun writeText(text: String, charset: Charset = Charsets.UTF_8)
    fun appendText(text: String, charset: Charset = Charsets.UTF_8)
}

enum class StorageKind {
    ROOT,
    LOCAL,
    CACHE,
    MODS,
    UNITS,
    MAPS,
    SAVES,
    REPLAYS,
    SCREENSHOTS,
    CRASH_REPORTS,
}

data class StorageLocation(
    val virtualPath: String,
    val file: PlatformFile,
) {
    fun resolve(relativePath: String): PlatformFile = file.resolve(relativePath)
}
