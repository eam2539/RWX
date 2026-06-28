package io.github.rwx.app

import io.github.rwx.ui.AppScreen

internal class StartupController(
    private val battleRoomController: BattleRoomController,
    private val warmupController: WarmupController,
) {
    fun start(targetScreen: AppScreen) {
        val startupWarmupMapPath = when (targetScreen) {
            AppScreen.InGame -> battleRoomController.selectedOrDefaultMap()?.mapAssetPath
            AppScreen.BattleRoom -> {
                battleRoomController.selectedOrDefaultMap()?.let { map ->
                    battleRoomController.prepareForMap(map)
                }
                null
            }

            else -> null
        }
        warmupController.request(
            mapPath = startupWarmupMapPath,
            targetScreen = targetScreen,
        )
    }
}
