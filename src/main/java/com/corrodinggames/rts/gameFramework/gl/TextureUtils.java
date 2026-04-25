package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.RectF;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ag */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/ag.class */
public class TextureUtils {
    public static void a(Texture texture, RectF rectF) {
        int i = 0;
        int i2 = 0;
        int iB = texture.b();
        int iC = texture.c();
        if (texture.f()) {
            i = 1;
            i2 = 1;
            iB--;
            iC--;
        }
        rectF.a(i, i2, iB, iC);
    }
}
