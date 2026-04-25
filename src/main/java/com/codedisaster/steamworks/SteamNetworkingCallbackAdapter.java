package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamNetworking;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamNetworkingCallbackAdapter.class */
class SteamNetworkingCallbackAdapter extends SteamCallbackAdapter {
    SteamNetworkingCallbackAdapter(SteamNetworkingCallback steamNetworkingCallback) {
        super(steamNetworkingCallback);
    }

    void onP2PSessionConnectFail(long j, int i) {
        ((SteamNetworkingCallback) this.callback).onP2PSessionConnectFail(new SteamID(j), SteamNetworking.P2PSessionError.byOrdinal(i));
    }

    void onP2PSessionRequest(long j) {
        ((SteamNetworkingCallback) this.callback).onP2PSessionRequest(new SteamID(j));
    }
}
