package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.saf.SafFileLoader;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.ae */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/ae.class */
public class FileLoaderFactory {

    /* JADX INFO: renamed from: a */
    static Object zipFileLoaderLock = new Object();

    /* JADX INFO: renamed from: b */
    static RwmodFileLoader modFileLoader = new RwmodFileLoader();

    /* JADX INFO: renamed from: c */
    static IFileLoader zipFileLoader;

    /* JADX INFO: renamed from: a */
    public static boolean isZipFSEnabled() {
        if (GameEngine.isDesktop()) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: a */
    public static IFileLoader getFileLoaderForPath(String str) {
        if (RwmodFileLoader.i(str)) {
            return modFileLoader;
        }
        if (isZipFSEnabled() && SafFileLoader.l(str)) {
            if (zipFileLoader == null) {
                synchronized (zipFileLoaderLock) {
                    if (zipFileLoader == null) {
                        zipFileLoader = new SafFileLoader();
                    }
                }
            }
            return zipFileLoader;
        }
        return null;
    }

    /* JADX INFO: renamed from: b */
    public static IFileLoader getZipFileLoaderForPath(String str) {
        if (isZipFSEnabled() && SafFileLoader.l(str)) {
            if (zipFileLoader == null) {
                synchronized (zipFileLoaderLock) {
                    if (zipFileLoader == null) {
                        zipFileLoader = new SafFileLoader();
                    }
                }
            }
            return zipFileLoader;
        }
        return null;
    }

    /* JADX INFO: renamed from: c */
    public static void closeModFile(String str) {
        if (modFileLoader != null && RwmodFileLoader.i(str)) {
            modFileLoader.k(str);
        }
    }

    /* JADX INFO: renamed from: b */
    public static void closeAll() {
        if (modFileLoader != null) {
            modFileLoader.isDirect();
        }
        if (zipFileLoader != null) {
            zipFileLoader.isDirect();
        }
    }
}
