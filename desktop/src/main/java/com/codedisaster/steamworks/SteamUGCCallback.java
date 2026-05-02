package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGCCallback.class */
public interface SteamUGCCallback {
    void onUGCQueryCompleted(SteamUGCQuery steamUGCQuery, int i, int i2, boolean z, SteamResult steamResult);

    void onSubscribeItem(SteamPublishedFileID steamPublishedFileID, SteamResult steamResult);

    void onUnsubscribeItem(SteamPublishedFileID steamPublishedFileID, SteamResult steamResult);

    void onRequestUGCDetails(SteamUGCDetails steamUGCDetails, SteamResult steamResult);

    void onCreateItem(SteamPublishedFileID steamPublishedFileID, boolean z, SteamResult steamResult);

    void onSubmitItemUpdate(boolean z, SteamResult steamResult);

    void onDownloadItemResult(int i, SteamPublishedFileID steamPublishedFileID, SteamResult steamResult);

    void onUserFavoriteItemsListChanged(SteamPublishedFileID steamPublishedFileID, boolean z, SteamResult steamResult);

    void onSetUserItemVote(SteamPublishedFileID steamPublishedFileID, boolean z, SteamResult steamResult);

    void onGetUserItemVote(SteamPublishedFileID steamPublishedFileID, boolean z, boolean z2, boolean z3, SteamResult steamResult);

    void onStartPlaytimeTracking(SteamResult steamResult);

    void onStopPlaytimeTracking(SteamResult steamResult);

    void onStopPlaytimeTrackingForAllItems(SteamResult steamResult);
}
