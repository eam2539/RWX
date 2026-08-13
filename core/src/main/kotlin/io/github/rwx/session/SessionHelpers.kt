package io.github.rwx.session

import io.github.rwx.battleRoomAiDifficultyLabel
import io.github.rwx.battleRoomPlayerDisplayName
import io.github.rwx.ui.model.BattleRoomPlayer


const val DRAFT_LOCAL_PLAYER_ID: String = "local"
const val DRAFT_AI_PLAYER_ID_PREFIX: String = "ai-"

/** The AI slot index encoded in [playerId], or null when it is not a draft AI id. */
fun draftAiPlayerIndex(playerId: String): Int? =
    playerId.removePrefix(DRAFT_AI_PLAYER_ID_PREFIX)
        .takeIf { it.length != playerId.length }
        ?.toIntOrNull()

/** Finds the player [playerId] refers to, accepting both live team ids and draft slot ids. */
fun resolveBattleRoomPlayerSnapshot(
    playerId: String,
    players: List<BattleRoomPlayer>,
): BattleRoomPlayer? {
    players.firstOrNull { it.id == playerId }?.let { return it }
    if (playerId == DRAFT_LOCAL_PLAYER_ID) {
        return players.firstOrNull { it.isLocal }
    }
    val draftIndex = draftAiPlayerIndex(playerId) ?: return null
    return players.firstOrNull { it.spawnLabel.toIntOrNull() == draftIndex + 1 }
        ?: players.getOrNull(draftIndex)
}

/** Ally-team letter for [teamColorId] ("A", "B", ...), or "-" when it has no letter. */
fun teamLabelFor(teamColorId: Int): String =
    if (teamColorId in 0..25) {
        ('A'.code + teamColorId).toChar().toString()
    } else {
        "-"
    }

internal fun BattleRoomLaunchConfig.draftBattleRoomPlayers(): List<BattleRoomPlayer> {
    val totalPlayers = 1 + aiPlayerCount.coerceAtLeast(0)
    return (0 until totalPlayers).map { index ->
        val isLocal = index == 0
        val teamIndex = draftTeamColorIndex(index)
        val isSpectator = teamIndex < 0
        BattleRoomPlayer(
            id = if (isLocal) DRAFT_LOCAL_PLAYER_ID else "$DRAFT_AI_PLAYER_ID_PREFIX$index",
            name = if (isLocal) {
                "Player"
            } else {
                battleRoomPlayerDisplayName(
                    rawName = "AI - ${battleRoomAiDifficultyLabel(room.options.aiDifficulty)}",
                    isAi = true,
                    slotIndex = index,
                )
            },
            spawnLabel = if (isSpectator) "Spec" else (index + 1).toString(),
            teamLabel = if (isSpectator) "-" else teamLabelFor(teamIndex),
            nameColorIndex = teamIndex,
            spawnColorIndex = if (isSpectator) -1 else index,
            teamColorIndex = teamIndex,
            isSpectator = isSpectator,
            isAI = !isLocal,
            isLocal = isLocal,
        )
    }
}

//TODO 打个标记
internal fun BattleRoomLaunchConfig.draftTeamColorIndex(playerIndex: Int): Int =
    when (teamLayout) {
        BattleRoomTeamLayout.TwoSides, null -> playerIndex % 2
        BattleRoomTeamLayout.ThreeSides -> playerIndex % 3
        BattleRoomTeamLayout.Ffa -> playerIndex

        BattleRoomTeamLayout.Spectators -> -1
        BattleRoomTeamLayout.AllVsAi -> if (playerIndex == 0) 0 else 1
        BattleRoomTeamLayout.AllVs2 -> if (playerIndex == 0) 0 else 1
        BattleRoomTeamLayout.Random -> playerIndex % 2
    }
