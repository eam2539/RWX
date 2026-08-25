package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.gameFramework.GameObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.s */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/s.class */
public final class GameObjectArrayList extends AbstractList<GameObject> implements Serializable, Cloneable, RandomAccess {
    public static final GameObject[] a = new GameObject[0];
    /* JADX INFO: renamed from: b */
    int size;

    transient GameObject[] c = a;

    /* JADX INFO: renamed from: d */
    String name;

    static /* synthetic */ int e(GameObjectArrayList gameObjectArrayList) {
        return gameObjectArrayList.modCount;
    }

    static /* synthetic */ int f(GameObjectArrayList gameObjectArrayList) {
        int i = gameObjectArrayList.modCount + 1;
        gameObjectArrayList.modCount = i;
        return i;
    }

    public GameObjectArrayList(String str) {
        this.name = str;
    }

    public GameObject[] a() {
        return this.c;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public boolean add(GameObject gameObject) {
        GameObject[] gameObjectArr = this.c;
        int i = this.size;
        if (i == gameObjectArr.length) {
            GameObject[] gameObjectArr2 = new GameObject[i + (i < 6 ? 12 : i >> 1)];
            System.arraycopy(gameObjectArr, 0, gameObjectArr2, 0, i);
            gameObjectArr = gameObjectArr2;
            this.c = gameObjectArr2;
        }
        gameObjectArr[i] = gameObject;
        this.size = i + 1;
        this.modCount++;
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public void add(int i, GameObject gameObject) {
        GameObject[] gameObjectArr = this.c;
        int i2 = this.size;
        if (i > i2 || i < 0) {
            a(i, i2);
        }
        if (i2 < gameObjectArr.length) {
            System.arraycopy(gameObjectArr, i, gameObjectArr, i + 1, i2 - i);
        } else {
            GameObject[] gameObjectArr2 = new GameObject[c(i2)];
            System.arraycopy(gameObjectArr, 0, gameObjectArr2, 0, i);
            System.arraycopy(gameObjectArr, i, gameObjectArr2, i + 1, i2 - i);
            gameObjectArr = gameObjectArr2;
            this.c = gameObjectArr2;
        }
        gameObjectArr[i] = gameObject;
        this.size = i2 + 1;
        this.modCount++;
    }

    private static int c(int i) {
        return i + (i < 6 ? 12 : i >> 1);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection collection) {
        GameObject[] gameObjectArr = (GameObject[]) collection.toArray();
        int length = gameObjectArr.length;
        if (length == 0) {
            return false;
        }
        GameObject[] gameObjectArr2 = this.c;
        int i = this.size;
        int i2 = i + length;
        if (i2 > gameObjectArr2.length) {
            GameObject[] gameObjectArr3 = new GameObject[c(i2 - 1)];
            System.arraycopy(gameObjectArr2, 0, gameObjectArr3, 0, i);
            gameObjectArr2 = gameObjectArr3;
            this.c = gameObjectArr3;
        }
        System.arraycopy(gameObjectArr, 0, gameObjectArr2, i, length);
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
        GameObject[] gameObjectArr = (GameObject[]) collection.toArray();
        int length = gameObjectArr.length;
        if (length == 0) {
            return false;
        }
        GameObject[] gameObjectArr2 = this.c;
        int i3 = i2 + length;
        if (i3 <= gameObjectArr2.length) {
            System.arraycopy(gameObjectArr2, i, gameObjectArr2, i + length, i2 - i);
        } else {
            GameObject[] gameObjectArr3 = new GameObject[c(i3 - 1)];
            System.arraycopy(gameObjectArr2, 0, gameObjectArr3, 0, i);
            System.arraycopy(gameObjectArr2, i, gameObjectArr3, i + length, i2 - i);
            gameObjectArr2 = gameObjectArr3;
            this.c = gameObjectArr3;
        }
        System.arraycopy(gameObjectArr, 0, gameObjectArr2, i, length);
        this.size = i3;
        this.modCount++;
        return true;
    }

    static IndexOutOfBoundsException a(int i, int i2) {
        throw new IndexOutOfBoundsException("Invalid index " + i + ", size is " + i2);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        if (this.size != 0) {
            Arrays.fill(this.c, 0, this.size, (Object) null);
            this.size = 0;
            this.modCount++;
        }
    }

    public void b() {
        if (this.size != 0) {
            this.size = 0;
            this.modCount++;
        }
    }

    public Object clone() {
        try {
            GameObjectArrayList gameObjectArrayList = (GameObjectArrayList) super.clone();
            gameObjectArrayList.c = (GameObject[]) this.c.clone();
            return gameObjectArrayList;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public GameObject get(int i) {
        if (i >= this.size) {
            a(i, this.size);
        }
        return this.c[i];
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
        GameObject[] gameObjectArr = this.c;
        int i = this.size;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(gameObjectArr[i2])) {
                    return true;
                }
            }
            return false;
        }
        for (int i3 = 0; i3 < i; i3++) {
            if (gameObjectArr[i3] == null) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object obj) {
        GameObject[] gameObjectArr = this.c;
        int i = this.size;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(gameObjectArr[i2])) {
                    return i2;
                }
            }
            return -1;
        }
        for (int i3 = 0; i3 < i; i3++) {
            if (gameObjectArr[i3] == null) {
                return i3;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object obj) {
        GameObject[] gameObjectArr = this.c;
        if (obj != null) {
            for (int i = this.size - 1; i >= 0; i--) {
                if (obj.equals(gameObjectArr[i])) {
                    return i;
                }
            }
            return -1;
        }
        for (int i2 = this.size - 1; i2 >= 0; i2--) {
            if (gameObjectArr[i2] == null) {
                return i2;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public GameObject remove(int i) {
        GameObject[] gameObjectArr = this.c;
        int i2 = this.size;
        if (i >= i2) {
            a(i, i2);
        }
        GameObject gameObject = gameObjectArr[i];
        int i3 = i2 - 1;
        System.arraycopy(gameObjectArr, i + 1, gameObjectArr, i, i3 - i);
        gameObjectArr[i3] = null;
        this.size = i3;
        this.modCount++;
        return gameObject;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object obj) {
        GameObject[] gameObjectArr = this.c;
        int i = this.size;
        if (obj != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (obj.equals(gameObjectArr[i2])) {
                    int i3 = i - 1;
                    System.arraycopy(gameObjectArr, i2 + 1, gameObjectArr, i2, i3 - i2);
                    gameObjectArr[i3] = null;
                    this.size = i3;
                    this.modCount++;
                    return true;
                }
            }
            return false;
        }
        for (int i4 = 0; i4 < i; i4++) {
            if (gameObjectArr[i4] == null) {
                int i5 = i - 1;
                System.arraycopy(gameObjectArr, i4 + 1, gameObjectArr, i4, i5 - i4);
                gameObjectArr[i5] = null;
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
        GameObject[] gameObjectArr = this.c;
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
        System.arraycopy(gameObjectArr, i2, gameObjectArr, i, i3 - i2);
        int i4 = i2 - i;
        Arrays.fill(gameObjectArr, i3 - i4, i3, (Object) null);
        this.size = i3 - i4;
        this.modCount++;
    }

    @Override // java.util.AbstractList, java.util.List
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public GameObject set(int i, GameObject gameObject) {
        GameObject[] gameObjectArr = this.c;
        if (i >= this.size) {
            a(i, this.size);
        }
        GameObject gameObject2 = gameObjectArr[i];
        gameObjectArr[i] = gameObject;
        return gameObject2;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray() {
        int i = this.size;
        Object[] objArr = new Object[i];
        System.arraycopy(this.c, 0, objArr, 0, i);
        return objArr;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray(Object[] objArr) {
        int i = this.size;
        if (objArr.length < i) {
            objArr = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), i);
        }
        System.arraycopy(this.c, 0, objArr, 0, i);
        if (objArr.length > i) {
            objArr[i] = null;
        }
        return objArr;
    }

    @Override
    // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
    public Iterator iterator() {
        return new GameObjectArrayListIterator(this);
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public int hashCode() {
        GameObject[] gameObjectArr = this.c;
        int iHashCode = 1;
        int i = this.size;
        for (int i2 = 0; i2 < i; i2++) {
            GameObject gameObject = gameObjectArr[i2];
            iHashCode = (31 * iHashCode) + (gameObject == null ? 0 : gameObject.hashCode());
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
        GameObject[] gameObjectArr = this.c;
        if (list instanceof RandomAccess) {
            for (int i2 = 0; i2 < i; i2++) {
                GameObject gameObject = gameObjectArr[i2];
                Object obj2 = list.get(i2);
                if (gameObject == null) {
                    if (obj2 != null) {
                        return false;
                    }
                } else if (!gameObject.equals(obj2)) {
                    return false;
                }
            }
            return true;
        }
        Iterator it = list.iterator();
        for (int i3 = 0; i3 < i; i3++) {
            GameObject gameObject2 = gameObjectArr[i3];
            Object next = it.next();
            if (gameObject2 == null) {
                if (next != null) {
                    return false;
                }
            } else if (!gameObject2.equals(next)) {
                return false;
            }
        }
        return true;
    }
}
