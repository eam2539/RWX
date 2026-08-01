package io.github.rwx

import java.io.InputStream
import java.io.OutputStream

interface SafPlatformAccess {
    fun registerTree(uri: String): String?
    fun exists(path: String): Boolean
    fun isDirectory(path: String): Boolean
    fun createDirectory(path: String): Boolean
    fun list(path: String): Array<String>?
    fun size(path: String): Long
    fun lastModified(path: String): Long
    fun openInput(path: String): InputStream?
    fun openOutput(path: String, append: Boolean): OutputStream?
    fun rename(sourcePath: String, targetPath: String): Boolean
    fun delete(path: String): Boolean
}

object SafPlatformBridge {
    @Volatile
    private var access: SafPlatformAccess? = null

    @JvmStatic
    fun install(access: SafPlatformAccess) {
        this.access = access
    }

    @JvmStatic
    fun uninstall(access: SafPlatformAccess) {
        if (this.access === access) this.access = null
    }

    @JvmStatic
    fun registerTree(uri: String): String? = access?.registerTree(uri)

    @JvmStatic
    fun resolveStorageRoot(treePath: String, pathExtra: String?): String? {
        if (!isDirectory(treePath)) return null

        normalizedRelativePath(pathExtra)?.let { relativePath ->
            join(treePath, relativePath).takeIf(::isDirectory)?.let { return it }
        }

        return join(treePath, LEGACY_ROOT_DIRECTORY)
            .takeIf(::isDirectory)
            ?: treePath
    }

    @JvmStatic
    fun exists(path: String): Boolean = access?.exists(path) == true

    @JvmStatic
    fun isDirectory(path: String): Boolean = access?.isDirectory(path) == true

    @JvmStatic
    fun createDirectory(path: String): Boolean = access?.createDirectory(path) == true

    @JvmStatic
    fun list(path: String): Array<String>? = access?.list(path)

    @JvmStatic
    fun size(path: String): Long = access?.size(path) ?: -1L

    @JvmStatic
    fun lastModified(path: String): Long = access?.lastModified(path) ?: 0L

    @JvmStatic
    fun openInput(path: String): InputStream? = access?.openInput(path)

    @JvmStatic
    fun openOutput(path: String, append: Boolean): OutputStream? = access?.openOutput(path, append)

    @JvmStatic
    fun rename(sourcePath: String, targetPath: String): Boolean =
        access?.rename(sourcePath, targetPath) == true

    @JvmStatic
    fun delete(path: String): Boolean = access?.delete(path) == true

    private fun normalizedRelativePath(path: String?): String? {
        val normalized = path
            ?.replace('\\', '/')
            ?.trim('/')
            ?.takeIf(String::isNotBlank)
            ?: return null
        val segments = normalized.split('/')
        return normalized.takeIf { segments.none { segment -> segment == "." || segment == ".." } }
    }

    private fun join(root: String, child: String): String =
        root.trimEnd('/', '\\') + "/" + child.trimStart('/', '\\')

    private const val LEGACY_ROOT_DIRECTORY = "rustedWarfare"
}
