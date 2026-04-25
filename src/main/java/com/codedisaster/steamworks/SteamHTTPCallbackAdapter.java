package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamHTTP;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamHTTPCallbackAdapter.class */
class SteamHTTPCallbackAdapter extends SteamCallbackAdapter {
    SteamHTTPCallbackAdapter(SteamHTTPCallback steamHTTPCallback) {
        super(steamHTTPCallback);
    }

    void onHTTPRequestCompleted(long j, long j2, boolean z, int i, int i2) {
        ((SteamHTTPCallback) this.callback).onHTTPRequestCompleted(new SteamHTTPRequestHandle(j), j2, z, SteamHTTP.HTTPStatusCode.byValue(i), i2);
    }

    void onHTTPRequestHeadersReceived(long j, long j2) {
        ((SteamHTTPCallback) this.callback).onHTTPRequestHeadersReceived(new SteamHTTPRequestHandle(j), j2);
    }

    void onHTTPRequestDataReceived(long j, long j2, int i, int i2) {
        ((SteamHTTPCallback) this.callback).onHTTPRequestDataReceived(new SteamHTTPRequestHandle(j), j2, i, i2);
    }
}
