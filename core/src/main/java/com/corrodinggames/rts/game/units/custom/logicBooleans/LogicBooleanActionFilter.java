package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionFilter;
import com.corrodinggames.rts.game.units.custom.CustomUnit;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanActionFilter.class */
public class LogicBooleanActionFilter extends ActionFilter {
    LogicBoolean logicBoolean;
    CustomUnit target;

    public LogicBooleanActionFilter(LogicBoolean logicBoolean, CustomUnit customUnit) {
        this.logicBoolean = logicBoolean;
    }

    @Override // com.corrodinggames.rts.game.units.actions.ActionFilter
    public boolean isAvailable(AbstractUnitAction abstractUnitAction, BaseUnit baseUnit) {
        return this.logicBoolean.read(this.target);
    }
}
