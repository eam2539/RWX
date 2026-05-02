package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUserStats.class */
public class SteamUserStats extends SteamInterface {

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUserStats$LeaderboardDataRequest.class */
    public enum LeaderboardDataRequest {
        Global,
        GlobalAroundUser,
        Friends,
        Users
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUserStats$LeaderboardDisplayType.class */
    public enum LeaderboardDisplayType {
        None,
        Numeric,
        TimeSeconds,
        TimeMilliSeconds
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUserStats$LeaderboardSortMethod.class */
    public enum LeaderboardSortMethod {
        None,
        Ascending,
        Descending
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUserStats$LeaderboardUploadScoreMethod.class */
    public enum LeaderboardUploadScoreMethod {
        None,
        KeepBest,
        ForceUpdate
    }

    private static native long createCallback(SteamUserStatsCallbackAdapter steamUserStatsCallbackAdapter);

    private static native boolean requestCurrentStats(long j);

    private static native boolean getStat(long j, String str, int[] iArr);

    private static native boolean setStat(long j, String str, int i);

    private static native boolean getStat(long j, String str, float[] fArr);

    private static native boolean setStat(long j, String str, float f);

    private static native boolean getAchievement(long j, String str, boolean[] zArr);

    private static native boolean setAchievement(long j, String str);

    private static native boolean clearAchievement(long j, String str);

    private static native boolean storeStats(long j);

    private static native boolean indicateAchievementProgress(long j, String str, int i, int i2);

    private static native int getNumAchievements(long j);

    private static native String getAchievementName(long j, int i);

    private static native boolean resetAllStats(long j, boolean z);

    private static native long findOrCreateLeaderboard(long j, long j2, String str, int i, int i2);

    private static native long findLeaderboard(long j, long j2, String str);

    private static native String getLeaderboardName(long j, long j2);

    private static native int getLeaderboardEntryCount(long j, long j2);

    private static native int getLeaderboardSortMethod(long j, long j2);

    private static native int getLeaderboardDisplayType(long j, long j2);

    private static native long downloadLeaderboardEntries(long j, long j2, long j3, int i, int i2, int i3);

    private static native boolean getDownloadedLeaderboardEntry(long j, long j2, int i, SteamLeaderboardEntry steamLeaderboardEntry, int[] iArr, int i2);

    private static native boolean getDownloadedLeaderboardEntry(long j, long j2, int i, SteamLeaderboardEntry steamLeaderboardEntry);

    private static native long uploadLeaderboardScore(long j, long j2, long j3, int i, int i2, int[] iArr, int i3);

    private static native long uploadLeaderboardScore(long j, long j2, long j3, int i, int i2);

    private static native long requestGlobalStats(long j, long j2, int i);

    private static native boolean getGlobalStat(long j, String str, long[] jArr);

    private static native boolean getGlobalStat(long j, String str, double[] dArr);

    private static native int getGlobalStatHistory(long j, String str, long[] jArr, int i);

    private static native int getGlobalStatHistory(long j, String str, double[] dArr, int i);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    public SteamUserStats(SteamUserStatsCallback steamUserStatsCallback) {
        super(SteamAPI.getSteamUserStatsPointer(), createCallback(new SteamUserStatsCallbackAdapter(steamUserStatsCallback)));
    }

    public boolean requestCurrentStats() {
        return requestCurrentStats(this.pointer);
    }

    public int getStatI(String str, int i) {
        int[] iArr = new int[1];
        if (getStat(this.pointer, str, iArr)) {
            return iArr[0];
        }
        return i;
    }

    public boolean setStatI(String str, int i) {
        return setStat(this.pointer, str, i);
    }

    public float getStatF(String str, float f) {
        float[] fArr = new float[1];
        if (getStat(this.pointer, str, fArr)) {
            return fArr[0];
        }
        return f;
    }

    public boolean setStatF(String str, float f) {
        return setStat(this.pointer, str, f);
    }

    public boolean isAchieved(String str, boolean z) {
        boolean[] zArr = new boolean[1];
        if (getAchievement(this.pointer, str, zArr)) {
            return zArr[0];
        }
        return z;
    }

    public boolean setAchievement(String str) {
        return setAchievement(this.pointer, str);
    }

    public boolean clearAchievement(String str) {
        return clearAchievement(this.pointer, str);
    }

    public boolean storeStats() {
        return storeStats(this.pointer);
    }

    public boolean indicateAchievementProgress(String str, int i, int i2) {
        return indicateAchievementProgress(this.pointer, str, i, i2);
    }

    public int getNumAchievements() {
        return getNumAchievements(this.pointer);
    }

    public String getAchievementName(int i) {
        return getAchievementName(this.pointer, i);
    }

    public boolean resetAllStats(boolean z) {
        return resetAllStats(this.pointer, z);
    }

    public SteamAPICall findOrCreateLeaderboard(String str, LeaderboardSortMethod leaderboardSortMethod, LeaderboardDisplayType leaderboardDisplayType) {
        return new SteamAPICall(findOrCreateLeaderboard(this.pointer, this.callback, str, leaderboardSortMethod.ordinal(), leaderboardDisplayType.ordinal()));
    }

    public SteamAPICall findLeaderboard(String str) {
        return new SteamAPICall(findLeaderboard(this.pointer, this.callback, str));
    }

    public String getLeaderboardName(SteamLeaderboardHandle steamLeaderboardHandle) {
        return getLeaderboardName(this.pointer, steamLeaderboardHandle.handle);
    }

    public int getLeaderboardEntryCount(SteamLeaderboardHandle steamLeaderboardHandle) {
        return getLeaderboardEntryCount(this.pointer, steamLeaderboardHandle.handle);
    }

    public LeaderboardSortMethod getLeaderboardSortMethod(SteamLeaderboardHandle steamLeaderboardHandle) {
        return LeaderboardSortMethod.values()[getLeaderboardSortMethod(this.pointer, steamLeaderboardHandle.handle)];
    }

    public LeaderboardDisplayType getLeaderboardDisplayType(SteamLeaderboardHandle steamLeaderboardHandle) {
        return LeaderboardDisplayType.values()[getLeaderboardDisplayType(this.pointer, steamLeaderboardHandle.handle)];
    }

    public SteamAPICall downloadLeaderboardEntries(SteamLeaderboardHandle steamLeaderboardHandle, LeaderboardDataRequest leaderboardDataRequest, int i, int i2) {
        return new SteamAPICall(downloadLeaderboardEntries(this.pointer, this.callback, steamLeaderboardHandle.handle, leaderboardDataRequest.ordinal(), i, i2));
    }

    public boolean getDownloadedLeaderboardEntry(SteamLeaderboardEntriesHandle steamLeaderboardEntriesHandle, int i, SteamLeaderboardEntry steamLeaderboardEntry, int[] iArr) {
        if (iArr == null) {
            return getDownloadedLeaderboardEntry(this.pointer, steamLeaderboardEntriesHandle.handle, i, steamLeaderboardEntry);
        }
        return getDownloadedLeaderboardEntry(this.pointer, steamLeaderboardEntriesHandle.handle, i, steamLeaderboardEntry, iArr, iArr.length);
    }

    public SteamAPICall uploadLeaderboardScore(SteamLeaderboardHandle steamLeaderboardHandle, LeaderboardUploadScoreMethod leaderboardUploadScoreMethod, int i, int[] iArr) {
        long jUploadLeaderboardScore;
        if (iArr == null) {
            jUploadLeaderboardScore = uploadLeaderboardScore(this.pointer, this.callback, steamLeaderboardHandle.handle, leaderboardUploadScoreMethod.ordinal(), i);
        } else {
            jUploadLeaderboardScore = uploadLeaderboardScore(this.pointer, this.callback, steamLeaderboardHandle.handle, leaderboardUploadScoreMethod.ordinal(), i, iArr, iArr.length);
        }
        return new SteamAPICall(jUploadLeaderboardScore);
    }

    public SteamAPICall requestGlobalStats(int i) {
        return new SteamAPICall(requestGlobalStats(this.pointer, this.callback, i));
    }

    public long getGlobalStat(String str, long j) {
        long[] jArr = new long[1];
        if (getGlobalStat(this.pointer, str, jArr)) {
            return jArr[0];
        }
        return j;
    }

    public double getGlobalStat(String str, double d) {
        double[] dArr = new double[1];
        if (getGlobalStat(this.pointer, str, dArr)) {
            return dArr[0];
        }
        return d;
    }

    public int getGlobalStatHistory(String str, long[] jArr) {
        return getGlobalStatHistory(this.pointer, str, jArr, jArr.length);
    }

    public int getGlobalStatHistory(String str, double[] dArr) {
        return getGlobalStatHistory(this.pointer, str, dArr, dArr.length);
    }
}
