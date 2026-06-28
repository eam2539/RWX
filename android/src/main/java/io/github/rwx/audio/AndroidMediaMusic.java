package io.github.rwx.audio;

import com.corrodinggames.rts.gameFramework.Music;

final class AndroidMediaMusic extends Music {
    AndroidMediaMusicFactory factory;

    AndroidMediaMusic(String path, AndroidMediaMusicFactory factory) {
        super(path, factory);
        this.factory = factory;
    }
}
