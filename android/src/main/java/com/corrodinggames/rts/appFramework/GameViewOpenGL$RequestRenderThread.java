package com.corrodinggames.rts.appFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.af */
/* JADX INFO: loaded from: classes.dex */
final class GameViewOpenGL$RequestRenderThread extends Thread {
    @Override // java.lang.Thread, java.lang.Runnable
    public final void run() {
        while (true) {
            synchronized (GameViewOpenGL.renderManagerLock) {
                if (!GameViewOpenGL.requestRenderQueued) {
                    try {
                        GameViewOpenGL.renderManagerLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                GameViewOpenGL.requestRenderQueued = false;
            }
            GameViewOpenGL.lastHeldSurfaceView.requestRender();
        }
    }
}
