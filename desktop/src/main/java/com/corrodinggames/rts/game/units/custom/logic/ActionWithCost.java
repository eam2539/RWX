package com.corrodinggames.rts.game.units.custom.logic;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitAction;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/b.class */
public class ActionWithCost extends UnitAction {

    /* JADX INFO: renamed from: b */
    public UnitAction wrappedAction;

    /* JADX INFO: renamed from: c */
    public UnitPrice buildCost;

    /* JADX INFO: renamed from: d */
    public UnitPrice resourceCost;

    public ActionWithCost(UnitAction unitAction) {
        this.wrappedAction = unitAction;
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public boolean b(BaseUnit baseUnit) {
        return this.wrappedAction.b(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public String c(BaseUnit baseUnit) {
        return this.wrappedAction.c(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public boolean a(BaseUnit baseUnit, boolean z) {
        return this.wrappedAction.a(baseUnit, z);
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public boolean d(BaseUnit baseUnit) {
        return this.wrappedAction.d(baseUnit);
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public UnitPrice a() {
        if (this.buildCost != null) {
            return this.buildCost;
        }
        return this.wrappedAction.a();
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public UnitPrice b() {
        if (this.resourceCost != null) {
            return this.resourceCost;
        }
        return this.wrappedAction.b();
    }

    @Override // com.corrodinggames.rts.game.units.UnitAction
    public void a(BaseUnit baseUnit, BaseUnit baseUnit2) {
        this.wrappedAction.a(baseUnit, baseUnit2);
    }
}
