package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.game.units.BaseUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/b.class */
public class ActionFilter {
    public static final ActionFilter emptyActionFilter = new ActionFilter();

    public boolean isAvailable(AbstractUnitAction abstractUnitAction, BaseUnit baseUnit) {
        return true;
    }
}
