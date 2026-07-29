package io.github.rwx.app

import io.github.rwx.i18n.I18n
import io.github.rwx.net.ResourceBrowserRepository
import io.github.rwx.ui.*
import io.github.rwx.ui.host.DialogSceneHost
import io.github.rwx.ui.host.LoadingDialogSceneHost
import io.github.rwx.ui.host.ResourceBrowserSceneHost
import io.github.rwx.ui.model.Dialog
import io.github.rwx.ui.model.DialogButton
import io.github.rwx.ui.model.ResourceBrowserItem
import io.github.rwx.ui.model.ResourceBrowserSearchResult
import io.github.rwx.ui.model.ResourceBrowserType
import java.util.concurrent.atomic.AtomicReference

internal class ResourceBrowserController(
    private val repository: ResourceBrowserRepository,
    private val sceneHost: ResourceBrowserSceneHost,
    private val loadingDialogSceneHost: LoadingDialogSceneHost,
    private val dialogSceneHost: DialogSceneHost,
    private val showUnavailableDialog: (String) -> Unit,
    private val onDownloaded: (ResourceBrowserType) -> Unit,
) {
    private var requestId = 0
    private val searchResult = AtomicReference<ResourceBrowserSearchResult?>(null)
    private val downloadResult = AtomicReference<ResourceBrowserDownloadResult?>(null)
    private val downloadProgress = AtomicReference<Float?>(null)

    fun requestSearch(append: Boolean = false) {
        val state = sceneHost.currentModel()
        val page = if (append) state.page + 1 else 1
        val nextRequestId = ++requestId
        sceneHost.setLoading(true, I18n.resourcebrowser.loading())
        launchOnIO("resource-browser-search")
        {
            val result = repository.searchBlocking(
                source = state.source,
                type = state.type,
                page = page,
                keyword = state.keyword,
            )
            searchResult.set(
                result.fold(
                    onSuccess = { items ->
                        ResourceBrowserSearchResult(
                            requestId = nextRequestId,
                            append = append,
                            page = page,
                            items = items,
                        )
                    },
                    onFailure = { error ->
                        ResourceBrowserSearchResult(
                            requestId = nextRequestId,
                            append = append,
                            page = page,
                            errorMessage = I18n.resourcebrowser.failed(error.message ?: error.javaClass.simpleName),
                        )
                    },
                )
            )
            CoreUiEventQueue.requestResourceBrowserSearchCompleted()
        }
    }

    fun selectType(type: ResourceBrowserType) {
        sceneHost.setType(type)
        requestSearch(append = false)
    }

    fun requestInitialSearchIfEmpty() {
        if (sceneHost.currentModel().items.isEmpty()) {
            requestSearch(append = false)
        }
    }

    fun handleSearchCompleted() {
        val result = searchResult.getAndSet(null) ?: return
        if (result.requestId == requestId) {
            sceneHost.applySearchResult(result)
        }
    }

    fun download(item: ResourceBrowserItem) {
        if (item.downloadUrl.isNullOrBlank()) {
            showUnavailableDialog(I18n.resourcebrowser.missingDownloadUrl())
            return
        }

        val job = launchOnIO("resource-browser-download") {
            val result = repository.download(item) { progress ->
                if (progress >= 0.0f) {
                    downloadProgress.set(progress)
                    CoreUiEventQueue.requestResourceBrowserDownloadProgress()
                }
            }
            downloadResult.set(ResourceBrowserDownloadResult(item, result.exceptionOrNull()))
            CoreUiEventQueue.requestResourceBrowserDownloadCompleted()
        }
        loadingDialogSceneHost.showProgress(
            title = I18n.resourcebrowser.downloading(),
            message = item.title,
            progress = 0.01f,
        ) {
            loadingDialogSceneHost.hide()
            job.cancel()
        }
    }

    fun handleDownloadProgress() {
        val progress = downloadProgress.getAndSet(null) ?: return
        loadingDialogSceneHost.updateProgress(I18n.resourcebrowser.downloading(), progress)
    }

    fun handleDownloadCompleted() {
        val result = downloadResult.getAndSet(null) ?: return
        loadingDialogSceneHost.hide()
        if (result.error == null) {
            onDownloaded(result.item.type)
            dialogSceneHost.show(
                Dialog(
                    title = I18n.resourcebrowser.downloadDone(),
                    message = I18n.resourcebrowser.downloadDoneMessage(),
                    buttons = listOf(DialogButton(I18n.common.ok())),
                ),
            )
        } else {
            showUnavailableDialog(
                I18n.resourcebrowser.downloadFailed(result.error.message ?: result.error.javaClass.simpleName)
            )
        }
    }
}

private data class ResourceBrowserDownloadResult(
    val item: ResourceBrowserItem,
    val error: Throwable?,
)
