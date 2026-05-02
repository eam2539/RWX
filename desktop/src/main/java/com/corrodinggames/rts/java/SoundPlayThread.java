package com.corrodinggames.rts.java;

import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.r */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/r.class */
public class SoundPlayThread extends Thread {
    final /* synthetic */ OpenALSoundFactory a;

    public SoundPlayThread(OpenALSoundFactory openALSoundFactory) {
        this.a = openALSoundFactory;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        while (true) {
            try {
                SoundPlayRequest soundPlayRequest = (SoundPlayRequest) this.a.b.take();
                soundPlayRequest.a();
                this.a.c.release(soundPlayRequest);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
