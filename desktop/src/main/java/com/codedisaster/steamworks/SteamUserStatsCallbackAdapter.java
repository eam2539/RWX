package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUserStatsCallbackAdapter.class */
class SteamUserStatsCallbackAdapter extends SteamCallbackAdapter {
    SteamUserStatsCallbackAdapter(SteamUserStatsCallback steamUserStatsCallback) {
        super(steamUserStatsCallback);
    }

    void onUserStatsReceived(long j, long j2, int i) {
        ((SteamUserStatsCallback) this.callback).onUserStatsReceived(j, new SteamID(j2), SteamResult.byValue(i));
    }

    void onUserStatsStored(long j, int i) {
        ((SteamUserStatsCallback) this.callback).onUserStatsStored(j, SteamResult.byValue(i));
    }

    void onUserStatsUnloaded(long j) {
        ((SteamUserStatsCallback) this.callback).onUserStatsUnloaded(new SteamID(j));
    }

    void onUserAchievementStored(long j, boolean z, String str, int i, int i2) {
        ((SteamUserStatsCallback) this.callback).onUserAchievementStored(j, z, str, i, i2);
    }

    void onLeaderboardFindResult(long j, boolean z) {
        ((SteamUserStatsCallback) this.callback).onLeaderboardFindResult(new SteamLeaderboardHandle(j), z);
    }

    void onLeaderboardScoresDownloaded(long j, long j2, int i) {
        ((SteamUserStatsCallback) this.callback).onLeaderboardScoresDownloaded(new SteamLeaderboardHandle(j), new SteamLeaderboardEntriesHandle(j2), i);
    }

    void onLeaderboardScoreUploaded(boolean z, long j, int i, boolean z2, int i2, int i3) {
        ((SteamUserStatsCallback) this.callback).onLeaderboardScoreUploaded(z, new SteamLeaderboardHandle(j), i, z2, i2, i3);
    }

    void onGlobalStatsReceived(long j, int i) {
        ((SteamUserStatsCallback) this.callback).onGlobalStatsReceived(j, SteamResult.byValue(i));
    }
}
