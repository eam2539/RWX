package com.corrodinggames.rts.java;

import org.lwjgl.opengl.Display;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c.class */
public class DisplayMessageThread extends Thread {
    final /* synthetic */ SlickGameContainer a;

    public DisplayMessageThread(SlickGameContainer slickGameContainer) {
        this.a = slickGameContainer;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (true) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
            }
            Display.processMessages();
        }
    }
}
