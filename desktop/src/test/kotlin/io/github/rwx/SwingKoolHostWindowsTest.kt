package io.github.rwx

import org.lwjgl.awt.AWT as LwjglAwt
import org.lwjgl.system.jawt.JAWTWin32DrawingSurfaceInfo
import org.lwjgl.system.windows.User32
import java.awt.BorderLayout
import java.awt.Canvas
import java.awt.Color
import java.awt.GraphicsEnvironment
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JWindow
import javax.swing.SwingUtilities
import kotlin.test.Test
import kotlin.test.assertTrue

class SwingKoolHostWindowsTest {
    @Test
    fun `kool overlay stays transparent and receives mouse clicks on Windows`() {
        if (!isWindows() || GraphicsEnvironment.isHeadless()) return

        val clicked = CountDownLatch(1)
        var frame: JFrame? = null
        var overlayWindow: JWindow? = null
        var layeredStylePreserved = false

        try {
            SwingUtilities.invokeAndWait {
                val testFrame = JFrame("RWX overlay input test").apply {
                    setBounds(120, 120, 320, 240)
                    contentPane.background = UnderlayColor
                    isVisible = true
                }
                frame = testFrame

                val koolCanvas = Canvas().apply {
                    background = Color(0, 0, 0, 0)
                    addMouseListener(object : MouseAdapter() {
                        override fun mouseClicked(event: MouseEvent) {
                            clicked.countDown()
                        }
                    })
                }
                val overlayPanel = JPanel(BorderLayout())
                overlayWindow = JWindow(testFrame).apply {
                    configureKoolOverlayWindow(this, overlayPanel, koolCanvas)
                    bounds = testFrame.bounds
                    isVisible = true
                    layeredStylePreserved = isLayeredWindow(this)
                    toFront()
                }
            }

            val robot = Robot().apply {
                waitForIdle()
                delay(100)
                mouseMove(280, 240)
                mousePress(InputEvent.BUTTON1_DOWN_MASK)
                mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
                waitForIdle()
            }

            val displayedBackground = robot.getPixelColor(280, 240)
            assertTrue(layeredStylePreserved, "透明 Kool 覆盖层不应移除 WS_EX_LAYERED")
            assertTrue(
                displayedBackground.red > 200 && displayedBackground.blue > 200 && displayedBackground.green < 50,
                "透明 Kool 覆盖层遮住了底层 Swing 背景: $displayedBackground",
            )
            assertTrue(clicked.await(2, TimeUnit.SECONDS), "透明 Kool 覆盖层没有收到鼠标点击")
        } finally {
            SwingUtilities.invokeAndWait {
                overlayWindow?.dispose()
                frame?.dispose()
            }
        }
    }

    private fun isWindows(): Boolean =
        System.getProperty("os.name").lowercase(Locale.ROOT).contains("win")

    private fun isLayeredWindow(window: JWindow): Boolean =
        LwjglAwt(window).use { awt ->
            val windowHandle = JAWTWin32DrawingSurfaceInfo.create(awt.platformInfo).hwnd()
            val extendedStyle = User32.GetWindowLongPtr(null, windowHandle, User32.GWL_EXSTYLE)
            extendedStyle and User32.WS_EX_LAYERED.toLong() != 0L
        }

    private companion object {
        val UnderlayColor = Color(240, 0, 240)
    }
}
