package com.corrodinggames.rts.java;

import org.newdawn.slick.Font;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/f.class */
class FontKey {
    int a;
    boolean b;
    boolean c;
    Font d;
    int e;
    String[] f = new String[30];
    final /* synthetic */ SlickGraphicsEngine g;

    FontKey(SlickGraphicsEngine slickGraphicsEngine) {
        this.g = slickGraphicsEngine;
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public FontKey clone() {
        FontKey fontKey = new FontKey(this.g);
        fontKey.a = this.a;
        fontKey.b = this.b;
        fontKey.c = this.c;
        return fontKey;
    }

    public String toString() {
        return "FontKey:(size:" + this.a + ",  bold:" + this.b + " fallback:" + this.c + ")";
    }

    boolean a(String str) {
        if (str == null || !SlickGraphicsEngine.a(str)) {
            return true;
        }
        for (int i = 0; i < this.f.length; i++) {
            String str2 = this.f[i];
            if (str2 != null && str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    void b(String str) {
        this.f[this.e] = str;
        this.e++;
        if (this.e >= this.f.length) {
            this.e = 0;
        }
    }
}
