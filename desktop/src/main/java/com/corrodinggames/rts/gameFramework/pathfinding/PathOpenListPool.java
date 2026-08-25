package com.corrodinggames.rts.gameFramework.pathfinding;

import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/m.class */
public final class PathOpenListPool {
    int a;
    int b;
    public static int c;
    /* JADX INFO: renamed from: e */
    final BucketedNodeQueue queue = new BucketedNodeQueue();

    /* JADX INFO: renamed from: d */
    final DirectAccessPathNodeArrayList pool = new DirectAccessPathNodeArrayList(1000 + 100);

    PathOpenListPool() {
        for (int i = 0; i < 1000; i++) {
            this.pool.add(new PathOpenListNode());
        }
    }

    PathOpenListNode a() {
        if (this.pool.size == 0) {
            c++;
            return new PathOpenListNode();
        }
        return this.pool.b();
    }

    final void a(PathOpenListNode pathOpenListNode) {
        if (pathOpenListNode != null) {
            this.pool.b(pathOpenListNode);
        }
    }

    void b() {
        if (this.pool.size() > 50000) {
            GameEngine.log("PathOpenList: resetPool:memoryPool over 50000 clearing");
            this.pool.clear();
        }
        this.queue.a(this);
    }

    public void a(int i, int i2) {
        b();
        this.a = i;
        this.b = i2;
    }

    public final void a(int i, short s, short s2) {
        PathOpenListNode pathOpenListNodeA = a();
        pathOpenListNodeA.a(s, s2);
        pathOpenListNodeA.a(i, this.a, this.b);
        this.queue.a(pathOpenListNodeA);
    }

    public final PathOpenListNode c() {
        PathOpenListNode pathOpenListNodeA = this.queue.a();
        if (pathOpenListNodeA != null) {
            a(pathOpenListNodeA);
        }
        return pathOpenListNodeA;
    }
}
