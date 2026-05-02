package com.corrodinggames.rts.java.audio;

import com.corrodinggames.rts.java.audio.util.AudioFile;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/Audio.class */
public interface Audio {
    AudioDevice newAudioDevice(int i, boolean z);

    AudioRecorder newAudioRecorder(int i, boolean z);

    Sound newSound(AudioFile audioFile);

    Music newMusic(AudioFile audioFile);
}
