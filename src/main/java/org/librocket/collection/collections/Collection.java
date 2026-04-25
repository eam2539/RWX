package org.librocket.collection.collections;

import org.librocket.collection.iterators.Iterable;
import org.librocket.collection.lists.Iterator;

/* JADX INFO: renamed from: org.a.a.a.a */
/* JADX INFO: loaded from: game-lib.jar:org/a/a/a/a.class */
public interface Collection extends LongCollection, Iterable {
    @Override // java.util.Collection, java.lang.Iterable, org.librocket.collection.iterators.Iterable
    /* JADX INFO: renamed from: a */
    Iterator iterator();
}
