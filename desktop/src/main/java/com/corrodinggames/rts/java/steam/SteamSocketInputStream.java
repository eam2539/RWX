package com.corrodinggames.rts.java.steam;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c/l.class */
public class SteamSocketInputStream extends InputStream {
    LinkedBlockingDeque a = new LinkedBlockingDeque();
    boolean b = true;
    byte[] c = new byte[1];
    final /* synthetic */ SteamSocket d;

    public SteamSocketInputStream(SteamSocket steamSocket) {
        this.d = steamSocket;
    }

    public void a(byte[] bArr) {
        this.a.add(ByteBuffer.wrap(bArr));
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        while (read(this.c, 0, 1) <= 0) {
        }
        return this.c[0] & 255;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int iRemaining;
        if (this.d.b) {
            throw new IOException("closed");
        }
        int i3 = 0;
        int i4 = i2;
        int i5 = i;
        while (!this.d.b) {
            try {
                ByteBuffer byteBuffer = (ByteBuffer) this.a.take();
                if (byteBuffer != null) {
                    if (this.b) {
                        this.b = false;
                        GameEngine.log("First packet from:" + this.d.e);
                    }
                    if (byteBuffer.remaining() <= i4) {
                        iRemaining = byteBuffer.remaining();
                        byteBuffer.get(bArr, i5, iRemaining);
                    } else {
                        iRemaining = i4;
                        byteBuffer.get(bArr, i5, i4);
                        this.a.addFirst(byteBuffer);
                    }
                    i3 += iRemaining;
                    i4 -= iRemaining;
                    i5 += iRemaining;
                    if (i4 < 0) {
                        throw new IOException("bytesNeeded<0:" + i4);
                    }
                    if (i4 == 0) {
                        return i3;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return i3;
            }
        }
        throw new IOException("Closed");
    }
}
