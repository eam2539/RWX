package com.corrodinggames.rts.gameFramework.utility;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/o.class */
public final class TransactionalArrayList<T> extends AbstractList<T> implements Serializable, Cloneable, RandomAccess {
    /* JADX INFO: renamed from: c */
    public int size;

    /* JADX INFO: renamed from: a */
    public FastArrayList<ListOperationEntry> pendingOperations = new FastArrayList();

    /* JADX INFO: renamed from: b */
    public FastArrayList<ListOperationEntry> completedOperations = new FastArrayList();

    /* JADX INFO: renamed from: d */
    transient Object[] elements = EmptyArrays.EMPTY_OBJECT;

    static /* synthetic */ int e(TransactionalArrayList transactionalArrayList) {
        return transactionalArrayList.modCount;
    }

    static /* synthetic */ int d(TransactionalArrayList transactionalArrayList) {
        int i = transactionalArrayList.modCount + 1;
        transactionalArrayList.modCount = i;
        return i;
    }

    public void a(Object obj) {
        ListOperationEntry listOperationEntry;
        if (this.completedOperations.size != 0) {
            listOperationEntry = (ListOperationEntry) this.completedOperations.b();
        } else {
            listOperationEntry = new ListOperationEntry();
        }
        listOperationEntry.operation = ListOperation.add;
        listOperationEntry.value = obj;
        this.pendingOperations.add(listOperationEntry);
    }

    public void b(Object obj) {
        ListOperationEntry listOperationEntry;
        if (this.completedOperations.size != 0) {
            listOperationEntry = (ListOperationEntry) this.completedOperations.b();
        } else {
            listOperationEntry = new ListOperationEntry();
        }
        listOperationEntry.operation = ListOperation.remove;
        listOperationEntry.value = obj;
        this.pendingOperations.add(listOperationEntry);
    }

    public void a() {
        this.modCount++;
        if (this.pendingOperations.size != 0) {
            for (ListOperationEntry listOperationEntry : this.pendingOperations) {
                if (listOperationEntry.operation == ListOperation.add) {
                    Object obj = listOperationEntry.value;
                    if (obj == null) {
                        throw new RuntimeException("Trying to insert null into array");
                    }
                    add(obj);
                } else if (listOperationEntry.operation == ListOperation.remove) {
                    remove(listOperationEntry.value);
                } else {
                    throw new RuntimeException("Unknown operationType:" + listOperationEntry.operation);
                }
                listOperationEntry.value = null;
            }
            if (this.pendingOperations.size() < 100) {
                this.completedOperations.addAll(this.pendingOperations);
            }
            this.pendingOperations.clear();
        }
    }

    public Object[] b() {
        return this.elements;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean add(Object obj) {
        Object[] objArr = this.elements;
        int i = this.size;
        if (i == objArr.length) {
            Object[] objArr2 = new Object[i + (i < 6 ? 12 : i >> 1)];
            System.arraycopy(objArr, 0, objArr2, 0, i);
            objArr = objArr2;
            this.elements = objArr2;
        }
        objArr[i] = obj;
        this.size = i + 1;
        this.modCount++;
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int i, Object obj) {
        Object[] objArr = this.elements;
        int i2 = this.size;
        if (i > i2 || i < 0) {
            a(i, i2);
        }
        if (i2 < objArr.length) {
            System.arraycopy(objArr, i, objArr, i + 1, i2 - i);
        } else {
            Object[] objArr2 = new Object[a(i2)];
            System.arraycopy(objArr, 0, objArr2, 0, i);
            System.arraycopy(objArr, i, objArr2, i + 1, i2 - i);
            objArr = objArr2;
            this.elements = objArr2;
        }
        objArr[i] = obj;
        this.size = i2 + 1;
        this.modCount++;
    }

    private static int a(int i) {
        return i + (i < 6 ? 12 : i >> 1);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection collection) {
        Object[] array = collection.toArray();
        int length = array.length;
        if (length == 0) {
            return false;
        }
        Object[] objArr = this.elements;
        int i = this.size;
        int i2 = i + length;
        if (i2 > objArr.length) {
            Object[] objArr2 = new Object[a(i2 - 1)];
            System.arraycopy(objArr, 0, objArr2, 0, i);
            objArr = objArr2;
            this.elements = objArr2;
        }
        System.arraycopy(array, 0, objArr, i, length);
        this.size = i2;
        this.modCount++;
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    public boolean addAll(int i, Collection collection) {
        int i2 = this.size;
        if (i > i2 || i < 0) {
            a(i, i2);
        }
        Object[] array = collection.toArray();
        int length = array.length;
        if (length == 0) {
            return false;
        }
        Object[] objArr = this.elements;
        int i3 = i2 + length;
        if (i3 <= objArr.length) {
            System.arraycopy(objArr, i, objArr, i + length, i2 - i);
        } else {
            Object[] objArr2 = new Object[a(i3 - 1)];
            System.arraycopy(objArr, 0, objArr2, 0, i);
            System.arraycopy(objArr, i, objArr2, i + length, i2 - i);
            objArr = objArr2;
            this.elements = objArr2;
        }
        System.arraycopy(array, 0, objArr, i, length);
        this.size = i3;
        this.modCount++;
        return true;
    }

    static IndexOutOfBoundsException a(int i, int i2) {
        throw new IndexOutOfBoundsException("Invalid index " + i + ", size is " + i2);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized void clear() {
        this.pendingOperations.clear();
        if (this.size != 0) {
            Arrays.fill(this.elements, 0, this.size, (Object) null);
            this.size = 0;
            this.modCount++;
        }
    }

    public Object clone() {
        try {
            TransactionalArrayList transactionalArrayList = (TransactionalArrayList) super.clone();
            transactionalArrayList.elements = (Object[]) this.elements.clone();
            return transactionalArrayList;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public T get(int i) {
        if (i >= this.size) {
            a(i, this.size);
        }
        return (T) this.elements[i];
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.size;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object obj) {
        Object[] objArr = this.elements;
        int i = this.size;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(objArr[i2])) {
                    return true;
                }
            }
            return false;
        }
        for (int i3 = 0; i3 < i; i3++) {
            if (objArr[i3] == null) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object obj) {
        Object[] objArr = this.elements;
        int i = this.size;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(objArr[i2])) {
                    return i2;
                }
            }
            return -1;
        }
        for (int i3 = 0; i3 < i; i3++) {
            if (objArr[i3] == null) {
                return i3;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object obj) {
        Object[] objArr = this.elements;
        if (obj != null) {
            for (int i = this.size - 1; i >= 0; i--) {
                if (obj.equals(objArr[i])) {
                    return i;
                }
            }
            return -1;
        }
        for (int i2 = this.size - 1; i2 >= 0; i2--) {
            if (objArr[i2] == null) {
                return i2;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractList, java.util.List
    public T remove(int i) {
        Object[] objArr = this.elements;
        int i2 = this.size;
        if (i >= i2) {
            a(i, i2);
        }
        Object obj = objArr[i];
        int i3 = i2 - 1;
        System.arraycopy(objArr, i + 1, objArr, i, i3 - i);
        objArr[i3] = null;
        this.size = i3;
        this.modCount++;
        return (T) obj;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object obj) {
        Object[] objArr = this.elements;
        int i = this.size;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(objArr[i2])) {
                    int i3 = i - 1;
                    System.arraycopy(objArr, i2 + 1, objArr, i2, i3 - i2);
                    objArr[i3] = null;
                    this.size = i3;
                    this.modCount++;
                    return true;
                }
            }
            return false;
        }
        for (int i4 = 0; i4 < i; i4++) {
            if (objArr[i4] == null) {
                int i5 = i - 1;
                System.arraycopy(objArr, i4 + 1, objArr, i4, i5 - i4);
                objArr[i5] = null;
                this.size = i5;
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
        Object[] objArr = this.elements;
        int i3 = this.size;
        if (i >= i3) {
            throw new IndexOutOfBoundsException("fromIndex " + i + " >= size " + this.size);
        }
        if (i2 > i3) {
            throw new IndexOutOfBoundsException("toIndex " + i2 + " > size " + this.size);
        }
        if (i > i2) {
            throw new IndexOutOfBoundsException("fromIndex " + i + " > toIndex " + i2);
        }
        System.arraycopy(objArr, i2, objArr, i, i3 - i2);
        int i4 = i2 - i;
        Arrays.fill(objArr, i3 - i4, i3, (Object) null);
        this.size = i3 - i4;
        this.modCount++;
    }

    @Override // java.util.AbstractList, java.util.List
    public Object set(int i, Object obj) {
        Object[] objArr = this.elements;
        if (i >= this.size) {
            a(i, this.size);
        }
        Object obj2 = objArr[i];
        objArr[i] = obj;
        return obj2;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray() {
        int i = this.size;
        Object[] objArr = new Object[i];
        System.arraycopy(this.elements, 0, objArr, 0, i);
        return objArr;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray(Object[] objArr) {
        int i = this.size;
        if (objArr.length < i) {
            objArr = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), i);
        }
        System.arraycopy(this.elements, 0, objArr, 0, i);
        if (objArr.length > i) {
            objArr[i] = null;
        }
        return objArr;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
    public Iterator iterator() {
        return new TransactionalListIterator(this);
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public int hashCode() {
        Object[] objArr = this.elements;
        int iHashCode = 1;
        int i = this.size;
        for (int i2 = 0; i2 < i; i2++) {
            Object obj = objArr[i2];
            iHashCode = (31 * iHashCode) + (obj == null ? 0 : obj.hashCode());
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
        int i = this.size;
        if (list.size() != i) {
            return false;
        }
        Object[] objArr = this.elements;
        if (list instanceof RandomAccess) {
            for (int i2 = 0; i2 < i; i2++) {
                Object obj2 = objArr[i2];
                Object obj3 = list.get(i2);
                if (obj2 == null) {
                    if (obj3 != null) {
                        return false;
                    }
                } else if (!obj2.equals(obj3)) {
                    return false;
                }
            }
            return true;
        }
        Iterator it = list.iterator();
        for (int i3 = 0; i3 < i; i3++) {
            Object obj4 = objArr[i3];
            Object next = it.next();
            if (obj4 == null) {
                if (next != null) {
                    return false;
                }
            } else if (!obj4.equals(next)) {
                return false;
            }
        }
        return true;
    }
}
