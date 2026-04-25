package com.corrodinggames.rts.game.units.custom.tracking;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.c.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/c/e.class */
public class TrackingGroup {
    AnimationTrackingEntry a;
    FastArrayList<TrackingData> b = new FastArrayList<>();

    public TrackingGroup(AnimationTrackingEntry animationTrackingEntry) {
        this.a = animationTrackingEntry;
    }

    public TrackingData a(BaseUnit baseUnit) {
        int i = this.b.size;
        Object[] objArrA = this.b.a();
        for (int i2 = 0; i2 < i; i2++) {
            TrackingData trackingData = (TrackingData) objArrA[i2];
            if (trackingData.a == baseUnit) {
                return trackingData;
            }
        }
        return null;
    }
}
