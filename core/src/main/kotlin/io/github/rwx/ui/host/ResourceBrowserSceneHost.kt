package io.github.rwx.ui.host

import com.corrodinggames.rts.gameFramework.GameEngine
import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.Scene
import io.github.rwx.i18n.I18n
import io.github.rwx.ui.component.BodyText
import io.github.rwx.ui.ColorSchemeDefinition
import io.github.rwx.ui.component.PanelStyle
import io.github.rwx.ui.model.ResourceBrowserAction
import io.github.rwx.ui.model.ResourceBrowserItem
import io.github.rwx.ui.model.ResourceBrowserModel
import io.github.rwx.ui.model.ResourceBrowserSearchResult
import io.github.rwx.ui.model.ResourceBrowserType
import io.github.rwx.ui.ResponsiveContentWidth
import io.github.rwx.ui.ResponsiveViewportHeight
import io.github.rwx.ui.component.RwxTextField
import io.github.rwx.ui.component.ScrollableVerticalList
import io.github.rwx.ui.model.SettingsModel
import io.github.rwx.ui.UiTheme
import io.github.rwx.ui.component.addPanelSurface
import io.github.rwx.ui.component.CircularLoadingIndicator
import io.github.rwx.ui.component.Icon
import io.github.rwx.ui.component.IconButton
import io.github.rwx.ui.component.TextIconButton
import io.github.rwx.ui.fraction
import io.github.rwx.ui.remainingAfter
import io.github.rwx.ui.model.resourceBrowserColumnCount
import io.github.rwx.ui.splitEvenly

class ResourceBrowserSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onAction: (ResourceBrowserAction) -> Unit = {},
) {
    private val browserModel = mutableStateOf(ResourceBrowserModel())
    private val searchDraft = mutableStateOf("")

    fun currentModel(): ResourceBrowserModel = browserModel.value

    fun setLoading(loading: Boolean, statusText: String = browserModel.value.statusText) {
        browserModel.value = browserModel.value.copy(isLoading = loading, statusText = statusText)
    }

    fun setType(type: ResourceBrowserType) {
        browserModel.value = browserModel.value.copy(type = type, page = 1, items = emptyList())
    }

    fun applySearchResult(result: ResourceBrowserSearchResult) {
        val current = browserModel.value
        val items = if (result.append) current.items + result.items else result.items
        browserModel.value = current.copy(
            items = if (result.errorMessage == null) items else current.items,
            page = result.page,
            isLoading = false,
            statusText = result.errorMessage ?: if (items.isEmpty()) I18n.resourcebrowser.empty() else "",
        )
    }

    fun dispatch(action: ResourceBrowserAction) = onAction(action)

    fun createScene(): Scene = UiScene(RESOURCE_BROWSER_SCENE_NAME) {
        addPanelSurface(PanelStyle.Menu, "rwx-resource-browser-panel", model) { theme ->
            val metrics = resourceBrowserLayoutMetrics()
            val state = browserModel.use()

            ResourceBrowserFilters(
                state = state,
                searchText = searchDraft.use(),
                theme = theme,
                contentWidth = metrics.contentWidth,
                onBack = { dispatch(ResourceBrowserAction.Back) },
                onSearchTextChanged = { searchDraft.value = it },
                onTypeSelected = { dispatch(ResourceBrowserAction.SelectType(it)) },
                onSearch = {
                    browserModel.value = browserModel.value.copy(
                        keyword = searchDraft.value.trim(),
                        page = 1,
                        items = emptyList(),
                    )
                    dispatch(ResourceBrowserAction.Search)
                },
            )

            val rows = state.items.ifEmpty {
                listOf(
                    ResourceBrowserItem(
                        id = "empty",
                        title = state.statusText.ifBlank {
                            if (state.isLoading) I18n.resourcebrowser.loading() else I18n.resourcebrowser.empty()
                        },
                        type = state.type,
                        sourceName = state.source.displayName,
                    )
                )
            }
            val gridColumns = resourceBrowserColumnCount(GameEngine.isAndroidPlatform())
            if (gridColumns > 1 && rows.none { it.id == "empty" }) {
                ResourceBrowserGrid(
                    items = rows,
                    theme = theme,
                    metrics = metrics,
                    columns = gridColumns,
                    onOpen = { dispatch(ResourceBrowserAction.OpenLink(it)) },
                    onDownload = { dispatch(ResourceBrowserAction.Download(it)) },
                )
            } else {
                ResourceBrowserList(
                    items = rows,
                    theme = theme,
                    metrics = metrics,
                    onOpen = { dispatch(ResourceBrowserAction.OpenLink(it)) },
                    onDownload = { dispatch(ResourceBrowserAction.Download(it)) },
                )
            }

            ResourceBrowserActionBar(
                loading = state.isLoading,
                theme = theme,
                contentWidth = metrics.contentWidth,
                onLoadMore = { dispatch(ResourceBrowserAction.LoadMore) },
            )
        }
    }

    companion object {
        const val RESOURCE_BROWSER_SCENE_NAME: String = "rwx-resource-browser"
    }
}

private data class ResourceBrowserLayoutMetrics(
    val contentWidth: Dp,
    val viewportHeight: Dp,
)

private fun UiScope.resourceBrowserLayoutMetrics(): ResourceBrowserLayoutMetrics {
    val isAndroid = GameEngine.isAndroidPlatform()
    return ResourceBrowserLayoutMetrics(
        contentWidth = ResponsiveContentWidth(
            defaultWidth = UiTheme.Layout.modsContentWidth,
            minWidth = UiTheme.Layout.modsMinContentWidth,
            maxWidth = UiTheme.Layout.modsMaxContentWidth,
        ),
        viewportHeight = ResponsiveViewportHeight(
            defaultHeight = UiTheme.Layout.modsViewportHeight,
            minHeight = if (isAndroid) {
                RESOURCE_BROWSER_ANDROID_MIN_VIEWPORT_HEIGHT
            } else {
                UiTheme.Layout.modsMinViewportHeight
            },
            maxHeight = UiTheme.Layout.modsMaxViewportHeight,
            verticalChrome = RESOURCE_BROWSER_VERTICAL_CHROME,
        ),
    )
}

private fun UiScope.ResourceBrowserList(
    items: List<ResourceBrowserItem>,
    theme: ColorSchemeDefinition,
    metrics: ResourceBrowserLayoutMetrics,
    onOpen: (ResourceBrowserItem) -> Unit,
    onDownload: (ResourceBrowserItem) -> Unit,
) {
    ScrollableVerticalList(
        items = items,
        theme = theme,
        width = metrics.contentWidth,
        height = metrics.viewportHeight,
        framed = true,
        contentPadding = UiTheme.Spacing.sm,
    ) { item ->
        if (item.id == "empty") {
            BodyText(item.title, theme, metrics.contentWidth)
        } else {
            ResourceBrowserCard(
                item = item,
                theme = theme,
                contentWidth = metrics.contentWidth.remainingAfter(UiTheme.Spacing.lg),
                onOpen = { onOpen(item) },
                onDownload = { onDownload(item) },
            )
        }
    }
}

private fun UiScope.ResourceBrowserGrid(
    items: List<ResourceBrowserItem>,
    theme: ColorSchemeDefinition,
    metrics: ResourceBrowserLayoutMetrics,
    columns: Int,
    onOpen: (ResourceBrowserItem) -> Unit,
    onDownload: (ResourceBrowserItem) -> Unit,
) {
    val gridWidth = metrics.contentWidth.remainingAfter(Dp(UiTheme.Spacing.sm.value * 2f))
    val cardWidth = gridWidth.splitEvenly(
        count = columns,
        minWidth = Dp(280f),
        maxWidth = gridWidth,
    )
    ScrollableVerticalList(
        items = items.chunked(columns),
        theme = theme,
        width = metrics.contentWidth,
        height = metrics.viewportHeight,
        framed = true,
        contentPadding = UiTheme.Spacing.sm,
    ) { rowItems ->
        Row(width = gridWidth, height = RESOURCE_BROWSER_CARD_HEIGHT) {
            rowItems.forEach { item ->
                ResourceBrowserCard(
                    item = item,
                    theme = theme,
                    contentWidth = cardWidth,
                    onOpen = { onOpen(item) },
                    onDownload = { onDownload(item) },
                )
            }
        }
    }
}

private fun UiScope.ResourceBrowserFilters(
    state: ResourceBrowserModel,
    searchText: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    onBack: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onTypeSelected: (ResourceBrowserType) -> Unit,
    onSearch: () -> Unit,
) {
    Column(width = contentWidth, height = FitContent) {
        ResourceBrowserChoiceBar(
            onBack = onBack,
            contentWidth = contentWidth,
            theme = theme,
            type = state.type,
            onTypeSelected = onTypeSelected,
        )
        Row(width = contentWidth, height = UiTheme.Layout.menuButtonHeight) {
            modifier.margin(bottom = UiTheme.Spacing.sm)
            Icon(Icon.Search, UiTheme.Layout.textButtonGlyphSize, theme.palette.primary)
                .modifier.alignY(AlignmentY.Center)
            RwxTextField(searchText) {
                modifier
                    .width(Grow.Std)
                    .height(UiTheme.Layout.menuButtonHeight)
                    .margin(start = UiTheme.Spacing.sm)
                    .hint(I18n.resourcebrowser.searchHint())
                    .font(UiTheme.Fonts.bodySmall)
                    .colors(
                        textColor = theme.palette.textPrimary,
                        hintColor = theme.palette.textSecondary,
                        lineColor = theme.palette.borderSubtle,
                        lineColorFocused = theme.palette.primary,
                        cursorColor = theme.palette.primary,
                        selectionColor = theme.palette.primaryContainer,
                    )
                    .onChange(onSearchTextChanged)
            }
            TextIconButton(
                label = I18n.resourcebrowser.search(),
                icon = Icon.Search,
                width = UiTheme.Layout.modsToggleButtonWidth,
                theme = theme,
                onPressed = onSearch,
            )
        }
    }
}

private fun UiScope.ResourceBrowserChoiceBar(
    onBack: () -> Unit,
    contentWidth: Dp,
    theme: ColorSchemeDefinition,
    type: ResourceBrowserType,
    onTypeSelected: (ResourceBrowserType) -> Unit,
) {
    val choicesWidth = contentWidth.remainingAfter(UiTheme.Layout.iconButtonSize, Dp(280f))
    val groupWidth = choicesWidth.splitEvenly(
        count = 2,
        totalGap = UiTheme.Spacing.sm,
        minWidth = Dp(140f),
        maxWidth = choicesWidth,
    )
    Row(width = contentWidth, height = UiTheme.Layout.menuButtonHeight) {
        modifier.margin(bottom = UiTheme.Spacing.xs)
        IconButton(Icon.Back, theme, onPressed = onBack)
        ResourceBrowserChoiceGroup(
            label = I18n.resourcebrowser.resourceType(),
            icon = Icon.Map,
            options = ResourceBrowserType.entries,
            selected = type,
            theme = theme,
            contentWidth = groupWidth,
            optionLabel = { it.label },
            onSelected = onTypeSelected,
        )
    }
}

private fun <T> UiScope.ResourceBrowserChoiceGroup(
    label: String,
    icon: Icon,
    options: List<T>,
    selected: T,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
) {
    val labelWidth = contentWidth.fraction(0.25f, minWidth = Dp(72f), maxWidth = Dp(116f))
    val optionsWidth = contentWidth.remainingAfter(labelWidth, Dp(120f))
    val optionWidth = optionsWidth.splitEvenly(
        count = options.size,
        minWidth = Dp(76f),
        maxWidth = optionsWidth,
    )
    Row(width = contentWidth, height = Grow.Std) {
        Box(width = labelWidth, height = Grow.Std) {
            Row(width = Grow.Std, height = Grow.Std) {
                Icon(icon, UiTheme.Layout.textButtonGlyphSize, theme.palette.primary)
                    .modifier.alignY(AlignmentY.Center)
                Text(label) {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                        .margin(start = UiTheme.Spacing.sm)
                        .font(UiTheme.Fonts.caption)
                        .textAlign(AlignmentX.Start, AlignmentY.Center)
                        .textColor(theme.palette.textPrimary)
                }
            }
        }
        options.forEach { option ->
            TextIconButton(
                label = optionLabel(option),
                icon = if (option == selected) Icon.Apply else Icon.Filter,
                width = optionWidth,
                theme = theme,
                emphasized = option == selected,
                font = UiTheme.Fonts.caption,
            ) {
                onSelected(option)
            }
        }
    }
}

private fun UiScope.ResourceBrowserCard(
    item: ResourceBrowserItem,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    onOpen: () -> Unit,
    onDownload: () -> Unit,
) {
    Row(width = contentWidth, height = RESOURCE_BROWSER_CARD_HEIGHT) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .padding(horizontal = UiTheme.Spacing.md, vertical = UiTheme.Spacing.sm)
            .background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.xs))
            .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.xs, Dp(1f)))

        Box(width = Dp(92f), height = Grow.Std) {
            modifier
                .background(RoundRectBackground(theme.palette.surfaceBase, UiTheme.Spacing.xs))
                .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.xs, Dp(1f)))
            val previewTexture = item.imageUrl?.let(ResourceBrowserPreviewTextureCache::textureFor)
            if (previewTexture != null) {
                Image(previewTexture) {
                    modifier
                        .width(Grow.Std)
                        .height(Grow.Std)
                        .padding(UiTheme.Spacing.xs)
                        .imageSize(ImageSize.FitContent)
                }
            } else {
                Icon(
                    if (item.type == ResourceBrowserType.Mod) Icon.Mods else Icon.Map,
                    Dp(38f),
                    theme.palette.primary,
                ).modifier.align(AlignmentX.Center, AlignmentY.Center)
            }
        }

        Column(width = Grow.Std, height = Grow.Std) {
            modifier.margin(start = UiTheme.Spacing.md, end = UiTheme.Spacing.sm)
            Text(item.title) {
                modifier
                    .width(Grow.Std)
                    .height(Dp(32f))
                    .font(UiTheme.Fonts.bodyMedium)
                    .clipToBounds(true)
                    .textColor(theme.palette.textPrimary)
            }
            Text(item.metadataText()) {
                modifier
                    .width(Grow.Std)
                    .height(Dp(26f))
                    .font(UiTheme.Fonts.caption)
                    .clipToBounds(true)
                    .textColor(theme.palette.textSecondary)
            }
            item.description?.takeIf { it.isNotBlank() }?.let { description ->
                Text(description) {
                    modifier
                        .width(Grow.Std)
                        .height(Dp(30f))
                        .font(UiTheme.Fonts.caption)
                        .clipToBounds(true)
                        .textColor(theme.palette.textSecondary)
                }
            }
        }

        if (!item.bbsUrl.isNullOrBlank()) {
            IconButton(Icon.Search, theme, tooltip = I18n.resourcebrowser.open()) {
                onOpen()
            }
        }
        if (!item.downloadUrl.isNullOrBlank()) {
            IconButton(Icon.Import, theme, tooltip = I18n.resourcebrowser.download()) {
                onDownload()
            }
        }
    }
}

private fun UiScope.ResourceBrowserActionBar(
    loading: Boolean,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    onLoadMore: () -> Unit,
) {
    Row(width = contentWidth) {
        modifier.margin(top = UiTheme.Spacing.md)
        TextIconButton(
            label = I18n.resourcebrowser.loadMore(),
            icon = Icon.Refresh,
            width = UiTheme.Layout.modsActionButtonWidth,
            theme = theme,
            emphasized = !loading,
            onPressed = onLoadMore,
        )
        Box(width = Grow.Std, height = UiTheme.Layout.menuButtonHeight) {
            if (loading) {
                CircularLoadingIndicator(Dp(30f), Dp(3f), theme).modifier.align(AlignmentX.Center, AlignmentY.Center)
            }
        }
    }
}

private fun ResourceBrowserItem.metadataText(): String = buildList {
    add(type.label)
    author?.takeIf { it.isNotBlank() }?.let { add(I18n.resourcebrowser.author(it)) }
    version?.takeIf { it.isNotBlank() }?.let { add(I18n.resourcebrowser.version(it)) }
    downloadCount?.let { add(I18n.resourcebrowser.downloads(it)) }
}.joinToString("  |  ")

private object ResourceBrowserPreviewTextureCache {
    private val textures = mutableMapOf<String, Texture2d>()

    fun textureFor(imageUrl: String): Texture2d = textures.getOrPut(imageUrl) {
        Texture2d(name = "rwx-resource-preview:$imageUrl") {
            Assets.defaultLoader.loadImage2d(imageUrl).getOrNull() ?: AssetLoader.textureDataLoadFailed
        }
    }

    fun invalidate() {
        textures.clear()
    }
}

fun invalidateResourceBrowserPreviewTextureCache() {
    ResourceBrowserPreviewTextureCache.invalidate()
}

private val RESOURCE_BROWSER_CARD_HEIGHT: Dp = Dp(112f)
private val RESOURCE_BROWSER_ANDROID_MIN_VIEWPORT_HEIGHT: Dp = Dp(120f)
private val RESOURCE_BROWSER_VERTICAL_CHROME: Dp = Dp(216f)
