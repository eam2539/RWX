package com.corrodinggames.rts.gameFramework.pathfinding;

import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/n.class */
public final class PathOpenListNode implements Comparable<PathOpenListNode> {

    /* JADX INFO: renamed from: a */
    public short x;

    /* JADX INFO: renamed from: b */
    public short y;

    /* JADX INFO: renamed from: c */
    public int score;

    public final void a(short s, short s2) {
        this.x = s;
        this.y = s2;
    }

    public final void a(int i, int i2, int i3) {
        int i4 = i2 - this.x;
        int i5 = i3 - this.y;
        int i6 = i4 > 0 ? i4 : -i4;
        int i7 = i5 > 0 ? i5 : -i5;
        this.score = i + ((i6 + i7) * 11) + (Utility.min(i6, i7) * (-7));
    }

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public final int compareTo(PathOpenListNode pathOpenListNode) {
        if (this.score == pathOpenListNode.score) {
            if (this.x - pathOpenListNode.x != 0) {
                return this.x - pathOpenListNode.x;
            }
            return this.y - pathOpenListNode.y;
        }
        return this.score - pathOpenListNode.score;
    }

    public String toString() {
        return "PathOpenListNode [x=" + ((int) this.x) + ", y=" + ((int) this.y) + ", score=" + this.score + "]";
    }
}
