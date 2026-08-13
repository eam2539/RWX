package io.github.rwx.mod.asset

import io.github.rwx.mod.assets.AssetPkiFiles
import io.github.rwx.mod.assets.AssetRevocationList
import io.github.rwx.mod.assets.AssetVaultFormat
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

internal fun interface AssetRevocationListFetcher {
    fun fetch(url: String): AssetRevocationList
}

internal object OnlineAssetRevocationListFetcher : AssetRevocationListFetcher {
    private const val TIMEOUT_MILLIS = 10_000L
    private const val BUFFER_SIZE = 16 * 1024

    private val client by lazy {
        HttpClient(CIO) {
            followRedirects = false
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_MILLIS
                connectTimeoutMillis = TIMEOUT_MILLIS
                socketTimeoutMillis = TIMEOUT_MILLIS
            }
        }
    }

    override fun fetch(url: String): AssetRevocationList {
        val validatedUrl = AssetVaultFormat.validateRevocationListUrl(url)
        return try {
            runBlocking {
                val response = client.get(validatedUrl) {
                    header(HttpHeaders.Accept, "application/octet-stream")
                    header(HttpHeaders.AcceptEncoding, "identity")
                    header(HttpHeaders.CacheControl, "no-cache, no-store, max-age=0")
                    header(HttpHeaders.Pragma, "no-cache")
                }
                val channel = response.bodyAsChannel()
                val bytes = try {
                    require(response.status == HttpStatusCode.OK) {
                        "Online revocation server returned HTTP ${response.status.value}"
                    }
                    response.contentLength()?.let { length ->
                        require(length in 1..AssetPkiFiles.MAX_REVOCATION_LIST_FILE_BYTES.toLong()) {
                            "Online revocation list has an invalid size: $length bytes"
                        }
                    }
                    channel.readLimited()
                } finally {
                    channel.cancel(null)
                }
                AssetPkiFiles.readRevocationList(ByteArrayInputStream(bytes))
            }
        } catch (error: Exception) {
            throw OnlineJvmModAssetRevocationCheckException(validatedUrl, error)
        }
    }

    private suspend fun ByteReadChannel.readLimited(): ByteArray {
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(BUFFER_SIZE)
        while (!isClosedForRead) {
            val count = readAvailable(buffer, 0, buffer.size)
            if (count < 0) break
            if (count == 0) continue
            require(output.size() + count <= AssetPkiFiles.MAX_REVOCATION_LIST_FILE_BYTES) {
                "Online revocation list exceeds ${AssetPkiFiles.MAX_REVOCATION_LIST_FILE_BYTES} bytes"
            }
            output.write(buffer, 0, count)
        }
        require(output.size() > 0) { "Online revocation server returned an empty response" }
        return output.toByteArray()
    }
}

class OnlineJvmModAssetRevocationCheckException(url: String, cause: Throwable) : IllegalStateException(
    "Online JVM mod asset revocation check failed for $url: ${cause.message}",
    cause,
)
