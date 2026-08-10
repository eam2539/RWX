package io.github.rwx.session

import com.corrodinggames.rts.game.GameTeam
import com.corrodinggames.rts.game.PlayerTeam
import com.corrodinggames.rts.game.PlayerTeam.resetTeamRegistry
import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.NetworkEngine
import com.corrodinggames.rts.gameFramework.network.TeamLayoutType


/**
 * Applies the room fog rules restored by a standalone skirmish/sandbox save to the loaded map.
 *
 * A sandbox save restores its room rules but deliberately leaves the map's initial fog untouched.
 * Android initializes a skirmish map without explicit fog metadata as no-fog, while the desktop
 * engine initializes it as LOS. Apply the restored room rules here so that backend default does
 * not change the saved game mode.
 */
fun GameEngine.applyLoadedSinglePlayerFogRules() {
    val network = networkEngine ?: return
    if (!network.singleplayerServer || network.networkGameActive) return
    val map = tileMap ?: return
    when (network.roomSettings.fogMode) {
        0 -> {
            map.fogEnabled = false
            map.fogPeriodicMaintenanceEnabled = false
        }

        1 -> {
            map.fogEnabled = true
            map.fogPeriodicMaintenanceEnabled = false
        }

        2 -> {
            map.fogEnabled = true
            map.fogPeriodicMaintenanceEnabled = true
        }
    }
    map.fogRenderActive = network.roomSettings.revealedMap
}

/** The engine layout constant for [this], or null when the layout has no direct engine equivalent. */
fun BattleRoomTeamLayout.toEngineTeamLayoutType(): TeamLayoutType? =
    when (this) {
        BattleRoomTeamLayout.TwoSides -> TeamLayoutType.layout_2sides
        BattleRoomTeamLayout.ThreeSides -> TeamLayoutType.layout_3sides
        BattleRoomTeamLayout.Ffa -> TeamLayoutType.layout_ffa
        BattleRoomTeamLayout.Spectators -> TeamLayoutType.layout_spectators
        BattleRoomTeamLayout.Random,
        BattleRoomTeamLayout.AllVsAi,
        BattleRoomTeamLayout.AllVs2 -> null
    }

/** Applies [layout], falling back to a manual team-colour assignment for RWX-only layouts. */
fun NetworkEngine.applyBattleRoomTeamLayout(layout: BattleRoomTeamLayout) {
    val engineLayout = layout.toEngineTeamLayoutType()
    if (engineLayout != null) {
        a(engineLayout)
    } else {
        applyCustomTeamMode(layout)
    }
}

private fun NetworkEngine.applyCustomTeamMode(layout: BattleRoomTeamLayout) {
    if (!isServer) return
    val teams = PlayerTeam.getTeams().filter { !it.isSpectatorTeamColor() }
    when (layout) {
        BattleRoomTeamLayout.AllVsAi ->
            teams.forEach { it.teamColorId = if (it.isTeamControlledByAI) 1 else 0 }

        BattleRoomTeamLayout.AllVs2 ->
            teams.forEach { it.teamColorId = if (it.teamId == 0) 1 else 0 }

        BattleRoomTeamLayout.Random -> {
            val shuffled = teams.shuffled()
            val teamsPerSide = when (shuffled.size) {
                in 0..8 -> 2
                in 9..21 -> 3
                in 22..32 -> 4
                else -> 5
            }
            var remainingInSide = teamsPerSide
            var currentSide = 0
            shuffled.forEach { team ->
                team.teamColorId = currentSide
                remainingInSide--
                if (remainingInSide == 0) {
                    remainingInSide = teamsPerSide
                    currentSide++
                }
            }
        }

        else -> return
    }
}


fun GameEngine.configureLocalBattleRoom(
    config: BattleRoomLaunchConfig,
    logLabel: String,
): NetworkEngine? {
    val networkEngine = networkEngine ?: return null
    networkEngine.selectedMapPath = config.room.mapPath
    val playerName = networkEngine.playerName
        ?: settingsEngine?.lastNetworkPlayerName
        ?: "Player"
    networkEngine.playerName = playerName
    networkEngine.requireActiveMods = true
    resetTeamRegistry()
    val localPlayerTeam = GameTeam(0)
    localPlayerTeam.teamName = playerName.ifBlank { "Player" }
    localPlayerTeam.isTeamSpectator = false
    localPlayerTeam.isTeamControlledByAI = false
    localPlayerTeam.isTeamObserver = false
    localPlayerTeam.isTeamReady = true
    localPlayerTeam.teamPingTime = 0
    playerTeam = localPlayerTeam
    this.networkEngine?.localPlayerTeam = localPlayerTeam

    val started = if (config.sandbox) {
        networkEngine.startSandboxServer()
    } else {
        networkEngine.startSingleplayerServer()
    }
    check(started) { "Unable to start $logLabel battle room" }
    check(networkEngine.localPlayerTeam === localPlayerTeam) {
        "Unable to start $logLabel battle room: local player team was not registered"
    }

    if (config.sandbox)
        networkEngine.roomSettings.apply {
            fogMode = 0
            revealedMap = true
        }

    val settings = networkEngine.getEditableRoomSettings() ?: networkEngine.roomSettings
    if (!config.room.isSavedGame) {
        config.room.options.apply {
            gameModeType = getGameModeType()
        }
    }

    networkEngine.a(settings)

    repeat(config.aiPlayerCount.coerceAtLeast(0)) {
        networkEngine.addAIToGame()
    }
    config.teamLayout?.let(networkEngine::applyBattleRoomTeamLayout)
    return networkEngine
}

/** Sanitises a user-supplied save name into something the engine's save directory accepts. */
fun String.toRwSaveName(): String =
    trim()
        .ifBlank { "rwx_save" }
        .replace(".", "_")
        .replace("/", "_")
        .replace("\\", "_")

/** Sanitises a user-supplied map name into a filesystem-safe `.tmx` basename. */
fun String.toRwExportMapName(): String =
    trim()
        .ifBlank { "rwx_map" }
        .replace(".", "_")
        .replace("/", "_")
        .replace("\\", "_")
        .replace("|", "_")
        .replace("?", "_")
        .replace(":", "_")
        .replace("*", "_")
        .replace("\"", "_")
        .replace("<", "_")
        .replace(">", "_")
