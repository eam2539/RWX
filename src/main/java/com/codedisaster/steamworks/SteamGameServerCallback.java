package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamAuth;
import com.codedisaster.steamworks.SteamGameServer;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamGameServerCallback.class */
public interface SteamGameServerCallback {
    void onValidateAuthTicketResponse(SteamID steamID, SteamAuth.AuthSessionResponse authSessionResponse, SteamID steamID2);

    void onSteamServersConnected();

    void onSteamServerConnectFailure(SteamResult steamResult, boolean z);

    void onSteamServersDisconnected(SteamResult steamResult);

    void onClientApprove(SteamID steamID, SteamID steamID2);

    void onClientDeny(SteamID steamID, SteamGameServer.DenyReason denyReason, String str);

    void onClientKick(SteamID steamID, SteamGameServer.DenyReason denyReason);

    void onClientGroupStatus(SteamID steamID, SteamID steamID2, boolean z, boolean z2);

    void onAssociateWithClanResult(SteamResult steamResult);

    void onComputeNewPlayerCompatibilityResult(SteamResult steamResult, int i, int i2, int i3, SteamID steamID);
}
