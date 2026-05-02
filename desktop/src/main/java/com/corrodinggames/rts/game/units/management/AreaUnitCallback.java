package com.corrodinggames.rts.game.units.management;

import android.graphics.RectF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/d.class */
public final class AreaUnitCallback extends UnitCallback {
    public RectF a = new RectF();
    public float b;
    public float c;
    public float d;
    public float e;
    public float f;
    public float g;
    public float h;

    @Override // com.corrodinggames.rts.game.units.management.UnitCallback
    public final boolean a(BaseUnit baseUnit) {
        float f = baseUnit.posX;
        float f2 = baseUnit.posY;
        return this.b <= f && f <= this.c && this.d <= f2 && f2 <= this.e && Utility.distanceSq(this.f, this.g, f, f2) < this.h;
    }
}
