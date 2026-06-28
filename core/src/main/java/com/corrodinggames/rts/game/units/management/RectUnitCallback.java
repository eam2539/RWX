package com.corrodinggames.rts.game.units.management;

import com.corrodinggames.rts.game.units.BaseUnit;
import io.github.rwx.geometry.RectF;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/g.class */
public final class RectUnitCallback extends UnitCallback {
    public RectF a = new RectF();
    public float b;
    public float c;
    public float d;
    public float e;

    public void a(float f, float f2, float f3, float f4) {
        this.b = f;
        this.c = f3;
        this.d = f2;
        this.e = f4;
        this.a.a(f, f2, f3, f4);
    }

    @Override // com.corrodinggames.rts.game.units.management.UnitCallback
    public final boolean a(BaseUnit baseUnit) {
        float f = baseUnit.posX;
        float f2 = baseUnit.posY;
        return this.b <= f && f <= this.c && this.d <= f2 && f2 <= this.e;
    }
}
