package io.github.rwx.mod.assets

import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

data class AssetAuthorityCertificate(
    val authorityId: String,
    val displayName: String,
    val publicKey: ByteArray,
    val createdAtEpochSeconds: Long,
    val signature: ByteArray,
)

data class AssetAuthorityPrivate(
    val certificate: AssetAuthorityCertificate,
    val privateKey: ByteArray,
)

data class AssetLicenseCertificate(
    val certificateId: String,
    val authorityId: String,
    val subject: String,
    val keyId: String,
    val publicKey: ByteArray,
    val notBeforeEpochSeconds: Long,
    val notAfterEpochSeconds: Long,
    val signature: ByteArray,
)

data class AssetLicenseCredential(
    val certificate: AssetLicenseCertificate,
    val privateKey: ByteArray,
)

data class AssetRevocation(
    val certificateId: String,
    val revokedAtEpochSeconds: Long,
    val reason: String,
)

data class AssetRevocationList(
    val authorityId: String,
    val sequence: Long,
    val issuedAtEpochSeconds: Long,
    val nextUpdateEpochSeconds: Long,
    val entries: List<AssetRevocation>,
    val signature: ByteArray,
)

interface AssetCredentialResolver {
    fun findSymmetricKey(keyId: String): AssetPrivateKey?
    fun findLicense(certificateId: String): AssetLicenseCredential?
    fun findTrustedAuthority(authorityId: String): AssetAuthorityCertificate?
}

object EmptyAssetCredentialResolver : AssetCredentialResolver {
    override fun findSymmetricKey(keyId: String): AssetPrivateKey? = null
    override fun findLicense(certificateId: String): AssetLicenseCredential? = null
    override fun findTrustedAuthority(authorityId: String): AssetAuthorityCertificate? = null
}

object AssetPki {
    const val DEFAULT_REVOCATION_LIST_VALIDITY_HOURS: Long = 24
    const val MAX_REVOCATION_LIST_VALIDITY_HOURS: Long = 24

    private const val SIGNATURE_ALGORITHM = "SHA256withRSA"
    private const val MAX_VALIDITY_DAYS = 3650L
    private const val CLOCK_SKEW_SECONDS = 300L

    fun createAuthority(
        displayName: String,
        nowEpochSeconds: Long = currentEpochSeconds(),
    ): AssetAuthorityPrivate {
        require(displayName.isNotBlank()) { "Authority display name must not be blank" }
        val (publicKey, privateKey) = AssetKeyFiles.generateRsa()
        val unsigned = AssetAuthorityCertificate(
            authorityId = publicKey.keyId,
            displayName = displayName.trim(),
            publicKey = publicKey.encoded,
            createdAtEpochSeconds = nowEpochSeconds,
            signature = byteArrayOf(),
        )
        val certificate = unsigned.copy(signature = sign(privateKey.encoded, authorityPayload(unsigned)))
        return AssetAuthorityPrivate(certificate, privateKey.encoded).also(::validateAuthorityPrivate)
    }

    fun issueLicense(
        authority: AssetAuthorityPrivate,
        subject: String,
        validityDays: Long = 365,
        nowEpochSeconds: Long = currentEpochSeconds(),
    ): AssetLicenseCredential {
        validateAuthorityPrivate(authority)
        require(subject.isNotBlank()) { "License subject must not be blank" }
        require(validityDays in 1..MAX_VALIDITY_DAYS) { "License validity must be between 1 and $MAX_VALIDITY_DAYS days" }
        val (publicKey, privateKey) = AssetKeyFiles.generateRsa()
        val notBefore = nowEpochSeconds - 300
        val notAfter = Math.addExact(nowEpochSeconds, Math.multiplyExact(validityDays, 86_400L))
        val identity = licenseIdentityPayload(
            authorityId = authority.certificate.authorityId,
            subject = subject.trim(),
            keyId = publicKey.keyId,
            publicKey = publicKey.encoded,
            notBefore = notBefore,
            notAfter = notAfter,
        )
        val unsigned = AssetLicenseCertificate(
            certificateId = fingerprint(identity),
            authorityId = authority.certificate.authorityId,
            subject = subject.trim(),
            keyId = publicKey.keyId,
            publicKey = publicKey.encoded,
            notBeforeEpochSeconds = notBefore,
            notAfterEpochSeconds = notAfter,
            signature = byteArrayOf(),
        )
        val certificate = unsigned.copy(
            signature = sign(authority.privateKey, licenseSignaturePayload(unsigned)),
        )
        return AssetLicenseCredential(certificate, privateKey.encoded).also {
            validateCredential(it, authority.certificate, nowEpochSeconds, enforceValidity = true)
        }
    }

    fun revoke(
        authority: AssetAuthorityPrivate,
        certificate: AssetLicenseCertificate,
        previous: AssetRevocationList? = null,
        reason: String = "revoked",
        validityHours: Long = DEFAULT_REVOCATION_LIST_VALIDITY_HOURS,
        nowEpochSeconds: Long = currentEpochSeconds(),
    ): AssetRevocationList {
        validateAuthorityPrivate(authority)
        validateLicenseCertificate(certificate, authority.certificate, nowEpochSeconds, enforceValidity = false)
        previous?.let { validateRevocationList(it, authority.certificate) }
        require(previous == null || previous.authorityId == authority.certificate.authorityId) {
            "Revocation list belongs to another authority"
        }
        val entries = previous?.entries.orEmpty()
            .associateByTo(linkedMapOf(), AssetRevocation::certificateId)
            .apply {
                put(
                    certificate.certificateId,
                    AssetRevocation(certificate.certificateId, nowEpochSeconds, reason.trim()),
                )
            }
            .values
            .sortedBy(AssetRevocation::certificateId)
        return issueRevocationList(authority, previous, entries, validityHours, nowEpochSeconds)
    }

    fun refreshRevocationList(
        authority: AssetAuthorityPrivate,
        previous: AssetRevocationList? = null,
        validityHours: Long = DEFAULT_REVOCATION_LIST_VALIDITY_HOURS,
        nowEpochSeconds: Long = currentEpochSeconds(),
    ): AssetRevocationList {
        validateAuthorityPrivate(authority)
        previous?.let { validateRevocationList(it, authority.certificate) }
        require(previous == null || previous.authorityId == authority.certificate.authorityId) {
            "Revocation list belongs to another authority"
        }
        return issueRevocationList(
            authority,
            previous,
            previous?.entries.orEmpty(),
            validityHours,
            nowEpochSeconds,
        )
    }

    private fun issueRevocationList(
        authority: AssetAuthorityPrivate,
        previous: AssetRevocationList?,
        entries: List<AssetRevocation>,
        validityHours: Long,
        nowEpochSeconds: Long,
    ): AssetRevocationList {
        require(validityHours in 1..MAX_REVOCATION_LIST_VALIDITY_HOURS) {
            "Revocation list validity must be between 1 and $MAX_REVOCATION_LIST_VALIDITY_HOURS hours"
        }
        previous?.let {
            require(nowEpochSeconds >= it.issuedAtEpochSeconds) {
                "Revocation list issue time cannot move backwards"
            }
        }
        val unsigned = AssetRevocationList(
            authorityId = authority.certificate.authorityId,
            sequence = Math.addExact(previous?.sequence ?: 0L, 1L),
            issuedAtEpochSeconds = nowEpochSeconds,
            nextUpdateEpochSeconds = Math.addExact(
                nowEpochSeconds,
                Math.multiplyExact(validityHours, 3_600L),
            ),
            entries = entries,
            signature = byteArrayOf(),
        )
        return unsigned.copy(signature = sign(authority.privateKey, revocationPayload(unsigned))).also {
            validateRevocationList(it, authority.certificate)
        }
    }

    fun validateAuthority(certificate: AssetAuthorityCertificate) {
        require(ID.matches(certificate.authorityId)) { "Invalid authority id" }
        require(certificate.displayName.isNotBlank()) { "Authority display name must not be blank" }
        val publicKey = AssetPublicKey(certificate.authorityId, certificate.publicKey)
        AssetKeyFiles.validatePublic(publicKey)
        require(certificate.signature.isNotEmpty()) { "Authority certificate is unsigned" }
        require(verify(certificate.publicKey, authorityPayload(certificate), certificate.signature)) {
            "Invalid authority self-signature"
        }
    }

    fun validateAuthorityPrivate(authority: AssetAuthorityPrivate) {
        validateAuthority(authority.certificate)
        val privateKey = AssetPrivateKey(AssetKeyKind.RSA, authority.certificate.authorityId, authority.privateKey)
        AssetKeyFiles.validatePrivate(privateKey)
    }

    fun validateLicenseCertificate(
        certificate: AssetLicenseCertificate,
        authority: AssetAuthorityCertificate,
        nowEpochSeconds: Long = currentEpochSeconds(),
        enforceValidity: Boolean = true,
    ) {
        validateAuthority(authority)
        require(certificate.authorityId == authority.authorityId) { "License certificate issuer mismatch" }
        require(certificate.subject.isNotBlank()) { "License subject must not be blank" }
        require(ID.matches(certificate.keyId)) { "Invalid license key id" }
        AssetKeyFiles.validatePublic(AssetPublicKey(certificate.keyId, certificate.publicKey))
        require(certificate.notAfterEpochSeconds > certificate.notBeforeEpochSeconds) { "Invalid license validity interval" }
        val expectedId = fingerprint(
            licenseIdentityPayload(
                certificate.authorityId,
                certificate.subject,
                certificate.keyId,
                certificate.publicKey,
                certificate.notBeforeEpochSeconds,
                certificate.notAfterEpochSeconds,
            )
        )
        require(certificate.certificateId == expectedId) { "License certificate id does not match its contents" }
        require(verify(authority.publicKey, licenseSignaturePayload(certificate), certificate.signature)) {
            "Invalid license certificate signature"
        }
        if (enforceValidity) {
            require(nowEpochSeconds >= certificate.notBeforeEpochSeconds) { "License certificate is not valid yet" }
            require(nowEpochSeconds <= certificate.notAfterEpochSeconds) { "License certificate has expired" }
        }
    }

    fun validateCredential(
        credential: AssetLicenseCredential,
        authority: AssetAuthorityCertificate? = null,
        nowEpochSeconds: Long = currentEpochSeconds(),
        enforceValidity: Boolean = false,
    ) {
        val certificate = credential.certificate
        require(ID.matches(certificate.certificateId)) { "Invalid license certificate id" }
        val privateKey = AssetPrivateKey(AssetKeyKind.RSA, certificate.keyId, credential.privateKey)
        AssetKeyFiles.validatePrivate(privateKey)
        authority?.let {
            validateLicenseCertificate(certificate, it, nowEpochSeconds, enforceValidity)
        }
    }

    fun validateRevocationList(
        list: AssetRevocationList,
        authority: AssetAuthorityCertificate,
        nowEpochSeconds: Long = currentEpochSeconds(),
        enforceFreshness: Boolean = false,
    ) {
        validateAuthority(authority)
        require(list.authorityId == authority.authorityId) { "Revocation list issuer mismatch" }
        require(list.sequence > 0) { "Revocation list sequence must be positive" }
        require(list.nextUpdateEpochSeconds > list.issuedAtEpochSeconds) {
            "Revocation list validity interval is invalid"
        }
        val validitySeconds = Math.subtractExact(list.nextUpdateEpochSeconds, list.issuedAtEpochSeconds)
        require(validitySeconds <= MAX_REVOCATION_LIST_VALIDITY_HOURS * 3_600L) {
            "Revocation list validity exceeds $MAX_REVOCATION_LIST_VALIDITY_HOURS hours"
        }
        require(list.entries.size <= 100_000) { "Revocation list is too large" }
        require(list.entries.map(AssetRevocation::certificateId).toSet().size == list.entries.size) {
            "Revocation list contains duplicate certificate ids"
        }
        list.entries.forEach { entry ->
            require(ID.matches(entry.certificateId)) { "Invalid revoked certificate id" }
            require(entry.revokedAtEpochSeconds <= Math.addExact(list.issuedAtEpochSeconds, CLOCK_SKEW_SECONDS)) {
                "Revocation time is later than the CRL issue time"
            }
        }
        require(verify(authority.publicKey, revocationPayload(list), list.signature)) {
            "Invalid revocation list signature"
        }
        if (enforceFreshness) {
            require(Math.addExact(nowEpochSeconds, CLOCK_SKEW_SECONDS) >= list.issuedAtEpochSeconds) {
                "Online revocation list is not valid yet"
            }
            require(nowEpochSeconds <= list.nextUpdateEpochSeconds) {
                "Online revocation list has expired"
            }
        }
    }

    fun signPackage(authority: AssetAuthorityPrivate, payload: ByteArray): ByteArray {
        validateAuthorityPrivate(authority)
        return sign(authority.privateKey, payload)
    }

    fun verifyPackage(authority: AssetAuthorityCertificate, payload: ByteArray, signature: ByteArray): Boolean {
        validateAuthority(authority)
        return verify(authority.publicKey, payload, signature)
    }

    fun isRevoked(list: AssetRevocationList, certificateId: String): Boolean =
        list.entries.any { it.certificateId == certificateId }

    fun currentEpochSeconds(): Long = System.currentTimeMillis() / 1000L

    private fun authorityPayload(certificate: AssetAuthorityCertificate): ByteArray = canonical {
        writeDomain(it, "RWX-ASSET-AUTHORITY-V1")
        writeString(it, certificate.authorityId)
        writeString(it, certificate.displayName)
        writeBytes(it, certificate.publicKey)
        it.writeLong(certificate.createdAtEpochSeconds)
    }

    private fun licenseIdentityPayload(
        authorityId: String,
        subject: String,
        keyId: String,
        publicKey: ByteArray,
        notBefore: Long,
        notAfter: Long,
    ): ByteArray = canonical {
        writeDomain(it, "RWX-ASSET-LICENSE-ID-V1")
        writeString(it, authorityId)
        writeString(it, subject)
        writeString(it, keyId)
        writeBytes(it, publicKey)
        it.writeLong(notBefore)
        it.writeLong(notAfter)
    }

    private fun licenseSignaturePayload(certificate: AssetLicenseCertificate): ByteArray = canonical {
        writeDomain(it, "RWX-ASSET-LICENSE-V1")
        writeString(it, certificate.certificateId)
        it.write(licenseIdentityPayload(
            certificate.authorityId,
            certificate.subject,
            certificate.keyId,
            certificate.publicKey,
            certificate.notBeforeEpochSeconds,
            certificate.notAfterEpochSeconds,
        ))
    }

    private fun revocationPayload(list: AssetRevocationList): ByteArray = canonical {
        writeDomain(it, "RWX-ASSET-CRL-V2")
        writeString(it, list.authorityId)
        it.writeLong(list.sequence)
        it.writeLong(list.issuedAtEpochSeconds)
        it.writeLong(list.nextUpdateEpochSeconds)
        val entries = list.entries.sortedBy(AssetRevocation::certificateId)
        it.writeInt(entries.size)
        entries.forEach { entry ->
            writeString(it, entry.certificateId)
            it.writeLong(entry.revokedAtEpochSeconds)
            writeString(it, entry.reason)
        }
    }

    private fun sign(privateKey: ByteArray, payload: ByteArray): ByteArray =
        Signature.getInstance(SIGNATURE_ALGORITHM).run {
            initSign(KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(privateKey)))
            update(payload)
            sign()
        }

    private fun verify(publicKey: ByteArray, payload: ByteArray, signature: ByteArray): Boolean =
        runCatching {
            Signature.getInstance(SIGNATURE_ALGORITHM).run {
                initVerify(KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey)))
                update(payload)
                verify(signature)
            }
        }.getOrDefault(false)

    private fun fingerprint(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02x".format(it.toInt() and 0xff) }

    private fun canonical(block: (DataOutputStream) -> Unit): ByteArray =
        ByteArrayOutputStream().also { bytes ->
            DataOutputStream(bytes).use(block)
        }.toByteArray()

    private fun writeDomain(output: DataOutputStream, value: String) = writeString(output, value)

    private fun writeString(output: DataOutputStream, value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    private fun writeBytes(output: DataOutputStream, value: ByteArray) {
        output.writeInt(value.size)
        output.write(value)
    }

    private val ID = Regex("[0-9a-f]{64}")
}

object AssetPkiFiles {
    const val MAX_REVOCATION_LIST_FILE_BYTES: Int = 8 * 1024 * 1024 + 12

    private const val AUTHORITY_PRIVATE_MAGIC = 0x52574150 // RWAP
    private const val AUTHORITY_PUBLIC_MAGIC = 0x52574155 // RWAU
    private const val LICENSE_CERTIFICATE_MAGIC = 0x52574c43 // RWLC
    private const val LICENSE_PRIVATE_MAGIC = 0x52574c50 // RWLP
    private const val REVOCATION_LIST_MAGIC = 0x52574352 // RWCR
    private const val FORMAT_VERSION = 2
    private const val MAX_FIELD_BYTES = 4 * 1024
    private const val MAX_KEY_BYTES = 64 * 1024
    private const val MAX_SIGNATURE_BYTES = 64 * 1024
    private const val MAX_ENCODED_OBJECT_BYTES = 8 * 1024 * 1024
    private const val MAX_REVOCATIONS = 100_000

    fun writeAuthorityPrivate(file: File, authority: AssetAuthorityPrivate) {
        AssetPki.validateAuthorityPrivate(authority)
        writeFile(file, AUTHORITY_PRIVATE_MAGIC, privateMaterial = true) { output ->
            writeBytes(output, encodeAuthority(authority.certificate), MAX_ENCODED_OBJECT_BYTES)
            writeBytes(output, authority.privateKey, MAX_KEY_BYTES)
        }
    }

    fun readAuthorityPrivate(file: File): AssetAuthorityPrivate = readFile(file, AUTHORITY_PRIVATE_MAGIC) { input ->
        AssetAuthorityPrivate(
            decodeAuthority(readBytes(input, MAX_ENCODED_OBJECT_BYTES)),
            readBytes(input, MAX_KEY_BYTES),
        ).also(AssetPki::validateAuthorityPrivate)
    }

    fun writeAuthority(file: File, certificate: AssetAuthorityCertificate) {
        AssetPki.validateAuthority(certificate)
        writeFile(file, AUTHORITY_PUBLIC_MAGIC) { output ->
            writeBytes(output, encodeAuthority(certificate), MAX_ENCODED_OBJECT_BYTES)
        }
    }

    fun readAuthority(file: File): AssetAuthorityCertificate = readFile(file, AUTHORITY_PUBLIC_MAGIC) { input ->
        decodeAuthority(readBytes(input, MAX_ENCODED_OBJECT_BYTES)).also(AssetPki::validateAuthority)
    }

    fun writeLicenseCertificate(file: File, certificate: AssetLicenseCertificate) {
        writeFile(file, LICENSE_CERTIFICATE_MAGIC) { output ->
            writeBytes(output, encodeLicenseCertificate(certificate), MAX_ENCODED_OBJECT_BYTES)
        }
    }

    fun readLicenseCertificate(file: File): AssetLicenseCertificate =
        readFile(file, LICENSE_CERTIFICATE_MAGIC) { input ->
            decodeLicenseCertificate(readBytes(input, MAX_ENCODED_OBJECT_BYTES))
        }

    fun writeLicenseCredential(file: File, credential: AssetLicenseCredential) {
        AssetPki.validateCredential(credential)
        writeFile(file, LICENSE_PRIVATE_MAGIC, privateMaterial = true) { output ->
            writeBytes(output, encodeLicenseCertificate(credential.certificate), MAX_ENCODED_OBJECT_BYTES)
            writeBytes(output, credential.privateKey, MAX_KEY_BYTES)
        }
    }

    fun readLicenseCredential(file: File): AssetLicenseCredential = readFile(file, LICENSE_PRIVATE_MAGIC) { input ->
        AssetLicenseCredential(
            decodeLicenseCertificate(readBytes(input, MAX_ENCODED_OBJECT_BYTES)),
            readBytes(input, MAX_KEY_BYTES),
        ).also(AssetPki::validateCredential)
    }

    fun writeRevocationList(file: File, list: AssetRevocationList) {
        writeFile(file, REVOCATION_LIST_MAGIC) { output ->
            writeBytes(output, encodeRevocationList(list), MAX_ENCODED_OBJECT_BYTES)
        }
    }

    fun readRevocationList(file: File): AssetRevocationList = file.inputStream().use(::readRevocationList)

    fun readRevocationList(input: InputStream): AssetRevocationList = readFile(input, REVOCATION_LIST_MAGIC) { data ->
        decodeRevocationList(readBytes(data, MAX_ENCODED_OBJECT_BYTES))
    }

    fun encodeRevocationList(list: AssetRevocationList): ByteArray = encode { output ->
        writeString(output, list.authorityId)
        output.writeLong(list.sequence)
        output.writeLong(list.issuedAtEpochSeconds)
        output.writeLong(list.nextUpdateEpochSeconds)
        require(list.entries.size <= MAX_REVOCATIONS) { "Revocation list is too large" }
        output.writeInt(list.entries.size)
        list.entries.forEach { entry ->
            writeString(output, entry.certificateId)
            output.writeLong(entry.revokedAtEpochSeconds)
            writeString(output, entry.reason)
        }
        writeBytes(output, list.signature, MAX_SIGNATURE_BYTES)
    }

    fun decodeRevocationList(bytes: ByteArray): AssetRevocationList = decode(bytes) { input ->
        val authorityId = readString(input)
        val sequence = input.readLong()
        val issuedAt = input.readLong()
        val nextUpdate = input.readLong()
        val count = input.readInt()
        require(count in 0..MAX_REVOCATIONS) { "Invalid revocation count: $count" }
        val entries = List(count) {
            AssetRevocation(readString(input), input.readLong(), readString(input))
        }
        AssetRevocationList(
            authorityId,
            sequence,
            issuedAt,
            nextUpdate,
            entries,
            readBytes(input, MAX_SIGNATURE_BYTES),
        )
    }

    private fun encodeAuthority(certificate: AssetAuthorityCertificate): ByteArray = encode { output ->
        writeString(output, certificate.authorityId)
        writeString(output, certificate.displayName)
        writeBytes(output, certificate.publicKey, MAX_KEY_BYTES)
        output.writeLong(certificate.createdAtEpochSeconds)
        writeBytes(output, certificate.signature, MAX_SIGNATURE_BYTES)
    }

    private fun decodeAuthority(bytes: ByteArray): AssetAuthorityCertificate = decode(bytes) { input ->
        AssetAuthorityCertificate(
            readString(input),
            readString(input),
            readBytes(input, MAX_KEY_BYTES),
            input.readLong(),
            readBytes(input, MAX_SIGNATURE_BYTES),
        )
    }

    private fun encodeLicenseCertificate(certificate: AssetLicenseCertificate): ByteArray = encode { output ->
        writeString(output, certificate.certificateId)
        writeString(output, certificate.authorityId)
        writeString(output, certificate.subject)
        writeString(output, certificate.keyId)
        writeBytes(output, certificate.publicKey, MAX_KEY_BYTES)
        output.writeLong(certificate.notBeforeEpochSeconds)
        output.writeLong(certificate.notAfterEpochSeconds)
        writeBytes(output, certificate.signature, MAX_SIGNATURE_BYTES)
    }

    private fun decodeLicenseCertificate(bytes: ByteArray): AssetLicenseCertificate = decode(bytes) { input ->
        AssetLicenseCertificate(
            readString(input),
            readString(input),
            readString(input),
            readString(input),
            readBytes(input, MAX_KEY_BYTES),
            input.readLong(),
            input.readLong(),
            readBytes(input, MAX_SIGNATURE_BYTES),
        )
    }

    private fun writeFile(
        file: File,
        magic: Int,
        privateMaterial: Boolean = false,
        block: (DataOutputStream) -> Unit,
    ) {
        val target = file.absoluteFile
        val parent = checkNotNull(target.parentFile) { "Asset PKI output has no parent directory: $target" }
        require(parent.mkdirs() || parent.isDirectory) { "Cannot create asset PKI directory: $parent" }
        val temporary = Files.createTempFile(parent.toPath(), ".asset-pki-", ".tmp").toFile()
        try {
            if (privateMaterial) AssetKeyFiles.restrictPrivateKeyPermissions(temporary)
            DataOutputStream(BufferedOutputStream(temporary.outputStream())).use { output ->
                output.writeInt(magic)
                output.writeInt(FORMAT_VERSION)
                block(output)
            }
            if (privateMaterial) AssetKeyFiles.restrictPrivateKeyPermissions(temporary)
            moveIntoPlace(temporary, target)
            if (privateMaterial) AssetKeyFiles.restrictPrivateKeyPermissions(target)
        } finally {
            temporary.delete()
        }
    }

    private fun moveIntoPlace(temporary: File, target: File) {
        runCatching {
            Files.move(
                temporary.toPath(),
                target.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING,
            )
        }.getOrElse {
            Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun <T> readFile(file: File, magic: Int, block: (DataInputStream) -> T): T =
        file.inputStream().use { readFile(it, magic, block) }

    private fun <T> readFile(input: InputStream, magic: Int, block: (DataInputStream) -> T): T =
        DataInputStream(BufferedInputStream(input)).use { data ->
            require(data.readInt() == magic) { "Unexpected asset PKI file type" }
            require(data.readInt() == FORMAT_VERSION) { "Unsupported asset PKI file version" }
            block(data).also { require(data.read() == -1) { "Trailing data in asset PKI file" } }
        }

    private fun encode(block: (DataOutputStream) -> Unit): ByteArray =
        ByteArrayOutputStream().also { bytes -> DataOutputStream(bytes).use(block) }.toByteArray()

    private fun <T> decode(bytes: ByteArray, block: (DataInputStream) -> T): T =
        DataInputStream(ByteArrayInputStream(bytes)).use { input ->
            block(input).also { require(input.read() == -1) { "Trailing data in encoded asset PKI object" } }
        }

    private fun writeString(output: DataOutputStream, value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        require(bytes.size <= MAX_FIELD_BYTES) { "Asset PKI field is too long" }
        output.writeInt(bytes.size)
        output.write(bytes)
    }

    private fun readString(input: DataInputStream): String {
        val size = input.readInt()
        require(size in 0..MAX_FIELD_BYTES) { "Invalid asset PKI field length: $size" }
        return String(ByteArray(size).also(input::readFully), StandardCharsets.UTF_8)
    }

    private fun writeBytes(output: DataOutputStream, value: ByteArray, maximum: Int) {
        require(value.size <= maximum) { "Asset PKI binary field is too large" }
        output.writeInt(value.size)
        output.write(value)
    }

    private fun readBytes(input: DataInputStream, maximum: Int): ByteArray {
        val size = input.readInt()
        require(size in 0..maximum) { "Invalid asset PKI binary field length: $size" }
        return ByteArray(size).also(input::readFully)
    }
}
