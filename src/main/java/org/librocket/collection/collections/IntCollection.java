package org.librocket.collection.collections;

import org.librocket.collection.iterators.IntIterable;
import org.librocket.collection.lists.IntIterator;

/* JADX INFO: renamed from: org.a.a.a.b */
/* JADX INFO: loaded from: game-lib.jar:org/a/a/a/b.class */
public interface IntCollection extends LongCollection, IntIterable {
    @Override // java.util.Collection, java.lang.Iterable, org.librocket.collection.iterators.IntIterable
    /* JADX INFO: renamed from: a */
    IntIterator iterator();
}
