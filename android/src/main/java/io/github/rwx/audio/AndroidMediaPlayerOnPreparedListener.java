package io.github.rwx.audio;

import android.media.MediaPlayer;

final class AndroidMediaPlayerOnPreparedListener implements MediaPlayer.OnPreparedListener {
    final AndroidMediaMusicTrack musicTrack;

    AndroidMediaPlayerOnPreparedListener(AndroidMediaMusicTrack musicTrack) {
        this.musicTrack = musicTrack;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
