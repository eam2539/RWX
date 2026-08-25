package com.corrodinggames.rts.game;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnitSpawnList;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/h.class */
public class UnitFilter {

    /* JADX INFO: renamed from: a */
    public AnimationSet requiredTags;

    /* JADX INFO: renamed from: b */
    public AnimationSet excludedTags;

    public float c;
    public float d;
    public UnitPrice e;
    public UnitPrice f;

    /* JADX INFO: renamed from: g */
    public CustomUnitSpawnList spawnList;

    public boolean a(BaseUnit baseUnit) {
        if (this.excludedTags != null && AnimationTag.a(this.excludedTags, baseUnit.getTags())) {
            return false;
        }
        if (this.requiredTags != null && !AnimationTag.a(this.requiredTags, baseUnit.getTags())) {
            return false;
        }
        return true;
    }
}
