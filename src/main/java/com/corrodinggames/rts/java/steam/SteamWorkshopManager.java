package com.corrodinggames.rts.java.steam;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamRemoteStorage;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCCallback;
import com.codedisaster.steamworks.SteamUGCQuery;
import com.codedisaster.steamworks.SteamUGCUpdateHandle;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.mod.ModInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.c.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/c/g.class */
public class SteamWorkshopManager {
    JavaSteamEngine b;
    SteamUGC d;
    ModInfo e;
    boolean f;
    ModInfo g;
    int k;
    boolean a = false;
    ArrayList<WorkshopDownloadRequest> h = new ArrayList();
    HashMap i = new HashMap();
    ArrayList j = new ArrayList();
    SteamUGCCallback c = new SteamUGCCallbackHandler(this);

    private void e() {
        if (this.j.size() == 0) {
            return;
        }
        SteamUGCQuery steamUGCQueryCreateQueryUGCDetailsRequest = this.d.createQueryUGCDetailsRequest(this.j);
        this.j.clear();
        this.d.sendQueryUGCRequest(steamUGCQueryCreateQueryUGCDetailsRequest);
        this.d.releaseQueryUserUGCRequest(steamUGCQueryCreateQueryUGCDetailsRequest);
        SteamAPI.runCallbacks();
    }

    private void e(SteamPublishedFileID steamPublishedFileID) {
        GameEngine.isInSpace("Adding request for workshop details: " + steamPublishedFileID);
        this.j.add(steamPublishedFileID);
    }

    public WorkshopItemInfo a(SteamPublishedFileID steamPublishedFileID) {
        WorkshopItemInfo workshopItemInfo = (WorkshopItemInfo) this.i.get(steamPublishedFileID);
        if (workshopItemInfo == null) {
            GameEngine.isInSpace("New ItemInfo: " + steamPublishedFileID);
            workshopItemInfo = new WorkshopItemInfo();
            workshopItemInfo.a = steamPublishedFileID;
            this.i.put(steamPublishedFileID, workshopItemInfo);
        }
        return workshopItemInfo;
    }

    public String b(SteamPublishedFileID steamPublishedFileID) {
        WorkshopItemInfo workshopItemInfoA = a(steamPublishedFileID);
        if (workshopItemInfoA.b == 0 && !workshopItemInfoA.c) {
            workshopItemInfoA.b = System.currentTimeMillis();
            e(steamPublishedFileID);
        }
        if (workshopItemInfoA.c) {
            GameEngine.isInSpace("Got data for: " + steamPublishedFileID + " - " + workshopItemInfoA.d);
            return workshopItemInfoA.d;
        }
        GameEngine.isInSpace("No data for: " + steamPublishedFileID);
        return null;
    }

    public WorkshopDownloadRequest c(SteamPublishedFileID steamPublishedFileID) {
        for (WorkshopDownloadRequest workshopDownloadRequest : this.h) {
            if (workshopDownloadRequest.a.equals(steamPublishedFileID)) {
                return workshopDownloadRequest;
            }
        }
        return null;
    }

    public SteamWorkshopManager(JavaSteamEngine javaSteamEngine) {
        this.b = javaSteamEngine;
    }

    public void a(SteamUGC steamUGC) {
        this.d = steamUGC;
    }

    public SteamUGCCallback a() {
        return this.c;
    }

    public void a(ModInfo modInfo, String str, boolean z) {
        GameEngine.isInSpace("workshop: " + str);
        if (z) {
            GameEngine.getInstance().alert(str);
        }
    }

    public boolean a(ModInfo modInfo) {
        if (modInfo.title == null) {
            a(modInfo, "A title is required in the file 'mod-info.txt'", true);
            return false;
        }
        if (!modInfo.dataRefreshed) {
            a(modInfo, "Please add and setup the file 'mod-info.txt' to this mod before uploading", true);
            return false;
        }
        String thumbnail = modInfo.getThumbnail();
        if (thumbnail != null && !FileHelper.fileExists(thumbnail)) {
            a(modInfo, "Could not find thumbnail file: " + thumbnail + " referenced mod-info.txt", true);
            return false;
        }
        return true;
    }

    public void b(ModInfo modInfo) {
        if (this.e != null) {
            a(modInfo, "A mod is already pending publishing", false);
            return;
        }
        if (modInfo.steamId != 0) {
            a(modInfo, "This mod has already been published", false);
            return;
        }
        if (!a(modInfo)) {
            return;
        }
        if (!modInfo.setSteamId(0L)) {
            a(modInfo, "Failed to write metadata to mod, check file permissions", true);
            return;
        }
        this.e = modInfo;
        this.d.createItem(this.b.j.getAppID(), SteamRemoteStorage.WorkshopFileType.Community);
    }

    public void a(ModInfo modInfo, boolean z, String str) {
        if (!a(modInfo)) {
            return;
        }
        SteamUGCUpdateHandle steamUGCUpdateHandleStartItemUpdate = this.d.startItemUpdate(this.b.j.getAppID(), new SteamPublishedFileID(modInfo.steamId));
        if (z) {
            if (modInfo.title != null) {
                this.d.setItemTitle(steamUGCUpdateHandleStartItemUpdate, modInfo.title);
            }
            if (modInfo.description != null) {
                this.d.setItemDescription(steamUGCUpdateHandleStartItemUpdate, modInfo.description);
            }
        }
        if (z) {
            this.d.setItemVisibility(steamUGCUpdateHandleStartItemUpdate, SteamRemoteStorage.PublishedFileVisibility.Public);
        }
        String thumbnail = modInfo.getThumbnail();
        if (thumbnail != null) {
            this.d.setItemPreview(steamUGCUpdateHandleStartItemUpdate, thumbnail);
        }
        String modInfoValue = modInfo.getModInfoValue("tags");
        if (modInfoValue != null) {
            String[] strArrSplit = modInfoValue.split(",");
            for (int i = 0; i < strArrSplit.length; i++) {
                strArrSplit[i] = strArrSplit[i].trim();
                GameEngine.isInSpace("Adding tag:" + strArrSplit[i]);
            }
            this.d.setItemTags(steamUGCUpdateHandleStartItemUpdate, strArrSplit);
        }
        String absolutePath = modInfo.getAbsolutePath();
        GameEngine.isInSpace("convertedAbsolutePath:" + absolutePath);
        this.d.setItemContent(steamUGCUpdateHandleStartItemUpdate, absolutePath);
        modInfo.firstWarning = "Uploading to workshop";
        this.f = z;
        this.g = modInfo;
        this.d.submitItemUpdate(steamUGCUpdateHandleStartItemUpdate, str);
        GameEngine.isInSpace("submitted item update for:" + modInfo.steamId);
    }

    public void b() {
        this.b.c.activateGameOverlayToWebPage("http://steamcommunity.com/workshop/browse/?appid=" + this.b.j.getAppID());
    }

    public void c(ModInfo modInfo) {
        this.b.c.activateGameOverlayToWebPage("steam://url/CommunityFilePage/" + modInfo.steamId);
    }

    public long d(SteamPublishedFileID steamPublishedFileID) {
        return SteamNativeHandle.getNativeHandle(steamPublishedFileID);
    }

    public void c() throws IOException {
        if (this.a) {
            this.a = false;
            GameEngine.getInstance().modManager.triggerStatisticsUpdate();
        }
        int numSubscribedItems = this.d.getNumSubscribedItems();
        if (numSubscribedItems != this.k) {
            GameEngine.isInSpace("Number of subscribed items changed from: " + this.k + " to: " + numSubscribedItems);
            this.k = numSubscribedItems;
            GameEngine.getInstance();
            d();
            this.a = true;
        }
    }

    public void d() throws IOException {
        String strB;
        GameEngine gameEngine = GameEngine.getInstance();
        SteamAPI.runCallbacks();
        GameEngine.isInSpace("--------------");
        GameEngine.isInSpace("Steam: loadWorkshopMods");
        int numSubscribedItems = this.d.getNumSubscribedItems();
        this.k = numSubscribedItems;
        SteamPublishedFileID[] steamPublishedFileIDArr = new SteamPublishedFileID[numSubscribedItems];
        this.d.getSubscribedItems(steamPublishedFileIDArr);
        for (SteamPublishedFileID steamPublishedFileID : steamPublishedFileIDArr) {
            Collection<SteamUGC.ItemState> itemState = this.d.getItemState(steamPublishedFileID);
            long jD = d(steamPublishedFileID);
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = false;
            boolean z5 = false;
            String str = null;
            for (SteamUGC.ItemState itemState2 : itemState) {
                if (itemState2 != SteamUGC.ItemState.None) {
                    if (str == null) {
                        str = VariableScope.nullOrMissingString + itemState2;
                    } else {
                        str = str + ", " + itemState2;
                    }
                    if (itemState2 == SteamUGC.ItemState.Downloading) {
                        z2 = true;
                    }
                    if (itemState2 == SteamUGC.ItemState.DownloadPending) {
                        z2 = true;
                        z3 = true;
                    }
                    if (itemState2 == SteamUGC.ItemState.Installed) {
                        z = true;
                    }
                    if (itemState2 == SteamUGC.ItemState.NeedsUpdate) {
                        z4 = true;
                    }
                    if (itemState2 == SteamUGC.ItemState.Subscribed) {
                        z5 = true;
                    }
                }
            }
            GameEngine.isInSpace("Found workshop item " + jD + " with state: " + str + VariableScope.nullOrMissingString);
            String str2 = "sw" + jD;
            String str3 = "(Workshop item - " + jD + ")";
            if (!z && (strB = b(steamPublishedFileID)) != null) {
                str3 = strB;
            }
            String folder = null;
            String str4 = null;
            if (!z5) {
                GameEngine.isInSpace("Skipping: " + jD + " as it is not subscribed");
            } else {
                if (z) {
                    SteamUGC.ItemInstallInfo itemInstallInfo = new SteamUGC.ItemInstallInfo();
                    this.d.getItemInstallInfo(steamPublishedFileID, itemInstallInfo);
                    folder = itemInstallInfo.getFolder();
                    GameEngine.isInSpace(" Installed at: " + folder);
                } else {
                    GameEngine.isInSpace(" Not installed");
                    str4 = "Not installed.";
                    if (z4) {
                        str4 = "Update needed..";
                    }
                    if (z3) {
                        str4 = "Download pending in steam...";
                    } else if (z2) {
                        str4 = "Steam is downloading files..";
                        final SteamUGC.ItemDownloadInfo itemDownloadInfo = new SteamUGC.ItemDownloadInfo();
                        if (this.d.getItemDownloadInfo(steamPublishedFileID, new SteamUGC.ItemDownloadInfo())) {
                            str4 = str4 + " " + Utility.md5((itemDownloadInfo.getBytesDownloaded() / (double)itemDownloadInfo.getBytesTotal()) * 100.0d) + "%";
                        }
                    }
                }
                int i = 0;
                if (!z) {
                    i = -1;
                }
                ModInfo modInfoAddOrUpdateMod = gameEngine.modManager.addOrUpdateMod(str2, str2, folder, str2, true, true, false, i);
                if (modInfoAddOrUpdateMod.title == null) {
                    modInfoAddOrUpdateMod.name = str3;
                }
                if (str4 == null && z4) {
                    if (z3) {
                        str4 = "An update is pending download in Steam.";
                    } else if (z3) {
                        str4 = "An update is downloading...";
                    } else {
                        str4 = "An update is available.";
                    }
                }
                modInfoAddOrUpdateMod.otherErrors = str4;
                if ((!z || z4) && (!z2 || z3)) {
                    GameEngine.isInSpace("Queuing download on: " + steamPublishedFileID);
                    this.d.downloadItem(steamPublishedFileID, false);
                }
            }
        }
        e();
    }
}
