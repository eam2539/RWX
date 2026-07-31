package io.github.rwx.app

import io.github.rwx.render.canvas.*
import io.github.rwx.session.GameSession
import io.github.rwx.ui.AppScreen

internal class FrameRenderController(
    private val gameSession: GameSession,
    private val screenPresenter: ScreenPresenter,
    private val warmupController: WarmupController,
    private val koolCanvasSceneHost: KoolCanvasSceneHost,
    private val lastExternalFrame: () -> KoolCanvasFrame?,
    private val setLastExternalFrame: (KoolCanvasFrame) -> Unit,
) {
    fun render(
        screen: AppScreen,
        isExternalBattleRoomJoinPending: Boolean,
        canResumeForFrame: Boolean,
        canvasViewport: KoolCanvasViewport,
        deltaSeconds: Float,
    ) {
        val isRwGameVisible = screen == AppScreen.InGame
        val isRwMenuBackgroundVisible = !isExternalBattleRoomJoinPending &&
                screenPresenter.shouldShowRwMenuBackground(screen)
        val isResumeBackgroundVisible = shouldShowResumeMenuBackground(
            screen = screen,
            canResume = canResumeForFrame,
        )
        val isLastExternalFrameBackgroundVisible = screen == AppScreen.MainMenu &&
                !gameSession.rendersIntoKoolCanvas &&
                lastExternalFrame() != null
        val isStartupMenuBackgroundLoading = warmupController.isStartupMenuBackgroundLoading(screen)
        val isRwGameLoading = warmupController.isRwGameLoading(screen)
        val rwCanvasFrame = when {
            isExternalBattleRoomJoinPending -> KoolCanvasFrame(canvasViewport, emptyList())
            isRwGameVisible -> gameSession.updateFrame(canvasViewport, deltaSeconds)
            isRwMenuBackgroundVisible -> {
                gameSession.updateFrame(
                    canvasViewport,
                    deltaSeconds,
                    drainVisibleLayerBuffers = true,
                )
            }

            isResumeBackgroundVisible -> {
                resumeBackgroundFrameForSession(gameSession, canvasViewport, deltaSeconds)
            }

            isStartupMenuBackgroundLoading -> warmupController.renderStartupMenuBackgroundLoadingFrame(
                canvasViewport,
                deltaSeconds,
            )

            isRwGameLoading -> warmupController.renderRwGameLoadingFrame(
                canvasViewport,
                deltaSeconds,
            )

            else -> gameSession.currentFrame()
        }
        if (!gameSession.rendersIntoKoolCanvas && isRwGameVisible && rwCanvasFrame.commands.isNotEmpty()) {
            setLastExternalFrame(rwCanvasFrame)
        }
        warmupController.updateLoadingStatus(screen)
        val externalFrameBackground = if (isLastExternalFrameBackgroundVisible) {
            val currentFrame = gameSession.currentFrame()
            currentFrame.takeIf { it.commands.isNotEmpty() } ?: lastExternalFrame()
        } else {
            null
        }
        val shouldUseRwCanvasFrame = shouldUseRwCanvasFrameForFrame(
            isRwGameVisible = isRwGameVisible,
            isRwMenuBackgroundVisible = isRwMenuBackgroundVisible,
            isResumeBackgroundVisible = isResumeBackgroundVisible,
            isRwGameLoading = isRwGameLoading,
            isLastExternalFrameBackgroundVisible = isLastExternalFrameBackgroundVisible,
            rendersIntoKoolCanvas = gameSession.rendersIntoKoolCanvas,
        )
        koolCanvasSceneHost.render(
            if (shouldUseRwCanvasFrame) {
                val frame = externalFrameBackground ?: rwCanvasFrame
                // Canvas/OpenGL Android backends render into a native surface below Kool's transparent
                // surface. A synthetic opaque clear here hides that native frame completely;
                // only the Kool-owned backend needs the fallback black clear.
                if (gameSession.rendersIntoKoolCanvas) frame.withDefaultSurfaceClear() else frame
            } else {
                KoolCanvasFrame(canvasViewport, emptyList())
            },
        )
    }
}

internal fun resumeBackgroundFrameForSession(
    gameSession: GameSession,
    canvasViewport: KoolCanvasViewport,
    deltaSeconds: Float,
): KoolCanvasFrame =
    if (gameSession.rendersIntoKoolCanvas) {
        gameSession.currentFrame()
    } else {
        gameSession.updateFrame(
            canvasViewport,
            deltaSeconds,
            drainVisibleLayerBuffers = true,
        )
    }

internal fun KoolCanvasFrame.withDefaultSurfaceClear(): KoolCanvasFrame {
    if (commands.any { it is KoolCanvasCommand.Clear && it.renderTarget == null }) {
        return this
    }
    return copy(
        commands = listOf(
            KoolCanvasCommand.Clear(
                color = KoolCanvasColor(0xff000000.toInt()),
                blendMode = KoolCanvasBlendMode.Source,
            ),
        ) + commands,
    )
}
