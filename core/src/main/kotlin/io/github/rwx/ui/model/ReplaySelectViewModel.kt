package io.github.rwx.ui.model

import com.corrodinggames.rts.gameFramework.Utility
import com.corrodinggames.rts.gameFramework.file.FileHelper
import io.github.rwx.i18n.I18n
import io.github.rwx.ui.AppScreen
import java.text.SimpleDateFormat
import java.util.*

data class ReplayEntry(
    val id: Int,
    val fileName: String,
    val displayName: String,
    val replayName: String,
    val modifiedAt: String,
    val sizeLabel: String,
)

object ReplayBrowser {
    fun visibleReplays(replays: List<ReplayEntry>, query: String): List<ReplayEntry> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return replays
        return replays.filter {
            it.displayName.contains(trimmedQuery, ignoreCase = true) ||
                    it.fileName.contains(trimmedQuery, ignoreCase = true)
        }
    }
}

class ReplaySelectViewModel {
    fun items(): List<ReplayEntry> {
        return FileHelper.listFiles(REPLAY_DIRECTORY)
            ?.asSequence()
            ?.filter { it.substringAfterLast('/').endsWith(".replay", ignoreCase = true) }
            ?.map { entry ->
                val replayPath = Utility.joinPath(REPLAY_DIRECTORY, entry)
                Triple(entry, replayPath, FileHelper.getLastModified(replayPath))
            }
            ?.sortedByDescending { (_, _, lastModified) -> lastModified }
            ?.mapIndexed { index, (entry, replayPath, lastModified) ->
                val fileName = entry.substringAfterLast('/')
                ReplayEntry(
                    id = index,
                    fileName = fileName,
                    displayName = displayName(fileName),
                    replayName = entry,
                    modifiedAt = modifiedAt(lastModified),
                    sizeLabel = sizeLabel(FileHelper.getFileLength(replayPath)),
                )
            }
            ?.toList()
            ?: emptyList()
    }

    companion object {
        private const val REPLAY_DIRECTORY = "/SD/rustedWarfare/replays"
        private val whitespaceRegex = Regex("""\s+""")
        private val replayExtensionRegex = Regex("""\.replay$""", RegexOption.IGNORE_CASE)
        private val dateFormat = ThreadLocal.withInitial {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        }

        fun displayName(fileName: String): String =
            fileName
                .replace(replayExtensionRegex, "")
                .replace('_', ' ')
                .replace(whitespaceRegex, " ")
                .trim()

        fun modifiedAt(lastModifiedMillis: Long): String {
            if (lastModifiedMillis <= 0L) return I18n.replay.unknownTime()
            return dateFormat.get().format(Date(lastModifiedMillis))
        }

        fun sizeLabel(bytes: Long): String {
            if (bytes <= 0L) return I18n.replay.unknownSize()
            val kib = bytes / 1024.0
            if (kib < 1024.0) return "%.1f KiB".format(Locale.US, kib)
            return "%.1f MiB".format(Locale.US, kib / 1024.0)
        }
    }
}

fun interface ReplaySelectViewModelFactory {
    fun create(): ReplaySelectViewModel
}

fun interface ReplaySelectActionHandler {
    fun onAction(action: ReplaySelectAction)
}

sealed interface ReplaySelectAction {
    data class SelectReplay(val replay: ReplayEntry) : ReplaySelectAction
    data object Back : ReplaySelectAction
}

sealed interface ReplaySelectOutcome {
    data class Navigate(val screen: AppScreen) : ReplaySelectOutcome
    data class WatchReplay(val replay: ReplayEntry) : ReplaySelectOutcome
}

object ReplaySelectNavigation {
    fun outcomeFor(action: ReplaySelectAction): ReplaySelectOutcome = when (action) {
        is ReplaySelectAction.SelectReplay -> ReplaySelectOutcome.WatchReplay(action.replay)
        ReplaySelectAction.Back -> ReplaySelectOutcome.Navigate(AppScreen.MainMenu)
    }
}
