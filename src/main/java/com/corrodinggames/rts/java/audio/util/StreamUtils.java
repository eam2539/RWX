package com.corrodinggames.rts.java.audio.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.s */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/s.class */
public final class StreamUtils {
    public static final byte[] a = new byte[0];

    public static void a(InputStream inputStream, OutputStream outputStream) throws IOException {
        a(inputStream, outputStream, new byte[4096]);
    }

    public static void a(InputStream inputStream, OutputStream outputStream, byte[] bArr) throws IOException {
        while (true) {
            int i = inputStream.read(bArr);
            if (i != -1) {
                outputStream.write(bArr, 0, i);
            } else {
                return;
            }
        }
    }

    public static byte[] a(InputStream inputStream, int i) throws IOException {
        FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream(Math.max(0, i));
        a(inputStream, fastByteArrayOutputStream);
        return fastByteArrayOutputStream.toByteArray();
    }

    public static void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable th) {
            }
        }
    }
}
