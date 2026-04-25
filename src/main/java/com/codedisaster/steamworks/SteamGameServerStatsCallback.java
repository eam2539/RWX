package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamGameServerStatsCallback.class */
public interface SteamGameServerStatsCallback {
    void onStatsReceived(SteamResult steamResult, SteamID steamID);

    void onStatsStored(SteamResult steamResult, SteamID steamID);

    void onStatsUnloaded(SteamID steamID);
}
