package com.corrodinggames.rts.gameFramework

import java.io.File
import java.nio.charset.Charset

class DesktopPlatformBridge : PlatformBridge {
    override val storage: PlatformStorage = DesktopPlatformStorage()

    override fun isMobilePlatform(): Boolean = false

    override fun createModClassLoader(modFile: File, parent: ClassLoader): ClassLoader {
        return java.net.URLClassLoader(arrayOf(modFile.toURI().toURL()), parent)
    }
}

class DesktopPlatformStorage(
    private val baseDir: File = File(System.getProperty("user.dir")),
    private val localBaseDir: File = defaultLocalBaseDir(),
) : PlatformStorage {
    override val rootDir: StorageLocation = location("/SD/rustedWarfare/", baseDir)
    override val localDir: StorageLocation = location("/LOCAL/", localBaseDir)
    override val cacheDir: StorageLocation = location("/LOCAL/cache/", File(localBaseDir, "cache"))
    override val modsDir: StorageLocation = location("/SD/mods/", File(baseDir, "mods"))
    override val unitsDir: StorageLocation = location("/SD/rustedWarfare/units/", File(baseDir, "units"))
    override val mapsDir: StorageLocation = location("/SD/rustedWarfare/maps/", File(baseDir, "maps"))
    override val savesDir: StorageLocation = location("/SD/rustedWarfare/saves/", File(baseDir, "saves"))
    override val replaysDir: StorageLocation = location("/SD/rustedWarfare/replays/", File(baseDir, "replays"))
    override val screenshotsDir: StorageLocation =
        location("/SD/rustedWarfare/screenshots/", File(baseDir, "screenshots"))
    override val crashReportsFile: StorageLocation =
        StorageLocation("/SD/rustedWarfare/crashes.txt", DesktopPlatformFile(File(baseDir, "crashes.txt")))

    override fun location(kind: StorageKind): StorageLocation = when (kind) {
        StorageKind.ROOT -> rootDir
        StorageKind.LOCAL -> localDir
        StorageKind.CACHE -> cacheDir
        StorageKind.MODS -> modsDir
        StorageKind.UNITS -> unitsDir
        StorageKind.MAPS -> mapsDir
        StorageKind.SAVES -> savesDir
        StorageKind.REPLAYS -> replaysDir
        StorageKind.SCREENSHOTS -> screenshotsDir
        StorageKind.CRASH_REPORTS -> crashReportsFile
    }

    override fun resolveVirtualPath(virtualPath: String): PlatformFile {
        val normalized = virtualPath.replace('\\', '/')
        return when {
            normalized == rootDir.virtualPath.trimEnd('/') || normalized == rootDir.virtualPath -> rootDir.file
            normalized == localDir.virtualPath.trimEnd('/') || normalized == localDir.virtualPath -> localDir.file
            normalized.startsWith(rootDir.virtualPath) -> rootDir.file.resolve(normalized.substring(rootDir.virtualPath.length))
            normalized.startsWith("/SD/") -> rootDir.file.resolve(normalized.substring("/SD/".length))
            normalized.startsWith(localDir.virtualPath) -> localDir.file.resolve(normalized.substring(localDir.virtualPath.length))
            else -> DesktopPlatformFile(File(normalized))
        }
    }

    override fun createDirectories() {
        listOf(
            rootDir,
            localDir,
            cacheDir,
            modsDir,
            unitsDir,
            mapsDir,
            savesDir,
            replaysDir,
            screenshotsDir,
        ).forEach { it.file.mkdirs() }
    }

    private fun location(virtualPath: String, file: File): StorageLocation {
        return StorageLocation(virtualPath, DesktopPlatformFile(file.absoluteFile))
    }

    companion object {
        private fun defaultLocalBaseDir(): File {
            val os = System.getProperty("os.name", "").lowercase()
            val appDirName = "RWX"
            return when {
                os.contains("win") -> File(System.getenv("APPDATA") ?: System.getProperty("user.home"), appDirName)
                os.contains("mac") -> File(System.getProperty("user.home"), "Library/Application Support/$appDirName")
                else -> File(
                    System.getenv("XDG_DATA_HOME") ?: File(
                        System.getProperty("user.home"),
                        ".local/share"
                    ).path, appDirName
                )
            }
        }
    }
}

class DesktopPlatformFile(
    val javaFile: File,
) : PlatformFile {
    override val path: String get() = javaFile.path
    override val name: String get() = javaFile.name
    override val parent: PlatformFile? get() = javaFile.parentFile?.let(::DesktopPlatformFile)

    override fun exists(): Boolean = javaFile.exists()

    override fun isDirectory(): Boolean = javaFile.isDirectory

    override fun mkdirs(): Boolean = javaFile.mkdirs()

    override fun resolve(relativePath: String): PlatformFile {
        return DesktopPlatformFile(javaFile.resolve(relativePath))
    }

    override fun writeText(text: String, charset: Charset) {
        javaFile.parentFile?.mkdirs()
        javaFile.writeText(text, charset)
    }

    override fun appendText(text: String, charset: Charset) {
        javaFile.parentFile?.mkdirs()
        javaFile.appendText(text, charset)
    }
}
