package com.corrodinggames.rts.gameFramework.mod;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.CacheManager;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.i.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/i/b.class */
public class ModInfo implements Comparable<ModInfo> {

    /* JADX INFO: renamed from: a */
    public int id;

    /* JADX INFO: renamed from: c */
    public String dirName;

    /* JADX INFO: renamed from: d */
    public String shortName;

    /* JADX INFO: renamed from: e */
    public String uuid;

    /* JADX INFO: renamed from: f */
    public boolean disabled;

    /* JADX INFO: renamed from: g */
    public boolean wasDisabled;

    /* JADX INFO: renamed from: h */
    public boolean selectionChanged;

    /* JADX INFO: renamed from: i */
    public boolean foundInSettings;

    /* JADX INFO: renamed from: j */
    public boolean isArchive;

    /* JADX INFO: renamed from: k */
    public long steamId;

    /* JADX INFO: renamed from: l */
    boolean found;

    /* JADX INFO: renamed from: m */
    public boolean isCoreMod;

    /* JADX INFO: renamed from: n */
    public String slowExternalPath;

    /* JADX INFO: renamed from: o */
    public String storageDescription;

    /* JADX INFO: renamed from: p */
    public String path;

    /* JADX INFO: renamed from: q */
    public String sourceFolder;

    /* JADX INFO: renamed from: r */
    public boolean dataRefreshed;

    /* JADX INFO: renamed from: s */
    public String title;

    /* JADX INFO: renamed from: t */
    public String name;

    /* JADX INFO: renamed from: u */
    public String description;

    /* JADX INFO: renamed from: v */
    public String minVersion;

    /* JADX INFO: renamed from: w */
    public int customUnitCount;

    /* JADX INFO: renamed from: x */
    public int modIndex;

    /* JADX INFO: renamed from: y */
    public boolean isFromSteam;

    /* JADX INFO: renamed from: z */
    public boolean isBuiltIn;

    /* JADX INFO: renamed from: A */
    public boolean isEnabledAndNotHidden = false;

    /* JADX INFO: renamed from: B */
    public boolean canBeDisabled = true;

    /* JADX INFO: renamed from: C */
    public boolean disabledOrNotLoaded;

    /* JADX INFO: renamed from: D */
    public boolean hasError;

    /* JADX INFO: renamed from: E */
    public int imageMemory;

    /* JADX INFO: renamed from: F */
    public int soundMemory;

    /* JADX INFO: renamed from: G */
    public long totalImageMemory;

    /* JADX INFO: renamed from: H */
    public long totalSoundMemory;

    /* JADX INFO: renamed from: I */
    public int imageCount;

    /* JADX INFO: renamed from: J */
    public int soundCount;

    /* JADX INFO: renamed from: L */
    public int mapsCount;

    /* JADX INFO: renamed from: M */
    public String musicFolder;

    /* JADX INFO: renamed from: N */
    public boolean playMusicExclusively;

    /* JADX INFO: renamed from: O */
    public boolean addToNormalPlaylist;

    /* JADX INFO: renamed from: P */
    public int hasImages;

    /* JADX INFO: renamed from: Q */
    ArrayList unitBlueprints;

    /* JADX INFO: renamed from: R */
    public String firstError;

    /* JADX INFO: renamed from: S */
    public String firstWarning;

    /* JADX INFO: renamed from: T */
    public String otherErrors;

    /* JADX INFO: renamed from: U */
    public ArrayList<String> warnings;

    /* JADX INFO: renamed from: V */
    public ArrayList errors;

    /* JADX INFO: renamed from: b */
    public static int nextId = 1;

    /* JADX INFO: renamed from: K */
    public static int musicCount = 1;

    public ModInfo() {
        int i = musicCount;
        musicCount = i + 1;
        this.mapsCount = i;
        this.unitBlueprints = new ArrayList();
        this.warnings = new ArrayList();
        this.errors = new ArrayList();
        this.id = nextId;
        nextId++;
    }

    /* JADX INFO: renamed from: a */
    public String getDisplayTitle() {
        if (this.title != null) {
            return this.title;
        }
        if (this.name != null) {
            return this.name;
        }
        return this.dirName;
    }

    /* JADX INFO: renamed from: b */
    public String getPaddedTitle() {
        return Utility.padLeft(getDisplayTitle(), 25);
    }

    /* JADX INFO: renamed from: c */
    public String getPaddedTitle40() {
        return Utility.padLeft(getDisplayTitle(), 40);
    }

    /* JADX INFO: renamed from: d */
    public int getMapCount() {
        return this.mapsCount;
    }

    /* JADX INFO: renamed from: e */
    public String getDescription() {
        return getFullDescription();
    }

    /* JADX INFO: renamed from: f */
    public String getFullDescription() {
        String str = VariableScope.nullOrMissingString;
        if (this.description != null) {
            str = str + this.description;
        }
        String str2 = "RAM:" + getMemoryUsageString();
        if (this.slowExternalPath != null) {
            str2 = str2 + " Storage: slow external unpacked";
        }
        if (GameEngine.isDesktop() && this.sourceFolder != null && FileHelper.isManagedPath(this.sourceFolder) && !this.isArchive) {
            str2 = str2 + " Warning: slow external storage";
        }
        return str + "\n (" + str2 + ")";
    }

    /* JADX INFO: renamed from: g */
    public String getSourceFolder() {
        if (this.isFromSteam) {
            return this.sourceFolder;
        }
        return FileHelper.convertAbstractPath(this.sourceFolder);
    }

    /* JADX INFO: renamed from: h */
    public String getPath() {
        if (this.isFromSteam) {
            return this.path;
        }
        return FileHelper.convertAbstractPath(this.path);
    }

    /* JADX INFO: renamed from: i */
    public String getAbsolutePath() {
        return new File(getSourceFolder()).getAbsolutePath();
    }

    /* JADX INFO: renamed from: j */
    public String getSourceFolderRaw() {
        return this.sourceFolder;
    }

    /* JADX INFO: renamed from: k */
    public String getCanonicalPath() throws IOException {
        return new File(getSourceFolder()).getCanonicalPath();
    }

    /* JADX INFO: renamed from: l */
    public String getErrorsAndWarnings() {
        String str = this.firstWarning;
        if (this.otherErrors != null) {
            if (str == null) {
                str = VariableScope.nullOrMissingString;
            }
            str = str + this.otherErrors;
        }
        if (str == null && this.warnings.size() > 0) {
            str = VariableScope.nullOrMissingString;
            int i = 0;
            int i2 = 0;
            for (String str2 : this.warnings) {
                if (i2 <= 2) {
                    if (str == null) {
                        str = str2;
                    } else {
                        str = str + "\n" + str2;
                    }
                } else {
                    i++;
                }
                i2++;
            }
            if (i > 0) {
                str = str + "\n" + i + " more warnings...";
            }
        }
        if ((!this.disabledOrNotLoaded || str != null) && !this.hasError && str == null) {
            if (str == null) {
                str = VariableScope.nullOrMissingString;
            }
            str = str + "Not yet loaded, reload needed";
        }
        return str;
    }

    /* JADX INFO: renamed from: m */
    public boolean isEnabled() {
        return !this.disabled && this.firstError == null;
    }

    /* JADX INFO: renamed from: a */
    public void addError(String str) {
        GameEngine.updatePaintTextSizeIfNeeded("Adding error for mod: " + getPaddedTitle() + (isEnabled() ? VariableScope.nullOrMissingString : "(disabled)") + ": " + str);
        if (this.firstError == null) {
            if (!this.disabled) {
                GameEngine gameEngine = GameEngine.getInstance();
                String str2 = str;
                if (str2 != null && (!str2.contains(getDisplayTitle()) || str2.contains(getPaddedTitle()))) {
                    str2 = "Error loading mod '" + getPaddedTitle() + "': " + str2;
                }
                int modCountByPaddedTitle = gameEngine.modManager.getModCountByPaddedTitle(getPaddedTitle());
                if (modCountByPaddedTitle > 1) {
                    str2 = str2 + " (NOTE: You have " + modCountByPaddedTitle + " mods with the same title: '" + getPaddedTitle() + "' this might make debugging tricky)";
                }
                gameEngine.alert(str2);
            }
            GameEngine.isInSpace("Disabling mod due to error: " + getPaddedTitle() + " path:" + getAbsolutePath());
            this.firstError = str;
        }
        this.errors.add(str);
    }

    /* JADX INFO: renamed from: b */
    public void addWarning(String str) {
        if (this.warnings.contains(str)) {
            return;
        }
        this.warnings.add(str);
    }

    /* JADX INFO: renamed from: a */
    public String findModInfoFile(String str, int i) {
        String[] strArrListFiles;
        String strFindModInfoFile;
        if (i > 4 || (strArrListFiles = FileHelper.listFiles(str)) == null) {
            return null;
        }
        for (String str2 : strArrListFiles) {
            if (str2.equalsIgnoreCase("mod-info.txt")) {
                return str + "/mod-info.txt";
            }
        }
        if (strArrListFiles.length > 5) {
            return null;
        }
        for (String str3 : strArrListFiles) {
            String str4 = str + "/" + str3;
            if (FileHelper.isDirectoryNonZip(str4) && (strFindModInfoFile = findModInfoFile(str4, i + 1)) != null) {
                return strFindModInfoFile;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: n */
    public void fixSourceFolder() throws IOException {
        if (GameEngine.getInstance().isExperimental()) {
            GameEngine.isInSpace("SAFE MODE: skipping setSourceFolder");
            return;
        }
        if (!CacheManager.existsInCache("mods-info", this.sourceFolder + "/mod-info.txt")) {
            String str = this.sourceFolder;
            if (str == null) {
                GameEngine.isInSpace("setSourceFolder: sourceFolder==null");
                return;
            }
            String[] strArrListDirCached = CacheManager.listDirCached("mods-dir-search", str);
            if (strArrListDirCached != null && strArrListDirCached.length == 1) {
                String str2 = str + "/" + strArrListDirCached[0];
                String str3 = str2 + "/mod-info.txt";
                if (FileHelper.isDirectoryNonZip(str2) && FileHelper.fileExists(str3)) {
                    GameEngine.isInSpace("Changing mod sourceFolder to:" + str2);
                    this.sourceFolder = str2;
                }
            }
        }
    }

    /* JADX INFO: renamed from: o */
    public IniFile getModInfoFile() {
        InputStream inputStreamOpenFileByPath;
        String strFindModInfoFile;
        if (this.sourceFolder == null) {
            GameEngine.isInSpace("No source yet for mod: " + this.dirName);
            return null;
        }
        String str = this.sourceFolder + "/mod-info.txt";
        try {
            if (this.isArchive) {
                inputStreamOpenFileByPath = CacheManager.openAssetCached("mods-info", str);
            } else {
                inputStreamOpenFileByPath = FileHelper.openFileByPath(str);
            }
            if (inputStreamOpenFileByPath == null && (strFindModInfoFile = findModInfoFile(this.sourceFolder, 1)) != null) {
                AssetInputStream assetInputStreamOpenFileByPath = FileHelper.openFileByPath(str);
                if (assetInputStreamOpenFileByPath != null) {
                    GameEngine.printLog("mod-info.txt cache seems to be invalid for: " + str);
                    CacheManager.deleteFromCache("mods-info", str);
                    inputStreamOpenFileByPath = assetInputStreamOpenFileByPath;
                } else {
                    addError("No mod info at " + FileHelper.getFileName(str) + " but found one nested at: " + FileHelper.getFileName(strFindModInfoFile) + " (Hint: This mod might have been extracted with an extra folder)");
                }
            }
            if (inputStreamOpenFileByPath == null) {
                GameEngine.isInSpace("No mod info for: " + this.dirName + " at " + str);
                return null;
            }
            try {
                return new IniFile(inputStreamOpenFileByPath, str);
            } catch (IOException e) {
                GameEngine.isInSpace("Error loading mod info for: " + this.dirName + " at " + str);
                e.printStackTrace();
                addWarning("Error loading mod-info.txt: " + e.getMessage());
                return null;
            }
        } catch (Exception e2) {
            GameEngine.isInSpace("Error loading mod info for: " + this.dirName + " at " + str);
            e2.printStackTrace();
            addWarning("Error loading mod-info.txt: " + e2.getMessage());
            return null;
        }
    }

    /* JADX INFO: renamed from: p */
    public String getThumbnail() {
        String modInfoValue = getModInfoValue("thumbnail");
        if (modInfoValue != null) {
            return getAbsolutePath() + "/" + modInfoValue;
        }
        return null;
    }

    /* JADX INFO: renamed from: q */
    public ArrayList getUnitBlueprints() {
        return this.unitBlueprints;
    }

    /* JADX INFO: renamed from: c */
    public String getModInfoValue(String str) {
        IniFile modInfoFile = getModInfoFile();
        if (modInfoFile == null) {
            return null;
        }
        return modInfoFile.getString("mod", str, (String) null);
    }

    /* JADX INFO: renamed from: r */
    public void refreshData() throws IOException {
        if (GameEngine.getInstance().isExperimental()) {
            GameEngine.isInSpace("SAFE MODE: refreshData: Skipping mod read");
            this.description = "<< SAFE MODE ACTIVE: MOD DATA SKIPPED. RESTART IN NORMAL MODE. >>";
            return;
        }
        IniFile modInfoFile = getModInfoFile();
        if (modInfoFile != null) {
            this.title = modInfoFile.getString("mod", "title", (String) null);
            this.description = modInfoFile.getString("mod", "description", (String) null);
            if (this.description != null && this.description.contains("\\n")) {
                this.description = this.description.replace("\\n", "\n");
            }
            this.minVersion = modInfoFile.getString("mod", "minVersion", (String) null);
            if (this.minVersion != null && !this.minVersion.trim().equals(VariableScope.nullOrMissingString)) {
                try {
                    ModManager.checkMinVersion(this.minVersion);
                } catch (ConfigParseException e) {
                    addError(e.getMessage());
                }
            }
            this.musicFolder = modInfoFile.getString("music", "sourceFolder", (String) null);
            this.playMusicExclusively = modInfoFile.getBoolean("music", "whenUsingUnitsFromThisMod_playExclusively", (Boolean) false).booleanValue();
            this.addToNormalPlaylist = modInfoFile.getBoolean("music", "addToNormalPlaylist", (Boolean) false).booleanValue();
            if (this.musicFolder != null && isEnabled()) {
                GameEngine.isInSpace("Loading music for: " + getDisplayTitle());
                String randomUUID = Utility.getRandomUUID(this.sourceFolder, this.musicFolder);
                String[] strArrListDirCached = CacheManager.listDirCached("mods-dir-music", randomUUID);
                if (strArrListDirCached == null) {
                    addWarning("Could not read target music folder: " + FileHelper.convertAbstractPath(randomUUID));
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (String str : strArrListDirCached) {
                        if (str.toLowerCase().endsWith(".ogg")) {
                            String randomUUID2 = Utility.getRandomUUID(randomUUID, str);
                            if (!this.unitBlueprints.contains(randomUUID2)) {
                                GameEngine.isInSpace("Found music track: " + str);
                            }
                            arrayList.add(randomUUID2);
                        }
                    }
                    this.unitBlueprints = arrayList;
                    if (this.unitBlueprints.size() == 0) {
                        addWarning("Could not find any .ogg files in music folder: " + FileHelper.convertAbstractPath(randomUUID));
                    }
                }
            }
            this.dataRefreshed = true;
        }
        String steamDatPath = getSteamDatPath();
        File file = new File(steamDatPath);
        if (file.exists() && !file.isDirectory()) {
            IniFile iniFile = null;
            try {
                iniFile = new IniFile(steamDatPath);
            } catch (IOException e2) {
                e2.printStackTrace();
                addWarning("IO error reading: " + FileHelper.convertAbstractPath(steamDatPath));
            }
            if (iniFile != null) {
                this.steamId = iniFile.getLong("steam", "id", 0L);
            }
        }
    }

    /* JADX INFO: renamed from: w */
    private String getSteamDatPath() {
        return getSourceFolder() + "/steam.dat";
    }

    /* JADX INFO: renamed from: a */
    public boolean setSteamId(long j) {
        this.steamId = j;
        String steamDatPath = getSteamDatPath();
        try {
            PrintWriter printWriter = new PrintWriter(steamDatPath);
            printWriter.println("[steam]");
            printWriter.println("id: " + j);
            printWriter.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            GameEngine.getInstance().alert("IO error: Failed to save workshop id for mod at: " + steamDatPath);
            return false;
        }
    }

    /* JADX INFO: renamed from: s */
    public String getMemoryUsageString() {
        return VariableScope.nullOrMissingString + String.format("%.2f", Float.valueOf((float) (((this.totalImageMemory + this.totalSoundMemory) / 1000.0d) / 1000.0d))) + " mb" + (this.disabledOrNotLoaded ? " - disabled" : VariableScope.nullOrMissingString);
    }

    /* JADX INFO: renamed from: t */
    public void logMemoryUsage() {
        GameEngine.isInSpace("Mod: '" + getDisplayTitle() + "' - Memory use:" + getMemoryUsageString() + " " + (isEnabled() ? VariableScope.nullOrMissingString : " (disabled)"));
    }

    /* JADX INFO: renamed from: u */
    public boolean delete() {
        GameEngine.isInSpace("Trying to delete mod: '" + getDisplayTitle() + "'");
        String path = getPath();
        GameEngine.isInSpace("sourceFolder: '" + path + "'");
        if (!canBeDeleted()) {
            GameEngine.isInSpace("Mod: '" + getDisplayTitle() + "' - Cannot be deleted");
            return false;
        }
        File file = new File(path);
        if (!FileHelper.fileExists(file.getAbsolutePath())) {
            GameEngine.isInSpace("Mod: '" + getDisplayTitle() + "' - cannot delete: Not a file");
            return false;
        }
        boolean zDeleteDirectory = FileHelper.deleteDirectory(file);
        GameEngine.isInSpace("Delete result: " + zDeleteDirectory);
        return zDeleteDirectory;
    }

    /* JADX INFO: renamed from: v */
    public boolean canBeDeleted() {
        if (this.isBuiltIn) {
            return false;
        }
        if (GameEngine.isDebugVersionStatic2 && this.isArchive) {
            return true;
        }
        if (GameEngine.isDesktop() && this.isArchive) {
            return true;
        }
        return false;
    }

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compareTo(ModInfo modInfo) {
        if (modInfo == null) {
            return 0;
        }
        int i = this.modIndex;
        int i2 = modInfo.modIndex;
        if (i != i2) {
            return i - i2;
        }
        String displayTitle = getDisplayTitle();
        String displayTitle2 = modInfo.getDisplayTitle();
        if (displayTitle == null) {
            displayTitle = VariableScope.nullOrMissingString;
        }
        if (displayTitle2 == null) {
            displayTitle2 = VariableScope.nullOrMissingString;
        }
        return displayTitle.compareTo(displayTitle2);
    }
}
