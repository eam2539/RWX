package io.github.rwx

import java.awt.BorderLayout
import java.awt.Canvas
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
    fun `kool overlay receives mouse clicks on Windows`() {
        if (!isWindows() || GraphicsEnvironment.isHeadless()) return

        val clicked = CountDownLatch(1)
        var frame: JFrame? = null
        var overlayWindow: JWindow? = null

        try {
            SwingUtilities.invokeAndWait {
                val testFrame = JFrame("RWX overlay input test").apply {
                    setBounds(120, 120, 320, 240)
                    isVisible = true
                }
                frame = testFrame

                val koolCanvas = Canvas().apply {
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
                    enableWindowsKoolOverlayInput(this)
                    toFront()
                }
            }

            Robot().apply {
                waitForIdle()
                delay(100)
                mouseMove(280, 240)
                mousePress(InputEvent.BUTTON1_DOWN_MASK)
                mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
                waitForIdle()
            }

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
}
