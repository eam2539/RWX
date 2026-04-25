package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamRemoteStorage;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGCDetails.class */
public class SteamUGCDetails {
    long publishedFileID;
    int result;
    int fileType;
    String title;
    String description;
    long ownerID;
    int timeCreated;
    int timeUpdated;
    boolean tagsTruncated;
    String tags;
    long fileHandle;
    long previewFileHandle;
    String fileName;
    int fileSize;
    int previewFileSize;
    String url;
    int votesUp;
    int votesDown;

    public SteamPublishedFileID getPublishedFileID() {
        return new SteamPublishedFileID(this.publishedFileID);
    }

    public SteamResult getResult() {
        return SteamResult.byValue(this.result);
    }

    public SteamRemoteStorage.WorkshopFileType getFileType() {
        return SteamRemoteStorage.WorkshopFileType.byOrdinal(this.fileType);
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public SteamID getOwnerID() {
        return new SteamID(this.ownerID);
    }

    public int getTimeCreated() {
        return this.timeCreated;
    }

    public int getTimeUpdated() {
        return this.timeUpdated;
    }

    public boolean areTagsTruncated() {
        return this.tagsTruncated;
    }

    public String getTags() {
        return this.tags;
    }

    public SteamUGCHandle getFileHandle() {
        return new SteamUGCHandle(this.fileHandle);
    }

    public SteamUGCHandle getPreviewFileHandle() {
        return new SteamUGCHandle(this.previewFileHandle);
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public int getPreviewFileSize() {
        return this.previewFileSize;
    }

    public String getURL() {
        return this.url;
    }

    public int getVotesUp() {
        return this.votesUp;
    }

    public int getVotesDown() {
        return this.votesDown;
    }
}
