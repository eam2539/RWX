package io.github.rwx.input

import de.fabmax.kool.input.KeyCode
import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.input.KeyboardInput

object KoolKeyCodeMapping {
    fun androidKeyCode(event: KeyEvent): Int? =
        androidKeyCode(event.keyCode) ?: androidKeyCode(event.localKeyCode)

    fun modifierMask(event: KeyEvent): Int {
        var mask = 0
        if (event.isCtrlDown) mask = mask or MOD_CTRL
        if (event.isShiftDown) mask = mask or MOD_SHIFT
        if (event.isAltDown) mask = mask or MOD_ALT
        return mask
    }

    fun isModifierOnly(event: KeyEvent): Boolean =
        androidKeyCode(event)?.let(::isModifierKey) == true

    private fun androidKeyCode(keyCode: KeyCode): Int? = when (keyCode) {
        KeyboardInput.KEY_CURSOR_UP -> ANDROID_DPAD_UP
        KeyboardInput.KEY_CURSOR_DOWN -> ANDROID_DPAD_DOWN
        KeyboardInput.KEY_CURSOR_LEFT -> ANDROID_DPAD_LEFT
        KeyboardInput.KEY_CURSOR_RIGHT -> ANDROID_DPAD_RIGHT
        KeyboardInput.KEY_ESC -> ANDROID_ESCAPE
        KeyboardInput.KEY_ENTER -> ANDROID_ENTER
        KeyboardInput.KEY_BACKSPACE -> ANDROID_DEL
        KeyboardInput.KEY_TAB -> ANDROID_TAB
        KeyboardInput.KEY_SHIFT_LEFT -> ANDROID_SHIFT_LEFT
        KeyboardInput.KEY_SHIFT_RIGHT -> ANDROID_SHIFT_RIGHT
        KeyboardInput.KEY_CTRL_LEFT -> ANDROID_CTRL_LEFT
        KeyboardInput.KEY_CTRL_RIGHT -> ANDROID_CTRL_RIGHT
        KeyboardInput.KEY_ALT_LEFT -> ANDROID_ALT_LEFT
        KeyboardInput.KEY_ALT_RIGHT -> ANDROID_ALT_RIGHT
        KeyboardInput.KEY_NP_ENTER -> ANDROID_NUMPAD_ENTER
        KeyboardInput.KEY_NP_DIV -> ANDROID_NUMPAD_DIVIDE
        KeyboardInput.KEY_NP_MUL -> ANDROID_NUMPAD_MULTIPLY
        KeyboardInput.KEY_NP_PLUS -> ANDROID_NUMPAD_ADD
        KeyboardInput.KEY_NP_MINUS -> ANDROID_NUMPAD_SUBTRACT
        KeyboardInput.KEY_NP_DECIMAL -> ANDROID_NUMPAD_DOT
        KeyboardInput.KEY_DEL -> ANDROID_FORWARD_DEL
        KeyboardInput.KEY_INSERT -> ANDROID_INSERT
        KeyboardInput.KEY_HOME -> ANDROID_MOVE_HOME
        KeyboardInput.KEY_END -> ANDROID_MOVE_END
        KeyboardInput.KEY_PAGE_UP -> ANDROID_PAGE_UP
        KeyboardInput.KEY_PAGE_DOWN -> ANDROID_PAGE_DOWN
        KeyboardInput.KEY_F1 -> ANDROID_F1
        KeyboardInput.KEY_F2 -> ANDROID_F2
        KeyboardInput.KEY_F3 -> ANDROID_F3
        KeyboardInput.KEY_F4 -> ANDROID_F4
        KeyboardInput.KEY_F5 -> ANDROID_F5
        KeyboardInput.KEY_F6 -> ANDROID_F6
        KeyboardInput.KEY_F7 -> ANDROID_F7
        KeyboardInput.KEY_F8 -> ANDROID_F8
        KeyboardInput.KEY_F9 -> ANDROID_F9
        KeyboardInput.KEY_F10 -> ANDROID_F10
        KeyboardInput.KEY_F11 -> ANDROID_F11
        KeyboardInput.KEY_F12 -> ANDROID_F12
        else -> androidKeyCode(keyCode.code)
    }

    private fun androidKeyCode(code: Int): Int? {
        if (code < 0) return null
        val upper = code.toChar().uppercaseChar().code
        return when {
            code in AWT_NUMPAD_0..AWT_NUMPAD_9 -> ANDROID_NUMPAD_0 + (code - AWT_NUMPAD_0)
            code in GLFW_NUMPAD_0..GLFW_NUMPAD_9 -> ANDROID_NUMPAD_0 + (code - GLFW_NUMPAD_0)
            code == AWT_PAUSE || code == GLFW_PAUSE -> ANDROID_BREAK
            code == AWT_BACK_QUOTE -> ANDROID_GRAVE
            code == AWT_QUOTE -> ANDROID_APOSTROPHE
            upper in 'A'.code..'Z'.code -> ANDROID_A + (upper - 'A'.code)
            code in '0'.code..'9'.code -> ANDROID_0 + (code - '0'.code)
            code == ' '.code -> ANDROID_SPACE
            code == ','.code -> ANDROID_COMMA
            code == '.'.code -> ANDROID_PERIOD
            code == '-'.code -> ANDROID_MINUS
            code == '='.code -> ANDROID_EQUALS
            code == '['.code -> ANDROID_LEFT_BRACKET
            code == ']'.code -> ANDROID_RIGHT_BRACKET
            code == '\\'.code -> ANDROID_BACKSLASH
            code == ';'.code -> ANDROID_SEMICOLON
            code == '\''.code -> ANDROID_APOSTROPHE
            code == '/'.code -> ANDROID_SLASH
            code == '`'.code -> ANDROID_GRAVE
            else -> null
        }
    }

    private fun isModifierKey(androidKeyCode: Int): Boolean =
        androidKeyCode == ANDROID_SHIFT_LEFT ||
                androidKeyCode == ANDROID_SHIFT_RIGHT ||
                androidKeyCode == ANDROID_CTRL_LEFT ||
                androidKeyCode == ANDROID_CTRL_RIGHT ||
                androidKeyCode == ANDROID_ALT_LEFT ||
                androidKeyCode == ANDROID_ALT_RIGHT

    const val MOD_CTRL: Int = 1
    const val MOD_SHIFT: Int = 2
    const val MOD_ALT: Int = 4

    const val ANDROID_0: Int = 7
    const val ANDROID_A: Int = 29
    const val ANDROID_DPAD_UP: Int = 19
    const val ANDROID_DPAD_DOWN: Int = 20
    const val ANDROID_DPAD_LEFT: Int = 21
    const val ANDROID_DPAD_RIGHT: Int = 22
    const val ANDROID_STAR: Int = 17
    const val ANDROID_COMMA: Int = 55
    const val ANDROID_PERIOD: Int = 56
    const val ANDROID_ALT_LEFT: Int = 57
    const val ANDROID_ALT_RIGHT: Int = 58
    const val ANDROID_SHIFT_LEFT: Int = 59
    const val ANDROID_SHIFT_RIGHT: Int = 60
    const val ANDROID_TAB: Int = 61
    const val ANDROID_SPACE: Int = 62
    const val ANDROID_ENTER: Int = 66
    const val ANDROID_DEL: Int = 67
    const val ANDROID_GRAVE: Int = 68
    const val ANDROID_MINUS: Int = 69
    const val ANDROID_EQUALS: Int = 70
    const val ANDROID_LEFT_BRACKET: Int = 71
    const val ANDROID_RIGHT_BRACKET: Int = 72
    const val ANDROID_BACKSLASH: Int = 73
    const val ANDROID_SEMICOLON: Int = 74
    const val ANDROID_APOSTROPHE: Int = 75
    const val ANDROID_SLASH: Int = 76
    const val ANDROID_PLUS: Int = 81
    const val ANDROID_ESCAPE: Int = 111
    const val ANDROID_FORWARD_DEL: Int = 112
    const val ANDROID_CTRL_LEFT: Int = 113
    const val ANDROID_CTRL_RIGHT: Int = 114
    const val ANDROID_INSERT: Int = 124
    const val ANDROID_MOVE_HOME: Int = 122
    const val ANDROID_MOVE_END: Int = 123
    const val ANDROID_BREAK: Int = 121
    const val ANDROID_PAGE_UP: Int = 92
    const val ANDROID_PAGE_DOWN: Int = 93
    const val ANDROID_F1: Int = 131
    const val ANDROID_F2: Int = 132
    const val ANDROID_F3: Int = 133
    const val ANDROID_F4: Int = 134
    const val ANDROID_F5: Int = 135
    const val ANDROID_F6: Int = 136
    const val ANDROID_F7: Int = 137
    const val ANDROID_F8: Int = 138
    const val ANDROID_F9: Int = 139
    const val ANDROID_F10: Int = 140
    const val ANDROID_F11: Int = 141
    const val ANDROID_F12: Int = 142
    const val ANDROID_NUMPAD_0: Int = 144
    const val ANDROID_NUMPAD_DIVIDE: Int = 154
    const val ANDROID_NUMPAD_MULTIPLY: Int = 155
    const val ANDROID_NUMPAD_SUBTRACT: Int = 156
    const val ANDROID_NUMPAD_ADD: Int = 157
    const val ANDROID_NUMPAD_DOT: Int = 158
    const val ANDROID_NUMPAD_ENTER: Int = 160

    private const val AWT_PAUSE: Int = 19
    private const val AWT_NUMPAD_0: Int = 96
    private const val AWT_NUMPAD_9: Int = 105
    private const val AWT_BACK_QUOTE: Int = 192
    private const val AWT_QUOTE: Int = 222
    private const val GLFW_PAUSE: Int = 284
    private const val GLFW_NUMPAD_0: Int = 320
    private const val GLFW_NUMPAD_9: Int = 329
}
