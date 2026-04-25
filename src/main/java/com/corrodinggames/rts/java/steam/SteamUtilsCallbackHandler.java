package com.corrodinggames.rts.java.steam;

import com.codedisaster.steamworks.SteamUtilsCallback;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c/c.class */
public class SteamUtilsCallbackHandler implements SteamUtilsCallback {
    final /* synthetic */ JavaSteamEngine a;

    public SteamUtilsCallbackHandler(JavaSteamEngine javaSteamEngine) {
        this.a = javaSteamEngine;
    }

    @Override // com.codedisaster.steamworks.SteamUtilsCallback
    public void onSteamShutdown() {
    }
}
