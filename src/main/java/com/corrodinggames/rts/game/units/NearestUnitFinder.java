package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.ac */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/ac.class */
public class NearestUnitFinder extends FilteredUnitCallback {

    /* JADX INFO: renamed from: a */
    public float searchPosX;

    /* JADX INFO: renamed from: b */
    public float searchPosY;

    /* JADX INFO: renamed from: c */
    public AnimationSet animationSetFilter;

    /* JADX INFO: renamed from: d */
    public float closestDistanceSq;

    /* JADX INFO: renamed from: e */
    public BaseUnit nearestUnit;

    /* JADX INFO: renamed from: f */
    public boolean checkLineOfSight;

    /* JADX INFO: renamed from: g */
    public boolean includeNonGroundUnits = false;

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public void setup(OrderableUnit orderableUnit, float f) {
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
    public PlayerTeam onlyTeam(OrderableUnit orderableUnit) {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
    public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
        if (this.checkLineOfSight && baseUnit.getUnitHealthPercent() <= 0.0f) {
            return;
        }
        float fDistanceSq = Utility.distanceSq(this.searchPosX, this.searchPosY, baseUnit.posX, baseUnit.posY);
        if (fDistanceSq < this.closestDistanceSq) {
            if (baseUnit.deceleration < 1.0f && !this.includeNonGroundUnits) {
                return;
            }
            if (this.animationSetFilter != null && !AnimationTag.a(this.animationSetFilter, baseUnit.getUnitCombatAnimation())) {
                return;
            }
            if ((this.checkLineOfSight && !orderableUnit.g(baseUnit, true)) || baseUnit.unitTransportTarget != null) {
                return;
            }
            this.nearestUnit = baseUnit;
            this.closestDistanceSq = fDistanceSq;
        }
    }
}
