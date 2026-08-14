package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.Colors
import de.fabmax.kool.modules.ui2.Dp
import de.fabmax.kool.modules.ui2.Sizes
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MsdfFont
import io.github.rwx.render.canvas.KoolCanvasFontRegistry

@JvmInline
value class ColorSchemeId(val value: String)

data class ColorSchemePalette(
    val panelOverlay: Color,
    val panelOverlayDark: Color,
    val panelOverlayLight: Color,
    val panelHud: Color,
    val surfaceBase: Color,
    val surfaceRaised: Color,
    val surfaceSunken: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimary: Color,
    val secondary: Color,
    val danger: Color,
    val borderSubtle: Color,
)

data class ColorSchemeDefinition(
    val id: ColorSchemeId,
    val displayName: String,
    val palette: ColorSchemePalette,
)

object ColorSchemeRegistry {
    val defaultSchemeId: ColorSchemeId = ColorSchemeId("rwx")

    private val materialDarkDefaults = ColorScheme()

    val schemes: List<ColorSchemeDefinition> = listOf(
        scheme(
            id = "rwx",
            displayName = "RWX",
            scheme = ColorScheme(
                surface = Color("1b1212ff"),
                surfaceContainer = Color("444444ff"),
                onSurface = Color.WHITE,
                primaryContainer = Color("97bc62ff"),
                secondary = Color("5fbe5fff"),
                secondaryContainer = Color("97bc62ff"),
                primary = Color("97bc62ff"),
                onPrimary = Color.BLACK,
                background = Color("353935ff"),
                inversePrimary = Color("2c5f2dff"),
                surfaceTint = Color.WHITE,
            ),
        ),
        scheme(
            id = "material-purple",
            displayName = "Material Purple",
            scheme = materialDarkDefaults.copy(
                background = Color("302838ff"),
                surfaceContainer = Color("443c4cff"),
            ),
        ),
        scheme(
            id = "material-default",
            displayName = "Material Default",
            scheme = materialDarkDefaults.copy(
                primary = Color("bb86fcff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("6200eeff"),
                secondary = Color("03dac6ff"),
                secondaryContainer = Color("005047ff"),
                tertiary = Color("03dac6ff"),
                tertiaryContainer = Color("003e3eff"),
                inversePrimary = Color("543f6aff"),
                background = Color("302838ff"),
                surfaceContainer = Color("443c4cff"),
            ),
        ),
        scheme(
            id = "amber-blue",
            displayName = "Amber Blue",
            scheme = materialDarkDefaults.copy(
                primary = Color("ffb300ff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("c87200ff"),
                secondary = Color("82b1ffff"),
                secondaryContainer = Color("3770cfff"),
                tertiary = Color("448affff"),
                tertiaryContainer = Color("0b429cff"),
                inversePrimary = Color("6a510aff"),
                background = Color("302c28ff"),
                surfaceContainer = Color("44403cff"),
            ),
        ),
        scheme(
            id = "aqua-blue",
            displayName = "Aqua Blue",
            scheme = materialDarkDefaults.copy(
                primary = Color("5db3d5ff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("297ea0ff"),
                secondary = Color("a1e9dfff"),
                secondaryContainer = Color("005049ff"),
                tertiary = Color("a0e5e5ff"),
                tertiaryContainer = Color("004f50ff"),
                inversePrimary = Color("2f515fff"),
                background = Color("283238ff"),
                surfaceContainer = Color("3c464cff"),
            ),
        ),
        scheme(
            id = "bahama-and-trinidad",
            displayName = "Bahama And Trinidad",
            scheme = materialDarkDefaults.copy(
                primary = Color("4585b5ff"),
                onPrimary = Color.WHITE,
                primaryContainer = Color("095d9eff"),
                secondary = Color("e57c4aff"),
                secondaryContainer = Color("dd520fff"),
                tertiary = Color("9cd5f9ff"),
                tertiaryContainer = Color("3a7292ff"),
                inversePrimary = Color("253f52ff"),
                background = Color("283038ff"),
                surfaceContainer = Color("3c444cff"),
            ),
        ),
        scheme(
            id = "gold-sunset",
            displayName = "Gold Sunset",
            scheme = materialDarkDefaults.copy(
                primary = Color("eda85eff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("b86914ff"),
                secondary = Color("d28f60ff"),
                secondaryContainer = Color("b5642cff"),
                tertiary = Color("ddab88ff"),
                tertiaryContainer = Color("bf7d4eff"),
                inversePrimary = Color("684d2fff"),
                background = Color("383028ff"),
                surfaceContainer = Color("4c443cff"),
            ),
        ),
        scheme(
            id = "flutter-dash",
            displayName = "Flutter Dash",
            scheme = materialDarkDefaults.copy(
                primary = Color("b4e6ffff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("1e8fdbff"),
                secondary = Color("99ccf9ff"),
                secondaryContainer = Color("202b6dff"),
                tertiary = Color("baa99dff"),
                tertiaryContainer = Color("514239ff"),
                inversePrimary = Color("52666aff"),
                background = Color("283238ff"),
                surfaceContainer = Color("3c464cff"),
            ),
        ),
        scheme(
            id = "hippie-blue",
            displayName = "Hippie Blue",
            scheme = materialDarkDefaults.copy(
                primary = Color("669db3ff"),
                onPrimary = Color.WHITE,
                primaryContainer = Color("078282ff"),
                secondary = Color("fc6e75ff"),
                secondaryContainer = Color("92001aff"),
                tertiary = Color("f75f67ff"),
                tertiaryContainer = Color("580810ff"),
                inversePrimary = Color("324851ff"),
                background = Color("283238ff"),
                surfaceContainer = Color("3c464cff"),
            ),
        ),
        scheme(
            id = "pink-sakura",
            displayName = "Pink Sakura",
            scheme = materialDarkDefaults.copy(
                primary = Color("eec4d8ff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("ce5b78ff"),
                secondary = Color("f5d6c6ff"),
                secondaryContainer = Color("eba689ff"),
                tertiary = Color("f7e0d4ff"),
                tertiaryContainer = Color("eebda8ff"),
                inversePrimary = Color("695860ff"),
                background = Color("383034ff"),
                surfaceContainer = Color("4c4448ff"),
            ),
        ),
        scheme(
            id = "blumine",
            displayName = "Blumine",
            scheme = materialDarkDefaults.copy(
                primary = Color("82baceff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("04666fff"),
                secondary = Color("ffd682ff"),
                secondaryContainer = Color("9e7910ff"),
                tertiary = Color("243e4dff"),
                tertiaryContainer = Color("426173ff"),
                inversePrimary = Color("3e545cff"),
                background = Color("283238ff"),
                surfaceContainer = Color("3c464cff"),
            ),
        ),
        scheme(
            id = "green-money",
            displayName = "Green Money",
            scheme = materialDarkDefaults.copy(
                primary = Color("7ab893ff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("224430ff"),
                secondary = Color("d5d6a8ff"),
                secondaryContainer = Color("515402ff"),
                tertiary = Color("bbbe74ff"),
                tertiaryContainer = Color("404204ff"),
                inversePrimary = Color("3a5344ff"),
                background = Color("28322cff"),
                surfaceContainer = Color("3c4640ff"),
            ),
        ),
        scheme(
            id = "rosewood",
            displayName = "Rosewood",
            scheme = materialDarkDefaults.copy(
                primary = Color("9c5a69ff"),
                onPrimary = Color.WHITE,
                primaryContainer = Color("5f111eff"),
                secondary = Color("edce9bff"),
                secondaryContainer = Color("805e23ff"),
                tertiary = Color("f5dfb9ff"),
                tertiaryContainer = Color("8e6e3cff"),
                inversePrimary = Color("482e34ff"),
                background = Color("382c30ff"),
                surfaceContainer = Color("4c4044ff"),
            ),
        ),
        scheme(
            id = "verdun-lime",
            displayName = "Verdun Lime",
            scheme = materialDarkDefaults.copy(
                primary = Color("bcd063ff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("3f4c00ff"),
                secondary = Color("ffe17bff"),
                secondaryContainer = Color("3b2f00ff"),
                tertiary = Color("78d3ecff"),
                tertiaryContainer = Color("224e43ff"),
                inversePrimary = Color("555d31ff"),
                background = Color("303228ff"),
                surfaceContainer = Color("44463cff"),
            ),
        ),
        scheme(
            id = "grey-law",
            displayName = "Grey Law",
            scheme = materialDarkDefaults.copy(
                primary = Color("90a4aeff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("37474fff"),
                secondary = Color("815aa3ff"),
                secondaryContainer = Color("421f62ff"),
                tertiary = Color("373d5cff"),
                tertiaryContainer = Color("1d2449ff"),
                inversePrimary = Color("434b4fff"),
                background = Color("303234ff"),
                surfaceContainer = Color("444648ff"),
            ),
        ),
        scheme(
            id = "red-tornado",
            displayName = "Red Tornado",
            scheme = materialDarkDefaults.copy(
                primary = Color("ef9a9aff"),
                onPrimary = Color.BLACK,
                primaryContainer = Color("b71c1cff"),
                secondary = Color("f8bbd0ff"),
                secondaryContainer = Color("ad1457ff"),
                tertiary = Color("fce4ecff"),
                tertiaryContainer = Color("c2185bff"),
                inversePrimary = Color("694747ff"),
                background = Color("382c2cff"),
                surfaceContainer = Color("4c4040ff"),
            ),
        ),
    )

    fun schemeFor(id: ColorSchemeId): ColorSchemeDefinition {
        return schemes.firstOrNull { it.id == id } ?: schemes.first()
    }

    private data class ColorScheme(
        val primary: Color = Color("d0bcffff"),
        val onPrimary: Color = Color("381e72ff"),
        val primaryContainer: Color = Color("4f378bff"),
        val secondary: Color = Color("ccc2dcff"),
        val secondaryContainer: Color = Color("4a4458ff"),
        val tertiary: Color = Color("e6e0e9ff"),
        val tertiaryContainer: Color = Color("3b383eff"),
        val inversePrimary: Color = Color("d0bcffff"),
        val background: Color = Color("141218ff"),
        val surface: Color = Color("141218ff"),
        val surfaceContainer: Color = Color("211f26ff"),
        val onSurface: Color = Color("e6e0e9ff"),
        val surfaceTint: Color = primary,
    )

    private fun scheme(
        id: String,
        displayName: String,
        scheme: ColorScheme,
    ): ColorSchemeDefinition = ColorSchemeDefinition(
        id = ColorSchemeId(id),
        displayName = displayName,
        palette = scheme.toKoolPalette(),
    )

    private fun ColorScheme.toKoolPalette(): ColorSchemePalette = ColorSchemePalette(
        panelOverlay = background.withAlpha(0.82f),
        panelOverlayDark = background.withAlpha(0.9f),
        panelOverlayLight = background.withAlpha(0.72f),
        panelHud = background.withAlpha(0.82f),
        surfaceBase = surface,
        surfaceRaised = surfaceContainer,
        surfaceSunken = surface.mix(Color.BLACK, 0.35f),
        textPrimary = onSurface,
        textSecondary = onSurface.withAlpha(0.78f),
        textDisabled = onSurface.withAlpha(0.42f),
        primary = primary,
        primaryContainer = primaryContainer,
        onPrimary = onPrimary,
        secondary = secondary,
        danger = Color("ff6b6bff"),
        borderSubtle = surfaceContainer,
    )
}

object UiTheme {
    object Palette {
        private val classic = ColorSchemeRegistry.schemeFor(ColorSchemeRegistry.defaultSchemeId).palette
        val panelOverlay: Color = classic.panelOverlay
        val panelOverlayDark: Color = classic.panelOverlayDark
        val panelOverlayLight: Color = classic.panelOverlayLight
        val panelHud: Color = classic.panelHud
        val surfaceBase: Color = classic.surfaceBase
        val surfaceRaised: Color = classic.surfaceRaised
        val surfaceSunken: Color = classic.surfaceSunken
        val textPrimary: Color = classic.textPrimary
        val textSecondary: Color = classic.textSecondary
        val danger: Color = classic.danger
        val accentRed: Color = classic.primaryContainer
        val accentRedBright: Color = classic.primary
    }

    object Spacing {
        val xs: Dp = Dp(4f)
        val sm: Dp = Dp(8f)
        val md: Dp = Dp(12f)
        val lg: Dp = Dp(16f)
        val xl: Dp = Dp(24f)
    }

    object Layout {
        val mainMenuContentWidth: Dp = Dp(1280f)
        val mainMenuMinContentWidth: Dp = Dp(280f)
        val mainMenuMaxContentWidth: Dp = Dp(1720f)
        val mainMenuTitleHeight: Dp = Dp(128f)
        val mainMenuTileWidth: Dp = Dp(172f)
        val mainMenuTileMinWidth: Dp = Dp(124f)
        val mainMenuTileMaxWidth: Dp = Dp(176f)
        val mainMenuTileHeight: Dp = Dp(124f)
        val mainMenuTileIconSize: Dp = Dp(44f)
        val mainMenuColumnGap: Dp = Dp(20f)
        val mainMenuColumnStagger: Dp = Dp(36f)
        val mainMenuViewportHeight: Dp = Dp(420f)
        val mainMenuMinViewportHeight: Dp = Dp(260f)
        val mainMenuMaxViewportHeight: Dp = Dp(460f)
        val menuButtonWidth: Dp = Dp(560f)
        val menuButtonHeight: Dp = Dp(56f)
        val CompactMenuButtonHeight: Dp = Dp(34f)
        val iconButtonSize: Dp = Dp(56f)
        val iconButtonGlyphSize: Dp = Dp(28f)
        val textButtonIconSlotSize: Dp = Dp(32f)
        val textButtonGlyphSize: Dp = Dp(24f)
        val settingsContentWidth: Dp = Dp(1080f)
        val settingsMinContentWidth: Dp = Dp(320f)
        val settingsMaxContentWidth: Dp = Dp(1560f)
        val settingsRowWidth: Dp = Dp(1040f)
        val settingsRowHeight: Dp = Dp(64f)
        val settingsRowIconSlotSize: Dp = Dp(42f)
        val levelSelectButtonWidth: Dp = Dp(520f)
        val levelSelectContentWidth: Dp = Dp(1280f)
        val levelSelectMinContentWidth: Dp = Dp(280f)
        val levelSelectMaxContentWidth: Dp = Dp(1720f)
        val levelSelectControlLabelWidth: Dp = Dp(112f)
        val levelSelectComboWidth: Dp = Dp(210f)
        val levelSelectMapTileWidth: Dp = Dp(260f)
        val levelSelectMapTileHeight: Dp = Dp(204f)
        val levelSelectPreviewHeight: Dp = Dp(138f)
        val levelSelectGridViewportHeight: Dp = Dp(636f)
        val replaySelectListHeight: Dp = Dp(560f)
        val replaySelectMinListHeight: Dp = Dp(240f)
        val replaySelectMaxListHeight: Dp = Dp(680f)
        val replaySelectRowHeight: Dp = Dp(76f)
        val replaySelectIconSlotWidth: Dp = Dp(56f)
        val scrollViewportHeight: Dp = Dp(480f)
        val settingsViewportHeight: Dp = Dp(460f)
        val settingsMinViewportHeight: Dp = Dp(260f)
        val settingsMaxViewportHeight: Dp = Dp(960f)
        val dialogMessageWidth: Dp = Dp(560f)
        val dialogMinMessageWidth: Dp = Dp(320f)
        val dialogMaxMessageWidth: Dp = Dp(780f)
        val dialogHeaderHeight: Dp = Dp(64f)
        val dialogScrollableMessageHeight: Dp = Dp(320f)
        val dialogButtonWidth: Dp = Dp(200f)
        val snackbarDismissButtonSize: Dp = Dp(44f)
        val snackbarDismissIconSize: Dp = Dp(22f)
        val snackbarBottomMargin: Dp = Dp(28f)
        val multiplayerRoomRowWidth: Dp = Dp(1280f)
        val multiplayerMinContentWidth: Dp = Dp(320f)
        val multiplayerMaxContentWidth: Dp = Dp(1560f)
        val multiplayerMinViewportHeight: Dp = Dp(260f)
        val multiplayerMaxViewportHeight: Dp = Dp(720f)
        val battleRoomContentWidth: Dp = Dp(1360f)
        val battleRoomMinContentWidth: Dp = Dp(320f)
        val battleRoomMaxContentWidth: Dp = Dp(1800f)
        val battleRoomPreviewHeight: Dp = Dp(420f)
        val battleRoomMinPreviewHeight: Dp = Dp(220f)
        val battleRoomMaxPreviewHeight: Dp = Dp(560f)
        val battleRoomPlayersViewportHeight: Dp = Dp(260f)
        val battleRoomMinPlayersViewportHeight: Dp = Dp(180f)
        val battleRoomMaxPlayersViewportHeight: Dp = Dp(430f)
        val battleRoomChatViewportHeight: Dp = Dp(160f)
        val battleRoomMinChatViewportHeight: Dp = Dp(96f)
        val battleRoomMaxChatViewportHeight: Dp = Dp(260f)
        val battleRoomRowHeight: Dp = Dp(40f)
        val battleRoomCellSpawnWidth: Dp = Dp(72f)
        val battleRoomCellTeamWidth: Dp = Dp(72f)
        val battleRoomCellPingWidth: Dp = Dp(84f)
        val battleRoomActionButtonWidth: Dp = Dp(200f)
        val battleRoomSendButtonWidth: Dp = Dp(120f)

        val modsContentWidth: Dp = Dp(1180f)
        val modsMinContentWidth: Dp = Dp(320f)
        val modsMaxContentWidth: Dp = Dp(1560f)
        val modsViewportHeight: Dp = Dp(560f)
        val modsMinViewportHeight: Dp = Dp(260f)
        val modsMaxViewportHeight: Dp = Dp(720f)
        val modsCardHeight: Dp = Dp(88f)
        val modsThumbnailSize: Dp = Dp(68f)
        val modsCardErrorMaxHeight: Dp = Dp(240f)
        val modsCardDescriptionMaxHeight: Dp = Dp(45f)
        val modsToggleButtonWidth: Dp = Dp(156f)
        val modsActionButtonWidth: Dp = Dp(210f)
    }

    object Fonts {
        const val CJK_MSDF_FONT_PATH: String = "font/NotoSansCJKsc-Regular"
        const val TITLE_MSDF_FONT_PATH: String = "font/ZenDots-Regular"

        const val PORTABLE_MAX_ATLAS_DIMENSION: Int = 4096

        @Volatile
        private var installedBase: MsdfFont? = null

        val base: MsdfFont
            get() = installedBase ?: defaultFont()

        @Volatile
        private var installedTitleBase: MsdfFont? = null

        val titleBase: MsdfFont
            get() = installedTitleBase ?: defaultFont()

        /** Installs the pre-baked Latin+CJK atlas as the [base] font once it has finished loading. */
        fun installBaseFont(font: MsdfFont) {
            installedBase = font
            KoolCanvasFontRegistry.installBaseFont(font)
        }

        fun installTitleFont(font: MsdfFont) {
            installedTitleBase = font
        }

        suspend fun install(maxTextureSize: Int = PORTABLE_MAX_ATLAS_DIMENSION) {
            installBaseFont(loadAtlas(CJK_MSDF_FONT_PATH, maxTextureSize))
            installTitleFont(loadAtlas(TITLE_MSDF_FONT_PATH, maxTextureSize))
        }

        private suspend fun loadAtlas(path: String, maxTextureSize: Int): MsdfFont {
            val font = MsdfFont(path).getOrElse { error ->
                throw IllegalStateException("Failed to load required MSDF font: $path", error)
            }
            val atlas = font.data.meta.atlas
            check(atlas.width <= maxTextureSize && atlas.height <= maxTextureSize) {
                "MSDF atlas $path is ${atlas.width}x${atlas.height}, which exceeds the GPU texture limit of $maxTextureSize."
            }
            return font
        }

        val caption: Font get() = base.derive(14f)
        val hud: Font get() = base.derive(16f)
        val bodySmall: Font get() = base.derive(18f)
        val bodyMedium: Font get() = base.derive(22f)
        val bodyLarge: Font get() = base.derive(26f)
        val headingSmall: Font get() = base.derive(28f)
        val headingMedium: Font get() = base.derive(34f)
        val headingLarge: Font get() = base.derive(44f)
        val displayTitle: Font get() = titleBase.derive(94f)

        private fun defaultFont(): MsdfFont =
            runCatching { MsdfFont.DEFAULT_FONT }
                .getOrElse { error("Kool default font is unavailable before Kool runtime initialization") }
    }

    val sizes: Sizes
        get() = Sizes.medium(
            smallText = Fonts.bodySmall,
            normalText = Fonts.bodyMedium,
            largeText = Fonts.headingMedium,
            gap = Spacing.sm,
            smallGap = Spacing.xs,
            largeGap = Spacing.lg,
        )

    fun colors(scheme: ColorSchemeDefinition, background: Color): Colors = Colors.darkColors(
        primary = scheme.palette.primary,
        primaryVariant = scheme.palette.primaryContainer,
        secondary = scheme.palette.surfaceRaised,
        secondaryVariant = scheme.palette.surfaceSunken,
        background = background,
        backgroundVariant = scheme.palette.surfaceBase,
        onPrimary = scheme.palette.onPrimary,
        onSecondary = scheme.palette.textPrimary,
        onBackground = scheme.palette.textPrimary,
    )

    fun colors(background: Color): Colors =
        colors(ColorSchemeRegistry.schemeFor(ColorSchemeRegistry.defaultSchemeId), background)
}
