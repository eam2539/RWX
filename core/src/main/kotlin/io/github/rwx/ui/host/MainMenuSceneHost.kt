package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import io.github.rwx.ui.UiTheme
import io.github.rwx.ui.component.*
import io.github.rwx.ui.model.*
import io.github.rwx.ui.splitEvenly

class MainMenuSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onAction: (MainMenuAction) -> Unit = {},
) {
    var items: List<MainMenuItem> = emptyList()
        private set

    private val menuItems = mutableStateListOf<MainMenuItem>()
    private val battleBackgroundVisible = mutableStateOf(false)

    fun updateItems(conditions: MainMenuConditions) {
        val next = MainMenuViewModel.items(conditions)
        if (next != items) {
            items = next
            menuItems.atomic {
                clear()
                addAll(next)
            }
        }
    }

    /** Dispatches a menu action to the handler. Kept separate so click routing is testable. */
    fun dispatch(action: MainMenuAction) {
        onAction(action)
    }

    fun setBattleBackgroundVisible(visible: Boolean) {
        battleBackgroundVisible.value = visible
    }

    fun createScene(): Scene = UiScene(MAIN_MENU_SCENE_NAME) {
        addPanelSurface(
            PanelStyle.Menu,
            "main-menu-panel",
            model,
            backgroundColor = { theme ->
                if (battleBackgroundVisible.use()) {
                    TRANSPARENT_BACKGROUND
                } else {
                    theme.palette.panelOverlayLight.withAlpha(MAIN_MENU_SURFACE_ALPHA)
                }
            },
            themeBackgroundColor = { theme ->
                theme.palette.panelOverlayLight.withAlpha(MAIN_MENU_SURFACE_ALPHA)
            },
        ) { theme ->
            MainMenuLauncher(menuItems.use(), theme, ::dispatch)
        }
    }

    companion object {
        const val MAIN_MENU_SCENE_NAME: String = "main-menu"
        const val MENU_TITLE: String = "RWX"
        const val MENU_SUBTITLE: String = "Rusted Warfare Extension"
    }
}

private val TRANSPARENT_BACKGROUND = Color("00000000")

private const val MAIN_MENU_SURFACE_ALPHA: Float = 0.52f
private const val MAIN_MENU_PANEL_START_ALPHA: Float = 0.68f
private const val MAIN_MENU_PANEL_END_ALPHA: Float = 0.58f
private const val MAIN_MENU_HORIZONTAL_MARGIN_DP: Float = 48f
private const val MAIN_MENU_VERTICAL_CHROME_DP: Float = 260f
private const val MAIN_MENU_COLUMN_COUNT: Int = 2
private val MAIN_MENU_FOOTER_BUTTON_WIDTH: Dp = Dp(220f)
private val MAIN_MENU_FOOTER_HEIGHT: Dp = Dp(64f)

private data class MainMenuLayoutMetrics(
    val contentWidth: Dp,
    val menuViewportHeight: Dp,
    val isShortLandscape: Boolean,
)

private fun UiScope.MainMenuLauncher(
    items: List<MainMenuItem>,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    onAction: (MainMenuAction) -> Unit,
) {
    val metrics = mainMenuLayoutMetrics()
    val menuWidth = mainMenuPanelWidth(metrics.contentWidth)
    val primaryItems = items.filterNot { it.action == MainMenuAction.About || it.action == MainMenuAction.Exit }
    val footerItems = items.filter { it.action == MainMenuAction.About || it.action == MainMenuAction.Exit }

    MainMenuHeader(metrics.contentWidth, theme, metrics.isShortLandscape)
    if (metrics.isShortLandscape) {
        MainMenuHorizontal(
            items = primaryItems,
            theme = theme,
            width = metrics.contentWidth,
            height = metrics.menuViewportHeight,
            onAction = onAction,
        )
    } else {
        MainMenuColumns(
            primaryItems,
            theme,
            width = menuWidth,
            height = metrics.menuViewportHeight,
        ) { item ->
            mainMenuTileCard(item, theme) {
                onAction(item.action)
            }
        }
    }
    MainMenuFooter(footerItems, theme, metrics.contentWidth, onAction)
}

private fun UiScope.MainMenuFooter(
    items: List<MainMenuItem>,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    width: Dp,
    onAction: (MainMenuAction) -> Unit,
) {
    Box(width = width, height = MAIN_MENU_FOOTER_HEIGHT) {
        modifier.margin(top = UiTheme.Spacing.xs)
        Row(width = FitContent, height = UiTheme.Layout.menuButtonHeight) {
            modifier.align(AlignmentX.Center, AlignmentY.Center)
            items.forEach { item ->
                TextIconButton(
                    label = item.label,
                    icon = item.action.menuIcon,
                    width = MAIN_MENU_FOOTER_BUTTON_WIDTH,
                    theme = theme,
                ) {
                    onAction(item.action)
                }
            }
        }
    }
}

private fun UiScope.MainMenuHorizontal(
    items: List<MainMenuItem>,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    width: Dp,
    height: Dp,
    onAction: (MainMenuAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    val initialized = remember(false)
    if (!initialized.use()) {
        scrollState.scrollRelativeX(0f, smooth = false)
        initialized.value = true
    }
    Box(width = width, height = height) {
        modifier
            .alignX(AlignmentX.Center)
            .margin(top = UiTheme.Spacing.xs, bottom = UiTheme.Spacing.sm)
            .background(
                LinearGradientBackground(
                    theme.palette.surfaceBase.withAlpha(MAIN_MENU_PANEL_START_ALPHA),
                    theme.palette.panelOverlayDark.withAlpha(MAIN_MENU_PANEL_END_ALPHA),
                    cornerRadius = UiTheme.Spacing.sm,
                )
            )
            .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.sm, Dp(1f)))
            .padding(horizontal = UiTheme.Spacing.sm, vertical = UiTheme.Spacing.xs)

        ScrollArea(
            width = Grow.Std,
            height = Grow.Std,
            withVerticalScrollbar = false,
            withHorizontalScrollbar = true,
            isScrollableVertical = false,
            isScrollableHorizontal = true,
            scrollbarColor = theme.palette.primary,
            state = scrollState,
            containerModifier = {
                it
                    .backgroundColor(null)
                    .onDrag { event ->
                        scrollState.scrollDpX(Dp.fromPx(-event.pointer.delta.x).value)
                    }
            },
        ) {
            modifier.width(FitContent).height(Grow.Std)
            Row(width = FitContent, height = Grow.Std) {
                items.forEach { item ->
                    Box(width = UiTheme.Layout.mainMenuTileWidth, height = Grow.Std) {
                        mainMenuTileCard(item, theme) {
                            onAction(item.action)
                        }
                    }
                }
            }
        }
    }
}

private fun UiScope.MainMenuColumns(
    items: List<MainMenuItem>,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    width: Dp,
    height: Dp,
    itemContent: UiScope.(MainMenuItem) -> Unit,
) {
    val leftItems = items.filterIndexed { index, _ -> index % MAIN_MENU_COLUMN_COUNT == 0 }
    val rightItems = items.filterIndexed { index, _ -> index % MAIN_MENU_COLUMN_COUNT == 1 }
    val columnWidth = mainMenuColumnWidth(width)
    val columnsWidth = mainMenuColumnsContentWidth(columnWidth)
    val scrollState = rememberScrollState()

    Box(width = width, height = height) {
        modifier
            .alignX(AlignmentX.Center)
            .margin(top = UiTheme.Spacing.md, bottom = UiTheme.Spacing.lg)
            .background(
                LinearGradientBackground(
                    theme.palette.surfaceBase.withAlpha(MAIN_MENU_PANEL_START_ALPHA),
                    theme.palette.panelOverlayDark.withAlpha(MAIN_MENU_PANEL_END_ALPHA),
                    cornerRadius = UiTheme.Spacing.sm,
                )
            )
            .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.sm, Dp(1f)))
            .padding(vertical = UiTheme.Spacing.md)

        ScrollArea(
            width = Grow.Std,
            height = Grow.Std,
            withVerticalScrollbar = true,
            withHorizontalScrollbar = false,
            isScrollableVertical = true,
            isScrollableHorizontal = false,
            scrollbarColor = theme.palette.primary,
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

            Box(width = Grow.Std, height = FitContent) {
                Row(width = columnsWidth, height = FitContent) {
                    modifier.align(AlignmentX.Center, AlignmentY.Top)
                    MainMenuColumn(leftItems, columnWidth, Dp.ZERO, Dp.ZERO, itemContent)
                    MainMenuColumn(
                        rightItems,
                        columnWidth,
                        UiTheme.Layout.mainMenuColumnGap,
                        UiTheme.Layout.mainMenuColumnStagger,
                        itemContent,
                    )
                }
            }
        }
    }
}

private fun UiScope.MainMenuColumn(
    items: List<MainMenuItem>,
    width: Dp,
    startOffset: Dp,
    topOffset: Dp,
    itemContent: UiScope.(MainMenuItem) -> Unit,
) {
    Column(width = width, height = FitContent) {
        modifier.margin(start = startOffset, top = topOffset)
        items.forEach { item ->
            itemContent(item)
        }
    }
}

private fun mainMenuPanelWidth(contentWidth: Dp): Dp =
    Dp(mainMenuColumnsContentWidth(UiTheme.Layout.mainMenuTileWidth).value.coerceAtMost(contentWidth.value))

private fun mainMenuColumnWidth(contentWidth: Dp): Dp =
    contentWidth.splitEvenly(
        count = MAIN_MENU_COLUMN_COUNT,
        totalGap = UiTheme.Layout.mainMenuColumnGap,
        minWidth = UiTheme.Layout.mainMenuTileMinWidth,
        maxWidth = UiTheme.Layout.mainMenuTileMaxWidth,
    )

private fun mainMenuColumnsContentWidth(columnWidth: Dp): Dp =
    Dp(columnWidth.value * MAIN_MENU_COLUMN_COUNT + UiTheme.Layout.mainMenuColumnGap.value)

private fun UiScope.mainMenuTileCard(
    item: MainMenuItem,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    onPressed: () -> Unit,
) {
    val hovered = remember(false)
    val isHovered = hovered.use()
    val background = if (isHovered) theme.palette.surfaceRaised else theme.palette.surfaceSunken
    val border = if (isHovered) theme.palette.primary else theme.palette.borderSubtle
    val iconColor = if (isHovered) theme.palette.secondary else theme.palette.primary
    val textColor = if (isHovered) theme.palette.primary else theme.palette.textPrimary

    Box(width = Grow.Std, height = UiTheme.Layout.mainMenuTileHeight) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .background(RoundRectBackground(background, UiTheme.Spacing.sm))
            .border(RoundRectBorder(border, UiTheme.Spacing.sm, Dp(1f)))
            .onEnter { hovered.value = true }
            .onExit { hovered.value = false }
            .onClick { onPressed() }

        Column(width = Grow.Std, height = Grow.Std) {
            modifier
                .align(AlignmentX.Center, AlignmentY.Center)
                .padding(horizontal = UiTheme.Spacing.sm, vertical = UiTheme.Spacing.sm)
            Box(width = Grow.Std, height = Dp(62f)) {
                Icon(item.action.menuIcon, UiTheme.Layout.mainMenuTileIconSize, iconColor).modifier
                    .align(AlignmentX.Center, AlignmentY.Bottom)
            }
            Text(item.label) {
                modifier
                    .width(Grow.Std)
                    .height(Grow.Std)
                    .font(UiTheme.Fonts.bodySmall)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
                    .isWrapText(true)
                    .clipToBounds(true)
                    .textColor(textColor)
            }
        }
    }
}

internal fun UiScope.MainMenuHeader(
    contentWidth: Dp,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    isShortLandscape: Boolean = false,
) {
    GradientMainTitle(MainMenuSceneHost.MENU_TITLE, contentWidth, theme)
    Text(MainMenuSceneHost.MENU_SUBTITLE) {
        modifier
            .width(contentWidth)
            .margin(bottom = if (isShortLandscape) UiTheme.Spacing.sm else UiTheme.Spacing.xl)
            .font(UiTheme.Fonts.bodySmall)
            .textAlign(AlignmentX.Center, AlignmentY.Center)
            .textColor(theme.palette.textSecondary)
    }
}

internal fun UiScope.GradientMainTitle(
    text: String,
    contentWidth: Dp,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
) {
    Box(width = contentWidth, height = UiTheme.Layout.mainMenuTitleHeight) {
        modifier
            .width(contentWidth)
            .margin(bottom = UiTheme.Spacing.xs)

        GradientText(text) {
            modifier
                .height(Grow.Std)
                .align(AlignmentX.Center, AlignmentY.Center)
                .font(UiTheme.Fonts.displayTitle)
                .textAlign(AlignmentX.Center, AlignmentY.Center)
                .gradientColors(theme.palette.secondary, theme.palette.primary)
        }
    }
}

private fun UiScope.mainMenuLayoutMetrics(): MainMenuLayoutMetrics {
    val viewportWidthDp = Dp.fromPx(surface.viewportWidth.use()).value
    val viewportHeightDp = Dp.fromPx(surface.viewportHeight.use()).value
    val isShortLandscape = viewportWidthDp > viewportHeightDp &&
            viewportHeightDp in 1f..<MAIN_MENU_SHORT_LANDSCAPE_HEIGHT_DP
    val contentWidth = if (viewportWidthDp > 0f) {
        Dp(
            (viewportWidthDp - MAIN_MENU_HORIZONTAL_MARGIN_DP)
                .coerceIn(
                    UiTheme.Layout.mainMenuMinContentWidth.value,
                    UiTheme.Layout.mainMenuMaxContentWidth.value
                )
        )
    } else {
        UiTheme.Layout.mainMenuContentWidth
    }
    val viewportHeight = if (isShortLandscape) {
        Dp(UiTheme.Layout.mainMenuTileHeight.value + MAIN_MENU_HORIZONTAL_SCROLLBAR_CHROME_DP)
    } else if (viewportHeightDp > 0f) {
        Dp(
            (viewportHeightDp - MAIN_MENU_VERTICAL_CHROME_DP)
                .coerceIn(
                    UiTheme.Layout.mainMenuMinViewportHeight.value,
                    UiTheme.Layout.mainMenuMaxViewportHeight.value,
                )
        )
    } else {
        UiTheme.Layout.mainMenuViewportHeight
    }
    return MainMenuLayoutMetrics(
        contentWidth = contentWidth,
        menuViewportHeight = viewportHeight,
        isShortLandscape = isShortLandscape,
    )
}

private const val MAIN_MENU_SHORT_LANDSCAPE_HEIGHT_DP: Float = 520f
private const val MAIN_MENU_HORIZONTAL_SCROLLBAR_CHROME_DP: Float = 28f

private val MainMenuAction.menuIcon: Icon
    get() = when (this) {
        MainMenuAction.Continue -> Icon.Continue
        MainMenuAction.SinglePlayer -> Icon.Map
        MainMenuAction.WatchReplay -> Icon.Replay
        MainMenuAction.Sandbox -> Icon.Sandbox
        MainMenuAction.Multiplayer,
        MainMenuAction.P2PMultiplayer -> Icon.Multiplayer

        MainMenuAction.Settings -> Icon.Settings
        MainMenuAction.Mods -> Icon.Mods
        MainMenuAction.ResourceBrowser -> Icon.Search
        MainMenuAction.About -> Icon.Help
        MainMenuAction.Exit -> Icon.Exit
    }
