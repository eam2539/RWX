package com.corrodinggames.rts.appFramework;

import com.corrodinggames.rts.gameFramework.GameEngine;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/h.class */
class LoadGameRunnable implements Runnable {

    /* JADX INFO: renamed from: a */
    public String saveName;
    final /* synthetic */ InGameActivity b;

    LoadGameRunnable(InGameActivity inGameActivity) {
        this.b = inGameActivity;
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.stopGameThread();
        try {
            gameEngine.gameSaver.updateAutosave(this.saveName, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameEngine.startGameThread();
        if (this.b.progressDialog != null && this.b.progressDialog.isShowing()) {
            this.b.b(0);
        }
    }
}
