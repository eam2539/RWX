package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamMatchmaking;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamMatchmakingCallback.class */
public interface SteamMatchmakingCallback {
    void onFavoritesListChanged(int i, int i2, int i3, int i4, int i5, boolean z, int i6);

    void onLobbyInvite(SteamID steamID, SteamID steamID2, long j);

    void onLobbyEnter(SteamID steamID, int i, boolean z, SteamMatchmaking.ChatRoomEnterResponse chatRoomEnterResponse);

    void onLobbyDataUpdate(SteamID steamID, SteamID steamID2, boolean z);

    void onLobbyChatUpdate(SteamID steamID, SteamID steamID2, SteamID steamID3, SteamMatchmaking.ChatMemberStateChange chatMemberStateChange);

    void onLobbyChatMessage(SteamID steamID, SteamID steamID2, SteamMatchmaking.ChatEntryType chatEntryType, int i);

    void onLobbyGameCreated(SteamID steamID, SteamID steamID2, int i, short s);

    void onLobbyMatchList(int i);

    void onLobbyKicked(SteamID steamID, SteamID steamID2, boolean z);

    void onLobbyCreated(SteamResult steamResult, SteamID steamID);

    void onFavoritesListAccountsUpdated(SteamResult steamResult);
}
