package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamRemoteStorage;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC.class */
public class SteamUGC extends SteamInterface {

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$ItemStatistic.class */
    public enum ItemStatistic {
        NumSubscriptions,
        NumFavorites,
        NumFollowers,
        NumUniqueSubscriptions,
        NumUniqueFavorites,
        NumUniqueFollowers,
        NumUniqueWebsiteViews,
        ReportScore,
        NumSecondsPlayed,
        NumPlaytimeSessions,
        NumComments
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$UGCQueryType.class */
    public enum UGCQueryType {
        RankedByVote,
        RankedByPublicationDate,
        AcceptedForGameRankedByAcceptanceDate,
        RankedByTrend,
        FavoritedByFriendsRankedByPublicationDate,
        CreatedByFriendsRankedByPublicationDate,
        RankedByNumTimesReported,
        CreatedByFollowedUsersRankedByPublicationDate,
        NotYetRated,
        RankedByTotalVotesAsc,
        RankedByVotesUp,
        RankedByTextSearch,
        RankedByTotalUniqueSubscriptions,
        RankedByPlaytimeTrend,
        RankedByTotalPlaytime,
        RankedByAveragePlaytimeTrend,
        RankedByLifetimeAveragePlaytime,
        RankedByPlaytimeSessionsTrend,
        RankedByLifetimePlaytimeSessions
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$UserUGCList.class */
    public enum UserUGCList {
        Published,
        VotedOn,
        VotedUp,
        VotedDown,
        WillVoteLater,
        Favorited,
        Subscribed,
        UsedOrPlayed,
        Followed
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$UserUGCListSortOrder.class */
    public enum UserUGCListSortOrder {
        CreationOrderDesc,
        CreationOrderAsc,
        TitleAsc,
        LastUpdatedDesc,
        SubscriptionDateDesc,
        VoteScoreDesc,
        ForModeration
    }

    private static native long createCallback(SteamUGCCallbackAdapter steamUGCCallbackAdapter);

    private static native long createQueryUserUGCRequest(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6);

    private static native long createQueryAllUGCRequest(long j, int i, int i2, int i3, int i4, int i5);

    private static native long createQueryUGCDetailsRequest(long j, long[] jArr, int i);

    private static native long sendQueryUGCRequest(long j, long j2, long j3);

    private static native boolean getQueryUGCResult(long j, long j2, int i, SteamUGCDetails steamUGCDetails);

    private static native String getQueryUGCPreviewURL(long j, long j2, int i);

    private static native String getQueryUGCMetadata(long j, long j2, int i);

    private static native long getQueryUGCStatistic(long j, long j2, int i, int i2);

    private static native int getQueryUGCNumAdditionalPreviews(long j, long j2, int i);

    private static native boolean getQueryUGCAdditionalPreview(long j, long j2, int i, int i2, ItemAdditionalPreview itemAdditionalPreview);

    private static native int getQueryUGCNumKeyValueTags(long j, long j2, int i);

    private static native boolean getQueryUGCKeyValueTag(long j, long j2, int i, int i2, String[] strArr);

    private static native boolean releaseQueryUserUGCRequest(long j, long j2);

    private static native boolean addRequiredTag(long j, long j2, String str);

    private static native boolean addExcludedTag(long j, long j2, String str);

    private static native boolean setReturnOnlyIDs(long j, long j2, boolean z);

    private static native boolean setReturnKeyValueTags(long j, long j2, boolean z);

    private static native boolean setReturnLongDescription(long j, long j2, boolean z);

    private static native boolean setReturnMetadata(long j, long j2, boolean z);

    private static native boolean setReturnChildren(long j, long j2, boolean z);

    private static native boolean setReturnAdditionalPreviews(long j, long j2, boolean z);

    private static native boolean setReturnTotalOnly(long j, long j2, boolean z);

    private static native boolean setLanguage(long j, long j2, String str);

    private static native boolean setAllowCachedResponse(long j, long j2, int i);

    private static native boolean setCloudFileNameFilter(long j, long j2, String str);

    private static native boolean setMatchAnyTag(long j, long j2, boolean z);

    private static native boolean setSearchText(long j, long j2, String str);

    private static native boolean setRankedByTrendDays(long j, long j2, int i);

    private static native boolean addRequiredKeyValueTag(long j, long j2, String str, String str2);

    private static native long requestUGCDetails(long j, long j2, long j3, int i);

    private static native long createItem(long j, long j2, int i, int i2);

    private static native long startItemUpdate(long j, int i, long j2);

    private static native boolean setItemTitle(long j, long j2, String str);

    private static native boolean setItemDescription(long j, long j2, String str);

    private static native boolean setItemUpdateLanguage(long j, long j2, String str);

    private static native boolean setItemMetadata(long j, long j2, String str);

    private static native boolean setItemVisibility(long j, long j2, int i);

    private static native boolean setItemTags(long j, long j2, String[] strArr, int i);

    private static native boolean setItemContent(long j, long j2, String str);

    private static native boolean setItemPreview(long j, long j2, String str);

    private static native boolean removeItemKeyValueTags(long j, long j2, String str);

    private static native boolean addItemKeyValueTag(long j, long j2, String str, String str2);

    private static native long submitItemUpdate(long j, long j2, long j3, String str);

    private static native int getItemUpdateProgress(long j, long j2, long[] jArr);

    private static native long setUserItemVote(long j, long j2, long j3, boolean z);

    private static native long getUserItemVote(long j, long j2, long j3);

    private static native long addItemToFavorites(long j, long j2, int i, long j3);

    private static native long removeItemFromFavorites(long j, long j2, int i, long j3);

    private static native long subscribeItem(long j, long j2, long j3);

    private static native long unsubscribeItem(long j, long j2, long j3);

    private static native int getNumSubscribedItems(long j);

    private static native int getSubscribedItems(long j, long[] jArr, int i);

    private static native int getItemState(long j, long j2);

    private static native boolean getItemInstallInfo(long j, long j2, ItemInstallInfo itemInstallInfo);

    private static native boolean getItemDownloadInfo(long j, long j2, long[] jArr);

    private static native boolean downloadItem(long j, long j2, boolean z);

    private static native boolean initWorkshopForGameServer(long j, int i, String str);

    private static native void suspendDownloads(long j, boolean z);

    private static native long startPlaytimeTracking(long j, long j2, long[] jArr, int i);

    private static native long stopPlaytimeTracking(long j, long j2, long[] jArr, int i);

    private static native long stopPlaytimeTrackingForAllItems(long j, long j2);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$MatchingUGCType.class */
    public enum MatchingUGCType {
        Items(0),
        ItemsMtx(1),
        ItemsReadyToUse(2),
        Collections(3),
        Artwork(4),
        Videos(5),
        Screenshots(6),
        AllGuides(7),
        WebGuides(8),
        IntegratedGuides(9),
        UsableInGame(10),
        ControllerBindings(11),
        GameManagedItems(12),
        All(-1);

        private final int value;

        MatchingUGCType(int i) {
            this.value = i;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$ItemUpdateStatus.class */
    public enum ItemUpdateStatus {
        Invalid,
        PreparingConfig,
        PreparingContent,
        UploadingContent,
        UploadingPreviewFile,
        CommittingChanges;

        private static final ItemUpdateStatus[] values = values();

        static ItemUpdateStatus byOrdinal(int i) {
            return values[i];
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$ItemUpdateInfo.class */
    public class ItemUpdateInfo {
        long bytesProcessed;
        long bytesTotal;

        public long getBytesProcessed() {
            return this.bytesProcessed;
        }

        public long getBytesTotal() {
            return this.bytesTotal;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$ItemState.class */
    public enum ItemState {
        None(0),
        Subscribed(1),
        LegacyItem(2),
        Installed(4),
        NeedsUpdate(8),
        Downloading(16),
        DownloadPending(32);

        private final int bits;
        private static final ItemState[] values = values();

        ItemState(int i) {
            this.bits = i;
        }

        static Collection fromBits(int i) {
            EnumSet enumSetNoneOf = EnumSet.noneOf(ItemState.class);
            for (ItemState itemState : values) {
                if ((i & itemState.bits) == itemState.bits) {
                    enumSetNoneOf.add(itemState);
                }
            }
            return enumSetNoneOf;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$ItemPreviewType.class */
    public enum ItemPreviewType {
        Image(0),
        YouTubeVideo(1),
        Sketchfab(2),
        EnvironmentMap_HorizontalCross(3),
        EnvironmentMap_LatLong(4),
        ReservedMax(255),
        UnknownPreviewType_NotImplementedByAPI(-1);

        private final int value;
        private static final ItemPreviewType[] values = values();

        ItemPreviewType(int i) {
            this.value = i;
        }

        static ItemPreviewType byValue(int i) {
            for (ItemPreviewType itemPreviewType : values) {
                if (itemPreviewType.value == i) {
                    return itemPreviewType;
                }
            }
            return UnknownPreviewType_NotImplementedByAPI;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$ItemInstallInfo.class */
    public static class ItemInstallInfo {
        private String folder;
        private int sizeOnDisk;

        public String getFolder() {
            return this.folder;
        }

        public int getSizeOnDisk() {
            return this.sizeOnDisk;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$ItemDownloadInfo.class */
    public static class ItemDownloadInfo {
        long bytesDownloaded;
        long bytesTotal;

        public long getBytesDownloaded() {
            return this.bytesDownloaded;
        }

        public long getBytesTotal() {
            return this.bytesTotal;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUGC$ItemAdditionalPreview.class */
    public class ItemAdditionalPreview {
        private String urlOrVideoID;
        private String originalFileName;
        private int previewType;

        public String getUrlOrVideoID() {
            return this.urlOrVideoID;
        }

        public String getOriginalFileName() {
            return this.originalFileName;
        }

        public ItemPreviewType getPreviewType() {
            return ItemPreviewType.byValue(this.previewType);
        }
    }

    public SteamUGC(SteamUGCCallback steamUGCCallback) {
        super(SteamAPI.getSteamUGCPointer(), createCallback(new SteamUGCCallbackAdapter(steamUGCCallback)));
    }

    public SteamUGCQuery createQueryUserUGCRequest(long j, UserUGCList userUGCList, MatchingUGCType matchingUGCType, UserUGCListSortOrder userUGCListSortOrder, int i, int i2, int i3) {
        return new SteamUGCQuery(createQueryUserUGCRequest(this.pointer, j, userUGCList.ordinal(), matchingUGCType.value, userUGCListSortOrder.ordinal(), i, i2, i3));
    }

    public SteamUGCQuery createQueryAllUGCRequest(UGCQueryType uGCQueryType, MatchingUGCType matchingUGCType, int i, int i2, int i3) {
        return new SteamUGCQuery(createQueryAllUGCRequest(this.pointer, uGCQueryType.ordinal(), matchingUGCType.value, i, i2, i3));
    }

    public SteamUGCQuery createQueryUGCDetailsRequest(SteamPublishedFileID steamPublishedFileID) {
        return new SteamUGCQuery(createQueryUGCDetailsRequest(this.pointer, new long[]{steamPublishedFileID.handle}, 1));
    }

    public SteamUGCQuery createQueryUGCDetailsRequest(Collection collection) {
        int size = collection.size();
        long[] jArr = new long[size];
        int i = 0;
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            int i2 = i;
            i++;
            jArr[i2] = ((SteamPublishedFileID) it.next()).handle;
        }
        return new SteamUGCQuery(createQueryUGCDetailsRequest(this.pointer, jArr, size));
    }

    public SteamAPICall sendQueryUGCRequest(SteamUGCQuery steamUGCQuery) {
        return new SteamAPICall(sendQueryUGCRequest(this.pointer, this.callback, steamUGCQuery.handle));
    }

    public boolean getQueryUGCResult(SteamUGCQuery steamUGCQuery, int i, SteamUGCDetails steamUGCDetails) {
        return getQueryUGCResult(this.pointer, steamUGCQuery.handle, i, steamUGCDetails);
    }

    public String getQueryUGCPreviewURL(SteamUGCQuery steamUGCQuery, int i) {
        return getQueryUGCPreviewURL(this.pointer, steamUGCQuery.handle, i);
    }

    public String getQueryUGCMetadata(SteamUGCQuery steamUGCQuery, int i) {
        return getQueryUGCMetadata(this.pointer, steamUGCQuery.handle, i);
    }

    public long getQueryUGCStatistic(SteamUGCQuery steamUGCQuery, int i, ItemStatistic itemStatistic) {
        return getQueryUGCStatistic(this.pointer, steamUGCQuery.handle, i, itemStatistic.ordinal());
    }

    public int getQueryUGCNumAdditionalPreviews(SteamUGCQuery steamUGCQuery, int i) {
        return getQueryUGCNumAdditionalPreviews(this.pointer, steamUGCQuery.handle, i);
    }

    public boolean getQueryUGCAdditionalPreview(SteamUGCQuery steamUGCQuery, int i, int i2, ItemAdditionalPreview itemAdditionalPreview) {
        return getQueryUGCAdditionalPreview(this.pointer, steamUGCQuery.handle, i, i2, itemAdditionalPreview);
    }

    public int getQueryUGCNumKeyValueTags(SteamUGCQuery steamUGCQuery, int i) {
        return getQueryUGCNumKeyValueTags(this.pointer, steamUGCQuery.handle, i);
    }

    public boolean getQueryUGCKeyValueTag(SteamUGCQuery steamUGCQuery, int i, int i2, String[] strArr) {
        return getQueryUGCKeyValueTag(this.pointer, steamUGCQuery.handle, i, i2, strArr);
    }

    public boolean releaseQueryUserUGCRequest(SteamUGCQuery steamUGCQuery) {
        return releaseQueryUserUGCRequest(this.pointer, steamUGCQuery.handle);
    }

    public boolean addRequiredTag(SteamUGCQuery steamUGCQuery, String str) {
        return addRequiredTag(this.pointer, steamUGCQuery.handle, str);
    }

    public boolean addExcludedTag(SteamUGCQuery steamUGCQuery, String str) {
        return addExcludedTag(this.pointer, steamUGCQuery.handle, str);
    }

    public boolean setReturnOnlyIDs(SteamUGCQuery steamUGCQuery, boolean z) {
        return setReturnOnlyIDs(this.pointer, steamUGCQuery.handle, z);
    }

    public boolean setReturnKeyValueTags(SteamUGCQuery steamUGCQuery, boolean z) {
        return setReturnKeyValueTags(this.pointer, steamUGCQuery.handle, z);
    }

    public boolean setReturnLongDescription(SteamUGCQuery steamUGCQuery, boolean z) {
        return setReturnLongDescription(this.pointer, steamUGCQuery.handle, z);
    }

    public boolean setReturnMetadata(SteamUGCQuery steamUGCQuery, boolean z) {
        return setReturnMetadata(this.pointer, steamUGCQuery.handle, z);
    }

    public boolean setReturnChildren(SteamUGCQuery steamUGCQuery, boolean z) {
        return setReturnChildren(this.pointer, steamUGCQuery.handle, z);
    }

    public boolean setReturnAdditionalPreviews(SteamUGCQuery steamUGCQuery, boolean z) {
        return setReturnAdditionalPreviews(this.pointer, steamUGCQuery.handle, z);
    }

    public boolean setReturnTotalOnly(SteamUGCQuery steamUGCQuery, boolean z) {
        return setReturnTotalOnly(this.pointer, steamUGCQuery.handle, z);
    }

    public boolean setLanguage(SteamUGCQuery steamUGCQuery, String str) {
        return setLanguage(this.pointer, steamUGCQuery.handle, str);
    }

    public boolean setAllowCachedResponse(SteamUGCQuery steamUGCQuery, int i) {
        return setAllowCachedResponse(this.pointer, steamUGCQuery.handle, i);
    }

    public boolean setCloudFileNameFilter(SteamUGCQuery steamUGCQuery, String str) {
        return setCloudFileNameFilter(this.pointer, steamUGCQuery.handle, str);
    }

    public boolean setMatchAnyTag(SteamUGCQuery steamUGCQuery, boolean z) {
        return setMatchAnyTag(this.pointer, steamUGCQuery.handle, z);
    }

    public boolean setSearchText(SteamUGCQuery steamUGCQuery, String str) {
        return setSearchText(this.pointer, steamUGCQuery.handle, str);
    }

    public boolean setRankedByTrendDays(SteamUGCQuery steamUGCQuery, int i) {
        return setRankedByTrendDays(this.pointer, steamUGCQuery.handle, i);
    }

    public boolean addRequiredKeyValueTag(SteamUGCQuery steamUGCQuery, String str, String str2) {
        return addRequiredKeyValueTag(this.pointer, steamUGCQuery.handle, str, str2);
    }

    @Deprecated
    public SteamAPICall requestUGCDetails(SteamPublishedFileID steamPublishedFileID, int i) {
        return new SteamAPICall(requestUGCDetails(this.pointer, this.callback, steamPublishedFileID.handle, i));
    }

    public SteamAPICall createItem(int i, SteamRemoteStorage.WorkshopFileType workshopFileType) {
        return new SteamAPICall(createItem(this.pointer, this.callback, i, workshopFileType.ordinal()));
    }

    public SteamUGCUpdateHandle startItemUpdate(int i, SteamPublishedFileID steamPublishedFileID) {
        return new SteamUGCUpdateHandle(startItemUpdate(this.pointer, i, steamPublishedFileID.handle));
    }

    public boolean setItemTitle(SteamUGCUpdateHandle steamUGCUpdateHandle, String str) {
        return setItemTitle(this.pointer, steamUGCUpdateHandle.handle, str);
    }

    public boolean setItemDescription(SteamUGCUpdateHandle steamUGCUpdateHandle, String str) {
        return setItemDescription(this.pointer, steamUGCUpdateHandle.handle, str);
    }

    public boolean setItemUpdateLanguage(SteamUGCUpdateHandle steamUGCUpdateHandle, String str) {
        return setItemUpdateLanguage(this.pointer, steamUGCUpdateHandle.handle, str);
    }

    public boolean setItemMetadata(SteamUGCUpdateHandle steamUGCUpdateHandle, String str) {
        return setItemMetadata(this.pointer, steamUGCUpdateHandle.handle, str);
    }

    public boolean setItemVisibility(SteamUGCUpdateHandle steamUGCUpdateHandle, SteamRemoteStorage.PublishedFileVisibility publishedFileVisibility) {
        return setItemVisibility(this.pointer, steamUGCUpdateHandle.handle, publishedFileVisibility.ordinal());
    }

    public boolean setItemTags(SteamUGCUpdateHandle steamUGCUpdateHandle, String[] strArr) {
        return setItemTags(this.pointer, steamUGCUpdateHandle.handle, strArr, strArr.length);
    }

    public boolean setItemContent(SteamUGCUpdateHandle steamUGCUpdateHandle, String str) {
        return setItemContent(this.pointer, steamUGCUpdateHandle.handle, str);
    }

    public boolean setItemPreview(SteamUGCUpdateHandle steamUGCUpdateHandle, String str) {
        return setItemPreview(this.pointer, steamUGCUpdateHandle.handle, str);
    }

    public boolean removeItemKeyValueTags(SteamUGCUpdateHandle steamUGCUpdateHandle, String str) {
        return removeItemKeyValueTags(this.pointer, steamUGCUpdateHandle.handle, str);
    }

    public boolean addItemKeyValueTag(SteamUGCUpdateHandle steamUGCUpdateHandle, String str, String str2) {
        return addItemKeyValueTag(this.pointer, steamUGCUpdateHandle.handle, str, str2);
    }

    public SteamAPICall submitItemUpdate(SteamUGCUpdateHandle steamUGCUpdateHandle, String str) {
        return new SteamAPICall(submitItemUpdate(this.pointer, this.callback, steamUGCUpdateHandle.handle, str));
    }

    public ItemUpdateStatus getItemUpdateProgress(SteamUGCUpdateHandle steamUGCUpdateHandle, ItemUpdateInfo itemUpdateInfo) {
        long[] jArr = new long[2];
        ItemUpdateStatus itemUpdateStatusByOrdinal = ItemUpdateStatus.byOrdinal(getItemUpdateProgress(this.pointer, steamUGCUpdateHandle.handle, jArr));
        itemUpdateInfo.bytesProcessed = jArr[0];
        itemUpdateInfo.bytesTotal = jArr[1];
        return itemUpdateStatusByOrdinal;
    }

    public SteamAPICall setUserItemVote(SteamPublishedFileID steamPublishedFileID, boolean z) {
        return new SteamAPICall(setUserItemVote(this.pointer, this.callback, steamPublishedFileID.handle, z));
    }

    public SteamAPICall getUserItemVote(SteamPublishedFileID steamPublishedFileID) {
        return new SteamAPICall(getUserItemVote(this.pointer, this.callback, steamPublishedFileID.handle));
    }

    public SteamAPICall addItemToFavorites(int i, SteamPublishedFileID steamPublishedFileID) {
        return new SteamAPICall(addItemToFavorites(this.pointer, this.callback, i, steamPublishedFileID.handle));
    }

    public SteamAPICall removeItemFromFavorites(int i, SteamPublishedFileID steamPublishedFileID) {
        return new SteamAPICall(removeItemFromFavorites(this.pointer, this.callback, i, steamPublishedFileID.handle));
    }

    public SteamAPICall subscribeItem(SteamPublishedFileID steamPublishedFileID) {
        return new SteamAPICall(subscribeItem(this.pointer, this.callback, steamPublishedFileID.handle));
    }

    public SteamAPICall unsubscribeItem(SteamPublishedFileID steamPublishedFileID) {
        return new SteamAPICall(unsubscribeItem(this.pointer, this.callback, steamPublishedFileID.handle));
    }

    public int getNumSubscribedItems() {
        return getNumSubscribedItems(this.pointer);
    }

    public int getSubscribedItems(SteamPublishedFileID[] steamPublishedFileIDArr) {
        long[] jArr = new long[steamPublishedFileIDArr.length];
        int subscribedItems = getSubscribedItems(this.pointer, jArr, steamPublishedFileIDArr.length);
        for (int i = 0; i < subscribedItems; i++) {
            steamPublishedFileIDArr[i] = new SteamPublishedFileID(jArr[i]);
        }
        return subscribedItems;
    }

    public Collection getItemState(SteamPublishedFileID steamPublishedFileID) {
        return ItemState.fromBits(getItemState(this.pointer, steamPublishedFileID.handle));
    }

    public boolean getItemInstallInfo(SteamPublishedFileID steamPublishedFileID, ItemInstallInfo itemInstallInfo) {
        return getItemInstallInfo(this.pointer, steamPublishedFileID.handle, itemInstallInfo);
    }

    public boolean getItemDownloadInfo(SteamPublishedFileID steamPublishedFileID, ItemDownloadInfo itemDownloadInfo) {
        long[] jArr = new long[2];
        if (getItemDownloadInfo(this.pointer, steamPublishedFileID.handle, jArr)) {
            itemDownloadInfo.bytesDownloaded = jArr[0];
            itemDownloadInfo.bytesTotal = jArr[1];
            return true;
        }
        return false;
    }

    public boolean downloadItem(SteamPublishedFileID steamPublishedFileID, boolean z) {
        return downloadItem(this.pointer, steamPublishedFileID.handle, z);
    }

    public boolean initWorkshopForGameServer(int i, String str) {
        return initWorkshopForGameServer(this.pointer, i, str);
    }

    public void suspendDownloads(boolean z) {
        suspendDownloads(this.pointer, z);
    }

    public SteamAPICall startPlaytimeTracking(SteamPublishedFileID[] steamPublishedFileIDArr) {
        long[] jArr = new long[steamPublishedFileIDArr.length];
        for (int i = 0; i < jArr.length; i++) {
            jArr[i] = steamPublishedFileIDArr[i].handle;
        }
        return new SteamAPICall(startPlaytimeTracking(this.pointer, this.callback, jArr, jArr.length));
    }

    public SteamAPICall stopPlaytimeTracking(SteamPublishedFileID[] steamPublishedFileIDArr) {
        long[] jArr = new long[steamPublishedFileIDArr.length];
        for (int i = 0; i < jArr.length; i++) {
            jArr[i] = steamPublishedFileIDArr[i].handle;
        }
        return new SteamAPICall(stopPlaytimeTracking(this.pointer, this.callback, jArr, jArr.length));
    }

    public SteamAPICall stopPlaytimeTrackingForAllItems() {
        return new SteamAPICall(stopPlaytimeTrackingForAllItems(this.pointer, this.callback));
    }
}
