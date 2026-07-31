package io.github.rwx.mod.tools

import io.github.rwx.mod.assets.*
import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty() || args[0] in setOf("help", "--help", "-h")) {
        printUsage()
        return
    }
    when (args[0]) {
        "encrypt" -> encrypt(args.drop(1))
        "keygen" -> keygen(args.drop(1))
        "pki" -> pki(args.drop(1))
        else -> error("Unknown command '${args[0]}'. Run with --help for usage.")
    }
}

private fun encrypt(arguments: List<String>) {
    var overwrite = false
    var embeddedKey = false
    var symmetricKeyFile: File? = null
    var authorityFile: File? = null
    var revocationListFile: File? = null
    val recipientFiles = mutableListOf<File>()
    val paths = mutableListOf<String>()
    var index = 0
    while (index < arguments.size) {
        when (val argument = arguments[index]) {
            "--force" -> overwrite = true
            "--embedded-key" -> embeddedKey = true
            "--symmetric-key" -> symmetricKeyFile = File(requiredValue(arguments, ++index, argument))
            "--pki-author" -> authorityFile = File(requiredValue(arguments, ++index, argument))
            "--recipient" -> recipientFiles += File(requiredValue(arguments, ++index, argument))
            "--crl" -> revocationListFile = File(requiredValue(arguments, ++index, argument))
            else -> {
                require(!argument.startsWith("--")) { "Unknown encrypt option: $argument" }
                paths += argument
            }
        }
        index++
    }
    require(paths.size == 2) { "encrypt requires <input.jar> and <output.jar>" }
    val selectedModes = listOf(embeddedKey, symmetricKeyFile != null, authorityFile != null).count { it }
    require(selectedModes == 1) {
        "Select exactly one of --embedded-key, --symmetric-key, or --pki-author"
    }

    val protection = when {
        embeddedKey -> {
            require(recipientFiles.isEmpty() && revocationListFile == null) {
                "--recipient and --crl require --pki-author"
            }
            ModAssetPacker.Protection.EmbeddedSymmetric
        }

        symmetricKeyFile != null -> {
            require(recipientFiles.isEmpty() && revocationListFile == null) {
                "--recipient and --crl require --pki-author"
            }
            val key = AssetKeyFiles.readPrivate(symmetricKeyFile)
            require(key.kind == AssetKeyKind.SYMMETRIC) { "$symmetricKeyFile is not a symmetric asset key" }
            ModAssetPacker.Protection.Symmetric(key)
        }

        else -> {
            require(recipientFiles.isNotEmpty()) { "PKI encryption requires at least one --recipient .rwxcert" }
            ModAssetPacker.Protection.Pki(
                authority = AssetPkiFiles.readAuthorityPrivate(checkNotNull(authorityFile)),
                recipients = recipientFiles.map(AssetPkiFiles::readLicenseCertificate),
                revocationList = revocationListFile?.let(AssetPkiFiles::readRevocationList),
            )
        }
    }

    val result = ModAssetPacker.encryptJar(File(paths[0]), File(paths[1]), protection, overwrite)
    println(
        "Encrypted ${result.encryptedAssets} assets (${result.encryptedPlaintextBytes} bytes) " +
                "for ${result.modId} using ${result.protectionMode.name.lowercase()} " +
                "${result.protectionId.take(12)}... (${result.recipients} recipient(s)) -> ${result.output}"
    )
}

private fun keygen(arguments: List<String>) {
    when (arguments.firstOrNull()) {
        "symmetric" -> keygenSymmetric(arguments.drop(1))
        "authority" -> keygenAuthority(arguments.drop(1))
        else -> error("keygen mode must be symmetric or authority")
    }
}

private fun keygenSymmetric(arguments: List<String>) {
    val overwrite = "--force" in arguments
    val values = arguments.filterNot { it == "--force" }
    require(values.size == 1) { "keygen symmetric requires <private.rwxkey>" }
    val privateFile = File(values[0])
    requireOutputAvailable(privateFile, overwrite)
    val key = AssetKeyFiles.generateSymmetric()
    AssetKeyFiles.writePrivate(privateFile, key)
    println("Generated symmetric key ${key.keyId} -> ${privateFile.absoluteFile}")
}

private fun keygenAuthority(arguments: List<String>) {
    var overwrite = false
    var displayName: String? = null
    val paths = mutableListOf<String>()
    var index = 0
    while (index < arguments.size) {
        when (val argument = arguments[index]) {
            "--force" -> overwrite = true
            "--name" -> displayName = requiredValue(arguments, ++index, argument)
            else -> {
                require(!argument.startsWith("--")) { "Unknown authority option: $argument" }
                paths += argument
            }
        }
        index++
    }
    require(paths.size == 2) {
        "keygen authority requires <private.rwxauthor> and <public.rwxpub>"
    }
    val privateFile = File(paths[0])
    val publicFile = File(paths[1])
    requireDistinctOutputs(privateFile, publicFile)
    requireOutputAvailable(privateFile, overwrite)
    requireOutputAvailable(publicFile, overwrite)
    val authority = AssetPki.createAuthority(requireNotNull(displayName) { "keygen authority requires --name" })
    AssetPkiFiles.writeAuthorityPrivate(privateFile, authority)
    AssetPkiFiles.writeAuthority(publicFile, authority.certificate)
    println("Generated authority ${authority.certificate.authorityId} (${authority.certificate.displayName})")
    println("Private: ${privateFile.absoluteFile}")
    println("Trust:   ${publicFile.absoluteFile}")
}

private fun pki(arguments: List<String>) {
    when (arguments.firstOrNull()) {
        "issue" -> issueLicense(arguments.drop(1))
        "revoke" -> revokeLicense(arguments.drop(1))
        else -> error("pki command must be issue or revoke")
    }
}

private fun issueLicense(arguments: List<String>) {
    var overwrite = false
    var subject: String? = null
    var validityDays = 365L
    val paths = mutableListOf<String>()
    var index = 0
    while (index < arguments.size) {
        when (val argument = arguments[index]) {
            "--force" -> overwrite = true
            "--subject" -> subject = requiredValue(arguments, ++index, argument)
            "--days" -> validityDays = requiredValue(arguments, ++index, argument).toLong()
            else -> {
                require(!argument.startsWith("--")) { "Unknown issue option: $argument" }
                paths += argument
            }
        }
        index++
    }
    require(paths.size == 3) {
        "pki issue requires <authority.rwxauthor> <recipient.rwxcert> <recipient.rwxlicense>"
    }
    val certificateFile = File(paths[1])
    val credentialFile = File(paths[2])
    requireDistinctOutputs(certificateFile, credentialFile)
    requireOutputAvailable(certificateFile, overwrite)
    requireOutputAvailable(credentialFile, overwrite)
    val authority = AssetPkiFiles.readAuthorityPrivate(File(paths[0]))
    val credential = AssetPki.issueLicense(
        authority,
        requireNotNull(subject) { "pki issue requires --subject" },
        validityDays,
    )
    AssetPkiFiles.writeLicenseCertificate(certificateFile, credential.certificate)
    AssetPkiFiles.writeLicenseCredential(credentialFile, credential)
    println("Issued license ${credential.certificate.certificateId} to ${credential.certificate.subject}")
    println("Author copy: ${certificateFile.absoluteFile}")
    println("Player file: ${credentialFile.absoluteFile}")
}

private fun revokeLicense(arguments: List<String>) {
    var overwrite = false
    var previousFile: File? = null
    var reason = "revoked"
    val paths = mutableListOf<String>()
    var index = 0
    while (index < arguments.size) {
        when (val argument = arguments[index]) {
            "--force" -> overwrite = true
            "--previous" -> previousFile = File(requiredValue(arguments, ++index, argument))
            "--reason" -> reason = requiredValue(arguments, ++index, argument)
            else -> {
                require(!argument.startsWith("--")) { "Unknown revoke option: $argument" }
                paths += argument
            }
        }
        index++
    }
    require(paths.size == 3) {
        "pki revoke requires <authority.rwxauthor> <recipient.rwxcert> <output.rwxcrl>"
    }
    val output = File(paths[2])
    val previous = (previousFile ?: output.takeIf(File::isFile))?.let(AssetPkiFiles::readRevocationList)
    requireOutputAvailable(output, overwrite)
    val authority = AssetPkiFiles.readAuthorityPrivate(File(paths[0]))
    val certificate = AssetPkiFiles.readLicenseCertificate(File(paths[1]))
    val list = AssetPki.revoke(authority, certificate, previous, reason)
    AssetPkiFiles.writeRevocationList(output, list)
    println(
        "Revoked ${certificate.certificateId}; CRL sequence ${list.sequence} " +
                "contains ${list.entries.size} certificate(s) -> ${output.absoluteFile}"
    )
}

private fun requiredValue(arguments: List<String>, index: Int, option: String): String =
    arguments.getOrNull(index) ?: error("$option requires a value")

private fun requireOutputAvailable(file: File, overwrite: Boolean) {
    require(overwrite || !file.exists()) { "Output already exists: $file (use --force to replace it)" }
}

private fun requireDistinctOutputs(first: File, second: File) {
    require(first.canonicalFile != second.canonicalFile) { "Output files must be different" }
}

private fun printUsage() {
    println(
        """
        RWX JVM mod asset tool

        Key and PKI management:
          mod-tools keygen symmetric <private.rwxkey> [--force]
          mod-tools keygen authority <private.rwxauthor> <public.rwxpub> --name <author> [--force]
          mod-tools pki issue <authority.rwxauthor> <recipient.rwxcert> <recipient.rwxlicense> --subject <player> [--days <n>] [--force]
          mod-tools pki revoke <authority.rwxauthor> <recipient.rwxcert> <output.rwxcrl> [--previous <current.rwxcrl>] [--reason <text>] [--force]

        Asset packaging:
          mod-tools encrypt <input.jar> <output.jar> --embedded-key [--force]
          mod-tools encrypt <input.jar> <output.jar> --symmetric-key <private.rwxkey> [--force]
          mod-tools encrypt <input.jar> <output.jar> --pki-author <authority.rwxauthor> --recipient <recipient.rwxcert>... [--crl <list.rwxcrl>] [--force]
        """.trimIndent()
    )
}
