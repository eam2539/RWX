package com.corrodinggames.rts.game.units.custom.condition.resources;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.LocaleString;

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
        return baseUnit.currentEnergy;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void a(BaseUnit baseUnit, double d) {
        baseUnit.currentEnergy = (float) d;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void b(BaseUnit baseUnit, double d) {
        baseUnit.currentEnergy = (float) (((double) baseUnit.currentEnergy) + d);
    }
}
