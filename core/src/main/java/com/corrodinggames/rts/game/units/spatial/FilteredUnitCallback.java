package com.corrodinggames.rts.game.units.spatial;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/i.class */
public abstract class FilteredUnitCallback extends UnitSpatialCallback {
    public abstract int excludeTeam(OrderableUnit orderableUnit);

    public abstract PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit);

    public PlayerTeam onlyTeam(OrderableUnit orderableUnit) {
        return null;
    }

    public void setup(OrderableUnit orderableUnit, float f) {
    }

    public BaseUnit getResult() {
        return null;
    }
}
