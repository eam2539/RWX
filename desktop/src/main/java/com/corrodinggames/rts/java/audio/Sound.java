package com.corrodinggames.rts.java.audio;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/Sound.class */
public interface Sound {
    long play();

    long play(float f);

    long play(float f, float f2, float f3);

    long loop();

    long loop(float f);

    long loop(float f, float f2, float f3);

    void stop();

    void pause();

    void resume();

    void dispose();

    void stop(long j);

    void pause(long j);

    void resume(long j);

    void setLooping(long j, boolean z);

    void setPitch(long j, float f);

    void setVolume(long j, float f);

    void setPan(long j, float f, float f2);

    int getBytesUsed();
}
