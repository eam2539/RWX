package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.ah */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/ah.class */
public class MultiTurretPassiveTargetCallback extends FilteredUnitCallback {

    /* JADX INFO: renamed from: a */
    public int callbackCount;

    /* JADX INFO: renamed from: b */
    public float[] bestDistanceSqByTurret = new float[31];

    /* JADX INFO: renamed from: c */
    public boolean[] isTurretSelectable = new boolean[31];

    /* JADX INFO: renamed from: d */
    int turretCount;

    /* JADX INFO: renamed from: e */
    public boolean requireEnemyCanAttackOwner;

    /* JADX INFO: renamed from: f */
    public boolean isPrepared;

    MultiTurretPassiveTargetCallback(boolean z) {
        this.requireEnemyCanAttackOwner = z;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public int excludeTeam(OrderableUnit orderableUnit) {
        return -2;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public PlayerTeam onlyEnemiesOfTeam(OrderableUnit orderableUnit) {
        return orderableUnit.team;
    }

    /* JADX INFO: renamed from: a */
    public void prepareForUnit(OrderableUnit orderableUnit) {
        float targetSearchRange = orderableUnit.getTargetSearchRange(false);
        this.turretCount = orderableUnit.getTechLevel();
        for (int i = 0; i < this.turretCount; i++) {
            float turretTargetSearchRange = orderableUnit.getTurretTargetSearchRange(i);
            if (turretTargetSearchRange > targetSearchRange) {
                turretTargetSearchRange = targetSearchRange;
            }
            this.bestDistanceSqByTurret[i] = (turretTargetSearchRange * turretTargetSearchRange) + 1.0f;
            this.isTurretSelectable[i] = false;
            if (orderableUnit.getLinkedTurretIndex(i) == -1 && orderableUnit.movementLevels[i].targetUnit == null) {
                this.isTurretSelectable[i] = true;
            }
        }
        this.isPrepared = true;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback
    public void setup(OrderableUnit orderableUnit, float f) {
        this.callbackCount = 0;
        if (!this.isPrepared) {
            throw new RuntimeException("PassiveTargetCallback not ready");
        }
        this.isPrepared = false;
    }

    @Override // com.corrodinggames.rts.game.units.spatial.UnitSpatialCallback
    public void callback(OrderableUnit orderableUnit, float f, BaseUnit baseUnit) {
        if (orderableUnit.b(baseUnit, true)) {
            this.callbackCount++;
            if (this.requireEnemyCanAttackOwner) {
                if (!(baseUnit instanceof OrderableUnit)) {
                    return;
                }
                OrderableUnit orderableUnit2 = (OrderableUnit) baseUnit;
                if (!orderableUnit2.canAttack() || !orderableUnit2.canAttackUnitType(orderableUnit)) {
                    return;
                }
            }
            float fDistanceSq = Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY);
            for (int i = 0; i < this.turretCount; i++) {
                if (this.isTurretSelectable[i] && orderableUnit.a(i, baseUnit, true, false) && fDistanceSq < this.bestDistanceSqByTurret[i] && fDistanceSq > orderableUnit.A(i)) {
                    this.bestDistanceSqByTurret[i] = fDistanceSq;
                    orderableUnit.movementLevels[i].targetUnit = baseUnit;
                }
            }
        }
    }
}
