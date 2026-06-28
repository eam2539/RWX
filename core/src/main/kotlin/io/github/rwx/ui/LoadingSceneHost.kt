package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.scene.Scene
import io.github.rwx.session.GameLoadingStatus

class LoadingSceneHost(
    private val model: SettingsModel = SettingsModel(),
) {
    private val label: MutableStateValue<String> = mutableStateOf(LOADING_LABEL)
    private val progress: MutableStateValue<Float> = mutableStateOf(LOADING_MIN_VISIBLE_PROGRESS)
    private val recentSteps: MutableStateValue<List<String>> = mutableStateOf(emptyList())
    private val warmupUiTextures: MutableStateValue<Boolean> = mutableStateOf(false)

    fun update(status: GameLoadingStatus) {
        label.value = status.text.ifBlank { LOADING_LABEL }
        progress.value = (status.progress ?: LOADING_MIN_VISIBLE_PROGRESS)
            .coerceIn(0.0f, 1.0f)
            .coerceAtLeast(LOADING_MIN_VISIBLE_PROGRESS)
        recentSteps.value = status.recentSteps
            .filter { it.isNotBlank() }
            .takeLast(LOADING_HISTORY_ROW_COUNT + 1)
    }

    fun showUiTextureWarmup() {
        warmupUiTextures.value = true
    }

    fun hideUiTextureWarmup() {
        warmupUiTextures.value = false
    }

    fun createScene(sceneName: String = LOADING_SCENE_NAME): Scene =
        createLoadingScene(model, sceneName, label, progress, recentSteps, warmupUiTextures)

    companion object {
        const val LOADING_SCENE_NAME: String = "rwx-loading-scene"
        const val GAME_LOADING_SCENE_NAME: String = "rwx-game-loading-scene"
        const val LOADING_LABEL: String = "Loading..."

        fun createScene(
            model: SettingsModel = SettingsModel(),
            sceneName: String = LOADING_SCENE_NAME,
            label: String = LOADING_LABEL,
        ): Scene =
            createLoadingScene(
                model = model,
                sceneName = sceneName,
                label = mutableStateOf(label.ifBlank { LOADING_LABEL }),
                progress = mutableStateOf(LOADING_MIN_VISIBLE_PROGRESS),
                recentSteps = mutableStateOf(emptyList()),
                warmupUiTextures = mutableStateOf(false),
            )
    }
}

private const val LOADING_MIN_VISIBLE_PROGRESS: Float = 0.02f
private val LoadingTextHeight: Dp = Dp(64f)
private val LoadingTextSpinnerSize: Dp = Dp(28f)
private val LoadingTextSpinnerStroke: Dp = Dp(3f)
private val LoadingHistoryHeight: Dp = Dp(92f)
private val LoadingHistoryRowHeight: Dp = Dp(22f)
private const val LOADING_HISTORY_ROW_COUNT: Int = 4

private fun createLoadingScene(
    model: SettingsModel,
    sceneName: String,
    label: MutableStateValue<String>,
    progress: MutableStateValue<Float>,
    recentSteps: MutableStateValue<List<String>>,
    warmupUiTextures: MutableStateValue<Boolean>,
): Scene {
    val theme = ColorSchemeRegistry.schemeFor(model.selectedColorSchemeId.value)
    return UiScene(sceneName, clearColor = ClearColorFill(theme.palette.surfaceBase)) {
        addPanelSurface(PanelStyle.Menu, "rwx-loading-panel", model) { activeTheme ->
            val contentWidth = loadingContentWidth()
            MainMenuHeader(contentWidth, activeTheme)
            LoadingStatus(label.use(), progress.use(), recentSteps.use(), activeTheme, contentWidth)
            if (warmupUiTextures.use()) {
                UiIconWarmup(activeTheme)
            }
        }
    }
}

private fun UiScope.LoadingStatus(
    text: String,
    progress: Float,
    recentSteps: List<String>,
    theme: ColorSchemeDefinition,
    contentWidth: Dp,
) {
    val statusWidth = contentWidth.fraction(0.72f, minWidth = Dp(240f), maxWidth = Dp(560f))
    Box(width = statusWidth, height = LoadingTextHeight) {
        modifier
            .margin(bottom = UiTheme.Spacing.md)
            .alignX(AlignmentX.Center)

        Row(width = FitContent, height = Grow.Std) {
            modifier.align(AlignmentX.Center, AlignmentY.Center)

            CircularLoadingIndicator(
                size = LoadingTextSpinnerSize,
                strokeWidth = LoadingTextSpinnerStroke,
                theme = theme,
            ).modifier
                .alignY(AlignmentY.Center)
                .margin(end = UiTheme.Spacing.sm)

            Text(text) {
                modifier
                    .height(Grow.Std)
                    .font(UiTheme.Fonts.bodySmall)
                    .textAlign(AlignmentX.Start, AlignmentY.Center)
                    .isWrapText(false)
                    .clipToBounds(true)
                    .textColor(theme.palette.textPrimary)
            }
        }
    }
    LoadingProgressBar(
        progress = progress,
        width = statusWidth,
        theme = theme,
    )
    LoadingStepHistory(text, recentSteps, statusWidth, theme)
}

private fun UiScope.LoadingStepHistory(
    currentText: String,
    recentSteps: List<String>,
    width: Dp,
    theme: ColorSchemeDefinition,
) {
    val completedSteps = recentSteps
        .dropLastWhile { it == currentText }
        .takeLast(LOADING_HISTORY_ROW_COUNT)
    Box(width = width, height = LoadingHistoryHeight) {
        modifier
            .alignX(AlignmentX.Center)
            .margin(top = UiTheme.Spacing.md)

        Column(width = Grow.Std, height = FitContent) {
            completedSteps.forEach { step ->
                Text(step) {
                    modifier
                        .width(Grow.Std)
                        .height(LoadingHistoryRowHeight)
                        .font(UiTheme.Fonts.bodySmall)
                        .textAlign(AlignmentX.Start, AlignmentY.Center)
                        .isWrapText(false)
                        .clipToBounds(true)
                        .textColor(theme.palette.textSecondary)
                }
            }
        }
    }
}

private fun UiScope.loadingContentWidth(): Dp =
    ResponsiveContentWidth(
        defaultWidth = Dp(620f),
        minWidth = Dp(280f),
        maxWidth = Dp(760f),
        horizontalMargin = Dp(64f),
    )
