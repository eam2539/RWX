package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/i.class */
public class CustomUnitAnimationTags {

    /* JADX INFO: renamed from: a */
    public FastArrayList activeTags = new FastArrayList();

    public CustomUnitAnimationTags() {
    }

    public CustomUnitAnimationTags(AnimationSet animationSet) {
        if (animationSet == null) {
            return;
        }
        for (AnimationTag animationTag : animationSet.a) {
            this.activeTags.add(animationTag);
        }
    }

    public boolean a(AnimationSet animationSet) {
        if (animationSet == null) {
            return false;
        }
        boolean z = false;
        for (AnimationTag animationTag : animationSet.a) {
            if (a(animationTag)) {
                z = true;
            }
        }
        return z;
    }

    public boolean a(AnimationTag animationTag) {
        if (!this.activeTags.contains(animationTag)) {
            this.activeTags.add(animationTag);
            return true;
        }
        return false;
    }

    public boolean b(AnimationSet animationSet) {
        if (animationSet == null) {
            return false;
        }
        boolean z = false;
        for (AnimationTag animationTag : animationSet.a) {
            if (this.activeTags.remove(animationTag)) {
                z = true;
            }
        }
        return z;
    }

    public AnimationSet a() {
        if (this.activeTags.size() == 0) {
            return AnimationTag.d;
        }
        return new AnimationSet((AnimationTag[]) this.activeTags.toArray(AnimationTag.c));
    }
}
