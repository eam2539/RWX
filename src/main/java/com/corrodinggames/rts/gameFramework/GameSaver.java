package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.AIPlayer;
import com.corrodinggames.rts.game.GameTeam;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.ScorchMark;
import com.corrodinggames.rts.game.ai.AIController;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.EditorOrBuilder;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.ConfigValidationException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfigParser;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.effects.EffectEmitter;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.mission.MissionEngine;
import com.corrodinggames.rts.gameFramework.network.DebugOutputStream;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.TransactionalArrayList;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.y */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/y.class */
public class GameSaver {

    /* JADX INFO: renamed from: a */
    public static boolean debugPlainTextSave = false;

    /* JADX INFO: renamed from: b */
    final boolean isProfilingEnabled;

    /* JADX INFO: renamed from: c */
    int firstTick;

    /* JADX INFO: renamed from: d */
    int lastAutosaveTick;

    public GameSaver() {
        if (!GameEngine.isGameBetaStatic) {
        }
        this.isProfilingEnabled = false;
        this.firstTick = -9999;
        this.lastAutosaveTick = -9999;
    }

    /* JADX INFO: renamed from: a */
    public File isAutosaveEnabled(String str, boolean z) {
        return getSaveFile(str, "saves/", z);
    }

    /* JADX INFO: renamed from: a */
    public static File getSaveFile(String str, String str2, boolean z) {
        return FileHelper.createTempFile(str, str2, z);
    }

    /* JADX INFO: renamed from: b */
    public void updateAutosave(String str, boolean z) throws IOException {
        File fileIsAutosaveEnabled = null;
        GameEngine gameEngine = GameEngine.getInstance();
        String str2 = str;
        if (str2 != null && !str2.endsWith(".rwsave")) {
            str2 = str2 + ".rwsave";
        }
        String absolutePath = "SD card";
        File fileIsAutosaveEnabled2 = null;
        boolean z2 = false;
        try {
            fileIsAutosaveEnabled2 = isAutosaveEnabled(str2 + ".tmp", true);
            if (fileIsAutosaveEnabled2.exists()) {
                fileIsAutosaveEnabled2 = isAutosaveEnabled(str2 + ".tmp2", true);
            }
            fileIsAutosaveEnabled = isAutosaveEnabled(str2, true);
            absolutePath = fileIsAutosaveEnabled.getAbsolutePath();
            GameEngine.isInSpace("Saving game to: " + absolutePath);
            OutputStream outputStreamOpenOutputStream = FileHelper.openOutputStream(fileIsAutosaveEnabled2, false);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStreamOpenOutputStream);
            if (!debugPlainTextSave) {
                DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
                try {
                    createTempFile(new GameOutputStream(dataOutputStream));
                    dataOutputStream.close();
                    bufferedOutputStream.close();
                    outputStreamOpenOutputStream.close();
                } catch (Throwable th) {
                    dataOutputStream.close();
                    bufferedOutputStream.close();
                    outputStreamOpenOutputStream.close();
                    throw th;
                }
            } else {
                PrintStream printStream = new PrintStream(bufferedOutputStream);
                try {
                    createTempFile(new DebugOutputStream(printStream));
                    printStream.close();
                    bufferedOutputStream.close();
                    outputStreamOpenOutputStream.close();
                    GameEngine.reportProblem("DEBUG plain text save created");
                } catch (Throwable th2) {
                    printStream.close();
                    bufferedOutputStream.close();
                    outputStreamOpenOutputStream.close();
                    throw th2;
                }
            }
            if (z && GameEngine.isDesktop() && FileHelper.fileExists(fileIsAutosaveEnabled.getAbsolutePath())) {
                GameEngine.isInSpace("Autosave file already exists: " + fileIsAutosaveEnabled.getAbsolutePath());
                if (!FileHelper.deleteDirectory(fileIsAutosaveEnabled)) {
                    GameEngine.isInSpace("Old autosave failed to delete");
                }
            }
            GameEngine.isInSpace("Finished writing save, renaming to final filename");
        } catch (Exception e) {
            if (z) {
                GameEngine.isInSpace("Auto save failed: " + e.getMessage());
                return;
            }
            e.printStackTrace();
            gameEngine.alert("Error saving game, please check permissions, disk space, etc. (" + Utility.isGreaterThan(e) + ")", 1);
            if (fileIsAutosaveEnabled2 != null && FileHelper.fileExists(fileIsAutosaveEnabled2.getAbsolutePath())) {
                GameEngine.isInSpace("saveGame: Removing temp save file after crash");
                FileHelper.deleteDirectory(fileIsAutosaveEnabled2);
            }
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            gameEngine.alert("Error. Run out of memory error while saving game to " + absolutePath + ".", 1);
            if (fileIsAutosaveEnabled2 != null && FileHelper.fileExists(fileIsAutosaveEnabled2.getAbsolutePath())) {
                GameEngine.isInSpace("saveGame: Removing temp save file after crash");
                FileHelper.deleteDirectory(fileIsAutosaveEnabled2);
            }
        }
        if (!FileHelper.renameFileInternal(fileIsAutosaveEnabled2, fileIsAutosaveEnabled)) {
            GameEngine.isInSpace("Failed to rename to final file");
            throw new IOException("Failed to rename to final file. Check file permissions of storage.");
        }
        FileHelper.scanFile(fileIsAutosaveEnabled);
        z2 = true;
        if (z2) {
            if (z) {
                gameEngine.gameUI.warLogDisplay.a("Auto Saved", 1000);
            } else {
                gameEngine.gameUI.messageManager.addMessage((String) null, "Game saved");
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void createTempFile(GameOutputStream gameOutputStream) throws IOException {
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("GameSaver", "saveCurrentMap took:" + (System.currentTimeMillis() - System.currentTimeMillis()));
        long jCurrentTimeMillis = System.currentTimeMillis();
        try {
            gameOutputStream.writeStringUTF("rustedWarfareSave");
            gameOutputStream.writeInt(gameEngine.getVersionCode(true));
            gameOutputStream.writeInt(96);
            gameOutputStream.writeBoolean(gameEngine.isDemo);
            gameOutputStream.beginBlockInternal("saveCompression", true);
            gameOutputStream.startBlock("customUnitsBlock");
            CustomUnitConfig.a(gameOutputStream);
            gameOutputStream.endBlock("customUnitsBlock");
            gameOutputStream.startBlock("gameSetup");
            boolean z = gameEngine.networkEngine.B || gameEngine.networkEngine.F;
            gameOutputStream.writeBoolean(gameEngine.networkEngine.B);
            gameOutputStream.writeBoolean(gameEngine.networkEngine.F);
            gameOutputStream.writeBoolean(z);
            if (z) {
                gameEngine.networkEngine.getFogModeString(gameOutputStream);
            }
            gameOutputStream.endBlock("gameSetup");
            gameOutputStream.writeStringUTF(gameEngine.currentMapPath);
            boolean z2 = gameEngine.remoteMapStream != null;
            gameOutputStream.writeBoolean(z2);
            if (z2) {
                GameEngine.isInSpace("Writing remote map steam into save");
                gameOutputStream.writeGameInputStreamWithLength(gameEngine.remoteMapStream);
            }
            gameOutputStream.writeInt(gameEngine.lastTick);
            gameOutputStream.writeFloat(gameEngine.viewpointX + gameEngine.halfViewpointWidth);
            gameOutputStream.writeFloat(gameEngine.viewpointY + gameEngine.halfViewpointHeight);
            gameOutputStream.writeFloat(gameEngine.cameraEdgeScrollZone);
            gameOutputStream.writeInt(gameEngine.groupController.a);
            gameOutputStream.writeInt(0);
            gameOutputStream.writeMagicShort();
            gameEngine.tileMap.writeCursorSelectionPresenceFlag(gameOutputStream);
            gameOutputStream.writeBoolean(gameEngine.isGameStarted);
            gameOutputStream.writeBoolean(gameEngine.tileMap.fogEnabled);
            gameOutputStream.writeBoolean(gameEngine.tileMap.fogPeriodicMaintenanceEnabled);
            gameOutputStream.writeBoolean(gameEngine.tileMap.fogRenderActive);
            gameOutputStream.writeBoolean(gameEngine.missionEngine != null);
            if (gameEngine.missionEngine != null) {
                gameEngine.missionEngine.a(gameOutputStream);
            }
            gameOutputStream.writeMagicShort();
            int i = -1;
            if (gameEngine.playerTeam != null) {
                i = gameEngine.playerTeam.teamId;
            }
            gameOutputStream.writeInt(i);
            gameOutputStream.writeInt(PlayerTeam.TEAM_NEUTRAL);
            for (int i2 = 0; i2 < PlayerTeam.TEAM_NEUTRAL; i2++) {
                PlayerTeam playerTeamK = PlayerTeam.k(i2);
                gameOutputStream.writeBoolean(playerTeamK instanceof AIController);
                gameOutputStream.writeBoolean(playerTeamK instanceof AIPlayer);
                gameOutputStream.writeBoolean(playerTeamK != null);
                if (playerTeamK != null) {
                    playerTeamK.writeToStream(gameOutputStream);
                }
            }
            if (!gameEngine.gameUI.e) {
            }
            gameOutputStream.debugPlaceholder("Section: unit shells");
            gameOutputStream.writeInt(GameObject.fastGameObjectList.size());
            for (GameObject gameObject : GameObject.fastGameObjectList) {
                if (gameObject == null) {
                    throw new RuntimeException("Found null in fastGameObjectList");
                }
                if (gameObject instanceof BaseUnit) {
                    BaseUnit baseUnit = (BaseUnit) gameObject;
                    if (baseUnit.r() instanceof UnitTypeEnum) {
                        gameOutputStream.writeByte(1);
                        gameOutputStream.writeEnumOrdinal((Enum) baseUnit.r());
                    } else if (baseUnit.r() instanceof CustomUnitConfig) {
                        gameOutputStream.writeByte(3);
                        gameOutputStream.writeStringUTF(((CustomUnitConfig) baseUnit.r()).onNewMapSpawn);
                    } else {
                        throw new IOException("Unhandled getUnitType on save:" + baseUnit.r().getClass().toString());
                    }
                } else {
                    gameOutputStream.writeByte(2);
                    if (gameObject instanceof ScorchMark) {
                        gameOutputStream.writeByte(1);
                    } else if (gameObject instanceof Projectile) {
                        gameOutputStream.writeByte(2);
                    } else if (gameObject instanceof EffectEmitter) {
                        gameOutputStream.writeByte(3);
                    } else {
                        String string = null;
                        if (gameObject.getClass() != null) {
                            string = gameObject.getClass().toString();
                        }
                        throw new IOException("Unhandled class on save: " + string);
                    }
                }
                gameOutputStream.writeLong(gameObject.objectId);
            }
            gameOutputStream.debugPlaceholder("Section: CurrentUnitId");
            gameOutputStream.writeLong(gameEngine.networkEngine.z());
            gameEngine.groupController.a(gameOutputStream);
            gameEngine.gameUI.a(gameOutputStream);
            gameEngine.gameStatistics.a(gameOutputStream);
            for (int i3 = 0; i3 < PlayerTeam.TEAM_NEUTRAL; i3++) {
                PlayerTeam playerTeamK2 = PlayerTeam.k(i3);
                if (playerTeamK2 != null) {
                    playerTeamK2.a(gameOutputStream);
                }
            }
            gameOutputStream.writeMagicShort();
            for (GameObject gameObject2 : GameObject.fastGameObjectList) {
                if (gameOutputStream.isCompressionEnabled()) {
                    String simpleName = gameObject2.getClass().getSimpleName();
                    if (gameObject2 instanceof BaseUnit) {
                        simpleName = ((BaseUnit) gameObject2).r().getUnitTypeDescriptionShort();
                    }
                    gameOutputStream.debugPlaceholder("Saving unit:" + simpleName + " (id" + gameObject2.objectId + ")");
                }
                gameObject2.a(gameOutputStream);
                gameOutputStream.writeMagicShort();
            }
            gameOutputStream.endBlock("saveCompression");
            gameOutputStream.writeMagicShort();
            gameOutputStream.writeStringUTF("<SAVE END>");
            GameEngine.log("GameSaver", "saveGame took:" + (System.currentTimeMillis() - jCurrentTimeMillis));
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* JADX INFO: renamed from: a */
    public String writeSaveGame(String str) {
        if (str == null) {
            return null;
        }
        return str.equals("maps/normal/l010;mission_1__-__Dividing_River.tmx") ? "maps/normal/l010;[demo]mission_1__-__Dividing_River.tmx" : str.equals("maps/normal/l030;mission_3__-__Crossfire.tmx") ? "maps/normal/l030;[demo]mission_3__-__Crossfire.tmx" : str;
    }

    /* JADX INFO: renamed from: c */
    public boolean performAutosave(String str, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        try {
            File fileIsAutosaveEnabled = isAutosaveEnabled(str, false);
            if (fileIsAutosaveEnabled.isDirectory()) {
                gameEngine.alert("Could not load, is a directory", 1);
                return false;
            }
            AssetInputStream assetInputStreamOpenFileByPath = FileHelper.openFileByPath(fileIsAutosaveEnabled.getAbsolutePath());
            if (assetInputStreamOpenFileByPath == null) {
                gameEngine.alert("Could not load, failed to open: " + FileHelper.getFileName(fileIsAutosaveEnabled.getAbsolutePath()), 1);
                return false;
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(assetInputStreamOpenFileByPath);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            try {
                boolean zWriteSaveToStream = writeSaveToStream(new GameInputStream(dataInputStream), z, false, false);
                dataInputStream.close();
                bufferedInputStream.close();
                assetInputStreamOpenFileByPath.close();
                return zWriteSaveToStream;
            } catch (Throwable th) {
                dataInputStream.close();
                bufferedInputStream.close();
                assetInputStreamOpenFileByPath.close();
                throw th;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: a */
    public void convertMapPath(String str, GameOutputStream gameOutputStream) throws IOException {
        File fileIsAutosaveEnabled = isAutosaveEnabled(str, false);
        if (fileIsAutosaveEnabled == null) {
            throw new IOException("Failed to get game save: " + str);
        }
        gameOutputStream.writeStreamWithLength(fileIsAutosaveEnabled);
    }

    /* JADX INFO: renamed from: a */
    public synchronized boolean writeSaveToStream(GameInputStream gameInputStream, boolean z, boolean z2, boolean z3) {
        GameObject effectEmitter;
        PlayerTeam playerTeamK;
        GameEngine gameEngine = GameEngine.getInstance();
        try {
            PerformanceProfiler performanceProfiler = gameEngine.performanceProfiler;
            if (this.isProfilingEnabled) {
                performanceProfiler.a(ProfilerSection.load_total);
            }
            ArrayList arrayList = null;
            if (z3) {
                arrayList = new ArrayList();
                Iterator it = gameEngine.gameUI.selectedUnitsList.iterator();
                while (it.hasNext()) {
                    arrayList.add(Long.valueOf(((BaseUnit) it.next()).objectId));
                }
            }
            try {
                try {
                    String utf = gameInputStream.readUTF();
                    if (!utf.equals("rustedWarfareSave")) {
                        GameEngine.updatePaintTextSizeIfNeeded("Map Load: Header is not correct:" + utf.substring(0, Math.min(utf.length(), 50)));
                        String str = "Failed to load save. (Could not find correct header)";
                        if (utf.equals("rustedWarfareReplay")) {
                            str = "Failed to load save. (This file appears to be a replay file, not a save file)";
                        }
                        GameEngine.updatePaintTextSizeIfNeeded(str);
                        gameEngine.alert(str, 1);
                        return false;
                    }
                    gameInputStream.readInt();
                    int i = gameInputStream.readInt();
                    GameEngine.log("gameSaver", "Loading save from version: " + i);
                    gameInputStream.setProtocolVersion(i);
                    if (i > 96) {
                        gameEngine.alert("Cannot load: This save was made with a newer game", 1);
                        return false;
                    }
                    if (i >= 5) {
                        gameInputStream.readBoolean();
                    }
                    if (i >= 23) {
                        performanceProfiler.a(ProfilerSection.load_compression);
                        gameInputStream.a("saveCompression", true);
                        performanceProfiler.b(ProfilerSection.load_compression);
                    }
                    if (i >= 54) {
                        gameInputStream.startBlockNamed("customUnitsBlock");
                        if (gameEngine.replayEngine.j() && !z3) {
                            GameEngine.isInSpace("Loading mods from replay");
                            try {
                                CustomUnitConfig.loadAndValidateCustomUnits(gameInputStream);
                                CustomUnitConfigParser.applyPendingNetworkUnits();
                            } catch (ConfigValidationException e) {
                                GameEngine.isInSpace("Replay load: Missing unit:" + e.getMessage() + " d:" + e.messageDetail);
                                gameEngine.alert(e.getMessage() + ", this is likely to cause the replay to desync (reverting to default units & mods)");
                                CustomUnitConfigParser.enableAllCustomUnits(true);
                            }
                        }
                        gameInputStream.d("customUnitsBlock");
                    }
                    Integer numValueOf = null;
                    Integer numValueOf2 = null;
                    if (gameEngine.replayEngine.j() && z3) {
                        numValueOf = Integer.valueOf(gameEngine.currentTimeMillis);
                        numValueOf2 = Integer.valueOf(gameEngine.lastTimeMillis);
                    }
                    if (i >= 56) {
                        gameInputStream.startBlockNamed("gameSetup");
                        boolean z4 = gameInputStream.readBoolean();
                        boolean z5 = z4;
                        boolean z6 = false;
                        if (i >= 94) {
                            z6 = gameInputStream.readBoolean();
                            z5 = gameInputStream.readBoolean();
                        }
                        if ((gameEngine.replayEngine.j() || !gameEngine.networkEngine.B) && !z3 && z5) {
                            GameEngine.isInSpace("Using game rules from save");
                            gameEngine.replayEngine.O = true;
                            gameEngine.networkEngine.a(gameInputStream);
                            numValueOf = Integer.valueOf(gameEngine.currentTimeMillis);
                            numValueOf2 = Integer.valueOf(gameEngine.lastTimeMillis);
                            if ((z4 || z6) && !gameEngine.networkEngine.F && !gameEngine.networkEngine.B && !gameEngine.replayEngine.j()) {
                                GameEngine.isInSpace("Enabling use of singlePlayer rules from saved game.");
                                gameEngine.networkEngine.F = true;
                            }
                        }
                        gameInputStream.d("gameSetup");
                    }
                    gameEngine.remoteMapStream = null;
                    gameEngine.currentMapPath = writeSaveGame(FileHelper.mapPath(gameInputStream.readUTF()));
                    boolean z7 = false;
                    if (i >= 72) {
                        z7 = gameInputStream.readBoolean();
                        if (z7) {
                            GameEngine.isInSpace("Reading remote map stream");
                            gameEngine.remoteMapStream = gameInputStream.readNestedStream();
                        }
                    }
                    if (gameEngine.networkEngine.B && !gameEngine.networkEngine.isServer && z3 && gameEngine.networkEngine.aB != null && !z7) {
                        gameEngine.currentMapPath = VariableScope.nullOrMissingString;
                        gameEngine.remoteMapStream = gameEngine.networkEngine.aB;
                    }
                    performanceProfiler.a(ProfilerSection.load_map);
                    if (z3) {
                        gameEngine.loadLevel(true, true, GameMode.normalSave);
                        if (GameEngine.isDesktop()) {
                            gameEngine.pinchDistance = true;
                        }
                    } else {
                        gameEngine.loadGame(true, GameMode.normalSave);
                    }
                    if (!gameEngine.tileMap.isCursorActive) {
                        GameEngine.isInSpace("Not loading save because map failed to load");
                        return false;
                    }
                    if (numValueOf != null) {
                        gameEngine.currentTimeMillis = numValueOf.intValue();
                    }
                    if (numValueOf2 != null) {
                        Integer.valueOf(gameEngine.lastTimeMillis);
                    }
                    synchronized (gameEngine) {
                        performanceProfiler.b(ProfilerSection.load_map);
                        gameEngine.lastTick = gameInputStream.readInt();
                        float f = gameInputStream.readFloat();
                        float f2 = gameInputStream.readFloat();
                        float f3 = gameInputStream.readFloat();
                        if (!z3) {
                            gameEngine.centerViewpoint(f, f2);
                            gameEngine.cameraEdgeScrollZone = f3;
                        }
                        if (i >= 18) {
                            gameEngine.groupController.a = gameInputStream.readInt();
                        }
                        gameInputStream.readInt();
                        if (i >= 19) {
                            gameInputStream.a("end of setup");
                        }
                        gameEngine.tileMap.readCursorSelectionBlockFromStream(gameInputStream);
                        if (i >= 86) {
                            boolean z8 = gameInputStream.readBoolean();
                            boolean z9 = gameInputStream.readBoolean();
                            boolean z10 = gameInputStream.readBoolean();
                            boolean z11 = gameInputStream.readBoolean();
                            if (!z && !z8) {
                                gameEngine.tileMap.fogEnabled = z9;
                                gameEngine.tileMap.fogPeriodicMaintenanceEnabled = z10;
                                gameEngine.tileMap.fogRenderActive = z11;
                            }
                        }
                        if (gameInputStream.readBoolean()) {
                            if (gameEngine.missionEngine == null) {
                                GameEngine.log("gameSaver", "making new mission engine on load, this shouldn't happen");
                                gameEngine.missionEngine = new MissionEngine();
                                gameEngine.missionEngine.a(false);
                            }
                            gameEngine.missionEngine.a(gameInputStream);
                        }
                        if (i >= 19) {
                            gameInputStream.a("start of teams");
                        }
                        GameEngine.log("gameSaver", "loading teams");
                        PlayerTeam[] playerTeamArr = new PlayerTeam[PlayerTeam.TEAM_ALLIES];
                        int i2 = -1;
                        if (i >= 36) {
                            i2 = gameInputStream.readInt();
                        }
                        int i3 = 8;
                        if (i >= 49) {
                            i3 = gameInputStream.readInt();
                            PlayerTeam.getResourceCost(i3, false);
                            for (int i4 = 0; i4 < PlayerTeam.TEAM_NEUTRAL; i4++) {
                                if (i4 >= i3 && !z && (playerTeamK = PlayerTeam.k(i4)) != null) {
                                    playerTeamK.updateTeamActiveStatus();
                                }
                            }
                        }
                        for (int i5 = 0; i5 < i3; i5++) {
                            PlayerTeam playerTeamK2 = PlayerTeam.k(i5);
                            boolean z12 = gameInputStream.readBoolean();
                            boolean z13 = false;
                            if (i >= 7) {
                                z13 = gameInputStream.readBoolean();
                            }
                            if (gameInputStream.readBoolean()) {
                                if (z12) {
                                    if (playerTeamK2 == null || !(playerTeamK2 instanceof AIController)) {
                                        if (z && !z3 && playerTeamK2 != null) {
                                            GameEngine.updatePaintTextSizeIfNeeded("Would replace team:" + i5 + " with AI, writing to dummy AI");
                                            playerTeamK2 = new AIController(i5, false);
                                            playerTeamArr[i5] = playerTeamK2;
                                        } else {
                                            if (z3) {
                                                GameEngine.updatePaintTextSizeIfNeeded("Adding new AI " + i5 + " on resync");
                                            }
                                            playerTeamK2 = new AIController(i5);
                                        }
                                    }
                                } else if (z13) {
                                    if (playerTeamK2 == null || !(playerTeamK2 instanceof AIPlayer)) {
                                        if (z) {
                                            GameEngine.updatePaintTextSizeIfNeeded("Replacing team:" + i5 + " with NetworkedPlayer");
                                        }
                                        playerTeamK2 = new AIPlayer(i5);
                                    }
                                } else if (playerTeamK2 == null || !(playerTeamK2 instanceof GameTeam)) {
                                    if (z) {
                                        GameEngine.updatePaintTextSizeIfNeeded("Replacing team:" + i5 + " with Player");
                                        if (playerTeamK2 != null) {
                                            playerTeamK2.c("Existing");
                                        }
                                    }
                                    playerTeamK2 = new GameTeam(i5);
                                }
                                Integer num = playerTeamK2.teamAIDifficultyOverride;
                                if (i >= 2) {
                                    playerTeamK2.getAlliedTeams(gameInputStream);
                                } else {
                                    playerTeamK2.getActiveTeams(gameInputStream);
                                }
                                if (!z3) {
                                    playerTeamK2.getTeamColorIdByName();
                                    if (z) {
                                        playerTeamK2.teamAIDifficultyOverride = num;
                                        playerTeamK2.c("networkLoad aiDifficultyOverride=" + num);
                                        gameEngine.networkEngine.a(playerTeamK2);
                                        gameEngine.networkEngine.getAIDifficultyString(playerTeamK2);
                                    }
                                    if (playerTeamK2 != null && playerTeamK2 != playerTeamK2) {
                                        playerTeamK2.c("Transfering team stats");
                                        playerTeamK2.credits = playerTeamK2.credits;
                                        playerTeamK2.getTeamColorEffect().a(playerTeamK2.getTeamColorEffect());
                                    }
                                }
                            } else if (z && !gameEngine.replayEngine.j()) {
                                GameEngine.updatePaintTextSizeIfNeeded("GameSaver: Would normally remove team:" + i5 + VariableScope.nullOrMissingString);
                                playerTeamArr[i5] = PlayerTeam.TEAM_NULL;
                            } else {
                                PlayerTeam playerTeamK3 = PlayerTeam.k(i5);
                                if (playerTeamK3 != null) {
                                    playerTeamK3.updateTeamActiveStatus();
                                }
                            }
                        }
                        boolean z14 = false;
                        gameEngine.networkEngine.aq();
                        if (gameEngine.replayEngine.j()) {
                            gameEngine.playerTeam = PlayerTeam.TEAM_ALL;
                        } else if (gameEngine.networkEngine.B) {
                            if (gameEngine.networkEngine.localPlayerTeam != null) {
                                int i6 = gameEngine.networkEngine.localPlayerTeam.teamId;
                                if (i6 != -3) {
                                    PlayerTeam playerTeamK4 = PlayerTeam.k(i6);
                                    if (playerTeamK4 == null) {
                                        throw new RuntimeException("GameSaver: Cannot relink player team: " + i6);
                                    }
                                    gameEngine.playerTeam = playerTeamK4;
                                }
                            }
                        } else if (i2 != -1 && i2 != -3) {
                            gameEngine.playerTeam = PlayerTeam.k(i2);
                        } else {
                            for (int i7 = 0; i7 < PlayerTeam.TEAM_NEUTRAL; i7++) {
                                if (PlayerTeam.k(i7) instanceof GameTeam) {
                                    gameEngine.playerTeam = PlayerTeam.k(i7);
                                }
                            }
                        }
                        for (GameObject o : GameObject.dK()) {
                            o.remove();
                        }
                        if (gameEngine.isMissionActive()) {
                            for (GameObject gameObject : GameObject.dK()) {
                                if (gameObject.objectId == 0) {
                                    if (gameObject instanceof BaseUnit) {
                                        GameEngine.isInSpace("object: " + ((BaseUnit) gameObject).getUnitShortName());
                                    }
                                    throw new RuntimeException("GameLoad preload: Found object in list with id:0");
                                }
                            }
                        }
                        boolean z15 = false;
                        int i8 = gameInputStream.readInt();
                        for (int i9 = 0; i9 < i8; i9++) {
                            byte b = gameInputStream.readByte();
                            if (b == 1) {
                                UnitTypeEnum unitTypeEnum = (UnitTypeEnum) gameInputStream.readEnumOrdinalOrNull(UnitTypeEnum.class);
                                if (unitTypeEnum == UnitTypeEnum.editorOrBuilder) {
                                    if (gameEngine.replayEngine.j() || gameEngine.isGameStarted) {
                                        GameEngine.isInSpace("Creating DebugEditorBuilder for replay");
                                        effectEmitter = new EditorOrBuilder(false);
                                        EditorOrBuilder editorOrBuilder = gameEngine.gameUI.getEditorOrBuilder();
                                        if (editorOrBuilder == null || editorOrBuilder.isDestroyed) {
                                            GameEngine.isInSpace("Relinking editor");
                                            gameEngine.gameUI.drawTextInRect((EditorOrBuilder) effectEmitter);
                                        }
                                    } else {
                                        GameEngine.isInSpace("Creating DebugEditorBuilder for load");
                                        effectEmitter = new EditorOrBuilder(false);
                                        z14 = true;
                                    }
                                } else {
                                    effectEmitter = unitTypeEnum.a();
                                }
                            } else if (b == 3) {
                                String utf2 = gameInputStream.readUTF();
                                CustomUnitConfig customUnitConfigFindConfigByName = CustomUnitConfig.findConfigByName(utf2);
                                if (customUnitConfigFindConfigByName == null) {
                                    String str2 = "Could not find custom unit:" + utf2;
                                    GameEngine.isInSpace(str2);
                                    if (!z15) {
                                        z15 = true;
                                        NetworkEngine.g(str2);
                                    }
                                    customUnitConfigFindConfigByName = CustomUnitConfig.instance;
                                    if (customUnitConfigFindConfigByName == null) {
                                        throw new RuntimeException("Could not find custom unit:" + utf2 + " and missingPlaceHolder is null");
                                    }
                                }
                                UnitType unitTypeC = CustomUnitConfig.c(customUnitConfigFindConfigByName);
                                if (unitTypeC != null) {
                                    if (unitTypeC instanceof CustomUnitConfig) {
                                        customUnitConfigFindConfigByName = (CustomUnitConfig) unitTypeC;
                                    } else {
                                        GameEngine.updatePaintTextSizeIfNeeded("replacement not a custom unit:" + unitTypeC.getUnitTypeDescriptionShort());
                                    }
                                }
                                effectEmitter = customUnitConfigFindConfigByName.a();
                            } else if (b == 2) {
                                byte b2 = gameInputStream.readByte();
                                if (b2 == 1) {
                                    effectEmitter = new ScorchMark();
                                } else if (b2 == 2) {
                                    effectEmitter = new Projectile(false);
                                } else if (b2 == 3) {
                                    effectEmitter = new EffectEmitter(gameEngine.effectManager);
                                } else {
                                    throw new IOException("Unhandled gameType on load:" + ((int) b2));
                                }
                            } else {
                                throw new IOException("Unhandled basic type on load:" + ((int) b));
                            }
                            effectEmitter.objectId = gameInputStream.readLong();
                            if (effectEmitter.objectId == 0) {
                                GameEngine.updatePaintTextSizeIfNeeded("GameSaver: Adding object with id==0");
                                if (effectEmitter instanceof BaseUnit) {
                                    GameEngine.updatePaintTextSizeIfNeeded(((BaseUnit) effectEmitter).getVelocityY());
                                }
                            }
                            GameObject.dL();
                        }
                        if (i >= 3) {
                            long j = gameInputStream.readLong();
                            if (j <= 0) {
                                GameEngine.printLog("GameLoad: Trying to set next unit id <= 0: " + j);
                                j = 100000;
                            }
                            gameEngine.networkEngine.a(j);
                        } else {
                            gameEngine.networkEngine.a(100000L);
                        }
                        if (i >= 24) {
                            gameEngine.groupController.a(gameInputStream);
                        }
                        if (i >= 4) {
                            gameEngine.gameUI.a(gameInputStream, z3);
                        }
                        if (i >= 57) {
                            gameEngine.gameStatistics.a(gameInputStream, z3);
                        }
                        if (i >= 7) {
                            for (int i10 = 0; i10 < i3; i10++) {
                                PlayerTeam playerTeamK5 = PlayerTeam.k(i10);
                                if (playerTeamArr[i10] != null) {
                                    playerTeamK5 = playerTeamArr[i10];
                                    if (playerTeamK5 == PlayerTeam.TEAM_NULL) {
                                        playerTeamK5 = null;
                                    }
                                }
                                if (playerTeamK5 != null) {
                                    Integer num2 = playerTeamK5.teamAIDifficultyOverride;
                                    playerTeamK5.getActiveTeams(gameInputStream);
                                    if (!z3) {
                                        if (z) {
                                            playerTeamK5.teamAIDifficultyOverride = num2;
                                            playerTeamK5.c("networkLoad2 aiDifficultyOverride=" + num2);
                                        }
                                        gameEngine.networkEngine.a(playerTeamK5);
                                        gameEngine.networkEngine.getAIDifficultyString(playerTeamK5);
                                    }
                                }
                            }
                        } else if (i >= 2) {
                        }
                        if (i >= 10) {
                            gameInputStream.a("Pre-unit data");
                        }
                        performanceProfiler.a(ProfilerSection.load_units);
                        TransactionalArrayList<GameObject> transactionalArrayListDK = GameObject.dK();
                        GameEngine.log("gameSaver", "Loading unit data for " + transactionalArrayListDK.size() + " objects.");
                        for (GameObject gameObject2 : transactionalArrayListDK) {
                            gameObject2.a(gameInputStream);
                            if (i >= 10) {
                                gameInputStream.a("post unit: " + gameObject2.getClass().toString() + " with id:" + gameObject2.objectId);
                            }
                        }
                        GameEngine.log("gameSaver", "Loading unit data done.");
                        performanceProfiler.b(ProfilerSection.load_units);
                        if (z14) {
                            for (GameObject gameObject3 : GameObject.dK()) {
                                if (gameObject3 instanceof BaseUnit) {
                                    BaseUnit baseUnit = (BaseUnit) gameObject3;
                                    if (!gameEngine.replayEngine.j() && !gameEngine.isGameStarted && baseUnit.r() == UnitTypeEnum.editorOrBuilder) {
                                        baseUnit.getUnitAICondition();
                                    }
                                }
                            }
                        }
                        if (i >= 23) {
                            gameInputStream.d("saveCompression");
                        }
                        if (i >= 19) {
                            gameInputStream.a("End of Save");
                            gameInputStream.readUTF();
                        }
                        GameEngine.log("gameSaver", "Checking for ID overlaps");
                        int i11 = 0;
                        if (1 != 0) {
                            GameObject[] gameObjectArrA = GameObject.fastGameObjectList.a();
                            int size = GameObject.fastGameObjectList.size();
                            for (int i12 = 0; i12 < size; i12++) {
                                GameObject gameObject4 = gameObjectArrA[i12];
                                if (gameObject4.objectId == 0) {
                                    GameEngine.updatePaintTextSizeIfNeeded("GameSaver: Fixing object with zero id.");
                                    gameObject4.objectId = gameEngine.networkEngine.y();
                                }
                                for (int i13 = i12 + 1; i13 < size; i13++) {
                                    GameObject gameObject5 = gameObjectArrA[i13];
                                    if (gameObject4 != gameObject5 && gameObject4.objectId == gameObject5.objectId) {
                                        i11++;
                                        gameObject5.objectId = gameEngine.networkEngine.y();
                                    }
                                }
                            }
                        }
                        GameEngine.log("gameSaver", "clearing out dead units.");
                        GameEngine.isInSpace("Unit.fastLiveUnitList before:" + BaseUnit.bE.size());
                        Iterator it3 = BaseUnit.bE.iterator();
                        while (it3.hasNext()) {
                            if (((BaseUnit) it3.next()).isDestroyed) {
                                it3.remove();
                            }
                        }
                        GameEngine.isInSpace("Unit.fastLiveUnitList after:" + BaseUnit.bE.size());
                        if (i11 > 0) {
                            if (i <= 2) {
                                gameEngine.alert("Warning: " + i11 + " errors were found in this save, this is due to a bug in the old version", 1);
                            } else {
                                gameEngine.alert("Warning: " + i11 + " errors were found in this save", 1);
                            }
                        }
                        GameEngine.log("gameSaver", "Fixing map cost.");
                        gameEngine.pathfindingEngine.a((OrderableUnit) null);
                        gameEngine.pathfindingEngine.b();
                        GameEngine.log("gameSaver", "Fixing map cost done.");
                        PlayerTeam.updateTeamVictoryStatus();
                        for (int i14 = 0; i14 < PlayerTeam.TEAM_NEUTRAL; i14++) {
                            PlayerTeam playerTeamK6 = PlayerTeam.k(i14);
                            if (playerTeamK6 != null) {
                                playerTeamK6.d(false);
                            }
                        }
                        PlayerTeam.getTeamStatistics();
                        GameEngine.log("gameSaver", "Rebuilt unit caches");
                        PlayerTeam.updateTeamVictoryStatus();
                        PlayerTeam.staticUpdateTeamColors();
                        PlayerTeam.TEAM_ALL.d(false);
                        PlayerTeam.TEAM_UNKNOWN.d(false);
                        for (int i15 = 0; i15 < PlayerTeam.TEAM_NEUTRAL; i15++) {
                            PlayerTeam playerTeamK7 = PlayerTeam.k(i15);
                            if (playerTeamK7 != null && (playerTeamK7 instanceof AIController)) {
                                ((AIController) playerTeamK7).checkZoneIntegrity();
                            }
                        }
                        if (arrayList != null) {
                            gameEngine.gameUI.clearSelection();
                            Iterator it4 = arrayList.iterator();
                            while (it4.hasNext()) {
                                BaseUnit baseUnitA = GameObject.a(((Long) it4.next()).longValue(), true);
                                if (baseUnitA != null) {
                                    gameEngine.gameUI.addToSelection(baseUnitA);
                                }
                            }
                        }
                        if (gameEngine.isMissionActive()) {
                            for (GameObject gameObject6 : GameObject.dK()) {
                                if (gameObject6.objectId == 0) {
                                    if (gameObject6 instanceof BaseUnit) {
                                        GameEngine.isInSpace("object: " + ((BaseUnit) gameObject6).getUnitShortName());
                                    }
                                    throw new RuntimeException("GameLoad postload: Found object in list with id:0");
                                }
                            }
                        }
                        GameEngine.isInSpace("--- Save file load complete ---");
                        GameEngine.isInSpace("GameObject.fastGameObjectList:" + GameObject.fastGameObjectList.size());
                        GameEngine.isInSpace("Unit.fastLiveUnitList:" + BaseUnit.bE.size());
                        if (!z3) {
                            gameEngine.replayEngine.a(z3);
                        }
                        if (this.isProfilingEnabled) {
                            performanceProfiler.b(ProfilerSection.load_total);
                            performanceProfiler.a(true, true);
                        }
                    }
                    return true;
                } catch (EOFException e2) {
                    e2.printStackTrace();
                    GameEngine.updatePaintTextSizeIfNeeded("Failed to load save. (End of file trying to read header)");
                    gameEngine.alert("Failed to load save. (End of file trying to read header)", 1);
                    return false;
                }
            } catch (IOException e3) {
                e3.printStackTrace();
                String str3 = "Failed to load save. (Failed to read header: " + e3.getMessage() + ")";
                GameEngine.updatePaintTextSizeIfNeeded(str3);
                gameEngine.alert(str3, 1);
                return false;
            }
        } catch (Exception e4) {
            e4.printStackTrace();
            GameEngine.isInSpace("Save load error, clearing all units");
            for (GameObject gameObject7 : GameObject.dK()) {
                if (gameObject7.objectId == 0) {
                    gameObject7.objectId = gameEngine.networkEngine.y();
                }
                gameObject7.remove();
            }
            throw new RuntimeException(e4);
        }
    }

    /* JADX INFO: renamed from: b */
    public boolean saveGame(String str) {
        GameEngine.isInSpace("Deleting: " + str);
        String strMapPath = FileHelper.mapPath(str);
        if (strMapPath.contains("\\") || strMapPath.contains("/")) {
            GameEngine.isInSpace("Cannot get save with path: " + str);
            return false;
        }
        File fileIsAutosaveEnabled = isAutosaveEnabled(str, true);
        boolean zDeleteDirectory = FileHelper.deleteDirectory(fileIsAutosaveEnabled);
        FileHelper.deleteDirectory(isAutosaveEnabled(str + ".map", true));
        if (!zDeleteDirectory) {
            GameEngine.isInSpace("Failed to delete: " + fileIsAutosaveEnabled.getAbsolutePath());
            GameEngine.getInstance().alert("Failed to delete: " + fileIsAutosaveEnabled.getAbsolutePath());
            return true;
        }
        return true;
    }

    /* JADX INFO: renamed from: a */
    public void readSaveGame(boolean z) {
        GameEngine.getInstance();
        if (!z) {
            this.firstTick = -9999;
            this.lastAutosaveTick = -9999;
        }
    }

    /* JADX INFO: renamed from: a */
    public boolean resetAutosaveTimers() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!gameEngine.settingsEngine.autosaving || GameEngine.isDebug() || !gameEngine.loadNewGame || gameEngine.reloadMap || gameEngine.replayEngine.j() || gameEngine.isNetworkGameActive()) {
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: b */
    public void deleteSave() throws IOException {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!resetAutosaveTimers()) {
            return;
        }
        if (this.lastAutosaveTick == -9999) {
            this.firstTick = gameEngine.lastTick;
            this.lastAutosaveTick = gameEngine.lastTick;
        }
        if (this.lastAutosaveTick + 300000 < gameEngine.lastTick) {
            this.lastAutosaveTick = gameEngine.lastTick;
            long jA = PerformanceProfiler.a();
            loadSave();
            GameEngine.isInSpace("Autosaved (" + PerformanceProfiler.a(PerformanceProfiler.a(jA)) + ")");
        }
    }

    /* JADX INFO: renamed from: c */
    public void loadSave() throws IOException {
        updateAutosave("autosave", true);
    }
}
