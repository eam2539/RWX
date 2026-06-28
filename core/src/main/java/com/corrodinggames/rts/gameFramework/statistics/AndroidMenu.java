package com.corrodinggames.rts.gameFramework.statistics;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.v */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/v.class */
public class AndroidMenu {
    public List<AndroidMenuItem> a = new ArrayList<AndroidMenuItem>();

    public AndroidMenuItem add(CharSequence charSequence) {
        AndroidMenuItem androidMenuItem = new AndroidMenuItem();
        androidMenuItem.setTitle(charSequence);
        this.a.add(androidMenuItem);
        return androidMenuItem;
    }

    public AndroidMenuItem add(int i) {
        throw new RuntimeException("not Implemented");
    }

    public AndroidMenuItem add(int i, int i2, int i3, CharSequence charSequence) {
        AndroidMenuItem androidMenuItem = new AndroidMenuItem();
        androidMenuItem.setTitle(charSequence);
        androidMenuItem.a(i2);
        this.a.add(androidMenuItem);
        return androidMenuItem;
    }

    public AndroidMenuItem add(int i, int i2, int i3, int i4) {
        throw new RuntimeException("not Implemented");
    }

    public void clear() {
        this.a.clear();
    }

    public AndroidMenuItem getItem(int i) {
        return this.a.get(i);
    }

    public int size() {
        return this.a.size();
    }
}
