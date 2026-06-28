package com.corrodinggames.rts.java.audio.util;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/c.class */
public class AudioException extends RuntimeException {
    public AudioException(String str) {
        super(str);
    }

    public AudioException(Throwable th) {
        super(th);
    }

    public AudioException(String str, Throwable th) {
        super(str, th);
    }
}
