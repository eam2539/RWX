package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.*
import io.github.rwx.i18n.I18n


fun UiScope.MultiplayerRoomRow(
    room: MultiplayerRoomItem,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.multiplayerRoomRowWidth,
    onPressed: () -> Unit,
) {
    val compact = contentWidth.value < MULTIPLAYER_COMPACT_WIDTH_DP
    val hovered = remember(false)
    val isHovered = hovered.use()
    val background = if (isHovered) theme.palette.surfaceRaised else theme.palette.surfaceSunken
    val border = if (isHovered) theme.palette.primary else theme.palette.borderSubtle
    val rowHeight = if (compact) {
        UiTheme.Layout.menuButtonHeight + Dp(28f)
    } else {
        UiTheme.Layout.menuButtonHeight
    }
    if (compact) {
        Column(width = contentWidth, height = rowHeight) {
            modifier
                .margin(UiTheme.Spacing.xs)
                .padding(horizontal = UiTheme.Spacing.md, vertical = UiTheme.Spacing.xs)
                .background(RoundRectBackground(background, UiTheme.Spacing.xs))
                .border(RoundRectBorder(border, UiTheme.Spacing.xs, Dp(1f)))
                .onEnter { hovered.value = true }
                .onExit { hovered.value = false }
                .onClick { onPressed() }

            Text("${room.hostName} | ${room.mapName}") {
                modifier
                    .width(Grow.Std)
                    .height(Dp(34f))
                    .font(UiTheme.Fonts.bodySmall)
                    .textAlign(AlignmentX.Start, AlignmentY.Center)
                    .isWrapText(true)
                    .textColor(theme.palette.textPrimary)
            }
            Text(roomStatusLabel(room)) {
                modifier
                    .width(Grow.Std)
                    .height(Dp(28f))
                    .font(UiTheme.Fonts.caption)
                    .textAlign(AlignmentX.Start, AlignmentY.Center)
                    .textColor(theme.palette.textSecondary)
            }
        }
        return
    }

    Row(width = contentWidth, height = rowHeight) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .padding(horizontal = UiTheme.Spacing.md)
            .background(RoundRectBackground(background, UiTheme.Spacing.xs))
            .border(RoundRectBorder(border, UiTheme.Spacing.xs, Dp(1f)))
            .onEnter { hovered.value = true }
            .onExit { hovered.value = false }
            .onClick { onPressed() }

        multiplayerCell(room.hostName, contentWidth.fraction(0.18f, Dp(120f), Dp(280f)), theme, AlignmentX.Start)
        multiplayerCell(room.mapName, Grow.Std, theme, AlignmentX.Start)
        multiplayerCell(room.playersLabel, Dp(92f), theme, AlignmentX.Center)
        multiplayerCell(room.stateLabel, Dp(116f), theme, AlignmentX.Center)
        multiplayerCell(room.versionLabel, Dp(112f), theme, AlignmentX.Center)
        multiplayerCell(room.transportLabel, Dp(120f), theme, AlignmentX.Center)
        multiplayerCell(roomMarkers(room), Dp(68f), theme, AlignmentX.Center)
    }
}


fun UiScope.MultiplayerRoomList(
    model: MultiplayerRoomListModel,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.multiplayerRoomRowWidth,
    viewportHeight: Dp = UiTheme.Layout.scrollViewportHeight,
    actions: MultiplayerRoomListActions,
) {
    MultiplayerLobbySwitcher(
        selected = model.lobbyKind,
        theme = theme,
        contentWidth = contentWidth,
        onBack = actions.onBack,
        onConfigure = actions.onConfigure,
        onSelected = actions.onSwitchLobby,
    )

    if (model.rooms.isEmpty() && model.statusText.isNotEmpty()) {
        BodyText(model.statusText, theme, contentWidth)
    } else {
        ScrollableVerticalList(
            items = model.rooms,
            theme = theme,
            width = contentWidth,
            height = viewportHeight,
        ) { room ->
            MultiplayerRoomRow(room, theme, contentWidth) {
                actions.onJoinRoom(room.roomId)
            }
        }

        if (model.statusText.isNotEmpty()) {
            BodyText(model.statusText, theme, contentWidth)
        }
    }

    val buttonWidth = if (contentWidth.value < MULTIPLAYER_COMPACT_WIDTH_DP) {
        contentWidth.remainingAfter(UiTheme.Spacing.sm)
    } else {
        contentWidth.splitEvenly(
            count = 3,
            totalGap = Dp(3f * UiTheme.Spacing.sm.value),
            minWidth = Dp(180f),
            maxWidth = UiTheme.Layout.menuButtonWidth,
        )
    }
    if (contentWidth.value < MULTIPLAYER_COMPACT_WIDTH_DP) {
        Column(width = contentWidth) {
            TextIconButton("Refresh", Icon.Refresh, buttonWidth, theme) {
                actions.onRefresh()
            }
            TextIconButton("Join", Icon.Multiplayer, buttonWidth, theme) {
                actions.onJoinDirect()
            }
            TextIconButton("Host", Icon.Start, buttonWidth, theme) {
                actions.onHostGame()
            }
        }
    } else {
        Row {
            modifier.alignX(AlignmentX.Center)
            TextIconButton("Refresh", Icon.Refresh, buttonWidth, theme) {
                actions.onRefresh()
            }
            TextIconButton("Join", Icon.Multiplayer, buttonWidth, theme) {
                actions.onJoinDirect()
            }
            TextIconButton("Host", Icon.Start, buttonWidth, theme) {
                actions.onHostGame()
            }
        }
    }
}

private fun UiScope.MultiplayerLobbySwitcher(
    selected: MultiplayerLobbyKind,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    onBack: () -> Unit,
    onConfigure: () -> Unit,
    onSelected: (MultiplayerLobbyKind) -> Unit,
) {
    val buttonsWidth = contentWidth.remainingAfter(Dp(UiTheme.Layout.iconButtonSize.value * 2f))
    val buttonWidth = buttonsWidth.splitEvenly(
        count = MultiplayerLobbyKind.entries.size,
        totalGap = Dp(MultiplayerLobbyKind.entries.size * UiTheme.Spacing.sm.value),
        minWidth = if (contentWidth.value < MULTIPLAYER_COMPACT_WIDTH_DP) Dp(88f) else Dp(140f),
        maxWidth = UiTheme.Layout.menuButtonWidth,
    )
    Row(width = contentWidth, height = UiTheme.Layout.menuButtonHeight) {
        modifier.margin(bottom = UiTheme.Spacing.sm)
        IconButton(Icon.Back, theme, onPressed = onBack)
        MultiplayerLobbyKind.entries.forEach { kind ->
            val active = kind == selected
            TextIconButton(
                label = kind.label,
                icon = if (kind == MultiplayerLobbyKind.P2P) Icon.Multiplayer else Icon.Refresh,
                width = buttonWidth,
                theme = theme,
                emphasized = active,
                font = UiTheme.Fonts.bodySmall,
            ) {
                onSelected(kind)
            }
        }
        IconButton(
            icon = Icon.Settings,
            theme = theme,
            tooltip = I18n.multiplayer.configurePlayerName(),
            onPressed = onConfigure,
        )
    }
}

private fun UiScope.multiplayerCell(
    text: String,
    width: Dimension,
    theme: ColorSchemeDefinition,
    align: AlignmentX,
) {
    Text(text) {
        modifier
            .width(width)
            .height(UiTheme.Layout.menuButtonHeight)
            .padding(horizontal = UiTheme.Spacing.xs)
            .font(UiTheme.Fonts.bodySmall)
            .textAlign(align, AlignmentY.Center)
            .clipToBounds(true)
            .textColor(theme.palette.textPrimary)
    }
}

private fun roomStatusLabel(room: MultiplayerRoomItem): String =
    "${room.playersLabel} | ${room.stateLabel} | ${room.versionLabel} | ${room.transportLabel} ${roomMarkers(room)}"
        .trim()

private fun roomMarkers(room: MultiplayerRoomItem): String = buildString {
    if (room.requiresPassword) append("P")
    if (room.hasMods) {
        if (isNotEmpty()) append(" ")
        append("M")
    }
}

private const val MULTIPLAYER_COMPACT_WIDTH_DP: Float = 720f

data class MultiplayerRoomListActions(
    val onJoinRoom: (String) -> Unit,
    val onBack: () -> Unit,
    val onRefresh: () -> Unit,
    val onSwitchLobby: (MultiplayerLobbyKind) -> Unit,
    val onHostGame: () -> Unit,
    val onJoinDirect: () -> Unit,
    val onConfigure: () -> Unit,
)
