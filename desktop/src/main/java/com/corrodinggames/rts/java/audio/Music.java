package com.corrodinggames.rts.java.audio;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/Music.class */
public interface Music {

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/Music$OnCompletionListener.class */
    public interface OnCompletionListener {
        void onCompletion(Music music);
    }

    void play();

    void pause();

    void stop();

    boolean isPlaying();

    void setLooping(boolean z);

    boolean isLooping();

    void setVolume(float f);

    float getVolume();

    void setPan(float f, float f2);

    void setPosition(float f);

    float getPosition();

    void dispose();

    void setOnCompletionListener(OnCompletionListener onCompletionListener);
}
