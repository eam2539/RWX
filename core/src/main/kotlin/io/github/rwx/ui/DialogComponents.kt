package io.github.rwx.ui

import com.corrodinggames.rts.gameFramework.GameEngine
import de.fabmax.kool.modules.ui2.*


fun UiScope.MessageDialog(
    dialog: Dialog,
    theme: ColorSchemeDefinition,
    onDismiss: () -> Unit,
) {
    val compact = dialog.compactOnAndroid && GameEngine.isAndroidPlatform()
    val inputValue = remember(dialog.textInput?.initialText.orEmpty())
    val formValues = remember(dialog.form?.fields?.associate { field ->
        field.id to field.initialFormValue()
    }.orEmpty())
    val contentWidth = ResponsiveContentWidth(
        defaultWidth = if (compact) CompactDialogMessageWidth else UiTheme.Layout.dialogMessageWidth,
        minWidth = if (compact) CompactDialogMinMessageWidth else UiTheme.Layout.dialogMinMessageWidth,
        maxWidth = if (compact) CompactDialogMaxMessageWidth else UiTheme.Layout.dialogMaxMessageWidth,
    )
    DialogHeader(dialog.title, theme, contentWidth, compact)
    if (dialog.message.isNotBlank()) {
        DialogMessage(dialog.message, theme, contentWidth, dialog.scrollableMessage, compact)
    }
    if (dialog.infoRows.isNotEmpty()) {
        DialogInfoRows(dialog.infoRows, theme, contentWidth, compact)
    }
    if (dialog.listItems.isNotEmpty()) {
        DialogScrollableList(dialog.listItems, theme, contentWidth, compact)
    }
    dialog.textInput?.let { input ->
        DialogTextInput(inputValue.use(), input.hint, theme, contentWidth, compact) {
            inputValue.value = it
        }
    }
    dialog.form?.let { form ->
        DialogForm(form, formValues.use(), theme, contentWidth, compact, dialog.usesScrollableForm()) { id, value ->
            formValues.value += (id to value)
        }
    }
    DialogButtonRow(
        buttons = dialog.buttons,
        theme = theme,
        contentWidth = contentWidth,
        compact = compact,
        onDismiss = onDismiss,
        inputText = { inputValue.value },
        formValues = { formValues.value },
    )
}

private fun UiScope.DialogScrollableList(
    items: List<DialogListItem>,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
) {
    val listHeight = if (compact) CompactDialogListHeight else DialogListHeight
    val innerWidth = Dp((contentWidth.value - UiTheme.Spacing.sm.value * 2f).coerceAtLeast(1f))
    val innerHeight = Dp((listHeight.value - UiTheme.Spacing.sm.value * 2f).coerceAtLeast(1f))
    Box(
        width = contentWidth,
        height = listHeight,
    ) {
        modifier
            .margin(bottom = if (compact) UiTheme.Spacing.sm else UiTheme.Spacing.lg)
            .background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.xs))
            .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.xs, Dp(1f)))
            .padding(UiTheme.Spacing.sm)

        ScrollableVerticalList(
            items = items,
            theme = theme,
            width = innerWidth,
            height = innerHeight,
        ) { item ->
            Text(item.text) {
                modifier
                    .width(Grow.Std)
                    .margin(bottom = UiTheme.Spacing.xs)
                    .padding(horizontal = UiTheme.Spacing.xs, vertical = Dp(2f))
                    .font(UiTheme.Fonts.bodySmall)
                    .isWrapText(true)
                    .textColor(BattleRoomTeamColors.colorFor(item.colorIndex, theme.palette.textSecondary))
            }
        }
    }
}

private fun UiScope.DialogHeader(
    title: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
) {
    val cornerRadius = UiTheme.Spacing.sm
    val headerHeight = if (compact) CompactDialogHeaderHeight else UiTheme.Layout.dialogHeaderHeight
    Box(width = contentWidth, height = headerHeight) {
        modifier
            .margin(bottom = if (compact) UiTheme.Spacing.sm else UiTheme.Spacing.md)
            .background(RoundRectBackground(theme.palette.primary, cornerRadius))

        Box(width = Grow.Std, height = Grow.Std) {
            modifier.background(
                RoundRectGradientBackground(
                    cornerRadius = cornerRadius,
                    colorA = theme.palette.primary,
                    colorB = theme.palette.secondary,
                    gradientCx = Dp.ZERO,
                    gradientCy = Dp(headerHeight.value * 0.5f),
                    gradientRx = contentWidth,
                    gradientRy = Dp(contentWidth.value * 4f),
                )
            )
        }
        Box(width = Grow.Std, height = Grow.Std) {
            modifier.border(RoundRectBorder(theme.palette.borderSubtle, cornerRadius, Dp(1f)))
        }

        Text(title) {
            modifier
                .width(Grow.Std)
                .height(Grow.Std)
                .padding(horizontal = UiTheme.Spacing.lg)
                .font(UiTheme.Fonts.headingMedium)
                .textAlign(AlignmentX.Center, AlignmentY.Center)
                .textColor(theme.palette.onPrimary)
        }
    }
}

/**
 * Renders the dialog message body text.
 */
fun UiScope.DialogMessage(
    message: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.dialogMessageWidth,
    scrollable: Boolean = false,
    compact: Boolean = false,
) {
    if (scrollable) {
        ScrollableDialogMessage(message, theme, contentWidth, compact)
        return
    }

    Text(message) {
        modifier
            .width(contentWidth)
            .margin(bottom = if (compact) UiTheme.Spacing.lg else UiTheme.Spacing.xl)
            .font(UiTheme.Fonts.bodySmall)
            .isWrapText(true)
            .textColor(theme.palette.textSecondary)
    }
}

private fun UiScope.ScrollableDialogMessage(
    message: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
) {
    val scrollState = rememberScrollState()
    Box(
        width = contentWidth,
        height = if (compact) CompactDialogScrollableMessageHeight else UiTheme.Layout.dialogScrollableMessageHeight,
    ) {
        modifier
            .margin(bottom = UiTheme.Spacing.xl)
            .background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.xs))
            .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.xs, Dp(1f)))
            .padding(UiTheme.Spacing.sm)

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
            Text(message) {
                modifier
                    .width(Grow.Std)
                    .height(FitContent)
                    .font(UiTheme.Fonts.bodySmall)
                    .isWrapText(true)
                    .textColor(theme.palette.textSecondary)
            }
        }
    }
}

fun UiScope.DialogInfoRows(
    rows: List<DialogInfoRow>,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.dialogMessageWidth,
    compact: Boolean = false,
) {
    Column(width = contentWidth, height = FitContent) {
        modifier.margin(bottom = if (compact) UiTheme.Spacing.sm else UiTheme.Spacing.lg)
        rows.forEach { row ->
            DialogInfoRow(row, theme, contentWidth, compact)
        }
    }
}

private fun UiScope.DialogInfoRow(
    row: DialogInfoRow,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
) {
    val clickable = row.onPress != null
    val hovered = remember(false)
    val isHovered = hovered.use() && clickable
    val iconColor = when {
        isHovered -> theme.palette.secondary
        row.emphasis -> theme.palette.secondary
        else -> theme.palette.primary
    }
    val valueColor = when {
        isHovered -> theme.palette.secondary
        row.emphasis -> theme.palette.primary
        else -> theme.palette.textSecondary
    }
    val background = if (isHovered) theme.palette.surfaceRaised else theme.palette.surfaceSunken
    val border = if (isHovered) theme.palette.primary else theme.palette.borderSubtle

    val rowHeight = if (compact) CompactDialogInfoRowHeight else DialogInfoRowHeight
    val iconSlotWidth = if (compact) CompactDialogInfoIconSlotWidth else DialogInfoIconSlotWidth
    val iconSize = if (compact) CompactDialogInfoIconSize else DialogInfoIconSize
    val labelWidth = if (compact) CompactDialogInfoLabelWidth else DialogInfoLabelWidth
    Row(width = contentWidth, height = rowHeight) {
        modifier
            .margin(bottom = UiTheme.Spacing.xs)
            .padding(horizontal = UiTheme.Spacing.sm)
            .background(RoundRectBackground(background, UiTheme.Spacing.xs))
            .border(RoundRectBorder(border, UiTheme.Spacing.xs, Dp(1f)))
        if (clickable) {
            modifier
                .onEnter { hovered.value = true }
                .onExit { hovered.value = false }
                .onClick { row.onPress.invoke() }
        }

        Box(width = iconSlotWidth, height = Grow.Std) {
            Icon(row.icon, iconSize, iconColor).modifier.align(AlignmentX.Center, AlignmentY.Center)
        }
        Text(row.label) {
            modifier
                .width(labelWidth)
                .height(Grow.Std)
                .font(UiTheme.Fonts.bodySmall)
                .textAlign(AlignmentX.Start, AlignmentY.Center)
                .clipToBounds(true)
                .textColor(theme.palette.textPrimary)
        }
        Text(row.value) {
            modifier
                .width(Grow.Std)
                .height(Grow.Std)
                .font(UiTheme.Fonts.caption)
                .textAlign(AlignmentX.Start, AlignmentY.Center)
                .isWrapText(true)
                .clipToBounds(true)
                .textColor(valueColor)
        }
    }
}

/**
 * Renders the button row for a dialog. Each button fires its [DialogButton.onPress] callback
 * (if non-null) then calls [onDismiss] to close the dialog.
 */
fun UiScope.DialogButtonRow(
    buttons: List<DialogButton>,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.dialogMessageWidth,
    compact: Boolean = false,
    onDismiss: () -> Unit,
    inputText: () -> String = { "" },
    formValues: () -> Map<String, String> = { emptyMap() },
) {
    val buttonsPerRow = dialogButtonsPerRow(buttons.size, compact)
    Column(width = contentWidth, height = FitContent) {
        buttons.withIndex().chunked(buttonsPerRow).forEachIndexed { rowIndex, rowButtons ->
            val buttonWidth = contentWidth.splitEvenly(
                count = rowButtons.size.coerceAtLeast(1),
                totalGap = Dp(rowButtons.size.coerceAtLeast(1) * UiTheme.Spacing.sm.value),
                minWidth = if (compact) Dp(88f) else Dp(120f),
                maxWidth = UiTheme.Layout.dialogButtonWidth,
            )
            Row {
                if (rowIndex > 0) {
                    modifier.margin(top = UiTheme.Spacing.xs)
                }
                modifier.alignX(AlignmentX.Center)
                rowButtons.forEach { indexedButton ->
                    val index = indexedButton.index
                    val btn = indexedButton.value
                    TextIconButton(
                        label = btn.label,
                        icon = btn.dialogIcon(index, buttons.lastIndex),
                        width = buttonWidth,
                        theme = theme,
                        emphasized = btn.isPrimaryDialogButton(index, buttons.lastIndex),
                        font = if (compact) UiTheme.Fonts.caption else UiTheme.Fonts.bodySmall,
                        height = if (compact) CompactDialogButtonHeight else UiTheme.Layout.menuButtonHeight,
                    ) {
                        when {
                            btn.onFormPress != null -> btn.onFormPress.invoke(formValues())
                            btn.onInputPress != null -> btn.onInputPress.invoke(inputText())
                            else -> btn.onPress?.invoke()
                        }
                        onDismiss()
                    }
                }
            }
        }
    }
}

internal fun Dialog.usesScrollableForm(): Boolean =
    scrollableForm || (form?.fields?.size ?: 0) > DialogAutomaticScrollFieldThreshold

internal fun dialogButtonsPerRow(buttonCount: Int, compact: Boolean): Int = when {
    buttonCount <= 0 -> 1
    compact -> buttonCount.coerceAtMost(CompactDialogMaxButtonsPerRow)
    else -> buttonCount.coerceAtMost(DialogMaxButtonsPerRow)
}

private fun DialogButton.dialogIcon(index: Int, lastIndex: Int): Icon {
    return when {
        isDismissiveDialogButton() -> Icon.Close
        index == lastIndex -> Icon.Apply
        else -> Icon.Options
    }
}

private fun DialogButton.isPrimaryDialogButton(index: Int, lastIndex: Int): Boolean =
    index == lastIndex && !isDismissiveDialogButton()

private fun DialogButton.isDismissiveDialogButton(): Boolean {
    val normalized = label.lowercase()
    return normalized in setOf("cancel", "close", "back", "no") ||
            label in setOf("取消", "关闭", "返回", "否")
}

fun UiScope.DialogForm(
    form: DialogForm,
    values: Map<String, String>,
    theme: ColorSchemeDefinition,
    contentWidth: Dp = UiTheme.Layout.dialogMessageWidth,
    compact: Boolean = false,
    scrollable: Boolean = false,
    onChange: (String, String) -> Unit,
) {
    if (scrollable) {
        val scrollState = rememberScrollState()
        val innerWidth = Dp((contentWidth.value - UiTheme.Spacing.sm.value * 2f).coerceAtLeast(1f))
        Box(
            width = contentWidth,
            height = if (compact) CompactDialogScrollableFormHeight else UiTheme.Layout.dialogScrollableMessageHeight,
        ) {
            modifier
                .margin(bottom = if (compact) UiTheme.Spacing.sm else UiTheme.Spacing.lg)
                .background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.xs))
                .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.xs, Dp(1f)))
                .padding(UiTheme.Spacing.sm)
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
                DialogFormContent(form, values, theme, innerWidth, compact, onChange)
            }
        }
        return
    }
    DialogFormContent(form, values, theme, contentWidth, compact, onChange)
}

private fun UiScope.DialogFormContent(
    form: DialogForm,
    values: Map<String, String>,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
    onChange: (String, String) -> Unit,
) {
    Column(width = contentWidth) {
        modifier.margin(bottom = if (compact) UiTheme.Spacing.sm else UiTheme.Spacing.lg)
        if (compact) {
            val columnCount = if (form.fields.size >= CompactDialogWideFormFieldThreshold) 3 else 2
            val fieldWidth = contentWidth.splitEvenly(
                count = columnCount,
                totalGap = Dp((columnCount - 1) * UiTheme.Spacing.sm.value),
                minWidth = Dp(96f),
                maxWidth = contentWidth,
            )
            form.fields.chunked(columnCount).forEach { fields ->
                Row(width = contentWidth, height = FitContent) {
                    fields.forEachIndexed { index, field ->
                        if (index > 0) {
                            Box(width = UiTheme.Spacing.sm, height = CompactDialogFormFieldHeight) {}
                        }
                        DialogFormField(
                            field,
                            values[field.id],
                            theme,
                            fieldWidth,
                            compact = true,
                            onChange = onChange,
                        )
                    }
                }
            }
        } else {
            form.fields.forEach { field ->
                DialogFormField(
                    field,
                    values[field.id],
                    theme,
                    contentWidth,
                    compact = false,
                    onChange = onChange,
                )
            }
        }
    }
}

private fun UiScope.DialogFormField(
    field: DialogFormField,
    value: String?,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
    onChange: (String, String) -> Unit,
) {
    when (field) {
        is DialogFormField.Choice -> DialogChoiceField(field, value, theme, contentWidth, compact, onChange)
        is DialogFormField.Toggle -> DialogToggleField(field, value, theme, contentWidth, compact, onChange)
        is DialogFormField.Text -> DialogTextField(field, value, theme, contentWidth, compact, onChange)
    }
}

private fun UiScope.DialogChoiceField(
    field: DialogFormField.Choice,
    value: String?,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
    onChange: (String, String) -> Unit,
) {
    val selectedIndex = field.options.indexOfFirst { it.value == value }
        .takeIf { it >= 0 }
        ?: field.selectedIndex.coerceIn(0, field.options.lastIndex.coerceAtLeast(0))
    DialogFieldRow(field.label, theme, contentWidth, compact) {
        RwxComboBox {
            modifier
                .width(Grow.Std)
                .height(if (compact) CompactDialogFormControlHeight else UiTheme.Layout.menuButtonHeight)
                .font(UiTheme.Fonts.bodySmall)
                .items(field.options)
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
                .onItemSelected { index ->
                    field.options.getOrNull(index)?.let { onChange(field.id, it.value) }
                }
        }
    }
}

private fun UiScope.DialogToggleField(
    field: DialogFormField.Toggle,
    value: String?,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
    onChange: (String, String) -> Unit,
) {
    val checked = value?.toBooleanStrictOrNull() ?: field.checked
    DialogFieldRow(field.label, theme, contentWidth, compact) {
        Button(if (checked) "On" else "Off") {
            modifier
                .width(Grow.Std)
                .height(if (compact) CompactDialogFormControlHeight else UiTheme.Layout.menuButtonHeight)
                .font(UiTheme.Fonts.bodySmall)
                .colors(
                    buttonColor = if (checked) theme.palette.primaryContainer else theme.palette.surfaceSunken,
                    textColor = theme.palette.textPrimary,
                    buttonHoverColor = theme.palette.surfaceRaised,
                    textHoverColor = theme.palette.primary,
                )
                .onClick { onChange(field.id, (!checked).toString()) }
        }
    }
}

private fun UiScope.DialogTextField(
    field: DialogFormField.Text,
    value: String?,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
    onChange: (String, String) -> Unit,
) {
    DialogFieldRow(field.label, theme, contentWidth, compact) {
        DialogTextInput(value ?: field.initialText, field.hint, theme, Grow.Std, compact) {
            onChange(field.id, it)
        }
    }
}

private fun UiScope.DialogFieldRow(
    label: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
    compact: Boolean,
    content: UiScope.() -> Unit,
) {
    if (compact) {
        Column(width = contentWidth, height = CompactDialogFormFieldHeight) {
            modifier.margin(bottom = UiTheme.Spacing.xs)
            Text(label) {
                modifier
                    .width(contentWidth)
                    .height(CompactDialogFormLabelHeight)
                    .font(UiTheme.Fonts.caption)
                    .textAlign(AlignmentX.Start, AlignmentY.Center)
                    .clipToBounds(true)
                    .textColor(theme.palette.textPrimary)
            }
            Box(width = contentWidth, height = CompactDialogFormControlHeight) {
                content()
            }
        }
        return
    }
    val labelWidth = contentWidth.fraction(0.38f, minWidth = Dp(136f), maxWidth = Dp(220f))
    val inputWidth = contentWidth.remainingAfter(labelWidth, Dp(160f))
    Row(width = contentWidth, height = UiTheme.Layout.menuButtonHeight + UiTheme.Spacing.sm) {
        modifier.margin(bottom = UiTheme.Spacing.xs)
        Text(label) {
            modifier
                .width(labelWidth)
                .height(UiTheme.Layout.menuButtonHeight)
                .font(UiTheme.Fonts.bodySmall)
                .textAlign(AlignmentX.Start, AlignmentY.Center)
                .isWrapText(true)
                .textColor(theme.palette.textPrimary)
        }
        Box(width = inputWidth, height = UiTheme.Layout.menuButtonHeight) {
            content()
        }
    }
}

fun UiScope.DialogTextInput(
    value: String,
    hint: String,
    theme: ColorSchemeDefinition,
    contentWidth: Dimension = UiTheme.Layout.dialogMessageWidth,
    compact: Boolean = false,
    onChange: (String) -> Unit,
) {
    RwxTextField(value) {
        modifier
            .width(contentWidth)
            .height(if (compact) CompactDialogFormControlHeight else UiTheme.Layout.menuButtonHeight)
            .margin(bottom = if (compact) Dp.ZERO else UiTheme.Spacing.lg)
            .padding(start = UiTheme.Spacing.sm)
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
            .onChange(onChange)
    }
}

private fun DialogFormField.initialFormValue(): String =
    when (this) {
        is DialogFormField.Choice -> options.getOrNull(selectedIndex)?.value
            ?: options.firstOrNull()?.value
            ?: ""

        is DialogFormField.Toggle -> checked.toString()
        is DialogFormField.Text -> initialText
    }

private val DialogInfoRowHeight: Dp = Dp(52f)
private val DialogInfoIconSlotWidth: Dp = Dp(44f)
private val DialogInfoIconSize: Dp = Dp(26f)
private val DialogInfoLabelWidth: Dp = Dp(116f)
private val DialogListHeight: Dp = Dp(340f)
private val CompactDialogMessageWidth: Dp = Dp(460f)
private val CompactDialogMinMessageWidth: Dp = Dp(280f)
private val CompactDialogMaxMessageWidth: Dp = Dp(560f)
private val CompactDialogHeaderHeight: Dp = Dp(42f)
private val CompactDialogInfoRowHeight: Dp = Dp(38f)
private val CompactDialogInfoIconSlotWidth: Dp = Dp(38f)
private val CompactDialogInfoIconSize: Dp = Dp(22f)
private val CompactDialogInfoLabelWidth: Dp = Dp(96f)
private val CompactDialogButtonHeight: Dp = Dp(42f)
private val CompactDialogScrollableMessageHeight: Dp = Dp(240f)
private val CompactDialogScrollableFormHeight: Dp = Dp(250f)
private val CompactDialogListHeight: Dp = Dp(230f)
private val CompactDialogFormLabelHeight: Dp = Dp(20f)
private val CompactDialogFormControlHeight: Dp = Dp(34f)
private val CompactDialogFormFieldHeight: Dp = Dp(58f)
private const val CompactDialogWideFormFieldThreshold: Int = 15
private const val DialogAutomaticScrollFieldThreshold: Int = 6
private const val DialogMaxButtonsPerRow: Int = 4
private const val CompactDialogMaxButtonsPerRow: Int = 3
