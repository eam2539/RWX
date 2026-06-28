package com.corrodinggames.rts.java.audio.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/f.class */
public class IntObjectMapEntries extends IntObjectMapIterator implements Iterable, Iterator {
    private IntObjectMapEntry f;

    @Override // com.corrodinggames.rts.java.audio.util.IntObjectMapIterator
    public /* bridge */ /* synthetic */ void b() {
        super.b();
    }

    public IntObjectMapEntries(IntObjectMap intObjectMap) {
        super(intObjectMap);
        this.f = new IntObjectMapEntry();
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public IntObjectMapEntry next() {
        if (!this.a) {
            throw new NoSuchElementException();
        }
        if (!this.e) {
            throw new AudioException("#iterator() cannot FastArrayList used nested.");
        }
        int[] iArr = this.b.b;
        if (this.c == -1) {
            this.f.a = 0;
            this.f.b = this.b.f;
        } else {
            this.f.a = iArr[this.c];
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

    @Override // com.corrodinggames.rts.java.audio.util.IntObjectMapIterator, java.util.Iterator
    public void remove() {
        super.remove();
    }
}
