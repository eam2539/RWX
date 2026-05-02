package org.librocket.collection.collections;

import org.librocket.collection.iterators.LongIterable;
import org.librocket.collection.lists.ShortIterator;

/* JADX INFO: renamed from: org.a.a.a.d */
/* JADX INFO: loaded from: game-lib.jar:org/a/a/a/d.class */
public interface ShortCollection extends LongCollection, LongIterable {
    @Override // java.util.Collection, java.lang.Iterable, org.librocket.collection.iterators.LongIterable
    /* JADX INFO: renamed from: a */
    ShortIterator iterator();
}
