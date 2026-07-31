package io.github.rwx.app

import io.github.rwx.PlatformBridge
import io.github.rwx.PlatformFileSelection
import io.github.rwx.i18n.I18n
import io.github.rwx.logger
import io.github.rwx.mod.ModRepository
import io.github.rwx.session.GameSession
import io.github.rwx.ui.component.Icon
import io.github.rwx.ui.host.DialogSceneHost
import io.github.rwx.ui.host.LoadingDialogSceneHost
import io.github.rwx.ui.host.ModsSceneHost
import io.github.rwx.ui.model.Dialog
import io.github.rwx.ui.model.DialogButton
import io.github.rwx.ui.model.DialogTextInput
import org.koin.mp.KoinPlatform.getKoin
import java.util.concurrent.atomic.AtomicReference

internal class ModsController(
    private val modRepository: ModRepository,
    private val gameSession: GameSession,
    private val sceneHost: ModsSceneHost,
    private val loadingDialogSceneHost: LoadingDialogSceneHost,
    private val dialogSceneHost: DialogSceneHost,
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
        var selectedFile: PlatformFileSelection? = null
        dialogSceneHost.show(
            Dialog(
                title = "Import Mod",
                message = "Enter a mod, asset key, author trust certificate, license, or revocation list path.",
                textInput = DialogTextInput(
                    hint = "/path/to/mod.rwmod",
                    trailingIcon = Icon.Import,
                    trailingIconTooltip = "Choose file",
                    onTrailingIconPress = { setInputValue ->
                        val host=getKoin().get<PlatformBridge>().filePickerHost?:return@DialogTextInput
                        host.openFilePicker(
                            title = "Choose a mod file or directory",
                            allowedExtensions = setOf(
                                "rwmod", "zip", "jar", "ini", "rwxkey", "rwxpub", "rwxlicense", "rwxcrl"
                            ),
                            allowDirectories = true
                        ) { selection ->
                            if (selection != null) {
                                selectedFile?.release()
                                selectedFile = selection
                                setInputValue(selection.displayPath)
                            }
                        }
                    },
                ),
                buttons = listOf(
                    DialogButton(
                        "Import",
                        onInputPress = { inputPath ->
                            val path = selectedFile
                                ?.takeIf { inputPath == it.displayPath }
                                ?.path
                                ?: inputPath
                            try {
                                val result = modRepository.importMod(path)
                                refresh(result.message)
                            } finally {
                                selectedFile?.release()
                                selectedFile = null
                            }
                        },
                    ),
                    DialogButton(
                        I18n.common.cancel(),
                        onPress = {
                            selectedFile?.release()
                            selectedFile = null
                        },
                    ),
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

        val job = launchOnIO("mods-reload")
        {
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
        }
        loadingDialogSceneHost.showProgress(
            title = "Reloading Mods",
            message = "Loading custom unit data",
            progress = 0.05f,
        ) {
            loadingDialogSceneHost.hide()
            job.cancel()
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
