package io.github.rwx.slick

import java.awt.Canvas
import java.awt.Dimension
import javax.swing.SwingUtilities

object SlickCanvasHost {
    @Volatile
    private var canvasProvider: (() -> Canvas?)? = null

    @Volatile
    private var visibilityController: ((Boolean, Boolean) -> Unit)? = null

    @Volatile
    private var resizeController: ((Int, Int) -> Unit)? = null

    @Volatile
    private var rendererShutdown: (() -> Unit)? = null

    fun install(
        canvasProvider: () -> Canvas?,
        visibilityController: (Boolean, Boolean) -> Unit,
    ) {
        this.canvasProvider = canvasProvider
        this.visibilityController = visibilityController
    }

    fun setRendererShutdown(shutdown: (() -> Unit)?) {
        rendererShutdown = shutdown
    }

    /**
     * Stops the embedded renderer and blocks until its GL thread has released the AWT drawing
     * surface. Call before disposing the game canvas' window to avoid a native JAWT crash.
     */
    fun shutdownRenderer() {
        rendererShutdown?.invoke()
    }

    fun gameCanvas(): Canvas? = canvasProvider?.invoke()

    fun gameCanvasSize(): Dimension? = canvasProvider?.invoke()?.size

    fun setResizeController(controller: ((Int, Int) -> Unit)?) {
        resizeController = controller
    }

    fun notifyGameCanvasResized(width: Int, height: Int) {
        resizeController?.invoke(width, height)
    }

    fun setGameVisible(visible: Boolean, koolOverlay: Boolean = false) {
        visibilityController?.invoke(visible, koolOverlay)
    }

    fun requestGameFocus() {
        val canvas = gameCanvas() ?: return
        val action = {
            if (canvas.isVisible && canvas.isShowing) {
                canvas.isFocusable = true
                if (!canvas.requestFocusInWindow()) {
                    canvas.requestFocus()
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
