package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamInterface.class */
abstract class SteamInterface {
    protected final long pointer;
    protected long callback;

    protected static native void deleteCallback(long j);

    SteamInterface(long j) {
        this(j, 0L);
    }

    SteamInterface(long j, long j2) {
        if (j == 0) {
            throw new RuntimeException("Steam interface created with null pointer. Always check result of SteamAPI.init(), or with SteamAPI.isSteamRunning()!");
        }
        this.pointer = j;
        this.callback = j2;
    }

    void setCallback(long j) {
        this.callback = j;
    }

    public void dispose() {
        deleteCallback(this.callback);
    }
}
