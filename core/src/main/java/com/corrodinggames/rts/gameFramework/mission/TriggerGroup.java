package com.corrodinggames.rts.gameFramework.mission;

import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/b.class */
public class TriggerGroup {

    /* JADX INFO: renamed from: a */
    FastArrayList triggers = new FastArrayList();

    /* JADX INFO: renamed from: b */
    boolean requireAll;

    public void a(MapTrigger mapTrigger) {
        this.triggers.add(mapTrigger);
    }

    public boolean a() {
        return this.triggers.size > 0;
    }

    public boolean b() {
        boolean z = false;
        boolean z2 = true;
        Iterator it = this.triggers.iterator();
        while (it.hasNext()) {
            if (((MapTrigger) it.next()).isActive) {
                z = true;
            } else {
                z2 = false;
            }
        }
        if (this.requireAll && !z2) {
            z = false;
        }
        return z;
    }
}
