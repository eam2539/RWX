package io.github.rwx.net

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
import com.corrodinggames.rts.gameFramework.network.NetworkConnection
import io.github.rwx.ui.CoreUiEventQueue

open class CoreUiNetworkCallbacks : NetworkCallbacks() {
    override fun onStartGameEvent() {
        super.onStartGameEvent()
        if (!GameEngine.isNonAndroidVersion) return
        CoreUiEventQueue.requestBattleRoomGameStarted()
    }

    /** Chat / system message shown in the battle room (sender is null for system messages). */
    override fun a(color: Int, sender: String?, message: String?, connection: NetworkConnection?) {
        if (!GameEngine.isNonAndroidVersion) return // Android already routes through the bridge
        val networkEngine = GameEngine.getInstance()?.networkEngine ?: return
        if (networkEngine.gameHasBeenStarted) return // in-game chat goes to the message manager
        val text = message?.takeIf { it.isNotBlank() } ?: return
        CoreUiEventQueue.appendBattleRoomChatMessage(
            text = if (sender.isNullOrBlank()) text else "$sender: $text",
            // `color` is the author team id (-1 = system/no team), used directly as the tint index.
            teamColorIndex = color,
        )
    }

    /** Battle-room UI refresh (original desktop runs librocket `mp.refreshUI()` here). */
    override fun c() {
        if (GameEngine.getInstance()?.networkEngine?.gameHasBeenStarted == true) return
        CoreUiEventQueue.requestBattleRoomRefresh()
    }
}
