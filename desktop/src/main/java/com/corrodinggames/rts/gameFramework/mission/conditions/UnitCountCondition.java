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
    Integer a;
    Integer b;
    PlayerTeam c;
    UnitType d;
    boolean e;
    boolean f;
    boolean g;
    boolean h;
    boolean i;
    boolean j;
    int k;
    boolean l;
    boolean m;
    boolean n;
    AnimationTag o;
    boolean p;

    public static UnitCountCondition d(MapTrigger mapTrigger) throws MapLoadException {
        UnitCountCondition unitCountCondition = new UnitCountCondition();
        unitCountCondition.a = mapTrigger.d("maxUnits");
        int i = 1;
        if (unitCountCondition.a != null) {
            i = 0;
        }
        unitCountCondition.b = Integer.valueOf(mapTrigger.a("minUnits", i));
        unitCountCondition.c = mapTrigger.a();
        UnitType unitTypeByName = null;
        String strB = mapTrigger.b("unitType");
        if (strB != null) {
            unitTypeByName = UnitTypeEnum.getUnitTypeByName(strB);
            if (unitTypeByName == null) {
                mapTrigger.g("Cound not find unitType:" + strB);
            }
        }
        unitCountCondition.d = unitTypeByName;
        unitCountCondition.e = mapTrigger.a("onlybuildings", "onlyBuildings", false);
        unitCountCondition.g = mapTrigger.a("onlyMainBuildings", false);
        unitCountCondition.h = mapTrigger.a("onlyOnResourcePool", false);
        unitCountCondition.f = mapTrigger.a("onlyidle", "onlyIdle", false);
        unitCountCondition.k = mapTrigger.a("onlyTechLevel", -1);
        unitCountCondition.j = mapTrigger.a("onlyBuilders", false);
        unitCountCondition.i = mapTrigger.a("onlyEmptyQueue", false);
        unitCountCondition.l = mapTrigger.a("onlyAttack", false);
        unitCountCondition.m = mapTrigger.a("onlyAttackAir", false);
        unitCountCondition.n = mapTrigger.a("onlyIfEmpty", false);
        String strB2 = mapTrigger.b("onlyWithTag");
        if (strB2 != null && !strB2.equals(VariableScope.nullOrMissingString)) {
            try {
                unitCountCondition.o = AnimationTag.b(strB2);
            } catch (ConfigParseException e) {
                throw new MapLoadException(e.getMessage());
            }
        }
        unitCountCondition.p = mapTrigger.a("includeIncomplete", false);
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
            if ((this.c == null || baseUnit.team == this.c) && (baseUnit instanceof OrderableUnit) && baseUnit.unitTransportTarget == null && mapTrigger.a(baseUnit) && (this.d == null || baseUnit.r() == this.d)) {
                OrderableUnit orderableUnit = (OrderableUnit) baseUnit;
                if ((this.p || baseUnit.isAlive()) && ((!this.l || baseUnit.canAttack()) && ((!this.m || (baseUnit.canAttack() && orderableUnit.canAttackFlyingUnits())) && ((!this.e || baseUnit.bI()) && ((!this.g || (baseUnit.bI() && baseUnit.bJ())) && ((!this.h || baseUnit.r().p()) && ((!this.j || baseUnit.canMove()) && ((!this.f || orderableUnit.hasNoCurrentWaypoint()) && ((!this.i || orderableUnit.a((AnimationTag) null) <= 0) && ((this.k == -1 || baseUnit.getUpgradeLevel() == this.k) && ((this.o == null || AnimationTag.a(this.o, baseUnit.getUnitCombatAnimation())) && (!this.n || orderableUnit.getTransportedUnitCount() <= 0)))))))))))) {
                    i++;
                }
            }
        }
        if (i >= this.b.intValue()) {
            if (this.a == null || i <= this.a.intValue()) {
                return true;
            }
            return false;
        }
        return false;
    }
}
