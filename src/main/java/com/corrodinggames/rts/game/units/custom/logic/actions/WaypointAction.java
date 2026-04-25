package com.corrodinggames.rts.game.units.custom.logic.actions;

import android.graphics.PointF;
import com.corrodinggames.rts.game.TeamRelation;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitCommand;
import com.corrodinggames.rts.game.units.UnitCommandType;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitActionHandler;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.UnitTypeReference;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/b.class */
public class WaypointAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    public boolean clearAllWaypoints;

    /* JADX INFO: renamed from: b */
    public boolean clearActiveWaypoint;

    /* JADX INFO: renamed from: c */
    public UnitCommandType waypointType;

    /* JADX INFO: renamed from: d */
    public UnitTypeReference waypointUnitType;

    /* JADX INFO: renamed from: e */
    public boolean prependWaypoint;

    /* JADX INFO: renamed from: f */
    public AnimationSet nearestUnitTagged;

    /* JADX INFO: renamed from: g */
    public TeamRelation nearestUnitTeam;

    /* JADX INFO: renamed from: h */
    public float nearestUnitMaxRange;

    /* JADX INFO: renamed from: i */
    public boolean targetRandomUnit;

    /* JADX INFO: renamed from: j */
    public AnimationSet randomUnitTagged;

    /* JADX INFO: renamed from: k */
    public TeamRelation randomUnitTeam;

    /* JADX INFO: renamed from: l */
    public float randomUnitMaxRange;

    /* JADX INFO: renamed from: m */
    public boolean mapMustBeReachable;

    /* JADX INFO: renamed from: n */
    public boolean positionFromAction;

    /* JADX INFO: renamed from: o */
    public PointF positionOffsetFromSelf;

    /* JADX INFO: renamed from: p */
    public PointF positionRelativeOffsetFromSelf;

    /* JADX INFO: renamed from: q */
    public PointF positionRandomOffsetFromSelf;

    /* JADX INFO: renamed from: r */
    public LogicBoolean targetFromReference;

    /* JADX INFO: renamed from: s */
    public float waypointMaxTime = -1.0f;

    /* JADX INFO: renamed from: t */
    public CustomUnitActionHandler triggerActionIfFailed;

    /* JADX INFO: renamed from: u */
    public CustomUnitActionHandler triggerActionIfMatched;
    public static UnitCommand v = new UnitCommand();
    public static final UnitSearchCallback w = new UnitSearchCallback();

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) {
        boolean z2 = false;
        boolean zBooleanValue = iniFile.getBoolean(str, str2 + "clearAllWaypoints", (Boolean) false).booleanValue();
        if (zBooleanValue) {
            z2 = true;
        }
        boolean zBooleanValue2 = iniFile.getBoolean(str, str2 + "clearActiveWaypoint", (Boolean) false).booleanValue();
        if (zBooleanValue2) {
            z2 = true;
        }
        UnitCommandType unitCommandType = (UnitCommandType) iniFile.getEnum(str, "addWaypoint_type", (Enum) null, UnitCommandType.class);
        boolean zBooleanValue3 = iniFile.getBoolean(str, str2 + "addWaypoint_prepend", (Boolean) false).booleanValue();
        AnimationSet animationSet = iniFile.getAnimationSet(customUnitConfig, str, str2 + "addWaypoint_target_nearestUnit_tagged", (AnimationSet) null);
        TeamRelation teamRelation = (TeamRelation) iniFile.getEnum(str, "addWaypoint_target_nearestUnit_team", TeamRelation.own, TeamRelation.class);
        float fFloatValue = iniFile.getFloat(str, str2 + "addWaypoint_target_nearestUnit_maxRange", Float.valueOf(10000.0f)).floatValue();
        AnimationSet animationSet2 = iniFile.getAnimationSet(customUnitConfig, str, str2 + "addWaypoint_target_randomUnit_tagged", (AnimationSet) null);
        TeamRelation teamRelation2 = (TeamRelation) iniFile.getEnum(str, "addWaypoint_target_randomUnit_team", TeamRelation.own, TeamRelation.class);
        float fFloatValue2 = iniFile.getFloat(str, str2 + "addWaypoint_target_randomUnit_maxRange", Float.valueOf(10000.0f)).floatValue();
        float fFloatValue3 = iniFile.getTime(str, str2 + "addWaypoint_maxTime", Float.valueOf(-1.0f)).floatValue();
        CustomUnitActionHandler customUnitAction = iniFile.getCustomUnitAction(customUnitConfig, str, str2 + "addWaypoint_triggerActionIfFailed", (CustomUnitActionHandler) null);
        CustomUnitActionHandler customUnitAction2 = iniFile.getCustomUnitAction(customUnitConfig, str, str2 + "addWaypoint_triggerActionIfMatched", (CustomUnitActionHandler) null);
        PointF pointF = iniFile.getPointF(str, str2 + "addWaypoint_position_offsetFromSelf", (PointF) null);
        PointF pointF2 = iniFile.getPointF(str, str2 + "addWaypoint_position_relativeOffsetFromSelf", (PointF) null);
        PointF pointF3 = iniFile.getPointF(str, str2 + "addWaypoint_position_randomOffsetFromSelf", (PointF) null);
        boolean z3 = (pointF == null && pointF2 == null && pointF3 == null) ? false : true;
        boolean z4 = animationSet != null;
        boolean z5 = animationSet2 != null;
        boolean zBooleanValue4 = iniFile.getBoolean(str, str2 + "addWaypoint_position_fromAction", (Boolean) false).booleanValue();
        LogicBoolean logicBoolean = iniFile.getInt(customUnitConfig, str, str2 + "addWaypoint_target_fromReference", null);
        if (logicBoolean != null) {
            z2 = true;
            if (z4 || z5) {
                throw new RuntimeException("[" + str + "] addWaypoint_target_nearestUnit/randomUnit and addWaypoint_target_fromReference cannot be used together");
            }
            if (z3) {
                throw new RuntimeException("[" + str + "] addWaypoint_position_offset* and addWaypoint_target_fromReference cannot be used together");
            }
            if (zBooleanValue4) {
                throw new RuntimeException("[" + str + "] addWaypoint_position_fromAction and addWaypoint_target_fromReference cannot be used together");
            }
        }
        if (zBooleanValue4) {
            z2 = true;
            if (z4 || z5) {
                throw new RuntimeException("[" + str + "] addWaypoint_target_* and addWaypoint_position_fromAction cannot be used together");
            }
            if (z3) {
                throw new RuntimeException("[" + str + "] addWaypoint_position_offset* and addWaypoint_position_fromAction cannot be used together");
            }
        }
        if ((z4 || z5 || z3 || logicBoolean != null) && unitCommandType == null) {
            throw new RuntimeException("[" + str + "] addWaypoint_type is required when using addWaypoint_*");
        }
        if (unitCommandType != null) {
            z2 = true;
            if (!z4 && !z5 && !z3 && !zBooleanValue4 && logicBoolean == null) {
                throw new RuntimeException("[" + str + "] addWaypoint_target_nearestUnit_tagged, addWaypoint_position_offsetFromSelf or addWaypoint_target_fromReference is required when using addWaypoint_*");
            }
        }
        if (z3) {
            z2 = true;
            if (z4 || z5) {
                throw new RuntimeException("[" + str + "] addWaypoint_target_* and addWaypoint_position_* cannot be used together");
            }
            if (unitCommandType != UnitCommandType.move && unitCommandType != UnitCommandType.attackMove) {
                throw new RuntimeException("[" + str + "] addWaypoint_position_* only supports position based waypoints (eg: move, attackMove)");
            }
        }
        if (z4 && z5) {
            throw new RuntimeException("[" + str + "] addWaypoint_target_nearestUnit_* and addWaypoint_target_randomUnit_* cannot be used together");
        }
        if (z2) {
            WaypointAction waypointAction = new WaypointAction();
            waypointAction.clearAllWaypoints = zBooleanValue;
            waypointAction.clearActiveWaypoint = zBooleanValue2;
            if (unitCommandType != null) {
                waypointAction.waypointType = unitCommandType;
                if (waypointAction.waypointType == UnitCommandType.build) {
                    waypointAction.waypointUnitType = customUnitConfig.reloadAllCustomUnits(iniFile.getString(str, str2 + "addWaypoint_unitType", (String) null), str2 + "addWaypoint_unitType", str);
                    if (waypointAction.waypointUnitType == null) {
                        throw new RuntimeException("[" + str + "] addWaypoint_type: build requires addWaypoint_unitType");
                    }
                }
                waypointAction.prependWaypoint = zBooleanValue3;
                waypointAction.nearestUnitTagged = animationSet;
                waypointAction.nearestUnitTeam = teamRelation;
                waypointAction.nearestUnitMaxRange = fFloatValue;
                waypointAction.randomUnitTagged = animationSet2;
                waypointAction.randomUnitTeam = teamRelation2;
                waypointAction.randomUnitMaxRange = fFloatValue2;
                if (z5) {
                    waypointAction.targetRandomUnit = true;
                }
                waypointAction.mapMustBeReachable = iniFile.getBoolean(str, str2 + "addWaypoint_target_mapMustBeReachable", (Boolean) true).booleanValue();
                waypointAction.positionOffsetFromSelf = pointF;
                waypointAction.positionRelativeOffsetFromSelf = pointF2;
                waypointAction.positionRandomOffsetFromSelf = pointF3;
                waypointAction.positionFromAction = zBooleanValue4;
                waypointAction.targetFromReference = logicBoolean;
                waypointAction.waypointMaxTime = fFloatValue3;
                waypointAction.triggerActionIfFailed = customUnitAction;
                waypointAction.triggerActionIfMatched = customUnitAction2;
            }
            customActionDef.ac.add(waypointAction);
        }
    }

    public UnitCommand a(CustomUnit customUnit, float f, float f2, BaseUnit baseUnit, int i) {
        UnitCommand unitCommandAp;
        if (this.waypointMaxTime == 0.0f) {
            unitCommandAp = v;
            unitCommandAp.resetCommand();
        } else if (this.prependWaypoint) {
            unitCommandAp = customUnit.queueNextWaypoint();
            customUnit.detachTransportedUnits();
            customUnit.clearTransportState();
        } else {
            unitCommandAp = customUnit.appendWaypoint();
        }
        if (this.waypointType == UnitCommandType.move) {
            unitCommandAp.setMoveTarget(f, f2);
        } else if (this.waypointType == UnitCommandType.attackMove) {
            unitCommandAp.setAttackMoveTarget(f, f2);
        } else if (this.waypointType == UnitCommandType.guard && baseUnit != null) {
            unitCommandAp.setGuardCommand(baseUnit);
        } else if (this.waypointType == UnitCommandType.follow && baseUnit != null) {
            unitCommandAp.setFollowTargetUnit(baseUnit);
        } else if (this.waypointType == UnitCommandType.loadInto && baseUnit != null) {
            unitCommandAp.setLoadIntoTargetUnit(baseUnit);
        } else if (this.waypointType == UnitCommandType.loadUp && baseUnit != null) {
            unitCommandAp.setLoadUpTargetUnit(baseUnit);
        } else if (this.waypointType == UnitCommandType.attack && baseUnit != null) {
            unitCommandAp.setAttackTarget(baseUnit);
        } else if (this.waypointType == UnitCommandType.reclaim && baseUnit != null) {
            unitCommandAp.setReclaimTargetUnit(baseUnit);
        } else if (this.waypointType == UnitCommandType.repair && baseUnit != null) {
            unitCommandAp.setRepairCommand(baseUnit);
        } else if (this.waypointType == UnitCommandType.touchTarget && baseUnit != null) {
            unitCommandAp.setTouchTargetUnit(baseUnit);
        } else if (this.waypointType == UnitCommandType.build) {
            unitCommandAp.setBuildCommand(f, f2, this.waypointUnitType.c(), 1);
        } else {
            customUnit.advanceWaypoint();
        }
        unitCommandAp.maxWayPointSurvivingTime = this.waypointMaxTime;
        unitCommandAp.isForceMove = true;
        if (this.triggerActionIfMatched != null) {
            this.triggerActionIfMatched.a(customUnit, new PointF(unitCommandAp.getTargetX(), unitCommandAp.getTargetY()), unitCommandAp.getTargetUnit(), i + 1, 0);
        }
        return unitCommandAp;
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        if (this.clearAllWaypoints) {
            customUnit.clearAllWaypoints();
        } else if (this.clearActiveWaypoint) {
            customUnit.advanceWaypoint();
        }
        if (this.waypointType != null) {
            if (this.targetFromReference != null) {
                BaseUnit unit = this.targetFromReference.readUnit(customUnit);
                if (unit != null) {
                    a(customUnit, unit.posX, unit.posY, unit, i);
                    return true;
                }
                if (this.triggerActionIfFailed != null) {
                    this.triggerActionIfFailed.a(customUnit, pointF, baseUnit, i + 1, 0);
                    return true;
                }
                return true;
            }
            if (this.positionFromAction) {
                if (pointF == null) {
                    if (this.triggerActionIfFailed != null) {
                        this.triggerActionIfFailed.a(customUnit, pointF, baseUnit, i + 1, 0);
                        return true;
                    }
                    return true;
                }
                a(customUnit, pointF.x, pointF.y, (BaseUnit) null, i);
                return true;
            }
            if (this.positionOffsetFromSelf != null || this.positionRelativeOffsetFromSelf != null || this.positionRandomOffsetFromSelf != null) {
                float deterministicRandomInt = customUnit.posX;
                float deterministicRandomInt2 = customUnit.posY;
                if (this.positionOffsetFromSelf != null) {
                    deterministicRandomInt += this.positionOffsetFromSelf.x;
                    deterministicRandomInt2 += this.positionOffsetFromSelf.y;
                }
                if (this.positionRelativeOffsetFromSelf != null) {
                    float f = this.positionRelativeOffsetFromSelf.x;
                    float f2 = this.positionRelativeOffsetFromSelf.y;
                    float fFastCos = Utility.fastCos(customUnit.rotationSpeed);
                    float fFastSin = Utility.fastSin(customUnit.rotationSpeed);
                    deterministicRandomInt += (fFastCos * f2) - (fFastSin * f);
                    deterministicRandomInt2 += (fFastSin * f2) + (fFastCos * f);
                }
                if (this.positionRandomOffsetFromSelf != null) {
                    deterministicRandomInt += Utility.getDeterministicRandomInt((GameObject) customUnit, -((int) this.positionRandomOffsetFromSelf.x), (int) this.positionRandomOffsetFromSelf.x, i + 1);
                    deterministicRandomInt2 += Utility.getDeterministicRandomInt((GameObject) customUnit, -((int) this.positionRandomOffsetFromSelf.y), (int) this.positionRandomOffsetFromSelf.y, i + 2);
                }
                a(customUnit, deterministicRandomInt, deterministicRandomInt2, (BaseUnit) null, i);
                return true;
            }
            if (this.targetRandomUnit) {
                w.maxRange = this.randomUnitMaxRange * this.randomUnitMaxRange;
                w.unitTags = this.randomUnitTagged;
                w.includeNotBuilt = false;
                w.foundUnit = null;
                w.teamFilter = this.randomUnitTeam;
                w.mapMustBeReachable = this.mapMustBeReachable;
                w.collectMultipleUnits = true;
                w.collectedUnits.clear();
                GameEngine.getInstance().unitSpatialIndex.a(customUnit.posX, customUnit.posY, this.randomUnitMaxRange, customUnit, 0.0f, w);
                if (w.collectedUnits.size() == 0) {
                    if (this.triggerActionIfFailed != null) {
                        this.triggerActionIfFailed.a(customUnit, pointF, baseUnit, i + 1, 0);
                        return true;
                    }
                    return true;
                }
                FastArrayList fastArrayList = w.collectedUnits;
                int deterministicRandomInt3 = Utility.getDeterministicRandomInt((GameObject) customUnit, 0, fastArrayList.size(), 0);
                customUnit.unitFlags4++;
                if (deterministicRandomInt3 > fastArrayList.size() - 1) {
                    deterministicRandomInt3 = fastArrayList.size() - 1;
                }
                BaseUnit baseUnit2 = (BaseUnit) fastArrayList.get(deterministicRandomInt3);
                a(customUnit, baseUnit2.posX, baseUnit2.posY, baseUnit2, i);
                return true;
            }
            w.maxRange = this.nearestUnitMaxRange * this.nearestUnitMaxRange;
            w.unitTags = this.nearestUnitTagged;
            w.includeNotBuilt = false;
            w.foundUnit = null;
            w.teamFilter = this.nearestUnitTeam;
            w.mapMustBeReachable = this.mapMustBeReachable;
            w.collectMultipleUnits = false;
            GameEngine.getInstance().unitSpatialIndex.a(customUnit.posX, customUnit.posY, this.nearestUnitMaxRange, customUnit, 0.0f, w);
            if (w.foundUnit == null) {
                if (this.triggerActionIfFailed != null) {
                    this.triggerActionIfFailed.a(customUnit, pointF, baseUnit, i + 1, 0);
                    return true;
                }
                return true;
            }
            BaseUnit baseUnit3 = w.foundUnit;
            a(customUnit, baseUnit3.posX, baseUnit3.posY, baseUnit3, i);
            return true;
        }
        return true;
    }
}
