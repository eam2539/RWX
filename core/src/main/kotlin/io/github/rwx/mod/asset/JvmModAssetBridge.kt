package io.github.rwx.mod.asset

import com.corrodinggames.rts.gameFramework.mod.ModInfo
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream
import java.io.File
import java.util.*

object JvmModAssetBridge {
    private data class Mount(
        val sourceRoot: String,
        val store: JvmModAssetStore,
    )

    private val mounts = IdentityHashMap<ModInfo, Mount>()

    internal fun contains(modInfo: ModInfo): Boolean =mounts.containsKey(modInfo)
    internal fun mount(modInfo: ModInfo, sourceRoot: File, store: JvmModAssetStore) = synchronized(mounts) {
        mounts[modInfo] = Mount(normalizeFilePath(sourceRoot.absolutePath), store)
    }

    internal fun unmount(modInfo: ModInfo) = synchronized(mounts) {
        mounts.remove(modInfo)
        Unit
    }

    @JvmStatic
    fun openAsset(modInfo: ModInfo?, requestedPath: String?): AssetInputStream? {
        if (modInfo == null || requestedPath == null) return null
        val mount = synchronized(mounts) { mounts[modInfo] } ?: return null
        val requested = normalizeFilePath(requestedPath)
        return openAsset(mount, requested, requestedPath)
    }

    @JvmStatic
    fun openAsset(requestedPath: String?): AssetInputStream? {
        if (requestedPath == null) return null
        val requested = normalizeFilePath(requestedPath)
        val mount = synchronized(mounts) {
            mounts.values
                .filter { requested.startsWith(it.sourceRoot + "/") }
                .maxByOrNull { it.sourceRoot.length }
        } ?: return null
        return openAsset(mount, requested, requestedPath)
    }

    private fun openAsset(mount: Mount, requested: String, sourcePath: String): AssetInputStream? {
        val relative = when {
            requested == mount.sourceRoot -> return null
            requested.startsWith(mount.sourceRoot + "/") -> requested.removePrefix(mount.sourceRoot + "/")
            else -> return null
        }
        val input = mount.store.open(relative) ?: return null
        return AssetInputStream(input, sourcePath)
    }

    private fun normalizeFilePath(path: String): String = path.replace('\\', '/').trimEnd('/')
}
