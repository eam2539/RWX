package com.corrodinggames.rts.gameFramework.network;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.aj */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/aj.class */
public class BanEntry {
    String a;
    long b;
    String c;

    public String a() {
        return this.c != null ? this.c : "Active ban";
    }

    public float b() {
        return (this.b - System.currentTimeMillis()) / 1000.0f;
    }
}
