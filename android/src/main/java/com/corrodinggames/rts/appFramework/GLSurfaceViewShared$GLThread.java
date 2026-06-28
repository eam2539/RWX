package com.corrodinggames.rts.appFramework;

import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.v */
/* JADX INFO: loaded from: classes.dex */
final class GLSurfaceViewShared$GLThread extends Thread {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    boolean f269a;
    boolean b;
    boolean c;
    boolean d;
    boolean e;
    boolean f;
    boolean g;
    boolean h;
    boolean i;
    boolean m;
    private boolean p;
    private boolean q;
    private GLSurfaceViewShared$EglHelper u;
    private WeakReference v;
    ArrayList n = new ArrayList();
    boolean o = true;
    private Runnable t = null;
    int j = 0;
    int k = 0;
    boolean l = true;
    private int r = 1;
    private boolean s = false;

    static /* synthetic */ boolean a(GLSurfaceViewShared$GLThread gLSurfaceViewShared$GLThread) {
        gLSurfaceViewShared$GLThread.f269a = true;
        return true;
    }

    GLSurfaceViewShared$GLThread(WeakReference weakReference) {
        this.v = weakReference;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public final void run() {
        setName("GLThread " + getId());
        try {
            f();
        } catch (InterruptedException e) {
        } finally {
            GLSurfaceViewShared.sGLThreadManager.a(this);
        }
    }

    private void d() {
        if (this.h) {
            this.h = false;
            this.u.a();
        }
    }

    private void e() {
        if (this.g) {
            GLSurfaceViewShared$EglHelper gLSurfaceViewShared$EglHelper = this.u;
            if (gLSurfaceViewShared$EglHelper.f != null) {
                GLSurfaceViewShared gLSurfaceViewShared = (GLSurfaceViewShared) gLSurfaceViewShared$EglHelper.f268a.get();
                if (gLSurfaceViewShared != null) {
                    gLSurfaceViewShared.mEGLContextFactory.a(gLSurfaceViewShared$EglHelper.b, gLSurfaceViewShared$EglHelper.c, gLSurfaceViewShared$EglHelper.f);
                }
                gLSurfaceViewShared$EglHelper.f = null;
            }
            if (gLSurfaceViewShared$EglHelper.c != null) {
                gLSurfaceViewShared$EglHelper.b.eglTerminate(gLSurfaceViewShared$EglHelper.c);
                gLSurfaceViewShared$EglHelper.c = null;
            }
            this.g = false;
            GLSurfaceViewShared.sGLThreadManager.notifyAll();
        }
    }

    private void f() throws InterruptedException {
        this.u = new GLSurfaceViewShared$EglHelper(this.v);
        this.g = false;
        this.h = false;
        this.s = false;
        boolean createEglContext = false;
        boolean createEglSurface = false;
        boolean createGlInterface = false;
        boolean lostEglContext = false;
        boolean sizeChanged = false;
        boolean wantRenderNotification = false;
        boolean doRenderNotification = false;
        boolean askedToReleaseEglContext = false;
        int width = 0;
        int height = 0;
        Runnable event = null;
        Runnable finishDrawingRunnable = null;
        GL10 gl = null;

        try {
            while (true) {
                synchronized (GLSurfaceViewShared.sGLThreadManager) {
                    while (true) {
                        if (this.p) {
                            return;
                        }

                        if (!this.n.isEmpty()) {
                            event = (Runnable) this.n.remove(0);
                            break;
                        }

                        boolean pausing = false;
                        if (this.c != this.b) {
                            pausing = this.b;
                            this.c = this.b;
                            GLSurfaceViewShared.sGLThreadManager.notifyAll();
                        }

                        if (this.q) {
                            d();
                            e();
                            this.q = false;
                            askedToReleaseEglContext = true;
                        }

                        if (lostEglContext) {
                            d();
                            e();
                            lostEglContext = false;
                        }

                        if (pausing && this.h) {
                            d();
                        }

                        if (pausing && this.g) {
                            GLSurfaceViewShared view = (GLSurfaceViewShared) this.v.get();
                            boolean preserveEglContext = view != null && view.mPreserveEGLContextOnPause;
                            if (!preserveEglContext) {
                                e();
                            }
                        }

                        if (!this.d && !this.f) {
                            if (this.h) {
                                d();
                            }
                            this.f = true;
                            this.e = false;
                            GLSurfaceViewShared.sGLThreadManager.notifyAll();
                        }

                        if (this.d && this.f) {
                            this.f = false;
                            GLSurfaceViewShared.sGLThreadManager.notifyAll();
                        }

                        if (doRenderNotification) {
                            this.s = false;
                            doRenderNotification = false;
                            this.m = true;
                            GLSurfaceViewShared.sGLThreadManager.notifyAll();
                        }

                        if (this.t != null) {
                            finishDrawingRunnable = this.t;
                            this.t = null;
                        }

                        if (a()) {
                            if (!this.g) {
                                if (askedToReleaseEglContext) {
                                    askedToReleaseEglContext = false;
                                } else {
                                    try {
                                        this.u.b = (EGL10) EGLContext.getEGL();
                                        this.u.c = this.u.b.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
                                        if (this.u.c == EGL10.EGL_NO_DISPLAY) {
                                            throw new RuntimeException("eglGetDisplay failed");
                                        }
                                        if (!this.u.b.eglInitialize(this.u.c, new int[2])) {
                                            throw new RuntimeException("eglInitialize failed");
                                        }

                                        GLSurfaceViewShared view = (GLSurfaceViewShared) this.u.f268a.get();
                                        if (view != null) {
                                            this.u.e = view.mEGLConfigChooser.a(this.u.b, this.u.c);
                                            this.u.f = view.mEGLContextFactory.a(this.u.b, this.u.c, this.u.e);
                                        } else {
                                            this.u.e = null;
                                            this.u.f = null;
                                        }
                                        if (this.u.f == null || this.u.f == EGL10.EGL_NO_CONTEXT) {
                                            this.u.f = null;
                                            GLSurfaceViewShared$EglHelper.a("createContext", this.u.b.eglGetError());
                                        }
                                        this.u.d = null;
                                        this.g = true;
                                        createEglContext = true;
                                        GLSurfaceViewShared.sGLThreadManager.notifyAll();
                                    } catch (RuntimeException exception) {
                                        GLSurfaceViewShared.sGLThreadManager.notifyAll();
                                        throw exception;
                                    }
                                }
                            }

                            if (this.g && !this.h) {
                                this.h = true;
                                createEglSurface = true;
                                createGlInterface = true;
                                sizeChanged = true;
                            }

                            if (this.h) {
                                if (this.o) {
                                    sizeChanged = true;
                                    width = this.j;
                                    height = this.k;
                                    this.s = true;
                                    createEglSurface = true;
                                    this.o = false;
                                }
                                this.l = false;
                                GLSurfaceViewShared.sGLThreadManager.notifyAll();
                                if (this.s) {
                                    wantRenderNotification = true;
                                }
                                break;
                            }
                        } else if (finishDrawingRunnable != null) {
                            Log.w("GLSurfaceView", "Warning, !readyToDraw() but waiting for draw finished! Early reporting draw finished.");
                            finishDrawingRunnable.run();
                            finishDrawingRunnable = null;
                        }

                        GLSurfaceViewShared.sGLThreadManager.wait();
                    }
                }

                if (event != null) {
                    event.run();
                    event = null;
                    continue;
                }

                if (createEglSurface) {
                    if (this.u.b == null) {
                        throw new RuntimeException("egl not initialized");
                    }
                    if (this.u.c == null) {
                        throw new RuntimeException("eglDisplay not initialized");
                    }
                    if (this.u.e == null) {
                        throw new RuntimeException("mEglConfig not initialized");
                    }

                    this.u.a();
                    GLSurfaceViewShared view = (GLSurfaceViewShared) this.u.f268a.get();
                    if (view != null) {
                        this.u.d = view.mEGLWindowSurfaceFactory.a(this.u.b, this.u.c, this.u.e, view.getHolder());
                    } else {
                        this.u.d = null;
                    }

                    boolean surfaceCreated;
                    if (this.u.d == null || this.u.d == EGL10.EGL_NO_SURFACE) {
                        if (this.u.b.eglGetError() == 12299) {
                            Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                        }
                        surfaceCreated = false;
                    } else if (!this.u.b.eglMakeCurrent(this.u.c, this.u.d, this.u.d, this.u.f)) {
                        GLSurfaceViewShared$EglHelper.a("EGLHelper", "eglMakeCurrent", this.u.b.eglGetError());
                        surfaceCreated = false;
                    } else {
                        surfaceCreated = true;
                    }

                    if (surfaceCreated) {
                        synchronized (GLSurfaceViewShared.sGLThreadManager) {
                            this.i = true;
                            GLSurfaceViewShared.sGLThreadManager.notifyAll();
                        }
                    } else {
                        synchronized (GLSurfaceViewShared.sGLThreadManager) {
                            this.i = true;
                            this.e = true;
                            GLSurfaceViewShared.sGLThreadManager.notifyAll();
                        }
                        continue;
                    }
                    createEglSurface = false;
                }

                if (createGlInterface) {
                    GL rawGl = this.u.f.getGL();
                    GLSurfaceViewShared view = (GLSurfaceViewShared) this.u.f268a.get();
                    if (view != null) {
                        if (view.mGLWrapper != null) {
                            rawGl = view.mGLWrapper.a();
                        }
                        if ((view.mDebugFlags & 3) != 0) {
                            int unused = view.mDebugFlags;
                            if ((view.mDebugFlags & 2) != 0) {
                                new GLSurfaceViewShared$LogWriter();
                            }
                        }
                    }
                    gl = (GL10) rawGl;
                    createGlInterface = false;
                }

                if (createEglContext) {
                    GLSurfaceViewShared view = (GLSurfaceViewShared) this.v.get();
                    if (view != null) {
                        view.mRenderer.onSurfaceCreated(gl, this.u.e);
                    }
                    createEglContext = false;
                }

                if (sizeChanged) {
                    GLSurfaceViewShared view = (GLSurfaceViewShared) this.v.get();
                    if (view != null) {
                        view.mRenderer.onSurfaceChanged(gl, width, height);
                    }
                    sizeChanged = false;
                }

                GLSurfaceViewShared view = (GLSurfaceViewShared) this.v.get();
                if (view != null) {
                    view.mRenderer.onDrawFrame(gl);
                    if (finishDrawingRunnable != null) {
                        finishDrawingRunnable.run();
                        finishDrawingRunnable = null;
                    }
                }

                int swapError;
                if (this.u.b.eglSwapBuffers(this.u.c, this.u.d)) {
                    swapError = 12288;
                } else {
                    swapError = this.u.b.eglGetError();
                }

                switch (swapError) {
                    case 12288:
                        break;
                    case 12302:
                        lostEglContext = true;
                        break;
                    default:
                        GLSurfaceViewShared$EglHelper.a("GLThread", "eglSwapBuffers", swapError);
                        synchronized (GLSurfaceViewShared.sGLThreadManager) {
                            this.e = true;
                            GLSurfaceViewShared.sGLThreadManager.notifyAll();
                        }
                        break;
                }

                if (wantRenderNotification) {
                    doRenderNotification = true;
                    wantRenderNotification = false;
                }
            }
        } finally {
            synchronized (GLSurfaceViewShared.sGLThreadManager) {
                d();
                e();
            }
        }
    }

    final boolean a() {
        return !this.c && this.d && !this.e && this.j > 0 && this.k > 0 && (this.l || this.r == 1);
    }

    public final void a(int i) {
        if (i >= 0 && i <= 1) {
            synchronized (GLSurfaceViewShared.sGLThreadManager) {
                this.r = i;
                GLSurfaceViewShared.sGLThreadManager.notifyAll();
            }
            return;
        }
        throw new IllegalArgumentException("renderMode");
    }

    public final int b() {
        int i;
        synchronized (GLSurfaceViewShared.sGLThreadManager) {
            i = this.r;
        }
        return i;
    }

    public final void c() {
        synchronized (GLSurfaceViewShared.sGLThreadManager) {
            this.p = true;
            GLSurfaceViewShared.sGLThreadManager.notifyAll();
            while (!this.f269a) {
                try {
                    GLSurfaceViewShared.sGLThreadManager.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
