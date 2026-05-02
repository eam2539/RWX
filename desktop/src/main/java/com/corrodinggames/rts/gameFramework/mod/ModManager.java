package com.corrodinggames.rts.gameFramework.mod;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfigParser;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine;
import com.corrodinggames.rts.gameFramework.ui.GameUI;
import com.corrodinggames.rts.gameFramework.utility.FileLoaderFactory;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.i.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/i/a.class */
public class ModManager {

    /* JADX INFO: renamed from: a */
    public static String minVersion;

    /* JADX INFO: renamed from: b */
    public static String latestVersion;

    /* JADX INFO: renamed from: c */
    public ModInfo modInfo = new ModInfo();

    /* JADX INFO: renamed from: d */
    Object listLock = new Object();

    /* JADX INFO: renamed from: e */
    ArrayList<ModInfo> mods = new ArrayList();

    /* JADX INFO: renamed from: f */
    ArrayList<LegacyDisabledMod> invalidMods = new ArrayList();

    public ModManager() {
        try {
            checkMinVersion(GameEngine.getInstance().getVersion());
        } catch (ConfigParseException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: a */
    private static int parseVersionPart(String str, int i) throws ConfigParseException {
        String[] strArrSplitByChar = Utility.splitByChar(str, '.');
        if (strArrSplitByChar == null) {
            throw new ConfigParseException("Unexpected version format (Missing " + i + ")");
        }
        if (strArrSplitByChar.length > 3) {
            throw new ConfigParseException("Unexpected version format (" + str + ")");
        }
        if (strArrSplitByChar.length <= i) {
            return 0;
        }
        try {
            return Integer.valueOf(strArrSplitByChar[i]).intValue();
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Unexpected version format (Bad " + i + ")", e);
        }
    }

    /* JADX INFO: renamed from: a */
    public static void checkMinVersion(String str) throws ConfigParseException {
        checkVersion(str, GameEngine.getInstance().getVersion());
    }

    /* JADX INFO: renamed from: b */
    public static String cleanVersionString(String str) {
        return Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(Utility.readStreamToString(str, "v", VariableScope.nullOrMissingString).trim(), "a", VariableScope.nullOrMissingString), "b", VariableScope.nullOrMissingString), "c", VariableScope.nullOrMissingString), "d", VariableScope.nullOrMissingString), "e", VariableScope.nullOrMissingString), "f", VariableScope.nullOrMissingString), "g", VariableScope.nullOrMissingString), "h1", VariableScope.nullOrMissingString), "h2", VariableScope.nullOrMissingString), "h3", VariableScope.nullOrMissingString), "h4", VariableScope.nullOrMissingString);
    }

    /* JADX INFO: renamed from: a */
    public static void checkVersion(String str, String str2) throws ConfigParseException {
        String strCleanVersionString = cleanVersionString(str2);
        String strCleanVersionString2 = cleanVersionString(str);
        try {
            int iIntValue = 1000;
            int iIntValue2 = 1000;
            if (strCleanVersionString.contains("p")) {
                String[] strArrB = StringUtils.b(strCleanVersionString, "p");
                try {
                    iIntValue = Integer.valueOf(strArrB[1]).intValue();
                    strCleanVersionString = strArrB[0];
                } catch (NumberFormatException e) {
                    throw new ConfigParseException("Unexpected min version:" + strCleanVersionString2 + " (Bad build number)", e);
                }
            }
            if (strCleanVersionString2.contains("p")) {
                String[] strArrB2 = StringUtils.b(strCleanVersionString2, "p");
                try {
                    iIntValue2 = Integer.valueOf(strArrB2[1]).intValue();
                    strCleanVersionString2 = strArrB2[0];
                } catch (NumberFormatException e2) {
                    throw new ConfigParseException("Unexpected min version:" + strCleanVersionString2 + "(Bad build number)", e2);
                }
            }
            try {
                int versionPart = parseVersionPart(strCleanVersionString, 0);
                int versionPart2 = parseVersionPart(strCleanVersionString2, 0);
                int versionPart3 = parseVersionPart(strCleanVersionString, 1);
                int versionPart4 = parseVersionPart(strCleanVersionString2, 1);
                int versionPart5 = parseVersionPart(strCleanVersionString, 2);
                int versionPart6 = parseVersionPart(strCleanVersionString2, 2);
                if (versionPart2 < 1) {
                    throw new ConfigParseException("Min version cannot be less than v1.10");
                }
                if (versionPart2 > versionPart) {
                    throw new ConfigParseException("Requires version: " + strCleanVersionString2 + " or higher. (You have: " + strCleanVersionString + ")");
                }
                if (versionPart > versionPart2) {
                    return;
                }
                if (versionPart4 < 10 && versionPart2 == 1) {
                    throw new ConfigParseException("Min version cannot be less than v1.10");
                }
                if (versionPart4 > versionPart3) {
                    throw new ConfigParseException("Requires version: " + strCleanVersionString2 + " or higher. (You have: " + strCleanVersionString + ")");
                }
                if (versionPart3 > versionPart4) {
                    return;
                }
                if (versionPart6 > versionPart5) {
                    throw new ConfigParseException("Requires version: " + strCleanVersionString2 + " or higher. (You have: " + strCleanVersionString + ")");
                }
                if (versionPart5 <= versionPart6 && iIntValue2 > iIntValue) {
                    throw new ConfigParseException("Requires newer build: " + strCleanVersionString2 + " or higher. (You have: " + strCleanVersionString + ")");
                }
            } catch (ConfigParseException e3) {
                throw new ConfigParseException("Requires version: " + strCleanVersionString2 + " or higher. " + e3.getMessage(), e3);
            }
        } catch (RuntimeException e4) {
            throw new ConfigParseException("Requires version: " + strCleanVersionString2 + " or higher." + e4.getMessage(), e4);
        }
    }

    /* JADX INFO: renamed from: a */
    public void loadAndApply()  {
        loadAllMods();
        loadModSelection();
    }

    /* JADX INFO: renamed from: a */
    public int getEnabledModCount(boolean z) {
        int i = 0;
        for (ModInfo modInfo : this.mods) {
            if (!modInfo.disabled && !modInfo.hasError && (!z || modInfo.firstError == null)) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: b */
    public int getEnabledModsWithLevelsCount() {
        int i = 0;
        for (ModInfo modInfo : this.mods) {
            if (!modInfo.disabled && modInfo.firstError != null) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: c */
    public int getStorageModsCount() {
        int i = 0;
        Iterator it = this.mods.iterator();
        while (it.hasNext()) {
            if (!((ModInfo) it.next()).isBuiltIn) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: d */
    public void backupModSelection() {
        for (ModInfo modInfo : this.mods) {
            modInfo.wasDisabled = modInfo.disabled;
            modInfo.selectionChanged = false;
        }
    }

    /* JADX INFO: renamed from: e */
    public void saveModSelection() {
        GameEngine gameEngine = GameEngine.getInstance();
        String str = VariableScope.nullOrMissingString;
        for (ModInfo modInfo : this.mods) {
            String strReplace = modInfo.dirName.replace(",", " ").replace("|", " ");
            if (strReplace.length() > 15) {
                strReplace = strReplace.substring(12) + "...";
            }
            if (str.length() != 0) {
                str = str + ",";
            }
            str = str + strReplace + "|" + modInfo.uuid + "|" + (modInfo.disabled ? "disabled" : "enabled");
        }
        gameEngine.settingsEngine.modSettingsVersion = 1;
        gameEngine.settingsEngine.modSettings = str;
    }

    /* JADX INFO: renamed from: f */
    public void loadModSelection() {
        boolean z = false;
        GameEngine.log("Loading mod selection");
        for (String str : GameEngine.getInstance().settingsEngine.modSettings.split(",")) {
            String[] strArrSplit = str.split("\\|");
            if (strArrSplit.length != 3) {
                GameEngine.log("loadSelection: wrong count (" + strArrSplit.length + "):" + str);
            } else {
                String str2 = strArrSplit[0];
                String str3 = strArrSplit[1];
                String str4 = strArrSplit[2];
                if (str4.equals("enabled")) {
                    z = false;
                } else if (str4.equals("disabled")) {
                    z = true;
                } else {
                    GameEngine.log("loadSelection: Unknown option:" + str);
                }
                ModInfo modByUuid = getModByUuid(str3);
                if (modByUuid == null) {
                    GameEngine.log("loadSelection: Did not find mod in settings:" + str2);
                } else {
                    modByUuid.disabled = z;
                    modByUuid.foundInSettings = true;
                }
            }
        }
    }

    /* JADX INFO: renamed from: c */
    public ModInfo getModByUuid(String str) {
        for (ModInfo modInfo : this.mods) {
            if (modInfo.uuid.equals(str)) {
                return modInfo;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: d */
    public int getModCountByPaddedTitle(String str) {
        if (str == null) {
            return 0;
        }
        int i = 0;
        Iterator it = this.mods.iterator();
        while (it.hasNext()) {
            if (str.equals(((ModInfo) it.next()).getPaddedTitle40())) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: a */
    public ModInfo getModByMapsCount(int i) {
        for (ModInfo modInfo : this.mods) {
            if (modInfo.mapsCount == i) {
                return modInfo;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: g */
    public void disableAllMods() {
        Iterator it = this.mods.iterator();
        while (it.hasNext()) {
            ((ModInfo) it.next()).disabled = true;
        }
    }

    /* JADX INFO: renamed from: h */
    public int getActiveModCount() {
        int i = 0;
        for (ModInfo modInfo : this.mods) {
            if (!modInfo.disabled || modInfo.hasError) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: e */
    public ModInfo getModByShortName(String str) {
        for (ModInfo modInfo : this.mods) {
            if (modInfo.shortName.equals(str)) {
                return modInfo;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: f */
    public ModInfo getModByName(String str) {
        for (ModInfo modInfo : this.mods) {
            if (modInfo.getDisplayTitle().equals(str)) {
                return modInfo;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    public ModInfo addOrUpdateMod(String str, String str2, String str3, String str4, boolean z, boolean z2, boolean z3, int i) throws IOException {
        ModInfo modByUuid = getModByUuid(str4);
        if (modByUuid == null) {
            modByUuid = new ModInfo();
            modByUuid.dirName = str;
            modByUuid.shortName = str2;
            modByUuid.uuid = str4;
            modByUuid.disabled = !z;
        }
        if (modByUuid.sourceFolder == null && str3 != null) {
            modByUuid.sourceFolder = str3;
            modByUuid.path = modByUuid.sourceFolder;
            modByUuid.fixSourceFolder();
            if (modByUuid.sourceFolder != null && modByUuid.sourceFolder.toLowerCase(Locale.ROOT).contains("rwmod")) {
                modByUuid.isArchive = true;
            }
        }
        modByUuid.modIndex = i;
        modByUuid.found = true;
        modByUuid.isFromSteam = z2;
        modByUuid.isBuiltIn = z3;
        if (!modByUuid.isBuiltIn) {
            modByUuid.storageDescription = "Storage: " + FileHelper.getFileName(modByUuid.sourceFolder);
        }
        modByUuid.refreshData();
        synchronized (this.listLock) {
            if (!this.mods.contains(modByUuid)) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(this.mods);
                arrayList.add(modByUuid);
                Collections.sort(arrayList);
                this.mods = arrayList;
            }
        }
        return modByUuid;
    }

    /* JADX INFO: renamed from: a */
    public void removeMod(ModInfo modInfo) {
        synchronized (this.listLock) {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(this.mods);
            arrayList.remove(modInfo);
            this.mods = arrayList;
        }
    }

    /* JADX INFO: renamed from: a */
    public void loadModsFromDir(String str, boolean z, boolean z2) throws IOException {
        GameEngine.log("loading mod custom units at:" + str);
        String[] strArrListFiles = FileHelper.listFiles(str);
        if (strArrListFiles == null) {
            GameEngine.updatePaintTextSizeIfNeeded("getAllModList: ERROR");
            GameEngine.updatePaintTextSizeIfNeeded("getAllModList: Failed to load:" + str);
            return;
        }
        for (String str2 : strArrListFiles) {
            String str3 = str + "/" + str2;
            if (FileHelper.isDirectoryNonZip(str3) || str2.endsWith(".ini")) {
                String strTruncate = Utility.truncate(str2);
                String strSubstring = str2;
                if (strSubstring.contains("/")) {
                    strSubstring = strSubstring.substring(str2.lastIndexOf("/") + 1);
                }
                addOrUpdateMod(strSubstring, str2, str3, strTruncate, z, false, z2, 0);
            }
        }
    }

    /* JADX INFO: renamed from: i */
    public ArrayList<String> getAllUnitBlueprintsFromEnabledMods() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (ModInfo modInfo : this.mods) {
            if (modInfo.isEnabled()) {
                arrayList.addAll(modInfo.getUnitBlueprints());
            }
        }
        return arrayList;
    }

    /* JADX INFO: renamed from: j */
    public ArrayList getActiveMods() {
        ArrayList arrayList = new ArrayList();
        for (ModInfo modInfo : this.mods) {
            if (modInfo.isEnabled()) {
                arrayList.add(modInfo);
            }
        }
        return arrayList;
    }

    /* JADX INFO: renamed from: k */
    public ArrayList<ModInfo> loadAllMods() {
        try {


            for (ModInfo modInfo : this.mods) {
                modInfo.found = modInfo.isCoreMod;
            }
            DisabledSteamEngine disabledSteamEngineA = DisabledSteamEngine.a();
            if (disabledSteamEngineA != null) {
                disabledSteamEngineA.l();
            } else {
                GameEngine.log("getAllModList: SteamEngine==null");
            }
            String defaultUserModsFolder = CustomUnitConfigParser.getDefaultUserModsFolder();
            if (!FileHelper.isDirectoryNonZip(defaultUserModsFolder)) {
                GameEngine.log("Modded Custom '" + defaultUserModsFolder + "' directory not found");
            } else {
                loadModsFromDir(defaultUserModsFolder, true, false);
            }
            String builtinModsFolderName = CustomUnitConfigParser.getBuiltinModsFolderName();
            if (!FileHelper.isDirectoryNonZip(builtinModsFolderName)) {
                GameEngine.log("Modded Custom '" + builtinModsFolderName + "' directory not found");
            } else {
                loadModsFromDir(builtinModsFolderName, false, true);
            }
            String builtinModsEnabledFolderName = CustomUnitConfigParser.getBuiltinModsEnabledFolderName();
            if (!FileHelper.isDirectoryNonZip(builtinModsEnabledFolderName)) {
                GameEngine.log("Modded Custom '" + builtinModsEnabledFolderName + "' directory not found");
            } else {
                loadModsFromDir(builtinModsEnabledFolderName, true, true);
            }
            for (ModInfo modInfo2 : this.mods) {
                if (!modInfo2.found) {
                    GameEngine.log("Removing mod no longer found on system: " + modInfo2.getDisplayTitle());
                    removeMod(modInfo2);
                }
            }
            GameEngine.log("========= Mods ===========");
            GameEngine.log("Number of mods:" + this.mods.size());
            for (ModInfo mod : this.mods) {
                GameEngine.log("Mod: '" + mod.getDisplayTitle());
            }
            GameEngine.log("================================");
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine.settingsEngine.lastModCount == -1 || gameEngine.settingsEngine.modSettingsVersion < 1) {
                GameEngine.log("Disabling all new mods for first/new load");
                for (ModInfo mod : this.mods) {
                    mod.disabled = true;
                }
                saveModSelection();
                gameEngine.settingsEngine.save();
            } else if (this.mods.size() > gameEngine.settingsEngine.lastModCount + 4) {
                GameEngine.log("Too many new mods found, not enabling new mods");
                GameEngine.log("Number of mods:" + this.mods.size() + " vs " + gameEngine.settingsEngine.lastModCount);
                for (ModInfo modInfo3 : this.mods) {
                    if (!modInfo3.foundInSettings) {
                        modInfo3.disabled = true;
                    }
                }
                saveModSelection();
                gameEngine.settingsEngine.save();
            }
            gameEngine.settingsEngine.lastModCount = this.mods.size();

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.mods;
    }

    /* JADX INFO: renamed from: l */
    public void applyAndSaveMods() {
        GameEngine gameEngine = GameEngine.getInstance();
        try {
            gameEngine.isSaving = true;
            gameEngine.stopAndReset();
            applyMods(false, false);
            gameEngine.loadMenuBackground();
        } finally {
            gameEngine.isSaving = false;
        }
    }

    /* JADX INFO: renamed from: a */
    public void applyMods(boolean z, boolean z2) {
        GameEngine.getInstance();
        FileLoaderFactory.closeAll();
        if (!z2) {
            for (ModInfo modInfo : this.mods) {
                if (modInfo.firstError != null) {
                    GameEngine.log("re-enabling mod: " + modInfo.getDisplayTitle());
                }
                modInfo.firstError = null;
                modInfo.errors.clear();
                modInfo.firstWarning = null;
                modInfo.warnings.clear();
                modInfo.disabledOrNotLoaded = false;
                modInfo.hasError = false;
                modInfo.imageMemory = 0;
                modInfo.soundMemory = 0;
                modInfo.totalImageMemory = 0L;
                modInfo.totalSoundMemory = 0L;
                modInfo.imageCount = 0;
                modInfo.soundCount = 0;
                modInfo.customUnitCount = 0;
            }
        }
        loadAllMods();
        ArrayList<CustomUnitConfig> arrayList = new ArrayList(CustomUnitConfig.activeConfigs);
        if (!z2) {
            CustomUnitConfigParser.loadAllCustomUnitsAndMods();
        } else {
            CustomUnitConfigParser.reloadAllActiveCustomUnits();
        }
        if (z) {
            int i = 0;
            for (CustomUnitConfig customUnitConfig : arrayList) {
                if (customUnitConfig.modInfo != null && !customUnitConfig.modInfo.disabled && customUnitConfig.modInfo.firstError != null && CustomUnitConfig.a(customUnitConfig) == null) {
                    GameEngine.log("Was missing: " + customUnitConfig.name);
                    CustomUnitConfig.activeConfigs.add(customUnitConfig);
                    i++;
                }
            }
            if (i > 0) {
                CustomUnitConfigParser.rebuildUnitTypeListsAsync();
            }
        }
        CustomUnitConfig.load();
        PlayerTeam.updateTeamDefeatStatus();
        GameUI.notifySelectionChanged();
    }

    /* JADX INFO: renamed from: m */
    public void triggerStatisticsUpdate() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.missionEngine2 != null) {
            gameEngine.missionEngine2.d();
        } else {
            GameEngine.log("No active callbacks");
        }
    }

    /* JADX INFO: renamed from: a */
    public String[] addExtraMapsForPath(String[] strArr, String str) {
        String[] strArrListFilesRecursive;
        GameEngine.log("addExtraMapsForPath: " + str);
        ArrayList arrayList = new ArrayList();
        if (strArr != null) {
            for (String str2 : strArr) {
                arrayList.add(str2);
            }
        }
        if (GameEngine.isDesktop() && "/SD/rusted_warfare_maps".equals(str) && (strArrListFilesRecursive = FileHelper.listFilesRecursive("/SD/rustedWarfare/maps", true)) != null) {
            for (String str3 : strArrListFilesRecursive) {
                arrayList.add("NEW_PATH|maps2/" + str3);
            }
        }
        for (LegacyDisabledMod legacyDisabledMod : getLegacyDisabledModsByPath(str)) {
            arrayList.add("MOD|" + legacyDisabledMod.modInfo.uuid + "/" + legacyDisabledMod.modPath);
        }
        if (strArr == null && arrayList.size() == 0) {
            return null;
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    /* JADX INFO: renamed from: g */
    public ArrayList<LegacyDisabledMod> getLegacyDisabledModsByPath(String str) {
        ArrayList arrayList = new ArrayList();
        for (LegacyDisabledMod legacyDisabledMod : this.invalidMods) {
            boolean z = false;
            if (str.startsWith("mod/") && str.startsWith("mod/" + legacyDisabledMod.modInfo.uuid)) {
                z = true;
            }
            if (!legacyDisabledMod.modInfo.disabled && str.startsWith("/SD/rusted_warfare_maps")) {
                z = true;
            }
            if (z) {
                GameEngine.log("Adding extra map:" + legacyDisabledMod.modId);
                arrayList.add(legacyDisabledMod);
            }
        }
        return arrayList;
    }

    /* JADX INFO: renamed from: n */
    public void clearInvalidMods() {
        this.invalidMods.clear();
    }

    /* JADX INFO: renamed from: a */
    public void addInvalidMod(String str, ModInfo modInfo) {
        LegacyDisabledMod legacyDisabledMod = new LegacyDisabledMod(this);
        legacyDisabledMod.modId = str;
        legacyDisabledMod.modInfo = modInfo;
        if (modInfo.sourceFolder == null) {
            GameEngine.printLog("Skipping:" + str + " as mod sourceFolder is null");
            return;
        }
        String strSubstring = str;
        String str2 = modInfo.sourceFolder;
        if (strSubstring.startsWith(str2)) {
            strSubstring = strSubstring.substring(str2.length());
        } else {
            String strMapPath = FileHelper.mapPath(strSubstring);
            if (strMapPath.startsWith(str2)) {
                strSubstring = strMapPath.substring(str2.length());
                GameEngine.log("Mod path:" + modInfo.sourceFolder + " in map path without tag:" + strSubstring);
            } else {
                GameEngine.printLog("Mod path:" + modInfo.sourceFolder + " not in map path:" + strSubstring);
            }
        }
        legacyDisabledMod.modPath = strSubstring;
        modInfo.isEnabledAndNotHidden = true;
        modInfo.soundMemory++;
        this.invalidMods.add(legacyDisabledMod);
    }

    /* JADX INFO: renamed from: h */
    public ModInfo getLinkedModForFile(String str) {
        if (str.contains("MOD|")) {
            String[] strArrSplit = str.split("/");
            if (strArrSplit.length >= 2) {
                for (int length = strArrSplit.length - 2; length >= 0; length--) {
                    String str2 = strArrSplit[length];
                    if (str2.startsWith("MOD|")) {
                        String strSubstring = str2.substring("MOD|".length());
                        ModInfo modByUuid = getModByUuid(strSubstring);
                        if (modByUuid == null) {
                            GameEngine.log("getLinkedModForFile: Failed to find mod with hash:" + strSubstring);
                            return null;
                        }
                        return modByUuid;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }
}
