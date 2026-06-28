package com.corrodinggames.rts.java.audio.util;

import java.io.ByteArrayOutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/t.class */
public class FastByteArrayOutputStream extends ByteArrayOutputStream {
    public FastByteArrayOutputStream(int i) {
        super(i);
    }

    @Override // java.io.ByteArrayOutputStream
    public synchronized byte[] toByteArray() {
        return this.count == this.buf.length ? this.buf : super.toByteArray();
    }
}
