package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamNetworking;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamNetworkingCallback.class */
public interface SteamNetworkingCallback {
    void onP2PSessionConnectFail(SteamID steamID, SteamNetworking.P2PSessionError p2PSessionError);

    void onP2PSessionRequest(SteamID steamID);
}
