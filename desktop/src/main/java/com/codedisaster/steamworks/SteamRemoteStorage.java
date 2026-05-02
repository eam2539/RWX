package com.codedisaster.steamworks;

import java.nio.ByteBuffer;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamRemoteStorage.class */
public class SteamRemoteStorage extends SteamInterface {

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamRemoteStorage$PublishedFileVisibility.class */
    public enum PublishedFileVisibility {
        Public,
        FriendsOnly,
        Private
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamRemoteStorage$UGCReadAction.class */
    public enum UGCReadAction {
        ContinueReadingUntilFinished,
        ContinueReading,
        Close
    }

    private static native long createCallback(SteamRemoteStorageCallbackAdapter steamRemoteStorageCallbackAdapter);

    private static native boolean fileWrite(long j, String str, ByteBuffer byteBuffer, int i);

    private static native boolean fileRead(long j, String str, ByteBuffer byteBuffer, int i);

    private static native long fileWriteAsync(long j, long j2, String str, ByteBuffer byteBuffer, int i);

    private static native long fileReadAsync(long j, long j2, String str, int i, int i2);

    private static native boolean fileReadAsyncComplete(long j, long j2, ByteBuffer byteBuffer, int i);

    private static native boolean fileForget(long j, String str);

    private static native boolean fileDelete(long j, String str);

    private static native long fileShare(long j, long j2, String str);

    private static native boolean setSyncPlatforms(long j, String str, int i);

    private static native long fileWriteStreamOpen(long j, String str);

    private static native boolean fileWriteStreamWriteChunk(long j, long j2, ByteBuffer byteBuffer, int i);

    private static native boolean fileWriteStreamClose(long j, long j2);

    private static native boolean fileWriteStreamCancel(long j, long j2);

    private static native boolean fileExists(long j, String str);

    private static native boolean filePersisted(long j, String str);

    private static native int getFileSize(long j, String str);

    private static native long getFileTimestamp(long j, String str);

    private static native int getSyncPlatforms(long j, String str);

    private static native int getFileCount(long j);

    private static native String getFileNameAndSize(long j, int i, int[] iArr);

    private static native boolean getQuota(long j, long[] jArr, long[] jArr2);

    private static native boolean isCloudEnabledForAccount(long j);

    private static native boolean isCloudEnabledForApp(long j);

    private static native void setCloudEnabledForApp(long j, boolean z);

    private static native long ugcDownload(long j, long j2, long j3, int i);

    private static native boolean getUGCDownloadProgress(long j, long j2, int[] iArr, int[] iArr2);

    private static native int ugcRead(long j, long j2, ByteBuffer byteBuffer, int i, int i2, int i3);

    private static native int getCachedUGCCount(long j);

    private static native long getCachedUGCHandle(long j, int i);

    private static native long publishWorkshopFile(long j, long j2, String str, String str2, int i, String str3, String str4, int i2, String[] strArr, int i3, int i4);

    private static native long createPublishedFileUpdateRequest(long j, long j2);

    private static native boolean updatePublishedFileFile(long j, long j2, String str);

    private static native boolean updatePublishedFilePreviewFile(long j, long j2, String str);

    private static native boolean updatePublishedFileTitle(long j, long j2, String str);

    private static native boolean updatePublishedFileDescription(long j, long j2, String str);

    private static native boolean updatePublishedFileVisibility(long j, long j2, int i);

    private static native boolean updatePublishedFileTags(long j, long j2, String[] strArr, int i);

    private static native long commitPublishedFileUpdate(long j, long j2, long j3);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamRemoteStorage$RemoteStoragePlatform.class */
    public enum RemoteStoragePlatform {
        None(0),
        Windows(1),
        OSX(2),
        PS3(4),
        Linux(8),
        Reserved2(16),
        All(-1);

        private final int mask;
        private static final RemoteStoragePlatform[] values = values();

        RemoteStoragePlatform(int i) {
            this.mask = i;
        }

        static RemoteStoragePlatform[] byMask(int i) {
            RemoteStoragePlatform[] remoteStoragePlatformArr = new RemoteStoragePlatform[Integer.bitCount(i)];
            int i2 = 0;
            for (RemoteStoragePlatform remoteStoragePlatform : values) {
                if ((remoteStoragePlatform.mask & i) != 0) {
                    int i3 = i2;
                    i2++;
                    remoteStoragePlatformArr[i3] = remoteStoragePlatform;
                }
            }
            return remoteStoragePlatformArr;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamRemoteStorage$WorkshopFileType.class */
    public enum WorkshopFileType {
        Community,
        Microtransaction,
        Collection,
        Art,
        Video,
        Screenshot,
        Game,
        Software,
        Concept,
        WebGuide,
        IntegratedGuide,
        Merch,
        ControllerBinding,
        SteamworksAccessInvite,
        SteamVideo,
        GameManagedItem;

        private static final WorkshopFileType[] values = values();

        static WorkshopFileType byOrdinal(int i) {
            return values[i];
        }
    }

    public SteamRemoteStorage(SteamRemoteStorageCallback steamRemoteStorageCallback) {
        super(SteamAPI.getSteamRemoteStoragePointer(), createCallback(new SteamRemoteStorageCallbackAdapter(steamRemoteStorageCallback)));
    }

    public boolean fileWrite(String str, ByteBuffer byteBuffer, int i) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        return fileWrite(this.pointer, str, byteBuffer, i);
    }

    public boolean fileRead(String str, ByteBuffer byteBuffer, int i) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        return fileRead(this.pointer, str, byteBuffer, i);
    }

    public SteamAPICall fileWriteAsync(String str, ByteBuffer byteBuffer) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        return new SteamAPICall(fileWriteAsync(this.pointer, this.callback, str, byteBuffer, byteBuffer.remaining()));
    }

    public SteamAPICall fileReadAsync(String str, int i, int i2) {
        return new SteamAPICall(fileReadAsync(this.pointer, this.callback, str, i, i2));
    }

    public boolean fileReadAsyncComplete(SteamAPICall steamAPICall, ByteBuffer byteBuffer, int i) {
        return fileReadAsyncComplete(this.pointer, steamAPICall.handle, byteBuffer, i);
    }

    public boolean fileForget(String str) {
        return fileForget(this.pointer, str);
    }

    public boolean fileDelete(String str) {
        return fileDelete(this.pointer, str);
    }

    public SteamAPICall fileShare(String str) {
        return new SteamAPICall(fileShare(this.pointer, this.callback, str));
    }

    public boolean setSyncPlatforms(String str, RemoteStoragePlatform remoteStoragePlatform) {
        return setSyncPlatforms(this.pointer, str, remoteStoragePlatform.mask);
    }

    public SteamUGCFileWriteStreamHandle fileWriteStreamOpen(String str) {
        return new SteamUGCFileWriteStreamHandle(fileWriteStreamOpen(this.pointer, str));
    }

    public boolean fileWriteStreamWriteChunk(SteamUGCFileWriteStreamHandle steamUGCFileWriteStreamHandle, ByteBuffer byteBuffer, int i) {
        return fileWriteStreamWriteChunk(this.pointer, steamUGCFileWriteStreamHandle.handle, byteBuffer, i);
    }

    public boolean fileWriteStreamClose(SteamUGCFileWriteStreamHandle steamUGCFileWriteStreamHandle) {
        return fileWriteStreamClose(this.pointer, steamUGCFileWriteStreamHandle.handle);
    }

    public boolean fileWriteStreamCancel(SteamUGCFileWriteStreamHandle steamUGCFileWriteStreamHandle) {
        return fileWriteStreamCancel(this.pointer, steamUGCFileWriteStreamHandle.handle);
    }

    public boolean fileExists(String str) {
        return fileExists(this.pointer, str);
    }

    public boolean filePersisted(String str) {
        return filePersisted(this.pointer, str);
    }

    public int getFileSize(String str) {
        return getFileSize(this.pointer, str);
    }

    public long getFileTimestamp(String str) {
        return getFileTimestamp(this.pointer, str);
    }

    public RemoteStoragePlatform[] getSyncPlatforms(String str) {
        return RemoteStoragePlatform.byMask(getSyncPlatforms(this.pointer, str));
    }

    public int getFileCount() {
        return getFileCount(this.pointer);
    }

    public String getFileNameAndSize(int i, int[] iArr) {
        return getFileNameAndSize(this.pointer, i, iArr);
    }

    public boolean getQuota(long[] jArr, long[] jArr2) {
        return getQuota(this.pointer, jArr, jArr2);
    }

    public boolean isCloudEnabledForAccount() {
        return isCloudEnabledForAccount(this.pointer);
    }

    public boolean isCloudEnabledForApp() {
        return isCloudEnabledForApp(this.pointer);
    }

    public void setCloudEnabledForApp(boolean z) {
        setCloudEnabledForApp(this.pointer, z);
    }

    public SteamAPICall ugcDownload(SteamUGCHandle steamUGCHandle, int i) {
        return new SteamAPICall(ugcDownload(this.pointer, this.callback, steamUGCHandle.handle, i));
    }

    public boolean getUGCDownloadProgress(SteamUGCHandle steamUGCHandle, int[] iArr, int[] iArr2) {
        return getUGCDownloadProgress(this.pointer, steamUGCHandle.handle, iArr, iArr2);
    }

    public int ugcRead(SteamUGCHandle steamUGCHandle, ByteBuffer byteBuffer, int i, int i2, UGCReadAction uGCReadAction) {
        return ugcRead(this.pointer, steamUGCHandle.handle, byteBuffer, i, i2, uGCReadAction.ordinal());
    }

    public int getCachedUGCCount() {
        return getCachedUGCCount(this.pointer);
    }

    public SteamUGCHandle getCachedUGCHandle(int i) {
        return new SteamUGCHandle(getCachedUGCHandle(this.pointer, i));
    }

    public SteamAPICall publishWorkshopFile(String str, String str2, int i, String str3, String str4, PublishedFileVisibility publishedFileVisibility, String[] strArr, WorkshopFileType workshopFileType) {
        return new SteamAPICall(publishWorkshopFile(this.pointer, this.callback, str, str2, i, str3, str4, publishedFileVisibility.ordinal(), strArr, strArr != null ? strArr.length : 0, workshopFileType.ordinal()));
    }

    public SteamPublishedFileUpdateHandle createPublishedFileUpdateRequest(SteamPublishedFileID steamPublishedFileID) {
        return new SteamPublishedFileUpdateHandle(createPublishedFileUpdateRequest(this.pointer, steamPublishedFileID.handle));
    }

    public boolean updatePublishedFileFile(SteamPublishedFileUpdateHandle steamPublishedFileUpdateHandle, String str) {
        return updatePublishedFileFile(this.pointer, steamPublishedFileUpdateHandle.handle, str);
    }

    public boolean updatePublishedFilePreviewFile(SteamPublishedFileUpdateHandle steamPublishedFileUpdateHandle, String str) {
        return updatePublishedFilePreviewFile(this.pointer, steamPublishedFileUpdateHandle.handle, str);
    }

    public boolean updatePublishedFileTitle(SteamPublishedFileUpdateHandle steamPublishedFileUpdateHandle, String str) {
        return updatePublishedFileTitle(this.pointer, steamPublishedFileUpdateHandle.handle, str);
    }

    public boolean updatePublishedFileDescription(SteamPublishedFileUpdateHandle steamPublishedFileUpdateHandle, String str) {
        return updatePublishedFileDescription(this.pointer, steamPublishedFileUpdateHandle.handle, str);
    }

    public boolean updatePublishedFileVisibility(SteamPublishedFileUpdateHandle steamPublishedFileUpdateHandle, PublishedFileVisibility publishedFileVisibility) {
        return updatePublishedFileVisibility(this.pointer, steamPublishedFileUpdateHandle.handle, publishedFileVisibility.ordinal());
    }

    public boolean updatePublishedFileTags(SteamPublishedFileUpdateHandle steamPublishedFileUpdateHandle, String[] strArr) {
        return updatePublishedFileTags(this.pointer, steamPublishedFileUpdateHandle.handle, strArr, strArr != null ? strArr.length : 0);
    }

    public SteamAPICall commitPublishedFileUpdate(SteamPublishedFileUpdateHandle steamPublishedFileUpdateHandle) {
        return new SteamAPICall(commitPublishedFileUpdate(this.pointer, this.callback, steamPublishedFileUpdateHandle.handle));
    }
}
