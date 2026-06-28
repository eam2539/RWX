package com.corrodinggames.rts.gameFramework.graphics

import com.corrodinggames.rts.game.ColorMode
import com.corrodinggames.rts.gameFramework.GameEngine
import java.io.IOException

class TeamColorTexture(
    private val source: Texture?,
    val teamColor: Int,
    val colorMode: ColorMode,
    private val teamIndex: Int,
) : Texture() {
    val teamColorAmount: Float =
        if (colorMode == ColorMode.hueAdd) HUE_ADD_TEAM_COLOR_AMOUNT else 1f

    init {
        if (source == null) {
            throw RuntimeException("baseImage==null")
        }
        source.copyTextureSettingsTo(this)
        D()
    }

    override fun a(): String =
        if (source == null) {
            "LazyColoring (error sourceTexture==null)"
        } else {
            "LazyColoring($teamIndex):" + source.sourceName()
        }

    fun c(z2: Boolean) = Unit

    fun sourceTexture(): Texture =
        source ?: throw RuntimeException("team color source texture is null")

    fun resolveSourceTexture(): Texture = sourceTexture()

    override fun b(): IntArray =
        resolveSourceTexture().editablePixels() ?: IntArray(0)

    override fun c(): Texture = resolveSourceTexture()

    override fun w() = Unit

    override fun u(): Int =
        source?.estimatedMemoryBytes() ?: super.estimatedMemoryBytes()

    fun D() {
        ensureTeamShaders()
        a(
            when (colorMode) {
                ColorMode.hueAdd -> hueAddShader
                ColorMode.hueShift -> hueShiftShader
                else -> pureGreenShader
            }
        )
    }

    companion object {
        const val HUE_ADD_TEAM_COLOR_AMOUNT = 0.15f

        @JvmStatic
        var pureGreenShader: ShaderProgram? = null
            private set

        @JvmStatic
        var hueAddShader: ShaderProgram? = null
            private set

        @JvmStatic
        var hueShiftShader: ShaderProgram? = null
            private set

        @JvmStatic
        var teamShadersLoaded: Boolean = false
            private set

        @JvmStatic
        @Synchronized
        fun C() {
            if (teamShadersLoaded) {
                return
            }
            try {
                GameEngine.log("Loading team shaders...")
                pureGreenShader = TeamColorShader("assets/shaders/pureGreenTeamColor.frag", true).also {
                    it.a("teamColor", -1)
                    it.c()
                }
                hueAddShader = TeamColorShader("assets/shaders/hueAddTeamColor.frag", false).also {
                    it.a("teamColorAmount", HUE_ADD_TEAM_COLOR_AMOUNT)
                    it.a("teamColor", -1)
                    it.c()
                }
                hueShiftShader = TeamColorShader("assets/shaders/hueShiftTeamColor.frag", false).also {
                    it.a("teamColor", -1)
                    it.c()
                }
                teamShadersLoaded = true
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        private fun ensureTeamShaders() = C()
    }
}
