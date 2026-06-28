package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k.class */
class FileChangeThread extends Thread {
    boolean a = true;

    FileChangeThread() {
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (this.a) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }
            FileChangeEngine.b();
        }
    }
}
