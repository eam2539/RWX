package com.corrodinggames.rts.java.audio.util;

import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/p.class */
public class ObjectMapEntries extends ObjectMapIterator {
    ObjectMapEntry a;

    @Override // com.corrodinggames.rts.java.audio.util.ObjectMapIterator, java.util.Iterator
    public /* bridge */ /* synthetic */ void remove() {
        super.remove();
    }

    @Override // com.corrodinggames.rts.java.audio.util.ObjectMapIterator
    public /* bridge */ /* synthetic */ void c() {
        super.c();
    }

    public ObjectMapEntries(ObjectMap objectMap) {
        super(objectMap);
        this.a = new ObjectMapEntry();
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public ObjectMapEntry next() {
        if (!this.b) {
            throw new NoSuchElementException();
        }
        if (!this.f) {
            throw new AudioException("#iterator() cannot FastArrayList used nested.");
        }
        Object[] objArr = this.c.b;
        this.a.a = objArr[this.d];
        this.a.b = this.c.c[this.d];
        this.e = this.d;
        d();
        return this.a;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (this.f) {
            return this.b;
        }
        throw new AudioException("#iterator() cannot FastArrayList used nested.");
    }

    @Override // java.lang.Iterable
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public ObjectMapEntries iterator() {
        return this;
    }
}
