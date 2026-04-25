package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamFriends;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamFriendsCallbackAdapter.class */
class SteamFriendsCallbackAdapter extends SteamCallbackAdapter {
    private static final SteamFriends.PersonaChange[] personaChangeValues = SteamFriends.PersonaChange.values();

    SteamFriendsCallbackAdapter(SteamFriendsCallback steamFriendsCallback) {
        super(steamFriendsCallback);
    }

    void onSetPersonaNameResponse(boolean z, boolean z2, int i) {
        ((SteamFriendsCallback) this.callback).onSetPersonaNameResponse(z, z2, SteamResult.byValue(i));
    }

    void onPersonaStateChange(long j, int i) {
        SteamID steamID = new SteamID(j);
        for (SteamFriends.PersonaChange personaChange : personaChangeValues) {
            if (SteamFriends.PersonaChange.isSet(personaChange, i)) {
                ((SteamFriendsCallback) this.callback).onPersonaStateChange(steamID, personaChange);
            }
        }
    }

    void onGameOverlayActivated(boolean z) {
        ((SteamFriendsCallback) this.callback).onGameOverlayActivated(z);
    }

    void onGameLobbyJoinRequested(long j, long j2) {
        ((SteamFriendsCallback) this.callback).onGameLobbyJoinRequested(new SteamID(j), new SteamID(j2));
    }

    void onAvatarImageLoaded(long j, int i, int i2, int i3) {
        ((SteamFriendsCallback) this.callback).onAvatarImageLoaded(new SteamID(j), i, i2, i3);
    }

    void onFriendRichPresenceUpdate(long j, int i) {
        ((SteamFriendsCallback) this.callback).onFriendRichPresenceUpdate(new SteamID(j), i);
    }

    void onGameRichPresenceJoinRequested(long j, String str) {
        ((SteamFriendsCallback) this.callback).onGameRichPresenceJoinRequested(new SteamID(j), str);
    }
}
