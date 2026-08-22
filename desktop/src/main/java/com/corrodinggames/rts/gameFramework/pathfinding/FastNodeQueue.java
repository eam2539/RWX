package com.corrodinggames.rts.gameFramework.pathfinding;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.util.PriorityQueue;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/d.class */
public final class FastNodeQueue extends NodeQueue {
    public static int a;
    public static int b;
    public static int c;
    public static int d;
    public static int e;
    public static int f;
    public static int g;
    public static double h;
    public static double i;
    int j;
    int k;
    int l;
    int m;
    int n;
    PathOpenListNode[] o = new PathOpenListNode[975];
    FastArrayList p = new FastArrayList(100);
    final PriorityQueue<PathOpenListNode> q = new PriorityQueue();
    final FastArrayList r = new FastArrayList(300);
    int s;
    int t;
    public static int u;

    private void c() {
        if (this.k == this.m) {
            d();
            return;
        }
        int i2 = this.n;
        PathOpenListNode[] pathOpenListNodeArr = this.o;
        if (this.j == -2) {
            for (int i3 = 0; i3 <= i2; i3++) {
                int i4 = pathOpenListNodeArr[i3].score;
                if (this.k == i4) {
                    this.j = i3;
                    this.k = i4;
                    return;
                }
            }
        }
        int i5 = -1;
        int i6 = Integer.MAX_VALUE;
        for (int i7 = 0; i7 <= i2; i7++) {
            int i8 = pathOpenListNodeArr[i7].score;
            if (i6 > i8) {
                i5 = i7;
                i6 = i8;
            }
        }
        if (this.k != i6) {
            g++;
        }
        this.j = i5;
        this.k = i6;
    }

    private void a(int i2, PathOpenListNode pathOpenListNode) {
        this.o[i2] = pathOpenListNode;
        int i3 = pathOpenListNode.score;
        if (this.j == -1 || this.k >= i3) {
            if (this.k > i3) {
            }
            if (this.k != i3) {
                g++;
            }
            this.j = i2;
            this.k = i3;
        }
        if (this.l == -1 || this.m < i3) {
            this.l = i2;
            this.m = i3;
        }
    }

    private void d() {
        this.j = -1;
        this.k = Integer.MAX_VALUE;
        this.l = -1;
        this.m = Integer.MIN_VALUE;
        for (int i2 = 0; i2 <= this.n; i2++) {
            PathOpenListNode pathOpenListNode = this.o[i2];
            if (pathOpenListNode == null) {
                GameEngine.log("n:" + i2);
                GameEngine.log("lowestBufferLastIndex:" + this.n);
                throw new RuntimeException("null with n:" + i2 + ", lowestBufferLastIndex:" + this.n);
            }
            int i3 = pathOpenListNode.score;
            if (this.k > i3) {
                this.j = i2;
                this.k = i3;
            }
            if (this.m < i3) {
                this.l = i2;
                this.m = i3;
            }
        }
    }

    private void e() {
        if (this.n < 30) {
            PathOpenListNode pathOpenListNode = (PathOpenListNode) this.q.poll();
            if (pathOpenListNode != null) {
                b(pathOpenListNode);
            }
            PathOpenListNode pathOpenListNode2 = (PathOpenListNode) this.q.peek();
            if (pathOpenListNode2 != null) {
                this.s = pathOpenListNode2.score;
                return;
            }
            return;
        }
        this.s = Integer.MAX_VALUE;
        PathOpenListNode pathOpenListNode3 = (PathOpenListNode) this.q.peek();
        if (pathOpenListNode3 != null) {
            this.s = pathOpenListNode3.score;
        }
    }

    public FastNodeQueue() {
        f();
    }

    private void b(PathOpenListNode pathOpenListNode) {
        this.n++;
        a(this.n, pathOpenListNode);
        if (this.n > a) {
            a = this.n;
        }
    }

    private void c(PathOpenListNode pathOpenListNode) {
        this.q.offer(pathOpenListNode);
        if (pathOpenListNode.score < this.s) {
            this.s = pathOpenListNode.score;
        }
        if (this.q.size() > b) {
            b = this.q.size();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue
    public void a(PathOpenListNode pathOpenListNode) {
        d++;
        boolean z = false;
        if (this.n < this.o.length - 1) {
            z = true;
        }
        if (z) {
            if (pathOpenListNode.score <= this.s) {
                b(pathOpenListNode);
                return;
            } else {
                c(pathOpenListNode);
                return;
            }
        }
        if (pathOpenListNode.score < this.m) {
            PathOpenListNode pathOpenListNode2 = this.o[this.l];
            this.o[this.l] = pathOpenListNode;
            d();
            c(pathOpenListNode2);
            return;
        }
        c(pathOpenListNode);
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue
    public PathOpenListNode a() {
        if (this.j == -2) {
            int i2 = this.k;
            c();
            this.t++;
            if (u < this.t) {
                u = this.t;
            }
            e++;
            if (i2 == this.k) {
                f++;
            }
        } else {
            this.t = 0;
        }
        if (this.k < this.s && this.j != -1) {
            PathOpenListNode[] pathOpenListNodeArr = this.o;
            PathOpenListNode pathOpenListNode = pathOpenListNodeArr[this.j];
            if (this.n != this.j) {
                pathOpenListNodeArr[this.j] = pathOpenListNodeArr[this.n];
                pathOpenListNodeArr[this.n] = null;
            } else {
                pathOpenListNodeArr[this.n] = null;
            }
            this.n--;
            this.j = -2;
            return pathOpenListNode;
        }
        PathOpenListNode pathOpenListNode2 = (PathOpenListNode) this.q.poll();
        e();
        return pathOpenListNode2;
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue
    public void b() {
        a((PathOpenListPool) null);
    }

    public void a(PathOpenListPool pathOpenListPool) {
        for (int i2 = 0; i2 < this.o.length; i2++) {
            if (this.o[i2] != null) {
                if (pathOpenListPool != null) {
                    pathOpenListPool.a(this.o[i2]);
                }
                this.o[i2] = null;
            }
        }
        this.n = -1;
        for (PathOpenListNode pathOpenListNode : this.q) {
            if (pathOpenListPool != null) {
                pathOpenListPool.a(pathOpenListNode);
            }
        }
        this.q.clear();
        f();
    }

    private void f() {
        this.j = -1;
        this.k = Integer.MAX_VALUE;
        this.l = -1;
        this.m = Integer.MIN_VALUE;
        this.s = Integer.MAX_VALUE;
    }
}
