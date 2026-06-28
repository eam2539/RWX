package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.*

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

/**
 * Search field that filters the mod list by name. The MSDF atlas
 * covers Latin + CJK, so CJK input and a translated hint render through the same field font.
 */
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

/**
 * Centered section header for the Enabled / Disabled groups. Centering
 * (rather than the left-aligned [SectionTitle]) keeps the wide CJK heading glyphs clear of the
 * panel's left edge.
 */
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

/**
 * A single mod row: name with an optional RAM / error sub-line on the left, and Enable/Disable plus
 * Delete buttons on the right. Visual structure is expressed with RWX theme
 * tokens. [toggleLabel] is "Enable" or "Disable" depending on the mod's current state (chosen by the
 * caller); [ramText] is pre-formatted and blank when unknown.
 */
fun UiScope.ModCard(
    mod: ModEntry,
    theme: ColorSchemeDefinition,
    toggleLabel: String,
    deleteLabel: String,
    ramText: String,
    contentWidth: Dp = UiTheme.Layout.modsContentWidth,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    val errorText = mod.errorMessage?.takeIf { it.isNotBlank() }
    val hasError = errorText != null
    val borderColor = if (hasError) theme.palette.danger else theme.palette.borderSubtle

    Row(width = contentWidth, height = UiTheme.Layout.modsCardHeight) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .padding(start = UiTheme.Spacing.md, end = UiTheme.Spacing.sm)
            .background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.xs))
            .border(RoundRectBorder(borderColor, UiTheme.Spacing.xs, Dp(if (hasError) 2f else 1f)))

        Column(width = Grow.Std, height = UiTheme.Layout.modsCardHeight) {
            modifier.alignY(AlignmentY.Center).padding(vertical = UiTheme.Spacing.xs)
            Text(mod.name) {
                modifier
                    .width(Grow.Std)
                    .clipToBounds(true)
                    .font(UiTheme.Fonts.bodyMedium)
                    .textColor(theme.palette.textPrimary)
            }
            if (ramText.isNotBlank()) {
                Text(ramText) {
                    modifier
                        .width(Grow.Std)
                        .margin(top = UiTheme.Spacing.xs)
                        .clipToBounds(true)
                        .font(UiTheme.Fonts.caption)
                        .textColor(theme.palette.textSecondary)
                }
            }
            if (errorText != null) {
                Text(errorText) {
                    modifier
                        .width(Grow.Std)
                        .margin(top = UiTheme.Spacing.xs)
                        .clipToBounds(true)
                        .font(UiTheme.Fonts.caption)
                        .textColor(theme.palette.danger)
                }
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
