package com.corrodinggames.librocket.scripts;

import com.Element;
import com.ElementDocument;
import com.corrodinggames.rts.appFramework.LevelSelectActivity;
import com.corrodinggames.rts.appFramework.ManageReplaysActivity;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.network.GameModeType;
import com.corrodinggames.rts.gameFramework.network.GameRoomSettings;
import com.corrodinggames.rts.gameFramework.network.TeamLayoutType;
import com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/Multiplayer.class */
public class Multiplayer extends ScriptContext {
    Root root;
    String[] currentDropdownRawArray;
    Root.TableData lastPlayerTable;
    boolean useMapDropdown = false;

    Multiplayer(Root root) {
        this.root = root;
    }

    void updateMapDropdown(Element element, String str, String str2) {
        GameEngine gameEngine = GameEngine.getInstance();
        int iIntValue = element.getElementById(str2).getValueAsInt(0).intValue();
        this.currentDropdownRawArray = null;
        ArrayList arrayList = new ArrayList();
        if (iIntValue == 0) {
            this.currentDropdownRawArray = FileHelper.listFilesRecursive("maps/skirmish", true);
            Arrays.sort(this.currentDropdownRawArray);
            for (String str3 : this.currentDropdownRawArray) {
                arrayList.add(LevelSelectActivity.getMapName(str3));
            }
        } else if (iIntValue == 1) {
            this.currentDropdownRawArray = FileHelper.listFilesRecursive("/SD/rusted_warfare_maps", true);
            if (this.currentDropdownRawArray == null) {
                gameEngine.alert("Could not find folder: /SD/rusted_warfare_maps", 1);
                this.currentDropdownRawArray = new String[0];
            }
            Arrays.sort(this.currentDropdownRawArray);
            for (String str4 : this.currentDropdownRawArray) {
                arrayList.add(LevelSelectActivity.getMapName(str4));
            }
        } else if (iIntValue == 2) {
            this.currentDropdownRawArray = ManageReplaysActivity.getReplayFiles();
            if (this.currentDropdownRawArray == null) {
                gameEngine.alert("Could not find a save folder on SD card", 1);
                this.currentDropdownRawArray = new String[0];
            }
            for (String str5 : this.currentDropdownRawArray) {
                arrayList.add(LevelSelectActivity.getMapName(str5));
            }
        } else {
            throw new RuntimeException("Unknown typeIndex:" + iIntValue);
        }
        String str6 = VariableScope.nullOrMissingString;
        int i = 0;
        int i2 = 1;
        for (String str7 : this.currentDropdownRawArray) {
            i++;
            if (iIntValue == 0 && str7.equalsIgnoreCase("[p8]Many Islands (8p).tmx")) {
                i2 = i;
            }
        }
        int i3 = 0;
        for (String str8 : this.currentDropdownRawArray) {
            i3++;
            str6 = str6 + generateOption(str8, this.root.convertMapName(str8), i3 == i2) + "\n";
        }
        GameEngine.log("mapList:" + str6);
        if (iIntValue != 2) {
        }
        element.getElementById("mapsSelectorParent").setInnerRML("<p data-workaround='this stops disappearing select'></p><select id='mapsSelector' class='mapsSelector'><option value='0'>...</option></select>");
        getMapDropdown().setInnerRML(str6);
    }

    String generateOption(String str, String str2, boolean z) {
        return generateOption(str, str2, z, null, false);
    }

    String generateOption(String str, String str2, boolean z, Integer num, boolean z2) {
        String str3 = VariableScope.nullOrMissingString;
        if (z) {
            str3 = str3 + " selected='selected'";
        }
        String strHtmlString = this.root.htmlString(str2);
        String str4 = VariableScope.nullOrMissingString;
        if (num != null) {
            str4 = str4 + " style='color:" + Utility.toHexString(num.intValue()) + ";'";
        }
        if (z2) {
            str4 = str4 + " class='disabled-option'";
        }
        if (str4 != null && !VariableScope.nullOrMissingString.equals(str4)) {
            strHtmlString = "<span " + str4 + ">" + strHtmlString + "</span>";
        }
        return "<option value=" + this.root.escapedString(str) + " " + str3 + ">" + strHtmlString + "</option>";
    }

    Element getMapDropdown() {
        return this.libRocket.getCurrentPopup().findByClassName("mapsSelector");
    }

    String getMapDropdownSelected() {
        return getMapDropdown().getAttribute("value");
    }

    void readInterfaceIntoNetworkSettings() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.networkEngine.isServer) {
            String mapDropdownSelected = getMapDropdownSelected();
            if (mapDropdownSelected == null) {
                mapDropdownSelected = "<No Map>";
            }
            gameEngine.networkEngine.roomSettings.mapPath = mapDropdownSelected;
            gameEngine.networkEngine.roomSettings.gameModeType = GameModeType.values()[0];
        }
    }

    public void multiplayerStart() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.networkEngine.isServer) {
            if (gameEngine.networkEngine.roomSettings.gameModeType == GameModeType.skirmishMap) {
                gameEngine.networkEngine.selectedMapPath = "maps/skirmish/" + gameEngine.networkEngine.roomSettings.mapPath;
            } else if (gameEngine.networkEngine.roomSettings.gameModeType == GameModeType.customMap) {
                gameEngine.networkEngine.selectedMapPath = "/SD/rusted_warfare_maps/" + gameEngine.networkEngine.roomSettings.mapPath;
            } else if (gameEngine.networkEngine.roomSettings.gameModeType == GameModeType.savedGame) {
                gameEngine.networkEngine.selectedMapPath = null;
            } else {
                this.libRocket.showMessageBox2("Error: No map type selected");
                return;
            }
            if (gameEngine.networkEngine.roomSettings.mapPath == null || VariableScope.nullOrMissingString.equals(gameEngine.networkEngine.roomSettings.mapPath) || gameEngine.networkEngine.roomSettings.mapPath.equals("<No Map>")) {
                this.libRocket.showMessageBox2("Error: No map selected");
                return;
            } else {
                gameEngine.networkEngine.startBattleRoomGame();
                return;
            }
        }
        if (gameEngine.networkEngine.isProxyController) {
            gameEngine.networkEngine.k("-start");
        } else {
            GameEngine.log("startNetButton.setOnClickListener", "Clicked but not server or proxy controller");
        }
    }

    public void battleroomSetup() {
        GameEngine gameEngine = GameEngine.getInstance();
        this.lastPlayerTable = null;
        refreshUI();
        this.root.refreshChat();
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        if (activeDocument != null && gameEngine.networkEngine.singleplayerServer) {
            activeDocument.addClass("singlePlayer");
        }
        gameEngine.networkEngine.startMasterServerUpdateTimer();
    }

    public void refreshUI() {
        GameEngine gameEngine = GameEngine.getInstance();
        Element activeElementById = this.libRocket.getActiveElementById("infoDiv");
        if (activeElementById == null) {
            GameEngine.log("refreshUI: infoTextElement==null");
            return;
        }
        ElementDocument activeDocument = this.libRocket.getActiveDocument();
        boolean z = gameEngine.networkEngine.isServer || gameEngine.networkEngine.isProxyController;
        boolean z2 = gameEngine.networkEngine.isServer;
        boolean z3 = (z || gameEngine.networkEngine.roomSettings.teamLock) ? false : true;
        for (Element item : activeDocument.findElementsByClassName("forHostOnly")) {
            item.show(z);
        }
        for (Element value : activeDocument.findElementsByClassName("forLocalHostOnly")) {
            value.show(z2);
        }
        for (Element element : activeDocument.findElementsByClassName("forUnlockedTeamsNonHost")) {
            element.show(z3);
        }
        if (gameEngine.isSinglePlayerGame()) {
            for (Element element : activeDocument.findElementsByClassName("forRealNetworkOnly")) {
                element.show(false);
            }
        }
        activeElementById.compareAndSetText(gameEngine.networkEngine.getPublicIpStatusText());
        String networkMapPath = gameEngine.networkEngine.getNetworkMapPath();
        if (gameEngine.networkEngine.roomSettings.gameModeType == GameModeType.savedGame) {
            networkMapPath = "saves/" + gameEngine.networkEngine.roomSettings.mapPath;
        }
        Element activeElementById2 = this.libRocket.getActiveElementById("mapImage");
        if (gameEngine.networkEngine.chatOnlyMode) {
            activeElementById2.hide();
        }
        String attribute = activeElementById2.getAttribute("src");
        if (networkMapPath == null) {
            if (!VariableScope.nullOrMissingString.equals(attribute)) {
                activeElementById2.setAttribute("src", VariableScope.nullOrMissingString);
            }
        } else {
            String mapThumbnail = this.root.getMapThumbnail(networkMapPath);
            if (mapThumbnail == null) {
                mapThumbnail = VariableScope.nullOrMissingString;
            }
            if (!mapThumbnail.equals(attribute)) {
                activeElementById2.setAttribute("src", mapThumbnail);
            }
        }
        refreshPlayerTable();
    }

    public void refreshPlayerTable() {
        Root.TableData playerTable = getPlayerTable();
        if (this.lastPlayerTable != null) {
            if (this.lastPlayerTable.same(playerTable, false)) {
                return;
            }
            if (this.lastPlayerTable.same(playerTable, true)) {
                this.root.updateTableTextOnly("playersDiv", playerTable, this.lastPlayerTable);
                return;
            }
        }
        this.root.refreshTable("playersDiv", playerTable);
        this.lastPlayerTable = playerTable;
    }

    public Root.TableData getPlayerTable() {
        GameEngine gameEngine = GameEngine.getInstance();
        Root.TableData tableData = new Root.TableData();
        ArrayList arrayList = tableData.rows;
        int i = -1;
        int i2 = 0;
        ArrayList<PlayerTeam> sortedTeams = PlayerTeam.getSortedTeams(true);
        for (PlayerTeam playerTeam : sortedTeams) {
            if (playerTeam != null) {
                if (i != -1 && i != playerTeam.teamColorId) {
                    i2++;
                }
                i = playerTeam.teamColorId;
            }
        }
        int i3 = -1;
        for (PlayerTeam playerTeam2 : sortedTeams) {
            if (playerTeam2 != null) {
                if (i3 != -1 && i3 != playerTeam2.teamColorId && i2 <= 3) {
                    Root.TableRow tableRow = new Root.TableRow();
                    for (int i4 = 0; i4 < 4; i4++) {
                        tableRow.addCell(VariableScope.nullOrMissingString).addClass("spacer");
                    }
                    arrayList.add(tableRow);
                }
                i3 = playerTeam2.teamColorId;
                String str = "unnamed";
                if (playerTeam2.teamName != null) {
                    str = playerTeam2.teamName;
                }
                String strZ = playerTeam2.z();
                String string = Integer.toString(playerTeam2.teamId + 1);
                boolean zAddCredits = playerTeam2.isSpectatorTeamColor();
                if (zAddCredits) {
                    string = "S";
                }
                if (!zAddCredits && playerTeam2.startingUnitsOverride != null && playerTeam2.startingUnitsOverride.intValue() != gameEngine.networkEngine.roomSettings.startingUnits) {
                    string = string + " - " + gameEngine.networkEngine.d(playerTeam2.startingUnitsOverride.intValue());
                }
                String teamColorName = playerTeam2.getTeamSlotLabel();
                Root.TableRow tableRow2 = new Root.TableRow();
                Root.TableCell tableCellAddCell = tableRow2.addCell(str);
                if (playerTeam2.playerColorOverride != null) {
                    tableCellAddCell.color = Integer.valueOf(PlayerTeam.i(playerTeam2.playerColorOverride.intValue()));
                }
                if (playerTeam2 == gameEngine.networkEngine.localPlayerTeam) {
                    tableCellAddCell.addClass("boldText");
                }
                tableRow2.addCell(string).color = Integer.valueOf(playerTeam2.getTeamSlotColorArgb());
                tableRow2.addCell(teamColorName).color = Integer.valueOf(PlayerTeam.i(playerTeam2.teamColorId));
                tableRow2.addCell(strZ);
                tableRow2.setLibrocketOnClick("mp.showPlayerConfig('" + playerTeam2.teamId + "')");
                arrayList.add(tableRow2);
            }
        }
        if (!gameEngine.networkEngine.isServer && gameEngine.networkEngine.serverUuid == null) {
            arrayList.clear();
            String str2 = "Connecting...";
            if (gameEngine.networkEngine.sendQueue.size() == 0) {
                str2 = "Disconnected";
            }
            Root.TableRow tableRow3 = new Root.TableRow();
            tableRow3.addCell(str2);
            tableRow3.addCell(VariableScope.nullOrMissingString);
            tableRow3.addCell(VariableScope.nullOrMissingString);
            tableRow3.addCell(VariableScope.nullOrMissingString);
            arrayList.add(tableRow3);
        }
        return tableData;
    }

    public void showSetTeamsDialog() {
        GameEngine.getInstance();
        if (this.root.createAndShowPopup("battleroom_setTeams.rml", null, "Set Teams") != null) {
        }
    }

    public void showPlayerConfigForSelf() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.networkEngine.localPlayerTeam != null) {
            showPlayerConfig(VariableScope.nullOrMissingString + gameEngine.networkEngine.localPlayerTeam.teamId);
        }
    }

    public void showPlayerConfig(final String str) {
        GameEngine.getInstance();
        this.scriptEngine.addRunnableToQueue(new Runnable() { // from class: com.corrodinggames.librocket.scripts.Multiplayer.1
            @Override // java.lang.Runnable
            public void run() {
                Multiplayer.this.showPlayerConfigNow(str);
            }
        });
    }

    public void showPlayerConfigNow(String str) {
        ElementDocument elementDocumentCreateAndShowPopup;
        GameEngine gameEngine = GameEngine.getInstance();
        PlayerTeam playerTeamK = PlayerTeam.k(Integer.parseInt(str));
        if (playerTeamK == null) {
            this.root.logWarn("showPlayerConfig: " + str + "==null");
            return;
        }
        if ((gameEngine.networkEngine.isServerOrProxyController() || (gameEngine.networkEngine.localPlayerTeam == playerTeamK && !gameEngine.networkEngine.roomSettings.teamLock)) && (elementDocumentCreateAndShowPopup = this.root.createAndShowPopup("battleroom_player.rml", playerTeamK, playerTeamK.teamName)) != null) {
            Element elementById = elementDocumentCreateAndShowPopup.getElementById("team_id");
            Element elementById2 = elementDocumentCreateAndShowPopup.getElementById("spawnPoint");
            Element elementById3 = elementDocumentCreateAndShowPopup.getElementById("allyTeam");
            Element elementById4 = elementDocumentCreateAndShowPopup.getElementById("aiDifficulty");
            Element elementById5 = elementDocumentCreateAndShowPopup.getElementById("startingUnits");
            Element elementById6 = elementDocumentCreateAndShowPopup.getElementById("playerColor");
            Element elementById7 = elementDocumentCreateAndShowPopup.getElementById("playerOverridesSection");
            Element elementById8 = elementDocumentCreateAndShowPopup.getElementById("aiDifficultySelection");
            if (!GameEngine.isPlatformName("sd")) {
                setupStartingUnitDropDown(elementById5, true);
                setupPlayerColorDropDown(elementById6, true, true, playerTeamK);
            } else {
                GameEngine.log("sd");
            }
            elementById.setValue(VariableScope.nullOrMissingString + playerTeamK.teamId);
            String str2 = VariableScope.nullOrMissingString + (playerTeamK.teamId + 1);
            if (playerTeamK.isSpectatorTeamColor()) {
                str2 = "-2";
            }
            elementById2.setValue(str2);
            if (playerTeamK.isTeamDefeated) {
                elementById3.setValue(VariableScope.nullOrMissingString + (playerTeamK.teamColorId + 1));
            } else {
                elementById3.setValue("fromSpawn2");
            }
            if (elementById7 == null) {
                throw new RuntimeException("playerOverridesSection==null");
            }
            if (!gameEngine.networkEngine.isServer) {
                elementById7.hide();
            }
            if (elementById8 == null) {
                throw new RuntimeException("aiDifficultySelection==null");
            }
            if (!GameEngine.isPlatformName("s1")) {
                if (playerTeamK.isTeamSpectator) {
                    if (playerTeamK.teamAIDifficultyOverride == null) {
                        elementById4.setValue("-99");
                    } else {
                        elementById4.setValue(VariableScope.nullOrMissingString + playerTeamK.teamAIDifficultyOverride);
                    }
                } else {
                    elementById8.hide();
                }
            } else {
                GameEngine.log("s1");
            }
            if (!GameEngine.isPlatformName("s2")) {
                if (playerTeamK.startingUnitsOverride == null) {
                    elementById5.setValue("-99");
                } else {
                    GameEngine.log("startingUnitOverride: " + playerTeamK.startingUnitsOverride);
                    elementById5.setValue(VariableScope.nullOrMissingString + playerTeamK.startingUnitsOverride);
                }
            } else {
                GameEngine.log("s2");
            }
            if (!GameEngine.isPlatformName("s3")) {
                if (playerTeamK.playerColorOverride == null) {
                    elementById6.setValue("-99");
                    return;
                } else {
                    GameEngine.log("playerColor: " + playerTeamK.playerColorOverride);
                    elementById6.setValue(VariableScope.nullOrMissingString + playerTeamK.playerColorOverride);
                    return;
                }
            }
            GameEngine.log("s3");
        }
    }

    public void teamsSet_apply() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!gameEngine.networkEngine.isServer) {
            GameEngine.log("Not server");
            return;
        }
        GameEngine.log("playerConfig_kick");
        String value = this.libRocket.getCurrentPopup().getElementById("teamLayout").getValue();
        if ("2t".equalsIgnoreCase(value)) {
            gameEngine.networkEngine.a(TeamLayoutType.layout_2sides);
        } else if ("3t".equalsIgnoreCase(value)) {
            gameEngine.networkEngine.a(TeamLayoutType.layout_3sides);
        } else if ("FFA".equalsIgnoreCase(value)) {
            gameEngine.networkEngine.a(TeamLayoutType.layout_ffa);
        } else if ("spectators".equalsIgnoreCase(value)) {
            gameEngine.networkEngine.a(TeamLayoutType.layout_spectators);
        } else {
            GameEngine.logColored("teamsSet_apply: unknown layout: " + value);
        }
        refreshUI();
    }

    public void playerConfig_kick() {
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("playerConfig_kick");
        String value = this.libRocket.getCurrentPopup().getElementById("team_id").getValue();
        PlayerTeam playerTeamK = PlayerTeam.k(Integer.parseInt(value));
        if (playerTeamK == null) {
            this.root.logWarn("playerConfig_kick: " + value + "==null");
        } else {
            gameEngine.networkEngine.e(playerTeamK);
        }
    }

    public void playerConfig_apply() {
        boolean z;
        int iIntValue;
        Integer numValueOf;
        Integer numValueOf2;
        Integer numValueOf3;
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("playerConfig_kick");
        String value = this.libRocket.getCurrentPopup().getElementById("team_id").getValue();
        PlayerTeam playerTeamK = PlayerTeam.k(Integer.parseInt(value));
        if (playerTeamK == null) {
            this.root.logWarn("playerConfig_apply: " + value + "==null");
            return;
        }
        ElementDocument currentPopup = this.libRocket.getCurrentPopup();
        Element elementById = currentPopup.getElementById("spawnPoint");
        Element elementById2 = currentPopup.getElementById("allyTeam");
        Element elementById3 = currentPopup.getElementById("aiDifficulty");
        Element elementById4 = currentPopup.getElementById("startingUnits");
        Element elementById5 = currentPopup.getElementById("playerColor");
        String value2 = elementById.getValue();
        String value3 = elementById2.getValue();
        int iIntValue2 = Integer.valueOf(value2).intValue() - 1;
        boolean z2 = false;
        if (iIntValue2 == -3) {
            z2 = true;
        } else {
            if (iIntValue2 < 0) {
                iIntValue2 = 1;
            }
            if (iIntValue2 > PlayerTeam.TEAM_NEUTRAL - 1) {
                iIntValue2 = PlayerTeam.TEAM_NEUTRAL - 1;
            }
        }
        boolean z3 = false;
        if (z2) {
            iIntValue = -3;
            z = true;
        } else if (value3.equals("fromSpawn2")) {
            iIntValue = iIntValue2 % 2;
            playerTeamK.isTeamDefeated = false;
            z = true;
        } else {
            z = false;
            iIntValue = playerTeamK.teamColorId;
            try {
                iIntValue = Integer.valueOf(value3).intValue() - 1;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            playerTeamK.isTeamDefeated = true;
        }
        if (playerTeamK.teamColorId != iIntValue) {
            if (gameEngine.networkEngine.isServer || gameEngine.networkEngine.isProxyController || gameEngine.networkEngine.localPlayerTeam == playerTeamK) {
                z3 = true;
            } else {
                GameEngine.log("row.setOnClickListener", "Clicked but not server or proxy controller");
            }
        }
        try {
            if (playerTeamK.teamId != iIntValue2) {
                if (gameEngine.networkEngine.isServer) {
                    z3 = false;
                    gameEngine.networkEngine.a(playerTeamK, iIntValue2);
                    playerTeamK.teamColorId = iIntValue;
                } else if (gameEngine.networkEngine.isProxyController || gameEngine.networkEngine.localPlayerTeam == playerTeamK) {
                    z3 = false;
                    int i = iIntValue;
                    if (z) {
                        i = -1;
                    }
                    gameEngine.networkEngine.a(playerTeamK, iIntValue2, Integer.valueOf(i));
                } else {
                    GameEngine.log("row.setOnClickListener", "Clicked but not server or proxy controller");
                }
            }
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
        }
        if (playerTeamK.isTeamSpectator) {
            int iIntValue3 = elementById3.getValueAsInt(-99).intValue();
            if (iIntValue3 == -99) {
                numValueOf3 = null;
            } else {
                numValueOf3 = Integer.valueOf(iIntValue3);
            }
            if (playerTeamK.teamAIDifficultyOverride != numValueOf3) {
                if (gameEngine.networkEngine.isServer) {
                    playerTeamK.teamAIDifficultyOverride = numValueOf3;
                } else {
                    GameEngine.log("aiDifficultyOverride: not server or proxy controller");
                }
            }
        }
        int iIntValue4 = elementById4.getValueAsInt(-99).intValue();
        GameEngine.log("startingUnits now: " + iIntValue4);
        if (iIntValue4 == -99) {
            numValueOf = null;
        } else {
            numValueOf = Integer.valueOf(iIntValue4);
        }
        if (playerTeamK.startingUnitsOverride != numValueOf) {
            if (gameEngine.networkEngine.isServer) {
                playerTeamK.startingUnitsOverride = numValueOf;
            } else {
                GameEngine.log("startingUnitOverride: not server or proxy controller");
            }
        }
        int iIntValue5 = elementById5.getValueAsInt(-99).intValue();
        GameEngine.log("playerColor now: " + iIntValue5);
        if (iIntValue5 == -99) {
            numValueOf2 = null;
        } else {
            numValueOf2 = Integer.valueOf(iIntValue5);
        }
        if (playerTeamK.playerColorOverride != numValueOf2) {
            if (gameEngine.networkEngine.isServer) {
                playerTeamK.playerColorOverride = numValueOf2;
            } else {
                GameEngine.log("colorOverride: not server or proxy controller");
            }
        }
        if (z3) {
            if (gameEngine.networkEngine.isServer) {
                playerTeamK.teamColorId = iIntValue;
            } else if (z) {
                gameEngine.networkEngine.b(playerTeamK, -1);
            } else {
                gameEngine.networkEngine.b(playerTeamK, iIntValue);
            }
        }
        gameEngine.networkEngine.refreshAIDifficultyForTeams();
        gameEngine.networkEngine.refreshTeamSortAndAiGroups();
        refreshUI();
    }

    public void disconnect(String str) {
        GameEngine.getInstance().networkEngine.disconnectNetworking(str);
    }

    public void multiplayerBackPrompt() {
        this.root.showPopup(Locale.get("menus.ingame.multiplayerClose.title", new Object[0]), "What would you like to do?", true, (Locale.get("menus.ingame.multiplayerClose.disconnectButton", new Object[0]) + ":") + "closePopup(); mp.disconnect('exited'); back();", null);
    }

    public void surrenderPrompt() {
        this.root.showPopup(Locale.get("menus.ingame.surrender.title", new Object[0]), Locale.get("menus.ingame.surrender.message", new Object[0]), true, (Locale.get("menus.ingame.surrender.surrenderButton", new Object[0]) + ":") + "closePopup(); mp.surrender();", null);
    }

    public void surrender() {
        GameEngine.log("Surrender requested");
        this.root.sendChatMessage("-surrender");
    }

    public void multiplayerExitPrompt() {
        String str = Locale.get("menus.ingame.multiplayerClose.titleDisconnect", new Object[0]);
        String str2 = Locale.get("menus.ingame.multiplayerClose.messageDisconnect", new Object[0]);
        GameEngine gameEngine = GameEngine.getInstance();
        String str3 = (Locale.get("menus.ingame.multiplayerClose.disconnectButton", new Object[0]) + ":") + "closePopup(); mp.disconnect('exited'); showMainMenu();";
        String str4 = null;
        if (gameEngine.networkEngine.isServer) {
            str = Locale.get("menus.ingame.multiplayerClose.title", new Object[0]);
            str2 = Locale.get("menus.ingame.multiplayerClose.messageEndGame", new Object[0]);
            str3 = (Locale.get("menus.ingame.exitGame", new Object[0]) + ":") + "closePopup(); mp.disconnect('exited'); showMainMenu();";
            str4 = (Locale.get("menus.ingame.multiplayerClose.returnToBattleroom", new Object[0]) + ":") + "closePopup(); mp.sendReturnToBattleroomEvent();";
        }
        this.root.showPopup(str, str2, true, str3, str4);
    }

    public void sendReturnToBattleroomEvent() {
        GameEngine.log("mp.sendReturnToBattleroomEvent()");
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.networkEngine.scheduleDefaultReturnToBattleroom();
        gameEngine.gameUI.isDraggingSelection = false;
    }

    public void addAI() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.networkEngine.isServer) {
            gameEngine.networkEngine.addAIToGame();
        } else if (gameEngine.networkEngine.isProxyController) {
            gameEngine.networkEngine.k("-addai");
        } else {
            this.root.logWarn("addAI(): Clicked but not server or proxy controller");
        }
    }

    public String _getRandomDefaultPlayerName() {
        return "Unnamed" + Utility.getRandomIntInRange(0, 999);
    }

    public void loadUsername() {
        GameEngine.log("mp.loadUsername()");
        String str_getRandomDefaultPlayerName = GameEngine.getInstance().settingsEngine.lastNetworkPlayerName;
        Element activeElementById = this.libRocket.getActiveElementById("username");
        String strC = DisabledSteamEngine.a().c();
        GameEngine.log("steamName:" + strC);
        if (strC != null && str_getRandomDefaultPlayerName == null) {
            str_getRandomDefaultPlayerName = strC;
        }
        if (str_getRandomDefaultPlayerName == null || VariableScope.nullOrMissingString.equals(str_getRandomDefaultPlayerName)) {
            str_getRandomDefaultPlayerName = _getRandomDefaultPlayerName();
        }
        activeElementById.loadCharsetIfNeeded(str_getRandomDefaultPlayerName);
        activeElementById.setAttribute("value", str_getRandomDefaultPlayerName);
    }

    public void getUsernameFromInterface() {
        GameEngine gameEngine = GameEngine.getInstance();
        String valueById = this.root.getValueById("username");
        if (valueById == null) {
            GameEngine.logColored("getUsernameFromInterface: Cannot find username");
            return;
        }
        String strTrim = valueById.trim();
        GameEngine.log("set username:" + strTrim);
        if (strTrim.equals(VariableScope.nullOrMissingString)) {
            strTrim = _getRandomDefaultPlayerName();
        }
        gameEngine.networkEngine.setPlayerNameFromInput(strTrim);
    }

    public void gameOptionsGet() {
        gameOptionsGetOrPush(false);
    }

    public void gameOptionsPush() {
        gameOptionsGetOrPush(true);
    }

    public void gameOptionsRefreshTypes() {
        GameEngine.getInstance();
        ElementDocument currentPopup = this.libRocket.getCurrentPopup();
        if (this.useMapDropdown) {
            updateMapDropdown(currentPopup, "mapsSelector", "typeSelector");
        }
    }

    public void gameOptionsGetOrPush(boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        ElementDocument currentPopup = this.libRocket.getCurrentPopup();
        Element elementById = currentPopup.getElementById("fogMode");
        Element elementById2 = currentPopup.getElementById("startingCredits");
        Element elementById3 = currentPopup.getElementById("incomeMultiplier");
        Element elementById4 = currentPopup.getElementById("noNukes");
        Element elementById5 = currentPopup.getElementById("sharedControl");
        Element elementById6 = currentPopup.getElementById("aiDifficulty");
        Element elementById7 = currentPopup.getElementById("startingUnits");
        if (!z) {
            setupStartingUnitDropDown(elementById7, false);
        }
        Element elementById8 = currentPopup.getElementById("typeSelector");
        Element mapDropdown = getMapDropdown();
        if (!z) {
            if (gameEngine.networkEngine.roomSettings.gameModeType == null) {
                GameEngine.log("gameOptionsGetOrPush: game.network.setup.currentType==null");
            } else {
                elementById8.setValue(VariableScope.nullOrMissingString + gameEngine.networkEngine.roomSettings.gameModeType.ordinal());
            }
            if (this.useMapDropdown) {
                updateMapDropdown(currentPopup, "mapsSelector", "typeSelector");
                Element mapDropdown2 = getMapDropdown();
                GameEngine.log("new currentMapSelection=" + gameEngine.networkEngine.roomSettings.mapPath);
                mapDropdown2.setValue(VariableScope.nullOrMissingString + gameEngine.networkEngine.roomSettings.mapPath);
            }
            currentPopup.getElementById("typeSelector");
            elementById.setValue(VariableScope.nullOrMissingString + gameEngine.networkEngine.roomSettings.fogMode);
            elementById2.setValue(VariableScope.nullOrMissingString + gameEngine.networkEngine.roomSettings.startingCredits);
            elementById7.setValue(VariableScope.nullOrMissingString + gameEngine.networkEngine.roomSettings.startingUnits);
            gameEngine.networkEngine.roomSettings.revealedMap = true;
            elementById4.setCheckbox(gameEngine.networkEngine.roomSettings.noNukes);
            elementById5.setCheckbox(gameEngine.networkEngine.roomSettings.sharedControl);
            elementById3.setValue(VariableScope.nullOrMissingString + Utility.padString(gameEngine.networkEngine.roomSettings.incomeMultiplier, 1) + "x");
            elementById6.setValue(VariableScope.nullOrMissingString + gameEngine.networkEngine.roomSettings.aiDifficulty);
            return;
        }
        GameRoomSettings gameRoomSettingsE = gameEngine.networkEngine.getEditableRoomSettings();
        if (gameRoomSettingsE != null) {
            String value = null;
            if (this.useMapDropdown) {
                value = mapDropdown.getValue();
                if (value == null) {
                    GameEngine.log("gameOptionsGetOrPush: mapDropdownSelected==null");
                    value = "<No Map>";
                }
            }
            int iIntValue = elementById8.getValueAsInt(0).intValue();
            GameModeType gameModeType = gameRoomSettingsE.gameModeType;
            gameRoomSettingsE.gameModeType = GameModeType.values()[iIntValue];
            if (this.useMapDropdown) {
                gameRoomSettingsE.mapPath = value;
            } else if (gameModeType != gameRoomSettingsE.gameModeType) {
                gameRoomSettingsE.mapPath = null;
            }
            gameRoomSettingsE.fogMode = elementById.getValueAsInt(null).intValue();
            gameRoomSettingsE.startingCredits = elementById2.getValueAsInt(null).intValue();
            float f = 1.0f;
            try {
                f = Float.parseFloat(elementById3.getValue().replace("x", VariableScope.nullOrMissingString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            gameRoomSettingsE.incomeMultiplier = f;
            gameRoomSettingsE.noNukes = elementById4.getCheckbox();
            gameRoomSettingsE.sharedControl = elementById5.getCheckbox();
            gameRoomSettingsE.aiDifficulty = elementById6.getValueAsInt(null).intValue();
            gameRoomSettingsE.startingUnits = elementById7.getValueAsInt(1).intValue();
            gameEngine.networkEngine.a(gameRoomSettingsE);
        }
    }

    public void closeBattleroomIfOpen() {
        GameEngine.getInstance();
        if (this.libRocket.getActiveElementById("battleroomPage") == null) {
            GameEngine.log("closeBattleroomIfOpen: battleroomPage==null");
        } else {
            this.libRocket.backToLastDocument();
        }
    }

    public void reinviteAsk() {
        this.root.showPopup(Locale.get("menus.ingame.multiplayerReinvite.title", new Object[0]), "While in-game you can only reinvite players who were in-game before but dropped out", true, "reInvite:closePopup(); mp.showSteamInviteDialog();", null);
    }

    public void showSteamInviteDialog() {
        DisabledSteamEngine.a().g();
    }

    public void setMapFromPopup(String str) {
        if (!isInControlOfServer()) {
            this.root.sendChatMessage("clicked on '" + this.root.getMapNameFromPath(str) + "'");
            this.root.closePopup();
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        GameRoomSettings gameRoomSettingsE = gameEngine.networkEngine.getEditableRoomSettings();
        if (gameRoomSettingsE != null) {
            String fileNameWithoutExtension = str;
            if (!fileNameWithoutExtension.contains("MOD|")) {
                fileNameWithoutExtension = Utility.getFileName(fileNameWithoutExtension);
            }
            gameRoomSettingsE.mapPath = fileNameWithoutExtension;
            gameEngine.networkEngine.a(gameRoomSettingsE);
        }
        this.root.closePopup();
    }

    public void showMapSelect() {
        this.root.showMapPopup(this.root.getModeMapPath(null, null), "mp.setMapFromPopup");
    }

    public boolean isInControlOfServer() {
        GameEngine gameEngine = GameEngine.getInstance();
        return gameEngine.networkEngine.isServer || gameEngine.networkEngine.isProxyController;
    }

    public void askPassword() {
        GameEngine.log("mp.askPassword()");
        GameEngine.getInstance();
        this.root.showInputPopupNonClose("Password Required", "This server requires a password to join", VariableScope.nullOrMissingString, "Close:mp.cancelPaswordAsk()", "[onenter]Join:mp.askPasswordEntered(getPopupText())");
    }

    public void askPasswordEntered(String str) {
        GameEngine.log("mp.askPasswordEntered()");
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.networkEngine.roomPassword = str;
        gameEngine.networkEngine.sendRegisterConnectionsToAll();
        this.root.closePopup();
    }

    public void cancelPaswordAsk() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.networkEngine.isServer) {
            this.root.logWarn("cancelPaswordAsk: we are the server");
        } else {
            gameEngine.networkEngine.disconnectNetworking("Cancel password");
            closeBattleroomIfOpen();
        }
        this.root.closePopup();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/Multiplayer$DropdownOption.class */
    public class DropdownOption {
        String key;
        String value;

        public DropdownOption(String str, String str2) {
            this.key = str;
            this.value = str2;
        }
    }

    public void setupStartingUnitDropDown(Element element, boolean z) {
        String str = VariableScope.nullOrMissingString;
        if (z) {
            str = str + generateOption("-99", Locale.get("menus.settings.option.default", new Object[0]), false);
        }
        for (DropdownOption dropdownOption : getStartingUnitOptions()) {
            str = str + generateOption(dropdownOption.key, dropdownOption.value, false);
        }
        element.setInnerRML(str);
    }

    public void setupPlayerColorDropDown(Element element, boolean z, boolean z2, PlayerTeam playerTeam) {
        GameEngine gameEngine = GameEngine.getInstance();
        String str = VariableScope.nullOrMissingString;
        if (z) {
            str = str + generateOption("-99", Locale.get("menus.settings.option.default", new Object[0]), false);
        }
        for (int i = 0; i < 10; i++) {
            boolean z3 = false;
            if (z2 && gameEngine.networkEngine.a(i, playerTeam)) {
                z3 = true;
            }
            String strD = StringUtils.d(PlayerTeam.j(i));
            int i2 = i;
            int i3 = i;
            if (z3) {
                strD = strD + " (used)";
                i2 = -7829368;
                i3 = -99;
            }
            str = str + generateOption(VariableScope.nullOrMissingString + i3, strD, false, Integer.valueOf(PlayerTeam.i(i2)), z3);
        }
        element.setInnerRML(str);
    }

    public ArrayList<DropdownOption> getStartingUnitOptions() {
        GameEngine gameEngine = GameEngine.getInstance();
        ArrayList arrayList = new ArrayList();
        for (Integer num : gameEngine.networkEngine.i()) {
            arrayList.add(new DropdownOption(num.toString(), gameEngine.networkEngine.d(num.intValue())));
        }
        return arrayList;
    }
}
