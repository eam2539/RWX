package com.corrodinggames.rts.appFramework;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.z */
/* JADX INFO: loaded from: classes.dex */
public interface GLSurfaceViewShared$Renderer {
    void onDrawFrame(GL10 gl10);

    void onSurfaceChanged(GL10 gl10, int i, int i2);

    void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig);
}
