package io.github.rwx.mod.assets

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.SecureRandom
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

enum class AssetProtectionMode(val formatId: Int) {
    SYMMETRIC_KEY(1),
    RSA_PUBLIC_KEY(2),
}

data class AssetVaultHeader(
    val mode: AssetProtectionMode,
    val keyId: String,
    val wrappedContentKey: ByteArray = byteArrayOf(),
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
    private const val MAX_PATH_BYTES = 16 * 1024
    private const val COPY_BUFFER_SIZE = 64 * 1024
    private val random = SecureRandom()

    data class AssetRecord(
        val path: String,
        val blobEntry: String,
        val plaintextSize: Long,
    )

    fun isProtectedAsset(path: String): Boolean =
        normalizeArchivePath(path).startsWith("assets/") && !path.endsWith('/')

    fun writeHeader(header: AssetVaultHeader, output: OutputStream) {
        validateHeader(header)
        DataOutputStream(output).apply {
            writeInt(HEADER_MAGIC)
            writeInt(FORMAT_VERSION)
            writeInt(header.mode.formatId)
            writeString(this, header.keyId)
            writeInt(header.wrappedContentKey.size)
            write(header.wrappedContentKey)
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
            val keyId = readString(data)
            val wrappedSize = data.readInt()
            require(wrappedSize in 0..16_384) { "Invalid wrapped content key size: $wrappedSize" }
            AssetVaultHeader(mode, keyId, ByteArray(wrappedSize).also(data::readFully)).also(::validateHeader)
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
        }
        data.flush()
    }

    fun readIndex(input: InputStream): List<AssetRecord> {
        DataInputStream(BufferedInputStream(input)).use { data ->
            require(data.readInt() == INDEX_MAGIC) { "Invalid asset index magic" }
            val version = data.readInt()
            require(version == FORMAT_VERSION) { "Unsupported asset format version: $version" }
            val count = data.readInt()
            require(count in 0..MAX_INDEX_ENTRIES) { "Invalid asset index entry count: $count" }
            val seenPaths = hashSetOf<String>()
            val seenBlobs = hashSetOf<String>()
            return List(count) {
                val path = normalizeAssetPath(readString(data))
                val blob = validateBlobEntry(readString(data))
                val size = data.readLong()
                require(size >= 0) { "Negative asset size for $path" }
                require(seenPaths.add(path)) { "Duplicate encrypted asset path: $path" }
                require(seenBlobs.add(blob)) { "Duplicate encrypted asset blob: $blob" }
                AssetRecord(path, blob, size)
            }
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
        validateHeader(header)
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

    fun newBlobEntry(): String {
        return BLOB_DIRECTORY + newBlobId() + ".bin"
    }

    private fun createCipher(
        mode: Int,
        header: AssetVaultHeader,
        contentKey: ByteArray,
        modId: String,
        path: String,
        nonce: ByteArray,
    ): Cipher {
        require(contentKey.size == 32) { "asset content key must be 256 bits" }
        require(modId.isNotBlank()) { "Mod id must not be blank" }
        return Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(mode, SecretKeySpec(contentKey, "AES"), GCMParameterSpec(AUTH_TAG_BITS, nonce))
            updateAAD(
                "RWX-ASSET-V1\u0000${header.mode.name}\u0000${header.keyId}\u0000$modId\u0000$path"
                    .toByteArray(StandardCharsets.UTF_8)
            )
        }
    }

    private fun validateBlobEntry(value: String): String {
        val normalized = normalizeArchivePath(value)
        require(normalized.startsWith(BLOB_DIRECTORY)) {
            "Invalid encrypted asset blob path: $value"
        }
        require(normalized.split('/').none { it == "." || it == ".." }) {
            "Invalid encrypted asset blob path: $value"
        }
        return normalized
    }

    private fun validateHeader(header: AssetVaultHeader) {
        when (header.mode) {
            AssetProtectionMode.SYMMETRIC_KEY -> {
                require(KEY_ID.matches(header.keyId)) { "Invalid symmetric asset key id" }
                require(header.wrappedContentKey.isEmpty()) { "Symmetric asset vault must not wrap a key" }
            }

            AssetProtectionMode.RSA_PUBLIC_KEY -> {
                require(KEY_ID.matches(header.keyId)) { "Invalid RSA asset key id" }
                require(header.wrappedContentKey.isNotEmpty()) { "RSA asset vault is missing its wrapped content key" }
            }
        }
    }

    private fun newBlobId(): String = ByteArray(16).also(random::nextBytes)
        .joinToString("") { "%02x".format(Locale.ROOT, it.toInt() and 0xff) }

    private fun normalizeArchivePath(path: String): String = path.replace('\\', '/').trimStart('/')

    private fun writeString(output: DataOutputStream, value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        require(bytes.size <= MAX_PATH_BYTES) { "asset index value is too long" }
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    private fun readString(input: DataInputStream): String {
        val length = input.readInt()
        require(length in 0..MAX_PATH_BYTES) { "Invalid asset index string length: $length" }
        return String(input.readExactly(length), StandardCharsets.UTF_8)
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

    private val KEY_ID = Regex("[0-9a-f]{64}")
}

object ModAssetPacker {
    sealed interface Protection {
        data class Symmetric(val key: AssetPrivateKey) : Protection
        data class PublicKey(val key: AssetPublicKey) : Protection
    }

    data class Result(
        val output: File,
        val modId: String,
        val encryptedAssets: Int,
        val encryptedPlaintextBytes: Long,
        val protectionMode: AssetProtectionMode,
        val keyId: String,
    )

    private data class EncryptionPlan(
        val header: AssetVaultHeader,
        val contentKey: ByteArray,
    )

    fun encryptJar(
        input: File,
        output: File,
        protection: Protection,
        overwrite: Boolean = false,
    ): Result = encryptJarInternal(input, output, overwrite) {
        when (protection) {
            is Protection.Symmetric -> {
                AssetKeyFiles.validatePrivate(protection.key)
                require(protection.key.kind == AssetKeyKind.SYMMETRIC) {
                    "The symmetric protection mode requires a symmetric .rwxkey"
                }
                EncryptionPlan(
                    header = AssetVaultHeader(AssetProtectionMode.SYMMETRIC_KEY, protection.key.keyId),
                    contentKey = protection.key.encoded.copyOf(),
                )
            }

            is Protection.PublicKey -> {
                AssetKeyFiles.validatePublic(protection.key)
                val contentKey = AssetVaultFormat.newContentKey()
                EncryptionPlan(
                    header = AssetVaultHeader(
                        mode = AssetProtectionMode.RSA_PUBLIC_KEY,
                        keyId = protection.key.keyId,
                        wrappedContentKey = AssetKeyFiles.wrapContentKey(protection.key, contentKey),
                    ),
                    contentKey = contentKey,
                )
            }
        }
    }

    private fun encryptJarInternal(
        input: File,
        output: File,
        overwrite: Boolean,
        createPlan: () -> EncryptionPlan,
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
                val plan = createPlan()
                val assets = source.entries().asSequence()
                    .filterNot(ZipEntry::isDirectory)
                    .filter { AssetVaultFormat.isProtectedAsset(it.name) }
                    .sortedBy(ZipEntry::getName)
                    .map { entry ->
                        require(entry.size >= 0) { "Asset size is unavailable: ${entry.name}" }
                        entry to AssetVaultFormat.AssetRecord(
                            path = AssetVaultFormat.normalizeAssetPath(entry.name),
                            blobEntry = AssetVaultFormat.newBlobEntry(),
                            plaintextSize = entry.size,
                        )
                    }
                    .toList()
                require(assets.isNotEmpty()) { "No files under assets/ were found in ${input.name}" }

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
                    target.putNextEntry(ZipEntry(AssetVaultFormat.HEADER_ENTRY))
                    AssetVaultFormat.writeHeader(plan.header, target)
                    target.closeEntry()
                    target.putNextEntry(ZipEntry(AssetVaultFormat.INDEX_ENTRY))
                    AssetVaultFormat.writeIndex(assets.map { it.second }, target)
                    target.closeEntry()
                    assets.forEach { (entry, record) ->
                        target.putNextEntry(ZipEntry(record.blobEntry))
                        source.getInputStream(entry).use { plain ->
                            AssetVaultFormat.encrypt(
                                plan.header,
                                plan.contentKey,
                                modId,
                                record.path,
                                plain,
                                target,
                            )
                        }
                        target.closeEntry()
                    }
                }
                Result(
                    output = output.absoluteFile,
                    modId = modId,
                    encryptedAssets = assets.size,
                    encryptedPlaintextBytes = assets.sumOf { it.second.plaintextSize },
                    protectionMode = plan.header.mode,
                    keyId = plan.header.keyId,
                )
            }
            moveIntoPlace(temporary, output.absoluteFile)
            return result
        } finally {
            temporary.delete()
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
