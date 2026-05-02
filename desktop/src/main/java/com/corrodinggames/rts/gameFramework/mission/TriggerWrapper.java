package com.corrodinggames.rts.gameFramework.mission;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/m.class */
public class TriggerWrapper {
    MapTrigger a;

    public String a() {
        if (this.a.z == null) {
            return "<null>";
        }
        return this.a.z.resolveText();
    }

    public boolean b() {
        return this.a.j;
    }
}
