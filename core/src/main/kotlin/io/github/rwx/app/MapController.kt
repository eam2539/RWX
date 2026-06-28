package io.github.rwx.app

import com.corrodinggames.rts.gameFramework.GameEngine
import de.fabmax.kool.scene.Scene
import io.github.rwx.PlatformStorage
import io.github.rwx.i18n.I18n
import io.github.rwx.map.LinkedMapAvailability
import io.github.rwx.map.MapLinkResolver
import io.github.rwx.map.MapMetadata
import io.github.rwx.map.PortalTransferMessage
import io.github.rwx.mod.ModUiRegistry
import io.github.rwx.p2p.P2PLobbyService
import io.github.rwx.render.canvas.KoolCanvasViewport
import io.github.rwx.session.BattleRoomSnapshot
import io.github.rwx.session.GameMemorySnapshot
import io.github.rwx.session.GameSession
import io.github.rwx.ui.*

internal class MapController(
    private val gameSession: GameSession,
    private val koolCanvasScene: Scene,
    private val storage: () -> PlatformStorage?,
    private val viewport: () -> KoolCanvasViewport,
    private val pendingStartMapPath: () -> String?,
    private val setPendingStart: (mapPath: String, failureReturnScreen: AppScreen) -> Unit,
    private val navigateToInGame: () -> Unit,
    private val currentBattleRoomSnapshotForJoin: () -> BattleRoomSnapshot?,
    private val showUnavailableDialog: (String) -> Unit,
    private val showDialogOverGame: (Dialog) -> Unit,
    private val hideDialog: () -> Unit,
    private val state: MapRuntimeState = MapRuntimeState(),
) {
    fun jumpToLinkedMap(event: CoreUiEvent.InGameMapJumpRequested) {
        if (!LinkedMapAvailability.ENABLED) {
            showUnavailableDialog(LinkedMapAvailability.UNAVAILABLE_MESSAGE)
            return
        }
        showUnavailableDialog("RWX multi-map switching is not wired to the HUD yet: ${event.targetMapId}")
    }

    fun canShowMapList(): Boolean =
        state.hasKnownLinkedMaps() ||
                ((P2PLobbyService.getInstance().currentMultiMapAssignments()?.instances?.size ?: 0) > 1)

    fun showMapSwitchDialog() {
        if (!LinkedMapAvailability.ENABLED) {
            showUnavailableDialog(LinkedMapAvailability.UNAVAILABLE_MESSAGE)
            return
        }
        val mapStorage = storage()
        if (mapStorage == null) {
            showUnavailableDialog("RWX map switching is unavailable on this platform.")
            return
        }
        val currentMapPath = gameSession.runningMapPath()
            ?: pendingStartMapPath()
            ?: currentBattleRoomSnapshotForJoin()?.mapPath
        if (currentMapPath.isNullOrBlank()) {
            showUnavailableDialog("No active RWX map.")
            return
        }
        val rootPath = state.graphRootPath?.takeIf { root ->
            state.samePath(root, currentMapPath) ||
                    state.runtimeLinkedMapNames.keys.any { state.samePath(it, currentMapPath) } ||
                    MapLinkResolver.resolveLinkedMapGraph(mapStorage, root).maps.any {
                        state.samePath(it.mapPath, currentMapPath)
                    }
        } ?: currentMapPath
        val graph = MapLinkResolver.resolveLinkedMapGraph(mapStorage, rootPath)
        val mapRows = linkedMapOf<String, String>()
        graph.maps.forEach { node ->
            mapRows[node.mapPath] = node.displayName
        }
        state.runtimeLinkedMapNames.forEach { (path, name) ->
            mapRows.putIfAbsent(path, name)
        }
        mapRows.putIfAbsent(currentMapPath, MapMetadata.getMapName(currentMapPath))
        val runtimeMissingTargets = mutableListOf<String>()
        GameEngine.getInstance()?.missionEngine?.getMapPortalTargetMapIds()?.forEach { targetId ->
            val targetPath = MapLinkResolver.findMapPathById(mapStorage, targetId)
            if (targetPath == null) {
                runtimeMissingTargets += targetId
            } else {
                mapRows.putIfAbsent(targetPath, MapMetadata.getMapName(targetPath))
            }
        }
        state.runtimeLinkedMapNames.clear()
        state.runtimeLinkedMapNames.putAll(mapRows)
        if (mapRows.size <= 1) {
            showUnavailableDialog("This map has no linked RWX maps.")
            return
        }
        val missingTargets = (graph.missingTargetMapIds + runtimeMissingTargets).distinct()
        if (missingTargets.isNotEmpty()) {
            showUnavailableDialog("Linked map missing: ${missingTargets.joinToString(", ")}")
            return
        }
        state.graphRootPath = rootPath
        val assignments = P2PLobbyService.getInstance().currentMultiMapAssignments()
        val localPlayerId = currentLocalPlayerId()
        showDialogOverGame(
            Dialog(
                title = "RWX Maps",
                message = "Select a linked map.",
                infoRows = mapRows.map { (mapPath, displayName) ->
                    val assignment = assignments?.instances?.firstOrNull {
                        state.samePath(it.mapPath, mapPath)
                    }
                    val isCurrent = state.samePath(mapPath, currentMapPath)
                    val isLocalSimulator = assignment != null && assignment.simulatorPlayerId == localPlayerId
                    DialogInfoRow(
                        icon = Icon.Map,
                        label = displayName,
                        value = when {
                            isCurrent -> "Current"
                            assignment != null -> "Sim: ${assignment.simulatorPlayerName}"
                            else -> mapPath.substringAfterLast('/')
                        },
                        emphasis = isCurrent || isLocalSimulator,
                        onPress = {
                            switchMap(mapPath)
                        },
                    )
                },
                buttons = listOf(DialogButton(I18n.common.cancel())),
                scrollableMessage = true,
            )
        )
    }

    fun handlePortalTransfer(transfer: PortalTransferMessage) {
        if (!LinkedMapAvailability.ENABLED) {
            showUnavailableDialog(LinkedMapAvailability.UNAVAILABLE_MESSAGE)
            return
        }
        val targetMapPath = storage()?.let { MapLinkResolver.findMapPathById(it, transfer.targetMapId) }
        if (targetMapPath == null) {
            showUnavailableDialog("Linked map not found: ${transfer.targetMapId}")
            return
        }
        if (GameEngine.getInstance()?.isNetworkGameActive() == true) {
            P2PLobbyService.getInstance().broadcastPortalTransfer(transfer)
            GameEngine.getInstance()?.gameUI?.showMediumPriorityMessage("RWX portal transfer sent")
            return
        }
        state.queuePortalTransfer(targetMapPath, transfer)
        GameEngine.getInstance()?.gameUI?.showMediumPriorityMessage(
            "Unit transferred to ${MapMetadata.getMapName(targetMapPath)}"
        )
    }

    fun drainP2PPortalTransfers() {
        if (!LinkedMapAvailability.ENABLED) {
            P2PLobbyService.getInstance().drainPortalTransfers()
            return
        }
        P2PLobbyService.getInstance().drainPortalTransfers().forEach { transfer ->
            val targetMapPath = storage()?.let {
                MapLinkResolver.findMapPathById(it, transfer.targetMapId)
            } ?: return@forEach
            if (!shouldSimulateMapLocally(targetMapPath)) {
                return@forEach
            }
            state.queuePortalTransfer(targetMapPath, transfer)
        }
    }

    fun applyPendingPortalTransfers() {
        if (!LinkedMapAvailability.ENABLED) return
        val currentMapPath = gameSession.runningMapPath() ?: return
        if (!gameSession.isMapLoaded(currentMapPath)) {
            return
        }
        val transfers = state.removePortalTransfersFor(currentMapPath) ?: return
        transfers.groupBy { it.targetPortalId }.forEach { (portalId, groupedTransfers) ->
            val units = groupedTransfers.flatMap { it.units }
            gameSession.injectTransferredUnits(units, portalId)
        }
    }

    fun ensureP2PAssignedMapLoaded() {
        if (!LinkedMapAvailability.ENABLED) return
        val engine = GameEngine.getInstance() ?: return
        val networkEngine = engine.networkEngine ?: return
        if (!engine.isNetworkGameActive() || !networkEngine.rwxP2PSession) {
            return
        }
        val localPlayerId = currentLocalPlayerId() ?: return
        val assignment = P2PLobbyService.getInstance().currentMultiMapAssignments()
            ?.instances
            ?.firstOrNull { it.simulatorPlayerId == localPlayerId }
            ?: return
        if (state.samePath(gameSession.runningMapPath(), assignment.mapPath) ||
            state.samePath(pendingStartMapPath(), assignment.mapPath)
        ) {
            return
        }
        if (state.samePath(state.appliedP2PAssignedMapPath, assignment.mapPath) &&
            gameSession.isPreparingMap(assignment.mapPath)
        ) {
            return
        }
        requestP2PAssignedMap(assignment.mapPath)
    }

    private fun switchMap(targetMapPath: String) {
        hideDialog()
        val currentMapPath = gameSession.runningMapPath()
        if (state.samePath(currentMapPath, targetMapPath)) {
            return
        }
        if (GameEngine.getInstance()?.isNetworkGameActive() == true) {
            val assignment = P2PLobbyService.getInstance().currentMultiMapAssignments()
                ?.instances
                ?.firstOrNull { state.samePath(it.mapPath, targetMapPath) }
            val localPlayerId = currentLocalPlayerId()
            if (assignment == null || assignment.simulatorPlayerId != localPlayerId) {
                showUnavailableDialog(
                    "This map is simulated by ${assignment?.simulatorPlayerName ?: "another player"}."
                )
                return
            }
            requestP2PAssignedMap(targetMapPath)
            return
        }
        currentMapPath?.let { mapPath ->
            gameSession.captureMemorySnapshot()?.let { snapshot ->
                state.putSnapshot(mapPath, snapshot)
            }
        }
        val snapshot = state.snapshotFor(targetMapPath)
        queueMapLoad(targetMapPath, snapshot)
    }

    private fun requestP2PAssignedMap(targetMapPath: String) {
        queueMapLoad(targetMapPath)
        state.appliedP2PAssignedMapPath = targetMapPath
    }

    private fun queueMapLoad(targetMapPath: String, snapshot: GameMemorySnapshot? = null) {
        val currentBattleRoomConfig = gameSession.activeRendererBattleRoomConfig()
        val effectiveSnapshot = snapshot?.let { savedSnapshot ->
            if (savedSnapshot.battleRoomConfig == null && currentBattleRoomConfig != null) {
                savedSnapshot.copy(battleRoomConfig = currentBattleRoomConfig.copy(mapPath = targetMapPath))
            } else {
                savedSnapshot
            }
        }
        val battleRoomConfig = if (effectiveSnapshot == null) {
            currentBattleRoomConfig?.copy(mapPath = targetMapPath)
        } else {
            null
        }
        if (gameSession.rendersIntoKoolCanvas) {
            when {
                effectiveSnapshot != null -> gameSession.prepareMemorySnapshotAsync(effectiveSnapshot, viewport())
                battleRoomConfig != null -> gameSession.prepareBattleRoomAsync(battleRoomConfig, viewport())
                else -> gameSession.prepareMapAsync(targetMapPath, viewport())
            }
        } else {
            when {
                effectiveSnapshot != null -> gameSession.requestMemorySnapshot(effectiveSnapshot)
                battleRoomConfig != null -> {
                    check(gameSession.prepareLocalBattleRoom(battleRoomConfig)) {
                        "Game session rejected local battle room map switch"
                    }
                }

                else -> gameSession.requestMap(targetMapPath)
            }
        }
        setPendingStart(targetMapPath, AppScreen.InGame)
        navigateToInGame()
        if (!gameSession.rendersIntoKoolCanvas) {
            koolCanvasScene.isVisible = false
            gameSession.setGameVisible(
                true,
                viewport(),
                koolOverlay = ModUiRegistry.hasActiveHudLayers(),
            )
        }
    }

    private fun shouldSimulateMapLocally(mapPath: String): Boolean {
        val engine = GameEngine.getInstance()
        val networkEngine = engine?.networkEngine
        if (engine?.isNetworkGameActive() != true || networkEngine?.rwxP2PSession != true) {
            return true
        }
        val localPlayerId = currentLocalPlayerId() ?: return false
        return P2PLobbyService.getInstance().currentMultiMapAssignments()
            ?.instances
            ?.any { state.samePath(it.mapPath, mapPath) && it.simulatorPlayerId == localPlayerId }
            ?: false
    }

    private fun currentLocalPlayerId(): String? {
        gameSession.currentBattleRoom()?.players?.firstOrNull { it.isLocal }?.id?.let { return it }
        val engine = GameEngine.getInstance() ?: return null
        return engine.networkEngine?.localPlayerTeam?.teamId?.toString()
            ?: engine.playerTeam?.teamId?.toString()
    }
}
