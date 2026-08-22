package com.corrodinggames.rts.gameFramework.audio;

import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/i.class */
public abstract class Sound {

    /* JADX INFO: renamed from: d */
    public float volume = 1.0f;

    public String e;
    public boolean g;

    /* JADX INFO: renamed from: f */
    public boolean isLooping = false;

    public abstract void a(float f, float f2, int i, int i2, float f3);

    public abstract int a();

    public Sound(String str, SoundFactory soundFactory) {
        this.e = Utility.getFileNameWithoutExtension(str);
        if (soundFactory != null) {
            soundFactory.h.put(this.e, this);
        }
    }
}
