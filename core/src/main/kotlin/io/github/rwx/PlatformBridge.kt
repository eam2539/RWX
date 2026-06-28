package io.github.rwx

import java.io.File
import java.io.InputStream

interface PlatformBridge {
    val storage: PlatformStorage
    val preferenceStorage: PreferenceStorage
    val appMetadata: AppMetadata
    val logger: AppLogger
    val crashReporter: CrashReporter
    val isMobilePlatform: Boolean
    val displayDensity: Float
        get() = 1f
    val audio: PlatformAudio
        get() = NoopPlatformAudio

    /**
     * Create an isolated ClassLoader for a mod JAR file.
     * Desktop: URLClassLoader over JVM .class files.
     * Android: DexClassLoader over classes.dex inside the JAR.
     */
    fun createModClassLoader(modFile: File, parent: ClassLoader): ClassLoader

    fun openUrl(url: String): Boolean = false
}

interface PlatformAudio {
    fun registerSound(id: String, file: File)
    fun registerSound(id: String, data: ByteArray, fileName: String)
    fun registerBuiltInSound(id: String, name: String): Boolean
    fun unregisterSound(id: String)
    fun playSound(id: String, volume: Float, pan: Float = 0f, pitch: Float = 1f)
}

object NoopPlatformAudio : PlatformAudio {
    override fun registerSound(id: String, file: File) = Unit
    override fun registerSound(id: String, data: ByteArray, fileName: String) = Unit
    override fun registerBuiltInSound(id: String, name: String): Boolean = false
    override fun unregisterSound(id: String) = Unit
    override fun playSound(id: String, volume: Float, pan: Float, pitch: Float) = Unit
}

data class AppMetadata(
    val packageName: String = DEFAULT_PACKAGE_NAME,
    val installerPackageName: String = DEFAULT_INSTALLER_PACKAGE_NAME,
    val versionName: String = DEFAULT_VERSION_NAME,
    val versionCode: Int = DEFAULT_VERSION_CODE,
    val signature: String? = null,
) {
    companion object {
        const val DEFAULT_PACKAGE_NAME = "com.corrodinggames.rts.gdx"
        const val DEFAULT_INSTALLER_PACKAGE_NAME = "java-gdx"
        const val DEFAULT_VERSION_NAME = "1.0.0"
        const val DEFAULT_VERSION_CODE = 176
    }
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

    fun location(kind: StorageKind): StorageLocation
    fun location(virtualPath: String, file: File): StorageLocation {
        return StorageLocation(virtualPath, file.absoluteFile)
    }

    fun resolveVirtualPath(virtualPath: String): File
    fun listAssets(prefix: String): List<String>
    fun assetExists(path: String): Boolean
    fun readAssetBytes(path: String): ByteArray? = null
    fun openAssetStream(path: String): InputStream? = readAssetBytes(path)?.inputStream()
    fun assetLoadPath(path: String): String = path.trimAssetPath()
    fun createDirectories()
}

fun String.trimAssetPath(): String = trim().replace('\\', '/').trim('/')

enum class StorageKind {
    ROOT,
    LOCAL,
    CACHE,
    MODS,
    UNITS,
    MAPS,
    SAVES,
    REPLAYS,
}

data class StorageLocation(
    val virtualPath: String,
    val file: File,
) {
    fun resolve(relativePath: String): File = File(file, relativePath)

    fun resolveVirtualPathOrNull(path: String): File? {
        val normalizedRoot = virtualPath.replace('\\', '/').trimEnd('/')
        val normalizedPath = path.replace('\\', '/')
        if (normalizedPath == normalizedRoot) {
            return file
        }
        val childPrefix = "$normalizedRoot/"
        if (!normalizedPath.startsWith(childPrefix)) {
            return null
        }
        return resolve(normalizedPath.substring(childPrefix.length))
    }
}

interface AppLogger {
    fun debug(tag: String?, message: String)
    fun info(tag: String?, message: String)
    fun warn(tag: String?, message: String, throwable: Throwable? = null)
    fun error(tag: String?, message: String, throwable: Throwable? = null)
}

interface CrashReporter {
    fun recordException(throwable: Throwable)
    fun log(message: String)
    fun setUserId(userId: String)
    fun setCustomKey(key: String, value: String)
}
