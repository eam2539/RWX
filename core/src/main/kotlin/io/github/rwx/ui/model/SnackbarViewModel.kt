package io.github.rwx.ui.model

import de.fabmax.kool.modules.ui2.mutableStateOf

enum class SnackbarDuration(val timeoutMillis: Long?) {
    Short(4_000L),
    Long(10_000L),
    Indefinite(null),
}

enum class SnackbarResult {
    ActionPerformed,
    Dismissed,
}

data class SnackbarVisuals(
    val message: String,
    val actionLabel: String? = null,
    val withDismissAction: Boolean = false,
    val duration: SnackbarDuration = if (actionLabel == null) {
        SnackbarDuration.Short
    } else {
        SnackbarDuration.Indefinite
    },
)

data class SnackbarData(
    val id: Long,
    val visuals: SnackbarVisuals,
)

@JvmInline
value class SnackbarHandle(val id: Long)

class SnackbarHostState(
    private val clockMillis: () -> Long = System::currentTimeMillis,
) {
    val currentSnackbarData = mutableStateOf<SnackbarData?>(null)

    private val pending = ArrayDeque<SnackbarRequest>()
    private var currentRequest: SnackbarRequest? = null
    private var shownAtMillis: Long = 0L
    private var nextId: Long = 1L

    val isVisible: Boolean
        get() = currentRequest != null

    val hasQueuedSnackbars: Boolean
        get() = pending.isNotEmpty()

    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = if (actionLabel == null) {
            SnackbarDuration.Short
        } else {
            SnackbarDuration.Indefinite
        },
        onResult: ((SnackbarResult) -> Unit)? = null,
    ): SnackbarHandle = showSnackbar(
        visuals = SnackbarVisuals(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration,
        ),
        onResult = onResult,
    )

    fun showSnackbar(
        visuals: SnackbarVisuals,
        onResult: ((SnackbarResult) -> Unit)? = null,
    ): SnackbarHandle {
        val id = nextId++
        pending += SnackbarRequest(
            data = SnackbarData(id, visuals),
            onResult = onResult,
        )
        showNextIfIdle()
        return SnackbarHandle(id)
    }

    fun performAction(handle: SnackbarHandle): Boolean = performAction(handle.id)

    fun performAction(data: SnackbarData): Boolean = performAction(data.id)

    fun performAction(id: Long): Boolean {
        val request = currentRequest ?: return false
        if (request.data.id != id || request.data.visuals.actionLabel == null) {
            return false
        }
        finishCurrent(SnackbarResult.ActionPerformed)
        return true
    }

    fun dismiss(handle: SnackbarHandle): Boolean = dismiss(handle.id)

    fun dismiss(data: SnackbarData): Boolean = dismiss(data.id)

    fun dismiss(id: Long): Boolean {
        val request = currentRequest ?: return false
        if (request.data.id != id) {
            return false
        }
        finishCurrent(SnackbarResult.Dismissed)
        return true
    }

    fun dismissCurrent(): Boolean {
        if (currentRequest == null) {
            return false
        }
        finishCurrent(SnackbarResult.Dismissed)
        return true
    }

    fun tick(nowMillis: Long = clockMillis()): Boolean {
        val request = currentRequest ?: return false
        val timeoutMillis = request.data.visuals.duration.timeoutMillis ?: return false
        if (nowMillis - shownAtMillis < timeoutMillis) {
            return false
        }
        finishCurrent(SnackbarResult.Dismissed, nowMillis)
        return true
    }

    fun clear() {
        val current = currentRequest
        val queued = pending.toList()
        currentRequest = null
        currentSnackbarData.value = null
        pending.clear()
        current?.onResult?.invoke(SnackbarResult.Dismissed)
        queued.forEach { it.onResult?.invoke(SnackbarResult.Dismissed) }
    }

    private fun finishCurrent(result: SnackbarResult, nowMillis: Long = clockMillis()) {
        val request = currentRequest ?: return
        currentRequest = null
        currentSnackbarData.value = null
        request.onResult?.invoke(result)
        showNextIfIdle(nowMillis)
    }

    private fun showNextIfIdle(nowMillis: Long = clockMillis()) {
        if (currentRequest != null || pending.isEmpty()) {
            return
        }
        val request = pending.removeFirst()
        currentRequest = request
        shownAtMillis = nowMillis
        currentSnackbarData.value = request.data
    }

    private data class SnackbarRequest(
        val data: SnackbarData,
        val onResult: ((SnackbarResult) -> Unit)?,
    )
}
