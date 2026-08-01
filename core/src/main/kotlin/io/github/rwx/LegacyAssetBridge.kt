package io.github.rwx

import com.corrodinggames.rts.gameFramework.utility.AssetInputStream
import org.koin.mp.KoinPlatform.getKoin
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

object LegacyAssetBridge {
    private val assetRoot: File by lazy { findProjectAssetRoot() }

    @JvmStatic
    fun openAsset(path: String): AssetInputStream? {
        val normalized = path.trimAssetPath()
        val sourcePath = "assets/$normalized"
        val file = File(assetRoot, normalized)
        if (file.isFile) {
            return try {
                AssetInputStream(FileInputStream(file), sourcePath, normalized)
            } catch (_: FileNotFoundException) {
                null
            }
        }
        val stream = platformStorage()?.openAssetStream(normalized) ?: return null
        return AssetInputStream(stream, sourcePath, normalized)
    }

    @JvmStatic
    fun listAssets(prefix: String): Array<String> {
        val normalized = prefix.trimAssetPath()
        val platformAssets = runCatching { platformStorage()?.listAssets(normalized) }
            .getOrNull()
        if (!platformAssets.isNullOrEmpty()) {
            return platformAssets
                .map { it.substringAfterLast('/') }
                .filter { it.isNotEmpty() }
                .sorted()
                .toTypedArray()
        }
        val directory = File(assetRoot, normalized)
        return directory.listFiles()
            ?.map { it.name }
            ?.sorted()
            ?.toTypedArray()
            ?: emptyArray()
    }

    @JvmStatic
    fun assetExists(path: String): Boolean {
        val normalized = path.trimAssetPath()
        if (File(assetRoot, normalized).isFile) {
            return true
        }
        return platformStorage()?.assetExists(normalized) == true
    }

    @JvmStatic
    fun assetLength(path: String): Long {
        val normalized = path.trimAssetPath()
        val file = File(assetRoot, normalized)
        if (file.isFile) {
            return file.length()
        }
        return platformStorage()?.readAssetBytes(normalized)?.size?.toLong() ?: -1L
    }

    @JvmStatic
    fun localDir(): File =
        (platformStorage()?.localDir?.file ?: File(System.getProperty("user.dir"), "local")).also { it.mkdirs() }

    @JvmStatic
    fun storageRootDir(): File =
        (platformStorage()?.rootDir?.file ?: localDir()).also { it.mkdirs() }

    @JvmStatic
    fun cacheDir(): File =
        (platformStorage()?.cacheDir?.file ?: File(localDir(), "cache")).also { it.mkdirs() }

    private fun platformStorage(): PlatformStorage? =
        runCatching { getKoin().get<PlatformStorage>() }.getOrNull()

    private fun findProjectAssetRoot(): File {
        val cwd = File(System.getProperty("user.dir")).absoluteFile
        return generateSequence(cwd) { it.parentFile }
            .map { File(it, "assets") }
            .firstOrNull(File::isDirectory)
            ?: File(cwd, "assets")
    }
}
