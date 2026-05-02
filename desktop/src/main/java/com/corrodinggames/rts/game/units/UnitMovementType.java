package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.ao */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/ao.class */
public enum UnitMovementType {
    NONE,
    LAND,
    BUILDING,
    AIR,
    WATER,
    HOVER,
    OVER_CLIFF,
    OVER_CLIFF_WATER;

    public static UnitMovementType a(String str, String str2) {
        try {
            return valueOf(str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            String str3 = VariableScope.nullOrMissingString;
            for (UnitMovementType unitMovementType : values()) {
                str3 = str3 + ", " + unitMovementType.toString();
            }
            throw new IllegalArgumentException("Unknown movement type:'" + str + "' possible type:" + str3 + " on key:" + str2);
        }
    }
}
