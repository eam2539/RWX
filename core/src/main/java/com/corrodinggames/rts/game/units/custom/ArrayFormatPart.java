package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.al */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/al.class */
public class ArrayFormatPart extends LogicFormatPart {
    ArrayFormatPart(LogicBoolean logicBoolean) {
        super(logicBoolean);
    }

    @Override
        // com.corrodinggames.rts.game.units.custom.LocalePart
    String a(OrderableUnit orderableUnit) {
        return LogicString.arrayToString(orderableUnit, this.logicBoolean);
    }
}
