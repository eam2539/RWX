package com.corrodinggames.librocket.scripts;

import com.corrodinggames.rts.debug.DebugSocketServer;
import com.corrodinggames.rts.debug.test.Assert;
import com.corrodinggames.rts.debug.test.NetworkSocketsTest;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.*;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.*;
import com.corrodinggames.rts.gameFramework.network.*;
import com.corrodinggames.rts.gameFramework.pathfinding.FastNodeQueue;
import com.corrodinggames.rts.gameFramework.pathfinding.Path;
import com.corrodinggames.rts.gameFramework.pathfinding.PathOpenListPool;
import com.corrodinggames.rts.gameFramework.pathfinding.PathPoint;
import net.rudp.ReliableSocket;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/Debug.class */
public class Debug extends ScriptContext {
    Root root;
    boolean allFeatures;
    ConcurrentLinkedQueue<NetworkConnection> backgroundClientConnections;
    Thread backgroundConnectionThread;
    Runnable backgroundConnectionRunnable = new Runnable() { // from class: com.corrodinggames.librocket.scripts.Debug.1
        @Override // java.lang.Runnable
        public void run() {
            for (NetworkConnection networkConnection : Debug.this.backgroundClientConnections) {
            }
        }
    };
    boolean forceNonThreaded = true;

    Debug(Root root) {
        this.root = root;
    }

    public int currentPid() {
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            int iIndexOf = name.indexOf(64);
            String str = iIndexOf > 0 ? name.substring(0, iIndexOf) : name;
            return Integer.parseInt(str);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setLocalPlayerName(String str) {
        GameEngine.getInstance().networkEngine.applyAIDifficultyOverride(str);
    }

    public void setDdosProtection(boolean z) {
        ConnectionAcceptor.b = z;
    }

    public void lookAt(float f, float f2) {
        GameEngine.getInstance().centerViewpoint(f, f2);
    }

    public void createManyUnits(String str, float f, float f2, int i, boolean z, int i2) {
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (i5 < i2) {
            i3 += 9;
            if (i3 > 400) {
                i3 = 0;
                i4 += 9;
            }
            createUnit(str, f + i3, f2 + i4, i, i5 == 0 ? z : false);
            i5++;
        }
    }

    public Long createUnit(String str, float f, float f2, int i, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        UnitType unitTypeByName = UnitTypeEnum.getUnitTypeByName(str);
        if (unitTypeByName == null) {
            this.root.logWarn("Could not find type:" + str);
            return null;
        }
        BaseUnit baseUnitA = unitTypeByName.a();
        baseUnitA.posX = f;
        baseUnitA.posY = f2;
        try {
            baseUnitA.setTeam(i);
            PlayerTeam.c(baseUnitA);
            baseUnitA.isInitialized = true;
            if (z) {
                gameEngine.centerViewpoint(f, f2);
            }
            return Long.valueOf(baseUnitA.objectId);
        } catch (MapLoadException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMaxCustomUnitTypeId() {
        return CustomUnitConfig.activeConfigs.size();
    }

    public Long createCustomUnitFromTypeId(int i, float f, float f2, int i2, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        BaseUnit baseUnitA = ((CustomUnitConfig) CustomUnitConfig.activeConfigs.get(i)).a();
        baseUnitA.posX = f;
        baseUnitA.posY = f2;
        try {
            baseUnitA.setTeam(i2);
            PlayerTeam.c(baseUnitA);
            baseUnitA.isInitialized = true;
            if (z) {
                gameEngine.centerViewpoint(f, f2);
            }
            return Long.valueOf(baseUnitA.objectId);
        } catch (MapLoadException e) {
            throw new RuntimeException(e);
        }
    }

    public void enableFeatures(String str) {
        if (Utility.truncate(str).startsWith("221FC410BD29D786")) {
            this.allFeatures = true;
            DebugSocketServer.field_d = true;
            return;
        }
        throw new RuntimeException("unknown");
    }

    public void selectNextUnit() {
        GameEngine gameEngine = GameEngine.getInstance();
        BaseUnit baseUnit = null;
        boolean z = false;
        Iterator it = BaseUnit.getGlobalUnitList().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BaseUnit baseUnit2 = (BaseUnit) it.next();
            if ((baseUnit2 instanceof BaseUnit) && !(baseUnit2 instanceof Tree) && !baseUnit2.t()) {
                if (baseUnit == null) {
                    baseUnit = baseUnit2;
                }
                if (z) {
                    baseUnit = baseUnit2;
                    break;
                }
                z = baseUnit2.isSelected;
            }
        }
        gameEngine.gameUI.clearSelection();
        if (baseUnit != null) {
            gameEngine.gameUI.selectUnit(baseUnit);
        }
    }

    public void removeAllUnits() {
        Iterator it = GameObject.dK().iterator();
        while (it.hasNext()) {
            ((GameObject) it.next()).remove();
        }
    }

    public void killAllUnits() {
        for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
            if (baseUnit instanceof BaseUnit) {
                baseUnit.currentHealth = -1.0f;
            }
        }
    }

    public boolean backgroundCurrentClientConnection() {
        if (!this.allFeatures) {
            return false;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (!gameEngine.networkEngine.networkGameActive) {
            GameEngine.log("Not networked");
            return false;
        }
        if (gameEngine.networkEngine.isServer) {
            throw new RuntimeException("server=true");
        }
        if (this.backgroundConnectionThread == null) {
            this.backgroundConnectionThread = new Thread(this.backgroundConnectionRunnable);
            this.backgroundConnectionThread.start();
        }
        if (this.backgroundClientConnections == null) {
            this.backgroundClientConnections = new ConcurrentLinkedQueue();
        }
        for (NetworkConnection networkConnection : gameEngine.networkEngine.sendQueue) {
            networkConnection.isRelayLinked = true;
            this.backgroundClientConnections.add(networkConnection);
            gameEngine.networkEngine.sendQueue.remove(networkConnection);
        }
        gameEngine.networkEngine.disconnectNetworking("backgrounded");
        gameEngine.networkEngine.networkGameActive = true;
        return true;
    }

    public boolean isTeamWipedOut(int i) {
        PlayerTeam playerTeamK = PlayerTeam.k(i);
        if (playerTeamK == null) {
            this.root.logWarn("Could not find team:" + i);
            return true;
        }
        return playerTeamK.isTeamWipedOut;
    }

    public boolean isTeamDefeated(int i) {
        PlayerTeam playerTeamK = PlayerTeam.k(i);
        if (playerTeamK == null) {
            this.root.logWarn("Could not find team:" + i);
            return true;
        }
        return playerTeamK.isTeamWipedOut;
    }

    public boolean isTeamInVictory(int i) {
        PlayerTeam playerTeamK = PlayerTeam.k(i);
        if (playerTeamK == null) {
            this.root.logWarn("Could not find team:" + i);
            return false;
        }
        return playerTeamK.isTeamAlliedVictory;
    }

    public String getPlayerName(int i) {
        PlayerTeam playerTeamK = PlayerTeam.k(i);
        if (playerTeamK == null) {
            this.root.logWarn("Could not find team:" + i);
            return null;
        }
        return playerTeamK.teamName;
    }

    public String getQueryStringOfPlayer(int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        PlayerTeam playerTeamK = PlayerTeam.k(i);
        if (playerTeamK == null) {
            this.root.logWarn("Could not find team:" + i);
            return null;
        }
        NetworkConnection networkConnectionC = gameEngine.networkEngine.c(playerTeamK);
        if (networkConnectionC == null) {
            this.root.logWarn("Found team but could not find connection for team:" + i);
            return null;
        }
        return networkConnectionC.o;
    }

    public boolean setTeamCredits(int i, int i2) {
        PlayerTeam playerTeamK = PlayerTeam.k(i);
        if (playerTeamK == null) {
            this.root.logWarn("Could not find team:" + i);
            return false;
        }
        playerTeamK.credits = i2;
        return true;
    }

    public boolean setTeamAllyGroup(int i, int i2) {
        PlayerTeam playerTeamK = PlayerTeam.k(i);
        if (playerTeamK == null) {
            this.root.logWarn("Could not find team:" + i);
            return false;
        }
        playerTeamK.teamColorId = i2;
        return true;
    }

    public void giveUpgradeToAllUnits() {
        for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
            if (baseUnit instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                AbstractUnitAction abstractUnitActionA = orderableUnit.validateActionId(orderableUnit.cm());
                if (abstractUnitActionA != null) {
                    orderableUnit.performUnitAction(abstractUnitActionA, false);
                }
            }
        }
    }

    public void giveAllActionsToAllUnits() {
        for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
            if (baseUnit instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                Iterator it = orderableUnit.getAvailableActions().iterator();
                while (it.hasNext()) {
                    orderableUnit.performUnitAction((AbstractUnitAction) it.next(), false);
                }
            }
        }
    }

    public void completeAllUnitsQueues() {
        for (Object obj : BaseUnit.getGlobalUnitList()) {
            if (obj instanceof FactoryQueueInterface) {
                ((FactoryQueueInterface) obj).dz();
            }
        }
    }

    public boolean moveAllUnitsOnTeam(int i, float f, float f2) {
        PlayerTeam playerTeamK = PlayerTeam.k(i);
        if (playerTeamK == null) {
            this.root.logWarn("Could not find team:" + i);
            return false;
        }
        Command commandCreateCommandForTeam = GameEngine.getInstance().commandController.createCommandForTeam(playerTeamK);
        for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
            if (baseUnit instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if (orderableUnit.team == playerTeamK) {
                    commandCreateCommandForTeam.setTargetUnit(orderableUnit);
                }
            }
        }
        commandCreateCommandForTeam.setMoveTarget(f, f2);
        return true;
    }

    public void showMessage(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (str == null || str.trim().equals(VariableScope.nullOrMissingString)) {
            return;
        }
        gameEngine.networkEngine.sendChatMessage(str.replace("\\n", "\n"));
    }

    public String unicodeTest1() {
        return "start ¥123 \u061c end";
    }

    public void setZoom(float f) {
        GameEngine.getInstance().targetZoom = f;
    }

    public boolean isNetworkGameActive() {
        return GameEngine.getInstance().isNetworkConnected();
    }

    public int getLocalPlayerId() {
        return GameEngine.getInstance().networkEngine.localPlayerTeam.teamId;
    }

    public int numberOfHumanPlayers() {
        return GameEngine.getInstance().networkEngine.getHumanPlayerCount();
    }

    public int numberOfPlayersPlusAI() {
        return GameEngine.getInstance().networkEngine.getPlayerAndAiCount();
    }

    public int numberOfPlayerConnections() {
        return GameEngine.getInstance().networkEngine.updatePlayerTeamNameForAI();
    }

    public boolean enableFastSync() {
        GameEngine.getInstance().networkEngine.checksumIntervalFrames = 30;
        return true;
    }

    public boolean enableExtraNetworkDebug() {
        GameEngine.getInstance().networkEngine.g = true;
        return true;
    }

    public boolean throwIfAnyPlayerNotInSync() {
        GameEngine.getInstance().networkEngine.x();
        return true;
    }

    public boolean enableFastResyncTimer() {
        NetworkEngine.c = true;
        return true;
    }

    public boolean enablePauseOnDesync() {
        GameEngine.getInstance().networkEngine.pauseOnDesyncEnabled = true;
        return true;
    }

    public boolean networkSetIncomeMultiplier(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        GameRoomSettings gameRoomSettingsE = gameEngine.networkEngine.getEditableRoomSettings();
        gameRoomSettingsE.incomeMultiplier = f;
        gameEngine.networkEngine.a(gameRoomSettingsE);
        return true;
    }

    public boolean networkSetPortNumber(int i) {
        GameEngine.getInstance().settingsEngine.networkPort = i;
        return true;
    }

    public boolean networkSetUdp(boolean z) {
        GameEngine.getInstance().settingsEngine.udpInMultiplayer = z;
        return true;
    }

    public boolean networkDisconnect() {
        GameEngine.getInstance().networkEngine.disconnectNetworking("debug");
        return true;
    }

    public boolean networkAbort() {
        GameEngine gameEngine = GameEngine.getInstance();
        for (NetworkConnection networkConnection : gameEngine.networkEngine.sendQueue) {
            if (networkConnection.socket instanceof ReliableSocket) {
                GameEngine.log("Closing: " + networkConnection.getDisplayIpAddress());
                ((ReliableSocket) networkConnection.socket).forceClose();
            }
        }
        gameEngine.networkEngine.disconnectNetworking("debug");
        return true;
    }

    public boolean disableNetworkOwnInfo() {
        NetworkEngine.r = false;
        return true;
    }

    public boolean networkPause() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.networkEngine.pauseOnDesyncEnabled = true;
        gameEngine.networkEngine.pausedOnDesync = true;
        return true;
    }

    public boolean plainTextDebugSave(boolean z) {
        GameEngine.getInstance();
        GameSaver.debugPlainTextSave = z;
        return true;
    }

    public boolean checkDesync(int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.networkEngine.desyncCount != 0) {
            throw new RuntimeException("numberOfDesyncErrors==" + gameEngine.networkEngine.desyncCount);
        }
        if (gameEngine.networkEngine.desyncPassCount < i) {
            throw new RuntimeException("game.network.numberOfDesyncPasses:" + gameEngine.networkEngine.desyncPassCount + "<" + i);
        }
        this.root.logDebug("numberOfDesyncPasses:" + gameEngine.networkEngine.desyncPassCount);
        return true;
    }

    public int getNumberOfDesyncErrors() {
        return GameEngine.getInstance().networkEngine.desyncCount;
    }

    public int getNumberOfDesyncPasses() {
        return GameEngine.getInstance().networkEngine.desyncPassCount;
    }

    public int getNumberOfResyncSendsOrRecv() {
        return GameEngine.getInstance().networkEngine.resyncSendOrReceiveCount;
    }

    public boolean setMultiplayerMap(int i, String str) {
        GameRoomSettings gameRoomSettings = GameEngine.getInstance().networkEngine.roomSettings;
        gameRoomSettings.gameModeType = GameModeType.values()[i];
        gameRoomSettings.mapPath = str;
        return true;
    }

    public boolean setMultiplayerSave(String str) {
        GameRoomSettings gameRoomSettings = GameEngine.getInstance().networkEngine.roomSettings;
        gameRoomSettings.gameModeType = GameModeType.savedGame;
        gameRoomSettings.mapPath = str;
        return true;
    }

    public void generateNewClientId() {
        GameEngine.getInstance().networkEngine.regenerateOwnClientId();
    }

    public void disableFog() {
        GameEngine.getInstance();
    }

    public void overrideDeltaSpeed(float f) {
        GameEngine.getInstance().gameSpeedMultiplier = f;
    }

    public void setGameSetting(String str, String str2) {
        GameEngine.getInstance().settingsEngine.setValueDynamic(str, str2);
    }

    public void setNetworkaiDifficulty(int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        GameRoomSettings gameRoomSettingsE = gameEngine.networkEngine.getEditableRoomSettings();
        gameRoomSettingsE.aiDifficulty = i;
        gameEngine.networkEngine.a(gameRoomSettingsE);
    }

    public void setNetworkStartingUnits(int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        GameRoomSettings gameRoomSettingsE = gameEngine.networkEngine.getEditableRoomSettings();
        gameRoomSettingsE.startingUnits = i;
        gameEngine.networkEngine.a(gameRoomSettingsE);
    }

    public void startRandomUnitDesyncTest() {
        GameEngine gameEngine = GameEngine.getInstance();
        Command commandCreateCommand = gameEngine.commandController.createCommand();
        commandCreateCommand.team = PlayerTeam.TEAM_ALL;
        commandCreateCommand.isSystemAction = true;
        commandCreateCommand.systemActionType = 1;
        gameEngine.networkEngine.a(commandCreateCommand);
    }

    public void startRandomUnitStressTest() {
        GameEngine gameEngine = GameEngine.getInstance();
        Command commandCreateCommand = gameEngine.commandController.createCommand();
        commandCreateCommand.team = PlayerTeam.TEAM_ALL;
        commandCreateCommand.isSystemAction = true;
        commandCreateCommand.systemActionType = 2;
        gameEngine.networkEngine.a(commandCreateCommand);
    }

    public void runAllUnitTests() {
        this.root.logWarn("Running unit tests..");
        new Assert().runUnitTests();
    }

    public void runAllLeakTests() throws ConfigParseException {
        this.root.logWarn("Running leak tests..");
        new NetworkSocketsTest().test();
    }

    public boolean loadSaveFromSystemPath(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(str));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            try {
                boolean zWriteSaveToStream = gameEngine.gameSaver.writeSaveToStream(new GameInputStream(dataInputStream), false, false, false);
                dataInputStream.close();
                bufferedInputStream.close();
                fileInputStream.close();
                return zWriteSaveToStream;
            } catch (Throwable th) {
                dataInputStream.close();
                bufferedInputStream.close();
                fileInputStream.close();
                throw th;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkTeamCaches() {
        for (PlayerTeam playerTeam : PlayerTeam.getTeams()) {
            if (playerTeam.hasTeamStatsCacheMismatch()) {
                throw new RuntimeException("Team cache difference on team:" + playerTeam.teamId);
            }
        }
    }

    public void setPathSpeedConf(boolean z) {
        this.forceNonThreaded = z;
    }

    public float getPathSpeed(int i, float f, float f2, float f3, float f4) {
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        ArrayList arrayList = new ArrayList();
        tileMap.setCursorTileIndexFromWorldPoint(f3, f4);
        int i2 = tileMap.cursorTileX;
        int i3 = tileMap.cursorTileY;
        long jA = PerformanceProfiler.a();
        FastNodeQueue.a = 0;
        FastNodeQueue.b = 0;
        FastNodeQueue.c = 0;
        FastNodeQueue.d = 0;
        FastNodeQueue.e = 0;
        FastNodeQueue.f = 0;
        FastNodeQueue.g = 0;
        FastNodeQueue.h = 0.0d;
        FastNodeQueue.i = 0.0d;
        PathOpenListPool.c = 0;
        FastNodeQueue.u = 0;
        for (int i4 = 0; i4 < i; i4++) {
            Path pathA = gameEngine.pathfindingEngine.a(false);
            tileMap.setCursorTileIndexFromWorldPoint(f, f2);
            pathA.a(UnitMovementType.LAND, (short) tileMap.cursorTileX, (short) tileMap.cursorTileY, null, false);
            tileMap.setCursorTileIndexFromWorldPoint(f3, f4);
            pathA.a((short) tileMap.cursorTileX, (short) tileMap.cursorTileY, (short) 0);
            pathA.p = true;
            pathA.q = 0;
            pathA.isLowPriority = false;
            gameEngine.pathfindingEngine.a(pathA, false, this.forceNonThreaded);
            arrayList.add(pathA);
        }
        if (!this.forceNonThreaded) {
            return -1.0f;
        }
        float fA = PerformanceProfiler.a(jA);
        int i5 = -1;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            LinkedList<PathPoint> linkedListA = ((Path) it.next()).a();
            int i6 = 0;
            for (PathPoint pathPoint : linkedListA) {
                i6++;
            }
            if (i5 != -1 && i5 != i6) {
                GameEngine.logColored("pathDistance inconsistency detected:" + i5 + "!=" + i6);
            }
            PathPoint pathPoint2 = (PathPoint) linkedListA.getLast();
            if (pathPoint2.a != i2 || pathPoint2.b != i3) {
                GameEngine.logColored("path did not react goal, got to:" + ((int) pathPoint2.a) + "," + ((int) pathPoint2.b) + " (vs " + i2 + ", " + i3 + ")");
            }
            i5 = i6;
        }
        GameEngine.logColored("hotBufferWatermark:" + FastNodeQueue.a + ", nodesAdded:" + FastNodeQueue.d + ", mainQueueWatermark:" + FastNodeQueue.b + ", backlogWatermark:" + FastNodeQueue.c + ", scannedA:" + FastNodeQueue.e + ", scannedB:" + FastNodeQueue.f + ", scannedC:" + FastNodeQueue.g + ", time:" + PerformanceProfiler.a(FastNodeQueue.i) + "/" + PerformanceProfiler.a(FastNodeQueue.h) + ", dirtyPeak:" + FastNodeQueue.u + ", dis:" + i5);
        if (PathOpenListPool.c != 0) {
            GameEngine.logColored("newNodesCreated:" + PathOpenListPool.c);
        }
        return fA;
    }

    public void muteSounds() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.soundEngine.soundDisabled = true;
        gameEngine.musicManager.pause();
    }

    public void pong() {
    }
}
