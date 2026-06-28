package io.github.rwx.app

import io.github.rwx.ui.*

data class AppOptions(
    val initialScreen: AppScreen = AppScreen.MainMenu,
    val colorSchemeId: ColorSchemeId = ColorSchemeRegistry.defaultSchemeId,
    val levelSelectMode: LevelSelectMode? = null,
    val showDemoDialog: Boolean = false,
    val settingsPage: SettingsPage? = null,
    val autoStartSinglePlayerFromUi: Boolean = false,
    val autoStartBattleRoom: Boolean = false,
    val autoStartBattleRoomTwice: Boolean = false,
    val autoReturnMainMenuAfterGameReady: Boolean = false,
    val joinServer: String? = null,
    val isDesktop: Boolean = false,
) {
    companion object {
        private const val SCREEN_PREFIX = "--screen="
        private const val COLOR_SCHEME_PREFIX = "--color-scheme="
        private const val LEVEL_MODE_PREFIX = "--level-mode="
        private const val DEMO_DIALOG_ARG = "--demo-dialog"
        private const val SETTINGS_PAGE_PREFIX = "--settings-page="
        private const val AUTO_START_SINGLE_PLAYER_UI_ARG = "--auto-start-singleplayer-ui"
        private const val AUTO_START_BATTLE_ROOM_ARG = "--auto-start-battleroom"
        private const val AUTO_START_BATTLE_ROOM_TWICE_ARG = "--auto-start-battleroom-twice"
        private const val AUTO_RETURN_MAIN_MENU_AFTER_GAME_READY_ARG = "--auto-return-main-menu-after-game-ready"
        private const val JOIN_SERVER_PREFIX = "--join-server="

        private val screensByArgument = mapOf(
            "main-menu" to AppScreen.MainMenu,
            "single-player" to AppScreen.MainMenu,
            "level-select" to AppScreen.LevelSelect,
            "replay-select" to AppScreen.ReplaySelect,
            "replays" to AppScreen.ReplaySelect,
            "settings" to AppScreen.Settings,
            "loading" to AppScreen.Loading,
            "paused" to AppScreen.Paused,
            "in-game" to AppScreen.InGame,
            "multiplayer" to AppScreen.Multiplayer,
            "mods" to AppScreen.Mods,
            "resource-browser" to AppScreen.ResourceBrowser,
            "resources" to AppScreen.ResourceBrowser,
            "battleroom" to AppScreen.BattleRoom,
        )
        private val levelModesByArgument = mapOf(
            "campaign" to LevelSelectMode.Campaign,
            "skirmish" to LevelSelectMode.Skirmish,
            "challenge" to LevelSelectMode.Challenge,
            "survival" to LevelSelectMode.Survival,
            "custom" to LevelSelectMode.CustomMaps,
            "custom-maps" to LevelSelectMode.CustomMaps,
        )
        private val settingsPagesByArgument = mapOf(
            "display" to SettingsPage.Display,
            "audio" to SettingsPage.Audio,
            "interface" to SettingsPage.Interface,
            "gameplay" to SettingsPage.Gameplay,
            "keys" to SettingsPage.KeyBindings,
            "keybindings" to SettingsPage.KeyBindings,
            "key-bindings" to SettingsPage.KeyBindings,
            "color-scheme" to SettingsPage.ColorScheme,
            "colors" to SettingsPage.ColorScheme,
        )

        fun parseArgs(args: Array<String>, isDesktop: Boolean = false): AppOptions {
            var initialScreen = AppScreen.MainMenu
            var colorSchemeId = ColorSchemeRegistry.defaultSchemeId
            var levelSelectMode: LevelSelectMode? = null
            var showDemoDialog = false
            var settingsPage: SettingsPage? = null
            var autoStartSinglePlayerFromUi = false
            var autoStartBattleRoom = false
            var autoStartBattleRoomTwice = false
            var autoReturnMainMenuAfterGameReady = false
            var joinServer: String? = null

            args.forEach { arg ->
                when {
                    arg.startsWith(SCREEN_PREFIX) -> initialScreen = parseScreen(arg.removePrefix(SCREEN_PREFIX))
                    arg.startsWith(COLOR_SCHEME_PREFIX) -> {
                        colorSchemeId = parseColorScheme(arg.removePrefix(COLOR_SCHEME_PREFIX))
                    }

                    arg.startsWith(LEVEL_MODE_PREFIX) -> {
                        levelSelectMode = parseLevelMode(arg.removePrefix(LEVEL_MODE_PREFIX))
                    }

                    arg == DEMO_DIALOG_ARG -> showDemoDialog = true
                    arg == AUTO_START_SINGLE_PLAYER_UI_ARG -> autoStartSinglePlayerFromUi = true
                    arg == AUTO_START_BATTLE_ROOM_ARG -> autoStartBattleRoom = true
                    arg == AUTO_START_BATTLE_ROOM_TWICE_ARG -> {
                        autoStartBattleRoom = true
                        autoStartBattleRoomTwice = true
                    }

                    arg == AUTO_RETURN_MAIN_MENU_AFTER_GAME_READY_ARG -> autoReturnMainMenuAfterGameReady = true
                    arg.startsWith(JOIN_SERVER_PREFIX) -> {
                        joinServer = arg.removePrefix(JOIN_SERVER_PREFIX).trim().also {
                            require(it.isNotEmpty()) { "Server address must not FastArrayList empty" }
                        }
                    }

                    arg.startsWith(SETTINGS_PAGE_PREFIX) -> {
                        settingsPage = parseSettingsPage(arg.removePrefix(SETTINGS_PAGE_PREFIX))
                    }

                    else -> throw IllegalArgumentException("Unsupported Kool app argument: $arg")
                }
            }

            return AppOptions(
                initialScreen = initialScreen,
                colorSchemeId = colorSchemeId,
                levelSelectMode = levelSelectMode,
                showDemoDialog = showDemoDialog,
                settingsPage = settingsPage,
                autoStartSinglePlayerFromUi = autoStartSinglePlayerFromUi,
                autoStartBattleRoom = autoStartBattleRoom,
                autoStartBattleRoomTwice = autoStartBattleRoomTwice,
                autoReturnMainMenuAfterGameReady = autoReturnMainMenuAfterGameReady,
                joinServer = joinServer,
                isDesktop = isDesktop,
            )
        }

        private fun parseScreen(value: String): AppScreen = requireNotNull(screensByArgument[value]) {
            "Unsupported Kool app screen: $value"
        }

        private fun parseColorScheme(value: String): ColorSchemeId {
            val id = ColorSchemeId(value)
            require(ColorSchemeRegistry.schemes.any { it.id == id }) {
                "Unsupported Kool app color scheme: $value"
            }
            return id
        }

        private fun parseLevelMode(value: String): LevelSelectMode = requireNotNull(levelModesByArgument[value]) {
            "Unsupported Kool app level mode: $value"
        }

        private fun parseSettingsPage(value: String): SettingsPage = requireNotNull(settingsPagesByArgument[value]) {
            "Unsupported Kool app settings page: $value"
        }
    }
}
