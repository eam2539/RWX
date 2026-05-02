package com.corrodinggames.rts.gameFramework.utility;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/h.class */
class CircularDequeIterator implements Iterator {
    private int b;
    private int c;
    private int d;
    final /* synthetic */ CircularDeque a;

    CircularDequeIterator(CircularDeque circularDeque) {
        this.a = circularDeque;
        this.b = this.a.c;
        this.c = this.a.d;
        this.d = -1;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.b != this.c;
    }

    @Override // java.util.Iterator
    public Object next() {
        if (this.b != this.c) {
            Object obj = this.a.b[this.b];
            if (this.a.d != this.c || obj == null) {
                throw new ConcurrentModificationException();
            }
            this.d = this.b;
            this.b = (this.b + 1) & (this.a.b.length - 1);
            return obj;
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.Iterator
    public void remove() {
        if (this.d >= 0) {
            if (this.a.a(this.d)) {
                this.b = (this.b - 1) & (this.a.b.length - 1);
                this.c = this.a.d;
            }
            this.d = -1;
            return;
        }
        throw new IllegalStateException();
    }
}
