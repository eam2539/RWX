package io.github.rwx

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.DocumentsContract.Document
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap

internal class AndroidSafAccess(context: Context) : SafPlatformAccess {
    private val resolver: ContentResolver = context.applicationContext.contentResolver
    private val trees = ConcurrentHashMap<String, Uri>()

    override fun registerTree(uri: String): String? = runCatching {
        val treeUri = Uri.parse(uri)
        DocumentsContract.getTreeDocumentId(treeUri)
        val key = "saf-${uri.hashCode().toUInt().toString(16)}.[saflink]"
        trees[key] = treeUri
        "/$key"
    }.getOrNull()

    override fun exists(path: String): Boolean = resolve(path) != null

    override fun isDirectory(path: String): Boolean = resolve(path)?.mimeType == Document.MIME_TYPE_DIR

    override fun createDirectory(path: String): Boolean {
        val parsed = parse(path) ?: return false
        return ensureDirectory(parsed.treeUri, parsed.segments) != null
    }

    override fun list(path: String): Array<String>? {
        val directory = resolve(path)?.takeIf { it.mimeType == Document.MIME_TYPE_DIR } ?: return null
        return queryChildren(directory.treeUri, directory.documentId)
            .map { it.name }
            .sorted()
            .toTypedArray()
    }

    override fun size(path: String): Long = resolve(path)?.size ?: -1L

    override fun lastModified(path: String): Long = resolve(path)?.lastModified ?: 0L

    override fun openInput(path: String): InputStream? =
        resolve(path)?.let { runCatching { resolver.openInputStream(it.documentUri) }.getOrNull() }

    override fun openOutput(path: String, append: Boolean): OutputStream? {
        val parsed = parse(path) ?: return null
        val fileName = parsed.segments.lastOrNull() ?: return null
        val parent = ensureDirectory(parsed.treeUri, parsed.segments.dropLast(1)) ?: return null
        val existing = findChild(parent.treeUri, parent.documentId, fileName)
        val file = existing ?: createDocument(
            parent,
            fileName,
            "application/octet-stream",
            parsed.segments.dropLast(1),
        ) ?: return null
        return runCatching {
            resolver.openOutputStream(file.documentUri, if (append) "wa" else "w")
        }.getOrNull()
    }

    override fun rename(sourcePath: String, targetPath: String): Boolean {
        val source = resolve(sourcePath) ?: return false
        val target = parse(targetPath) ?: return false
        val targetName = target.segments.lastOrNull() ?: return false
        val targetParentSegments = target.segments.dropLast(1)
        if (source.parentSegments != targetParentSegments) return false
        return runCatching {
            DocumentsContract.renameDocument(resolver, source.documentUri, targetName) != null
        }.getOrDefault(false)
    }

    override fun delete(path: String): Boolean {
        val document = resolve(path) ?: return false
        return runCatching {
            DocumentsContract.deleteDocument(resolver, document.documentUri)
        }.getOrDefault(false)
    }

    private fun resolve(path: String): SafDocument? {
        val parsed = parse(path) ?: return null
        val rootId = DocumentsContract.getTreeDocumentId(parsed.treeUri)
        var current = queryDocument(parsed.treeUri, rootId, emptyList()) ?: return null
        parsed.segments.forEachIndexed { index, segment ->
            current = findChild(parsed.treeUri, current.documentId, segment, parsed.segments.take(index))
                ?: return null
        }
        return current
    }

    private fun ensureDirectory(treeUri: Uri, segments: List<String>): SafDocument? {
        val rootId = DocumentsContract.getTreeDocumentId(treeUri)
        var current = queryDocument(treeUri, rootId, emptyList()) ?: return null
        segments.forEachIndexed { index, segment ->
            val existing = findChild(treeUri, current.documentId, segment, segments.take(index))
            current = when {
                existing == null -> createDocument(
                    current,
                    segment,
                    Document.MIME_TYPE_DIR,
                    segments.take(index),
                ) ?: return null

                existing.mimeType == Document.MIME_TYPE_DIR -> existing
                else -> return null
            }
        }
        return current
    }

    private fun createDocument(
        parent: SafDocument,
        name: String,
        mimeType: String,
        parentSegments: List<String>,
    ): SafDocument? {
        val uri = runCatching {
            DocumentsContract.createDocument(resolver, parent.documentUri, mimeType, name)
        }.getOrNull() ?: return null
        val id = runCatching { DocumentsContract.getDocumentId(uri) }.getOrNull() ?: return null
        return queryDocument(parent.treeUri, id, parentSegments)
    }

    private fun findChild(
        treeUri: Uri,
        parentId: String,
        name: String,
        parentSegments: List<String> = emptyList(),
    ): SafDocument? = queryChildren(treeUri, parentId, parentSegments).firstOrNull { it.name == name }

    private fun queryDocument(treeUri: Uri, documentId: String, parentSegments: List<String>): SafDocument? {
        val uri = DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)
        return resolver.query(uri, PROJECTION, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            cursor.toSafDocument(treeUri, parentSegments)
        }
    }

    private fun queryChildren(
        treeUri: Uri,
        parentId: String,
        parentSegments: List<String> = emptyList(),
    ): List<SafDocument> {
        val uri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentId)
        return resolver.query(uri, PROJECTION, null, null, null)?.use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.toSafDocument(treeUri, parentSegments))
            }
        } ?: emptyList()
    }

    private fun android.database.Cursor.toSafDocument(
        treeUri: Uri,
        parentSegments: List<String>,
    ): SafDocument {
        val id = getString(0)
        return SafDocument(
            treeUri = treeUri,
            documentId = id,
            documentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, id),
            name = getString(1),
            mimeType = getString(2),
            size = if (isNull(3)) 0L else getLong(3),
            lastModified = if (isNull(4)) 0L else getLong(4),
            parentSegments = parentSegments,
        )
    }

    private fun parse(path: String): ParsedSafPath? {
        val normalized = path.replace('\\', '/')
        val markerIndex = normalized.indexOf(SAF_LINK_SUFFIX)
        if (markerIndex < 0) return null
        val key = normalized.substring(0, markerIndex + SAF_LINK_SUFFIX.length).substringAfterLast('/')
        val treeUri = trees[key] ?: return null
        val relative = normalized.substring(markerIndex + SAF_LINK_SUFFIX.length).trim('/')
        val segments = relative.split('/').filter { it.isNotBlank() && it != "." }
        if (segments.any { it == ".." }) return null
        return ParsedSafPath(treeUri, segments)
    }

    private data class ParsedSafPath(val treeUri: Uri, val segments: List<String>)

    private data class SafDocument(
        val treeUri: Uri,
        val documentId: String,
        val documentUri: Uri,
        val name: String,
        val mimeType: String,
        val size: Long,
        val lastModified: Long,
        val parentSegments: List<String>,
    )

    companion object {
        private const val SAF_LINK_SUFFIX = ".[saflink]"
        private val PROJECTION = arrayOf(
            Document.COLUMN_DOCUMENT_ID,
            Document.COLUMN_DISPLAY_NAME,
            Document.COLUMN_MIME_TYPE,
            Document.COLUMN_SIZE,
            Document.COLUMN_LAST_MODIFIED,
        )
    }
}
