package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.logger
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

enum class P2PDiscoverySource(val id: String) {
    GOSSIPSUB("gossipsub"),
    MDNS("mdns"),
    SERVICE("service"),
    DHT("dht"),
    MANUAL("manual")
}

data class P2PDiscoveryResult(
    val room: P2PRoomAdvertisement,
    val source: P2PDiscoverySource,
    val trustScore: Int = 0,
    val receivedAtMs: Long = System.currentTimeMillis()
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
    companion object {
        private const val MAX_BYTES = 262144
    }

    fun fetchPeers(): List<String> {
        if (urls.isEmpty()) return emptyList()
        return urls.flatMap { fetchPeers(it) }.filter { it.startsWith("/") }.distinct()
    }

    private fun fetchPeers(url: String): List<String> {
        return runCatching {
            val apiUrl = normalizeNodesUrl(url)
            val bytes = fetchBytes(apiUrl)
            val listType = objectMapper.typeFactory
                .constructCollectionType(List::class.java, P2PBootstrapNode::class.java)
            val nodes: List<P2PBootstrapNode> = objectMapper.readValue(bytes, listType)
            nodes.flatMap { it.addresses }
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
        return connection.inputStream.use { readBytesLimited(it, MAX_BYTES) }
    }
}

class P2PServiceDiscoveryProvider(
    private val config: P2PConfig.ServiceDiscoveryConfig,
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
            val rooms = parseRooms(bytes)
            val now = System.currentTimeMillis()
            rooms
                .asSequence()
                .take(config.maxRoomsPerUrl.coerceAtLeast(1))
                .filter { it.isValid() }
                .filterNot { it.isExpired(now) }
                .map {
                    it.lastSeenTimeMs = now
                    P2PDiscoveryResult(it, P2PDiscoverySource.SERVICE, trustScore = 30, receivedAtMs = now)
                }
                .toList()
        }.onFailure { e ->
            GameEngine.log("Lobby service discovery failed: $url - ${e.message}")
        }.getOrDefault(emptyList())
    }

    private fun fetchBytes(url: String): ByteArray {
        val connection = URL(normalizeRoomsUrl(url)).openConnection() as HttpURLConnection
        connection.connectTimeout = config.timeoutMs.coerceAtLeast(1000)
        connection.readTimeout = config.timeoutMs.coerceAtLeast(1000)
        connection.instanceFollowRedirects = true
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("User-Agent", "RWX-P2P-Discovery")
        return connection.inputStream.use { readBytesLimited(it, config.maxBytes.coerceAtLeast(1024)) }
    }

    private fun parseRooms(bytes: ByteArray): List<P2PRoomAdvertisement> {
        val listType = objectMapper.typeFactory
            .constructCollectionType(List::class.java, P2PRoomAdvertisement::class.java)
        return objectMapper.readValue(bytes, listType)
    }
}

class P2PServicePublisher(
    private val config: P2PConfig.ServicePublishConfig,
    private val serviceUrls: List<String>,
    private val objectMapper: ObjectMapper = ObjectMapper().configure(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false
    )
) {
    fun isEnabled(): Boolean {
        return config.enable && serviceUrls.isNotEmpty()
    }

    fun publishRoom(room: P2PRoomAdvertisement?): Boolean {
        if (!isEnabled() || room == null) return false
        val now = System.currentTimeMillis()
        val payload = room.copy(
            lastSeenTimeMs = now,
            expiresAtMs = now + config.roomTtlMs.coerceAtLeast(config.updateIntervalMs * 3)
        )
        var success = false
        for (url in serviceUrls) {
            if (upsertRoom(url, payload)) {
                success = true
            }
        }
        logger.debug { "room schema:\n $payload" }
        return success
    }

    fun closeRoom(room: P2PRoomAdvertisement?): Boolean {
        if (!isEnabled() || !config.deleteOnClose || room == null) return false
        val roomId = room.roomId ?: return false
        var success = false
        for (url in serviceUrls) {
            if (deleteRoom(url, roomId)) {
                success = true
            }
        }
        return success
    }

    private fun upsertRoom(baseUrl: String, room: P2PRoomAdvertisement): Boolean {
        val roomId = room.roomId ?: return false
        return runCatching {
            val url = buildRoomUrl(baseUrl, roomId, upsert = true)
            request(url, "PUT", objectMapper.writeValueAsBytes(room))
            true
        }.onFailure { e ->
            GameEngine.log("Lobby service publish failed: $baseUrl - ${e.message}")
        }.getOrDefault(false)
    }

    private fun deleteRoom(baseUrl: String, roomId: String): Boolean {
        return runCatching {
            val url = buildRoomUrl(baseUrl, roomId, upsert = false)
            request(url, "DELETE", null)
            true
        }.onFailure { e ->
            GameEngine.log("Lobby service delete failed: $baseUrl - ${e.message}")
        }.getOrDefault(false)
    }

    private fun buildRoomUrl(baseUrl: String, roomId: String, upsert: Boolean): String {
        val roomsUrl = normalizeRoomsUrl(baseUrl)
        val suffix = "/$roomId"
        val query = if (upsert) "?upsert=1" else ""
        return roomsUrl + suffix + query
    }

    private fun request(url: String, method: String, body: ByteArray?): ByteArray {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.connectTimeout = config.timeoutMs.coerceAtLeast(1000)
        connection.readTimeout = config.timeoutMs.coerceAtLeast(1000)
        connection.instanceFollowRedirects = true
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("User-Agent", "RWX-P2P-Discovery")
        if (body != null) {
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.outputStream.use { it.write(body) }
        }
        val status = connection.responseCode
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val bytes = stream?.use { readBytesLimited(it, 65536) } ?: ByteArray(0)
        if (status !in 200..299) {
            throw IOException("Lobby service HTTP $status: ${bytes.toString(Charsets.UTF_8).take(300)}")
        }
        return bytes
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

private fun normalizeBaseUrl(url: String): String {
    return url.trim().trimEnd('/')
}

private fun normalizeRoomsUrl(url: String): String {
    val base = normalizeBaseUrl(url)
    if (base.isBlank()) return base
    return if (base.endsWith("/rooms")) base else "$base/rooms"
}

private fun normalizeNodesUrl(url: String): String {
    val base = normalizeBaseUrl(url)
    if (base.isBlank()) return base
    return if (base.endsWith("/nodes")) base else "$base/nodes"
}

private fun readBytesLimited(input: InputStream, maxBytes: Int): ByteArray {
    val output = ByteArrayOutputStream()
    val buffer = ByteArray(8192)
    val limit = maxBytes.coerceAtLeast(1024)
    var total = 0
    while (true) {
        val read = input.read(buffer)
        if (read < 0) break
        total += read
        if (total > limit) {
            throw IllegalArgumentException("Lobby service response is too large")
        }
        output.write(buffer, 0, read)
    }
    return output.toByteArray()
}
