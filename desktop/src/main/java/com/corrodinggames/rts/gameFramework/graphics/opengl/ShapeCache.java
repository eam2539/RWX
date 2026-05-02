package com.corrodinggames.rts.gameFramework.graphics.opengl;

import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.ab */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/ab.class */
public class ShapeCache {
    int a;
    int b;
    boolean c;
    Texture d;

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public ShapeCache clone() {
        ShapeCache shapeCache = new ShapeCache();
        shapeCache.a = this.a;
        shapeCache.b = this.b;
        shapeCache.c = this.c;
        return shapeCache;
    }
}
