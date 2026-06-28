package io.github.rwx.ui

data class MultiplayerRoomItem(
    val roomId: String,
    val hostName: String,
    val mapName: String,
    val playersLabel: String,
    val versionLabel: String,
    val requiresPassword: Boolean,
    val hasMods: Boolean,
    val transportLabel: String,
    val stateLabel: String,
    val joinAddress: String = roomId,
    val originalServerId: String? = null,
    val requiresJoinInput: Boolean = false,
    val joinInputHint: String = "",
    val infoText: String = "",
)

enum class MultiplayerLobbyKind(val label: String) {
    Original("Original RW"),
    P2P("RWX P2P"),
}


data class MultiplayerRoomListModel(
    val title: String,
    val lobbyKind: MultiplayerLobbyKind,
    val rooms: List<MultiplayerRoomItem>,
    val statusText: String = "",
)

/** Actions the user can take on the multiplayer room-list screen. */
sealed interface MultiplayerAction {
    /** Navigate back to the main menu. */
    data object Back : MultiplayerAction

    /** Refresh the room list. */
    data object Refresh : MultiplayerAction

    /** Switch between the original RW lobby and RWX's P2P lobby. */
    data class SwitchLobby(val lobbyKind: MultiplayerLobbyKind) : MultiplayerAction

    /** Host a game in the active lobby. */
    data object HostGame : MultiplayerAction

    /** Join by a manually-entered address / room code in the active lobby. */
    data object JoinDirect : MultiplayerAction

    /** Configure the player name used by multiplayer sessions. */
    data object ConfigurePlayerName : MultiplayerAction

    /** Join a specific room by its ID. */
    data class JoinRoom(val roomId: String) : MultiplayerAction
}

/** Outcome produced by [MultiplayerNavigation] for each [MultiplayerAction]. */
sealed interface MultiplayerOutcome {
    data class Navigate(val screen: AppScreen) : MultiplayerOutcome
    data object RefreshRequested : MultiplayerOutcome
    data class SwitchLobby(val lobbyKind: MultiplayerLobbyKind) : MultiplayerOutcome
    data object HostGameRequested : MultiplayerOutcome
    data object JoinDirectRequested : MultiplayerOutcome
    data object ConfigurePlayerNameRequested : MultiplayerOutcome
    data class JoinRoom(val roomId: String) : MultiplayerOutcome
}

/** Pure function that maps each [MultiplayerAction] to its [MultiplayerOutcome]. */
object MultiplayerNavigation {
    fun outcomeFor(action: MultiplayerAction): MultiplayerOutcome = when (action) {
        MultiplayerAction.Back -> MultiplayerOutcome.Navigate(AppScreen.MainMenu)
        MultiplayerAction.Refresh -> MultiplayerOutcome.RefreshRequested
        is MultiplayerAction.SwitchLobby -> MultiplayerOutcome.SwitchLobby(action.lobbyKind)
        MultiplayerAction.HostGame -> MultiplayerOutcome.HostGameRequested
        MultiplayerAction.JoinDirect -> MultiplayerOutcome.JoinDirectRequested
        MultiplayerAction.ConfigurePlayerName -> MultiplayerOutcome.ConfigurePlayerNameRequested
        is MultiplayerAction.JoinRoom -> MultiplayerOutcome.JoinRoom(action.roomId)
    }
}
