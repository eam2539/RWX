package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.game.units.BaseUnit;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.v */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/v.class */
class UnitListIterator implements Iterator {
    private int b;
    private int c;
    private int d;
    final /* synthetic */ UnitList a;

    UnitListIterator(UnitList unitList) {
        this.a = unitList;
        this.b = this.a.b;
        this.c = -1;
        this.d = UnitList.e(this.a);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.b != 0;
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public BaseUnit next() {
        UnitList unitList = this.a;
        int i = this.b;
        if (UnitList.e(unitList) != this.d) {
            throw new ConcurrentModificationException();
        }
        if (i == 0) {
            throw new NoSuchElementException();
        }
        this.b = i - 1;
        BaseUnit[] baseUnitArr = unitList.c;
        int i2 = unitList.b - i;
        this.c = i2;
        return baseUnitArr[i2];
    }

    @Override // java.util.Iterator
    public void remove() {
        BaseUnit[] baseUnitArr = this.a.c;
        int i = this.c;
        if (UnitList.e(this.a) != this.d) {
            throw new ConcurrentModificationException();
        }
        if (i < 0) {
            throw new IllegalStateException();
        }
        System.arraycopy(baseUnitArr, i + 1, baseUnitArr, i, this.b);
        UnitList unitList = this.a;
        int i2 = unitList.b - 1;
        unitList.b = i2;
        baseUnitArr[i2] = null;
        this.c = -1;
        this.d = UnitList.d(this.a);
    }
}
