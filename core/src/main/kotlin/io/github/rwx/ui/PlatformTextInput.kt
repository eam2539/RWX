package io.github.rwx.ui

import de.fabmax.kool.modules.ui2.TextField
import de.fabmax.kool.modules.ui2.TextFieldScope
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.remember

data class PlatformTextInputRequest(
    val owner: Any,
    val text: String,
    val hint: String,
    val maxLength: Int,
    val onChange: (String) -> Unit,
    val onEnter: ((String) -> Unit)?,
)

interface PlatformTextInputController {
    fun showOrUpdate(request: PlatformTextInputRequest)
    fun hide(owner: Any)
    fun dismissKeyboard() = Unit
}

object PlatformTextInputBridge {
    @Volatile
    private var controller: PlatformTextInputController? = null

    fun install(controller: PlatformTextInputController) {
        this.controller = controller
    }

    fun uninstall(controller: PlatformTextInputController) {
        if (this.controller === controller) {
            this.controller = null
        }
    }

    internal fun showOrUpdate(request: PlatformTextInputRequest): Boolean {
        val activeController = controller ?: return false
        activeController.showOrUpdate(request)
        return true
    }

    internal fun hide(owner: Any) {
        controller?.hide(owner)
    }

    internal fun dismissKeyboard() {
        controller?.dismissKeyboard()
    }
}

/**
 *  provide a native editor for the active field.
 */
fun UiScope.RwxTextField(
    text: String = "",
    scopeName: String? = null,
    block: TextFieldScope.() -> Unit,
): TextFieldScope {
    val owner = remember(Any())
    val wasFocused = remember(false)
    val textField = TextField(text, scopeName, block)
    val isFocused = textField.isFocused.use()

    if (isFocused) {
        val modifier = textField.modifier
        PlatformTextInputBridge.showOrUpdate(
            PlatformTextInputRequest(
                owner = owner.value,
                text = text,
                hint = modifier.hint,
                maxLength = modifier.maxLength,
                onChange = { modifier.onChange?.invoke(it) },
                onEnter = modifier.onEnterPressed,
            )
        )
    } else if (wasFocused.value) {
        PlatformTextInputBridge.hide(owner.value)
    }
    wasFocused.value = isFocused
    return textField
}
