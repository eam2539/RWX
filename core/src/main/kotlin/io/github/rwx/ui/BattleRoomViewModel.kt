package io.github.rwx.ui

import de.fabmax.kool.util.Color


/** A single player slot row in the battle-room table. */
data class BattleRoomPlayer(
    val id: String,
    val name: String,
    /** Spawn point label, e.g. "1".."10", or the spectator marker. */
    val spawnLabel: String,
    /** Ally-team label, e.g. "A".."J". */
    val teamLabel: String,
    /** Ping text, e.g. "50"; blank for local player and AIs. */
    val pingLabel: String = "",
    /** Player color index (RW team palette); -1 uses the default text color. */
    val nameColorIndex: Int = -1,
    /** Spawn-point index used only to tint the spawn cell; -1 for spectators. */
    val spawnColorIndex: Int = -1,
    /** Team index used only to tint the team cell; -1 uses the default color. */
    val teamColorIndex: Int = -1,
    val isSpectator: Boolean = false,
    /** A not-ready player is shown dimmed. */
    val isReady: Boolean = true,
    val isAI: Boolean = false,
    val isLocal: Boolean = false,
)

/** The room map/info shown in the left panel. */
data class BattleRoomInfo(
    val mapName: String,
    val mapTypeLabel: String,
    val detailLines: List<String> = emptyList(),
    val mapPreviewAssetPath: String? = null,
    val rwxModeLabel: String? = null,
    val rwxCompatibilityLabel: String? = null,
)

/**
 * One line in the battle-room chat log.
 * @param teamColorIndex author team id supplied by the engine for the tint; -1 = system/no team.
 */
data class BattleRoomChatLine(
    val text: String,
    val teamColorIndex: Int = -1,
)

/** Full battle-room presentation model. */
data class BattleRoomModel(
    val info: BattleRoomInfo,
    val players: List<BattleRoomPlayer>,
    val chatLines: List<BattleRoomChatLine>,
    val isHost: Boolean,
)

data class BattleRoomActions(
    val onBack: () -> Unit,
    val onSelectMap: () -> Unit,
    val onOpenOptions: () -> Unit,
    val onStart: () -> Unit,
    val onAddAI: () -> Unit,
    val onSelectPlayer: (String) -> Unit,
    val onSendChat: (String) -> Unit,
)

sealed interface BattleRoomAction {
    data object Back : BattleRoomAction

    /** Host: open the map-select screen. */
    data object SelectMap : BattleRoomAction

    /** Host: open the game-options dialog. */
    data object OpenOptions : BattleRoomAction

    /** Host: start the game. */
    data object Start : BattleRoomAction

    /** Host: add an AI player. */
    data object AddAI : BattleRoomAction

    /** Open a specific player's config dialog (host or self). */
    data class SelectPlayer(val playerId: String) : BattleRoomAction

    /** Send a chat message / command. */
    data class SendChat(val message: String) : BattleRoomAction
}

sealed interface BattleRoomOutcome {
    data object Close : BattleRoomOutcome
    data object StartGame : BattleRoomOutcome
    data object OpenMapSelect : BattleRoomOutcome
    data object OpenGameOptions : BattleRoomOutcome
    data class OpenPlayerConfig(val playerId: String) : BattleRoomOutcome
    data object AddAI : BattleRoomOutcome
    data class SendChat(val message: String) : BattleRoomOutcome
}


object BattleRoomNavigation {
    fun outcomeFor(action: BattleRoomAction): BattleRoomOutcome = when (action) {
        BattleRoomAction.Back -> BattleRoomOutcome.Close
        BattleRoomAction.SelectMap -> BattleRoomOutcome.OpenMapSelect
        BattleRoomAction.OpenOptions -> BattleRoomOutcome.OpenGameOptions
        BattleRoomAction.Start -> BattleRoomOutcome.StartGame
        BattleRoomAction.AddAI -> BattleRoomOutcome.AddAI
        is BattleRoomAction.SelectPlayer -> BattleRoomOutcome.OpenPlayerConfig(action.playerId)
        is BattleRoomAction.SendChat -> BattleRoomOutcome.SendChat(action.message)
    }
}

/**
 * RW team-color palette (green, red, blue, yellow, cyan, white, dark, pink, orange, purple), used to
 * tint player name / spawn / team cells by index. Out-of-range or negative indices use the supplied
 * fallback so spectators and default-colored players keep the theme's text color.
 */
object BattleRoomTeamColors {
    private val palette: List<Color> = listOf(
        Color("4caf50ff"), Color("e64545ff"), Color("5c8aedff"), Color("e2c541ff"),
        Color("46c5c5ff"), Color("eef1f4ff"), Color("8a8f96ff"), Color("e667b0ff"),
        Color("e8923cff"), Color("9b6bd0ff"),
    )

    fun colorFor(index: Int, fallback: Color): Color = palette.getOrNull(index) ?: fallback
}

internal fun battleRoomChatColorIndexFor(line: BattleRoomChatLine, players: List<BattleRoomPlayer>): Int? {
    // Prefer the team color the engine attached to the message; fall back to matching the author
    // name against the current players (covers locally-echoed lines that carry no index).
    line.teamColorIndex.takeIf { it >= 0 }?.let { return it }
    val author = line.text.substringBefore(":", missingDelimiterValue = "")
        .trim()
        .takeIf { it.isNotEmpty() }
        ?: return null
    return players.firstOrNull { it.name == author }
        ?.nameColorIndex
        ?.takeIf { it >= 0 }
}

