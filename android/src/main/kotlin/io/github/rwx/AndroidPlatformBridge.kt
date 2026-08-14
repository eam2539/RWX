package io.github.rwx

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.net.http.AndroidHttpClient
import android.os.Build
import com.corrodinggames.rts.gameFramework.audio.SoundEngine
import dalvik.system.DexClassLoader
import org.apache.http.client.HttpClient
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.util.jar.JarFile

class AndroidPlatformBridge(context: Context) : PlatformBridge {
    private val appContext: Context = context.applicationContext
    override val storage: AndroidPlatformStorage = AndroidPlatformStorage(this.appContext)
    override val appMetadata: AppMetadata = createAppMetadata(this.appContext)
    override val displayDensity: Float = this.appContext.resources.displayMetrics.density
    override val audio: PlatformAudio = AndroidPlatformAudio(appContext)
    override var filePickerHost: PlatformFilePickerHost?=null
    override val isMobilePlatform: Boolean = true

    override fun createModClassLoader(modFile: File, parent: ClassLoader): ClassLoader {
        val jarFingerprint = sha1Hex(modFile)
        val extractDir = File(File(this.appContext.filesDir, "mod-dex"), jarFingerprint)
        val optimizedDir = File(File(this.appContext.codeCacheDir, "mod-odex"), jarFingerprint)
        val dexFiles = extractDexFiles(modFile, extractDir)
        optimizedDir.mkdirs()
        return DexClassLoader(dexFiles.joinToString(File.pathSeparator), optimizedDir.absolutePath, null, parent)
    }

    override fun cleanupModClassLoaderCache(activeModFiles: Collection<File>) {
        val activeFingerprints = activeModFiles.mapNotNull { file ->
            runCatching { sha1Hex(file) }.getOrNull()
        }.toSet()
        pruneCacheDir(File(this.appContext.filesDir, "mod-dex"), activeFingerprints)
        pruneCacheDir(File(this.appContext.codeCacheDir, "mod-odex"), activeFingerprints)
    }

    private fun pruneCacheDir(root: File, keep: Set<String>) {
        root.listFiles()?.forEach { child ->
            if (child.isDirectory) {
                if (child.name !in keep) child.deleteRecursively()
            } else {
                child.delete()
            }
        }
    }

    private fun extractDexFiles(jarFile: File, extractDir: File): List<File> {
        val alreadyExtracted = extractDir.isDirectory &&
                extractDir.listFiles()?.any { it.isFile && it.name.endsWith(".dex") } == true
        if (!alreadyExtracted) {
            extractDir.mkdirs()
            JarFile(jarFile).use { jar ->
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    if (!entry.isDirectory && entry.name.startsWith("classes") && entry.name.endsWith(".dex")) {
                        val target = File(extractDir, entry.name.substringAfterLast('/'))
                        if (target.exists()) target.setWritable(true)
                        jar.getInputStream(entry).use { input ->
                            target.outputStream().use { output -> input.copyTo(output) }
                        }
                        // Android 14 (targetSdk 34+) rejects dex files that are still writable
                        // mark read-only before loading.
                        target.setReadOnly()
                    }
                }
            }
        }
        val dexFiles = extractDir.listFiles()
            ?.filter { it.isFile && it.name.endsWith(".dex") }
            ?.filter { it.length() > 0 }
            ?.sorted()
        return dexFiles?.takeIf { it.isNotEmpty() }
            ?: throw IOException("No classes.dex found in ${jarFile.name}")
    }

    private fun sha1Hex(file: File): String {
        val digest = MessageDigest.getInstance("SHA-1")
        file.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    @Suppress("DEPRECATION")
    override fun createMasterServerHttpClient(): HttpClient = AndroidHttpClient.newInstance(null)

    @Suppress("DEPRECATION")
    override fun closeMasterServerHttpClient(httpClient: HttpClient) {
        (httpClient as? AndroidHttpClient)?.close()
    }

    override fun openUrl(url: String): Boolean =
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
            true
        }.getOrDefault(false)

    override val preferenceStorage: PreferenceStorage = AndroidPreferenceStorage(appContext)
    override val logger: AppLogger = AndroidLogger()
    override val crashReporter: CrashReporter = FileCrashReporter.get(
        crashFile = storage.rootDir.resolve(CRASH_FILE_NAME),
        environment = androidCrashEnvironment(appContext, appMetadata),
    )

    @Suppress("DEPRECATION")
    private fun createAppMetadata(context: Context): AppMetadata {
        val packageName = context.packageName
        val packageManager = context.packageManager
        val packageInfo = runCatching { packageManager.getPackageInfo(packageName, 0) }.getOrNull()
        val installerPackageName = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                packageManager.getInstallSourceInfo(packageName).installingPackageName
            } else {
                packageManager.getInstallerPackageName(packageName)
            }
        }.getOrNull() ?: "android"
        val signature = runCatching {
            val signatureInfo =
                packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo
                    ?.apkContentsSigners
            signatureInfo?.firstOrNull()?.toByteArray()?.joinToString(separator = "") { byte ->
                "%02x".format(byte)
            }
        }.getOrNull()

        return AppMetadata(
            packageName = packageName,
            installerPackageName = installerPackageName,
            versionName = packageInfo?.versionName ?: AppMetadata.VERSION_NAME,
            versionCode = packageInfo?.versionCode ?: AppMetadata.COMPATIBLE_CORE_VERSION_CODE,
            signature = signature,
        )
    }

}

private class AndroidPlatformAudio(context: Context) : PlatformAudio {
    private data class Playback(
        val volume: Float,
        val pan: Float,
        val pitch: Float,
    )

    private val appContext = context.applicationContext
    private val lock = Any()
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(24)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
    private val soundIds = linkedMapOf<String, Int>()
    private val loadedSoundIds = mutableSetOf<Int>()
    private val pendingPlaybacks = linkedMapOf<Int, MutableList<Playback>>()
    private val cachedSoundFiles = linkedMapOf<String, File>()
    private val soundCacheDir = File(appContext.cacheDir, "sounds").apply(File::mkdirs)

    init {
        soundPool.setOnLoadCompleteListener { _, soundId, status ->
            val cachedFilesToDelete = mutableListOf<File>()
            val queuedPlaybacks = synchronized(lock) {
                if (status == 0) {
                    loadedSoundIds += soundId
                    pendingPlaybacks.remove(soundId).orEmpty()
                } else {
                    val failedIds = soundIds.filterValues { it == soundId }.keys.toList()
                    failedIds.forEach { id ->
                        soundIds.remove(id)
                        cachedSoundFiles.remove(id)?.let(cachedFilesToDelete::add)
                    }
                    pendingPlaybacks.remove(soundId)
                    emptyList()
                }
            }
            cachedFilesToDelete.forEach(File::delete)
            queuedPlaybacks.forEach { playback -> playLoaded(soundId, playback) }
        }
    }

    override fun registerSound(id: String, file: File) {
        synchronized(lock) {
            check(id !in soundIds) { "Sound is already registered: $id" }
            val soundId = soundPool.load(file.absolutePath, 1)
            check(soundId != 0) { "SoundPool failed to load: ${file.absolutePath}" }
            soundIds[id] = soundId
        }
    }

    override fun registerSound(id: String, data: ByteArray, fileName: String) {
        synchronized(lock) {
            check(id !in soundIds) { "Sound is already registered: $id" }
            val extension = fileName.substringAfterLast('.', "audio")
                .filter(Char::isLetterOrDigit)
                .ifBlank { "audio" }
            val safeId = id.map { char -> if (char.isLetterOrDigit()) char else '_' }.joinToString("")
            val cacheFile = File(soundCacheDir, "${safeId}_${id.hashCode().toString(16)}.$extension")
            cacheFile.writeBytes(data)
            val soundId = soundPool.load(cacheFile.absolutePath, 1)
            if (soundId == 0) {
                cacheFile.delete()
                error("SoundPool failed to load: $fileName")
            }
            cachedSoundFiles[id] = cacheFile
            soundIds[id] = soundId
        }
    }

    override fun registerBuiltInSound(id: String, name: String): Boolean = synchronized(lock) {
        if (id in soundIds) return@synchronized true
        val afd = BUILT_IN_SOUND_EXTENSIONS.firstNotNullOfOrNull { ext ->
            try {
                appContext.assets.openFd("sounds/$name.$ext")
            } catch (_: Exception) { null }
        } ?: return@synchronized false
        val soundId = soundPool.load(afd, 1)
        if (soundId == 0) return@synchronized false
        soundIds[id] = soundId
        true
    }

    override fun unregisterSound(id: String) {
        val (soundId, cacheFile) = synchronized(lock) {
            val removedSoundId = soundIds.remove(id)
            if (removedSoundId != null) {
                loadedSoundIds -= removedSoundId
                pendingPlaybacks.remove(removedSoundId)
            }
            removedSoundId to cachedSoundFiles.remove(id)
        }
        if (soundId != null) soundPool.unload(soundId)
        cacheFile?.delete()
    }

    override fun playSound(id: String, volume: Float, pan: Float, pitch: Float) {
        if (!SoundEngine.areGameSoundsEnabled()) return
        val playback = Playback(
            volume = volume.coerceIn(0f, 1f),
            pan = pan.coerceIn(-1f, 1f),
            pitch = pitch.coerceIn(0.5f, 2f),
        )
        val soundId: Int
        val isLoaded: Boolean
        synchronized(lock) {
            soundId = soundIds[id] ?: error("Sound is not registered: $id")
            isLoaded = soundId in loadedSoundIds
            if (!isLoaded) {
                val pending = pendingPlaybacks.getOrPut(soundId) { mutableListOf() }
                if (pending.size < MAX_PENDING_PLAYS_PER_SOUND) pending += playback
            }
        }
        if (isLoaded) playLoaded(soundId, playback)
    }

    private fun playLoaded(soundId: Int, playback: Playback) {
        if (!SoundEngine.areGameSoundsEnabled()) return
        val left = playback.volume * if (playback.pan > 0f) 1f - playback.pan else 1f
        val right = playback.volume * if (playback.pan < 0f) 1f + playback.pan else 1f
        soundPool.play(soundId, left, right, 1, 0, playback.pitch)
    }

    private companion object {
        const val MAX_PENDING_PLAYS_PER_SOUND = 4
        val BUILT_IN_SOUND_EXTENSIONS = listOf("ogg", "wav")
    }
}

internal fun androidCrashEnvironment(
    context: Context,
    appMetadata: AppMetadata? = null,
): Map<String, String> = linkedMapOf(
    "platform" to "Android",
    "app.package" to context.packageName,
    "app.versionName" to (appMetadata?.versionName ?: AppMetadata.VERSION_NAME),
    "app.versionCode" to (appMetadata?.versionCode ?: AppMetadata.COMPATIBLE_CORE_VERSION_CODE).toString(),
    "android.release" to Build.VERSION.RELEASE,
    "android.sdk" to Build.VERSION.SDK_INT.toString(),
    "device.manufacturer" to Build.MANUFACTURER,
    "device.model" to Build.MODEL,
)

class AndroidLogger : AppLogger {
    override fun debug(tag: String?, message: String) {
        tag?.let {
            Timber.tag(it).d(message)
        } ?: Timber.d(message)
    }

    override fun info(tag: String?, message: String) {
        tag?.let {
            Timber.tag(it).i(message)
        } ?: Timber.i(message)
    }

    override fun warn(tag: String?, message: String, throwable: Throwable?) {
        tag?.let {
            Timber.tag(it).w(throwable, message)
        } ?: Timber.w(throwable, message)
    }

    override fun error(tag: String?, message: String, throwable: Throwable?) {
        tag?.let {
            Timber.tag(it).e(throwable, message)
        } ?: Timber.e(throwable, message)
    }
}
