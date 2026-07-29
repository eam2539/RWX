package io.github.rwx

data class ExternalStorageSelection(
    val uri: String,
    val displayPath: String,
)

data class PlatformFileSelection(
    val path: String,
    val displayPath: String = path,
    val release: () -> Unit = {},
)

interface PlatformFilePickerHost {
    fun openFilePicker(
        title: String,
        allowedExtensions: Set<String>,
        allowDirectories: Boolean,
        onResult: (PlatformFileSelection?) -> Unit,
    )
    fun requestExternalStorage(onResult: (ExternalStorageSelection?) -> Unit)=Unit
}

