package com.corrodinggames.rts.gameFramework.graphics

import com.corrodinggames.rts.game.ColorMode
import com.corrodinggames.rts.gameFramework.GameEngine

open class Texture : Cloneable {
    @JvmField
    var a: Array<Texture>? = null

    @JvmField
    var b: Array<Texture>? = null

    @JvmField
    var c: Array<Texture>? = null

    @JvmField
    var d: Int = nextTextureId()

    @JvmField
    var e: Int = 1

    @JvmField
    var f: Int = 0

    @JvmField
    var g: String? = null

    @JvmField
    var h: Int? = null

    @JvmField
    var j: IntArray? = null

    private var committedArgbPixels: IntArray? = null
    private var argbPixelLoader: (() -> IntArray?)? = null
    private var orderedAlphaRequired: Boolean = false
    private var premultipliedAlpha: Boolean = false
    private var pixelRevision: Int = 0
    private var shaderProgram: ShaderProgram? = null

    @JvmField
    var m: Boolean = false

    @JvmField
    var n: Boolean = false

    @JvmField
    var p: Int = 0

    @JvmField
    var q: Int = 0

    @JvmField
    var r: Int = 0

    @JvmField
    var s: Int = 0

    @JvmField
    var t: Float = 0f

    @JvmField
    var u: Float = 0f

    @JvmField
    var v: Boolean = false

    @JvmField
    var l: Boolean = true

    @JvmField
    var o: Boolean = false

    @JvmField
    var w: Boolean = false

    open fun a(colorMode: ColorMode): Array<Texture>? =
        when (colorMode) {
            ColorMode.pureGreen -> a
            ColorMode.hueAdd -> b
            ColorMode.hueShift -> c
            else -> {
                GameEngine.logColored("getTeamImageCache coloringMode:$colorMode")
                a
            }
        }

    open fun a(colorMode: ColorMode, textures: Array<Texture>?) {
        when (colorMode) {
            ColorMode.pureGreen -> a = textures
            ColorMode.hueAdd -> b = textures
            ColorMode.hueShift -> c = textures
            else -> {
                GameEngine.logColored("setTeamImageCache coloringMode:$colorMode")
                a = textures
            }
        }
    }

    open fun a(name: String?) {
        g = name
    }

    open fun a(): String? = g

    open fun b(): IntArray? {
        i()
        return j
    }

    val argbPixelsCopy: IntArray?
        get() = loadArgbPixelsIfNeeded()?.clone()

    open fun c(): Texture = this

    open fun a(value: Boolean) {
        o = value
        e()
    }

    open fun b(value: Boolean) {
        w = value
    }

    open fun d(): Boolean = w

    protected open fun e() = Unit

    open fun f(): Boolean = m

    open fun g() {
        r = p / 2
        s = q / 2
        t = p / 2.0f
        u = q / 2.0f
    }

    open fun a(texture: Texture) {
        texture.o = o
        texture.m = m
        texture.p = p
        texture.q = q
        texture.r = r
        texture.s = s
        texture.t = t
        texture.u = u
    }

    public open override fun clone(): Texture {
        val texture = Texture()
        texture.o = o
        texture.m = m
        texture.p = p
        texture.q = q
        texture.g()
        val pixels = argbPixelsCopy
        if (pixels != null) {
            texture.j = pixels
            texture.committedArgbPixels = pixels.clone()
        }
        return texture
    }

    open fun a(width: Int, height: Int, copyPixels: Boolean): Texture {
        val texture = Texture()
        texture.o = o
        texture.m = m
        texture.p = width
        texture.q = height
        texture.g()
        if (copyPixels) {
            texture.j = IntArray(width * height)
            val copyWidth = minOf(width, p)
            val copyHeight = minOf(height, q)
            for (x in 0 until copyWidth) {
                for (y in 0 until copyHeight) {
                    texture.j!![x + (y * width)] = a(x, y)
                }
            }
            texture.committedArgbPixels = texture.j!!.clone()
            texture.m = containsTransparentPixels(texture.j!!)
        }
        return texture
    }

    open fun i() {
        if (j == null) {
            j()
        }
    }

    open fun j() {
        val committedPixels = loadArgbPixelsIfNeeded()
        if (j == null && committedPixels != null) {
            j = committedPixels.clone()
            return
        }
        if (j == null) {
            if (p <= 0 || q <= 0) {
                return
            }
            j = IntArray(p * q)
        }
    }

    open fun k(): Boolean = true

    open fun a(x: Int, y: Int): Int {
        loadArgbPixelsIfNeeded()
        val editable = j
        if (editable != null) {
            return editable[x + (y * p)]
        }
        val committed = committedArgbPixels
        if (committed != null) {
            return committed[x + (y * p)]
        }
        return 0
    }

    open fun a(x: Int, y: Int, color: Int) {
        loadArgbPixelsIfNeeded()
        val editable = j
        when {
            editable != null -> {
                editable[x + (y * p)] = color
                pixelRevision++
            }

            committedArgbPixels != null -> {
                committedArgbPixels!![x + (y * p)] = color
                pixelRevision++
            }

            else -> {
                if (p <= 0 || q <= 0) {
                    p = maxOf(p, x + 1)
                    q = maxOf(q, y + 1)
                    g()
                }
                committedArgbPixels = IntArray(p * q)
                committedArgbPixels!![x + (y * p)] = color
                pixelRevision++
            }
        }
    }

    open fun l(): Int = q

    open fun m(): Int = p

    open fun n() = Unit

    open fun o() {
        if (w) {
            GameEngine.logColored("remove with keepInGPUMemory=true")
        }
        argbPixelLoader = null
    }

    open fun p() {
        val editable = j
        if (editable != null) {
            committedArgbPixels = editable.clone()
            argbPixelLoader = null
            m = containsTransparentPixels(editable)
            pixelRevision++
        }
        e++
    }

    open fun q() = Unit

    open fun r() {
        j = null
    }

    open fun s() {
        r()
    }

    open fun t() = Unit

    open fun u(): Int = p * q * 8

    open fun v() {
        a = null
        b = null
        c = null
        pixelRevision++
        e++
    }

    open fun w() = Unit

    open fun x() = Unit

    open fun y() = Unit

    open fun z() = Unit

    open fun A(): Boolean = false

    open fun B(): ShaderProgram? = shaderProgram

    open fun a(shaderProgram: ShaderProgram?) {
        this.shaderProgram = shaderProgram
    }

    fun shaderProgram(): ShaderProgram? = B()

    fun setShaderProgram(shaderProgram: ShaderProgram?) = a(shaderProgram)

    fun setSourceName(name: String?) = a(name)

    fun sourceName(): String? = a()

    fun editablePixels(): IntArray? = b()

    fun resolvedTexture(): Texture = c()

    fun updateCenter() = g()

    fun copyTextureSettingsTo(texture: Texture) = a(texture)

    fun width(): Int = m()

    fun height(): Int = l()

    fun commitPixels() = p()

    fun discardEditPixels() = s()

    fun releaseTexture() = o()

    fun estimatedMemoryBytes(): Int = u()

    fun ensureLoaded() = w()

    fun setCommittedArgbPixels(pixels: IntArray) {
        j = null
        committedArgbPixels = pixels.clone()
        argbPixelLoader = null
        m = containsTransparentPixels(pixels)
        pixelRevision++
    }

    fun setArgbPixelLoader(loader: (() -> IntArray?)?) {
        if (j == null && committedArgbPixels == null) {
            argbPixelLoader = loader
        }
    }

    protected fun invalidateArgbPixelSnapshot(loader: (() -> IntArray?)? = null) {
        j = null
        committedArgbPixels = null
        argbPixelLoader = loader
        pixelRevision++
    }

    fun setPremultipliedAlpha(value: Boolean) {
        premultipliedAlpha = value
    }

    fun usesPremultipliedAlpha(): Boolean = premultipliedAlpha

    fun getPixelRevision(): Int = pixelRevision

    fun requireOrderedAlpha() {
        orderedAlphaRequired = true
    }

    fun requiresOrderedAlpha(): Boolean = orderedAlphaRequired

    companion object {
        private var x: Int = 0

        private fun containsTransparentPixels(pixels: IntArray): Boolean =
            pixels.any { (it ushr 24) != 0xff }

        private fun nextTextureId(): Int {
            val id = x
            x = id + 1
            return id
        }
    }

    private fun loadArgbPixelsIfNeeded(): IntArray? {
        j?.let { return it }
        committedArgbPixels?.let { return it }
        val loaded = argbPixelLoader?.invoke()
        argbPixelLoader = null
        if (loaded != null) {
            committedArgbPixels = loaded
            m = containsTransparentPixels(loaded)
        }
        return loaded
    }
}
