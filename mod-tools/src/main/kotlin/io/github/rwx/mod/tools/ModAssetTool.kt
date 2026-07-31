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
    var revocationListUrl: String? = null
    val recipientFiles = mutableListOf<File>()
    val paths = mutableListOf<String>()
    var index = 0
    while (index < arguments.size) {
        when (val argument = arguments[index]) {
            "--force" -> {
                require(!overwrite) { "$argument was specified more than once" }
                overwrite = true
            }

            "--embedded-key" -> {
                require(!embeddedKey) { "$argument was specified more than once" }
                embeddedKey = true
            }

            "--symmetric-key" -> {
                require(symmetricKeyFile == null) { "$argument was specified more than once" }
                symmetricKeyFile = File(requiredValue(arguments, ++index, argument))
            }

            "--pki-author" -> {
                require(authorityFile == null) { "$argument was specified more than once" }
                authorityFile = File(requiredValue(arguments, ++index, argument))
            }
            "--recipient" -> recipientFiles += File(requiredValue(arguments, ++index, argument))
            "--crl-url" -> {
                require(revocationListUrl == null) { "$argument was specified more than once" }
                revocationListUrl = requiredValue(arguments, ++index, argument)
            }
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
    val input = File(paths[0]).requireExtension("jar")
    val output = File(paths[1]).requireExtension("jar")
    symmetricKeyFile?.requireExtension("rwxkey")
    authorityFile?.requireExtension("rwxauthor")
    recipientFiles.forEach { it.requireExtension("rwxcert") }
    val protectionInputs = listOfNotNull(symmetricKeyFile, authorityFile) + recipientFiles
    requireDistinctFiles(listOf(input) + protectionInputs, "Packaging inputs must be different files")
    requireDistinctFiles(recipientFiles, "Recipient certificate files must be different")
    require(output.canonicalFile !in protectionInputs.map(File::getCanonicalFile)) {
        "Output JAR must not overwrite a key or certificate"
    }

    val protection = when {
        embeddedKey -> {
            require(recipientFiles.isEmpty() && revocationListUrl == null) {
                "--recipient and --crl-url require --pki-author"
            }
            ModAssetPacker.Protection.EmbeddedSymmetric
        }

        symmetricKeyFile != null -> {
            require(recipientFiles.isEmpty() && revocationListUrl == null) {
                "--recipient and --crl-url require --pki-author"
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
                revocationListUrl = AssetVaultFormat.validateRevocationListUrl(
                    requireNotNull(revocationListUrl) { "PKI encryption requires --crl-url" },
                ),
            )
        }
    }

    val result = ModAssetPacker.encryptJar(input, output, protection, overwrite)
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
    var overwrite = false
    val values = arguments.filter { argument ->
        if (argument == "--force") {
            require(!overwrite) { "$argument was specified more than once" }
            overwrite = true
            false
        } else {
            require(!argument.startsWith("--")) { "Unknown symmetric key option: $argument" }
            true
        }
    }
    require(values.size == 1) { "keygen symmetric requires <private.rwxkey>" }
    val privateFile = File(values[0]).requireExtension("rwxkey")
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
            "--force" -> {
                require(!overwrite) { "$argument was specified more than once" }
                overwrite = true
            }

            "--name" -> {
                require(displayName == null) { "$argument was specified more than once" }
                displayName = requiredValue(arguments, ++index, argument)
            }
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
    val privateFile = File(paths[0]).requireExtension("rwxauthor")
    val publicFile = File(paths[1]).requireExtension("rwxpub")
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
        "crl" -> refreshRevocationList(arguments.drop(1))
        "revoke" -> revokeLicense(arguments.drop(1))
        else -> error("pki command must be issue, crl, or revoke")
    }
}

private fun issueLicense(arguments: List<String>) {
    var overwrite = false
    var subject: String? = null
    var validityDays = 365L
    var validitySpecified = false
    val paths = mutableListOf<String>()
    var index = 0
    while (index < arguments.size) {
        when (val argument = arguments[index]) {
            "--force" -> {
                require(!overwrite) { "$argument was specified more than once" }
                overwrite = true
            }

            "--subject" -> {
                require(subject == null) { "$argument was specified more than once" }
                subject = requiredValue(arguments, ++index, argument)
            }

            "--days" -> {
                require(!validitySpecified) { "$argument was specified more than once" }
                validityDays = requiredValue(arguments, ++index, argument).toLong()
                validitySpecified = true
            }
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
    val authorityFile = File(paths[0]).requireExtension("rwxauthor")
    val certificateFile = File(paths[1]).requireExtension("rwxcert")
    val credentialFile = File(paths[2]).requireExtension("rwxlicense")
    requireDistinctFiles(listOf(authorityFile, certificateFile, credentialFile), "PKI issue files must be different")
    requireOutputAvailable(certificateFile, overwrite)
    requireOutputAvailable(credentialFile, overwrite)
    val authority = AssetPkiFiles.readAuthorityPrivate(authorityFile)
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

private fun refreshRevocationList(arguments: List<String>) {
    var overwrite = false
    var previousFile: File? = null
    var validityHours = AssetPki.DEFAULT_REVOCATION_LIST_VALIDITY_HOURS
    var validitySpecified = false
    val paths = mutableListOf<String>()
    var index = 0
    while (index < arguments.size) {
        when (val argument = arguments[index]) {
            "--force" -> {
                require(!overwrite) { "$argument was specified more than once" }
                overwrite = true
            }

            "--previous" -> {
                require(previousFile == null) { "$argument was specified more than once" }
                previousFile = File(requiredValue(arguments, ++index, argument))
            }

            "--valid-hours" -> {
                require(!validitySpecified) { "$argument was specified more than once" }
                validityHours = requiredValue(arguments, ++index, argument).toLong()
                validitySpecified = true
            }

            else -> {
                require(!argument.startsWith("--")) { "Unknown CRL option: $argument" }
                paths += argument
            }
        }
        index++
    }
    require(paths.size == 2) {
        "pki crl requires <authority.rwxauthor> and <output.rwxcrl>"
    }
    val authorityFile = File(paths[0]).requireExtension("rwxauthor")
    val output = File(paths[1]).requireExtension("rwxcrl")
    previousFile?.requireExtension("rwxcrl")
    require(authorityFile.canonicalFile != output.canonicalFile) {
        "CRL output must not overwrite the authority private key"
    }
    val previous = (previousFile ?: output.takeIf(File::isFile))?.let(AssetPkiFiles::readRevocationList)
    requireOutputAvailable(output, overwrite)
    val authority = AssetPkiFiles.readAuthorityPrivate(authorityFile)
    val list = AssetPki.refreshRevocationList(authority, previous, validityHours)
    AssetPkiFiles.writeRevocationList(output, list)
    println(
        "Published CRL sequence ${list.sequence} with ${list.entries.size} revocation(s); " +
                "valid until ${list.nextUpdateEpochSeconds} -> ${output.absoluteFile}"
    )
}

private fun revokeLicense(arguments: List<String>) {
    var overwrite = false
    var previousFile: File? = null
    var reason = "revoked"
    var reasonSpecified = false
    var validityHours = AssetPki.DEFAULT_REVOCATION_LIST_VALIDITY_HOURS
    var validitySpecified = false
    val paths = mutableListOf<String>()
    var index = 0
    while (index < arguments.size) {
        when (val argument = arguments[index]) {
            "--force" -> {
                require(!overwrite) { "$argument was specified more than once" }
                overwrite = true
            }

            "--previous" -> {
                require(previousFile == null) { "$argument was specified more than once" }
                previousFile = File(requiredValue(arguments, ++index, argument))
            }

            "--reason" -> {
                require(!reasonSpecified) { "$argument was specified more than once" }
                reason = requiredValue(arguments, ++index, argument)
                reasonSpecified = true
            }

            "--valid-hours" -> {
                require(!validitySpecified) { "$argument was specified more than once" }
                validityHours = requiredValue(arguments, ++index, argument).toLong()
                validitySpecified = true
            }
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
    val authorityFile = File(paths[0]).requireExtension("rwxauthor")
    val certificateFile = File(paths[1]).requireExtension("rwxcert")
    val output = File(paths[2]).requireExtension("rwxcrl")
    previousFile?.requireExtension("rwxcrl")
    requireDistinctFiles(listOf(authorityFile, certificateFile), "Authority and certificate inputs must be different")
    require(output.canonicalFile !in listOf(authorityFile, certificateFile).map(File::getCanonicalFile)) {
        "CRL output must not overwrite the authority or recipient certificate"
    }
    val previous = (previousFile ?: output.takeIf(File::isFile))?.let(AssetPkiFiles::readRevocationList)
    requireOutputAvailable(output, overwrite)
    val authority = AssetPkiFiles.readAuthorityPrivate(authorityFile)
    val certificate = AssetPkiFiles.readLicenseCertificate(certificateFile)
    val list = AssetPki.revoke(
        authority,
        certificate,
        previous,
        reason,
        validityHours,
    )
    AssetPkiFiles.writeRevocationList(output, list)
    println(
        "Revoked ${certificate.certificateId}; CRL sequence ${list.sequence} " +
                "contains ${list.entries.size} certificate(s) -> ${output.absoluteFile}"
    )
}

private fun requiredValue(arguments: List<String>, index: Int, option: String): String =
    arguments.getOrNull(index)
        ?.takeUnless { it.startsWith("--") }
        ?: error("$option requires a value")

private fun requireOutputAvailable(file: File, overwrite: Boolean) {
    require(overwrite || !file.exists()) { "Output already exists: $file (use --force to replace it)" }
}

private fun requireDistinctOutputs(first: File, second: File) {
    require(first.canonicalFile != second.canonicalFile) { "Output files must be different" }
}

private fun requireDistinctFiles(files: List<File>, message: String) {
    require(files.map(File::getCanonicalFile).toSet().size == files.size) { message }
}

private fun File.requireExtension(expected: String): File = apply {
    require(extension.equals(expected, ignoreCase = true)) { "$this must use the .$expected extension" }
}

private fun printUsage() {
    println(
        """
        RWX JVM mod asset tool

        Key and PKI management:
          mod-tools keygen symmetric <private.rwxkey> [--force]
          mod-tools keygen authority <private.rwxauthor> <public.rwxpub> --name <author> [--force]
          mod-tools pki issue <authority.rwxauthor> <recipient.rwxcert> <recipient.rwxlicense> --subject <player> [--days <n>] [--force]
          mod-tools pki crl <authority.rwxauthor> <output.rwxcrl> [--previous <current.rwxcrl>] [--valid-hours <1-24>] [--force]
          mod-tools pki revoke <authority.rwxauthor> <recipient.rwxcert> <output.rwxcrl> [--previous <current.rwxcrl>] [--reason <text>] [--valid-hours <1-24>] [--force]

        Asset packaging:
          mod-tools encrypt <input.jar> <output.jar> --embedded-key [--force]
          mod-tools encrypt <input.jar> <output.jar> --symmetric-key <private.rwxkey> [--force]
          mod-tools encrypt <input.jar> <output.jar> --pki-author <authority.rwxauthor> --recipient <recipient.rwxcert>... --crl-url <https-url> [--force]
        """.trimIndent()
    )
}
