package io.github.rwx

data class ExternalStorageSelection(
    val uri: String,
    val displayPath: String,
)

interface PlatformStoragePickerController {
    fun requestExternalStorage(onResult: (ExternalStorageSelection?) -> Unit)
}

object PlatformStoragePickerBridge {
    @Volatile
    private var controller: PlatformStoragePickerController? = null

    fun install(controller: PlatformStoragePickerController) {
        this.controller = controller
    }

    fun uninstall(controller: PlatformStoragePickerController) {
        if (this.controller === controller) {
            this.controller = null
        }
    }

    fun requestExternalStorage(onResult: (ExternalStorageSelection?) -> Unit): Boolean {
        val activeController = controller ?: return false
        activeController.requestExternalStorage(onResult)
        return true
    }
}
