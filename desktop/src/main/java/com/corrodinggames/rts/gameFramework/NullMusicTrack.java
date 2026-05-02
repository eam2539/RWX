package com.corrodinggames.rts.gameFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ax */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ax.class */
public class NullMusicTrack extends MusicTrack {
    NullMusic a;
    NullMusicFactory b;

    public NullMusicTrack(NullMusicFactory nullMusicFactory) {
        this.b = nullMusicFactory;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(Music music) {
        this.a = (NullMusic) music;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(boolean z) {
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a() {
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void b() {
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void d() {
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void e() {
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public boolean c() {
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(float f) {
    }
}
