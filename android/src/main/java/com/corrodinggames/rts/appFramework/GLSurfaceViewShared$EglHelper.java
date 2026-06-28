package com.corrodinggames.rts.appFramework;

import android.util.Log;

import javax.microedition.khronos.egl.*;
import java.lang.ref.WeakReference;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.u */
/* JADX INFO: loaded from: classes.dex */
final class GLSurfaceViewShared$EglHelper {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    WeakReference f268a;
    EGL10 b;
    EGLDisplay c;
    EGLSurface d;
    EGLConfig e;
    EGLContext f;

    public GLSurfaceViewShared$EglHelper(WeakReference weakReference) {
        this.f268a = weakReference;
    }

    final void a() {
        if (this.d != null && this.d != EGL10.EGL_NO_SURFACE) {
            EGL10 egl10 = this.b;
            EGLDisplay eGLDisplay = this.c;
            EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
            egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
            GLSurfaceViewShared gLSurfaceViewShared = (GLSurfaceViewShared) this.f268a.get();
            if (gLSurfaceViewShared != null) {
                gLSurfaceViewShared.mEGLWindowSurfaceFactory.a(this.b, this.c, this.d);
            }
            this.d = null;
        }
    }

    public static void a(String str, int i) {
        throw new RuntimeException(b(str, i));
    }

    public static void a(String str, String str2, int i) {
        Log.w(str, b(str2, i));
    }

    private static String b(String str, int i) {
        return str + " failed: " + i;
    }
}
