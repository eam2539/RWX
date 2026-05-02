package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.ap */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/ap.class */
public class UnitFormatPart extends LogicFormatPart {
    UnitFormatPart(LogicBoolean logicBoolean) {
        super(logicBoolean);
    }

    @Override // com.corrodinggames.rts.game.units.custom.LocalePart
    String a(OrderableUnit orderableUnit) {
        return BaseUnit.f(this.logicBoolean.readUnit(orderableUnit), false);
    }
}
