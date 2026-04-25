package com.corrodinggames.rts.gameFramework.sound;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.audio.AndroidSoundFactory;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/d.class */
public class SoundThread extends Thread {

    /* JADX INFO: renamed from: a */
    final /* synthetic */ AndroidSoundFactory soundFactory;

    public SoundThread(AndroidSoundFactory androidSoundFactory) {
        this.soundFactory = androidSoundFactory;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        while (true) {
            try {
                SoundRequest soundRequest = (SoundRequest) this.soundFactory.soundRequestQueue.take();
                soundRequest.process();
                this.soundFactory.soundPlaybackList.release(soundRequest);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
