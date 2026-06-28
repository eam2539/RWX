package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.ao */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/ao.class */
public class StringFormatPart extends LogicFormatPart {
    StringFormatPart(LogicBoolean logicBoolean) {
        super(logicBoolean);
    }

    @Override
        // com.corrodinggames.rts.game.units.custom.LocalePart
    String a(OrderableUnit orderableUnit) {
        return this.logicBoolean.readString(orderableUnit);
    }
}
