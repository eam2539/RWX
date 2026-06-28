package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.q */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/q.class */
public class TaskQueue {
    FastArrayList a = new FastArrayList();

    public void a(Runnable runnable) {
        this.a.add(runnable);
    }

    public void a() {
        if (this.a.size > 0) {
            Iterator it = this.a.iterator();
            while (it.hasNext()) {
                ((Runnable) it.next()).run();
            }
        }
    }

    public void b() {
        if (this.a.size > 0) {
            Iterator it = this.a.iterator();
            while (it.hasNext()) {
                ((Runnable) it.next()).run();
            }
            this.a.clear();
        }
    }
}
