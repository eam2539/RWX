package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGCCallbackAdapter.class */
class SteamUGCCallbackAdapter extends SteamCallbackAdapter {
    SteamUGCCallbackAdapter(SteamUGCCallback steamUGCCallback) {
        super(steamUGCCallback);
    }

    void onUGCQueryCompleted(long j, int i, int i2, boolean z, int i3) {
        ((SteamUGCCallback) this.callback).onUGCQueryCompleted(new SteamUGCQuery(j), i, i2, z, SteamResult.byValue(i3));
    }

    void onSubscribeItem(long j, int i) {
        ((SteamUGCCallback) this.callback).onSubscribeItem(new SteamPublishedFileID(j), SteamResult.byValue(i));
    }

    void onUnsubscribeItem(long j, int i) {
        ((SteamUGCCallback) this.callback).onUnsubscribeItem(new SteamPublishedFileID(j), SteamResult.byValue(i));
    }

    void onRequestUGCDetails(long j, int i, String str, String str2, long j2, long j3, String str3, boolean z, int i2, int i3, long j4, int i4, int i5) {
        SteamUGCDetails steamUGCDetails = new SteamUGCDetails();
        steamUGCDetails.publishedFileID = j;
        steamUGCDetails.result = i;
        steamUGCDetails.title = str;
        steamUGCDetails.description = str2;
        steamUGCDetails.fileHandle = j2;
        steamUGCDetails.previewFileHandle = j3;
        steamUGCDetails.fileName = str3;
        steamUGCDetails.votesUp = i2;
        steamUGCDetails.votesDown = i3;
        steamUGCDetails.ownerID = j4;
        steamUGCDetails.timeCreated = i4;
        steamUGCDetails.timeUpdated = i5;
        ((SteamUGCCallback) this.callback).onRequestUGCDetails(steamUGCDetails, SteamResult.byValue(i));
    }

    void onCreateItem(long j, boolean z, int i) {
        ((SteamUGCCallback) this.callback).onCreateItem(new SteamPublishedFileID(j), z, SteamResult.byValue(i));
    }

    void onSubmitItemUpdate(boolean z, int i) {
        ((SteamUGCCallback) this.callback).onSubmitItemUpdate(z, SteamResult.byValue(i));
    }

    void onDownloadItemResult(int i, long j, int i2) {
        ((SteamUGCCallback) this.callback).onDownloadItemResult(i, new SteamPublishedFileID(j), SteamResult.byValue(i2));
    }

    void onUserFavoriteItemsListChanged(long j, boolean z, int i) {
        ((SteamUGCCallback) this.callback).onUserFavoriteItemsListChanged(new SteamPublishedFileID(j), z, SteamResult.byValue(i));
    }

    void onSetUserItemVote(long j, boolean z, int i) {
        ((SteamUGCCallback) this.callback).onSetUserItemVote(new SteamPublishedFileID(j), z, SteamResult.byValue(i));
    }

    void onGetUserItemVote(long j, boolean z, boolean z2, boolean z3, int i) {
        ((SteamUGCCallback) this.callback).onGetUserItemVote(new SteamPublishedFileID(j), z, z2, z3, SteamResult.byValue(i));
    }

    void onStartPlaytimeTracking(int i) {
        ((SteamUGCCallback) this.callback).onStartPlaytimeTracking(SteamResult.byValue(i));
    }

    void onStopPlaytimeTracking(int i) {
        ((SteamUGCCallback) this.callback).onStopPlaytimeTracking(SteamResult.byValue(i));
    }

    void onStopPlaytimeTrackingForAllItems(int i) {
        ((SteamUGCCallback) this.callback).onStopPlaytimeTrackingForAllItems(SteamResult.byValue(i));
    }
}
