package com.corrodinggames.rts.game;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnitSpawnList;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/h.class */
public class UnitFilter {
    public AnimationSet a;
    public AnimationSet b;
    public float c;
    public float d;
    public UnitPrice e;
    public UnitPrice f;
    public CustomUnitSpawnList g;

    public boolean a(BaseUnit baseUnit) {
        if (this.b != null && AnimationTag.a(this.b, baseUnit.getUnitCombatAnimation())) {
            return false;
        }
        if (this.a != null && !AnimationTag.a(this.a, baseUnit.getUnitCombatAnimation())) {
            return false;
        }
        return true;
    }
}
