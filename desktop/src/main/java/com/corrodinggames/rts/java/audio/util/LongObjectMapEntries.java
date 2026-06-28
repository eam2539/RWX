package com.corrodinggames.rts.java.audio.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/j.class */
public class LongObjectMapEntries extends LongObjectMapIterator implements Iterable, Iterator {
    private LongObjectMapEntry f;

    @Override // com.corrodinggames.rts.java.audio.util.LongObjectMapIterator
    public /* bridge */ /* synthetic */ void b() {
        super.b();
    }

    public LongObjectMapEntries(LongObjectMap longObjectMap) {
        super(longObjectMap);
        this.f = new LongObjectMapEntry();
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public LongObjectMapEntry next() {
        if (!this.a) {
            throw new NoSuchElementException();
        }
        if (!this.e) {
            throw new AudioException("#iterator() cannot FastArrayList used nested.");
        }
        long[] jArr = this.b.b;
        if (this.c == -1) {
            this.f.a = 0L;
            this.f.b = this.b.f;
        } else {
            this.f.a = jArr[this.c];
            this.f.b = this.b.c[this.c];
        }
        this.d = this.c;
        c();
        return this.f;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (this.e) {
            return this.a;
        }
        throw new AudioException("#iterator() cannot FastArrayList used nested.");
    }

    @Override // java.lang.Iterable
    public Iterator iterator() {
        return this;
    }

    @Override // com.corrodinggames.rts.java.audio.util.LongObjectMapIterator, java.util.Iterator
    public void remove() {
        super.remove();
    }
}
