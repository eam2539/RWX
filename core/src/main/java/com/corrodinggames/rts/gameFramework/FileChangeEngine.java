package com.corrodinggames.rts.gameFramework;

import java.io.File;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j.class */
public class FileChangeEngine {

    /* JADX INFO: renamed from: a */
    static ConcurrentHashMap fileTimestamps = new ConcurrentHashMap();

    /* JADX INFO: renamed from: b */
    static FileChangeThread watchThread;

    public static long a(String str, boolean z) {
        if (str == null) {
            return 0L;
        }
        Long l = (Long) fileTimestamps.get(str);
        if (l != null) {
            return l.longValue();
        }
        Long lValueOf = Long.valueOf(a(str));
        if (!z) {
            fileTimestamps.put(str, lValueOf);
            if (watchThread == null) {
            }
        }
        return lValueOf.longValue();
    }

    private static long a(String str) {
        return new File(str).lastModified();
    }

    public static synchronized void a() {
        a(GameEngine.getInstance().settingsEngine.liveReloading);
    }

    public static synchronized void a(boolean z) {
        if (!GameEngine.isPC()) {
            return;
        }
        if (z) {
            if (watchThread != null) {
                GameEngine.log("FileChangeEngine: Already running");
                return;
            }
            GameEngine.log("FileChangeEngine: Starting");
            watchThread = new FileChangeThread();
            watchThread.start();
            return;
        }
        if (watchThread != null) {
            watchThread.a = false;
            watchThread = null;
        }
    }

    public static void b() {
        int i = 0;
        Enumeration enumerationKeys = fileTimestamps.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            long jA = a(str);
            Long l = (Long) fileTimestamps.get(str);
            if (l == null) {
                GameEngine.log("FileChangeEngine: old lastModified null for " + str);
            } else if (l.longValue() != jA) {
                GameEngine.log("FileChangeEngine: Detected change to:" + str + " now " + jA);
            }
            fileTimestamps.put(str, Long.valueOf(jA));
            i++;
            if (i > 50) {
                i = 0;
                try {
                    Thread.sleep(2L);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
