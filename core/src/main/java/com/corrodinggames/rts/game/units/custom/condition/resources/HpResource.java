package com.corrodinggames.rts.game.units.custom.condition.resources;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.LocaleString;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e/a/e.class */
public class HpResource extends AbstractResource {
    public HpResource() {
        this.u = true;
        this.t = true;
        this.b = "hp";
        this.c = LocaleString.fromRawText("hp");
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public double a(BaseUnit baseUnit) {
        return baseUnit.currentHealth;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void a(BaseUnit baseUnit, double d) {
        baseUnit.o((float) d);
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void b(BaseUnit baseUnit, double d) {
        baseUnit.o(baseUnit.currentHealth + ((float) d));
    }
}
