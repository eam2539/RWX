package io.github.rwx.app

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.GameRoomSettings
import io.github.rwx.BATTLE_ROOM_AUTO_TEAM_VALUE
import io.github.rwx.BATTLE_ROOM_CLEAR_OVERRIDE
import io.github.rwx.BATTLE_ROOM_SPECTATOR_SPAWN_VALUE
import io.github.rwx.i18n.I18n
import io.github.rwx.p2p.MapFeatureDetector
import io.github.rwx.session.BattleRoomSnapshot
import io.github.rwx.session.BattleRoomTeamLayout
import io.github.rwx.session.teamLabelFor
import io.github.rwx.ui.model.*
import java.util.*

internal const val BATTLE_ROOM_JOIN_POLL_INTERVAL_MS: Long = 100L

private const val BATTLE_ROOM_JOIN_TIMEOUT_NANOS: Long = 120_000_000_000L

internal enum class BattleRoomJoinPollResult {
    Connecting,
    Connected,
    Failed,
    TimedOut,
}

internal fun battleRoomJoinPollResult(
    startedAtNanos: Long,
    nowNanos: Long,
    hasBattleRoomSnapshot: Boolean,
    isJoinInProgress: Boolean,
    errorMessage: String?,
    timeoutNanos: Long = BATTLE_ROOM_JOIN_TIMEOUT_NANOS,
): BattleRoomJoinPollResult =
    when {
        !errorMessage.isNullOrBlank() -> BattleRoomJoinPollResult.Failed
        hasBattleRoomSnapshot && !isJoinInProgress -> BattleRoomJoinPollResult.Connected
        nowNanos - startedAtNanos >= timeoutNanos -> BattleRoomJoinPollResult.TimedOut
        else -> BattleRoomJoinPollResult.Connecting
    }

internal fun MultiplayerRoomItem.joinDisplayLabel(): String =
    hostName.takeIf { it.isNotBlank() }
        ?.let { host -> "$host / $mapName" }
        ?: mapName.ifBlank { "server" }

internal fun joinRoomDialogMessage(room: MultiplayerRoomItem): String =
    listOfNotNull(
        room.infoText.trim().takeIf { it.isNotBlank() },
        listOf(
            I18n.multiplayer.roomInfo.host(room.hostName),
            I18n.multiplayer.roomInfo.map(room.mapName),
            I18n.multiplayer.roomInfo.players(room.playersLabel),
            I18n.multiplayer.roomInfo.state(room.stateLabel),
            I18n.multiplayer.roomInfo.version(room.versionLabel),
            I18n.multiplayer.roomInfo.transport(room.transportLabel),
            I18n.multiplayer.roomInfo.password(
                if (room.requiresPassword) I18n.common.required() else I18n.common.no(),
            ),
            I18n.multiplayer.roomInfo.mods(
                if (room.hasMods) I18n.common.required() else I18n.common.no(),
            ),
        ).joinToString("\n"),
    ).joinToString("\n\n")

internal fun defaultBattleRoomAiPlayerCount(playerCount: Int?): Int =
    (playerCount ?: 1).minus(1).coerceAtLeast(0)


internal fun String.toBattleRoomTeamLayoutOrNull(): BattleRoomTeamLayout? =
    when (this) {
        "2" -> BattleRoomTeamLayout.TwoSides
        "3" -> BattleRoomTeamLayout.ThreeSides
        "ffa" -> BattleRoomTeamLayout.Ffa
        "spectators" -> BattleRoomTeamLayout.Spectators
        "allvsai" -> BattleRoomTeamLayout.AllVsAi
        "allvs2" -> BattleRoomTeamLayout.AllVs2
        "random" -> BattleRoomTeamLayout.Random
        else -> null
    }

// GameRoomSettings <-> GameRoomSettings mapping lives in

internal fun Map<String, String>.toGameRoomSettings(base: GameRoomSettings = GameRoomSettings()): GameRoomSettings =
    GameRoomSettings().apply {
        aiDifficulty = get("aiDifficulty")?.toIntOrNull() ?: base.aiDifficulty
        startingUnits = get("startingUnits")?.toIntOrNull() ?: base.startingUnits
        fogMode = get("fogMode")?.toIntOrNull() ?: base.fogMode
        revealedMap = get("revealedMap")?.toBooleanStrictOrNull() ?: base.revealedMap
        startingCredits = get("startingCredits")?.toIntOrNull() ?: base.startingCredits
        incomeMultiplier = get("incomeMultiplier")?.toFloatOrNull() ?: base.incomeMultiplier
        noNukes = get("noNukes")?.toBooleanStrictOrNull() ?: base.noNukes
        sharedControl = get("sharedControl")?.toBooleanStrictOrNull() ?: base.sharedControl
        allowSpectators = get("allowSpectators")?.toBooleanStrictOrNull() ?: base.allowSpectators
        teamLock = get("teamLock")?.toBooleanStrictOrNull() ?: base.teamLock
        roomLock = get("roomLocked")?.toBooleanStrictOrNull() ?: base.roomLock
        fixedAllyTeams = get("fixedAllyTeams")?.toBooleanStrictOrNull() ?: base.fixedAllyTeams
    }


internal fun battleRoomOptionsForm(options: GameRoomSettings, maxPlayers: Int = 10): DialogForm =
    DialogForm(
        fields = listOf(
            DialogFormField.Choice(
                id = "maxPlayers",
                label = "Max players",
                options = (2..10).map { DialogFormOption(it.toString(), it.toString()) },
                selectedIndex = (maxPlayers - 2).coerceIn(0, 8),
            ),
            DialogFormField.Choice(
                id = "aiDifficulty",
                label = "AI difficulty",
                options = listOf(
                    DialogFormOption("Very Easy", "-2"),
                    DialogFormOption("Easy", "-1"),
                    DialogFormOption("Medium", "0"),
                    DialogFormOption("Hard", "1"),
                    DialogFormOption("Very Hard", "2"),
                    DialogFormOption("Impossible", "3"),
                ),
                selectedIndex = listOf("-2", "-1", "0", "1", "2", "3")
                    .indexOf(options.aiDifficulty.toString())
                    .coerceAtLeast(0),
            ),
            DialogFormField.Choice(
                id = "startingUnits",
                label = "Starting units",
                options = (1..5).map {
                    DialogFormOption(startingUnitsLabel(it), it.toString())
                },
                selectedIndex = (options.startingUnits - 1).coerceIn(0, 4),
            ),
            DialogFormField.Choice(
                id = "fogMode",
                label = "Fog",
                options = listOf(
                    DialogFormOption("No fog", "0"),
                    DialogFormOption("Basic", "1"),
                    DialogFormOption("Line of Sight", "2"),
                ),
                selectedIndex = options.fogMode.coerceIn(0, 2),
            ),
            DialogFormField.Toggle("revealedMap", "Revealed Map", options.revealedMap),
            DialogFormField.Choice(
                id = "startingCredits",
                label = "Starting credits",
                options = (0..8).map {
                    DialogFormOption(startingCreditsLabel(it), it.toString())
                },
                selectedIndex = (0..8)
                    .indexOf(options.startingCredits)
                    .coerceAtLeast(0),
            ),
            DialogFormField.Choice(
                id = "incomeMultiplier",
                label = "Income",
                options = listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f, 5.0f).map {
                    DialogFormOption("${it}x", it.toString())
                },
                selectedIndex = listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f, 5.0f)
                    .indexOf(options.incomeMultiplier)
                    .coerceAtLeast(1),
            ),
            DialogFormField.Choice(
                id = "teamLayout",
                label = "Team layout",
                options = listOf(
                    DialogFormOption("No change", ""),
                    DialogFormOption("2 Sides", "2"),
                    DialogFormOption("3 Sides", "3"),
                    DialogFormOption("FFA", "ffa"),
                    DialogFormOption("Spectators", "spectators"),
                    DialogFormOption("Random Team", "random"),
                    DialogFormOption("All vs AI", "allvsai"),
                    DialogFormOption("All vs 2 (survival)", "allvs2"),
                ),
            ),
            DialogFormField.Toggle("noNukes", "No nukes", options.noNukes),
            DialogFormField.Toggle("sharedControl", "Shared control", options.sharedControl),
            DialogFormField.Toggle("allowSpectators", "Allow spectators", options.allowSpectators),
            DialogFormField.Toggle("teamLock", "Team lock", options.teamLock),
            DialogFormField.Toggle("roomLocked", "Lock room (no new players)", options.roomLock),
            DialogFormField.Toggle("fixedAllyTeams", "Fixed ally teams", options.fixedAllyTeams),
        ),
    )

internal fun playerConfigForm(player: BattleRoomPlayer, isHost: Boolean = false): DialogForm {
    val spawn = player.spawnLabel.toIntOrNull() ?: 1
    val baseFields = listOf(
        DialogFormField.Choice(
            id = "spawn",
            label = "Spawn point",
            options = (1..10).map { DialogFormOption(it.toString(), it.toString()) } +
                    DialogFormOption("Spectator", BATTLE_ROOM_SPECTATOR_SPAWN_VALUE.toString()),
            selectedIndex = if (player.isSpectator) 10 else (spawn - 1).coerceIn(0, 9),
        ),
        DialogFormField.Choice(
            id = "team",
            label = "Team",
            options = listOf(DialogFormOption("Auto", BATTLE_ROOM_AUTO_TEAM_VALUE.toString())) +
                    (1..10).map { DialogFormOption(teamLabelFor(it - 1), it.toString()) },
            selectedIndex = 0,
        ),
    )
    val hostFields = if (isHost) playerOverrideFields() else emptyList()
    return DialogForm(fields = baseFields + hostFields)
}

private fun playerOverrideFields(): List<DialogFormField> {
    val clear = BATTLE_ROOM_CLEAR_OVERRIDE.toString()
    return listOf(
        DialogFormField.Choice(
            id = "startingUnits",
            label = "Starting units override",
            options = listOf(DialogFormOption("Default", clear)) +
                    (1..5).map { DialogFormOption(startingUnitsLabel(it), it.toString()) },
            selectedIndex = 0,
        ),
        DialogFormField.Choice(
            id = "aiDifficulty",
            label = "AI difficulty override",
            options = listOf(DialogFormOption("Default", clear)) + listOf(
                DialogFormOption("Very Easy", "-2"),
                DialogFormOption("Easy", "-1"),
                DialogFormOption("Medium", "0"),
                DialogFormOption("Hard", "1"),
                DialogFormOption("Very Hard", "2"),
                DialogFormOption("Impossible", "3"),
            ),
            selectedIndex = 0,
        ),
    )
}

internal fun BattleRoomSnapshot.toBattleRoomModel(
    previewAssetPath: String?,
    chatLines: List<BattleRoomChatLine>,
): BattleRoomModel {
    val requiredRwxFeatures = MapFeatureDetector.requiredFeaturesForMap(room.mapPath)
    val rwxModeLabel = requiredRwxFeatures.ModeLabel()
    return BattleRoomModel(
        info = BattleRoomInfo(
            mapName = mapDisplayName,
            mapTypeLabel = mapTypeLabel,
            detailLines = battleRoomDetailLines(
                settings = room.options,
                networkStatusText = networkStatusText,
                requiredModsSummary = requiredModsSummary,
            ),
            mapPreviewAssetPath = previewAssetPath,
            rwxModeLabel = rwxModeLabel,
            rwxCompatibilityLabel = rwxModeLabel?.let {
                if (isNetworkMultiplayer) {
                    if (rwxP2PSession) "RWX P2P enabled" else "Original multiplayer blocked"
                } else {
                    "Single-player"
                }
            },
        ),
        players = players,
        chatLines = chatLines.toList(),
        isHost = isHost,
    )
}

internal fun battleRoomDetailLines(
    settings: GameRoomSettings,
    networkStatusText: String? = null,
    requiredModsSummary: String? = null,
): List<String> {
    val statusLines = networkStatusText
        ?.takeIf { it.isNotBlank() }
        ?.let(::originalBattleRoomStatusLines)
    val lines = statusLines ?: buildList {
        add("Starting Credits: ${startingCreditsLabel(settings.startingCredits)}")
        add("Fog: ${fogLabel(settings.fogMode)}")
        if (settings.startingUnits != 1) {
            add("Starting Units: ${startingUnitsLabel(settings.startingUnits)}")
        }
        if (settings.incomeMultiplier != 1.0f) {
            add("${incomeLabel(settings.incomeMultiplier)}X income")
        }
        if (settings.noNukes) add("No nukes")
        if (settings.sharedControl) add("Shared control: On")
        if (settings.roomLock) add("Room locked")
        if (settings.fixedAllyTeams) add("Fixed ally teams")
    }
    if (requiredModsSummary.isNullOrBlank() || lines.any { it.contains("Required Mods") }) {
        return lines
    }
    return lines + "Required mods: $requiredModsSummary"
}

internal fun originalBattleRoomStatusLines(statusText: String): List<String> =
    statusText.lineSequence()
        .map(String::trim)
        .filter(String::isNotBlank)
        .filterNot { it.startsWith("Game Mode:") || it.startsWith("Map:") }
        .toList()

internal fun startingCreditsLabel(code: Int): String =
    when (code) {
        0 -> "Default ($4000)"
        1 -> "$0"
        2 -> "$1000"
        3 -> "$2000"
        4 -> "$5000"
        5 -> "$10000"
        6 -> "$50000"
        7 -> "$100000"
        8 -> "$200000"
        else -> "$999"
    }

internal fun startingUnitsLabel(value: Int): String =
    when (value) {
        1 -> "Normal (1 builder)"
        2 -> "Small Army"
        3 -> "3 Engineers"
        4 -> "3 Engineers (No Command Center)"
        5 -> "Experimental Spider"
        9 -> "Custom"
        else -> runCatching {
            GameEngine.getInstance()?.networkEngine?.d(value)
        }.getOrNull()?.takeIf { it != "Unknown" } ?: "Unknown"
    }

private fun incomeLabel(value: Float): String =
    if (value == value.toInt().toFloat()) {
        value.toInt().toString()
    } else {
        String.format(Locale.ROOT, "%.1f", value)
    }

private fun fogLabel(fogMode: Int): String =
    when (fogMode) {
        0 -> "No fog"
        1 -> "Basic"
        2 -> "Line of Sight"
        else -> "Unknown"
    }

internal const val DEFAULT_MAX_PLAYERS: Int = 10
