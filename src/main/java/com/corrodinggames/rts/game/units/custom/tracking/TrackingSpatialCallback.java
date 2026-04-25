package com.corrodinggames.rts.game.units.custom.tracking;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.c.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/c/f.class */
public class TrackingSpatialCallback extends FilteredUnitCallback {
    public AnimationTrackingManager a;
    public AnimationTrackingEntry b;
    public BaseUnit c;
    public float d;

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public void setup(OrderableUnit orderableUnit, float f) {
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public int excludeTeam(OrderableUnit orderableUnit) {
        return -3;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit) {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public PlayerTeam onlyTeam(OrderableUnit orderableUnit) {
        return orderableUnit.team;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
    public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
        AnimationSet unitAICombatTimer;
        if (orderableUnit != baseUnit && (unitAICombatTimer = baseUnit.getUnitAICombatTimer()) != null && AnimationTag.a(this.b.a, unitAICombatTimer)) {
            if (orderableUnit.team != baseUnit.team) {
                if (orderableUnit.team.d(baseUnit.team)) {
                    if (!this.b.b) {
                        return;
                    }
                } else if (!orderableUnit.team.c(baseUnit.team) || !this.b.c) {
                    return;
                }
            }
            float fDistanceSq = Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY);
            if (fDistanceSq < this.d) {
                TrackingGroup trackingGroupA = this.a.a(this.b, false);
                if (trackingGroupA == null || trackingGroupA.a(baseUnit) == null) {
                    this.c = baseUnit;
                    this.d = fDistanceSq;
                }
            }
        }
    }
}
