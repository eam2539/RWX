package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.aj */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/aj.class */
public class FireUnitFinder extends FilteredUnitCallback {

    /* JADX INFO: renamed from: c */
    public FireUnit foundFireUnit;
    /* JADX INFO: renamed from: a */
    float queryX;
    /* JADX INFO: renamed from: b */
    float queryY;

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
        this.foundFireUnit = null;
    }

    public void a(float f, float f2) {
        this.queryX = f;
        this.queryY = f2;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
    public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
        if ((baseUnit instanceof FireUnit) && !baseUnit.isDead && baseUnit.isWithinRange(this.queryX, this.queryY, 0.0f)) {
            this.foundFireUnit = (FireUnit) baseUnit;
        }
    }
}
