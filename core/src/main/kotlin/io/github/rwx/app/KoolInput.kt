package io.github.rwx.app

import com.corrodinggames.rts.gameFramework.GameEngine
import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.input.PointerState
import io.github.rwx.input.KoolKeyCodeMapping
import io.github.rwx.mod.ModUiRegistry
import io.github.rwx.mod.api.WorldPosition
import io.github.rwx.session.GameSession

fun interface KoolScreenScaleProvider {
    fun screenToFramebufferScale(): Float
}

fun interface KoolLegacyPointerSink {
    fun onPointer(pointer: Pointer)
}

class LegacyGamePointerSink(
    private val gameSession: GameSession,
    private val scaleProvider: KoolScreenScaleProvider = KoolScreenScaleProvider { 1.0f },
) : KoolLegacyPointerSink, InputStack.PointerListener {
    private var activePointerId = NO_BUTTON_ID
    private var suppressPointerUntilRelease = false

    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        onPointer(pointerState.primaryPointer)
    }

    override fun onPointer(pointer: Pointer) {
        val scale = scaleProvider.screenToFramebufferScale().takeIf { it.isFinite() && it > 0.0f } ?: 1.0f
        val screenX = pointer.pos.x * scale
        val screenY = pointer.pos.y * scale
        if (handleWorldPositionSelection(pointer, screenX, screenY)) return
        pointer.scroll.y.takeIf { it != 0f }?.let { scroll ->
            gameSession.submitMouseWheel((scroll * 120f).toInt())
        }
        val pointerId = pointer.legacyButtonId()
        if (pointerId == NO_BUTTON_ID) {
            if (activePointerId != NO_BUTTON_ID) {
                gameSession.submitPointer(
                    screenX = screenX,
                    screenY = screenY,
                    isDown = false,
                    pointerId = activePointerId,
                )
                activePointerId = NO_BUTTON_ID
            }
            if (pointer.isValid) {
                gameSession.movePointer(screenX, screenY)
            }
            return
        }
        if (activePointerId != NO_BUTTON_ID && activePointerId != pointerId) {
            gameSession.submitPointer(
                screenX = screenX,
                screenY = screenY,
                isDown = false,
                pointerId = activePointerId,
            )
        }
        val isDown = pointer.isValid && pointer.isLegacyButtonDown()
        gameSession.submitPointer(
            screenX = screenX,
            screenY = screenY,
            isDown = isDown,
            pointerId = pointerId,
        )
        activePointerId = if (isDown) pointerId else NO_BUTTON_ID
    }

    private fun handleWorldPositionSelection(pointer: Pointer, screenX: Float, screenY: Float): Boolean {
        if (!ModUiRegistry.hasActiveWorldPositionSelection()) {
            if (!suppressPointerUntilRelease) return false
            if (!pointer.hasLegacyButtonActivity()) suppressPointerUntilRelease = false
            return true
        }

        suppressPointerUntilRelease = true
        when {
            pointer.isRightButtonPressed -> ModUiRegistry.cancelWorldPositionSelection()
            pointer.isLeftButtonPressed -> {
                val engine = GameEngine.getInstance()
                val zoom = engine.zoom
                if (zoom.isFinite() && zoom > 0f && screenX.isFinite() && screenY.isFinite()) {
                    ModUiRegistry.selectWorldPosition(
                        WorldPosition(
                            x = screenX / zoom + engine.viewpointXSnapped,
                            y = screenY / zoom + engine.viewpointYSnapped,
                        )
                    )
                }
            }
        }
        return true
    }

    private fun Pointer.hasLegacyButtonActivity(): Boolean =
        isLeftButtonDown || isLeftButtonPressed || isLeftButtonReleased ||
                isRightButtonDown || isRightButtonPressed || isRightButtonReleased ||
                isMiddleButtonDown || isMiddleButtonPressed || isMiddleButtonReleased

    private fun Pointer.legacyButtonId(): Int = when {
        isLeftButtonDown || isLeftButtonPressed || isLeftButtonReleased -> LEFT_BUTTON_ID
        isRightButtonDown || isRightButtonPressed || isRightButtonReleased -> RIGHT_BUTTON_ID
        isMiddleButtonDown || isMiddleButtonPressed || isMiddleButtonReleased -> MIDDLE_BUTTON_ID
        else -> NO_BUTTON_ID
    }

    private fun Pointer.isLegacyButtonDown(): Boolean =
        isLeftButtonDown || isRightButtonDown || isMiddleButtonDown

    private companion object {
        const val NO_BUTTON_ID: Int = -1
        const val LEFT_BUTTON_ID: Int = 1
        const val RIGHT_BUTTON_ID: Int = 2
        const val MIDDLE_BUTTON_ID: Int = 3
    }
}

class LegacyGameKeyboardSink(
    private val gameSession: GameSession,
) : InputStack.KeyboardListener {
    override fun handleKeyboard(keyEvents: List<KeyEvent>, ctx: KoolContext) {
        keyEvents.forEach { event ->
            if (event.isCharTyped) {
                return@forEach
            }
            val androidKeyCode = event.androidKeyCode() ?: return@forEach
            when {
                event.isPressed || event.isRepeated -> {
                    gameSession.submitKey(androidKeyCode, true)
                    event.isConsumed = true
                }

                event.isReleased -> {
                    gameSession.submitKey(androidKeyCode, false)
                    event.isConsumed = true
                }
            }
        }
    }

    private fun KeyEvent.androidKeyCode(): Int? = KoolKeyCodeMapping.androidKeyCode(this)
}

class GatedPointerListener(
    private val isEnabled: () -> Boolean,
    private val delegate: InputStack.PointerListener,
) : InputStack.PointerListener {
    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        if (isEnabled()) {
            delegate.handlePointer(pointerState, ctx)
        }
    }
}

class GatedKeyboardListener(
    private val isEnabled: () -> Boolean,
    private val delegate: InputStack.KeyboardListener,
) : InputStack.KeyboardListener {
    override fun handleKeyboard(keyEvents: List<KeyEvent>, ctx: KoolContext) {
        if (isEnabled()) {
            delegate.handleKeyboard(keyEvents, ctx)
        }
    }
}
