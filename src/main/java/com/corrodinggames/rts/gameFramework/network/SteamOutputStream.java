package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.OutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/j.class */
public class SteamOutputStream extends OutputStream {

    /* JADX INFO: renamed from: a */
    boolean logSlowWrite = true;

    /* JADX INFO: renamed from: b */
    final /* synthetic */ SteamSocket steamSocket;

    public SteamOutputStream(SteamSocket steamSocket) {
        this.steamSocket = steamSocket;
    }

    @Override // java.io.OutputStream
    public void write(int i) {
        GameEngine.logWarningAndStack("SteamSocketOutputStream: Slow write: " + i);
        write(new byte[]{(byte) i});
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) {
        if (this.steamSocket.closed) {
            GameEngine.isInSpace("cannot write steam socket closed");
        } else {
            GameEngine.isInSpace("Forwarded message to client: " + this.steamSocket.steamChannelId + " with old method");
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr) {
        write(bArr, 0, bArr.length);
    }
}
