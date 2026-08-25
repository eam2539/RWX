package com.corrodinggames.rts.gameFramework.pathfinding;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/b.class */
class DirectAccessPathNodeArrayListIterator implements Iterator {
    /* JADX INFO: renamed from: b */
    private int remaining;

    /* JADX INFO: renamed from: c */
    private int lastRetrieved;

    /* JADX INFO: renamed from: d */
    private int expectedModCount;
    final /* synthetic */ DirectAccessPathNodeArrayList a;

    DirectAccessPathNodeArrayListIterator(DirectAccessPathNodeArrayList directAccessPathNodeArrayList) {
        this.a = directAccessPathNodeArrayList;
        this.remaining = this.a.size;
        this.lastRetrieved = -1;
        this.expectedModCount = DirectAccessPathNodeArrayList.e(this.a);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.remaining != 0;
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public PathOpenListNode next() {
        DirectAccessPathNodeArrayList directAccessPathNodeArrayList = this.a;
        int i = this.remaining;
        if (DirectAccessPathNodeArrayList.e(directAccessPathNodeArrayList) != this.expectedModCount) {
            throw new ConcurrentModificationException();
        }
        if (i == 0) {
            throw new NoSuchElementException();
        }
        this.remaining = i - 1;
        PathOpenListNode[] pathOpenListNodeArr = directAccessPathNodeArrayList.elements;
        int i2 = directAccessPathNodeArrayList.size - i;
        this.lastRetrieved = i2;
        return pathOpenListNodeArr[i2];
    }

    @Override // java.util.Iterator
    public void remove() {
        PathOpenListNode[] pathOpenListNodeArr = this.a.elements;
        int i = this.lastRetrieved;
        if (DirectAccessPathNodeArrayList.e(this.a) != this.expectedModCount) {
            throw new ConcurrentModificationException();
        }
        if (i < 0) {
            throw new IllegalStateException();
        }
        System.arraycopy(pathOpenListNodeArr, i + 1, pathOpenListNodeArr, i, this.remaining);
        DirectAccessPathNodeArrayList directAccessPathNodeArrayList = this.a;
        int i2 = directAccessPathNodeArrayList.size - 1;
        directAccessPathNodeArrayList.size = i2;
        pathOpenListNodeArr[i2] = null;
        this.lastRetrieved = -1;
        this.expectedModCount = DirectAccessPathNodeArrayList.d(this.a);
    }
}
