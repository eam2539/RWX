package org.librocket.collection.maps;

import org.librocket.collection.collections.Collection;
import org.librocket.collection.lists.Iterator;
import org.librocket.collection.sets.ListIterator;

/* JADX INFO: renamed from: org.a.a.d.a */
/* JADX INFO: loaded from: game-lib.jar:org/a/a/d/a.class */
public interface Map extends Collection, LongList {
    @Override // org.librocket.collection.collections.Collection, java.util.Collection, java.lang.Iterable, org.librocket.collection.iterators.Iterable
    /* JADX INFO: renamed from: a */
    Iterator iterator();

    @Override // java.util.List
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    ListIterator listIterator();

    @Override // java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    ListIterator listIterator(int i);

    @Override // java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    Map subList(int i, int i2);

    @Override // java.util.List
    @Deprecated
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    Float remove(int i);
}
