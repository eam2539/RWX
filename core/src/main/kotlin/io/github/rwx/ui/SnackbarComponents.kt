package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color

class SnackbarSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onVisibilityChanged: (Boolean) -> Unit = {},
) {
    val hostState: SnackbarHostState = SnackbarHostState()

    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = if (actionLabel == null) {
            SnackbarDuration.Short
        } else {
            SnackbarDuration.Indefinite
        },
        onResult: ((SnackbarResult) -> Unit)? = null,
    ): SnackbarHandle {
        val handle = hostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration,
            onResult = onResult,
        )
        syncVisibility()
        return handle
    }

    fun showSnackbar(
        visuals: SnackbarVisuals,
        onResult: ((SnackbarResult) -> Unit)? = null,
    ): SnackbarHandle {
        val handle = hostState.showSnackbar(visuals, onResult)
        syncVisibility()
        return handle
    }

    fun dismissCurrent(): Boolean {
        val dismissed = hostState.dismissCurrent()
        syncVisibility()
        return dismissed
    }

    fun clear() {
        hostState.clear()
        syncVisibility()
    }

    fun createScene(): Scene = UiScene(SNACKBAR_SCENE_NAME) {
        onUpdate {
            if (hostState.tick()) {
                syncVisibility()
            }
        }

        addPanelSurface(
            PanelStyle.Snackbar,
            "rwx-snackbar-panel",
            model,
            backgroundColor = { TRANSPARENT_BACKGROUND },
        ) { theme ->
            val data = hostState.currentSnackbarData.use() ?: return@addPanelSurface
            Snackbar(
                data = data,
                theme = theme,
                onAction = {
                    hostState.performAction(data)
                    syncVisibility()
                },
                onDismiss = {
                    hostState.dismiss(data)
                    syncVisibility()
                },
            )
        }
    }

    private fun syncVisibility() {
        onVisibilityChanged(hostState.isVisible)
    }

    companion object {
        const val SNACKBAR_SCENE_NAME: String = "rwx-snackbar"
    }
}

fun UiScope.Snackbar(
    data: SnackbarData,
    theme: ColorSchemeDefinition,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
) {
    Snackbar(data.visuals, theme, onAction, onDismiss)
}

fun UiScope.Snackbar(
    visuals: SnackbarVisuals,
    theme: ColorSchemeDefinition,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
) {
    val contentWidth = ResponsiveContentWidth(
        defaultWidth = UiTheme.Layout.snackbarWidth,
        minWidth = UiTheme.Layout.snackbarMinWidth,
        maxWidth = UiTheme.Layout.snackbarMaxWidth,
        horizontalMargin = UiTheme.Spacing.xl,
    )
    val actionWidth = if (visuals.actionLabel != null) {
        UiTheme.Layout.snackbarActionButtonWidth
    } else {
        Dp.ZERO
    }
    val dismissWidth = if (visuals.withDismissAction) {
        UiTheme.Layout.snackbarDismissButtonSize + UiTheme.Spacing.sm
    } else {
        Dp.ZERO
    }
    val messageWidth = Dp(
        (
                contentWidth.value -
                        SnackbarAccentWidth.value -
                        actionWidth.value -
                        dismissWidth.value -
                        UiTheme.Spacing.lg.value
                ).coerceAtLeast(SnackbarMinMessageWidth.value)
    )

    Box(width = contentWidth, height = FitContent) {
        modifier
            .align(AlignmentX.Center, AlignmentY.Bottom)
            .margin(bottom = UiTheme.Layout.snackbarBottomMargin)
            .padding(horizontal = UiTheme.Spacing.md, vertical = UiTheme.Spacing.sm)
            .background(
                LinearGradientBackground(
                    startColor = theme.palette.surfaceBase.withAlpha(SnackbarSurfaceAlpha),
                    endColor = theme.palette.surfaceRaised.withAlpha(SnackbarSurfaceAlpha),
                    cornerRadius = UiTheme.Spacing.sm,
                )
            )
            .border(RoundRectBorder(theme.palette.primary, UiTheme.Spacing.sm, Dp(1f)))

        Row(width = Grow.Std, height = FitContent) {
            modifier.align(AlignmentX.Center, AlignmentY.Center)

            SnackbarAccent(theme)
            SnackbarMessage(visuals.message, theme, messageWidth)
            visuals.actionLabel?.let { label ->
                SnackbarAction(label, theme, onAction)
            }
            if (visuals.withDismissAction) {
                SnackbarDismiss(theme, onDismiss)
            }
        }
    }
}

private fun UiScope.SnackbarAccent(theme: ColorSchemeDefinition) {
    Box(width = SnackbarAccentWidth, height = UiTheme.Layout.snackbarMinHeight) {
        modifier
            .margin(end = UiTheme.Spacing.md)
            .background(
                LinearGradientBackground(
                    startColor = theme.palette.primary,
                    endColor = theme.palette.secondary,
                    cornerRadius = Dp(3f),
                )
            )
    }
}

private fun UiScope.SnackbarMessage(
    message: String,
    theme: ColorSchemeDefinition,
    width: Dp,
) {
    Text(message) {
        modifier
            .width(width)
            .height(FitContent)
            .alignY(AlignmentY.Center)
            .font(UiTheme.Fonts.bodySmall)
            .textAlign(AlignmentX.Start, AlignmentY.Center)
            .isWrapText(true)
            .textColor(theme.palette.textPrimary)
    }
}

private fun UiScope.SnackbarAction(
    label: String,
    theme: ColorSchemeDefinition,
    onAction: () -> Unit,
) {
    TextIconButton(
        label = label,
        icon = Icon.Apply,
        width = UiTheme.Layout.snackbarActionButtonWidth,
        theme = theme,
        emphasized = true,
        font = UiTheme.Fonts.bodySmall,
        onPressed = onAction,
    )
}

private fun UiScope.SnackbarDismiss(
    theme: ColorSchemeDefinition,
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

private val TRANSPARENT_BACKGROUND = Color("00000000")
private val SnackbarAccentWidth: Dp = Dp(6f)
private val SnackbarMinMessageWidth: Dp = Dp(148f)
private const val SnackbarSurfaceAlpha: Float = 0.96f
