package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Scene
import io.github.rwx.ui.model.MultiplayerAction
import io.github.rwx.ui.model.MultiplayerLobbyKind
import io.github.rwx.ui.model.MultiplayerRoomItem
import io.github.rwx.ui.model.MultiplayerRoomListModel
import io.github.rwx.ui.component.PanelStyle
import io.github.rwx.ui.ResponsiveContentWidth
import io.github.rwx.ui.ResponsiveViewportHeight
import io.github.rwx.ui.model.SettingsModel
import io.github.rwx.ui.UiTheme
import io.github.rwx.ui.component.addPanelSurface
import io.github.rwx.ui.component.MultiplayerRoomList
import io.github.rwx.ui.component.MultiplayerRoomListActions
import io.github.rwx.ui.component.replaceAllIncrementally

class MultiplayerSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onAction: (MultiplayerAction) -> Unit = {},
) {
    private val rooms = mutableStateListOf<MultiplayerRoomItem>()
    private val statusText = mutableStateOf("")
    private val lobbyKind = mutableStateOf(MultiplayerLobbyKind.Original)

    fun updateRooms(
        rooms: List<MultiplayerRoomItem>,
        statusText: String = "",
        lobbyKind: MultiplayerLobbyKind = this.lobbyKind.value,
    ) {
        this.lobbyKind.value = lobbyKind
        this.statusText.value = statusText
        this.rooms.replaceAllIncrementally(rooms)
    }

    fun dispatch(action: MultiplayerAction) = onAction(action)

    fun createScene(): Scene = UiScene(MULTIPLAYER_SCENE_NAME) {
        addPanelSurface(PanelStyle.Menu, "rwx-multiplayer-panel", model) { theme ->
            val metrics = multiplayerLayoutMetrics()
            MultiplayerRoomList(
                model = MultiplayerRoomListModel(
                    title = "Multiplayer Rooms",
                    lobbyKind = lobbyKind.use(),
                    rooms = rooms.use(),
                    statusText = statusText.use(),
                ),
                theme = theme,
                contentWidth = metrics.contentWidth,
                viewportHeight = metrics.viewportHeight,
                actions = MultiplayerRoomListActions(
                    onJoinRoom = { dispatch(MultiplayerAction.JoinRoom(it)) },
                    onBack = { dispatch(MultiplayerAction.Back) },
                    onRefresh = { dispatch(MultiplayerAction.Refresh) },
                    onSwitchLobby = { dispatch(MultiplayerAction.SwitchLobby(it)) },
                    onHostGame = { dispatch(MultiplayerAction.HostGame) },
                    onJoinDirect = { dispatch(MultiplayerAction.JoinDirect) },
                    onConfigure = { dispatch(MultiplayerAction.ConfigurePlayerName) },
                ),
            )
        }
    }

    companion object {
        const val MULTIPLAYER_SCENE_NAME: String = "rwx-multiplayer"
    }
}

private data class MultiplayerLayoutMetrics(
    val contentWidth: Dp,
    val viewportHeight: Dp,
)

private fun UiScope.multiplayerLayoutMetrics(): MultiplayerLayoutMetrics =
    MultiplayerLayoutMetrics(
        contentWidth = ResponsiveContentWidth(
            defaultWidth = UiTheme.Layout.multiplayerRoomRowWidth,
            minWidth = UiTheme.Layout.multiplayerMinContentWidth,
            maxWidth = UiTheme.Layout.multiplayerMaxContentWidth,
        ),
        viewportHeight = ResponsiveViewportHeight(
            defaultHeight = UiTheme.Layout.scrollViewportHeight,
            minHeight = UiTheme.Layout.multiplayerMinViewportHeight,
            maxHeight = UiTheme.Layout.multiplayerMaxViewportHeight,
            verticalChrome = Dp(160f),
        ),
    )
