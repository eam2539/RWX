package com.corrodinggames.rts.game.units.custom.variables;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.f.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/f/e.class */
public class VariableNameValidator {
    public static void a(String str) throws ConfigParseException {
        if (str.length() == 0) {
            throw new ConfigParseException("name cannot be empty");
        }
        if (str.contains(" ") || str.contains("}") || str.contains("$") || str.contains(".") || str.contains("{") || str.contains("-") || str.contains("+") || str.contains(":") || str.contains("(")) {
            throw new ConfigParseException("invalid character in name");
        }
        if (Character.isDigit(str.charAt(0))) {
            throw new ConfigParseException("name cannot start with a digit");
        }
    }
}
