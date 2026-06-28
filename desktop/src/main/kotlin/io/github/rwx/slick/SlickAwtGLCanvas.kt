package io.github.rwx.slick

import org.lwjgl.opengl.*
import org.lwjgl.opengl.awt.AWTGLCanvas
import org.lwjgl.opengl.awt.GLData
import org.lwjgl.opengl.awt.PlatformLinuxGLCanvas
import org.lwjgl.system.Platform

internal class SlickAwtGLCanvas(
    data: GLData,
    private var requestedSwapInterval: Int? = null,
) : AWTGLCanvas(data) {
    private var appliedSwapInterval: Int? = null

    override fun initGL() = Unit

    override fun paintGL() = Unit

    fun requestSwapInterval(interval: Int?) {
        requestedSwapInterval = interval
    }

    fun applyRuntimeGlSettings() {
        val interval = requestedSwapInterval ?: return
        if (appliedSwapInterval == interval) return

        val applied = runCatching {
            when (Platform.get()) {
                Platform.LINUX -> applyLinuxSwapInterval(interval)
                Platform.WINDOWS -> applyWindowsSwapInterval(interval)
                Platform.MACOSX -> applyMacSwapInterval(interval)
                else -> false
            }
        }.getOrDefault(false)

        if (applied) {
            appliedSwapInterval = interval
        }
    }

    private fun applyLinuxSwapInterval(interval: Int): Boolean {
        val capabilities = GL.getCapabilitiesGLX()
        if (!capabilities.GLX_EXT_swap_control) return false

        val linuxCanvas = platformCanvas as? PlatformLinuxGLCanvas
        val display = linuxCanvas?.display?.takeIf { it != 0L } ?: GLX12.glXGetCurrentDisplay()
        val drawable = linuxCanvas?.drawable?.takeIf { it != 0L } ?: GLX.glXGetCurrentDrawable()
        if (display == 0L || drawable == 0L) return false

        GLXEXTSwapControl.glXSwapIntervalEXT(display, drawable, interval)
        return true
    }

    private fun applyWindowsSwapInterval(interval: Int): Boolean {
        val capabilities = GL.getCapabilitiesWGL()
        if (!capabilities.WGL_EXT_swap_control) return false
        return WGLEXTSwapControl.wglSwapIntervalEXT(interval)
    }

    private fun applyMacSwapInterval(interval: Int): Boolean {
        val context = CGL.CGLGetCurrentContext()
        if (context == 0L) return false
        return CGL.CGLSetParameter(context, CGL.kCGLCPSwapInterval, interval) == 0
    }
}
