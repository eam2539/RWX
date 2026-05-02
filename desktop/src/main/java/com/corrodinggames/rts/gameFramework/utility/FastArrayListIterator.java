package com.corrodinggames.rts.gameFramework.utility;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/n.class */
class FastArrayListIterator implements Iterator {

    /* JADX INFO: renamed from: b */
    private int remaining;

    /* JADX INFO: renamed from: c */
    private int lastPosition;

    /* JADX INFO: renamed from: d */
    private int expectedModCount;

    /* JADX INFO: renamed from: a */
    final /* synthetic */ FastArrayList list;

    FastArrayListIterator(FastArrayList fastArrayList) {
        this.list = fastArrayList;
        this.remaining = this.list.size;
        this.lastPosition = -1;
        this.expectedModCount = FastArrayList.e(this.list);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.remaining != 0;
    }

    @Override // java.util.Iterator
    public Object next() {
        FastArrayList fastArrayList = this.list;
        int i = this.remaining;
        if (FastArrayList.e(fastArrayList) != this.expectedModCount) {
            throw new ConcurrentModificationException();
        }
        if (i == 0) {
            throw new NoSuchElementException();
        }
        this.remaining = i - 1;
        Object[] objArr = fastArrayList.elements;
        int i2 = fastArrayList.size - i;
        this.lastPosition = i2;
        return objArr[i2];
    }

    @Override // java.util.Iterator
    public void remove() {
        Object[] objArr = this.list.elements;
        int i = this.lastPosition;
        if (FastArrayList.e(this.list) != this.expectedModCount) {
            throw new ConcurrentModificationException();
        }
        if (i < 0) {
            throw new IllegalStateException();
        }
        System.arraycopy(objArr, i + 1, objArr, i, this.remaining);
        FastArrayList fastArrayList = this.list;
        int i2 = fastArrayList.size - 1;
        fastArrayList.size = i2;
        objArr[i2] = null;
        this.lastPosition = -1;
        this.expectedModCount = FastArrayList.d(this.list);
    }
}
