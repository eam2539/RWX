package io.github.rwx.ui

/**
 * The top-level screens the app can show. Neutral screen state — no gameplay, no rendering. The
 * render backend is driven separately; this only decides what UI layer is shown.
 */
enum class AppScreen {
    MainMenu,
    LevelSelect,
    ReplaySelect,
    Settings,
    Loading,
    Paused,
    InGame,
    Multiplayer,
    Mods,
    ResourceBrowser,
    BattleRoom,
    ModWindow,
}

/** Which rendering layers are shown on a given [AppScreen]. Plain data so the policy is testable. */
data class ScreenVisibility(
    val world: Boolean,
    val hud: Boolean,
    val mainMenu: Boolean,
    val levelSelect: Boolean,
    val replaySelect: Boolean = false,
    val settings: Boolean,
    val loading: Boolean = false,
    val paused: Boolean,
    val multiplayer: Boolean = false,
    val mods: Boolean = false,
    val resourceBrowser: Boolean = false,
    val battleRoom: Boolean = false,
    val modWindow: Boolean = false,
)

/**
 * Maps each [AppScreen] to the layers it shows. The game frame stays available behind menu screens
 * when the backend provides one; the HUD and menu overlay swap in and out. A host turns this into
 * Kool scene visibility — this object owns no Kool types.
 */
object AppScreenLayout {
    fun visibilityFor(screen: AppScreen): ScreenVisibility = when (screen) {
        AppScreen.MainMenu -> ScreenVisibility(
            world = true,
            hud = false,
            mainMenu = true,
            levelSelect = false,
            replaySelect = false,
            settings = false,
            paused = false,
            multiplayer = false
        )

        AppScreen.LevelSelect -> ScreenVisibility(
            world = true,
            hud = false,
            mainMenu = false,
            levelSelect = true,
            replaySelect = false,
            settings = false,
            paused = false,
            multiplayer = false
        )

        AppScreen.ReplaySelect -> ScreenVisibility(
            world = true,
            hud = false,
            mainMenu = false,
            levelSelect = false,
            replaySelect = true,
            settings = false,
            paused = false,
            multiplayer = false
        )

        AppScreen.Settings -> ScreenVisibility(
            world = true,
            hud = false,
            mainMenu = false,
            levelSelect = false,
            replaySelect = false,
            settings = true,
            loading = false,
            paused = false,
            multiplayer = false
        )

        AppScreen.Loading -> ScreenVisibility(
            world = false,
            hud = false,
            mainMenu = false,
            levelSelect = false,
            replaySelect = false,
            settings = false,
            loading = true,
            paused = false,
            multiplayer = false
        )

        AppScreen.Paused -> ScreenVisibility(
            world = true,
            hud = true,
            mainMenu = false,
            levelSelect = false,
            replaySelect = false,
            settings = false,
            loading = false,
            paused = true,
            multiplayer = false
        )

        AppScreen.InGame -> ScreenVisibility(
            world = true,
            hud = true,
            mainMenu = false,
            levelSelect = false,
            replaySelect = false,
            settings = false,
            loading = false,
            paused = false,
            multiplayer = false
        )

        AppScreen.Multiplayer -> ScreenVisibility(
            world = true,
            hud = false,
            mainMenu = false,
            levelSelect = false,
            replaySelect = false,
            settings = false,
            loading = false,
            paused = false,
            multiplayer = true
        )

        AppScreen.Mods -> ScreenVisibility(
            world = true,
            hud = false,
            mainMenu = false,
            levelSelect = false,
            replaySelect = false,
            settings = false,
            loading = false,
            paused = false,
            multiplayer = false,
            mods = true
        )

        AppScreen.BattleRoom -> ScreenVisibility(
            world = true,
            hud = false,
            mainMenu = false,
            levelSelect = false,
            replaySelect = false,
            settings = false,
            loading = false,
            paused = false,
            multiplayer = false,
            mods = false,
            battleRoom = true
        )

        AppScreen.ResourceBrowser -> ScreenVisibility(
            world = true,
            hud = false,
            mainMenu = false,
            levelSelect = false,
            replaySelect = false,
            settings = false,
            loading = false,
            paused = false,
            multiplayer = false,
            mods = false,
            resourceBrowser = true,
            battleRoom = false
        )

        AppScreen.ModWindow -> ScreenVisibility(
            world = true,
            hud = false,
            mainMenu = false,
            levelSelect = false,
            replaySelect = false,
            settings = false,
            loading = false,
            paused = false,
            multiplayer = false,
            mods = false,
            resourceBrowser = false,
            battleRoom = false,
            modWindow = true,
        )
    }
}

/** Which live input a given [AppScreen] accepts. Plain data so the policy is testable. */
data class ScreenInput(
    val worldInteraction: Boolean,
)

/**
 * Maps each [AppScreen] to the input it accepts. On the menu the world is drawn but not
 * interactive, so world clicks (selection/move) and camera pan/zoom are off — only the menu UI
 * reacts; in game they are on. A host gates its live pointer/camera input on this — this object
 * owns no platform or input types.
 */
object AppScreenInput {
    fun policyFor(screen: AppScreen): ScreenInput = when (screen) {
        AppScreen.MainMenu,
        AppScreen.LevelSelect,
        AppScreen.ReplaySelect,
        AppScreen.Settings,
        AppScreen.Loading,
        AppScreen.Multiplayer,
        AppScreen.Mods,
        AppScreen.ResourceBrowser,
        AppScreen.BattleRoom,
        AppScreen.ModWindow,
        AppScreen.Paused -> ScreenInput(worldInteraction = false)

        AppScreen.InGame -> ScreenInput(worldInteraction = true)
    }
}


class ScreenNavigator(
    initialScreen: AppScreen = AppScreen.MainMenu,
    private val onScreenChanged: (AppScreen) -> Unit = {},
) {
    var current: AppScreen = initialScreen
        private set

    fun navigateTo(screen: AppScreen) {
        if (screen != current) {
            current = screen
            onScreenChanged(screen)
        }
    }
}
