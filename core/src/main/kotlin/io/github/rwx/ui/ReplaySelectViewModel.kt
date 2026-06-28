package io.github.rwx.ui

import io.github.rwx.PlatformStorage
import io.github.rwx.i18n.I18n
import java.io.File
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

class ReplaySelectViewModel(
    private val storage: PlatformStorage,
) {
    fun items(): List<ReplayEntry> {
        val replaysRoot = storage.replaysDir.file
        return replaysRoot
            .takeIf(File::isDirectory)
            ?.walkTopDown()
            ?.filter { it.isFile && it.extension.equals("replay", ignoreCase = true) }
            ?.sortedByDescending(File::lastModified)
            ?.mapIndexed { index, file -> replayEntry(index, replaysRoot, file) }
            ?.toList()
            ?: emptyList()
    }

    private fun replayEntry(index: Int, root: File, file: File): ReplayEntry {
        val replayName = file.relativeTo(root).invariantSeparatorsPath
        return ReplayEntry(
            id = index,
            fileName = file.name,
            displayName = displayName(file.name),
            replayName = replayName,
            modifiedAt = modifiedAt(file.lastModified()),
            sizeLabel = sizeLabel(file.length()),
        )
    }

    companion object {
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
