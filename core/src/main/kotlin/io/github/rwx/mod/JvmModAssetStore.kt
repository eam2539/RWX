package io.github.rwx.mod

import io.github.rwx.mod.assets.AssetVaultFormat
import io.github.rwx.mod.assets.AssetKeyFiles
import io.github.rwx.mod.assets.AssetKeyKind
import io.github.rwx.mod.assets.AssetPrivateKey
import io.github.rwx.mod.assets.AssetProtectionMode
import io.github.rwx.mod.assets.AssetVaultHeader
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile

internal class JvmModAssetStore(
    jarFile: File,
    private val modId: String,
    private val keyResolver: (String) -> AssetPrivateKey? = { null },
) : Closeable {
    private val zip = ZipFile(jarFile)
    private val header: AssetVaultHeader? = loadHeader()
    private val encryptedAssets: Map<String, AssetVaultFormat.AssetRecord> = loadEncryptedIndex()
    private val plainAssets: Map<String, String> = zip.entries().asSequence()
        .filterNot { it.isDirectory }
        .map { it.name.replace('\\', '/') }
        .filter(::isPackagedAsset)
        .filterNot(encryptedAssets::containsKey)
        .associateWith { it }
    private val allPaths: Set<String> = plainAssets.keys + encryptedAssets.keys
    private val contentKey: ByteArray by lazy(::resolveContentKey)
    private var closed = false

    val encrypted: Boolean
        get() = encryptedAssets.isNotEmpty()

    internal fun requireAuthorized() {
        if (header != null) contentKey
    }

    fun open(path: String): InputStream? {
        check(!closed) { "Asset store is closed" }
        val normalized = normalizeAssetOrNull(path) ?: return null
        encryptedAssets[normalized]?.let { record ->
            val entry = zip.getEntry(record.blobEntry)
                ?: error("Missing encrypted asset blob ${record.blobEntry} for $normalized")
            val expectedEncryptedSize = record.plaintextSize + AssetVaultFormat.NONCE_SIZE + 16L
            require(entry.size == expectedEncryptedSize) {
                "Encrypted asset size mismatch for $normalized"
            }
            return AssetVaultFormat.decrypt(
                checkNotNull(header),
                contentKey,
                modId,
                normalized,
                zip.getInputStream(entry),
            )
        }
        val entryName = plainAssets[normalized] ?: return null
        return zip.getInputStream(zip.getEntry(entryName))
    }

    fun exists(path: String): Boolean {
        if (path.removePrefix("ROOT:").trim('/', '\\').isBlank()) return allPaths.isNotEmpty()
        if (path.replace('\\', '/').endsWith('/')) return directoryExists(path.trimEnd('/', '\\'))
        val normalized = normalizeAssetOrNull(path) ?: return false
        return normalized in allPaths || directoryExists(normalized)
    }

    fun list(path: String): List<String> {
        val directory = normalizeDirectoryOrNull(path) ?: return emptyList()
        return allPaths.asSequence()
            .filter { it.startsWith(directory) }
            .map { it.removePrefix(directory).substringBefore('/') }
            .filter(String::isNotBlank)
            .distinct()
            .sorted()
            .map { directory + it }
            .toList()
    }

    fun size(path: String): Long? {
        val normalized = normalizeAssetOrNull(path) ?: return null
        return encryptedAssets[normalized]?.plaintextSize
            ?: plainAssets[normalized]?.let(zip::getEntry)?.size
    }

    private fun directoryExists(path: String): Boolean {
        val directory = normalizeDirectoryOrNull(path) ?: return false
        return allPaths.any { it.startsWith(directory) }
    }

    private fun loadEncryptedIndex(): Map<String, AssetVaultFormat.AssetRecord> {
        val indexEntry = zip.getEntry(AssetVaultFormat.INDEX_ENTRY) ?: run {
            require(header == null) { "asset vault is missing its index" }
            return emptyMap()
        }
        require(header != null) { "asset vault index is missing its header" }
        return zip.getInputStream(indexEntry).use(AssetVaultFormat::readIndex)
            .onEach { record ->
                require(AssetVaultFormat.isProtectedAsset(record.path)) {
                    "Encrypted asset is outside assets/: ${record.path}"
                }
            }
            .associateBy(AssetVaultFormat.AssetRecord::path)
    }

    private fun loadHeader(): AssetVaultHeader? {
        val entry = zip.getEntry(AssetVaultFormat.HEADER_ENTRY) ?: return null
        return zip.getInputStream(entry).use(AssetVaultFormat::readHeader)
    }

    private fun resolveContentKey(): ByteArray {
        val header = checkNotNull(header)
        return when (header.mode) {
            AssetProtectionMode.SYMMETRIC_KEY -> {
                val key = resolveRequiredKey(header.keyId)
                require(key.kind == AssetKeyKind.SYMMETRIC) {
                    "Imported asset key ${header.keyId} is not symmetric"
                }
                key.encoded.copyOf()
            }

            AssetProtectionMode.RSA_PUBLIC_KEY -> {
                val key = resolveRequiredKey(header.keyId)
                require(key.kind == AssetKeyKind.RSA) {
                    "Imported asset key ${header.keyId} is not an RSA private key"
                }
                AssetKeyFiles.unwrapContentKey(key, header.wrappedContentKey)
            }
        }
    }

    private fun resolveRequiredKey(keyId: String): AssetPrivateKey {
        val key = keyResolver(keyId) ?: throw MissingJvmModAssetKeyException(keyId)
        AssetKeyFiles.validatePrivate(key)
        require(key.keyId == keyId) { "Imported asset key does not match requested key $keyId" }
        return key
    }

    private fun normalizeAssetOrNull(path: String): String? =
        runCatching { AssetVaultFormat.normalizeAssetPath(path) }
            .getOrNull()
            ?.takeIf(::isAssetNamespacePath)

    private fun normalizeDirectoryOrNull(path: String): String? {
        val value = path.removePrefix("ROOT:").replace('\\', '/').trim('/')
        if (value.isBlank()) return ""
        val normalized = runCatching { AssetVaultFormat.normalizeAssetPath(value) }.getOrNull()
            ?: return null
        if (normalized != ASSET_ROOT && !normalized.startsWith("$ASSET_ROOT/")) return null
        return normalized.trimEnd('/') + "/"
    }

    override fun close() {
        if (closed) return
        closed = true
        zip.close()
    }

    private companion object {
        const val ASSET_ROOT = "assets"

        fun isAssetPath(path: String): Boolean = path.startsWith("$ASSET_ROOT/")

        fun isAssetNamespacePath(path: String): Boolean = path == ASSET_ROOT || isAssetPath(path)

        fun isPackagedAsset(path: String): Boolean = isAssetPath(path)
    }
}

class MissingJvmModAssetKeyException(keyId: String) : IllegalStateException(
    "Missing JVM mod asset key $keyId; import the matching .rwxkey file into RWX"
)
