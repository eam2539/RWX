package com.corrodinggames.rts.gameFramework.utility;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/p.class */
class TransactionalListIterator implements Iterator {
    private int b;
    private int c;
    private int d;
    final /* synthetic */ TransactionalArrayList a;

    TransactionalListIterator(TransactionalArrayList transactionalArrayList) {
        this.a = transactionalArrayList;
        this.b = this.a.c;
        this.c = -1;
        this.d = TransactionalArrayList.e(this.a);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.b != 0;
    }

    @Override // java.util.Iterator
    public Object next() {
        TransactionalArrayList transactionalArrayList = this.a;
        int i = this.b;
        if (TransactionalArrayList.e(transactionalArrayList) != this.d) {
            throw new ConcurrentModificationException();
        }
        if (i == 0) {
            throw new NoSuchElementException();
        }
        this.b = i - 1;
        Object[] objArr = transactionalArrayList.d;
        int i2 = transactionalArrayList.c - i;
        this.c = i2;
        return objArr[i2];
    }

    @Override // java.util.Iterator
    public void remove() {
        Object[] objArr = this.a.d;
        int i = this.c;
        if (TransactionalArrayList.e(this.a) != this.d) {
            throw new ConcurrentModificationException();
        }
        if (i < 0) {
            throw new IllegalStateException();
        }
        System.arraycopy(objArr, i + 1, objArr, i, this.b);
        TransactionalArrayList transactionalArrayList = this.a;
        int i2 = transactionalArrayList.c - 1;
        transactionalArrayList.c = i2;
        objArr[i2] = null;
        this.c = -1;
        this.d = TransactionalArrayList.d(this.a);
    }
}
