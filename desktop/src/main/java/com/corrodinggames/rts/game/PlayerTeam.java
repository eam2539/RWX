package com.corrodinggames.rts.game;

import android.graphics.Color;
import android.graphics.Paint;
import com.corrodinggames.rts.game.ai.AIController;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.DummyNonUnitWithTeam;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.buildings.CommandCenter;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnitAnimationTags;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.condition.ResourceConditionChecker;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.game.units.custom.condition.StoredResources;
import com.corrodinggames.rts.gameFramework.*;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.TeamColorTexture;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GraphicsUtils;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/n.class */
public abstract class PlayerTeam extends Serializable implements Comparable<PlayerTeam> {

    /* JADX INFO: renamed from: k */
    public int teamId;

    /* JADX INFO: renamed from: l */
    public String MODDER_CREDITS_WARNING = "Note to modifiers: Changing credits will not allow you to cheat in multiplayer games, but it will only break sync";

    /* JADX INFO: renamed from: m */
    public boolean isTeamActive;

    /* JADX INFO: renamed from: n */
    public boolean isTeamControlledByAI;

    /* JADX INFO: renamed from: o */
    public double credits;

    /* JADX INFO: renamed from: p */
    public double energy;

    /* JADX INFO: renamed from: q */
    public int teamUnitCount;

    /* JADX INFO: renamed from: r */
    public int teamColorId;

    /* JADX INFO: renamed from: s */
    public OrderableUnit teamCommandCenter;

    /* JADX INFO: renamed from: t */
    public OrderableUnit teamPrimaryUnit;

    /* JADX INFO: renamed from: u */
    public boolean isTeamDefeated;

    /* JADX INFO: renamed from: v */
    public String teamName;

    /* JADX INFO: renamed from: w */
    public boolean isTeamSpectator;

    /* JADX INFO: renamed from: x */
    public int teamPingTime;

    /* JADX INFO: renamed from: y */
    public boolean isTeamLocked;

    /* JADX INFO: renamed from: z */
    public Integer teamAIDifficultyOverride;

    /* JADX INFO: renamed from: A */
    public Integer teamAIBehaviourOverride;

    /* JADX INFO: renamed from: B */
    public Integer teamAILevelOverride;

    /* JADX INFO: renamed from: C */
    public Integer teamAIControlOverride;

    /* JADX INFO: renamed from: D */
    public int teamAIGroupOverride;

    /* JADX INFO: renamed from: at */
    private boolean teamColorBrightness;

    /* JADX INFO: renamed from: au */
    private int teamColorHex;

    /* JADX INFO: renamed from: E */
    public boolean isTeamVictory;

    /* JADX INFO: renamed from: av */
    private int teamColorPacked;

    /* JADX INFO: renamed from: F */
    public boolean isTeamDefeatedTech;

    /* JADX INFO: renamed from: G */
    public boolean isTeamWipedOut;

    /* JADX INFO: renamed from: H */
    public boolean isTeamAlliedVictory;

    /* JADX INFO: renamed from: I */
    public boolean isTeamConnectionActive;

    /* JADX INFO: renamed from: J */
    public boolean isTeamNetworkActive;

    /* JADX INFO: renamed from: K */
    public final Object synchronizationLock;

    /* JADX INFO: renamed from: L */
    public int fogOfWarWidth;

    /* JADX INFO: renamed from: M */
    public int fogOfWarHeight;

    /* JADX INFO: renamed from: N */
    public byte[][] fogOfWarData;

    /* JADX INFO: renamed from: O */
    public String teamSharedControlType;

    /* JADX INFO: renamed from: P */
    public String teamAIHint;

    /* JADX INFO: renamed from: Q */
    public int teamAILevel;

    /* JADX INFO: renamed from: R */
    public int teamAIBaseLevel;

    /* JADX INFO: renamed from: S */
    public boolean isTeamReady;

    /* JADX INFO: renamed from: T */
    public TeamUnitStats teamStatistics;

    /* JADX INFO: renamed from: U */
    public boolean isTeamObserver;

    /* JADX INFO: renamed from: V */
    public byte teamColorIndex;

    /* JADX INFO: renamed from: W */
    public int teamNetworkId;

    /* JADX INFO: renamed from: X */
    public long teamLastPingTime;

    /* JADX INFO: renamed from: Y */
    public long teamLastConnectionTime;

    /* JADX INFO: renamed from: Z */
    public int teamPingCount;

    /* JADX INFO: renamed from: aa */
    public boolean isTeamAutoStart;

    /* JADX INFO: renamed from: ab */
    public boolean isTeamAutoStartQueued;

    /* JADX INFO: renamed from: ac */
    public int teamSortIndex;

    /* JADX INFO: renamed from: ad */
    int lastUnitCountCacheTick;

    /* JADX INFO: renamed from: ae */
    public Paint teamColorPaint;

    /* JADX INFO: renamed from: af */
    public Paint teamTextPaint;

    /* JADX INFO: renamed from: ai */
    int lastCustomColorCacheTick;

    /* JADX INFO: renamed from: ak */
    AnimationSet teamAnimationSet;

    /* JADX INFO: renamed from: al */
    StoredResources teamColorEffect;

    /* JADX INFO: renamed from: am */
    public ResourceConditionChecker teamColorTexture;

    /* JADX INFO: renamed from: an */
    public float teamColorAlpha;

    /* JADX INFO: renamed from: aq */
    long teamColorBlue;

    /* JADX INFO: renamed from: ar */
    double teamColorHue;

    /* JADX INFO: renamed from: a */
    static FastArrayList teamStaticHelper = new FastArrayList();

    /* JADX INFO: renamed from: b */
    static PlayerTeam[] teamInstances = new PlayerTeam[0];

    /* JADX INFO: renamed from: c */
    public static int TEAM_NEUTRAL = 10;

    /* JADX INFO: renamed from: d */
    public static int TEAM_ALL_NEUTRAL = 0;

    /* JADX INFO: renamed from: e */
    public static int TEAM_ALLIES = 100;

    /* JADX INFO: renamed from: f */
    public static int TEAM_ENEMIES = TEAM_NEUTRAL + TEAM_ALL_NEUTRAL;

    /* JADX INFO: renamed from: g */
    public static final PlayerTeam TEAM_NULL = new GameTeam(-1, false, "<blank>");

    /* JADX INFO: renamed from: h */
    public static final PlayerTeam TEAM_UNKNOWN = new SystemTeam(-2);

    /* JADX INFO: renamed from: i */
    public static final PlayerTeam TEAM_ALL = new SystemTeam(-1);

    /* JADX INFO: renamed from: as */
    private static PlayerTeam[] teamColorArray = new PlayerTeam[TEAM_ENEMIES];

    /* JADX INFO: renamed from: j */
    public static PlayerTeam TEAM_SELF = new WaveTeam(-99);

    /* JADX INFO: renamed from: ag */
    static int[] teamColorIds = new int[10];

    /* JADX INFO: renamed from: ah */
    static String[] teamColorNames = new String[10];

    /* JADX INFO: renamed from: aj */
    static int teamColorCount = -99;

    /* JADX INFO: renamed from: ao */
    public static float teamColorRed = 40.0f;

    /* JADX INFO: renamed from: ap */
    public static float teamColorGreen = 10.0f;

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compareTo(PlayerTeam playerTeam) {
        int i = this.teamSortIndex - playerTeam.teamSortIndex;
        if (i != 0) {
            return i;
        }
        int i2 = this.teamId - playerTeam.teamId;
        if (i2 != 0) {
            return i2;
        }
        if (this.teamName != null && playerTeam.teamName != null) {
            return this.teamName.compareTo(playerTeam.teamName);
        }
        return 0;
    }

    /* JADX INFO: renamed from: b */
    public void writeToStream(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeByte(this.teamId);
        gameOutputStream.writeInt((int) this.credits);
        gameOutputStream.writeInt(this.teamColorId);
        gameOutputStream.writeStringNullable(this.teamName);
        gameOutputStream.writeBoolean(this.isTeamObserver);
        if (gameOutputStream.getMaxSize() > 26) {
            gameOutputStream.writeInt(getTeamId());
            gameOutputStream.debugPlaceholder("lastPingTimeReceivedAt");
            gameOutputStream.writeLong(this.teamLastPingTime);
        }
        if (gameOutputStream.getMaxSize() >= 55) {
            gameOutputStream.writeBoolean(this.isTeamSpectator);
            gameOutputStream.writeInt(this.teamPingTime);
        }
        if (gameOutputStream.getMaxSize() >= 91) {
            gameOutputStream.writeInt(this.teamSortIndex);
            gameOutputStream.writeByte(0);
        }
        if (gameOutputStream.getMaxSize() >= 97) {
            gameOutputStream.writeBoolean(this.isTeamConnectionActive);
            gameOutputStream.writeBoolean(this.isTeamNetworkActive);
        }
        if (gameOutputStream.getMaxSize() >= 125) {
            gameOutputStream.writeBoolean(this.isTeamVictory);
            gameOutputStream.writeBoolean(this.teamColorBrightness);
            gameOutputStream.writeInt(this.teamColorHex);
        }
        if (gameOutputStream.getMaxSize() >= 149) {
            gameOutputStream.writeStringNullable(this.teamAIHint);
            gameOutputStream.writeInt(this.teamAILevel);
        }
        if (gameOutputStream.getMaxSize() >= 156) {
            gameOutputStream.writeIntNullable(this.teamAIDifficultyOverride);
            gameOutputStream.writeIntNullable(this.teamAIBehaviourOverride);
            gameOutputStream.writeIntNullable(this.teamAILevelOverride);
            gameOutputStream.writeIntNullable(this.teamAIControlOverride);
            gameOutputStream.writeInt(this.teamAIGroupOverride);
        }
    }

    /* JADX INFO: renamed from: c */
    public void updateTeam(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeByte(0);
        gameOutputStream.writeInt(getTeamId());
        gameOutputStream.writeBoolean(this.isTeamConnectionActive);
        gameOutputStream.writeBoolean(this.isTeamNetworkActive);
    }

    /* JADX INFO: renamed from: a */
    public void readFromStream(GameInputStream gameInputStream) throws IOException {
        gameInputStream.readByte();
        this.teamNetworkId = gameInputStream.readInt();
        this.teamLastPingTime = System.currentTimeMillis();
        this.isTeamConnectionActive = gameInputStream.readBoolean();
        this.isTeamNetworkActive = gameInputStream.readBoolean();
    }

    /* JADX INFO: renamed from: b */
    public void getAlliedTeams(GameInputStream gameInputStream) throws IOException {
        compareToTeam(gameInputStream, false);
    }

    /* JADX INFO: renamed from: a */
    public void compareToTeam(GameInputStream gameInputStream, boolean z) throws IOException {
        if (!z) {
            setTeamId((int) gameInputStream.readByte());
            this.credits = gameInputStream.readInt();
            this.energy = 0.0d;
            this.teamUnitCount = 0;
            this.teamColorId = gameInputStream.readInt();
            this.teamName = gameInputStream.readNullableString();
            this.isTeamObserver = gameInputStream.readBoolean();
        } else {
            gameInputStream.readByte();
            gameInputStream.readInt();
            gameInputStream.readInt();
            gameInputStream.readNullableString();
            gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 14) {
            this.teamNetworkId = gameInputStream.readInt();
            gameInputStream.readLong();
            this.teamLastPingTime = System.currentTimeMillis();
        }
        if (gameInputStream.getProtocolVersion() >= 34 && gameInputStream.getMaxBlockSize() >= 55) {
            boolean z2 = gameInputStream.readBoolean();
            int i = gameInputStream.readInt();
            if (!z) {
                this.isTeamSpectator = z2;
                this.teamPingTime = i;
            }
        } else if (GameEngine.getInstance().networkEngine.B) {
            NetworkEngine.g("AI was skipping in networked game, steam version:" + gameInputStream.getMaxBlockSize());
        }
        if (gameInputStream.getProtocolVersion() >= 50 && gameInputStream.getMaxBlockSize() >= 91) {
            this.teamSortIndex = gameInputStream.readInt();
            gameInputStream.readByte();
        }
        if (gameInputStream.getProtocolVersion() >= 52 && gameInputStream.getMaxBlockSize() >= 97) {
            this.isTeamConnectionActive = gameInputStream.readBoolean();
            this.isTeamNetworkActive = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 70 && gameInputStream.getMaxBlockSize() >= 125) {
            boolean z3 = gameInputStream.readBoolean();
            boolean z4 = gameInputStream.readBoolean();
            int i2 = gameInputStream.readInt();
            if (!z) {
                this.isTeamVictory = z3;
                this.teamColorBrightness = z4;
                this.teamColorHex = i2;
            }
        }
        if (gameInputStream.getProtocolVersion() >= 90 && gameInputStream.getMaxBlockSize() >= 149) {
            String nullableString = gameInputStream.readNullableString();
            int i3 = gameInputStream.readInt();
            if (!z) {
                this.teamAIHint = nullableString;
                this.teamAILevel = i3;
            }
        }
        if (gameInputStream.getProtocolVersion() >= 93 && gameInputStream.getMaxBlockSize() >= 156) {
            Integer nullableInt = gameInputStream.readNullableInt();
            Integer nullableInt2 = gameInputStream.readNullableInt();
            Integer nullableInt3 = gameInputStream.readNullableInt();
            Integer nullableInt4 = gameInputStream.readNullableInt();
            int i4 = gameInputStream.readInt();
            if (!z) {
                if (this.teamAIDifficultyOverride != nullableInt) {
                    c("readIn aiDifficultyOverride was:" + this.teamAIDifficultyOverride + " now:  " + nullableInt);
                }
                this.teamAIDifficultyOverride = nullableInt;
                this.teamAIBehaviourOverride = nullableInt2;
                this.teamAILevelOverride = nullableInt3;
                this.teamAIControlOverride = nullableInt4;
                this.teamAIGroupOverride = i4;
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.debugPlaceholder("Writing team: " + this.teamName);
        writeToStream(gameOutputStream);
        if (gameOutputStream.getMaxSize() >= 44) {
            gameOutputStream.writeByte(4);
            gameOutputStream.writeBoolean(this.isTeamWipedOut);
            gameOutputStream.writeBoolean(this.isTeamDefeatedTech);
            gameOutputStream.writeBoolean(true);
            updateTeam2(gameOutputStream);
            this.teamColorEffect.a(gameOutputStream);
            AnimationTag.a(this.teamAnimationSet, gameOutputStream);
            gameOutputStream.writeBoolean(this.isTeamLocked);
        }
    }

    /* JADX INFO: renamed from: c */
    public void getActiveTeams(GameInputStream gameInputStream) throws IOException {
        getAlliedTeams(gameInputStream);
        if (gameInputStream.getProtocolVersion() >= 26) {
            byte b = gameInputStream.readByte();
            this.isTeamWipedOut = gameInputStream.readBoolean();
            if (b >= 1) {
                this.isTeamDefeatedTech = gameInputStream.readBoolean();
            }
            if (gameInputStream.readBoolean()) {
                updateTeamStatus(gameInputStream);
            }
            if (b >= 2) {
                this.teamColorEffect.a(gameInputStream);
            }
            if (b >= 3) {
                a(AnimationTag.a(gameInputStream));
            }
            if (b >= 4) {
                this.isTeamLocked = gameInputStream.readBoolean();
            }
        }
    }

    /* JADX INFO: renamed from: d */
    public void updateTeam2(GameOutputStream gameOutputStream) throws IOException {
        GameEngine.getInstance();
        gameOutputStream.debugPlaceholder("-- Saving fog --");
        gameOutputStream.writeBoolean(this.fogOfWarData != null);
        if (this.fogOfWarData != null) {
            gameOutputStream.writeInt(this.fogOfWarWidth);
            gameOutputStream.writeInt(this.fogOfWarHeight);
            for (int i = 0; i < this.fogOfWarWidth; i++) {
                for (int i2 = 0; i2 < this.fogOfWarHeight; i2++) {
                    gameOutputStream.writeByte(this.fogOfWarData[i][i2]);
                }
            }
        }
        gameOutputStream.debugPlaceholder("--End fog--");
    }

    /* JADX INFO: renamed from: d */
    public void updateTeamStatus(GameInputStream gameInputStream) throws IOException {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameInputStream.readBoolean()) {
            this.fogOfWarWidth = gameInputStream.readInt();
            this.fogOfWarHeight = gameInputStream.readInt();
            int i = this.fogOfWarWidth;
            int i2 = this.fogOfWarHeight;
            if (gameEngine.tileMap != null) {
                i = gameEngine.tileMap.tileCountX;
                i2 = gameEngine.tileMap.tileCountY;
                if (this.fogOfWarWidth != i || this.fogOfWarHeight != i2) {
                    GameEngine.updatePaintTextSizeIfNeeded("Map size does not match fog size: " + this.fogOfWarWidth + "!=" + i + "|" + this.fogOfWarHeight + "!=" + i2);
                }
            }
            this.fogOfWarData = new byte[i][i2];
            for (int i3 = 0; i3 < this.fogOfWarWidth; i3++) {
                for (int i4 = 0; i4 < this.fogOfWarHeight; i4++) {
                    this.fogOfWarData[i3][i4] = gameInputStream.readByte();
                }
            }
            return;
        }
        this.fogOfWarData = (byte[][]) null;
    }

    /* JADX INFO: renamed from: a */
    public void writeTeamDataToStream() {
        if (this.fogOfWarData != null) {
            for (int i = 0; i < this.fogOfWarWidth; i++) {
                for (int i2 = 0; i2 < this.fogOfWarHeight; i2++) {
                    this.fogOfWarData[i][i2] = 0;
                }
            }
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.playerTeam == this) {
            gameEngine.minimap.field_O = true;
            if (gameEngine.tileMap != null) {
                gameEngine.tileMap.resetFogToInvisible();
                gameEngine.tileMap.invalidateAllLayerCells();
            }
        }
    }

    /* JADX INFO: renamed from: b */
    public boolean addCredits() {
        return this.teamColorId == -3;
    }

    /* JADX INFO: renamed from: a */
    public static ArrayList<PlayerTeam> readTeamDataFromStream(boolean z) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < TEAM_ENEMIES; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null && (z || playerTeam.addCredits())) {
                arrayList.add(playerTeam);
            }
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    /* JADX INFO: renamed from: c */
    public static ArrayList<PlayerTeam> addEnergy() {
        return isEnemyToTeam(false);
    }

    /* JADX INFO: renamed from: b */
    public static ArrayList isEnemyToTeam(boolean z) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < TEAM_ENEMIES; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null && (z || !playerTeam.addCredits())) {
                arrayList.add(playerTeam);
            }
        }
        return arrayList;
    }

    /* JADX INFO: renamed from: d */
    public static PlayerTeam[] setTeamDefeated() {
        return teamInstances;
    }

    /* JADX INFO: renamed from: e */
    public static void getTeamStatistics() {
        FastArrayList fastArrayList = teamStaticHelper;
        fastArrayList.clear();
        fastArrayList.add(TEAM_ALL);
        fastArrayList.add(TEAM_UNKNOWN);
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null) {
                fastArrayList.add(playerTeam);
            }
        }
        if (teamInstances.length != fastArrayList.size) {
            teamInstances = new PlayerTeam[fastArrayList.size];
        }
        int i2 = fastArrayList.size;
        Object[] objArrA = fastArrayList.a();
        for (int i3 = 0; i3 < i2; i3++) {
            teamInstances[i3] = (PlayerTeam) objArrA[i3];
        }
    }

    /* JADX INFO: renamed from: f */
    public static ArrayList<Integer> getAllTeams() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null && !playerTeam.addCredits() && !arrayList.contains(Integer.valueOf(playerTeam.teamColorId))) {
                arrayList.add(Integer.valueOf(playerTeam.teamColorId));
            }
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    /* JADX INFO: renamed from: a */
    public static int updateNetworkTeamData(int i, boolean z) {
        int i2 = 0;
        for (int i3 = 0; i3 < TEAM_NEUTRAL; i3++) {
            PlayerTeam playerTeam = teamColorArray[i3];
            if (playerTeam != null && playerTeam.teamColorId == i && !playerTeam.addCredits() && (!z || !playerTeam.isTeamSpectator)) {
                i2++;
            }
        }
        return i2;
    }

    /* JADX INFO: renamed from: g */
    public static int getTeamById() {
        int i = 0;
        for (int i2 = 0; i2 < TEAM_NEUTRAL; i2++) {
            PlayerTeam playerTeam = teamColorArray[i2];
            if (playerTeam != null && !playerTeam.addCredits() && !playerTeam.isTeamDefeatedTech && !playerTeam.isTeamWipedOut) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: b */
    public static void getResourceCost(int i, boolean z) throws IOException {
        if (i < 10 || i == TEAM_NEUTRAL) {
            return;
        }
        if (i > TEAM_ALLIES) {
            throw new IOException("setMaxTeamId: " + i + " is over limit of:" + TEAM_ALLIES);
        }
        if (!z && i <= TEAM_NEUTRAL) {
            return;
        }
        int i2 = i + TEAM_ALL_NEUTRAL;
        PlayerTeam[] playerTeamArr = new PlayerTeam[i2];
        for (int i3 = 0; i3 < teamColorArray.length; i3++) {
            PlayerTeam playerTeam = teamColorArray[i3];
            if (i3 < playerTeamArr.length) {
                playerTeamArr[i3] = playerTeam;
            }
        }
        teamColorArray = playerTeamArr;
        TEAM_NEUTRAL = i;
        TEAM_ENEMIES = i2;
    }

    /* JADX INFO: renamed from: a */
    public static String updateFogOfWar(int i) {
        return i == 0 ? "A" : i == 1 ? "B" : i == 2 ? "C" : i == 3 ? "D" : i == 4 ? "E" : i == 5 ? "F" : i == 6 ? "G" : i == 7 ? "H" : i == 8 ? "I" : i == 9 ? "J" : i == 10 ? "K" : i == -3 ? "S" : VariableScope.nullOrMissingString + i;
    }

    /* JADX INFO: renamed from: h */
    public String getTeamColorName() {
        return updateFogOfWar(this.teamColorId);
    }

    /* JADX INFO: renamed from: i */
    public void getTeamColorIdByName() {
        this.isTeamVictory = false;
        this.teamColorBrightness = false;
        this.teamColorHex = -9999;
    }

    /* JADX INFO: renamed from: j */
    public boolean getTeamColorNameById() {
        return this.isTeamVictory;
    }

    /* JADX INFO: renamed from: k */
    public boolean isTeamAlly() {
        return this.teamColorHex >= 0;
    }

    /* JADX INFO: renamed from: l */
    public void updateTeamAllyStatus() {
        this.teamColorHex = GameEngine.getInstance().lastTick;
    }

    /* JADX INFO: renamed from: m */
    public boolean isTeamEnemy() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (((this.isTeamDefeatedTech || this.isTeamWipedOut) && !gameEngine.networkEngine.roomSettings.sharedControl) || this.isTeamSpectator || isTeamActiveCheck()) {
            return false;
        }
        if (this.isTeamAutoStartQueued && !isTeamAlly()) {
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: b */
    public static int removeCredits(int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < TEAM_NEUTRAL; i3++) {
            PlayerTeam playerTeam = teamColorArray[i3];
            if (playerTeam != null && playerTeam.teamColorId == i && playerTeam.isTeamAlly() && playerTeam.isTeamEnemy()) {
                i2++;
            }
        }
        return i2;
    }

    /* JADX INFO: renamed from: c */
    public static int removeUnitFromTeam(int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < TEAM_NEUTRAL; i3++) {
            PlayerTeam playerTeam = teamColorArray[i3];
            if (playerTeam != null && playerTeam.teamColorId == i && playerTeam.isTeamEnemy()) {
                i2++;
            }
        }
        return i2;
    }

    /* JADX INFO: renamed from: n */
    public static void staticUpdateTeamData() {
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null) {
                playerTeam.updateTeamPing();
            }
        }
        staticUpdateTeamColors();
    }

    /* JADX INFO: renamed from: o */
    public static void staticUpdateTeamData2() {
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null) {
                playerTeam.teamColorHex = -9999;
            }
        }
    }

    public static void d(int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!gameEngine.networkEngine.isServer || gameEngine.replayEngine.j()) {
            return;
        }
        for (int i2 = 0; i2 < TEAM_NEUTRAL; i2++) {
            PlayerTeam playerTeam = teamColorArray[i2];
            if (playerTeam != null && playerTeam.teamColorId == i && !playerTeam.teamColorBrightness) {
                playerTeam.teamColorBrightness = true;
                Command commandCreateCommand = gameEngine.commandController.createCommand();
                commandCreateCommand.team = playerTeam;
                commandCreateCommand.isSystemAction = true;
                commandCreateCommand.systemActionType = 100;
                gameEngine.networkEngine.a(commandCreateCommand);
            }
        }
    }

    /* JADX INFO: renamed from: e */
    public static void updateTeamVictoryStatus(int i) {
        int i2 = -9999;
        for (int i3 = 0; i3 < TEAM_NEUTRAL; i3++) {
            PlayerTeam playerTeam = teamColorArray[i3];
            if (playerTeam != null && playerTeam.teamColorId == i && playerTeam.isTeamAlly() && playerTeam.isTeamEnemy() && playerTeam.teamColorHex > i2) {
                i2 = playerTeam.teamColorHex;
            }
        }
        if (i2 >= 0 && GameViewUtils.a(i2, 120000)) {
            for (PlayerTeam playerTeam2 : teamColorArray) {
                if (playerTeam2 != null && playerTeam2.teamColorId == i) {
                    playerTeam2.teamColorHex = -9999;
                }
            }
        }
    }

    public boolean b(PlayerTeam playerTeam) {
        if (isTeamNeutral() && playerTeam != null && d(playerTeam)) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: p */
    public boolean isTeamNeutral() {
        if (this.isTeamConnectionActive || this.isTeamNetworkActive) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: q */
    public boolean isTeamSpectatorCheck() {
        if (GameEngine.getInstance().playerTeam == this) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: c */
    public void getResourceAmount(boolean z) {
        if (z) {
            this.teamAILevel = 1;
        } else {
            this.teamAILevel = 0;
        }
    }

    /* JADX INFO: renamed from: r */
    public boolean isTeamObserverCheck() {
        return this.teamAILevel == 1;
    }

    /* JADX INFO: renamed from: a */
    public final int addUnitToTeam(boolean z, boolean z2) {
        TeamUnitStats teamUnitStats = this.teamStatistics;
        int i = teamUnitStats.c;
        if (z) {
            i += teamUnitStats.f;
        }
        if (z2) {
            i += teamUnitStats.e;
        }
        return i;
    }

    /* JADX INFO: renamed from: s */
    public final int getTeamPing() {
        return this.teamStatistics.c + this.teamStatistics.f + this.teamStatistics.e;
    }

    /* JADX INFO: renamed from: a */
    public final int canAffordResource(AnimationTag animationTag, boolean z, boolean z2) {
        TeamUnitStats teamUnitStats = this.teamStatistics;
        if (teamUnitStats.d == 0) {
            return 0;
        }
        AnimationTagEntry animationTagEntryA = null;
        AnimationTagList animationTagList = teamUnitStats.p;
        AnimationTagEntry[] animationTagEntryArr = animationTagList.b;
        int i = 0;
        int i2 = animationTagList.c;
        while (true) {
            if (i >= i2) {
                break;
            }
            AnimationTagEntry animationTagEntry = animationTagEntryArr[i];
            if (animationTagEntry.a != animationTag) {
                i++;
            } else {
                animationTagEntryA = animationTagEntry;
                break;
            }
        }
        if (animationTagEntryA == null) {
            animationTagEntryA = teamUnitStats.a(animationTag);
            if (animationTagEntryA.e > 50) {
                animationTagList.a(animationTagEntryA);
            }
            animationTagEntryA.e = (short) (animationTagEntryA.e + 1);
        }
        int i3 = animationTagEntryA.b;
        if (z) {
            i3 += animationTagEntryA.c;
        }
        if (z2) {
            i3 += animationTagEntryA.d;
        }
        return i3;
    }

    /* JADX INFO: renamed from: t */
    public boolean isTeamDefeatedCheck() {
        boolean z = false;
        TeamUnitStats teamUnitStatsE = e(false);
        if (this.teamStatistics.b != teamUnitStatsE.b) {
            GameEngine.updatePaintTextSizeIfNeeded("unitCountExcludingBuildingsIncludingQueued: " + this.teamStatistics.b + "!=" + teamUnitStatsE.b + " (team:" + this.teamId + " fails: " + this.lastUnitCountCacheTick + ")");
            this.lastUnitCountCacheTick++;
            z = true;
        }
        if (this.teamStatistics.a != teamUnitStatsE.a) {
            GameEngine.updatePaintTextSizeIfNeeded("unitsMax: " + this.teamStatistics.a + "!=" + teamUnitStatsE.a + " (team:" + this.teamId + " fails: " + this.lastUnitCountCacheTick + ")");
            this.lastUnitCountCacheTick++;
            z = true;
        }
        if (this.teamStatistics.g != teamUnitStatsE.g) {
            GameEngine.updatePaintTextSizeIfNeeded("incomeRate: " + this.teamStatistics.g + "!=" + teamUnitStatsE.g + " (team:" + this.teamId + " fails: " + this.lastUnitCountCacheTick + ")");
            this.lastUnitCountCacheTick++;
            z = true;
        }
        if (this.teamStatistics.f != teamUnitStatsE.f) {
            GameEngine.updatePaintTextSizeIfNeeded("incompleteUnitCountOfAllTypes: " + this.teamStatistics.f + "!=" + teamUnitStatsE.f + " (team:" + this.teamId + " fails: " + this.lastUnitCountCacheTick + ")");
            this.lastUnitCountCacheTick++;
            z = true;
        }
        if (this.teamStatistics.e != teamUnitStatsE.e) {
            GameEngine.updatePaintTextSizeIfNeeded("queuedCountOfAllTypes: " + this.teamStatistics.e + "!=" + teamUnitStatsE.e + " (team:" + this.teamId + " fails: " + this.lastUnitCountCacheTick + ")");
            this.lastUnitCountCacheTick++;
            z = true;
        }
        if (this.teamStatistics.c != teamUnitStatsE.c) {
            GameEngine.updatePaintTextSizeIfNeeded("unitCountOfAllTypesOnlyCompleted: " + this.teamStatistics.c + "!=" + teamUnitStatsE.c + " (team:" + this.teamId + " fails: " + this.lastUnitCountCacheTick + ")");
            this.lastUnitCountCacheTick++;
            z = true;
        }
        if (!this.teamStatistics.h.e(teamUnitStatsE.h)) {
            GameEngine.updatePaintTextSizeIfNeeded("customIncomeRate: " + this.teamStatistics.h + "!=" + teamUnitStatsE.h + " (team:" + this.teamId + " fails: " + this.lastUnitCountCacheTick + ")");
            GameEngine.updatePaintTextSizeIfNeeded("currentCaches:" + this.teamStatistics.h.a(false, true, 30, true, true));
            GameEngine.updatePaintTextSizeIfNeeded("targetUnitCache:" + teamUnitStatsE.h.a(false, true, 30, true, true));
            this.lastUnitCountCacheTick++;
            z = true;
        }
        if (!this.teamStatistics.l.e(teamUnitStatsE.l)) {
            GameEngine.updatePaintTextSizeIfNeeded("streamingRateNegative (team:" + this.teamId + " fails: " + this.lastUnitCountCacheTick + ")");
            GameEngine.updatePaintTextSizeIfNeeded("currentCaches:" + this.teamStatistics.l.a(false, true, 30, true, true));
            GameEngine.updatePaintTextSizeIfNeeded("targetUnitCache:" + teamUnitStatsE.l.a(false, true, 30, true, true));
            this.lastUnitCountCacheTick++;
            z = true;
        }
        if (!this.teamStatistics.k.e(teamUnitStatsE.k)) {
            GameEngine.updatePaintTextSizeIfNeeded("streamingRatePositive (team:" + this.teamId + " fails: " + this.lastUnitCountCacheTick + ")");
            GameEngine.updatePaintTextSizeIfNeeded("currentCaches:" + this.teamStatistics.k.a(false, true, 30, true, true));
            GameEngine.updatePaintTextSizeIfNeeded("targetUnitCache:" + teamUnitStatsE.k.a(false, true, 30, true, true));
            this.lastUnitCountCacheTick++;
            z = true;
        }
        if (z) {
        }
        return z;
    }

    private TeamUnitStats e(boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        TeamUnitStats teamUnitStats = new TeamUnitStats();
        teamUnitStats.a = gameEngine.currentTimeMillis;
        BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
        int size = BaseUnit.bE.size();
        for (int i = 0; i < size; i++) {
            BaseUnit baseUnit = baseUnitArrA[i];
            if (baseUnit.team == this) {
                teamUnitStats.a(baseUnit);
                if (z) {
                    baseUnit.isHidden = true;
                }
            }
        }
        if (teamUnitStats.a > gameEngine.lastTimeMillis) {
            teamUnitStats.a = gameEngine.lastTimeMillis;
        }
        return teamUnitStats;
    }

    public void d(boolean z) {
        if (!z && !this.isTeamReady) {
            return;
        }
        this.teamStatistics = e(true);
        this.isTeamReady = false;
        if (this.teamAIBaseLevel < this.teamStatistics.b) {
            this.teamAIBaseLevel = this.teamStatistics.b;
        }
        if (!this.isTeamControlledByAI && this.teamStatistics.m) {
            this.isTeamControlledByAI = true;
        }
        if (!this.isTeamActive && getTeamPing() > 0) {
            this.isTeamActive = true;
        }
        T();
    }

    /* JADX INFO: renamed from: u */
    public int getCreditsScaledByEconomy() {
        return (int) (this.teamStatistics.g * getEconomyMultiplier());
    }

    /* JADX INFO: renamed from: v */
    public int getEnergyScaledByCredits() {
        return (int) ((getCreditsScaledByEconomy() * getSpectatorEnergyFactor()) + 0.5f);
    }

    /* JADX INFO: renamed from: a */
    public int isAllyToTeam(Resource resource) {
        return 0 - ((int) this.teamStatistics.l.a(resource));
    }

    public int b(Resource resource) {
        int iA;
        if (resource == Resource.D) {
            iA = this.teamStatistics.g;
        } else {
            iA = (int) this.teamStatistics.h.a(resource);
        }
        int iA2 = iA + ((int) this.teamStatistics.k.a(resource));
        GameEngine.getInstance();
        boolean z = false;
        if (resource == Resource.D) {
            z = true;
        }
        if (z) {
            iA2 = (int) (iA2 * getEconomyMultiplier());
        }
        return iA2;
    }

    /* JADX INFO: renamed from: w */
    public int getTeamUnitCountInt() {
        return this.teamStatistics.b;
    }

    /* JADX INFO: renamed from: x */
    public int getTeamBuildingCountInt() {
        return this.teamStatistics.a;
    }

    /* JADX INFO: renamed from: y */
    public String getTeamDisplayName() {
        int teamId = getTeamId();
        if (teamId == -99 || this.isTeamSpectator) {
            return VariableScope.nullOrMissingString;
        }
        if (teamId == -2 || teamId == -1) {
            return "(disconnected)";
        }
        return "(" + teamId + ")";
    }

    public String z() {
        int teamId = getTeamId();
        if (teamId == -99) {
            return "HOST";
        }
        if (this.isTeamSpectator) {
            return "-";
        }
        if (teamId == -1) {
            return "N/A";
        }
        if (teamId == -2) {
            return "-";
        }
        if (isTeamObserverCheck()) {
            return teamId + " (HOST)";
        }
        return VariableScope.nullOrMissingString + teamId;
    }

    /* JADX INFO: renamed from: A */
    public int getTeamId() {
        if (this.teamLastPingTime == -1) {
            return -2;
        }
        if (this.teamLastPingTime < System.currentTimeMillis() - 5000) {
            return -1;
        }
        return this.teamNetworkId;
    }

    /* JADX INFO: renamed from: B */
    public boolean isTeamActiveCheck() {
        if (this.teamLastPingTime != -99 && this.teamLastPingTime != -1 && this.teamLastPingTime < System.currentTimeMillis() - 15000) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: P */
    public static void updateTeamDefeatStatus() {
        if (GameEngine.getInstance().isNetworkGameActive()) {
            GameEngine.log("Skipping updateAllCachesFromChangedMetadata due to desync risk");
            return;
        }
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null) {
                playerTeam.isTeamReady = true;
            }
        }
    }

    /* JADX INFO: renamed from: C */
    public final int getTeamColorId() {
        if (this.isTeamLocked) {
            return this.teamPingTime;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if ((gameEngine.networkEngine.B || gameEngine.replayEngine.i()) && !gameEngine.networkEngine.F) {
            if (this.teamAIDifficultyOverride != null && this.teamAIDifficultyOverride.intValue() != this.teamPingTime) {
                c("aiDifficultyOverride:  " + this.teamAIDifficultyOverride + "!=" + this.teamPingTime);
            }
            return this.teamPingTime;
        }
        if (this.teamAIDifficultyOverride != null) {
            return this.teamAIDifficultyOverride.intValue();
        }
        return GameEngine.getInstance().settingsEngine.aiDifficulty;
    }

    /* JADX INFO: renamed from: D */
    public final float getEconomyMultiplier() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.isInGameOrLobby()) {
            return gameEngine.networkEngine.roomSettings.incomeMultiplier;
        }
        return 1.0f;
    }

    /* JADX INFO: renamed from: E */
    public final float getSpectatorEnergyFactor() {
        float f;
        if (!this.isTeamSpectator) {
            return 1.0f;
        }
        int teamColorId = getTeamColorId();
        if (teamColorId > 0) {
            f = 1.0f + (teamColorId * 0.4f);
        } else {
            f = 1.0f + (teamColorId * 0.3f);
        }
        if (teamColorId == 3) {
            f += 1.5f;
        }
        if (f < 0.1f) {
            f = 0.1f;
        }
        return f;
    }

    public final void b(float f) {
        if (!this.isTeamSpectator) {
            isNeutralToTeam(f);
        } else {
            isNeutralToTeam(getSpectatorEnergyFactor() * f);
        }
    }

    /* JADX INFO: renamed from: c */
    public final void isNeutralToTeam(float f) {
        d(f * getEconomyMultiplier());
    }

    public final void d(float f) {
        this.credits += (double) f;
        if (this.credits > 9.99999999E8d) {
            this.credits = 9.99999999E8d;
        }
    }

    /* JADX INFO: renamed from: F */
    public static void staticInitTeamData() {
        try {
            getResourceCost(10, true);
            for (int i = 0; i < teamColorArray.length; i++) {
                teamColorArray[i] = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: a */
    public static GameTeam getTeamById(String str) {
        if (str == null || str.equals(VariableScope.nullOrMissingString)) {
            GameEngine.updatePaintTextSizeIfNeeded("findExistingPlayer: No clientId id");
            return null;
        }
        for (int i = 0; i < teamColorArray.length; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null && str.equals(playerTeam.teamSharedControlType)) {
                if (playerTeam instanceof GameTeam) {
                    return (GameTeam) playerTeam;
                }
                GameEngine.updatePaintTextSizeIfNeeded("Player:" + i + " with matching clientId is not an instanceof player");
            }
        }
        return null;
    }

    public static GameTeam b(String str) {
        if (str == null || str.equals(VariableScope.nullOrMissingString)) {
            GameEngine.updatePaintTextSizeIfNeeded("No id");
            return null;
        }
        for (int i = 0; i < teamColorArray.length; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null && str.equals(playerTeam.teamAIHint)) {
                if (playerTeam instanceof GameTeam) {
                    return (GameTeam) playerTeam;
                }
                GameEngine.updatePaintTextSizeIfNeeded("Player:" + i + " with matching clientId is not an instanceof player");
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: G */
    public static int getActiveTeamCount() {
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            if (teamColorArray[i] == null) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: renamed from: H */
    public static int getTeamCount() {
        for (int i = TEAM_NEUTRAL; i < TEAM_ENEMIES; i++) {
            if (teamColorArray[i] == null) {
                return i;
            }
        }
        for (int i2 = TEAM_NEUTRAL - 1; i2 >= 0; i2--) {
            if (teamColorArray[i2] == null) {
                return i2;
            }
        }
        return -1;
    }

    /* JADX INFO: renamed from: I */
    public void updateTeamActiveStatus() {
        for (int i = 0; i < teamColorArray.length; i++) {
            if (teamColorArray[i] == this) {
                teamColorArray[i] = null;
            }
        }
    }

    public PlayerTeam() {
        this.teamId = -1;
        this.MODDER_CREDITS_WARNING = "Note to modifiers: Changing credits will not allow you to cheat in multiplayer games, but it will only break sync";
        this.credits = 4000.0d;
        this.energy = 0.0d;
        this.teamUnitCount = 0;
        this.teamCommandCenter = DummyNonUnitWithTeam.a(this);
        this.teamPrimaryUnit = DummyNonUnitWithTeam.a(this);
        this.isTeamDefeated = false;
        this.teamAIGroupOverride = -1;
        this.teamColorHex = -9999;
        this.teamColorPacked = -9999;
        this.synchronizationLock = new Object();
        this.isTeamReady = true;
        this.teamStatistics = new TeamUnitStats();
        this.teamNetworkId = -1;
        this.teamLastPingTime = -1L;
        this.teamLastConnectionTime = -1L;
        this.teamPingCount = -1;
        this.teamSortIndex = 0;
        this.teamColorPaint = new GamePaint();
        this.teamTextPaint = new GamePaint();
        this.lastCustomColorCacheTick = -2;
        this.teamAnimationSet = AnimationTag.d;
        this.teamColorEffect = new StoredResources();
        this.teamColorTexture = new ResourceConditionChecker();
        this.teamColorBlue = -9999L;
        this.isTeamSpectator = this instanceof AIController;
    }

    public PlayerTeam(int i) {
        this(i, true);
    }

    public PlayerTeam(int i, boolean z) {
        this();
        c(i, z);
    }

    /* JADX INFO: renamed from: f */
    public void setTeamId(int i) {
        c(i, true);
    }

    public void c(int i, boolean z) {
        if (this.teamId != i) {
            if (z) {
                updateTeamActiveStatus();
            }
            this.teamId = i;
            this.teamColorId = i;
            if (z && i != -3) {
                PlayerTeam playerTeam = teamColorArray[i];
                if (playerTeam != null) {
                    playerTeam.c("Being replaced");
                }
                teamColorArray[i] = this;
            }
            updateTeamConnectionStatus();
        }
    }

    /* JADX INFO: renamed from: J */
    public void updateTeamConnectionStatus() {
        int teamUnitCount = getTeamUnitCount();
        this.teamColorPaint.b(teamUnitCount);
        this.teamTextPaint.b(Color.a(Color.a(teamUnitCount), (int) (Color.b(teamUnitCount) * 0.5f), (int) (Color.c(teamUnitCount) * 0.5f), (int) (Color.d(teamUnitCount) * 0.5f)));
    }

    /* JADX INFO: renamed from: a */
    public boolean getTeamColorName(double d) {
        if (this.credits >= d || d == 0.0d) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: g */
    public boolean isTeamIndexActive(int i) {
        if (this.credits + this.energy >= i || i == 0) {
            return true;
        }
        return false;
    }

    public final boolean c(PlayerTeam playerTeam) {
        return (playerTeam == TEAM_ALL || this == TEAM_ALL || this.teamColorId == playerTeam.teamColorId) ? false : true;
    }

    public final boolean d(PlayerTeam playerTeam) {
        if (playerTeam == TEAM_ALL && this == TEAM_ALL) {
            return true;
        }
        return (playerTeam == TEAM_ALL || this == TEAM_ALL || this.teamColorId != playerTeam.teamColorId) ? false : true;
    }

    /* JADX INFO: renamed from: K */
    public int getTeamUnitCount() {
        return i(getTeamColorIndex());
    }

    /* JADX INFO: renamed from: L */
    public static void updateAllTeamData() {
        GameEngine gameEngine = GameEngine.getInstance();
        try {
            d(gameEngine.settingsEngine.teamColors);
        } catch (IllegalArgumentException e) {
            GameEngine.log("initColors: Failed to read setting: '" + gameEngine.settingsEngine.teamColors + "': " + e.getMessage(), (Throwable) e);
            d("#00ff00,#d02013,#0463f3,#ffff40,#00ffff,#d0f8f7,#000000,#ff00ea,#ff7f18,#9368c4");
        }
        try {
            e(gameEngine.settingsEngine.teamColorsNames);
        } catch (IllegalArgumentException e2) {
            GameEngine.log("initColors: Failed to read setting: '" + gameEngine.settingsEngine.teamColorsNames + "': " + e2.getMessage(), (Throwable) e2);
            e("GREEN,RED,BLUE,YELLOW,CYAN,WHITE,BLACK,PINK,ORANGE,PURPLE");
        }
    }

    private static void d(String str) {
        String[] strArrSplit = str.split(",");
        if (strArrSplit.length != 10) {
            throw new IllegalArgumentException("Expected 10 hex colors");
        }
        for (int i = 0; i < 10; i++) {
            teamColorIds[i] = Color.a(strArrSplit[i]);
        }
    }

    private static void e(String str) {
        String[] strArrSplit = str.split(",");
        if (strArrSplit.length != 10) {
            throw new IllegalArgumentException("Expected 10 team color names");
        }
        for (int i = 0; i < 10; i++) {
            teamColorNames[i] = strArrSplit[i];
        }
    }

    /* JADX INFO: renamed from: M */
    public int getTeamBuildingCount() {
        if (this.teamColorId == -3) {
            return i(-3);
        }
        return h(this.teamId);
    }

    public static int h(int i) {
        if (i >= TEAM_NEUTRAL) {
            return i(-3);
        }
        return i(i % 2);
    }

    public static int i(int i) {
        if (i >= 0 && i < 10) {
            return teamColorIds[i];
        }
        if (i == -3) {
            return Color.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_PROG_YELLOW, 90, 90, 90);
        }
        return -7829368;
    }

    /* JADX INFO: renamed from: N */
    public String getTeamName() {
        if (this.teamId == -1 || this.teamId == -2) {
            return "GRAY";
        }
        return j(getTeamColorIndex());
    }

    public static String j(int i) {
        if (i >= 0 && i < 10) {
            return teamColorNames[i];
        }
        return "GRAY";
    }

    /* JADX INFO: renamed from: a */
    public static Texture[] getUnitCountByType(Texture texture) {
        return compareToNew(texture, ColorMode.pureGreen, false);
    }

    /* JADX INFO: renamed from: a */
    public static Texture[] compareToNew(Texture texture, ColorMode colorMode, boolean z) {
        if (!z || texture.A()) {
            return b(texture, colorMode);
        }
        return a(texture, colorMode);
    }

    public static Texture[] a(Texture texture, ColorMode colorMode) {
        Texture[] textureArr = new Texture[10];
        if ((GameEngine.isPausedStatic2 && !GameEngine.isAndroidVersionStatic2) || colorMode == ColorMode.disabled) {
            for (int i = 0; i < textureArr.length; i++) {
                textureArr[i] = texture;
            }
            return textureArr;
        }
        Texture[] textureArrA = texture.a(colorMode);
        if (textureArrA != null) {
            return textureArrA;
        }
        PerformanceProfiler performanceProfiler = GameEngine.getInstance().performanceProfiler;
        performanceProfiler.a(ProfilerSection.init_unitcolour);
        for (int i2 = 0; i2 < textureArr.length; i2++) {
            int i3 = i(i2);
            if (i2 == 0) {
                textureArr[i2] = texture;
            } else {
                textureArr[i2] = new TeamColorTexture(texture, i3, colorMode, i2);
            }
        }
        performanceProfiler.b(ProfilerSection.init_unitcolour);
        texture.a(colorMode, textureArr);
        return textureArr;
    }

    public static Texture[] b(Texture texture, ColorMode colorMode) {
        Texture[] textureArr = new Texture[10];
        if ((GameEngine.isPausedStatic2 && !GameEngine.isAndroidVersionStatic2) || colorMode == ColorMode.disabled || texture.A()) {
            for (int i = 0; i < textureArr.length; i++) {
                textureArr[i] = texture;
            }
            return textureArr;
        }
        Texture[] textureArrA = texture.a(colorMode);
        if (textureArrA != null) {
            return textureArrA;
        }
        PerformanceProfiler performanceProfiler = GameEngine.getInstance().performanceProfiler;
        performanceProfiler.a(ProfilerSection.init_unitcolour);
        int[] iArr = new int[10];
        int[] iArr2 = new int[10];
        for (int i2 = 0; i2 < iArr.length; i2++) {
            iArr[i2] = i(i2);
            iArr2[i2] = i2;
        }
        for (int i3 = 0; i3 < textureArr.length; i3++) {
            if (i3 != 0) {
                textureArr[i3] = texture.clone();
                textureArr[i3].a("color(" + i3 + "):" + texture.a());
                textureArr[i3].j();
            }
        }
        texture.j();
        if (colorMode == ColorMode.hueAdd) {
            b(texture, textureArr, iArr);
        } else if (colorMode == ColorMode.hueShift) {
            a(texture, textureArr, iArr, iArr2);
        } else {
            a(texture, textureArr, iArr);
        }
        for (int i4 = 0; i4 < textureArr.length; i4++) {
            if (textureArr[i4] != null) {
                textureArr[i4].p();
                textureArr[i4].s();
            }
        }
        texture.r();
        textureArr[0] = texture;
        performanceProfiler.b(ProfilerSection.init_unitcolour);
        texture.a(colorMode, textureArr);
        return textureArr;
    }

    public static void a(Texture texture, Texture[] textureArr, int[] iArr) {
        int iB;
        int iM = texture.m();
        int iL = texture.l();
        int[] iArr2 = new int[iArr.length];
        int[] iArr3 = new int[iArr.length];
        int[] iArr4 = new int[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr2[i] = Color.b(iArr[i]);
            iArr3[i] = Color.c(iArr[i]);
            iArr4[i] = Color.d(iArr[i]);
        }
        for (int i2 = 0; i2 < iL; i2++) {
            for (int i3 = 0; i3 < iM; i3++) {
                int iA = texture.a(i3, i2);
                int iA2 = GraphicsUtils.a(iA);
                if (iA2 == 0) {
                    if (iA != 0) {
                        for (int i4 = 0; i4 < textureArr.length; i4++) {
                            if (textureArr[i4] != null) {
                                textureArr[i4].a(i3, i2, 0);
                            }
                        }
                    }
                } else {
                    int iC = GraphicsUtils.c(iA);
                    if (iC > 0 && (iB = GraphicsUtils.b(iA)) == GraphicsUtils.d(iA)) {
                        if (iB == 0) {
                            for (int i5 = 0; i5 < textureArr.length; i5++) {
                                if (textureArr[i5] != null) {
                                    textureArr[i5].a(i3, i2, Color.a(iA2, (iArr2[i5] * iC) >> 8, (iArr3[i5] * iC) >> 8, (iArr4[i5] * iC) >> 8));
                                }
                            }
                        } else if (iC != iB) {
                            float f = (iC * 0.003921569f) - (iB * 0.003921569f);
                            for (int i6 = 0; i6 < textureArr.length; i6++) {
                                if (textureArr[i6] != null) {
                                    textureArr[i6].a(i3, i2, Color.a(iA2, Utility.distance((int) (iB + (iArr2[i6] * f)), 0, 255), Utility.distance((int) (iB + (iArr3[i6] * f)), 0, 255), Utility.distance((int) (iB + (iArr4[i6] * f)), 0, 255)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void a(Texture texture, Texture[] textureArr, int[] iArr, int[] iArr2) {
        int iM = texture.m();
        int iL = texture.l();
        int[] iArr3 = new int[iArr.length];
        int[] iArr4 = new int[iArr.length];
        int[] iArr5 = new int[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr3[i] = Color.b(iArr[i]);
            iArr4[i] = Color.c(iArr[i]);
            iArr5[i] = Color.d(iArr[i]);
        }
        for (int i2 = 0; i2 < iL; i2++) {
            for (int i3 = 0; i3 < iM; i3++) {
                int iA = texture.a(i3, i2);
                int iA2 = Color.a(iA);
                if (iA2 == 0) {
                    if (Color.b(iA) > 0 || Color.c(iA) > 0 || Color.d(iA) > 0) {
                        for (int i4 = 0; i4 < textureArr.length; i4++) {
                            if (textureArr[i4] != null) {
                                textureArr[i4].a(i3, i2, Color.a(0, 0, 0, 0));
                            }
                        }
                    }
                } else {
                    int iC = Color.c(iA);
                    int iB = Color.b(iA);
                    int iD =Color.d(iA);
                    float fMin = Utility.min(Utility.min(iB, iC), iD);
                    float fMax = Utility.max(Utility.max(Utility.abs(iB - iC), Utility.abs(iC - iD)), Utility.abs(iD - iB));
                    if (fMax > 15.0f) {
                        for (int i5 = 0; i5 < textureArr.length; i5++) {
                            if (textureArr[i5] != null) {
                                float f = fMax / 255.0f;
                                textureArr[i5].a(i3, i2, Color.a(iA2, Utility.distance((int) (fMin + (iArr3[i5] * f)), 0, 255), Utility.distance((int) (fMin + (iArr4[i5] * f)), 0, 255), Utility.distance((int) (fMin + (iArr5[i5] * f)), 0, 255)));
                            }
                        }
                    }
                }
            }
        }
    }

    public static void b(Texture texture, Texture[] textureArr, int[] iArr) {
        int iM = texture.m();
        int iL = texture.l();
        int[] iArr2 = new int[iArr.length];
        int[] iArr3 = new int[iArr.length];
        int[] iArr4 = new int[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr2[i] = Color.b(iArr[i]);
            iArr3[i] = Color.c(iArr[i]);
            iArr4[i] = Color.d(iArr[i]);
        }
        for (int i2 = 0; i2 < iM; i2++) {
            for (int i3 = 0; i3 < iL; i3++) {
                int iA = texture.a(i2, i3);
                int iA2 = Color.a(iA);
                if (iA2 > 0) {
                    int iB = Color.b(iA);
                    int iC = Color.c(iA);
                    int iD = Color.d(iA);
                    for (int i4 = 0; i4 < textureArr.length; i4++) {
                        int i5 = (int) (iB + (iArr2[i4] * 0.15f));
                        int i6 = (int) (iC + (iArr3[i4] * 0.15f));
                        int i7 = (int) (iD + (iArr4[i4] * 0.15f));
                        int iDistance = Utility.distance(i5, 0, 255);
                        int iDistance2 = Utility.distance(i6, 0, 255);
                        int iDistance3 = Utility.distance(i7, 0, 255);
                        if (textureArr[i4] != null) {
                            textureArr[i4].a(i2, i3, Color.a(iA2, iDistance, iDistance2, iDistance3));
                        }
                    }
                }
            }
        }
    }

    public static PlayerTeam k(int i) {
        if (i == -1) {
            return TEAM_ALL;
        }
        if (i == -2) {
            return TEAM_UNKNOWN;
        }
        if (i >= TEAM_ENEMIES) {
            GameEngine.logWarningAndStack("team index too high: " + i);
            return null;
        }
        if (i < 0) {
            GameEngine.logWarningAndStack("team index too low: " + i);
            return null;
        }
        return teamColorArray[i];
    }

    public void e(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.lastCustomColorCacheTick > 0) {
            this.lastCustomColorCacheTick--;
            return;
        }
        if (this.lastCustomColorCacheTick == -2) {
            this.lastCustomColorCacheTick = this.teamId;
        } else {
            this.lastCustomColorCacheTick = 10;
        }
        if (!this.isTeamWipedOut && !gameEngine.replayEngine.j()) {
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = gameEngine.networkEngine.roomSettings.sharedControl;
            boolean z5 = false;
            BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
            int size = BaseUnit.bE.size();
            for (int i = 0; i < size; i++) {
                BaseUnit baseUnit = baseUnitArrA[i];
                if (baseUnit.team == this) {
                    if (!baseUnit.getUnitVersion()) {
                        z = true;
                        if (!this.isTeamDefeatedTech && (baseUnit.bJ() || baseUnit.canMove())) {
                            z2 = true;
                            break;
                        }
                    } else {
                        z5 = true;
                    }
                } else if (z4 && baseUnit.team != null && baseUnit.team.d(this) && !baseUnit.getUnitVersion()) {
                    z3 = true;
                }
            }
            if (!z && !z3) {
                boolean z6 = false;
                if (z5 && gameEngine.currentTick < 100 && gameEngine.isGameStarted) {
                    z6 = true;
                }
                this.isTeamWipedOut = true;
                writeTeamDataToStream();
                for (BaseUnit baseUnit2 : BaseUnit.bE) {
                    if (baseUnit2.team == this && !baseUnit2.u()) {
                        if (z6 && !baseUnit2.isDestroyed && baseUnit2.getUnitVersion()) {
                            UnitType unitTypeR = baseUnit2.r();
                            String str = baseUnit2.getUnitShortName() + " Warning: This unit got ignored in defeated check and now being removed";
                            if ((unitTypeR instanceof CustomUnitConfig) && ((CustomUnitConfig) unitTypeR).canNotBeDirectlyAttacked) {
                                str = str + " (Likely due to canNotBeDirectlyAttacked:true)";
                            }
                            NetworkEngine.a((String) null, str);
                        }
                        baseUnit2.getUnitAIConditionTime();
                    }
                }
                gameEngine.networkEngine.i(this);
            }
            if (!z2 && !this.isTeamDefeatedTech && !this.isTeamWipedOut) {
                this.isTeamDefeatedTech = true;
                gameEngine.networkEngine.h(this);
            }
        }
    }

    public void a(OrderableUnit orderableUnit) {
    }

    public static void b(OrderableUnit orderableUnit) {
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null) {
                playerTeam.a(orderableUnit);
            }
        }
    }

    public static void a(BaseUnit baseUnit) {
        if (baseUnit.team != null && baseUnit.isHidden && baseUnit.isHighlighted) {
            PlayerTeam playerTeam = baseUnit.team;
            baseUnit.isHidden = false;
            playerTeam.teamStatistics.b(baseUnit);
            baseUnit.getUnitAICombatStateTime();
        }
    }

    public static void b(BaseUnit baseUnit) {
        a(baseUnit);
    }

    public static void c(BaseUnit baseUnit) {
        if (baseUnit.team != null && !baseUnit.isHidden && baseUnit.isHighlighted && !baseUnit.isDestroyed) {
            baseUnit.isHidden = true;
            PlayerTeam playerTeam = baseUnit.team;
            playerTeam.teamStatistics.a(baseUnit);
            baseUnit.updateUnitCombatTimer();
            if (!playerTeam.isTeamControlledByAI && playerTeam.teamStatistics.m) {
                playerTeam.isTeamControlledByAI = true;
            }
            if (!playerTeam.isTeamActive) {
                playerTeam.isTeamActive = true;
            }
            playerTeam.T();
        }
    }

    /* JADX INFO: renamed from: O */
    public static void updateTeamVictoryStatus() {
        TEAM_ALL.isTeamReady = true;
        TEAM_UNKNOWN.isTeamReady = true;
        Iterator it = addEnergy().iterator();
        while (it.hasNext()) {
            ((PlayerTeam) it.next()).isTeamReady = true;
        }
    }

    /* JADX INFO: renamed from: a */
    public void updateTeam(float f) {
        this.teamColorAlpha += f;
        if (this.teamColorAlpha > 90.0f) {
            this.teamColorAlpha = 0.0f;
            this.teamColorTexture.a();
        }
        this.teamUnitCount++;
        if (this.teamUnitCount > 1000 && this.energy != 0.0d) {
            GameEngine.log("Warning: anti-lag credits is still: " + this.energy + " (force clearing)");
            this.energy = 0.0d;
        }
    }

    /* JADX INFO: renamed from: f */
    public static void update2(float f) {
        int iRemoveCredits;
        GameEngine gameEngine = GameEngine.getInstance();
        TEAM_ALL.updateTeam(f);
        TEAM_UNKNOWN.updateTeam(f);
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null) {
                playerTeam.updateTeam(f);
                playerTeam.e(f);
                if (!playerTeam.teamColorBrightness && (iRemoveCredits = removeCredits(playerTeam.teamColorId)) > 0) {
                    if (iRemoveCredits >= removeUnitFromTeam(playerTeam.teamColorId)) {
                        d(playerTeam.teamColorId);
                        staticUpdateTeamData2();
                    } else {
                        updateTeamVictoryStatus(playerTeam.teamColorId);
                    }
                }
                if (playerTeam.isTeamVictory) {
                    if (playerTeam.teamColorPacked < 0) {
                        playerTeam.teamColorPacked = gameEngine.lastTick;
                    }
                    if (!playerTeam.isTeamWipedOut) {
                        int i2 = 0;
                        for (BaseUnit baseUnit : BaseUnit.bE) {
                            if (baseUnit.team == playerTeam && !baseUnit.u()) {
                                boolean z = false;
                                int i3 = 1;
                                if (GameViewUtils.a(playerTeam.teamColorPacked, 10000)) {
                                    z = true;
                                    i3 = 50;
                                } else if (GameViewUtils.a(playerTeam.teamColorPacked, 6000)) {
                                    z = Utility.readStreamToString(baseUnit, 0, 100) > 90;
                                    i3 = 20;
                                } else if (GameViewUtils.a(playerTeam.teamColorPacked, 2000)) {
                                    z = Utility.readStreamToString(baseUnit, 0, 100) > 98;
                                    i3 = 2;
                                }
                                if (baseUnit instanceof CommandCenter) {
                                    z = true;
                                }
                                if (z) {
                                    baseUnit.currentHealth = -1.0f;
                                    i2++;
                                    if (i2 > i3) {
                                        break;
                                    }
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (gameEngine.loadLevelNetwork() && gameEngine.settingsEngine.aiDifficulty != teamColorCount) {
            gameEngine.networkEngine.aq();
            teamColorCount = gameEngine.settingsEngine.aiDifficulty;
        }
    }

    public static void g(float f) {
        getTeamStatistics();
        for (PlayerTeam playerTeam : setTeamDefeated()) {
            playerTeam.d(false);
        }
    }

    /* JADX INFO: renamed from: Q */
    public static void updateTeamWipeStatus() {
        TEAM_ALL.d(false);
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null && !playerTeam.addCredits() && !playerTeam.isTeamWipedOut && !playerTeam.isTeamDefeatedTech && !playerTeam.isTeamVictory) {
                GameEngine.getInstance().networkEngine.g(playerTeam);
            }
        }
    }

    public static void h(float f) {
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null && (playerTeam instanceof AIController)) {
                ((AIController) playerTeam).renderDebugOverlay(f);
            }
        }
    }

    /* JADX INFO: renamed from: R */
    public int getTeamColorIndex() {
        if (this.teamAIGroupOverride == -1) {
            return getTeamNetworkId();
        }
        return this.teamAIGroupOverride;
    }

    /* JADX INFO: renamed from: S */
    public int getTeamNetworkId() {
        PlayerTeam playerTeam;
        if (this.teamId == -1 || this.teamId == -2) {
            return 5;
        }
        int i = this.teamId;
        if (i >= 10) {
            i %= 10;
        }
        if (TEAM_NEUTRAL > 10 && (playerTeam = GameEngine.getInstance().networkEngine.localPlayerTeam) != null && playerTeam != this && playerTeam.getTeamColorIndex() == i) {
            if (i != 5) {
                i = 5;
            } else {
                i = 4;
            }
        }
        return i;
    }

    public void T() {
    }

    public void a(AnimationSet animationSet) {
        this.teamAnimationSet = animationSet;
    }

    /* JADX INFO: renamed from: U */
    public AnimationSet getTeamAnimationSet() {
        return this.teamAnimationSet;
    }

    public void b(AnimationSet animationSet) {
        AnimationSet teamAnimationSet = getTeamAnimationSet();
        if (teamAnimationSet == null || teamAnimationSet.b() == 0) {
            a(animationSet);
        } else {
            if (AnimationTag.b(teamAnimationSet, animationSet)) {
                return;
            }
            CustomUnitAnimationTags customUnitAnimationTags = new CustomUnitAnimationTags(teamAnimationSet);
            if (customUnitAnimationTags.a(animationSet)) {
                a(customUnitAnimationTags.a());
            }
        }
    }

    public void c(AnimationSet animationSet) {
        AnimationSet teamAnimationSet = getTeamAnimationSet();
        if (teamAnimationSet == null || teamAnimationSet.b() == 0 || !AnimationTag.a(animationSet, teamAnimationSet)) {
            return;
        }
        CustomUnitAnimationTags customUnitAnimationTags = new CustomUnitAnimationTags(teamAnimationSet);
        if (customUnitAnimationTags.b(animationSet)) {
            a(customUnitAnimationTags.a());
        }
    }

    /* JADX INFO: renamed from: V */
    public StoredResources getTeamColorEffect() {
        return this.teamColorEffect;
    }

    public double c(Resource resource) {
        return this.teamColorEffect.a(resource);
    }

    public boolean a(TeamRelation teamRelation, PlayerTeam playerTeam) {
        if (teamRelation == TeamRelation.own) {
            return playerTeam == this;
        }
        if (teamRelation == TeamRelation.any) {
            return true;
        }
        if (teamRelation == TeamRelation.ally) {
            return d(playerTeam);
        }
        if (teamRelation == TeamRelation.allyNotOwn) {
            return playerTeam != this && d(playerTeam);
        }
        if (teamRelation == TeamRelation.enemy) {
            return c(playerTeam);
        }
        if (teamRelation == TeamRelation.neutral) {
            return playerTeam == TEAM_ALL;
        }
        if (teamRelation == TeamRelation.notOwn) {
            return playerTeam != this;
        }
        throw new RuntimeException("Unsupported type: " + teamRelation);
    }

    public void d(BaseUnit baseUnit) {
    }

    /* JADX INFO: renamed from: W */
    public void updateTeamTextures() {
        GameEngine.log("debugUnitCountByType for team:" + this.teamId);
        FastArrayList<UnitTypeCount> fastArrayList = new FastArrayList();
        BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
        int size = BaseUnit.bE.size();
        for (int i = 0; i < size; i++) {
            BaseUnit baseUnit = baseUnitArrA[i];
            if (baseUnit.team == this && !baseUnit.isDestroyed) {
                UnitType unitType = baseUnit.unitType;
                boolean z = false;
                Iterator it = fastArrayList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    UnitTypeCount unitTypeCount = (UnitTypeCount) it.next();
                    if (unitTypeCount.a == unitType) {
                        unitTypeCount.b++;
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    UnitTypeCount unitTypeCount2 = new UnitTypeCount();
                    unitTypeCount2.a = unitType;
                    unitTypeCount2.b = 1;
                    fastArrayList.add(unitTypeCount2);
                }
            }
        }
        GameEngine.log("--- Units ---");
        int i2 = 0;
        for (UnitTypeCount unitTypeCount3 : fastArrayList) {
            if (!unitTypeCount3.a.k()) {
                GameEngine.log(unitTypeCount3.a.getUnitTypeDescriptionShort() + " - count:" + unitTypeCount3.b);
                i2 += unitTypeCount3.b;
            }
        }
        GameEngine.log("total:" + i2);
        GameEngine.log("--- Buildings/Ignored in count ---");
        int i3 = 0;
        for (UnitTypeCount unitTypeCount4 : fastArrayList) {
            if (unitTypeCount4.a.k()) {
                GameEngine.log(unitTypeCount4.a.getUnitTypeDescriptionShort() + " - count:" + unitTypeCount4.b);
                i3 += unitTypeCount4.b;
            }
        }
        GameEngine.log("total:" + i3);
    }

    public void c(String str) {
        GameEngine.log("Team(id: " + this.teamId + ", name:" + this.teamName + "):" + str);
    }

    public int b(AnimationTag animationTag, boolean z, boolean z2) {
        int iCanAffordResource = 0;
        if (this == TEAM_ALL) {
            return 0;
        }
        PlayerTeam[] playerTeamArr = teamColorArray;
        int i = TEAM_NEUTRAL;
        for (int i2 = 0; i2 < i; i2++) {
            PlayerTeam playerTeam = playerTeamArr[i2];
            if (playerTeam != null && this != playerTeam && this.teamColorId != playerTeam.teamColorId) {
                if (animationTag == null) {
                    iCanAffordResource += playerTeam.addUnitToTeam(z, z2);
                } else {
                    iCanAffordResource += playerTeam.canAffordResource(animationTag, z, z2);
                }
            }
        }
        return iCanAffordResource;
    }

    public int c(AnimationTag animationTag, boolean z, boolean z2) {
        int iCanAffordResource = 0;
        PlayerTeam[] playerTeamArr = teamColorArray;
        int i = TEAM_NEUTRAL;
        for (int i2 = 0; i2 < i; i2++) {
            PlayerTeam playerTeam = playerTeamArr[i2];
            if (playerTeam != null && this != playerTeam && d(playerTeam)) {
                if (animationTag == null) {
                    iCanAffordResource += playerTeam.addUnitToTeam(z, z2);
                } else {
                    iCanAffordResource += playerTeam.canAffordResource(animationTag, z, z2);
                }
            }
        }
        return iCanAffordResource;
    }

    /* JADX INFO: renamed from: X */
    public static void staticUpdateAllTeamColors() {
        GameEngine gameEngine = GameEngine.getInstance();
        TEAM_ALL.teamStatistics.a = gameEngine.currentTimeMillis;
        TEAM_UNKNOWN.teamStatistics.a = gameEngine.currentTimeMillis;
        for (int i = 0; i < TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeam = teamColorArray[i];
            if (playerTeam != null) {
                playerTeam.teamStatistics.a = gameEngine.currentTimeMillis;
            }
        }
    }

    /* JADX INFO: renamed from: Y */
    public static void staticUpdateTeamColors() {
        TEAM_ALL.updateTeamPing();
        TEAM_UNKNOWN.updateTeamPing();
    }

    /* JADX INFO: renamed from: Z */
    public void updateTeamPing() {
        this.isTeamActive = false;
        this.isTeamControlledByAI = false;
        this.credits = 4000.0d;
        this.energy = 0.0d;
        this.teamUnitCount = 0;
        this.lastCustomColorCacheTick = -2;
        this.teamColorBrightness = false;
        this.teamColorHex = -9999;
        this.isTeamVictory = false;
        this.teamColorPacked = -9999;
        this.isTeamDefeatedTech = false;
        this.isTeamWipedOut = false;
        this.isTeamAlliedVictory = false;
        this.isTeamConnectionActive = false;
        this.isTeamNetworkActive = false;
        this.teamColorTexture.a();
        this.teamColorAlpha = 0.0f;
        this.lastUnitCountCacheTick = 0;
        this.teamAIBaseLevel = 0;
        this.isTeamReady = true;
        this.teamStatistics = new TeamUnitStats();
        this.teamStatistics.a = GameEngine.getInstance().currentTimeMillis;
        this.teamAnimationSet = AnimationTag.d;
        this.teamColorEffect = new StoredResources();
    }

    /* JADX INFO: renamed from: aa */
    public double getDisplayedResourcesTotal() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (Utility.abs(this.teamColorBlue - jCurrentTimeMillis) > 166.66666f) {
            this.teamColorBlue = jCurrentTimeMillis;
            this.teamColorHue = this.credits + this.energy;
        }
        return this.teamColorHue;
    }

    /* JADX INFO: renamed from: ab */
    public StoredResources getTeamColorEffect2() {
        return getTeamColorEffect();
    }
}
