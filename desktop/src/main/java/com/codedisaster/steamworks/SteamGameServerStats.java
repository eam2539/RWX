package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamGameServerStats.class */
public class SteamGameServerStats extends SteamInterface {
    private static native long createCallback(SteamGameServerStatsCallbackAdapter steamGameServerStatsCallbackAdapter);

    private static native long requestUserStats(long j, long j2);

    private static native boolean getUserStat(long j, long j2, String str, int[] iArr);

    private static native boolean getUserStat(long j, long j2, String str, float[] fArr);

    private static native boolean getUserAchievement(long j, long j2, String str, boolean[] zArr);

    private static native boolean setUserStat(long j, long j2, String str, int i);

    private static native boolean setUserStat(long j, long j2, String str, float f);

    private static native boolean updateUserAvgRateStat(long j, long j2, String str, float f, double d);

    private static native boolean setUserAchievement(long j, long j2, String str);

    private static native boolean clearUserAchievement(long j, long j2, String str);

    private static native long storeUserStats(long j, long j2);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    public SteamGameServerStats(SteamGameServerStatsCallback steamGameServerStatsCallback) {
        super(SteamGameServerAPI.getSteamGameServerStatsPointer(), createCallback(new SteamGameServerStatsCallbackAdapter(steamGameServerStatsCallback)));
    }

    public SteamAPICall requestUserStats(SteamID steamID) {
        return new SteamAPICall(requestUserStats(this.pointer, steamID.handle));
    }

    public int getUserStatI(SteamID steamID, String str, int i) {
        int[] iArr = new int[1];
        if (getUserStat(this.pointer, steamID.handle, str, iArr)) {
            return iArr[0];
        }
        return i;
    }

    public float getUserStatF(SteamID steamID, String str, float f) {
        float[] fArr = new float[1];
        if (getUserStat(this.pointer, steamID.handle, str, fArr)) {
            return fArr[0];
        }
        return f;
    }

    public boolean getUserAchievement(SteamID steamID, String str, boolean z) {
        boolean[] zArr = new boolean[1];
        if (getUserAchievement(this.pointer, steamID.handle, str, zArr)) {
            return zArr[0];
        }
        return z;
    }

    public boolean setUserStatI(SteamID steamID, String str, int i) {
        return setUserStat(this.pointer, steamID.handle, str, i);
    }

    public boolean setUserStatF(SteamID steamID, String str, float f) {
        return setUserStat(this.pointer, steamID.handle, str, f);
    }

    public boolean updateUserAvgRateStat(SteamID steamID, String str, float f, double d) {
        return updateUserAvgRateStat(this.pointer, steamID.handle, str, f, d);
    }

    public boolean setUserAchievement(SteamID steamID, String str) {
        return setUserAchievement(this.pointer, steamID.handle, str);
    }

    public boolean clearUserAchievement(SteamID steamID, String str) {
        return clearUserAchievement(this.pointer, steamID.handle, str);
    }

    public SteamAPICall storeUserStats(SteamID steamID) {
        return new SteamAPICall(storeUserStats(this.pointer, steamID.handle));
    }
}
