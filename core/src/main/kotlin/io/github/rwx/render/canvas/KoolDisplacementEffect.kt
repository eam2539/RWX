package io.github.rwx.render.canvas

import com.corrodinggames.rts.gameFramework.graphics.Texture

class KoolDisplacementEffect {
    @JvmField
    var screenBase: Texture? = null

    @JvmField
    var offsetBy: Float = 0f

    fun configure(screenBase: Texture?, offsetBy: Float) {
        this.screenBase = screenBase
        this.offsetBy = offsetBy
    }
}
