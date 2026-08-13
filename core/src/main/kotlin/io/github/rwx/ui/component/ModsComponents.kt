package io.github.rwx.ui.component

import de.fabmax.kool.Assets
import de.fabmax.kool.MimeType
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.ImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Uint8Buffer
import io.github.rwx.i18n.I18n
import io.github.rwx.ui.ColorSchemeDefinition
import io.github.rwx.ui.UiTheme
import io.github.rwx.ui.model.ModEntry
import io.github.rwx.ui.remainingAfter
import io.github.rwx.ui.splitEvenly
import java.util.zip.ZipFile

/** Labels for the Mods bottom action bar; resolved from [io.github.rwx.i18n.I18n] by the host. */
data class ModsActionLabels(
    val reload: String,
    val importFile: String,
    val disableAll: String,
    val apply: String,
)

/** Callbacks for the Mods bottom action bar. */
data class ModsActionCallbacks(
    val onReload: () -> Unit,
    val onImport: () -> Unit,
    val onDisableAll: () -> Unit,
    val onApply: () -> Unit,
)

fun UiScope.ModsFilterField(
    filter: String,
    hint: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.modsContentWidth,
    onBack: () -> Unit = {},
    onChange: (String) -> Unit,
) {
    Row(width = contentWidth, height = UiTheme.Layout.menuButtonHeight) {
        modifier
            .margin(bottom = UiTheme.Spacing.sm)
        IconButton(Icon.Back, theme, onPressed = onBack)
        Icon(Icon.Search, UiTheme.Layout.textButtonGlyphSize, theme.palette.primary)
            .modifier.alignY(AlignmentY.Center)
        RwxTextField(filter) {
            modifier
                .width(Grow.Std)
                .height(UiTheme.Layout.menuButtonHeight)
                .margin(start = UiTheme.Spacing.sm)
                .hint(hint)
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

fun UiScope.ModsSectionHeader(
    text: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.modsContentWidth,
) {
    Text(text) {
        modifier
            .width(contentWidth)
            .margin(top = UiTheme.Spacing.sm, bottom = UiTheme.Spacing.sm)
            .font(UiTheme.Fonts.headingSmall)
            .textAlign(AlignmentX.Center, AlignmentY.Center)
            .textColor(theme.palette.primary)
    }
}


/** Height of the fade-out mask behind the "More..." overlay in [ClampedText]. */
private val MODS_TEXT_FADE_HEIGHT = Dp(16f)


private fun UiScope.ClampedText(
    text: String,
    maxHeight: Dp,
    theme: ColorSchemeDefinition,
    textColor: Color,
    moreLabel: String,
    onMore: () -> Unit,
) {
    val overflow = remember(false)
    val isOverflow = overflow.use()
    val hovered = remember(false)
    val isHovered = hovered.use()

    Box(width = Grow.Std, height = if (isOverflow) maxHeight else FitContent) {
        modifier.margin(top = UiTheme.Spacing.xs)
        Text(text) {
            modifier
                .width(Grow.Std)
                .height(FitContent)
                .clipToBounds(true)
                .font(UiTheme.Fonts.caption)
                .isWrapText(true)
                .textAlignY(AlignmentY.Top)
                .textColor(textColor)
                .onPositioned { node -> overflow.value = node.contentHeightPx > maxHeight.px }
        }
        if (isOverflow) {
            Box(width = Grow.Std, height = MODS_TEXT_FADE_HEIGHT) {
                modifier
                    .align(AlignmentX.Start, AlignmentY.Bottom)
                    .background(
                        RoundRectGradientBackground(
                            cornerRadius = Dp.ZERO,
                            colorA = theme.palette.surfaceSunken,
                            colorB = Color(0f, 0f, 0f, 0f),
                            gradientCx = Dp.ZERO,
                            gradientCy = MODS_TEXT_FADE_HEIGHT,
                            gradientRx = Dp(1e5f),
                            gradientRy = MODS_TEXT_FADE_HEIGHT,
                        )
                    )
            }
            Column(width = FitContent, height = FitContent) {
                modifier
                    .align(AlignmentX.End, AlignmentY.Bottom)
                    .margin(end = UiTheme.Spacing.sm, bottom = UiTheme.Spacing.xs)
                    .padding(horizontal = UiTheme.Spacing.xs)
                    .onEnter { hovered.value = true }
                    .onExit { hovered.value = false }
                    .onClick { onMore() }
                Text(moreLabel) {
                    modifier
                        .width(FitContent)
                        .font(UiTheme.Fonts.caption)
                        .textAlign(AlignmentX.Center, AlignmentY.Center)
                        .textColor(if (isHovered) theme.palette.secondary else theme.palette.primary)
                }
                Box(width = Grow.Std, height = Dp(1f)) {
                    modifier.background(
                        RoundRectBackground(
                            if (isHovered) theme.palette.secondary else theme.palette.primary,
                            Dp.ZERO
                        )
                    )
                }
            }
        }
    }
}

fun UiScope.ModCard(
    mod: ModEntry,
    theme: ColorSchemeDefinition,
    toggleLabel: String,
    deleteLabel: String,
    contentWidth: Dp = UiTheme.Layout.modsContentWidth,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onShowFullText: (title: String, sectionLabel: String, text: String) -> Unit = { _, _, _ -> },
) {
    val errorText = mod.errorMessage?.takeIf { it.isNotBlank() }
    val hasError = errorText != null
    val borderColor = if (hasError) theme.palette.danger else theme.palette.borderSubtle
    val cardHeight: Dimension = if (hasError) FitContent else UiTheme.Layout.modsCardHeight

    Row(width = contentWidth, height = cardHeight) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .padding(start = UiTheme.Spacing.md, end = UiTheme.Spacing.sm)
            .background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.xs))
            .border(RoundRectBorder(borderColor, UiTheme.Spacing.xs, Dp(if (hasError) 2f else 1f)))

        if (!mod.thumbnail.isNullOrBlank()) {
            ModCardThumbnail(mod, theme)
        }

        Column(width = Grow.Std, height = cardHeight) {
            modifier.alignY(if (hasError) AlignmentY.Top else AlignmentY.Center).padding(vertical = UiTheme.Spacing.xs)
            Text(mod.name) {
                modifier
                    .width(Grow.Std)
                    .clipToBounds(true)
                    .font(UiTheme.Fonts.bodyMedium)
                    .textColor(theme.palette.textPrimary)
            }
            Row {
                if (!mod.author.isNullOrBlank()) {
                    Text("Author:${mod.author}") {
                        modifier
                            .width(Grow.Std)
                            .clipToBounds(true)
                            .font(UiTheme.Fonts.caption)
                            .textColor(theme.palette.textSecondary)
                    }
                }
                if (!mod.version.isNullOrBlank()) {
                    Text("Version:${mod.version}") {
                        modifier
                        .width(Grow.Std)
                            .margin(start = UiTheme.Spacing.md)
                            .padding(end = UiTheme.Spacing.xs)
                        .clipToBounds(true)
                        .font(UiTheme.Fonts.caption)
                            .textColor(theme.palette.textSecondary)
                    }
                }
            }

            if (mod.description.isNotBlank()) {
                ClampedText(
                    text = mod.description,
                    maxHeight = UiTheme.Layout.modsCardDescriptionMaxHeight,
                    theme = theme,
                    textColor = theme.palette.textSecondary,
                    moreLabel = I18n.mods.more(),
                    onMore = { onShowFullText(mod.name, I18n.mods.description(), mod.description) },
                )
            }
            if (errorText != null) {
                ClampedText(
                    text = errorText,
                    maxHeight = UiTheme.Layout.modsCardErrorMaxHeight,
                    theme = theme,
                    textColor = theme.palette.danger,
                    moreLabel = I18n.mods.more(),
                    onMore = { onShowFullText(mod.name, I18n.mods.error(), errorText) },
                )
            }
        }

        ModCardButton(
            label = toggleLabel,
            icon = if (mod.isEnabled) Icon.DisableAll else Icon.Apply,
            theme = theme,
            onPressed = onToggle,
        )
        ModCardButton(deleteLabel, Icon.Delete, theme, onDelete)
    }
}

/** The mod thumbnail shown on the left side of a card; renders nothing when the mod declares none. */
private fun UiScope.ModCardThumbnail(
    mod: ModEntry,
    theme: ColorSchemeDefinition,
) {
    Box(width = UiTheme.Layout.modsThumbnailSize, height = UiTheme.Layout.modsThumbnailSize) {
        modifier
            .margin(end = UiTheme.Spacing.sm)
            .alignY(AlignmentY.Center)
            .padding(UiTheme.Spacing.xs)
            .background(RoundRectBackground(theme.palette.surfaceBase, UiTheme.Spacing.xs))
            .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.xs, Dp(1f)))
        val texture = ModThumbnailTextureCache.textureFor(mod)
        if (texture != null) {
            Image(texture) {
                modifier
                    .width(Grow.Std)
                    .height(Grow.Std)
                    .imageSize(ImageSize.FitContent)
            }
        }
    }
}

private object ModThumbnailTextureCache {
    private val textures = mutableMapOf<String, Texture2d>()
    private val transparentPlaceholder: ImageData2d = BufferedImageData2d.singleColor(Color(0f, 0f, 0f, 0f))

    fun textureFor(mod: ModEntry): Texture2d? {
        if (mod.thumbnail.isNullOrBlank()) return null
        val key = "${mod.path}#${mod.thumbnail}"
        return textures.getOrPut(key) {
            Texture2d(name = "mod-thumb:$key") {
                loadThumbnailImage(mod) ?: transparentPlaceholder
            }
        }
    }

    fun invalidate() {
        textures.clear()
    }

    private suspend fun loadThumbnailImage(mod: ModEntry): ImageData2d? {
        if (mod.thumbnail.isNullOrBlank()) return null
        val entryName = mod.thumbnail.replace('\\', '/').trimStart('/')
        val bytes = runCatching {
            ZipFile(mod.path).use { zip ->
                zip.getEntry(entryName)?.let { entry ->
                    zip.getInputStream(entry).use { it.readBytes() }
                }
            }
        }.getOrNull() ?: return null
        if (bytes.isEmpty()) return null
        val encoded = Uint8Buffer(bytes.size)
        bytes.forEachIndexed { index, value -> encoded[index] = value.toUByte() }
        return runCatching {
            Assets.loadImageFromBuffer(
                texData = encoded,
                mimeType = MimeType.forFileName(entryName),
                format = TexFormat.RGBA,
            )
        }.getOrNull()
    }
}

/** Drops all cached mod thumbnails so they are re-read from their archives on next display. */
fun invalidateModThumbnailTextureCache() {
    ModThumbnailTextureCache.invalidate()
}

/** A compact, vertically-centered card button (Enable/Disable, Delete). */
private fun UiScope.ModCardButton(
    label: String,
    icon: Icon,
    theme: ColorSchemeDefinition,
    onPressed: () -> Unit,
) {
    val hovered = remember(false)
    val isHovered = hovered.use()
    val background = if (isHovered) theme.palette.primaryContainer else theme.palette.surfaceRaised
    val border = if (isHovered) theme.palette.primary else theme.palette.borderSubtle
    val iconColor = if (isHovered) theme.palette.secondary else theme.palette.primary

    Box(width = UiTheme.Layout.modsToggleButtonWidth, height = UiTheme.Layout.menuButtonHeight) {
        modifier
            .margin(start = UiTheme.Spacing.xs)
            .alignY(AlignmentY.Center)
            .padding(horizontal = UiTheme.Spacing.sm, vertical = UiTheme.Spacing.xs)
            .background(RoundRectBackground(background, UiTheme.Spacing.xs))
            .border(RoundRectBorder(border, UiTheme.Spacing.xs, Dp(1f)))
            .onEnter { hovered.value = true }
            .onExit { hovered.value = false }
            .onClick { onPressed() }

        Row(width = Grow.Std, height = Grow.Std) {
            modifier.align(AlignmentX.Center, AlignmentY.Center)
            Icon(icon, UiTheme.Layout.textButtonGlyphSize, iconColor).modifier.alignY(AlignmentY.Center)
            Text(label) {
                modifier
                    .width(Grow.Std)
                    .height(Grow.Std)
                    .margin(start = UiTheme.Spacing.xs)
                    .font(UiTheme.Fonts.caption)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
                    .clipToBounds(true)
                    .textColor(if (isHovered) theme.palette.primary else theme.palette.textPrimary)
            }
        }
    }
}

/** The bottom action bar: Reload, Import, Disable All, Apply. */
fun UiScope.ModsActionBar(
    labels: ModsActionLabels,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.modsContentWidth,
    callbacks: ModsActionCallbacks,
) {
    val buttons = listOf(
        ModsActionButtonSpec(labels.reload, Icon.Refresh, callbacks.onReload),
        ModsActionButtonSpec(labels.importFile, Icon.Import, callbacks.onImport),
        ModsActionButtonSpec(labels.disableAll, Icon.DisableAll, callbacks.onDisableAll),
        ModsActionButtonSpec(labels.apply, Icon.Apply, callbacks.onApply),
    )
    val compact = contentWidth.value < MODS_ACTION_BAR_COMPACT_WIDTH_DP
    if (compact) {
        Column(width = contentWidth) {
            modifier.margin(top = UiTheme.Spacing.sm, bottom = UiTheme.Spacing.xs)
            buttons.forEach { spec ->
                ModsActionButton(
                    spec = spec,
                    width = contentWidth.remainingAfter(UiTheme.Spacing.sm),
                    theme = theme,
                )
            }
        }
    } else {
        val buttonWidth = contentWidth.splitEvenly(
            count = buttons.size,
            totalGap = Dp(buttons.size * UiTheme.Spacing.sm.value),
            minWidth = Dp(140f),
            maxWidth = UiTheme.Layout.modsActionButtonWidth,
        )
        Row {
            modifier
                .alignX(AlignmentX.Center)
                .margin(top = UiTheme.Spacing.sm, bottom = UiTheme.Spacing.xs)
            buttons.forEach { spec ->
                ModsActionButton(spec, buttonWidth, theme)
            }
        }
    }
}

private data class ModsActionButtonSpec(
    val label: String,
    val icon: Icon,
    val onPressed: () -> Unit,
)

private fun UiScope.ModsActionButton(
    spec: ModsActionButtonSpec,
    width: Dp = UiTheme.Layout.modsActionButtonWidth,
    theme: ColorSchemeDefinition,
) {
    TextIconButton(
        label = spec.label,
        icon = spec.icon,
        width = width,
        theme = theme,
        font = UiTheme.Fonts.bodySmall,
    ) {
        spec.onPressed()
    }
}

private const val MODS_ACTION_BAR_COMPACT_WIDTH_DP: Float = 720f
