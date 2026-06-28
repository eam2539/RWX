package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import io.github.rwx.i18n.I18n

class ReplaySelectSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val viewModelFactory: ReplaySelectViewModelFactory,
    private val onAction: ReplaySelectActionHandler = ReplaySelectActionHandler {},
) {
    private val replays = mutableStateListOf<ReplayEntry>()

    fun refresh() {
        val entries = viewModelFactory.create().items()
        replays.clear()
        replays.addAll(entries)
    }

    fun dispatch(action: ReplaySelectAction) = onAction.onAction(action)

    fun createScene(): Scene = UiScene(REPLAY_SELECT_SCENE_NAME) {
        addPanelSurface(PanelStyle.Menu, "rwx-replay-select-panel", model) { theme ->
            ReplaySelectList(
                replays = replays.use(),
                theme = theme,
                onReplaySelected = { dispatch(ReplaySelectAction.SelectReplay(it)) },
                onBack = { dispatch(ReplaySelectAction.Back) },
            )
        }
    }

    companion object {
        const val REPLAY_SELECT_SCENE_NAME: String = "rwx-replay-select"
    }
}

private data class ReplaySelectLayoutMetrics(
    val contentWidth: Dp,
    val listHeight: Dp,
)

private fun UiScope.ReplaySelectList(
    replays: List<ReplayEntry>,
    theme: ColorSchemeDefinition,
    onReplaySelected: (ReplayEntry) -> Unit,
    onBack: () -> Unit,
) {
    val metrics = replaySelectLayoutMetrics()
    val filter = remember("")
    val visibleReplays = ReplayBrowser.visibleReplays(replays, filter.use())

    Row(width = metrics.contentWidth) {
        modifier.margin(bottom = UiTheme.Spacing.sm)
        IconButton(Icon.Back, theme, onPressed = onBack)
        Box(width = UiTheme.Layout.levelSelectControlLabelWidth, height = UiTheme.Layout.menuButtonHeight) {
            Row(width = Grow.Std, height = Grow.Std) {
                Icon(Icon.Search, UiTheme.Layout.textButtonGlyphSize, theme.palette.primary).modifier
                    .align(AlignmentX.Center, AlignmentY.Center)
                    .margin(horizontal = UiTheme.Spacing.xs)
                Text(I18n.replay.filter()) {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                        .font(UiTheme.Fonts.bodySmall)
                        .textAlign(AlignmentX.Start, AlignmentY.Center)
                        .textColor(theme.palette.textSecondary)
                }
            }
        }
        RwxTextField(filter.use()) {
            modifier
                .width(
                    metrics.contentWidth.remainingAfter(
                        Dp(
                            UiTheme.Layout.iconButtonSize.value +
                                    UiTheme.Layout.levelSelectControlLabelWidth.value
                        )
                    )
                )
                .height(UiTheme.Layout.menuButtonHeight)
                .padding(start = UiTheme.Spacing.sm)
                .hint(I18n.replay.searchHint())
                .font(UiTheme.Fonts.bodySmall)
                .colors(
                    textColor = theme.palette.textPrimary,
                    hintColor = theme.palette.textSecondary,
                    lineColor = theme.palette.borderSubtle,
                    lineColorFocused = theme.palette.primary,
                    cursorColor = theme.palette.primary,
                    selectionColor = theme.palette.primaryContainer,
                )
                .onChange { filter.value = it }
        }
    }

    if (visibleReplays.isEmpty()) {
        Box(width = metrics.contentWidth, height = metrics.listHeight) {
            Text(if (replays.isEmpty()) I18n.replay.empty() else I18n.replay.noMatches()) {
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
            items = visibleReplays,
            theme = theme,
            width = metrics.contentWidth,
            height = metrics.listHeight,
            framed = true,
            contentPadding = UiTheme.Spacing.xs,
        ) { replay ->
            ReplayRow(replay, metrics.contentWidth.remainingAfter(UiTheme.Spacing.md), theme) {
                onReplaySelected(replay)
            }
        }
    }

}

private fun UiScope.ReplayRow(
    replay: ReplayEntry,
    width: Dp,
    theme: ColorSchemeDefinition,
    onPressed: () -> Unit,
) {
    val hovered = remember(false)
    val isHovered = hovered.use()
    val background = if (isHovered) theme.palette.surfaceRaised else theme.palette.surfaceSunken
    val border = if (isHovered) theme.palette.primary else theme.palette.borderSubtle
    val iconColor = if (isHovered) theme.palette.secondary else theme.palette.primary
    val titleColor = if (isHovered) theme.palette.primary else theme.palette.textPrimary

    Box(width = width, height = UiTheme.Layout.replaySelectRowHeight) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .padding(horizontal = UiTheme.Spacing.md, vertical = UiTheme.Spacing.xs)
            .background(RoundRectBackground(background, UiTheme.Spacing.xs))
            .border(RoundRectBorder(border, UiTheme.Spacing.xs, Dp(1f)))
            .onEnter { hovered.value = true }
            .onExit { hovered.value = false }
            .onClick { onPressed() }

        Row(width = Grow.Std, height = Grow.Std) {
            Box(width = UiTheme.Layout.replaySelectIconSlotWidth, height = Grow.Std) {
                Icon(Icon.Replay, UiTheme.Layout.iconButtonGlyphSize, iconColor).modifier
                    .align(AlignmentX.Center, AlignmentY.Center)
            }
            Column(width = Grow.Std, height = Grow.Std) {
                modifier.align(AlignmentX.Center, AlignmentY.Center)
                Text(replay.displayName) {
                    modifier
                        .width(Grow.Std)
                        .height(FitContent)
                        .font(UiTheme.Fonts.bodyMedium)
                        .textColor(titleColor)
                        .clipToBounds(true)
                }
                Text("${replay.modifiedAt}  ${replay.sizeLabel}") {
                    modifier
                        .width(Grow.Std)
                        .height(FitContent)
                        .font(UiTheme.Fonts.caption)
                        .textColor(theme.palette.textSecondary)
                        .clipToBounds(true)
                }
            }
        }
    }
}

private fun UiScope.replaySelectLayoutMetrics(): ReplaySelectLayoutMetrics {
    val viewportWidthDp = Dp.fromPx(surface.viewportWidth.use()).value
    val viewportHeightDp = Dp.fromPx(surface.viewportHeight.use()).value
    val contentWidth = if (viewportWidthDp > 0f) {
        Dp(
            (viewportWidthDp - REPLAY_SELECT_HORIZONTAL_MARGIN_DP)
                .coerceIn(
                    UiTheme.Layout.levelSelectMinContentWidth.value,
                    UiTheme.Layout.levelSelectMaxContentWidth.value,
                )
        )
    } else {
        UiTheme.Layout.levelSelectContentWidth
    }
    val listHeight = if (viewportHeightDp > 0f) {
        Dp(
            (viewportHeightDp - REPLAY_SELECT_VERTICAL_CHROME_DP)
                .coerceIn(
                    UiTheme.Layout.replaySelectMinListHeight.value,
                    UiTheme.Layout.replaySelectMaxListHeight.value,
                )
        )
    } else {
        UiTheme.Layout.replaySelectListHeight
    }
    return ReplaySelectLayoutMetrics(contentWidth = contentWidth, listHeight = listHeight)
}

private const val REPLAY_SELECT_HORIZONTAL_MARGIN_DP: Float = 96f
private const val REPLAY_SELECT_VERTICAL_CHROME_DP: Float = 130f
