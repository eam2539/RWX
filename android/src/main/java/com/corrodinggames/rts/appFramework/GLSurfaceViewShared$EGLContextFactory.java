package com.corrodinggames.rts.appFramework;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.s */
/* JADX INFO: loaded from: classes.dex */
public interface GLSurfaceViewShared$EGLContextFactory {
    EGLContext a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig);

    void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext);
}
