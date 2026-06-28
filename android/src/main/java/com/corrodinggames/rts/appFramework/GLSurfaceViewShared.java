package com.corrodinggames.rts.appFramework;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class GLSurfaceViewShared extends SurfaceView implements SurfaceHolder.Callback2 {
    private static final int CONFIG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;
    private static final int EGL14_EGL_OPENGL_ES2_BIT = 4;
    private static final int EGLExt_EGL_OPENGL_ES3_BIT_KHR = 64;
    private static final boolean LOG_ATTACH_DETACH = false;
    private static final boolean LOG_EGL = false;
    private static final boolean LOG_PAUSE_RESUME = false;
    private static final boolean LOG_RENDERER = false;
    private static final boolean LOG_RENDERER_DRAW_FRAME = false;
    private static final boolean LOG_SURFACE = false;
    private static final boolean LOG_THREADS = false;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    private static final String TAG = "GLSurfaceView";
    private static final long Trace_TRACE_TAG_VIEW = 8;
    static final GLSurfaceViewShared$GLThreadManager sGLThreadManager = new GLSurfaceViewShared$GLThreadManager((byte) 0);
    int mDebugFlags;
    private boolean mDetached;
    GLSurfaceViewShared$EGLConfigChooser mEGLConfigChooser;
    int mEGLContextClientVersion;
    GLSurfaceViewShared$EGLContextFactory mEGLContextFactory;
    GLSurfaceViewShared$EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLSurfaceViewShared$GLThread mGLThread;
    GLSurfaceViewShared$GLWrapper mGLWrapper;
    boolean mPreserveEGLContextOnPause;
    GLSurfaceViewShared$Renderer mRenderer;
    private final WeakReference mThisWeakRef;

    public GLSurfaceViewShared(Context context) {
        super(context);
        this.mThisWeakRef = new WeakReference(this);
        init();
    }

    public GLSurfaceViewShared(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mThisWeakRef = new WeakReference(this);
        init();
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mGLThread != null) {
                this.mGLThread.c();
            }
        } finally {
            super.finalize();
        }
    }

    private void init() {
        getHolder().addCallback(this);
    }

    public void setGLWrapper(GLSurfaceViewShared$GLWrapper gLSurfaceViewShared$GLWrapper) {
        this.mGLWrapper = gLSurfaceViewShared$GLWrapper;
    }

    public void setDebugFlags(int i) {
        this.mDebugFlags = i;
    }

    public int getDebugFlags() {
        return this.mDebugFlags;
    }

    public void setPreserveEGLContextOnPause(boolean z) {
        this.mPreserveEGLContextOnPause = z;
    }

    public boolean getPreserveEGLContextOnPause() {
        return this.mPreserveEGLContextOnPause;
    }

    public void setRenderer(GLSurfaceViewShared$Renderer gLSurfaceViewShared$Renderer) {
        checkRenderThreadState();
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new GLSurfaceViewShared$DepthComponentSizeChooser(this, true);
        }
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = new GLSurfaceViewShared$DefaultContextFactory(this, (byte) 0);
        }
        if (this.mEGLWindowSurfaceFactory == null) {
            this.mEGLWindowSurfaceFactory = new GLSurfaceViewShared$DefaultWindowSurfaceFactory((byte) 0);
        }
        this.mRenderer = gLSurfaceViewShared$Renderer;
        this.mGLThread = new GLSurfaceViewShared$GLThread(this.mThisWeakRef);
        this.mGLThread.start();
    }

    public void setEGLContextFactory(GLSurfaceViewShared$EGLContextFactory gLSurfaceViewShared$EGLContextFactory) {
        checkRenderThreadState();
        this.mEGLContextFactory = gLSurfaceViewShared$EGLContextFactory;
    }

    public void setEGLWindowSurfaceFactory(GLSurfaceViewShared$EGLWindowSurfaceFactory gLSurfaceViewShared$EGLWindowSurfaceFactory) {
        checkRenderThreadState();
        this.mEGLWindowSurfaceFactory = gLSurfaceViewShared$EGLWindowSurfaceFactory;
    }

    public void setEGLConfigChooser(GLSurfaceViewShared$EGLConfigChooser gLSurfaceViewShared$EGLConfigChooser) {
        checkRenderThreadState();
        this.mEGLConfigChooser = gLSurfaceViewShared$EGLConfigChooser;
    }

    public void setEGLConfigChooser(boolean z) {
        setEGLConfigChooser(new GLSurfaceViewShared$DepthComponentSizeChooser(this, z));
    }

    public void setEGLConfigChooser(int i, int i2, int i3, int i4, int i5, int i6) {
        setEGLConfigChooser(new GLSurfaceViewShared$ComponentSizeChooser(this, i, i2, i3, i4, i5, i6));
    }

    public void setEGLContextClientVersion(int i) {
        checkRenderThreadState();
        this.mEGLContextClientVersion = i;
    }

    public void setRenderMode(int i) {
        this.mGLThread.a(i);
    }

    public int getRenderMode() {
        return this.mGLThread.b();
    }

    public boolean isSurfaceBadHack() {
        if (this.mGLThread == null) {
            return false;
        }
        return this.mGLThread.e;
    }

    public void requestRender() {
        GLSurfaceViewShared$GLThread gLSurfaceViewShared$GLThread = this.mGLThread;
        synchronized (sGLThreadManager) {
            gLSurfaceViewShared$GLThread.l = true;
            sGLThreadManager.notifyAll();
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        GLSurfaceViewShared$GLThread gLSurfaceViewShared$GLThread = this.mGLThread;
        synchronized (sGLThreadManager) {
            gLSurfaceViewShared$GLThread.d = true;
            gLSurfaceViewShared$GLThread.i = false;
            sGLThreadManager.notifyAll();
            while (gLSurfaceViewShared$GLThread.f && !gLSurfaceViewShared$GLThread.i && !gLSurfaceViewShared$GLThread.f269a) {
                try {
                    sGLThreadManager.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        GLSurfaceViewShared$GLThread gLSurfaceViewShared$GLThread = this.mGLThread;
        synchronized (sGLThreadManager) {
            gLSurfaceViewShared$GLThread.d = false;
            sGLThreadManager.notifyAll();
            while (!gLSurfaceViewShared$GLThread.f && !gLSurfaceViewShared$GLThread.f269a) {
                try {
                    sGLThreadManager.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        GLSurfaceViewShared$GLThread gLSurfaceViewShared$GLThread = this.mGLThread;
        synchronized (sGLThreadManager) {
            gLSurfaceViewShared$GLThread.j = i2;
            gLSurfaceViewShared$GLThread.k = i3;
            gLSurfaceViewShared$GLThread.o = true;
            gLSurfaceViewShared$GLThread.l = true;
            gLSurfaceViewShared$GLThread.m = false;
            if (Thread.currentThread() != gLSurfaceViewShared$GLThread) {
                sGLThreadManager.notifyAll();
                while (!gLSurfaceViewShared$GLThread.f269a && !gLSurfaceViewShared$GLThread.c && !gLSurfaceViewShared$GLThread.m) {
                    if (!(gLSurfaceViewShared$GLThread.g && gLSurfaceViewShared$GLThread.h && gLSurfaceViewShared$GLThread.a())) {
                        break;
                    }
                    try {
                        sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    @Override // android.view.SurfaceHolder.Callback2
    @Deprecated
    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
    }

    public void onPause() {
        GLSurfaceViewShared$GLThread gLSurfaceViewShared$GLThread = this.mGLThread;
        synchronized (sGLThreadManager) {
            gLSurfaceViewShared$GLThread.b = true;
            sGLThreadManager.notifyAll();
            while (!gLSurfaceViewShared$GLThread.f269a && !gLSurfaceViewShared$GLThread.c) {
                try {
                    sGLThreadManager.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void onResume() {
        GLSurfaceViewShared$GLThread gLSurfaceViewShared$GLThread = this.mGLThread;
        synchronized (sGLThreadManager) {
            gLSurfaceViewShared$GLThread.b = false;
            gLSurfaceViewShared$GLThread.l = true;
            gLSurfaceViewShared$GLThread.m = false;
            sGLThreadManager.notifyAll();
            while (!gLSurfaceViewShared$GLThread.f269a && gLSurfaceViewShared$GLThread.c && !gLSurfaceViewShared$GLThread.m) {
                try {
                    sGLThreadManager.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void queueEvent(Runnable runnable) {
        GLSurfaceViewShared$GLThread gLSurfaceViewShared$GLThread = this.mGLThread;
        if (runnable != null) {
            synchronized (sGLThreadManager) {
                gLSurfaceViewShared$GLThread.n.add(runnable);
                sGLThreadManager.notifyAll();
            }
            return;
        }
        throw new IllegalArgumentException("r must not FastArrayList null");
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mDetached && this.mRenderer != null) {
            int iB = this.mGLThread != null ? this.mGLThread.b() : 1;
            this.mGLThread = new GLSurfaceViewShared$GLThread(this.mThisWeakRef);
            if (iB != 1) {
                this.mGLThread.a(iB);
            }
            this.mGLThread.start();
        }
        this.mDetached = false;
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onDetachedFromWindow() {
        if (this.mGLThread != null) {
            this.mGLThread.c();
        }
        this.mDetached = true;
        super.onDetachedFromWindow();
    }

    private void checkRenderThreadState() {
        if (this.mGLThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }
}
