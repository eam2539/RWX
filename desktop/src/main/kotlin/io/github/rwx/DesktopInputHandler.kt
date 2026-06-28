package io.github.rwx

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.InputHandler
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes

class DesktopInputHandler : InputHandler() {
    override fun c(i: Int, i2: Int): String =
        GameEngine.getModifierString(i2) + SlickToAndroidKeycodes.a(i)
}
