package com.corrodinggames.rts.gameFramework.utility;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/h.class */
class CircularDequeIterator implements Iterator {

    /* JADX INFO: renamed from: b */
    private int cursor;

    /* JADX INFO: renamed from: c */
    private int end;

    /* JADX INFO: renamed from: d */
    private int lastRetrieved;
    final /* synthetic */ CircularDeque a;

    CircularDequeIterator(CircularDeque circularDeque) {
        this.a = circularDeque;
        this.cursor = this.a.head;
        this.end = this.a.tail;
        this.lastRetrieved = -1;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.cursor != this.end;
    }

    @Override // java.util.Iterator
    public Object next() {
        if (this.cursor != this.end) {
            Object obj = this.a.elements[this.cursor];
            if (this.a.tail != this.end || obj == null) {
                throw new ConcurrentModificationException();
            }
            this.lastRetrieved = this.cursor;
            this.cursor = (this.cursor + 1) & (this.a.elements.length - 1);
            return obj;
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.Iterator
    public void remove() {
        if (this.lastRetrieved >= 0) {
            if (this.a.a(this.lastRetrieved)) {
                this.cursor = (this.cursor - 1) & (this.a.elements.length - 1);
                this.end = this.a.tail;
            }
            this.lastRetrieved = -1;
            return;
        }
        throw new IllegalStateException();
    }
}
