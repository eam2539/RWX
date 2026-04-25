package com.corrodinggames.rts.gameFramework.pathfinding;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/a.class */
public final class DirectAccessPathNodeArrayList extends AbstractList<PathOpenListNode> implements Serializable, Cloneable, RandomAccess {
    public static final PathOpenListNode[] a = new PathOpenListNode[0];
    public int b;
    transient PathOpenListNode[] c;

    static /* synthetic */ int e(DirectAccessPathNodeArrayList directAccessPathNodeArrayList) {
        return directAccessPathNodeArrayList.modCount;
    }

    static /* synthetic */ int d(DirectAccessPathNodeArrayList directAccessPathNodeArrayList) {
        int i = directAccessPathNodeArrayList.modCount + 1;
        directAccessPathNodeArrayList.modCount = i;
        return i;
    }

    public DirectAccessPathNodeArrayList(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("capacity < 0: " + i);
        }
        this.c = i == 0 ? a : new PathOpenListNode[i];
    }

    public DirectAccessPathNodeArrayList() {
        this.c = a;
    }

    public PathOpenListNode[] a() {
        return this.c;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public boolean add(PathOpenListNode pathOpenListNode) {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int i = this.b;
        if (i == pathOpenListNodeArr.length) {
            PathOpenListNode[] pathOpenListNodeArr2 = new PathOpenListNode[i + (i < 6 ? 12 : i >> 1)];
            System.arraycopy(pathOpenListNodeArr, 0, pathOpenListNodeArr2, 0, i);
            pathOpenListNodeArr = pathOpenListNodeArr2;
            this.c = pathOpenListNodeArr2;
        }
        pathOpenListNodeArr[i] = pathOpenListNode;
        this.b = i + 1;
        this.modCount++;
        return true;
    }

    public void b(PathOpenListNode pathOpenListNode) {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int i = this.b;
        if (i == pathOpenListNodeArr.length) {
            PathOpenListNode[] pathOpenListNodeArr2 = new PathOpenListNode[i + (i < 6 ? 12 : i >> 1)];
            System.arraycopy(pathOpenListNodeArr, 0, pathOpenListNodeArr2, 0, i);
            pathOpenListNodeArr = pathOpenListNodeArr2;
            this.c = pathOpenListNodeArr2;
        }
        pathOpenListNodeArr[i] = pathOpenListNode;
        this.b = i + 1;
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public void add(int i, PathOpenListNode pathOpenListNode) {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int i2 = this.b;
        if (i > i2 || i < 0) {
            a(i, i2);
        }
        if (i2 < pathOpenListNodeArr.length) {
            System.arraycopy(pathOpenListNodeArr, i, pathOpenListNodeArr, i + 1, i2 - i);
        } else {
            PathOpenListNode[] pathOpenListNodeArr2 = new PathOpenListNode[c(i2)];
            System.arraycopy(pathOpenListNodeArr, 0, pathOpenListNodeArr2, 0, i);
            System.arraycopy(pathOpenListNodeArr, i, pathOpenListNodeArr2, i + 1, i2 - i);
            pathOpenListNodeArr = pathOpenListNodeArr2;
            this.c = pathOpenListNodeArr2;
        }
        pathOpenListNodeArr[i] = pathOpenListNode;
        this.b = i2 + 1;
        this.modCount++;
    }

    private static int c(int i) {
        return i + (i < 6 ? 12 : i >> 1);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection collection) {
        PathOpenListNode[] pathOpenListNodeArr = (PathOpenListNode[]) collection.toArray();
        int length = pathOpenListNodeArr.length;
        if (length == 0) {
            return false;
        }
        PathOpenListNode[] pathOpenListNodeArr2 = this.c;
        int i = this.b;
        int i2 = i + length;
        if (i2 > pathOpenListNodeArr2.length) {
            PathOpenListNode[] pathOpenListNodeArr3 = new PathOpenListNode[c(i2 - 1)];
            System.arraycopy(pathOpenListNodeArr2, 0, pathOpenListNodeArr3, 0, i);
            pathOpenListNodeArr2 = pathOpenListNodeArr3;
            this.c = pathOpenListNodeArr3;
        }
        System.arraycopy(pathOpenListNodeArr, 0, pathOpenListNodeArr2, i, length);
        this.b = i2;
        this.modCount++;
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    public boolean addAll(int i, Collection collection) {
        int i2 = this.b;
        if (i > i2 || i < 0) {
            a(i, i2);
        }
        PathOpenListNode[] pathOpenListNodeArr = (PathOpenListNode[]) collection.toArray();
        int length = pathOpenListNodeArr.length;
        if (length == 0) {
            return false;
        }
        PathOpenListNode[] pathOpenListNodeArr2 = this.c;
        int i3 = i2 + length;
        if (i3 <= pathOpenListNodeArr2.length) {
            System.arraycopy(pathOpenListNodeArr2, i, pathOpenListNodeArr2, i + length, i2 - i);
        } else {
            PathOpenListNode[] pathOpenListNodeArr3 = new PathOpenListNode[c(i3 - 1)];
            System.arraycopy(pathOpenListNodeArr2, 0, pathOpenListNodeArr3, 0, i);
            System.arraycopy(pathOpenListNodeArr2, i, pathOpenListNodeArr3, i + length, i2 - i);
            pathOpenListNodeArr2 = pathOpenListNodeArr3;
            this.c = pathOpenListNodeArr3;
        }
        System.arraycopy(pathOpenListNodeArr, 0, pathOpenListNodeArr2, i, length);
        this.b = i3;
        this.modCount++;
        return true;
    }

    static IndexOutOfBoundsException a(int i, int i2) {
        throw new IndexOutOfBoundsException("Invalid index " + i + ", size is " + i2);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        if (this.b != 0) {
            Arrays.fill(this.c, 0, this.b, (Object) null);
            this.b = 0;
            this.modCount++;
        }
    }

    public Object clone() {
        try {
            DirectAccessPathNodeArrayList directAccessPathNodeArrayList = (DirectAccessPathNodeArrayList) super.clone();
            directAccessPathNodeArrayList.c = (PathOpenListNode[]) this.c.clone();
            return directAccessPathNodeArrayList;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public PathOpenListNode get(int i) {
        if (i >= this.b) {
            a(i, this.b);
        }
        return this.c[i];
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.b;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean isEmpty() {
        return this.b == 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object obj) {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int i = this.b;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(pathOpenListNodeArr[i2])) {
                    return true;
                }
            }
            return false;
        }
        for (int i3 = 0; i3 < i; i3++) {
            if (pathOpenListNodeArr[i3] == null) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object obj) {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int i = this.b;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(pathOpenListNodeArr[i2])) {
                    return i2;
                }
            }
            return -1;
        }
        for (int i3 = 0; i3 < i; i3++) {
            if (pathOpenListNodeArr[i3] == null) {
                return i3;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object obj) {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        if (obj != null) {
            for (int i = this.b - 1; i >= 0; i--) {
                if (obj.equals(pathOpenListNodeArr[i])) {
                    return i;
                }
            }
            return -1;
        }
        for (int i2 = this.b - 1; i2 >= 0; i2--) {
            if (pathOpenListNodeArr[i2] == null) {
                return i2;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public PathOpenListNode remove(int i) {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int i2 = this.b;
        if (i >= i2) {
            a(i, i2);
        }
        PathOpenListNode pathOpenListNode = pathOpenListNodeArr[i];
        int i3 = i2 - 1;
        System.arraycopy(pathOpenListNodeArr, i + 1, pathOpenListNodeArr, i, i3 - i);
        pathOpenListNodeArr[i3] = null;
        this.b = i3;
        this.modCount++;
        return pathOpenListNode;
    }

    public PathOpenListNode b() {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int i = this.b - 1;
        PathOpenListNode pathOpenListNode = pathOpenListNodeArr[i];
        pathOpenListNodeArr[i] = null;
        this.b = i;
        return pathOpenListNode;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object obj) {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int i = this.b;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(pathOpenListNodeArr[i2])) {
                    int i3 = i - 1;
                    System.arraycopy(pathOpenListNodeArr, i2 + 1, pathOpenListNodeArr, i2, i3 - i2);
                    pathOpenListNodeArr[i3] = null;
                    this.b = i3;
                    this.modCount++;
                    return true;
                }
            }
            return false;
        }
        for (int i4 = 0; i4 < i; i4++) {
            if (pathOpenListNodeArr[i4] == null) {
                int i5 = i - 1;
                System.arraycopy(pathOpenListNodeArr, i4 + 1, pathOpenListNodeArr, i4, i5 - i4);
                pathOpenListNodeArr[i5] = null;
                this.b = i5;
                this.modCount++;
                return true;
            }
        }
        return false;
    }

    @Override // java.util.AbstractList
    protected void removeRange(int i, int i2) {
        if (i == i2) {
            return;
        }
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int i3 = this.b;
        if (i >= i3) {
            throw new IndexOutOfBoundsException("fromIndex " + i + " >= size " + this.b);
        }
        if (i2 > i3) {
            throw new IndexOutOfBoundsException("toIndex " + i2 + " > size " + this.b);
        }
        if (i > i2) {
            throw new IndexOutOfBoundsException("fromIndex " + i + " > toIndex " + i2);
        }
        System.arraycopy(pathOpenListNodeArr, i2, pathOpenListNodeArr, i, i3 - i2);
        int i4 = i2 - i;
        Arrays.fill(pathOpenListNodeArr, i3 - i4, i3, (Object) null);
        this.b = i3 - i4;
        this.modCount++;
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public PathOpenListNode set(int i, PathOpenListNode pathOpenListNode) {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        if (i >= this.b) {
            a(i, this.b);
        }
        PathOpenListNode pathOpenListNode2 = pathOpenListNodeArr[i];
        pathOpenListNodeArr[i] = pathOpenListNode;
        return pathOpenListNode2;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray() {
        int i = this.b;
        Object[] objArr = new Object[i];
        System.arraycopy(this.c, 0, objArr, 0, i);
        return objArr;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray(Object[] objArr) {
        int i = this.b;
        if (objArr.length < i) {
            objArr = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), i);
        }
        System.arraycopy(this.c, 0, objArr, 0, i);
        if (objArr.length > i) {
            objArr[i] = null;
        }
        return objArr;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
    public Iterator iterator() {
        return new DirectAccessPathNodeArrayListIterator(this);
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public int hashCode() {
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        int iHashCode = 1;
        int i = this.b;
        for (int i2 = 0; i2 < i; i2++) {
            PathOpenListNode pathOpenListNode = pathOpenListNodeArr[i2];
            iHashCode = (31 * iHashCode) + (pathOpenListNode == null ? 0 : pathOpenListNode.hashCode());
        }
        return iHashCode;
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof List)) {
            return false;
        }
        List list = (List) obj;
        int i = this.b;
        if (list.size() != i) {
            return false;
        }
        PathOpenListNode[] pathOpenListNodeArr = this.c;
        if (list instanceof RandomAccess) {
            for (int i2 = 0; i2 < i; i2++) {
                PathOpenListNode pathOpenListNode = pathOpenListNodeArr[i2];
                Object obj2 = list.get(i2);
                if (pathOpenListNode == null) {
                    if (obj2 != null) {
                        return false;
                    }
                } else if (!pathOpenListNode.equals(obj2)) {
                    return false;
                }
            }
            return true;
        }
        Iterator it = list.iterator();
        for (int i3 = 0; i3 < i; i3++) {
            PathOpenListNode pathOpenListNode2 = pathOpenListNodeArr[i3];
            Object next = it.next();
            if (pathOpenListNode2 == null) {
                if (next != null) {
                    return false;
                }
            } else if (!pathOpenListNode2.equals(next)) {
                return false;
            }
        }
        return true;
    }
}
