package io.github.rwx.ui.host

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import io.github.rwx.i18n.I18n
import io.github.rwx.ui.component.BodyText
import io.github.rwx.ui.component.PanelStyle
import io.github.rwx.ui.ResponsiveContentWidth
import io.github.rwx.ui.ResponsiveViewportHeight
import io.github.rwx.ui.component.ScrollableVerticalList
import io.github.rwx.ui.model.SettingsModel
import io.github.rwx.ui.UiTheme
import io.github.rwx.ui.component.addPanelSurface
import io.github.rwx.ui.component.ModCard
import io.github.rwx.ui.component.ModsActionBar
import io.github.rwx.ui.component.ModsActionCallbacks
import io.github.rwx.ui.component.ModsActionLabels
import io.github.rwx.ui.component.ModsFilterField
import io.github.rwx.ui.component.ModsSectionHeader
import io.github.rwx.ui.model.ModEntry
import io.github.rwx.ui.model.ModsAction
import io.github.rwx.ui.model.ModsListModel
import io.github.rwx.ui.model.ModsScrollRow
import io.github.rwx.ui.model.ModsScrollRows


class ModsSceneHost(
    private val model: SettingsModel = SettingsModel(),
    private val onAction: (ModsAction) -> Unit = {},
) {
    private val mods = mutableStateListOf<ModEntry>()
    private val filter: MutableStateValue<String> = mutableStateOf("")
    private var statusText: String = ""

    /** Replaces the displayed mod list (platform-supplied), e.g. after a reload. */
    fun updateMods(mods: List<ModEntry>, statusText: String = "") {
        this.statusText = statusText
        this.mods.atomic {
            clear()
            addAll(mods)
        }
    }

    fun dispatch(action: ModsAction) = onAction(action)

    private fun setFilter(text: String) {
        filter.value = text
    }

    private fun toggleEnable(id: String) {
        val updated = mods.map { if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it }
        mods.atomic { clear(); addAll(updated) }
        dispatch(ModsAction.ToggleEnable(id))
    }

    private fun deleteMod(id: String) {
        val updated = mods.filterNot { it.id == id }
        mods.atomic { clear(); addAll(updated) }
        dispatch(ModsAction.Delete(id))
    }

    private fun disableAll() {
        val updated = mods.map { it.copy(isEnabled = false) }
        mods.atomic { clear(); addAll(updated) }
        dispatch(ModsAction.DisableAll)
    }

    fun createScene(): Scene = UiScene(MODS_SCENE_NAME) {
        addPanelSurface(PanelStyle.Menu, "rwx-mods-panel", model) { theme ->
            val metrics = modsLayoutMetrics()

            ModsFilterField(
                filter = filter.use(),
                hint = I18n.mods.filter(),
                theme = theme,
                contentWidth = metrics.contentWidth,
                onBack = { dispatch(ModsAction.Back) },
                onChange = ::setFilter,
            )

            val listModel = ModsListModel(mods.use(), filter.use(), statusText)
            val rows = ModsScrollRows.from(
                model = listModel,
                enabledTitle = I18n.mods.enabled(),
                disabledTitle = I18n.mods.disabled(),
                emptyText = statusText.ifBlank { I18n.mods.empty() },
            )

            ScrollableVerticalList(
                items = rows,
                theme = theme,
                width = metrics.contentWidth,
                height = metrics.viewportHeight,
            ) { row ->
                when (row) {
                    is ModsScrollRow.SectionTitle -> ModsSectionHeader(row.text, theme, metrics.contentWidth)
                    is ModsScrollRow.Empty -> BodyText(row.text, theme, metrics.contentWidth)
                    is ModsScrollRow.Card -> {
                        val mod = row.mod
                        ModCard(
                            mod = mod,
                            theme = theme,
                            toggleLabel = if (mod.isEnabled) I18n.mods.disable() else I18n.mods.enable(),
                            deleteLabel = I18n.mods.delete(),
                            ramText = if (mod.ramLabel.isBlank()) "" else I18n.mods.ramUsed(mod.ramLabel),
                            contentWidth = metrics.contentWidth,
                            onToggle = { toggleEnable(mod.id) },
                            onDelete = { deleteMod(mod.id) },
                        )
                    }
                }
            }

            ModsActionBar(
                labels = ModsActionLabels(
                    reload = I18n.mods.reload(),
                    importFile = I18n.mods.importFile(),
                    disableAll = I18n.mods.disableAll(),
                    apply = I18n.mods.apply(),
                ),
                theme = theme,
                contentWidth = metrics.contentWidth,
                callbacks = ModsActionCallbacks(
                    onReload = { dispatch(ModsAction.Reload) },
                    onImport = { dispatch(ModsAction.ImportFile) },
                    onDisableAll = { disableAll() },
                    onApply = { dispatch(ModsAction.Apply) },
                ),
            )

        }
    }

    companion object {
        const val MODS_SCENE_NAME: String = "rwx-mods"
    }
}

private data class ModsLayoutMetrics(
    val contentWidth: Dp,
    val viewportHeight: Dp,
)

private fun UiScope.modsLayoutMetrics(): ModsLayoutMetrics =
    ModsLayoutMetrics(
        contentWidth = ResponsiveContentWidth(
            defaultWidth = UiTheme.Layout.modsContentWidth,
            minWidth = UiTheme.Layout.modsMinContentWidth,
            maxWidth = UiTheme.Layout.modsMaxContentWidth,
        ),
        viewportHeight = ResponsiveViewportHeight(
            defaultHeight = UiTheme.Layout.modsViewportHeight,
            minHeight = UiTheme.Layout.modsMinViewportHeight,
            maxHeight = UiTheme.Layout.modsMaxViewportHeight,
            verticalChrome = Dp(192f),
        ),
    )
