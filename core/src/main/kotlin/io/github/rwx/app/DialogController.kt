package io.github.rwx.app

import io.github.rwx.AppMetadata
import io.github.rwx.PlatformBridge
import io.github.rwx.i18n.I18n
import io.github.rwx.ui.Dialog
import io.github.rwx.ui.DialogButton
import io.github.rwx.ui.DialogSceneHost

internal class DialogController(
    private val platformBridge: PlatformBridge?,
    private val appMetadata: AppMetadata,
    private val dialogSceneHost: DialogSceneHost,
    private val requestManualUpdateCheck: () -> Unit,
) {
    fun showUnavailable(message: String) {
        dialogSceneHost.show(
            Dialog(
                title = I18n.common.actionUnavailable(),
                message = message,
                buttons = listOf(DialogButton(I18n.common.ok())),
            ),
        )
    }

    fun openLink(url: String) {
        if (platformBridge?.openUrl(url) != true) {
            showUnavailable(I18n.mainmenu.about.openLinkFailed(url))
        }
    }

    fun showAboutDialog() {
        dialogSceneHost.show(
            AboutDialog(
                appMetadata = appMetadata,
                onVersionPress = {
                    dialogSceneHost.hide()
                    requestManualUpdateCheck()
                },
                onOpenLink = ::openLink,
            ),
        )
    }

}
