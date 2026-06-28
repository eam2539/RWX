package com.corrodinggames.rts.appFramework;

import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.q */
/* JADX INFO: loaded from: classes.dex */
final class GLSurfaceViewShared$DefaultWindowSurfaceFactory implements GLSurfaceViewShared$EGLWindowSurfaceFactory {
    private GLSurfaceViewShared$DefaultWindowSurfaceFactory() {
    }

    /* synthetic */ GLSurfaceViewShared$DefaultWindowSurfaceFactory(byte b) {
        this();
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$EGLWindowSurfaceFactory
    public final EGLSurface a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj) {
        try {
            return egl10.eglCreateWindowSurface(eGLDisplay, eGLConfig, obj, null);
        } catch (IllegalArgumentException e) {
            Log.e("GLSurfaceView", "eglCreateWindowSurface", e);
            return null;
        }
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$EGLWindowSurfaceFactory
    public final void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface) {
        egl10.eglDestroySurface(eGLDisplay, eGLSurface);
    }
}
