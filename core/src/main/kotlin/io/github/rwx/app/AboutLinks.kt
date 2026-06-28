package io.github.rwx.app

import io.github.rwx.AppMetadata
import io.github.rwx.i18n.I18n
import io.github.rwx.ui.model.Dialog
import io.github.rwx.ui.model.DialogButton
import io.github.rwx.ui.model.DialogInfoRow
import io.github.rwx.ui.component.Icon

internal const val ABOUT_LICENSE_URL: String = "https://www.gnu.org/licenses/agpl-3.0.html"
internal const val ABOUT_QQ_URL: String =
    "https://qm.qq.com/cgi-bin/qm/qr?k=kupOkNOePIjHK4sSdiJE-9YRdh3ANwum&jump_from=webapi&authKey=/fjvR18rZdV+4fe6gmVlBQkSwLZxoT0L2MYpxl8G2yph2YtqseZn2RAO556LJooZ"
internal const val ABOUT_DISCORD_URL: String = "https://discord.gg/q2amh4Gt3f"
internal const val ABOUT_GITHUB_URL: String = "https://github.com/eam2539/RWX"

internal fun AboutDialog(
    appMetadata: AppMetadata,
    onVersionPress: () -> Unit,
    onOpenLink: (String) -> Unit,
): Dialog =
    Dialog(
        title = I18n.mainmenu.about.title(),
        message = I18n.mainmenu.about.message(),
        infoRows = listOf(
            DialogInfoRow(
                icon = Icon.Version,
                label = I18n.mainmenu.about.version(),
                value = I18n.mainmenu.about.versionCheckValue(
                    appMetadata.versionName,
                    appMetadata.versionCode,
                    appMetadata.compatibleCoreVersionCode
                ),
                onPress = onVersionPress,
            ),
            DialogInfoRow(
                icon = Icon.License,
                label = I18n.mainmenu.about.license(),
                value = I18n.mainmenu.about.licenseValue(),
                onPress = { onOpenLink(ABOUT_LICENSE_URL) },
            ),
            DialogInfoRow(
                icon = Icon.Qq,
                label = I18n.mainmenu.about.qq(),
                value = "982838086",
                onPress = { onOpenLink(ABOUT_QQ_URL) },
            ),
            DialogInfoRow(
                icon = Icon.Discord,
                label = I18n.mainmenu.about.discord(),
                value = "discord.gg/q2amh4Gt3f",
                onPress = { onOpenLink(ABOUT_DISCORD_URL) },
            ),
            DialogInfoRow(
                icon = Icon.Github,
                label = I18n.mainmenu.about.github(),
                value = "github.com/eam2539/RWX",
                onPress = { onOpenLink(ABOUT_GITHUB_URL) },
            ),
        ),
        buttons = listOf(DialogButton(I18n.common.ok())),
        compactOnAndroid = true,
    )
