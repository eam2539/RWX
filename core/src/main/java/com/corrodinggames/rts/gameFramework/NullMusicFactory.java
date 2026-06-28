package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.av */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/av.class */
public class NullMusicFactory extends MusicFactory {
    boolean a = false;

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    public Music a(String str) {
        return new NullMusic(str, this);
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    public MusicTrack a() {
        return new NullMusicTrack(this);
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    public void a(MusicManager musicManager) {
        GameEngine.log("Null musicFactory - load");
        this.musicManager = musicManager;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    /* JADX INFO: renamed from: b */
    public void release() {
    }
}
