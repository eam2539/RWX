package com.corrodinggames.rts.game.units.custom.logic.actions;

import android.graphics.PointF;
import com.corrodinggames.rts.game.TeamRelation;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitActionHandler;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/m.class */
public class TakeResourcesAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    public UnitPrice takeResources;

    /* JADX INFO: renamed from: b */
    public boolean takeResources_includeUnitsInTransport;

    /* JADX INFO: renamed from: c */
    public boolean takeResources_includeParent;

    /* JADX INFO: renamed from: e */
    public TeamRelation takeResources_includeUnitsWithinRange_team;

    /* JADX INFO: renamed from: f */
    public LogicBoolean takeResources_includeReference;

    /* JADX INFO: renamed from: g */
    public AnimationSet takeResources_excludeUnitsWithoutTags;

    /* JADX INFO: renamed from: i */
    public UnitPrice takeResources_excludeUnitsWithTheseResources;

    /* JADX INFO: renamed from: j */
    public UnitReference takeResources_excludeUnitsWithoutCustomTarget1EqualTo;

    /* JADX INFO: renamed from: l */
    public CustomUnitActionHandler takeResources_triggerActionIfAnyCollected;

    /* JADX INFO: renamed from: m */
    public CustomUnitActionHandler takeResources_triggerActionIfNoneCollected;

    /* JADX INFO: renamed from: n */
    public CustomUnitActionHandler takeResources_triggerActionForEach;

    /* JADX INFO: renamed from: o */
    public boolean takeResources_saveFirstUnitToCustomTarget1;

    /* JADX INFO: renamed from: p */
    public boolean takeResources_saveFirstUnitToCustomTarget2;

    /* JADX INFO: renamed from: q */
    public boolean takeResources_discardCollected;

    /* JADX INFO: renamed from: r */
    public boolean takeResources_keepResourcesOnTarget;
    public static final FastArrayList t = new FastArrayList();
    public static final ResourceUnitSearchCallback u = new ResourceUnitSearchCallback();

    /* JADX INFO: renamed from: d */
    public float takeResources_includeUnitsWithinRange = -1.0f;

    /* JADX INFO: renamed from: h */
    public boolean takeResources_excludeUnitsWithoutAllResources = true;

    /* JADX INFO: renamed from: k */
    public int takeResources_maxUnits = 1;

    /* JADX INFO: renamed from: s */
    public boolean takeResources_directTransferStoppingAtZero = false;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) throws ConfigParseException {
        boolean zBooleanValue = iniFile.getBoolean(str, str2 + "takeResources_includeUnitsInTransport", (Boolean) false).booleanValue();
        boolean zBooleanValue2 = iniFile.getBoolean(str, str2 + "takeResources_includeParent", (Boolean) false).booleanValue();
        LogicBoolean logicBoolean = iniFile.getInt(customUnitConfig, str, str2 + "takeResources_includeReference", null);
        float fFloatValue = iniFile.getFloat(str, str2 + "takeResources_includeUnitsWithinRange", Float.valueOf(-1.0f)).floatValue();
        boolean zBooleanValue3 = iniFile.getBoolean(str, str2 + "takeResources_searchOnly", (Boolean) false).booleanValue();
        UnitPrice unitPriceA = UnitPrice.a(customUnitConfig, iniFile, str, str2 + "takeResources", true);
        if (zBooleanValue3 && unitPriceA != null && !unitPriceA.c()) {
            throw new ConfigParseException("[" + str + "]takeResources not supported with takeResources_searchOnly");
        }
        boolean zBooleanValue4 = iniFile.getBoolean(str, str2 + "takeResources_saveFirstUnitToCustomTarget1", (Boolean) false).booleanValue();
        boolean zBooleanValue5 = iniFile.getBoolean(str, str2 + "takeResources_saveFirstUnitToCustomTarget2", (Boolean) false).booleanValue();
        if (!zBooleanValue && !zBooleanValue2 && fFloatValue < 0.0f && logicBoolean == null) {
            if (unitPriceA != null && !unitPriceA.c()) {
                throw new ConfigParseException("[" + str + "]takeResources requires an include (eg: takeResources_includeUnitsWithinRange, takeResources_includeUnitsInTransport) otherwise no results would be found");
            }
            if (zBooleanValue3) {
                throw new ConfigParseException("[" + str + "]takeResources_searchOnly requires an include (eg: takeResources_includeUnitsWithinRange) otherwise no results would be found");
            }
            return;
        }
        TakeResourcesAction takeResourcesAction = new TakeResourcesAction();
        customActionDef.ac.add(takeResourcesAction);
        takeResourcesAction.takeResources_includeUnitsInTransport = zBooleanValue;
        takeResourcesAction.takeResources_includeUnitsWithinRange = fFloatValue;
        takeResourcesAction.takeResources_includeParent = zBooleanValue2;
        takeResourcesAction.takeResources_includeReference = logicBoolean;
        takeResourcesAction.takeResources = unitPriceA;
        takeResourcesAction.takeResources_directTransferStoppingAtZero = iniFile.getBoolean(str, str2 + "takeResources_directTransferStoppingAtZero", Boolean.valueOf(takeResourcesAction.takeResources_directTransferStoppingAtZero)).booleanValue();
        takeResourcesAction.takeResources_includeUnitsWithinRange_team = (TeamRelation) iniFile.getEnum(str, "takeResources_includeUnitsWithinRange_team", TeamRelation.own, TeamRelation.class);
        takeResourcesAction.takeResources_excludeUnitsWithoutTags = iniFile.getAnimationSet(customUnitConfig, str, str2 + "takeResources_excludeUnitsWithoutTags", (AnimationSet) null);
        takeResourcesAction.takeResources_excludeUnitsWithoutCustomTarget1EqualTo = UnitReference.parseUnitReferenceFromConf(customUnitConfig, iniFile, str, str2 + "takeResources_excludeUnitsWithoutCustomTarget1EqualTo", null);
        if (takeResourcesAction.takeResources_directTransferStoppingAtZero) {
            takeResourcesAction.takeResources_excludeUnitsWithoutAllResources = false;
        }
        takeResourcesAction.takeResources_excludeUnitsWithoutAllResources = iniFile.getBoolean(str, str2 + "takeResources_excludeUnitsWithoutAllResources", Boolean.valueOf(takeResourcesAction.takeResources_excludeUnitsWithoutAllResources)).booleanValue();
        if (zBooleanValue3) {
            takeResourcesAction.takeResources_maxUnits = 200;
            takeResourcesAction.takeResources_discardCollected = true;
            takeResourcesAction.takeResources_keepResourcesOnTarget = true;
        }
        takeResourcesAction.takeResources_maxUnits = iniFile.getLogicBooleanUnit(str, str2 + "takeResources_maxUnits", Integer.valueOf(takeResourcesAction.takeResources_maxUnits)).intValue();
        takeResourcesAction.takeResources_triggerActionIfAnyCollected = iniFile.getCustomUnitAction(customUnitConfig, str, str2 + "takeResources_triggerActionIfAnyCollected", (CustomUnitActionHandler) null);
        takeResourcesAction.takeResources_triggerActionIfNoneCollected = iniFile.getCustomUnitAction(customUnitConfig, str, str2 + "takeResources_triggerActionIfNoneCollected", (CustomUnitActionHandler) null);
        takeResourcesAction.takeResources_triggerActionForEach = iniFile.getCustomUnitAction(customUnitConfig, str, str2 + "takeResources_triggerActionForEach", (CustomUnitActionHandler) null);
        takeResourcesAction.takeResources_saveFirstUnitToCustomTarget1 = zBooleanValue4;
        takeResourcesAction.takeResources_saveFirstUnitToCustomTarget2 = zBooleanValue5;
        takeResourcesAction.takeResources_discardCollected = iniFile.getBoolean(str, str2 + "takeResources_discardCollected", Boolean.valueOf(takeResourcesAction.takeResources_discardCollected)).booleanValue();
        takeResourcesAction.takeResources_keepResourcesOnTarget = iniFile.getBoolean(str, str2 + "takeResources_keepResourcesOnTarget", Boolean.valueOf(takeResourcesAction.takeResources_keepResourcesOnTarget)).booleanValue();
        if (zBooleanValue3 && (!takeResourcesAction.takeResources_discardCollected || !takeResourcesAction.takeResources_keepResourcesOnTarget)) {
            throw new ConfigParseException("[" + str + "]takeResources_searchOnly shortcut expects takeResources_discardCollected and takeResources_keepResourcesOnTarget to be true");
        }
        takeResourcesAction.takeResources_excludeUnitsWithTheseResources = UnitPrice.a(customUnitConfig, iniFile, str, str2 + "takeResources_excludeUnitsWithTheseResources", true);
        if (takeResourcesAction.takeResources_excludeUnitsWithTheseResources != null && takeResourcesAction.takeResources_excludeUnitsWithTheseResources.c()) {
            takeResourcesAction.takeResources_excludeUnitsWithTheseResources = null;
        }
        if (takeResourcesAction.takeResources_directTransferStoppingAtZero) {
            if (takeResourcesAction.takeResources.e()) {
                throw new ConfigParseException("[" + str + "]takeResources_directTransferStoppingAtZero:true only supports custom resources right now");
            }
            if (takeResourcesAction.takeResources_excludeUnitsWithoutAllResources) {
                throw new ConfigParseException("[" + str + "]takeResources_directTransferStoppingAtZero:true is not supported at the same time as takeResources_excludeUnitsWithoutAllResources:true");
            }
            if (takeResourcesAction.takeResources_keepResourcesOnTarget) {
                throw new ConfigParseException("[" + str + "]takeResources_directTransferStoppingAtZero:true is not supported at the same time as takeResources_keepResourcesOnTarget:true");
            }
            if (takeResourcesAction.takeResources_discardCollected) {
                throw new ConfigParseException("[" + str + "]takeResources_directTransferStoppingAtZero:true is not supported at the same time as takeResources_discardCollected:true");
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        BaseUnit unit;
        FastArrayList fastArrayList = t;
        fastArrayList.clear();
        if (this.takeResources_includeUnitsInTransport) {
            for (BaseUnit baseUnit2 : customUnit.transportedUnits) {
                if (baseUnit2 != null && !baseUnit2.isDead) {
                    fastArrayList.add(baseUnit2);
                }
            }
        }
        if (this.takeResources_includeParent && (customUnit.parentEntity != null || customUnit.unitTransportTarget != null)) {
            fastArrayList.add(customUnit.parentEntity);
        }
        if (this.takeResources_includeReference != null && (unit = this.takeResources_includeReference.readUnit(customUnit)) != null && !unit.isDead) {
            fastArrayList.add(unit);
        }
        if (this.takeResources_includeUnitsWithinRange > 0.0f) {
            u.rangeSq = this.takeResources_includeUnitsWithinRange * this.takeResources_includeUnitsWithinRange;
            u.tags = this.takeResources_excludeUnitsWithoutTags;
            u.includeDead = true;
            u.teamFilter = this.takeResources_includeUnitsWithinRange_team;
            u.foundUnits = fastArrayList;
            GameEngine.getInstance().unitSpatialIndex.a(customUnit.posX, customUnit.posY, this.takeResources_includeUnitsWithinRange, customUnit, 0.0f, u);
        }
        if (this.takeResources_excludeUnitsWithoutTags != null) {
            for (int size = fastArrayList.size() - 1; size >= 0; size--) {
                if (!AnimationTag.a(this.takeResources_excludeUnitsWithoutTags, ((BaseUnit) fastArrayList.get(size)).getUnitCombatAnimation())) {
                    fastArrayList.remove(size);
                }
            }
        }
        if (this.takeResources_excludeUnitsWithoutAllResources) {
            for (int size2 = fastArrayList.size() - 1; size2 >= 0; size2--) {
                if (!this.takeResources.b((BaseUnit) fastArrayList.get(size2))) {
                    fastArrayList.remove(size2);
                }
            }
        }
        if (this.takeResources_excludeUnitsWithTheseResources != null) {
            for (int size3 = fastArrayList.size() - 1; size3 >= 0; size3--) {
                if (this.takeResources_excludeUnitsWithTheseResources.b((BaseUnit) fastArrayList.get(size3))) {
                    fastArrayList.remove(size3);
                }
            }
        }
        if (this.takeResources_excludeUnitsWithoutCustomTarget1EqualTo != null) {
            BaseUnit baseUnit3 = this.takeResources_excludeUnitsWithoutCustomTarget1EqualTo.get((OrderableUnit) customUnit);
            for (int size4 = fastArrayList.size() - 1; size4 >= 0; size4--) {
                if (((BaseUnit) fastArrayList.get(size4)).unitTarget2 != baseUnit3) {
                    fastArrayList.remove(size4);
                }
            }
        }
        if (this.takeResources_triggerActionForEach != null) {
            fastArrayList = new FastArrayList(fastArrayList);
        }
        boolean z = false;
        BaseUnit baseUnit4 = null;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < fastArrayList.size(); i4++) {
            BaseUnit baseUnit5 = (BaseUnit) fastArrayList.get(i4);
            if (baseUnit4 == null) {
                baseUnit4 = baseUnit5;
            }
            if (this.takeResources_directTransferStoppingAtZero) {
                if (!this.takeResources.a(baseUnit5, customUnit)) {
                    continue;
                }
            } else {
                if (!this.takeResources_keepResourcesOnTarget) {
                    this.takeResources.a(baseUnit5);
                }
                if (!this.takeResources_discardCollected) {
                    this.takeResources.h(customUnit);
                }
            }
            if (this.takeResources_triggerActionForEach != null && baseUnit5 != null) {
                this.takeResources_triggerActionForEach.a(customUnit, new PointF(baseUnit5.posX, baseUnit5.posY), baseUnit5, i + 1, i3);
                i3++;
            }
            z = true;
            i2++;
            if (i2 >= this.takeResources_maxUnits) {
                break;
            }
        }
        if (baseUnit4 != null) {
            if (this.takeResources_saveFirstUnitToCustomTarget1) {
                customUnit.unitTarget2 = baseUnit4;
            }
            if (this.takeResources_saveFirstUnitToCustomTarget2) {
                customUnit.unitTarget3 = baseUnit4;
            }
        }
        if (z) {
            if (this.takeResources_triggerActionIfAnyCollected != null) {
                this.takeResources_triggerActionIfAnyCollected.a(customUnit, pointF, baseUnit, i + 1, 0);
            }
        } else if (this.takeResources_triggerActionIfNoneCollected != null) {
            this.takeResources_triggerActionIfNoneCollected.a(customUnit, pointF, baseUnit, i + 1, 0);
        }
        fastArrayList.clear();
        return true;
    }
}
