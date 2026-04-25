package com.corrodinggames.rts.game.units.custom.resources;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.LocaleString;
import com.corrodinggames.rts.game.units.custom.e.a.AbstractResource;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e.a.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e/a/d.class */
public class EnergyResource extends AbstractResource {
    public EnergyResource() {
        this.u = true;
        this.t = true;
        this.b = "energy";
        this.c = LocaleString.replaceAll("energy");
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public double a(BaseUnit baseUnit) {
        return baseUnit.f0cB;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void a(BaseUnit baseUnit, double d) {
        baseUnit.f0cB = (float) d;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void b(BaseUnit baseUnit, double d) {
        baseUnit.f0cB = (float) (((double) baseUnit.f0cB) + d);
    }
}
