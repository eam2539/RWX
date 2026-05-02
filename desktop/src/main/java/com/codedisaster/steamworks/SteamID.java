package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamID.class */
public class SteamID extends SteamNativeHandle {
    private static final long InvalidSteamID = getInvalidSteamID();

    private static native boolean isValid(long j);

    private static native long getInvalidSteamID();

    public SteamID() {
        super(InvalidSteamID);
    }

    public SteamID(SteamID steamID) {
        super(steamID.handle);
    }

    SteamID(long j) {
        super(j);
    }

    public boolean isValid() {
        return isValid(this.handle);
    }

    public int getAccountID() {
        return (int) (this.handle % 4294967296L);
    }

    public static SteamID createFromNativeHandle(long j) {
        return new SteamID(j);
    }
}
