package com.corrodinggames.rts.game.units.custom.resources;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.LocaleString;
import com.corrodinggames.rts.game.units.custom.e.a.AbstractResource;
import com.corrodinggames.rts.game.units.custom.resources.DigitGroupingStyle;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e/a/c.class */
public class CreditsResource extends AbstractResource {
    public CreditsResource() {
        this.u = true;
        this.t = true;
        this.b = "credits";
        this.c = LocaleString.replaceAll("$");
        this.o = true;
        this.q = DigitGroupingStyle.space;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public double a(BaseUnit baseUnit) {
        return baseUnit.team.credits;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void a(BaseUnit baseUnit, double d) {
        baseUnit.team.credits = d;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public void b(BaseUnit baseUnit, double d) {
        baseUnit.team.credits += d;
    }

    @Override // com.corrodinggames.rts.game.units.custom.resources.Resource
    public String a(boolean z) {
        return "$";
    }
}
