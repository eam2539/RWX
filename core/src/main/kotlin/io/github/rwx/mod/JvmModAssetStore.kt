package io.github.rwx.mod

import io.github.rwx.mod.assets.*
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipFile

internal class JvmModAssetStore(
    jarFile: File,
    private val modId: String,
    private val credentialResolver: AssetCredentialResolver = EmptyAssetCredentialResolver,
) : Closeable {
    private val zip = ZipFile(jarFile)
    private val header: AssetVaultHeader?
    private val encryptedAssets: Map<String, AssetVaultFormat.AssetRecord>
    private val plainAssets: Map<String, String>
    private val allPaths: Set<String>
    private val contentKey: ByteArray by lazy(::resolveContentKey)
    private var closed = false

    init {
        try {
            requireUniqueArchiveEntries()
            header = loadHeader()
            encryptedAssets = loadEncryptedIndex()
            plainAssets = zip.entries().asSequence()
                .filterNot { it.isDirectory }
                .map { it.name.replace('\\', '/') }
                .filter(::isPackagedAsset)
                .filterNot(encryptedAssets::containsKey)
                .associateWith { it }
            allPaths = plainAssets.keys + encryptedAssets.keys
            if (header != null) {
                require(encryptedAssets.isNotEmpty()) { "Encrypted asset vault is empty" }
                require(plainAssets.isEmpty()) {
                    "Encrypted asset vault contains unsigned plaintext assets: ${plainAssets.keys.first()}"
                }
            }
        } catch (error: Throwable) {
            zip.close()
            throw error
        }
    }

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
            require(header == null) { "Asset vault is missing its index" }
            return emptyMap()
        }
        val vaultHeader = requireNotNull(header) { "Asset vault index is missing its header" }
        val digest = MessageDigest.getInstance("SHA-256")
        val records = zip.getInputStream(indexEntry).use { input ->
            AssetVaultFormat.readIndex(DigestInputStream(input, digest))
        }
        require(MessageDigest.isEqual(vaultHeader.indexDigest, digest.digest())) {
            "Asset vault index digest mismatch"
        }
        return records
            .onEach { record ->
                require(AssetVaultFormat.isProtectedAsset(record.path)) {
                    "Encrypted asset is outside assets/: ${record.path}"
                }
                verifyEncryptedBlob(record)
            }
            .associateBy(AssetVaultFormat.AssetRecord::path)
    }

    private fun verifyEncryptedBlob(record: AssetVaultFormat.AssetRecord) {
        val entry = zip.getEntry(record.blobEntry)
            ?: error("Missing encrypted asset blob ${record.blobEntry} for ${record.path}")
        val expectedSize = record.plaintextSize + AssetVaultFormat.NONCE_SIZE + 16L
        require(entry.size == expectedSize) { "Encrypted asset size mismatch for ${record.path}" }
        val digest = MessageDigest.getInstance("SHA-256")
        zip.getInputStream(entry).use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        require(MessageDigest.isEqual(record.ciphertextDigest, digest.digest())) {
            "Encrypted asset digest mismatch for ${record.path}"
        }
    }

    private fun loadHeader(): AssetVaultHeader? {
        val entry = zip.getEntry(AssetVaultFormat.HEADER_ENTRY) ?: return null
        return zip.getInputStream(entry).use(AssetVaultFormat::readHeader)
    }

    private fun requireUniqueArchiveEntries() {
        val names = hashSetOf<String>()
        zip.entries().asSequence().forEach { entry ->
            require(names.add(entry.name)) { "Duplicate JAR entry: ${entry.name}" }
        }
    }

    private fun resolveContentKey(): ByteArray {
        val vaultHeader = checkNotNull(header)
        return when (vaultHeader.mode) {
            AssetProtectionMode.SYMMETRIC -> {
                if (vaultHeader.embeddedContentKey.isNotEmpty()) {
                    vaultHeader.embeddedContentKey.copyOf()
                } else {
                    resolveRequiredSymmetricKey(vaultHeader.keyId).encoded.copyOf()
                }
            }

            AssetProtectionMode.PKI -> resolvePkiContentKey(vaultHeader)
        }
    }

    private fun resolveRequiredSymmetricKey(keyId: String): AssetPrivateKey {
        val key = credentialResolver.findSymmetricKey(keyId)
            ?: throw MissingJvmModAssetKeyException(keyId)
        AssetKeyFiles.validatePrivate(key)
        require(key.kind == AssetKeyKind.SYMMETRIC) { "Imported asset key $keyId is not symmetric" }
        require(key.keyId == keyId) { "Imported asset key does not match requested key $keyId" }
        return key
    }

    private fun resolvePkiContentKey(vaultHeader: AssetVaultHeader): ByteArray {
        val authority = credentialResolver.findTrustedAuthority(vaultHeader.authorityId)
            ?: throw UntrustedJvmModAssetAuthorityException(vaultHeader.authorityId)
        AssetPki.validateAuthority(authority)
        require(authority.authorityId == vaultHeader.authorityId) {
            "Trusted asset authority does not match ${vaultHeader.authorityId}"
        }
        require(
            AssetPki.verifyPackage(
                authority,
                AssetVaultFormat.packageSignaturePayload(vaultHeader, modId),
                vaultHeader.signature,
            )
        ) { "Invalid asset package signature for authority ${vaultHeader.authorityId}" }

        val revocationList = selectRevocationList(vaultHeader, authority)
        var revokedCredential: AssetLicenseCredential? = null
        vaultHeader.recipients.forEach { recipient ->
            val credential = credentialResolver.findLicense(recipient.certificateId) ?: return@forEach
            AssetPki.validateCredential(
                credential,
                authority,
                enforceValidity = true,
            )
            require(credential.certificate.certificateId == recipient.certificateId) {
                "Imported asset license does not match recipient ${recipient.certificateId}"
            }
            require(credential.certificate.keyId == recipient.keyId) {
                "Asset recipient key does not match license ${recipient.certificateId}"
            }
            if (AssetPki.isRevoked(revocationList, recipient.certificateId)) {
                revokedCredential = credential
                return@forEach
            }
            return AssetKeyFiles.unwrapContentKey(
                AssetPrivateKey(AssetKeyKind.RSA, recipient.keyId, credential.privateKey),
                recipient.wrappedContentKey,
            )
        }
        revokedCredential?.let {
            throw RevokedJvmModAssetLicenseException(it.certificate.certificateId)
        }
        throw MissingJvmModAssetLicenseException(
            vaultHeader.authorityId,
            vaultHeader.recipients.map { it.certificateId },
        )
    }

    private fun selectRevocationList(
        vaultHeader: AssetVaultHeader,
        authority: io.github.rwx.mod.assets.AssetAuthorityCertificate,
    ): AssetRevocationList? {
        val embedded = vaultHeader.revocationList
        val local = credentialResolver.findRevocationList(authority.authorityId)
        listOfNotNull(embedded, local).forEach { list ->
            AssetPki.validateRevocationList(list, authority)
        }
        if (embedded != null && local != null && embedded.sequence == local.sequence) {
            require(
                AssetPkiFiles.encodeRevocationList(embedded)
                    .contentEquals(AssetPkiFiles.encodeRevocationList(local))
            ) { "Conflicting asset revocation lists at sequence ${embedded.sequence}" }
        }
        return listOfNotNull(embedded, local).maxByOrNull(AssetRevocationList::sequence)
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

class UntrustedJvmModAssetAuthorityException(authorityId: String) : IllegalStateException(
    "Untrusted JVM mod asset authority $authorityId; import the author's .rwxpub trust certificate into RWX"
)

class MissingJvmModAssetLicenseException(authorityId: String, recipientIds: List<String>) : IllegalStateException(
    "Missing JVM mod asset license for authority $authorityId; import an authorized .rwxlicense file " +
            "(${recipientIds.size} accepted recipient certificate(s))"
)

class RevokedJvmModAssetLicenseException(certificateId: String) : IllegalStateException(
    "JVM mod asset license $certificateId has been revoked"
)
