package com.corrodinggames.rts.gameFramework.pathfinding;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/e.class */
public final class BucketedNodeQueue extends NodeQueue {
    boolean a;
    int b;
    final DirectAccessPathNodeArrayList c = new DirectAccessPathNodeArrayList(100);
    final DirectAccessPathNodeArrayList d = new DirectAccessPathNodeArrayList(900);

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue
    public void a(PathOpenListNode pathOpenListNode) {
        int i = pathOpenListNode.score;
        if (i <= this.b) {
            if (i == this.b) {
                this.c.b(pathOpenListNode);
                return;
            }
            c();
            this.b = i;
            this.c.add(pathOpenListNode);
            return;
        }
        this.d.b(pathOpenListNode);
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue
    public PathOpenListNode a() {
        if (this.c.b > 0) {
            return this.c.b();
        }
        if (this.d.b == 0) {
            this.b = Integer.MAX_VALUE;
            return null;
        }
        d();
        return this.c.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue
    public void b() {
        a((PathOpenListPool) null);
    }

    public void a(PathOpenListPool pathOpenListPool) {
        if (pathOpenListPool != null) {
            PathOpenListNode[] pathOpenListNodeArrA = this.c.a();
            for (int i = this.c.b - 1; i >= 0; i--) {
                pathOpenListPool.a(pathOpenListNodeArrA[i]);
            }
            PathOpenListNode[] pathOpenListNodeArrA2 = this.d.a();
            for (int i2 = this.d.b - 1; i2 >= 0; i2--) {
                pathOpenListPool.a(pathOpenListNodeArrA2[i2]);
            }
        }
        this.c.clear();
        this.d.clear();
        this.b = Integer.MAX_VALUE;
        this.a = true;
    }

    private void c() {
        PathOpenListNode[] pathOpenListNodeArrA = this.c.a();
        int i = this.c.b;
        for (int i2 = 0; i2 < i; i2++) {
            this.d.add(pathOpenListNodeArrA[i2]);
        }
        this.c.clear();
    }

    private void d() {
        int i = Integer.MAX_VALUE;
        DirectAccessPathNodeArrayList directAccessPathNodeArrayList = this.d;
        PathOpenListNode[] pathOpenListNodeArrA = directAccessPathNodeArrayList.a();
        for (int i2 = directAccessPathNodeArrayList.b - 1; i2 >= 0; i2--) {
            int i3 = pathOpenListNodeArrA[i2].score;
            if (i3 < i) {
                i = i3;
            }
        }
        for (int i4 = directAccessPathNodeArrayList.b - 1; i4 >= 0; i4--) {
            PathOpenListNode pathOpenListNode = pathOpenListNodeArrA[i4];
            if (pathOpenListNode.score == i) {
                this.c.add(pathOpenListNode);
                int i5 = directAccessPathNodeArrayList.b - 1;
                pathOpenListNodeArrA[i4] = pathOpenListNodeArrA[i5];
                pathOpenListNodeArrA[i5] = null;
                directAccessPathNodeArrayList.b = i5;
            }
        }
        this.b = i;
    }
}
