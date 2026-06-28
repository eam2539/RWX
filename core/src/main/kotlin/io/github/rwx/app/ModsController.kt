package io.github.rwx.app

import io.github.rwx.i18n.I18n
import io.github.rwx.logger
import io.github.rwx.mod.ModRepository
import io.github.rwx.session.GameSession
import io.github.rwx.ui.host.DialogSceneHost
import io.github.rwx.ui.host.LoadingDialogSceneHost
import io.github.rwx.ui.host.ModsSceneHost
import io.github.rwx.ui.model.Dialog
import io.github.rwx.ui.model.DialogButton
import io.github.rwx.ui.model.DialogTextInput
import java.util.concurrent.atomic.AtomicReference

internal class ModsController(
    private val modRepository: ModRepository,
    private val gameSession: GameSession,
    private val sceneHost: ModsSceneHost,
    private val loadingDialogSceneHost: LoadingDialogSceneHost,
    private val dialogSceneHost: DialogSceneHost,
    // Invoked after mods are applied/reloaded so map lists can drop stale caches, mirroring RWPP's
    // getAllMaps(forceRefresh=true) at the end of modReload().
    private val onModsReloaded: () -> Unit = {},
) {
    private var reloadLoading = false
    private val reloadResult = AtomicReference<ModsReloadResult?>(null)

    fun refresh(statusText: String = "") {
        sceneHost.updateMods(modRepository.listMods(), statusText)
    }

    fun applyChangesAndRefresh() {
        modRepository.applyChanges()
        refresh()
    }

    fun reloadAvailableAndRefresh() {
        modRepository.reloadAvailableMods()
        refresh()
    }

    fun disableAllAndRefresh() {
        modRepository.disableAll()
        refresh()
    }

    fun toggleEnabledAndRefresh(modId: String) {
        modRepository.toggleEnabled(modId)
        refresh()
    }

    fun deleteAndRefresh(modId: String) {
        val deleted = modRepository.delete(modId)
        refresh(if (deleted) "" else "Unable to delete mod")
    }

    fun showImportDialog() {
        dialogSceneHost.show(
            Dialog(
                title = "Import Mod",
                message = "Enter a local .rwmod, .zip, .jar, .ini, or mod directory path.",
                textInput = DialogTextInput(hint = "/path/to/mod.rwmod"),
                buttons = listOf(
                    DialogButton(
                        "Import",
                        onInputPress = { path ->
                            val result = modRepository.importMod(path)
                            refresh(result.message)
                        },
                    ),
                    DialogButton(I18n.common.cancel()),
                ),
            ),
        )
    }

    fun reloadWithDialog() {
        if (reloadLoading) {
            return
        }
        reloadLoading = true
        reloadResult.set(null)
        loadingDialogSceneHost.showProgress(
            title = "Reloading Mods",
            message = "Loading custom unit data",
            progress = 0.05f,
        )
        Thread({
            val error = runCatching {
                modRepository.applyChanges()
                val handledByBackend = gameSession.requestReloadMods()
                if (handledByBackend) {
                    waitForLoadingText("Mods reloaded", timeoutMillis = 60_000L)
                } else {
                    modRepository.reloadAppliedMods()
                }
            }.exceptionOrNull()
            reloadResult.set(ModsReloadResult(error))
        }, "RWX-mods-reload").apply {
            isDaemon = true
            start()
        }
    }

    fun driveReload() {
        if (reloadLoading) {
            val status = gameSession.loadingStatus()
            loadingDialogSceneHost.updateProgress(
                message = status.text.ifBlank { "Loading custom unit data" },
                progress = status.progress ?: 0.05f,
            )
        }
        reloadResult.getAndSet(null)?.let { result ->
            if (reloadLoading) {
                finishReload(result.error)
            }
        }
    }

    private fun waitForLoadingText(expectedText: String, timeoutMillis: Long) {
        val deadline = System.currentTimeMillis() + timeoutMillis
        while (System.currentTimeMillis() < deadline) {
            val status = gameSession.loadingStatus()
            if (status.text == expectedText && (status.progress ?: 0.0f) >= 1.0f) {
                return
            }
            Thread.sleep(50L)
        }
        throw IllegalStateException("Timed out waiting for $expectedText")
    }

    private fun finishReload(error: Throwable?) {
        reloadLoading = false
        loadingDialogSceneHost.hide()
        if (error == null) {
            onModsReloaded()
            refresh("Mods reloaded")
        } else {
            logger.warn(error) { "Unable to reload mods" }
            refresh("Unable to reload mods: ${error.message ?: error.javaClass.simpleName}")
        }
    }
}

private data class ModsReloadResult(
    val error: Throwable?,
)
