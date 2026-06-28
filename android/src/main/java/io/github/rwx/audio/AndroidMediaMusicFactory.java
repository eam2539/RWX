package io.github.rwx.audio;

import android.content.Context;
import android.media.MediaPlayer;
import com.corrodinggames.rts.gameFramework.*;

import java.util.ArrayList;

public final class AndroidMediaMusicFactory extends MusicFactory {
    final Context context;
    final ArrayList<MediaPlayer> allPlayers = new ArrayList<>();
    final ArrayList<MediaPlayer> idlePlayers = new ArrayList<>();
    final ArrayList<AndroidMediaMusicTrack> activePlayers = new ArrayList<>();
    boolean loaded = false;

    public AndroidMediaMusicFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Music a(String path) {
        return new AndroidMediaMusic(path, this);
    }

    @Override
    public MusicTrack a() {
        return new AndroidMediaMusicTrack(this);
    }

    @Override
    public void a(MusicManager musicManager) {
        this.musicManager = musicManager;
        if (this.loaded) {
            GameEngine.log("AndroidMusicFactory already loaded");
        }
        GameEngine.log("AndroidMusicFactory - load");
        this.loaded = true;
        this.allPlayers.add(new MediaPlayer());
        this.allPlayers.add(new MediaPlayer());
        this.idlePlayers.addAll(this.allPlayers);
    }

    @Override
    public void release() {
        for (AndroidMediaMusicTrack track : new ArrayList<>(this.activePlayers)) {
            track.e();
        }
        for (MediaPlayer player : this.allPlayers) {
            player.release();
        }
        this.activePlayers.clear();
        this.idlePlayers.clear();
        this.allPlayers.clear();
        this.loaded = false;
    }
}
