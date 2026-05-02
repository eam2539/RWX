package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.game.units.BaseUnit;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.u */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/u.class */
public final class UnitList extends AbstractList<BaseUnit> implements Serializable, Cloneable, RandomAccess {
    public static final BaseUnit[] a = new BaseUnit[0];
    public int b;
    transient BaseUnit[] c = a;

    static /* synthetic */ int e(UnitList unitList) {
        return unitList.modCount;
    }

    static /* synthetic */ int d(UnitList unitList) {
        int i = unitList.modCount + 1;
        unitList.modCount = i;
        return i;
    }

    public BaseUnit[] a() {
        return this.c;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public boolean add(BaseUnit baseUnit) {
        BaseUnit[] baseUnitArr = this.c;
        int i = this.b;
        if (i == baseUnitArr.length) {
            BaseUnit[] baseUnitArr2 = new BaseUnit[i + (i < 6 ? 12 : i >> 1)];
            System.arraycopy(baseUnitArr, 0, baseUnitArr2, 0, i);
            baseUnitArr = baseUnitArr2;
            this.c = baseUnitArr2;
        }
        baseUnitArr[i] = baseUnit;
        this.b = i + 1;
        this.modCount++;
        return true;
    }

    public final void b(BaseUnit baseUnit) {
        BaseUnit[] baseUnitArr = this.c;
        int i = this.b;
        if (i == baseUnitArr.length) {
            BaseUnit[] baseUnitArr2 = new BaseUnit[i + (i < 6 ? 12 : i >> 1)];
            System.arraycopy(baseUnitArr, 0, baseUnitArr2, 0, i);
            baseUnitArr = baseUnitArr2;
            this.c = baseUnitArr2;
        }
        baseUnitArr[i] = baseUnit;
        this.b = i + 1;
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public void add(int i, BaseUnit baseUnit) {
        BaseUnit[] baseUnitArr = this.c;
        int i2 = this.b;
        if (i > i2 || i < 0) {
            a(i, i2);
        }
        if (i2 < baseUnitArr.length) {
            System.arraycopy(baseUnitArr, i, baseUnitArr, i + 1, i2 - i);
        } else {
            BaseUnit[] baseUnitArr2 = new BaseUnit[c(i2)];
            System.arraycopy(baseUnitArr, 0, baseUnitArr2, 0, i);
            System.arraycopy(baseUnitArr, i, baseUnitArr2, i + 1, i2 - i);
            baseUnitArr = baseUnitArr2;
            this.c = baseUnitArr2;
        }
        baseUnitArr[i] = baseUnit;
        this.b = i2 + 1;
        this.modCount++;
    }

    private static int c(int i) {
        return i + (i < 6 ? 12 : i >> 1);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection collection) {
        BaseUnit[] baseUnitArr = (BaseUnit[]) collection.toArray();
        int length = baseUnitArr.length;
        if (length == 0) {
            return false;
        }
        BaseUnit[] baseUnitArr2 = this.c;
        int i = this.b;
        int i2 = i + length;
        if (i2 > baseUnitArr2.length) {
            BaseUnit[] baseUnitArr3 = new BaseUnit[c(i2 - 1)];
            System.arraycopy(baseUnitArr2, 0, baseUnitArr3, 0, i);
            baseUnitArr2 = baseUnitArr3;
            this.c = baseUnitArr3;
        }
        System.arraycopy(baseUnitArr, 0, baseUnitArr2, i, length);
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
        BaseUnit[] baseUnitArr = (BaseUnit[]) collection.toArray();
        int length = baseUnitArr.length;
        if (length == 0) {
            return false;
        }
        BaseUnit[] baseUnitArr2 = this.c;
        int i3 = i2 + length;
        if (i3 <= baseUnitArr2.length) {
            System.arraycopy(baseUnitArr2, i, baseUnitArr2, i + length, i2 - i);
        } else {
            BaseUnit[] baseUnitArr3 = new BaseUnit[c(i3 - 1)];
            System.arraycopy(baseUnitArr2, 0, baseUnitArr3, 0, i);
            System.arraycopy(baseUnitArr2, i, baseUnitArr3, i + length, i2 - i);
            baseUnitArr2 = baseUnitArr3;
            this.c = baseUnitArr3;
        }
        System.arraycopy(baseUnitArr, 0, baseUnitArr2, i, length);
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
            UnitList unitList = (UnitList) super.clone();
            unitList.c = (BaseUnit[]) this.c.clone();
            return unitList;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public BaseUnit get(int i) {
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
        BaseUnit[] baseUnitArr = this.c;
        int i = this.b;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(baseUnitArr[i2])) {
                    return true;
                }
            }
            return false;
        }
        for (int i3 = 0; i3 < i; i3++) {
            if (baseUnitArr[i3] == null) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object obj) {
        BaseUnit[] baseUnitArr = this.c;
        int i = this.b;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(baseUnitArr[i2])) {
                    return i2;
                }
            }
            return -1;
        }
        for (int i3 = 0; i3 < i; i3++) {
            if (baseUnitArr[i3] == null) {
                return i3;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object obj) {
        BaseUnit[] baseUnitArr = this.c;
        if (obj != null) {
            for (int i = this.b - 1; i >= 0; i--) {
                if (obj.equals(baseUnitArr[i])) {
                    return i;
                }
            }
            return -1;
        }
        for (int i2 = this.b - 1; i2 >= 0; i2--) {
            if (baseUnitArr[i2] == null) {
                return i2;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public BaseUnit remove(int i) {
        BaseUnit[] baseUnitArr = this.c;
        int i2 = this.b;
        if (i >= i2) {
            a(i, i2);
        }
        BaseUnit baseUnit = baseUnitArr[i];
        int i3 = i2 - 1;
        System.arraycopy(baseUnitArr, i + 1, baseUnitArr, i, i3 - i);
        baseUnitArr[i3] = null;
        this.b = i3;
        this.modCount++;
        return baseUnit;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object obj) {
        BaseUnit[] baseUnitArr = this.c;
        int i = this.b;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(baseUnitArr[i2])) {
                    int i3 = i - 1;
                    System.arraycopy(baseUnitArr, i2 + 1, baseUnitArr, i2, i3 - i2);
                    baseUnitArr[i3] = null;
                    this.b = i3;
                    this.modCount++;
                    return true;
                }
            }
            return false;
        }
        for (int i4 = 0; i4 < i; i4++) {
            if (baseUnitArr[i4] == null) {
                int i5 = i - 1;
                System.arraycopy(baseUnitArr, i4 + 1, baseUnitArr, i4, i5 - i4);
                baseUnitArr[i5] = null;
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
        BaseUnit[] baseUnitArr = this.c;
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
        System.arraycopy(baseUnitArr, i2, baseUnitArr, i, i3 - i2);
        int i4 = i2 - i;
        Arrays.fill(baseUnitArr, i3 - i4, i3, (Object) null);
        this.b = i3 - i4;
        this.modCount++;
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public BaseUnit set(int i, BaseUnit baseUnit) {
        BaseUnit[] baseUnitArr = this.c;
        if (i >= this.b) {
            a(i, this.b);
        }
        BaseUnit baseUnit2 = baseUnitArr[i];
        baseUnitArr[i] = baseUnit;
        return baseUnit2;
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
        return new UnitListIterator(this);
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public int hashCode() {
        BaseUnit[] baseUnitArr = this.c;
        int iHashCode = 1;
        int i = this.b;
        for (int i2 = 0; i2 < i; i2++) {
            BaseUnit baseUnit = baseUnitArr[i2];
            iHashCode = (31 * iHashCode) + (baseUnit == null ? 0 : baseUnit.hashCode());
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
        BaseUnit[] baseUnitArr = this.c;
        if (list instanceof RandomAccess) {
            for (int i2 = 0; i2 < i; i2++) {
                BaseUnit baseUnit = baseUnitArr[i2];
                Object obj2 = list.get(i2);
                if (baseUnit == null) {
                    if (obj2 != null) {
                        return false;
                    }
                } else if (!baseUnit.equals(obj2)) {
                    return false;
                }
            }
            return true;
        }
        Iterator it = list.iterator();
        for (int i3 = 0; i3 < i; i3++) {
            BaseUnit baseUnit2 = baseUnitArr[i3];
            Object next = it.next();
            if (baseUnit2 == null) {
                if (next != null) {
                    return false;
                }
            } else if (!baseUnit2.equals(next)) {
                return false;
            }
        }
        return true;
    }
}
