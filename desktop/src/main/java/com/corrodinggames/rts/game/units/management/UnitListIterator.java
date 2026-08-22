package com.corrodinggames.rts.game.units.management;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.utility.UnitList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/f.class */
public class UnitListIterator implements Iterable<BaseUnit>, Iterator<BaseUnit> {

    /* JADX INFO: renamed from: a */
    int remaining;

    /* JADX INFO: renamed from: b */
    BaseUnit[] elements;

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.remaining > 0;
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public BaseUnit next() {
        this.remaining--;
        return this.elements[this.remaining];
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
        this.elements = unitList.a();
        this.remaining = unitList.b;
    }
}
