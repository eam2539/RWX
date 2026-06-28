package com.corrodinggames.rts.appFramework;

import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.p */
/* JADX INFO: loaded from: classes.dex */
final class GLSurfaceViewShared$DefaultContextFactory implements GLSurfaceViewShared$EGLContextFactory {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ GLSurfaceViewShared f267a;
    private int b;

    private GLSurfaceViewShared$DefaultContextFactory(GLSurfaceViewShared gLSurfaceViewShared) {
        this.f267a = gLSurfaceViewShared;
        this.b = 12440;
    }

    /* synthetic */ GLSurfaceViewShared$DefaultContextFactory(GLSurfaceViewShared gLSurfaceViewShared, byte b) {
        this(gLSurfaceViewShared);
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$EGLContextFactory
    public final EGLContext a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
        int[] iArr = {this.b, this.f267a.mEGLContextClientVersion, 12344};
        EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
        if (this.f267a.mEGLContextClientVersion == 0) {
            iArr = null;
        }
        return egl10.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$EGLContextFactory
    public final void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
        if (!egl10.eglDestroyContext(eGLDisplay, eGLContext)) {
            Log.e("DefaultContextFactory", "display:" + eGLDisplay + " context: " + eGLContext);
            GLSurfaceViewShared$EglHelper.a("eglDestroyContex", egl10.eglGetError());
        }
    }
}
