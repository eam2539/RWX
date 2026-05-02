package com.corrodinggames.rts.gameFramework.utility;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/g.class */
public class CircularDeque extends AbstractCollection implements Serializable, Cloneable {
    transient Object[] b = new Object[16];
    transient int c;
    transient int d;
    static final /* synthetic */ boolean a;

    static {
        a = !CircularDeque.class.desiredAssertionStatus();
    }

    private void c() {
        if (!a && this.c != this.d) {
            throw new AssertionError();
        }
        int i = this.c;
        int length = this.b.length;
        int i2 = length - i;
        int i3 = length << 1;
        if (i3 < 0) {
            throw new IllegalStateException("Sorry, deque too big");
        }
        Object[] objArr = new Object[i3];
        System.arraycopy(this.b, i, objArr, 0, i2);
        System.arraycopy(this.b, 0, objArr, i2, i);
        this.b = objArr;
        this.c = 0;
        this.d = length;
    }

    private Object[] a(Object[] objArr) {
        if (this.c < this.d) {
            System.arraycopy(this.b, this.c, objArr, 0, size());
        } else if (this.c > this.d) {
            int length = this.b.length - this.c;
            System.arraycopy(this.b, this.c, objArr, 0, length);
            System.arraycopy(this.b, 0, objArr, length, this.d);
        }
        return objArr;
    }

    public void a(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        this.b[this.d] = obj;
        int length = (this.d + 1) & (this.b.length - 1);
        this.d = length;
        if (length == this.c) {
            c();
        }
    }

    public Object a() {
        int i = this.c;
        Object obj = this.b[i];
        if (obj == null) {
            return null;
        }
        this.b[i] = null;
        this.c = (i + 1) & (this.b.length - 1);
        return obj;
    }

    public boolean b(Object obj) {
        if (obj == null) {
            return false;
        }
        int length = this.b.length - 1;
        int i = this.c;
        while (true) {
            int i2 = i;
            Object obj2 = this.b[i2];
            if (obj2 != null) {
                if (obj.equals(obj2)) {
                    a(i2);
                    return true;
                }
                i = (i2 + 1) & length;
            } else {
                return false;
            }
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean add(Object obj) {
        a(obj);
        return true;
    }

    private void d() {
        if (!a && this.b[this.d] != null) {
            throw new AssertionError();
        }
        if (!a && (this.c != this.d ? this.b[this.c] == null || this.b[(this.d - 1) & (this.b.length - 1)] == null : this.b[this.c] != null)) {
            throw new AssertionError();
        }
        if (!a && this.b[(this.c - 1) & (this.b.length - 1)] != null) {
            throw new AssertionError();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean a(int i) {
        d();
        Object[] objArr = this.b;
        int length = objArr.length - 1;
        int i2 = this.c;
        int i3 = this.d;
        int i4 = (i - i2) & length;
        int i5 = (i3 - i) & length;
        if (i4 >= ((i3 - i2) & length)) {
            throw new ConcurrentModificationException();
        }
        if (i4 < i5) {
            if (i2 <= i) {
                System.arraycopy(objArr, i2, objArr, i2 + 1, i4);
            } else {
                System.arraycopy(objArr, 0, objArr, 1, i);
                objArr[0] = objArr[length];
                System.arraycopy(objArr, i2, objArr, i2 + 1, length - i2);
            }
            objArr[i2] = null;
            this.c = (i2 + 1) & length;
            return false;
        }
        if (i < i3) {
            System.arraycopy(objArr, i + 1, objArr, i, i5);
            this.d = i3 - 1;
            return true;
        }
        System.arraycopy(objArr, i + 1, objArr, i, length - i);
        objArr[length] = objArr[0];
        System.arraycopy(objArr, 1, objArr, 0, i3);
        this.d = (i3 - 1) & length;
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public int size() {
        return (this.d - this.c) & (this.b.length - 1);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        return this.c == this.d;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator iterator() {
        return new CircularDequeIterator(this);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object obj) {
        if (obj == null) {
            return false;
        }
        int length = this.b.length - 1;
        int i = this.c;
        while (true) {
            int i2 = i;
            Object obj2 = this.b[i2];
            if (obj2 != null) {
                if (obj.equals(obj2)) {
                    return true;
                }
                i = (i2 + 1) & length;
            } else {
                return false;
            }
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object obj) {
        return b(obj);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        int i = this.c;
        int i2 = this.d;
        if (i != i2) {
            this.d = 0;
            this.c = 0;
            int i3 = i;
            int length = this.b.length - 1;
            do {
                this.b[i3] = null;
                i3 = (i3 + 1) & length;
            } while (i3 != i2);
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public Object[] toArray() {
        return a(new Object[size()]);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public Object[] toArray(Object[] objArr) {
        int size = size();
        if (objArr.length < size) {
            objArr = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), size);
        }
        a(objArr);
        if (objArr.length > size) {
            objArr[size] = null;
        }
        return objArr;
    }

    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public CircularDeque clone() {
        try {
            CircularDeque circularDeque = (CircularDeque) super.clone();
            circularDeque.b = (Object[]) this.b.clone();
            return circularDeque;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
