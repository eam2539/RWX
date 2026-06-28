package io.github.rwx.net

import io.github.rwx.PlatformStorage
import io.github.rwx.ui.ResourceBrowserItem
import io.github.rwx.ui.ResourceBrowserSource
import io.github.rwx.ui.ResourceBrowserType
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import java.io.File
import java.net.URLDecoder
import java.util.*

class ResourceBrowserRepository(
    private val storage: PlatformStorage,
    private val client: HttpClient = defaultResourceBrowserHttpClient(),
) {
    fun searchBlocking(
        source: ResourceBrowserSource,
        type: ResourceBrowserType,
        page: Int,
        keyword: String,
    ): Result<List<ResourceBrowserItem>> =
        runCatching {
            runBlocking {
                when (source) {
                    ResourceBrowserSource.RtsBoxSearch -> searchRtsBox(type, page, keyword)
                    ResourceBrowserSource.RtsBoxWeeklyDownloads -> searchWeeklyDownloads(type, page)
                }
            }
        }

    fun downloadBlocking(
        item: ResourceBrowserItem,
        progress: (Float) -> Unit,
    ): Result<File> =
        runCatching {
            val url = requireNotNull(item.downloadUrl) { "Missing download URL" }
            val target = runBlocking {
                val response = client.get(url) {
                    timeout {
                        requestTimeoutMillis = RESOURCE_BROWSER_DOWNLOAD_TIMEOUT_MS
                        connectTimeoutMillis = RESOURCE_BROWSER_TIMEOUT_MS
                        socketTimeoutMillis = RESOURCE_BROWSER_DOWNLOAD_SOCKET_TIMEOUT_MS
                    }
                }
                val target = item.downloadTargetFile(storage, response)
                target.parentFile?.mkdirs()
                val contentLength = response.contentLength() ?: -1L
                val channel = response.bodyAsChannel()
                var downloaded = 0L
                val buffer = ByteArray(DOWNLOAD_BUFFER_SIZE)
                target.outputStream().buffered().use { output ->
                    while (!channel.isClosedForRead) {
                        val read = channel.readAvailable(buffer, 0, buffer.size)
                        if (read == -1) break
                        if (read == 0) continue
                        output.write(buffer, 0, read)
                        downloaded += read
                        if (contentLength > 0L) {
                            progress((downloaded.toFloat() / contentLength.toFloat()).coerceIn(0.0f, 1.0f))
                        }
                    }
                }
                target
            }
            progress(1.0f)
            target
        }.onFailure {
            progress(-1.0f)
        }

    private suspend fun searchRtsBox(
        type: ResourceBrowserType,
        page: Int,
        keyword: String,
    ): List<ResourceBrowserItem> {
        val text = client.post(RTSBOX_SEARCH_URL) {
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("bbs_id", if (type == ResourceBrowserType.Mod) "4" else "5")
                        append("keyword", keyword)
                        append("page", page.coerceAtLeast(1).toString())
                    }
                )
            )
        }.bodyAsText()
        val root = Json.parseToJsonElement(text).jsonObject
        val data = root["data"] as? JsonArray ?: return emptyList()
        return data.mapIndexedNotNull { index, element ->
            val info = element.jsonObjectOrNull() ?: return@mapIndexedNotNull null
            val title = info.string("title")?.takeIf { it.isNotBlank() } ?: return@mapIndexedNotNull null
            ResourceBrowserItem(
                id = "rtsbox-search-${type.name}-$page-$index-${title.hashCode()}",
                title = title,
                type = type,
                sourceName = ResourceBrowserSource.RtsBoxSearch.displayName,
                bbsUrl = info.string("bbsurl"),
                imageUrl = info.string("img")?.resourcePreviewUrl(),
                downloadUrl = info.firstString("downurl", "file_url", "download_url", "downloadUrl"),
                downloadCount = info.firstInt("download_num", "downloadNum", "download", "downnum", "downloads"),
            )
        }
    }

    private suspend fun searchWeeklyDownloads(
        type: ResourceBrowserType,
        page: Int,
    ): List<ResourceBrowserItem> {
        val text = client.post(RTSBOX_WEEKLY_DOWNLOADS_URL) {
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("catID", if (type == ResourceBrowserType.Mod) "mod" else "map")
                        append("type", "WeekDownload")
                        append("page", page.coerceAtLeast(1).toString())
                    }
                )
            )
        }.bodyAsText()
        val data = Json.parseToJsonElement(text).jsonArray
        return data.mapNotNull { element ->
            val info = element.jsonObjectOrNull() ?: return@mapNotNull null
            val postId = info.int("postID") ?: return@mapNotNull null
            val title = info.string("title")?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            ResourceBrowserItem(
                id = "rtsbox-weekly-${type.name}-$postId",
                title = title,
                type = type,
                sourceName = ResourceBrowserSource.RtsBoxWeeklyDownloads.displayName,
                bbsUrl = "https://www.rtsbox.cn/$postId.html",
                imageUrl = info.string("img_url")?.resourcePreviewUrl(),
                downloadUrl = info.firstString("file_url", "downurl", "download_url", "downloadUrl"),
                downloadCount = info.firstInt("download_num", "downloadNum", "download", "downnum", "downloads"),
            )
        }
    }

    private fun ResourceBrowserItem.downloadTargetFile(storage: PlatformStorage, response: HttpResponse): File {
        val fileName = response.downloadFileName(this)
        return when (type) {
            ResourceBrowserType.Mod -> uniqueDestination(storage.unitsDir.file, fileName)
            ResourceBrowserType.Map -> uniqueDestination(storage.mapsDir.file, fileName)
        }
    }
}

private fun defaultResourceBrowserHttpClient(): HttpClient =
    HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = RESOURCE_BROWSER_TIMEOUT_MS
            connectTimeoutMillis = RESOURCE_BROWSER_TIMEOUT_MS
            socketTimeoutMillis = RESOURCE_BROWSER_TIMEOUT_MS
        }
    }

private fun JsonElement.jsonObjectOrNull(): JsonObject? = this as? JsonObject

private fun JsonObject.string(name: String): String? =
    this[name]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }

private fun JsonObject.int(name: String): Int? =
    this[name]?.jsonPrimitive?.intOrNull

private fun JsonObject.firstString(vararg names: String): String? =
    names.firstNotNullOfOrNull { string(it) }

private fun JsonObject.firstInt(vararg names: String): Int? =
    names.firstNotNullOfOrNull { int(it) }

private fun String.resourcePreviewUrl(): String =
    removeSuffix("!webp")

private fun HttpResponse.downloadFileName(item: ResourceBrowserItem): String {
    val dispositionName = headers["Content-Disposition"]?.let(::contentDispositionFileName)
    val urlName = request.url.encodedPath.substringAfterLast('/').substringBefore('?').takeIf { it.isNotBlank() }
    val decoded = (dispositionName ?: urlName)
        ?.let { runCatching { URLDecoder.decode(it, Charsets.UTF_8.name()) }.getOrDefault(it) }
        ?.substringBefore('?')
        ?.takeIf { it.isNotBlank() }
    val safeDecoded = decoded?.safeResourceFileName()
    if (safeDecoded != null && safeDecoded.extensionFor(item.type) != null) {
        return safeDecoded
    }

    val extension = safeDecoded?.extensionFor(item.type) ?: item.type.defaultExtension
    return "${item.title.safeResourceFileName().substringBeforeLast('.', item.title.safeResourceFileName())}.$extension"
}

private fun contentDispositionFileName(value: String): String? {
    val parts = value.split(';').map { it.trim() }
    val encoded = parts.firstOrNull { it.startsWith("filename*=", ignoreCase = true) }
        ?.substringAfter('=')
        ?.substringAfter("''")
        ?.trim('"')
        ?.takeIf { it.isNotBlank() }
    if (encoded != null) return encoded
    return parts.firstOrNull { it.startsWith("filename=", ignoreCase = true) }
        ?.substringAfter('=')
        ?.trim('"')
        ?.takeIf { it.isNotBlank() }
}

private fun String.safeResourceFileName(): String =
    replace(Regex("""[\\/:*?"<>|]"""), "_").trim().ifBlank { "resource" }

private fun String.extensionFor(type: ResourceBrowserType): String? {
    val extension = substringAfterLast('.', "").lowercase(Locale.ROOT).takeIf { it.isNotBlank() } ?: return null
    return extension.takeIf { it in type.allowedExtensions }
}

private val ResourceBrowserType.defaultExtension: String
    get() = when (this) {
        ResourceBrowserType.Mod -> "rwmod"
        ResourceBrowserType.Map -> "tmx"
    }

private val ResourceBrowserType.allowedExtensions: Set<String>
    get() = when (this) {
        ResourceBrowserType.Mod -> setOf("rwmod", "zip", "jar", "ini")
        ResourceBrowserType.Map -> setOf("tmx")
    }

private fun uniqueDestination(root: File, fileName: String): File {
    root.mkdirs()
    val first = File(root, fileName)
    if (!first.exists()) return first
    val base = first.nameWithoutExtension.ifBlank { first.name }
    val extension = first.extension.takeIf { it.isNotBlank() }?.let { ".$it" }.orEmpty()
    for (index in 1..999) {
        val candidate = File(root, "$base-$index$extension")
        if (!candidate.exists()) return candidate
    }
    return File(root, "$base-${System.currentTimeMillis()}$extension")
}

private const val RESOURCE_BROWSER_TIMEOUT_MS: Long = 10000L
private const val RESOURCE_BROWSER_DOWNLOAD_TIMEOUT_MS: Long = 10 * 60 * 1000L
private const val RESOURCE_BROWSER_DOWNLOAD_SOCKET_TIMEOUT_MS: Long = 60 * 1000L
private const val DOWNLOAD_BUFFER_SIZE: Int = 64 * 1024
private const val RTSBOX_SEARCH_URL = "https://www.rtsbox.cn/api/search_bbs.php"
private const val RTSBOX_WEEKLY_DOWNLOADS_URL = "https://www.rtsbox.cn/api/lt_api/data.php"
