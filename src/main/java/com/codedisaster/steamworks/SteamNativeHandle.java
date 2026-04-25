package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamNativeHandle.class */
public abstract class SteamNativeHandle {
    long handle;

    SteamNativeHandle(long j) {
        this.handle = j;
    }

    public static long getNativeHandle(SteamNativeHandle steamNativeHandle) {
        return steamNativeHandle.handle;
    }

    public int hashCode() {
        return Long.valueOf(this.handle).hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof SteamNativeHandle) && this.handle == ((SteamNativeHandle) obj).handle;
    }

    public String toString() {
        return Long.toHexString(this.handle);
    }
}
