package com.corrodinggames.rts.gameFramework.sound;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.audio.AndroidSoundFactory;
import com.corrodinggames.rts.gameFramework.audio.Sound;
import com.corrodinggames.rts.gameFramework.audio.SoundFactory;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/b.class */
public class AndroidSound extends Sound {

    /* JADX INFO: renamed from: a */
    public AndroidSoundFactory soundFactory;

    /* JADX INFO: renamed from: b */
    public int soundId;

    public String backendId;

    /* JADX INFO: renamed from: c */
    final /* synthetic */ AndroidSoundFactory parentFactory;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AndroidSound(AndroidSoundFactory androidSoundFactory, String str, SoundFactory soundFactory) {
        super(str, soundFactory);
        this.parentFactory = androidSoundFactory;
        this.soundId = -1;
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.Sound
    public void a(float f, float f2, int i, int i2, float f3) {
        SoundRequest soundRequest = (SoundRequest) this.parentFactory.soundPlaybackList.get();
        if (soundRequest == null) {
            return;
        }
        soundRequest.volume = f;
        soundRequest.pitch = f2;
        soundRequest.loop = i2;
        soundRequest.rate = f3;
        soundRequest.sound = this;
        boolean z = false;
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine != null && gameEngine.settingsEngine != null && gameEngine.settingsEngine.androidNoSoundPrioritiesDebug) {
            z = true;
        }
        if (z) {
            soundRequest.priority = 0;
        } else {
            this.parentFactory.SOUND_THREAD_SLEEP_MS--;
            if (this.parentFactory.SOUND_THREAD_SLEEP_MS <= 1) {
                this.parentFactory.SOUND_THREAD_SLEEP_MS = 1000;
            }
            soundRequest.priority = this.parentFactory.SOUND_THREAD_SLEEP_MS;
        }
        this.parentFactory.soundRequestQueue.offer(soundRequest);
    }

    /* JADX INFO: renamed from: b */
    public void playDirect(float leftVolume, float rightVolume, int priority, int loop, float rate) {
        this.parentFactory.play(this, leftVolume, rightVolume, rate);
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.Sound
    public int a() {
        return 0;
    }
}
