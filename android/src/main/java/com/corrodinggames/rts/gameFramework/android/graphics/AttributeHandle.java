package com.corrodinggames.rts.gameFramework.android.graphics;

import android.opengl.GLES20;
import android.util.Log;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.u */
/* JADX INFO: loaded from: classes.dex */
final class AttributeHandle extends ShaderHandleBase {
    public AttributeHandle(String str) {
        super(str);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ShaderHandleBase
    public final void a(int i) {
        if (this.c != i) {
            this.f574a = GLES20.glGetAttribLocation(i, this.b);
            this.c = i;
            OpenGLRenderer.k();
            if (this.f574a == -1) {
                Log.e(OpenGLRenderer.Q, "loadHandle: Failed to find: " + this.b);
            }
        }
    }
}
