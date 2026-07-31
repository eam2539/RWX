package io.github.rwx.app

import io.github.rwx.i18n.I18n
import io.github.rwx.logger
import io.github.rwx.p2p.P2PLobbyService
import io.github.rwx.session.BattleRoomHostConfig
import io.github.rwx.session.GameSession
import io.github.rwx.ui.host.DialogSceneHost
import io.github.rwx.ui.model.*

internal class MultiplayerConnectionController(
    private val gameSession: GameSession,
    private val lobbyController: MultiplayerLobbyController,
    private val battleRoomJoinController: BattleRoomJoinController,
    private val dialogSceneHost: DialogSceneHost,
    private val selectHostMap: () -> MapEntry?,
    private val onHostPreparing: (MapEntry) -> Unit,
    private val updateBattleRoomFromNetwork: () -> Unit,
    private val navigateToBattleRoom: () -> Unit,
    private val showUnavailableDialog: (String) -> Unit,
) {
    fun joinOriginalServer(connectDescriptor: String, roomLabel: String = "server", serverId: String? = null) {
        if (connectDescriptor.isBlank()) return
        battleRoomJoinController.start(
            address = connectDescriptor,
            roomLabel = roomLabel,
            failurePrefix = I18n.multiplayer.unableToJoinServer(),
        ) {
            check(gameSession.joinBattleRoom(connectDescriptor, serverId, p2pSession = false)) {
                "Game session rejected join request"
            }
        }
    }

    fun joinP2PRoom(roomId: String, roomLabel: String = "P2P room") {
        if (roomId.isBlank()) return
        runCatching {
            P2PLobbyService.getInstance().prepareJoin(roomId)
        }.onSuccess { address ->
            battleRoomJoinController.start(
                address = address,
                roomLabel = roomLabel,
                failurePrefix = I18n.multiplayer.unableToJoinP2pRoom(),
            ) {
                check(gameSession.joinBattleRoom(address, serverId = null, p2pSession = true)) {
                    "Game session rejected P2P join request"
                }
            }
        }.onFailure { error ->
            logger.warn(error) { "P2P join failed" }
            showUnavailableDialog("${I18n.multiplayer.unableToJoinP2pRoom()}: ${error.message ?: error.javaClass.simpleName}")
        }
    }

    fun showJoinRoomDialog(roomId: String) {
        val room = lobbyController.roomById(roomId)
        if (room == null) {
            showUnavailableDialog(I18n.multiplayer.roomNoLongerListed())
            return
        }
        if (lobbyController.activeLobbyKind == MultiplayerLobbyKind.Original && room.requiresJoinInput) {
            showOriginalRoomInputDialog(room)
            return
        }
        dialogSceneHost.show(
            Dialog(
                title = I18n.multiplayer.joinServerQuestion(),
                message = joinRoomDialogMessage(room),
                buttons = listOf(
                    DialogButton(I18n.common.join()) {
                        if (lobbyController.activeLobbyKind == MultiplayerLobbyKind.P2P) {
                            joinP2PRoom(room.roomId, room.joinDisplayLabel())
                        } else {
                            joinOriginalServer(
                                room.joinAddress,
                                room.joinDisplayLabel(),
                                room.originalServerId,
                            )
                        }
                    },
                    DialogButton(I18n.common.cancel()),
                ),
                scrollableMessage = true,
            )
        )
    }

    fun showJoinDirectDialog() {
        val activeLobbyKind = lobbyController.activeLobbyKind
        val title = when (activeLobbyKind) {
            MultiplayerLobbyKind.Original -> I18n.multiplayer.joinServer()
            MultiplayerLobbyKind.P2P -> I18n.multiplayer.joinP2pRoom()
        }
        val hint = when (activeLobbyKind) {
            MultiplayerLobbyKind.Original -> I18n.multiplayer.joinServerHint()
            MultiplayerLobbyKind.P2P -> I18n.multiplayer.p2pRoomIdHint()
        }
        val message = when (activeLobbyKind) {
            MultiplayerLobbyKind.Original -> I18n.multiplayer.joinServerInput()
            MultiplayerLobbyKind.P2P -> I18n.multiplayer.joinP2pRoomInput()
        }
        dialogSceneHost.show(
            Dialog(
                title = title,
                message = message,
                textInput = DialogTextInput(hint = hint),
                buttons = listOf(
                    DialogButton(
                        I18n.common.join(),
                        onInputPress = { value ->
                            val input = value.trim()
                            if (lobbyController.activeLobbyKind == MultiplayerLobbyKind.P2P) {
                                joinP2PRoom(input)
                            } else {
                                joinOriginalServer(input)
                            }
                        },
                    ),
                    DialogButton(I18n.common.cancel()),
                ),
            ),
        )
    }

    fun showPlayerNameDialog() {
        dialogSceneHost.show(
            Dialog(
                title = I18n.multiplayer.configurePlayerName(),
                message = I18n.multiplayer.playerNamePrompt(),
                textInput = DialogTextInput(
                    initialText = gameSession.currentMultiplayerPlayerName(),
                    hint = I18n.multiplayer.playerNameHint(),
                ),
                buttons = listOf(
                    DialogButton(
                        I18n.common.save(),
                        onInputPress = { value ->
                            if (!gameSession.updateMultiplayerPlayerName(value)) {
                                showUnavailableDialog(I18n.multiplayer.missingPlayerName())
                            }
                        },
                    ),
                    DialogButton(I18n.common.cancel()),
                ),
            ),
        )
    }

    fun hostMultiplayerGame() {
        val map = selectHostMap()
        if (map == null) {
            showUnavailableDialog("No map is available to host")
            return
        }
        val lobbyKind = lobbyController.activeLobbyKind
        dialogSceneHost.show(
            Dialog(
                title = I18n.multiplayer.hostGame(),
                message = when (lobbyKind) {
                    MultiplayerLobbyKind.Original -> I18n.multiplayer.hostGameInfo(map.displayName)
                    MultiplayerLobbyKind.P2P -> I18n.multiplayer.hostP2pGameInfo(map.displayName)
                },
                form = multiplayerHostGameForm(lobbyKind),
                buttons = multiplayerHostGameButtons(map, lobbyKind),
            ),
        )
    }

    private fun hostMultiplayerGame(
        map: MapEntry,
        lobbyKind: MultiplayerLobbyKind,
        options: MultiplayerHostOptions,
    ) {
        runCatching {
            onHostPreparing(map)
            check(
                gameSession.hostBattleRoom(
                    BattleRoomHostConfig(
                        mapPath = map.saveName ?: map.mapAssetPath,
                        savedGame = map.saveName != null,
                        isPublic = options.isPublic,
                        password = options.password,
                        useMods = options.useMods,
                        rwxP2PSession = lobbyKind == MultiplayerLobbyKind.P2P,
                    ),
                )
            ) { "Game session rejected host request" }
            if (lobbyKind == MultiplayerLobbyKind.P2P) {
                P2PLobbyService.getInstance().hostCurrentServer()
            }
            updateBattleRoomFromNetwork()
            navigateToBattleRoom()
        }.onFailure { error ->
            logger.warn(error) { "Host game failed" }
            showUnavailableDialog("Unable to host game: ${error.message ?: error.javaClass.simpleName}")
        }
    }

    private fun multiplayerHostGameButtons(
        map: MapEntry,
        lobbyKind: MultiplayerLobbyKind,
    ): List<DialogButton> = buildList {
        add(DialogButton(I18n.common.cancel()))
        when (lobbyKind) {
            MultiplayerLobbyKind.Original -> {
                add(
                    DialogButton(
                        I18n.multiplayer.hostStartPrivate(),
                        onFormPress = { values ->
                            hostMultiplayerGame(
                                map,
                                lobbyKind,
                                values.toMultiplayerHostOptions(lobbyKind, isPublic = false),
                            )
                        },
                    ),
                )
                add(
                    DialogButton(
                        I18n.multiplayer.hostStartPublic(),
                        onFormPress = { values ->
                            hostMultiplayerGame(
                                map,
                                lobbyKind,
                                values.toMultiplayerHostOptions(lobbyKind, isPublic = true),
                            )
                        },
                    ),
                )
            }

            MultiplayerLobbyKind.P2P -> add(
                DialogButton(
                    I18n.multiplayer.hostStartP2p(),
                    onFormPress = { values ->
                        hostMultiplayerGame(
                            map,
                            lobbyKind,
                            values.toMultiplayerHostOptions(lobbyKind, isPublic = false),
                        )
                    },
                ),
            )
        }
    }

    private fun showOriginalRoomInputDialog(room: MultiplayerRoomItem) {
        val hint = room.joinInputHint.ifBlank { I18n.multiplayer.joinServerHint() }
        val roomDetails = room.infoText.ifBlank { joinRoomDialogMessage(room) }
        dialogSceneHost.show(
            Dialog(
                title = I18n.multiplayer.joinServer(),
                message = listOf(
                    roomDetails,
                    I18n.multiplayer.joinServerInput(),
                ).joinToString("\n\n"),
                textInput = DialogTextInput(hint = hint),
                buttons = listOf(
                    DialogButton(I18n.common.cancel()),
                    DialogButton(
                        I18n.common.join(),
                        onInputPress = { value ->
                            val address = value.trim()
                            if (address.isBlank()) {
                                showUnavailableDialog(I18n.multiplayer.missingJoinInput())
                            } else {
                                joinOriginalServer(address, room.joinDisplayLabel())
                            }
                        },
                    ),
                ),
                scrollableMessage = true,
            )
        )
    }
}

internal data class MultiplayerHostOptions(
    val useMods: Boolean,
    val password: String?,
    val isPublic: Boolean,
)

internal fun multiplayerHostGameForm(lobbyKind: MultiplayerLobbyKind): DialogForm =
    DialogForm(
        fields = buildList {
            add(
                DialogFormField.Toggle(
                    id = HOST_USE_MODS_FIELD,
                    label = I18n.multiplayer.hostUseMods(),
                    checked = false,
                ),
            )
            add(
                DialogFormField.Text(
                    id = HOST_PASSWORD_FIELD,
                    label = I18n.multiplayer.hostPassword(),
                    initialText = "",
                    hint = I18n.multiplayer.hostPasswordHint(),
                ),
            )
        },
    )

internal fun Map<String, String>.toMultiplayerHostOptions(
    lobbyKind: MultiplayerLobbyKind,
    isPublic: Boolean = lobbyKind == MultiplayerLobbyKind.Original,
): MultiplayerHostOptions =
    MultiplayerHostOptions(
        useMods = get(HOST_USE_MODS_FIELD)?.toBooleanStrictOrNull() ?: false,
        password = get(HOST_PASSWORD_FIELD)?.trim()?.takeIf(String::isNotBlank),
        isPublic = lobbyKind == MultiplayerLobbyKind.Original && isPublic,
    )

private const val HOST_USE_MODS_FIELD: String = "useMods"
private const val HOST_PASSWORD_FIELD: String = "password"
