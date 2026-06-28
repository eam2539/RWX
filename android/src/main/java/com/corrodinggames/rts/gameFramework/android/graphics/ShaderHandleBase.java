package com.corrodinggames.rts.gameFramework.android.graphics;

import android.opengl.GLES20;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.x */
/* JADX INFO: loaded from: classes.dex */
abstract class ShaderHandleBase {
    protected final String b;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f574a = -1;
    public int c = -1;

    public abstract void a(int i);

    public ShaderHandleBase(String str) {
        this.b = str;
    }

    public final void a(float[] fArr) {
        GLES20.glUniformMatrix4fv(this.f574a, 1, false, fArr, 0);
    }
}
