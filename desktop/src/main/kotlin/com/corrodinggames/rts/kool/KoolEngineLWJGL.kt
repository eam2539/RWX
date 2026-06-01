package com.corrodinggames.rts.kool

import com.corrodinggames.rts.gameFramework.*
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil

/**
 * LWJGL 3 / GLFW implementation of the kool-engine.
 */
class KoolEngineLWJGL : KoolEngine() {

    private var _window: KoolWindowLWJGL? = null
    private var _graphics: KoolGraphicsLWJGL? = null
    private var _input: KoolInputGLFW? = null
    private var _audio: KoolAudioLWJGL? = null
    private var _initialized = false

    override lateinit var window: KoolWindow
        private set
    override lateinit var graphics: KoolGraphics
        private set
    val input: KoolInputGLFW get() = _input!!
    val audio: KoolAudioLWJGL get() = _audio!!

    override fun init(config: KoolConfig) {
        if (_initialized) return

        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw RuntimeException("Failed to initialize GLFW")
        }

        // Configure GLFW window hints
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, if (config.resizable) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, config.samples)

        // Create the window
        val handle = GLFW.glfwCreateWindow(config.width, config.height, config.title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (handle == MemoryUtil.NULL) {
            GLFW.glfwTerminate()
            throw RuntimeException("Failed to create GLFW window")
        }

        // Center the window
        val videoMode: GLFWVidMode? = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
        if (videoMode != null) {
            GLFW.glfwSetWindowPos(
                handle,
                (videoMode.width() - config.width) / 2,
                (videoMode.height() - config.height) / 2
            )
        }

        if (config.fullscreen) {
            val monitor = GLFW.glfwGetPrimaryMonitor()
            GLFW.glfwSetWindowMonitor(handle, monitor, 0, 0, videoMode?.width() ?: config.width, videoMode?.height() ?: config.height, GLFW.GLFW_DONT_CARE)
        }

        // Make OpenGL context current
        GLFW.glfwMakeContextCurrent(handle)
        GLFW.glfwSwapInterval(if (config.vsync) 1 else 0)
        GLFW.glfwShowWindow(handle)

        // Initialize OpenGL
        GL.createCapabilities()

        // Set up the default OpenGL state
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glClearColor(0f, 0f, 0f, 1f)

        // Create subsystems
        _window = KoolWindowLWJGL(handle, config)
        _graphics = KoolGraphicsLWJGL()
        _input = KoolInputGLFW(handle)
        _audio = KoolAudioLWJGL()

        window = _window!!
        graphics = _graphics!!

        // Set input callbacks on GLFW
        _input!!.setupCallbacks(handle)

        _initialized = true

        // Register as singleton
        instance = this
    }

    override fun update(dt: Float) {
        _input!!.poll()
        _audio!!.update()
        GLFW.glfwPollEvents()
    }

    override fun dispose() {
        _audio!!.dispose()
        _graphics!!.dispose()
        _window!!.dispose()
        _input!!.dispose()

        GLFW.glfwTerminate()
        _initialized = false
        instance = null
    }
}

class KoolWindowLWJGL(
    val handle: Long,
    config: KoolConfig,
) : KoolWindow {

    override var width: Int = config.width
        private set
    override var height: Int = config.height
        private set
    override val isCloseRequested: Boolean get() = GLFW.glfwWindowShouldClose(handle)
    override val isVisible: Boolean get() = !GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_ICONIFIED)

    override fun setTitle(title: String) {
        GLFW.glfwSetWindowTitle(handle, title)
    }

    override fun setSize(w: Int, h: Int) {
        GLFW.glfwSetWindowSize(handle, w, h)
        width = w
        height = h
    }

    override fun setFullscreen(fullscreen: Boolean) {
        if (fullscreen) {
            val monitor = GLFW.glfwGetPrimaryMonitor()
            val mode = GLFW.glfwGetVideoMode(monitor)
            if (mode != null) {
                GLFW.glfwSetWindowMonitor(handle, monitor, 0, 0, mode.width(), mode.height(), GLFW.GLFW_DONT_CARE)
                width = mode.width()
                height = mode.height()
            }
        } else {
            GLFW.glfwSetWindowMonitor(handle, MemoryUtil.NULL, 100, 100, width, height, GLFW.GLFW_DONT_CARE)
        }
    }

    override fun setVSync(enabled: Boolean) {
        GLFW.glfwSwapInterval(if (enabled) 1 else 0)
    }

    override fun setIcon(pixels: IntArray, w: Int, h: Int) {
        val buf = org.lwjgl.system.MemoryUtil.memAlloc(pixels.size * 4)
        buf.asIntBuffer().put(pixels).flip()
        GLFW.glfwSetWindowIcon(handle, org.lwjgl.glfw.GLFWImage.malloc(1).apply {
            width(w); height(h); pixels(buf)
        })
        buf.free()
    }

    override fun swapBuffers() {
        GLFW.glfwSwapBuffers(handle)
    }

    override fun processMessages() {
        GLFW.glfwPollEvents()
    }

    override fun requestClose() {
        GLFW.glfwSetWindowShouldClose(handle, true)
    }

    internal fun onResized(w: Int, h: Int) {
        width = w
        height = h
    }

    fun dispose() {
        Callbacks.glfwFreeCallbacks(handle)
    }
}

class KoolInputGLFW(val windowHandle: Long) : KoolInput {

    private val keyStates = BooleanArray(GLFW.GLFW_KEY_LAST + 1)
    private val justPressedKeyStates = BooleanArray(GLFW.GLFW_KEY_LAST + 1)
    private val mouseButtonStates = BooleanArray(5) // GLFW supports up to 5 mouse buttons
    private val justPressedMouseStates = BooleanArray(5)

    private var _mouseX = 0
    private var _mouseY = 0
    private var _mouseDeltaX = 0
    private var _mouseDeltaY = 0
    private var _scrollDelta = 0
    private val _inputText = StringBuilder()

    private var wasMouseGrabbed = false
    private var lastMouseX = 0
    private var lastMouseY = 0

    override fun isKeyDown(key: KoolKey): Boolean {
        val glfwKey = koolKeyToGLFW(key) ?: return false
        return if (glfwKey in keyStates.indices) keyStates[glfwKey] else false
    }

    override fun isKeyJustPressed(key: KoolKey): Boolean {
        val glfwKey = koolKeyToGLFW(key) ?: return false
        return if (glfwKey in justPressedKeyStates.indices) justPressedKeyStates[glfwKey] else false
    }

    override fun isMouseButtonDown(button: KoolMouseButton): Boolean {
        val idx = koolMouseToGLFW(button)
        return if (idx in mouseButtonStates.indices) mouseButtonStates[idx] else false
    }

    override fun isMouseButtonJustPressed(button: KoolMouseButton): Boolean {
        val idx = koolMouseToGLFW(button)
        return if (idx in justPressedMouseStates.indices) justPressedMouseStates[idx] else false
    }

    override val mouseX: Int get() = _mouseX
    override val mouseY: Int get() = _mouseY
    override val mouseDeltaX: Int get() = _mouseDeltaX
    override val mouseDeltaY: Int get() = _mouseDeltaY
    override val scrollDelta: Int get() = _scrollDelta
    override val inputText: String get() = _inputText.toString()

    override fun endFrame() {
        justPressedKeyStates.fill(false)
        justPressedMouseStates.fill(false)
        _mouseDeltaX = 0
        _mouseDeltaY = 0
        _scrollDelta = 0
        _inputText.clear()
    }

    override fun setCursorPosition(x: Int, y: Int) {
        GLFW.glfwSetCursorPos(windowHandle, x.toDouble(), y.toDouble())
    }

    override fun showCursor(visible: Boolean) {
        GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR,
            if (visible) GLFW.GLFW_CURSOR_NORMAL else GLFW.GLFW_CURSOR_HIDDEN)
    }

    fun poll() {
        // Get current cursor position for delta calculation
        val newXBuf = org.lwjgl.glfw.GLFW.glfwGetCursorPos(windowHandle)
        if (newXBuf != null) {
            val newX = newXBuf[0].toInt()
            val newY = newXBuf[1].toInt()
            _mouseDeltaX = newX - _mouseX
            _mouseDeltaY = newY - _mouseY
            _mouseX = newX
            _mouseY = newY
        }
    }

    internal fun setupCallbacks(handle: Long) {
        // Key callback
        GLFW.glfwSetKeyCallback(handle) { _, key, _, action, _ ->
            if (key in keyStates.indices) {
                when (action) {
                    GLFW.GLFW_PRESS -> {
                        if (!keyStates[key]) justPressedKeyStates[key] = true
                        keyStates[key] = true
                    }
                    GLFW.GLFW_RELEASE -> {
                        keyStates[key] = false
                    }
                }
            }
        }

        // Mouse button callback
        GLFW.glfwSetMouseButtonCallback(handle) { _, button, action, _ ->
            if (button in mouseButtonStates.indices) {
                when (action) {
                    GLFW.GLFW_PRESS -> {
                        if (!mouseButtonStates[button]) justPressedMouseStates[button] = true
                        mouseButtonStates[button] = true
                    }
                    GLFW.GLFW_RELEASE -> {
                        mouseButtonStates[button] = false
                    }
                }
            }
        }

        // Cursor position callback
        GLFW.glfwSetCursorPosCallback(handle) { _, xpos, ypos ->
            val newX = xpos.toInt()
            val newY = ypos.toInt()
            _mouseDeltaX = newX - _mouseX
            _mouseDeltaY = newY - _mouseY
            _mouseX = newX
            _mouseY = newY
        }

        // Scroll callback
        GLFW.glfwSetScrollCallback(handle) { _, _, yoffset ->
            _scrollDelta = yoffset.toInt()
        }

        // Character input callback
        GLFW.glfwSetCharCallback(handle) { _, codepoint ->
            _inputText.append(codepoint.toChar())
        }

        // Window resize callback
        GLFW.glfwSetWindowSizeCallback(handle) { _, w, h ->
            GL11.glViewport(0, 0, w, h)
        }
    }

    fun dispose() {}

    companion object {
        private val KOOL_KEY_MAP = mapOf(
            KoolKey.A to GLFW.GLFW_KEY_A, KoolKey.B to GLFW.GLFW_KEY_B,
            KoolKey.C to GLFW.GLFW_KEY_C, KoolKey.D to GLFW.GLFW_KEY_D,
            KoolKey.E to GLFW.GLFW_KEY_E, KoolKey.F to GLFW.GLFW_KEY_F,
            KoolKey.G to GLFW.GLFW_KEY_G, KoolKey.H to GLFW.GLFW_KEY_H,
            KoolKey.I to GLFW.GLFW_KEY_I, KoolKey.J to GLFW.GLFW_KEY_J,
            KoolKey.K to GLFW.GLFW_KEY_K, KoolKey.L to GLFW.GLFW_KEY_L,
            KoolKey.M to GLFW.GLFW_KEY_M, KoolKey.N to GLFW.GLFW_KEY_N,
            KoolKey.O to GLFW.GLFW_KEY_O, KoolKey.P to GLFW.GLFW_KEY_P,
            KoolKey.Q to GLFW.GLFW_KEY_Q, KoolKey.R to GLFW.GLFW_KEY_R,
            KoolKey.S to GLFW.GLFW_KEY_S, KoolKey.T to GLFW.GLFW_KEY_T,
            KoolKey.U to GLFW.GLFW_KEY_U, KoolKey.V to GLFW.GLFW_KEY_V,
            KoolKey.W to GLFW.GLFW_KEY_W, KoolKey.X to GLFW.GLFW_KEY_X,
            KoolKey.Y to GLFW.GLFW_KEY_Y, KoolKey.Z to GLFW.GLFW_KEY_Z,
            KoolKey.NUM_0 to GLFW.GLFW_KEY_0, KoolKey.NUM_1 to GLFW.GLFW_KEY_1,
            KoolKey.NUM_2 to GLFW.GLFW_KEY_2, KoolKey.NUM_3 to GLFW.GLFW_KEY_3,
            KoolKey.NUM_4 to GLFW.GLFW_KEY_4, KoolKey.NUM_5 to GLFW.GLFW_KEY_5,
            KoolKey.NUM_6 to GLFW.GLFW_KEY_6, KoolKey.NUM_7 to GLFW.GLFW_KEY_7,
            KoolKey.NUM_8 to GLFW.GLFW_KEY_8, KoolKey.NUM_9 to GLFW.GLFW_KEY_9,
            KoolKey.F1 to GLFW.GLFW_KEY_F1, KoolKey.F2 to GLFW.GLFW_KEY_F2,
            KoolKey.F3 to GLFW.GLFW_KEY_F3, KoolKey.F4 to GLFW.GLFW_KEY_F4,
            KoolKey.F5 to GLFW.GLFW_KEY_F5, KoolKey.F6 to GLFW.GLFW_KEY_F6,
            KoolKey.F7 to GLFW.GLFW_KEY_F7, KoolKey.F8 to GLFW.GLFW_KEY_F8,
            KoolKey.F9 to GLFW.GLFW_KEY_F9, KoolKey.F10 to GLFW.GLFW_KEY_F10,
            KoolKey.F11 to GLFW.GLFW_KEY_F11, KoolKey.F12 to GLFW.GLFW_KEY_F12,
            KoolKey.LEFT_SHIFT to GLFW.GLFW_KEY_LEFT_SHIFT,
            KoolKey.RIGHT_SHIFT to GLFW.GLFW_KEY_RIGHT_SHIFT,
            KoolKey.LEFT_CONTROL to GLFW.GLFW_KEY_LEFT_CONTROL,
            KoolKey.RIGHT_CONTROL to GLFW.GLFW_KEY_RIGHT_CONTROL,
            KoolKey.LEFT_ALT to GLFW.GLFW_KEY_LEFT_ALT,
            KoolKey.RIGHT_ALT to GLFW.GLFW_KEY_RIGHT_ALT,
            KoolKey.SPACE to GLFW.GLFW_KEY_SPACE,
            KoolKey.ENTER to GLFW.GLFW_KEY_ENTER,
            KoolKey.BACKSPACE to GLFW.GLFW_KEY_BACKSPACE,
            KoolKey.TAB to GLFW.GLFW_KEY_TAB,
            KoolKey.ESCAPE to GLFW.GLFW_KEY_ESCAPE,
            KoolKey.DELETE to GLFW.GLFW_KEY_DELETE,
            KoolKey.INSERT to GLFW.GLFW_KEY_INSERT,
            KoolKey.HOME to GLFW.GLFW_KEY_HOME,
            KoolKey.END to GLFW.GLFW_KEY_END,
            KoolKey.PAGE_UP to GLFW.GLFW_KEY_PAGE_UP,
            KoolKey.PAGE_DOWN to GLFW.GLFW_KEY_PAGE_DOWN,
            KoolKey.UP to GLFW.GLFW_KEY_UP,
            KoolKey.DOWN to GLFW.GLFW_KEY_DOWN,
            KoolKey.LEFT to GLFW.GLFW_KEY_LEFT,
            KoolKey.RIGHT to GLFW.GLFW_KEY_RIGHT,
            KoolKey.MINUS to GLFW.GLFW_KEY_MINUS,
            KoolKey.EQUALS to GLFW.GLFW_KEY_EQUAL,
            KoolKey.LEFT_BRACKET to GLFW.GLFW_KEY_LEFT_BRACKET,
            KoolKey.RIGHT_BRACKET to GLFW.GLFW_KEY_RIGHT_BRACKET,
            KoolKey.BACKSLASH to GLFW.GLFW_KEY_BACKSLASH,
            KoolKey.SEMICOLON to GLFW.GLFW_KEY_SEMICOLON,
            KoolKey.APOSTROPHE to GLFW.GLFW_KEY_APOSTROPHE,
            KoolKey.COMMA to GLFW.GLFW_KEY_COMMA,
            KoolKey.PERIOD to GLFW.GLFW_KEY_PERIOD,
            KoolKey.SLASH to GLFW.GLFW_KEY_SLASH,
            KoolKey.CAPS_LOCK to GLFW.GLFW_KEY_CAPS_LOCK,
            KoolKey.SCROLL_LOCK to GLFW.GLFW_KEY_SCROLL_LOCK,
            KoolKey.NUM_LOCK to GLFW.GLFW_KEY_NUM_LOCK,
        )

        private val MOUSE_BUTTON_MAP = mapOf(
            KoolMouseButton.LEFT to GLFW.GLFW_MOUSE_BUTTON_LEFT,
            KoolMouseButton.RIGHT to GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            KoolMouseButton.MIDDLE to GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            KoolMouseButton.BUTTON4 to GLFW.GLFW_MOUSE_BUTTON_4,
            KoolMouseButton.BUTTON5 to GLFW.GLFW_MOUSE_BUTTON_5,
        )

        fun koolKeyToGLFW(key: KoolKey): Int? = KOOL_KEY_MAP[key]
        fun koolMouseToGLFW(button: KoolMouseButton): Int = MOUSE_BUTTON_MAP[button] ?: GLFW.GLFW_MOUSE_BUTTON_LEFT
    }
}