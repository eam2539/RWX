package com.corrodinggames.rts.appFramework;

import com.corrodinggames.rts.gameFramework.GameEngine;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.ae */
/* JADX INFO: loaded from: classes.dex */
final class GameViewOpenGL$RetainedContextFactory implements GLSurfaceViewShared$EGLContextFactory {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ GameViewOpenGL f34a;
    private final int b = 12440;

    GameViewOpenGL$RetainedContextFactory(GameViewOpenGL gameViewOpenGL) {
        this.f34a = gameViewOpenGL;
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$EGLContextFactory
    public final EGLContext a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
        if (GameViewOpenGL.retainedGlContext != null) {
            GameEngine.log("GameView:setEGLContextFactory using retainedGlContext - " + hashCode());
            EGLContext eGLContext = GameViewOpenGL.retainedGlContext;
            this.f34a.makeActive();
            return eGLContext;
        }
        GameEngine.log("GameView:setEGLContextFactory creating new GlContext - " + hashCode());
        EGLContext unused = GameViewOpenGL.retainedGlContext = egl10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
        GameViewOpenGL.lastHeldSurfaceView = this.f34a;
        return GameViewOpenGL.retainedGlContext;
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$EGLContextFactory
    public final void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
    }
}
