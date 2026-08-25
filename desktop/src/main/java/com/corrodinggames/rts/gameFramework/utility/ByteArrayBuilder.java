package com.corrodinggames.rts.gameFramework.utility;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/w.class */
public class ByteArrayBuilder extends OutputStream {

    /* JADX INFO: renamed from: a */
    public byte[] buffer;

    /* JADX INFO: renamed from: b */
    protected int size;

    public ByteArrayBuilder() {
        this.buffer = new byte[32];
    }

    public ByteArrayBuilder(int i) {
        if (i >= 0) {
            this.buffer = new byte[i];
            return;
        }
        throw new IllegalArgumentException("size < 0");
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
    }

    private void a(int i) {
        if (this.size + i <= this.buffer.length) {
            return;
        }
        byte[] bArr = new byte[(this.size + i) * 2];
        System.arraycopy(this.buffer, 0, bArr, 0, this.size);
        this.buffer = bArr;
    }

    public synchronized void a() {
        this.size = 0;
    }

    public int b() {
        return this.size;
    }

    public String toString() {
        return new String(this.buffer, 0, this.size);
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
        System.arraycopy(bArr, i, this.buffer, this.size, i2);
        this.size += i2;
    }

    @Override // java.io.OutputStream
    public synchronized void write(int i) {
        if (this.size == this.buffer.length) {
            a(1);
        }
        byte[] bArr = this.buffer;
        int i2 = this.size;
        this.size = i2 + 1;
        bArr[i2] = (byte) i;
    }
}
