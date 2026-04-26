package com.corrodinggames.rts.gameFramework.p2p

import com.corrodinggames.rts.gameFramework.GameEngine

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
    var transport: String = "rudp-over-easytier",
    var gamePort: Int = 5123,
    var mapPath: String? = null,
    var gameMode: String? = null,
    var gameState: String? = null,
    var currentPlayers: Int = 0,
    var maxPlayers: Int = 8,
    var hasMods: Boolean = false,
    var modsRequired: String? = null,
    var networkName: String? = null,
    var networkSecret: String? = null,
    var hostVirtualIp: String? = null,
    var libp2pBootstrapPeers: MutableList<String> = mutableListOf(),
    var easyTierPeers: MutableList<String> = mutableListOf(),
    var expiresAtMs: Long = 0,
    var seq: Long = 0,
    @Transient var lastSeenTimeMs: Long = 0,
) {
    fun isValid(): Boolean {
        return magic == "rwx-p2p-lobby" && schema == 1 && !roomId.isNullOrBlank() && !hostVirtualIp.isNullOrBlank()
    }

    fun isExpired(nowMs: Long): Boolean = expiresAtMs > 0 && nowMs > expiresAtMs

    fun isVersionCompatible(): Boolean {
        val gameEngine = GameEngine.getInstance() ?: return false
        return gameEngine.getVersionCode(true) == gameVersionCode
    }

    fun getConnectAddress(): String = "$hostVirtualIp:$gamePort"

    fun getMapDisplayName(): String {
        val value = mapPath ?: return "<No Map>"
        val fileName = value.substringAfterLast('/').substringAfterLast('\\')
        return fileName.ifBlank { value }
    }

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
        parts += "EasyTier IP: ${hostVirtualIp ?: "?"}"
        if (requiresPassword) {
            parts += "Password Required"
        }
        if (hasMods && !modsRequired.isNullOrBlank()) {
            parts += "Mods Needed: $modsRequired"
        }
        if (libp2pBootstrapPeers.isNotEmpty()) {
            parts += "Libp2p Peers:"
            libp2pBootstrapPeers.forEach { parts += " - $it" }
        }
        if (easyTierPeers.isNotEmpty()) {
            parts += "EasyTier Peers:"
            easyTierPeers.forEach { parts += " - $it" }
        }
        return parts.joinToString("\n")
    }
}
