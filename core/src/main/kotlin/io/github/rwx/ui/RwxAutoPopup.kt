package io.github.rwx.ui

import com.corrodinggames.rts.gameFramework.GameEngine
import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.launchDelayed

/**
 * Kool 0.19.0 AutoPopup with its Android focus calls bypassed. Kool's Android
 * KeyboardInput.hideKeyboard() uses toggleSoftInput(), which can show a hidden keyboard.
 */
fun UiScope.RwxAutoPopup(
    hideOnEsc: Boolean = true,
    hideOnOutsideClick: Boolean = true,
    scopeName: String? = null,
    block: UiScope.() -> Unit,
): RwxAutoPopup = remember {
    val popup = RwxAutoPopup(hideOnEsc, hideOnOutsideClick, scopeName)
    popup.popupContent = Composable(block)
    popup
}

open class RwxAutoPopup(
    val hideOnEsc: Boolean = true,
    val hideOnOutsideClick: Boolean = true,
    private val scopeName: String? = null,
) : Composable, Focusable {

    val isVisible = mutableStateOf(false)
    val screenPosPx = mutableStateOf(Vec2f.ZERO)

    var popupContent = Composable { }
    var onShow: (() -> Unit)? = null
    var onHide: (() -> Unit)? = null

    override val isFocused = mutableStateOf(false)
    private var parentSurface: UiSurface? = null
    private var hideCnt = 0
    private var revokeHide = 0

    open fun show(pointerEvent: PointerEvent) = show(pointerEvent.screenPosition)

    open fun show(screenPosPx: Vec2f) {
        this.screenPosPx.set(Vec2f(screenPosPx))
        revokeHide = hideCnt
        isVisible.set(true)
        requestPopupFocus(parentSurface)
        onShow?.invoke()
    }

    open fun hide() {
        isVisible.set(false)
        if (isFocused.value) {
            parentSurface?.unfocus(this)
        }
        if (GameEngine.isAndroidPlatform()) {
            PlatformTextInputBridge.dismissKeyboard()
        }
        onHide?.invoke()
    }

    open fun toggleVisibility(showScreenPosPx: Vec2f) {
        if (isVisible.value) {
            hide()
        } else {
            show(showScreenPosPx)
        }
    }

    override fun UiScope.compose() {
        parentSurface = surface

        if (isVisible.use()) {
            val pos = screenPosPx.use()
            Popup(pos.x, pos.y, scopeName = scopeName) {
                modifier
                    .onPositioned { checkPopupPos(it) }
                    .onHover { }
                    .onEnter { }
                    .onDrag { }
                    .onClick { requestPopupFocus(surface) }

                popupContent()

                if (hideOnOutsideClick) {
                    // Keep Kool's one-frame delay so the outside control can reopen the popup.
                    surface.onEachFrame {
                        val ptr = PointerInput.primaryPointer
                        if (ptr.isAnyButtonReleased && !uiNode.isInBounds(ptr.pos)) {
                            hideCnt++
                            coroutineScope.launchDelayed(1) {
                                if (revokeHide < hideCnt) {
                                    hide()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestPopupFocus(surface: UiSurface?) {
        if (GameEngine.isAndroidPlatform()) {
            PlatformTextInputBridge.dismissKeyboard()
        } else {
            surface?.requestFocus(this)
        }
    }

    private fun checkPopupPos(popupNode: UiNode) {
        var movedPos = screenPosPx.value
        if (screenPosPx.value.x < 0f) {
            movedPos = Vec2f(0f, movedPos.y)
        }
        if (screenPosPx.value.y < 0f) {
            movedPos = Vec2f(movedPos.x, 0f)
        }
        if (popupNode.rightPx > popupNode.surface.viewport.rightPx) {
            movedPos = Vec2f(movedPos.x - popupNode.rightPx + popupNode.surface.viewport.rightPx, movedPos.y)
        }
        if (popupNode.bottomPx > popupNode.surface.viewport.bottomPx) {
            movedPos = Vec2f(movedPos.x, movedPos.y - popupNode.bottomPx + popupNode.surface.viewport.bottomPx)
        }
        screenPosPx.set(movedPos)
    }

    override fun onKeyEvent(keyEvent: KeyEvent) {
        if (hideOnEsc && keyEvent.keyCode == KeyboardInput.KEY_ESC && keyEvent.isPressed) {
            hide()
        }
    }
}
