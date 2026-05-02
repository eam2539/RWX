package com.corrodinggames.rts.java.steam;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamResult;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c/e.class */
public class SteamMatchmakingCallbackHandler implements SteamMatchmakingCallback {
    JavaSteamEngine a;

    public SteamMatchmakingCallbackHandler(JavaSteamEngine javaSteamEngine) {
        this.a = javaSteamEngine;
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onFavoritesListChanged(int i, int i2, int i3, int i4, int i5, boolean z, int i6) {
        GameEngine.log("onFavoritesListChanged");
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onLobbyInvite(SteamID steamID, SteamID steamID2, long j) {
        GameEngine.log("onLobbyInvite");
        new SteamGameInviteHandler(this.a, steamID, steamID2, j).a();
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onLobbyEnter(SteamID steamID, int i, boolean z, SteamMatchmaking.ChatRoomEnterResponse chatRoomEnterResponse) {
        GameEngine.log("onLobbyEnter");
        if (z) {
            GameEngine.log("onLobbyEnter blocked: " + chatRoomEnterResponse);
        }
        this.a.c(steamID);
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onLobbyDataUpdate(SteamID steamID, SteamID steamID2, boolean z) {
        GameEngine.log("onLobbyDataUpdate success: " + z);
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onLobbyChatUpdate(SteamID steamID, SteamID steamID2, SteamID steamID3, SteamMatchmaking.ChatMemberStateChange chatMemberStateChange) {
        GameEngine.log("onLobbyChatUpdate steamIDUserChanged: " + steamID2 + " stateChange:" + chatMemberStateChange);
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onLobbyChatMessage(SteamID steamID, SteamID steamID2, SteamMatchmaking.ChatEntryType chatEntryType, int i) {
        GameEngine.log("onLobbyChatMessage");
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onLobbyGameCreated(SteamID steamID, SteamID steamID2, int i, short s) {
        GameEngine.log("onLobbyGameCreated");
        this.a.a(steamID);
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onLobbyMatchList(int i) {
        GameEngine.log("onLobbyMatchList");
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onLobbyKicked(SteamID steamID, SteamID steamID2, boolean z) {
        GameEngine.log("onLobbyKicked");
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onLobbyCreated(SteamResult steamResult, SteamID steamID) {
        GameEngine.log("onLobbyCreated");
        this.a.a(steamID);
    }

    @Override // com.codedisaster.steamworks.SteamMatchmakingCallback
    public void onFavoritesListAccountsUpdated(SteamResult steamResult) {
        GameEngine.log("onFavoritesListAccountsUpdated");
    }
}
