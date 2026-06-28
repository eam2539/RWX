package com.corrodinggames.rts.gameFramework.android.graphics;

import android.opengl.GLES20;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.aa */
/* JADX INFO: loaded from: classes.dex */
public final class OpenGLResourceManager implements ResourceManagerInterface {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private final int[] f554a = new int[1];

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ResourceManagerInterface
    public final int a() {
        GLES20.glGenTextures(1, this.f554a, 0);
        OpenGLRenderer.j();
        return this.f554a[0];
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ResourceManagerInterface
    public final void a(int[] iArr) {
        GLES20.glGenBuffers(1, iArr, 0);
        OpenGLRenderer.j();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ResourceManagerInterface
    public final void a(int i, int[] iArr) {
        GLES20.glDeleteTextures(i, iArr, 0);
        OpenGLRenderer.j();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ResourceManagerInterface
    public final void b(int i, int[] iArr) {
        GLES20.glDeleteBuffers(i, iArr, 0);
        OpenGLRenderer.j();
    }
}
