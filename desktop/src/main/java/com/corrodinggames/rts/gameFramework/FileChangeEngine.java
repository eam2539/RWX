package com.corrodinggames.rts.gameFramework;

import java.io.File;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j.class */
public class FileChangeEngine {
    static ConcurrentHashMap a = new ConcurrentHashMap();
    static FileChangeThread b;

    public static long a(String str, boolean z) {
        if (str == null) {
            return 0L;
        }
        Long l = (Long) a.get(str);
        if (l != null) {
            return l.longValue();
        }
        Long lValueOf = Long.valueOf(a(str));
        if (!z) {
            a.put(str, lValueOf);
            if (b == null) {
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
            if (b != null) {
                GameEngine.log("FileChangeEngine: Already running");
                return;
            }
            GameEngine.log("FileChangeEngine: Starting");
            b = new FileChangeThread();
            b.start();
            return;
        }
        if (b != null) {
            b.a = false;
            b = null;
        }
    }

    public static void b() {
        int i = 0;
        Enumeration enumerationKeys = a.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            long jA = a(str);
            Long l = (Long) a.get(str);
            if (l == null) {
                GameEngine.log("FileChangeEngine: old lastModified null for " + str);
            } else if (l.longValue() != jA) {
                GameEngine.log("FileChangeEngine: Detected change to:" + str + " now " + jA);
            }
            a.put(str, Long.valueOf(jA));
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
