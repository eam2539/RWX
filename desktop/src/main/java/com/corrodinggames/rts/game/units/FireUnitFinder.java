package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.aj */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/aj.class */
public class FireUnitFinder extends FilteredUnitCallback {
    float a;
    float b;
    public FireUnit c;

    FireUnitFinder() {
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public int excludeTeam(OrderableUnit orderableUnit) {
        return -2;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit) {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public void setup(OrderableUnit orderableUnit, float f) {
        this.c = null;
    }

    public void a(float f, float f2) {
        this.a = f;
        this.b = f2;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
    public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
        if ((baseUnit instanceof FireUnit) && !baseUnit.isDead && baseUnit.checkAttackCooldown(this.a, this.b, 0.0f)) {
            this.c = (FireUnit) baseUnit;
        }
    }
}
