package com.corrodinggames.rts.java.audio;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/AudioDevice.class */
public interface AudioDevice {
    boolean isMono();

    void writeSamples(short[] sArr, int i, int i2);

    void writeSamples(float[] fArr, int i, int i2);

    int getLatency();

    void dispose();

    void setVolume(float f);
}
