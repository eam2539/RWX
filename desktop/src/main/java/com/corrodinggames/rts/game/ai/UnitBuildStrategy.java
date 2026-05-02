package com.corrodinggames.rts.game.ai;

import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.a.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/a/d.class */
public abstract class UnitBuildStrategy {

    /* JADX INFO: renamed from: b */
    String strategyName;

    /* JADX INFO: renamed from: c */
    public ArrayList<UnitBuildPriority> unitPriorities = new ArrayList();

    /* JADX INFO: renamed from: a */
    private ArrayList shuffledUnits = new ArrayList();

    /* JADX INFO: renamed from: d */
    final /* synthetic */ AIController aiController;

    /* JADX INFO: renamed from: a */
    public abstract boolean canBuildUnit(UnitType unitType);

    public UnitBuildStrategy(AIController aIController, String str) {
        this.aiController = aIController;
        this.strategyName = str;
        aIController.debugMessages.add(this);
    }

    /* JADX INFO: renamed from: b */
    public boolean hasUnitPriority(UnitType unitType) {
        Iterator it = this.unitPriorities.iterator();
        while (it.hasNext()) {
            if (((UnitBuildPriority) it.next()).unitType == unitType) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: renamed from: c */
    public float getUnitPriority(UnitType unitType) {
        return 10.0f;
    }

    /* JADX INFO: renamed from: a */
    public ArrayList getShuffledUnits() {
        Collections.shuffle(this.shuffledUnits);
        return this.shuffledUnits;
    }

    /* JADX INFO: renamed from: b */
    public void rebuildUnitMix() {
        this.unitPriorities = new ArrayList();
        float f = 0.0f;
        for (UnitType unitType : UnitTypeEnum.ae) {
            if (canBuildUnit(unitType)) {
                float unitPriority = getUnitPriority(unitType);
                f += unitPriority;
                this.unitPriorities.add(new UnitBuildPriority(this, unitType, unitPriority));
            }
        }
        this.shuffledUnits = new ArrayList(this.unitPriorities);
        if (this.unitPriorities.size() == 0) {
            GameEngine.log("AI: rebuildUnitMix: no units in unitMix:" + this.strategyName);
        }
    }

    /* JADX INFO: renamed from: c */
    public UnitType getRandomUnitType() {
        return getRandomUnitTypeWithPrice((UnitMovementType) null, -1);
    }

    /* JADX INFO: renamed from: a */
    public UnitType getRandomUnitTypeByMovement(UnitMovementType unitMovementType) {
        return getRandomUnitTypeWithPrice(unitMovementType, -1);
    }

    /* JADX INFO: renamed from: a */
    public boolean matchesMovementType(UnitType unitType, UnitMovementType unitMovementType) {
        if (unitMovementType == null) {
            return true;
        }
        UnitMovementType unitMovementTypeO = unitType.o();
        if (unitMovementTypeO == UnitMovementType.OVER_CLIFF) {
            unitMovementTypeO = UnitMovementType.LAND;
        }
        if (unitMovementTypeO == UnitMovementType.OVER_CLIFF_WATER) {
            unitMovementTypeO = UnitMovementType.HOVER;
        }
        if (unitMovementTypeO == unitMovementType) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: a */
    public UnitType getRandomUnitTypeWithPrice(UnitMovementType unitMovementType, int i) {
        if (this.unitPriorities.size() == 0) {
            GameEngine.log("AI: getRandomUnitType: no units in unitMix:" + this.strategyName);
            return null;
        }
        float f = 0.0f;
        int i2 = 0;
        for (UnitBuildPriority unitBuildPriority : this.unitPriorities) {
            if (matchesMovementType(unitBuildPriority.unitType, unitMovementType) && (i == -1 || unitBuildPriority.unitType.c() <= i)) {
                f += unitBuildPriority.priority;
                i2++;
            }
        }
        if (i2 == 0) {
            return null;
        }
        float fRandomFloatInRange = Utility.randomFloatInRange(0.0f, f);
        float f2 = 0.0f;
        for (UnitBuildPriority unitBuildPriority2 : this.unitPriorities) {
            if (matchesMovementType(unitBuildPriority2.unitType, unitMovementType) && (i == -1 || unitBuildPriority2.unitType.c() <= i)) {
                f2 += unitBuildPriority2.priority;
                if (f2 > fRandomFloatInRange) {
                    return unitBuildPriority2.unitType;
                }
            }
        }
        GameEngine.log("Did not find getRandomUnit, this should only happen very rarely, name:" + this.strategyName + " unitMix.size:" + this.unitPriorities.size() + " minPrice:" + i + " movementType:" + unitMovementType + " totalUnits:" + i2);
        return ((UnitBuildPriority) this.unitPriorities.get(this.unitPriorities.size() - 1)).unitType;
    }
}
