package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamRemoteStorageCallbackAdapter.class */
class SteamRemoteStorageCallbackAdapter extends SteamCallbackAdapter {
    SteamRemoteStorageCallbackAdapter(SteamRemoteStorageCallback steamRemoteStorageCallback) {
        super(steamRemoteStorageCallback);
    }

    void onFileShareResult(long j, String str, int i) {
        ((SteamRemoteStorageCallback) this.callback).onFileShareResult(new SteamUGCHandle(j), str, SteamResult.byValue(i));
    }

    void onDownloadUGCResult(long j, int i) {
        ((SteamRemoteStorageCallback) this.callback).onDownloadUGCResult(new SteamUGCHandle(j), SteamResult.byValue(i));
    }

    void onPublishFileResult(long j, boolean z, int i) {
        ((SteamRemoteStorageCallback) this.callback).onPublishFileResult(new SteamPublishedFileID(j), z, SteamResult.byValue(i));
    }

    void onUpdatePublishedFileResult(long j, boolean z, int i) {
        ((SteamRemoteStorageCallback) this.callback).onUpdatePublishedFileResult(new SteamPublishedFileID(j), z, SteamResult.byValue(i));
    }

    void onPublishedFileSubscribed(long j, int i) {
        ((SteamRemoteStorageCallback) this.callback).onPublishedFileSubscribed(new SteamPublishedFileID(j), i);
    }

    void onPublishedFileUnsubscribed(long j, int i) {
        ((SteamRemoteStorageCallback) this.callback).onPublishedFileUnsubscribed(new SteamPublishedFileID(j), i);
    }

    void onPublishedFileDeleted(long j, int i) {
        ((SteamRemoteStorageCallback) this.callback).onPublishedFileDeleted(new SteamPublishedFileID(j), i);
    }

    void onFileWriteAsyncComplete(int i) {
        ((SteamRemoteStorageCallback) this.callback).onFileWriteAsyncComplete(SteamResult.byValue(i));
    }

    void onFileReadAsyncComplete(long j, int i, int i2, int i3) {
        ((SteamRemoteStorageCallback) this.callback).onFileReadAsyncComplete(new SteamAPICall(j), SteamResult.byValue(i), i2, i3);
    }
}
