package com.corrodinggames.rts.game.units.management;

import com.corrodinggames.rts.game.units.BaseUnit;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/b.class */
public final class UnitList {
    public static final BaseUnit[] a = new BaseUnit[0];
    public int b;
    transient BaseUnit[] c = a;

    public boolean a(BaseUnit baseUnit) {
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
        return true;
    }

    public boolean b(BaseUnit baseUnit) {
        BaseUnit[] baseUnitArr = this.c;
        int i = this.b;
        if (baseUnit != null) {
            for (int i2 = 0; i2 < i; i2++) {
                if (baseUnit.equals(baseUnitArr[i2])) {
                    int i3 = i - 1;
                    System.arraycopy(baseUnitArr, i2 + 1, baseUnitArr, i2, i3 - i2);
                    baseUnitArr[i3] = null;
                    this.b = i3;
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
                return true;
            }
        }
        return false;
    }

    public final BaseUnit[] a() {
        return this.c;
    }
}
