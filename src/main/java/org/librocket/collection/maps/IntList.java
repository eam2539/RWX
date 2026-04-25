package org.librocket.collection.maps;

import org.librocket.collection.collections.IntCollection;
import org.librocket.collection.lists.IntIterator;
import org.librocket.collection.sets.IntListIterator;

/* JADX INFO: renamed from: org.a.a.d.b */
/* JADX INFO: loaded from: game-lib.jar:org/a/a/d/b.class */
public interface IntList extends IntCollection, LongList {
    @Override // org.librocket.collection.collections.IntCollection, java.util.Collection, java.lang.Iterable, org.librocket.collection.iterators.IntIterable
    /* JADX INFO: renamed from: a */
    IntIterator iterator();

    @Override // java.util.List
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    IntListIterator listIterator();

    @Override // java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    IntListIterator listIterator(int i);

    @Override // java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    IntList subList(int i, int i2);

    @Override // java.util.List
    @Deprecated
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    Integer remove(int i);
}
