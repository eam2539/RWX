package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/c.class */
public final class CustomUnitAnimationSet {

    /* JADX INFO: renamed from: a */
    CustomUnitAnimationType animationType;

    /* JADX INFO: renamed from: b */
    int animationId;

    /* JADX INFO: renamed from: c */
    String animationName;

    /* JADX INFO: renamed from: d */
    float maxTime;
    public CustomUnitData[] e;
    private FastArrayList<CustomUnitData> g = new FastArrayList();
    public final boolean f = false;

    public CustomUnitAnimation a() {
        CustomUnitAnimation customUnitAnimation = null;
        if (this.g.size() > 0) {
            customUnitAnimation = (CustomUnitAnimation) this.g.get(this.g.size() - 1);
            if (customUnitAnimation.finalized) {
                customUnitAnimation = null;
            }
        }
        return customUnitAnimation;
    }

    public void a(CustomUnit customUnit, float f, float f2, boolean z) {
        if (z || f2 <= f) {
            return;
        }
        CustomUnitData[] customUnitDataArr = this.e;
        int length = customUnitDataArr.length;
        int i = -1;
        while (i + 1 < length && f > customUnitDataArr[i + 1].value) {
            i++;
        }
        int i2 = i;
        while (i2 + 1 < length && f2 > customUnitDataArr[i2 + 1].value) {
            i2++;
        }
        if (i2 <= i) {
            return;
        }
        for (int i3 = i + 1; i3 <= i2; i3++) {
            ((CustomUnitAnimation) customUnitDataArr[i3]).a(customUnit);
        }
    }

    public void b() throws ConfigParseException {
        CustomUnitAnimation customUnitAnimationA;
        if (this.animationType == CustomUnitAnimationType.event && (customUnitAnimationA = a()) != null) {
            customUnitAnimationA.finalize();
        }
    }

    public void a(CustomUnitConfig customUnitConfig, float f, String str, String str2) throws ConfigParseException {
        if (this.animationType == CustomUnitAnimationType.event) {
            CustomUnitAnimation customUnitAnimationA = a();
            if (customUnitAnimationA == null) {
                customUnitAnimationA = new CustomUnitAnimation(f, 0.0f);
                this.g.add(customUnitAnimationA);
            }
            customUnitAnimationA.a(customUnitConfig, str, str2);
            return;
        }
        try {
            a(f, Float.parseFloat(str2));
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Failed to parse float:" + str2);
        }
    }

    public void a(float f, float f2) throws ConfigParseException {
        if (this.animationType == CustomUnitAnimationType.event) {
            throw new ConfigParseException("Adding key frame value to event set");
        }
        if (this.g.size() == 0 && f > 0.0f && f2 != 0.0f) {
            this.g.add(new CustomUnitData(0.0f, 0.0f));
        }
        this.g.add(new CustomUnitData(f, f2));
    }

    public void a(float f) {
        Iterator it = this.g.iterator();
        while (it.hasNext()) {
            ((CustomUnitData) it.next()).value *= f;
        }
    }

    public void c() {
        Collections.sort(this.g);
        CustomUnitData customUnitData = null;
        for (CustomUnitData customUnitData2 : this.g) {
            if (customUnitData != null) {
                customUnitData2.c = 1.0f / (customUnitData2.value - customUnitData.value);
                customUnitData2.d = customUnitData2.b - customUnitData.b;
            }
            customUnitData = customUnitData2;
            this.maxTime = customUnitData2.value;
        }
        this.e = (CustomUnitData[]) this.g.toArray(new CustomUnitData[0]);
        this.g = null;
    }

    public float b(float f) {
        CustomUnitData[] customUnitDataArr = this.e;
        int length = customUnitDataArr.length;
        if (length == 1 || f <= customUnitDataArr[0].value) {
            return customUnitDataArr[0].b;
        }
        if (f >= this.maxTime) {
            return customUnitDataArr[length - 1].b;
        }
        int i = 1;
        while (f > customUnitDataArr[i].value) {
            i++;
            if (i >= length) {
                return customUnitDataArr[length - 1].b;
            }
        }
        CustomUnitData customUnitData = customUnitDataArr[i - 1];
        CustomUnitData customUnitData2 = customUnitDataArr[i];
        return customUnitData.b + (customUnitData2.d * (f - customUnitData.value) * customUnitData2.c);
    }
}
