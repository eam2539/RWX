package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamAuth;
import com.codedisaster.steamworks.SteamGameServer;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamGameServerCallbackAdapter.class */
class SteamGameServerCallbackAdapter extends SteamCallbackAdapter {
    SteamGameServerCallbackAdapter(SteamGameServerCallback steamGameServerCallback) {
        super(steamGameServerCallback);
    }

    void onValidateAuthTicketResponse(long j, int i, long j2) {
        ((SteamGameServerCallback) this.callback).onValidateAuthTicketResponse(new SteamID(j), SteamAuth.AuthSessionResponse.byOrdinal(i), new SteamID(j2));
    }

    void onSteamServersConnected() {
        ((SteamGameServerCallback) this.callback).onSteamServersConnected();
    }

    void onSteamServerConnectFailure(int i, boolean z) {
        ((SteamGameServerCallback) this.callback).onSteamServerConnectFailure(SteamResult.byValue(i), z);
    }

    void onSteamServersDisconnected(int i) {
        ((SteamGameServerCallback) this.callback).onSteamServersDisconnected(SteamResult.byValue(i));
    }

    void onClientApprove(long j, long j2) {
        ((SteamGameServerCallback) this.callback).onClientApprove(new SteamID(j), new SteamID(j2));
    }

    void onClientDeny(long j, int i, String str) {
        ((SteamGameServerCallback) this.callback).onClientDeny(new SteamID(j), SteamGameServer.DenyReason.byOrdinal(i), str);
    }

    void onClientKick(long j, int i) {
        ((SteamGameServerCallback) this.callback).onClientKick(new SteamID(j), SteamGameServer.DenyReason.byOrdinal(i));
    }

    void onClientGroupStatus(long j, long j2, boolean z, boolean z2) {
        ((SteamGameServerCallback) this.callback).onClientGroupStatus(new SteamID(j), new SteamID(j2), z, z2);
    }

    void onAssociateWithClanResult(int i) {
        ((SteamGameServerCallback) this.callback).onAssociateWithClanResult(SteamResult.byValue(i));
    }

    void onComputeNewPlayerCompatibilityResult(int i, int i2, int i3, int i4, long j) {
        ((SteamGameServerCallback) this.callback).onComputeNewPlayerCompatibilityResult(SteamResult.byValue(i), i2, i3, i4, new SteamID(j));
    }
}
