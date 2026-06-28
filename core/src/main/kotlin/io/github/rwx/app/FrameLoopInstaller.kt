package io.github.rwx.app

import de.fabmax.kool.KoolContext
import io.github.rwx.render.GameRenderBackend
import io.github.rwx.render.canvas.KoolCanvasFrame
import io.github.rwx.render.canvas.KoolCanvasSceneHost
import io.github.rwx.render.canvas.KoolCanvasViewport
import io.github.rwx.session.GameSession
import io.github.rwx.ui.AppScreen
import io.github.rwx.ui.CoreUiEventQueue

internal class FrameLoopInstaller(
    private val context: KoolContext,
    private val gameSession: GameSession,
    private val menuBackgroundRenderer: GameRenderBackend,
    private val screenPresenter: ScreenPresenter,
    private val warmupController: WarmupController,
    private val koolCanvasSceneHost: KoolCanvasSceneHost,
    private val currentScreen: () -> AppScreen,
    private val lastExternalFrame: () -> KoolCanvasFrame?,
    private val setLastExternalFrame: (KoolCanvasFrame) -> Unit,
    private val multiplayerLobbyController: MultiplayerLobbyController,
    private val battleRoomController: BattleRoomController,
    private val battleRoomLaunchController: BattleRoomLaunchController,
    private val resourceBrowserController: ResourceBrowserController,
    private val inGameDialogController: InGameDialogController,
    private val rwxMapController: MapController,
    private val sessionActions: SessionActions,
    private val updateController: UpdateController,
    private val battleRoomJoinController: BattleRoomJoinController,
    private val pendingStartController: PendingStartController,
    private val externalGameController: ExternalGameController,
    private val modsController: ModsController,
    private val inputController: InputController,
    private val gameReadyController: GameReadyController,
    private val refreshModWindow: () -> Unit,
    private val onBattleRoomClosed: (reason: String?, message: String?) -> Unit,
) {
    fun install() {
        CoreUiEventQueue.setOverlayRequestHandler(inGameDialogController::requestKoolOverlayForQueuedEvent)

        val coreEventDispatcher = CoreEventDispatcher(
            currentScreen = currentScreen,
            multiplayerLobbyController = multiplayerLobbyController,
            battleRoomController = battleRoomController,
            battleRoomLaunchController = battleRoomLaunchController,
            resourceBrowserController = resourceBrowserController,
            inGameDialogController = inGameDialogController,
            rwxMapController = rwxMapController,
            openInGameSettings = sessionActions::openInGameSettings,
            requestInGameSurrender = sessionActions::requestInGameSurrender,
            exitRwGameToMainMenu = sessionActions::exitRwGameToMainMenu,
            returnRwGameToBattleRoom = sessionActions::returnRwGameToBattleRoom,
            openInGameModWindow = sessionActions::openInGameModWindow,
            closeInGameModWindow = sessionActions::closeInGameModWindow,
            refreshInGameModWindow = refreshModWindow,
            onBattleRoomClosed = onBattleRoomClosed,
        )
        val frameRenderController = FrameRenderController(
            gameSession = gameSession,
            menuBackgroundRenderer = menuBackgroundRenderer,
            screenPresenter = screenPresenter,
            warmupController = warmupController,
            koolCanvasSceneHost = koolCanvasSceneHost,
            lastExternalFrame = lastExternalFrame,
            setLastExternalFrame = setLastExternalFrame,
        )
        val frameDriver = FrameDriver(
            gameSession = gameSession,
            currentScreen = currentScreen,
            canvasViewport = ::canvasViewport,
            coreEventDispatcher = coreEventDispatcher,
            updateController = updateController,
            battleRoomController = battleRoomController,
            battleRoomLaunchController = battleRoomLaunchController,
            rwxMapController = rwxMapController,
            battleRoomJoinController = battleRoomJoinController,
            pendingStartController = pendingStartController,
            externalGameController = externalGameController,
            modsController = modsController,
            inputController = inputController,
            frameRenderController = frameRenderController,
            gameReadyController = gameReadyController,
        )
        frameDriver.drive()
        context.onRender += { frameDriver.drive(isRenderLoopFrame = true) }
    }

    private fun canvasViewport(): KoolCanvasViewport {
        val windowSize = context.window.size
        return KoolCanvasViewport(windowSize.x, windowSize.y)
    }
}
