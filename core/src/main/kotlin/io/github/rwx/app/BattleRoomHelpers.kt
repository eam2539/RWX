package io.github.rwx.app

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.GameRoomSettings
import io.github.rwx.*
import io.github.rwx.i18n.I18n
import io.github.rwx.p2p.MapFeatureDetector
import io.github.rwx.session.*
import io.github.rwx.ui.*
import java.util.*

internal const val BATTLE_ROOM_JOIN_POLL_INTERVAL_MS: Long = 100L

private const val BATTLE_ROOM_JOIN_TIMEOUT_NANOS: Long = 120_000_000_000L

internal data class BattleRoomDraft(
    val map: MapEntry,
    val sandbox: Boolean,
    val settings: GameRoomSettings,
    val aiPlayerCount: Int = 0,
    val teamLayout: BattleRoomTeamLayout? = null,
)

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

internal fun isBattleRoomSnapshotReadyForJoin(snapshot: BattleRoomSnapshot): Boolean =
    snapshot.players.isNotEmpty()

internal fun BattleRoomSnapshot.isReadyForJoinedRoom(): Boolean =
    isBattleRoomSnapshotReadyForJoin(this)

internal fun defaultBattleRoomAiPlayerCount(playerCount: Int?): Int =
    (playerCount ?: 1).minus(1).coerceAtLeast(0)

internal fun newBattleRoomDraft(
    map: MapEntry,
    sandbox: Boolean,
    aiDifficulty: Int,
): BattleRoomDraft {
    val settings = GameRoomSettings()
    settings.mapPath = map.saveName ?: map.fileName
    settings.aiDifficulty = aiDifficulty
    if (sandbox) {
        settings.fogMode = 0
        settings.revealedMap = true
    }
    return BattleRoomDraft(
        map = map,
        sandbox = sandbox,
        settings = settings,
        aiPlayerCount = defaultBattleRoomAiPlayerCount(map.playerCount),
    )
}

internal fun BattleRoomDraft.toLaunchConfig(): BattleRoomLaunchConfig =
    BattleRoomLaunchConfig(
        mapPath = map.saveName ?: map.mapAssetPath,
        savedGame = map.type == LevelEntryType.SavedGame,
        sandbox = sandbox,
        options = settings.toBattleRoomOptions().let { base ->
            // Sandbox forces no-fog/revealed regardless of the draft's stored fog rules.
            if (sandbox) base.copy(fogMode = 0, revealedMap = true) else base
        },
        aiPlayerCount = aiPlayerCount,
        teamLayout = teamLayout,
    )

internal fun BattleRoomDraft.toBattleRoomModel(
    mapTypeLabel: String,
    chatLines: List<BattleRoomChatLine>,
): BattleRoomModel =
    BattleRoomModel(
        info = BattleRoomInfo(
            mapName = map.displayName,
            mapTypeLabel = mapTypeLabel,
            detailLines = battleRoomDetailLines(settings) +
                    listOfNotNull(teamLayout?.let { "Teams: ${it.displayLabel()}" }),
            mapPreviewAssetPath = map.previewAssetPath,
            rwxModeLabel = map.ModeLabel(),
            rwxCompatibilityLabel = map.CompatibilityLabel(),
        ),
        players = draftBattleRoomPlayers(),
        chatLines = chatLines,
        isHost = true,
    )

private fun BattleRoomDraft.draftBattleRoomPlayers(): List<BattleRoomPlayer> {
    val totalPlayers = 1 + aiPlayerCount.coerceAtLeast(0)
    return (0 until totalPlayers).map { index ->
        val isLocal = index == 0
        val teamIndex = draftTeamColorIndex(index)
        val isSpectator = teamIndex < 0
        BattleRoomPlayer(
            id = if (isLocal) "local" else "ai-$index",
            name = if (isLocal) {
                "Player"
            } else {
                battleRoomPlayerDisplayName(
                    rawName = "AI - ${battleRoomAiDifficultyLabel(settings.aiDifficulty)}",
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

private fun BattleRoomDraft.draftTeamColorIndex(playerIndex: Int): Int =
    when (teamLayout) {
        BattleRoomTeamLayout.TwoSides -> playerIndex % 2
        BattleRoomTeamLayout.ThreeSides -> playerIndex % 3
        BattleRoomTeamLayout.Ffa,
        null -> playerIndex

        BattleRoomTeamLayout.Spectators -> -1
        BattleRoomTeamLayout.AllVsAi -> if (playerIndex == 0) 0 else 1
        BattleRoomTeamLayout.AllVs2 -> if (playerIndex == 0) 0 else 1
        BattleRoomTeamLayout.Random -> playerIndex % 2
    }

private fun BattleRoomTeamLayout.displayLabel(): String =
    when (this) {
        BattleRoomTeamLayout.TwoSides -> "2 Sides"
        BattleRoomTeamLayout.ThreeSides -> "3 Sides"
        BattleRoomTeamLayout.Ffa -> "FFA"
        BattleRoomTeamLayout.Spectators -> "Spectators"
        BattleRoomTeamLayout.AllVsAi -> "All vs AI"
        BattleRoomTeamLayout.AllVs2 -> "All vs 2 (survival)"
        BattleRoomTeamLayout.Random -> "Random Team"
    }

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

// GameRoomSettings <-> BattleRoomOptions mapping lives in

internal fun Map<String, String>.toBattleRoomOptions(base: BattleRoomOptions = BattleRoomOptions()): BattleRoomOptions =
    BattleRoomOptions(
        aiDifficulty = get("aiDifficulty")?.toIntOrNull() ?: base.aiDifficulty,
        startingUnits = get("startingUnits")?.toIntOrNull() ?: base.startingUnits,
        fogMode = get("fogMode")?.toIntOrNull() ?: base.fogMode,
        revealedMap = get("revealedMap")?.toBooleanStrictOrNull() ?: base.revealedMap,
        startingCredits = get("startingCredits")?.toIntOrNull() ?: base.startingCredits,
        incomeMultiplier = get("incomeMultiplier")?.toFloatOrNull() ?: base.incomeMultiplier,
        noNukes = get("noNukes")?.toBooleanStrictOrNull() ?: base.noNukes,
        sharedControl = get("sharedControl")?.toBooleanStrictOrNull() ?: base.sharedControl,
        allowSpectators = get("allowSpectators")?.toBooleanStrictOrNull() ?: base.allowSpectators,
        teamLock = get("teamLock")?.toBooleanStrictOrNull() ?: base.teamLock,
        roomLocked = get("roomLocked")?.toBooleanStrictOrNull() ?: base.roomLocked,
        fixedAllyTeams = get("fixedAllyTeams")?.toBooleanStrictOrNull() ?: base.fixedAllyTeams
    )

internal fun battleRoomOptionsForm(options: BattleRoomOptions, maxPlayers: Int = 10): DialogForm =
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
            DialogFormField.Toggle("roomLocked", "Lock room (no new players)", options.roomLocked),
            DialogFormField.Toggle("fixedAllyTeams", "Fixed ally teams", options.fixedAllyTeams),
        ),
    )

internal fun playerConfigForm(player: BattleRoomPlayerSnapshot, isHost: Boolean = false): DialogForm {
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

private val battleRoomColorNames = listOf(
    "Green", "Red", "Blue", "Yellow", "Cyan", "White", "Black", "Pink", "Orange", "Purple",
)

internal fun BattleRoomSnapshot.toBattleRoomModel(
    previewAssetPath: String?,
    chatLines: List<BattleRoomChatLine>,
): BattleRoomModel {
    val requiredRwxFeatures = MapFeatureDetector.requiredFeaturesForMap(mapPath)
    val rwxModeLabel = requiredRwxFeatures.ModeLabel()
    return BattleRoomModel(
        info = BattleRoomInfo(
            mapName = mapDisplayName,
            mapTypeLabel = mapTypeLabel,
            detailLines = battleRoomDetailLines(
                settings = options.toGameRoomSettings(),
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
        players = players.map { it.toBattleRoomPlayer() },
        chatLines = chatLines.toList(),
        isHost = isHost,
    )
}

internal fun BattleRoomOptions.toGameRoomSettings(): GameRoomSettings =
    GameRoomSettings().also { settings ->
        settings.applyBattleRoomOptions(this)
    }

internal fun resolveBattleRoomPlayerSnapshot(
    playerId: String,
    players: List<BattleRoomPlayerSnapshot>,
): BattleRoomPlayerSnapshot? {
    players.firstOrNull { it.id == playerId }?.let { return it }
    if (playerId == DRAFT_LOCAL_PLAYER_ID) {
        return players.firstOrNull { it.isLocal }
    }
    val draftIndex = draftBattleRoomPlayerIndex(playerId) ?: return null
    return players.firstOrNull { it.spawnLabel.toIntOrNull() == draftIndex + 1 }
        ?: players.getOrNull(draftIndex)
}

private fun draftBattleRoomPlayerIndex(playerId: String): Int? =
    playerId.removePrefix(DRAFT_AI_PLAYER_ID_PREFIX)
        .takeIf { it.length != playerId.length }
        ?.toIntOrNull()

private fun BattleRoomPlayerSnapshot.toBattleRoomPlayer(): BattleRoomPlayer =
    BattleRoomPlayer(
        id = id,
        name = name,
        spawnLabel = spawnLabel,
        teamLabel = teamLabel,
        pingLabel = pingLabel,
        nameColorIndex = nameColorIndex,
        spawnColorIndex = spawnColorIndex,
        teamColorIndex = teamColorIndex,
        isSpectator = isSpectator,
        isReady = isReady,
        isAI = isAI,
        isLocal = isLocal,
    )

private fun teamLabelFor(teamColorId: Int): String =
    if (teamColorId in 0..25) {
        ('A'.code + teamColorId).toChar().toString()
    } else {
        "-"
    }

private fun battleRoomDetailLines(
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
        0 -> "Default (\$4000)"
        1 -> "\$0"
        2 -> "\$1000"
        3 -> "\$2000"
        4 -> "\$5000"
        5 -> "\$10000"
        6 -> "\$50000"
        7 -> "\$100000"
        8 -> "\$200000"
        else -> "\$999"
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
private const val DRAFT_LOCAL_PLAYER_ID: String = "local"
private const val DRAFT_AI_PLAYER_ID_PREFIX: String = "ai-"
