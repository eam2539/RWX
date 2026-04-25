package com.corrodinggames.rts.gameFramework.gl;

import android.opengl.GLES20;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/t.class */
public class TextureResource implements ITextureResource {
    private final int[] a = new int[1];

    @Override // com.corrodinggames.rts.gameFramework.gl.ITextureResource
    public int a() {
        GLES20.glGenTextures(1, this.a, 0);
        OpenGLRenderer.q();
        return this.a[0];
    }
}
