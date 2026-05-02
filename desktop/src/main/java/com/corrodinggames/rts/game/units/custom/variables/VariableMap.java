package com.corrodinggames.rts.game.units.custom.variables;

import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.f.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/f/d.class */
public class VariableMap {
    HashMap a = new HashMap();

    public void a(String str, String str2) {
        this.a.put(str, str2);
    }

    public String a(String str) {
        return (String) this.a.get(str);
    }

    public void a() {
        this.a.clear();
    }
}
