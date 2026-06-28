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

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/n.class */
public class ResourceUnitSearchCallback extends FilteredUnitCallback {

    /* JADX INFO: renamed from: a */
    public AnimationSet tags;

    /* JADX INFO: renamed from: b */
    public float rangeSq;

    /* JADX INFO: renamed from: c */
    public boolean includeDead;

    /* JADX INFO: renamed from: d */
    public TeamRelation teamFilter;

    /* JADX INFO: renamed from: e */
    public FastArrayList foundUnits;

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
        AnimationSet unitCombatAnimation = baseUnit.getTags();
        if ((this.tags == null || (unitCombatAnimation != null && AnimationTag.a(this.tags, unitCombatAnimation))) && Utility.distanceSq(orderableUnit.posX, orderableUnit.posY, baseUnit.posX, baseUnit.posY) < this.rangeSq) {
            if (baseUnit.buildProgress < 1.0f && !this.includeDead) {
                return;
            }
            if (this.teamFilter != null && !orderableUnit.team.a(this.teamFilter, baseUnit.team)) {
                return;
            }
            this.foundUnits.add(baseUnit);
        }
    }
}
