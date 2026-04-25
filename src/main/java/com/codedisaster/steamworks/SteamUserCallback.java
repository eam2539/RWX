package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamAuth;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUserCallback.class */
public interface SteamUserCallback {
    void onValidateAuthTicket(SteamID steamID, SteamAuth.AuthSessionResponse authSessionResponse, SteamID steamID2);

    void onMicroTxnAuthorization(int i, long j, boolean z);
}
