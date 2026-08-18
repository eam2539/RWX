package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.ui.GameInterfaceRenderer;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.v */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/v.class */
public class PlaceBuildingAction extends AbstractUnitAction {
    UnitType a;
    int b;

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        PlaceBuildingAction placeBuildingAction = (PlaceBuildingAction) obj;
        if (this.b != placeBuildingAction.b || this.a != placeBuildingAction.a) {
            return false;
        }
        return super.equals(obj);
    }

    public PlaceBuildingAction(UnitType unitType) {
        this(unitType, 1, null);
    }

    public PlaceBuildingAction(UnitType unitType, int i, Integer num) {
        super("b_" + unitType.v());
        this.b = 1;
        UnitType unitTypeC = CustomUnitConfig.c(unitType);
        if (unitTypeC != null) {
            unitType = unitTypeC;
            setActionId("b_" + unitType.v());
        }
        if (i != 1) {
            setActionId(getActionId() + "_" + i);
        }
        this.a = unitType;
        this.b = i;
        if (num != null) {
            this.sortOrder = num.intValue();
        }
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: i */
    public UnitType getUnitType() {
        return this.a;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: y */
    public UnitType getAttachedUnitType() {
        return this.a;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: t */
    public int getQueueSize() {
        return this.b;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        String strF = getUnitType().f();
        BaseUnit baseUnitCanAttack = BaseUnit.getPrototypeForUnitType(getUnitType());
        if (this.b != 1 && (baseUnitCanAttack instanceof OrderableUnit)) {
            ((OrderableUnit) baseUnitCanAttack).a(this.b);
        }
        String str = strF + "\n\n" + GameInterfaceRenderer.a(baseUnitCanAttack, false, false, true);
        if (this.b != 1 && (baseUnitCanAttack instanceof OrderableUnit)) {
            ((OrderableUnit) baseUnitCanAttack).a(1);
        }
        return str;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        UnitType unitType = getUnitType();
        String unitName = getUnitType().getUnitName();
        if (!(unitType instanceof CustomUnitConfig)) {
            if (getQueueSize() == 2) {
                unitName = unitName + " T-2";
            }
            if (getQueueSize() == 3) {
                unitName = unitName + " T-3";
            }
        }
        return unitName;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: c */
    public int getCostAmount() {
        return getDisplayText().a();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: B */
    public UnitPrice getDisplayText() {
        UnitPrice unitPriceA = this.unitAction.a();
        if (unitPriceA != null) {
            return unitPriceA;
        }
        return getUnitType().d(getQueueSize());
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: r_ */
    public UnitPrice getAdditionalCost() {
        UnitPrice unitPriceB = this.unitAction.b();
        if (unitPriceB != null) {
            return unitPriceB;
        }
        return getUnitType().B();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public int getActiveCount(BaseUnit baseUnit, boolean z) {
        return -1;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public ActionType getActionType() {
        return ActionType.placeBuilding;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType getActionDisplayType() {
        return ActionDisplayType.building;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n_ */
    public boolean isQueuable() {
        return !getUnitType().isAvailableInDemo();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isNotAvailable(BaseUnit baseUnit) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (((getUnitType() == UnitTypeEnum.AntiNukeLaucher || getUnitType() == UnitTypeEnum.NukeLaucher) && gameEngine.isInGameOrLobby() && gameEngine.networkEngine.roomSettings.noNukes) || getUnitType().w()) {
            return true;
        }
        return super.isNotAvailable(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: u */
    public boolean isGuiBlinking() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: D */
    public boolean shouldShowUnitPreview() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: p */
    public float getProgress(BaseUnit baseUnit) {
        BaseUnit currentRepairOrReclaimTarget;
        if ((baseUnit instanceof OrderableUnit) && (currentRepairOrReclaimTarget = ((OrderableUnit) baseUnit).getCurrentRepairOrReclaimTarget()) != null && currentRepairOrReclaimTarget.buildProgress < 1.0f && currentRepairOrReclaimTarget.r() == getUnitType()) {
            return currentRepairOrReclaimTarget.buildProgress;
        }
        return -1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: r */
    public boolean isAvailable(BaseUnit baseUnit) {
        return this.unitAction.a(baseUnit, true);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    public boolean b(BaseUnit baseUnit) {
        return this.unitAction.a(baseUnit, false);
    }
}
