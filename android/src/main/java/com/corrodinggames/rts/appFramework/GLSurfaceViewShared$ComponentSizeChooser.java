package com.corrodinggames.rts.appFramework;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.o */
/* JADX INFO: loaded from: classes.dex */
class GLSurfaceViewShared$ComponentSizeChooser extends GLSurfaceViewShared$BaseConfigChooser {
    protected int c;
    protected int d;
    protected int e;
    protected int f;
    protected int g;
    protected int h;
    final /* synthetic */ GLSurfaceViewShared i;
    private int[] j;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public GLSurfaceViewShared$ComponentSizeChooser(GLSurfaceViewShared gLSurfaceViewShared, int i, int i2, int i3, int i4, int i5, int i6) {
        super(gLSurfaceViewShared, new int[]{12324, i, 12323, i2, 12322, i3, 12321, i4, 12325, i5, 12326, i6, 12344});
        this.i = gLSurfaceViewShared;
        this.j = new int[1];
        this.c = i;
        this.d = i2;
        this.e = i3;
        this.f = i4;
        this.g = i5;
        this.h = i6;
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$BaseConfigChooser
    public final EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
        for (EGLConfig eGLConfig : eGLConfigArr) {
            int iA = a(egl10, eGLDisplay, eGLConfig, 12325);
            int iA2 = a(egl10, eGLDisplay, eGLConfig, 12326);
            if (iA >= this.g && iA2 >= this.h) {
                int iA3 = a(egl10, eGLDisplay, eGLConfig, 12324);
                int iA4 = a(egl10, eGLDisplay, eGLConfig, 12323);
                int iA5 = a(egl10, eGLDisplay, eGLConfig, 12322);
                int iA6 = a(egl10, eGLDisplay, eGLConfig, 12321);
                if (iA3 == this.c && iA4 == this.d && iA5 == this.e && iA6 == this.f) {
                    return eGLConfig;
                }
            }
        }
        return null;
    }

    private int a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i) {
        if (egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.j)) {
            return this.j[0];
        }
        return 0;
    }
}
