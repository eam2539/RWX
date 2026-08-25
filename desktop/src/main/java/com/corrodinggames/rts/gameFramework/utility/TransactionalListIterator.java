package com.corrodinggames.rts.gameFramework.utility;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/p.class */
class TransactionalListIterator implements Iterator {
    /* JADX INFO: renamed from: b */
    private int remaining;

    /* JADX INFO: renamed from: c */
    private int lastRetrieved;

    /* JADX INFO: renamed from: d */
    private int expectedModCount;
    final /* synthetic */ TransactionalArrayList a;

    TransactionalListIterator(TransactionalArrayList transactionalArrayList) {
        this.a = transactionalArrayList;
        this.remaining = this.a.size;
        this.lastRetrieved = -1;
        this.expectedModCount = TransactionalArrayList.e(this.a);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.remaining != 0;
    }

    @Override // java.util.Iterator
    public Object next() {
        TransactionalArrayList transactionalArrayList = this.a;
        int i = this.remaining;
        if (TransactionalArrayList.e(transactionalArrayList) != this.expectedModCount) {
            throw new ConcurrentModificationException();
        }
        if (i == 0) {
            throw new NoSuchElementException();
        }
        this.remaining = i - 1;
        Object[] objArr = transactionalArrayList.elements;
        int i2 = transactionalArrayList.size - i;
        this.lastRetrieved = i2;
        return objArr[i2];
    }

    @Override // java.util.Iterator
    public void remove() {
        Object[] objArr = this.a.elements;
        int i = this.lastRetrieved;
        if (TransactionalArrayList.e(this.a) != this.expectedModCount) {
            throw new ConcurrentModificationException();
        }
        if (i < 0) {
            throw new IllegalStateException();
        }
        System.arraycopy(objArr, i + 1, objArr, i, this.remaining);
        TransactionalArrayList transactionalArrayList = this.a;
        int i2 = transactionalArrayList.size - 1;
        transactionalArrayList.size = i2;
        objArr[i2] = null;
        this.lastRetrieved = -1;
        this.expectedModCount = TransactionalArrayList.d(this.a);
    }
}
