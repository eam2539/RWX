package io.github.rwx.mod.assets

import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

enum class AssetProtectionMode(val formatId: Int) {
    SYMMETRIC(1),
    PKI(2),
}

data class AssetRecipientEnvelope(
    val certificateId: String,
    val keyId: String,
    val wrappedContentKey: ByteArray,
)

data class AssetVaultHeader(
    val mode: AssetProtectionMode,
    val vaultId: String,
    val keyId: String = "",
    val embeddedContentKey: ByteArray = byteArrayOf(),
    val authorityId: String = "",
    val recipients: List<AssetRecipientEnvelope> = emptyList(),
    val revocationList: AssetRevocationList? = null,
    val indexDigest: ByteArray,
    val signature: ByteArray = byteArrayOf(),
)

object AssetVaultFormat {
    const val HEADER_ENTRY: String = "META-INF/rwx-assets/header.bin"
    const val INDEX_ENTRY: String = "META-INF/rwx-assets/index.bin"
    const val BLOB_DIRECTORY: String = "META-INF/rwx-assets/blobs/"
    const val FORMAT_VERSION: Int = 1
    const val NONCE_SIZE: Int = 12
    const val AUTH_TAG_BITS: Int = 128

    private const val INDEX_MAGIC = 0x52575841 // RWXA
    private const val HEADER_MAGIC = 0x52575848 // RWXH
    private const val MAX_INDEX_ENTRIES = 100_000
    private const val MAX_RECIPIENTS = 10_000
    private const val MAX_PATH_BYTES = 16 * 1024
    private const val MAX_WRAPPED_KEY_BYTES = 16 * 1024
    private const val MAX_CRL_BYTES = 8 * 1024 * 1024
    private const val MAX_SIGNATURE_BYTES = 64 * 1024
    private const val COPY_BUFFER_SIZE = 64 * 1024
    private val random = SecureRandom()
    private val idPattern = Regex("[0-9a-f]{64}")

    data class AssetRecord(
        val path: String,
        val blobEntry: String,
        val plaintextSize: Long,
        val ciphertextDigest: ByteArray,
    )

    fun isProtectedAsset(path: String): Boolean =
        normalizeArchivePath(path).startsWith("assets/") && !path.endsWith('/')

    fun writeHeader(header: AssetVaultHeader, output: OutputStream) {
        validateHeader(header)
        DataOutputStream(output).apply {
            writeInt(HEADER_MAGIC)
            writeInt(FORMAT_VERSION)
            writeInt(header.mode.formatId)
            writeString(this, header.vaultId)
            writeString(this, header.keyId)
            writeBytes(this, header.embeddedContentKey, 32)
            writeString(this, header.authorityId)
            writeInt(header.recipients.size)
            header.recipients.forEach { recipient ->
                writeString(this, recipient.certificateId)
                writeString(this, recipient.keyId)
                writeBytes(this, recipient.wrappedContentKey, MAX_WRAPPED_KEY_BYTES)
            }
            val encodedCrl = header.revocationList?.let(AssetPkiFiles::encodeRevocationList) ?: byteArrayOf()
            writeBytes(this, encodedCrl, MAX_CRL_BYTES)
            writeBytes(this, header.indexDigest, 32)
            writeBytes(this, header.signature, MAX_SIGNATURE_BYTES)
            flush()
        }
    }

    fun readHeader(input: InputStream): AssetVaultHeader =
        DataInputStream(BufferedInputStream(input)).use { data ->
            require(data.readInt() == HEADER_MAGIC) { "Invalid asset vault header" }
            require(data.readInt() == FORMAT_VERSION) { "Unsupported asset vault version" }
            val modeId = data.readInt()
            val mode = AssetProtectionMode.entries.firstOrNull { it.formatId == modeId }
                ?: error("Unsupported asset protection mode: $modeId")
            val vaultId = readString(data)
            val keyId = readString(data)
            val embeddedContentKey = readBytes(data, 32)
            val authorityId = readString(data)
            val recipientCount = data.readInt()
            require(recipientCount in 0..MAX_RECIPIENTS) { "Invalid asset recipient count: $recipientCount" }
            val recipients = List(recipientCount) {
                AssetRecipientEnvelope(
                    certificateId = readString(data),
                    keyId = readString(data),
                    wrappedContentKey = readBytes(data, MAX_WRAPPED_KEY_BYTES),
                )
            }
            val revocationList = readBytes(data, MAX_CRL_BYTES)
                .takeIf(ByteArray::isNotEmpty)
                ?.let(AssetPkiFiles::decodeRevocationList)
            val header = AssetVaultHeader(
                mode = mode,
                vaultId = vaultId,
                keyId = keyId,
                embeddedContentKey = embeddedContentKey,
                authorityId = authorityId,
                recipients = recipients,
                revocationList = revocationList,
                indexDigest = readBytes(data, 32),
                signature = readBytes(data, MAX_SIGNATURE_BYTES),
            ).also(::validateHeader)
            require(data.read() == -1) { "Trailing data in asset vault header" }
            header
        }

    fun packageSignaturePayload(header: AssetVaultHeader, modId: String): ByteArray {
        require(header.mode == AssetProtectionMode.PKI) { "Only PKI asset vaults are signed" }
        validateHeader(header, requireSignature = false)
        return ByteArrayOutputStream().also { bytes ->
            DataOutputStream(bytes).use { output ->
                writeString(output, "RWX-ASSET-PACKAGE-V1")
                writeString(output, modId)
                output.writeInt(FORMAT_VERSION)
                output.writeInt(header.mode.formatId)
                writeString(output, header.vaultId)
                writeString(output, header.authorityId)
                val recipients = header.recipients.sortedBy(AssetRecipientEnvelope::certificateId)
                output.writeInt(recipients.size)
                recipients.forEach { recipient ->
                    writeString(output, recipient.certificateId)
                    writeString(output, recipient.keyId)
                    writeBytes(output, recipient.wrappedContentKey, MAX_WRAPPED_KEY_BYTES)
                }
                val encodedCrl = header.revocationList?.let(AssetPkiFiles::encodeRevocationList) ?: byteArrayOf()
                writeBytes(output, encodedCrl, MAX_CRL_BYTES)
                writeBytes(output, header.indexDigest, 32)
            }
        }.toByteArray()
    }

    fun normalizeAssetPath(path: String): String {
        val withoutRoot = path.removePrefix("ROOT:").replace('\\', '/')
        require(!withoutRoot.startsWith('/')) { "Asset path must be relative: $path" }
        val normalized = normalizeArchivePath(withoutRoot)
        require(normalized.isNotBlank()) { "Asset path must not be blank" }
        require(normalized.split('/').none { it.isBlank() || it == "." || it == ".." }) {
            "Invalid asset path: $path"
        }
        return normalized
    }

    fun writeIndex(records: Collection<AssetRecord>, output: OutputStream) {
        val data = DataOutputStream(output)
        data.writeInt(INDEX_MAGIC)
        data.writeInt(FORMAT_VERSION)
        data.writeInt(records.size)
        records.sortedBy(AssetRecord::path).forEach { record ->
            writeString(data, normalizeAssetPath(record.path))
            writeString(data, validateBlobEntry(record.blobEntry))
            require(record.plaintextSize >= 0) { "Negative asset size for ${record.path}" }
            data.writeLong(record.plaintextSize)
            require(record.ciphertextDigest.size == 32) {
                "Encrypted asset digest must be SHA-256 for ${record.path}"
            }
            writeBytes(data, record.ciphertextDigest, 32)
        }
        data.flush()
    }

    fun encodeIndex(records: Collection<AssetRecord>): ByteArray =
        ByteArrayOutputStream().also { writeIndex(records, it) }.toByteArray()

    fun readIndex(input: InputStream): List<AssetRecord> {
        DataInputStream(BufferedInputStream(input)).use { data ->
            require(data.readInt() == INDEX_MAGIC) { "Invalid asset index magic" }
            val version = data.readInt()
            require(version == FORMAT_VERSION) { "Unsupported asset format version: $version" }
            val count = data.readInt()
            require(count in 0..MAX_INDEX_ENTRIES) { "Invalid asset index entry count: $count" }
            val seenPaths = hashSetOf<String>()
            val seenBlobs = hashSetOf<String>()
            val records = List(count) {
                val path = normalizeAssetPath(readString(data))
                val blob = validateBlobEntry(readString(data))
                val size = data.readLong()
                require(size >= 0) { "Negative asset size for $path" }
                val ciphertextDigest = readBytes(data, 32)
                require(ciphertextDigest.size == 32) { "Encrypted asset digest must be SHA-256 for $path" }
                require(seenPaths.add(path)) { "Duplicate encrypted asset path: $path" }
                require(seenBlobs.add(blob)) { "Duplicate encrypted asset blob: $blob" }
                AssetRecord(path, blob, size, ciphertextDigest)
            }
            require(data.read() == -1) { "Trailing data in asset vault index" }
            return records
        }
    }

    fun encrypt(
        header: AssetVaultHeader,
        contentKey: ByteArray,
        modId: String,
        path: String,
        input: InputStream,
        output: OutputStream,
    ) {
        validateHeader(header, requireSignature = false)
        val normalizedPath = normalizeAssetPath(path)
        val nonce = ByteArray(NONCE_SIZE).also(random::nextBytes)
        output.write(nonce)
        val cipher = createCipher(Cipher.ENCRYPT_MODE, header, contentKey, modId, normalizedPath, nonce)
        val buffer = ByteArray(COPY_BUFFER_SIZE)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            cipher.update(buffer, 0, count)?.let(output::write)
        }
        output.write(cipher.doFinal())
    }

    fun decrypt(
        header: AssetVaultHeader,
        contentKey: ByteArray,
        modId: String,
        path: String,
        input: InputStream,
    ): InputStream {
        validateHeader(header)
        val normalizedPath = normalizeAssetPath(path)
        val nonce = input.readExactly(NONCE_SIZE)
        return CipherInputStream(input, createCipher(Cipher.DECRYPT_MODE, header, contentKey, modId, normalizedPath, nonce))
    }

    fun newContentKey(): ByteArray = ByteArray(32).also(random::nextBytes)

    fun newVaultId(): String = ByteArray(32).also(random::nextBytes).toHex()

    fun newBlobEntry(): String = BLOB_DIRECTORY + ByteArray(16).also(random::nextBytes).toHex() + ".bin"

    fun sha256(bytes: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256").digest(bytes)

    private fun createCipher(
        mode: Int,
        header: AssetVaultHeader,
        contentKey: ByteArray,
        modId: String,
        path: String,
        nonce: ByteArray,
    ): Cipher {
        require(contentKey.size == 32) { "Asset content key must be 256 bits" }
        require(modId.isNotBlank()) { "Mod id must not be blank" }
        return Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(mode, SecretKeySpec(contentKey, "AES"), GCMParameterSpec(AUTH_TAG_BITS, nonce))
            updateAAD(
                "RWX-ASSET-V1\u0000${header.mode.name}\u0000${header.vaultId}\u0000$modId\u0000$path"
                    .toByteArray(StandardCharsets.UTF_8)
            )
        }
    }

    private fun validateBlobEntry(value: String): String {
        val normalized = normalizeArchivePath(value)
        require(normalized.startsWith(BLOB_DIRECTORY)) { "Invalid encrypted asset blob path: $value" }
        require(normalized.split('/').none { it == "." || it == ".." }) {
            "Invalid encrypted asset blob path: $value"
        }
        return normalized
    }

    private fun validateHeader(header: AssetVaultHeader, requireSignature: Boolean = true) {
        require(idPattern.matches(header.vaultId)) { "Invalid asset vault id" }
        require(header.indexDigest.size == 32) { "Asset vault index digest must be SHA-256" }
        require(header.recipients.size <= MAX_RECIPIENTS) { "Too many asset recipients" }
        require(header.recipients.map(AssetRecipientEnvelope::certificateId).toSet().size == header.recipients.size) {
            "Duplicate asset recipient certificate"
        }
        header.recipients.forEach { recipient ->
            require(idPattern.matches(recipient.certificateId)) { "Invalid recipient certificate id" }
            require(idPattern.matches(recipient.keyId)) { "Invalid recipient key id" }
            require(recipient.wrappedContentKey.isNotEmpty()) { "Recipient content key envelope is empty" }
        }
        when (header.mode) {
            AssetProtectionMode.SYMMETRIC -> {
                val embedsKey = header.embeddedContentKey.isNotEmpty()
                require(embedsKey.xor(header.keyId.isNotEmpty())) {
                    "Symmetric vault must select exactly one key distribution method"
                }
                if (embedsKey) {
                    require(header.embeddedContentKey.size == 32) { "Embedded asset key must be 256 bits" }
                } else {
                    require(idPattern.matches(header.keyId)) { "Invalid symmetric asset key id" }
                }
                require(header.authorityId.isEmpty() && header.recipients.isEmpty()) { "Symmetric vault must not contain PKI data" }
                require(header.revocationList == null && header.signature.isEmpty()) { "Symmetric vault must not be signed" }
            }

            AssetProtectionMode.PKI -> {
                require(header.keyId.isEmpty() && header.embeddedContentKey.isEmpty()) { "PKI vault must not contain symmetric authorization data" }
                require(idPattern.matches(header.authorityId)) { "Invalid asset authority id" }
                require(header.recipients.isNotEmpty()) { "PKI asset vault has no recipients" }
                header.revocationList?.let { require(it.authorityId == header.authorityId) { "Embedded CRL issuer mismatch" } }
                if (requireSignature) require(header.signature.isNotEmpty()) { "PKI asset vault is unsigned" }
            }
        }
    }

    private fun normalizeArchivePath(path: String): String = path.replace('\\', '/')

    private fun writeString(output: DataOutputStream, value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        require(bytes.size <= MAX_PATH_BYTES) { "Asset field is too long" }
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    private fun readString(input: DataInputStream): String {
        val length = input.readInt()
        require(length in 0..MAX_PATH_BYTES) { "Invalid asset field length: $length" }
        return String(input.readExactly(length), StandardCharsets.UTF_8)
    }

    private fun writeBytes(output: DataOutputStream, bytes: ByteArray, maximum: Int) {
        require(bytes.size <= maximum) { "Asset binary field is too large" }
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    private fun readBytes(input: DataInputStream, maximum: Int): ByteArray {
        val size = input.readInt()
        require(size in 0..maximum) { "Invalid asset binary field length: $size" }
        return input.readExactly(size)
    }

    private fun InputStream.readExactly(length: Int): ByteArray {
        val result = ByteArray(length)
        var offset = 0
        while (offset < length) {
            val count = read(result, offset, length - offset)
            require(count >= 0) { "Truncated encrypted asset" }
            offset += count
        }
        return result
    }

    private fun ByteArray.toHex(): String = joinToString("") {
        "%02x".format(Locale.ROOT, it.toInt() and 0xff)
    }
}

object ModAssetPacker {
    sealed interface Protection {
        data object EmbeddedSymmetric : Protection
        data class Symmetric(val key: AssetPrivateKey) : Protection
        data class Pki(
            val authority: AssetAuthorityPrivate,
            val recipients: List<AssetLicenseCertificate>,
            val revocationList: AssetRevocationList? = null,
        ) : Protection
    }

    data class Result(
        val output: File,
        val modId: String,
        val encryptedAssets: Int,
        val encryptedPlaintextBytes: Long,
        val protectionMode: AssetProtectionMode,
        val protectionId: String,
        val recipients: Int,
    )

    private data class EncryptionPlan(
        val header: AssetVaultHeader,
        val contentKey: ByteArray,
    )

    private data class SourceAsset(
        val entry: ZipEntry,
        val path: String,
        val blobEntry: String,
        val plaintextSize: Long,
    )

    fun encryptJar(
        input: File,
        output: File,
        protection: Protection,
        overwrite: Boolean = false,
    ): Result {
        require(input.isFile) { "Input mod JAR does not exist: $input" }
        if (output.exists() && !overwrite && input.canonicalFile != output.canonicalFile) {
            error("Output already exists: $output")
        }
        output.absoluteFile.parentFile?.mkdirs()
        val temporary = Files.createTempFile(output.absoluteFile.parentFile.toPath(), ".rwx-assets-", ".jar").toFile()
        try {
            val result = ZipFile(input).use { source ->
                require(
                    source.getEntry(AssetVaultFormat.HEADER_ENTRY) == null &&
                            source.getEntry(AssetVaultFormat.INDEX_ENTRY) == null
                ) { "The input JAR already contains an encrypted asset vault" }
                val modId = readModId(source)
                val assets = source.entries().asSequence()
                    .filterNot(ZipEntry::isDirectory)
                    .filter { AssetVaultFormat.isProtectedAsset(it.name) }
                    .sortedBy(ZipEntry::getName)
                    .map { entry ->
                        require(entry.size >= 0) { "Asset size is unavailable: ${entry.name}" }
                        SourceAsset(
                            entry = entry,
                            path = AssetVaultFormat.normalizeAssetPath(entry.name),
                            blobEntry = AssetVaultFormat.newBlobEntry(),
                            plaintextSize = entry.size,
                        )
                    }
                    .toList()
                require(assets.isNotEmpty()) { "No files under assets/ were found in ${input.name}" }
                val plan = createPlan(protection)
                lateinit var finalizedHeader: AssetVaultHeader

                ZipOutputStream(BufferedOutputStream(temporary.outputStream())).use { target ->
                    source.entries().asSequence().forEach { entry ->
                        if (AssetVaultFormat.isProtectedAsset(entry.name)) return@forEach
                        if (entry.name.startsWith(AssetVaultFormat.BLOB_DIRECTORY) ||
                            entry.name == AssetVaultFormat.INDEX_ENTRY ||
                            entry.name == AssetVaultFormat.HEADER_ENTRY
                        ) return@forEach
                        val copy = ZipEntry(entry.name).apply { time = entry.time }
                        target.putNextEntry(copy)
                        if (!entry.isDirectory) source.getInputStream(entry).use { it.copyTo(target) }
                        target.closeEntry()
                    }
                    val records = assets.map { asset ->
                        target.putNextEntry(ZipEntry(asset.blobEntry))
                        val digest = MessageDigest.getInstance("SHA-256")
                        val digestingOutput = java.security.DigestOutputStream(target, digest)
                        source.getInputStream(asset.entry).use { plain ->
                            AssetVaultFormat.encrypt(
                                plan.header,
                                plan.contentKey,
                                modId,
                                asset.path,
                                plain,
                                digestingOutput,
                            )
                        }
                        target.closeEntry()
                        AssetVaultFormat.AssetRecord(
                            path = asset.path,
                            blobEntry = asset.blobEntry,
                            plaintextSize = asset.plaintextSize,
                            ciphertextDigest = digest.digest(),
                        )
                    }
                    val indexBytes = AssetVaultFormat.encodeIndex(records)
                    finalizedHeader = finalizeHeader(
                        protection,
                        plan.header,
                        modId,
                        AssetVaultFormat.sha256(indexBytes),
                    )
                    target.putNextEntry(ZipEntry(AssetVaultFormat.HEADER_ENTRY))
                    AssetVaultFormat.writeHeader(finalizedHeader, target)
                    target.closeEntry()
                    target.putNextEntry(ZipEntry(AssetVaultFormat.INDEX_ENTRY))
                    target.write(indexBytes)
                    target.closeEntry()
                }
                Result(
                    output = output.absoluteFile,
                    modId = modId,
                    encryptedAssets = assets.size,
                    encryptedPlaintextBytes = assets.sumOf(SourceAsset::plaintextSize),
                    protectionMode = finalizedHeader.mode,
                    protectionId = when (finalizedHeader.mode) {
                        AssetProtectionMode.SYMMETRIC -> finalizedHeader.keyId.ifEmpty { finalizedHeader.vaultId }
                        AssetProtectionMode.PKI -> finalizedHeader.authorityId
                    },
                    recipients = finalizedHeader.recipients.size,
                )
            }
            moveIntoPlace(temporary, output.absoluteFile)
            return result
        } finally {
            temporary.delete()
        }
    }

    private fun createPlan(
        protection: Protection,
    ): EncryptionPlan {
        val vaultId = AssetVaultFormat.newVaultId()
        return when (protection) {
            Protection.EmbeddedSymmetric -> {
                val key = AssetVaultFormat.newContentKey()
                EncryptionPlan(
                    AssetVaultHeader(
                        mode = AssetProtectionMode.SYMMETRIC,
                        vaultId = vaultId,
                        embeddedContentKey = key.copyOf(),
                        indexDigest = ByteArray(32),
                    ),
                    key,
                )
            }

            is Protection.Symmetric -> {
                AssetKeyFiles.validatePrivate(protection.key)
                require(protection.key.kind == AssetKeyKind.SYMMETRIC) {
                    "The symmetric protection mode requires a symmetric .rwxkey"
                }
                EncryptionPlan(
                    AssetVaultHeader(
                        mode = AssetProtectionMode.SYMMETRIC,
                        vaultId = vaultId,
                        keyId = protection.key.keyId,
                        indexDigest = ByteArray(32),
                    ),
                    protection.key.encoded.copyOf(),
                )
            }

            is Protection.Pki -> {
                AssetPki.validateAuthorityPrivate(protection.authority)
                require(protection.recipients.isNotEmpty()) { "PKI protection requires at least one recipient" }
                val authority = protection.authority.certificate
                protection.revocationList?.let { AssetPki.validateRevocationList(it, authority) }
                val now = AssetPki.currentEpochSeconds()
                val contentKey = AssetVaultFormat.newContentKey()
                val recipients = protection.recipients
                    .distinctBy(AssetLicenseCertificate::certificateId)
                    .onEach { certificate ->
                        AssetPki.validateLicenseCertificate(certificate, authority, now, enforceValidity = true)
                        require(!AssetPki.isRevoked(protection.revocationList, certificate.certificateId)) {
                            "Recipient ${certificate.certificateId} is revoked"
                        }
                    }
                    .sortedBy(AssetLicenseCertificate::certificateId)
                    .map { certificate ->
                        AssetRecipientEnvelope(
                            certificateId = certificate.certificateId,
                            keyId = certificate.keyId,
                            wrappedContentKey = AssetKeyFiles.wrapContentKey(
                                AssetPublicKey(certificate.keyId, certificate.publicKey),
                                contentKey,
                            ),
                        )
                    }
                val unsigned = AssetVaultHeader(
                    mode = AssetProtectionMode.PKI,
                    vaultId = vaultId,
                    authorityId = authority.authorityId,
                    recipients = recipients,
                    revocationList = protection.revocationList,
                    indexDigest = ByteArray(32),
                )
                EncryptionPlan(unsigned, contentKey)
            }
        }
    }

    private fun finalizeHeader(
        protection: Protection,
        header: AssetVaultHeader,
        modId: String,
        indexDigest: ByteArray,
    ): AssetVaultHeader {
        val withDigest = header.copy(indexDigest = indexDigest)
        return when (protection) {
            is Protection.Pki -> withDigest.copy(
                signature = AssetPki.signPackage(
                    protection.authority,
                    AssetVaultFormat.packageSignaturePayload(withDigest, modId),
                )
            )

            Protection.EmbeddedSymmetric,
            is Protection.Symmetric -> withDigest
        }
    }

    private fun readModId(zip: ZipFile): String {
        val entry = zip.getEntry("mod.toml") ?: error("Missing mod.toml")
        val text = zip.getInputStream(entry).bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        val match = MOD_ID.find(text) ?: error("mod.toml is missing a basic-string id")
        return match.groupValues[1]
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .takeIf(String::isNotBlank)
            ?: error("mod.toml id must not be blank")
    }

    private fun moveIntoPlace(temporary: File, output: File) {
        runCatching {
            Files.move(
                temporary.toPath(),
                output.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING,
            )
        }.getOrElse {
            Files.move(temporary.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private val MOD_ID = Regex("(?m)^\\s*id\\s*=\\s*\"((?:\\\\.|[^\"\\\\])+)\"")
}
