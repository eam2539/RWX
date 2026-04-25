package com.corrodinggames.rts.game.units.management;

import android.graphics.RectF;
import com.corrodinggames.rts.game.units.BaseUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/h.class */
public final class MovingUnitCallback extends UnitCallback {
    public RectF a = new RectF();
    public float b;
    public float c;
    public float d;
    public float e;

    @Override // com.corrodinggames.rts.game.units.management.UnitCallback
    public final boolean a(BaseUnit baseUnit) {
        float f = baseUnit.speed;
        float f2 = baseUnit.posX;
        float f3 = baseUnit.posY;
        return this.b - f <= f2 && f2 <= this.c + f && this.d - f <= f3 && f3 <= this.e + f;
    }
}
