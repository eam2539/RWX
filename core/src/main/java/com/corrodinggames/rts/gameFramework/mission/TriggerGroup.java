package com.corrodinggames.rts.gameFramework.mission;

import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/b.class */
public class TriggerGroup {
    FastArrayList a = new FastArrayList();
    boolean b;

    public void a(MapTrigger mapTrigger) {
        this.a.add(mapTrigger);
    }

    public boolean a() {
        return this.a.size > 0;
    }

    public boolean b() {
        boolean z = false;
        boolean z2 = true;
        Iterator it = this.a.iterator();
        while (it.hasNext()) {
            if (((MapTrigger) it.next()).j) {
                z = true;
            } else {
                z2 = false;
            }
        }
        if (this.b && !z2) {
            z = false;
        }
        return z;
    }
}
