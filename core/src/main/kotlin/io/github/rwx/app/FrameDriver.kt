package io.github.rwx.app

import com.corrodinggames.rts.gameFramework.GameEngine
import de.fabmax.kool.util.Time
import io.github.rwx.render.canvas.KoolCanvasViewport
import io.github.rwx.session.GameSession
import io.github.rwx.ui.AppScreen

internal class FrameDriver(
    private val gameSession: GameSession,
    private val currentScreen: () -> AppScreen,
    private val canvasViewport: () -> KoolCanvasViewport,
    private val coreEventDispatcher: CoreEventDispatcher,
    private val updateController: UpdateController,
    private val battleRoomController: BattleRoomController,
    private val battleRoomLaunchController: BattleRoomLaunchController,
    private val mapController: MapController,
    private val battleRoomJoinController: BattleRoomJoinController,
    private val pendingStartController: PendingStartController,
    private val externalGameController: ExternalGameController,
    private val modsController: ModsController,
    private val inputController: InputController,
    private val frameRenderController: FrameRenderController,
    private val gameReadyController: GameReadyController,
) {
    private var nextBattleRoomNetworkPollMillis: Long = 0L

    fun drive(isRenderLoopFrame: Boolean = false) {
        coreEventDispatcher.drain(isRenderLoopFrame)
        battleRoomLaunchController.driveDeferredNetworkGameStart()
        updateController.maybeRequestAutomatic(currentScreen() == AppScreen.MainMenu)
        updateController.drive()
        driveBattleRoomNetworkPolling(isRenderLoopFrame)
        driveInGameMapTransfers()
        battleRoomJoinController.drive()
        pendingStartController.drive {
            mapController.applyPendingPortalTransfers()
        }
        if (currentScreen() == AppScreen.InGame) {
            mapController.applyPendingPortalTransfers()
        }
        externalGameController.drive(
            screen = currentScreen(),
            isStartingMap = pendingStartController.isPending,
        )
        modsController.driveReload()

        val isExternalBattleRoomJoinPending = battleRoomJoinController.isPending &&
                !gameSession.rendersIntoKoolCanvas
        val canResumeForFrame = !isExternalBattleRoomJoinPending && gameSession.canResume()
        inputController.forwardPointerForFrame()
        frameRenderController.render(
            screen = currentScreen(),
            isExternalBattleRoomJoinPending = isExternalBattleRoomJoinPending,
            canResumeForFrame = canResumeForFrame,
            canvasViewport = canvasViewport(),
            deltaSeconds = Time.deltaT,
        )
        driveMusicOutsideRwFrame(currentScreen(), Time.deltaT)
        val isVisibleRwGameReady = currentScreen() == AppScreen.InGame &&
                !isExternalBattleRoomJoinPending &&
                gameSession.isReadyForDisplay()
        gameReadyController.drive(
            isRenderLoopFrame = isRenderLoopFrame,
            currentScreen = currentScreen(),
            isVisibleRwGameReady = isVisibleRwGameReady,
        )
    }

    private fun driveBattleRoomNetworkPolling(isRenderLoopFrame: Boolean) {
        val nowMillis = System.currentTimeMillis()
        val shouldPoll = shouldPollConnectedBattleRoomNetwork(
            isRenderLoopFrame = isRenderLoopFrame,
            screen = currentScreen(),
            rendersIntoKoolCanvas = gameSession.rendersIntoKoolCanvas,
            hasDraft = battleRoomController.hasDraft,
            nowMillis = nowMillis,
            nextPollMillis = nextBattleRoomNetworkPollMillis,
        )
        if (!shouldPoll) return

        nextBattleRoomNetworkPollMillis = nowMillis + BATTLE_ROOM_NETWORK_POLL_INTERVAL_MILLIS
        battleRoomController.updateFromNetwork(refreshNetworkStatus = false)
    }

    private fun driveInGameMapTransfers() {
        if (currentScreen() != AppScreen.InGame) return

        mapController.ensureP2PAssignedMapLoaded()
        mapController.drainP2PPortalTransfers()
    }

    private fun driveMusicOutsideRwFrame(screen: AppScreen, deltaSeconds: Float) {
        if (screen == AppScreen.InGame) return
        if (gameSession.isMenuBackgroundActive()) return
        val musicManager = GameEngine.getInstance()?.musicManager ?: return
        musicManager.update((deltaSeconds * 60f).coerceIn(0f, 3f))
    }
}

private const val BATTLE_ROOM_NETWORK_POLL_INTERVAL_MILLIS = 500L

internal fun shouldPollConnectedBattleRoomNetwork(
    isRenderLoopFrame: Boolean,
    screen: AppScreen,
    rendersIntoKoolCanvas: Boolean,
    hasDraft: Boolean,
    nowMillis: Long,
    nextPollMillis: Long,
): Boolean =
    isRenderLoopFrame &&
            screen == AppScreen.BattleRoom &&
            !rendersIntoKoolCanvas &&
            !hasDraft &&
            nowMillis >= nextPollMillis
