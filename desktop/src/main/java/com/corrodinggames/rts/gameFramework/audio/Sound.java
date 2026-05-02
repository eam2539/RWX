package com.corrodinggames.rts.gameFramework.audio;

import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/i.class */
public abstract class Sound {
    public String e;
    public boolean g;
    public float d = 1.0f;
    public boolean f = false;

    public abstract void a(float f, float f2, int i, int i2, float f3);

    public abstract int a();

    public Sound(String str, SoundFactory soundFactory) {
        this.e = Utility.getFileNameFromPath(str);
        if (soundFactory != null) {
            soundFactory.h.put(this.e, this);
        }
    }
}
