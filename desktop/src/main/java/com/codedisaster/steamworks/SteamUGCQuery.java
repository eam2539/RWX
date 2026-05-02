package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGCQuery.class */
public class SteamUGCQuery extends SteamNativeHandle {
    public SteamUGCQuery(long j) {
        super(j);
    }

    public boolean isValid() {
        return this.handle != -1;
    }
}
