package io.github.rwx.ui

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Texture2d
import io.github.rwx.i18n.I18n
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * A single map tile button for the level-select grid. The tile shows a map preview when the
 * matching `_map.png` asset exists, then the map name and player count.
 */
fun UiScope.MapTileButton(
    map: MapEntry,
    theme: ColorSchemeDefinition,
    width: Dp = UiTheme.Layout.levelSelectMapTileWidth,
    height: Dp = UiTheme.Layout.levelSelectMapTileHeight,
    previewHeight: Dp = UiTheme.Layout.levelSelectPreviewHeight,
    onPressed: () -> Unit,
) {
    val hovered = remember(false)
    val isHovered = hovered.use()
    val background = if (isHovered) theme.palette.surfaceRaised else theme.palette.surfaceSunken
    val border = if (isHovered) theme.palette.primary else theme.palette.borderSubtle

    Box(width = width, height = height) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .padding(UiTheme.Spacing.xs)
            .background(RoundRectBackground(background, UiTheme.Spacing.sm))
            .border(RoundRectBorder(border, UiTheme.Spacing.sm, Dp(1f)))
            .onEnter { hovered.value = true }
            .onExit { hovered.value = false }
            .onClick { onPressed() }

        Column(width = Grow.Std, height = Grow.Std) {
            MapPreview(map, theme, previewHeight)
            Text(map.displayName) {
                modifier
                    .width(Grow.Std)
                    .height(Dp(34f))
                    .margin(top = UiTheme.Spacing.xs)
                    .font(UiTheme.Fonts.caption)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
                    .isWrapText(true)
                    .textColor(theme.palette.textPrimary)
            }
            Text(map.playerLabel()) {
                modifier
                    .width(Grow.Std)
                    .height(Dp(16f))
                    .font(UiTheme.Fonts.caption)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
                    .textColor(theme.palette.textSecondary)
            }
        }
    }
}

private fun UiScope.MapPreview(map: MapEntry, theme: ColorSchemeDefinition, height: Dp) =
    Box(width = Grow.Std, height = height) {
        MapPreviewImage(map.previewAssetPath, theme, Grow.Std, height)
        map.ModeLabel()?.let { label ->
            MapModeBadge(label, theme, Dp(116f), overlay = true)
        }
    }

private fun UiScope.MapModeBadge(
    label: String,
    theme: ColorSchemeDefinition,
    width: Dp,
    overlay: Boolean = false,
) {
    Box(width = width, height = Dp(24f)) {
        if (overlay) {
            modifier
                .align(AlignmentX.End, AlignmentY.Top)
                .margin(top = UiTheme.Spacing.xs, end = UiTheme.Spacing.xs)
                .zLayer(UiSurface.LAYER_FLOATING)
        }
        modifier
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

/**
 * A bordered map-preview box that draws the `_map.png` asset at [previewAssetPath] (scaled to fit),
 * or a "No preview" placeholder when the asset is missing. Shared by the level-select grid tile and
 * the battle-room info panel; the caller supplies the box [width]/[height].
 */
internal fun UiScope.MapPreviewImage(
    previewAssetPath: String?,
    theme: ColorSchemeDefinition,
    width: Dimension = Grow.Std,
    height: Dp = UiTheme.Layout.levelSelectPreviewHeight,
) {
    Box(width = width, height = height) {
        modifier
            .background(RoundRectBackground(theme.palette.surfaceBase, UiTheme.Spacing.xs))
            .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.xs, Dp(1f)))

        val previewTexture = previewAssetPath?.let { MapPreviewTextureCache.textureFor(it) }
        if (previewTexture != null) {
            Image(previewTexture) {
                modifier
                    .width(Grow.Std)
                    .height(Grow.Std)
                    .imageSize(ImageSize.FitContent)
            }
        } else {
            Text("No preview") {
                modifier
                    .width(Grow.Std)
                    .height(Grow.Std)
                    .font(UiTheme.Fonts.caption)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
                    .textColor(theme.palette.textDisabled)
            }
        }
    }
}

private object MapPreviewTextureCache {
    private val textures = mutableMapOf<String, Texture2d>()

    fun textureFor(assetPath: String): Texture2d = textures.getOrPut(assetPath) {
        Texture2d(name = "rwx-map-preview:$assetPath") {
            Assets.defaultLoader.loadImage2d(assetPath).getOrNull() ?: AssetLoader.textureDataLoadFailed
        }
    }

    suspend fun preload(assetPaths: List<String>, onProgress: (Int, Int) -> Unit) {
        var completed = assetPaths.count { textures[it]?.isLoaded == true }
        onProgress(completed, assetPaths.size)
        assetPaths.filter { textures[it]?.isLoaded != true }
            .chunked(MAP_PREVIEW_PRELOAD_CONCURRENCY)
            .forEach { paths ->
                val loaded = coroutineScope {
                    paths.map { assetPath ->
                        async {
                            assetPath to (
                                    Assets.defaultLoader.loadImage2d(assetPath).getOrNull()
                                        ?: AssetLoader.textureDataLoadFailed
                                    )
                        }
                    }.awaitAll()
                }
                loaded.forEach { (assetPath, imageData) ->
                    val texture = Texture2d(name = "rwx-map-preview:$assetPath")
                    texture.upload(imageData)
                    textures[assetPath] = texture
                    completed++
                    onProgress(completed, assetPaths.size)
                }
            }
    }

    fun invalidate() {
        textures.clear()
    }
}

/** Preloads the map previews expected in the first visible rows of built-in map browsers. */
suspend fun preloadMapPreviewTextures(
    assetPaths: List<String>,
    onProgress: (Int, Int) -> Unit = { _, _ -> },
) {
    MapPreviewTextureCache.preload(assetPaths.distinct(), onProgress)
}

internal fun invalidateMapPreviewTextureCache() {
    MapPreviewTextureCache.invalidate()
}

private const val MAP_PREVIEW_PRELOAD_CONCURRENCY: Int = 4

private fun MapEntry.playerLabel(): String =
    if (type == LevelEntryType.SavedGame) {
        I18n.singleplayer.loadSave()
    } else {
        CompatibilityLabel() ?: playerCount?.let { "${it}p" } ?: "Scenario"
    }

private const val MAP_GRID_MAX_COLUMNS: Int = 6
private const val LEVEL_SELECT_HORIZONTAL_MARGIN_DP: Float = 96f
private const val LEVEL_SELECT_VERTICAL_CHROME_DP: Float = 152f
private const val LEVEL_SELECT_SHORT_VERTICAL_CHROME_DP: Float = 112f
private const val LEVEL_SELECT_SHORT_LANDSCAPE_HEIGHT_DP: Float = 520f
private const val LEVEL_SELECT_SHORT_LANDSCAPE_COLUMNS: Int = 4
private const val LEVEL_SELECT_SHORT_OPTION_COUNT: Int = 3

private data class LevelSelectLayoutMetrics(
    val contentWidth: Dp,
    val gridViewportHeight: Dp,
    val gridColumns: Int,
    val mapTileWidth: Dp,
    val mapTileHeight: Dp,
    val previewHeight: Dp,
    val controlHeight: Dp,
    val controlLabelWidth: Dp,
    val comboWidth: Dp,
    val isShortLandscape: Boolean,
)

data class LevelSelectListModel(
    val title: String,
    val maps: List<MapEntry>,
    val currentMode: LevelSelectMode,
    val availableModes: List<LevelSelectMode> = LevelSelectMode.entries,
    val isLoading: Boolean = false,
)

data class LevelSelectListActions(
    val onMapSelected: (MapEntry) -> Unit,
    val onModeSelected: (LevelSelectMode) -> Unit,
    val onBack: () -> Unit,
)


fun UiScope.LevelSelectList(
    model: LevelSelectListModel,
    theme: ColorSchemeDefinition,
    actions: LevelSelectListActions,
) {
    val metrics = levelSelectLayoutMetrics()
    val nameFilter = remember("")
    val selectedFilterIndex = remember(0)
    val selectedSortIndex = remember(0)
    val filters = LevelSelectMapBrowser.filterOptions(model.maps)
    val filterIndex = selectedFilterIndex.use().coerceIn(0, filters.lastIndex.coerceAtLeast(0))
    val sortOptions = LevelSelectSortOption.entries
    val sortIndex = selectedSortIndex.use().coerceIn(0, sortOptions.lastIndex)
    val visibleMaps = LevelSelectMapBrowser.visibleMaps(
        maps = model.maps,
        query = nameFilter.use(),
        filter = filters.getOrElse(filterIndex) { LevelSelectFilterOption.All },
        sort = sortOptions[sortIndex],
    )
    val rows = visibleMaps.chunked(metrics.gridColumns)

    LevelSelectFilterInput(nameFilter.use(), metrics, theme, actions.onBack) { nameFilter.value = it }
    LevelSelectOptionBar(
        modes = model.availableModes,
        selectedModeIndex = model.availableModes.indexOf(model.currentMode).coerceAtLeast(0),
        filters = filters,
        selectedFilterIndex = filterIndex,
        sortOptions = sortOptions,
        selectedSortIndex = sortIndex,
        contentWidth = metrics.contentWidth,
        metrics = metrics,
        theme = theme,
        onModeSelected = actions.onModeSelected,
        onFilterSelected = { selectedFilterIndex.value = it },
        onSortSelected = { selectedSortIndex.value = it },
    )

    if (rows.isEmpty()) {
        Box(width = metrics.contentWidth, height = metrics.gridViewportHeight) {
            if (model.isLoading) {
                CircularLoadingIndicator(Dp(34f), Dp(3f), theme)
                    .modifier.align(AlignmentX.Center, AlignmentY.Center)
            } else {
                Text(
                    if (model.currentMode.savedGames) "No saved games found" else "No maps match the current filter"
                ) {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                        .font(UiTheme.Fonts.bodySmall)
                        .textAlign(AlignmentX.Center, AlignmentY.Center)
                        .textColor(theme.palette.textSecondary)
                }
            }
        }
    } else {
        ScrollableVerticalList(
            rows,
            theme,
            width = metrics.contentWidth,
            height = metrics.gridViewportHeight,
        ) { rowMaps ->
            Row(
                width = metrics.contentWidth,
                height = metrics.mapTileHeight + UiTheme.Spacing.sm,
            ) {
                rowMaps.forEach { map ->
                    MapTileButton(
                        map = map,
                        theme = theme,
                        width = metrics.mapTileWidth,
                        height = metrics.mapTileHeight,
                        previewHeight = metrics.previewHeight,
                    ) {
                        actions.onMapSelected(map)
                    }
                }
                repeat(metrics.gridColumns - rowMaps.size) {
                    Box(
                        width = metrics.mapTileWidth,
                        height = metrics.mapTileHeight,
                    ) {
                        modifier.margin(UiTheme.Spacing.xs)
                    }
                }
            }
        }
    }

}

private fun UiScope.levelSelectLayoutMetrics(): LevelSelectLayoutMetrics {
    val viewportWidthDp = Dp.fromPx(surface.viewportWidth.use()).value
    val viewportHeightDp = Dp.fromPx(surface.viewportHeight.use()).value
    val isShortLandscape = viewportWidthDp > viewportHeightDp &&
            viewportHeightDp in 1f..<LEVEL_SELECT_SHORT_LANDSCAPE_HEIGHT_DP
    val contentWidth = if (viewportWidthDp > 0f) {
        Dp(
            (viewportWidthDp - LEVEL_SELECT_HORIZONTAL_MARGIN_DP)
                .coerceIn(
                    UiTheme.Layout.levelSelectMinContentWidth.value,
                    UiTheme.Layout.levelSelectMaxContentWidth.value
                )
        )
    } else {
        UiTheme.Layout.levelSelectContentWidth
    }
    val mapTileHeight = if (isShortLandscape) Dp(160f) else UiTheme.Layout.levelSelectMapTileHeight
    val gridColumns =
        if (isShortLandscape) LEVEL_SELECT_SHORT_LANDSCAPE_COLUMNS else levelSelectColumnsFor(contentWidth)
    val mapTileWidth = if (isShortLandscape) {
        Dp(contentWidth.value / gridColumns)
    } else {
        UiTheme.Layout.levelSelectMapTileWidth
    }
    val gridViewportHeight = if (isShortLandscape) {
        levelSelectGridHeightFor(
            availableHeightDp = viewportHeightDp - LEVEL_SELECT_SHORT_VERTICAL_CHROME_DP,
            rowHeight = mapTileHeight + UiTheme.Spacing.sm,
        )
    } else if (viewportHeightDp > 0f) {
        levelSelectGridHeightFor(
            availableHeightDp = viewportHeightDp - LEVEL_SELECT_VERTICAL_CHROME_DP,
            rowHeight = mapTileHeight + UiTheme.Spacing.sm,
        )
    } else {
        UiTheme.Layout.levelSelectGridViewportHeight
    }
    return LevelSelectLayoutMetrics(
        contentWidth = contentWidth,
        gridViewportHeight = gridViewportHeight,
        gridColumns = gridColumns,
        mapTileWidth = mapTileWidth,
        mapTileHeight = mapTileHeight,
        previewHeight = if (isShortLandscape) Dp(94f) else UiTheme.Layout.levelSelectPreviewHeight,
        controlHeight = if (isShortLandscape) Dp(44f) else UiTheme.Layout.menuButtonHeight,
        controlLabelWidth = if (isShortLandscape) Dp(92f) else UiTheme.Layout.levelSelectControlLabelWidth,
        comboWidth = if (isShortLandscape) {
            Dp((contentWidth.value - UiTheme.Spacing.lg.value * 2f) / LEVEL_SELECT_SHORT_OPTION_COUNT)
                .remainingAfter(Dp(92f), Dp(112f))
        } else {
            UiTheme.Layout.levelSelectComboWidth
        },
        isShortLandscape = isShortLandscape,
    )
}

private fun levelSelectColumnsFor(contentWidth: Dp): Int {
    val tileOuterWidth = UiTheme.Layout.levelSelectMapTileWidth.value + UiTheme.Spacing.sm.value
    return (contentWidth.value / tileOuterWidth).toInt().coerceIn(1, MAP_GRID_MAX_COLUMNS)
}

private fun levelSelectGridHeightFor(availableHeightDp: Float, rowHeight: Dp): Dp =
    Dp(availableHeightDp.coerceIn(rowHeight.value, UiTheme.Layout.levelSelectGridViewportHeight.value))

private fun UiScope.LevelSelectFilterInput(
    filter: String,
    metrics: LevelSelectLayoutMetrics,
    theme: ColorSchemeDefinition,
    onBack: () -> Unit,
    onChange: (String) -> Unit,
) {
    Row(width = metrics.contentWidth) {
        modifier.margin(bottom = if (metrics.isShortLandscape) UiTheme.Spacing.xs else UiTheme.Spacing.sm)
        IconButton(
            icon = Icon.Back,
            theme = theme,
            size = metrics.controlHeight,
            iconSize = UiTheme.Layout.textButtonGlyphSize,
            onPressed = onBack,
        )
        LevelSelectControlLabel(
            "Filter",
            Icon.Search,
            theme,
            width = metrics.controlLabelWidth,
            height = metrics.controlHeight,
        )
        RwxTextField(filter) {
            modifier
                .width(
                    metrics.contentWidth.remainingAfter(
                        Dp(metrics.controlHeight.value + metrics.controlLabelWidth.value)
                    )
                )
                .height(metrics.controlHeight)
                .padding(start = UiTheme.Spacing.sm)
                .hint("Map name")
                .font(UiTheme.Fonts.bodySmall)
                .colors(
                    textColor = theme.palette.textPrimary,
                    hintColor = theme.palette.textSecondary,
                    lineColor = theme.palette.borderSubtle,
                    lineColorFocused = theme.palette.primary,
                    cursorColor = theme.palette.primary,
                    selectionColor = theme.palette.primaryContainer,
                )
                .onChange { onChange(it) }
        }
    }
}

private fun UiScope.LevelSelectOptionBar(
    modes: List<LevelSelectMode>,
    selectedModeIndex: Int,
    filters: List<LevelSelectFilterOption>,
    selectedFilterIndex: Int,
    sortOptions: List<LevelSelectSortOption>,
    selectedSortIndex: Int,
    contentWidth: Dp,
    metrics: LevelSelectLayoutMetrics,
    theme: ColorSchemeDefinition,
    onModeSelected: (LevelSelectMode) -> Unit,
    onFilterSelected: (Int) -> Unit,
    onSortSelected: (Int) -> Unit,
) {
    if (metrics.isShortLandscape) {
        Row(width = contentWidth) {
            modifier.margin(bottom = UiTheme.Spacing.xs)
            LevelSelectCombo(
                "Map Type", Icon.Map, modes, selectedModeIndex, null, theme,
                labelWidth = metrics.controlLabelWidth,
                comboWidth = metrics.comboWidth,
                controlHeight = metrics.controlHeight,
            ) { index -> modes.getOrNull(index)?.let(onModeSelected) }
            Box(width = UiTheme.Spacing.lg, height = metrics.controlHeight) {}
            LevelSelectCombo(
                "Players", Icon.Team, filters, selectedFilterIndex, null, theme,
                labelWidth = metrics.controlLabelWidth,
                comboWidth = metrics.comboWidth,
                controlHeight = metrics.controlHeight,
            ) { onFilterSelected(it) }
            Box(width = UiTheme.Spacing.lg, height = metrics.controlHeight) {}
            LevelSelectCombo(
                "Sort", Icon.Sort, sortOptions, selectedSortIndex, null, theme,
                labelWidth = metrics.controlLabelWidth,
                comboWidth = metrics.comboWidth,
                controlHeight = metrics.controlHeight,
            ) { onSortSelected(it) }
        }
        return
    }
    val compact = contentWidth.value < LEVEL_SELECT_OPTION_BAR_MIN_WIDTH_DP
    if (compact) {
        Column(width = contentWidth) {
            modifier.margin(bottom = UiTheme.Spacing.md)
            Row(width = contentWidth) {
                LevelSelectCombo("Map Type", Icon.Map, modes, selectedModeIndex, contentWidth, theme) { index ->
                    modes.getOrNull(index)?.let(onModeSelected)
                }
            }
            Row(width = contentWidth) {
                modifier.margin(top = UiTheme.Spacing.xs)
                LevelSelectCombo("Players", Icon.Team, filters, selectedFilterIndex, contentWidth, theme) {
                    onFilterSelected(it)
                }
            }
            Row(width = contentWidth) {
                modifier.margin(top = UiTheme.Spacing.xs)
                LevelSelectCombo("Sort", Icon.Sort, sortOptions, selectedSortIndex, contentWidth, theme) {
                    onSortSelected(it)
                }
            }
        }
    } else {
        Row(width = contentWidth) {
            modifier.margin(bottom = UiTheme.Spacing.md)
            LevelSelectCombo("Map Type", Icon.Map, modes, selectedModeIndex, null, theme) { index ->
                modes.getOrNull(index)?.let(onModeSelected)
            }
            Box(width = UiTheme.Spacing.lg, height = UiTheme.Layout.menuButtonHeight) {}
            LevelSelectCombo("Players", Icon.Team, filters, selectedFilterIndex, null, theme) {
                onFilterSelected(it)
            }
            Box(width = UiTheme.Spacing.lg, height = UiTheme.Layout.menuButtonHeight) {}
            LevelSelectCombo("Sort", Icon.Sort, sortOptions, selectedSortIndex, null, theme) {
                onSortSelected(it)
            }
        }
    }
}

private fun UiScope.LevelSelectCombo(
    label: String,
    icon: Icon,
    items: List<Any>,
    selectedIndex: Int,
    contentWidth: Dp?,
    theme: ColorSchemeDefinition,
    labelWidth: Dp = UiTheme.Layout.levelSelectControlLabelWidth,
    comboWidth: Dp = UiTheme.Layout.levelSelectComboWidth,
    controlHeight: Dp = UiTheme.Layout.menuButtonHeight,
    onSelected: (Int) -> Unit,
) {
    LevelSelectControlLabel(label, icon, theme, labelWidth, controlHeight)
    RwxComboBox {
        modifier
            .width(
                contentWidth?.remainingAfter(labelWidth)
                    ?: comboWidth
            )
            .height(controlHeight)
            .font(UiTheme.Fonts.bodySmall)
            .items(items)
            .selectedIndex(selectedIndex)
            .colors(
                textColor = theme.palette.textPrimary,
                textBackgroundColor = theme.palette.surfaceSunken,
                textBackgroundHoverColor = theme.palette.surfaceRaised,
                expanderColor = theme.palette.primaryContainer,
                expanderHoverColor = theme.palette.primary,
                expanderArrowColor = theme.palette.textPrimary,
            )
            .popupColors(
                popupTextColor = theme.palette.textPrimary,
                popupBackgroundColor = theme.palette.surfaceBase,
                popupHoverColor = theme.palette.primaryContainer,
                popupHoverTextColor = theme.palette.textPrimary,
                popupBorderColor = theme.palette.borderSubtle,
            )
            .onItemSelected(onSelected)
    }
}

private fun UiScope.LevelSelectControlLabel(
    label: String,
    icon: Icon,
    theme: ColorSchemeDefinition,
    width: Dp = UiTheme.Layout.levelSelectControlLabelWidth,
    height: Dp = UiTheme.Layout.menuButtonHeight,
) {
    Row(width = width, height = height) {
        Icon(icon, UiTheme.Layout.textButtonGlyphSize, theme.palette.primary)
            .modifier.alignY(AlignmentY.Center)
        Text(label) {
            modifier
                .width(Grow.Std)
                .height(Grow.Std)
                .margin(start = UiTheme.Spacing.xs)
                .font(UiTheme.Fonts.caption)
                .textAlign(AlignmentX.Start, AlignmentY.Center)
                .clipToBounds(true)
                .textColor(theme.palette.textSecondary)
        }
    }
}

private const val LEVEL_SELECT_OPTION_BAR_MIN_WIDTH_DP: Float = 620f

private fun Dp.remainingAfter(used: Dp): Dp = Dp((value - used.value).coerceAtLeast(120f))
