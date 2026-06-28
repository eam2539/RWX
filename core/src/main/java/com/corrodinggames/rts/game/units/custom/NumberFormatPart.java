package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.an */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/an.class */
public class NumberFormatPart extends LogicFormatPart {
    NumberFormatPart(LogicBoolean logicBoolean) {
        super(logicBoolean);
    }

    @Override
        // com.corrodinggames.rts.game.units.custom.LocalePart
    String a(OrderableUnit orderableUnit) {
        return Utility.min(this.logicBoolean.readNumber(orderableUnit));
    }
}
