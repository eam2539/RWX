package io.github.rwx.net

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*

class UpdateRepository(
    private val client: HttpClient = defaultUpdateHttpClient(),
) {
    suspend fun checkLatestRelease(currentVersion: String): Result<UpdateCheckResponse> =
        runCatching {
            val response = client.get(GITHUB_RELEASES_API_URL) {
                header("Accept", "application/vnd.github+json")
                header("User-Agent", "RWX")
            }
            check(response.status.isSuccess()) {
                "GitHub returned HTTP ${response.status.value}"
            }
            val releases = updateJson.decodeFromString<List<GithubReleaseResponse>>(response.bodyAsText())
            val latest = releases.firstOrNull { !it.draft }?.toUpdateRelease()
            UpdateCheckResponse(
                latestRelease = latest,
                isUpdateAvailable = latest?.let { isNewerVersion(it.tagName, currentVersion) } == true,
            )
        }
}

data class UpdateCheckResponse(
    val latestRelease: UpdateRelease?,
    val isUpdateAvailable: Boolean,
)

data class UpdateRelease(
    val tagName: String,
    val name: String,
    val body: String,
    val htmlUrl: String,
    val prerelease: Boolean,
    val publishedAt: String?,
) {
    val displayVersion: String
        get() = name.takeIf { it.isNotBlank() } ?: tagName
}

@Serializable
private data class GithubReleaseResponse(
    @SerialName("tag_name")
    val tagName: String = "",
    val name: String? = null,
    val body: String? = null,
    @SerialName("html_url")
    val htmlUrl: String = "",
    val draft: Boolean = false,
    val prerelease: Boolean = false,
    @SerialName("published_at")
    val publishedAt: String? = null,
)

private fun GithubReleaseResponse.toUpdateRelease(): UpdateRelease =
    UpdateRelease(
        tagName = tagName,
        name = name.orEmpty(),
        body = body.orEmpty(),
        htmlUrl = htmlUrl,
        prerelease = prerelease,
        publishedAt = publishedAt,
    )

private fun defaultUpdateHttpClient(): HttpClient =
    HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = UPDATE_CHECK_TIMEOUT_MS
            connectTimeoutMillis = UPDATE_CHECK_TIMEOUT_MS
            socketTimeoutMillis = UPDATE_CHECK_TIMEOUT_MS
        }
    }

private fun isNewerVersion(remoteVersion: String, currentVersion: String): Boolean {
    val remote = remoteVersion.normalizedVersion()
    val current = currentVersion.normalizedVersion()
    if (remote.canonical == current.canonical) return false

    val remoteParts = remote.numericParts
    val currentParts = current.numericParts
    if (remoteParts.isEmpty() || currentParts.isEmpty()) {
        return remote.canonical != current.canonical
    }

    val max = maxOf(remoteParts.size, currentParts.size)
    for (index in 0 until max) {
        val remotePart = remoteParts.getOrElse(index) { 0 }
        val currentPart = currentParts.getOrElse(index) { 0 }
        if (remotePart != currentPart) return remotePart > currentPart
    }
    return false
}

private data class NormalizedVersion(
    val canonical: String,
    val numericParts: List<Int>,
)

private fun String.normalizedVersion(): NormalizedVersion {
    val canonical = trim()
        .removePrefix("v")
        .removePrefix("V")
        .lowercase(Locale.ROOT)
    val numericParts = canonical
        .substringBefore('-')
        .substringBefore('+')
        .split('.')
        .mapNotNull { part -> part.takeWhile(Char::isDigit).takeIf { it.isNotBlank() }?.toIntOrNull() }
    return NormalizedVersion(canonical, numericParts)
}

private val updateJson = Json {
    ignoreUnknownKeys = true
}

private const val GITHUB_RELEASES_API_URL = "https://api.github.com/repos/eam2539/RWX/releases"
private const val UPDATE_CHECK_TIMEOUT_MS: Long = 10000L
