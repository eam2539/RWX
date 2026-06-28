package io.github.rwx.settings

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.SettingsEngine
import io.github.rwx.PREFERENCE_NAME
import io.github.rwx.Preference
import io.github.rwx.PreferenceStorage
import io.github.rwx.ui.ColorSchemeId
import io.github.rwx.ui.ColorSchemeRegistry
import io.github.rwx.ui.model.SettingsModel

const val KEY_ANDROID_OPENGL_RENDERER = "newRender"

class GameSettingsRepository(
    private val preferenceStorage: PreferenceStorage,
) {
    private val preferences: Preference
        get() = preferenceStorage.preference(PREFERENCE_NAME)

    fun loadInto(model: SettingsModel) {
        val live = GameEngine.getInstance()?.settingsEngine
        val prefs = preferences
        model.batterySaving.value = live?.batterySaving ?: prefs.getBoolean(KEY_BATTERY_SAVING, false)
        model.highRefreshRate.value =
            live?.highRefreshRate ?: prefs.getBoolean(KEY_HIGH_REFRESH_RATE, GameEngine.isPC())
        model.slick2dFullScreen.value =
            live?.slick2dFullScreen ?: prefs.getBoolean(KEY_SLICK2D_FULL_SCREEN, GameEngine.isPC())
        model.vsync.value = live?.renderVsync ?: prefs.getBoolean(KEY_RENDER_VSYNC, false)
        model.showUnitHp.value = live?.showHp ?: prefs.getBoolean(KEY_SHOW_HP, true)
        model.showWaypoints.value = live?.showUnitWaypoints ?: prefs.getBoolean(KEY_SHOW_UNIT_WAYPOINTS, true)
        model.showZoomButton.value = live?.showZoomButton ?: prefs.getBoolean(KEY_SHOW_ZOOM_BUTTON, true)
        model.showFps.value = live?.showFps ?: prefs.getBoolean(KEY_SHOW_FPS, false)
        model.renderClouds.value = live?.renderClouds ?: prefs.getBoolean(KEY_RENDER_CLOUDS, false)
        model.renderDoubleScale.value = live?.renderDoubleScale ?: prefs.getBoolean(KEY_RENDER_DOUBLE_SCALE, false)
        model.softFogFading.value = live?.softFogFading ?: prefs.getBoolean(KEY_SOFT_FOG_FADING, false)
        model.shaderEffects.value = live?.shaderEffects ?: prefs.getBoolean(KEY_SHADER_EFFECTS, false)
        model.teamShaders.value = live?.teamShaders ?: prefs.getBoolean(KEY_TEAM_SHADERS, false)
        model.useAndroidOpenGlRenderer.value =
            live?.newRender ?: prefs.getBoolean(KEY_ANDROID_OPENGL_RENDERER, false)
        model.showMainMenuBackgroundDemo.value = prefs.getBoolean(KEY_SHOW_MAIN_MENU_BACKGROUND_DEMO, false)
        model.renderBackground.value = live?.renderBackground ?: prefs.getBoolean(KEY_RENDER_BACKGROUND, true)
        model.renderExtraLayers.value = live?.renderExtraLayers ?: prefs.getBoolean(KEY_RENDER_EXTRA_LAYERS, true)
        model.showHpChanges.value = live?.showHpChanges ?: prefs.getBoolean(KEY_SHOW_HP_CHANGES, true)
        model.showUnitIcons.value = live?.showUnitIcons ?: prefs.getBoolean(KEY_SHOW_UNIT_ICONS, true)
        model.useMinimapAllyColors.value =
            live?.useMinimapAllyColors ?: prefs.getBoolean(KEY_USE_MINIMAP_ALLY_COLORS, true)
        model.showWarLogOnScreen.value =
            live?.showWarLogOnScreen ?: prefs.getBoolean(KEY_SHOW_WAR_LOG_ON_SCREEN, true)

        model.mouseCaptureEnabled.value = live?.enableMouseCapture ?: prefs.getBoolean(KEY_ENABLE_MOUSE_CAPTURE, false)
        model.mouseSupport.value = live?.mouseSupport ?: prefs.getBoolean(KEY_MOUSE_SUPPORT, !GameEngine.isBlueStacks())
        model.keyboardSupport.value = live?.keyboardSupport ?: prefs.getBoolean(KEY_KEYBOARD_SUPPORT, true)
        model.gestureZoom.value = live?.gestureZoom ?: prefs.getBoolean(KEY_GESTURE_ZOOM, true)
        model.useCircleSelect.value = live?.useCircleSelect ?: prefs.getBoolean(KEY_USE_CIRCLE_SELECT, false)
        model.showUnitGroups.value = live?.showUnitGroups ?: prefs.getBoolean(KEY_SHOW_UNIT_GROUPS, true)
        model.immersiveFullScreen.value = live?.immersiveFullScreen ?: prefs.getBoolean(KEY_IMMERSIVE_FULL_SCREEN, true)
        model.unlockedScreenRotation.value =
            live?.unlockedScreenRotation ?: prefs.getBoolean(KEY_UNLOCKED_SCREEN_ROTATION, false)
        model.classicInterface.value = live?.classicInterface ?: prefs.getBoolean(KEY_CLASSIC_INTERFACE, false)
        model.forceEnglish.value = live?.forceEnglish ?: prefs.getBoolean(KEY_FORCE_ENGLISH, false)

        model.quickRally.value = live?.quickRally ?: prefs.getBoolean(KEY_QUICK_RALLY, true)
        model.doubleClickToAttackMove.value =
            live?.doubleClickToAttackMove ?: prefs.getBoolean(KEY_DOUBLE_CLICK_TO_ATTACK_MOVE, true)
        model.showMapPingsOnBattlefield.value =
            live?.showMapPingsOnBattlefield ?: prefs.getBoolean(KEY_SHOW_MAP_PINGS_ON_BATTLEFIELD, true)
        model.showMapPingsOnMinimap.value =
            live?.showMapPingsOnMinimap ?: prefs.getBoolean(KEY_SHOW_MAP_PINGS_ON_MINIMAP, true)
        model.showPlayerChatInGame.value =
            live?.showPlayerChatInGame ?: prefs.getBoolean(KEY_SHOW_PLAYER_CHAT_IN_GAME, true)
        model.showChatAndPingShortcuts.value =
            live?.showChatAndPingShortcuts ?: prefs.getBoolean(KEY_SHOW_CHAT_AND_PING_SHORTCUTS, true)
        model.smartSelection.value = live?.smartSelection_v2 ?: prefs.getBoolean(KEY_SMART_SELECTION, true)
        model.autosaving.value = live?.autosaving ?: prefs.getBoolean(KEY_AUTOSAVING, true)
        model.udpInMultiplayer.value = live?.udpInMultiplayer ?: prefs.getBoolean(KEY_UDP_IN_MULTIPLAYER, false)
        model.saveMultiplayerReplays.value =
            live?.saveMultiplayerReplays ?: prefs.getBoolean(KEY_SAVE_MULTIPLAYER_REPLAYS, GameEngine.isPC())
        model.replaysShowRecordedChat.value =
            live?.replaysShowRecordedChat ?: prefs.getBoolean(KEY_REPLAYS_SHOW_RECORDED_CHAT, true)
        model.sendReports.value = live?.sendReports ?: prefs.getBoolean(KEY_SEND_REPORTS, true)

        model.enableSounds.value = live?.enableSounds ?: prefs.getBoolean(KEY_ENABLE_SOUNDS, true)
        model.masterVolume.value = (live?.masterVolume ?: prefs.getFloat(KEY_MASTER_VOLUME, 0.5f))
            .coerceIn(0f, 1f)
        model.gameVolume.value = (live?.gameVolume ?: prefs.getFloat(KEY_GAME_VOLUME, 1.0f))
            .coerceIn(0f, 1f)
        model.interfaceVolume.value = (live?.interfaceVolume ?: prefs.getFloat(KEY_INTERFACE_VOLUME, 0.8f))
            .coerceIn(0f, 1f)
        model.musicVolume.value = (live?.musicVolume ?: prefs.getFloat(KEY_MUSIC_VOLUME, 0.25f))
            .coerceIn(0f, 1f)
        model.scrollSpeed.value = live?.scrollSpeed ?: prefs.getFloat(KEY_SCROLL_SPEED, 1.0f)
        model.edgeScrollSpeed.value = live?.edgeScrollSpeed ?: prefs.getFloat(KEY_EDGE_SCROLL_SPEED, 1.0f)
        val storedStorageType = live?.storageType
            ?: prefs.getInt(KEY_STORAGE_TYPE, if (GameEngine.isAndroidPlatform()) 2 else 0)
        val externalStorageLink = live?.externalSAFLink ?: prefs.getString(KEY_EXTERNAL_SAF_LINK, null)
        model.storageType.value = if (
            GameEngine.isAndroidPlatform() &&
            storedStorageType >= 2 &&
            externalStorageLink.isNullOrBlank()
        ) {
            0
        } else {
            storedStorageType
        }

        val colorSchemeId = prefs.getString(KEY_RWX_COLOR_SCHEME, null)
            ?.let(::ColorSchemeId)
            ?.takeIf { id -> ColorSchemeRegistry.schemes.any { it.id == id } }
        if (colorSchemeId != null) {
            model.selectedColorSchemeId.value = colorSchemeId
        }
    }

    /**
     * Applies the current settings model to the running game without writing preferences.
     *
     * Sliders use this path while being dragged so audio and camera speed changes are observable
     * immediately, while [saveFrom] remains the only path that flushes settings to disk.
     */
    fun applyLive(model: SettingsModel) {
        normalizeAudioSettings(model)
        applySliderSettings(model, runtimeSettings())
        GameEngine.getInstance()?.musicManager?.onSettingsChanged()
    }

    fun saveFrom(model: SettingsModel) {
        normalizeAudioSettings(model)
        val settings = runtimeSettings()
        applyToLiveSettings(model, settings)
        settings.save()
        GameEngine.getInstance()?.musicManager?.onSettingsChanged()
        writePreferences(model)
        preferenceStorage.flush()
    }

    /**
     * Returns the settings object that a newly attached game engine will use.
     *
     * The settings screen is available before a [GameEngine] exists. In that state it is not
     * enough to update preferences: [SettingsEngine] may already have been initialized by the
     * platform bootstrap and would otherwise retain its old defaults when the game starts.
     */
    private fun runtimeSettings(): SettingsEngine =
        GameEngine.getInstance()?.settingsEngine ?: SettingsEngine.getInstance()

    private fun applyToLiveSettings(model: SettingsModel, settings: SettingsEngine) {
        settings.batterySaving = model.batterySaving.value
        settings.highRefreshRate = model.highRefreshRate.value
        settings.slick2dFullScreen = model.slick2dFullScreen.value
        settings.renderVsync = model.vsync.value
        settings.showHp = model.showUnitHp.value
        settings.showUnitWaypoints = model.showWaypoints.value
        settings.showZoomButton = model.showZoomButton.value
        settings.showFps = model.showFps.value
        settings.renderClouds = model.renderClouds.value
        settings.renderDoubleScale = model.renderDoubleScale.value
        settings.softFogFading = model.softFogFading.value
        settings.shaderEffects = model.shaderEffects.value
        settings.teamShaders = model.teamShaders.value
        settings.newRender = model.useAndroidOpenGlRenderer.value
        settings.renderBackground = model.renderBackground.value
        settings.renderExtraLayers = model.renderExtraLayers.value
        settings.showHpChanges = model.showHpChanges.value
        settings.showUnitIcons = model.showUnitIcons.value
        settings.useMinimapAllyColors = model.useMinimapAllyColors.value
        settings.showWarLogOnScreen = model.showWarLogOnScreen.value
        settings.enableMouseCapture = model.mouseCaptureEnabled.value
        settings.mouseSupport = model.mouseSupport.value
        settings.keyboardSupport = model.keyboardSupport.value
        settings.gestureZoom = model.gestureZoom.value
        settings.useCircleSelect = model.useCircleSelect.value
        settings.showUnitGroups = model.showUnitGroups.value
        settings.immersiveFullScreen = model.immersiveFullScreen.value
        settings.unlockedScreenRotation = model.unlockedScreenRotation.value
        settings.classicInterface = model.classicInterface.value
        settings.forceEnglish = model.forceEnglish.value
        settings.quickRally = model.quickRally.value
        settings.doubleClickToAttackMove = model.doubleClickToAttackMove.value
        settings.showMapPingsOnBattlefield = model.showMapPingsOnBattlefield.value
        settings.showMapPingsOnMinimap = model.showMapPingsOnMinimap.value
        settings.showPlayerChatInGame = model.showPlayerChatInGame.value
        settings.showChatAndPingShortcuts = model.showChatAndPingShortcuts.value
        settings.smartSelection_v2 = model.smartSelection.value
        settings.autosaving = model.autosaving.value
        settings.udpInMultiplayer = model.udpInMultiplayer.value
        settings.saveMultiplayerReplays = model.saveMultiplayerReplays.value
        settings.replaysShowRecordedChat = model.replaysShowRecordedChat.value
        settings.sendReports = model.sendReports.value
        settings.enableSounds = model.enableSounds.value
        settings.masterVolume = model.masterVolume.value
        settings.gameVolume = model.gameVolume.value
        settings.interfaceVolume = model.interfaceVolume.value
        settings.musicVolume = model.musicVolume.value
        settings.scrollSpeed = model.scrollSpeed.value
        settings.edgeScrollSpeed = model.edgeScrollSpeed.value
        settings.storageType = model.storageType.value
        if (GameEngine.isAndroidPlatform()) {
            settings.hasSelectedAStorageType = true
        }
    }

    private fun applySliderSettings(model: SettingsModel, settings: SettingsEngine) {
        settings.enableSounds = model.enableSounds.value
        settings.masterVolume = model.masterVolume.value
        settings.gameVolume = model.gameVolume.value
        settings.interfaceVolume = model.interfaceVolume.value
        settings.musicVolume = model.musicVolume.value
        settings.scrollSpeed = model.scrollSpeed.value
        settings.edgeScrollSpeed = model.edgeScrollSpeed.value
    }

    private fun normalizeAudioSettings(model: SettingsModel) {
        model.masterVolume.value = model.masterVolume.value.coerceIn(0f, 1f)
        model.gameVolume.value = model.gameVolume.value.coerceIn(0f, 1f)
        model.interfaceVolume.value = model.interfaceVolume.value.coerceIn(0f, 1f)
        model.musicVolume.value = model.musicVolume.value.coerceIn(0f, 1f)
    }

    private fun writePreferences(model: SettingsModel) {
        preferences
            .putBoolean(KEY_BATTERY_SAVING, model.batterySaving.value)
            .putBoolean(KEY_HIGH_REFRESH_RATE, model.highRefreshRate.value)
            .putBoolean(KEY_SLICK2D_FULL_SCREEN, model.slick2dFullScreen.value)
            .putBoolean(KEY_RENDER_VSYNC, model.vsync.value)
            .putBoolean(KEY_SHOW_HP, model.showUnitHp.value)
            .putBoolean(KEY_SHOW_UNIT_WAYPOINTS, model.showWaypoints.value)
            .putBoolean(KEY_SHOW_ZOOM_BUTTON, model.showZoomButton.value)
            .putBoolean(KEY_SHOW_FPS, model.showFps.value)
            .putBoolean(KEY_RENDER_CLOUDS, model.renderClouds.value)
            .putBoolean(KEY_RENDER_DOUBLE_SCALE, model.renderDoubleScale.value)
            .putBoolean(KEY_SOFT_FOG_FADING, model.softFogFading.value)
            .putBoolean(KEY_SHADER_EFFECTS, model.shaderEffects.value)
            .putBoolean(KEY_TEAM_SHADERS, model.teamShaders.value)
            .putBoolean(KEY_ANDROID_OPENGL_RENDERER, model.useAndroidOpenGlRenderer.value)
            .putBoolean(KEY_SHOW_MAIN_MENU_BACKGROUND_DEMO, model.showMainMenuBackgroundDemo.value)
            .putBoolean(KEY_RENDER_BACKGROUND, model.renderBackground.value)
            .putBoolean(KEY_RENDER_EXTRA_LAYERS, model.renderExtraLayers.value)
            .putBoolean(KEY_SHOW_HP_CHANGES, model.showHpChanges.value)
            .putBoolean(KEY_SHOW_UNIT_ICONS, model.showUnitIcons.value)
            .putBoolean(KEY_USE_MINIMAP_ALLY_COLORS, model.useMinimapAllyColors.value)
            .putBoolean(KEY_SHOW_WAR_LOG_ON_SCREEN, model.showWarLogOnScreen.value)
            .putBoolean(KEY_ENABLE_MOUSE_CAPTURE, model.mouseCaptureEnabled.value)
            .putBoolean(KEY_MOUSE_SUPPORT, model.mouseSupport.value)
            .putBoolean(KEY_KEYBOARD_SUPPORT, model.keyboardSupport.value)
            .putBoolean(KEY_GESTURE_ZOOM, model.gestureZoom.value)
            .putBoolean(KEY_USE_CIRCLE_SELECT, model.useCircleSelect.value)
            .putBoolean(KEY_SHOW_UNIT_GROUPS, model.showUnitGroups.value)
            .putBoolean(KEY_IMMERSIVE_FULL_SCREEN, model.immersiveFullScreen.value)
            .putBoolean(KEY_UNLOCKED_SCREEN_ROTATION, model.unlockedScreenRotation.value)
            .putBoolean(KEY_CLASSIC_INTERFACE, model.classicInterface.value)
            .putBoolean(KEY_FORCE_ENGLISH, model.forceEnglish.value)
            .putBoolean(KEY_QUICK_RALLY, model.quickRally.value)
            .putBoolean(KEY_DOUBLE_CLICK_TO_ATTACK_MOVE, model.doubleClickToAttackMove.value)
            .putBoolean(KEY_SHOW_MAP_PINGS_ON_BATTLEFIELD, model.showMapPingsOnBattlefield.value)
            .putBoolean(KEY_SHOW_MAP_PINGS_ON_MINIMAP, model.showMapPingsOnMinimap.value)
            .putBoolean(KEY_SHOW_PLAYER_CHAT_IN_GAME, model.showPlayerChatInGame.value)
            .putBoolean(KEY_SHOW_CHAT_AND_PING_SHORTCUTS, model.showChatAndPingShortcuts.value)
            .putBoolean(KEY_SMART_SELECTION, model.smartSelection.value)
            .putBoolean(KEY_AUTOSAVING, model.autosaving.value)
            .putBoolean(KEY_UDP_IN_MULTIPLAYER, model.udpInMultiplayer.value)
            .putBoolean(KEY_SAVE_MULTIPLAYER_REPLAYS, model.saveMultiplayerReplays.value)
            .putBoolean(KEY_REPLAYS_SHOW_RECORDED_CHAT, model.replaysShowRecordedChat.value)
            .putBoolean(KEY_SEND_REPORTS, model.sendReports.value)
            .putBoolean(KEY_ENABLE_SOUNDS, model.enableSounds.value)
            .putFloat(KEY_MASTER_VOLUME, model.masterVolume.value)
            .putFloat(KEY_GAME_VOLUME, model.gameVolume.value)
            .putFloat(KEY_INTERFACE_VOLUME, model.interfaceVolume.value)
            .putFloat(KEY_MUSIC_VOLUME, model.musicVolume.value)
            .putFloat(KEY_SCROLL_SPEED, model.scrollSpeed.value)
            .putFloat(KEY_EDGE_SCROLL_SPEED, model.edgeScrollSpeed.value)
            .putInt(KEY_STORAGE_TYPE, model.storageType.value)
            .putString(KEY_RWX_COLOR_SCHEME, model.selectedColorSchemeId.value.value)
    }

    companion object {
        private const val KEY_RWX_COLOR_SCHEME = "rwxColorSchemeId"
        private const val KEY_BATTERY_SAVING = "batterySaving"
        private const val KEY_HIGH_REFRESH_RATE = "highRefreshRate"
        private const val KEY_SLICK2D_FULL_SCREEN = "slick2dFullScreen"
        private const val KEY_RENDER_VSYNC = "renderVsync"
        private const val KEY_SHOW_HP = "showHp"
        private const val KEY_SHOW_UNIT_WAYPOINTS = "showUnitWaypoints"
        private const val KEY_SHOW_ZOOM_BUTTON = "showZoomButton"
        private const val KEY_SHOW_FPS = "showFps"
        private const val KEY_RENDER_CLOUDS = "renderClouds"
        private const val KEY_RENDER_DOUBLE_SCALE = "renderDoubleScale"
        private const val KEY_SOFT_FOG_FADING = "softFogFading"
        private const val KEY_SHADER_EFFECTS = "shaderEffects"
        private const val KEY_TEAM_SHADERS = "teamShaders"
        private const val KEY_SHOW_MAIN_MENU_BACKGROUND_DEMO = "showMainMenuBackgroundDemo"
        private const val KEY_RENDER_BACKGROUND = "renderBackground"
        private const val KEY_RENDER_EXTRA_LAYERS = "renderExtraLayers"
        private const val KEY_SHOW_HP_CHANGES = "showHpChanges"
        private const val KEY_SHOW_UNIT_ICONS = "showUnitIcons"
        private const val KEY_USE_MINIMAP_ALLY_COLORS = "useMinimapAllyColors"
        private const val KEY_SHOW_WAR_LOG_ON_SCREEN = "showWarLogOnScreen"
        private const val KEY_ENABLE_MOUSE_CAPTURE = "enableMouseCapture"
        private const val KEY_MOUSE_SUPPORT = "mouseSupport"
        private const val KEY_KEYBOARD_SUPPORT = "keyboardSupport"
        private const val KEY_GESTURE_ZOOM = "gestureZoom"
        private const val KEY_USE_CIRCLE_SELECT = "useCircleSelect"
        private const val KEY_SHOW_UNIT_GROUPS = "showUnitGroups"
        private const val KEY_IMMERSIVE_FULL_SCREEN = "immersiveFullScreen"
        private const val KEY_UNLOCKED_SCREEN_ROTATION = "unlockedScreenRotation"
        private const val KEY_CLASSIC_INTERFACE = "classicInterface"
        private const val KEY_FORCE_ENGLISH = "forceEnglish"
        private const val KEY_QUICK_RALLY = "quickRally"
        private const val KEY_DOUBLE_CLICK_TO_ATTACK_MOVE = "doubleClickToAttackMove"
        private const val KEY_SHOW_MAP_PINGS_ON_BATTLEFIELD = "showMapPingsOnBattlefield"
        private const val KEY_SHOW_MAP_PINGS_ON_MINIMAP = "showMapPingsOnMinimap"
        private const val KEY_SHOW_PLAYER_CHAT_IN_GAME = "showPlayerChatInGame"
        private const val KEY_SHOW_CHAT_AND_PING_SHORTCUTS = "showChatAndPingShortcuts"
        private const val KEY_SMART_SELECTION = "smartSelection_v2"
        private const val KEY_AUTOSAVING = "autosaving"
        private const val KEY_UDP_IN_MULTIPLAYER = "udpInMultiplayer"
        private const val KEY_SAVE_MULTIPLAYER_REPLAYS = "saveMultiplayerReplays"
        private const val KEY_REPLAYS_SHOW_RECORDED_CHAT = "replaysShowRecordedChat"
        private const val KEY_SEND_REPORTS = "sendReports"
        private const val KEY_ENABLE_SOUNDS = "enableSounds"
        private const val KEY_MASTER_VOLUME = "masterVolume"
        private const val KEY_GAME_VOLUME = "gameVolume"
        private const val KEY_INTERFACE_VOLUME = "interfaceVolume"
        private const val KEY_MUSIC_VOLUME = "musicVolume"
        private const val KEY_SCROLL_SPEED = "scrollSpeed"
        private const val KEY_EDGE_SCROLL_SPEED = "edgeScrollSpeed"
        private const val KEY_STORAGE_TYPE = "storageType"
        private const val KEY_EXTERNAL_SAF_LINK = "externalSAFLink"
    }
}
