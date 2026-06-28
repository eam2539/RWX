package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.gameFramework.FormationGroup;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.au */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/au.class */
public final class UnitCommand {

    /* JADX INFO: renamed from: a */
    UnitCommandType commandType;

    /* JADX INFO: renamed from: b */
    UnitType buildUnitType;

    /* JADX INFO: renamed from: c */
    ActionId actionId;

    /* JADX INFO: renamed from: d */
    int buildQueueSize;

    /* JADX INFO: renamed from: h */
    BaseUnit targetUnit;

    /* JADX INFO: renamed from: i */
    public FormationGroup transportTarget;

    /* JADX INFO: renamed from: j */
    public boolean isQueued;

    /* JADX INFO: renamed from: m */
    public boolean isRepeating;

    /* JADX INFO: renamed from: n */
    public boolean isForceMove;

    /* JADX INFO: renamed from: e */
    float targetX = 1.0f;

    /* JADX INFO: renamed from: f */
    float targetY = 1.0f;

    /* JADX INFO: renamed from: g */
    long targetUnitId = -1;

    /* JADX INFO: renamed from: k */
    public float attackMoveRange = -1.0f;

    /* JADX INFO: renamed from: l */
    public float maxWayPointSurvivingTime = -1.0f;

    /* JADX INFO: renamed from: a */
    public boolean isApproximatelySameTarget(UnitCommand unitCommand) {
        if (Utility.abs(this.targetX - unitCommand.targetX) > 3.0f || Utility.abs(this.targetY - unitCommand.targetY) > 3.0f) {
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: b */
    public boolean isSameCommand(UnitCommand unitCommand) {
        if (unitCommand == null || this.commandType != unitCommand.commandType || this.buildUnitType != unitCommand.buildUnitType || Utility.abs(this.targetX - unitCommand.targetX) > 1.0f || Utility.abs(this.targetY - unitCommand.targetY) > 1.0f || this.buildQueueSize != unitCommand.buildQueueSize || this.targetUnit != unitCommand.targetUnit) {
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: a */
    public UnitType getBuildUnitType() {
        return this.buildUnitType;
    }

    /* JADX INFO: renamed from: b */
    public int getBuildQueueSize() {
        return this.buildQueueSize;
    }

    /* JADX INFO: renamed from: a */
    public void serialize(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeEnumOrdinal(this.commandType);
        gameOutputStream.writeUnitTypeId(this.buildUnitType);
        gameOutputStream.writeFloat(this.targetX);
        gameOutputStream.writeFloat(this.targetY);
        if (this.targetUnitId != -1) {
            gameOutputStream.writeLong(this.targetUnitId);
        } else {
            gameOutputStream.writeUnitIdOrNullBaseUnit(this.targetUnit);
        }
        gameOutputStream.writeByte(this.buildQueueSize);
        gameOutputStream.writeFloat(this.attackMoveRange);
        gameOutputStream.writeFloat(this.maxWayPointSurvivingTime);
        gameOutputStream.writeBoolean(this.isRepeating);
        gameOutputStream.writeBoolean(this.isQueued);
        gameOutputStream.writeBoolean(this.isForceMove);
        ActionId.serialize(gameOutputStream, this.actionId);
    }

    /* JADX INFO: renamed from: a */
    public void deserialize(GameInputStream gameInputStream) throws IOException {
        this.commandType = (UnitCommandType) gameInputStream.readEnumOrdinalOrNull(UnitCommandType.class);
        this.buildUnitType = gameInputStream.q();
        this.targetX = gameInputStream.readFloat();
        this.targetY = gameInputStream.readFloat();
        this.targetUnitId = gameInputStream.readLongOptional();
        this.targetUnit = null;
        if (gameInputStream.getProtocolVersion() >= 40) {
            this.buildQueueSize = gameInputStream.readByte();
        }
        if (gameInputStream.getProtocolVersion() >= 46) {
            this.attackMoveRange = gameInputStream.readFloat();
            this.maxWayPointSurvivingTime = gameInputStream.readFloat();
        }
        if (gameInputStream.getProtocolVersion() >= 58) {
            this.isRepeating = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 65) {
            this.isQueued = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 79) {
            this.isForceMove = gameInputStream.readBoolean();
        }
        if (gameInputStream.getProtocolVersion() >= 82) {
            this.actionId = ActionId.deserialize(gameInputStream);
        }
    }

    /* JADX INFO: renamed from: c */
    public void resolveTargetUnitFromId() {
        if (this.targetUnitId != -1) {
            this.targetUnit = GameObject.a(this.targetUnitId, true);
            if (this.targetUnit == null) {
                GameEngine.logColored("convertUnitIds failed");
                if (this.commandType != null) {
                    GameEngine.logColored("convertUnitIds: type:" + this.commandType.toString());
                }
                if (this.buildUnitType != null) {
                    GameEngine.logColored("convertUnitIds: build:" + this.buildUnitType.toString());
                }
                GameEngine.logColored("convertUnitIds: x:" + this.targetX + ", y:" + this.targetY);
            }
            this.targetUnitId = -1L;
        }
    }

    /* JADX INFO: renamed from: d */
    public UnitCommandType getCommandType() {
        return this.commandType;
    }

    /* JADX INFO: renamed from: e */
    public void resetCommand() {
        this.commandType = UnitCommandType.move;
        this.buildUnitType = null;
        this.buildQueueSize = 1;
        this.targetX = 2.0f;
        this.targetY = 2.0f;
        this.targetUnitId = -1L;
        this.targetUnit = null;
        this.transportTarget = null;
        this.attackMoveRange = -1.0f;
        this.maxWayPointSurvivingTime = -1.0f;
        this.isRepeating = false;
        this.isQueued = false;
        this.isForceMove = false;
        this.actionId = null;
    }

    /* JADX INFO: renamed from: f */
    public boolean isUnitTargetCommand() {
        return this.commandType == UnitCommandType.attack || this.commandType == UnitCommandType.repair || this.commandType == UnitCommandType.reclaim || this.commandType == UnitCommandType.loadInto || this.commandType == UnitCommandType.loadUp || this.commandType == UnitCommandType.guard || this.commandType == UnitCommandType.touchTarget || this.commandType == UnitCommandType.follow;
    }

    /* JADX INFO: renamed from: g */
    public float getTargetX() {
        if (isUnitTargetCommand() && this.targetUnit != null) {
            return this.targetUnit.posX;
        }
        return this.targetX;
    }

    /* JADX INFO: renamed from: h */
    public float getTargetY() {
        if (isUnitTargetCommand() && this.targetUnit != null) {
            return this.targetUnit.posY;
        }
        return this.targetY;
    }

    /* JADX INFO: renamed from: i */
    public BaseUnit getTargetUnit() {
        return this.targetUnit;
    }

    /* JADX INFO: renamed from: a */
    public void setMoveTarget(float f, float f2) {
        resetCommand();
        this.commandType = UnitCommandType.move;
        this.targetX = f;
        this.targetY = f2;
    }

    /* JADX INFO: renamed from: b */
    public void setAttackMoveTarget(float f, float f2) {
        resetCommand();
        this.commandType = UnitCommandType.attackMove;
        this.targetX = f;
        this.targetY = f2;
    }

    /* JADX INFO: renamed from: a */
    public void setAttackTarget(BaseUnit baseUnit) {
        resetCommand();
        this.commandType = UnitCommandType.attack;
        this.targetUnit = baseUnit;
    }

    /* JADX INFO: renamed from: a */
    public void setBuildCommand(float f, float f2, UnitType unitType, int i) {
        resetCommand();
        this.commandType = UnitCommandType.build;
        this.targetX = f;
        this.targetY = f2;
        this.buildUnitType = unitType;
        this.buildQueueSize = (byte) i;
    }

    /* JADX INFO: renamed from: b */
    public void setRepairCommand(BaseUnit baseUnit) {
        resetCommand();
        this.commandType = UnitCommandType.repair;
        this.targetUnit = baseUnit;
    }

    /* JADX INFO: renamed from: c */
    public void setGuardCommand(BaseUnit baseUnit) {
        resetCommand();
        this.commandType = UnitCommandType.guard;
        this.targetUnit = baseUnit;
    }

    /* JADX INFO: renamed from: d */
    public void setTouchTargetUnit(BaseUnit baseUnit) {
        resetCommand();
        this.commandType = UnitCommandType.touchTarget;
        this.targetUnit = baseUnit;
    }

    /* JADX INFO: renamed from: e */
    public void setFollowTargetUnit(BaseUnit baseUnit) {
        resetCommand();
        this.commandType = UnitCommandType.follow;
        this.targetUnit = baseUnit;
    }

    /* JADX INFO: renamed from: c */
    public void setPatrolPoint(float f, float f2) {
        resetCommand();
        this.commandType = UnitCommandType.patrol;
        this.targetX = f;
        this.targetY = f2;
    }

    /* JADX INFO: renamed from: f */
    public void setReclaimTargetUnit(BaseUnit baseUnit) {
        resetCommand();
        this.commandType = UnitCommandType.reclaim;
        this.targetUnit = baseUnit;
    }

    /* JADX INFO: renamed from: g */
    public void setLoadIntoTargetUnit(BaseUnit baseUnit) {
        resetCommand();
        this.commandType = UnitCommandType.loadInto;
        this.targetUnit = baseUnit;
    }

    /* JADX INFO: renamed from: h */
    public void setLoadUpTargetUnit(BaseUnit baseUnit) {
        resetCommand();
        this.commandType = UnitCommandType.loadUp;
        this.targetUnit = baseUnit;
    }

    /* JADX INFO: renamed from: c */
    public void copyFrom(UnitCommand unitCommand) {
        resetCommand();
        this.commandType = unitCommand.commandType;
        this.buildUnitType = unitCommand.buildUnitType;
        this.targetX = unitCommand.targetX;
        this.targetY = unitCommand.targetY;
        this.targetUnit = unitCommand.targetUnit;
        this.transportTarget = unitCommand.transportTarget;
        this.buildQueueSize = unitCommand.buildQueueSize;
        this.isQueued = unitCommand.isQueued;
        this.actionId = unitCommand.actionId;
    }

    /* JADX INFO: renamed from: j */
    public long getCommandTypeOrdinal() {
        long jOrdinal = 0;
        if (this.commandType != null) {
            jOrdinal = 0 + ((long) this.commandType.ordinal());
        }
        return jOrdinal;
    }

    /* JADX INFO: renamed from: k */
    public void updateTargetUnitIdFromUnit() {
        if (this.targetUnit != null) {
            this.targetUnitId = this.targetUnit.objectId;
            this.targetUnit = null;
        }
        this.transportTarget = null;
    }

    /* JADX INFO: renamed from: l */
    public BaseUnit getResolvedTargetEntity() {
        if (isUnitTargetCommand()) {
            return getTargetUnit();
        }
        OrderableUnit orderableUnit = PlayerTeam.TEAM_ALL.teamPrimaryUnit;
        orderableUnit.rotationSpeed = 0.0f;
        orderableUnit.posX = this.targetX;
        orderableUnit.posY = this.targetY;
        orderableUnit.posZ = 0.0f;
        return orderableUnit;
    }
}
