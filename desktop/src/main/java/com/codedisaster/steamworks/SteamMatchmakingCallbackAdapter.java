package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamMatchmaking;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmakingCallbackAdapter.class */
class SteamMatchmakingCallbackAdapter extends SteamCallbackAdapter {
    private static final SteamMatchmaking.ChatMemberStateChange[] stateChangeValues = SteamMatchmaking.ChatMemberStateChange.values();

    SteamMatchmakingCallbackAdapter(SteamMatchmakingCallback steamMatchmakingCallback) {
        super(steamMatchmakingCallback);
    }

    void onFavoritesListChanged(int i, int i2, int i3, int i4, int i5, boolean z, int i6) {
        ((SteamMatchmakingCallback) this.callback).onFavoritesListChanged(i, i2, i3, i4, i5, z, i6);
    }

    void onLobbyInvite(long j, long j2, long j3) {
        ((SteamMatchmakingCallback) this.callback).onLobbyInvite(new SteamID(j), new SteamID(j2), j3);
    }

    void onLobbyEnter(long j, int i, boolean z, int i2) {
        ((SteamMatchmakingCallback) this.callback).onLobbyEnter(new SteamID(j), i, z, SteamMatchmaking.ChatRoomEnterResponse.byCode(i2));
    }

    void onLobbyDataUpdate(long j, long j2, boolean z) {
        ((SteamMatchmakingCallback) this.callback).onLobbyDataUpdate(new SteamID(j), new SteamID(j2), z);
    }

    void onLobbyChatUpdate(long j, long j2, long j3, int i) {
        SteamID steamID = new SteamID(j);
        SteamID steamID2 = new SteamID(j2);
        SteamID steamID3 = new SteamID(j3);
        for (SteamMatchmaking.ChatMemberStateChange chatMemberStateChange : stateChangeValues) {
            if (SteamMatchmaking.ChatMemberStateChange.isSet(chatMemberStateChange, i)) {
                ((SteamMatchmakingCallback) this.callback).onLobbyChatUpdate(steamID, steamID2, steamID3, chatMemberStateChange);
            }
        }
    }

    void onLobbyChatMessage(long j, long j2, int i, int i2) {
        ((SteamMatchmakingCallback) this.callback).onLobbyChatMessage(new SteamID(j), new SteamID(j2), SteamMatchmaking.ChatEntryType.byCode(i), i2);
    }

    void onLobbyGameCreated(long j, long j2, int i, short s) {
        ((SteamMatchmakingCallback) this.callback).onLobbyGameCreated(new SteamID(j), new SteamID(j2), i, s);
    }

    void onLobbyMatchList(int i) {
        ((SteamMatchmakingCallback) this.callback).onLobbyMatchList(i);
    }

    void onLobbyKicked(long j, long j2, boolean z) {
        ((SteamMatchmakingCallback) this.callback).onLobbyKicked(new SteamID(j), new SteamID(j2), z);
    }

    void onLobbyCreated(int i, long j) {
        ((SteamMatchmakingCallback) this.callback).onLobbyCreated(SteamResult.byValue(i), new SteamID(j));
    }

    void onFavoritesListAccountsUpdated(int i) {
        ((SteamMatchmakingCallback) this.callback).onFavoritesListAccountsUpdated(SteamResult.byValue(i));
    }
}
