package io.github.rwx.app

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.MasterServerClient
import com.corrodinggames.rts.gameFramework.network.ServerInfo
import io.github.rwx.logger
import io.github.rwx.map.MapMetadata
import io.github.rwx.p2p.P2PLobbyService
import io.github.rwx.p2p.P2PRoomAdvertisement
import io.github.rwx.ui.*

internal class MultiplayerLobbyController(
    private val sceneHost: MultiplayerSceneHost,
) {
    var activeLobbyKind: MultiplayerLobbyKind = MultiplayerLobbyKind.Original
        private set

    private var latestRooms: List<MultiplayerRoomItem> = emptyList()

    fun roomById(roomId: String): MultiplayerRoomItem? =
        latestRooms.firstOrNull { it.roomId == roomId }

    fun requestRefresh() {
        if (activeLobbyKind == MultiplayerLobbyKind.Original) {
            requestOriginalRefresh()
        } else {
            requestP2PRefresh()
        }
    }

    fun switchLobby(lobbyKind: MultiplayerLobbyKind) {
        if (activeLobbyKind == lobbyKind) {
            updateActiveRooms()
            return
        }
        activeLobbyKind = lobbyKind
        requestRefresh()
    }

    fun handleOriginalRoomListRefresh() {
        if (activeLobbyKind == MultiplayerLobbyKind.Original) {
            updateOriginalRooms()
        }
    }

    fun handleP2PRoomListRefresh() {
        if (activeLobbyKind == MultiplayerLobbyKind.P2P) {
            updateP2PRooms()
        }
    }

    private fun requestOriginalRefresh() {
        val gameEngine = GameEngine.getInstance()
        if (gameEngine?.networkEngine == null) {
            updateOriginalRooms()
            return
        }
        runCatching {
            MasterServerClient.loadServerListAsync {
                CoreUiEventQueue.requestOriginalRoomListRefresh()
            }
        }.onFailure { error ->
            logger.warn(error) { "Original room refresh failed" }
            sceneHost.updateRooms(
                emptyList(),
                "Original lobby unavailable: ${error.message ?: error.javaClass.simpleName}",
                MultiplayerLobbyKind.Original,
            )
            return
        }
        updateOriginalRooms("Searching for rooms...")
    }

    private fun requestP2PRefresh() {
        val lobby = P2PLobbyService.getInstance()
        runCatching {
            lobby.inLobby = true
            lobby.startIfNeeded()
            lobby.requestRefresh()
        }.onFailure { error ->
            logger.warn { "P2P room refresh failed: ${error.message}" }
            sceneHost.updateRooms(
                emptyList(),
                "P2P unavailable: ${error.message ?: error.javaClass.simpleName}",
                MultiplayerLobbyKind.P2P,
            )
            return
        }
        updateP2PRooms("Searching for rooms...")
    }

    private fun updateActiveRooms(statusText: String = "") {
        when (activeLobbyKind) {
            MultiplayerLobbyKind.Original -> updateOriginalRooms(statusText)
            MultiplayerLobbyKind.P2P -> updateP2PRooms(statusText)
        }
    }

    private fun updateP2PRooms(statusText: String = "") {
        val rooms = P2PLobbyService.getInstance().getRooms().map(::p2pRoomToMultiplayerItem)
        latestRooms = rooms
        sceneHost.updateRooms(
            rooms = rooms,
            statusText = statusText.ifBlank { if (rooms.isEmpty()) "No rooms found" else "" },
            lobbyKind = MultiplayerLobbyKind.P2P,
        )
    }

    private fun updateOriginalRooms(statusText: String = "") {
        val gameEngine = GameEngine.getInstance()
        if (gameEngine?.networkEngine == null) {
            latestRooms = emptyList()
            sceneHost.updateRooms(
                rooms = emptyList(),
                statusText = "Original lobby requires the RW engine to FastArrayList loaded",
                lobbyKind = MultiplayerLobbyKind.Original,
            )
            return
        }
        val rooms = runCatching {
            ServerListUiBridge.getServerList()
                .filterIsInstance<ServerInfo>()
                .map(::serverInfoToMultiplayerItem)
        }.getOrElse { error ->
            logger.warn(error) { "Original room list read failed" }
            latestRooms = emptyList()
            sceneHost.updateRooms(
                rooms = emptyList(),
                statusText = "Original lobby unavailable: ${error.message ?: error.javaClass.simpleName}",
                lobbyKind = MultiplayerLobbyKind.Original,
            )
            return
        }
        latestRooms = rooms
        sceneHost.updateRooms(
            rooms = rooms,
            statusText = statusText.ifBlank { if (rooms.isEmpty()) "No rooms found" else "" },
            lobbyKind = MultiplayerLobbyKind.Original,
        )
    }
}

internal fun p2pRoomToMultiplayerItem(room: P2PRoomAdvertisement): MultiplayerRoomItem =
    MultiplayerRoomItem(
        roomId = room.roomId.orEmpty(),
        hostName = room.createdBy ?: "Unknown",
        mapName = room.getMapDisplayName().withRwxModeSuffix(room.requiredRwxFeatures),
        playersLabel = "${room.currentPlayers.coerceAtLeast(0)}/${room.maxPlayers.coerceAtLeast(0)}",
        versionLabel = room.gameVersionString?.let { "v$it" } ?: "Unknown",
        requiresPassword = room.requiresPassword,
        hasMods = room.hasMods,
        transportLabel = when {
            !room.webrtcSignaling.isNullOrBlank() -> "WebRTC"
            room.transport.isNotBlank() -> room.transport
            else -> "P2P"
        },
        stateLabel = room.gameState ?: "unknown",
        infoText = room.getInfoText(),
    )

private fun String.withRwxModeSuffix(requiredRwxFeatures: List<String>): String =
    requiredRwxFeatures.ModeLabel()?.let { "$this - $it" } ?: this

internal fun serverInfoToMultiplayerItem(server: ServerInfo): MultiplayerRoomItem =
    MultiplayerRoomItem(
        roomId = server.getConnectDescriptor(),
        hostName = server.createdBy ?: server.publicHost ?: server.lanHost ?: "Unknown",
        mapName = server.mapPath?.let(MapMetadata::getMapName) ?: server.serverMessage ?: "<No Map>",
        playersLabel = "${server.currentPlayers.coerceAtLeast(0)}/${server.maxPlayers.coerceAtLeast(0)}",
        versionLabel = server.gameVersionString?.let { if (it == "ANY") it else "v$it" } ?: "Unknown",
        requiresPassword = server.requiresPassword,
        hasMods = server.hasMods,
        transportLabel = when {
            server.isLanServer -> "LAN"
            server.isDedicatedServer -> "Dedicated"
            server.isPortOpen -> "Internet"
            else -> "Relay"
        },
        stateLabel = server.gameState ?: "unknown",
        joinAddress = server.getConnectDescriptor(),
        originalServerId = server.serverId?.takeIf { it.isNotBlank() },
        infoText = runCatching { server.getInfoText() }.getOrDefault(""),
    )
