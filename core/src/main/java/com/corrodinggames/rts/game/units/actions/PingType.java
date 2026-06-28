package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.gameFramework.local.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/k.class */
public enum PingType {
    normal,
    attack,
    defend,
    nuke,
    build,
    upgrade,
    ok,
    no,
    happy,
    sad,
    retreat;

    public String a() {
        return " - " + b();
    }

    public String b() {
        return Locale.get(c(), new Object[0]);
    }

    public String c() {
        return "menus.ingame.ping.type." + name();
    }
}
