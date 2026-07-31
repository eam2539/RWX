package io.github.rwx.mod

import io.github.rwx.PlatformStorage
import io.github.rwx.mod.assets.*
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class JvmModAssetKeyStore(storage: PlatformStorage) : AssetCredentialResolver {
    private val root = storage.localDir.resolve(CREDENTIAL_DIRECTORY).also(File::mkdirs)
    private val symmetricDirectory = root.resolve("symmetric").also(File::mkdirs)
    private val authorityDirectory = root.resolve("authorities").also(File::mkdirs)
    private val licenseDirectory = root.resolve("licenses").also(File::mkdirs)
    private val symmetricCache = ConcurrentHashMap<String, AssetPrivateKey>()
    private val authorityCache = ConcurrentHashMap<String, AssetAuthorityCertificate>()
    private val licenseCache = ConcurrentHashMap<String, AssetLicenseCredential>()

    enum class CredentialKind {
        SYMMETRIC_KEY,
        TRUSTED_AUTHORITY,
        LICENSE,
    }

    data class ImportResult(
        val id: String,
        val kind: CredentialKind,
        val destination: File,
    )

    @Synchronized
    fun importFile(source: File): ImportResult {
        require(source.isFile) { "Asset credential file does not exist: $source" }
        return when (source.extension.lowercase(Locale.ROOT)) {
            "rwxkey" -> importSymmetricKey(source)
            "rwxpub" -> importAuthority(source)
            "rwxlicense" -> importLicense(source)
            else -> error("Unsupported asset credential type: ${source.extension}")
        }
    }

    override fun findSymmetricKey(keyId: String): AssetPrivateKey? {
        if (!ID.matches(keyId)) return null
        symmetricCache[keyId]?.let { return it }
        val file = symmetricDirectory.resolve("$keyId.rwxkey")
        if (!file.isFile) return null
        return runCatching { AssetKeyFiles.readPrivate(file) }
            .getOrNull()
            ?.takeIf { it.kind == AssetKeyKind.SYMMETRIC && it.keyId == keyId }
            ?.also { symmetricCache[keyId] = it }
    }

    override fun findLicense(certificateId: String): AssetLicenseCredential? {
        if (!ID.matches(certificateId)) return null
        licenseCache[certificateId]?.let { return it }
        val file = licenseDirectory.resolve("$certificateId.rwxlicense")
        if (!file.isFile) return null
        return runCatching { AssetPkiFiles.readLicenseCredential(file) }
            .getOrNull()
            ?.takeIf { it.certificate.certificateId == certificateId }
            ?.also { licenseCache[certificateId] = it }
    }

    override fun findTrustedAuthority(authorityId: String): AssetAuthorityCertificate? {
        if (!ID.matches(authorityId)) return null
        authorityCache[authorityId]?.let { return it }
        val file = authorityDirectory.resolve("$authorityId.rwxpub")
        if (!file.isFile) return null
        return runCatching { AssetPkiFiles.readAuthority(file) }
            .getOrNull()
            ?.takeIf { it.authorityId == authorityId }
            ?.also { authorityCache[authorityId] = it }
    }

    private fun importSymmetricKey(source: File): ImportResult {
        val key = AssetKeyFiles.readPrivate(source)
        require(key.kind == AssetKeyKind.SYMMETRIC) {
            "Only symmetric keys can be imported as .rwxkey credentials"
        }
        val destination = symmetricDirectory.resolve("${key.keyId}.rwxkey")
        AssetKeyFiles.writePrivate(destination, key)
        symmetricCache[key.keyId] = key
        return ImportResult(key.keyId, CredentialKind.SYMMETRIC_KEY, destination)
    }

    private fun importAuthority(source: File): ImportResult {
        val authority = AssetPkiFiles.readAuthority(source)
        findTrustedAuthority(authority.authorityId)?.let { existing ->
            require(sameAuthority(existing, authority)) {
                "A different trust certificate already exists for authority ${authority.authorityId}"
            }
        }
        val destination = authorityDirectory.resolve("${authority.authorityId}.rwxpub")
        AssetPkiFiles.writeAuthority(destination, authority)
        authorityCache[authority.authorityId] = authority
        return ImportResult(authority.authorityId, CredentialKind.TRUSTED_AUTHORITY, destination)
    }

    private fun importLicense(source: File): ImportResult {
        val credential = AssetPkiFiles.readLicenseCredential(source)
        findTrustedAuthority(credential.certificate.authorityId)?.let { authority ->
            AssetPki.validateCredential(credential, authority, enforceValidity = false)
        }
        val certificateId = credential.certificate.certificateId
        val destination = licenseDirectory.resolve("$certificateId.rwxlicense")
        AssetPkiFiles.writeLicenseCredential(destination, credential)
        licenseCache[certificateId] = credential
        return ImportResult(certificateId, CredentialKind.LICENSE, destination)
    }

    private fun sameAuthority(left: AssetAuthorityCertificate, right: AssetAuthorityCertificate): Boolean =
        left.authorityId == right.authorityId &&
                left.displayName == right.displayName &&
                left.publicKey.contentEquals(right.publicKey) &&
                left.createdAtEpochSeconds == right.createdAtEpochSeconds &&
                left.signature.contentEquals(right.signature)

    companion object {
        private const val CREDENTIAL_DIRECTORY = "jvm-mod-asset-credentials"
        private val ID = Regex("[0-9a-f]{64}")
    }
}
