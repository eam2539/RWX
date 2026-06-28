package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/h.class */
public final class AnimationSet {
    public final AnimationTag[] a;

    public AnimationSet(AnimationTag[] animationTagArr) {
        this.a = animationTagArr;
    }

    public boolean a() {
        return this.a.length == 0;
    }

    public boolean a(AnimationSet animationSet) {
        if (animationSet == null) {
            if (a()) {
                return true;
            }
            return false;
        }
        if (this.a.length != animationSet.a.length) {
            return false;
        }
        for (AnimationTag animationTag : this.a) {
            boolean z = false;
            AnimationTag[] animationTagArr = animationSet.a;
            int length = animationTagArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                if (animationTag != animationTagArr[i]) {
                    i++;
                } else {
                    z = true;
                    break;
                }
            }
            if (!z) {
                return false;
            }
        }
        return true;
    }

    public int b() {
        return this.a.length;
    }

    public String toString() {
        String str = VariableScope.nullOrMissingString;
        boolean z = true;
        for (AnimationTag animationTag : this.a) {
            if (!z) {
                str = str + ", ";
            }
            z = false;
            str = str + animationTag.tagName;
        }
        return "{" + str + "}";
    }
}
