package io.github.rwx

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.LocalKeyCode
import de.fabmax.kool.util.FrontendScope
import io.github.rwx.ui.component.PlatformTextInputController
import io.github.rwx.ui.component.PlatformTextInputRequest
import kotlinx.coroutines.launch
import android.view.KeyEvent as AndroidKeyEvent

internal class AndroidTextInputController(
    private val activity: Activity,
    private val root: FrameLayout,
    private val koolSurface: View,
) : PlatformTextInputController {
    private val inputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    private val editor = EditText(activity)
    private val visibleFrame = Rect()

    @Volatile
    private var activeRequest: PlatformTextInputRequest? = null

    @Volatile
    private var suppressedOwner: Any? = null

    private var suppressTextCallback = false
    private var lastForwardedText = ""
    private var keyboardWasVisible = false
    private var unexpectedKeyboardHidePending = false
    private val globalLayoutListener = android.view.ViewTreeObserver.OnGlobalLayoutListener {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            root.getWindowVisibleDisplayFrame(visibleFrame)
            val obscuredHeight = (root.rootView.height - visibleFrame.bottom).coerceAtLeast(0)
            val isVisible = obscuredHeight > root.rootView.height * KEYBOARD_VISIBLE_HEIGHT_RATIO
            updateKeyboardVisibility(isVisible, if (isVisible) obscuredHeight else 0)
        }
    }

    init {
        editor.apply {
            visibility = View.GONE
            isSingleLine = true
            textSize = EDITOR_TEXT_SIZE_SP
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            setPadding(dp(EDITOR_HORIZONTAL_PADDING_DP), 0, dp(EDITOR_HORIZONTAL_PADDING_DP), 0)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            imeOptions = EditorInfo.IME_ACTION_DONE or EditorInfo.IME_FLAG_NO_EXTRACT_UI
            elevation = dp(EDITOR_ELEVATION_DP).toFloat()
            background = GradientDrawable().apply {
                setColor(Color.rgb(35, 38, 43))
                setStroke(dp(EDITOR_BORDER_WIDTH_DP), Color.rgb(91, 163, 211))
                cornerRadius = dp(EDITOR_CORNER_RADIUS_DP).toFloat()
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

                override fun afterTextChanged(value: Editable?) {
                    if (suppressTextCallback) return
                    val request = activeRequest ?: return
                    val text = value?.toString().orEmpty()
                    lastForwardedText = text
                    FrontendScope.launch { request.onChange(text) }
                }
            })
            setOnEditorActionListener { _, actionId, event ->
                val isDone = actionId == EditorInfo.IME_ACTION_DONE ||
                        event?.keyCode == AndroidKeyEvent.KEYCODE_ENTER
                if (isDone) {
                    finishEditing(editor.text.toString())
                }
                isDone
            }
        }
        root.addView(
            editor,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(EDITOR_HEIGHT_DP),
                Gravity.BOTTOM,
            ).apply {
                leftMargin = dp(EDITOR_OUTER_MARGIN_DP)
                rightMargin = dp(EDITOR_OUTER_MARGIN_DP)
                bottomMargin = dp(EDITOR_OUTER_MARGIN_DP)
            },
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.setOnApplyWindowInsetsListener { _, insets ->
                val isVisible = insets.isVisible(WindowInsets.Type.ime())
                val keyboardHeight = if (isVisible) insets.getInsets(WindowInsets.Type.ime()).bottom else 0
                updateKeyboardVisibility(isVisible, keyboardHeight)
                insets
            }
        } else {
            root.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    override fun showOrUpdate(request: PlatformTextInputRequest) {
        if (suppressedOwner === request.owner) return
        val previous = activeRequest
        val isNewOwner = previous?.owner !== request.owner
        activeRequest = request
        activity.runOnUiThread {
            if (activeRequest?.owner !== request.owner) return@runOnUiThread
            editor.hint = request.hint
            editor.filters = arrayOf(InputFilter.LengthFilter(request.maxLength.coerceAtLeast(1)))
            if (isNewOwner) {
                lastForwardedText = request.text
                replaceEditorText(request.text)
                editor.visibility = View.VISIBLE
                editor.bringToFront()
                editor.requestFocus()
                editor.setSelection(editor.text.length)
                keyboardWasVisible = false
                editor.post {
                    inputMethodManager.showSoftInput(editor, InputMethodManager.SHOW_IMPLICIT)
                }
            } else if (request.text != lastForwardedText) {
                lastForwardedText = request.text
                replaceEditorText(request.text)
            }
        }
    }

    override fun hide(owner: Any) {
        if (suppressedOwner === owner) {
            suppressedOwner = null
        }
        if (activeRequest?.owner !== owner) return
        activeRequest = null
        activity.runOnUiThread { hideEditor() }
    }

    override fun dismissKeyboard() {
        activeRequest = null
        suppressedOwner = null
        activity.runOnUiThread { hideEditor() }
    }

    fun dispose() {
        activeRequest = null
        suppressedOwner = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.setOnApplyWindowInsetsListener(null)
        } else if (root.viewTreeObserver.isAlive) {
            root.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }
        hideEditor()
        root.removeView(editor)
    }

    private fun finishEditing(text: String) {
        val request = activeRequest ?: return
        suppressedOwner = request.owner
        activeRequest = null
        hideEditor()
        FrontendScope.launch {
            request.onChange(text)
            request.onEnter?.invoke(text)
            sendEscapeToKool()
        }
    }

    private fun updateKeyboardVisibility(isVisible: Boolean, keyboardHeight: Int) {
        if (isVisible) {
            keyboardWasVisible = true
            if (activeRequest == null) {
                hideUnexpectedKeyboard()
            } else {
                updateEditorBottomMargin(keyboardHeight)
            }
        } else {
            updateEditorBottomMargin(0)
            if (keyboardWasVisible && activeRequest != null) {
                finishEditing(editor.text.toString())
            }
            keyboardWasVisible = false
            unexpectedKeyboardHidePending = false
        }
    }

    private fun hideUnexpectedKeyboard() {
        if (unexpectedKeyboardHidePending) return
        unexpectedKeyboardHidePending = true
        root.postDelayed({
            if (activeRequest == null) {
                inputMethodManager.hideSoftInputFromWindow(root.windowToken, 0)
            }
            unexpectedKeyboardHidePending = false
        }, KEYBOARD_HIDE_DELAY_MS)
    }

    private fun hideEditor() {
        keyboardWasVisible = false
        editor.clearFocus()
        editor.visibility = View.GONE
        inputMethodManager.hideSoftInputFromWindow(editor.windowToken ?: root.windowToken, 0)
        koolSurface.requestFocus()
        koolSurface.postDelayed({
            if (activeRequest == null) {
                inputMethodManager.hideSoftInputFromWindow(koolSurface.windowToken, 0)
            }
        }, KEYBOARD_HIDE_DELAY_MS)
    }

    private fun replaceEditorText(text: String) {
        if (editor.text.toString() == text) return
        suppressTextCallback = true
        editor.setText(text)
        editor.setSelection(editor.text.length)
        suppressTextCallback = false
    }

    private fun updateEditorBottomMargin(keyboardHeight: Int) {
        val layoutParams = editor.layoutParams as FrameLayout.LayoutParams
        val bottomMargin = keyboardHeight + dp(EDITOR_OUTER_MARGIN_DP)
        if (layoutParams.bottomMargin != bottomMargin) {
            layoutParams.bottomMargin = bottomMargin
            editor.layoutParams = layoutParams
        }
    }

    private fun sendEscapeToKool() {
        val localKeyCode = LocalKeyCode(KeyboardInput.KEY_ESC.code)
        KeyboardInput.handleKeyEvent(
            KeyEvent(KeyboardInput.KEY_ESC, localKeyCode, KeyboardInput.KEY_EV_DOWN, 0)
        )
        KeyboardInput.handleKeyEvent(
            KeyEvent(KeyboardInput.KEY_ESC, localKeyCode, KeyboardInput.KEY_EV_UP, 0)
        )
    }

    private fun dp(value: Int): Int =
        (value * activity.resources.displayMetrics.density + 0.5f).toInt()
}

private const val EDITOR_HEIGHT_DP = 52
private const val EDITOR_OUTER_MARGIN_DP = 12
private const val EDITOR_HORIZONTAL_PADDING_DP = 14
private const val EDITOR_CORNER_RADIUS_DP = 6
private const val EDITOR_BORDER_WIDTH_DP = 1
private const val EDITOR_ELEVATION_DP = 8
private const val EDITOR_TEXT_SIZE_SP = 17f
private const val KEYBOARD_VISIBLE_HEIGHT_RATIO = 0.15f
private const val KEYBOARD_HIDE_DELAY_MS = 80L
