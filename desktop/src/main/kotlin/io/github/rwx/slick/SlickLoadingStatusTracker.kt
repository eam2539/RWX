package io.github.rwx.slick

import com.corrodinggames.rts.gameFramework.GameEngine
import io.github.rwx.session.GameLoadingStatus

internal class SlickLoadingStatusTracker {
    private val lock = Any()
    private val recentSteps = ArrayDeque<String>()

    @Volatile
    private var latestStatus: GameLoadingStatus? = null

    fun mergeWith(currentStatus: GameLoadingStatus): GameLoadingStatus {
        val engineSteps = latestStatus?.recentSteps.orEmpty()
        return if (engineSteps.isEmpty()) currentStatus else currentStatus.copy(recentSteps = engineSteps)
    }

    fun update(text: String) {
        val engine = GameEngine.getInstance()
        val loadingText = text.ifBlank { "Loading..." }
        synchronized(lock) {
            if (recentSteps.lastOrNull() != loadingText) {
                recentSteps.addLast(loadingText)
                while (recentSteps.size > HISTORY_LIMIT) {
                    recentSteps.removeFirst()
                }
            }
            latestStatus = GameLoadingStatus(
                text = loadingText,
                progress = engine?.getLoadingProgress()?.coerceIn(0.0f, 1.0f),
                recentSteps = recentSteps.toList(),
            )
        }
    }

    fun reset() {
        synchronized(lock) {
            recentSteps.clear()
            latestStatus = null
        }
    }

    private companion object {
        const val HISTORY_LIMIT = 6
    }
}
