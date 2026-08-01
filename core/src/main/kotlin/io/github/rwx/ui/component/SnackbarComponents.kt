package io.github.rwx.ui.component

import de.fabmax.kool.modules.ui2.*
import io.github.rwx.ui.UiTheme
import io.github.rwx.ui.model.SnackbarData
import io.github.rwx.ui.model.SnackbarVisuals

fun UiScope.Snackbar(
    data: SnackbarData,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
) {
    Snackbar(data.visuals, theme, onAction, onDismiss)
}

fun UiScope.Snackbar(
    visuals: SnackbarVisuals,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
) {

    Box(width = Grow.Std, height = FitContent) {
        modifier
            .align(AlignmentX.Center, AlignmentY.Bottom)
            .margin(bottom = io.github.rwx.ui.UiTheme.Layout.snackbarBottomMargin)
            .padding(horizontal = io.github.rwx.ui.UiTheme.Spacing.md, vertical = io.github.rwx.ui.UiTheme.Spacing.sm)
            .background(
                LinearGradientBackground(
                    startColor = theme.palette.surfaceBase.withAlpha(SnackbarSurfaceAlpha),
                    endColor = theme.palette.surfaceRaised.withAlpha(SnackbarSurfaceAlpha),
                    cornerRadius = io.github.rwx.ui.UiTheme.Spacing.sm,
                )
            )
            .border(RoundRectBorder(theme.palette.primary, io.github.rwx.ui.UiTheme.Spacing.sm, Dp(1f)))

        Row(width = Grow.Std, height = FitContent) {
            modifier.align(AlignmentX.Center, AlignmentY.Center)

            Text(visuals.message) {
                modifier
                    .height(FitContent)
                    .margin(end = UiTheme.Spacing.xl)
                    .alignY(AlignmentY.Center)
                    .font(UiTheme.Fonts.bodySmall)
                    .textAlign(AlignmentX.Start, AlignmentY.Center)
                    .isWrapText(true)
                    .textColor(theme.palette.textPrimary)
            }

            visuals.actionLabel?.let { label ->
                SnackbarAction(label, theme, onAction)
            }
            if (visuals.withDismissAction) {
                SnackbarDismiss(theme, onDismiss)
            }
        }
    }
}

/*private fun UiScope.SnackbarAccent(theme: io.github.rwx.ui.ColorSchemeDefinition) {
    Box(width = SnackbarAccentWidth, height = io.github.rwx.ui.UiTheme.Layout.snackbarMinHeight) {
        modifier
            .margin(end = io.github.rwx.ui.UiTheme.Spacing.md)
            .background(
                LinearGradientBackground(
                    startColor = theme.palette.primary,
                    endColor = theme.palette.secondary,
                    cornerRadius = Dp(3f),
                )
            )
    }
}*/

private fun UiScope.SnackbarAction(
    label: String,
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    onAction: () -> Unit,
) {
    TextIconButton(
        label = label,
        icon = Icon.Apply,
        // width = UiTheme.Layout.snackbarActionButtonWidth,
        theme = theme,
        emphasized = true,
        font = UiTheme.Fonts.bodySmall,
        height = UiTheme.Layout.snackbarDismissButtonSize,
        onPressed = onAction,
    )
}

private fun UiScope.SnackbarDismiss(
    theme: io.github.rwx.ui.ColorSchemeDefinition,
    onDismiss: () -> Unit,
) {
    IconButton(
        icon = Icon.Close,
        theme = theme,
        size = UiTheme.Layout.snackbarDismissButtonSize,
        iconSize = UiTheme.Layout.snackbarDismissIconSize,
        tooltip = "Dismiss",
        onPressed = onDismiss,
    )
}


private const val SnackbarSurfaceAlpha: Float = 0.96f
