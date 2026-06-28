package io.github.rwx.p2p

import io.github.rwx.PlatformStorage
import io.github.rwx.map.LinkedMapGraph
import io.github.rwx.map.MapLinkResolver
import io.github.rwx.session.BattleRoomPlayerSnapshot
import kotlinx.serialization.Serializable

@Serializable
data class MapInstanceAssignment(
    val mapId: String,
    val mapPath: String,
    val mapDisplayName: String,
    val simulatorPlayerId: String,
    val simulatorPlayerName: String,
    val simulatorSpawnLabel: String,
)

@Serializable
data class MultiMapAssignments(
    val rootMapPath: String,
    val instances: List<MapInstanceAssignment>,
)

data class MultiMapAssignmentPlan(
    val graph: LinkedMapGraph,
    val assignments: MultiMapAssignments?,
    val eligiblePlayers: List<BattleRoomPlayerSnapshot>,
) {
    val missingTargetMapIds: List<String>
        get() = graph.missingTargetMapIds

    val requiredPlayerCount: Int
        get() = graph.maps.size

    val availablePlayerCount: Int
        get() = eligiblePlayers.size

    val hasEnoughPlayers: Boolean
        get() = availablePlayerCount >= requiredPlayerCount
}

object MultiMapCoordinator {
    fun createAssignmentPlan(
        storage: PlatformStorage,
        sourceMapPath: String,
        players: List<BattleRoomPlayerSnapshot>,
    ): MultiMapAssignmentPlan {
        val graph = MapLinkResolver.resolveLinkedMapGraph(storage, sourceMapPath)
        val eligiblePlayers = players
            .filterNot { it.isSpectator || it.isAI }
            .sortedWith(compareBy<BattleRoomPlayerSnapshot> { it.spawnLabel.toIntOrNull() ?: Int.MAX_VALUE }
                .thenBy { it.id })
        val assignments = if (graph.missingTargetMapIds.isEmpty() && eligiblePlayers.size >= graph.maps.size) {
            MultiMapAssignments(
                rootMapPath = sourceMapPath,
                instances = graph.maps.mapIndexed { index, map ->
                    val player = eligiblePlayers[index]
                    MapInstanceAssignment(
                        mapId = map.mapId,
                        mapPath = map.mapPath,
                        mapDisplayName = map.displayName,
                        simulatorPlayerId = player.id,
                        simulatorPlayerName = player.name,
                        simulatorSpawnLabel = player.spawnLabel,
                    )
                },
            )
        } else {
            null
        }
        return MultiMapAssignmentPlan(
            graph = graph,
            assignments = assignments,
            eligiblePlayers = eligiblePlayers,
        )
    }
}
