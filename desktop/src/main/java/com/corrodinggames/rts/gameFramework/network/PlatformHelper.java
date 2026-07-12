package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.GameLogic;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ar */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ar.class */
public class PlatformHelper {
    public static int a(int i) {
        return i + 5;
    }

    public static String a() {
        if (!GameEngine.isAndroidPlatform()) {
            return null;
        }
        return ((GameLogic) GameEngine.getInstance()).getSignature();
    }
}
