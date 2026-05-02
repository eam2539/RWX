package com.corrodinggames.rts.java.steam;

import com.codedisaster.steamworks.SteamNetworking;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c/m.class */
public class SteamSocketOutputStream extends OutputStream {
    boolean a = true;
    final /* synthetic */ SteamSocket b;

    public SteamSocketOutputStream(SteamSocket steamSocket) {
        this.b = steamSocket;
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        GameEngine.logWarningAndStack("SteamSocketOutputStream: Slow write: " + i);
        write(new byte[]{(byte) i});
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        if (this.b.b) {
            GameEngine.log("cannot write steam socket closed");
            return;
        }
        if (i2 > 307200) {
            GameEngine.log("Steam spliting large packet to:" + this.b.e + " len:" + i2);
            int i3 = i2;
            do {
                int i4 = i3;
                if (i4 > 256000) {
                    i4 = 256000;
                }
                write(bArr, i, i4);
                i += i4;
                i3 -= i4;
            } while (i3 > 0);
            return;
        }
        ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(i2);
        byteBufferAllocateDirect.put(bArr, i, i2);
        byteBufferAllocateDirect.flip();
        synchronized (this.b.a) {
            try {
                if (this.a) {
                    this.a = false;
                    GameEngine.log("First packet to:" + this.b.e);
                }
                if (!this.b.a.h.sendP2PPacket(this.b.e, byteBufferAllocateDirect, SteamNetworking.P2PSend.Reliable, 0)) {
                    GameEngine.log("steam sendP2PPacket failed (size: " + i2 + " to:" + this.b.e + ")");
                }
            } catch (Exception/*SteamException*/ e) {
                throw new IOException(e);
            }
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }
}
