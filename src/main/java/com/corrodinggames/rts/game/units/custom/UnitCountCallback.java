package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bf */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bf.class */
public class UnitCountCallback extends FilteredUnitCallback {
    public float a;
    public float b;
    public UnitSearchRule c;
    public int d;

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
        if (orderableUnit == baseUnit) {
            return;
        }
        AnimationSet unitCombatAnimation = baseUnit.getUnitCombatAnimation();
        AnimationSet animationSet = this.c.c;
        if ((animationSet == null || (unitCombatAnimation != null && AnimationTag.a(animationSet, unitCombatAnimation))) && Utility.distanceSq(this.a, this.b, baseUnit.posX, baseUnit.posY) < this.c.f) {
            if (baseUnit.deceleration < 1.0f && this.c.i) {
                return;
            }
            if (this.c.j && !baseUnit.bI()) {
                return;
            }
            if (this.c.d != null && !orderableUnit.team.a(this.c.d, baseUnit.team)) {
                return;
            }
            this.d++;
        }
    }
}
