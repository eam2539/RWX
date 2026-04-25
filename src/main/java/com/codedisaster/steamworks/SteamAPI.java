package com.codedisaster.steamworks;

import java.io.PrintStream;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamAPI.class */
public class SteamAPI {
    private static boolean isRunning = false;

    public static native boolean restartAppIfNecessary(int i);

    public static native void releaseCurrentThreadMemory();

    private static native boolean nativeInit();

    private static native void nativeShutdown();

    public static native void runCallbacks();

    private static native boolean isSteamRunningNative();

    static native long getSteamAppsPointer();

    static native long getSteamControllerPointer();

    static native long getSteamFriendsPointer();

    static native long getSteamHTTPPointer();

    static native long getSteamMatchmakingPointer();

    static native long getSteamNetworkingPointer();

    static native long getSteamRemoteStoragePointer();

    static native long getSteamUGCPointer();

    static native long getSteamUserPointer();

    static native long getSteamUserStatsPointer();

    static native long getSteamUtilsPointer();

    public static boolean init() {
        return init(null);
    }

    public static boolean init(String str) throws SteamException {
        SteamSharedLibraryLoader.loadLibraries(str);
        isRunning = nativeInit();
        return isRunning;
    }

    public static void shutdown() {
        isRunning = false;
        nativeShutdown();
    }

    public static boolean isSteamRunning() {
        return isSteamRunning(false);
    }

    public static boolean isSteamRunning(boolean z) {
        return isRunning && (!z || isSteamRunningNative());
    }

    public static void printDebugInfo(PrintStream printStream) {
        if (SteamSharedLibraryLoader.alreadyLoaded) {
            printStream.println("  shared libraries loaded from: " + SteamSharedLibraryLoader.librarySystemPath);
        } else {
            printStream.println("  shared libraries not loaded");
        }
        printStream.println("  Steam API initialized: " + isRunning);
        printStream.println("  Steam client active: " + isSteamRunning());
    }
}
