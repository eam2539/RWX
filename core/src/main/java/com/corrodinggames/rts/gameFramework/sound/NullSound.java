package com.corrodinggames.rts.gameFramework.sound;

import com.corrodinggames.rts.gameFramework.audio.Sound;
import com.corrodinggames.rts.gameFramework.audio.SoundFactory;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/g.class */
public class NullSound extends Sound {
    public NullSound(String str, SoundFactory soundFactory) {
        super(str, soundFactory);
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.Sound
    public void a(float f, float f2, int i, int i2, float f3) {
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.Sound
    public int a() {
        return 0;
    }
}
