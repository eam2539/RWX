package io.github.rwx

import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.platform.swing.KoolGlCanvas
import de.fabmax.kool.platform.swing.SwingWindowSubsystem
import io.github.rwx.slick.SlickAwtGLCanvas
import io.github.rwx.slick.SlickCanvasHost
import org.lwjgl.opengl.awt.GLData
import java.awt.*
import java.awt.event.*
import java.awt.font.TextHitInfo
import java.awt.im.InputMethodRequests
import java.text.AttributedCharacterIterator
import java.text.AttributedString
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JWindow
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

class SwingKoolHost private constructor(
    val koolCanvas: Canvas,
    val gameCanvas: Canvas,
    val windowSubsystem: SwingWindowSubsystem,
    private val startupFullscreen: Boolean,
) {
    private val panel = JPanel(null)
    private val frame = JFrame(windowTitle())
    private val overlayPanel = JPanel(BorderLayout())
    private val overlayWindow = JWindow(frame)
    val windowSize: Vec2i
        get() {
            val width = panel.width.takeIf { it > 0 } ?: panel.preferredSize.width
            val height = panel.height.takeIf { it > 0 } ?: panel.preferredSize.height
            return Vec2i(width.coerceAtLeast(1), height.coerceAtLeast(1))
        }
    private var gameBufferStrategyCreated = false
    private var initialContentFitPending = true
    private var initialContentFitApplied = false
    private var applyingInitialContentFit = false
    private val closeRequested = AtomicBoolean(false)
    private var pointerCursor: Cursor? = null
    private var cachedOverlayLocation: Point? = null
    private val keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager()
    private val koolTypedControlCharacterFilter = KeyEventDispatcher { event ->
        event.source === koolCanvas && shouldSuppressKoolTypedCharacter(event)
    }

    init {
        val preferredWindowSize = initialWindowSize(startupFullscreen)
        panel.background = Color.BLACK
        panel.preferredSize = Dimension(preferredWindowSize.x, preferredWindowSize.y)
        panel.minimumSize = Dimension(800, 600)
        panel.isOpaque = true
        koolCanvas.name = KOOL_CARD
        koolCanvas.background = TransparentCanvasColor
        koolCanvas.isFocusable = true
        koolCanvas.ignoreRepaint = true
        keyboardFocusManager.addKeyEventDispatcher(koolTypedControlCharacterFilter)
        installKoolInputMethodListener()
        gameCanvas.name = GAME_CARD
        gameCanvas.background = Color.BLACK
        gameCanvas.isFocusable = true
        gameCanvas.ignoreRepaint = true
        gameCanvas.isVisible = false

        panel.add(gameCanvas, GAME_CARD)
        panel.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                resizeCanvases()
                fitInitialContentSize()
            }
        })

        overlayPanel.background = TransparentCanvasColor
        overlayPanel.isOpaque = false
        overlayPanel.add(koolCanvas, BorderLayout.CENTER)
        overlayWindow.background = TransparentCanvasColor
        overlayWindow.contentPane = overlayPanel
        overlayWindow.rootPane.isOpaque = false
        overlayWindow.focusableWindowState = true

        frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        frame.isUndecorated = startupFullscreen
        frame.background = Color.BLACK
        frame.contentPane.background = Color.BLACK
        frame.rootPane.background = Color.BLACK
        frame.layout = BorderLayout()
        frame.minimumSize = Dimension(800, 600)
        frame.add(panel, BorderLayout.CENTER)
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                requestClose()
            }
        })
        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentMoved(e: ComponentEvent) {
                syncOverlayBounds(forceLocationRefresh = true)
            }

            override fun componentResized(e: ComponentEvent) {
                syncOverlayBounds(forceLocationRefresh = true)
            }
        })
        frame.pack()
        if (startupFullscreen) {
            applyStartupFullscreen()
        } else {
            frame.setLocationRelativeTo(null)
        }
        frame.isVisible = true
        resizeCanvases()
        showKool()
    }

    fun showGame(koolOverlay: Boolean = false) {
        val action = {
            resizeCanvases()
            gameCanvas.isVisible = true
            setKoolOverlayVisible(koolOverlay)
            ensureGameBufferStrategy()
            if (koolOverlay) {
                koolCanvas.requestFocusInWindow()
            } else {
                gameCanvas.requestFocusInWindow()
            }
            Unit
        }
        // The first show must wait for the EDT to create the JAWT peer before Slick starts. Later
        // calls can originate inside AWTGLCanvas.runInContext(), which holds the AWT lock; waiting
        // for the EDT there deadlocks when setVisible() tries to acquire that same lock.
        dispatchCanvasVisibilityChange(gameCanvas.isShowing, action)
    }

    fun showKool() {
        dispatchCanvasVisibilityChange(gameCanvas.isShowing) {
            resizeCanvases()
            gameCanvas.isVisible = false
            setKoolOverlayVisible(true)
            koolCanvas.requestFocusInWindow()
        }
    }

    fun requestClose() {
        // Provided Swing canvases have a no-op KoolWindow.close(), so desktop exit closes the subsystem directly.
        if (!closeRequested.compareAndSet(false, true)) return
        keyboardFocusManager.removeKeyEventDispatcher(koolTypedControlCharacterFilter)
        // Stop the embedded Slick render thread and wait for it to release the game canvas'
        // JAWT drawing surface before disposing the window. Disposing while that thread still
        // holds the surface races the EDT inside JAWT_FreeDrawingSurface and crashes the JVM.
        // Run off the EDT so joining the render thread cannot deadlock its invokeAndWait calls.
        Thread({
            runCatching { SlickCanvasHost.shutdownRenderer() }
            windowSubsystem.close {
                overlayWindow.dispose()
                frame.dispose()
                exitProcess(0)
            }
        }, "RWX-shutdown").apply {
            isDaemon = true
            start()
        }
    }

    private fun resizeCanvases() {
        val width = panel.width.coerceAtLeast(1)
        val height = panel.height.coerceAtLeast(1)
        panel.revalidate()
        panel.doLayout()
        gameCanvas.setBounds(0, 0, width, height)
        gameCanvas.setSize(width, height)
        koolCanvas.setSize(width, height)
        syncOverlayBounds()
        SlickCanvasHost.notifyGameCanvasResized(width, height)
    }

    private fun setKoolOverlayVisible(visible: Boolean) {
        koolCanvas.isVisible = visible
        if (visible) {
            syncOverlayBounds(forceLocationRefresh = true)
            overlayWindow.isVisible = true
            overlayWindow.toFront()
        } else {
            overlayWindow.isVisible = false
        }
    }

    private fun syncOverlayBounds(forceLocationRefresh: Boolean = false) {
        if (!panel.isShowing) {
            cachedOverlayLocation = null
            return
        }
        val location = if (forceLocationRefresh || cachedOverlayLocation == null) {
            runCatching { panel.locationOnScreen }.getOrNull()?.also {
                cachedOverlayLocation = Point(it)
            } ?: return
        } else {
            cachedOverlayLocation!!
        }
        val width = panel.width.coerceAtLeast(1)
        val height = panel.height.coerceAtLeast(1)
        val boundsChanged =
            overlayWindow.x != location.x ||
                overlayWindow.y != location.y ||
                overlayWindow.width != width ||
                overlayWindow.height != height
        val canvasSizeChanged = koolCanvas.width != width || koolCanvas.height != height
        if (boundsChanged) {
            overlayWindow.setBounds(location.x, location.y, width, height)
        }
        if (canvasSizeChanged) {
            koolCanvas.setSize(width, height)
        }
        if (boundsChanged || canvasSizeChanged) {
            overlayWindow.validate()
        }
    }

    private fun fitInitialContentSize() {
        if (startupFullscreen) return
        if (!initialContentFitPending || applyingInitialContentFit || !frame.isShowing) return
        val preferred = panel.preferredSize
        val needsFit = panel.width < preferred.width || panel.height < preferred.height
        if (!needsFit) {
            if (initialContentFitApplied) {
                initialContentFitPending = false
            }
            return
        }

        val insets = frame.insets
        if (insets.top == 0 && insets.left == 0 && insets.bottom == 0 && insets.right == 0) return
        val targetWidth = preferred.width + insets.left + insets.right
        val targetHeight = preferred.height + insets.top + insets.bottom
        if (frame.width == targetWidth && frame.height == targetHeight) return

        applyingInitialContentFit = true
        initialContentFitApplied = true
        frame.setSize(targetWidth, targetHeight)
        frame.setLocationRelativeTo(null)
        applyingInitialContentFit = false
    }

    private fun ensureGameBufferStrategy() {
        if (gameBufferStrategyCreated || !gameCanvas.isDisplayable) return
        runCatching {
            gameCanvas.createBufferStrategy(2)
            gameBufferStrategyCreated = true
        }
    }

    fun setInGamePointerCursorActive(active: Boolean) {
        runOnEdt {
            val cursorCanvas = koolCanvas as? PointerCursorCanvas
            cursorCanvas?.inGamePointerCursorActive = active
            if (active) {
                applyPointerCursor(koolCanvas)
            } else {
                koolCanvas.cursor = Cursor.getDefaultCursor()
            }
        }
    }

    private fun applyPointerCursor(vararg canvases: Canvas) {
        val pointerCursor = pointerCursor ?: createPointerCursor()?.also { pointerCursor = it } ?: return
        canvases.forEach { canvas ->
            (canvas as? PointerCursorCanvas)?.pointerCursor = pointerCursor
            canvas.cursor = pointerCursor
        }
    }

    private fun installKoolInputMethodListener() {
        koolCanvas.enableInputMethods(true)
        koolCanvas.addInputMethodListener(object : InputMethodListener {
            override fun inputMethodTextChanged(event: InputMethodEvent) {
                committedInputMethodText(event.text, event.committedCharacterCount).forEach {
                    KeyboardInput.handleCharTyped(it)
                }
                event.consume()
            }

            override fun caretPositionChanged(event: InputMethodEvent) = Unit
        })
    }

    private fun createPointerCursor(): Cursor? =
        runCatching {
            val imageFile = DesktopPlatformStorage.resolveAssetRoot().resolve("drawable/pointer.png")
            val image = ImageIO.read(imageFile) ?: return null
            Toolkit.getDefaultToolkit().createCustomCursor(image, Point(0, 0), "rwx-pointer")
        }.getOrNull()

    private fun applyStartupFullscreen() {
        val bounds = fullscreenBounds()
        frame.extendedState = JFrame.MAXIMIZED_BOTH
        frame.bounds = bounds
        panel.preferredSize = Dimension(bounds.width, bounds.height)
        panel.setSize(bounds.width, bounds.height)
    }

    companion object {
        val DEFAULT_WINDOW_SIZE = Vec2i(1280, 720)
        private const val KOOL_CARD = "kool"
        private const val GAME_CARD = "game"
        private val TransparentCanvasColor = Color(0, 0, 0, 0)

        fun create(fullscreen: Boolean, useOpenGl: Boolean): SwingKoolHost {
            System.setProperty("org.lwjgl.opengl.contextAPI", "native")
            val koolCanvas = if (useOpenGl) {
                KoolGlCanvas(
                    GLData().apply {
                        alphaSize = 8
                        depthSize = 24
                        stencilSize = 8
                        samples = 4
                        swapInterval = 1
                    },
                )
            } else {
                PointerCursorCanvas()
            }
            val gameCanvas = SlickAwtGLCanvas(
                data = GLData().apply {
                    alphaSize = 8
                    depthSize = 24
                    stencilSize = 8
                },
                requestedSwapInterval = 0,
            )
            lateinit var host: SwingKoolHost
            val subsystem = SwingWindowSubsystem(
                providedCanvas = koolCanvas,
                makeFocusable = true,
            )
            host = SwingKoolHost(
                koolCanvas = koolCanvas,
                gameCanvas = gameCanvas,
                windowSubsystem = subsystem,
                startupFullscreen = fullscreen,
            )
            SlickCanvasHost.install(
                canvasProvider = { host.gameCanvas },
                visibilityController = { visible, koolOverlay ->
                    if (visible) host.showGame(koolOverlay) else host.showKool()
                },
            )
            return host
        }

        private fun windowTitle(): String =
            System.getProperty("rwx.windowTitle")
                ?.takeIf { it.isNotBlank() }
                ?: System.getenv("RWX_WINDOW_TITLE")?.takeIf { it.isNotBlank() }
                ?: "RWX Game"

        private fun runOnEdt(action: () -> Unit) {
            if (SwingUtilities.isEventDispatchThread()) {
                action()
            } else {
                SwingUtilities.invokeLater(action)
            }
        }

        private fun initialWindowSize(fullscreen: Boolean): Vec2i {
            if (!fullscreen) return DEFAULT_WINDOW_SIZE
            val bounds = fullscreenBounds()
            return Vec2i(bounds.width.coerceAtLeast(800), bounds.height.coerceAtLeast(600))
        }

        private fun fullscreenBounds() =
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                .defaultScreenDevice
                .defaultConfiguration
                .bounds

    }
}

internal fun dispatchCanvasVisibilityChange(
    gameCanvasShowing: Boolean,
    action: () -> Unit,
) {
    if (SwingUtilities.isEventDispatchThread()) {
        action()
    } else if (gameCanvasShowing) {
        SwingUtilities.invokeLater(action)
    } else {
        SwingUtilities.invokeAndWait(action)
    }
}

internal fun committedInputMethodText(
    text: AttributedCharacterIterator?,
    committedCharacterCount: Int,
): String {
    if (text == null || committedCharacterCount <= 0) return ""
    return buildString(committedCharacterCount) {
        var character = text.first()
        repeat(committedCharacterCount) {
            if (character == AttributedCharacterIterator.DONE) return@buildString
            if (!character.isISOControl()) {
                append(character)
            }
            character = text.next()
        }
    }
}

internal fun shouldSuppressKoolTypedCharacter(event: KeyEvent): Boolean =
    event.id == KeyEvent.KEY_TYPED && event.keyChar.isISOControl()

private class PointerCursorCanvas : Canvas() {
    var pointerCursor: Cursor? = null
    var inGamePointerCursorActive: Boolean = false
    private val imeRequests = object : InputMethodRequests {
        override fun getTextLocation(offset: TextHitInfo?): Rectangle {
            val screenLocation = runCatching { locationOnScreen }.getOrDefault(Point())
            return Rectangle(
                screenLocation.x + IME_CANDIDATE_OFFSET_PX,
                screenLocation.y + height - IME_CANDIDATE_OFFSET_PX,
                0,
                0,
            )
        }

        override fun getLocationOffset(x: Int, y: Int): TextHitInfo? = null

        override fun getInsertPositionOffset(): Int = 0

        override fun getCommittedText(
            beginIndex: Int,
            endIndex: Int,
            attributes: Array<out AttributedCharacterIterator.Attribute>?,
        ): AttributedCharacterIterator = AttributedString("").iterator

        override fun getCommittedTextLength(): Int = 0

        override fun cancelLatestCommittedText(
            attributes: Array<out AttributedCharacterIterator.Attribute>?,
        ): AttributedCharacterIterator? = null

        override fun getSelectedText(
            attributes: Array<out AttributedCharacterIterator.Attribute>?,
        ): AttributedCharacterIterator? = null
    }

    override fun getInputMethodRequests(): InputMethodRequests = imeRequests

    override fun setCursor(cursor: Cursor?) {
        if (inGamePointerCursorActive && cursor?.type == Cursor.DEFAULT_CURSOR) {
            super.setCursor(pointerCursor ?: cursor)
        } else {
            super.setCursor(cursor)
        }
    }
}

private const val IME_CANDIDATE_OFFSET_PX = 24
