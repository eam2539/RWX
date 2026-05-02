package com.corrodinggames.librocket.scripts;

import com.Element;
import com.ElementDocument;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfigParser;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.mod.ModInfo;
import com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/Mods.class */
public class Mods extends ScriptContext {
    Root root;
    Runnable updateModsRunnable = new Runnable() { // from class: com.corrodinggames.librocket.scripts.Mods.1
        @Override // java.lang.Runnable
        public void run() {
            Mods.this.updateMods();
        }
    };
    int checkWorkshopSkip = 0;

    Mods(Root root) {
        this.root = root;
    }

    public DisabledSteamEngine getSteam() {
        DisabledSteamEngine disabledSteamEngineA = DisabledSteamEngine.a();
        if (!disabledSteamEngineA.e()) {
            disabledSteamEngineA.h();
            return null;
        }
        return disabledSteamEngineA;
    }

    public void openWorkshop() {
        GameEngine.getInstance();
        DisabledSteamEngine steam = getSteam();
        if (steam == null) {
            return;
        }
        steam.m();
    }

    public void uploadModAsk(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.isBetaOrPreview()) {
            this.root.showAlert("Workshop uploading is disabled in BETA versions to ensure compatibility for others. Please test and upload this mod with a released version or wait till beta finishes.");
            return;
        }
        if (gameEngine.modManager.getModByUuid(str) == null) {
            this.root.showAlert("Could not find mod:" + str);
        } else {
            if (getSteam() == null) {
                return;
            }
            this.root.showPopup("Are you sure you want to upload to the workshop?", VariableScope.nullOrMissingString, true, "[onenter]Upload:closePopup(); mods.uploadMod('" + str + "');", null);
        }
    }

    public void uploadMod(String str) {
        ModInfo modByUuid = GameEngine.getInstance().modManager.getModByUuid(str);
        if (modByUuid == null) {
            this.root.showAlert("Could not find mod:" + str);
            return;
        }
        DisabledSteamEngine steam = getSteam();
        if (steam == null) {
            return;
        }
        if (modByUuid.steamId == 0) {
            steam.b(modByUuid);
        } else {
            steam.a(modByUuid, false, "Changes.");
        }
    }

    public void viewMod(String str) {
        ModInfo modByUuid = GameEngine.getInstance().modManager.getModByUuid(str);
        if (modByUuid == null) {
            this.root.showAlert("Could not find mod:" + str);
            return;
        }
        DisabledSteamEngine steam = getSteam();
        if (steam == null) {
            return;
        }
        steam.a(modByUuid);
    }

    public void deleteModPopup(String str) {
        ModInfo modByUuid = GameEngine.getInstance().modManager.getModByUuid(str);
        if (modByUuid == null) {
            this.root.showAlert("Could not find mod:" + str);
            return;
        }
        this.root.showPopup(VariableScope.nullOrMissingString, "Are you sure you want to permanently delete '" + modByUuid.getPaddedTitle() + "'? (Note: You can instead disable the mod by unticking it)", true, "[onenter]Delete:closePopup(); mods.deleteMod('" + str + "');", null);
    }

    public void deleteMod(String str) throws ConfigParseException {
        ModInfo modByUuid = GameEngine.getInstance().modManager.getModByUuid(str);
        if (modByUuid == null) {
            this.root.showAlert("Could not find mod:" + str);
        } else if (modByUuid.delete()) {
            reloadModData();
        } else {
            this.root.showAlert("Error failed to delete mod");
        }
    }

    public void setModFilter(String str) {
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        if (activeDocument == null) {
            GameEngine.log("loadMods: No Active Document");
        } else {
            activeDocument.setMetadata("modFilter", str);
            applyModFilter();
        }
    }

    public void applyModFilter() {
        GameEngine gameEngine = GameEngine.getInstance();
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        if (activeDocument == null) {
            GameEngine.log("loadMods: No Active Document");
            return;
        }
        String strTrim = (String) activeDocument.getMetadata("modFilter");
        Element elementById = activeDocument.getElementById("modList");
        if (elementById == null) {
            GameEngine.log("loadMods: Failed to find modList, wrong page?");
            return;
        }
        boolean checkbox = activeDocument.getElementById("onlyEnabledMods").getCheckbox();
        ArrayList<Element> arrayListFindElementsByClassName = elementById.findElementsByClassName("modItem");
        if (strTrim == null || strTrim.trim().equals(VariableScope.nullOrMissingString)) {
            strTrim = null;
        }
        if (strTrim != null) {
            strTrim = strTrim.toLowerCase(Locale.ROOT).trim();
        }
        int i = 0;
        int i2 = 0;
        for (Element element : arrayListFindElementsByClassName) {
            boolean z = false;
            int iIntValue = Utility.parseIntOrNull(element.getAttribute("data_sessionid")).intValue();
            ModInfo modByMapsCount = gameEngine.modManager.getModByMapsCount(iIntValue);
            if (modByMapsCount == null) {
                GameEngine.log("Could not find mod with mod session id: " + iIntValue);
            } else {
                if (strTrim != null) {
                    boolean z2 = false;
                    if (modByMapsCount.getDisplayTitle() != null && modByMapsCount.getDisplayTitle().toLowerCase(Locale.ROOT).contains(strTrim)) {
                        z2 = true;
                    }
                    if (modByMapsCount.getDescription() != null && modByMapsCount.getDescription().toLowerCase(Locale.ROOT).contains(strTrim)) {
                        z2 = true;
                    }
                    if (!z2) {
                        z = true;
                    }
                }
                if (checkbox && modByMapsCount.disabled) {
                    z = true;
                }
            }
            if (z) {
                i++;
                element.compareAndAddClass("modItemFilteredOut");
            } else {
                i2++;
                element.removeClass("modItemFilteredOut");
            }
        }
        String str = VariableScope.nullOrMissingString;
        if (i > 0 && i2 == 0) {
            str = "< No mods found with active filter (" + i + " hidden) >";
        } else if (i > 0) {
            str = "< " + i + " mods hidden with active filter >";
        }
        activeDocument.getElementById("filterStatus").setText(str);
    }

    public void updateMods() {
        this.checkWorkshopSkip++;
        if (this.checkWorkshopSkip > 100) {
            this.checkWorkshopSkip = 0;
            DisabledSteamEngine disabledSteamEngineA = DisabledSteamEngine.a();
            if (disabledSteamEngineA != null) {
                disabledSteamEngineA.k();
            }
        }
    }

    public void refreshModList() {
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        if (activeDocument == null) {
            GameEngine.log("refreshModList: No Active Document");
            return;
        }
        GameEngine.log("refreshModList");
        if (activeDocument.getElementById("modTemplate") == null) {
            GameEngine.log("refreshModList: Failed to find modTemplate, wrong page?");
            return;
        }
        GameEngine.getInstance().modManager.backupModSelection();
        _rememberTempModSelection();
        loadMods();
        _restoreTempModSelection();
    }

    public void loadMods() {
        ArrayList<ModInfo> arrayListLoadAllMods = GameEngine.getInstance().modManager.loadAllMods();
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        if (activeDocument == null) {
            GameEngine.log("loadMods: No Active Document");
            return;
        }
        GameEngine.log("loadMods");
        Element elementById = activeDocument.getElementById("modTemplate");
        Element elementById2 = activeDocument.getElementById("modList");
        if (elementById == null) {
            GameEngine.log("loadMods: Failed to find modTemplate, wrong page?");
            return;
        }
        if (elementById2 == null) {
            GameEngine.log("loadMods: Failed to find modList, wrong page?");
            return;
        }
        this.root.setDocumentUpdate(activeDocument, this.updateModsRunnable);
        String innerRML = elementById.getInnerRML();
        String str = VariableScope.nullOrMissingString;
        int i = 0;
        for (ModInfo modInfo : arrayListLoadAllMods) {
            String displayTitle = modInfo.getDisplayTitle();
            String str2 = VariableScope.nullOrMissingString;
            String strReplace = innerRML.replace("_NAME_", this.root.htmlString(displayTitle)).replace("_ID_", modInfo.uuid);
            String str3 = modInfo.firstError;
            if (str3 == null) {
                str3 = VariableScope.nullOrMissingString;
            } else {
                str2 = str2 + " modItemError";
            }
            if (modInfo.canBeDeleted()) {
                str2 = str2 + " modItemCanBeDeleted";
            }
            if (modInfo.steamId == 0) {
                if (!modInfo.isFromSteam && !modInfo.isBuiltIn) {
                    str2 = str2 + " modItemCanBePublished";
                }
            } else {
                if (!modInfo.isFromSteam) {
                    str2 = str2 + " modItemIsOwner";
                }
                str2 = str2 + " modItemIsPublished";
            }
            if (modInfo.isEnabledAndNotHidden) {
                str2 = str2 + " modItemHasMaps";
            }
            String errorsAndWarnings = modInfo.getErrorsAndWarnings();
            if (errorsAndWarnings == null) {
                errorsAndWarnings = VariableScope.nullOrMissingString;
            }
            i++;
            str = str + strReplace.replace("_ERROR_", this.root.htmlString(str3)).replace("_MESSAGE_", this.root.htmlStringWithNewlines(errorsAndWarnings)).replace("_DESCRIPTION_", this.root.htmlString(modInfo.getDescription())).replace("_CLASS_", str2).replace("_SESSIONID_", VariableScope.nullOrMissingString + modInfo.getMapCount());
        }
        elementById2.setInnerRML(str);
        elementById2.loadCharsetIfNeeded(str);
        for (ModInfo modInfo2 : arrayListLoadAllMods) {
            Element elementById3 = activeDocument.getElementById(modInfo2.uuid);
            if (elementById3 == null) {
                GameEngine.updatePaintTextSizeIfNeeded("Could not find:" + modInfo2.dirName);
            } else {
                elementById3.setCheckbox(!modInfo2.disabled);
            }
        }
        applyModFilter();
    }

    public void saveMods() throws ConfigParseException {
        _saveModsCommon(true);
    }

    private void _rememberTempModSelection() {
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("temp save");
        for (Element element : activeDocument.findElementsByClassName("modSelection")) {
            String id = element.getId();
            if (!id.equals("_ID_")) {
                ModInfo modByUuid = gameEngine.modManager.getModByUuid(id);
                if (modByUuid == null) {
                    GameEngine.printLog("Could not find mod:" + element.getInnerRML());
                } else {
                    boolean z = !element.getCheckbox();
                    if (modByUuid.wasDisabled != z) {
                    }
                    modByUuid.wasDisabled = z;
                    modByUuid.selectionChanged = true;
                }
            }
        }
    }

    private void _restoreTempModSelection() {
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("temp restore");
        for (Element element : activeDocument.findElementsByClassName("modSelection")) {
            String id = element.getId();
            if (id != null && !id.equals(VariableScope.nullOrMissingString) && !id.equals("_ID_")) {
                ModInfo modByUuid = gameEngine.modManager.getModByUuid(id);
                if (modByUuid == null) {
                    GameEngine.printLog("Could not find mod:" + element.getInnerRML() + " id:" + id);
                } else if (modByUuid.selectionChanged) {
                    if (modByUuid.wasDisabled != (!element.getCheckbox())) {
                        element.setCheckbox(!modByUuid.wasDisabled);
                    }
                }
            }
        }
    }

    private void _saveModsCommon(boolean z) throws ConfigParseException {
        boolean z2 = false;
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("savesMods");
        for (Element element : activeDocument.findElementsByClassName("modSelection")) {
            String id = element.getId();
            if (!id.equals("_ID_")) {
                ModInfo modByUuid = gameEngine.modManager.getModByUuid(id);
                if (modByUuid == null) {
                    this.root.showAlert("Could not find mod:" + element.getInnerRML());
                } else {
                    boolean z3 = !element.getCheckbox();
                    if (modByUuid.disabled != z3) {
                        z2 = true;
                    }
                    modByUuid.disabled = z3;
                    modByUuid.wasDisabled = z3;
                }
            }
        }
        if (z2) {
            GameEngine.log("mod changes made");
        } else {
            GameEngine.log("no mod changes made");
        }
        gameEngine.modManager.saveModSelection();
        gameEngine.settingsEngine.save();
        if (z) {
            _saveModsMessages(false);
        }
    }

    private void _saveModsMessages(boolean z) throws ConfigParseException {
        GameEngine gameEngine = GameEngine.getInstance();
        int enabledModCount = gameEngine.modManager.getEnabledModCount(false);
        int enabledModsWithLevelsCount = gameEngine.modManager.getEnabledModsWithLevelsCount();
        if (gameEngine.networkEngine.B) {
            GameEngine.log("savesMods: in network game");
            this.root.showAlert("You are currently in a network game, changes will be checked and applied on next game");
            return;
        }
        if (CustomUnitConfigParser.validateCustomUnitSet(true)) {
            if (enabledModCount == 0) {
                this.root.showAlert("Mod changes saved. Will be used in the next game.");
                return;
            }
            if (z) {
                String str = "Note: " + enabledModCount + " selected mods are still not loaded after reload";
                if (enabledModsWithLevelsCount > 0) {
                    str = "Warning: " + enabledModsWithLevelsCount + " selected mods had errors after reload";
                }
                this.root.showAlert(str);
                return;
            }
            String str2 = "Mod selection saved. But " + enabledModCount + " mod(s) aren't loaded. Load them now?";
            if (!gameEngine.isGameThreadRunning()) {
                str2 = str2 + " (This will end your current game).";
            }
            this.root.showPopup("Reload needed", str2, true, "[onenter]Reload:closePopup(); mods.reloadModData();", null);
            return;
        }
        GameEngine.log("Errors found");
    }

    public void disableAllAsk() {
        this.root.showPopup("Disable all mods?", VariableScope.nullOrMissingString, true, "[onenter]Disable All:closePopup(); mods.disableAll();", null);
    }

    public void disableAll() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.modManager.disableAllMods();
        gameEngine.modManager.saveModSelection();
        gameEngine.settingsEngine.save();
        gameEngine.modManager.applyAndSaveMods();
        loadMods();
    }

    public void reloadModDataAsk() throws ConfigParseException {
        if (GameEngine.getInstance().isGameThreadRunning()) {
            GameEngine.log("Menu active, reloading without asking");
            reloadModData();
        } else {
            this.root.showPopup("Reload all mod data?", VariableScope.nullOrMissingString + "Warning! this will end your current game.", true, "[onenter]Reload:closePopup(); mods.reloadModData();", null);
        }
    }

    public void reloadModData() throws ConfigParseException {
        GameEngine gameEngine = GameEngine.getInstance();
        _saveModsCommon(false);
        gameEngine.modManager.applyAndSaveMods();
        _saveModsMessages(true);
        loadMods();
    }
}
