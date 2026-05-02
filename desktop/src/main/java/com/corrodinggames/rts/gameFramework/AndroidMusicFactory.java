package com.corrodinggames.rts.gameFramework;

import android.media.MediaPlayer;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.an */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/an.class */
public class AndroidMusicFactory extends MusicFactory {

    /* JADX INFO: renamed from: a */
    ArrayList mediaPlayers = new ArrayList();

    /* JADX INFO: renamed from: b */
    ArrayList availablePlayers = new ArrayList();

    /* JADX INFO: renamed from: c */
    ArrayList playingPlayers = new ArrayList();

    /* JADX INFO: renamed from: d */
    boolean loaded = false;

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    public Music a(String str) {
        return new AndroidMusic(str, this);
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    public MusicTrack a() {
        return new AndroidMusicTrack(this);
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    public void a(MusicManager musicManager) {
        this.musicManager = musicManager;
        if (this.loaded) {
            GameEngine.log("AndroidMusicFactory already loaded");
        }
        GameEngine.log("AndroidMusicFactory - load");
        this.loaded = true;
        this.mediaPlayers.add(new MediaPlayer());
        this.mediaPlayers.add(new MediaPlayer());
        this.availablePlayers.addAll(this.mediaPlayers);
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    /* JADX INFO: renamed from: b */
    public void release() {
    }
}
