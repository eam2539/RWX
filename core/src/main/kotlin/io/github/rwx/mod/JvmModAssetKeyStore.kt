package io.github.rwx.mod

import io.github.rwx.PlatformStorage
import io.github.rwx.mod.assets.AssetKeyFiles
import io.github.rwx.mod.assets.AssetKeyKind
import io.github.rwx.mod.assets.AssetPrivateKey
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class JvmModAssetKeyStore(storage: PlatformStorage) {
    private val directory = storage.localDir.resolve(KEY_DIRECTORY).also(File::mkdirs)
    private val cache = ConcurrentHashMap<String, AssetPrivateKey>()

    data class ImportResult(
        val keyId: String,
        val kind: AssetKeyKind,
        val destination: File,
    )

    fun importKey(source: File): ImportResult {
        require(source.isFile) { "asset key file does not exist: $source" }
        val key = AssetKeyFiles.readPrivate(source)
        val destination = directory.resolve("${key.keyId}.rwxkey")
        AssetKeyFiles.writePrivate(destination, key)
        cache[key.keyId] = key
        return ImportResult(key.keyId, key.kind, destination)
    }

    fun find(keyId: String): AssetPrivateKey? {
        if (!KEY_ID.matches(keyId)) return null
        cache[keyId]?.let { return it }
        val file = directory.resolve("$keyId.rwxkey")
        if (!file.isFile) return null
        return runCatching { AssetKeyFiles.readPrivate(file) }
            .getOrNull()
            ?.also { cache[keyId] = it }
    }

    companion object {
        private const val KEY_DIRECTORY = "jvm-mod-asset-keys"
        private val KEY_ID = Regex("[0-9a-f]{64}")
    }
}
