package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.*;
import com.corrodinggames.rts.game.units.custom.*;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.game.units.custom.hooks.AttachmentSlotDefinition;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.game.units.custom.tracking.AnimationTrackingEntry;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.*;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions.class */
public class LogicBooleanGameFunctions {
    static void addBooleanType(LogicBoolean logicBoolean, String... strArr) {
        LogicBoolean.addBooleanType(logicBoolean, strArr);
    }

    static void loadTypes() {
        addBooleanType(new HeightBoolean().with("underwater=true"), "self.underwater", "self.isUnderwater");
        addBooleanType(new HeightBoolean().with("ground=true"), "self.gound", "self.ground", "self.isAtGroundHeight");
        addBooleanType(new HeightValueBoolean(), "self.height", "self.z");
        addBooleanType(new HeightBoolean().with("flying=true"), "self.flying", "self.isFlying");
        addBooleanType(new MovingBoolean(), "self.isMoving");
        addBooleanType(new HasActiveWaypoint(), "self.hasActiveWaypoint");
        addBooleanType(new NumberOfQueuedWaypoints(), "self.numberOfQueuedWaypoints");
        addBooleanType(new SpeedValueBoolean(), "self.speed");
        addBooleanType(new SpeedBoolean().with("atTopSpeed=true"), "self.maxspeed", "self.isAtTopSpeed");
        addBooleanType(new InMapBoolean(), "self.isInMap");
        addBooleanType(new TouchWaterBoolean(), "self.inwater", "self.isInWater");
        addBooleanType(new OverWaterBoolean(), "self.overwater", "self.isOverwater");
        addBooleanType(new OverLiquidBoolean(), "self.isOverLiquid");
        addBooleanType(new OverCliftBoolean(), "self.isOverClift", "self.isOverCliff");
        addBooleanType(new OverPassableTileBoolean(), "self.isOverPassableTile");
        addBooleanType(new OverPassableTileBoolean().with("type='LAND'").createLocked(), "self.isOverOpenLand");
        addBooleanType(new TagsBoolean(), "self.tags", "self.hasTags");
        addBooleanType(new TeamTagBoolean(), "self.globalTeamTags", "self.hasGlobalTeamTags");
        addBooleanType(new HasFlagDynamicBoolean(), "self.hasFlag");
        addBooleanType(new EnergyBoolean(), "self.energy");
        addBooleanType(new EnergyIncludingQueuedBoolean(), "self.energyIncludingQueued");
        addBooleanType(new EnergyBoolean().with("full=true"), "self.isEnergyFull");
        addBooleanType(new EnergyBoolean().with("empty=true"), "self.isEnergyEmpty");
        addBooleanType(new TransportingCountBoolean(), "self.transportingCount");
        addBooleanType(new TransportingUnitWithTagsBoolean(), "self.transportingUnitWithTags");
        addBooleanType(new isTransportUnloading(), "self.isTransportUnloading");
        addBooleanType(new PriceCreditsBoolean(), "self.priceCredits");
        addBooleanType(new HpBoolean(), "self.hp");
        addBooleanType(new MaxHpBoolean(), "self.maxHp");
        addBooleanType(new MaxShieldBoolean(), "self.maxShield");
        addBooleanType(new MaxEnergyBoolean(), "self.maxEnergy");
        addBooleanType(new UnitIdBoolean(), "self.id");
        addBooleanType(new TeamIdBoolean(), "self.teamId");
        addBooleanType(new TeamDefeatedTechBoolean(), "self.teamDefeatedTech");
        addBooleanType(new TeamWipedOutBoolean(), "self.teamWipedOut");
        addBooleanType(new TeamVictoryBoolean(), "self.teamVictory");
        addBooleanType(new isEnergyRechargingBoolean(), "self.isEnergyRecharging");
        addBooleanType(new PositionXBoolean(), "self.x");
        addBooleanType(new PositionYBoolean(), "self.y");
        addBooleanType(new RotationBoolean(), "self.dir");
        addBooleanType(new MaxMoveSpeedBoolean(), "self.maxMoveSpeed");
        addBooleanType(new BuiltAmountBoolean(), "self.builtAmount");
        addBooleanType(new CompletedBoolean(), "self.completed");
        addBooleanType(new ShieldBoolean(), "self.shield");
        addBooleanType(new AmmoBoolean(), "self.ammo");
        addBooleanType(new AmmoBoolean().with("empty=true"), "self.isAmmoEmpty");
        addBooleanType(new AmmoIncludingQueuedBoolean(), "self.ammoIncludingQueued");
        addBooleanType(new ResourceCountBoolean(), "self.resource");
        addBooleanType(new QueueSize(), "self.queueSize");
        addBooleanType(new NumberOfConnectionsBoolean(), "self.numberOfConnections");
        addBooleanType(new NumberOfAttachedUnitsBoolean(), "self.numberOfAttachedUnits");
        addBooleanType(new HasParent(), "self.hasParent");
        addBooleanType(new HasResourcesBoolean(), "self.hasResources");
        addBooleanType(new IsResourceLargerThan(), "self.isResourceLargerThan");
        addBooleanType(new KillsBoolean(), "self.kills");
        addBooleanType(new TimeAliveBoolean(), "self.timeAlive");
        addBooleanType(new LastConvertedBoolean(), "self.lastConverted");
        addBooleanType(new CustomTimerBoolean(), "self.customTimer");
        addBooleanType(new HasTakenDamage(), "self.hasTakenDamage");
        addBooleanType(new IsAttackingBoolean(), "self.isAttacking");
        addBooleanType(new IsReversingBoolean(), "self.isReversing");
        addBooleanType(new IsOnTeam().with("team=-1").createLocked(), "self.isOnNeutralTeam");
        addBooleanType(new IsControlledByAI(), "self.isControlledByAI");
        addBooleanType(new NumberOfUnitsInTeam(), "numberOfUnitsInTeam", "self.numberOfUnitsInTeam");
        addBooleanType(new NumberOfUnitsInTeam().with("greaterThan=0, lessThan=-1"), "hasUnitInTeam", "self.hasUnitInTeam");
        addBooleanType(new NumberOfUnitsInTeam().with("greaterThan=-1, lessThan=1"), "noUnitInTeam", "self.noUnitInTeam");
        addBooleanType(new NumberOfUnitsInTeam().with("neutralTeam=true"), "numberOfUnitsInNeutralTeam", "self.numberOfUnitsInNeutralTeam");
        addBooleanType(new NumberOfUnitsInTeam().with("aggressiveTeam=true"), "numberOfUnitsInAggressiveTeam", "self.numberOfUnitsInAggressiveTeam");
        addBooleanType(new NumberOfUnitsInTeam().with("allTeams=true"), "numberOfUnitsInAllTeams", "self.numberOfUnitsInAllTeams");
        addBooleanType(new NumberOfUnitsInEnemyOrAllyTeam().with("ally=false"), "numberOfUnitsInEnemyTeam", "self.numberOfUnitsInEnemyTeam");
        addBooleanType(new NumberOfUnitsInEnemyOrAllyTeam().with("ally=true"), "numberOfUnitsInAllyTeam", "self.numberOfUnitsInAllyTeam", "numberOfUnitsInAllyNotOwnTeam", "self.numberOfUnitsInAllyNotOwnTeam");
        addBooleanType(new GameModeBoolean().with("nukesEnabled=true").createLocked(), "game.nukesEnabled");
        addBooleanType(new GameMapWidthBoolean(), "game.mapWidth");
        addBooleanType(new GameMapHeightBoolean(), "game.mapHeight");
        addBooleanType(new ThisActionRepeatedCount(), "thisActionIndex", "index");
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$GameModeBoolean.class */
    public static class GameModeBoolean extends LogicBoolean.LogicBooleanCommonLocking {

        @LogicBoolean.Parameter
        public boolean nukesEnabled;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = true;
            GameEngine gameEngine = GameEngine.getInstance();
            if (this.nukesEnabled && gameEngine.isInGameOrLobby() && gameEngine.networkEngine.roomSettings.noNukes) {
                z = false;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "GameMode(" + (this.nukesEnabled ? "Nukes enabled" : "Nukes disabled") + ")";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$GameMapWidthBoolean.class */
    public static class GameMapWidthBoolean extends LogicNumberFunction {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "game.mapWidth";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return GameEngine.getInstance().tileMap.getWorldWidth();
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$GameMapHeightBoolean.class */
    public static class GameMapHeightBoolean extends LogicNumberFunction {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "game.mapHeight";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return GameEngine.getInstance().tileMap.getWorldHeight();
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$IsGameFrameBoolean.class */
    public class IsGameFrameBoolean extends LogicBoolean {

        @LogicBoolean.Parameter
        public int mod;

        @LogicBoolean.Parameter
        public int equalTo;

        @LogicBoolean.Parameter
        public boolean offset;

        @LogicBoolean.Parameter
        public void mod(int i) {
            this.mod = i;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z;
            GameEngine gameEngine = GameEngine.getInstance();
            if (this.mod >= 0) {
                if (this.offset) {
                    z = (((long) gameEngine.currentTick) + orderableUnit.objectId) % ((long) this.mod) == ((long) this.equalTo);
                } else {
                    z = gameEngine.currentTick % this.mod == this.equalTo;
                }
            } else if (this.offset) {
                z = ((long) gameEngine.currentTick) + orderableUnit.objectId == ((long) this.equalTo);
            } else {
                z = gameEngine.currentTick == this.equalTo;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "IsGameFrame(mod=" + this.mod + ")";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$HeightBoolean.class */
    public static class HeightBoolean extends LogicBoolean {

        @LogicBoolean.Parameter
        public boolean underwater;

        @LogicBoolean.Parameter
        public boolean ground;

        @LogicBoolean.Parameter
        public boolean flying;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            String str = VariableScope.nullOrMissingString;
            if (this.underwater) {
                str = str + "underwater";
            }
            if (this.ground) {
                str = str + "ground";
            }
            if (this.flying) {
                str = str + "flying";
            }
            if (str.equals(VariableScope.nullOrMissingString)) {
                str = str + "height";
            }
            return str;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (this.underwater && orderableUnit.posZ < -2.0f) {
                z = true;
            }
            if (this.ground && orderableUnit.posZ > -2.0f && orderableUnit.posZ < 5.0f) {
                z = true;
            }
            if (this.flying && orderableUnit.posZ > 5.0f) {
                z = true;
            }
            return z;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$SpeedValueBoolean.class */
    public static class SpeedValueBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "Speed";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            if (orderableUnit.isSlidingMovement()) {
                return Utility.distance(0.0f, 0.0f, orderableUnit.velocityX, orderableUnit.velocityY);
            }
            float f = orderableUnit.rotation;
            return f < 0.0f ? -f : f;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$SpeedBoolean.class */
    public static class SpeedBoolean extends LogicBoolean {

        @LogicBoolean.Parameter
        public boolean atTopSpeed;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            float moveSpeed = orderableUnit.getMoveSpeed() - 0.1f;
            if (orderableUnit.isSlidingMovement()) {
                float fDistanceSq = Utility.distanceSq(0.0f, 0.0f, orderableUnit.velocityX, orderableUnit.velocityY);
                if (fDistanceSq != 0.0f && fDistanceSq > moveSpeed * moveSpeed) {
                    z = true;
                }
            } else if (orderableUnit.rotation != 0.0f && orderableUnit.rotation > moveSpeed) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "Speed";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$MovingBoolean.class */
    public static class MovingBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.isMoving) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "Moving";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$HasActiveWaypoint.class */
    public static class HasActiveWaypoint extends LogicBoolean {
        UnitCommandType type;

        @LogicBoolean.Parameter
        public void type(String str) {
            try {
                this.type = (UnitCommandType) IniFile.parseEnum(str, (Enum) null, UnitCommandType.class);
            } catch (ConfigParseException e) {
                throw new ConfigException(e.getMessage(), e);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            UnitCommand currentWaypoint = orderableUnit.getCurrentWaypoint();
            if (currentWaypoint != null) {
                if (this.type == null) {
                    z = true;
                } else {
                    z = currentWaypoint.getCommandType() == this.type;
                }
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "HasActiveWaypoint(type=" + this.type + ")";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$NumberOfQueuedWaypoints.class */
    public static class NumberOfQueuedWaypoints extends LogicBoolean.AbstractNumberBoolean {
        UnitCommandType type;

        @LogicBoolean.Parameter
        public void type(String str) {
            try {
                this.type = (UnitCommandType) IniFile.parseEnum(str, (Enum) null, UnitCommandType.class);
            } catch (ConfigParseException e) {
                throw new ConfigException(e.getMessage(), e);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "NumberOfQueuedWaypoints";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            if (this.type == null) {
                return orderableUnit.getWaypointCount();
            }
            int i = 0;
            int waypointCount = orderableUnit.getWaypointCount();
            for (int i2 = 0; i2 < waypointCount; i2++) {
                UnitCommand waypointAt = orderableUnit.getWaypointAt(i2);
                if (waypointAt != null) {
                    if (waypointAt.getCommandType() == this.type) {
                        i++;
                    }
                }
            }
            return i;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$InMapBoolean.class */
    public static class InMapBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (GameViewUtils.a(orderableUnit.posX, orderableUnit.posY)) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "InMap";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TouchWaterBoolean.class */
    public static class TouchWaterBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.isTouchingWater()) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "TouchWater";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$OverWaterBoolean.class */
    public static class OverWaterBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.isOverWater()) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "OverWater";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$OverLiquidBoolean.class */
    public static class OverLiquidBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.isOverLiquid()) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "overLiquid";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$OverCliftBoolean.class */
    public static class OverCliftBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.isOverCliff()) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "OverClift";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$OverPassableTileBoolean.class */
    public static class OverPassableTileBoolean extends LogicBoolean.LogicBooleanCommonLocking {
        UnitMovementType movementType = UnitMovementType.LAND;

        @LogicBoolean.Parameter
        public void type(String str) {
            this.movementType = UnitMovementType.a(str, "isOverPassableTile()");
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            GameEngine.getInstance();
            if (!GameViewUtils.a(orderableUnit.posX, orderableUnit.posY, this.movementType)) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "OverLand";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$CompletedBoolean.class */
    public static class CompletedBoolean extends LogicBoolean.LogicBooleanCommonLocking {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.buildProgress >= 1.0f) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "isComplete";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TransportingCountBoolean.class */
    public static class TransportingCountBoolean extends LogicBoolean.AbstractNumberBoolean {
        public AnimationTag _withTag;
        public boolean filtered;

        @LogicBoolean.Parameter
        public int slot = -1;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "TransportingCount";
        }

        @LogicBoolean.Parameter
        public void withTag(String str) {
            this._withTag = AnimationTag.c(str);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this._withTag != null || this.slot != -1) {
                this.filtered = true;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:23:0x0070  */
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public float getValue(OrderableUnit orderableUnit) {
            int transportedUnitCount;
            if (!this.filtered) {
                transportedUnitCount = orderableUnit.getTransportedUnitCount();
            } else {
                transportedUnitCount = 0;
                FastArrayList transportedUnitList = orderableUnit.getTransportedUnitList();
                if (transportedUnitList != null) {
                    Object[] objArrA = transportedUnitList.a();
                    for (int i = transportedUnitList.size - 1; i >= 0; i--) {
                        OrderableUnit orderableUnit2 = (OrderableUnit) objArrA[i];
                        if (orderableUnit2 != null && (this.slot == -1 || i == this.slot)) {
                            if (this._withTag != null) {
                                if (AnimationTag.a(this._withTag, orderableUnit2.getTags())) {
                                    transportedUnitCount++;
                                }
                            }
                        }
                    }
                }
            }
            return transportedUnitCount;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$HasFlagDynamicBoolean.class */
    public static class HasFlagDynamicBoolean extends LogicBoolean {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0)
        public LogicBoolean id;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            validate(str, str2, str3, logicBooleanContext, z);
            if (this.id == null) {
                throw new BooleanParseException("Flag id must FastArrayList set");
            }
            Float staticNumber = getStaticNumber(this.id);
            if (staticNumber != null) {
                HasFlagBoolean hasFlagBoolean = new HasFlagBoolean();
                hasFlagBoolean.id((int) staticNumber.floatValue());
                return hasFlagBoolean;
            }
            return this;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            int number = (int) this.id.readNumber(getParameterContext(orderableUnit));
            if (number >= 0 && number <= 31) {
                if (HasFlagBoolean.isFlagSet(orderableUnit.unitExperience, 1 << number)) {
                    return true;
                }
                return false;
            }
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "HasFlag(id:" + this.id.getMatchFailReasonForPlayer(getParameterContext(orderableUnit)) + ")";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$HasFlagBoolean.class */
    public static class HasFlagBoolean extends LogicBoolean {
        public int flagMask = 0;
        public int flagId = -1;

        @LogicBoolean.Parameter(positional = 0)
        public void id(int i) {
            if (i < 0 || i > 31) {
                throw new BooleanParseException("Flag id must FastArrayList between 0-31");
            }
            this.flagId = i;
            this.flagMask = 1 << i;
        }

        public static boolean isFlagSet(int i, int i2) {
            return (i2 & i) == i2;
        }

        public static byte setFlag(int i, int i2) {
            return (byte) (i2 | i);
        }

        public static byte unsetFlag(int i, int i2) {
            return (byte) (i2 & (i ^ (-1)));
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "HasFlag(id:" + this.flagId + ")";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = true;
            if (this.flagMask != 0 && !isFlagSet(orderableUnit.unitExperience, this.flagMask)) {
                z = false;
            }
            return z;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$IsOnTeam.class */
    public static class IsOnTeam extends LogicBoolean.LogicBooleanCommonLocking {
        int teamId = -99;

        @LogicBoolean.Parameter
        public void team(int i) {
            if (i < -1 || i > PlayerTeam.TEAM_NEUTRAL) {
                throw new BooleanParseException("Flag id must FastArrayList between 0-" + PlayerTeam.TEAM_NEUTRAL);
            }
            this.teamId = i;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this.teamId == -99) {
                throw new BooleanParseException("Expended teamId argument for function:" + str);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "Team(id:" + this.teamId + ")";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = true;
            if (orderableUnit.team.teamId != this.teamId) {
                z = false;
            }
            return z;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TagsBoolean.class */
    public static class TagsBoolean extends LogicBoolean {
        public AnimationTag includesTag;

        @LogicBoolean.Parameter
        public void includes(String str) {
            this.includesTag = AnimationTag.c(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            String str = "Tag";
            if (this.includesTag != null) {
                str = str + " includes " + this.includesTag;
            }
            return str;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            AnimationSet animationSetDe;
            boolean z = true;
            if (this.includesTag != null && ((animationSetDe = orderableUnit.getTags()) == null || !AnimationTag.a(this.includesTag, animationSetDe))) {
                z = false;
            }
            return z;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TeamTagBoolean.class */
    public static class TeamTagBoolean extends LogicBoolean {
        public AnimationTag includesTag;

        @LogicBoolean.Parameter
        public void includes(String str) {
            this.includesTag = AnimationTag.c(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            String str = "Team Tag ";
            if (this.includesTag != null) {
                str = str + " includes " + this.includesTag;
            }
            return str;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            AnimationSet teamAnimationSet;
            boolean z = true;
            if (this.includesTag != null && ((teamAnimationSet = orderableUnit.team.getTeamAnimationSet()) == null || !AnimationTag.a(this.includesTag, teamAnimationSet))) {
                z = false;
            }
            return z;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$EventTagsBoolean.class */
    public class EventTagsBoolean extends LogicBoolean {
        public AnimationTag includesTag;

        @LogicBoolean.Parameter
        public void includes(String str) {
            this.includesTag = AnimationTag.c(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            String str = "EventTag";
            if (this.includesTag != null) {
                str = str + " includes " + this.includesTag;
            }
            return str;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = true;
            if (this.includesTag != null) {
                AnimationSet animationSet = null;
                if (LogicBoolean.currentEventContext != null) {
                    animationSet = LogicBoolean.currentEventContext.animationSet;
                }
                if (animationSet == null || !AnimationTag.a(this.includesTag, animationSet)) {
                    z = false;
                }
            }
            return z;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TransportingUnitWithTagsBoolean.class */
    public static class TransportingUnitWithTagsBoolean extends LogicBoolean {
        public AnimationTag includesTag;

        @LogicBoolean.Parameter
        public void includes(String str) {
            this.includesTag = AnimationTag.c(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            String str = "TransportingUnitWithTags ";
            if (this.includesTag != null) {
                str = str + " includes " + this.includesTag;
            }
            return str;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            FastArrayList transportedUnitList;
            boolean z = false;
            if (this.includesTag != null && (transportedUnitList = orderableUnit.getTransportedUnitList()) != null) {
                Object[] objArrA = transportedUnitList.a();
                for (int i = 0; i < transportedUnitList.size; i++) {
                    AnimationSet unitCombatAnimation = ((BaseUnit) objArrA[i]).getTags();
                    if (unitCombatAnimation != null && AnimationTag.a(this.includesTag, unitCombatAnimation)) {
                        z = true;
                    }
                }
            }
            return z;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$isTransportUnloading.class */
    public static class isTransportUnloading extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.isTransportUnloadingActive()) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "IsTransportUnloading";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$isDead.class */
    public class isDead extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.isDead) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "IsDead";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$HeightValueBoolean.class */
    public static final class HeightValueBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "Height";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.posZ;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$EnergyBoolean.class */
    public static final class EnergyBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "Energy";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.currentEnergy;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return orderableUnit.bd();
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$EnergyIncludingQueuedBoolean.class */
    public static final class EnergyIncludingQueuedBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "EnergyIncludingQueued";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.currentEnergy + orderableUnit.by().c;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return orderableUnit.bd();
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$PriceCreditsBoolean.class */
    public static final class PriceCreditsBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "PriceCredits";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.cL();
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$HpBoolean.class */
    public static final class HpBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "Hp";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.currentHealth;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return orderableUnit.maxHealth;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$MaxHpBoolean.class */
    public static final class MaxHpBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "maxHp";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.maxHealth;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$MaxShieldBoolean.class */
    public static final class MaxShieldBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "maxShield";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.unitEnergyMax;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$MaxEnergyBoolean.class */
    public static final class MaxEnergyBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "maxEnergy";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.bd();
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$UnitIdBoolean.class */
    public static final class UnitIdBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "id";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.objectId;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TeamDefeatedTechBoolean.class */
    public static final class TeamDefeatedTechBoolean extends LogicBoolean.LogicBooleanCommon {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicBooleanCommon
        public String getName() {
            return "teamDefeatedTech";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return orderableUnit.team.isTeamDefeatedTech;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TeamWipedOutBoolean.class */
    public static final class TeamWipedOutBoolean extends LogicBoolean.LogicBooleanCommon {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicBooleanCommon
        public String getName() {
            return "teamWipedOut";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return orderableUnit.team.isTeamWipedOut;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TeamVictoryBoolean.class */
    public static final class TeamVictoryBoolean extends LogicBoolean.LogicBooleanCommon {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicBooleanCommon
        public String getName() {
            return "teamVictory";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return orderableUnit.team.isTeamAlliedVictory;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$isEnergyRechargingBoolean.class */
    public static final class isEnergyRechargingBoolean extends LogicBoolean.LogicBooleanCommon {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicBooleanCommon
        public String getName() {
            return "isEnergyRecharging";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return orderableUnit.isUnitAtPositionX();
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TeamIdBoolean.class */
    public static final class TeamIdBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "teamId";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.team.teamId;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$PositionXBoolean.class */
    public static final class PositionXBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "x";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.posX;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$PositionYBoolean.class */
    public static final class PositionYBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "y";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.posY;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$RotationBoolean.class */
    public static final class RotationBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "dir";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.rotationSpeed;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$MaxMoveSpeedBoolean.class */
    public static final class MaxMoveSpeedBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "MaxMoveSpeed";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.getMoveSpeed();
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$BuiltAmountBoolean.class */
    public static final class BuiltAmountBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "BuiltAmount";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.buildProgress;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 1.0f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$ShieldBoolean.class */
    public static final class ShieldBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "Shield";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.shield;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return orderableUnit.unitEnergyMax;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$AmmoBoolean.class */
    public static final class AmmoBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "Ammo";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.unitLevel;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$ResourceCountBoolean.class */
    public static final class ResourceCountBoolean extends LogicBoolean.AbstractNumberBoolean {
        CustomUnitConfig meta;
        Resource type;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
            if (customUnitConfig == null) {
                throw new BooleanParseException("ResourceCountBoolean requires metadata");
            }
            this.meta = customUnitConfig;
        }

        @LogicBoolean.Parameter(positional = 0)
        public void type(String str) {
            this.type = this.meta.findOrCreateCustomResource(str);
            if (this.type == null) {
                throw new BooleanParseException("Could not find resource type: '" + str + "'");
            }
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this.type == null) {
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return this.type + VariableScope.nullOrMissingString;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            if (this.type == null) {
                return 0.0f;
            }
            return (float) this.type.a(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBooleanLoader.LogicBooleanContext createContext() {
            return new ResourceScope();
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            if (this.type == null) {
                return LogicBoolean.ReturnType.voidReturn;
            }
            if (this.greaterThan == -1.0f && this.lessThan == -1.0f && !this.full && !this.empty) {
                return LogicBoolean.ReturnType.number;
            }
            return LogicBoolean.ReturnType.bool;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void throwVoidReturnError(String str) {
            throw new RuntimeException("'" + str + "' requires type");
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean setChild(LogicBoolean logicBoolean) {
            return logicBoolean;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$ResourceScope.class */
    public static class ResourceScope extends LogicBooleanLoader.LogicBooleanScopeOnly {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.LogicBooleanContext
        public LogicBoolean parseNextElementInChain(String str, CustomUnitConfig customUnitConfig, String str2, boolean z, String str3, String str4, LogicBoolean logicBoolean) {
            Resource resourceFindOrCreateCustomResource = customUnitConfig.findOrCreateCustomResource(str2);
            if (resourceFindOrCreateCustomResource == null) {
                throw new BooleanParseException("'" + str3 + "': Could not find resource: '" + str2 + "'");
            }
            ResourceCountBoolean resourceCountBoolean = new ResourceCountBoolean();
            resourceCountBoolean.meta = customUnitConfig;
            resourceCountBoolean.type = resourceFindOrCreateCustomResource;
            return resourceCountBoolean;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$NumberOfConnectionsBoolean.class */
    public static final class NumberOfConnectionsBoolean extends LogicBoolean.AbstractNumberBoolean {
        CustomUnitConfig meta;
        AnimationTrackingEntry connectionMetadata;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
            if (customUnitConfig == null) {
                throw new BooleanParseException("NumberOfConnectionsBoolean requires metadata");
            }
            this.meta = customUnitConfig;
        }

        @LogicBoolean.Parameter
        public void name(String str) {
            this.connectionMetadata = this.meta.findAnimationChannelByTagName(str);
            if (this.connectionMetadata == null) {
                throw new BooleanParseException("Could not find connection type with name: " + str);
            }
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (logicBooleanContext != null && logicBooleanContext != LogicBooleanLoader.defaultContextReader) {
                throw new BooleanParseException("Function:" + str + " only supports use with 'self.'");
            }
            if (this.connectionMetadata == null) {
                throw new BooleanParseException("requires connection name");
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "NumberOfConnections";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.unitCustomComponents.a(this.connectionMetadata);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$HasResourcesBoolean.class */
    public static final class HasResourcesBoolean extends LogicBoolean {
        UnitPrice requiredResources;
        CustomUnitConfig meta;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
            if (customUnitConfig == null) {
                throw new BooleanParseException("HasResourcesBoolean requires metadata");
            }
            this.meta = customUnitConfig;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
            try {
                this.requiredResources = UnitPrice.b(this.meta, str);
            } catch (ConfigParseException e) {
                throw new BooleanParseException(e.getMessage(), e);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (this.requiredResources.b(orderableUnit)) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "HasResources(" + this.requiredResources.a(false, true, 8, true) + ")";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$IsResourceLargerThan.class */
    public static final class IsResourceLargerThan extends LogicBoolean {
        CustomUnitConfig meta;
        Resource source;
        Resource compareTarget;

        @LogicBoolean.Parameter
        public float byMoreThan = 0.0f;

        @LogicBoolean.Parameter
        public float multiplyTargetBy = 1.0f;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
            if (customUnitConfig == null) {
                throw new BooleanParseException("IsResourceLargerThan requires metadata");
            }
            this.meta = customUnitConfig;
        }

        @LogicBoolean.Parameter
        public void source(String str) {
            this.source = this.meta.findOrCreateCustomResource(str);
            if (this.source == null) {
                throw new BooleanParseException("Could not find custom resource type of:" + this.source);
            }
        }

        @LogicBoolean.Parameter
        public void compareTarget(String str) {
            this.compareTarget = this.meta.findOrCreateCustomResource(str);
            if (this.compareTarget == null) {
                throw new BooleanParseException("Could not find custom resource type of:" + this.compareTarget);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this.source == null) {
                throw new BooleanParseException("Requires 'source'");
            }
            if (this.compareTarget == null) {
                throw new BooleanParseException("Requires 'compareTarget'");
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (this.source.a(orderableUnit) > (this.compareTarget.a(orderableUnit) + ((double) this.byMoreThan)) * ((double) this.multiplyTargetBy)) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "IsResourceLargerThan(" + this.source.j() + " vs " + this.compareTarget.j() + ")";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$KillsBoolean.class */
    public static final class KillsBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "Kills";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.unitCargoType;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$NumberOfUnitsInTeam.class */
    public static final class NumberOfUnitsInTeam extends LogicBoolean.AbstractNumberBoolean {
        public AnimationTag _withTag;

        @LogicBoolean.Parameter
        public float withinRange = -1.0f;
        public float withinRangeSq = -1.0f;

        @LogicBoolean.Parameter
        public boolean incompleteBuildings;

        @LogicBoolean.Parameter
        public boolean factoryQueue;

        @LogicBoolean.Parameter
        public boolean neutralTeam;

        @LogicBoolean.Parameter
        public boolean allTeams;
        public boolean useAggressiveTeamInsteadOfNeutralTeam;
        public static final HandleCallbackCount handleCallbackCount = new HandleCallbackCount();

        @LogicBoolean.Parameter
        public void aggressiveTeam(boolean z) {
            if (z) {
                this.neutralTeam = true;
                this.useAggressiveTeamInsteadOfNeutralTeam = true;
            }
        }

        @LogicBoolean.Parameter
        public void withTag(String str) {
            this._withTag = AnimationTag.c(str);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this.withinRange > 1000.0f) {
                throw new BooleanParseException("For CPU reasons withinRange argument cannot FastArrayList over 1000 (but unlimited range is fine) in function:" + str);
            }
            if (this.withinRange > 0.0f) {
                this.withinRangeSq = this.withinRange * this.withinRange;
                if (this.factoryQueue) {
                    throw new BooleanParseException("'factoryQueue' and 'withinRange' are not supported at the same time in function:" + str);
                }
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "Unit count of " + this._withTag + (this.withinRange < 0.0f ? VariableScope.nullOrMissingString : " (within range " + this.withinRange + ")");
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            PlayerTeam playerTeam;
            int iCanAffordResource;
            int i;
            int iCanAffordResource2;
            AnimationTag animationTag = this._withTag;
            if (this.allTeams) {
                playerTeam = null;
            } else if (this.neutralTeam) {
                if (!this.useAggressiveTeamInsteadOfNeutralTeam) {
                    playerTeam = PlayerTeam.TEAM_ALL;
                } else {
                    playerTeam = PlayerTeam.TEAM_UNKNOWN;
                }
            } else {
                playerTeam = orderableUnit.team;
            }
            if (playerTeam == null) {
                iCanAffordResource = 0;
                for (PlayerTeam playerTeam2 : PlayerTeam.getTeamInstances()) {
                    if (animationTag == null) {
                        i = iCanAffordResource;
                        iCanAffordResource2 = playerTeam2.getUnitCount(this.incompleteBuildings, this.factoryQueue);
                    } else {
                        i = iCanAffordResource;
                        iCanAffordResource2 = playerTeam2.getUnitCountWithTag(animationTag, this.incompleteBuildings, this.factoryQueue);
                    }
                    iCanAffordResource = i + iCanAffordResource2;
                }
            } else if (animationTag == null) {
                iCanAffordResource = playerTeam.getUnitCount(this.incompleteBuildings, this.factoryQueue);
            } else {
                iCanAffordResource = playerTeam.getUnitCountWithTag(animationTag, this.incompleteBuildings, this.factoryQueue);
            }
            if (this.withinRange < 0.0f || iCanAffordResource == 0) {
                return iCanAffordResource;
            }
            handleCallbackCount.withinRangeSq = this.withinRangeSq;
            handleCallbackCount.count = 0;
            handleCallbackCount.tag = animationTag;
            handleCallbackCount.incompleteBuildings = this.incompleteBuildings;
            handleCallbackCount.targetTeam = playerTeam;
            GameEngine.getInstance().unitSpatialIndex.a(orderableUnit.posX, orderableUnit.posY, this.withinRange, orderableUnit, 0.0f, handleCallbackCount);
            return handleCallbackCount.count;
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$NumberOfUnitsInTeam$HandleCallbackCount.class */
        public static class HandleCallbackCount extends FilteredUnitCallback {
            public AnimationTag tag;
            public int count;
            public float withinRangeSq;
            public boolean incompleteBuildings;
            public PlayerTeam targetTeam;

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public void setup(OrderableUnit orderableUnit, float f) {
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public int excludeTeam(OrderableUnit orderableUnit) {
                return -1;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit) {
                return null;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public PlayerTeam onlyTeam(OrderableUnit orderableUnit) {
                return this.targetTeam;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
            public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
                AnimationSet unitCombatAnimation = baseUnit.getTags();
                if ((this.tag == null || (unitCombatAnimation != null && AnimationTag.a(this.tag, unitCombatAnimation))) && Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY) < this.withinRangeSq) {
                    if (baseUnit.buildProgress < 1.0f && !this.incompleteBuildings) {
                        return;
                    }
                    this.count++;
                }
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$NumberOfUnitsInEnemyOrAllyTeam.class */
    public static final class NumberOfUnitsInEnemyOrAllyTeam extends LogicBoolean.AbstractNumberBoolean {
        public AnimationTag _withTag;

        @LogicBoolean.Parameter
        public float withinRange = -1.0f;
        public float withinRangeSq = -1.0f;

        @LogicBoolean.Parameter
        public boolean incompleteBuildings;

        @LogicBoolean.Parameter
        public boolean factoryQueue;

        @LogicBoolean.Parameter
        public boolean ally;
        public static final HandleCallbackCountEnemies handleCallbackCountEnemies = new HandleCallbackCountEnemies();
        public static final HandleCallbackCountAlly handleCallbackCountAlly = new HandleCallbackCountAlly();

        @LogicBoolean.Parameter
        public void withTag(String str) {
            this._withTag = AnimationTag.c(str);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this.withinRange > 1000.0f) {
                throw new BooleanParseException("For CPU reasons withinRange argument cannot FastArrayList over 1000 (but unlimited range is fine) in function:" + str);
            }
            if (this.withinRange > 0.0f) {
                this.withinRangeSq = this.withinRange * this.withinRange;
                if (this.factoryQueue) {
                    throw new BooleanParseException("'factoryQueue' and 'withinRange' are not supported at the same time in function:" + str);
                }
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "Enemy Unit count of " + this._withTag + (this.withinRange < 0.0f ? VariableScope.nullOrMissingString : " (within range " + this.withinRange + ")");
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            int iC;
            PlayerTeam playerTeam = orderableUnit.team;
            if (!this.ally) {
                iC = 0 + playerTeam.b(this._withTag, this.incompleteBuildings, this.factoryQueue);
            } else {
                iC = 0 + playerTeam.c(this._withTag, this.incompleteBuildings, this.factoryQueue);
            }
            if (this.withinRange < 0.0f || iC == 0) {
                return iC;
            }
            if (!this.ally) {
                handleCallbackCountEnemies.withinRangeSq = this.withinRangeSq;
                handleCallbackCountEnemies.count = 0;
                handleCallbackCountEnemies.tag = this._withTag;
                handleCallbackCountEnemies.incompleteBuildings = this.incompleteBuildings;
                GameEngine.getInstance().unitSpatialIndex.a(orderableUnit.posX, orderableUnit.posY, this.withinRange, orderableUnit, 0.0f, handleCallbackCountEnemies);
                return handleCallbackCountEnemies.count;
            }
            handleCallbackCountAlly.withinRangeSq = this.withinRangeSq;
            handleCallbackCountAlly.count = 0;
            handleCallbackCountAlly.tag = this._withTag;
            handleCallbackCountAlly.incompleteBuildings = this.incompleteBuildings;
            handleCallbackCountAlly.ally = orderableUnit.team;
            GameEngine.getInstance().unitSpatialIndex.a(orderableUnit.posX, orderableUnit.posY, this.withinRange, orderableUnit, 0.0f, handleCallbackCountAlly);
            return handleCallbackCountAlly.count;
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$NumberOfUnitsInEnemyOrAllyTeam$HandleCallbackCountEnemies.class */
        public static class HandleCallbackCountEnemies extends FilteredUnitCallback {
            public AnimationTag tag;
            public int count;
            public float withinRangeSq;
            public boolean incompleteBuildings;

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public void setup(OrderableUnit orderableUnit, float f) {
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public int excludeTeam(OrderableUnit orderableUnit) {
                return -1;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit) {
                return orderableUnit.team;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public PlayerTeam onlyTeam(OrderableUnit orderableUnit) {
                return null;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
            public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
                AnimationSet unitCombatAnimation = baseUnit.getTags();
                if ((this.tag == null || (unitCombatAnimation != null && AnimationTag.a(this.tag, unitCombatAnimation))) && Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY) < this.withinRangeSq) {
                    if (baseUnit.buildProgress < 1.0f && !this.incompleteBuildings) {
                        return;
                    }
                    this.count++;
                }
            }
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$NumberOfUnitsInEnemyOrAllyTeam$HandleCallbackCountAlly.class */
        public static class HandleCallbackCountAlly extends FilteredUnitCallback {
            public PlayerTeam ally;
            public AnimationTag tag;
            public int count;
            public float withinRangeSq;
            public boolean incompleteBuildings;

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public void setup(OrderableUnit orderableUnit, float f) {
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public int excludeTeam(OrderableUnit orderableUnit) {
                return -1;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit) {
                return null;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
            public PlayerTeam onlyTeam(OrderableUnit orderableUnit) {
                return null;
            }

            @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
            public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
                if (this.ally == baseUnit.team || !this.ally.d(baseUnit.team)) {
                    return;
                }
                AnimationSet unitCombatAnimation = baseUnit.getTags();
                if ((this.tag == null || (unitCombatAnimation != null && AnimationTag.a(this.tag, unitCombatAnimation))) && Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY) < this.withinRangeSq) {
                    if (baseUnit.buildProgress < 1.0f && !this.incompleteBuildings) {
                        return;
                    }
                    this.count++;
                }
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$AmmoIncludingQueuedBoolean.class */
    public static final class AmmoIncludingQueuedBoolean extends LogicBoolean.AbstractNumberBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "AmmoIncludingQueued";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.unitLevel + orderableUnit.by().f;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$QueueSize.class */
    public static final class QueueSize extends LogicBoolean.AbstractNumberBoolean {
        public AnimationTag _withActionTag;

        @LogicBoolean.Parameter
        public void withActionTag(String str) {
            this._withActionTag = AnimationTag.c(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            return "QueueSize";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getValue(OrderableUnit orderableUnit) {
            return orderableUnit.a(this._withActionTag);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$TimeAliveBoolean.class */
    public static final class TimeAliveBoolean extends LogicBoolean.TimeBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.TimeBoolean
        public String getName() {
            return "TimeAlive";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.TimeBoolean
        public int getTime(OrderableUnit orderableUnit) {
            return orderableUnit.unitFlags1;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$LastConvertedBoolean.class */
    public static final class LastConvertedBoolean extends LogicBoolean.TimeBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.TimeBoolean
        public String getName() {
            return "LastConverted";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.TimeBoolean
        public int getTime(OrderableUnit orderableUnit) {
            return orderableUnit.unitFlags3;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$CustomTimerBoolean.class */
    public static final class CustomTimerBoolean extends LogicBoolean.TimeBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.TimeBoolean
        public String getName() {
            return "CustomTimer";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.TimeBoolean
        public int getTime(OrderableUnit orderableUnit) {
            return orderableUnit.unitFlags2;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$HasTakenDamage.class */
    public static class HasTakenDamage extends LogicBoolean.TimeBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.TimeBoolean
        public int getTime(OrderableUnit orderableUnit) {
            return orderableUnit.bs;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.TimeBoolean
        public String getName() {
            return "HasTakenDamage";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$IsAttackingBoolean.class */
    public static class IsAttackingBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.hasActiveMovementTarget()) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "Attacking";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$IsReversingBoolean.class */
    public static class IsReversingBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.isRotating) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "IsReversing";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$IsControlledByAI.class */
    public static class IsControlledByAI extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            if (orderableUnit.team.isTeamSpectator) {
                z = true;
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "IsControlledByAI";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$CompareUnitsBroken.class */
    public class CompareUnitsBroken extends LogicBoolean {
        CustomUnitConfig meta;
        UnitReference sameUnitAs;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
            if (customUnitConfig == null) {
                throw new BooleanParseException("SameUnitAs requires metadata");
            }
            this.meta = customUnitConfig;
        }

        @LogicBoolean.Parameter
        public void sameUnitAs(String str) {
            try {
                this.sameUnitAs = UnitReference.parseUnitReference(this.meta, str, VariableScope.nullOrMissingString, VariableScope.nullOrMissingString, null, false);
            } catch (ConfigParseException e) {
                throw new BooleanParseException(e.getMessage(), e);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this.sameUnitAs == null) {
                throw new BooleanParseException("Missing required parameters (Possible parameters:" + getParameters().allParametersString + ")");
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "SameUnitAs";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$HasParent.class */
    public static final class HasParent extends LogicBoolean {
        public AnimationTag _withTag;

        @LogicBoolean.Parameter
        public void withTag(String str) {
            this._withTag = AnimationTag.c(str);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = false;
            BaseUnit baseUnitDr = orderableUnit.dr();
            if (baseUnitDr != null) {
                z = true;
                if (this._withTag != null) {
                    if (!AnimationTag.a(this._withTag, baseUnitDr.getTags())) {
                        z = false;
                    }
                }
            }
            return z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "HasParent";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$NumberOfAttachedUnitsBoolean.class */
    public static final class NumberOfAttachedUnitsBoolean extends LogicBoolean.AbstractNumberBoolean {
        public AnimationTag _withTag;
        short attachmentId = -1;
        CustomUnitConfig meta;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
            if (customUnitConfig == null) {
                throw new ConfigException("NumberOfAttachedUnitsBoolean requires metadata");
            }
            this.meta = customUnitConfig;
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (logicBooleanContext != null && logicBooleanContext != LogicBooleanLoader.defaultContextReader && this.attachmentId != -1) {
                throw new BooleanParseException("Function:" + str + " only supports use with 'self.' when using 'slot'");
            }
        }

        @LogicBoolean.Parameter
        public void withTag(String str) {
            this._withTag = AnimationTag.c(str);
        }

        @LogicBoolean.Parameter
        public void slot(String str) {
            AttachmentSlotDefinition attachmentSlotDefinitionFindEnergyTransferRuleByName = this.meta.findEnergyTransferRuleByName(str);
            if (attachmentSlotDefinitionFindEnergyTransferRuleByName == null) {
                throw new ConfigException("No attachment slot with name: " + str + " found");
            }
            this.attachmentId = attachmentSlotDefinitionFindEnergyTransferRuleByName.a();
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public String getName() {
            String str = VariableScope.nullOrMissingString;
            if (this._withTag != null) {
                str = str + "tag=" + this._withTag;
            }
            if (this.attachmentId != -1) {
                str = str + " attachmentId=" + ((int) this.attachmentId);
            }
            return "NumberOfAttachedUnits(" + str + ")";
        }

        /* JADX WARN: Removed duplicated region for block: B:26:0x0075  */
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public float getValue(OrderableUnit orderableUnit) {
            if (!(orderableUnit instanceof CustomUnit)) {
                return 0.0f;
            }
            CustomUnit customUnit = (CustomUnit) orderableUnit;
            if (customUnit.C == null) {
                return 0.0f;
            }
            int i = 0;
            Object[] objArrA = customUnit.C.a();
            for (int i2 = customUnit.C.size - 1; i2 >= 0; i2--) {
                OrderableUnit orderableUnit2 = (OrderableUnit) objArrA[i2];
                if (orderableUnit2 != null && (this.attachmentId == -1 || i2 == this.attachmentId)) {
                    if (this._withTag != null) {
                        if (AnimationTag.a(this._withTag, orderableUnit2.getTags())) {
                            i++;
                        }
                    }
                }
            }
            return i;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.AbstractNumberBoolean
        public float getMaxValue(OrderableUnit orderableUnit) {
            return 2.1474836E9f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanGameFunctions$ThisActionRepeatedCount.class */
    public static class ThisActionRepeatedCount extends LogicBoolean.LogicNumberOnly {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return CustomUnit.dO;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "ThisActionRepeatedCount";
        }
    }
}
