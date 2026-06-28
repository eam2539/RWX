package com.corrodinggames.rts.gameFramework;

import java.util.ArrayList;

/* Core music factory retained for legacy callers. Playback is provided by the Kool layer later. */
public class AndroidMusicFactory extends MusicFactory {
    ArrayList playingPlayers = new ArrayList();
    boolean loaded = false;

    @Override
    public Music a(String str) {
        return new AndroidMusic(str, this);
    }

    @Override
    public MusicTrack a() {
        return new AndroidMusicTrack(this);
    }

    @Override
    public void a(MusicManager musicManager) {
        this.musicManager = musicManager;
        if (this.loaded) {
            GameEngine.log("AndroidMusicFactory already loaded");
        }
        GameEngine.log("AndroidMusicFactory - core no-op load");
        this.loaded = true;
    }

    @Override
    public void release() {
        this.playingPlayers.clear();
    }
}
