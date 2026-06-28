package com.corrodinggames.rts.java.audio.util;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.r */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/r.class */
abstract class ObjectMapIterator implements Iterable, Iterator {
    public boolean b;
    final ObjectMap c;
    int d;
    int e;
    boolean f = true;

    public ObjectMapIterator(ObjectMap objectMap) {
        this.c = objectMap;
        c();
    }

    public void c() {
        this.e = -1;
        this.d = -1;
        d();
    }

    void d() {
        this.b = false;
        Object[] objArr = this.c.b;
        int i = this.c.d + this.c.e;
        do {
            int i2 = this.d + 1;
            this.d = i2;
            if (i2 >= i) {
                return;
            }
        } while (objArr[this.d] == null);
        this.b = true;
    }

    public void remove() {
        if (this.e < 0) {
            throw new IllegalStateException("next must FastArrayList called before remove.");
        }
        if (this.e >= this.c.d) {
            this.c.a(this.e);
            this.d = this.e - 1;
            d();
        } else {
            this.c.b[this.e] = null;
            this.c.c[this.e] = null;
        }
        this.e = -1;
        this.c.a--;
    }
}
