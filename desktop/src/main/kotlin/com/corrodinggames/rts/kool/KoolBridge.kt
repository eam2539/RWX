package com.corrodinggames.rts.kool

import com.corrodinggames.rts.gameFramework.KoolEngine
import com.corrodinggames.rts.gameFramework.KoolGraphics
import com.corrodinggames.rts.gameFramework.KoolAudio
import com.corrodinggames.rts.gameFramework.KoolInput

/**
 * Singleton accessor for the kool-engine platform backends.
 */
object KoolBridge {
    val engine: KoolEngine get() = KoolEngine.instance ?: error("KoolEngine not initialized")
    val graphics: KoolGraphics get() = engine.graphics
    val audio: KoolAudio get() = (engine as? KoolEngineLWJGL)?.audio ?: error("KoolAudio not available")
    val input: KoolInput get() = (engine as? KoolEngineLWJGL)?.input ?: error("KoolInput not available")
    val window get() = engine.window
}