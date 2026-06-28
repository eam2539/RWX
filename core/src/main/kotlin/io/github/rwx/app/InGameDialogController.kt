package io.github.rwx.app

import com.corrodinggames.rts.gameFramework.local.Locale
import de.fabmax.kool.scene.Scene
import io.github.rwx.i18n.I18n
import io.github.rwx.logger
import io.github.rwx.mod.ModUiRegistry
import io.github.rwx.render.canvas.KoolCanvasViewport
import io.github.rwx.session.GameSession
import io.github.rwx.ui.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class InGameDialogController(
    private val gameSession: GameSession,
    private val koolCanvasScene: Scene,
    private val dialogSceneHost: DialogSceneHost,
    private val currentScreen: () -> AppScreen,
    private val viewport: () -> KoolCanvasViewport,
    private val showUnavailableDialog: (String) -> Unit
) {
    fun showDialogOverGame(dialog: Dialog) {
        val screen = currentScreen()
        val shouldOverlayGameCanvas = screen == AppScreen.InGame && !gameSession.rendersIntoKoolCanvas

        fun restoreGameCanvas() {
            if (currentScreen() == AppScreen.InGame && !gameSession.rendersIntoKoolCanvas) {
                gameSession.setGameVisible(
                    true,
                    viewport(),
                    koolOverlay = ModUiRegistry.hasActiveHudLayers(),
                    pausedBackground = false,
                )
            }
        }

        fun DialogButton.restoreGameAfterPress(): DialogButton {
            if (!shouldOverlayGameCanvas) return this
            return copy(
                onPress = if (onInputPress == null && onFormPress == null) {
                    {
                        try {
                            onPress?.invoke()
                        } finally {
                            restoreGameCanvas()
                        }
                        Unit
                    }
                } else {
                    onPress
                },
                onInputPress = onInputPress?.let { original ->
                    { input: String ->
                        try {
                            original(input)
                        } finally {
                            restoreGameCanvas()
                        }
                    }
                },
                onFormPress = onFormPress?.let { original ->
                    { values: Map<String, String> ->
                        try {
                            original(values)
                        } finally {
                            restoreGameCanvas()
                        }
                    }
                },
            )
        }

        if (shouldOverlayGameCanvas) {
            koolCanvasScene.isVisible = true
            gameSession.setGameVisible(true, viewport(), koolOverlay = true, pausedBackground = true)
        }
        dialogSceneHost.show(
            dialog.copy(buttons = dialog.buttons.map { it.restoreGameAfterPress() })
        )
    }

    fun showLegacyMessageDialog(event: CoreUiEvent.MessageDialogRequested) {
        showDialogOverGame(
            Dialog(
                title = event.title,
                message = event.message,
                buttons = listOf(DialogButton(I18n.common.ok())),
            )
        )
    }

    fun showLegacyPasswordDialog(event: CoreUiEvent.PasswordDialogRequested) {
        showDialogOverGame(
            Dialog(
                title = event.title,
                message = event.prompt,
                textInput = DialogTextInput(hint = event.title),
                buttons = listOf(
                    DialogButton(
                        label = event.confirmButtonLabel,
                        onInputPress = { input ->
                            runCatching {
                                event.handler.submitPassword(input)
                            }.onFailure { error ->
                                logger.warn(error) { "Legacy password dialog submit failed" }
                                showUnavailableDialog("Unable to submit input: ${error.message ?: error.javaClass.simpleName}")
                            }
                        },
                    ),
                    DialogButton(
                        label = event.cancelButtonLabel,
                        onPress = {
                            runCatching {
                                event.handler.cancelPasswordEntry()
                            }.onFailure { error ->
                                logger.warn(error) { "Legacy password dialog cancel failed" }
                            }
                        },
                    ),
                ),
            )
        )
    }

    fun showLegacyFormDialog(event: CoreUiEvent.FormDialogRequested) {
        showDialogOverGame(
            Dialog(
                title = event.title,
                message = event.message,
                form = DialogForm(
                    event.fields.map { field ->
                        DialogFormField.Text(
                            id = field.id,
                            label = field.label,
                            initialText = field.initialText,
                            hint = field.hint,
                        )
                    }
                ),
                buttons = listOf(
                    DialogButton(
                        label = event.confirmButtonLabel,
                        onFormPress = { values ->
                            runCatching {
                                event.handler.submit(values)
                            }.onFailure { error ->
                                logger.warn(error) { "Legacy form dialog submit failed" }
                                showUnavailableDialog("Unable to submit input: ${error.message ?: error.javaClass.simpleName}")
                            }
                        },
                    ),
                    DialogButton(event.cancelButtonLabel),
                ),
                scrollableForm = event.fields.size > 6,
            )
        )
    }

    fun showSaveGameDialog() {
        showDialogOverGame(
            Dialog(
                title = "Save Game",
                message = "Enter a name to save the game under.",
                textInput = DialogTextInput(
                    initialText = "rwx_save",
                    hint = "Save name",
                ),
                buttons = listOf(
                    DialogButton(
                        label = "Save",
                        onInputPress = { name ->
                            val saveName = name.trim().ifBlank { "rwx_save" }
                            gameSession.requestSaveGame(saveName)
                        },
                    ),
                    DialogButton(I18n.common.cancel()),
                ),
            ),
        )
    }

    fun showExportMapDialog() {
        val initialName = defaultExportMapName()
        showDialogOverGame(
            Dialog(
                title = "Export Map",
                message = "Enter a name to export the map as.",
                textInput = DialogTextInput(
                    initialText = initialName,
                    hint = "Map name",
                ),
                buttons = listOf(
                    DialogButton(
                        label = "Export",
                        onInputPress = { name ->
                            val exportName = name.trim().ifBlank { initialName }
                            gameSession.requestExportMap(exportName)
                        },
                    ),
                    DialogButton(I18n.common.cancel()),
                ),
            ),
        )
    }

    fun showInGameChatDialog(teamOnly: Boolean) {
        val history = gameSession.multiplayerChatHistory()
        showDialogOverGame(
            Dialog(
                title = if (teamOnly) "Team Chat" else "Chat",
                message = if (history.isEmpty()) "No chat messages yet." else "",
                listItems = history.asReversed().map { line ->
                    DialogListItem(line.text, line.teamColorIndex)
                },
                textInput = DialogTextInput(hint = "Message"),
                buttons = listOf(
                    DialogButton(
                        label = "Send",
                        onInputPress = { message ->
                            val text = message.trim()
                            if (text.isNotBlank()) {
                                gameSession.requestChatMessage(text, teamOnly)
                            }
                        },
                    ),
                    DialogButton(I18n.common.cancel()),
                ),
            ),
        )
    }

    fun showInGamePlayerListDialog() {
        val players = gameSession.multiplayerPlayerList()
        showDialogOverGame(
            Dialog(
                title = "Players",
                message = if (players.isEmpty()) "No multiplayer players found." else "",
                listItems = players.map { player ->
                    val ping = inGamePlayerPingSuffix(player.pingLabel)
                    DialogListItem(
                        text = "${player.name}  #${player.spawnLabel}  Team ${player.teamLabel}$ping",
                        colorIndex = player.nameColorIndex,
                    )
                },
                buttons = listOf(DialogButton(I18n.battleroom.close())),
            )
        )
    }

    fun showExitGameDialog(onExit: () -> Unit) {
        val multiplayer = gameSession.runningMultiplayerExitInfo()
        val title = when {
            multiplayer?.isHost == true -> Locale.get("menus.ingame.multiplayerClose.title")
            multiplayer != null -> Locale.get("menus.ingame.multiplayerClose.titleDisconnect")
            else -> "Exit Game"
        }
        val message = when {
            multiplayer?.isHost == true -> Locale.get("menus.ingame.multiplayerClose.messageEndGame")
            multiplayer != null -> Locale.get("menus.ingame.multiplayerClose.messageDisconnect")
            else -> "Are you sure you want to exit this game?"
        }
        val exitLabel = when {
            multiplayer?.isHost == true -> Locale.get("menus.ingame.exitGame")
            multiplayer != null -> Locale.get("menus.ingame.multiplayerClose.disconnectButton")
            else -> "Exit"
        }
        val buttons = buildList {
            add(
                DialogButton(exitLabel) {
                    if (multiplayer != null) {
                        gameSession.disconnectRunningMultiplayer()
                    }
                    onExit()
                }
            )
            if (multiplayer?.isHost == true) {
                add(
                    DialogButton(Locale.get("menus.ingame.multiplayerClose.returnToBattleroom")) {
                        if (!gameSession.scheduleReturnToBattleRoom()) {
                            showUnavailableDialog("Unable to schedule return to battle room")
                        }
                    }
                )
            }
            add(DialogButton(I18n.common.cancel()))
        }
        showDialogOverGame(
            Dialog(
                title = title,
                message = message,
                buttons = buttons,
            ),
        )
    }

    fun requestInGameKoolOverlay(enqueue: () -> Unit) {

        enqueue()
        if (currentScreen() == AppScreen.InGame && !gameSession.rendersIntoKoolCanvas) {

            gameSession.setGameVisible(true, viewport(), koolOverlay = true, pausedBackground = true)
        } else {

        }
    }

    fun requestKoolOverlayForQueuedEvent(event: CoreUiEvent) {
        if (!requiresKoolOverlay(event)) return
        if (currentScreen() != AppScreen.InGame || gameSession.rendersIntoKoolCanvas) return

        koolCanvasScene.isVisible = true
        gameSession.setGameVisible(true, viewport(), koolOverlay = true, pausedBackground = true)
    }

    private fun defaultExportMapName(): String {
        val mapName = gameSession.currentMapDisplayName()
            ?.takeIf { it.isNotBlank() }
            ?: gameSession.runningMapPath()
                ?.substringAfterLast('/')
                ?.removeSuffix(".tmx")
                ?.replace('_', ' ')
                ?.takeIf { it.isNotBlank() }
            ?: "Map"
        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("d MMM yyyy"))
        return "New $mapName - $date".replace("  ", " ")
    }
}

internal fun requiresKoolOverlay(event: CoreUiEvent): Boolean = when (event) {
    CoreUiEvent.InGameExitRequested,
    CoreUiEvent.InGameReturnToBattleRoomRequested,
    CoreUiEvent.InGameSettingsRequested,
    CoreUiEvent.InGameSaveRequested,
    CoreUiEvent.InGameExportMapRequested,
    CoreUiEvent.InGameSurrenderRequested,
    is CoreUiEvent.InGameChatRequested,
    CoreUiEvent.InGamePlayerListRequested,
    is CoreUiEvent.InGameMapJumpRequested,
    CoreUiEvent.InGameMapListRequested,
    CoreUiEvent.InGameModWindowRequested,
    is CoreUiEvent.MessageDialogRequested,
    is CoreUiEvent.PasswordDialogRequested,
    is CoreUiEvent.FormDialogRequested -> true

    else -> false
}

internal fun inGamePlayerPingSuffix(pingLabel: String): String {
    val label = pingLabel.trim()
    if (label.isEmpty()) return ""
    return if (label.toIntOrNull() != null) "  ${label}ms" else "  $label"
}
