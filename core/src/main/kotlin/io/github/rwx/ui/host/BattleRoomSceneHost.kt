package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.UiScene
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Scene
import io.github.rwx.ui.component.BattleRoom
import io.github.rwx.ui.component.PanelStyle
import io.github.rwx.ui.component.addPanelSurface
import io.github.rwx.ui.component.replaceAllIncrementally
import io.github.rwx.ui.model.*


class BattleRoomSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onAction: (BattleRoomAction) -> Unit = {},
) {
    private val players = mutableStateListOf<BattleRoomPlayer>()
    private val chatLines = mutableStateListOf<BattleRoomChatLine>()
    private val info: MutableStateValue<BattleRoomInfo> = mutableStateOf(
        BattleRoomInfo(
            mapName = "No map selected",
            mapTypeLabel = "Battle room",
        )
    )
    private val isHost: MutableStateValue<Boolean> = mutableStateOf(true)

    fun updateRoom(model: BattleRoomModel) {
        info.value = model.info
        isHost.value = model.isHost
        players.replaceAllIncrementally(model.players)
        chatLines.replaceAllIncrementally(model.chatLines)
    }

    fun appendChat(line: BattleRoomChatLine) {
        chatLines += line
    }

    fun dispatch(action: BattleRoomAction) = onAction(action)

    fun createScene(): Scene = UiScene(BATTLE_ROOM_SCENE_NAME) {
        addPanelSurface(PanelStyle.Menu, "rwx-battleroom-panel", model) { theme ->
            BattleRoom(
                model = BattleRoomModel(
                    info = info.use(),
                    players = players.use(),
                    chatLines = chatLines.use(),
                    isHost = isHost.use(),
                ),
                theme = theme,
                actions = BattleRoomActions(
                    onBack = { dispatch(BattleRoomAction.Back) },
                    onSelectMap = { dispatch(BattleRoomAction.SelectMap) },
                    onOpenOptions = { dispatch(BattleRoomAction.OpenOptions) },
                    onStart = { dispatch(BattleRoomAction.Start) },
                    onAddAI = { dispatch(BattleRoomAction.AddAI) },
                    onSelectPlayer = { dispatch(BattleRoomAction.SelectPlayer(it)) },
                    onSendChat = { dispatch(BattleRoomAction.SendChat(it)) },
                ),
            )
        }
    }

    companion object {
        const val BATTLE_ROOM_SCENE_NAME: String = "battleroom"
    }
}
