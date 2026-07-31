package io.github.rwx.session


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
    players: List<BattleRoomPlayerSnapshot>,
): BattleRoomPlayerSnapshot? {
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
