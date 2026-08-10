package io.github.rwx

import com.corrodinggames.rts.game.PlayerTeam
import com.corrodinggames.rts.gameFramework.network.NetworkEngine
import io.github.rwx.ui.BattleRoomUiBridge

fun applyBattleRoomPlayerConfig(
    networkEngine: NetworkEngine,
    player: PlayerTeam,
    spawn: Int?,
    team: Int?,
    startingUnits: Int? = null,
    aiDifficulty: Int? = null,
): Boolean {
    if (networkEngine.gameHasBeenStarted) {
        return false
    }

    val wantsSpectator = spawn == BATTLE_ROOM_SPECTATOR_SPAWN_VALUE
    val targetSlot = when {
        wantsSpectator -> BATTLE_ROOM_SPECTATOR_SPAWN_VALUE
        spawn == null -> null
        spawn in 1..PlayerTeam.TEAM_NEUTRAL -> spawn - 1
        else -> return false
    }
    val targetTeam = when {
        targetSlot == BATTLE_ROOM_SPECTATOR_SPAWN_VALUE -> SPECTATOR_TEAM_COLOR_ID
        team == null -> null
        team == BATTLE_ROOM_AUTO_TEAM_VALUE -> autoTeamForSlot(player, targetSlot)
        team in 1..MAX_ALLY_TEAM_VALUE -> team - 1
        else -> return false
    }

    if (networkEngine.isServer) {
        if (targetSlot != null && player.teamId != targetSlot) {
            networkEngine.a(player, targetSlot)
        }
        targetTeam?.let { player.teamColorId = it }
        applyPlayerOverrides(player, startingUnits, aiDifficulty)
        networkEngine.refreshAIDifficultyForTeams()
        networkEngine.refreshTeamSortAndAiGroups()
        runCatching { networkEngine.broadcastServerInfoToLargePacketConnections() }
        BattleRoomUiBridge.updateUI()
        return true
    }

    if (networkEngine.isProxyController || networkEngine.localPlayerTeam == player) {
        targetSlot?.let { slot ->
            val teamForMove = when {
                slot == BATTLE_ROOM_SPECTATOR_SPAWN_VALUE -> null
                team == BATTLE_ROOM_AUTO_TEAM_VALUE -> BATTLE_ROOM_AUTO_TEAM_VALUE
                targetTeam != null -> targetTeam
                else -> null
            }
            networkEngine.a(player, slot, teamForMove)
        }
        if (targetSlot == null && targetTeam != null) {
            val commandTeam = if (team == BATTLE_ROOM_AUTO_TEAM_VALUE) {
                BATTLE_ROOM_AUTO_TEAM_VALUE
            } else {
                targetTeam
            }
            networkEngine.b(player, commandTeam)
        }
        BattleRoomUiBridge.updateUI()
        return true
    }

    return false
}


private fun applyPlayerOverrides(
    player: PlayerTeam,
    startingUnits: Int?,
    aiDifficulty: Int?,
) {
    startingUnits?.let { player.startingUnitsOverride = it.overrideOrNull() }
    aiDifficulty?.let { player.teamAIDifficultyOverride = it.overrideOrNull() }
}

private fun Int.overrideOrNull(): Int? = takeIf { it != BATTLE_ROOM_CLEAR_OVERRIDE }

private fun autoTeamForSlot(player: PlayerTeam, targetSlot: Int?): Int {
    val slot = targetSlot?.takeIf { it >= 0 } ?: player.teamId.coerceAtLeast(0)
    val slotPlayer = PlayerTeam.k(slot)
    return slotPlayer
        ?.takeIf { it !== player && it.teamColorId >= 0 }
        ?.teamColorId
        ?: (slot % 2)
}

/*
 * - spawn=-3 means spectator.
 * - team=-1 means auto-team.
 * - team=1..99 maps to RW's zero-based ally team id.
 */
const val BATTLE_ROOM_AUTO_TEAM_VALUE: Int = -1
const val BATTLE_ROOM_SPECTATOR_SPAWN_VALUE: Int = -3

/** Sentinel for a per-player override form value meaning "clear the override / use room default". */
const val BATTLE_ROOM_CLEAR_OVERRIDE: Int = -99

private const val SPECTATOR_SPAWN_LABEL: String = "Spec"
private const val SPECTATOR_TEAM_LABEL: String = "-"
private const val SPECTATOR_TEAM_COLOR_ID: Int = -3
private const val MAX_ALLY_TEAM_VALUE: Int = 99
