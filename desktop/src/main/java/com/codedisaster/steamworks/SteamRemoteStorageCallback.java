package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamRemoteStorageCallback.class */
public interface SteamRemoteStorageCallback {
    void onFileShareResult(SteamUGCHandle steamUGCHandle, String str, SteamResult steamResult);

    void onDownloadUGCResult(SteamUGCHandle steamUGCHandle, SteamResult steamResult);

    void onPublishFileResult(SteamPublishedFileID steamPublishedFileID, boolean z, SteamResult steamResult);

    void onUpdatePublishedFileResult(SteamPublishedFileID steamPublishedFileID, boolean z, SteamResult steamResult);

    void onPublishedFileSubscribed(SteamPublishedFileID steamPublishedFileID, int i);

    void onPublishedFileUnsubscribed(SteamPublishedFileID steamPublishedFileID, int i);

    void onPublishedFileDeleted(SteamPublishedFileID steamPublishedFileID, int i);

    void onFileWriteAsyncComplete(SteamResult steamResult);

    void onFileReadAsyncComplete(SteamAPICall steamAPICall, SteamResult steamResult, int i, int i2);
}
