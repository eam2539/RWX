package com.corrodinggames.rts.gameFramework.platform;

import java.io.File;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.l.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/l/a.class */
public class PlatformExtension {
    static PlatformExtensionBackend a = new PlatformExtensionBackend();

    public static String a() {
        return a.a();
    }

    public static boolean b() {
        return a.b();
    }

    public static void a(File file) {
        a.a(file);
    }

    public static void a(FileSelectionCallback fileSelectionCallback) {
        a.a(fileSelectionCallback);
    }

    public static float c() {
        return a.c();
    }
}
