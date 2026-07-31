package io.github.rwx.mod.assets

import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.interfaces.RSAPrivateCrtKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

enum class AssetKeyKind(val formatId: Int) {
    SYMMETRIC(1),
    RSA(2),
}

data class AssetPrivateKey(
    val kind: AssetKeyKind,
    val keyId: String,
    val encoded: ByteArray,
)

data class AssetPublicKey(
    val keyId: String,
    val encoded: ByteArray,
)

object AssetKeyFiles {
    private const val PRIVATE_MAGIC = 0x5257584b // RWXK
    private const val FORMAT_VERSION = 1
    private const val AES_KEY_SIZE = 32
    private const val MAX_ENCODED_KEY_SIZE = 64 * 1024
    private val secureRandom = SecureRandom()

    fun generateSymmetric(): AssetPrivateKey {
        val bytes = ByteArray(AES_KEY_SIZE).also(secureRandom::nextBytes)
        return AssetPrivateKey(AssetKeyKind.SYMMETRIC, fingerprint(bytes), bytes)
    }

    fun generateRsa(keySize: Int = 3072): Pair<AssetPublicKey, AssetPrivateKey> {
        require(keySize >= 2048) { "RSA key size must be at least 2048 bits" }
        val pair = KeyPairGenerator.getInstance("RSA").apply { initialize(keySize, secureRandom) }.generateKeyPair()
        val keyId = fingerprint(pair.public.encoded)
        return AssetPublicKey(keyId, pair.public.encoded) to
                AssetPrivateKey(AssetKeyKind.RSA, keyId, pair.private.encoded)
    }

    fun writePrivate(file: File, key: AssetPrivateKey) {
        validatePrivate(key)
        file.absoluteFile.parentFile?.mkdirs()
        DataOutputStream(BufferedOutputStream(file.outputStream())).use { output ->
            output.writeInt(PRIVATE_MAGIC)
            output.writeInt(FORMAT_VERSION)
            output.writeInt(key.kind.formatId)
            writeString(output, key.keyId)
            writeBytes(output, key.encoded)
        }
        restrictPrivateKeyPermissions(file)
    }

    fun readPrivate(file: File): AssetPrivateKey =
        DataInputStream(BufferedInputStream(file.inputStream())).use { input ->
            require(input.readInt() == PRIVATE_MAGIC) { "Not an private asset key" }
            require(input.readInt() == FORMAT_VERSION) { "Unsupported private key version" }
            val kindId = input.readInt()
            val kind = AssetKeyKind.entries.firstOrNull { it.formatId == kindId }
                ?: error("Unsupported private key kind: $kindId")
            val storedKeyId = readString(input)
            val encoded = readBytes(input)
            val key = AssetPrivateKey(kind, storedKeyId, encoded)
            validatePrivate(key)
            require(input.read() == -1) { "Trailing data in private asset key" }
            key
        }

    fun wrapContentKey(publicKey: AssetPublicKey, contentKey: ByteArray): ByteArray {
        validatePublic(publicKey)
        require(contentKey.size == AES_KEY_SIZE) { "Content key must be 256 bits" }
        val key = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey.encoded))
        return rsaOaepCipher(Cipher.ENCRYPT_MODE, key).doFinal(contentKey)
    }

    fun unwrapContentKey(privateKey: AssetPrivateKey, wrappedKey: ByteArray): ByteArray {
        require(privateKey.kind == AssetKeyKind.RSA) { "An RSA private key is required" }
        validatePrivate(privateKey)
        val key = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(privateKey.encoded))
        return rsaOaepCipher(Cipher.DECRYPT_MODE, key).doFinal(wrappedKey).also { contentKey ->
            require(contentKey.size == AES_KEY_SIZE) { "Invalid unwrapped content key" }
        }
    }

    fun validatePrivate(key: AssetPrivateKey) {
        require(KEY_ID.matches(key.keyId)) { "Invalid asset key id" }
        val expected = when (key.kind) {
            AssetKeyKind.SYMMETRIC -> {
                require(key.encoded.size == AES_KEY_SIZE) { "Symmetric asset keys must be 256 bits" }
                fingerprint(key.encoded)
            }

            AssetKeyKind.RSA -> fingerprint(derivePublicEncoding(key.encoded))
        }
        require(key.keyId == expected) { "private asset key id does not match its key material" }
    }

    fun validatePublic(key: AssetPublicKey) {
        require(KEY_ID.matches(key.keyId)) { "Invalid asset key id" }
        KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(key.encoded))
        require(key.keyId == fingerprint(key.encoded)) { "public asset key id does not match its key material" }
    }

    private fun derivePublicEncoding(privateEncoding: ByteArray): ByteArray {
        val factory = KeyFactory.getInstance("RSA")
        val privateKey = factory.generatePrivate(PKCS8EncodedKeySpec(privateEncoding)) as? RSAPrivateCrtKey
            ?: error("RSA private key does not contain CRT public parameters")
        return factory.generatePublic(
            RSAPublicKeySpec(privateKey.modulus, privateKey.publicExponent)
        ).encoded
    }

    private fun rsaOaepCipher(mode: Int, key: java.security.Key): Cipher =
        Cipher.getInstance("RSA/ECB/OAEPPadding").apply {
            init(
                mode,
                key,
                OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec.SHA256,
                    PSource.PSpecified.DEFAULT,
                ),
            )
        }

    private fun fingerprint(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02x".format(it.toInt() and 0xff) }

    private fun writeString(output: DataOutputStream, value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        require(bytes.size <= 1024) { "key field is too long" }
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    private fun readString(input: DataInputStream): String {
        val size = input.readInt()
        require(size in 0..1024) { "Invalid key field length: $size" }
        return String(ByteArray(size).also(input::readFully), StandardCharsets.UTF_8)
    }

    private fun writeBytes(output: DataOutputStream, bytes: ByteArray) {
        require(bytes.size <= MAX_ENCODED_KEY_SIZE) { "key material is too large" }
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    private fun readBytes(input: DataInputStream): ByteArray {
        val size = input.readInt()
        require(size in 1..MAX_ENCODED_KEY_SIZE) { "Invalid key material length: $size" }
        return ByteArray(size).also(input::readFully)
    }

    internal fun restrictPrivateKeyPermissions(file: File) {
        runCatching {
            Files.setPosixFilePermissions(
                file.toPath(),
                setOf(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE),
            )
        }.onFailure {
            file.setReadable(false, false)
            file.setWritable(false, false)
            file.setReadable(true, true)
            file.setWritable(true, true)
        }
    }

    private val KEY_ID = Regex("[0-9a-f]{64}")
}
