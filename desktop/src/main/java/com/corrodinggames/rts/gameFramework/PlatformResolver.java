package com.corrodinggames.rts.gameFramework;

import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/g.class */
public final class PlatformResolver {
    protected static Platform a;

    public static Platform a() {
        if (a == null) {
            String lowerCase = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if (lowerCase.indexOf("mac") >= 0 || lowerCase.indexOf("darwin") >= 0) {
                a = Platform.MacOS;
            } else if (lowerCase.indexOf("win") >= 0) {
                a = Platform.Windows;
            } else if (lowerCase.indexOf("nux") >= 0) {
                a = Platform.Linux;
            } else {
                a = Platform.Other;
            }
        }
        return a;
    }
}
