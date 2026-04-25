package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUserStatsCallback.class */
public interface SteamUserStatsCallback {
    void onUserStatsReceived(long j, SteamID steamID, SteamResult steamResult);

    void onUserStatsStored(long j, SteamResult steamResult);

    void onUserStatsUnloaded(SteamID steamID);

    void onUserAchievementStored(long j, boolean z, String str, int i, int i2);

    void onLeaderboardFindResult(SteamLeaderboardHandle steamLeaderboardHandle, boolean z);

    void onLeaderboardScoresDownloaded(SteamLeaderboardHandle steamLeaderboardHandle, SteamLeaderboardEntriesHandle steamLeaderboardEntriesHandle, int i);

    void onLeaderboardScoreUploaded(boolean z, SteamLeaderboardHandle steamLeaderboardHandle, int i, boolean z2, int i2, int i3);

    void onGlobalStatsReceived(long j, SteamResult steamResult);
}
