package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.au */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/au.class */
class MusicUpdateThread extends Thread {

    /* JADX INFO: renamed from: a */
    final /* synthetic */ MusicManager musicManager;

    MusicUpdateThread(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        float f;
        while (true) {
            synchronized (this.musicManager.updateLock) {
                this.musicManager.updateCoreRunning = true;
                if (!this.musicManager.updateRunning) {
                    try {
                        this.musicManager.updateLock.wait(MusicManager.musicFactory.isMusicSupported());
                    } catch (InterruptedException e) {
                    }
                }
                this.musicManager.updateRunning = false;
                f = this.musicManager.delta;
            }
            synchronized (this.musicManager.pauseLock) {
                if (!this.musicManager.updateMusic(f)) {
                    return;
                }
            }
        }
    }
}
