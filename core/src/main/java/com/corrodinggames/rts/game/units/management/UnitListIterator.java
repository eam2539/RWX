package com.corrodinggames.rts.game.units.management;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.utility.UnitList;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/f.class */
public class UnitListIterator implements Iterable<BaseUnit>, Iterator<BaseUnit> {
    int a;
    BaseUnit[] b;

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.a > 0;
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public BaseUnit next() {
        this.a--;
        return this.b[this.a];
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new RuntimeException("Not supported");
    }

    @Override // java.lang.Iterable
    public Iterator iterator() {
        return this;
    }

    public void a(UnitList unitList) {
        this.b = unitList.a();
        this.a = unitList.b;
    }
}
