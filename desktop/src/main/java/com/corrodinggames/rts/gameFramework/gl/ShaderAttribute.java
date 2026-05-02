package com.corrodinggames.rts.gameFramework.gl;

import android.opengl.GLES20;
import android.util.Log;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/o.class */
public class ShaderAttribute extends ShaderVariable {
    public ShaderAttribute(String str) {
        super(str);
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.ShaderVariable
    public void a(int i) {
        if (this.c != i) {
            this.a = GLES20.glGetAttribLocation(i, this.b);
            this.c = i;
            OpenGLRenderer.r();
            if (this.a == -1) {
                Log.d(OpenGLRenderer.G, "loadHandle: Failed to find: " + this.b);
            }
        }
    }
}
