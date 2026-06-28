package com.corrodinggames.rts.gameFramework.graphics

import com.corrodinggames.rts.gameFramework.GameEngine
import io.github.rwx.render.canvas.KoolDisplacementEffect
import io.github.rwx.render.canvas.KoolPaint
import io.github.rwx.render.canvas.KoolTypeface

class GamePaint : KoolPaint() {
    private var displacementEffect: KoolDisplacementEffect? = null
    private var shaderProgram: ShaderProgram? = null
    private var hasFlag: Boolean = false
    private var locked: Boolean = false

    fun o() {
        locked = true
    }

    fun c(value: Float) {
        super.b(value)
    }

    override fun b(value: Float) {
        if (locked) {
            GameEngine.logColored("UniquePaint changed when locked down:")
            GameEngine.logColored("from:" + textSize() + " to: " + value)
            GameEngine.printStackTrace()
        }
        super.b(value)
    }

    override fun a(typeface: KoolTypeface?): KoolTypeface? {
        if (locked) {
            GameEngine.logColored("UniquePaint changed when locked down:")
            GameEngine.printStackTrace()
        }
        return super.a(typeface)
    }

    fun p(): Boolean = hasFlag

    fun hasLegacyFlag(): Boolean = p()

    override fun a(value: Boolean) {
        hasFlag = value
        super.a(value)
    }

    fun q(): KoolDisplacementEffect? = displacementEffect

    fun displacementEffect(): KoolDisplacementEffect? = q()

    fun a(effect: KoolDisplacementEffect?) {
        if (displacementEffect !== effect) {
            displacementEffect = effect
            markBackendStateChanged()
        }
    }

    fun setDisplacementEffect(effect: KoolDisplacementEffect?) = a(effect)

    fun shaderProgram(): ShaderProgram? = shaderProgram

    fun a(shaderProgram: ShaderProgram?) {
        if (this.shaderProgram !== shaderProgram) {
            this.shaderProgram = shaderProgram
            markBackendStateChanged()
        }
    }

    fun setShaderProgram(shaderProgram: ShaderProgram?) = a(shaderProgram)

    companion object {
        @JvmField
        val r: GamePaint = GamePaint().apply {
            setColor(-1)
            o()
        }

        @JvmStatic
        fun b(paint: KoolPaint) {
            (paint as GamePaint).o()
        }
    }
}
