package com.corrodinggames.rts.gameFramework

/**
 * KoolEngine cross-platform abstraction.
 *
 * Core lifecycle: init -> update (each frame) -> dispose.
 * Platform-specific backends (desktop, android) provide concrete implementations
 * by injecting the singleton into [instance].
 */
abstract class KoolEngine {

    // ======================== Lifecycle ========================

    /** Called once at startup to initialise the engine (window, GL context, etc.). */
    abstract fun init(config: KoolConfig)

    /** Called every frame. dt = delta time in seconds. */
    abstract fun update(dt: Float)

    /** Called once at shutdown to release native resources. */
    abstract fun dispose()

    // ======================== Window ========================

    abstract val window: KoolWindow

    // ======================== Renderer ========================

    abstract val graphics: KoolGraphics

    companion object {
        /** The active platform-specific engine instance. */
        @Volatile
        var instance: KoolEngine? = null
    }
}

data class KoolConfig(
    val title: String = "RWX",
    val width: Int = 1000,
    val height: Int = 733,
    val fullscreen: Boolean = false,
    val resizable: Boolean = true,
    val vsync: Boolean = true,
    val samples: Int = 0,           // MSAA samples
    val allowSoftwareOpenGL: Boolean = false,
)

interface KoolWindow {
    val width: Int
    val height: Int
    val isCloseRequested: Boolean
    val isVisible: Boolean

    fun setTitle(title: String)
    fun setSize(w: Int, h: Int)
    fun setFullscreen(fullscreen: Boolean)
    fun setVSync(enabled: Boolean)
    fun setIcon(pixels: IntArray, w: Int, h: Int)
    fun swapBuffers()
    fun processMessages()
    /** Request the window to close at next opportunity. */
    fun requestClose()
}

// ======================== Graphics / Rendering ========================

interface KoolGraphics {
    /** Clear the back buffer. */
    fun clear(r: Float = 0f, g: Float = 0f, b: Float = 0f, a: Float = 1f)

    /** Set the viewport (in device coordinates). */
    fun setViewport(x: Int, y: Int, width: Int, height: Int)

    // ---- Shape drawing (immediate mode style) ----

    /** Draw a filled rectangle. */
    fun fillRect(x: Float, y: Float, w: Float, h: Float, color: KoolColor)

    /** Draw a rectangle outline. */
    fun drawRect(x: Float, y: Float, w: Float, h: Float, color: KoolColor, lineWidth: Float = 1f)

    /** Draw a filled circle. */
    fun fillCircle(cx: Float, cy: Float, radius: Float, color: KoolColor)

    /** Draw a circle outline. */
    fun drawCircle(cx: Float, cy: Float, radius: Float, color: KoolColor, lineWidth: Float = 1f)

    /** Draw a line segment. */
    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, color: KoolColor, lineWidth: Float = 1f)

    /** Draw a triangle fan (list of vertices in CCW order). */
    fun fillTriangle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, color: KoolColor)

    // ---- Texture Rendering ----

    /** Create a texture from pixel data. Returns a handle. */
    fun createTexture(width: Int, height: Int, pixels: ByteArray): KoolTextureHandle

    /** Load a texture from raw RGBA data. */
    fun loadTexture(rgbaPixels: ByteArray, width: Int, height: Int): KoolTextureHandle

    /** Draw a texture (or portion thereof) at the given position. */
    fun drawTexture(
        texture: KoolTextureHandle,
        srcX: Int, srcY: Int, srcW: Int, srcH: Int,
        dstX: Float, dstY: Float, dstW: Float, dstH: Float,
        color: KoolColor = KoolColor.WHITE,
        alpha: Float = 1f,
    )

    /** Draw a texture at the given position with full size. */
    fun drawTexture(texture: KoolTextureHandle, x: Float, y: Float, color: KoolColor = KoolColor.WHITE, alpha: Float = 1f)

    /** Delete a texture and free GPU memory. */
    fun deleteTexture(texture: KoolTextureHandle)

    /** Set the current clip rect (null = no clipping). */
    fun setClipRect(x: Int?, y: Int?, w: Int?, h: Int?)

    // ---- Transform / State ----

    /** Push the current transformation matrix onto the stack. */
    fun pushTransform()

    /** Pop the current transformation matrix. */
    fun popTransform()

    /** Translate the current transformation matrix. */
    fun translate(x: Float, y: Float)

    /** Scale the current transformation matrix. */
    fun scale(sx: Float, sy: Float)

    /** Rotate the current transformation matrix (angle in degrees). */
    fun rotate(angleDeg: Float)

    // ---- Text ----

    /** Draw text at the given position. */
    fun drawText(text: String, x: Float, y: Float, color: KoolColor = KoolColor.WHITE, font: KoolFontHandle? = null)

    /** Measure the width (in pixels) of a string when rendered. */
    fun measureText(text: String, font: KoolFontHandle? = null): Float

    /** Load a font from a TrueType/OpenType file path. Returns a handle. */
    fun loadFont(path: String, size: Int, style: KoolFontStyle = KoolFontStyle.NORMAL): KoolFontHandle

    /** Release a font. */
    fun deleteFont(font: KoolFontHandle)

    // ---- Shaders ----

    /** Create a shader program from vertex + fragment source. Returns a handle. */
    fun createShader(vertexSource: String, fragmentSource: String): KoolShaderHandle

    /** Use a shader program (null = fixed function). */
    fun useShader(shader: KoolShaderHandle?)

    /** Delete a shader program. */
    fun deleteShader(shader: KoolShaderHandle)

    /** Set a uniform in the current shader. */
    fun setUniform(name: String, value: Float)
    fun setUniform(name: String, v0: Float, v1: Float)
    fun setUniform(name: String, v0: Float, v1: Float, v2: Float)
    fun setUniform(name: String, v0: Float, v1: Float, v2: Float, v3: Float)
    fun setUniform(name: String, value: Int)
    fun setUniform(name: String, textureUnit: Int, texture: KoolTextureHandle)

    /** Flush pending draw calls. */
    fun flush()
}

data class KoolColor(val r: Float, val g: Float, val b: Float, val a: Float = 1f) {
    companion object {
        val WHITE = KoolColor(1f, 1f, 1f)
        val BLACK = KoolColor(0f, 0f, 0f)
        val RED = KoolColor(1f, 0f, 0f)
        val GREEN = KoolColor(0f, 1f, 0f)
        val BLUE = KoolColor(0f, 0f, 1f)
        val TRANSPARENT = KoolColor(0f, 0f, 0f, 0f)
    }
}

enum class KoolFontStyle { NORMAL, BOLD, ITALIC, BOLD_ITALIC }

/** Opaque handle for a GPU texture. */
class KoolTextureHandle(val id: Int)

/** Opaque handle for a loaded font. */
class KoolFontHandle(val id: Int)

/** Opaque handle for a shader program. */
class KoolShaderHandle(val id: Int)

// ======================== Audio ========================

interface KoolAudio {
    /** Load a sound effect from a file. Returns a handle. */
    fun loadSound(filePath: String): KoolSoundHandle

    /** Load a sound effect from raw PCM data. */
    fun loadSound(pcmData: ByteArray, sampleRate: Int, numChannels: Int): KoolSoundHandle

    /** Play a sound effect (non-looping). Returns the source id for control. */
    fun playSound(sound: KoolSoundHandle, volume: Float = 1f, pitch: Float = 1f, pan: Float = 0f): Int

    /** Stop a playing sound source. */
    fun stopSound(sourceId: Int)

    /** Set volume for a playing source (0-1). */
    fun setSoundVolume(sourceId: Int, volume: Float)

    /** Load a music track from a file. */
    fun loadMusic(filePath: String): KoolMusicHandle

    /** Start playing music (looping by default). */
    fun playMusic(music: KoolMusicHandle, looping: Boolean = true, volume: Float = 1f)

    /** Pause music playback. */
    fun pauseMusic()

    /** Resume music playback. */
    fun resumeMusic()

    /** Stop music playback. */
    fun stopMusic()

    /** Set music volume (0-1). */
    fun setMusicVolume(volume: Float)

    /** Update audio system (must be called each frame). */
    fun update()

    /** Delete a sound and free resources. */
    fun deleteSound(sound: KoolSoundHandle)

    /** Delete music and free resources. */
    fun deleteMusic(music: KoolMusicHandle)

    /** Whether audio hardware is available. */
    val hasDevice: Boolean
}

class KoolSoundHandle(val id: Int)
class KoolMusicHandle(val id: Int)

// ======================== Input ========================

interface KoolInput {
    /** Whether a key is currently down. */
    fun isKeyDown(key: KoolKey): Boolean

    /** Whether a key was just pressed this frame. */
    fun isKeyJustPressed(key: KoolKey): Boolean

    /** Whether a mouse button is currently down. */
    fun isMouseButtonDown(button: KoolMouseButton): Boolean

    /** Whether a mouse button was just pressed this frame. */
    fun isMouseButtonJustPressed(button: KoolMouseButton): Boolean

    /** Current mouse X position in window coordinates. */
    val mouseX: Int

    /** Current mouse Y position in window coordinates. */
    val mouseY: Int

    /** Mouse delta X since last frame. */
    val mouseDeltaX: Int

    /** Mouse delta Y since last frame. */
    val mouseDeltaY: Int

    /** Scroll wheel delta since last frame. */
    val scrollDelta: Int

    /** Text input that occurred this frame. */
    val inputText: String

    /** Called at the end of each frame to reset transient state. */
    fun endFrame()

    /** Set the cursor position. */
    fun setCursorPosition(x: Int, y: Int)

    /** Show/hide the system cursor. */
    fun showCursor(visible: Boolean)
}

enum class KoolMouseButton {
    LEFT, RIGHT, MIDDLE, BUTTON4, BUTTON5
}

enum class KoolKey {
    NONE,
    A, B, C, D, E, F, G, H, I, J, K, L, M,
    N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
    NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9,
    F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12,
    LEFT_SHIFT, RIGHT_SHIFT, LEFT_CONTROL, RIGHT_CONTROL,
    LEFT_ALT, RIGHT_ALT,
    SPACE, ENTER, BACKSPACE, TAB, ESCAPE, DELETE, INSERT,
    HOME, END, PAGE_UP, PAGE_DOWN,
    UP, DOWN, LEFT, RIGHT,
    COMMA, PERIOD, SLASH, SEMICOLON, APOSTROPHE,
    LEFT_BRACKET, RIGHT_BRACKET, BACKSLASH, MINUS, EQUALS,
    CAPS_LOCK, SCROLL_LOCK, NUM_LOCK,
    NUMPAD_0, NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4,
    NUMPAD_5, NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9,
    NUMPAD_ADD, NUMPAD_SUBTRACT, NUMPAD_MULTIPLY, NUMPAD_DIVIDE,
    NUMPAD_DECIMAL, NUMPAD_ENTER,
}

// ======================== UI ========================

interface KoolUI {
    /** Initialise the UI system. Called once after engine init. */
    fun init()

    /** Load a document/sheet from the given file path. */
    fun loadDocument(path: String): KoolUIDocumentHandle

    /** Unload a document. */
    fun unloadDocument(doc: KoolUIDocumentHandle)

    /** Show a document. */
    fun showDocument(doc: KoolUIDocumentHandle)

    /** Hide a document. */
    fun hideDocument(doc: KoolUIDocumentHandle)

    /** Called once per frame to render the UI (after scene rendering). */
    fun render(graphics: KoolGraphics)

    /** Update UI animations/logic. dt in seconds. */
    fun update(dt: Float)

    /** Called when the window/viewport is resized. */
    fun resize(width: Int, height: Int)

    /** Send a mouse event to the UI system. */
    fun sendMouseEvent(x: Int, y: Int, button: Int, pressed: Boolean, scrolled: Int)

    /** Send a key event to the UI system. */
    fun sendKeyEvent(key: KoolKey, pressed: Boolean, text: String)

    /** Shut down the UI system. */
    fun dispose()

    /** The currently active document (or null). */
    val activeDocument: KoolUIDocumentHandle?

    /** Load a font for use in the UI system. */
    fun loadFont(path: String, cssName: String? = null)

    /** Execute a script in the UI context. */
    fun executeScript(script: String)
}

class KoolUIDocumentHandle(val id: Int)