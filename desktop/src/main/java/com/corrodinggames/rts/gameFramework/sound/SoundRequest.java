package com.corrodinggames.rts.gameFramework.sound;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/c.class */
public class SoundRequest {

    /* JADX INFO: renamed from: a */
    AndroidSound sound;

    /* JADX INFO: renamed from: b */
    float volume;

    /* JADX INFO: renamed from: c */
    float pitch;

    /* JADX INFO: renamed from: d */
    int priority;

    /* JADX INFO: renamed from: e */
    int loop;

    /* JADX INFO: renamed from: f */
    float rate;

    /* JADX INFO: renamed from: a */
    public void process() {
        this.sound.playDirect(this.volume, this.pitch, this.priority, this.loop, this.rate);
    }
}
