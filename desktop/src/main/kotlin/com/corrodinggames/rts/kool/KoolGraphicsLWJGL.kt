package com.corrodinggames.rts.kool

import com.corrodinggames.rts.gameFramework.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class KoolGraphicsLWJGL : KoolGraphics {

    private var nextTextureId = 1
    private val textures = mutableMapOf<Int, TextureData>()
    private var nextFontId = 1
    private val fonts = mutableMapOf<Int, FontData>()
    private var nextShaderId = 1
    private val shaders = mutableMapOf<Int, ShaderData>()

    // Transform stack
    private val transformStack = mutableListOf<Matrix4>()
    private var currentMatrix = Matrix4()

    // State tracking
    private var currentTexture: Int = 0
    private var currentShader: Int = 0
    private var clipRect: ClipRect? = null

    override fun clear(r: Float, g: Float, b: Float, a: Float) {
        GL11.glClearColor(r, g, b, a)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    }

    override fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        GL11.glViewport(x, y, width, height)
    }

    override fun fillRect(x: Float, y: Float, w: Float, h: Float, color: KoolColor) {
        bindColor(color)
        GL11.glBegin(GL11.GL_TRIANGLE_FAN)
        GL11.glVertex2f(x, y)
        GL11.glVertex2f(x + w, y)
        GL11.glVertex2f(x + w, y + h)
        GL11.glVertex2f(x, y + h)
        GL11.glEnd()
    }

    override fun drawRect(x: Float, y: Float, w: Float, h: Float, color: KoolColor, lineWidth: Float) {
        GL11.glLineWidth(lineWidth)
        bindColor(color)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        GL11.glVertex2f(x, y)
        GL11.glVertex2f(x + w, y)
        GL11.glVertex2f(x + w, y + h)
        GL11.glVertex2f(x, y + h)
        GL11.glEnd()
    }

    override fun fillCircle(cx: Float, cy: Float, radius: Float, color: KoolColor) {
        bindColor(color)
        GL11.glBegin(GL11.GL_TRIANGLE_FAN)
        GL11.glVertex2f(cx, cy)
        val segments = 32
        for (i in 0..segments) {
            val angle = Math.toRadians((i * 360.0 / segments).toDouble()).toFloat()
            GL11.glVertex2f(cx + kotlin.math.cos(angle) * radius, cy + kotlin.math.sin(angle) * radius)
        }
        GL11.glEnd()
    }

    override fun drawCircle(cx: Float, cy: Float, radius: Float, color: KoolColor, lineWidth: Float) {
        GL11.glLineWidth(lineWidth)
        bindColor(color)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        val segments = 32
        for (i in 0 until segments) {
            val angle = Math.toRadians((i * 360.0 / segments).toDouble()).toFloat()
            GL11.glVertex2f(cx + kotlin.math.cos(angle) * radius, cy + kotlin.math.sin(angle) * radius)
        }
        GL11.glEnd()
    }

    override fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, color: KoolColor, lineWidth: Float) {
        GL11.glLineWidth(lineWidth)
        bindColor(color)
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex2f(x1, y1)
        GL11.glVertex2f(x2, y2)
        GL11.glEnd()
    }

    override fun fillTriangle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, color: KoolColor) {
        bindColor(color)
        GL11.glBegin(GL11.GL_TRIANGLES)
        GL11.glVertex2f(x1, y1)
        GL11.glVertex2f(x2, y2)
        GL11.glVertex2f(x3, y3)
        GL11.glEnd()
    }

    override fun createTexture(width: Int, height: Int, pixels: ByteArray): KoolTextureHandle {
        val id = nextTextureId++
        val buf = ByteBuffer.allocateDirect(pixels.size)
        buf.put(pixels).flip()
        val glId = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glId)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf)
        textures[id] = TextureData(glId, width, height)
        return KoolTextureHandle(id)
    }

    override fun loadTexture(rgbaPixels: ByteArray, width: Int, height: Int): KoolTextureHandle {
        return createTexture(width, height, rgbaPixels)
    }

    override fun drawTexture(
        texture: KoolTextureHandle,
        srcX: Int, srcY: Int, srcW: Int, srcH: Int,
        dstX: Float, dstY: Float, dstW: Float, dstH: Float,
        color: KoolColor,
        alpha: Float,
    ) {
        val tex = textures[texture.id] ?: return
        bindTexture(tex.glId)
        bindColor(color, alpha)

        val texW = tex.width.toFloat()
        val texH = tex.height.toFloat()
        val u1 = srcX / texW
        val v1 = srcY / texH
        val u2 = (srcX + srcW) / texW
        val v2 = (srcY + srcH) / texH

        GL11.glBegin(GL11.GL_TRIANGLE_FAN)
        GL11.glTexCoord2f(u1, v1)
        GL11.glVertex2f(dstX, dstY)
        GL11.glTexCoord2f(u2, v1)
        GL11.glVertex2f(dstX + dstW, dstY)
        GL11.glTexCoord2f(u2, v2)
        GL11.glVertex2f(dstX + dstW, dstY + dstH)
        GL11.glTexCoord2f(u1, v2)
        GL11.glVertex2f(dstX, dstY + dstH)
        GL11.glEnd()
    }

    override fun drawTexture(texture: KoolTextureHandle, x: Float, y: Float, color: KoolColor, alpha: Float) {
        val tex = textures[texture.id] ?: return
        drawTexture(texture, 0, 0, tex.width, tex.height, x, y, tex.width.toFloat(), tex.height.toFloat(), color, alpha)
    }

    override fun deleteTexture(texture: KoolTextureHandle) {
        val tex = textures.remove(texture.id) ?: return
        GL11.glDeleteTextures(tex.glId)
    }

    override fun setClipRect(x: Int?, y: Int?, w: Int?, h: Int?) {
        if (x == null || y == null || w == null || h == null) {
            clipRect = null
            GL11.glDisable(GL11.GL_SCISSOR_TEST)
        } else {
            clipRect = ClipRect(x, y, w, h)
            GL11.glEnable(GL11.GL_SCISSOR_TEST)
            GL11.glScissor(x, y, w, h)
        }
    }

    override fun pushTransform() {
        transformStack.add(currentMatrix.copy())
    }

    override fun popTransform() {
        if (transformStack.isNotEmpty()) {
            currentMatrix = transformStack.removeAt(transformStack.size - 1)
            applyCurrentMatrix()
        }
    }

    override fun translate(x: Float, y: Float) {
        currentMatrix = currentMatrix.translated(x, y, 0f)
        applyCurrentMatrix()
    }

    override fun scale(sx: Float, sy: Float) {
        currentMatrix = currentMatrix.scaled(sx, sy, 1f)
        applyCurrentMatrix()
    }

    override fun rotate(angleDeg: Float) {
        currentMatrix = currentMatrix.rotatedZ(Math.toRadians(angleDeg.toDouble()).toFloat())
        applyCurrentMatrix()
    }

    override fun drawText(text: String, x: Float, y: Float, color: KoolColor, font: KoolFontHandle?) {
        // Basic text rendering using bitmap textures would go here.
        // For now, this is a stub - real text rendering will be added when
        // migrating the existing Slick font/text rendering code.
        // The texture atlas font system from SlickGraphicsEngine will be ported.
    }

    override fun measureText(text: String, font: KoolFontHandle?): Float {
        return text.length * 8f // Placeholder
    }

    override fun loadFont(path: String, size: Int, style: KoolFontStyle): KoolFontHandle {
        val id = nextFontId++
        fonts[id] = FontData(path, size, style)
        return KoolFontHandle(id)
    }

    override fun deleteFont(font: KoolFontHandle) {
        fonts.remove(font.id)
    }

    override fun createShader(vertexSource: String, fragmentSource: String): KoolShaderHandle {
        val glId = GL20.glCreateProgram()
        val vsId = compileShader(GL20.GL_VERTEX_SHADER, vertexSource)
        val fsId = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource)
        GL20.glAttachShader(glId, vsId)
        GL20.glAttachShader(glId, fsId)
        GL20.glLinkProgram(glId)
        if (GL20.glGetProgrami(glId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw RuntimeException("Shader link error: " + GL20.glGetProgramInfoLog(glId))
        }
        val id = nextShaderId++
        shaders[id] = ShaderData(glId, vsId, fsId)
        return KoolShaderHandle(id)
    }

    override fun useShader(shader: KoolShaderHandle?) {
        if (shader == null) {
            currentShader = 0
            GL20.glUseProgram(0)
        } else {
            val data = shaders[shader.id] ?: return
            currentShader = data.glId
            GL20.glUseProgram(data.glId)
        }
    }

    override fun deleteShader(shader: KoolShaderHandle) {
        val data = shaders.remove(shader.id) ?: return
        GL20.glDetachShader(data.glId, data.vsId)
        GL20.glDetachShader(data.glId, data.fsId)
        GL20.glDeleteShader(data.vsId)
        GL20.glDeleteShader(data.fsId)
        GL20.glDeleteProgram(data.glId)
    }

    override fun setUniform(name: String, value: Float) {
        val loc = GL20.glGetUniformLocation(currentShader, name)
        GL20.glUniform1f(loc, value)
    }

    override fun setUniform(name: String, v0: Float, v1: Float) {
        val loc = GL20.glGetUniformLocation(currentShader, name)
        GL20.glUniform2f(loc, v0, v1)
    }

    override fun setUniform(name: String, v0: Float, v1: Float, v2: Float) {
        val loc = GL20.glGetUniformLocation(currentShader, name)
        GL20.glUniform3f(loc, v0, v1, v2)
    }

    override fun setUniform(name: String, v0: Float, v1: Float, v2: Float, v3: Float) {
        val loc = GL20.glGetUniformLocation(currentShader, name)
        GL20.glUniform4f(loc, v0, v1, v2, v3)
    }

    override fun setUniform(name: String, value: Int) {
        val loc = GL20.glGetUniformLocation(currentShader, name)
        GL20.glUniform1i(loc, value)
    }

    override fun setUniform(name: String, textureUnit: Int, texture: KoolTextureHandle) {
        val tex = textures[texture.id] ?: return
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnit)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.glId)
        val loc = GL20.glGetUniformLocation(currentShader, name)
        GL20.glUniform1i(loc, textureUnit)
    }

    override fun flush() {
        GL11.glFlush()
    }

    fun dispose() {
        textures.values.forEach { GL11.glDeleteTextures(it.glId) }
        textures.clear()
        shaders.values.forEach {
            GL20.glDetachShader(it.glId, it.vsId)
            GL20.glDetachShader(it.glId, it.fsId)
            GL20.glDeleteShader(it.vsId)
            GL20.glDeleteShader(it.fsId)
            GL20.glDeleteProgram(it.glId)
        }
        shaders.clear()
        fonts.clear()
    }

    // ======================== Private Helpers ========================

    private fun bindColor(color: KoolColor, alpha: Float = color.a) {
        GL11.glColor4f(color.r, color.g, color.b, alpha)
    }

    private fun bindTexture(glId: Int) {
        if (currentTexture != glId) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, glId)
            currentTexture = glId
        }
    }

    private fun compileShader(type: Int, source: String): Int {
        val id = GL20.glCreateShader(type)
        GL20.glShaderSource(id, source)
        GL20.glCompileShader(id)
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            val log = GL20.glGetShaderInfoLog(id)
            GL20.glDeleteShader(id)
            throw RuntimeException("Shader compile error: $log")
        }
        return id
    }

    private fun applyCurrentMatrix() {
        val buf = BufferUtils.createFloatBuffer(16)
        currentMatrix.toOpenGLBuffer(buf)
        buf.flip()
        GL11.glLoadMatrixf(buf)
    }

    private data class TextureData(val glId: Int, val width: Int, val height: Int)
    private data class FontData(val path: String, val size: Int, val style: KoolFontStyle)
    private data class ShaderData(val glId: Int, val vsId: Int, val fsId: Int)
    private data class ClipRect(val x: Int, val y: Int, val w: Int, val h: Int)
}

/**
 * Simple 4x4 matrix for 2D transform stacking.
 */
class Matrix4 {
    private val m = FloatArray(16).also {
        it[0] = 1f; it[5] = 1f; it[10] = 1f; it[15] = 1f // identity
    }

    fun copy(): Matrix4 {
        val out = Matrix4()
        m.copyInto(out.m)
        return out
    }

    fun translated(x: Float, y: Float, z: Float): Matrix4 {
        val out = copy()
        out.m[12] = m[0] * x + m[4] * y + m[8] * z + m[12]
        out.m[13] = m[1] * x + m[5] * y + m[9] * z + m[13]
        out.m[14] = m[2] * x + m[6] * y + m[10] * z + m[14]
        out.m[15] = m[3] * x + m[7] * y + m[11] * z + m[15]
        return out
    }

    fun scaled(x: Float, y: Float, z: Float): Matrix4 {
        val out = copy()
        out.m[0] = m[0] * x; out.m[1] = m[1] * x; out.m[2] = m[2] * x; out.m[3] = m[3] * x
        out.m[4] = m[4] * y; out.m[5] = m[5] * y; out.m[6] = m[6] * y; out.m[7] = m[7] * y
        out.m[8] = m[8] * z; out.m[9] = m[9] * z; out.m[10] = m[10] * z; out.m[11] = m[11] * z
        return out
    }

    fun rotatedZ(angleRad: Float): Matrix4 {
        val c = kotlin.math.cos(angleRad)
        val s = kotlin.math.sin(angleRad)
        val out = copy()
        out.m[0] = m[0] * c + m[4] * s
        out.m[1] = m[1] * c + m[5] * s
        out.m[2] = m[2] * c + m[6] * s
        out.m[3] = m[3] * c + m[7] * s
        out.m[4] = m[0] * -s + m[4] * c
        out.m[5] = m[1] * -s + m[5] * c
        out.m[6] = m[2] * -s + m[6] * c
        out.m[7] = m[3] * -s + m[7] * c
        return out
    }

    fun toOpenGLBuffer(buf: FloatBuffer) {
        buf.put(m)
    }
}