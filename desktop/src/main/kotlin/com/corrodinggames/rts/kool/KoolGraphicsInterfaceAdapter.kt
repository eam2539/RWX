package com.corrodinggames.rts.kool

import com.corrodinggames.rts.gameFramework.*
import com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface
import com.corrodinggames.rts.gameFramework.graphics.Texture
import android.graphics.*
import java.util.concurrent.locks.Lock

/**
 * Adapter that wraps the existing RWX GraphicsInterface calls
 * onto the KoolGraphics abstraction.
 *
 * This allows the existing game code (which uses GraphicsInterface)
 * to work unmodified while the rendering backend is swapped to kool-engine.
 */
class KoolGraphicsInterfaceAdapter(
    private val koolGraphics: KoolGraphics,
) : GraphicsInterface {

    private var transformStack = mutableListOf<Unit>() // marker for push/pop balance

    override fun a(z: Boolean) {
        if (z) koolGraphics.pushTransform() else koolGraphics.popTransform()
    }

    override fun c(): Boolean = true

    override fun a(rect: Rect) {
        koolGraphics.setClipRect(rect.left, rect.top, rect.width(), rect.height())
    }

    override fun a(rectF: RectF) {
        koolGraphics.setClipRect(rectF.left.toInt(), rectF.top.toInt(), rectF.width().toInt(), rectF.height().toInt())
    }

    override fun a(texture: Texture, x: Float, y: Float, paint: Paint) {
        // Draw texture at position
        val handle = getTextureHandle(texture)
        if (handle != null) {
            koolGraphics.drawTexture(handle, x, y,
                KoolColor(paint.color.red() / 255f, paint.color.green() / 255f, paint.color.blue() / 255f, paint.color.alpha() / 255f),
                paint.color.alpha() / 255f)
        }
    }

    override fun a(texture: Texture, src: Rect, dst: Rect, paint: Paint) {
        val handle = getTextureHandle(texture)
        if (handle != null) {
            koolGraphics.drawTexture(handle, src.left, src.top, src.width(), src.height(),
                dst.left.toFloat(), dst.top.toFloat(), dst.width().toFloat(), dst.height().toFloat(),
                KoolColor(paint.color.red() / 255f, paint.color.green() / 255f, paint.color.blue() / 255f, paint.color.alpha() / 255f),
                paint.color.alpha() / 255f)
        }
    }

    override fun a(texture: Texture, src: Rect, dst: RectF, paint: Paint) {
        val handle = getTextureHandle(texture)
        if (handle != null) {
            koolGraphics.drawTexture(handle, src.left, src.top, src.width(), src.height(),
                dst.left, dst.top, dst.width(), dst.height(),
                KoolColor(paint.color.red() / 255f, paint.color.green() / 255f, paint.color.blue() / 255f, paint.color.alpha() / 255f),
                paint.color.alpha() / 255f)
        }
    }

    override fun a(x: Float, y: Float, size: Float, paint: Paint) {
        koolGraphics.fillCircle(x, y, size / 2f,
            KoolColor(paint.color.red() / 255f, paint.color.green() / 255f, paint.color.blue() / 255f, paint.color.alpha() / 255f))
    }

    override fun a(i: Int, mode: PorterDuff.Mode) {
        // Blend mode switching
    }

    override fun a(i: Int) {
        // Color setting
    }

    override fun a(x1: Float, y1: Float, x2: Float, y2: Float, paint: Paint) {
        koolGraphics.drawLine(x1, y1, x2, y2,
            KoolColor(paint.color.red() / 255f, paint.color.green() / 255f, paint.color.blue() / 255f, paint.color.alpha() / 255f))
    }

    override fun a(arr: FloatArray, i: Int, i2: Int, paint: Paint) {
        // Vertex array drawing - triangle fan
        if (arr.size >= 6) {
            koolGraphics.fillTriangle(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5],
                KoolColor(paint.color.red() / 255f, paint.color.green() / 255f, paint.color.blue() / 255f, paint.color.alpha() / 255f))
        }
    }

    override fun a(rect: Rect, paint: Paint) {
        koolGraphics.fillRect(rect.left.toFloat(), rect.top.toFloat(), rect.width().toFloat(), rect.height().toFloat(),
            KoolColor(paint.color.red() / 255f, paint.color.green() / 255f, paint.color.blue() / 255f, paint.color.alpha() / 255f))
    }

    override fun a(rectF: RectF, paint: Paint) {
        koolGraphics.fillRect(rectF.left, rectF.top, rectF.width(), rectF.height(),
            KoolColor(paint.color.red() / 255f, paint.color.green() / 255f, paint.color.blue() / 255f, paint.color.alpha() / 255f))
    }

    override fun a(str: String, x: Float, y: Float, paint: Paint) {
        koolGraphics.drawText(str, x, y,
            KoolColor(paint.color.red() / 255f, paint.color.green() / 255f, paint.color.blue() / 255f, paint.color.alpha() / 255f))
    }

    override fun a() {
        koolGraphics.flush()
    }

    override fun a(x: Float, y: Float, z: Float) {
        koolGraphics.translate(x, y)
    }

    override fun b() {
        // Restore - balanced with a(true)
    }

    override fun a(x: Float, y: Float) {
        // Move cursor - no-op in GL
    }

    override fun a(x: Float, y: Float, w: Float, h: Float) {
        koolGraphics.setClipRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    override fun a(texture: Texture) {
        // Bind texture
    }

    override fun b(x: Float, y: Float) {
        // Scale
        koolGraphics.scale(x, y)
    }

    override fun a(op: GraphicsOperation) {
        op.run(this)
    }

    override fun a(bitmap: Bitmap) {
        // Not used on desktop
    }

    override fun a(lock: Lock) {}
    override fun b(lock: Lock) {}

    override fun a(shader: ShaderProgram?): Boolean {
        // Shader support stub
        return true
    }

    companion object {
        private val textureHandleMap = mutableMapOf<Int, KoolTextureHandle>()
        private var nextTexHandle = 10000

        fun getTextureHandle(texture: Texture): KoolTextureHandle? {
            return textureHandleMap[texture.hashCode()]
        }

        fun registerTexture(texture: Texture, handle: KoolTextureHandle) {
            textureHandleMap[texture.hashCode()] = handle
        }
    }
}