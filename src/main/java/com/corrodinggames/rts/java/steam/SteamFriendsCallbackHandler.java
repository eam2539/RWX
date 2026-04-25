package com.corrodinggames.rts.java.steam;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamResult;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c/d.class */
public class SteamFriendsCallbackHandler implements SteamFriendsCallback {
    JavaSteamEngine a;

    public SteamFriendsCallbackHandler(JavaSteamEngine javaSteamEngine) {
        this.a = javaSteamEngine;
    }

    @Override // com.codedisaster.steamworks.SteamFriendsCallback
    public void onSetPersonaNameResponse(boolean z, boolean z2, SteamResult steamResult) {
    }

    @Override // com.codedisaster.steamworks.SteamFriendsCallback
    public void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange personaChange) {
    }

    @Override // com.codedisaster.steamworks.SteamFriendsCallback
    public void onGameOverlayActivated(boolean z) {
        GameEngine.isInSpace("onGameOverlayActivated");
    }

    @Override // com.codedisaster.steamworks.SteamFriendsCallback
    public void onGameLobbyJoinRequested(SteamID steamID, SteamID steamID2) {
    }

    @Override // com.codedisaster.steamworks.SteamFriendsCallback
    public void onAvatarImageLoaded(SteamID steamID, int i, int i2, int i3) {
    }

    @Override // com.codedisaster.steamworks.SteamFriendsCallback
    public void onFriendRichPresenceUpdate(SteamID steamID, int i) {
    }

    @Override // com.codedisaster.steamworks.SteamFriendsCallback
    public void onGameRichPresenceJoinRequested(SteamID steamID, String str) {
    }
}
