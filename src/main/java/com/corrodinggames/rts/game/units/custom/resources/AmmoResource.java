package com.corrodinggames.rts.game.units.custom.resources;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.LocaleString;
import com.corrodinggames.rts.game.units.custom.e.a.AbstractResource;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e/a/b.class */
public class AmmoResource extends AbstractResource {
    public AmmoResource() {
        this.u = true;
        this.t = true;
        this.b = "ammo";
        this.c = LocaleString.replaceAll("ammo");
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public double a(BaseUnit baseUnit) {
        return baseUnit.unitLevel;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void a(BaseUnit baseUnit, double d) {
        baseUnit.unitLevel = (int) d;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void b(BaseUnit baseUnit, double d) {
        baseUnit.unitLevel = (int) (((double) baseUnit.unitLevel) + d);
    }
}
