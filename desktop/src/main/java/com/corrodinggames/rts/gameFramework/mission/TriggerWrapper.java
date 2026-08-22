package com.corrodinggames.rts.gameFramework.mission;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/m.class */
public class TriggerWrapper {

    /* JADX INFO: renamed from: a */
    MapTrigger trigger;

    public String a() {
        if (this.trigger.text == null) {
            return "<null>";
        }
        return this.trigger.text.resolveText();
    }

    public boolean b() {
        return this.trigger.isActive;
    }
}
