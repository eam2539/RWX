package com.corrodinggames.rts.gameFramework.mission.conditions;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.mission.MapTrigger;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/a/c.class */
public class UnitCountCondition extends TriggerCondition {
    /* JADX INFO: renamed from: a */
    Integer maxCount;

    /* JADX INFO: renamed from: b */
    Integer minCount;

    /* JADX INFO: renamed from: c */
    PlayerTeam teamFilter;

    /* JADX INFO: renamed from: d */
    UnitType unitTypeFilter;

    /* JADX INFO: renamed from: e */
    boolean requireBuilding;

    /* JADX INFO: renamed from: f */
    boolean requireIdle;

    /* JADX INFO: renamed from: g */
    boolean requireUpgradedBuilding;

    /* JADX INFO: renamed from: h */
    boolean requireAirUnit;

    /* JADX INFO: renamed from: i */
    boolean requireNoAttacking;

    /* JADX INFO: renamed from: j */
    boolean requireMobile;

    /* JADX INFO: renamed from: k */
    int requiredUpgradeLevel;

    /* JADX INFO: renamed from: l */
    boolean requireCanAttack;

    /* JADX INFO: renamed from: m */
    boolean requireAntiAir;

    /* JADX INFO: renamed from: n */
    boolean requireNotTransporting;

    /* JADX INFO: renamed from: o */
    AnimationTag requiredTag;

    /* JADX INFO: renamed from: p */
    boolean requireAlive;

    public static UnitCountCondition d(MapTrigger mapTrigger) throws MapLoadException {
        UnitCountCondition unitCountCondition = new UnitCountCondition();
        unitCountCondition.maxCount = mapTrigger.d("maxUnits");
        int i = 1;
        if (unitCountCondition.maxCount != null) {
            i = 0;
        }
        unitCountCondition.minCount = Integer.valueOf(mapTrigger.a("minUnits", i));
        unitCountCondition.teamFilter = mapTrigger.a();
        UnitType unitTypeByName = null;
        String strB = mapTrigger.b("unitType");
        if (strB != null) {
            unitTypeByName = UnitTypeEnum.getUnitTypeByName(strB);
            if (unitTypeByName == null) {
                mapTrigger.g("Cound not find unitType:" + strB);
            }
        }
        unitCountCondition.unitTypeFilter = unitTypeByName;
        unitCountCondition.requireBuilding = mapTrigger.a("onlybuildings", "onlyBuildings", false);
        unitCountCondition.requireUpgradedBuilding = mapTrigger.a("onlyMainBuildings", false);
        unitCountCondition.requireAirUnit = mapTrigger.a("onlyOnResourcePool", false);
        unitCountCondition.requireIdle = mapTrigger.a("onlyidle", "onlyIdle", false);
        unitCountCondition.requiredUpgradeLevel = mapTrigger.a("onlyTechLevel", -1);
        unitCountCondition.requireMobile = mapTrigger.a("onlyBuilders", false);
        unitCountCondition.requireNoAttacking = mapTrigger.a("onlyEmptyQueue", false);
        unitCountCondition.requireCanAttack = mapTrigger.a("onlyAttack", false);
        unitCountCondition.requireAntiAir = mapTrigger.a("onlyAttackAir", false);
        unitCountCondition.requireNotTransporting = mapTrigger.a("onlyIfEmpty", false);
        String strB2 = mapTrigger.b("onlyWithTag");
        if (strB2 != null && !strB2.equals(VariableScope.nullOrMissingString)) {
            try {
                unitCountCondition.requiredTag = AnimationTag.b(strB2);
            } catch (ConfigParseException e) {
                throw new MapLoadException(e.getMessage());
            }
        }
        unitCountCondition.requireAlive = mapTrigger.a("includeIncomplete", false);
        return unitCountCondition;
    }

    @Override // com.corrodinggames.rts.gameFramework.mission.conditions.TriggerCondition
    public boolean b(MapTrigger mapTrigger) {
        return e(mapTrigger);
    }

    public boolean e(MapTrigger mapTrigger) {
        int i = 0;
        BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
        int size = BaseUnit.bE.size();
        for (int i2 = 0; i2 < size; i2++) {
            BaseUnit baseUnit = baseUnitArrA[i2];
            if ((this.teamFilter == null || baseUnit.team == this.teamFilter) && (baseUnit instanceof OrderableUnit) && baseUnit.transportContainer == null && mapTrigger.a(baseUnit) && (this.unitTypeFilter == null || baseUnit.r() == this.unitTypeFilter)) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if ((this.requireAlive || baseUnit.isAlive()) && ((!this.requireCanAttack || baseUnit.canAttack()) && ((!this.requireAntiAir || (baseUnit.canAttack() && orderableUnit.canAttackFlyingUnits())) && ((!this.requireBuilding || baseUnit.bI()) && ((!this.requireUpgradedBuilding || (baseUnit.bI() && baseUnit.bJ())) && ((!this.requireAirUnit || baseUnit.r().p()) && ((!this.requireMobile || baseUnit.canMove()) && ((!this.requireIdle || orderableUnit.hasNoCurrentWaypoint()) && ((!this.requireNoAttacking || orderableUnit.a((AnimationTag) null) <= 0) && ((this.requiredUpgradeLevel == -1 || baseUnit.getUpgradeLevel() == this.requiredUpgradeLevel) && ((this.requiredTag == null || AnimationTag.a(this.requiredTag, baseUnit.getTags())) && (!this.requireNotTransporting || orderableUnit.getTransportedUnitCount() <= 0)))))))))))) {
                    i++;
                }
            }
        }
        if (i >= this.minCount.intValue()) {
            if (this.maxCount == null || i <= this.maxCount.intValue()) {
                return true;
            }
            return false;
        }
        return false;
    }
}
