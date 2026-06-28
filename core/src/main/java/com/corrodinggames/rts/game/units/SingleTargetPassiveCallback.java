package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.ae */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/ae.class */
public class SingleTargetPassiveCallback extends FilteredUnitCallback {

    /* JADX INFO: renamed from: a */
    public int callbackCount;

    /* JADX INFO: renamed from: b */
    public float closestDistanceSq;

    /* JADX INFO: renamed from: c */
    public boolean checkLineOfSight;

    /* JADX INFO: renamed from: d */
    public boolean isReady;

    SingleTargetPassiveCallback(boolean z) {
        this.checkLineOfSight = z;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public int excludeTeam(OrderableUnit orderableUnit) {
        return -2;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit) {
        return orderableUnit.team;
    }

    public void a(float f) {
        this.closestDistanceSq = (f * f) + 1.0f;
        this.isReady = true;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public void setup(OrderableUnit orderableUnit, float f) {
        this.callbackCount = 0;
        if (!this.isReady) {
            throw new RuntimeException("PassiveTargetCallback not ready");
        }
        this.isReady = false;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
    public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
        if (orderableUnit.b(baseUnit, true)) {
            this.callbackCount++;
            if (this.checkLineOfSight) {
                if (!(baseUnit instanceof OrderableUnit)) {
                    return;
                }
                OrderableUnit orderableUnit2 = (OrderableUnit) baseUnit;
                if (!orderableUnit2.canAttack() || !orderableUnit2.canAttackUnitType(orderableUnit)) {
                    return;
                }
            }
            float fDistanceSq = Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY);
            if (fDistanceSq < this.closestDistanceSq) {
                this.closestDistanceSq = fDistanceSq;
                orderableUnit.attackTarget = baseUnit;
            }
        }
    }
}
