package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ah */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ah.class */
public class GameRoomSettings implements Cloneable {

    /* JADX INFO: renamed from: l */
    public boolean sharedControl;

    /* JADX INFO: renamed from: q */
    public int randomSeed;

    /* JADX INFO: renamed from: a */
    public GameModeType gameModeType = GameModeType.skirmishMap;

    /* JADX INFO: renamed from: b */
    public String mapPath = "[z;p10]Crossing Large (10p).tmx";

    /* JADX INFO: renamed from: c */
    public int startingCredits = 0;

    /* JADX INFO: renamed from: d */
    public int fodMode = 2;

    /* JADX INFO: renamed from: e */
    public boolean revealedMap = true;

    /* JADX INFO: renamed from: f */
    public int aiDifficulty = 1;

    /* JADX INFO: renamed from: g */
    public int startingUnits = 1;

    /* JADX INFO: renamed from: h */
    public float incomeMultiplier = 1.0f;

    /* JADX INFO: renamed from: i */
    public boolean noNukes = false;

    /* JADX INFO: renamed from: j */
    public boolean unknown = false;

    /* JADX INFO: renamed from: k */
    public boolean useDisplayedCostAsResourceCost = false;

    /* JADX INFO: renamed from: m */
    public boolean teamLock = false;

    /* JADX INFO: renamed from: n */
    public boolean fixedAllyTeams = false;

    /* JADX INFO: renamed from: o */
    public boolean allowSpectators = true;

    /* JADX INFO: renamed from: p */
    public boolean roomLock = false;

    /* JADX INFO: renamed from: a */
    public void resetToDefaults() {
        this.gameModeType = GameModeType.skirmishMap;
        this.mapPath = "[z;p10]Crossing Large (10p).tmx";
    }

    /* JADX INFO: renamed from: b */
    public String getSettingsSummary() {
        return ((((((((((VariableScope.nullOrMissingString + "startingCredits: " + this.startingCredits + "\n") + "fogMode: " + this.fodMode + "\n") + "revealedMap: " + this.revealedMap + "\n") + "aiDifficulty: " + this.aiDifficulty + "\n") + "startingUnits: " + this.startingUnits + "\n") + "incomeMultiplier: " + this.incomeMultiplier + "\n") + "noNukes: " + this.noNukes + "\n") + "sharedControl: " + this.sharedControl + "\n") + "allowSpectators: " + this.allowSpectators + "\n") + "lockedRoom: " + this.roomLock + "\n") + "randomSeed: " + this.randomSeed + "\n";
    }

    /* JADX INFO: renamed from: c, reason: merged with bridge method [inline-methods] */
    public GameRoomSettings clone() {
        try {
            return (GameRoomSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeToStream(GameOutputStream gos) throws IOException {
        gos.writeByte(4);
        gos.writeInt(this.fodMode);
        gos.writeInt(this.startingCredits);
        gos.writeBoolean(this.revealedMap);
        gos.writeInt(this.aiDifficulty);
        gos.writeInt(this.startingUnits);
        gos.writeFloat(this.incomeMultiplier);
        gos.writeBoolean(this.noNukes);
        gos.writeBoolean(this.unknown);
        gos.writeBoolean(this.sharedControl);
        gos.writeBoolean(this.teamLock);
        gos.writeBoolean(this.fixedAllyTeams);
        gos.writeBoolean(this.allowSpectators);
        gos.writeBoolean(this.roomLock);
        gos.writeInt(this.randomSeed);
    }

    /* JADX INFO: renamed from: a */
    public void readFromStream(GameInputStream gis) throws IOException {
        byte b = gis.readByte();
        this.fodMode = gis.readInt();
        this.startingCredits = gis.readInt();
        this.revealedMap = gis.readBoolean();
        this.aiDifficulty = gis.readInt();
        this.startingUnits = gis.readInt();
        this.incomeMultiplier = gis.readFloat();
        this.noNukes = gis.readBoolean();
        this.unknown = gis.readBoolean();
        this.sharedControl = gis.readBoolean();
        if (b >= 1) {
            this.teamLock = gis.readBoolean();
        }
        if (b >= 2) {
            this.fixedAllyTeams = gis.readBoolean();
        }
        if (b >= 3) {
            this.allowSpectators = gis.readBoolean();
            this.roomLock = gis.readBoolean();
        }
        if (b >= 4) {
            this.randomSeed = gis.readInt();
        }
    }
}
