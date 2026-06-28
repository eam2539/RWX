package com.corrodinggames.rts.gameFramework;

/* Core music track retained for legacy callers. */
public class AndroidMusicTrack extends MusicTrack {
    AndroidMusic music;
    AndroidMusicFactory factory;
    boolean playing;
    float volume;

    public AndroidMusicTrack(AndroidMusicFactory androidMusicFactory) {
        this.factory = androidMusicFactory;
        androidMusicFactory.playingPlayers.add(this);
    }

    @Override
    public void a(Music music) {
        this.music = (AndroidMusic) music;
    }

    @Override
    public void a(boolean z) {
        this.playing = true;
    }

    @Override
    public void a() {
        this.playing = false;
    }

    @Override
    public void b() {
        this.playing = true;
    }

    @Override
    public boolean c() {
        return this.playing;
    }

    @Override
    public void d() {
        this.playing = false;
    }

    @Override
    public void e() {
        this.playing = false;
        this.factory.playingPlayers.remove(this);
    }

    @Override
    public void a(float f) {
        this.volume = f;
    }
}
