package io.github.rwx

import com.corrodinggames.rts.gameFramework.MusicManager
import com.corrodinggames.rts.gameFramework.audio.SoundEngine
import com.corrodinggames.rts.java.OpenALMusicFactory
import com.corrodinggames.rts.java.audio.Sound
import com.corrodinggames.rts.java.audio.lwjgl.OpenALAudio
import com.corrodinggames.rts.java.audio.util.AudioFile
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.jar.JarFile

internal fun ensureDesktopOpenAlMusicFactory() {
    synchronized(MusicManager::class.java) {
        if (MusicManager.musicFactory !is OpenALMusicFactory) {
            MusicManager.musicFactory = OpenALMusicFactory(OpenALAudio())
        }
    }
}

class DesktopPlatformBridge : PlatformBridge {
    override val storage: DesktopPlatformStorage = DesktopPlatformStorage()
    override val preferenceStorage: PreferenceStorage = TomlPreferenceStorage(
        storage.location("/SD/rustedWarfare/", File(storage.rootDir.file, "${PREFERENCE_NAME}.toml"))
    )
    override val appMetadata: AppMetadata = AppMetadata()
    override val logger: AppLogger = DesktopLogger()
    override val crashReporter: CrashReporter = FileCrashReporter.get(
        crashFile = storage.localDir.resolve(CRASH_FILE_NAME),
        environment = linkedMapOf(
            "platform" to "Desktop",
            "app.versionName" to appMetadata.versionName,
            "app.versionCode" to appMetadata.versionCode.toString(),
            "os.name" to System.getProperty("os.name", "unknown"),
            "os.version" to System.getProperty("os.version", "unknown"),
            "os.arch" to System.getProperty("os.arch", "unknown"),
            "java.version" to System.getProperty("java.version", "unknown"),
        ),
    )
    override val isMobilePlatform: Boolean = false
    override val audio: PlatformAudio = DesktopPlatformAudio(storage)
    override var filePickerHost: PlatformFilePickerHost?=null

    override fun createModClassLoader(modFile: File, parent: ClassLoader): ClassLoader {
        return URLClassLoader(arrayOf(modFile.toURI().toURL()), parent)
    }

    override fun openUrl(url: String): Boolean =
        runCatching {
            if (!Desktop.isDesktopSupported()) return false
            val desktop = Desktop.getDesktop()
            if (!desktop.isSupported(Desktop.Action.BROWSE)) return false
            desktop.browse(URI(url))
            true
        }.getOrDefault(false)


}

private class DesktopPlatformAudio(
    private val storage: DesktopPlatformStorage,
) : PlatformAudio {
    private val sounds = linkedMapOf<String, Sound>()

    override fun registerSound(id: String, file: File) {
        check(id !in sounds) { "Sound is already registered: $id" }
        registerSound(id, AudioFile(file.absolutePath))
    }

    override fun registerSound(id: String, data: ByteArray, fileName: String) {
        check(id !in sounds) { "Sound is already registered: $id" }
        registerSound(id, AudioFile(data.inputStream(), fileName))
    }

    override fun registerBuiltInSound(id: String, name: String): Boolean {
        if (id in sounds) return true
        BUILT_IN_SOUND_EXTENSIONS.forEach { extension ->
            val fileName = "$name.$extension"
            storage.openAssetStream("sounds/$fileName")?.use { stream ->
                registerSound(id, stream.readBytes(), fileName)
                return true
            }
        }
        return false
    }

    override fun unregisterSound(id: String) {
        val sound = sounds.remove(id) ?: return
        val factory = MusicManager.musicFactory as? OpenALMusicFactory
        if (factory != null) {
            synchronized(factory.f()) { sound.dispose() }
        } else {
            sound.dispose()
        }
    }

    override fun playSound(id: String, volume: Float, pan: Float, pitch: Float) {
        if (!SoundEngine.areGameSoundsEnabled()) return
        val sound = sounds[id] ?: error("Sound is not registered: $id")
        val factory = MusicManager.musicFactory as? OpenALMusicFactory
            ?: error("Desktop OpenAL audio is not initialized")
        synchronized(factory.f()) {
            sound.play(volume.coerceIn(0f, 1f), pitch.coerceIn(0.5f, 2f), pan.coerceIn(-1f, 1f))
        }
    }

    private fun registerSound(id: String, audioFile: AudioFile) {
        val factory = MusicManager.musicFactory as? OpenALMusicFactory
            ?: error("Desktop OpenAL audio is not initialized")
        sounds[id] = synchronized(factory.f()) {
            factory.b.newSound(audioFile)
        }
    }

    private companion object {
        val BUILT_IN_SOUND_EXTENSIONS = listOf("ogg", "wav")
    }
}

class DesktopPlatformStorage : PlatformStorage {
    val baseDir: File = defaultProjectRoot()
    private val assetRoot: File = resolveAssetRoot()
    val localBaseDir: File = defaultLocalBaseDir()
    override val rootDir: StorageLocation = location("/SD/rustedWarfare/", baseDir)
    override val localDir: StorageLocation = location("/LOCAL/", localBaseDir)
    override val cacheDir: StorageLocation = location("/LOCAL/cache/", File(localBaseDir, "cache"))
    override val modsDir: StorageLocation = location("/SD/mods/", File(baseDir, "mods"))
    override val unitsDir: StorageLocation = location("/SD/rustedWarfare/units/", File(baseDir, "mods/units"))
    override val mapsDir: StorageLocation = location("/SD/rustedWarfare/maps/", File(baseDir, "mods/maps"))
    override val savesDir: StorageLocation = location("/SD/rustedWarfare/saves/", File(baseDir, "saves"))
    override val replaysDir: StorageLocation = location("/SD/rustedWarfare/replays/", File(baseDir, "replays"))

    override fun location(kind: StorageKind): StorageLocation = when (kind) {
        StorageKind.ROOT -> rootDir
        StorageKind.LOCAL -> localDir
        StorageKind.CACHE -> cacheDir
        StorageKind.MODS -> modsDir
        StorageKind.UNITS -> unitsDir
        StorageKind.MAPS -> mapsDir
        StorageKind.SAVES -> savesDir
        StorageKind.REPLAYS -> replaysDir
    }

    override fun resolveVirtualPath(virtualPath: String): File {
        val normalized = virtualPath.replace('\\', '/')
        val resolved = listOf(
            cacheDir,
            mapsDir,
            unitsDir,
            savesDir,
            replaysDir,
            modsDir,
            localDir,
            rootDir,
        ).firstNotNullOfOrNull { location ->
            location.resolveVirtualPathOrNull(normalized)
        }
        return resolved ?: when {
            normalized.startsWith("/SD/") -> File(rootDir.file, normalized.substring("/SD/".length))
            else -> File(normalized)
        }
    }

    override fun listAssets(prefix: String): List<String> {
        val normalized = prefix.trimAssetPath()
        val dir = File(assetRoot, normalized)
        return dir.listFiles()
            ?.map { file -> "$normalized/${file.name}".trimAssetPath() }
            ?.sorted()
            ?: emptyList()
    }

    override fun assetExists(path: String): Boolean {
        return File(assetRoot, path.trimAssetPath()).isFile
    }

    override fun readAssetBytes(path: String): ByteArray? {
        return File(assetRoot, path.trimAssetPath()).takeIf(File::isFile)?.readBytes()
    }

    override fun openAssetStream(path: String): InputStream? {
        return File(assetRoot, path.trimAssetPath()).takeIf(File::isFile)?.inputStream()
    }

    override fun assetLoadPath(path: String): String = path.trimAssetPath()

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
        ).forEach { it.file.mkdirs() }
    }


    companion object {
        private const val ASSETS_RESOURCE_PREFIX = "assets/"

        private val resolvedAssetRoot: File by lazy {
            configuredAssetRoot()
                ?: externalAssetRoot()
                ?: extractBundledAssets()
                ?: File(defaultProjectRoot(), "assets")
        }

        internal fun resolveAssetRoot(): File = resolvedAssetRoot

        internal fun defaultLocalBaseDir(): File {
            val os = System.getProperty("os.name", "").lowercase()
            val appDirName = "rwx"
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

        private fun defaultProjectRoot(): File {
            val cwd = File(System.getProperty("user.dir")).absoluteFile
            return generateSequence(cwd) { it.parentFile }
                .firstOrNull { candidate ->
                    File(candidate, "assets").isDirectory &&
                            (File(candidate, "settings.gradle.kts").isFile || File(
                                candidate,
                                "build.gradle.kts"
                            ).isFile)
                }
                ?: cwd
        }

        private fun configuredAssetRoot(): File? =
            (System.getProperty("rwx.assetsDir") ?: System.getenv("RWX_ASSETS_DIR"))
                ?.trim()
                ?.takeIf(String::isNotBlank)
                ?.let(::File)
                ?.takeIf(File::isDirectory)

        private fun externalAssetRoot(): File? {
            codeSourceFile()?.let { source ->
                if (source.isFile) {
                    listOfNotNull(
                        File(source.parentFile, "assets"),
                        source.parentFile?.parentFile?.let { File(it, "assets") },
                    ).firstOrNull(File::isDirectory)?.let { return it }
                } else if (source.isDirectory) {
                    findAssetsFrom(source)?.let { return it }
                }
            }
            return findAssetsFrom(defaultProjectRoot())
        }

        private fun findAssetsFrom(start: File): File? =
            generateSequence(start.absoluteFile) { it.parentFile }
                .map { File(it, "assets") }
                .firstOrNull { assets ->
                    assets.isDirectory &&
                            (File(assets, "font/NotoSansCJKsc-Regular.json").isFile ||
                                    File(assets.parentFile, "settings.gradle.kts").isFile)
                }

        private fun extractBundledAssets(): File? {
            val source = codeSourceFile()?.takeIf(File::isFile) ?: return null
            val targetAssetsDir = File(
                System.getProperty("java.io.tmpdir"),
                "rwx-assets-${bundleId(source)}/assets",
            )
            JarFile(source).use { jar ->
                val entries = jar.entries().asSequence()
                    .filter { !it.isDirectory && it.name.startsWith(ASSETS_RESOURCE_PREFIX) }
                    .toList()
                if (entries.isEmpty()) return null

                targetAssetsDir.mkdirs()
                entries.forEach { entry ->
                    val relativePath = entry.name.removePrefix(ASSETS_RESOURCE_PREFIX)
                    if (relativePath.isBlank()) return@forEach
                    val outputFile = File(targetAssetsDir, relativePath)
                    outputFile.parentFile.mkdirs()
                    jar.getInputStream(entry).use { input ->
                        Files.copy(input, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    }
                    outputFile.setReadable(true, false)
                }
            }
            return targetAssetsDir
        }

        private fun codeSourceFile(): File? =
            DesktopPlatformStorage::class.java.protectionDomain.codeSource
                ?.location
                ?.let { runCatching { File(it.toURI()) }.getOrNull() }

        private fun bundleId(source: File): String =
            "${source.nameWithoutExtension}-${source.length()}-${source.lastModified()}"
                .replace(Regex("[^A-Za-z0-9._-]"), "_")
    }
}

internal fun configureDesktopLogging() {
    fun configure(property: String, environment: String, defaultValue: String) {
        if (System.getProperty(property).isNullOrBlank()) {
            System.setProperty(property, System.getenv(environment)?.takeIf(String::isNotBlank) ?: defaultValue)
        }
    }

    configure(
        property = "rwx.log.dir",
        environment = "RWX_LOG_DIR",
        defaultValue = File(DesktopPlatformStorage.defaultLocalBaseDir(), "logs").absolutePath,
    )
    configure("rwx.log.level", "RWX_LOG_LEVEL", "INFO")
    configure("rwx.log.consoleLevel", "RWX_CONSOLE_LOG_LEVEL", "INFO")
    configure("rwx.log.thirdPartyLevel", "RWX_THIRD_PARTY_LOG_LEVEL", "WARN")
    configure("rwx.log.maxHistory", "RWX_LOG_MAX_HISTORY", "7")
    configure("rwx.log.maxFileSize", "RWX_LOG_MAX_FILE_SIZE", "20MB")
    configure("rwx.log.totalSizeCap", "RWX_LOG_TOTAL_SIZE_CAP", "200MB")
}

class DesktopLogger : AppLogger {
    val loggerName: String = "Desktop"
    override fun debug(tag: String?, message: String) {
        LoggerFactory.getLogger(tag ?: loggerName).debug(message)
    }

    override fun info(tag: String?, message: String) {
        LoggerFactory.getLogger(tag ?: loggerName).info(message)
    }

    override fun warn(tag: String?, message: String, throwable: Throwable?) {
        if (throwable != null) {
            LoggerFactory.getLogger(tag ?: loggerName).warn(message, throwable)
        } else {
            LoggerFactory.getLogger(tag ?: loggerName).warn(message)
        }
    }

    override fun error(tag: String?, message: String, throwable: Throwable?) {
        if (throwable != null) {
            LoggerFactory.getLogger(tag ?: loggerName).error(message, throwable)
        } else {
            LoggerFactory.getLogger(tag ?: loggerName).error(message)
        }
    }
}
