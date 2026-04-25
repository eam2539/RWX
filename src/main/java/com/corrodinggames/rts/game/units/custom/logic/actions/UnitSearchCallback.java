package com.corrodinggames.rts.game.units.custom.logic.actions;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.TeamRelation;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/c.class */
public class UnitSearchCallback extends FilteredUnitCallback {

    /* JADX INFO: renamed from: a */
    public boolean mapMustBeReachable;

    /* JADX INFO: renamed from: b */
    public AnimationSet unitTags;

    /* JADX INFO: renamed from: c */
    public float maxRange;

    /* JADX INFO: renamed from: d */
    public boolean includeNotBuilt;

    /* JADX INFO: renamed from: e */
    public TeamRelation teamFilter;

    /* JADX INFO: renamed from: f */
    public boolean collectMultipleUnits;

    /* JADX INFO: renamed from: g */
    public FastArrayList collectedUnits = new FastArrayList();

    /* JADX INFO: renamed from: h */
    public BaseUnit foundUnit;

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
        AnimationSet unitCombatAnimation = baseUnit.getUnitCombatAnimation();
        if (this.unitTags == null || (unitCombatAnimation != null && AnimationTag.a(this.unitTags, unitCombatAnimation))) {
            float fDistanceSq = Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY);
            if (fDistanceSq < this.maxRange) {
                if (baseUnit.deceleration < 1.0f && !this.includeNotBuilt) {
                    return;
                }
                if (this.teamFilter != null && !orderableUnit.team.a(this.teamFilter, baseUnit.team)) {
                    return;
                }
                if (this.mapMustBeReachable && !GameViewUtils.b(orderableUnit, baseUnit.posX, baseUnit.posY)) {
                    return;
                }
                if (!this.collectMultipleUnits) {
                    this.foundUnit = baseUnit;
                    this.maxRange = fDistanceSq;
                } else {
                    this.collectedUnits.add(baseUnit);
                }
            }
        }
    }
}
