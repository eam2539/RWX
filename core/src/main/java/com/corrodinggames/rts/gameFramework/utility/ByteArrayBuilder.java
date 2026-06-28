package com.corrodinggames.rts.gameFramework.utility;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/w.class */
public class ByteArrayBuilder extends OutputStream {
    public byte[] a;
    protected int b;

    public ByteArrayBuilder() {
        this.a = new byte[32];
    }

    public ByteArrayBuilder(int i) {
        if (i >= 0) {
            this.a = new byte[i];
            return;
        }
        throw new IllegalArgumentException("size < 0");
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
    }

    private void a(int i) {
        if (this.b + i <= this.a.length) {
            return;
        }
        byte[] bArr = new byte[(this.b + i) * 2];
        System.arraycopy(this.a, 0, bArr, 0, this.b);
        this.a = bArr;
    }

    public synchronized void a() {
        this.b = 0;
    }

    public int b() {
        return this.b;
    }

    public String toString() {
        return new String(this.a, 0, this.b);
    }

    public static void a(int i, int i2, int i3) {
        if ((i2 | i3) < 0 || i2 > i || i - i2 < i3) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override // java.io.OutputStream
    public synchronized void write(byte[] bArr, int i, int i2) {
        a(bArr.length, i, i2);
        if (i2 == 0) {
            return;
        }
        a(i2);
        System.arraycopy(bArr, i, this.a, this.b, i2);
        this.b += i2;
    }

    @Override // java.io.OutputStream
    public synchronized void write(int i) {
        if (this.b == this.a.length) {
            a(1);
        }
        byte[] bArr = this.a;
        int i2 = this.b;
        this.b = i2 + 1;
        bArr[i2] = (byte) i;
    }
}
