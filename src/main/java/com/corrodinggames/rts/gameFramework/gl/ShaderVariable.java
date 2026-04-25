package com.corrodinggames.rts.gameFramework.gl;

import android.opengl.GLES20;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.q */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/q.class */
public abstract class ShaderVariable {
    protected final String b;
    public int a = -1;
    public int c = -1;

    public abstract void a(int i);

    public ShaderVariable(String str) {
        this.b = str;
    }

    public void a(float[] fArr) {
        GLES20.glUniformMatrix4fv(this.a, 1, false, fArr, 0);
    }
}
