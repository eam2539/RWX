package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.*


fun <T> UiScope.ScrollableVerticalList(
    items: List<T>,
    theme: ColorSchemeDefinition,
    width: Dp = UiTheme.Layout.levelSelectButtonWidth,
    height: Dp = UiTheme.Layout.scrollViewportHeight,
    isScrollByDrag: Boolean = true,
    framed: Boolean = false,
    contentPadding: Dp = Dp.ZERO,
    bottomContentPadding: Dp = Dp.ZERO,
    itemContent: UiScope.(T) -> Unit,
) {
    if (framed) {
        Box(width = width, height = height) {
            modifier
                .background(RoundRectBackground(theme.palette.surfaceSunken, UiTheme.Spacing.sm))
                .border(RoundRectBorder(theme.palette.borderSubtle, UiTheme.Spacing.sm, Dp(1f)))
                .padding(contentPadding)

            LazyColumn(
                items = items,
                theme = theme,
                width = Grow.Std,
                height = Grow.Std,
                isScrollByDrag = isScrollByDrag,
                bottomContentPadding = bottomContentPadding,
                itemContent = itemContent,
            )
        }
    } else {
        LazyColumn(
            items = items,
            theme = theme,
            width = width,
            height = height,
            isScrollByDrag = isScrollByDrag,
            bottomContentPadding = bottomContentPadding,
            itemContent = itemContent,
        )
    }
}

private fun <T> UiScope.LazyColumn(
    items: List<T>,
    theme: ColorSchemeDefinition,
    width: Dimension,
    height: Dimension,
    isScrollByDrag: Boolean,
    bottomContentPadding: Dp,
    itemContent: UiScope.(T) -> Unit,
) {
    LazyColumn(
        width = width,
        height = height,
        withVerticalScrollbar = true,
        withHorizontalScrollbar = false,
        isScrollableHorizontal = false,
        isScrollByDrag = isScrollByDrag,
        scrollbarColor = theme.palette.primary,
        containerModifier = { it.backgroundColor(null) },
        scrollPaneModifier = { it.allowOverScroll(x = false, y = false) },
    ) {
        val hasBottomPadding = bottomContentPadding.value > 0f
        indices(items.size + if (hasBottomPadding) 1 else 0) { index ->
            if (index < items.size) {
                itemContent(items[index])
            } else {
                Box(width = Grow.Std, height = bottomContentPadding) { }
            }
        }
    }
}
