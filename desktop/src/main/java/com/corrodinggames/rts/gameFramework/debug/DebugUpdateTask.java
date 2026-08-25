package com.corrodinggames.rts.gameFramework.debug;

import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.c.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/c/c.class */
class DebugUpdateTask implements Runnable {
    final /* synthetic */ DebugServer a;

    DebugUpdateTask(DebugServer debugServer) {
        this.a = debugServer;
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.a.f) {
            this.a.f = false;
            return;
        }
        if (DebugServer.c) {
            if (gameEngine.tileMap == null) {
                return;
            }
            gameEngine.musicManager.quickFade = true;
            if (!gameEngine.musicManager.isFading()) {
                DebugServer.updateTimer += 1.0f;
            }
            if (DebugServer.updateTimer > 5.0f) {
                DebugServer.updateTimer = 0.0f;
                System.gc();
                System.gc();
                gameEngine.musicManager.skipToNextTrack();
            }
        }
        if (DebugServer.d && gameEngine.tileMap != null) {
            gameEngine.tileMap.invalidateAllLayerCells();
        }
    }
}
