package com.corrodinggames.rts.game.units.custom.logic.actions;

import android.graphics.PointF;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.*;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.game.units.g.SpecialActionBlockEffect;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.Vector3D;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/j.class */
public class UnitStatsAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    boolean deleteSelf;

    /* JADX INFO: renamed from: b */
    boolean switchToNeutralTeam;

    /* JADX INFO: renamed from: c */
    boolean switchToAggressiveTeam;

    /* JADX INFO: renamed from: d */
    LogicBoolean switchToTeam;

    /* JADX INFO: renamed from: e */
    LogicBoolean setBodyRotation;

    /* JADX INFO: renamed from: f */
    LogicBoolean setHeight;

    /* JADX INFO: renamed from: g */
    LogicBoolean teleportTo;

    /* JADX INFO: renamed from: h */
    boolean clearAllActionCooldowns;

    /* JADX INFO: renamed from: i */
    float addAllActionCooldownsTime;

    /* JADX INFO: renamed from: j */
    float addActionCooldownTime;

    /* JADX INFO: renamed from: k */
    CustomUnitActionHandler addActionCooldownApplyToActions;

    /* JADX INFO: renamed from: l */
    boolean removeAllQueuedItemsWithoutRefund;

    /* JADX INFO: renamed from: m */
    boolean refundAllQueuedItems;

    /* JADX INFO: renamed from: n */
    float setBuilt = -1.0f;

    /* JADX INFO: renamed from: o */
    Vector3D offsetSelfAbsolute;

    /* JADX INFO: renamed from: p */
    boolean resetUnitStats;

    /* JADX INFO: renamed from: q */
    VariableScope.CachedWriter setUnitStats;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) throws ConfigParseException {
        boolean zBooleanValue = iniFile.getBoolean(str, str2 + "resetUnitStats", (Boolean) false).booleanValue();
        String string = iniFile.getString(str, str2 + "setUnitStats", (String) null);
        if (zBooleanValue || string != null) {
            UnitStatsAction unitStatsAction = new UnitStatsAction();
            unitStatsAction.resetUnitStats = zBooleanValue;
            if (string != null) {
                unitStatsAction.setUnitStats = UnitStats.a(string, customUnitConfig, str, str2 + "setUnitStats");
            }
            customActionDef.ac.add(unitStatsAction);
        }
        boolean zBooleanValue2 = iniFile.getBoolean(str, str2 + "deleteSelf", (Boolean) false).booleanValue();
        if (zBooleanValue2) {
            UnitStatsAction unitStatsAction2 = new UnitStatsAction();
            unitStatsAction2.deleteSelf = zBooleanValue2;
            customActionDef.ac.add(unitStatsAction2);
        }
        boolean zBooleanValue3 = iniFile.getBoolean(str, str2 + "switchToNeutralTeam", (Boolean) false).booleanValue();
        boolean zBooleanValue4 = iniFile.getBoolean(str, str2 + "switchToAggressiveTeam", (Boolean) false).booleanValue();
        LogicBoolean logicBooleanWithReturnType = iniFile.getLogicBooleanWithReturnType(customUnitConfig, str, str2 + "switchToTeam", (LogicBoolean) null, LogicBoolean.ReturnType.number);
        if (zBooleanValue3 || zBooleanValue4 || logicBooleanWithReturnType != null) {
            UnitStatsAction unitStatsAction3 = new UnitStatsAction();
            unitStatsAction3.switchToNeutralTeam = zBooleanValue3;
            unitStatsAction3.switchToAggressiveTeam = zBooleanValue4;
            unitStatsAction3.switchToTeam = logicBooleanWithReturnType;
            customActionDef.ac.add(unitStatsAction3);
        }
        LogicBoolean logicBooleanNumber = iniFile.getLogicBooleanNumber(customUnitConfig, str, str2 + "setBodyRotation", null);
        if (logicBooleanNumber != null) {
            UnitStatsAction unitStatsAction4 = new UnitStatsAction();
            unitStatsAction4.setBodyRotation = logicBooleanNumber;
            customActionDef.ac.add(unitStatsAction4);
        }
        LogicBoolean logicBooleanNumber2 = iniFile.getLogicBooleanNumber(customUnitConfig, str, str2 + "setHeight", null);
        if (logicBooleanNumber2 != null) {
            UnitStatsAction unitStatsAction5 = new UnitStatsAction();
            unitStatsAction5.setHeight = logicBooleanNumber2;
            customActionDef.ac.add(unitStatsAction5);
        }
        LogicBoolean logicBoolean = iniFile.getInt(customUnitConfig, str, str2 + "teleportTo", null);
        if (logicBoolean != null) {
            UnitStatsAction unitStatsAction6 = new UnitStatsAction();
            unitStatsAction6.teleportTo = logicBoolean;
            customActionDef.ac.add(unitStatsAction6);
        }
        float fFloatValue = iniFile.getFloat(str, str2 + "setBuilt", Float.valueOf(-1.0f)).floatValue();
        if (fFloatValue > 1.0f) {
            throw new ConfigParseException("[" + str + "] setBuilt cannot be greater than 1");
        }
        boolean zBooleanValue5 = iniFile.getBoolean(str, str2 + "clearAllActionCooldowns", (Boolean) false).booleanValue();
        float fFloatValue2 = iniFile.getTimeAsFrames(str, str2 + "addAllActionCooldownsTime", Float.valueOf(0.0f)).floatValue();
        if (fFloatValue2 == 0.0f) {
            fFloatValue2 = iniFile.getTimeAsFrames(str, str2 + "addAllActionCooldownsFor", Float.valueOf(0.0f)).floatValue();
        }
        float fFloatValue3 = iniFile.getTimeAsFrames(str, str2 + "addActionCooldownTime", Float.valueOf(0.0f)).floatValue();
        if (fFloatValue3 == 0.0f) {
            fFloatValue3 = iniFile.getTimeAsFrames(str, str2 + "addActionCooldownFor", Float.valueOf(0.0f)).floatValue();
        }
        CustomUnitActionHandler customUnitAction = iniFile.getCustomUnitAction(customUnitConfig, str, str2 + "addActionCooldownApplyToActions", (CustomUnitActionHandler) null);
        Vector3D resourceOrAsset = iniFile.getResourceOrAsset(str, str2 + "offsetSelfAbsolute", (Vector3D) null);
        if (customUnitAction != null && fFloatValue3 <= 0.0f) {
            throw new ConfigParseException("[" + str + "]addActionCooldownApplyToActions requires addActionCooldownTime to be set");
        }
        boolean zBooleanValue6 = iniFile.getBoolean(str, str2 + "removeAllQueuedItemsWithoutRefund", (Boolean) false).booleanValue();
        boolean zBooleanValue7 = iniFile.getBoolean(str, str2 + "refundAllQueuedItems", (Boolean) false).booleanValue();
        if (zBooleanValue6 && zBooleanValue7) {
            throw new ConfigParseException("[" + str + "]Cannot set removeAllQueuedActionsWithoutRefund and refundAllQueuedActions at the same time, pick one.");
        }
        if (fFloatValue3 > 0.0f || fFloatValue2 > 0.0f || zBooleanValue5 || fFloatValue >= 0.0f || resourceOrAsset != null || zBooleanValue6 || zBooleanValue7) {
            UnitStatsAction unitStatsAction7 = new UnitStatsAction();
            unitStatsAction7.clearAllActionCooldowns = zBooleanValue5;
            unitStatsAction7.addAllActionCooldownsTime = fFloatValue2;
            unitStatsAction7.addActionCooldownTime = fFloatValue3;
            unitStatsAction7.addActionCooldownApplyToActions = customUnitAction;
            unitStatsAction7.setBuilt = fFloatValue;
            unitStatsAction7.offsetSelfAbsolute = resourceOrAsset;
            unitStatsAction7.removeAllQueuedItemsWithoutRefund = zBooleanValue6;
            unitStatsAction7.refundAllQueuedItems = zBooleanValue7;
            customActionDef.ac.add(unitStatsAction7);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        BaseUnit unit;
        PlayerTeam playerTeamK;
        if (this.resetUnitStats) {
            customUnit.y = customUnit.unitConfig.unitStats;
            customUnit.maxHealth = customUnit.y.maxHp;
            if (customUnit.currentHealth > customUnit.maxHealth) {
                customUnit.o(customUnit.maxHealth);
            }
            customUnit.unitEnergyMax = customUnit.y.maxShield;
            if (customUnit.shield > customUnit.unitEnergyMax) {
                customUnit.shield = customUnit.unitEnergyMax;
            }
        }
        if (this.setUnitStats != null) {
            this.setUnitStats.writeToUnit(customUnit);
            UnitPrice.d(customUnit);
        }
        if (this.deleteSelf) {
            customUnit.getUnitAICondition();
            if (customUnit.bI()) {
                GameEngine.getInstance().pathfindingEngine.a(customUnit);
            }
        }
        if (this.switchToNeutralTeam) {
            customUnit.isSelectable(PlayerTeam.TEAM_ALL);
        }
        if (this.switchToAggressiveTeam) {
            customUnit.isSelectable(PlayerTeam.TEAM_UNKNOWN);
        }
        if (this.switchToTeam != null && (playerTeamK = PlayerTeam.k((int) this.switchToTeam.readNumber(customUnit))) != null) {
            customUnit.isSelectable(playerTeamK);
        }
        if (this.setBodyRotation != null) {
            customUnit.h(this.setBodyRotation.readNumber(customUnit));
        }
        if (this.setHeight != null) {
            customUnit.posZ = this.setHeight.readNumber(customUnit);
        }
        if (this.teleportTo != null && (unit = this.teleportTo.readUnit(customUnit)) != null) {
            customUnit.f(unit.posX, unit.posY);
        }
        if (this.clearAllActionCooldowns) {
            SpecialActionBlockEffect.c(customUnit, AbstractUnitAction.NONE_ACTION_ID);
        }
        if (this.removeAllQueuedItemsWithoutRefund) {
            customUnit.i(false);
        }
        if (this.refundAllQueuedItems) {
            customUnit.i(true);
        }
        if (this.addAllActionCooldownsTime > 0.0f) {
            SpecialActionBlockEffect.a(customUnit, AbstractUnitAction.NONE_ACTION_ID, (int) this.addAllActionCooldownsTime);
        }
        if (this.addActionCooldownTime > 0.0f) {
            if (this.addActionCooldownApplyToActions == null) {
                SpecialActionBlockEffect.a(customUnit, abstractUnitAction.getActionId(), (int) this.addActionCooldownTime);
            } else {
                Iterator it = this.addActionCooldownApplyToActions.a().iterator();
                while (it.hasNext()) {
                    SpecialActionBlockEffect.a(customUnit, ((AbstractUnitAction) it.next()).getActionId(), (int) this.addActionCooldownTime);
                }
            }
        }
        if (this.setBuilt >= 0.0f) {
            customUnit.r(this.setBuilt);
            customUnit.movementAngle = this.setBuilt;
        }
        if (this.offsetSelfAbsolute != null) {
            customUnit.b(customUnit.posX + this.offsetSelfAbsolute.a, customUnit.posY + this.offsetSelfAbsolute.b);
            customUnit.posZ += this.offsetSelfAbsolute.c;
            customUnit.isRepairing = true;
            return true;
        }
        return true;
    }
}
