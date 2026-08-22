package com.corrodinggames.rts.gameFramework.statistics;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/w.class */
public class AndroidMenuItem {
    CharSequence title;
    int id;

    public AndroidMenuItem setTitle(CharSequence charSequence) {
        this.title = charSequence;
        return this;
    }

    public CharSequence getTitle() {
        return this.title;
    }

    public int getItemId() {
        return this.id;
    }

    public AndroidMenuItem a(int i) {
        this.id = i;
        return this;
    }
}
