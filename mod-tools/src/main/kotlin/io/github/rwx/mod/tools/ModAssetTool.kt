package io.github.rwx.mod.tools

import io.github.rwx.mod.assets.ModAssetPacker
import io.github.rwx.mod.assets.AssetKeyFiles
import io.github.rwx.mod.assets.AssetKeyKind
import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty() || args[0] in setOf("help", "--help", "-h")) {
        printUsage()
        return
    }
    when (args[0]) {
        "encrypt" -> encrypt(args.drop(1))
        "keygen" -> keygen(args.drop(1))
        else -> error("Unknown command '${args[0]}'. Run with --help for usage.")
    }
}

private fun encrypt(arguments: List<String>) {
    var overwrite = false
    var protection: ModAssetPacker.Protection? = null
    val paths = mutableListOf<String>()
    var index = 0
    while (index < arguments.size) {
        when (val argument = arguments[index]) {
            "--force" -> overwrite = true
            "--symmetric-key" -> {
                check(protection == null) { "Select only one asset protection mode" }
                val file = File(arguments.getOrNull(++index) ?: error("--symmetric-key requires a .rwxkey file"))
                val key = AssetKeyFiles.readPrivate(file)
                require(key.kind == AssetKeyKind.SYMMETRIC) { "$file is not a symmetric asset key" }
                protection = ModAssetPacker.Protection.Symmetric(key)
            }

            "--public-key" -> {
                check(protection == null) { "Select only one asset protection mode" }
                val file = File(arguments.getOrNull(++index) ?: error("--public-key requires a .rwxpub file"))
                protection = ModAssetPacker.Protection.PublicKey(AssetKeyFiles.readPublic(file))
            }

            else -> {
                require(!argument.startsWith("--")) { "Unknown encrypt option: $argument" }
                paths += argument
            }
        }
        index++
    }
    require(paths.size == 2) { "encrypt requires <input.jar> and <output.jar>" }
    val selectedProtection = requireNotNull(protection) {
        "encrypt requires --symmetric-key <.rwxkey> or --public-key <.rwxpub>"
    }
    val result = ModAssetPacker.encryptJar(File(paths[0]), File(paths[1]), selectedProtection, overwrite)
    println(
        "Encrypted ${result.encryptedAssets} assets (${result.encryptedPlaintextBytes} bytes) " +
                "for ${result.modId} using ${result.protectionMode.name.lowercase()}" +
                " key ${result.keyId.take(12)}…" +
                " -> ${result.output}"
    )
}

private fun keygen(arguments: List<String>) {
    val overwrite = "--force" in arguments
    val values = arguments.filterNot { it == "--force" }
    when (values.firstOrNull()) {
        "symmetric" -> {
            require(values.size == 2) { "keygen symmetric requires <private.rwxkey>" }
            val privateFile = File(values[1])
            requireOutputAvailable(privateFile, overwrite)
            val key = AssetKeyFiles.generateSymmetric()
            AssetKeyFiles.writePrivate(privateFile, key)
            println("Generated symmetric key ${key.keyId} -> ${privateFile.absoluteFile}")
        }

        "asymmetric" -> {
            require(values.size == 3) {
                "keygen asymmetric requires <public.rwxpub> and <private.rwxkey>"
            }
            val publicFile = File(values[1])
            val privateFile = File(values[2])
            require(publicFile.canonicalFile != privateFile.canonicalFile) {
                "Public and private key outputs must be different files"
            }
            requireOutputAvailable(publicFile, overwrite)
            requireOutputAvailable(privateFile, overwrite)
            val (publicKey, privateKey) = AssetKeyFiles.generateRsa()
            AssetKeyFiles.writePublic(publicFile, publicKey)
            AssetKeyFiles.writePrivate(privateFile, privateKey)
            println("Generated RSA key ${publicKey.keyId}")
            println("Public:  ${publicFile.absoluteFile}")
            println("Private: ${privateFile.absoluteFile}")
        }

        else -> error("keygen mode must be symmetric or asymmetric")
    }
}

private fun requireOutputAvailable(file: File, overwrite: Boolean) {
    require(overwrite || !file.exists()) { "Output already exists: $file (use --force to replace it)" }
}

private fun printUsage() {
    println(
        """
        RWX JVM mod asset tool

        Usage:
          mod-tools keygen symmetric <private.rwxkey> [--force]
          mod-tools keygen asymmetric <public.rwxpub> <private.rwxkey> [--force]
          mod-tools encrypt <input.jar> <output.jar> <protection> [--force]

        Protection:
          --symmetric-key <.rwxkey>   Shared author key; players import the same key
          --public-key <.rwxpub>      Public-key packaging; players import the private key

        """.trimIndent()
    )
}
