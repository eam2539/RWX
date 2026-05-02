package com.corrodinggames.rts.java;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.Sound;
import com.corrodinggames.rts.gameFramework.audio.SoundFactory;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.q */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/q.class */
public class OpenALSound extends Sound {
    com.corrodinggames.rts.java.audio.Sound a;
    final /* synthetic */ OpenALSoundFactory b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public OpenALSound(OpenALSoundFactory openALSoundFactory, String str, SoundFactory soundFactory) {
        super(str, soundFactory);
        this.b = openALSoundFactory;
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.Sound
    public void a(float f, float f2, int i, int i2, float f3) {
        SoundPlayRequest soundPlayRequest = (SoundPlayRequest) this.b.c.get();
        if (soundPlayRequest == null) {
            return;
        }
        soundPlayRequest.b = f;
        soundPlayRequest.c = f2;
        soundPlayRequest.d = i;
        soundPlayRequest.e = i2;
        soundPlayRequest.f = f3;
        soundPlayRequest.a = this;
        this.b.b.offer(soundPlayRequest);
    }

    public void b(float f, float f2, int i, int i2, float f3) {
        if (this.a == null) {
            GameEngine.log("Sound not loaded");
            return;
        }
        synchronized (this.b.b()) {
            this.a.play(Utility.max(f, f2), f3, 0.0f);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.Sound
    public int a() {
        return this.a.getBytesUsed();
    }
}
