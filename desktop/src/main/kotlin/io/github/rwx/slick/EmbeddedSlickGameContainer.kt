package io.github.rwx.slick

import com.corrodinggames.rts.gameFramework.GameEngine
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.awt.AWTGLCanvas
import org.newdawn.slick.*
import org.newdawn.slick.Image
import org.newdawn.slick.opengl.ImageData
import org.newdawn.slick.util.Log
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingUtilities
import kotlin.math.roundToInt
import org.lwjglx.input.Cursor as LwjglCursor
import java.awt.Cursor as AwtCursor

internal class EmbeddedSlickGameContainer(
    game: Game,
    width: Int,
    height: Int,
    fullscreen: Boolean,
    parentCanvas: Canvas? = null,
    private val onParentCanvasResize: (Int, Int) -> Unit = { _, _ -> },
) : GameContainer(game) {
    private val awtCanvas = parentCanvas as? AWTGLCanvas
        ?: error("Slick AWT backend requires an AWTGLCanvas parent")
    private val exitRequested = AtomicBoolean(false)
    private val inputListener = game as? InputListener
    private var initialized = false
    private var terminalFailure: Throwable? = null
    private val mouseStateLock = Any()
    private var mouseX = 0
    private var mouseY = 0
    private val frameSynchronizer = LwjglDisplayFrameSynchronizer()

    init {
        this.width = width.coerceAtLeast(320)
        this.height = height.coerceAtLeast(240)
        installAwtInput()
        if (fullscreen) {
            Log.warn("Fullscreen is ignored by the RWX AWT Slick container")
        }
    }

    fun requestExitFromAnyThread() {
        exitRequested.set(true)
        exit()
    }

    fun setDisplayMode(width: Int, height: Int, fullscreen: Boolean) {
        this.width = width.coerceAtLeast(320)
        this.height = height.coerceAtLeast(240)
        if (fullscreen) {
            Log.warn("Fullscreen is ignored by the RWX AWT Slick container")
        }
        if (initialized) {
            enterOrtho()
            getGraphics()?.setDimensions(this.width, this.height)
        }
    }

    fun start() {
        running = true
        while (running() && !exitRequested.get() && awtCanvas.isDisplayable) {
            if (!awtCanvas.isRenderable()) {
                skipFrameUntilCanvasReady()
                continue
            }
            renderFrame()
        }
        terminalFailure?.let { throw it }
    }

    private fun renderFrame() {
        val canvasWidth = awtCanvas.width.takeIf { it > 0 } ?: width
        val canvasHeight = awtCanvas.height.takeIf { it > 0 } ?: height
        if (canvasWidth != width || canvasHeight != height) {
            onParentCanvasResize(canvasWidth, canvasHeight)
            setDisplayMode(canvasWidth, canvasHeight, false)
        }

        try {
            awtCanvas.runInContext {
                try {
                    if (!initialized) {
                        org.lwjgl.opengl.GL.createCapabilities()
                        (awtCanvas as? SlickAwtGLCanvas)?.applyRuntimeGlSettings()
                        initSystem()
                        enterOrtho()
                        game.init(this)
                        initialized = true
                        requestCanvasFocus()
                        getDelta()
                    }
                    val viewport = framebufferViewportSize(
                        logicalWidth = width,
                        logicalHeight = height,
                        framebufferWidth = awtCanvas.framebufferWidth,
                        framebufferHeight = awtCanvas.framebufferHeight,
                    )
                    GL11.glViewport(0, 0, viewport.width, viewport.height)
                    updateAndRender(getDelta())
                    updateFPS()
                    awtCanvas.swapBuffers()
                } catch (error: SlickException) {
                    Log.error(error)
                    terminalFailure = error
                    running = false
                } catch (error: Throwable) {
                    GameEngine.log("Slick AWT container failed", error)
                    terminalFailure = error
                    running = false
                }
            }
        } catch (error: RuntimeException) {
            if (error.isCanvasLockFailure()) {
                skipFrameUntilCanvasReady()
                return
            }
            GameEngine.log("Slick AWT container failed", error)
            terminalFailure = error
            running = false
        } catch (error: Throwable) {
            GameEngine.log("Slick AWT container failed", error)
            terminalFailure = error
            running = false
        }
    }

    override fun updateAndRender(delta: Int) {
        var frameDelta = delta
        val recordedFrameRate = getFPS()
        if (smoothDeltas && recordedFrameRate != 0) {
            frameDelta = 1000 / recordedFrameRate
        }

        input.poll(width, height)
        Music.poll(frameDelta)

        if (paused) {
            game.update(this, 0)
        } else {
            storedDelta += frameDelta.toLong()
            if (storedDelta >= minimumLogicInterval) {
                if (maximumLogicInterval != 0L) {
                    val logicTicks = storedDelta / maximumLogicInterval
                    repeat(logicTicks.toInt()) {
                        game.update(this, maximumLogicInterval.toInt())
                    }
                    val remainder = (storedDelta % maximumLogicInterval).toInt()
                    if (remainder > minimumLogicInterval) {
                        game.update(this, remainder % maximumLogicInterval.toInt())
                        storedDelta = 0L
                    } else {
                        storedDelta = remainder.toLong()
                    }
                } else {
                    game.update(this, storedDelta.toInt())
                    storedDelta = 0L
                }
            }
        }
        if (hasFocus() || alwaysRender) {
            if (clearEachFrame) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
            }
            GL11.glLoadIdentity()
            val graphics = getGraphics()
            graphics.resetTransform()
            graphics.resetFont()
            graphics.resetLineWidth()
            graphics.setAntiAlias(false)
            game.render(this, graphics)
            graphics.resetTransform()
            if (isShowingFPS) {
                defaultFont.drawString(10.0f, 10.0f, "FPS: $recordedFPS")
            }
            GL11.glFlush()
        }
        if (targetFPS != -1) {
            syncFrame(targetFPS)
        }
    }

    override fun setVSync(vsync: Boolean) {
        super.setVSync(vsync)
        val slickCanvas = awtCanvas as? SlickAwtGLCanvas ?: return
        slickCanvas.requestSwapInterval(if (vsync) 1 else 0)
        if (initialized) {
            slickCanvas.applyRuntimeGlSettings()
        }
    }

    fun recommendedTargetFrameRate(highRefreshRate: Boolean): Int =
        legacySlickTargetFrameRate(highRefreshRate)

    private fun Canvas.isRenderable(): Boolean =
        isDisplayable && isVisible && isShowing && width > 0 && height > 0

    private fun skipFrameUntilCanvasReady() {
        Thread.sleep(CANVAS_RETRY_SLEEP_MILLIS)
    }

    private fun Throwable.isCanvasLockFailure(): Boolean =
        message == "Failed to lock Canvas" ||
                generateSequence(this) { it.cause }.any { cause ->
                    (cause is AWTException && cause.message?.contains("JAWT_DrawingSurface_Lock") == true) ||
                            (cause is NullPointerException &&
                                    cause.message?.contains("JAWTDrawingSurface.Lock()") == true)
                }

    private fun syncFrame(fps: Int) {
        frameSynchronizer.sync(fps)
    }

    override fun hasFocus(): Boolean = awtCanvas.hasFocus()

    override fun getScreenWidth(): Int = awtCanvas.graphicsConfiguration?.bounds?.width ?: width

    override fun getScreenHeight(): Int = awtCanvas.graphicsConfiguration?.bounds?.height ?: height

    override fun setIcon(ref: String?) = Unit

    override fun setIcons(refs: Array<out String>?) = Unit

    override fun setMouseCursor(ref: String?, hotSpotX: Int, hotSpotY: Int) = Unit

    override fun setMouseCursor(data: ImageData?, hotSpotX: Int, hotSpotY: Int) = Unit

    override fun setMouseCursor(image: Image?, hotSpotX: Int, hotSpotY: Int) {
        val cursorImage = image?.toBufferedImage() ?: return
        applyCustomCursor(cursorImage, hotSpotX, hotSpotY)
    }

    override fun setMouseCursor(cursor: LwjglCursor?, hotSpotX: Int, hotSpotY: Int) {
        if (cursor == null) {
            setDefaultMouseCursor()
        }
    }

    override fun setDefaultMouseCursor() {
        applyCursor(AwtCursor.getDefaultCursor())
    }

    override fun setMouseGrabbed(grabbed: Boolean) = Unit

    override fun isMouseGrabbed(): Boolean = false

    fun destroy() {
        setDefaultMouseCursor()
        exit()
        runCatching { awtCanvas.disposeCanvas() }
    }

    private fun applyCustomCursor(image: BufferedImage, hotSpotX: Int, hotSpotY: Int) {
        try {
            val cursor = Toolkit.getDefaultToolkit().createCustomCursor(
                image,
                Point(
                    hotSpotX.coerceIn(0, (image.width - 1).coerceAtLeast(0)),
                    hotSpotY.coerceIn(0, (image.height - 1).coerceAtLeast(0)),
                ),
                "rwx-pointer",
            )
            applyCursor(cursor)
        } catch (error: Throwable) {
            Log.error("Failed to load and apply cursor.", error)
            throw SlickException("Failed to set mouse cursor", error)
        }
    }

    private fun applyCursor(cursor: AwtCursor) {
        if (SwingUtilities.isEventDispatchThread()) {
            awtCanvas.cursor = cursor
        } else {
            SwingUtilities.invokeLater {
                awtCanvas.cursor = cursor
            }
        }
    }

    private fun Image.toBufferedImage(): BufferedImage {
        val cursorImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val color = getColor(x, y)
                cursorImage.setRGB(
                    x,
                    y,
                    ((color.a * 255f).roundToInt().coerceIn(0, 255) shl 24) or
                            ((color.r * 255f).roundToInt().coerceIn(0, 255) shl 16) or
                            ((color.g * 255f).roundToInt().coerceIn(0, 255) shl 8) or
                            (color.b * 255f).roundToInt().coerceIn(0, 255),
                )
            }
        }
        return cursorImage
    }

    private fun installAwtInput() {
        awtCanvas.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(event: KeyEvent) {
                inputListener?.keyPressed(
                    event.toSlickKey(),
                    event.keyChar.takeUnless { it == KeyEvent.CHAR_UNDEFINED } ?: 0.toChar())
            }

            override fun keyReleased(event: KeyEvent) {
                inputListener?.keyReleased(
                    event.toSlickKey(),
                    event.keyChar.takeUnless { it == KeyEvent.CHAR_UNDEFINED } ?: 0.toChar())
            }
        })
        val mouseListener = object : MouseAdapter() {
            override fun mouseEntered(event: MouseEvent) {
                recordAwtMousePosition(event)
                requestCanvasFocus()
            }

            override fun mouseExited(event: MouseEvent) {
                recordAwtMousePosition(event)
            }

            override fun mousePressed(event: MouseEvent) {
                requestCanvasFocus()
                val button = event.toSlickButton()
                inputListener?.mousePressed(button, event.x, event.y)
                recordAwtMousePosition(event)
            }

            override fun mouseReleased(event: MouseEvent) {
                val button = event.toSlickButton()
                recordAwtMousePosition(event)
                inputListener?.mouseReleased(button, event.x, event.y)
            }

            override fun mouseWheelMoved(event: MouseWheelEvent) {
                recordAwtMousePosition(event)
                inputListener?.mouseWheelMoved(event.toSlickDWheel())
            }
        }
        awtCanvas.addMouseListener(mouseListener)
        awtCanvas.addMouseWheelListener(mouseListener)
        awtCanvas.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(event: MouseEvent) {
                val oldPosition = getRecordedMousePosition()
                inputListener?.mouseMoved(oldPosition.x, oldPosition.y, event.x, event.y)
                recordAwtMouseMotion(event)
            }

            override fun mouseDragged(event: MouseEvent) {
                val oldPosition = getRecordedMousePosition()
                inputListener?.mouseDragged(oldPosition.x, oldPosition.y, event.x, event.y)
                recordAwtMouseMotion(event)
            }
        })
    }

    private fun recordAwtMouseMotion(event: MouseEvent) {
        recordAwtMousePosition(event)
    }

    private fun recordAwtMousePosition(event: MouseEvent) {
        synchronized(mouseStateLock) {
            mouseX = event.x
            mouseY = event.y
        }
    }

    private fun getRecordedMousePosition(): Point =
        synchronized(mouseStateLock) {
            Point(mouseX, mouseY)
        }

    private fun requestCanvasFocus() {
        val action = {
            if (awtCanvas.isVisible && awtCanvas.isShowing) {
                awtCanvas.isFocusable = true
                if (!awtCanvas.requestFocusInWindow()) {
                    awtCanvas.requestFocus()
                }
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            action()
        } else {
            SwingUtilities.invokeLater(action)
        }
    }
}

internal fun framebufferViewportSize(
    logicalWidth: Int,
    logicalHeight: Int,
    framebufferWidth: Int,
    framebufferHeight: Int,
): Dimension = Dimension(
    framebufferWidth.takeIf { it > 0 } ?: logicalWidth.coerceAtLeast(1),
    framebufferHeight.takeIf { it > 0 } ?: logicalHeight.coerceAtLeast(1),
)

private const val CANVAS_RETRY_SLEEP_MILLIS = 16L
private const val MAX_STANDARD_TARGET_FPS = 120
private const val MAX_HIGH_REFRESH_TARGET_FPS = 300
private const val NANOS_IN_SECOND = 1_000_000_000L
private const val SYNC_RUNNING_AVERAGE_SLOTS = 10
private const val SYNC_INITIAL_SLEEP_NANOS = 1_000_000L
private const val SYNC_LOW_RES_DAMPEN_THRESHOLD_NANOS = 10_000_000L
private const val SYNC_LOW_RES_DAMPEN_FACTOR = 0.9f

internal fun legacySlickTargetFrameRate(highRefreshRate: Boolean): Int =
    if (highRefreshRate) MAX_HIGH_REFRESH_TARGET_FPS else MAX_STANDARD_TARGET_FPS

// Mirrors LWJGL2 Display.sync -> org.lwjgl.opengl.Sync.sync, used by the original desktop build.
internal class LwjglDisplayFrameSynchronizer(
    private val clockNanos: () -> Long = System::nanoTime,
    private val sleepOneMillis: () -> Unit = { Thread.sleep(1L) },
    private val yieldThread: () -> Unit = { Thread.yield() },
) {
    private val sleepDurations = RunningAverage(SYNC_RUNNING_AVERAGE_SLOTS)
    private val yieldDurations = RunningAverage(SYNC_RUNNING_AVERAGE_SLOTS)
    private var nextFrameNanos = 0L
    private var initialized = false

    fun sync(fps: Int) {
        if (fps <= 0) return
        if (!initialized) {
            initialize()
        }

        try {
            var beforeSleep = clockNanos()
            while (nextFrameNanos - beforeSleep > sleepDurations.average()) {
                sleepOneMillis()
                val afterSleep = clockNanos()
                sleepDurations.add(afterSleep - beforeSleep)
                beforeSleep = afterSleep
            }

            sleepDurations.dampenForLowResTicker()

            var beforeYield = clockNanos()
            while (nextFrameNanos - beforeYield > yieldDurations.average()) {
                yieldThread()
                val afterYield = clockNanos()
                yieldDurations.add(afterYield - beforeYield)
                beforeYield = afterYield
            }
        } catch (_: InterruptedException) {
        }

        nextFrameNanos = maxOf(nextFrameNanos + NANOS_IN_SECOND / fps, clockNanos())
    }

    private fun initialize() {
        initialized = true
        sleepDurations.init(SYNC_INITIAL_SLEEP_NANOS)

        val before = clockNanos()
        val after = clockNanos()
        yieldDurations.init(((after - before) * 1.333).toInt().toLong())
        nextFrameNanos = clockNanos()
    }

    private class RunningAverage(slotCount: Int) {
        private val slots = LongArray(slotCount)
        private var offset = 0

        fun init(value: Long) {
            while (offset < slots.size) {
                slots[offset++] = value
            }
        }

        fun add(value: Long) {
            slots[offset++ % slots.size] = value
            offset %= slots.size
        }

        fun average(): Long {
            var sum = 0L
            for (slot in slots) {
                sum += slot
            }
            return sum / slots.size
        }

        fun dampenForLowResTicker() {
            if (average() > SYNC_LOW_RES_DAMPEN_THRESHOLD_NANOS) {
                for (index in slots.indices) {
                    slots[index] = (slots[index] * SYNC_LOW_RES_DAMPEN_FACTOR).toLong()
                }
            }
        }
    }
}

internal const val SLICK_MOUSE_WHEEL_UNITS_PER_NOTCH = 120

internal fun MouseWheelEvent.toSlickDWheel(): Int =
    (-preciseWheelRotation * SLICK_MOUSE_WHEEL_UNITS_PER_NOTCH).roundToInt()

private fun MouseEvent.toSlickButton(): Int = when (button) {
    MouseEvent.BUTTON1 -> Input.MOUSE_LEFT_BUTTON
    MouseEvent.BUTTON2 -> Input.MOUSE_MIDDLE_BUTTON
    MouseEvent.BUTTON3 -> Input.MOUSE_RIGHT_BUTTON
    else -> -1
}

private fun KeyEvent.toSlickKey(): Int = when (keyCode) {
    KeyEvent.VK_ESCAPE -> Input.KEY_ESCAPE
    KeyEvent.VK_1 -> Input.KEY_1
    KeyEvent.VK_2 -> Input.KEY_2
    KeyEvent.VK_3 -> Input.KEY_3
    KeyEvent.VK_4 -> Input.KEY_4
    KeyEvent.VK_5 -> Input.KEY_5
    KeyEvent.VK_6 -> Input.KEY_6
    KeyEvent.VK_7 -> Input.KEY_7
    KeyEvent.VK_8 -> Input.KEY_8
    KeyEvent.VK_9 -> Input.KEY_9
    KeyEvent.VK_0 -> Input.KEY_0
    KeyEvent.VK_MINUS -> Input.KEY_MINUS
    KeyEvent.VK_EQUALS -> Input.KEY_EQUALS
    KeyEvent.VK_BACK_SPACE -> Input.KEY_BACK
    KeyEvent.VK_TAB -> Input.KEY_TAB
    KeyEvent.VK_Q -> Input.KEY_Q
    KeyEvent.VK_W -> Input.KEY_W
    KeyEvent.VK_E -> Input.KEY_E
    KeyEvent.VK_R -> Input.KEY_R
    KeyEvent.VK_T -> Input.KEY_T
    KeyEvent.VK_Y -> Input.KEY_Y
    KeyEvent.VK_U -> Input.KEY_U
    KeyEvent.VK_I -> Input.KEY_I
    KeyEvent.VK_O -> Input.KEY_O
    KeyEvent.VK_P -> Input.KEY_P
    KeyEvent.VK_OPEN_BRACKET -> Input.KEY_LBRACKET
    KeyEvent.VK_CLOSE_BRACKET -> Input.KEY_RBRACKET
    KeyEvent.VK_ENTER -> if (keyLocation == KeyEvent.KEY_LOCATION_NUMPAD) Input.KEY_NUMPADENTER else Input.KEY_ENTER
    KeyEvent.VK_CONTROL -> if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT) Input.KEY_RCONTROL else Input.KEY_LCONTROL
    KeyEvent.VK_A -> Input.KEY_A
    KeyEvent.VK_S -> Input.KEY_S
    KeyEvent.VK_D -> Input.KEY_D
    KeyEvent.VK_F -> Input.KEY_F
    KeyEvent.VK_G -> Input.KEY_G
    KeyEvent.VK_H -> Input.KEY_H
    KeyEvent.VK_J -> Input.KEY_J
    KeyEvent.VK_K -> Input.KEY_K
    KeyEvent.VK_L -> Input.KEY_L
    KeyEvent.VK_SEMICOLON -> Input.KEY_SEMICOLON
    KeyEvent.VK_QUOTE -> Input.KEY_APOSTROPHE
    KeyEvent.VK_BACK_QUOTE -> Input.KEY_GRAVE
    KeyEvent.VK_SHIFT -> if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT) Input.KEY_RSHIFT else Input.KEY_LSHIFT
    KeyEvent.VK_BACK_SLASH -> Input.KEY_BACKSLASH
    KeyEvent.VK_Z -> Input.KEY_Z
    KeyEvent.VK_X -> Input.KEY_X
    KeyEvent.VK_C -> Input.KEY_C
    KeyEvent.VK_V -> Input.KEY_V
    KeyEvent.VK_B -> Input.KEY_B
    KeyEvent.VK_N -> Input.KEY_N
    KeyEvent.VK_M -> Input.KEY_M
    KeyEvent.VK_COMMA -> Input.KEY_COMMA
    KeyEvent.VK_PERIOD -> Input.KEY_PERIOD
    KeyEvent.VK_SLASH -> Input.KEY_SLASH
    KeyEvent.VK_ALT -> if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT) Input.KEY_RMENU else Input.KEY_LMENU
    KeyEvent.VK_SPACE -> Input.KEY_SPACE
    KeyEvent.VK_CAPS_LOCK -> Input.KEY_CAPITAL
    KeyEvent.VK_F1 -> Input.KEY_F1
    KeyEvent.VK_F2 -> Input.KEY_F2
    KeyEvent.VK_F3 -> Input.KEY_F3
    KeyEvent.VK_F4 -> Input.KEY_F4
    KeyEvent.VK_F5 -> Input.KEY_F5
    KeyEvent.VK_F6 -> Input.KEY_F6
    KeyEvent.VK_F7 -> Input.KEY_F7
    KeyEvent.VK_F8 -> Input.KEY_F8
    KeyEvent.VK_F9 -> Input.KEY_F9
    KeyEvent.VK_F10 -> Input.KEY_F10
    KeyEvent.VK_F11 -> Input.KEY_F11
    KeyEvent.VK_F12 -> Input.KEY_F12
    KeyEvent.VK_F13 -> Input.KEY_F13
    KeyEvent.VK_F14 -> Input.KEY_F14
    KeyEvent.VK_F15 -> Input.KEY_F15
    KeyEvent.VK_NUM_LOCK -> Input.KEY_NUMLOCK
    KeyEvent.VK_SCROLL_LOCK -> Input.KEY_SCROLL
    KeyEvent.VK_NUMPAD0 -> Input.KEY_NUMPAD0
    KeyEvent.VK_NUMPAD1 -> Input.KEY_NUMPAD1
    KeyEvent.VK_NUMPAD2 -> Input.KEY_NUMPAD2
    KeyEvent.VK_NUMPAD3 -> Input.KEY_NUMPAD3
    KeyEvent.VK_NUMPAD4 -> Input.KEY_NUMPAD4
    KeyEvent.VK_NUMPAD5 -> Input.KEY_NUMPAD5
    KeyEvent.VK_NUMPAD6 -> Input.KEY_NUMPAD6
    KeyEvent.VK_NUMPAD7 -> Input.KEY_NUMPAD7
    KeyEvent.VK_NUMPAD8 -> Input.KEY_NUMPAD8
    KeyEvent.VK_NUMPAD9 -> Input.KEY_NUMPAD9
    KeyEvent.VK_MULTIPLY -> Input.KEY_MULTIPLY
    KeyEvent.VK_ADD -> Input.KEY_ADD
    KeyEvent.VK_SUBTRACT -> Input.KEY_SUBTRACT
    KeyEvent.VK_DECIMAL -> Input.KEY_DECIMAL
    KeyEvent.VK_DIVIDE -> Input.KEY_DIVIDE
    KeyEvent.VK_UP -> Input.KEY_UP
    KeyEvent.VK_DOWN -> Input.KEY_DOWN
    KeyEvent.VK_LEFT -> Input.KEY_LEFT
    KeyEvent.VK_RIGHT -> Input.KEY_RIGHT
    KeyEvent.VK_HOME -> Input.KEY_HOME
    KeyEvent.VK_END -> Input.KEY_END
    KeyEvent.VK_PAGE_UP -> Input.KEY_PRIOR
    KeyEvent.VK_PAGE_DOWN -> Input.KEY_NEXT
    KeyEvent.VK_INSERT -> Input.KEY_INSERT
    KeyEvent.VK_DELETE -> Input.KEY_DELETE
    KeyEvent.VK_PAUSE -> Input.KEY_PAUSE
    KeyEvent.VK_PRINTSCREEN -> Input.KEY_SYSRQ
    KeyEvent.VK_WINDOWS -> if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT) Input.KEY_RWIN else Input.KEY_LWIN
    KeyEvent.VK_CONTEXT_MENU -> Input.KEY_APPS
    else -> 0
}
