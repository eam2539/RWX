package io.github.rwx.ui.component

import com.corrodinggames.rts.gameFramework.GameEngine
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import io.github.rwx.i18n.I18n
import io.github.rwx.ui.*
import io.github.rwx.ui.model.*


fun UiScope.BattleRoom(
    model: BattleRoomModel,
    theme: ColorSchemeDefinition,
    actions: BattleRoomActions,
) {
    val metrics = battleRoomLayoutMetrics(
        playerCount = model.players.size,
        isAndroid = GameEngine.isAndroidPlatform(),
    )

    if (metrics.isPageScrollable) {
        val scrollState = rememberScrollState()
        ScrollArea(
            width = metrics.contentWidth,
            height = metrics.pageViewportHeight,
            withVerticalScrollbar = false,
            withHorizontalScrollbar = false,
            isScrollableVertical = true,
            isScrollableHorizontal = false,
            //scrollbarColor = theme.palette.primary,
            state = scrollState,
            containerModifier = {
                it
                    .backgroundColor(null)
                    .onDrag { event ->
                        scrollState.scrollDpY(Dp.fromPx(-event.pointer.delta.y).value)
                    }
            },
        ) {
            modifier.width(Grow.Std).height(FitContent)
            Column(width = metrics.contentWidth, height = FitContent) {
                BattleRoomContent(model, theme, metrics, actions)
            }
        }
        return
    }

    BattleRoomContent(model, theme, metrics, actions)
}

private fun UiScope.BattleRoomContent(
    model: BattleRoomModel,
    theme: ColorSchemeDefinition,
    metrics: BattleRoomLayoutMetrics,
    actions: BattleRoomActions,
) {

    if (metrics.isCompact) {
        Column(width = metrics.contentWidth) {
            modifier.align(AlignmentX.Center, AlignmentY.Top)
            BattleRoomInfoPanel(model, model.isHost, theme, metrics, actions.onBack, actions.onSelectMap,actions.onOpenOptions)
            BattleRoomPlayersPanel(model, theme, metrics, actions)
            BattleRoomChatPanel(model.chatLines, model.players, theme, metrics, actions.onSendChat)
            BattleRoomActionBar(model.isHost, theme, metrics, actions)
        }
    } else {
        Row(width = metrics.contentWidth) {
            modifier.align(AlignmentX.Center, AlignmentY.Top)
            BattleRoomInfoPanel(model, model.isHost, theme, metrics, actions.onBack, actions.onSelectMap,actions.onOpenOptions)
            Column(width = metrics.playersWidth) {
                modifier.margin(start = UiTheme.Spacing.lg)
                BattleRoomPlayersPanel(model, theme, metrics, actions)
                BattleRoomChatPanel(model.chatLines, model.players, theme, metrics, actions.onSendChat)
                BattleRoomActionBar(model.isHost, theme, metrics, actions)
            }
        }
    }
}

private fun UiScope.BattleRoomInfoPanel(
    model: BattleRoomModel,
    canSelectMap: Boolean,
    theme: ColorSchemeDefinition,
    metrics: BattleRoomLayoutMetrics,
    onBack: () -> Unit,
    onSelectMap: () -> Unit,
    onOpenOptions: () -> Unit
) {
    val width = metrics.infoWidth
    Column(width = width) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .padding(UiTheme.Spacing.sm)
            .background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.sm))
            .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.sm, Dp(1f)))

        Box(width = Grow.Std, height = metrics.mapPreviewHeight) {
            if (canSelectMap) {
                modifier.onClick { onSelectMap() }
            }
            MapPreviewImage(
                previewAssetPath = model.info.mapPreviewAssetPath,
                theme = theme,
                width = Grow.Std,
                height = metrics.mapPreviewHeight,
            )
            IconButton(Icon.Back, theme, onPressed = onBack).modifier
                .align(AlignmentX.Start, AlignmentY.Top)
        }

        Text(model.info.mapTypeLabel) {
            modifier
                .width(Grow.Std)
                .margin(top = UiTheme.Spacing.sm)
                .font(UiTheme.Fonts.headingSmall)
                .textAlign(AlignmentX.Center, AlignmentY.Center)
                .textColor(theme.palette.primary)
        }
        Text(model.info.mapName) {
            modifier
                .width(Grow.Std)
                .margin(top = UiTheme.Spacing.xs, bottom = UiTheme.Spacing.sm)
                .font(UiTheme.Fonts.bodyMedium)
                .textAlign(AlignmentX.Center, AlignmentY.Center)
                .isWrapText(true)
                .textColor(theme.palette.textPrimary)
        }
        if (model.info.rwxModeLabel != null) {
            BattleRoomModeBadge(model.info.rwxModeLabel, theme, width)
            model.info.rwxCompatibilityLabel?.let { line ->
                Text(line) {
                    modifier
                        .width(Grow.Std)
                        .margin(top = Dp(2f), bottom = UiTheme.Spacing.xs)
                        .font(UiTheme.Fonts.caption)
                        .textAlign(AlignmentX.Center, AlignmentY.Center)
                        .textColor(theme.palette.textSecondary)
                }
            }
        }
        model.info.detailLines.forEach { line ->
            Text(line) {
                modifier
                    .width(Grow.Std)
                    .margin(vertical = Dp(2f))
                    .font(UiTheme.Fonts.caption)
                    .textColor(theme.palette.textSecondary)
            }
        }
        if (model.isHost)
            Row {
                modifier.alignX(AlignmentX.Center)
                TextIconButton(
                    label = I18n.battleroom.options(),
                    icon = Icon.Options,
                    width = UiTheme.Layout.battleRoomActionButtonWidth,
                    theme = theme,
                    font = UiTheme.Fonts.bodyMedium,
                ) {
                    modifier.align(AlignmentX.Center, AlignmentY.Center)
                    onOpenOptions()
                }
            }

    }
}

private fun UiScope.BattleRoomModeBadge(
    label: String,
    theme: ColorSchemeDefinition,
    panelWidth: Dp,
) {
    Box(width = panelWidth, height = Dp(26f)) {
        Box(width = Dp(132f), height = Dp(24f)) {
            modifier
                .align(AlignmentX.Center, AlignmentY.Center)
                .background(RoundRectBackground(theme.palette.primaryContainer, Dp(4f)))
                .border(RoundRectBorder(theme.palette.primary, Dp(4f), Dp(1f)))
            Text(label) {
                modifier
                    .width(Grow.Std)
                    .height(Grow.Std)
                    .padding(horizontal = UiTheme.Spacing.xs)
                    .font(UiTheme.Fonts.caption)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
                    .clipToBounds(true)
                    .textColor(theme.palette.textPrimary)
            }
        }
    }
}

private fun UiScope.BattleRoomPlayersPanel(
    model: BattleRoomModel,
    theme: ColorSchemeDefinition,
    metrics: BattleRoomLayoutMetrics,
    actions: BattleRoomActions,
) {
    Column(width = metrics.playersWidth) {
        if (metrics.isCompact) {
            modifier.margin(top = UiTheme.Spacing.sm)
        }

        BattleRoomHeaderRow(theme, metrics)

        if (model.players.isEmpty()) {
            Box(
                width = metrics.playersWidth,
                height = metrics.playersViewportHeight,
            ) {
                Text(I18n.battleroom.players()) {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                        .font(UiTheme.Fonts.bodySmall)
                        .textAlign(AlignmentX.Center, AlignmentY.Center)
                        .textColor(theme.palette.textDisabled)
                }
            }
        } else {
            ScrollableVerticalList(
                items = model.players,
                theme = theme,
                width = metrics.playersWidth,
                height = metrics.playersViewportHeight,
            ) { player ->
                val clickable = model.isHost || player.isLocal
                BattleRoomPlayerRow(player, theme, metrics, clickable) { actions.onSelectPlayer(player.id) }
            }
        }
    }
}

private fun UiScope.BattleRoomHeaderRow(
    theme: ColorSchemeDefinition,
    metrics: BattleRoomLayoutMetrics,
) {
    Row(
        width = metrics.playersWidth,
        height = UiTheme.Layout.battleRoomRowHeight,
    ) {
        modifier
            .margin(bottom = UiTheme.Spacing.xs)
            .background(RoundRectBackground(theme.palette.surfaceRaised, UiTheme.Spacing.xs))

        battleRoomCell(
            I18n.battleroom.heading.name(),
            metrics.playerNameWidth,
            theme.palette.textSecondary,
            AlignmentX.Start
        )
        battleRoomCell(
            I18n.battleroom.heading.spawn(),
            metrics.playerSpawnWidth,
            theme.palette.textSecondary,
            AlignmentX.Center
        )
        battleRoomCell(
            I18n.battleroom.heading.team(),
            metrics.playerTeamWidth,
            theme.palette.textSecondary,
            AlignmentX.Center
        )
        battleRoomCell(
            I18n.battleroom.heading.ping(),
            metrics.playerPingWidth,
            theme.palette.textSecondary,
            AlignmentX.Center
        )
    }
}

private fun UiScope.BattleRoomPlayerRow(
    player: BattleRoomPlayer,
    theme: ColorSchemeDefinition,
    metrics: BattleRoomLayoutMetrics,
    clickable: Boolean,
    onClick: () -> Unit,
) {
    val hovered = remember(false)
    val isHovered = hovered.use()
    val rowBg = if (clickable && isHovered) theme.palette.surfaceRaised else theme.palette.surfaceSunken

    Row(
        width = metrics.playersWidth,
        height = UiTheme.Layout.battleRoomRowHeight,
    ) {
        modifier
            .margin(bottom = UiTheme.Spacing.xs)
            .background(RoundRectBackground(rowBg, UiTheme.Spacing.xs))
        if (clickable) {
            modifier
                .onEnter { hovered.value = true }
                .onExit { hovered.value = false }
                .onClick { onClick() }
        }

        val nameColor = if (player.isSpectator || !player.isReady) {
            theme.palette.textDisabled
        } else {
            BattleRoomTeamColors.colorFor(player.nameColorIndex, theme.palette.textPrimary)
        }
        val spawnColor = if (player.isSpectator) {
            theme.palette.textDisabled
        } else {
            BattleRoomTeamColors.colorFor(player.spawnColorIndex, theme.palette.textSecondary)
        }
        val teamColor = if (player.isSpectator) {
            theme.palette.textDisabled
        } else {
            BattleRoomTeamColors.colorFor(player.teamColorIndex, theme.palette.textSecondary)
        }
        val pingColor = if (player.isSpectator) theme.palette.textDisabled else theme.palette.textSecondary

        battleRoomCell(player.name, metrics.playerNameWidth, nameColor, AlignmentX.Start)
        battleRoomCell(player.spawnLabel, metrics.playerSpawnWidth, spawnColor, AlignmentX.Center)
        battleRoomCell(player.teamLabel, metrics.playerTeamWidth, teamColor, AlignmentX.Center)
        battleRoomCell(
            player.pingLabel,
            metrics.playerPingWidth,
            pingColor,
            AlignmentX.Center
        )
    }
}

private fun UiScope.battleRoomCell(text: String, width: Dp, color: Color, align: AlignmentX) {
    Text(text) {
        modifier
            .width(width)
            .height(UiTheme.Layout.battleRoomRowHeight)
            .padding(horizontal = UiTheme.Spacing.xs)
            .font(UiTheme.Fonts.bodySmall)
            .textAlign(align, AlignmentY.Center)
            .textColor(color)
    }
}

private fun UiScope.BattleRoomChatPanel(
    chatLines: List<BattleRoomChatLine>,
    players: List<BattleRoomPlayer>,
    theme: ColorSchemeDefinition,
    metrics: BattleRoomLayoutMetrics,
    onSend: (String) -> Unit,
) {
    Column(width = metrics.actionAreaWidth) {
        modifier.margin(top = UiTheme.Spacing.sm)

        Text(I18n.battleroom.chat()) {
            modifier
                .margin(bottom = UiTheme.Spacing.xs)
                .font(UiTheme.Fonts.headingSmall)
                .textColor(theme.palette.primary)
        }

        if (chatLines.isEmpty()) {
            Box(
                width = metrics.actionAreaWidth,
                height = metrics.chatViewportHeight,
            ) {
                modifier.background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.xs))
                Text(I18n.battleroom.emptyChat()) {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                        .font(UiTheme.Fonts.bodySmall)
                        .textAlign(AlignmentX.Center, AlignmentY.Center)
                        .textColor(theme.palette.textSecondary)
                }
            }
        } else {
            ScrollableVerticalList(
                items = chatLines,
                theme = theme,
                width = metrics.actionAreaWidth,
                height = metrics.chatViewportHeight,
            ) { line ->
                val color = battleRoomChatColorIndexFor(line, players)
                    ?.let { BattleRoomTeamColors.colorFor(it, theme.palette.textPrimary) }
                    ?: theme.palette.textSecondary
                Text(line.text) {
                    modifier
                        .width(Grow.Std)
                        .margin(vertical = Dp(2f))
                        .padding(horizontal = UiTheme.Spacing.xs)
                        .font(UiTheme.Fonts.bodySmall)
                        .isWrapText(true)
                        .textColor(color)
                }
            }
        }

        val draft = remember("")
        Row(width = metrics.actionAreaWidth) {
            modifier.margin(top = UiTheme.Spacing.xs)

            RwxTextField(draft.use()) {
                modifier
                    .width(Grow.Std)
                    .height(UiTheme.Layout.menuButtonHeight)
                    .padding(start = UiTheme.Spacing.sm)
                    .hint(I18n.battleroom.sendMessage())
                    .font(UiTheme.Fonts.bodySmall)
                    .colors(
                        textColor = theme.palette.textPrimary,
                        hintColor = theme.palette.textSecondary,
                        lineColor = theme.palette.borderSubtle,
                        lineColorFocused = theme.palette.primary,
                        cursorColor = theme.palette.primary,
                        selectionColor = theme.palette.primaryContainer,
                    )
                    .onChange { draft.value = it }
                    .onEnterPressed {
                        val message = it.trim()
                        if (message.isNotEmpty()) {
                            onSend(message)
                            draft.value = ""
                        }
                    }
            }
            TextIconButton(
                label = I18n.battleroom.send(),
                icon = Icon.Send,
                width = metrics.sendButtonWidth,
                theme = theme,
                font = UiTheme.Fonts.bodyMedium,
            ) {
                val message1 = draft.value.trim()
                if (message1.isNotEmpty()) {
                    onSend(message1)
                    draft.value = ""
                }
            }
        }
    }
}

private fun UiScope.BattleRoomActionBar(
    isHost: Boolean,
    theme: ColorSchemeDefinition,
    metrics: BattleRoomLayoutMetrics,
    actions: BattleRoomActions,
) {
    if (isHost) {
        BattleRoomActionRow(
            buttons = listOf(
                BattleRoomActionButtonSpec(I18n.battleroom.addAI(), Icon.AddAi, actions.onAddAI),
                BattleRoomActionButtonSpec(I18n.battleroom.start(), Icon.Start, actions.onStart, emphasized = true),
            ),
            theme = theme,
            metrics = metrics,
            topMargin = UiTheme.Spacing.sm,
        )
    }
}

private data class BattleRoomActionButtonSpec(
    val label: String,
    val icon: Icon,
    val onPressed: () -> Unit,
    val emphasized: Boolean = false,
)

private fun UiScope.BattleRoomActionRow(
    buttons: List<BattleRoomActionButtonSpec>,
    theme: ColorSchemeDefinition,
    metrics: BattleRoomLayoutMetrics,
    topMargin: Dp,
) {
    if (metrics.isCompact) {
        Column(width = metrics.actionAreaWidth) {
            modifier.align(AlignmentX.Center, AlignmentY.Top).margin(top = topMargin)
            buttons.forEach { spec ->
                TextIconButton(
                    label = spec.label,
                    icon = spec.icon,
                    width = metrics.compactActionButtonWidth,
                    theme = theme,
                    emphasized = spec.emphasized,
                    font = UiTheme.Fonts.bodyMedium,
                ) {
                    spec.onPressed()
                }
            }
        }
        return
    }

    val buttonWidth = metrics.actionAreaWidth.splitEvenly(
        count = buttons.size,
        totalGap = Dp(buttons.size * UiTheme.Spacing.sm.value),
        minWidth = Dp(136f),
        maxWidth = UiTheme.Layout.battleRoomActionButtonWidth,
    )
    Row {
        modifier.align(AlignmentX.Center, AlignmentY.Top).margin(top = topMargin)
        buttons.forEach { spec ->
            TextIconButton(
                label = spec.label,
                icon = spec.icon,
                width = buttonWidth,
                theme = theme,
                emphasized = spec.emphasized,
                font = UiTheme.Fonts.bodyMedium,
            ) {
                spec.onPressed()
            }
        }
    }
}

private data class BattleRoomLayoutMetrics(
    val contentWidth: Dp,
    val infoWidth: Dp,
    val playersWidth: Dp,
    val mapPreviewHeight: Dp,
    val playersViewportHeight: Dp,
    val chatViewportHeight: Dp,
    val playerNameWidth: Dp,
    val playerSpawnWidth: Dp,
    val playerTeamWidth: Dp,
    val playerPingWidth: Dp,
    val sendButtonWidth: Dp,
    val actionAreaWidth: Dp,
    val compactActionButtonWidth: Dp,
    val isCompact: Boolean,
    val pageViewportHeight: Dp,
    val isShortLandscape: Boolean,
    val isPageScrollable: Boolean,
)

private fun UiScope.battleRoomLayoutMetrics(
    playerCount: Int,
    isAndroid: Boolean,
): BattleRoomLayoutMetrics {
    val viewportWidthDp = Dp.fromPx(surface.viewportWidth.use()).value
    val viewportHeightDp = Dp.fromPx(surface.viewportHeight.use()).value
    val isShortLandscape = viewportWidthDp > viewportHeightDp &&
            viewportHeightDp in 1f..<BATTLE_ROOM_SHORT_LANDSCAPE_HEIGHT_DP
    val pageVerticalChrome = if (viewportHeightDp in 1f..<BATTLE_ROOM_SHORT_LANDSCAPE_HEIGHT_DP) {
        Dp((UiTheme.Spacing.xs.value + UiTheme.Spacing.sm.value) * 2f)
    } else {
        Dp((UiTheme.Spacing.xs.value + UiTheme.Spacing.xl.value) * 2f)
    }
    val contentWidth = ResponsiveContentWidth(
        defaultWidth = UiTheme.Layout.battleRoomContentWidth,
        minWidth = UiTheme.Layout.battleRoomMinContentWidth,
        maxWidth = UiTheme.Layout.battleRoomMaxContentWidth,
        horizontalMargin = Dp(64f),
    )
    val isCompact = !isShortLandscape && contentWidth.value < BATTLE_ROOM_COMPACT_WIDTH_DP
    val infoWidth = if (isShortLandscape) {
        contentWidth.fraction(0.38f, Dp(240f), Dp(320f))
    } else if (isCompact) {
        contentWidth
    } else {
        contentWidth.fraction(0.48f, Dp(420f), Dp(780f))
    }
    val playersWidth = if (isCompact) {
        contentWidth
    } else {
        contentWidth.remainingAfter(Dp(infoWidth.value + UiTheme.Spacing.lg.value + UiTheme.Spacing.sm.value), Dp(440f))
    }
    val mapPreviewHeight = if (isShortLandscape) Dp(150f) else ResponsiveViewportHeight(
        defaultHeight = UiTheme.Layout.battleRoomPreviewHeight,
        minHeight = UiTheme.Layout.battleRoomMinPreviewHeight,
        maxHeight = UiTheme.Layout.battleRoomMaxPreviewHeight,
        verticalChrome = if (isCompact) Dp(560f) else Dp(360f),
    )
    val responsivePlayersViewportHeight = if (isShortLandscape) {
        UiTheme.Layout.battleRoomMinPlayersViewportHeight
    } else ResponsiveViewportHeight(
        defaultHeight = UiTheme.Layout.battleRoomPlayersViewportHeight,
        minHeight = UiTheme.Layout.battleRoomMinPlayersViewportHeight,
        maxHeight = UiTheme.Layout.battleRoomMaxPlayersViewportHeight,
        verticalChrome = if (isCompact) Dp(560f) else Dp(520f),
    )
    val playersViewportHeight = battleRoomPlayersViewportHeight(
        playerCount = playerCount,
        responsiveHeight = responsivePlayersViewportHeight,
    )
    val responsiveChatViewportHeight = if (isShortLandscape) {
        UiTheme.Layout.battleRoomMinChatViewportHeight
    } else ResponsiveViewportHeight(
        defaultHeight = UiTheme.Layout.battleRoomChatViewportHeight,
        minHeight = UiTheme.Layout.battleRoomMinChatViewportHeight,
        maxHeight = UiTheme.Layout.battleRoomMaxChatViewportHeight,
        verticalChrome = if (isCompact) Dp(660f) else Dp(700f),
    )
    val chatViewportHeight = battleRoomChatViewportHeight(
        responsiveHeight = responsiveChatViewportHeight,
        isAndroid = isAndroid,
    )
    val spawnWidth = if (isCompact) Dp(56f) else UiTheme.Layout.battleRoomCellSpawnWidth
    val teamWidth = if (isCompact) Dp(56f) else UiTheme.Layout.battleRoomCellTeamWidth
    val pingWidth = if (isCompact) Dp(68f) else UiTheme.Layout.battleRoomCellPingWidth
    val fixedPlayerCells = Dp(spawnWidth.value + teamWidth.value + pingWidth.value)
    val nameWidth = playersWidth.remainingAfter(fixedPlayerCells, Dp(120f))
    return BattleRoomLayoutMetrics(
        contentWidth = contentWidth,
        infoWidth = infoWidth,
        playersWidth = playersWidth,
        mapPreviewHeight = mapPreviewHeight,
        playersViewportHeight = playersViewportHeight,
        chatViewportHeight = chatViewportHeight,
        playerNameWidth = nameWidth,
        playerSpawnWidth = spawnWidth,
        playerTeamWidth = teamWidth,
        playerPingWidth = pingWidth,
        sendButtonWidth = if (isCompact) Dp(96f) else UiTheme.Layout.battleRoomSendButtonWidth,
        actionAreaWidth = if (isCompact) contentWidth else playersWidth,
        compactActionButtonWidth = contentWidth.remainingAfter(UiTheme.Spacing.sm, Dp(180f)),
        isCompact = isCompact,
        pageViewportHeight = if (viewportHeightDp > 0f) {
            Dp((viewportHeightDp - pageVerticalChrome.value).coerceAtLeast(240f))
        } else {
            UiTheme.Layout.scrollViewportHeight
        },
        isShortLandscape = isShortLandscape,
        isPageScrollable = isAndroid || isCompact || isShortLandscape,
    )
}

internal fun battleRoomPlayersViewportHeight(
    playerCount: Int,
    responsiveHeight: Dp,
): Dp {
    val playerRowsHeight = playerCount
        .coerceIn(1, BATTLE_ROOM_MAX_VISIBLE_PLAYER_ROWS)
        .times(UiTheme.Layout.battleRoomRowHeight.value)
    return Dp(maxOf(responsiveHeight.value, playerRowsHeight))
}

internal fun battleRoomChatViewportHeight(
    responsiveHeight: Dp,
    isAndroid: Boolean,
): Dp = if (isAndroid) {
    Dp(maxOf(responsiveHeight.value, BATTLE_ROOM_ANDROID_MIN_CHAT_HEIGHT_DP))
} else {
    responsiveHeight
}

private const val BATTLE_ROOM_COMPACT_WIDTH_DP: Float = 980f
private const val BATTLE_ROOM_SHORT_LANDSCAPE_HEIGHT_DP: Float = 520f
private const val BATTLE_ROOM_MAX_VISIBLE_PLAYER_ROWS: Int = 12
private const val BATTLE_ROOM_ANDROID_MIN_CHAT_HEIGHT_DP: Float = 200f
