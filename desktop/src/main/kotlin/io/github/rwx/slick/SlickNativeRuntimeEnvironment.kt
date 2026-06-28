package io.github.rwx.slick

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.jar.JarFile

internal object SlickNativeRuntimeEnvironment {
    private const val NATIVES_RESOURCE_PREFIX = "rwx/slick-natives/"

    private val extractionLock = Any()
    private var extractedNativesDir: File? = null

    fun nativesDir(): String {
        configuredNativesDir()?.let { return it }
        return extractBundledNatives()
            ?.absolutePath
            ?.also { nativesDir ->
                System.setProperty("rwx.slick.nativesDir", nativesDir)
                System.setProperty("org.lwjgl.librarypath", nativesDir)
                System.setProperty("java.library.path", nativesDir)
            }
            .orEmpty()
    }

    private fun configuredNativesDir(): String? =
        (System.getProperty("rwx.slick.nativesDir") ?: System.getenv("RWX_SLICK_NATIVES_DIR"))
            ?.trim()
            ?.takeIf { it.isNotBlank() }

    private fun extractBundledNatives(): File? = synchronized(extractionLock) {
        extractedNativesDir?.takeIf { it.isDirectory }?.let { return@synchronized it }

        val source = SlickNativeRuntimeEnvironment::class.java.protectionDomain.codeSource
            ?.location
            ?.let { runCatching { File(it.toURI()) }.getOrNull() }
            ?: return@synchronized null

        if (source.isDirectory) {
            val nativesDir = File(source, NATIVES_RESOURCE_PREFIX)
            if (nativesDir.isDirectory) {
                extractedNativesDir = nativesDir
                return@synchronized nativesDir
            }
            return@synchronized null
        }

        if (!source.isFile) {
            return@synchronized null
        }

        val targetDir = File(
            System.getProperty("java.io.tmpdir"),
            "rwx-slick-natives-${nativeBundleId(source)}",
        )
        JarFile(source).use { jar ->
            val entries = jar.entries().asSequence()
                .filter { !it.isDirectory && it.name.startsWith(NATIVES_RESOURCE_PREFIX) }
                .toList()
            if (entries.isEmpty()) {
                return@synchronized null
            }
            targetDir.mkdirs()
            entries.forEach { entry ->
                val relativePath = entry.name.removePrefix(NATIVES_RESOURCE_PREFIX)
                if (relativePath.isBlank()) return@forEach
                val outputFile = File(targetDir, relativePath)
                outputFile.parentFile.mkdirs()
                jar.getInputStream(entry).use { input ->
                    Files.copy(input, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
                outputFile.setReadable(true, false)
                outputFile.setExecutable(true, false)
            }
        }

        extractedNativesDir = targetDir
        targetDir
    }

    private fun nativeBundleId(source: File): String =
        "${source.nameWithoutExtension}-${source.length()}-${source.lastModified()}"
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
}
