package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bd */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bd.class */
public class ConfigValidationException extends ConfigParseException {
    public String a;

    public ConfigValidationException(String str, String str2) {
        super(str, str2);
        this.messageDetail = str2;
    }
}
