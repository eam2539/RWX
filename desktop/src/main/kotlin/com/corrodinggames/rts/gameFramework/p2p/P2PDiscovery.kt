package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URL

enum class P2PDiscoverySource(val id: String) {
    GOSSIPSUB("gossipsub"),
    MDNS("mdns"),
    GITHUB("github"),
    DHT("dht"),
    MANUAL("manual")
}

data class P2PDiscoveryResult(
    val room: P2PRoomAdvertisement,
    val source: P2PDiscoverySource,
    val trustScore: Int = 0,
    val receivedAtMs: Long = System.currentTimeMillis()
)

data class P2PGithubRoomIndex(
    var schema: Int = 1,
    var generatedAt: Long = 0,
    var ttlMs: Long = 30000L,
    var rooms: MutableList<P2PRoomAdvertisement> = mutableListOf()
)

data class P2PBootstrapNodeIndex(
    var schema: Int = 1,
    var generatedAt: Long = 0,
    var nodes: MutableList<P2PBootstrapNode> = mutableListOf()
)

data class P2PBootstrapNode(
    var name: String? = null,
    var kind: String? = null,
    var stability: String? = null,
    var addresses: MutableList<String> = mutableListOf()
)

class P2PBootstrapNodeSource(
    private val urls: List<String>,
    private val objectMapper: ObjectMapper = ObjectMapper().configure(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false
    )
) {
    fun fetchPeers(): List<String> {
        return urls.flatMap { fetchPeers(it) }.filter { it.startsWith("/") }.distinct()
    }

    private fun fetchPeers(url: String): List<String> {
        return runCatching {
            val apiUrl = normalizeGistApiUrl(url)
            val bytes = fetchBytes(apiUrl)
            val json = objectMapper.readTree(bytes)
            val content = if (apiUrl.startsWith("https://api.github.com/gists/")) {
                json.path("files").path("rwx-p2p-nodes.json").path("content").asText()
            } else {
                bytes.toString(Charsets.UTF_8)
            }
            val index = objectMapper.readValue(content, P2PBootstrapNodeIndex::class.java)
            if (index.schema != 1) return emptyList()
            index.nodes.flatMap { it.addresses }
        }.onFailure { e ->
            GameEngine.log("P2P bootstrap node source failed: $url - ${e.message}")
        }.getOrDefault(emptyList())
    }

    private fun fetchBytes(url: String): ByteArray {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.instanceFollowRedirects = true
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("User-Agent", "RWX-P2P-Discovery")
        return connection.inputStream.use { it.readBytes() }
    }

    private fun normalizeGistApiUrl(url: String): String {
        if (url.startsWith("https://api.github.com/gists/")) return url
        if (url.startsWith("https://gist.github.com/")) {
            val parts = url.removePrefix("https://gist.github.com/").split('/').filter { it.isNotBlank() }
            if (parts.size >= 2) return "https://api.github.com/gists/${parts[1]}"
        }
        return url
    }
}

data class P2PGithubRoomRequest(
    var magic: String = "rwx-p2p-github-request",
    var schema: Int = 1,
    var action: String = "upsert",
    var generatedAt: Long = 0,
    var room: P2PRoomAdvertisement? = null
) {
    fun isValid(nowMs: Long): Boolean {
        val currentRoom = room ?: return false
        return magic == "rwx-p2p-github-request" && schema == 1 && currentRoom.isValid() && !currentRoom.isExpired(nowMs)
    }
}

class P2PGithubDiscoveryProvider(
    private val config: P2PConfig.GithubDiscoveryConfig,
    private val objectMapper: ObjectMapper = ObjectMapper().configure(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false
    )
) {
    fun fetchRooms(): List<P2PDiscoveryResult> {
        if (!config.enable || config.urls.isEmpty()) return emptyList()
        val results = mutableListOf<P2PDiscoveryResult>()
        for (url in config.urls) {
            results += fetchRooms(url)
        }
        return results
    }

    private fun fetchRooms(url: String): List<P2PDiscoveryResult> {
        return runCatching {
            val bytes = fetchBytes(url)
            val index = objectMapper.readValue(bytes, P2PGithubRoomIndex::class.java)
            if (index.schema != 1) return emptyList()
            val now = System.currentTimeMillis()
            index.rooms
                .asSequence()
                .take(config.maxRoomsPerUrl.coerceAtLeast(1))
                .filter { it.isValid() }
                .filterNot { it.isExpired(now) }
                .map {
                    it.lastSeenTimeMs = now
                    P2PDiscoveryResult(it, P2PDiscoverySource.GITHUB, trustScore = 30, receivedAtMs = now)
                }
                .toList()
        }.onFailure { e ->
            GameEngine.log("GitHub P2P discovery failed: $url - ${e.message}")
        }.getOrDefault(emptyList())
    }

    private fun fetchBytes(url: String): ByteArray {
        val connection = URL(normalizeGithubUrl(url)).openConnection() as HttpURLConnection
        connection.connectTimeout = config.timeoutMs.coerceAtLeast(1000)
        connection.readTimeout = config.timeoutMs.coerceAtLeast(1000)
        connection.instanceFollowRedirects = true
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("User-Agent", "RWX-P2P-Discovery")
        connection.inputStream.use { input ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            var total = 0
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                total += read
                if (total > config.maxBytes.coerceAtLeast(1024)) {
                    throw IllegalArgumentException("GitHub discovery response is too large")
                }
                output.write(buffer, 0, read)
            }
            return output.toByteArray()
        }
    }

    private fun normalizeGithubUrl(url: String): String {
        if (!url.startsWith("https://github.com/") || !url.contains("/blob/")) return url
        return url.replace("https://github.com/", "https://raw.githubusercontent.com/").replace("/blob/", "/")
    }
}

class P2PGithubGistPublisher(
    private val config: P2PConfig.GithubGistPublishConfig,
    private val objectMapper: ObjectMapper = ObjectMapper().configure(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false
    )
) {
    companion object {
        const val DEFAULT_GITHUB_TOKEN = "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"
    }

    private var cachedGistId: String? = null
    private val httpClient: HttpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()

    fun isEnabled(): Boolean {
        return config.enable && resolveToken().isNotBlank() && config.fileName.isNotBlank()
    }

    fun publishRoom(room: P2PRoomAdvertisement?) {
        if (!isEnabled() || room == null) return
        val request = P2PGithubRoomRequest(
            action = "upsert",
            generatedAt = System.currentTimeMillis(),
            room = room
        )
        val gistId = ensureGistId() ?: return
        updateGist(gistId, request)
    }

    fun closeRoom(room: P2PRoomAdvertisement?) {
        if (!isEnabled() || !config.deleteOnClose || room == null) return
        val request = P2PGithubRoomRequest(
            action = "delete",
            generatedAt = System.currentTimeMillis(),
            room = room
        )
        val gistId = ensureGistId() ?: return
        updateGist(gistId, request)
    }

    private fun ensureGistId(): String? {
        cachedGistId?.let { return it }
        if (config.gistId.isNotBlank()) return config.gistId.also { cachedGistId = it }
        val file = File(config.gistIdFile)
        if (file.exists()) {
            val id = file.readText(Charsets.UTF_8).trim()
            if (id.isNotBlank()) return id.also { cachedGistId = it }
        }
        return runCatching {
            val body = mapOf(
                "description" to "RWX P2P room discovery request",
                "public" to config.publicGist,
                "files" to mapOf(config.fileName to mapOf("content" to "{}"))
            )
            val bytes = request("https://api.github.com/gists", "POST", objectMapper.writeValueAsBytes(body))
            val tree = objectMapper.readTree(bytes)
            val gistId = tree.get("id")?.asText().orEmpty()
            if (gistId.isBlank()) null else gistId.also {
                cachedGistId = it
                runCatching { file.writeText(it, Charsets.UTF_8) }
                GameEngine.log("Created RWX P2P GitHub Gist id=$it; saved to ${file.absolutePath}")
            }
        }.onFailure { e ->
            GameEngine.log("Failed to create RWX P2P GitHub Gist: ${e.message}")
        }.getOrNull()
    }

    private fun updateGist(gistId: String, request: P2PGithubRoomRequest) {
        runCatching {
            val body = mapOf(
                "files" to mapOf(
                    config.fileName to mapOf(
                        "content" to objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)
                    )
                )
            )
            request("https://api.github.com/gists/$gistId", "PATCH", objectMapper.writeValueAsBytes(body))
        }.onFailure { e ->
            GameEngine.log("Failed to update RWX P2P GitHub Gist: ${e.message}")
        }
    }

    private fun request(url: String, method: String, body: ByteArray): ByteArray {
        val request = HttpRequest.newBuilder(URI.create(url))
            .timeout(java.time.Duration.ofSeconds(10))
            .header("Accept", "application/vnd.github+json")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${resolveToken()}")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("User-Agent", "RWX-P2P-Discovery")
            .method(method, HttpRequest.BodyPublishers.ofByteArray(body))
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())
        val status = response.statusCode()
        val bytes = response.body() ?: ByteArray(0)
        if (status !in 200..299) {
            throw IOException("GitHub API HTTP $status: ${bytes.toString(Charsets.UTF_8).take(300)}")
        }
        return bytes
    }

    private fun resolveToken(): String {
        return config.token.ifBlank { DEFAULT_GITHUB_TOKEN }
    }
}

class P2PDhtDiscoveryProvider(private val config: P2PConfig.DhtDiscoveryConfig) {
    private var warned = false

    fun fetchRooms(): List<P2PDiscoveryResult> {
        if (!config.enable) return emptyList()
        if (!warned) {
            warned = true
            GameEngine.log("DHT P2P discovery is enabled but unavailable: jvm-libp2p 1.2.0 does not expose Kad-DHT provider APIs")
        }
        return emptyList()
    }

    fun publishRoom(room: P2PRoomAdvertisement?) {
        if (!config.enable || room == null) return
        if (!warned) {
            warned = true
            GameEngine.log("DHT P2P publish skipped: jvm-libp2p 1.2.0 does not expose Kad-DHT provider APIs")
        }
    }
}
