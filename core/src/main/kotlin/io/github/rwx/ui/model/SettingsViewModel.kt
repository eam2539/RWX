package io.github.rwx.ui.model

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.KeyBinding
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import io.github.rwx.i18n.I18n
import io.github.rwx.i18n.I18nText
import io.github.rwx.ui.AppScreen
import io.github.rwx.ui.ColorSchemeDefinition
import io.github.rwx.ui.ColorSchemeId
import io.github.rwx.ui.ColorSchemeRegistry
import kotlin.math.roundToInt


enum class SettingsPage(val title: String, val tabTitle: String) {
    Display("Display & Input", "Display"),
    Audio("Audio", "Audio"),
    Interface("Interface", "Interface"),
    Gameplay("Gameplay", "Gameplay"),
    KeyBindings("Key Bindings", "Keys"),
    ColorScheme("Color Scheme", "Colors"),
}

sealed interface SettingsPageItem {
    data class Toggle(val toggle: SettingToggle) : SettingsPageItem
    data class Slider(val slider: SettingSlider) : SettingsPageItem
    data class ColorSchemeSelector(val item: SettingColorSchemeItem, val selected: Boolean) : SettingsPageItem
    data class StorageLocation(val selectedType: Int) : SettingsPageItem
}

sealed interface SettingsScrollRow {
    data class SectionTitle(val text: String) : SettingsScrollRow
    data class BodyText(val text: String) : SettingsScrollRow
    data class Toggle(val toggle: SettingToggle) : SettingsScrollRow
    data class Slider(val slider: SettingSlider) : SettingsScrollRow
    data class KeyBinding(val row: SettingKeyBindingRow) : SettingsScrollRow
    data class ColorSchemeSelector(val item: SettingColorSchemeItem, val selected: Boolean) : SettingsScrollRow
    data class StorageLocation(val selectedType: Int) : SettingsScrollRow
}

data class SettingsPageContent(
    val page: SettingsPage,
    val items: List<SettingsPageItem>,
)

data class SettingToggle(val i18nText: I18nText, val state: MutableStateValue<Boolean>)

enum class AndroidStoragePreference(val storageType: Int, private val text: I18nText) {
    Internal(0, I18n.settings.storage.internal),
    External(2, I18n.settings.storage.external),
    ;

    override fun toString(): String = text()

    fun shortLabel(): String = when (this) {
        Internal -> I18n.settings.storage.internalShort()
        External -> I18n.settings.storage.externalShort()
    }

    companion object {
        fun fromStorageType(storageType: Int): AndroidStoragePreference =
            if (storageType >= External.storageType) External else Internal
    }
}

data class SettingSlider(
    val i18nText: I18nText,
    val state: MutableStateValue<Float>,
    val min: Float,
    val max: Float,
    val step: Float = 0.05f,
    val formatValue: (Float) -> String = { value -> "${(value * 100f).roundToInt()}%" },
) {
    fun normalize(value: Float): Float {
        val clamped = value.coerceIn(min, max)
        if (step <= 0f) return clamped
        return (min + (((clamped - min) / step).roundToInt() * step)).coerceIn(min, max)
    }

    fun formatted(value: Float): String = formatValue(normalize(value))
}

data class SettingColorSchemeItem(
    val label: String,
    val key: String,
    val id: ColorSchemeId,
    val scheme: ColorSchemeDefinition,
)

data class SettingKeyBindingRow(
    val index: Int,
    val keyBinding: KeyBinding,
    val primaryText: String,
    val secondaryText: String,
    val primaryOverlaps: Boolean,
    val secondaryOverlaps: Boolean,
    val activeSlot: Int?,
)

class SettingsModel {
    // Display
    val batterySaving: MutableStateValue<Boolean> = mutableStateOf(false)
    val highRefreshRate: MutableStateValue<Boolean> = mutableStateOf(true)
    val slick2dFullScreen: MutableStateValue<Boolean> = mutableStateOf(true)
    val vsync: MutableStateValue<Boolean> = mutableStateOf(false)
    val showUnitHp: MutableStateValue<Boolean> = mutableStateOf(true)
    val showWaypoints: MutableStateValue<Boolean> = mutableStateOf(true)
    val showZoomButton: MutableStateValue<Boolean> = mutableStateOf(true)
    val showFps: MutableStateValue<Boolean> = mutableStateOf(false)
    val renderClouds: MutableStateValue<Boolean> = mutableStateOf(false)
    val renderDoubleScale: MutableStateValue<Boolean> = mutableStateOf(false)
    val softFogFading: MutableStateValue<Boolean> = mutableStateOf(false)
    val shaderEffects: MutableStateValue<Boolean> = mutableStateOf(false)
    val teamShaders: MutableStateValue<Boolean> = mutableStateOf(false)
    val useAndroidOpenGlRenderer: MutableStateValue<Boolean> = mutableStateOf(false)
    val showMainMenuBackgroundDemo: MutableStateValue<Boolean> = mutableStateOf(false)

    // Render layers (engine-only, no UI toggle)
    val renderBackground: MutableStateValue<Boolean> = mutableStateOf(true)
    val renderExtraLayers: MutableStateValue<Boolean> = mutableStateOf(true)
    val showHpChanges: MutableStateValue<Boolean> = mutableStateOf(true)
    val showUnitIcons: MutableStateValue<Boolean> = mutableStateOf(true)
    val useMinimapAllyColors: MutableStateValue<Boolean> = mutableStateOf(true)
    val showWarLogOnScreen: MutableStateValue<Boolean> = mutableStateOf(true)

    // Interface
    val mouseCaptureEnabled: MutableStateValue<Boolean> = mutableStateOf(false)
    val mouseSupport: MutableStateValue<Boolean> = mutableStateOf(true)
    val keyboardSupport: MutableStateValue<Boolean> = mutableStateOf(true)
    val gestureZoom: MutableStateValue<Boolean> = mutableStateOf(true)
    val useCircleSelect: MutableStateValue<Boolean> = mutableStateOf(false)
    val showUnitGroups: MutableStateValue<Boolean> = mutableStateOf(true)
    val immersiveFullScreen: MutableStateValue<Boolean> = mutableStateOf(true)
    val unlockedScreenRotation: MutableStateValue<Boolean> = mutableStateOf(false)
    val classicInterface: MutableStateValue<Boolean> = mutableStateOf(false)
    val forceEnglish: MutableStateValue<Boolean> = mutableStateOf(false)

    // Gameplay
    val quickRally: MutableStateValue<Boolean> = mutableStateOf(true)
    val doubleClickToAttackMove: MutableStateValue<Boolean> = mutableStateOf(true)
    val showMapPingsOnBattlefield: MutableStateValue<Boolean> = mutableStateOf(true)
    val showMapPingsOnMinimap: MutableStateValue<Boolean> = mutableStateOf(true)
    val showPlayerChatInGame: MutableStateValue<Boolean> = mutableStateOf(true)
    val showChatAndPingShortcuts: MutableStateValue<Boolean> = mutableStateOf(true)
    val smartSelection: MutableStateValue<Boolean> = mutableStateOf(true)
    val autosaving: MutableStateValue<Boolean> = mutableStateOf(true)
    val udpInMultiplayer: MutableStateValue<Boolean> = mutableStateOf(false)
    val saveMultiplayerReplays: MutableStateValue<Boolean> = mutableStateOf(true)
    val replaysShowRecordedChat: MutableStateValue<Boolean> = mutableStateOf(true)
    val sendReports: MutableStateValue<Boolean> = mutableStateOf(true)

    // Audio (engine-only, no UI toggle)
    val enableSounds: MutableStateValue<Boolean> = mutableStateOf(true)

    // Volume
    val masterVolume: MutableStateValue<Float> = mutableStateOf(0.5f)
    val gameVolume: MutableStateValue<Float> = mutableStateOf(1.0f)
    val interfaceVolume: MutableStateValue<Float> = mutableStateOf(0.8f)
    val musicVolume: MutableStateValue<Float> = mutableStateOf(0.25f)

    // Scroll
    val scrollSpeed: MutableStateValue<Float> = mutableStateOf(1.0f)
    val edgeScrollSpeed: MutableStateValue<Float> = mutableStateOf(1.0f)

    // Color scheme
    val selectedColorSchemeId: MutableStateValue<ColorSchemeId> = mutableStateOf(ColorSchemeRegistry.defaultSchemeId)

    // Android original-engine file backend.
    val storageType: MutableStateValue<Int> = mutableStateOf(2)

}

fun SettingsModel.colorScheme(): ColorSchemeDefinition = ColorSchemeRegistry.schemeFor(selectedColorSchemeId.value)

class SettingsViewModel(val model: SettingsModel) {

    private fun displayToggles(): List<SettingToggle> = buildList {
        if (isAndroidPlatform()) {
            add(SettingToggle(I18n.settings.display.batterySaving, model.batterySaving))
            add(SettingToggle(I18n.settings.display.highRefreshRate, model.highRefreshRate))
            add(SettingToggle(I18n.settings.`interface`.unlockedScreenRotation, model.unlockedScreenRotation))
        }
        if (isPcPlatform()) {
            add(SettingToggle(I18n.settings.display.fullscreen, model.slick2dFullScreen))
            add(SettingToggle(I18n.settings.display.vsync, model.vsync))
        }
        add(SettingToggle(I18n.settings.display.showUnitHp, model.showUnitHp))
        add(SettingToggle(I18n.settings.display.showWaypoints, model.showWaypoints))
        add(SettingToggle(I18n.settings.display.showUnitIcons, model.showUnitIcons))
        add(SettingToggle(I18n.settings.display.useMinimapAllyColors, model.useMinimapAllyColors))
        if (isAndroidPlatform()) {
            add(SettingToggle(I18n.settings.display.showZoomButton, model.showZoomButton))
            add(SettingToggle(I18n.settings.`interface`.immersiveFullScreen, model.immersiveFullScreen))
            add(SettingToggle(I18n.settings.display.renderDoubleScale, model.renderDoubleScale))
        }
        add(SettingToggle(I18n.settings.display.renderClouds, model.renderClouds))
        add(SettingToggle(I18n.settings.display.showWarLogOnScreen, model.showWarLogOnScreen))
        add(SettingToggle(I18n.settings.display.showFps, model.showFps))
        if (isAndroidPlatform()) {
            add(SettingToggle(I18n.settings.`interface`.classicInterface, model.classicInterface))
        }
        if (isAndroidPlatform()) {
            add(
                SettingToggle(
                    I18n.settings.display.experimentalAndroidOpenGlRenderer,
                    model.useAndroidOpenGlRenderer,
                )
            )
        }
        if (!isAndroidPlatform() || model.useAndroidOpenGlRenderer.value) {
            add(SettingToggle(I18n.settings.display.shaderEffects, model.shaderEffects))
            add(SettingToggle(I18n.settings.display.teamShaders, model.teamShaders))
        }
        add(SettingToggle(I18n.settings.display.softFogFading, model.softFogFading))
        add(SettingToggle(I18n.settings.display.showMainMenuBackgroundDemo, model.showMainMenuBackgroundDemo))
        add(SettingToggle(I18n.settings.display.showHpChanges, model.showHpChanges))
    }

    private fun displaySliders(): List<SettingSlider> = buildList {
        add(SettingSlider(I18n.settings.display.scrollSpeed, model.scrollSpeed, min = 0.5f, max = 2.0f))
        if (isPcPlatform()) {
            add(SettingSlider(I18n.settings.display.edgeScrollSpeed, model.edgeScrollSpeed, min = 0.5f, max = 2.0f))
        }
    }

    private fun audioToggles(): List<SettingToggle> = listOf(
        SettingToggle(I18n.settings.audio.enableSounds, model.enableSounds),
    )

    private fun audioSliders(): List<SettingSlider> = listOf(
        SettingSlider(I18n.settings.audio.masterVolume, model.masterVolume, min = 0.0f, max = 1.0f),
        SettingSlider(I18n.settings.audio.gameVolume, model.gameVolume, min = 0.0f, max = 1.0f),
        SettingSlider(I18n.settings.audio.interfaceVolume, model.interfaceVolume, min = 0.0f, max = 1.0f),
        SettingSlider(I18n.settings.audio.musicVolume, model.musicVolume, min = 0.0f, max = 1.0f),
    )

    private fun interfaceToggles(): List<SettingToggle> = buildList {
        if (isPcPlatform()) {
            add(SettingToggle(I18n.settings.`interface`.mouseCapture, model.mouseCaptureEnabled))
        }
        if (isAndroidPlatform()) {
            add(SettingToggle(I18n.settings.`interface`.showUnitGroups, model.showUnitGroups))
            add(SettingToggle(I18n.settings.`interface`.gestureZoom, model.gestureZoom))
            add(SettingToggle(I18n.settings.`interface`.useCircleSelect, model.useCircleSelect))
            add(SettingToggle(I18n.settings.`interface`.mouseSupport, model.mouseSupport))
            add(SettingToggle(I18n.settings.`interface`.keyboardSupport, model.keyboardSupport))
        }
        add(SettingToggle(I18n.settings.`interface`.forceEnglish, model.forceEnglish))
    }

    private fun gameplayToggles(): List<SettingToggle> = buildList {
        add(SettingToggle(I18n.settings.gameplay.quickRally, model.quickRally))
        add(SettingToggle(I18n.settings.gameplay.doubleClickToAttackMove, model.doubleClickToAttackMove))
        add(SettingToggle(I18n.settings.gameplay.smartSelection, model.smartSelection))
        if (isAndroidPlatform()) {
            add(SettingToggle(I18n.settings.gameplay.autosaving, model.autosaving))
        }
        add(SettingToggle(I18n.settings.gameplay.udpInMultiplayer, model.udpInMultiplayer))
        add(SettingToggle(I18n.settings.gameplay.showMapPingsOnBattlefield, model.showMapPingsOnBattlefield))
        add(SettingToggle(I18n.settings.gameplay.showMapPingsOnMinimap, model.showMapPingsOnMinimap))
        add(SettingToggle(I18n.settings.gameplay.showPlayerChatInGame, model.showPlayerChatInGame))
        if (isPcPlatform()) {
            add(SettingToggle(I18n.settings.gameplay.showChatAndPingShortcuts, model.showChatAndPingShortcuts))
        }
        add(SettingToggle(I18n.settings.gameplay.saveMultiplayerReplays, model.saveMultiplayerReplays))
        add(SettingToggle(I18n.settings.gameplay.replaysShowRecordedChat, model.replaysShowRecordedChat))
        add(SettingToggle(I18n.settings.`interface`.sendReports, model.sendReports))
    }

    fun items(): List<SettingToggle> = displayToggles() + audioToggles() + interfaceToggles() + gameplayToggles()

    fun colorSchemeItems(): List<SettingColorSchemeItem> = ColorSchemeRegistry.schemes.map { scheme ->
        SettingColorSchemeItem(
            label = scheme.displayName,
            key = "color-scheme:${scheme.id.value}",
            id = scheme.id,
            scheme = scheme,
        )
    }

    fun pageAt(index: Int): SettingsPageContent {
        val pages = visibleSettingsPages().map { page ->
            when (page) {
                SettingsPage.Display -> SettingsPageContent(
                    page = page,
                    items = displayToggles().map { SettingsPageItem.Toggle(it) } +
                            displaySliders().map { SettingsPageItem.Slider(it) },
                )

                SettingsPage.Audio -> SettingsPageContent(
                    page = page,
                    items = audioToggles().map { SettingsPageItem.Toggle(it) } +
                            audioSliders().map { SettingsPageItem.Slider(it) },
                )

                SettingsPage.Interface -> SettingsPageContent(
                    page = page,
                    items = if (isAndroidPlatform()) {
                        listOf(SettingsPageItem.StorageLocation(model.storageType.value))
                    } else {
                        emptyList()
                    } + interfaceToggles().map { SettingsPageItem.Toggle(it) },
                )

                SettingsPage.Gameplay -> SettingsPageContent(
                    page = page,
                    items = gameplayToggles().map { SettingsPageItem.Toggle(it) },
                )

                SettingsPage.KeyBindings -> SettingsPageContent(
                    page = page,
                    items = emptyList(),
                )

                SettingsPage.ColorScheme -> SettingsPageContent(
                    page = page,
                    items = colorSchemeItems().map { item ->
                        SettingsPageItem.ColorSchemeSelector(item, model.selectedColorSchemeId.value == item.id)
                    },
                )
            }
        }
        val clamped = index.coerceIn(0, pages.lastIndex)
        return pages[clamped]
    }
}

internal fun visibleSettingsPages(): List<SettingsPage> =
    if (isAndroidPlatform()) {
        SettingsPage.entries.filterNot { it == SettingsPage.KeyBindings }
    } else {
        SettingsPage.entries.toList()
    }

private fun isAndroidPlatform(): Boolean = GameEngine.isAndroidPlatform()

private fun isPcPlatform(): Boolean = GameEngine.isPC()

sealed interface SettingsAction {
    data object PreviewChanges : SettingsAction
    data object ApplyChanges : SettingsAction
    data object Back : SettingsAction
}

sealed interface SettingsOutcome {
    data object PreviewChanges : SettingsOutcome
    data object ApplyChanges : SettingsOutcome
    data class Navigate(val screen: AppScreen) : SettingsOutcome
}

object SettingsNavigation {
    fun outcomeFor(action: SettingsAction): SettingsOutcome = when (action) {
        SettingsAction.PreviewChanges -> SettingsOutcome.PreviewChanges
        SettingsAction.ApplyChanges -> SettingsOutcome.ApplyChanges
        SettingsAction.Back -> SettingsOutcome.Navigate(AppScreen.MainMenu)
    }
}
