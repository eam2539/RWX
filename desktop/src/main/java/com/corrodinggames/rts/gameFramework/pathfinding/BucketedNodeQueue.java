package com.corrodinggames.rts.gameFramework.pathfinding;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/e.class */
public final class BucketedNodeQueue extends NodeQueue {

    /* JADX INFO: renamed from: c */
    final DirectAccessPathNodeArrayList currentBucket = new DirectAccessPathNodeArrayList(100);
    /* JADX INFO: renamed from: d */
    final DirectAccessPathNodeArrayList overflowBucket = new DirectAccessPathNodeArrayList(900);
    /* JADX INFO: renamed from: a */
    boolean isCleared;
    /* JADX INFO: renamed from: b */
    int currentBucketScore;

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue
    public void a(PathOpenListNode pathOpenListNode) {
        int i = pathOpenListNode.score;
        if (i <= this.currentBucketScore) {
            if (i == this.currentBucketScore) {
                this.currentBucket.b(pathOpenListNode);
                return;
            }
            c();
            this.currentBucketScore = i;
            this.currentBucket.add(pathOpenListNode);
            return;
        }
        this.overflowBucket.b(pathOpenListNode);
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue
    public PathOpenListNode a() {
        if (this.currentBucket.size > 0) {
            return this.currentBucket.b();
        }
        if (this.overflowBucket.size == 0) {
            this.currentBucketScore = Integer.MAX_VALUE;
            return null;
        }
        d();
        return this.currentBucket.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue
    public void b() {
        a((PathOpenListPool) null);
    }

    public void a(PathOpenListPool pathOpenListPool) {
        if (pathOpenListPool != null) {
            PathOpenListNode[] pathOpenListNodeArrA = this.currentBucket.a();
            for (int i = this.currentBucket.size - 1; i >= 0; i--) {
                pathOpenListPool.a(pathOpenListNodeArrA[i]);
            }
            PathOpenListNode[] pathOpenListNodeArrA2 = this.overflowBucket.a();
            for (int i2 = this.overflowBucket.size - 1; i2 >= 0; i2--) {
                pathOpenListPool.a(pathOpenListNodeArrA2[i2]);
            }
        }
        this.currentBucket.clear();
        this.overflowBucket.clear();
        this.currentBucketScore = Integer.MAX_VALUE;
        this.isCleared = true;
    }

    private void c() {
        PathOpenListNode[] pathOpenListNodeArrA = this.currentBucket.a();
        int i = this.currentBucket.size;
        for (int i2 = 0; i2 < i; i2++) {
            this.overflowBucket.add(pathOpenListNodeArrA[i2]);
        }
        this.currentBucket.clear();
    }

    private void d() {
        int i = Integer.MAX_VALUE;
        DirectAccessPathNodeArrayList directAccessPathNodeArrayList = this.overflowBucket;
        PathOpenListNode[] pathOpenListNodeArrA = directAccessPathNodeArrayList.a();
        for (int i2 = directAccessPathNodeArrayList.size - 1; i2 >= 0; i2--) {
            int i3 = pathOpenListNodeArrA[i2].score;
            if (i3 < i) {
                i = i3;
            }
        }
        for (int i4 = directAccessPathNodeArrayList.size - 1; i4 >= 0; i4--) {
            PathOpenListNode pathOpenListNode = pathOpenListNodeArrA[i4];
            if (pathOpenListNode.score == i) {
                this.currentBucket.add(pathOpenListNode);
                int i5 = directAccessPathNodeArrayList.size - 1;
                pathOpenListNodeArrA[i4] = pathOpenListNodeArrA[i5];
                pathOpenListNodeArrA[i5] = null;
                directAccessPathNodeArrayList.size = i5;
            }
        }
        this.currentBucketScore = i;
    }
}
