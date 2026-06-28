package com.corrodinggames.rts.appFramework;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.n */
/* JADX INFO: loaded from: classes.dex */
abstract class GLSurfaceViewShared$BaseConfigChooser implements GLSurfaceViewShared$EGLConfigChooser {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected int[] f266a;
    final /* synthetic */ GLSurfaceViewShared b;

    abstract EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

    public GLSurfaceViewShared$BaseConfigChooser(GLSurfaceViewShared gLSurfaceViewShared, int[] iArr) {
        this.b = gLSurfaceViewShared;
        if (this.b.mEGLContextClientVersion == 2 || this.b.mEGLContextClientVersion == 3) {
            int[] iArr2 = new int[15];
            System.arraycopy(iArr, 0, iArr2, 0, 12);
            iArr2[12] = 12352;
            if (this.b.mEGLContextClientVersion == 2) {
                iArr2[13] = 4;
            } else {
                iArr2[13] = 64;
            }
            iArr2[14] = 12344;
            iArr = iArr2;
        }
        this.f266a = iArr;
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$EGLConfigChooser
    public final EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay) {
        int[] iArr = new int[1];
        if (!egl10.eglChooseConfig(eGLDisplay, this.f266a, null, 0, iArr)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int i = iArr[0];
        if (i <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }
        EGLConfig[] eGLConfigArr = new EGLConfig[i];
        if (!egl10.eglChooseConfig(eGLDisplay, this.f266a, eGLConfigArr, i, iArr)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        EGLConfig eGLConfigA = a(egl10, eGLDisplay, eGLConfigArr);
        if (eGLConfigA == null) {
            throw new IllegalArgumentException("No config chosen");
        }
        return eGLConfigA;
    }
}
