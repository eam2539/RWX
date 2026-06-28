package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.OrderableUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.ar */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/ar.class */
public class TextFormatPart extends LocalePart {

    /* JADX INFO: renamed from: a */
    String content;

    TextFormatPart(String str) {
        this.content = str;
    }

    @Override
        // com.corrodinggames.rts.game.units.custom.LocalePart
    String a(OrderableUnit orderableUnit) {
        return this.content;
    }
}
