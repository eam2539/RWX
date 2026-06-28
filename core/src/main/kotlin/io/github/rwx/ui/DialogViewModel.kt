package io.github.rwx.ui

import io.github.rwx.i18n.I18n


data class DialogButton(
    val label: String,
    val onPress: (() -> Unit)? = null,
    val onInputPress: ((String) -> Unit)? = null,
    val onFormPress: ((Map<String, String>) -> Unit)? = null,
)


data class Dialog(
    val title: String,
    val message: String,
    val buttons: List<DialogButton> = listOf(DialogButton(I18n.common.ok())),
    val infoRows: List<DialogInfoRow> = emptyList(),
    val listItems: List<DialogListItem> = emptyList(),
    val textInput: DialogTextInput? = null,
    val form: DialogForm? = null,
    val scrollableMessage: Boolean = false,
    val scrollableForm: Boolean = false,
    val compactOnAndroid: Boolean = true,
)

data class DialogListItem(
    val text: String,
    val colorIndex: Int = -1,
)

data class DialogInfoRow(
    val icon: Icon,
    val label: String,
    val value: String,
    val emphasis: Boolean = false,
    val onPress: (() -> Unit)? = null,
)

data class DialogTextInput(
    val initialText: String = "",
    val hint: String = "",
)

data class DialogForm(
    val fields: List<DialogFormField>,
)

sealed interface DialogFormField {
    val id: String
    val label: String

    data class Choice(
        override val id: String,
        override val label: String,
        val options: List<DialogFormOption>,
        val selectedIndex: Int = 0,
    ) : DialogFormField

    data class Toggle(
        override val id: String,
        override val label: String,
        val checked: Boolean,
    ) : DialogFormField

    data class Text(
        override val id: String,
        override val label: String,
        val initialText: String,
        val hint: String = "",
    ) : DialogFormField
}

data class DialogFormOption(
    val label: String,
    val value: String,
) {
    override fun toString(): String = label
}
