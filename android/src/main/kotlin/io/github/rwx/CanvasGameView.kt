package io.github.rwx

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.corrodinggames.rts.gameFramework.android.graphics.DeferredGraphicsRenderer
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * Android Canvas presenter backed by its own Surface. Kool's translucent SurfaceView is placed
 * above this one while menus are visible, allowing the native game frame to remain visible below
 * the UI. A regular View cannot be used here because the upper SurfaceView punches a hole in the
 * host window and hides regular sibling views beneath it.
 */
class CanvasGameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    class Frame internal constructor(
        internal val buffer: FrameBuffer,
    ) {
        val renderer: DeferredGraphicsRenderer
            get() = buffer.renderer
    }

    internal class FrameBuffer(
        val index: Int,
    ) {
        val renderer = DeferredGraphicsRenderer()
        val drawLock = ReentrantLock()

        /** True while the game thread may record and present commands into this buffer. */
        @Volatile
        var writable = true
    }

    private val bufferChangeLock = Any()
    private val buffers = Array(BUFFER_COUNT) { FrameBuffer(it) }

    @Volatile
    private var surfaceReady: Boolean = false

    @Volatile
    var paused: Boolean = false
        set(value) {
            field = value
            if (value) {
                clearFrames()
            }
        }

    init {
        holder.setFormat(PixelFormat.OPAQUE)
        holder.addCallback(this)
    }

    fun isReady(): Boolean =
        !paused && surfaceReady && holder.surface.isValid && isAttachedToWindow &&
                visibility == VISIBLE && width > 0 && height > 0

    fun acquireFrame(): Frame? {
        val buffer = acquireUpdateBuffer() ?: return null
        if (!lockBuffer(buffer)) {
            return null
        }
        buffer.renderer.a()
        return Frame(buffer)
    }

    fun submitFrame(frame: Frame) {
        val buffer = frame.buffer
        buffer.renderer.a(true)
        var canvas: Canvas? = null
        try {
            if (surfaceReady && holder.surface.isValid) {
                canvas = holder.lockHardwareCanvas()
                if (canvas != null) {
                    buffer.renderer.a(canvas)
                }
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas)
            }
            buffer.renderer.a(false)
            synchronized(bufferChangeLock) {
                buffer.writable = true
                buffer.drawLock.unlock()
            }
        }
    }

    fun cancelFrame(frame: Frame) {
        val buffer = frame.buffer
        synchronized(bufferChangeLock) {
            buffer.drawLock.unlock()
            buffer.writable = true
        }
    }

    fun clearFrames() {
        buffers.forEach { buffer ->
            if (lockBuffer(buffer)) {
                try {
                    buffer.renderer.a()
                    buffer.renderer.a(false)
                } finally {
                    buffer.drawLock.unlock()
                }
            }
            synchronized(bufferChangeLock) {
                buffer.writable = true
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceReady = true
        var canvas: Canvas? = null
        try {
            canvas = holder.lockHardwareCanvas()
            canvas?.drawColor(Color.BLACK)
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        surfaceReady = width > 0 && height > 0
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceReady = false
        clearFrames()
    }

    private fun acquireUpdateBuffer(): FrameBuffer? {
        if (!isReady()) return null
        return findUpdateBuffer()
    }

    private fun findUpdateBuffer(): FrameBuffer? = synchronized(bufferChangeLock) {
        buffers.firstOrNull { it.writable }
    }

    private fun lockBuffer(buffer: FrameBuffer): Boolean =
        try {
            buffer.drawLock.tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            false
        }

    private companion object {
        const val BUFFER_COUNT = 2
        const val LOCK_TIMEOUT_MS = 250L
    }
}
