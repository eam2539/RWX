package com.corrodinggames.rts.game.units.custom.logic.actions;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.*;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import io.github.rwx.geometry.PointF;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/o.class */
public class TransportAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    public UnitSpawner addUnitsIntoTransport;

    /* JADX INFO: renamed from: b */
    public int deleteNumUnitsFromTransport;

    /* JADX INFO: renamed from: c */
    public AnimationSet deleteNumUnitsFromTransport_onlyWithTags;

    /* JADX INFO: renamed from: d */
    public boolean startUnloadingTransport;

    /* JADX INFO: renamed from: e */
    public boolean forceUnloadTransportNow;

    /* JADX INFO: renamed from: f */
    public int forceUnloadTransportNow_onlyOnSlot = -1;

    /* JADX INFO: renamed from: g */
    public LogicBoolean transportTargetNow;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) throws ConfigParseException {
        UnitSpawner unitSpawnerA = UnitSpawner.a(customUnitConfig, iniFile, str, str2 + "addUnitsIntoTransport");
        int iIntValue = iniFile.getInt(str, str2 + "deleteNumUnitsFromTransport", (Integer) 0).intValue();
        AnimationSet animationSetA = AnimationTag.a(iniFile.getString(str, "deleteNumUnitsFromTransport_onlyWithTags", (String) null), (AnimationSet) null);
        boolean zBooleanValue = iniFile.getBoolean(str, str2 + "startUnloadingTransport", (Boolean) false).booleanValue();
        boolean zBooleanValue2 = iniFile.getBoolean(str, str2 + "forceUnloadTransportNow", (Boolean) false).booleanValue();
        int iIntValue2 = iniFile.getInt(str, str2 + "forceUnloadTransportNow_onlyOnSlot", (Integer) (-1)).intValue();
        LogicBoolean logicBoolean = iniFile.getInt(customUnitConfig, str, str2 + "transportTargetNow", null);
        if (iIntValue2 != -1 && !zBooleanValue2) {
            throw new ConfigParseException("forceUnloadTransportNow_onlyOnSlot expects forceUnloadTransportNow");
        }
        if (!unitSpawnerA.b() || iIntValue > 0 || zBooleanValue || zBooleanValue2 || logicBoolean != null) {
            TransportAction transportAction = new TransportAction();
            if (!unitSpawnerA.b()) {
                transportAction.addUnitsIntoTransport = unitSpawnerA;
            }
            if (iIntValue > 0) {
                transportAction.deleteNumUnitsFromTransport = iIntValue;
                transportAction.deleteNumUnitsFromTransport_onlyWithTags = animationSetA;
            }
            transportAction.startUnloadingTransport = zBooleanValue;
            transportAction.forceUnloadTransportNow = zBooleanValue2;
            transportAction.forceUnloadTransportNow_onlyOnSlot = iIntValue2;
            transportAction.transportTargetNow = logicBoolean;
            customActionDef.logicActions.add(transportAction);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        BaseUnit unit;
        if (this.deleteNumUnitsFromTransport != 0) {
            for (int i2 = 0; i2 < this.deleteNumUnitsFromTransport; i2++) {
                if (customUnit.transportedUnits.size() > 0) {
                    for (int size = customUnit.transportedUnits.size() - 1; size >= 0; size--) {
                        BaseUnit baseUnit2 = (BaseUnit) customUnit.transportedUnits.get(size);
                        if (baseUnit2 == null) {
                            GameEngine.logColored("deleteNumUnitsFromTransport unit==null");
                        } else if (this.deleteNumUnitsFromTransport_onlyWithTags == null || AnimationTag.a(this.deleteNumUnitsFromTransport_onlyWithTags, baseUnit2.getTags())) {
                            customUnit.transportedUnits.remove(size);
                            customUnit.unloadTransportedUnit(baseUnit2);
                            if (baseUnit2 != null) {
                                baseUnit2.removeFromGame();
                            }
                        }
                    }
                }
            }
        }
        if (this.addUnitsIntoTransport != null) {
            FastArrayList<BaseUnit> fastArrayList = new FastArrayList();
            this.addUnitsIntoTransport.a(fastArrayList, customUnit.team, (BaseUnit) customUnit, false);
            for (BaseUnit baseUnit3 : fastArrayList) {
                baseUnit3.posX = customUnit.posX;
                baseUnit3.posY = customUnit.posY;
                baseUnit3.posZ = customUnit.posZ;
                customUnit.loadTransportedUnit(baseUnit3);
            }
        }
        if (this.startUnloadingTransport) {
            customUnit.startUnloading();
        }
        if (this.forceUnloadTransportNow) {
            for (int size2 = customUnit.transportedUnits.size() - 1; size2 >= 0; size2--) {
                if (this.forceUnloadTransportNow_onlyOnSlot == -1 || this.forceUnloadTransportNow_onlyOnSlot == size2) {
                    customUnit.canAttackTargetUnit((BaseUnit) customUnit.transportedUnits.get(size2), true, customUnit.transportedUnits.size() % 2 == 0);
                }
            }
        }
        if (this.transportTargetNow != null && (unit = this.transportTargetNow.readUnit(customUnit)) != null && unit.isHighlighted && customUnit.d(unit, true)) {
            customUnit.loadTransportedUnit(customUnit);
            return true;
        }
        return true;
    }
}
