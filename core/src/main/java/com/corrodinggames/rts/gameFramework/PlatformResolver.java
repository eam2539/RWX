package com.corrodinggames.rts.gameFramework;

import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/g.class */
public final class PlatformResolver {

    /* JADX INFO: renamed from: a */
    protected static Platform currentPlatform;

    public static Platform a() {
        if (currentPlatform == null) {
            String lowerCase = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if (lowerCase.indexOf("mac") >= 0 || lowerCase.indexOf("darwin") >= 0) {
                currentPlatform = Platform.MacOS;
            } else if (lowerCase.indexOf("win") >= 0) {
                currentPlatform = Platform.Windows;
            } else if (lowerCase.indexOf("nux") >= 0) {
                currentPlatform = Platform.Linux;
            } else {
                currentPlatform = Platform.Other;
            }
        }
        return currentPlatform;
    }
}
