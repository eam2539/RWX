package com.corrodinggames.rts.game.units.spatial;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/j.class */
public abstract class UnitSpatialCallback {
    public abstract void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit);
}
