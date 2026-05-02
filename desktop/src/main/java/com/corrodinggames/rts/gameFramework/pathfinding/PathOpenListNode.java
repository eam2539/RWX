package com.corrodinggames.rts.gameFramework.pathfinding;

import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/n.class */
public final class PathOpenListNode implements Comparable<PathOpenListNode> {
    public short a;
    public short b;
    public int c;

    public final void a(short s, short s2) {
        this.a = s;
        this.b = s2;
    }

    public final void a(int i, int i2, int i3) {
        int i4 = i2 - this.a;
        int i5 = i3 - this.b;
        int i6 = i4 > 0 ? i4 : -i4;
        int i7 = i5 > 0 ? i5 : -i5;
        this.c = i + ((i6 + i7) * 11) + (Utility.min(i6, i7) * (-7));
    }

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public final int compareTo(PathOpenListNode pathOpenListNode) {
        if (this.c == pathOpenListNode.c) {
            if (this.a - pathOpenListNode.a != 0) {
                return this.a - pathOpenListNode.a;
            }
            return this.b - pathOpenListNode.b;
        }
        return this.c - pathOpenListNode.c;
    }

    public String toString() {
        return "PathOpenListNode [x=" + ((int) this.a) + ", y=" + ((int) this.b) + ", score=" + this.c + "]";
    }
}
