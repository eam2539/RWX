package com.corrodinggames.rts.java.steam;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworkingCallback;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c/f.class */
public class SteamNetworkingCallbackHandler implements SteamNetworkingCallback {
    JavaSteamEngine a;

    public SteamNetworkingCallbackHandler(JavaSteamEngine javaSteamEngine) {
        this.a = javaSteamEngine;
    }

    @Override // com.codedisaster.steamworks.SteamNetworkingCallback
    public void onP2PSessionConnectFail(SteamID steamID, SteamNetworking.P2PSessionError p2PSessionError) {
        GameEngine.isInSpace("onP2PSessionConnectFail:" + p2PSessionError);
        SteamSocket steamSocket = (SteamSocket) this.a.l.get(steamID);
        if (steamSocket != null && !steamSocket.isClosed()) {
            GameEngine.isInSpace("onP2PSessionConnectFail: closing active socket");
            steamSocket.close();
        }
    }

    @Override // com.codedisaster.steamworks.SteamNetworkingCallback
    public void onP2PSessionRequest(SteamID steamID) {
        GameEngine.isInSpace("onP2PSessionRequest:" + steamID);
        this.a.h.acceptP2PSessionWithUser(steamID);
    }
}
