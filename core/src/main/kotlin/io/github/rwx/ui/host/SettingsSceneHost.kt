package io.github.rwx.ui.host

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.KeyBinding
import com.corrodinggames.rts.gameFramework.SettingsEngine
import com.corrodinggames.rts.gameFramework.file.FileHelper
import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import io.github.rwx.PlatformBridge
import io.github.rwx.input.KoolKeyCodeMapping
import io.github.rwx.ui.ResponsiveContentWidth
import io.github.rwx.ui.ResponsiveViewportHeight
import io.github.rwx.ui.UiTheme
import io.github.rwx.ui.component.*
import io.github.rwx.ui.model.*
import io.github.rwx.ui.remainingAfter
import org.koin.mp.KoinPlatform.getKoin
import com.corrodinggames.rts.gameFramework.InputController as LegacyInputController

class SettingsSceneHost(
    val model: SettingsModel = SettingsModel(),
    private val onAction: (SettingsAction) -> Unit = {},
) {
    private val viewModel = SettingsViewModel(model)
    private val currentPage: MutableStateValue<Int> = mutableStateOf(0)
    private val keyBindingRevision: MutableStateValue<Int> = mutableStateOf(0)
    private val activeKeyCapture: MutableStateValue<KeyBindingCaptureTarget?> = mutableStateOf(null)
    private val fallbackInputController: LegacyInputController = LegacyInputController().also { controller ->
        controller.a()
        SettingsEngine.getInstance().loadKeyBindingsInto(controller)
    }

    init {
        InputStack.defaultInputHandler.keyboardListeners += InputStack.KeyboardListener(::handleKeyBindingCapture)
    }

    fun dispatch(action: SettingsAction) = onAction(action)

    fun showPage(page: SettingsPage) {
        currentPage.value = page.ordinal
    }

    fun createScene(): Scene = UiScene(SETTINGS_SCENE_NAME) {
        addPanelSurface(PanelStyle.Menu, "settings-panel", model) { theme ->
            val metrics = settingsLayoutMetrics()

            val pageIndex = currentPage.use()
            val pageContent = viewModel.pageAt(pageIndex)
            val activeCapture = activeKeyCapture.use()
            keyBindingRevision.use()

            SettingsTabs(
                currentPage = pageContent.page,
                theme = theme,
                contentWidth = metrics.contentWidth,
                onBack = { dispatch(SettingsAction.Back) },
                onPageSelected = ::showPage,
            )

            val rows = settingsRows(pageContent, activeCapture)

            ScrollableVerticalList(
                items = rows,
                theme = theme,
                width = metrics.contentWidth,
                height = metrics.viewportHeight,
                isScrollByDrag = true,
                framed = true,
                contentPadding = UiTheme.Spacing.sm,
                bottomContentPadding = UiTheme.Spacing.lg,
            ) { row ->
                when (row) {
                    is SettingsScrollRow.SectionTitle -> SectionTitle(row.text, theme, metrics.rowWidth)
                    is SettingsScrollRow.BodyText -> BodyText(row.text, theme, metrics.rowWidth)
                    is SettingsScrollRow.Toggle -> {
                        val current = row.toggle.state.use()
                        SettingsToggleCard(
                            label = row.toggle.i18nText.text(),
                            checked = current,
                            theme = theme,
                            contentWidth = metrics.rowWidth,
                            icon = pageContent.page.settingsIcon,
                        ) {
                            handleToggleChange(row.toggle, it)
                        }
                    }

                    is SettingsScrollRow.Slider -> {
                        val current = row.slider.state.use()
                        SettingsSliderCard(
                            slider = row.slider,
                            value = current,
                            theme = theme,
                            contentWidth = metrics.rowWidth,
                            icon = pageContent.page.settingsIcon,
                            onChanged = {
                                row.slider.state.value = row.slider.normalize(it)
                                dispatch(SettingsAction.PreviewChanges)
                            },
                            // Kool's drag-end event can report the drag-start value after the
                            // pointer has been released. The latest onChanged value is already in
                            // the model, so only persist it here instead of overwriting it.
                            onChangeEnd = { dispatch(SettingsAction.ApplyChanges) },
                        )
                    }

                    is SettingsScrollRow.KeyBinding -> {
                        SettingsKeyBindingCard(
                            row = row.row,
                            theme = theme,
                            contentWidth = metrics.rowWidth,
                            onBind = { slot -> activeKeyCapture.value = KeyBindingCaptureTarget(row.row.index, slot) },
                            onClear = { slot -> clearKeyBinding(row.row.index, slot) },
                        )
                    }

                    is SettingsScrollRow.ColorSchemeSelector -> {
                        SettingsColorSchemeCard(row.item, row.selected, theme, metrics.rowWidth) {
                            model.selectedColorSchemeId.value = row.item.id
                            dispatch(SettingsAction.ApplyChanges)
                        }
                    }

                    is SettingsScrollRow.StorageLocation -> {
                        SettingsStorageLocationCard(
                            selected = AndroidStoragePreference.fromStorageType(row.selectedType),
                            theme = theme,
                            contentWidth = metrics.rowWidth,
                            onSelected = ::handleStorageLocationChange,
                        )
                    }
                }
            }

        }
    }

    private fun settingsRows(
        pageContent: SettingsPageContent,
        activeCapture: KeyBindingCaptureTarget?,
    ): List<SettingsScrollRow> {
        val sectionTitle = when (pageContent.page) {
            SettingsPage.Display -> DISPLAY_SECTION_TITLE
            SettingsPage.Audio -> AUDIO_SECTION_TITLE
            SettingsPage.Interface -> INTERFACE_SECTION_TITLE
            SettingsPage.Gameplay -> GAMEPLAY_SECTION_TITLE
            SettingsPage.KeyBindings -> KEY_BINDINGS_SECTION_TITLE
            SettingsPage.ColorScheme -> COLOR_SCHEME_SECTION_TITLE
        }
        val rows = mutableListOf<SettingsScrollRow>(SettingsScrollRow.SectionTitle(sectionTitle))

        when (pageContent.page) {
            SettingsPage.Display, SettingsPage.Audio, SettingsPage.Interface, SettingsPage.Gameplay -> {
                pageContent.items.forEach { item ->
                    when (item) {
                        is SettingsPageItem.Toggle -> rows.add(SettingsScrollRow.Toggle(item.toggle))
                        is SettingsPageItem.Slider -> rows.add(SettingsScrollRow.Slider(item.slider))
                        is SettingsPageItem.ColorSchemeSelector -> Unit
                        is SettingsPageItem.StorageLocation -> rows.add(
                            SettingsScrollRow.StorageLocation(item.selectedType)
                        )
                    }
                }
            }

            SettingsPage.KeyBindings -> {
                rows.add(SettingsScrollRow.BodyText(KEY_BINDINGS_HINT))
                val controller = keyBindingController()
                keyBindingRows(controller, activeCapture).forEach { row ->
                    if (row.keyBinding.d()) {
                        rows.add(SettingsScrollRow.SectionTitle(row.keyBinding.name))
                    } else {
                        rows.add(SettingsScrollRow.KeyBinding(row))
                    }
                }
            }

            SettingsPage.ColorScheme -> {
                rows.add(SettingsScrollRow.BodyText(COLOR_SCHEME_HINT))
                pageContent.items.filterIsInstance<SettingsPageItem.ColorSchemeSelector>().forEach { selectorItem ->
                    rows.add(SettingsScrollRow.ColorSchemeSelector(selectorItem.item, selectorItem.selected))
                }
            }
        }
        return rows
    }

    private fun handleToggleChange(toggle: SettingToggle, requestedValue: Boolean) {
        toggle.state.value = requestedValue
        dispatch(SettingsAction.ApplyChanges)
    }

    private fun handleStorageLocationChange(preference: AndroidStoragePreference) {
        if (preference == AndroidStoragePreference.External) {
            val host=getKoin().get<PlatformBridge>().filePickerHost?:return
            host.requestExternalStorage { selection ->
                if (selection == null) return@requestExternalStorage
                val engine = GameEngine.getInstance() ?: return@requestExternalStorage
                val settings = engine.settingsEngine ?: return@requestExternalStorage
                settings.externalSAFLink = selection.uri
                settings.externalSAFPathShown = selection.displayPath
                settings.externalSAFPathExtra = ""
                if (!settings.loadMainExternalFolder(false)) return@requestExternalStorage
                applyStoragePreference(preference, force = true)
            }
            return
        }
        applyStoragePreference(preference)
    }

    private fun applyStoragePreference(
        preference: AndroidStoragePreference,
        force: Boolean = false,
    ) {
        if (!force && model.storageType.value == preference.storageType) return
        model.storageType.value = preference.storageType
        dispatch(SettingsAction.ApplyChanges)
        if (GameEngine.getInstance() != null) {
            FileHelper.initialize()
            GameEngine.getInstance()?.modManager?.let { manager ->
                runCatching {
                    manager.loadAllMods()
                    manager.loadModSelection()
                }
            }
        }
    }

    private fun handleKeyBindingCapture(keyEvents: List<KeyEvent>, @Suppress("UNUSED_PARAMETER") ctx: KoolContext) {
        val target = activeKeyCapture.value ?: return
        for (event in keyEvents) {
            if (event.isConsumed || event.isCharTyped || event.isReleased) {
                continue
            }
            if (event.isRepeated) {
                event.isConsumed = true
                continue
            }
            val keyCode = KoolKeyCodeMapping.androidKeyCode(event) ?: continue
            event.isConsumed = true
            if (event.keyCode == KeyboardInput.KEY_ESC) {
                activeKeyCapture.value = null
                return
            }
            if (KoolKeyCodeMapping.isModifierOnly(event)) {
                return
            }
            val controller = keyBindingController()
            val keyBinding = controller.al.getOrNull(target.bindingIndex) ?: return
            keyBinding.a(keyCode, target.slot, KoolKeyCodeMapping.modifierMask(event), true)
            saveKeyBindings(controller)
            activeKeyCapture.value = null
            keyBindingRevision.value += 1
            return
        }
    }

    private fun clearKeyBinding(bindingIndex: Int, slot: Int) {
        val controller = keyBindingController()
        val keyBinding = controller.al.getOrNull(bindingIndex) ?: return
        keyBinding.a(0, slot, 0, true)
        saveKeyBindings(controller)
        activeKeyCapture.value = null
        keyBindingRevision.value += 1
    }

    private fun saveKeyBindings(controller: LegacyInputController) {
        SettingsEngine.getInstance().saveKeyBindingsFromInputController(controller)
        dispatch(SettingsAction.ApplyChanges)
    }

    private fun keyBindingController(): LegacyInputController =
        GameEngine.getInstance()?.inputController ?: fallbackInputController

    private fun keyBindingRows(
        controller: LegacyInputController,
        activeCapture: KeyBindingCaptureTarget?,
    ): List<SettingKeyBindingRow> {
        return controller.al.mapIndexedNotNull { index, keyBinding ->
            if (!keyBinding.isDefault) return@mapIndexedNotNull null
            SettingKeyBindingRow(
                index = index,
                keyBinding = keyBinding,
                primaryText = keyBinding.b(PRIMARY_KEY_SLOT),
                secondaryText = keyBinding.b(SECONDARY_KEY_SLOT),
                primaryOverlaps = hasOverlappingBinding(controller, keyBinding, PRIMARY_KEY_SLOT),
                secondaryOverlaps = hasOverlappingBinding(controller, keyBinding, SECONDARY_KEY_SLOT),
                activeSlot = activeCapture?.takeIf { it.bindingIndex == index }?.slot,
            )
        }
    }

    private fun hasOverlappingBinding(
        controller: LegacyInputController,
        keyBinding: KeyBinding,
        slot: Int,
    ): Boolean {
        val binding = keyBinding.a(slot) ?: return false
        if (binding.d()) return false
        for (other in controller.al) {
            if (other === keyBinding) continue
            for (otherBinding in other.bindings) {
                if (!otherBinding.d() && binding.a(otherBinding)) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        const val SETTINGS_SCENE_NAME: String = "settings"
        const val DISPLAY_SECTION_TITLE: String = "Display & Input"
        const val AUDIO_SECTION_TITLE: String = "Audio"
        const val INTERFACE_SECTION_TITLE: String = "Interface"
        const val GAMEPLAY_SECTION_TITLE: String = "Gameplay"
        const val KEY_BINDINGS_SECTION_TITLE: String = "Key Bindings"
        const val KEY_BINDINGS_HINT: String =
            "Click a binding slot, then press the replacement key. Escape cancels capture."
        const val COLOR_SCHEME_SECTION_TITLE: String = "Color Scheme"
        const val COLOR_SCHEME_HINT: String = "Color scheme changes apply immediately."
        private const val PRIMARY_KEY_SLOT: Int = 0
        private const val SECONDARY_KEY_SLOT: Int = 1
    }
}

private data class KeyBindingCaptureTarget(
    val bindingIndex: Int,
    val slot: Int,
)

private data class SettingsLayoutMetrics(
    val contentWidth: Dp,
    val rowWidth: Dp,
    val viewportHeight: Dp,
)

private fun UiScope.settingsLayoutMetrics(): SettingsLayoutMetrics {
    val contentWidth = ResponsiveContentWidth(
        defaultWidth = UiTheme.Layout.settingsContentWidth,
        minWidth = UiTheme.Layout.settingsMinContentWidth,
        maxWidth = UiTheme.Layout.settingsMaxContentWidth,
    )
    val viewportPadding = UiTheme.Spacing.sm
    return SettingsLayoutMetrics(
        contentWidth = contentWidth,
        rowWidth = contentWidth.remainingAfter(Dp(viewportPadding.value * 2f), UiTheme.Layout.settingsMinContentWidth),
        viewportHeight = ResponsiveViewportHeight(
            defaultHeight = UiTheme.Layout.settingsViewportHeight,
            minHeight = UiTheme.Layout.settingsMinViewportHeight,
            maxHeight = UiTheme.Layout.settingsMaxViewportHeight,
            // Tabs plus the menu surface's inner / outer padding must remain inside the viewport.
            verticalChrome = Dp(160f),
        ),
    )
}
