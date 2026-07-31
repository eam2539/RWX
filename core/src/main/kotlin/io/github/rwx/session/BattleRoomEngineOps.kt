package io.github.rwx.session

import com.corrodinggames.rts.game.GameTeam
import com.corrodinggames.rts.game.PlayerTeam
import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.GameModeType
import com.corrodinggames.rts.gameFramework.network.NetworkEngine
import com.corrodinggames.rts.gameFramework.network.TeamLayoutType

/**
 * Engine-level battle room operations shared by every renderer backend.
 *
 * These used to be copy-pasted into [GameSession] (twice) and into the desktop Slick renderer,
 * which let the three copies drift. They are plain functions on [GameEngine]/[NetworkEngine] so a
 * backend can call them from whichever thread owns the engine at that moment.
 */

/** Rebuilds the team registry around a single local human player and returns that team. */
fun GameEngine.installSinglePlayerLocalTeam(playerName: String): GameTeam {
    PlayerTeam.resetTeamRegistry()
    val localPlayerTeam = GameTeam(0)
    localPlayerTeam.teamName = playerName.ifBlank { "Player" }
    localPlayerTeam.isTeamSpectator = false
    localPlayerTeam.isTeamControlledByAI = false
    localPlayerTeam.isTeamObserver = false
    localPlayerTeam.isTeamReady = true
    localPlayerTeam.teamPingTime = 0
    playerTeam = localPlayerTeam
    networkEngine?.localPlayerTeam = localPlayerTeam
    return localPlayerTeam
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

/**
 * Brings up a host-side battle room for [config]: starts the singleplayer/sandbox server, registers
 * the local team, publishes the room settings and fills the AI slots.
 *
 * Stops short of `startBattleRoomGame()` so callers can decide whether the room stays open (the
 * battle room screen) or immediately loads the level (a direct skirmish launch).
 *
 * @param logLabel backend name used in failure messages, e.g. "Slick".
 * @return the engine's [NetworkEngine], or null when the engine has none (mods-only / headless).
 */
fun GameEngine.configureLocalBattleRoom(
    config: BattleRoomLaunchConfig,
    logLabel: String,
): NetworkEngine? {
    val networkEngine = networkEngine ?: return null
    networkEngine.selectedMapPath = config.mapPath
    val playerName = networkEngine.playerName
        ?: settingsEngine?.lastNetworkPlayerName
        ?: "Player"
    networkEngine.playerName = playerName
    networkEngine.requireActiveMods = true
    val localPlayerTeam = installSinglePlayerLocalTeam(playerName)

    val started = if (config.sandbox) {
        networkEngine.startSandboxServer()
    } else {
        networkEngine.startSingleplayerServer()
    }
    check(started) { "Unable to start $logLabel battle room" }
    check(networkEngine.localPlayerTeam === localPlayerTeam) {
        "Unable to start $logLabel battle room: local player team was not registered"
    }

    val settings = networkEngine.getEditableRoomSettings() ?: networkEngine.roomSettings
    settings.gameModeType = if (config.savedGame) GameModeType.savedGame else getGameModeType()
    settings.mapPath = if (config.savedGame) config.mapPath else currentMapFilename
    settings.applyBattleRoomOptions(
        // Sandbox always reveals the map regardless of what the room options carry.
        if (config.sandbox) config.options.copy(fogMode = 0, revealedMap = true) else config.options
    )
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
