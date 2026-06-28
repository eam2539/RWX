package com.corrodinggames.rts.appFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.w */
/* JADX INFO: loaded from: classes.dex */
final class GLSurfaceViewShared$GLThreadManager {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static String f270a = "GLThreadManager";

    private GLSurfaceViewShared$GLThreadManager() {
    }

    /* synthetic */ GLSurfaceViewShared$GLThreadManager(byte b) {
        this();
    }

    public final synchronized void a(GLSurfaceViewShared$GLThread gLSurfaceViewShared$GLThread) {
        GLSurfaceViewShared$GLThread.a(gLSurfaceViewShared$GLThread);
        notifyAll();
    }
}
