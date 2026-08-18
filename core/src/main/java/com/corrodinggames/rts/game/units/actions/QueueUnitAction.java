package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.ui.GameInterfaceRenderer;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/l.class */
public class QueueUnitAction extends PopupQueueAction {
    UnitType a;

    public QueueUnitAction(UnitType unitType) {
        this(unitType, -999.0f);
    }

    public QueueUnitAction(UnitType unitType, float f) {
        super("u_" + unitType.v());
        UnitType unitTypeC = CustomUnitConfig.c(unitType);
        if (unitTypeC != null) {
            unitType = unitTypeC;
            setActionId("u_" + unitType.v());
        }
        this.sortOrder = f;
        this.a = unitType;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: a */
    public String getDescription() {
        return this.a.f() + "\n\n" + GameInterfaceRenderer.a(BaseUnit.getPrototypeForUnitType(this.a), false, false, true);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: b */
    public String getDisplayName() {
        return this.a.getUnitName();
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
        return this.a.u();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: r_ */
    public UnitPrice getAdditionalCost() {
        UnitPrice unitPriceB = this.unitAction.b();
        if (unitPriceB != null) {
            return unitPriceB;
        }
        return this.a.B();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: i */
    public UnitType getUnitType() {
        return this.a;
    }

    @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction
    public float K() {
        return this.a.D();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: f */
    public ActionDisplayType getActionDisplayType() {
        return ActionDisplayType.queueUnit;
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: n_ */
    public boolean isQueuable() {
        return !this.a.isAvailableInDemo();
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isNotAvailable(BaseUnit baseUnit) {
        if (getUnitType().w()) {
            return true;
        }
        return super.isNotAvailable(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
    /* JADX INFO: renamed from: g */
    public boolean isHighPriority() {
        return true;
    }
}
