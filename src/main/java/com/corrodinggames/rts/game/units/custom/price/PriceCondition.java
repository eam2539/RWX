package com.corrodinggames.rts.game.units.custom.price;

import com.corrodinggames.rts.game.units.BaseUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.d.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/d/a.class */
public abstract class PriceCondition {
    public abstract void a(BaseUnit baseUnit);

    public abstract boolean b(BaseUnit baseUnit);

    public abstract void a(BaseUnit baseUnit, double d);

    public abstract boolean b(BaseUnit baseUnit, double d);

    public boolean c(BaseUnit baseUnit) {
        if (b(baseUnit)) {
            a(baseUnit);
            return true;
        }
        return false;
    }

    public boolean c(BaseUnit baseUnit, double d) {
        if (b(baseUnit, d)) {
            a(baseUnit, d);
            return true;
        }
        return false;
    }
}
