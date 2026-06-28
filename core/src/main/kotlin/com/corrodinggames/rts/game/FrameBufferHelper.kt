package com.corrodinggames.rts.game

import com.corrodinggames.rts.gameFramework.AssetType
import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.graphics.*
import io.github.rwx.geometry.Rect
import io.github.rwx.render.canvas.KoolDisplacementEffect
import io.github.rwx.render.canvas.KoolPaint

class FrameBufferHelper() {
    private var graphicsBackend: GraphicsEngine? = null

    @JvmField
    var a: Texture? = null

    @JvmField
    var b: GraphicsEngine? = null

    @JvmField
    val c: GamePaint = GamePaint()

    @JvmField
    var d: KoolDisplacementEffect? = null

    @JvmField
    var shaderProgram: ShaderProgram? = null

    @JvmField
    val e: KoolPaint = KoolPaint()

    @JvmField
    val f: Rect = Rect(-101, 0, -1, 100)

    @JvmField
    var g: Boolean = false

    constructor(shaderPath: String) : this() {
        shaderProgram = ShaderProgram(shaderPath)
        c.a(shaderProgram)
        if (shaderProgram?.o != 0) {
            g = true
        }
    }

    fun a(): Boolean = g || (shaderProgram?.o ?: 0) != 0

    fun a(graphicsEngine: GraphicsEngine) {
        a(graphicsEngine, graphicsEngine.m(), graphicsEngine.n(), 10)
    }

    fun a(graphicsEngine: GraphicsEngine, width: Int, height: Int, padding: Int) {
        if (g) {
            return
        }
        if (graphicsBackend !== graphicsEngine) {
            releaseTarget()
            graphicsBackend = graphicsEngine
        }
        if (a != null && (width > a!!.width() || height > a!!.height())) {
            releaseTarget()
        }
        if (a == null) {
            try {
                a = graphicsEngine.a(width + padding, height + padding, true)
                b = graphicsEngine.b(a, RenderTargetMode.DEFAULT)
            } catch (e: OutOfMemoryError) {
                g = true
                GameEngine.reportOOM(AssetType.gameImageCreate, e)
                return
            }
        }
        b!!.a(width, height)
    }

    private fun releaseTarget() {
        b?.q()
        b = null
        a?.releaseTexture()
        a = null
    }

    fun b() {
        val gameEngine = GameEngine.getInstance()
        val shader = shaderProgram
        val displacement = d
        if (shader != null && displacement?.screenBase != null) {
            shader.a("screenBase", displacement.screenBase)
            shader.b("screenBaseSize", displacement.screenBase)
            shader.a("u_resolution", gameEngine.screenWidth, gameEngine.screenHeight)
            shader.a("u_offsetBy", displacement.offsetBy)
            shader.a("u_uiScaling", 1f)
        }
        gameEngine.renderGraphicsEngine.b(f, e)
        gameEngine.renderGraphicsEngine.b(a, 0f, 0f, c)
    }

    companion object {
        @JvmStatic
        fun postBase(): FrameBufferHelper = FrameBufferHelper("assets/shaders/post_base.frag")

        @JvmStatic
        fun postDisplacement(): FrameBufferHelper =
            FrameBufferHelper("assets/shaders/post_displacement.frag").apply {
                d = KoolDisplacementEffect()
                c.a(d)
            }
    }
}
