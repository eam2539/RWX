package com.corrodinggames.rts.gameFramework.mission.conditions;

import com.corrodinggames.rts.gameFramework.mission.MapTrigger;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/a/a.class */
public abstract class TriggerCondition {
    public abstract boolean b(MapTrigger mapTrigger);

    public boolean a(MapTrigger mapTrigger) {
        return true;
    }

    public boolean c(MapTrigger mapTrigger) {
        return false;
    }
}
