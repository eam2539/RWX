package com.corrodinggames.rts.appFramework;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.t */
/* JADX INFO: loaded from: classes.dex */
public interface GLSurfaceViewShared$EGLWindowSurfaceFactory {
    EGLSurface a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj);

    void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface);
}
