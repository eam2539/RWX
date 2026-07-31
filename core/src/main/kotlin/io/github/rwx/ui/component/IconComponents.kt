package io.github.rwx.ui.component

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import io.github.rwx.ui.ColorSchemeDefinition
import io.github.rwx.ui.UiTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

enum class Icon(val assetPath: String) {
    AddAi("ui/icons/add-ai.svg"),
    Apply("ui/icons/apply.svg"),
    Back("ui/icons/back.svg"),
    Campaign("ui/icons/campaign.svg"),
    Challenge("ui/icons/challenge.svg"),
    ChangeTeam("ui/icons/change-team.svg"),
    Close("ui/icons/close.svg"),
    Continue("ui/icons/continue.svg"),
    Delete("ui/icons/delete.svg"),
    DisableAll("ui/icons/disable-all.svg"),
    Display("ui/icons/display.svg"),
    Discord("ui/icons/discord.svg"),
    Exit("ui/icons/exit.svg"),
    Filter("ui/icons/filter.svg"),
    Gameplay("ui/icons/gameplay.svg"),
    Github("ui/icons/github.svg"),
    Help("ui/icons/help.svg"),
    Import("ui/icons/import.svg"),
    Interface("ui/icons/interface.svg"),
    License("ui/icons/license.svg"),
    Map("ui/icons/map.svg"),
    Mods("ui/icons/mods.svg"),
    Multiplayer("ui/icons/multiplayer.svg"),
    Options("ui/icons/options.svg"),
    Refresh("ui/icons/refresh.svg"),
    Sandbox("ui/icons/sandbox.svg"),
    Save("ui/icons/save.svg"),
    Search("ui/icons/search.svg"),
    Send("ui/icons/send.svg"),
    Settings("ui/icons/settings.svg"),
    Skirmish("ui/icons/skirmish.svg"),
    Sort("ui/icons/sort.svg"),
    Start("ui/icons/start.svg"),
    Surrender("ui/icons/surrender.svg"),
    Survival("ui/icons/survival.svg"),
    Team("ui/icons/team.svg"),
    ColorScheme("ui/icons/theme.svg"),
    Qq("ui/icons/qq.svg"),
    Replay("ui/icons/replay.svg"),
    Version("ui/icons/version.svg"),
}

fun UiScope.Icon(
    icon: Icon,
    size: Dp,
    color: Color,
): ImageScope = Image(IconTextureCache.textureFor(icon)) {
    modifier
        .width(size)
        .height(size)
        .imageSize(ImageSize.FitContent)
        .tint(color)
}

fun UiScope.IconButton(
    icon: Icon,
    theme: ColorSchemeDefinition,
    size: Dp = UiTheme.Layout.iconButtonSize,
    iconSize: Dp = UiTheme.Layout.iconButtonGlyphSize,
    tooltip: String? = null,
    onPressed: () -> Unit,
): UiScope {
    val hovered = remember(false)
    val isHovered = hovered.use()
    val background = if (isHovered) theme.palette.surfaceRaised else theme.palette.surfaceBase
    val border = if (isHovered) theme.palette.primary else theme.palette.borderSubtle
    val iconColor = if (isHovered) theme.palette.secondary else theme.palette.primary

    val button = Box(width = size, height = size) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .padding(UiTheme.Spacing.xs)
            .background(RoundRectBackground(background, UiTheme.Spacing.xs))
            .border(RoundRectBorder(border, UiTheme.Spacing.xs, Dp(2f)))
            .onEnter { hovered.value = true }
            .onExit { hovered.value = false }
            .onClick { onPressed() }

        Icon(icon, iconSize, iconColor).modifier.align(AlignmentX.Center, AlignmentY.Center)
    }
    if (tooltip != null) {
        Tooltip(
            text = tooltip,
            target = button,
            backgroundColor = theme.palette.surfaceRaised,
            borderColor = theme.palette.primary,
        )
    }
    return button
}

fun UiScope.TextIconButton(
    label: String,
    icon: Icon,
    width: Dp,
    theme: ColorSchemeDefinition,
    emphasized: Boolean = false,
    font: Font = UiTheme.Fonts.bodySmall,
    height: Dp = UiTheme.Layout.menuButtonHeight,
    onPressed: () -> Unit,
) {
    val hovered = remember(false)
    val isHovered = hovered.use()
    val background = when {
        isHovered -> theme.palette.surfaceRaised
        emphasized -> theme.palette.primaryContainer
        else -> theme.palette.surfaceSunken
    }
    val border = if (isHovered) theme.palette.primary else theme.palette.borderSubtle
    val textColor = if (isHovered) theme.palette.primary else theme.palette.textPrimary
    val iconColor = if (isHovered) theme.palette.secondary else theme.palette.primary

    Box(width = width, height = height) {
        modifier
            .margin(UiTheme.Spacing.xs)
            .padding(horizontal = UiTheme.Spacing.md, vertical = UiTheme.Spacing.xs)
            .background(RoundRectBackground(background, UiTheme.Spacing.xs))
            .border(RoundRectBorder(border, UiTheme.Spacing.xs, Dp(1f)))
            .onEnter { hovered.value = true }
            .onExit { hovered.value = false }
            .onClick { onPressed() }

        Row(width = Grow.Std, height = Grow.Std) {
            modifier.align(AlignmentX.Center, AlignmentY.Center)
            Box(width = UiTheme.Layout.textButtonIconSlotSize, height = Grow.Std) {
                Icon(icon, UiTheme.Layout.textButtonGlyphSize, iconColor).modifier
                    .align(AlignmentX.Center, AlignmentY.Center)
            }
            Text(label) {
                modifier
                    .width(Grow.Std)
                    .height(Grow.Std)
                    .font(font)
                    .textAlign(AlignmentX.Center, AlignmentY.Center)
                    .clipToBounds(true)
                    .textColor(textColor)
            }
            Box(width = UiTheme.Layout.textButtonIconSlotSize, height = Grow.Std) {}
        }
    }
}

private object IconTextureCache {
    private val iconTextureSize = Vec2i(256, 256)
    private val textures = mutableMapOf<Icon, Texture2d>()

    fun textureFor(icon: Icon): Texture2d = textures.getOrPut(icon) {
        Texture2d(name = "icon:${icon.assetPath}") {
            Assets.defaultLoader.loadImage2d(icon.assetPath, resolveSize = iconTextureSize).getOrNull()
                ?: AssetLoader.textureDataLoadFailed
        }
    }

    suspend fun preload(onProgress: (Int, Int) -> Unit) {
        var completed = Icon.entries.count { textures[it]?.isLoaded == true }
        onProgress(completed, Icon.entries.size)
        Icon.entries.filter { textures[it]?.isLoaded != true }
            .chunked(UI_TEXTURE_PRELOAD_CONCURRENCY)
            .forEach { icons ->
                val loaded = coroutineScope {
                    icons.map { icon ->
                        async {
                            icon to (
                                    Assets.defaultLoader
                                        .loadImage2d(icon.assetPath, resolveSize = iconTextureSize)
                                        .getOrNull()
                                        ?: AssetLoader.textureDataLoadFailed
                                    )
                        }
                    }.awaitAll()
                }
                loaded.forEach { (icon, imageData) ->
                    val texture = Texture2d(name = "icon:${icon.assetPath}")
                    texture.upload(imageData)
                    textures[icon] = texture
                    completed++
                    onProgress(completed, Icon.entries.size)
                }
            }
    }

    fun invalidate() {
        textures.clear()
    }
}

/** Decodes and uploads shared icon textures before interactive scenes become visible. */
suspend fun preloadUiIconTextures(onProgress: (Int, Int) -> Unit = { _, _ -> }) {
    IconTextureCache.preload(onProgress)
}

/** Draws one preloaded texture to compile the shared image pipeline on Loading. */
internal fun UiScope.UiIconWarmup(theme: ColorSchemeDefinition) {
    Box(width = Dp(1f), height = Dp(1f)) {
        Icon(Icon.Back, Dp(1f), theme.palette.textPrimary.withAlpha(0f)).modifier
            .align(AlignmentX.Start, AlignmentY.Top)
    }
}
fun invalidateUiIconTextureCache() {
    IconTextureCache.invalidate()
}
private const val UI_TEXTURE_PRELOAD_CONCURRENCY: Int = 6