package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.aq */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/aq.class */
public abstract class MusicFactory {

    /* JADX INFO: renamed from: e */
    protected MusicManager musicManager;

    public abstract Music a(String str);

    public abstract MusicTrack a();

    public abstract void a(MusicManager musicManager);

    /* JADX INFO: renamed from: b */
    public abstract void release();

    /* JADX INFO: renamed from: a */
    public void createMusic(int i) {
    }

    /* JADX INFO: renamed from: a */
    public void createMusicTrack(float f) {
    }

    /* JADX INFO: renamed from: c */
    public boolean isMusicPlaying() {
        return false;
    }

    /* JADX INFO: renamed from: d */
    public boolean isThreaded() {
        return true;
    }

    /* JADX INFO: renamed from: e */
    public int isMusicSupported() {
        return 0;
    }
}
