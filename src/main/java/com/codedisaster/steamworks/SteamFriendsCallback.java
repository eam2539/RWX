package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamFriends;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriendsCallback.class */
public interface SteamFriendsCallback {
    void onSetPersonaNameResponse(boolean z, boolean z2, SteamResult steamResult);

    void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange personaChange);

    void onGameOverlayActivated(boolean z);

    void onGameLobbyJoinRequested(SteamID steamID, SteamID steamID2);

    void onAvatarImageLoaded(SteamID steamID, int i, int i2, int i3);

    void onFriendRichPresenceUpdate(SteamID steamID, int i);

    void onGameRichPresenceJoinRequested(SteamID steamID, String str);
}
