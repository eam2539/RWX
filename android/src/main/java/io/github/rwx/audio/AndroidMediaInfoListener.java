package io.github.rwx.audio;

import android.media.MediaPlayer;

final class AndroidMediaInfoListener implements MediaPlayer.OnInfoListener {
    final AndroidMediaMusicTrack musicTrack;

    AndroidMediaInfoListener(AndroidMediaMusicTrack musicTrack) {
        this.musicTrack = musicTrack;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        return true;
    }
}
