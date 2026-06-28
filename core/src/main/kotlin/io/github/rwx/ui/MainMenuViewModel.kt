package io.github.rwx.ui

import io.github.rwx.i18n.I18n

/**
 * The main-menu actions shown by the top-level launcher. Single-player map modes live behind the
 * shared level-select browser instead of separate top-level buttons.
 */
enum class MainMenuAction(val text: () -> String) {
    Continue({ I18n.mainmenu.`continue`() }),
    SinglePlayer({ I18n.mainmenu.singlePlayer() }),
    WatchReplay({ I18n.mainmenu.watchReplay() }),
    Sandbox({ I18n.singleplayer.sandbox() }),
    Multiplayer({ I18n.mainmenu.multiplayer() }),
    P2PMultiplayer({ I18n.mainmenu.p2pMultiplayer() }),
    Mods({ I18n.mainmenu.mods() }),
    ResourceBrowser({ I18n.mainmenu.resourceBrowser() }),
    Settings({ I18n.mainmenu.settings() }),
    About({ I18n.mainmenu.about() }),
    Exit({ I18n.mainmenu.exit() }),
}

/**
 * Conditions that drive which conditional buttons are shown: Continue only when a game can FastArrayList
 * resumed. [usingMods] and [isDesktop] are retained for callers that need platform / mod context;
 * About and Exit are now fixed footer actions on every platform.
 */
data class MainMenuConditions(
    val canResume: Boolean = false,
    val usingMods: Boolean = false,
    val isDesktop: Boolean = false,
)

/** One rendered menu entry: the action plus its display label. */
data class MainMenuItem(
    val action: MainMenuAction,
    val label: String,
    val badge: String,
)

object MainMenuViewModel {
    /** Builds the primary launcher items shown inside the scrollable menu. */
    fun primaryItems(conditions: MainMenuConditions): List<MainMenuItem> = buildList {
        if (conditions.canResume) add(MainMenuAction.Continue)
        add(MainMenuAction.SinglePlayer)
        add(MainMenuAction.WatchReplay)
        add(MainMenuAction.Sandbox)
        add(MainMenuAction.Multiplayer)
        add(MainMenuAction.Mods)
        add(MainMenuAction.ResourceBrowser)
        add(MainMenuAction.Settings)
    }.map(::menuItem)

    /** About and Exit stay visible below the scrollable launcher, including on Android. */
    fun footerItems(): List<MainMenuItem> = listOf(
        MainMenuAction.About,
        MainMenuAction.Exit,
    ).map(::menuItem)

    /** Complete visible menu model, retained for scene-host and existing callers. */
    fun items(conditions: MainMenuConditions): List<MainMenuItem> =
        primaryItems(conditions) + footerItems()

    private fun menuItem(action: MainMenuAction): MainMenuItem =
        MainMenuItem(
            action = action,
            label = action.text(),
            badge = action.badge,
        )

    private val MainMenuAction.badge: String
        get() = when (this) {
            MainMenuAction.Continue -> "GO"
            MainMenuAction.SinglePlayer -> "SP"
            MainMenuAction.WatchReplay -> "REP"
            MainMenuAction.Sandbox -> "SAN"
            MainMenuAction.Multiplayer -> "MP"
            MainMenuAction.P2PMultiplayer -> "P2P"
            MainMenuAction.Settings -> "SET"
            MainMenuAction.Mods -> "MOD"
            MainMenuAction.ResourceBrowser -> "RES"
            MainMenuAction.About -> "INFO"
            MainMenuAction.Exit -> "EXIT"
        }
}
