package com.corrodinggames.rts.gameFramework.statistics;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/w.class */
public class AndroidMenuItem {
    CharSequence a;
    int c;

    public AndroidMenuItem setTitle(CharSequence charSequence) {
        this.a = charSequence;
        return this;
    }

    public CharSequence getTitle() {
        return this.a;
    }

    public int getItemId() {
        return this.c;
    }

    public AndroidMenuItem a(int i) {
        this.c = i;
        return this;
    }
}
