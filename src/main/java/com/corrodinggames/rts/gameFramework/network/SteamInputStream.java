package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/i.class */
public class SteamInputStream extends InputStream {

    /* JADX INFO: renamed from: a */
    LinkedBlockingDeque incomingQueue = new LinkedBlockingDeque();

    /* JADX INFO: renamed from: b */
    boolean firstPacketLogged = true;

    /* JADX INFO: renamed from: c */
    byte[] singleByteBuffer = new byte[1];

    /* JADX INFO: renamed from: d */
    final /* synthetic */ SteamSocket steamSocket;

    public SteamInputStream(SteamSocket steamSocket) {
        this.steamSocket = steamSocket;
    }

    /* JADX INFO: renamed from: a */
    public void enqueuePacket(byte[] bArr) {
        this.incomingQueue.add(ByteBuffer.wrap(bArr));
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        while (read(this.singleByteBuffer, 0, 1) <= 0) {
        }
        return this.singleByteBuffer[0] & 255;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int iRemaining;
        if (this.steamSocket.closed) {
            throw new IOException("closed");
        }
        int i3 = 0;
        int i4 = i2;
        int i5 = i;
        while (!this.steamSocket.closed) {
            try {
                ByteBuffer byteBuffer = (ByteBuffer) this.incomingQueue.take();
                if (byteBuffer != null) {
                    if (this.firstPacketLogged) {
                        this.firstPacketLogged = false;
                        GameEngine.isInSpace("First packet from forwarded:" + this.steamSocket.steamChannelId);
                    }
                    if (byteBuffer.remaining() <= i4) {
                        iRemaining = byteBuffer.remaining();
                        byteBuffer.get(bArr, i5, iRemaining);
                    } else {
                        iRemaining = i4;
                        byteBuffer.get(bArr, i5, i4);
                        this.incomingQueue.addFirst(byteBuffer);
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
