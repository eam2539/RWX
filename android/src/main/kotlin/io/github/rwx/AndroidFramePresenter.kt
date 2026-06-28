package io.github.rwx

import android.app.Activity
import android.view.View
import com.corrodinggames.rts.appFramework.GameViewOpenGL
import com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface

internal enum class AndroidRendererMode(
    val backendId: String,
    val sessionLogName: String,
    val threadPrefix: String,
) {
    CANVAS(
        backendId = "android-canvas",
        sessionLogName = "Android Canvas RW",
        threadPrefix = "RWX-android-canvas",
    ),
    OPENGL(
        backendId = "android-opengles",
        sessionLogName = "Android OpenGL RW",
        threadPrefix = "RWX-android-opengl",
    ),
}

internal class AndroidPresentedFrame(
    val graphics: GraphicsInterface,
    private val submitAction: () -> Unit,
    private val cancelAction: () -> Unit,
) {
    fun submit() = submitAction()

    fun cancel() = cancelAction()
}

internal interface AndroidFramePresenter {
    val view: View

    fun isReady(): Boolean

    fun acquireFrame(): AndroidPresentedFrame?

    fun pause()

    fun resume()

    fun setVisible(visible: Boolean)
}

internal fun AndroidRendererMode.createPresenter(activity: Activity): AndroidFramePresenter =
    when (this) {
        AndroidRendererMode.CANVAS -> AndroidCanvasFramePresenter(CanvasGameView(activity))
        AndroidRendererMode.OPENGL -> AndroidOpenGlFramePresenter(GameViewOpenGL(activity, null))
    }

private class AndroidCanvasFramePresenter(
    private val canvasView: CanvasGameView,
) : AndroidFramePresenter {
    override val view: View
        get() = canvasView

    override fun isReady(): Boolean = canvasView.isReady()

    override fun acquireFrame(): AndroidPresentedFrame? {
        val frame = canvasView.acquireFrame() ?: return null
        return AndroidPresentedFrame(
            graphics = frame.renderer,
            submitAction = { canvasView.submitFrame(frame) },
            cancelAction = { canvasView.cancelFrame(frame) },
        )
    }

    override fun pause() {
        canvasView.paused = true
    }

    override fun resume() {
        canvasView.paused = false
        canvasView.invalidate()
    }

    override fun setVisible(visible: Boolean) {
        if (visible) resume() else pause()
    }
}

private class AndroidOpenGlFramePresenter(
    private val openGlView: GameViewOpenGL,
) : AndroidFramePresenter {
    override val view: View
        get() = openGlView

    override fun isReady(): Boolean =
        !openGlView.paused &&
                openGlView.surfaceExists &&
                openGlView.isAttachedToWindow &&
                openGlView.visibility == View.VISIBLE &&
                openGlView.width > 0 &&
                openGlView.height > 0

    override fun acquireFrame(): AndroidPresentedFrame {
        val graphics = openGlView.getNewCanvasLock(true)
        return AndroidPresentedFrame(
            graphics = graphics,
            submitAction = { openGlView.unlockAndReturnCanvas(graphics, true) },
            cancelAction = {},
        )
    }

    override fun pause() {
        openGlView.paused = true
        openGlView.onPause()
        openGlView.onParentPause()
    }

    override fun resume() {
        openGlView.onResume()
        openGlView.onParentResume()
    }

    override fun setVisible(visible: Boolean) {
        if (visible) resume() else pause()
    }
}
