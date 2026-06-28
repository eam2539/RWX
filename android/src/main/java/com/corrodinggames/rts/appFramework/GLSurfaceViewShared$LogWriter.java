package com.corrodinggames.rts.appFramework;

import android.util.Log;

import java.io.Writer;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.y */
/* JADX INFO: loaded from: classes.dex */
final class GLSurfaceViewShared$LogWriter extends Writer {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private StringBuilder f271a = new StringBuilder();

    GLSurfaceViewShared$LogWriter() {
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        a();
    }

    @Override // java.io.Writer, java.io.Flushable
    public final void flush() {
        a();
    }

    @Override // java.io.Writer
    public final void write(char[] cArr, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            char c = cArr[i + i3];
            if (c == '\n') {
                a();
            } else {
                this.f271a.append(c);
            }
        }
    }

    private void a() {
        if (this.f271a.length() > 0) {
            Log.v("GLSurfaceView", this.f271a.toString());
            this.f271a.delete(0, this.f271a.length());
        }
    }
}
