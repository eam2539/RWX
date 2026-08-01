package io.github.rwx

import android.content.Context
import java.io.File
import java.io.InputStream

class AndroidPlatformStorage(context: Context) : PlatformStorage {
    private val appContext: Context = context.applicationContext
    private val assets = appContext.assets
    private val externalRoot: File
    private val localRoot: File
    override val rootDir: StorageLocation
    override val localDir: StorageLocation
    override val cacheDir: StorageLocation
    override val modsDir: StorageLocation
    override val unitsDir: StorageLocation
    override val mapsDir: StorageLocation
    override val savesDir: StorageLocation
    override val replaysDir: StorageLocation

    init {
        val externalFilesDir = appContext.getExternalFilesDir(null)
        this.externalRoot = externalFilesDir ?: appContext.filesDir
        this.localRoot = appContext.filesDir
        this.rootDir = location("/SD/rustedWarfare/", this.externalRoot)
        this.localDir = location("/LOCAL/", this.localRoot)
        this.cacheDir = location("/LOCAL/cache/", appContext.externalCacheDir ?: appContext.cacheDir)
        this.unitsDir = location("/SD/rustedWarfare/units/", File(this.externalRoot, "units"))
        this.modsDir = this.unitsDir
        this.mapsDir = location("/SD/rustedWarfare/maps/", File(this.externalRoot, "maps"))
        this.savesDir = location("/SD/rustedWarfare/saves/", File(this.externalRoot, "saves"))
        this.replaysDir = location("/SD/rustedWarfare/replays/", File(this.externalRoot, "replays"))
    }

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

    override fun resolveVirtualPath(virtualPath: String): File {
        val normalized = virtualPath.replace('\\', '/')
        if (normalized == trimTrailingSlash(this.rootDir.virtualPath) || normalized == this.rootDir.virtualPath) {
            return this.rootDir.file
        }
        if (normalized == trimTrailingSlash(this.localDir.virtualPath) || normalized == this.localDir.virtualPath) {
            return this.localDir.file
        }
        if (normalized.startsWith(this.rootDir.virtualPath)) {
            return File(this.rootDir.file, normalized.substring(this.rootDir.virtualPath.length))
        }
        if (normalized.startsWith("/SD/")) {
            return File(this.rootDir.file, normalized.substring("/SD/".length))
        }
        if (normalized.startsWith(this.localDir.virtualPath)) {
            return File(this.localDir.file, normalized.substring(this.localDir.virtualPath.length))
        }
        return File(normalized)
    }

    override fun listAssets(prefix: String): List<String> {
        val normalized = prefix.trimAssetPath()
        return try {
            assets.list(normalized)
                ?.map { child -> "$normalized/$child".trimAssetPath() }
                ?.sorted()
                ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override fun assetExists(path: String): Boolean {
        val normalized = path.trimAssetPath()
        return try {
            assets.open(normalized).use { }
            true
        } catch (_: Exception) {
            false
        }
    }

    override fun readAssetBytes(path: String): ByteArray? {
        val normalized = path.trimAssetPath()
        return try {
            assets.open(normalized).use { it.readBytes() }
        } catch (_: Exception) {
            null
        }
    }

    override fun openAssetStream(path: String): InputStream? {
        val normalized = path.trimAssetPath()
        return try {
            assets.open(normalized)
        } catch (_: Exception) {
            null
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

    private fun trimTrailingSlash(path: String): String {
        if (path.endsWith("/") && path.length > 1) {
            return path.substring(0, path.length - 1)
        }
        return path
    }
}
