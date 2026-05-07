package com.corrodinggames.librocket.scripts;

import android.graphics.Color;
import com.Element;
import com.ElementDocument;
import com.corrodinggames.librocket.ButtonAction;
import com.corrodinggames.librocket.GameMainManager;
import com.corrodinggames.librocket.LibRocketManager;
import com.corrodinggames.rts.appFramework.*;
import com.corrodinggames.rts.debug.DebugSocketServer;
import com.corrodinggames.rts.game.GameLogic;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.*;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.network.*;
import com.corrodinggames.rts.gameFramework.network.ChatMessage;
import com.corrodinggames.rts.gameFramework.p2p.P2PLobbyService;
import com.corrodinggames.rts.gameFramework.p2p.P2PRoomAdvertisement;
import com.corrodinggames.rts.gameFramework.platform.FileSelectionCallback;
import com.corrodinggames.rts.gameFramework.platform.PlatformExtension;
import com.corrodinggames.rts.gameFramework.stats.StatGroup;
import com.corrodinggames.rts.gameFramework.stats.StatType;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/Root.class */
public class Root extends ScriptContext {
    public static final boolean DEBUG_TIMING = true;
    public Multiplayer multiplayer;
    public Mods mods;
    ProfilerTimer openDocumentTimer = new ProfilerTimer("openDocument", true);
    SocketConnector threadedGameConnector;
    ElementDocument lastConnectingPopup;
    static ProfilerTimer convertTextStopwatch = new ProfilerTimer("ConvertText", true);
    static ProfilerTimer loadSettingsStopwatch = new ProfilerTimer("LoadSettings", true);
    ArrayList lastSortedDiscoveredServers;
    ArrayList lastSortedDiscoveredP2PRooms;

    public void logDebug(String str) {
        GameEngine.log("ui[debug]: " + str);
    }

    public void logWarn(String str) {
        GameEngine.log("ui[warn]: " + str);
    }

    public void back() {
        this.libRocket.backToLastDocument();
        if (this.libRocket.getActiveDocument() == null) {
            GameEngine.updatePaintTextSizeIfNeeded("back: libRocket.getActiveDocument()==null");
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine == null || !gameEngine.isLoading) {
                GameEngine.updatePaintTextSizeIfNeeded("back: showing main menu!");
                showMainMenu();
            } else {
                GameEngine.updatePaintTextSizeIfNeeded("back: resuming game");
                this.guiEngine.setGamePaused(false);
            }
        }
    }

    public void backOrClose() {
        if (closePopup()) {
            return;
        }
        this.libRocket.backToLastDocument();
    }

    public String fullVersionOnlyStyle() {
        if (GameEngine.getInstance().isDemo) {
            return "notInDemo";
        }
        return VariableScope.nullOrMissingString;
    }

    public void openIfNotDemo(String str, Object obj, String str2) {
        if (GameEngine.getInstance().isDemo) {
            alert(str2);
        } else {
            open(str, obj);
        }
    }

    public String getVersionName() {
        return GameEngine.getInstance().getVersionName();
    }

    public void delayedOpenNoHistory(final String str, Object obj) {
        this.scriptEngine.addRunnableToQueue(new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.1
            @Override // java.lang.Runnable
            public void run() {
                Root.this.libRocket.setDocument(str, null, false);
                Root.this.onShowNewScreen();
            }
        }).framesDelay = 1;
    }

    public void openAfterHelpPopup(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (GameEngine.isAndroid() && !gameEngine.settingsEngine.hasPlayedGameOrSeenHelp) {
            gameEngine.settingsEngine.hasPlayedGameOrSeenHelp = true;
            gameEngine.settingsEngine.save();
            showPopup(VariableScope.nullOrMissingString, "First time playing? Would you like to view the quick help slides?", false, "[onenter]View Help:closePopup(); open('help_quick_mobile.rml');", "Skip:closePopup(); open(" + restrictedString(str) + ");");
            return;
        }
        open(str, null);
    }

    public void open(String str, Object obj) {
        this.openDocumentTimer.a();
        LibRocketManager.loadResourcesTimer.f();
        HashMap<String, Object> map = null;
        if (obj != null) {
            map = new HashMap<>();
            map.put("mode", obj);
        }
        this.libRocket.setDocument(str, map);
        onShowNewScreen();
        this.openDocumentTimer.d();
        LibRocketManager.loadResourcesTimer.e();
    }

    public void onShowNewScreen() {
        this.guiEngine.setGamePaused(true);
    }

    public void resume() {
        this.libRocket.closeActiveDocument();
        this.libRocket.clearHistory();
        this.guiEngine.resumeGame();
    }

    public void resumeNonMenu() {
        this.libRocket.closeActiveDocument();
        this.libRocket.clearHistory();
        this.guiEngine.setGamePaused(false);
    }

    public void startNew(String str) {
        this.guiEngine.loadGame(str);
        this.libRocket.closeActiveDocument();
        this.libRocket.clearHistory();
    }

    public void showRangeValue(String str, String str2, boolean z) {
        String string;
        Element activeElementById = this.libRocket.getActiveElementById(str);
        if (activeElementById == null) {
            logWarn("Could not find:" + str);
            return;
        }
        String attribute = activeElementById.getAttribute("value");
        if (z) {
            string = String.valueOf(attribute);
        } else {
            string = Integer.toString((int) Float.parseFloat(attribute));
        }
        Element activeElementById2 = this.libRocket.getActiveElementById(str2);
        if (activeElementById2 == null) {
            logWarn("Could not find:" + str2);
        } else {
            activeElementById2.setText(string);
        }
    }

    public String getValueById(String str) {
        Element activeElementById = this.libRocket.getActiveElementById(str);
        if (activeElementById == null) {
            logWarn("Could not find:" + str);
            return null;
        }
        return activeElementById.getAttribute("value");
    }

    public void setValueById(String str, String str2) {
        Element activeElementById = this.libRocket.getActiveElementById(str);
        if (activeElementById == null) {
            logWarn("Could not find:" + str);
        } else {
            activeElementById.setAttribute("value", str2);
        }
    }

    public Element getElementById(String str) {
        Element activeElementById = this.libRocket.getActiveElementById(str);
        if (activeElementById == null) {
            logWarn("Could not find:" + str);
            return null;
        }
        return activeElementById;
    }

    public boolean clickElement(Element element) {
        if (element == null) {
            logWarn("element is null");
            return false;
        }
        element.click();
        return true;
    }

    public void directJoinPopup() {
        GameEngine gameEngine = GameEngine.getInstance();
        String str = VariableScope.nullOrMissingString;
        if (gameEngine.settingsEngine.lastNetworkIP != null) {
            str = gameEngine.settingsEngine.lastNetworkIP;
        }
        showInputPopup("Direct Join", "Enter IP address or host name", str, "[onenter]Join:joinServerFromPopup(getPopupText())", null);
    }

    public void joinServerFromPopup(String str) {
        closePopup();
        hideKeyboard();
        if (str == null) {
            logDebug("joinAddress==null");
            return;
        }
        String strTrim = str.trim();
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.settingsEngine.lastNetworkIP = strTrim;
        gameEngine.settingsEngine.save();
        joinServerWithId(strTrim, null);
    }

    public void joinServerWithId(String str, String str2) {
        GameEngine.getInstance().networkEngine.serverAddress = str2;
        joinServer(str);
    }

    public void joinP2PServerWithId(String str) {
        try {
            P2PLobbyService p2PLobbyService = P2PLobbyService.getInstance();
            String strPrepareJoin = p2PLobbyService.prepareJoin(str);
            GameEngine.getInstance().networkEngine.serverAddress = str;
            joinServer(strPrepareJoin);
        } catch (IOException e) {
            showPopup("Connection failed", e.getMessage(), true, (String) null, (String) null);
        }
    }

    public void loadP2PConfig() {
        String savedPeerConfig = P2PLobbyService.getInstance().getSavedPeerConfig();
        setValueById("p2pConfigPath", savedPeerConfig);
    }

    public void joinServer(String str) {
        if (ScriptEngine.inDebugScript && !DebugSocketServer.field_d) {
            return;
        }
        logDebug("joinAddress=" + str);
        this.threadedGameConnector = GameEngine.getInstance().networkEngine.a(str, false, new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.2
            @Override // java.lang.Runnable
            public void run() {
                ScriptEngine.getInstance().addScriptToQueue("joinServerCallback();");
            }
        });
        this.lastConnectingPopup = createAndShowPopup("multiplayerLobby_connecting.rml", null, "Please wait");
    }

    public void joinServerCallback() {
        logDebug("joinServerCallback");
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.threadedGameConnector == null) {
            logDebug("threadedGameConnector==null");
            return;
        }
        closePopupOnly();
        if (this.threadedGameConnector.errorMessage != null) {
            if (this.threadedGameConnector.errorMessage.equals("CANCELLED")) {
                logDebug("Server join cancelled");
                return;
            } else {
                logWarn("Server join failed");
                showPopup("Connection failed", this.threadedGameConnector.errorMessage, true, (String) null, (String) null);
            }
        } else {
            try {
                gameEngine.networkEngine.disconnectNetworking("starting new");
                gameEngine.networkEngine.a(this.threadedGameConnector.connectedSocket);
                logDebug("connected");
                showBattleroom();
            } catch (Exception e) {
                gameEngine.showMessageBox(e.getMessage(), "Connection failed");
                e.printStackTrace();
            }
        }
        this.threadedGameConnector = null;
    }

    public void cancelJoinServer() {
        if (this.threadedGameConnector != null) {
            if (this.threadedGameConnector.a()) {
                closePopup();
                return;
            }
            return;
        }
        closePopup();
    }

    public void alert(String str) {
        showAlert(str);
    }

    public void showAlert(String str) {
        logDebug("alert:" + str);
        if (str == null) {
            str = "null";
        }
        this.libRocket.showMessageBox2(str);
    }

    public void showPopupWithButtons(String str, String str2, boolean z, ButtonAction buttonAction, ButtonAction buttonAction2) {
        logDebug("showPopup:" + str2);
        if (str2 == null) {
            str2 = "null";
        }
        this.libRocket.showDialogWithActions(str, str2, (String) null, buttonAction, buttonAction2, z);
    }

    public void showPopupWithButtonsAndInput(String str, String str2, boolean z, String str3, ButtonAction buttonAction, ButtonAction buttonAction2) {
        logDebug("showPopup:" + str2);
        if (str2 == null) {
            str2 = "null";
        }
        if (str3 == null) {
            str3 = VariableScope.nullOrMissingString;
        }
        if (VariableScope.nullOrMissingString.equals(str3)) {
            this.guiEngine.onUpdate();
        }
        this.libRocket.showDialogInternal(str, str2, str3, buttonAction, buttonAction2, true, z);
    }

    public void showPopup(String str, String str2, boolean z, String str3, String str4) {
        logDebug("showPopup:" + str2);
        if (str2 == null) {
            str2 = "null";
        }
        this.libRocket.showDialog(str, str2, (String) null, str3, str4, z);
    }

    public void showInputPopup(String str, String str2, String str3, String str4, String str5) {
        logDebug("showInputPopup:" + str2);
        if (str2 == null) {
            str2 = "null";
        }
        String str6 = str3;
        if (str6 == null) {
            str6 = VariableScope.nullOrMissingString;
        }
        if (VariableScope.nullOrMissingString.equals(str6)) {
            this.guiEngine.onUpdate();
        }
        this.libRocket.showDialog(str, str2, str6, str4, str5, true);
    }

    public void showInputPopupNonClose(String str, String str2, String str3, String str4, String str5) {
        logDebug("showInputPopup:" + str2);
        if (str2 == null) {
            str2 = "null";
        }
        String str6 = str3;
        if (str6 == null) {
            str6 = VariableScope.nullOrMissingString;
        }
        if (VariableScope.nullOrMissingString.equals(str6)) {
            this.guiEngine.onUpdate();
        }
        this.libRocket.showDialogInternal(str, str2, str6, str4, str5, true, false);
    }

    public ElementDocument getPopup() {
        return this.libRocket.getCurrentPopup();
    }

    public ElementDocument getAlertOrPopup() {
        return this.libRocket.getTopmostPopup();
    }

    public boolean closePopup() {
        return this.libRocket.canClosePopupOrAlert();
    }

    public boolean closePopupOnly() {
        return this.libRocket.closePopup();
    }

    public boolean closeAlertOnly() {
        return this.libRocket.closeAlert();
    }

    public String getPopupText() {
        return this.libRocket.getTextInputValue();
    }

    public void showHostOptions() {
        this.libRocket.showDialog("Host game", i("menus.hostgame.info_pc"), (String) null, "Host Private:closePopup();hostStart(false);", "Host Public:closePopup();hostStart(true);", true);
    }

    public void hostStartFromPopup(boolean z) throws ConfigParseException {
        ElementDocument topmostDocument = this.libRocket.getTopmostDocument();
        Element elementById = topmostDocument.getElementById("password");
        String str = null;
        if (elementById == null) {
            logWarn("hostStartFromPopup: password==null");
        } else {
            String value = elementById.getValue();
            if (value != null && !value.trim().equals(VariableScope.nullOrMissingString)) {
                str = value;
            }
        }
        boolean checkbox = topmostDocument.getElementById("useMods").getCheckbox();
        closePopup();
        hostStartWithPasswordAndMods(z, str, checkbox);
    }

    public void hostStart(boolean z) throws ConfigParseException {
        GameEngine.updatePaintTextSizeIfNeeded("old version of hostStart");
        hostStartWithPassword(z, null);
    }

    public void hostStartWithPassword(boolean z, String str) throws ConfigParseException {
        GameEngine.updatePaintTextSizeIfNeeded("old version of hostStartWithPassword");
        hostStartWithPasswordAndMods(z, str, true);
    }

    public void hostStartWithPasswordAndMods(boolean z, String str, boolean z2) throws ConfigParseException {
        GameEngine gameEngine = GameEngine.getInstance();
        P2PLobbyService.getInstance().stopSession();
        gameEngine.networkEngine.disconnectNetworking("starting new");
        gameEngine.networkEngine.n = str;
        gameEngine.networkEngine.o = z2;
        gameEngine.networkEngine.q = z;
        if (gameEngine.networkEngine.startServerHosting(false)) {
            logDebug("-Hosting-");
            logDebug("using password: " + (gameEngine.networkEngine.n != null));
            logDebug("using mods: " + (gameEngine.networkEngine.o));
            logDebug("public: " + (gameEngine.networkEngine.q));
            String strAv = gameEngine.networkEngine.av();
            if (strAv != null && !FileHelper.fileExists(strAv)) {
                GameEngine.updatePaintTextSizeIfNeeded("hostStart: map does not exist: " + strAv + " reseting");
                strAv = null;
            }
            if (strAv == null) {
                gameEngine.networkEngine.roomSettings.gameModeType = GameModeType.values()[0];
                gameEngine.networkEngine.az = "maps/skirmish/[p8]Many Islands (8p).tmx";
                gameEngine.networkEngine.roomSettings.mapPath = "[p8]Many Islands (8p).tmx";
            }
            this.libRocket.setDocument("battleroom.rml", null);
            return;
        }
        logWarn("hosting failed");
    }

    public void hostP2PStartFromPopup() throws ConfigParseException {
        ElementDocument topmostDocument = this.libRocket.getTopmostDocument();
        Element elementById = topmostDocument.getElementById("password");
        String str = null;
        if (elementById != null) {
            String value = elementById.getValue();
            if (value != null && !value.trim().equals(VariableScope.nullOrMissingString)) {
                str = value.trim();
            }
        }
        boolean checkbox = topmostDocument.getElementById("useMods").getCheckbox();
        closePopup();
        hostP2PStartWithPasswordAndMods(str, checkbox);
    }

    public void loadP2PHostPopup() {
    }

    public void hostP2PStartWithPasswordAndMods(String str, boolean z) throws ConfigParseException {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.networkEngine.disconnectNetworking("starting new p2p");
        gameEngine.networkEngine.n = str;
        gameEngine.networkEngine.o = z;
        gameEngine.networkEngine.q = false;
        gameEngine.networkEngine.useMasterServer = false;
        if (!gameEngine.networkEngine.startServerHosting(false)) {
            logWarn("p2p hosting failed");
            return;
        }
        String strAv = gameEngine.networkEngine.av();
        if (strAv != null && !FileHelper.fileExists(strAv)) {
            strAv = null;
        }
        if (strAv == null) {
            gameEngine.networkEngine.roomSettings.gameModeType = GameModeType.values()[0];
            gameEngine.networkEngine.az = "maps/skirmish/[p8]Many Islands (8p).tmx";
            gameEngine.networkEngine.roomSettings.mapPath = "[p8]Many Islands (8p).tmx";
        }
        try {
            P2PLobbyService.getInstance().startIfNeeded();
            P2PLobbyService.getInstance().hostCurrentServer();
            this.libRocket.setDocument("battleroom.rml", null);
        } catch (IOException e) {
            gameEngine.networkEngine.disconnectNetworking("p2p host setup failed");
            showPopup("P2P setup failed", e.getMessage(), true, (String) null, (String) null);
        }
    }

    public void exit() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.settingsEngine.numLoadsSinceRunningGameOrNormalExit != 0) {
            gameEngine.settingsEngine.numLoadsSinceRunningGameOrNormalExit = 0;
            gameEngine.settingsEngine.save();
        }
        this.scriptEngine.addRunnableToQueue(new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.3
            @Override // java.lang.Runnable
            public void run() {
                Root.this.guiEngine.setMouseGrabbed(true);
            }
        });
    }

    public String getMapDetails(String str) {
        return "hello 123";
    }

    public String getHTMLMapNameFromPath(String str) {
        return htmlString(getMapNameFromPath(str));
    }

    public String getMapNameFromPath(String str) {
        return convertMapName(new File(str).getName());
    }

    public String convertMapName(String str) {
        return Locale.translateMapName(convertMapNameWithoutTranslation(str));
    }

    public String convertMapNameWithoutTranslation(String str) {
        return LevelSelectActivity.getMapName(str);
    }

    public String getHTMLMapThumbnail(String str) {
        return escapedString(getMapThumbnail(str));
    }

    public String getMapThumbnail(String str) {
        if (str.startsWith("saves/")) {
        }
        String mapThumbnail = AppFrameworkUtils.getMapThumbnail(str);
        String str2 = "thumbnail:assets:" + mapThumbnail;
        if (!FileHelper.fileExists(mapThumbnail)) {
            if (GameEngine.isDebugVersionStatic2) {
                GameEngine.printLog("getMapThumbnail: Failed to find: " + mapThumbnail);
                return "drawable:error_missingmap.png";
            }
            return "drawable:error_missingmap.png";
        }
        return str2;
    }

    public boolean isMapSkirmish(String str) {
        return LevelSelectActivity.isSkirmishMap(str);
    }

    public void showLevelOptions() {
        GameEngine gameEngine = GameEngine.getInstance();
        String str = (String) this.libRocket.getActiveDocumentMetadata("mode");
        if (str == null) {
            GameEngine.logWarningAndStack("levelPath==null");
            return;
        }
        boolean z = true;
        if (!isMapSkirmish(str)) {
            z = false;
        }
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        Iterator it = activeDocument.findElementsByClassName("skirmishOnly").iterator();
        while (it.hasNext()) {
            ((Element) it.next()).show(z);
        }
        Element elementById = activeDocument.getElementById("advancedButton");
        if (elementById != null) {
            elementById.show(z || LevelSelectActivity.isFromSdCard(str));
        }
        activeDocument.getElementById("aiDifficulty").setValue(VariableScope.nullOrMissingString + gameEngine.settingsEngine.aiDifficulty);
    }

    public void loadConfigAndStartSwitchToAdvanced(String str) throws ConfigParseException {
        GameEngine.getInstance().isGameStarted = false;
        loadConfigCommon(str, true);
        _startAdvancedMode(false);
    }

    private void _startAdvancedMode(boolean z) throws ConfigParseException {
        boolean zS;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.networkEngine.disconnectNetworking("starting singleplayer");
        gameEngine.networkEngine.playerName = "You";
        gameEngine.networkEngine.o = true;
        if (z) {
            zS = gameEngine.networkEngine.R();
        } else {
            zS = gameEngine.networkEngine.S();
        }
        if (zS) {
            logDebug("started startSinglePlayerServer");
            GameRoomSettings gameRoomSettingsE = gameEngine.networkEngine.e();
            if (gameRoomSettingsE != null) {
                gameRoomSettingsE.aiDifficulty = gameEngine.settingsEngine.aiDifficulty;
                gameEngine.networkEngine.a(gameRoomSettingsE);
            }
            this.libRocket.setDocument("battleroom.rml", null);
            return;
        }
        logWarn("failed startSinglePlayerServer");
    }

    public void loadConfigAndStartNewSandbox(String str) {
        _loadConfigAndStartNewSandboxCommon(str, true);
    }

    public void loadConfigAndStartNewSandboxInAdvanced(String str) throws ConfigParseException {
        GameEngine.log("loadConfigAndStartNewSandboxInAdvanced");
        _loadConfigAndStartNewSandboxCommon(str, false);
        _startAdvancedMode(true);
        GameEngine.log("editorMode:" + GameEngine.getInstance().isGameStarted);
    }

    private void _loadConfigAndStartNewSandboxCommon(String str, boolean z) {
        if (str.startsWith("saves/")) {
            GameEngine.log("Starting sandbox from save: " + str);
            loadGame(str.substring("saves/".length()));
        } else {
            GameEngine.log("Starting sandbox from map: " + str);
            loadConfigCommon(str, false);
        }
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.tileMap.fogEnabled = false;
        gameEngine.gameUI.clearSelection();
        gameEngine.isGameStarted = true;
        if (z) {
            this.guiEngine.resumeGame();
        }
        this.libRocket.closeActiveDocument();
        this.libRocket.clearHistory();
    }

    public void loadConfigAndStartNew(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.isGameStarted = false;
        gameEngine.networkEngine.disconnectNetworking("starting singleplayer");
        loadConfigCommon(str, false);
        this.guiEngine.resumeGame();
        this.libRocket.closeActiveDocument();
        this.libRocket.clearHistory();
    }

    public void loadConfigCommon(String str, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        gameEngine.settingsEngine.aiDifficulty = activeDocument.getElementById("aiDifficulty").getValueAsInt(0).intValue();
        gameEngine.settingsEngine.save();
        this.guiEngine.endGame(true);
        this.guiEngine.showAbout(false);
        boolean zIsMapSkirmish = isMapSkirmish(str);
        int iIntValue = activeDocument.getElementById("numberOfAIs").getValueAsInt(4).intValue();
        boolean z2 = true;
        int iIntValue2 = activeDocument.getElementById("aiTeams").getValueAsInt(1).intValue();
        int i = iIntValue2 - 1;
        if (iIntValue2 == 5) {
            i = 0;
            z2 = false;
        }
        LevelSelectActivity.startNewGame(str, zIsMapSkirmish, iIntValue, i, z2, z);
    }

    public void showMapPopup(String str, String str2) {
        ElementDocument elementDocumentCreatePopupWithRML = this.libRocket.createPopupWithRML("levelSelect.rml", (Object) str, "Map Select", false);
        if (elementDocumentCreatePopupWithRML != null) {
            elementDocumentCreatePopupWithRML.setMetadata("mapClickFunction", str2);
            Iterator it = elementDocumentCreatePopupWithRML.findElementsByClassName("noStyleInPopup").iterator();
            while (it.hasNext()) {
                ((Element) it.next()).setAttribute("class", VariableScope.nullOrMissingString);
            }
            if (showMapsWithDoc(elementDocumentCreatePopupWithRML)) {
                GameEngine.log("showMapsWithDoc passed");
                this.libRocket.canClosePopupOrAlert();
                this.libRocket.showPopupFromDocument(elementDocumentCreatePopupWithRML);
            }
        }
    }

    public void refreshAfterFileImport() {
        GameEngine.log("refreshAfterFileImport");
        GameEngine.getInstance().modManager.loadAllMods();
        this.libRocket.reloadDocument();
    }

    public boolean showMaps() {
        return showMapsWithDoc(this.libRocket.getActiveOrPopup());
    }

    public boolean showMapsWithDoc(ElementDocument elementDocument) {
        String[] strArrAddExtraMapsForPath;
        String str;
        String str2;
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("showMaps");
        if (elementDocument == null) {
            GameEngine.log("showMaps: elementDocument==null");
            return false;
        }
        Element elementById = elementDocument.getElementById("mapTemplateHolder");
        Element elementById2 = elementDocument.getElementById("mapHolder");
        String innerRML = elementById.getInnerRML();
        String str3 = VariableScope.nullOrMissingString;
        String str4 = (String) elementDocument.getMetadata("mode");
        String str5 = (String) elementDocument.getMetadata("mapClickFunction");
        boolean zEquals = str4.equals("saves");
        boolean zEquals2 = str4.equals("replays");
        boolean zStartsWith = str4.startsWith("/SD/");
        if (zEquals) {
            strArrAddExtraMapsForPath = ManageReplaysActivity.getReplayFiles();
            if (strArrAddExtraMapsForPath == null) {
                gameEngine.alert("No saves", 1);
                return false;
            }
        } else if (zEquals2) {
            strArrAddExtraMapsForPath = ReplayBrowserActivity.getReplayFiles();
            if (!gameEngine.settingsEngine.saveMultiplayerReplays) {
                alert("Note: Multiplayer replay recordings are not turned on. You can enable them in the settings.");
            }
            if (strArrAddExtraMapsForPath == null) {
                if (gameEngine.settingsEngine.saveMultiplayerReplays) {
                    gameEngine.alert("No replays yet", 1);
                    return false;
                }
                return false;
            }
        } else {
            strArrAddExtraMapsForPath = gameEngine.modManager.addExtraMapsForPath(FileHelper.listFilesRecursive(str4, true), str4);
            if (strArrAddExtraMapsForPath == null) {
                gameEngine.alert("Could not find folder: " + FileHelper.convertAbstractPath(str4), 1);
                return false;
            }
        }
        String str6 = str4 + "/";
        int i = 0;
        for (String str7 : strArrAddExtraMapsForPath) {
            String strConvertMapName = convertMapName(str7);
            boolean zIsDemoMap = LevelSelectActivity.isDemoMap(str7, str6 + str7);
            String str8 = strConvertMapName + VariableScope.nullOrMissingString;
            if (gameEngine.isDemo && !zIsDemoMap) {
                str8 = "[LOCKED] " + str8;
            }
            String strReplace = innerRML.replace("_NAME_", htmlString(str8));
            if (zEquals) {
                str = "loadGame(" + escapedString(str7) + ")";
                str2 = "loadGameEdit(" + escapedString(str7) + ")";
            } else if (zEquals2) {
                str = "loadReplay(" + escapedString(str7) + ")";
                str2 = "loadReplayEdit(" + escapedString(str7) + ")";
            } else {
                str = "open('levelOptions.rml', " + escapedString(str6 + str7) + ")";
                str2 = VariableScope.nullOrMissingString;
            }
            if (str5 != null) {
                str = str5 + "(" + escapedString(str6 + str7) + ")";
                str2 = VariableScope.nullOrMissingString;
            }
            String strReplace2 = strReplace.replace("_ONCLICK_", str).replace("_ONCLICKEDIT_", str2);
            String str9 = "thumbnail:assets:";
            int i2 = 18;
            if (zStartsWith) {
                i2 = 2;
            }
            if (i > i2) {
                str9 = "lazy:" + str9;
            }
            String mapThumbnail = AppFrameworkUtils.getMapThumbnail(str6 + str7);
            if (GameEngine.isDebugVersionStatic2) {
            }
            String str10 = str9 + mapThumbnail;
            if (!FileHelper.fileExists(mapThumbnail)) {
                if (GameEngine.isDebugVersionStatic2) {
                    GameEngine.printLog("List: Failed to find: " + mapThumbnail + " after converting:" + mapThumbnail + " ( " + str10 + " )");
                }
                str10 = "drawable:error_missingmap.png";
            }
            if (zEquals || zEquals2) {
                str10 = VariableScope.nullOrMissingString;
            }
            i++;
            str3 = str3 + strReplace2.replace("_IMG_", htmlString(str10));
        }
        elementById2.setInnerRML(str3);
        elementById2.loadCharsetIfNeeded(str3);
        if (zEquals || zEquals2) {
            elementById2.addClass("savesOnly");
        } else {
            elementById2.addClass("notSavesOnly");
        }
        if (zStartsWith && str5 == null && GameEngine.isAndroid()) {
            elementDocument.addClass("showImportButton");
            return true;
        }
        return true;
    }

    public void convertTextOnPage() {
        GameEngine.getInstance();
        logDebug("convertTextOnPage");
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        if (isMobile()) {
            activeDocument.addClass("mobile");
        }
        if (this.libRocket.getHeight() < 800) {
            activeDocument.addClass("smallScreen");
        }
        convertTextStopwatch.a();
        for (Element element : activeDocument.getAllNestedChildren()) {
            int numAttributes = element.getNumAttributes();
            for (int i = 0; i < numAttributes; i++) {
                String attributeKey = element.getAttributeKey(i);
                String attributeValue = element.getAttributeValue(i);
                if (attributeKey != null) {
                    if (attributeKey.equals("nestedclone") && !attributeValue.equalsIgnoreCase("false")) {
                        GameEngine.log("nested clone:" + element.getId());
                        element.setAttribute(attributeKey, "false");
                        Element elementM29clone = element.m29clone();
                        element.prependChild(elementM29clone);
                        elementM29clone.removeReference();
                    } else if (attributeKey.equals("childclone") && !attributeValue.equalsIgnoreCase("false")) {
                        element.setAttribute(attributeKey, "false");
                        if (element.getNumChildren() < 1) {
                            GameEngine.log("child clone failed no children:" + element.getId());
                        }
                        Element elementM29clone2 = element.getChild(0).m29clone();
                        elementM29clone2.addClass("clone");
                        element.prependChild(elementM29clone2);
                        elementM29clone2.removeReference();
                    } else {
                        String textVariables = this.libRocket.parseTextVariables(attributeValue);
                        if (textVariables != null) {
                            GameEngine.log("convertTextOnPage:" + attributeKey + ": '" + attributeValue + "' to '" + textVariables + "'");
                            if (attributeKey.equals("_html")) {
                                GameEngine.log("setting html:" + attributeKey);
                                element.setInnerRML(textVariables);
                            } else {
                                if (attributeKey.startsWith("_")) {
                                    attributeKey = attributeKey.substring("_".length());
                                    GameEngine.log("converted key to:" + attributeKey);
                                }
                                element.setAttribute(attributeKey, textVariables);
                            }
                        }
                    }
                }
            }
            if (activeDocument.translatedToUnicode) {
                String tagName = element.getTagName();
                if (tagName.equals("p") || tagName.startsWith("h") || tagName.startsWith("label") || tagName.startsWith("button") || tagName.startsWith("select")) {
                    element.loadCharsetIfNeededWithCurrentText();
                }
            }
        }
        convertTextStopwatch.d();
    }

    public void keyBindingPopup_apply(boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        ElementDocument currentPopup = this.libRocket.getCurrentPopup();
        if (currentPopup == null) {
            logWarn("showKeyBindingActionPopup: popup==null");
            return;
        }
        String[] strArrSplit = ((String) currentPopup.getMetadata("mode")).split(":");
        int i = Integer.parseInt(strArrSplit[0]);
        int i2 = Integer.parseInt(strArrSplit[1]);
        KeyBinding keyBinding = (KeyBinding) gameEngine.inputController.al.get(i);
        if (!z) {
            Object metadata = currentPopup.getMetadata("lastKeyPressed");
            if (metadata == null) {
                logWarn("keyBindingPopup_apply: no last key entered");
                return;
            }
            int iIntValue = ((Integer) metadata).intValue();
            int iIntValue2 = 0;
            Object metadata2 = currentPopup.getMetadata("lastKeyModifiersPressed");
            if (metadata2 != null) {
                iIntValue2 = ((Integer) metadata2).intValue();
            }
            keyBinding.a(iIntValue, i2, iIntValue2, true);
        } else {
            keyBinding.a(0, i2, 0, true);
        }
        showKeyBinding();
        closePopup();
    }

    public void keyBindingPopup_onKeydown(int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        ElementDocument currentPopup = this.libRocket.getCurrentPopup();
        if (currentPopup == null) {
            logWarn("showKeyBindingActionPopup: popup==null");
            return;
        }
        Element elementById = currentPopup.getElementById("keyBindMessage");
        if (elementById == null) {
            logWarn("showKeyBindingActionPopup: keyBindMessage==null");
            return;
        }
        int modifiers = this.guiEngine.getModifiers();
        String str = VariableScope.nullOrMissingString + GameEngine.getModifierString(modifiers);
        if (i == 111) {
        }
        if (i == 0) {
            logWarn("keyBindingPopup_onKeydown: skipping keycode 0");
            return;
        }
        if (!gameEngine.isModifierKey(i)) {
            currentPopup.setMetadata("lastKeyPressed", Integer.valueOf(i));
            currentPopup.setMetadata("lastKeyModifiersPressed", Integer.valueOf(modifiers));
            String str2 = str + SlickToAndroidKeycodes.a(i);
            keyBindingPopup_apply(false);
            return;
        }
        elementById.setText("Key: " + str);
    }

    public void showKeyBindingPopup() {
        GameEngine gameEngine = GameEngine.getInstance();
        ElementDocument currentPopup = this.libRocket.getCurrentPopup();
        if (currentPopup == null) {
            logWarn("showKeyBindingActionPopup: popup==null");
            return;
        }
        Element elementById = currentPopup.getElementById("keyBindMessage");
        if (elementById == null) {
            logWarn("showKeyBindingActionPopup: keyBindMessage==null");
            return;
        }
        String[] strArrSplit = ((String) currentPopup.getMetadata("mode")).split(":");
        int i = Integer.parseInt(strArrSplit[0]);
        Integer.parseInt(strArrSplit[1]);
        elementById.setText("Press a key..");
    }

    public String getKeyBindingAction(int i, KeyBinding keyBinding, int i2) {
        return "createAndShowPopup('settingsKeyBindingSet.rml', " + escapedString(i + ":" + i2) + ", " + escapedString(keyBinding.a) + "); showKeyBindingPopup();";
    }

    public void backWarnIfOverlappingKeyBinding() {
        if (((Boolean) this.libRocket.getActiveDocument().getMetadata("hasOverlappingKeys", false)).booleanValue()) {
            showPopupWithButtons(null, "One or more keys are overlapping and have been highlighted in red. These can cause problems.", false, new ButtonAction("Ignore", new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.4
                @Override // java.lang.Runnable
                public void run() {
                    Root.this.closePopup();
                    Root.this.back();
                }
            }), new ButtonAction("Fix", new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.5
                @Override // java.lang.Runnable
                public void run() {
                    Root.this.closePopup();
                }
            }));
        } else {
            back();
        }
    }

    public void showKeyBinding() {
        GameEngine gameEngine = GameEngine.getInstance();
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        activeDocument.setMetadata("event_onkeydown", "keyBindingPopup_onKeydown");
        TableData tableData = new TableData();
        ArrayList arrayList = tableData.rows;
        ArrayList arrayList2 = gameEngine.inputController.al;
        boolean z = false;
        for (int i = 0; i < arrayList2.size(); i++) {
            KeyBinding keyBinding = (KeyBinding) arrayList2.get(i);
            if (keyBinding.b) {
                TableRow tableRow = new TableRow();
                tableRow.addCell(keyBinding.a);
                if (keyBinding.d()) {
                    tableRow.addClass("rowHeader");
                } else {
                    for (int i2 = 0; i2 <= 1; i2++) {
                        boolean zA = gameEngine.inputController.a(keyBinding, i2);
                        TableCell tableCellAddCell = tableRow.addCell(keyBinding.b(i2));
                        tableCellAddCell.setLibrocketOnClick(getKeyBindingAction(i, keyBinding, i2));
                        if (zA) {
                            tableCellAddCell.color = -65536;
                            z = true;
                        }
                    }
                }
                arrayList.add(tableRow);
            }
        }
        activeDocument.setMetadata("hasOverlappingKeys", Boolean.valueOf(z));
        refreshTable("keysDiv", tableData);
    }

    public void loadSettings() {
        GameEngine gameEngine = GameEngine.getInstance();
        loadSettingsStopwatch.a();
        logDebug("loadSettings");
        for (Element element : this.libRocket.getActiveElementById("body").getAllNestedChildren()) {
            if (element.getAttribute("data-settings") != null) {
                String id = element.getId();
                String attribute = element.getAttribute("type", "unknown");
                String valueDynamic = gameEngine.settingsEngine.getValueDynamic(id);
                if (attribute.equals("checkbox")) {
                    if (Boolean.parseBoolean(valueDynamic)) {
                        element.setAttribute("checked", VariableScope.nullOrMissingString);
                    } else {
                        element.setAttribute("checked", null);
                    }
                } else {
                    element.setAttribute("value", valueDynamic);
                }
            }
        }
        loadSettingsStopwatch.d();
    }

    public void loadLeaderboard() {
        GameEngine gameEngine = GameEngine.getInstance();
        logDebug("loadLeaderboard");
        Element activeElementById = this.libRocket.getActiveElementById("leaderboardType");
        Element activeElementById2 = this.libRocket.getActiveElementById("leaderboardGrouping");
        if (activeElementById == null || activeElementById2 == null) {
            GameEngine.printLog("loadLeaderboard: Failed to find elements. (For page: " + this.libRocket.getActiveDocumentPath() + ")");
        } else {
            activeElementById.setAttribute("value", gameEngine.teamStats.getStatType().name());
            activeElementById2.setAttribute("value", gameEngine.teamStats.getStatGroup().name());
        }
    }

    public void saveLeaderboard() {
        GameEngine gameEngine = GameEngine.getInstance();
        StatType statType = StatType.none;
        StatGroup statGroup = StatGroup.player;
        logDebug("saveLeaderboard");
        Element activeElementById = this.libRocket.getActiveElementById("leaderboardType");
        Element activeElementById2 = this.libRocket.getActiveElementById("leaderboardGrouping");
        if (activeElementById == null || activeElementById2 == null) {
            GameEngine.printLog("saveLeaderboard: Failed to find elements. (For page: " + this.libRocket.getActiveDocumentPath() + ")");
        } else {
            gameEngine.setupTeamStats(StatType.valueOf(activeElementById.getAttribute("value")), StatGroup.valueOf(activeElementById2.getAttribute("value")));
        }
    }

    public void applyResolution() {
        this.guiEngine.applyResolution();
    }

    public void updateRenderScaleInSettings(boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        Element activeElementById = this.libRocket.getActiveElementById("uiRenderScale");
        Element activeElementById2 = this.libRocket.getActiveElementById("renderDensity");
        Float valueAsFloat = null;
        Float valueAsFloat2 = null;
        if (activeElementById == null) {
            logDebug("updateRenderScaleInSettings: uiRenderScale==null");
        } else {
            valueAsFloat = activeElementById.getValueAsFloat(Float.valueOf(1.0f));
            this.libRocket.getActiveElementById("uiRenderScaleDisplay").compareAndSetText("x" + Utility.formatDouble(valueAsFloat.floatValue() + 0.01f, 1));
        }
        if (activeElementById2 == null) {
            logDebug("updateRenderScaleInSettings: renderDensity==null");
        } else {
            valueAsFloat2 = activeElementById2.getValueAsFloat(Float.valueOf(1.0f));
            this.libRocket.getActiveElementById("renderDensityDisplay").compareAndSetText("x" + Utility.formatDouble(valueAsFloat2.floatValue() + 0.01f, 1));
        }
        if (z) {
            float f = gameEngine.settingsEngine.uiRenderScale;
            if (valueAsFloat != null) {
                gameEngine.settingsEngine.uiRenderScale = valueAsFloat.floatValue();
            }
            float f2 = gameEngine.settingsEngine.renderDensity;
            if (valueAsFloat2 != null) {
                gameEngine.settingsEngine.renderDensity = valueAsFloat2.floatValue();
            }
            try {
                applyResolution();
                gameEngine.settingsEngine.uiRenderScale = f;
                gameEngine.settingsEngine.renderDensity = f2;
            } catch (Throwable th) {
                gameEngine.settingsEngine.uiRenderScale = f;
                gameEngine.settingsEngine.renderDensity = f2;
                throw th;
            }
        }
    }

    public void closeSettings() {
        applyResolution();
    }

    public void saveSettings() {
        String attribute;
        GameEngine gameEngine = GameEngine.getInstance();
        logDebug("saveSettings");
        for (Element element : this.libRocket.getActiveElementById("body").getAllNestedChildren()) {
            if (element.getAttribute("data-settings") != null) {
                String id = element.getId();
                if (element.getAttribute("type", "unknown").equals("checkbox")) {
                    String attribute2 = element.getAttribute("checked");
                    if (attribute2 == null || "false".equals(attribute2)) {
                        attribute = "false";
                    } else {
                        attribute = "true";
                    }
                } else {
                    attribute = element.getAttribute("value");
                }
                try {
                    gameEngine.settingsEngine.setValueDynamic(id, attribute);
                } catch (NumberFormatException e) {
                    alert("Error:" + e.getMessage());
                }
            }
        }
        this.guiEngine.applyResolution();
        FileChangeEngine.a();
        Locale.reload();
        this.guiEngine.postUpdate();
    }

    public String hideStyle(boolean z) {
        if (z) {
            return VariableScope.nullOrMissingString;
        }
        return "display:none;";
    }

    public String hideIf(boolean z) {
        return hideClass(!z);
    }

    public String hideUnless(boolean z) {
        return hideClass(z);
    }

    public String hideClass(boolean z) {
        if (z) {
            return VariableScope.nullOrMissingString;
        }
        return "hide";
    }

    public String hideIfMobile() {
        if (GameEngine.isAndroid()) {
            return "hide";
        }
        return VariableScope.nullOrMissingString;
    }

    public boolean canResume() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine != null && gameEngine.loadNewGame && !gameEngine.reloadMap) {
            return true;
        }
        return false;
    }

    public boolean isMobile() {
        return GameEngine.isAndroid();
    }

    public boolean isIOS() {
        return GameEngine.isDebugVersionStatic2;
    }

    public boolean isDesktop() {
        return GameEngine.isPC();
    }

    public boolean isMac() {
        return GameLogic.isSandboxEnabled;
    }

    public boolean hasModSupport() {
        return !GameEngine.isDebugVersionStatic2;
    }

    public boolean usingMods() {
        if (!GameEngine.isDebugVersionStatic2 || GameEngine.getInstance().modManager.getStorageModsCount() > 0) {
            return true;
        }
        return false;
    }

    public boolean hasWorkshopSupport() {
        return GameEngine.isPC();
    }

    public boolean hasReloadSupport() {
        return !GameEngine.isDebugVersionStatic2;
    }

    String restrictedString(String str) {
        if (str == null) {
            return null;
        }
        return "'" + str.replace("'", ".").replace("\"", ".").replace("(", ".").replace(")", ".").replace(",", ".").replace("<", ".").replace(">", ".") + "'";
    }

    String escapedString(String str) {
        return "'" + str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;").replace("${", "$ {") + "'";
    }

    String htmlString(String str) {
        return VariableScope.nullOrMissingString + str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("${", "$ {") + VariableScope.nullOrMissingString;
    }

    String htmlStringWithNewlines(String str) {
        return VariableScope.nullOrMissingString + htmlString(str).replace("\n", "<br/>") + VariableScope.nullOrMissingString;
    }

    public void checkServerListScroll() {
        Element activeElementById = this.libRocket.getActiveElementById("serverScrollDiv");
        if (activeElementById == null) {
            logWarn("serverScrollDiv==null");
            return;
        }
        Boolean bool = (Boolean) this.libRocket.getActiveDocumentMetadata("showFullServerList");
        if (bool == null) {
            bool = false;
        }
        if (!bool.booleanValue() && activeElementById.getScrollTop() > 200.0f) {
            this.libRocket.getActiveDocument().setMetadata("showFullServerList", true);
            this.scriptEngine.addScriptToQueue("displayServerList()");
        }
    }

    public void refreshServerList() {
        refreshServerListRaw("serverListData", "serverRowTemplateHolder", "refreshButton");
    }

    public void displayServerList() {
        displayServerListRaw("serverListData", "serverRowTemplateHolder", "refreshButton");
    }

    public void refreshP2PServerList() {
        Element activeElementById = this.libRocket.getActiveElementById("refreshButton");
        if (activeElementById != null) {
            activeElementById.setText("Refreshing");
        }
        try {
            P2PLobbyService.getInstance().startIfNeeded();
            P2PLobbyService.getInstance().requestRefresh();
        } catch (IOException e) {
            showPopup("P2P refresh failed", e.getMessage(), true, (String) null, (String) null);
        }
        displayP2PServerList();
    }

    public void displayP2PServerList() {
        displayP2PServerListRaw("serverListData", "serverRowTemplateHolder", "refreshButton");
    }

    public void displayP2PServerListRaw(String str, String str2, String str3) {
        GameEngine gameEngine = GameEngine.getInstance();
        Element activeElementById = this.libRocket.getActiveElementById(str);
        Element activeElementById2 = this.libRocket.getActiveElementById(str2);
        if (activeElementById == null) {
            return;
        }
        ArrayList<P2PRoomAdvertisement> rooms = P2PLobbyService.getInstance().getRooms();
        this.lastSortedDiscoveredP2PRooms = rooms;
        String str4 = Locale.get("menus.lobby.gameState.battleroom", new Object[0]);
        String str5 = Locale.get("menus.lobby.gameState.ingame", new Object[0]);
        String str6 = Locale.get("menus.lobby.gameState.chat", new Object[0]);
        if (activeElementById.getNumChildren() > rooms.size()) {
            for (int numChildren = activeElementById.getNumChildren() - 1; numChildren >= rooms.size(); numChildren--) {
                activeElementById.removeChild(activeElementById.getChild(numChildren));
            }
        }
        int i = 0;
        for (P2PRoomAdvertisement p2PRoomAdvertisement : rooms) {
            Element child = i < activeElementById.getNumChildren() ? activeElementById.getChild(i) : null;
            if (child != null && child.hasClassName("serverRowMessage")) {
                activeElementById.removeChild(child);
                child = null;
            }
            if (child != null && child.findByClassName("rState") == null) {
                activeElementById.removeChild(child);
                child = null;
            }
            if (child == null) {
                child = activeElementById2.m29clone();
                activeElementById.appendChild(child);
                child.removeReference();
                child.setAttribute("onclick", "clickedP2PServerRow(" + i + ")");
            }
            String strReplace = safeString(p2PRoomAdvertisement.getGameState()).replace("battleroom", str4).replace("ingame", str5).replace("chat", str6);
            boolean z = !p2PRoomAdvertisement.isVersionCompatible();
            int iA = Color.a(255, 240, 240, 240);
            String str7 = "serverRow serverRowData realServerRow openRow ";
            child.compareAndSetClassNames(str7);
            child.findByClassName("rState").compareAndSetText(strReplace);
            child.findByClassName("rHost").compareAndSetText(Utility.padLeft(safeString(p2PRoomAdvertisement.getCreatedBy()), 15));
            child.findByClassName("rPlayers").compareAndSetText(Utility.padLeft(p2PRoomAdvertisement.getCurrentPlayers() + "\\" + p2PRoomAdvertisement.getMaxPlayers(), 15));
            child.findByClassName("rMap").compareAndSetText(Utility.padLeft(p2PRoomAdvertisement.getMapDisplayName(), 40));
            Element elementFindByClassName = child.findByClassName("rVersion");
            elementFindByClassName.compareAndSetText("v" + Utility.padLeft(safeString(p2PRoomAdvertisement.getGameVersionString()), 8));
            elementFindByClassName.compareAndSetClassNames(z ? "cell rVersion nonMatchingRow " : "cell rVersion ");
            Element elementFindByClassName2 = child.findByClassName("rOpen");
            elementFindByClassName2.compareAndSetText("P2P");
            elementFindByClassName2.compareAndSetClassNames("cell rOpen ");
            i++;
        }
        if (rooms.isEmpty()) {
            Element child2 = activeElementById.getNumChildren() > 0 ? activeElementById.getChild(0) : null;
            if (child2 == null || !child2.hasClassName("serverRowMessage")) {
                Element elementM29clone = activeElementById2.m29clone();
                activeElementById.appendChild(elementM29clone);
                elementM29clone.removeReference();
                elementM29clone.addClass("serverRowMessage");
                elementM29clone.setText("No P2P rooms found. Edit p2p.toml for libp2p peers and WebRTC ICE servers, or host a room on this device.");
            } else {
                child2.setText("No P2P rooms found. Edit p2p.toml for libp2p peers and WebRTC ICE servers, or host a room on this device.");
            }
        }
        if (str3 != null) {
            Element activeElementById3 = this.libRocket.getActiveElementById(str3);
            if (activeElementById3 != null) {
                activeElementById3.setText("Refresh");
            }
        }
        GameEngine.updatePaintTextSizeIfNeeded("DONE");
    }

    public void refreshServerListRaw(final String str, final String str2, final String str3) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (str3 != null) {
            this.libRocket.getActiveElementById(str3).setText("Refreshing");
        }
        Runnable runnable = new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.6
            @Override // java.lang.Runnable
            public void run() {
                Root.this.scriptEngine.addScriptToQueue("displayServerListRaw(" + Root.this.restrictedString(str) + "," + Root.this.restrictedString(str2) + "," + Root.this.restrictedString(str3) + ")");
            }
        };
        gameEngine.networkEngine.masterServerErrorMessage = null;
        MasterServerClient.loadServerListAsync(runnable);
    }

    public void displayServerListRaw(String str, String str2, String str3) {
        String str4;
        String str5;
        String str6;
        GameEngine gameEngine = GameEngine.getInstance();
        Element activeElementById = this.libRocket.getActiveElementById(str);
        Element activeElementById2 = this.libRocket.getActiveElementById(str2);
        if (activeElementById == null) {
            GameEngine.updatePaintTextSizeIfNeeded("serverListData is null, we may have changed page");
            return;
        }
        ArrayList<ServerInfo> serverList = ServerListActivity.getServerList();
        this.lastSortedDiscoveredServers = serverList;
        String str7 = Locale.get("menus.lobby.gameState.battleroom", new Object[0]);
        String str8 = Locale.get("menus.lobby.gameState.ingame", new Object[0]);
        String str9 = Locale.get("menus.lobby.gameState.chat", new Object[0]);
        if (activeElementById.getNumChildren() > serverList.size()) {
            for (int numChildren = activeElementById.getNumChildren() - 1; numChildren >= serverList.size(); numChildren--) {
                GameEngine.log("removing rowIndex:" + numChildren);
                activeElementById.removeChild(activeElementById.getChild(numChildren));
            }
            if (activeElementById.getNumChildren() != serverList.size()) {
                GameEngine.updatePaintTextSizeIfNeeded("-- Non matching size after clean up:" + activeElementById.getNumChildren() + " vs " + serverList.size());
            }
        }
        Boolean bool = (Boolean) this.libRocket.getActiveDocumentMetadata("showFullServerList");
        if (bool == null) {
            bool = false;
        }
        int size = 0;
        if (!bool.booleanValue() && serverList.size() > 50) {
            ArrayList arrayList = new ArrayList();
            Iterator it = serverList.iterator();
            while (it.hasNext()) {
                arrayList.add((ServerInfo) it.next());
                if (arrayList.size() > 50) {
                    break;
                }
            }
            size = serverList.size() - arrayList.size();
            serverList = arrayList;
        }
        int i = 0;
        for (ServerInfo serverInfo : serverList) {
            Element elementM29clone = null;
            if (i < activeElementById.getNumChildren()) {
                elementM29clone = activeElementById.getChild(i);
            }
            if (elementM29clone != null && elementM29clone.hasClassName("serverRowMessage")) {
                GameEngine.log("removing non rowIndex:" + i);
                activeElementById.removeChild(elementM29clone);
                elementM29clone = null;
            }
            if (elementM29clone != null && elementM29clone.findByClassName("rState") == null) {
                GameEngine.log("removing non rowIndex with no rState:" + i);
                activeElementById.removeChild(elementM29clone);
                elementM29clone = null;
            }
            if (elementM29clone == null) {
                elementM29clone = activeElementById2.m29clone();
                activeElementById.appendChild(elementM29clone);
                elementM29clone.removeReference();
                elementM29clone.setAttribute("onclick", "clickedServerRow(" + i + ")");
            }
            String strReplace = serverInfo.gameState;
            if (strReplace != null) {
                strReplace = strReplace.replace("battleroom", str7).replace("ingame", str8).replace("chat", str9);
            }
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = false;
            if (serverInfo != null && serverInfo.isDedicatedServer) {
                z = true;
                if ("chat".equalsIgnoreCase(serverInfo.gameState)) {
                    z2 = true;
                }
                if (serverInfo.hasUrl()) {
                    z4 = true;
                }
            }
            int iA = Color.a(255, 255, 255, 255);
            String str10 = "serverRow serverRowData realServerRow ";
            if (serverInfo != null) {
                if (z) {
                    str10 = str10 + "dedicatedServerRow ";
                    if (z2 || z4) {
                        iA = Color.a(255, 152, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_DIGITAL, 249);
                        str10 = str10 + "chatRow ";
                    }
                } else {
                    if (serverInfo.isPortOpen) {
                        iA = Color.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_SATELLITE_SERVICE, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_SATELLITE_SERVICE, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_SATELLITE_SERVICE);
                        str10 = str10 + "openRow ";
                    }
                    if (serverInfo.isLanServer) {
                        iA = Color.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_LAST_CHANNEL, 149, 35);
                        str10 = str10 + "lanRow ";
                    }
                }
                if (serverInfo.isCurrentServer()) {
                    str10 = str10 + "lastConnectedRow ";
                }
                if (!z2 && !z4 && !(VariableScope.nullOrMissingString + gameEngine.getVersionCode(true)).equals(serverInfo.gameVersionCodeText)) {
                    z3 = true;
                }
            }
            String str11 = VariableScope.nullOrMissingString + "color:" + Utility.toHexString(iA) + ";";
            if (0 != 0) {
                String str12 = str11 + "font-weight: bold;";
                str10 = str10 + "boldRow ";
            }
            elementM29clone.compareAndSetClassNames(str10);
            elementM29clone.findByClassName("rState").compareAndSetText(strReplace);
            elementM29clone.findByClassName("rHost").compareAndSetText(Utility.padLeft(serverInfo.createdBy, 15));
            if (serverInfo.currentPlayersText == "?") {
                str4 = "?";
            } else {
                str4 = serverInfo.currentPlayersText + "\\" + serverInfo.maxPlayersText;
            }
            elementM29clone.findByClassName("rPlayers").compareAndSetText(Utility.padLeft(str4, 15));
            String strPadLeft = Utility.padLeft(LevelSelectActivity.getMapName(serverInfo.mapPath), 40);
            if (strPadLeft == null) {
                strPadLeft = VariableScope.nullOrMissingString;
            }
            elementM29clone.findByClassName("rMap").compareAndSetText(strPadLeft);
            if ("ANY".equalsIgnoreCase(serverInfo.gameVersionString)) {
                str5 = serverInfo.gameVersionString;
            } else {
                str5 = "v" + Utility.padLeft(serverInfo.gameVersionString, 8);
            }
            Element elementFindByClassName = elementM29clone.findByClassName("rVersion");
            elementFindByClassName.compareAndSetText(str5);
            String str13 = "cell rVersion ";
            if (z3) {
                String str14 = "color:" + Utility.toHexString(Color.a(255, 155, 147, 147)) + ";";
                str13 = str13 + "nonMatchingRow ";
            } else {
                String str15 = "color:" + Utility.toHexString(iA) + ";";
            }
            elementFindByClassName.compareAndSetClassNames(str13);
            if (serverInfo.isPortOpen) {
                if (serverInfo.requiresPassword) {
                    str6 = "P";
                } else {
                    str6 = "Y";
                }
            } else {
                str6 = "N";
            }
            if (serverInfo.isLanServer) {
                str6 = "L";
            }
            Element elementFindByClassName2 = elementM29clone.findByClassName("rOpen");
            elementFindByClassName2.compareAndSetText(str6);
            String str16 = "cell rOpen ";
            if (!serverInfo.isPortOpen && !serverInfo.isLanServer) {
                String str17 = "color:" + Utility.toHexString(Color.a(255, 155, 147, 147)) + ";";
                str16 = str16 + "notOpenRow ";
            } else {
                String str18 = "color:" + Utility.toHexString(iA) + ";";
            }
            elementFindByClassName2.compareAndSetClassNames(str16);
            i++;
        }
        if (serverList.size() == 0 && gameEngine.networkEngine.masterServerErrorMessage != null) {
            String str19 = "ERROR: " + gameEngine.networkEngine.masterServerErrorMessage;
            Element elementM29clone2 = activeElementById2.m29clone();
            activeElementById.appendChild(elementM29clone2);
            elementM29clone2.removeReference();
            elementM29clone2.setText(str19);
        }
        Element activeElementById3 = this.libRocket.getActiveElementById("padding");
        if (activeElementById3 == null && size > 0) {
            activeElementById3 = activeElementById2.m29clone();
            activeElementById.appendChild(activeElementById3);
            activeElementById3.removeReference();
            activeElementById3.setAttribute("id", "padding");
            activeElementById3.addClass("serverRowMessage");
        }
        if (activeElementById3 != null && size > 0) {
            activeElementById3.setStyle("height:" + (18 * size) + "px;");
        }
        if (str3 != null) {
            this.libRocket.getActiveElementById(str3).setText("Refresh");
        }
        GameEngine.updatePaintTextSizeIfNeeded("DONE");
    }

    public void clickedServerRow(int i) {
        clickedServer(((ServerInfo) this.lastSortedDiscoveredServers.get(i)).serverId);
    }

    public void clickedP2PServerRow(int i) {
        if (this.lastSortedDiscoveredP2PRooms == null || i < 0 || i >= this.lastSortedDiscoveredP2PRooms.size()) {
            return;
        }
        clickedP2PServer(((P2PRoomAdvertisement) this.lastSortedDiscoveredP2PRooms.get(i)).getRoomId());
    }

    public void clickedServer(String str) {
        String str2;
        if (getAlertOrPopup() != null) {
            logWarn("clickedServer: getAlertOrPopup!=null");
            return;
        }
        try {
            ServerInfo serverInfoFindServerById = MasterServerClient.findServerById(str);
            if (serverInfoFindServerById == null) {
                logWarn("clickedServer: server==null");
                alert("That server no longer exists");
                return;
            }
            String infoText = serverInfoFindServerById.getInfoText();
            if (serverInfoFindServerById.hasUrl()) {
                str2 = "[onenter]Open Link:closePopup(); openWhitelistedLink(" + restrictedString(serverInfoFindServerById.getUrl()) + ");";
            } else if (!serverInfoFindServerById.isLanServer) {
                str2 = "[onenter]Join:closePopup(); joinServerWithId(" + restrictedString(serverInfoFindServerById.getConnectDescriptor()) + "," + restrictedString(serverInfoFindServerById.serverId) + ");";
            } else {
                str2 = "[onenter]Join over LAN:closePopup(); joinServerWithId(" + restrictedString(serverInfoFindServerById.getLanAddress()) + "," + restrictedString(serverInfoFindServerById.serverId) + ");";
            }
            showPopup(null, infoText, true, str2, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clickedP2PServer(String str) {
        if (getAlertOrPopup() != null) {
            return;
        }
        P2PRoomAdvertisement room = P2PLobbyService.getInstance().findRoom(str);
        if (room == null) {
            alert("That P2P room no longer exists");
            return;
        }
        String str2 = "[onenter]Join:closePopup(); joinP2PServerWithId(" + restrictedString(room.getRoomId()) + ");";
        showPopup(null, room.getInfoText(), true, str2, null);
    }

    private String safeString(String str) {
        if (str == null) {
            return VariableScope.nullOrMissingString;
        }
        return str;
    }

    public void hideKeyboard() {
        this.guiEngine.onRender();
    }

    public void saveGame(String str) throws IOException {
        closePopup();
        hideKeyboard();
        GameEngine.getInstance().gameSaver.updateAutosave(str.replace(".", "_").replace("/", "_").replace("\\", "_"), false);
    }

    public void exportMap(String str) {
        closePopup();
        GameEngine gameEngine = GameEngine.getInstance();
        try {
            gameEngine.tileMap.exportMapToPath(gameEngine.currentMapPath, "/SD/rusted_warfare_maps/" + str.replace(".", "_").replace("/", "_").replace("\\", "_").replace("|", "_").replace("?", "_") + ".tmx");
            showAlert("Map exported");
        } catch (MapLoadException e) {
            showAlert("Failed to export map. error: " + e.getMessage());
        } catch (IOException e2) {
            showAlert("Failed to export map. IO error: " + e2.getMessage());
        }
    }

    public void loadGame(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.networkEngine.disconnectNetworking("loading new save");
        gameEngine.isGameStarted = false;
        if (gameEngine.gameSaver.performAutosave(str, false)) {
            resumeNonMenu();
        }
    }

    public void loadGameEdit(final String str) {
        final GameEngine gameEngine = GameEngine.getInstance();
        ButtonAction buttonAction = null;
        if (PlatformExtension.b()) {
            buttonAction = new ButtonAction("Share", new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.7
                @Override // java.lang.Runnable
                public void run() {
                    Root.this.closePopup();
                    PlatformExtension.a(gameEngine.gameSaver.isAutosaveEnabled(str, false));
                }
            });
        }
        showPopupWithButtons(null, str, true, buttonAction, new ButtonAction("Delete", new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.8
            @Override // java.lang.Runnable
            public void run() {
                gameEngine.gameSaver.saveGame(str);
                Root.this.closePopup();
                Root.this.showMaps();
            }
        }));
    }

    public void loadReplay(String str) throws ConfigParseException {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.isGameStarted = false;
        if (gameEngine.replayEngine.c(str)) {
            resumeNonMenu();
        }
    }

    public void loadReplayEdit(final String str) {
        final GameEngine gameEngine = GameEngine.getInstance();
        ButtonAction buttonAction = null;
        if (PlatformExtension.b()) {
            buttonAction = new ButtonAction("Share", new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.9
                @Override // java.lang.Runnable
                public void run() {
                    Root.this.closePopup();
                    PlatformExtension.a(gameEngine.replayEngine.a(str, false));
                }
            });
        }
        showPopupWithButtons(null, str, true, buttonAction, new ButtonAction("Delete", new Runnable() { // from class: com.corrodinggames.librocket.scripts.Root.10
            @Override // java.lang.Runnable
            public void run() {
                gameEngine.replayEngine.e(str);
                Root.this.closePopup();
                Root.this.showMaps();
            }
        }));
    }

    public void makeSaveGamePopup(String str) {
        String strReplace;
        GameEngine gameEngine = GameEngine.getInstance();
        if (str == null) {
            strReplace = (gameEngine.getCurrentMapName() + " (" + Utility.formatDate("d MMM yyyy HH-mm-ss") + ")").replace("  ", " ");
        } else {
            strReplace = str;
        }
        showInputPopup("Save Game", "Enter a name to save the game under", strReplace, "[onenter]Save:saveGame(getPopupText())", null);
    }

    public void makeExportMapGamePopup(String str) {
        String strReplace;
        GameEngine gameEngine = GameEngine.getInstance();
        if (str == null) {
            strReplace = ("New " + gameEngine.getCurrentMapName() + " - " + Utility.formatDate("d MMM yyyy")).replace("  ", " ");
        } else {
            strReplace = str;
        }
        showInputPopup("Export Map", "Enter a name to export the map as", strReplace, "[onenter]Export:exportMap(getPopupText())", null);
    }

    public void makeSendMessagePopup() {
        GameEngine.getInstance();
        showInputPopup("Send Message", VariableScope.nullOrMissingString, VariableScope.nullOrMissingString, "[onenter]Send: sendChatMessage(getPopupText()); closePopup();", "Switch - Team only: makeSendTeamMessagePopupWithDefaultText(getPopupText()); ");
    }

    public void makeSendTeamMessagePopup() {
        makeSendTeamMessagePopupWithDefaultText(VariableScope.nullOrMissingString);
    }

    public void makeSendTeamMessagePopupWithDefaultText(String str) {
        GameEngine.getInstance();
        showInputPopup("Send Team Message", VariableScope.nullOrMissingString, str, "[onenter]Send Team:sendTeamChatMessage(getPopupText()); closePopup();", "+ Ping Map:sendTeamChatMessageAndPing(getPopupText()); closePopup();");
    }

    public void sendChatMessage(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        this.guiEngine.onRender();
        if (str == null || str.trim().equals(VariableScope.nullOrMissingString)) {
            return;
        }
        gameEngine.networkEngine.m(str);
        gameEngine.gameUI.isDraggingSelection = false;
    }

    public void sendTeamChatMessageAndPing(String str) {
        sendTeamChatMessage(str);
        GameEngine.getInstance().gameUI.activatePingMapMode();
    }

    public void sendTeamChatMessage(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        this.guiEngine.onRender();
        if (str == null || str.trim().equals(VariableScope.nullOrMissingString)) {
            return;
        }
        gameEngine.networkEngine.l(str);
    }

    public void receiveChatMessage(int i, String str, String str2, NetworkConnection networkConnection) {
        refreshChat();
    }

    public void refreshChat() {
        Element activeElementById;
        Element activeElementById2;
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.libRocket.getActiveDocument() == null || (activeElementById = this.libRocket.getActiveElementById("chatLogHistory")) == null) {
            return;
        }
        boolean attributeBoolean = activeElementById.getAttributeBoolean("reversed", false);
        if (gameEngine.networkEngine.F && (activeElementById2 = this.libRocket.getActiveElementById("chatBox")) != null) {
            activeElementById2.hide();
        }
        activeElementById.setInnerRML(VariableScope.nullOrMissingString);
        ConcurrentLinkedQueue<ChatMessage> concurrentLinkedQueueB = gameEngine.networkEngine.chatLog.b();
        StringBuffer stringBuffer = new StringBuffer();
        for (ChatMessage chatMessage : concurrentLinkedQueueB) {
            if (attributeBoolean) {
                stringBuffer.insert(0, "<div>" + chatMessage.b() + "</div>");
            } else {
                stringBuffer.append("<div>" + chatMessage.b() + "</div>");
            }
        }
        stringBuffer.append("<div id='chatLastRowSpacer'></div>");
        activeElementById.setInnerRML(stringBuffer.toString());
        activeElementById.loadCharsetIfNeededWithCurrentText();
        Element activeElementById3 = this.libRocket.getActiveElementById("chatLastRowSpacer");
        if (activeElementById3 != null) {
            activeElementById3.scrollIntoView(false);
        }
    }

    public void trace(String str) {
        GameEngine.log("Trace:" + str);
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/Root$TableData.class */
    public static class TableData {
        public ArrayList rows = new ArrayList();

        public boolean same(TableData tableData, boolean z) {
            if (this.rows.size() != tableData.rows.size()) {
                return false;
            }
            for (int i = 0; i < this.rows.size(); i++) {
                if (!((TableRow) this.rows.get(i)).same((TableRow) tableData.rows.get(i), z)) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/Root$TableRow.class */
    public static class TableRow {
        public ArrayList<TableCell> tableCells = new ArrayList();
        public Runnable androidOnclick;
        public String librocketOnClick;
        public String extraClasses;

        public void addClass(String str) {
            if (this.extraClasses == null) {
                this.extraClasses = str;
            } else {
                this.extraClasses += " " + str;
            }
        }

        public TableCell addCell(String str) {
            TableCell tableCell = new TableCell(str);
            this.tableCells.add(tableCell);
            return tableCell;
        }

        public void setLibrocketOnClick(String str) {
            this.librocketOnClick = str;
        }

        public void setAndroidOnClick(Runnable runnable) {
            this.androidOnclick = runnable;
        }

        public boolean same(TableRow tableRow, boolean z) {
            if (!Utility.md5(this.librocketOnClick, tableRow.librocketOnClick) || !Utility.md5(this.extraClasses, tableRow.extraClasses) || this.tableCells.size() != tableRow.tableCells.size()) {
                return false;
            }
            for (int i = 0; i < this.tableCells.size(); i++) {
                if (!((TableCell) this.tableCells.get(i)).same((TableCell) tableRow.tableCells.get(i), z)) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/Root$TableCell.class */
    public static class TableCell {
        public String text;
        public String classes;
        public String librocketOnClick;
        public Integer color;

        public void setLibrocketOnClick(String str) {
            this.librocketOnClick = str;
        }

        public TableCell(String str) {
            this.text = str;
        }

        public void addClass(String str) {
            if (this.classes != null) {
                this.classes += " " + str;
            } else {
                this.classes = str;
            }
        }

        public boolean same(TableCell tableCell, boolean z) {
            if (!Utility.md5(this.classes, tableCell.classes) || !Utility.md5(this.librocketOnClick, tableCell.librocketOnClick) || !Utility.sqrt(this.color, tableCell.color)) {
                return false;
            }
            if (!z && !Utility.md5(this.text, tableCell.text)) {
                return false;
            }
            return true;
        }
    }

    public void updateTableTextOnly(String str, TableData tableData, TableData tableData2) {
        ArrayList arrayList = tableData.rows;
        Element activeElementById = this.libRocket.getActiveElementById(str);
        if (activeElementById == null) {
            GameEngine.updatePaintTextSizeIfNeeded("updateTableText: tableElement:" + str + " is null, we may have changed page");
            return;
        }
        Element elementById = activeElementById.getElementById("tableListData");
        for (int i = 0; i < arrayList.size(); i++) {
            TableRow tableRow = (TableRow) arrayList.get(i);
            for (int i2 = 0; i2 < tableRow.tableCells.size(); i2++) {
                TableCell tableCell = (TableCell) tableRow.tableCells.get(i2);
                Element child = elementById.getChild(i);
                if (child == null) {
                    GameEngine.updatePaintTextSizeIfNeeded("updateTableText failed to get row " + i);
                    return;
                }
                Element child2 = child.getChild(i2);
                if (child2 == null) {
                    GameEngine.updatePaintTextSizeIfNeeded("updateTableText failed to get cell " + i2);
                    return;
                }
                child2.compareAndSetText(tableCell.text);
            }
        }
    }

    public void refreshTable(String str, TableData tableData) {
        ArrayList<TableRow> arrayList = tableData.rows;
        Element activeElementById = this.libRocket.getActiveElementById(str);
        if (activeElementById == null) {
            GameEngine.updatePaintTextSizeIfNeeded("refreshTable: tableElement:" + str + " is null, we may have changed page");
            return;
        }
        Element elementById = activeElementById.getElementById("tableRowTemplateHolder");
        Element elementById2 = activeElementById.getElementById("tableListData");
        Element child = elementById.findByClassName("rowTemplate").getChild(0);
        Element child2 = elementById.findByClassName("cellTemplate").getChild(0);
        elementById2.setInnerRML(VariableScope.nullOrMissingString);
        for (TableRow tableRow : arrayList) {
            Element elementCloneAndFix = child.cloneAndFix();
            if (tableRow.librocketOnClick != null) {
                elementCloneAndFix.setAttribute("onclick", tableRow.librocketOnClick);
            }
            if (tableRow.extraClasses != null) {
                elementCloneAndFix.addClass(tableRow.extraClasses);
            }
            for (TableCell tableCell : tableRow.tableCells) {
                Element elementCloneAndFix2 = child2.cloneAndFix();
                elementCloneAndFix2.compareAndSetText(tableCell.text);
                if (tableCell.librocketOnClick != null) {
                    elementCloneAndFix2.setAttribute("onclick", tableCell.librocketOnClick);
                    elementCloneAndFix2.addClass("clickablecell");
                }
                if (tableCell.classes != null) {
                    elementCloneAndFix2.addClass(tableCell.classes);
                }
                if (tableCell.color != null) {
                    elementCloneAndFix2.setAttribute("style", "color:" + Utility.toHexString(tableCell.color.intValue()) + ";");
                }
                elementCloneAndFix.appendChild(elementCloneAndFix2);
                elementCloneAndFix2.removeReference();
            }
            elementById2.appendChild(elementCloneAndFix);
            elementCloneAndFix.removeReference();
        }
    }

    public ElementDocument createAndShowPopup(String str, Object obj, String str2) {
        return this.libRocket.createPopupWithRML(str, obj, str2, true);
    }

    public ElementDocument createPopupHidden(String str, Object obj, String str2) {
        return this.libRocket.createPopupWithRML(str, obj, str2, false);
    }

    public boolean tryToShowPopupDocument(ElementDocument elementDocument) {
        return this.libRocket.showPopupFromDocument2(elementDocument);
    }

    public void showMainMenu() {
        GameEngine.getInstance().gameUI.isDraggingSelection = false;
        GameMainManager.getInstance().showMainMenu();
    }

    public void onEnter() {
        ElementDocument topmostDocument = this.libRocket.getTopmostDocument();
        if (topmostDocument == null) {
            GameEngine.log("onEnter: elementDocument==null");
            return;
        }
        for (Element element : topmostDocument.getAllNestedChildren()) {
            String attribute = element.getAttribute("onenter");
            if (attribute != null && element.isFocused()) {
                this.scriptEngine.processScript(attribute);
            }
        }
    }

    public void scrollFromFocusedElement(float f) {
        ElementDocument topmostDocument = this.libRocket.getTopmostDocument();
        if (topmostDocument == null) {
            GameEngine.log("onEnter: elementDocument==null");
            return;
        }
        Element topLevelFocusedElement = topmostDocument.getTopLevelFocusedElement();
        if (topLevelFocusedElement == null) {
            GameEngine.log("focusedElement: Not found");
            return;
        }
        ArrayList<Element> chainFromChildElement = topmostDocument.getChainFromChildElement(topLevelFocusedElement);
        if (chainFromChildElement == null) {
            GameEngine.log("scrollFromFocusedElement: Failed to find chain");
            return;
        }
        for (Element element : chainFromChildElement) {
            boolean z = false;
            if ("scrollDiv".equals(element.getId())) {
                z = true;
            }
            if (element.hasClassName("slider")) {
                z = true;
            }
            if (z) {
                element.setScrollTop(element.getScrollTop() + f);
                return;
            }
        }
        GameEngine.log("Found no slider element to offset");
    }

    public boolean isSliderOrUIElementSelected() {
        ElementDocument topmostDocument = this.libRocket.getTopmostDocument();
        if (topmostDocument == null) {
            GameEngine.log("onEnter: elementDocument==null");
            return false;
        }
        Element topLevelFocusedElement = topmostDocument.getTopLevelFocusedElement();
        if (topLevelFocusedElement != null) {
            String tagName = topLevelFocusedElement.getTagName();
            boolean z = false;
            if ("scrollDiv".equals(topLevelFocusedElement.getId())) {
                z = true;
            }
            if (topLevelFocusedElement.hasClassName("slider")) {
                z = true;
            }
            if ("input".equals(tagName) && "range".equals(topLevelFocusedElement.getAttribute("type", "text"))) {
                z = true;
            }
            if (z) {
                GameEngine.log("Slider element: true");
                return true;
            }
            GameEngine.log("Slider element: false");
        }
        GameEngine.log("Slider element: no element focused");
        return false;
    }

    public void onTouch() {
        ElementDocument topmostDocument = this.libRocket.getTopmostDocument();
        if (topmostDocument == null) {
            GameEngine.log("onEnter: elementDocument==null");
            return;
        }
        for (Element element : topmostDocument.getAllNestedChildren()) {
            if ("text".equals(element.getAttribute("type")) && element.isFocused()) {
                this.guiEngine.onUpdate();
            }
        }
    }

    public void onEscape() {
        ElementDocument topmostDocument = this.libRocket.getTopmostDocument();
        if (topmostDocument == null) {
            GameEngine.log("onEscape: elementDocument==null");
            return;
        }
        boolean z = false;
        Iterator it = topmostDocument.getAllNestedChildren().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Element element = (Element) it.next();
            if (element.getAttribute("click_on_escape") != null) {
                element.click();
                z = true;
                break;
            }
        }
        if (!z && closePopup()) {
        }
    }

    public void askQuitGame() {
        closePopup();
        showPopup("Are you sure you want to quit?", VariableScope.nullOrMissingString, true, "[onenter]Quit:closePopup(); exit();", null);
    }

    public String getCurrentDocumentPath() {
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        if (activeDocument == null) {
            return null;
        }
        return activeDocument.documentPath;
    }

    public String getCurrentPopupPath() {
        ElementDocument currentPopup = this.libRocket.getCurrentPopup();
        if (currentPopup == null) {
            return null;
        }
        return currentPopup.documentPath;
    }

    public String getCreditsText() {
        return "Credits goes here";
    }

    public void runRunnable(Runnable runnable) {
        logDebug("runRunnable");
        if (runnable == null) {
            logDebug("runnable==null");
        }
        runnable.run();
    }

    public boolean isLinux() {
        return PlatformResolver.a() == Platform.Linux;
    }

    public boolean not(boolean z) {
        return !z;
    }

    public boolean and(boolean z, boolean z2) {
        return z && z2;
    }

    public boolean or(boolean z, boolean z2) {
        return z || z2;
    }

    public void showBattleroom() {
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        boolean z = true;
        if (activeDocument != null && "battleroom.rml".equals(activeDocument.documentPath)) {
            GameEngine.log("Already on battleroom page");
            z = false;
        }
        this.libRocket.setDocument("battleroom.rml", null, z);
    }

    public void setDocument(String str) {
        this.libRocket.setDocument(str);
    }

    public void playNextMusicTrack() {
        GameEngine.getInstance().musicManager.skipToNextTrack();
    }

    public void toggleMusic() {
        GameEngine.getInstance().musicManager.disabled = !GameEngine.getInstance().musicManager.disabled;
    }

    public void updateMusicButton(String str) {
        Element activeElementById = this.libRocket.getActiveElementById(str);
        if (activeElementById != null) {
            if (GameEngine.getInstance().musicManager.disabled) {
                activeElementById.setText(">");
            } else {
                activeElementById.setText("||");
            }
        }
    }

    public void setSandboxMapFromPopup(String str) {
        GameEngine.getInstance();
        closePopup();
        this.libRocket.getActiveDocument().setMetadata("mode", str);
        showLevelOptions();
        this.libRocket.getActiveDocument().findByClassName("mapImage").setAttribute("src", getMapThumbnail(str));
        this.libRocket.getActiveDocument().findByClassName("mapText").setText(getMapNameFromPath(str));
    }

    public void showSandboxMapSelectOnChange() {
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        int i = Integer.parseInt(activeDocument.getElementById("typeSelector").getValue());
        int iIntValue = ((Integer) activeDocument.getMetadata("lastTypeSelector", 0)).intValue();
        this.libRocket.getActiveDocument().setMetadata("lastTypeSelector", Integer.valueOf(i));
        if (i != iIntValue) {
            showSandboxMapSelect();
        }
    }

    public void showSandboxMapSelect() {
        showMapPopup(getModeMapPath(this.libRocket.getActiveDocument(), "typeSelector"), "setSandboxMapFromPopup");
    }

    public String getModeMapPath(Element element, String str) {
        int iIntValue;
        GameEngine gameEngine = GameEngine.getInstance();
        if (str == null) {
            if (gameEngine.networkEngine.roomSettings.gameModeType == null) {
                GameEngine.updatePaintTextSizeIfNeeded("getModeMapPath: currentType==0");
                iIntValue = 0;
            } else {
                iIntValue = gameEngine.networkEngine.roomSettings.gameModeType.ordinal();
            }
        } else {
            Element elementById = element.getElementById(str);
            if (elementById == null) {
                GameEngine.logWarningAndStack("getModeMapPath: typeSelector==null");
                iIntValue = 0;
            } else {
                iIntValue = elementById.getValueAsInt(0).intValue();
            }
        }
        if (iIntValue == 0) {
            return "maps/skirmish";
        }
        if (iIntValue == 1) {
            return "/SD/rusted_warfare_maps";
        }
        if (iIntValue == 2) {
            return "saves";
        }
        throw new RuntimeException("Unknown typeIndex:" + iIntValue);
    }

    public void event_unicodeEntered() {
        ElementDocument topmostDocument = this.libRocket.getTopmostDocument();
        if (topmostDocument != null) {
            Element elementFindByClassName = topmostDocument.findByClassName("textinputUnicodeWrap");
            if (elementFindByClassName != null) {
                elementFindByClassName.compareAndAddClass("unicodeWasTyped");
                return;
            } else {
                GameEngine.log("event_unicodeEntered: missing textinput");
                return;
            }
        }
        GameEngine.log("event_unicodeEntered: missing document");
    }

    public boolean isVersionBeta() {
        return GameEngine.getInstance().isBetaOrPreview();
    }

    public Object ifCondition(boolean z, Object obj, Object obj2) {
        return z ? obj : obj2;
    }

    public String i(String str) {
        return Locale.get(str, new Object[0]);
    }

    public void openLinkToCG(String str) {
        openWhitelistedLink("http://corrodinggames.com/" + str);
    }

    public void openWhitelistedLink(String str) {
        GameEngine.log("Opening link:" + str);
        if (!str.startsWith("http://corrodinggames.com/") && !str.startsWith("https://corrodinggames.com/") && !str.startsWith("http://corrodinggames.net/") && !str.startsWith("https://corrodinggames.net/")) {
            GameEngine.log("Not in whitelist");
        } else if (this.guiEngine.openURL(str)) {
            alert("Opened link: " + str);
        } else {
            alert("Sorry couldn't load browser to: " + str + " please navigate manually");
        }
    }

    public void writeGameLog(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        boolean z = false;
        LinkedList debugInfo = GameMainManager.getInstance().getDebugInfo();
        if (debugInfo == null) {
            z = true;
        } else {
            synchronized (debugInfo) {
                ListIterator listIterator = debugInfo.listIterator(Utility.max(0, debugInfo.size() - 3000));
                while (listIterator.hasNext()) {
                    stringBuffer.append(Element.excapeHTML((String) listIterator.next()));
                    stringBuffer.append("<br/>");
                }
            }
        }
        if (z) {
            alert("Internal game logging not active");
            return;
        }
        GameEngine.log("writeGameLog ready");
        Element activeElementById = this.libRocket.getActiveElementById(str);
        if (activeElementById == null) {
            GameEngine.log("Failed to find: " + str);
        } else {
            activeElementById.setInnerRML(stringBuffer.toString());
        }
    }

    public void exportGameLog() {
        StringBuffer stringBuffer = new StringBuffer();
        boolean z = false;
        LinkedList debugInfo = GameMainManager.getInstance().getDebugInfo();
        if (debugInfo == null) {
            z = true;
        } else {
            synchronized (debugInfo) {
                ListIterator listIterator = debugInfo.listIterator(Utility.max(0, debugInfo.size() - 3000));
                while (listIterator.hasNext()) {
                    stringBuffer.append(Element.excapeHTML((String) listIterator.next()));
                    stringBuffer.append("\n");
                }
            }
        }
        if (z) {
            alert("Internal game logging not active");
            return;
        }
        try {
            File file = new File(FileHelper.convertAbstractPath("/SD/rustedWarfare/RustedWarfareLog-" + Utility.formatDate("d_MMM_yyyy_HH.mm.ss") + ".txt"));
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append((CharSequence) stringBuffer.toString());
            fileWriter.flush();
            fileWriter.close();
            PlatformExtension.a(file);
            file.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
            alert("Failed to export logs: " + e.getMessage());
        }
    }

    public void setPageMinWidthAndHeight(float f, float f2) {
        GameEngine.log("setPageMinWidthAndHeight(" + f + ", " + f2 + ")");
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        if (activeDocument == null) {
            GameEngine.log("setPageMinWidthAndHeight - no page");
            return;
        }
        activeDocument.setMetadataFloat("minWidth", Float.valueOf(f));
        activeDocument.setMetadataFloat("minHeight", Float.valueOf(f2));
        this.guiEngine.onResize();
    }

    public void importFilePopup() {
        PlatformExtension.a(new FileSelectionCallback() { // from class: com.corrodinggames.librocket.scripts.Root.11
            @Override // com.corrodinggames.rts.gameFramework.platform.FileSelectionCallback
            public void onFileSelected() {
                GameEngine.log("importFilePopup: onFileSelected");
            }

            @Override // com.corrodinggames.rts.gameFramework.platform.FileSelectionCallback
            public void onCancelled() {
                GameEngine.log("importFilePopup: onCancelled");
            }
        });
    }

    protected void setDocumentUpdate(ElementDocument elementDocument, Runnable runnable) {
        elementDocument.setMetadata("onUpdateFunction", runnable);
    }

    public void onFrameUpdate(float f) {
        Object metadata;
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        if (activeDocument != null && (metadata = activeDocument.getMetadata("onUpdateFunction")) != null) {
            ((Runnable) metadata).run();
        }
    }
}
