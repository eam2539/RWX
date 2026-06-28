package com.corrodinggames.rts.gameFramework.graphics.opengl

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.graphics.*
import io.github.rwx.render.canvas.KoolPaint

class TextureAtlas {
    @JvmField
    var a: Texture? = null

    @JvmField
    var b: GraphicsEngine? = null

    @JvmField
    var c: KoolPaint? = null

    @JvmField
    var d: Int = 0

    var e: Boolean = false
    var f: Int = 0
    var g: Boolean = false
    var h: Int = 0
    var i: Int = 0
    var j: Int = 0
    var k: Int = 1
    var l: HashMap<Texture, AtlasRegion> = HashMap()
    var m: ArrayList<Texture> = ArrayList()

    constructor(width: Int, height: Int) {
        a(width, height)
    }

    constructor(width: Int, height: Int, graphicsEngine: GraphicsEngine) {
        a(width, height, graphicsEngine)
    }

    fun a(width: Int, height: Int) {
        val gameEngine = GameEngine.getInstance()
            ?: throw IllegalStateException("GameEngine is not initialized")
        a(width, height, gameEngine.renderGraphicsEngine)
    }

    fun a(width: Int, height: Int, graphicsEngine: GraphicsEngine) {
        GameEngine.log("Creating BitmapOrTextureAlias: ${width}x$height")
        a = graphicsEngine.a(width, height, true)
        a?.i()
        b = null
        c = GamePaint().apply {
            a(TeamColorFilter(BlendMode.copy))
        }
    }

    fun a(texture: Texture, x: Int, y: Int) {
        copyPixelsIntoAtlas(texture, texture.argbPixelsCopy ?: return, x, y)
    }

    fun a() {
        val atlasTexture = a ?: return
        atlasTexture.i()
        atlasTexture.j?.fill(0)
        atlasTexture.e++
    }

    fun b() {
        d = 0
        e = false
        f = 0
        h = 0
        i = 0
        j = 0
        l.clear()
        a()
    }

    fun c() {
        f++
        if (e && f > 600) {
            g = true
            m.clear()
        }
    }

    fun d() {
        if (g) {
            g = false
            b()
            val iterator = m.iterator()
            while (iterator.hasNext()) {
                a(iterator.next())
            }
            m.clear()
        }
    }

    fun a(texture: Texture): AtlasRegion? {
        val atlasRegion = l[texture]
        if (atlasRegion != null) {
            if (g) {
                m.add(texture)
            }
            if (atlasRegion.f != texture.e) {
                GameEngine.log("BitmapOrTextureAlias: Image was updated: " + texture.a())
                l.remove(texture)
            } else {
                return atlasRegion
            }
        }
        return b(texture)
    }

    fun b(texture: Texture): AtlasRegion? {
        val textureWidth = texture.m()
        val textureHeight = texture.l()
        val sourcePixels = texture.argbPixelsCopy ?: return null
        val atlasTexture = a ?: return null
        val atlasWidth = atlasTexture.m()
        val atlasHeight = atlasTexture.l()
        if (h + textureWidth > atlasWidth) {
            h = 0
            i += j + k
            j = 0
        }
        if (i + textureHeight > atlasHeight) {
            if (!e) {
                e = true
                return null
            }
            return null
        }
        val atlasRegion = AtlasRegion()
        atlasRegion.a = atlasTexture
        val x = h
        val y = i
        h += textureWidth + k
        if (j < textureHeight) {
            j = textureHeight
        }
        copyPixelsIntoAtlas(texture, sourcePixels, x, y)
        atlasRegion.b = x
        atlasRegion.c = y
        atlasRegion.d = textureWidth.toFloat()
        atlasRegion.e = textureHeight.toFloat()
        atlasRegion.f = texture.e
        d++
        l[texture] = atlasRegion
        return atlasRegion
    }

    private fun copyPixelsIntoAtlas(texture: Texture, sourcePixels: IntArray, x: Int, y: Int) {
        val atlasTexture = a ?: return
        atlasTexture.i()
        val atlasPixels = atlasTexture.j ?: return
        val sourceWidth = texture.m()
        val sourceHeight = texture.l()
        val atlasWidth = atlasTexture.m()
        val atlasHeight = atlasTexture.l()
        val copyWidth = minOf(sourceWidth, atlasWidth - x)
        val copyHeight = minOf(sourceHeight, atlasHeight - y)
        if (copyWidth <= 0 || copyHeight <= 0) {
            return
        }
        for (row in 0 until copyHeight) {
            sourcePixels.copyInto(
                destination = atlasPixels,
                destinationOffset = x + ((y + row) * atlasWidth),
                startIndex = row * sourceWidth,
                endIndex = (row * sourceWidth) + copyWidth,
            )
        }
        atlasTexture.m = atlasTexture.m || texture.f()
        atlasTexture.e++
    }
}
