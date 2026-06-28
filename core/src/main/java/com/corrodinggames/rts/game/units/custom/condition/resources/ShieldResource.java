package com.corrodinggames.rts.game.units.custom.condition.resources;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.LocaleString;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e.a.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e/a/f.class */
public class ShieldResource extends AbstractResource {
    public ShieldResource() {
        this.u = true;
        this.t = true;
        this.b = "shield";
        this.c = LocaleString.replaceAll("shield");
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public double a(BaseUnit baseUnit) {
        return baseUnit.shield;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void a(BaseUnit baseUnit, double d) {
        baseUnit.shield = (float) d;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void b(BaseUnit baseUnit, double d) {
        baseUnit.shield = (float) (((double) baseUnit.shield) + d);
    }
}
