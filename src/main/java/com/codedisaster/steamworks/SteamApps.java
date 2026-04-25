package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamApps.class */
public class SteamApps extends SteamInterface {
    private static native boolean isSubscribedApp(long j, int i);

    private static native String getCurrentGameLanguage(long j);

    private static native String getAvailableGameLanguages(long j);

    private static native long getAppOwner(long j);

    private static native int getAppBuildId(long j);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    public SteamApps() {
        super(SteamAPI.getSteamAppsPointer());
    }

    public boolean isSubscribedApp(int i) {
        return isSubscribedApp(this.pointer, i);
    }

    public String getCurrentGameLanguage() {
        return getCurrentGameLanguage(this.pointer);
    }

    public String getAvailableGameLanguages() {
        return getAvailableGameLanguages(this.pointer);
    }

    public SteamID getAppOwner() {
        return new SteamID(getAppOwner(this.pointer));
    }

    public int getAppBuildId() {
        return getAppBuildId(this.pointer);
    }
}
