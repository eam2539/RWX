package io.github.rwx.ui


data class ModEntry(
    val id: String,
    val name: String,
    val isEnabled: Boolean,
    val description: String = "",
    /** Human-readable RAM usage, e.g. "1.2 MB"; blank when unknown. */
    val ramLabel: String = "",
    /** Load error to surface on the card; null when the mod loaded cleanly. */
    val errorMessage: String? = null,
)


data class ModsListModel(
    val mods: List<ModEntry>,
    val filter: String = "",
    val statusText: String = "",
) {
    /** Mods matching the current filter (case-insensitive name match); all mods when filter blank. */
    val filtered: List<ModEntry>
        get() = filter.trim().let { needle ->
            if (needle.isEmpty()) mods
            else mods.filter { it.name.contains(needle, ignoreCase = true) }
        }

    /** Filtered mods that are currently enabled. */
    val enabled: List<ModEntry> get() = filtered.filter { it.isEnabled }

    /** Filtered mods that are currently disabled. */
    val disabled: List<ModEntry> get() = filtered.filter { !it.isEnabled }

    /** True when no mods are installed at all (drives the empty-state message). */
    val isEmpty: Boolean get() = mods.isEmpty()
}


sealed interface ModsAction {
    /** Discard navigation and return to the main menu. */
    data object Back : ModsAction

    /** Re-scan the mod source from disk. */
    data object Reload : ModsAction

    /** Open a file chooser to import a mod archive. */
    data object ImportFile : ModsAction

    /** Disable every mod. */
    data object DisableAll : ModsAction

    /** Persist enable/disable changes and leave the screen. */
    data object Apply : ModsAction

    /** Flip the enabled state of one mod. */
    data class ToggleEnable(val modId: String) : ModsAction

    /** Remove one mod from disk. */
    data class Delete(val modId: String) : ModsAction
}

sealed interface ModsOutcome {
    data class Navigate(val screen: AppScreen) : ModsOutcome
    data object ReloadRequested : ModsOutcome
    data object ImportRequested : ModsOutcome
    data object DisableAllRequested : ModsOutcome
    data object ApplyRequested : ModsOutcome
    data class ToggleEnable(val modId: String) : ModsOutcome
    data class Delete(val modId: String) : ModsOutcome
}


sealed interface ModsScrollRow {
    data class SectionTitle(val text: String) : ModsScrollRow
    data class Card(val mod: ModEntry) : ModsScrollRow
    data class Empty(val text: String) : ModsScrollRow
}

object ModsScrollRows {
    fun from(
        model: ModsListModel,
        enabledTitle: String,
        disabledTitle: String,
        emptyText: String,
    ): List<ModsScrollRow> = buildList {
        if (model.isEmpty) {
            add(ModsScrollRow.Empty(emptyText))
            return@buildList
        }
        add(ModsScrollRow.SectionTitle(enabledTitle))
        model.enabled.forEach { add(ModsScrollRow.Card(it)) }
        add(ModsScrollRow.SectionTitle(disabledTitle))
        model.disabled.forEach { add(ModsScrollRow.Card(it)) }
    }
}

object ModsNavigation {
    fun outcomeFor(action: ModsAction): ModsOutcome = when (action) {
        ModsAction.Back -> ModsOutcome.Navigate(AppScreen.MainMenu)
        ModsAction.Reload -> ModsOutcome.ReloadRequested
        ModsAction.ImportFile -> ModsOutcome.ImportRequested
        ModsAction.DisableAll -> ModsOutcome.DisableAllRequested
        ModsAction.Apply -> ModsOutcome.ApplyRequested
        is ModsAction.ToggleEnable -> ModsOutcome.ToggleEnable(action.modId)
        is ModsAction.Delete -> ModsOutcome.Delete(action.modId)
    }
}
