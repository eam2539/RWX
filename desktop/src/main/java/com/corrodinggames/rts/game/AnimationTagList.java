package com.corrodinggames.rts.game;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/t.class */
public class AnimationTagList {
    public static final AnimationTagEntry[] a = new AnimationTagEntry[0];
    AnimationTagEntry[] b = a;
    int c = 0;

    public boolean a(AnimationTagEntry animationTagEntry) {
        AnimationTagEntry[] animationTagEntryArr = this.b;
        int i = this.c;
        if (i == animationTagEntryArr.length) {
            AnimationTagEntry[] animationTagEntryArr2 = new AnimationTagEntry[i + (i < 6 ? 12 : i >> 1)];
            System.arraycopy(animationTagEntryArr, 0, animationTagEntryArr2, 0, i);
            animationTagEntryArr = animationTagEntryArr2;
            this.b = animationTagEntryArr2;
        }
        animationTagEntryArr[i] = animationTagEntry;
        this.c = i + 1;
        return true;
    }
}
