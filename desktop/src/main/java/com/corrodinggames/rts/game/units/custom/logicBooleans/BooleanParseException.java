package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.gameFramework.utility.ConfigException;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/BooleanParseException.class */
public class BooleanParseException extends ConfigException {
    private static final long serialVersionUID = 1;

    public BooleanParseException(String str, Throwable th) {
        super(str, th);
    }

    public BooleanParseException(String str) {
        super(str);
    }
}
