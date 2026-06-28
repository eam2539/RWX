package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a.class */
class CustomUnitData implements Comparable<CustomUnitData> {

    /* JADX INFO: renamed from: a */
    public float value;
    public float b;
    public float c;
    public float d;

    public CustomUnitData(float f, float f2) {
        this.value = f;
        this.b = f2;
    }

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compareTo(CustomUnitData customUnitData) {
        if (this.value == customUnitData.value) {
            return 0;
        }
        return this.value > customUnitData.value ? 1 : -1;
    }
}
