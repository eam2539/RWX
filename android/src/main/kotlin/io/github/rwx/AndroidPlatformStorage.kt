package io.github.rwx

import android.content.Context
import com.corrodinggames.rts.gameFramework.file.FileHelper
import java.io.File
import java.io.InputStream

class AndroidPlatformStorage(context: Context) : PlatformStorage {
    private val appContext: Context = context.applicationContext
    private val assets = appContext.assets
    private val externalRoot: File = appContext.getExternalFilesDir(null)!!
    override val rootDir: StorageLocation = location("/SD/rustedWarfare/", this.externalRoot)
    override val localDir: StorageLocation = location("/LOCAL/", appContext.filesDir)
    override val cacheDir: StorageLocation = location("/LOCAL/cache/", appContext.externalCacheDir!!)
    override val unitsDir: StorageLocation = location("/SD/rustedWarfare/units/", File(this.externalRoot, "units"))
    override val modsDir: StorageLocation = this.unitsDir
    override val mapsDir: StorageLocation = location("/SD/rustedWarfare/maps/", File(this.externalRoot, "maps"))
    override val savesDir: StorageLocation = location("/SD/rustedWarfare/saves/", File(this.externalRoot, "saves"))
    override val replaysDir: StorageLocation =
        location("/SD/rustedWarfare/replays/", File(this.externalRoot, "replays"))

    override fun location(kind: StorageKind): StorageLocation {
        return when (kind) {
            StorageKind.ROOT -> this.rootDir
            StorageKind.LOCAL -> this.localDir
            StorageKind.CACHE -> this.cacheDir
            StorageKind.MODS -> this.modsDir
            StorageKind.UNITS -> this.unitsDir
            StorageKind.MAPS -> this.mapsDir
            StorageKind.SAVES -> this.savesDir
            StorageKind.REPLAYS -> this.replaysDir
        }
    }


    override fun listAssets(prefix: String): List<String> {
        val normalized = prefix.trimAssetPath()
        return runCatching {
            assets.list(normalized)
                ?.map { child -> "$normalized/$child".trimAssetPath() }
                ?.sorted()
                ?: resolveVirtualPath(normalized)?.list()?.toList()?.sorted()
                ?: safList(normalized)?.map { child -> "$normalized/$child".trimAssetPath() }?.sorted()
                ?: emptyList()
        }.getOrElse { emptyList() }
    }

    override fun assetFileExists(path: String): Boolean {
        val normalized = path.trimAssetPath()
        return runCatching {
            assets.open(normalized).use { }
            true
        }.getOrElse {
            resolveVirtualPath(normalized)?.isFile == true || safExists(normalized)
        }
    }

    override fun readAssetBytes(path: String): ByteArray? {
        val normalized = path.trimAssetPath()
        return runCatching {
            assets.open(normalized).use { it.readBytes() }
        }.getOrElse {
            resolveVirtualPath(normalized)?.takeIf(File::isFile)?.readBytes()
                ?: safOpen(normalized)?.readBytes()
        }
    }

    override fun openAssetStream(path: String): InputStream? {
        val normalized = path.trimAssetPath()
        return runCatching {
            assets.open(normalized)
        }.getOrElse {
            resolveVirtualPath(normalized)?.takeIf(File::isFile)?.inputStream()
                ?: safOpen(normalized)
        }
    }

    override fun createDirectories() {
        this.rootDir.file.mkdirs()
        this.localDir.file.mkdirs()
        this.cacheDir.file.mkdirs()
        this.unitsDir.file.mkdirs()
        this.mapsDir.file.mkdirs()
        this.savesDir.file.mkdirs()
        this.replaysDir.file.mkdirs()
    }

    private fun safPath(normalized: String): String? = runCatching {
        FileHelper.convertAbstractPath(normalized)
    }.getOrNull()?.takeIf { it.contains(SAF_LINK_SUFFIX) }

    private fun safExists(normalized: String): Boolean =
        safPath(normalized)?.let(SafPlatformBridge::exists) == true

    private fun safList(normalized: String): List<String>? =
        safPath(normalized)?.let(SafPlatformBridge::list)?.toList()

    private fun safOpen(normalized: String): InputStream? =
        safPath(normalized)?.let(SafPlatformBridge::openInput)

    private companion object {
        const val SAF_LINK_SUFFIX = ".[saflink]"
    }
}
