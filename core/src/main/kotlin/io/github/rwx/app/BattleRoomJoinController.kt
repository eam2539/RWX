package io.github.rwx.app

import io.github.rwx.i18n.I18n
import io.github.rwx.logger
import io.github.rwx.session.BattleRoomSnapshot
import io.github.rwx.session.GameSession
import io.github.rwx.ui.host.LoadingDialogSceneHost
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.milliseconds

internal class BattleRoomJoinController(
    private val gameSession: GameSession,
    private val loadingDialogSceneHost: LoadingDialogSceneHost,
    private val onStarted: () -> Unit,
    private val onConnected: (BattleRoomSnapshot?) -> Unit,
    private val onFailed: (String) -> Unit,
) {
    private var pendingJoin: PendingBattleRoomJoin? = null
    private val requestError = AtomicReference<String?>(null)
    private val probe = AtomicReference<PendingBattleRoomJoinProbe?>(null)
    private val token = AtomicReference<String?>(null)

    val isPending: Boolean
        get() = pendingJoin != null

    fun start(
        address: String,
        roomLabel: String,
        failurePrefix: String,
        requestJoin: () -> Unit,
    ) {
        val trimmedAddress = address.trim()
        if (trimmedAddress.isBlank()) return
        runCatching {
            val joinToken = "$trimmedAddress:${System.nanoTime()}"
            onStarted()
            requestError.set(null)
            probe.set(PendingBattleRoomJoinProbe(isJoinInProgress = true))
            token.set(joinToken)
            pendingJoin = PendingBattleRoomJoin(
                token = joinToken,
                address = trimmedAddress,
                roomLabel = roomLabel,
                failurePrefix = failurePrefix,
                startedAtNanos = System.nanoTime(),
            )
            val job = launchOnIO("battleroom-join") {
                monitor(
                    token = joinToken,
                    requestJoin = requestJoin,
                    failurePrefix = failurePrefix,
                )
            }
            loadingDialogSceneHost.showCircular(
                title = I18n.multiplayer.joiningRoom(),
                message = I18n.multiplayer.connectingTo(roomLabel),
            ) {
                loadingDialogSceneHost.hide()
                job.cancel()
            }
        }.onFailure { error ->
            clearPending()
            loadingDialogSceneHost.hide()
            logger.warn(error) { "Battle room join failed" }
            onFailed("$failurePrefix: ${error.message ?: error.javaClass.simpleName}")
        }
    }

    fun drive(nowNanos: Long = System.nanoTime()) {
        val pending = pendingJoin ?: return
        val latestProbe = probe.get()
        val joinError = requestError.get()
            ?: latestProbe?.errorMessage
        when (
            battleRoomJoinPollResult(
                startedAtNanos = pending.startedAtNanos,
                nowNanos = nowNanos,
                hasBattleRoomSnapshot = latestProbe?.hasJoinedSnapshot == true,
                isJoinInProgress = latestProbe?.isJoinInProgress ?: true,
                errorMessage = joinError,
            )
        ) {
            BattleRoomJoinPollResult.Connecting -> Unit
            BattleRoomJoinPollResult.Connected -> {
                clearPending()
                loadingDialogSceneHost.hide()
                onConnected(latestProbe?.snapshot)
            }

            BattleRoomJoinPollResult.Failed -> {
                fail("${pending.failurePrefix}: ${joinError ?: "connection failed"}")
            }

            BattleRoomJoinPollResult.TimedOut -> {
                fail("${pending.failurePrefix}: connection timed out")
            }
        }
    }

    fun handleBattleRoomClosed() {
        if (pendingJoin == null) return
        clearPending()
        gameSession.cancelBattleRoomJoin()
        loadingDialogSceneHost.hide()
    }

    private suspend fun monitor(
        token: String,
        requestJoin: () -> Unit,
        failurePrefix: String,
    ) {
        runCatching {
            gameSession.cancelBattleRoomJoin()
            requestJoin()
        }.onFailure { error ->
            logger.warn(error) { "Battle room join request failed" }
            requestError.set(
                "$failurePrefix: ${error.message ?: error.javaClass.simpleName}"
            )
            return
        }
        while (this.token.get() == token) {
            val snapshot = gameSession.currentBattleRoom()
            val isJoinInProgress = gameSession.isJoiningBattleRoom
            val hasJoinedSnapshot = snapshot != null &&
                    snapshot.isReadyForJoinedRoom()
            val errorMessage = gameSession.latestBattleRoomJoinError
            probe.set(
                PendingBattleRoomJoinProbe(
                    snapshot = snapshot,
                    hasJoinedSnapshot = hasJoinedSnapshot,
                    isJoinInProgress = isJoinInProgress,
                    errorMessage = errorMessage,
                )
            )
            if (!errorMessage.isNullOrBlank() || (hasJoinedSnapshot && !isJoinInProgress)) {
                return
            }
            delay(BATTLE_ROOM_JOIN_POLL_INTERVAL_MS.milliseconds)
        }
    }

    private fun fail(message: String) {
        clearPending()
        gameSession.cancelBattleRoomJoin()
        loadingDialogSceneHost.hide()
        onFailed(message)
    }

    private fun clearPending() {
        pendingJoin = null
        requestError.set(null)
        probe.set(null)
        token.set(null)
    }
}

private data class PendingBattleRoomJoin(
    val token: String,
    val address: String,
    val roomLabel: String,
    val failurePrefix: String,
    val startedAtNanos: Long,
)

private data class PendingBattleRoomJoinProbe(
    val snapshot: BattleRoomSnapshot? = null,
    val hasJoinedSnapshot: Boolean = false,
    val isJoinInProgress: Boolean = false,
    val errorMessage: String? = null,
)
