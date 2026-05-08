package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.rts.gameFramework.GameEngine
import com.fasterxml.jackson.annotation.JsonIgnore

data class P2PRoomAdvertisement(
    var magic: String = "rwx-p2p-lobby",
    var schema: Int = 1,
    var type: String = "room_announce",
    var roomId: String? = null,
    var hostPeerId: String? = null,
    var createdBy: String? = null,
    var gameVersionCode: Int = 0,
    var gameVersionString: String? = null,
    var requiresPassword: Boolean = false,
    var transport: String = "webrtc-datachannel",
    var gamePort: Int = 5123,
    var mapPath: String? = null,
    var gameMode: String? = null,
    var gameState: String? = null,
    var currentPlayers: Int = 0,
    var maxPlayers: Int = 8,
    var hasMods: Boolean = false,
    var modsRequired: String? = null,
    var libp2pBootstrapPeers: MutableList<String> = mutableListOf(),
    var libp2pDirectAddresses: MutableList<String> = mutableListOf(),
    var libp2pMappedAddresses: MutableList<String> = mutableListOf(),
    var webrtcSignaling: String? = null,
    var webrtcIceServers: MutableList<String> = mutableListOf(),
    var discoverySources: MutableList<String> = mutableListOf(),
    var expiresAtMs: Long = 0,
    var seq: Long = 0,
    @Transient var lastSeenTimeMs: Long = 0,
) {
    @JsonIgnore
    fun isValid(): Boolean {
        return magic == "rwx-p2p-lobby" && schema == 1 && !roomId.isNullOrBlank() && !hostPeerId.isNullOrBlank()
    }

    @JsonIgnore
    fun isExpired(nowMs: Long): Boolean = expiresAtMs in 1..<nowMs

    @JsonIgnore
    fun isVersionCompatible(): Boolean {
        val gameEngine = GameEngine.getInstance() ?: return false
        return gameEngine.getVersionCode(true) == gameVersionCode
    }

    @JsonIgnore
    fun getConnectAddress(): String = "127.0.0.1:$gamePort"

    @JsonIgnore
    fun getMapDisplayName(): String {
        val value = mapPath ?: return "<No Map>"
        val fileName = value.substringAfterLast('/').substringAfterLast('\\')
        return fileName.ifBlank { value }
    }

    @JsonIgnore
    fun getInfoText(): String {
        val parts = mutableListOf<String>()
        parts += "Host: ${createdBy ?: "?"}"
        parts += "Map: ${getMapDisplayName()}"
        parts += buildString {
            append("Version: v")
            append(gameVersionString ?: "?")
            if (!isVersionCompatible()) {
                append(" (different game version!)")
            }
        }
        parts += "Transport: $transport"
        if (requiresPassword) {
            parts += "Password Required"
        }
        if (hasMods && !modsRequired.isNullOrBlank()) {
            parts += "Mods Needed: $modsRequired"
        }
        if (!webrtcSignaling.isNullOrBlank()) {
            parts += "WebRTC DataChannel: available"
        }
        if (discoverySources.isNotEmpty()) {
            parts += "Discovery: ${discoverySources.joinToString(", ")}"
        }
        if (webrtcIceServers.isNotEmpty()) {
            parts += "ICE Servers:"
            webrtcIceServers.forEach { parts += " - $it" }
        }
        if (libp2pMappedAddresses.isNotEmpty()) {
            parts += "Direct fallback: available"
        }
        return parts.joinToString("\n")
    }
}
