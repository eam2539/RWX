package io.github.rwx.mod

import com.corrodinggames.rts.gameFramework.GameEngine

internal fun isJvmModSimulationAllowed(engine: GameEngine? = GameEngine.getInstance()): Boolean {
    if (engine == null) return false
    if (engine.replayEngine?.j() == true) return false
    val networkEngine = engine.networkEngine ?: return true
    val multiplayerActive = networkEngine.networkGameActive && !networkEngine.singleplayerServer
    return !multiplayerActive || networkEngine.p2pSession
}
