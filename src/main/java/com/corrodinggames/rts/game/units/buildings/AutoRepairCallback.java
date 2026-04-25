package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.s */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/s.class */
public class AutoRepairCallback extends FilteredUnitCallback {

    /* JADX INFO: renamed from: a */
    public float searchRangeSquared;

    /* JADX INFO: renamed from: b */
    public boolean isEnabled;

    /* JADX INFO: renamed from: c */
    public boolean isReady;

    /* JADX INFO: renamed from: d */
    PlayerTeam searcherTeam;

    /* JADX INFO: renamed from: e */
    BaseUnit bestTarget;

    /* JADX INFO: renamed from: f */
    float bestTargetHealth;

    /* JADX INFO: renamed from: g */
    float bestTargetDistance;

    /* JADX INFO: renamed from: h */
    boolean prioritizeDistance;

    AutoRepairCallback(boolean z) {
        this.isEnabled = z;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public int excludeTeam(OrderableUnit orderableUnit) {
        return -2;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit) {
        return null;
    }

    /* JADX INFO: renamed from: a */
    public void configure(float f, boolean z) {
        this.searchRangeSquared = f * f;
        this.prioritizeDistance = z;
        this.isReady = true;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public void setup(OrderableUnit orderableUnit, float f) {
        this.bestTarget = null;
        this.bestTargetHealth = -1.0f;
        this.bestTargetDistance = -1.0f;
        this.searcherTeam = orderableUnit.team;
        if (!this.isReady) {
            throw new RuntimeException("AutoRepairCallback not ready");
        }
        this.isReady = false;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
    public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
        if (orderableUnit == baseUnit) {
            return;
        }
        if ((baseUnit.currentHealth < baseUnit.maxHealth || baseUnit.deceleration < 1.0f) && !baseUnit.isDestroyed && baseUnit.unitTransportTarget == null && this.searcherTeam.d(baseUnit.team) && orderableUnit.canRepairTarget(baseUnit)) {
            float fDistanceSq = Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY);
            if (fDistanceSq < this.searchRangeSquared) {
                if (baseUnit.deceleration < 1.0f && orderableUnit.getRepairOrReclaimPrice(baseUnit) != null) {
                    return;
                }
                boolean z = false;
                if (this.prioritizeDistance) {
                    if (this.bestTargetDistance == -1.0f || this.bestTargetDistance > fDistanceSq) {
                        z = true;
                    }
                } else if (this.bestTargetHealth == -1.0f || this.bestTargetHealth > baseUnit.currentHealth) {
                    z = true;
                }
                if (z && baseUnit.getUnitHealthPercent() == 0.0f) {
                    this.bestTargetHealth = baseUnit.currentHealth;
                    this.bestTargetDistance = fDistanceSq;
                    this.bestTarget = baseUnit;
                }
            }
        }
    }
}
