package com.corrodinggames.rts.game.units.custom.logic;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.CustomUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a.class */
public abstract class LogicAction {
    /* JADX INFO: renamed from: a */
    public abstract boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i);
}
