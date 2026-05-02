package com.corrodinggames.rts.gameFramework.pathfinding;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/b.class */
class DirectAccessPathNodeArrayListIterator implements Iterator {
    private int b;
    private int c;
    private int d;
    final /* synthetic */ DirectAccessPathNodeArrayList a;

    DirectAccessPathNodeArrayListIterator(DirectAccessPathNodeArrayList directAccessPathNodeArrayList) {
        this.a = directAccessPathNodeArrayList;
        this.b = this.a.b;
        this.c = -1;
        this.d = DirectAccessPathNodeArrayList.e(this.a);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.b != 0;
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public PathOpenListNode next() {
        DirectAccessPathNodeArrayList directAccessPathNodeArrayList = this.a;
        int i = this.b;
        if (DirectAccessPathNodeArrayList.e(directAccessPathNodeArrayList) != this.d) {
            throw new ConcurrentModificationException();
        }
        if (i == 0) {
            throw new NoSuchElementException();
        }
        this.b = i - 1;
        PathOpenListNode[] pathOpenListNodeArr = directAccessPathNodeArrayList.c;
        int i2 = directAccessPathNodeArrayList.b - i;
        this.c = i2;
        return pathOpenListNodeArr[i2];
    }

    @Override // java.util.Iterator
    public void remove() {
        PathOpenListNode[] pathOpenListNodeArr = this.a.c;
        int i = this.c;
        if (DirectAccessPathNodeArrayList.e(this.a) != this.d) {
            throw new ConcurrentModificationException();
        }
        if (i < 0) {
            throw new IllegalStateException();
        }
        System.arraycopy(pathOpenListNodeArr, i + 1, pathOpenListNodeArr, i, this.b);
        DirectAccessPathNodeArrayList directAccessPathNodeArrayList = this.a;
        int i2 = directAccessPathNodeArrayList.b - 1;
        directAccessPathNodeArrayList.b = i2;
        pathOpenListNodeArr[i2] = null;
        this.c = -1;
        this.d = DirectAccessPathNodeArrayList.d(this.a);
    }
}
