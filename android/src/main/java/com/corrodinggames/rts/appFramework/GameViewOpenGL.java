package com.corrodinggames.rts.appFramework;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.corrodinggames.rts.appFramework.android.AndroidSAF;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.LogicNumberFuntion;
import com.corrodinggames.rts.gameFramework.android.graphics.*;
import com.corrodinggames.rts.gameFramework.m.GLDrawCommand;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

/* JADX INFO: loaded from: classes.dex */
public class GameViewOpenGL extends GLSurfaceViewShared implements InputContext, MultiTouchController$MultiTouchObjectCanvas, GLSurfaceViewShared$Renderer {
    private static final int EGL_CONTEXT_CLIENT_VERSION_VALUE = 2;
    static GameViewOpenGL$RequestRenderThread renderManagerThread = null;
    public static final boolean retainGlContext = true;
    protected static GraphicsEngine retainedCanvas;
    OpenGLGraphicsRenderer canvasDirectGL;
    DeferredGraphicsInterface canvasProxy;
    Context context;
    Resources contextResources;
    public CurrTouchPoint currTouchPoint;
    Object drawDone;
    volatile boolean drawPending;
    int drawTimeouts;
    int fullHeight;
    int fullWidth;
    public Object gameThreadSync;
    protected GL10 gl;
    boolean hasCanvasRendered;
    public InGameActivity inGameActivity;
    boolean isActive;
    Method lockHardwareCanvasMethod;
    boolean loggedDrawTimeout;
    protected GraphicsEngine mCanvas;
    public MotionEventCompat multiTouchController;
    public volatile boolean paused;
    public AndroidGLRenderer renderer;
    public volatile boolean surfaceExists;
    SurfaceHolder surfaceHolderOnLock;
    static int numberOfNonRenderedCanvas = 0;
    static Object renderManagerLock = new Object();
    static boolean requestRenderQueued = false;
    public static Object makeActiveLock = new Object();
    static EGLContext retainedGlContext = null;
    public static GameViewOpenGL lastHeldSurfaceView = null;

    void requestRenderNonBlocking() {
        requestRender();
    }

    public String getStats() {
        return "NO STATS";
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void onParentStop() {
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void onParentStart() {
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void onReplacedByAnotherView() {
        this.paused = true;
        synchronized (this.drawDone) {
            this.drawDone.notifyAll();
        }
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void onParentPause() {
        GameEngine.log("GameView:onParentPause start - " + hashCode());
        synchronized (this.gameThreadSync) {
            GameEngine.log("GameView:onParentPause synchronized - " + hashCode());
        }
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void onParentResume() {
        GameEngine.log("GameView:onParentResume - " + hashCode());
        this.paused = false;
        makeActive();
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void onParentWindowFocusChanged(boolean z) {
    }

    public GameViewOpenGL(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.surfaceExists = false;
        this.gameThreadSync = new Object();
        this.fullWidth = -1;
        this.fullHeight = -1;
        this.hasCanvasRendered = false;
        this.paused = false;
        this.canvasProxy = new DeferredGraphicsInterface();
        this.drawDone = new Object();
        this.drawTimeouts = 0;
        this.loggedDrawTimeout = false;
        this.canvasDirectGL = new OpenGLGraphicsRenderer();
        this.isActive = true;
        Log.e(AndroidSAF.TAG, "GameView:GameViewOpenGL()");
        this.multiTouchController = new MotionEventCompat(this);
        this.currTouchPoint = new CurrTouchPoint();
        init(context);
    }

    void init(Context context) {
        initGL();
        this.context = context;
        this.contextResources = context.getResources();
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared
    protected void finalize() throws Throwable {
        Log.e(AndroidSAF.TAG, "GameView:finalize()");
        super.finalize();
    }

    public Resources getContextResources() {
        return this.contextResources;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        synchronized (this.gameThreadSync) {
            GameEngine.log("GameEngine onSizeChanged currentGameView.gameThreadSync - " + this.gameThreadSync.hashCode());
        }
        super.onSizeChanged(i, i2, i3, i4);
        GameEngine.log("GameViewOpenGL onSizeChanged: " + i + ", " + i2);
        this.fullWidth = i;
        this.fullHeight = i2;
        updateResolution();
        if (lastHeldSurfaceView == this && this.mCanvas != null) {
            GameEngine.log("GameViewOpenGL mCanvas.setSize: " + i + ", " + i2);
            this.mCanvas.a(i, i2);
        }
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void updateResolution() {
        if (this.fullWidth != -1) {
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine == null) {
                return;
            }
            int i = this.fullWidth;
            int i2 = this.fullHeight;
            if (gameEngine.settingsEngine != null && gameEngine.settingsEngine.renderDoubleScale) {
                i = this.fullWidth / 2;
                i2 = this.fullHeight / 2;
            }
            getHolder().setFixedSize(i, i2);
            gameEngine.updateWindowResolution(i, i2);
        }
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared, android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        GameEngine.log("GameView:surfaceCreated start - " + hashCode());
        updateResolution();
        this.surfaceExists = true;
        super.surfaceCreated(surfaceHolder);
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared, android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("GameView:surfaceDestroyed start - " + hashCode());
        this.surfaceExists = false;
        synchronized (this.gameThreadSync) {
            GameEngine.log("GameEngine catch currentGameView.gameThreadSync - " + this.gameThreadSync.hashCode());
        }
        GameEngine.log("GameView:surfaceDestroyed finished - " + hashCode());
        this.drawPending = false;
        super.surfaceDestroyed(surfaceHolder);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (Build.VERSION.SDK_INT >= 9) {
            motionEvent.getSource();
            motionEvent.getSource();
        }
        return this.multiTouchController.a(motionEvent);
    }

    @Override // com.corrodinggames.rts.appFramework.MultiTouchController$MultiTouchObjectCanvas
    public Object getDraggableObjectAtPoint(CurrTouchPoint currTouchPoint) {
        return this;
    }

    @Override // com.corrodinggames.rts.appFramework.MultiTouchController$MultiTouchObjectCanvas
    public void getPositionAndScale(Object obj, MultiTouchController$PositionAndScale multiTouchController$PositionAndScale) {
    }

    @Override // com.corrodinggames.rts.appFramework.MultiTouchController$MultiTouchObjectCanvas
    public void selectObject(Object obj, CurrTouchPoint currTouchPoint) {
        this.currTouchPoint.a(currTouchPoint);
    }

    @Override // com.corrodinggames.rts.appFramework.MultiTouchController$MultiTouchObjectCanvas
    public boolean setPositionAndScale(Object obj, MultiTouchController$PositionAndScale multiTouchController$PositionAndScale, CurrTouchPoint currTouchPoint) {
        this.currTouchPoint.a(currTouchPoint);
        return true;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void forceSurfaceUnlockWorkaround() {
        GameEngine.log("Forcing an unlock of surfaceview to avoid freeze - " + hashCode());
        try {
            Field declaredField = SurfaceView.class.getDeclaredField("mSurfaceLock");
            declaredField.setAccessible(true);
            ((ReentrantLock) declaredField.get(this)).unlock();
        } catch (Exception e) {
            GameEngine.log("Exception while forcing unlock - " + hashCode());
            e.printStackTrace();
        }
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public boolean getSurfaceExists() {
        return this.surfaceExists;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public boolean getDirectSurfaceRendering() {
        return true;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public AndroidGLRenderer getRenderer() {
        return this.renderer;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public boolean isPaused() {
        return this.paused;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public Object getGameThreadSync() {
        return this.gameThreadSync;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public InGameActivity getInGameActivity() {
        return this.inGameActivity;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void setInGameActivity(InGameActivity inGameActivity) {
        this.inGameActivity = inGameActivity;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public CurrTouchPoint getCurrTouchPoint() {
        return this.currTouchPoint;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void drawStarting(float f, int i) {
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void drawCompleted(float f, int i) {
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void flushCanvas() {
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public GraphicsInterface getNewCanvasLock(boolean z) {
        GraphicsInterface nullGraphicsRenderer;
        synchronized (this.drawDone) {
            if (this.drawPending) {
                try {
                    this.drawDone.wait(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (this.drawPending) {
                this.drawTimeouts++;
                if (this.drawTimeouts > 3 && !this.loggedDrawTimeout) {
                    GameEngine.log("getNewCanvasLock - Timing out - surfaceExists:" + this.surfaceExists);
                    if (this.surfaceExists && isSurfaceBadHack()) {
                        GameEngine.log("Detected bad surface, removing all retained opengl context");
                        clearRetainedGLContext();
                    }
                }
                nullGraphicsRenderer = new NullGraphicsRenderer();
            } else {
                this.drawTimeouts = 0;
                this.canvasProxy.a();
                nullGraphicsRenderer = this.canvasProxy;
            }
        }
        return nullGraphicsRenderer;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public void unlockAndReturnCanvas(GraphicsInterface graphicsInterface, boolean z) {
        synchronized (this.drawDone) {
            this.drawPending = true;
        }
        requestRenderNonBlocking();
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public boolean usingBasicDraw() {
        return false;
    }

    @Override // com.corrodinggames.rts.appFramework.InputContext
    public boolean isFullscreen() {
        return false;
    }

    public void onNewWindow() {
    }

    public static void clearRetainedGLContext() {
        retainedGlContext = null;
        retainedCanvas = null;
    }

    void makeActive() {
        synchronized (makeActiveLock) {
            if (lastHeldSurfaceView != null && lastHeldSurfaceView != this) {
                lastHeldSurfaceView.isActive = false;
                lastHeldSurfaceView.onPause();
            }
            lastHeldSurfaceView = this;
            if (!this.isActive) {
                lastHeldSurfaceView.isActive = true;
                onResume();
            }
        }
    }

    protected void initGL() {
        setZOrderOnTop(false);
        setEGLContextClientVersion(2);
        setEGLContextFactory(new GameViewOpenGL$RetainedContextFactory(this));
        if (Build.VERSION.SDK_INT >= 16) {
            setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        } else {
            setEGLConfigChooser(5, 6, 5, 8, 0, 0);
        }
        if (Build.VERSION.SDK_INT >= 11) {
            setPreserveEGLContextOnPause(true);
        }
        getHolder().setFormat(-3);
        setRenderer(this);
        setRenderMode(0);
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$Renderer
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        GameEngine.log("GameViewOpenGL onSurfaceCreated");
        this.mCanvas = getOrCreateRetainedCanvas();
    }

    private static synchronized GraphicsEngine getOrCreateRetainedCanvas() {
        if (retainedCanvas == null) {
            retainedCanvas = new GraphicsEngine();
        }
        return retainedCanvas;
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$Renderer
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        GameEngine.log("GameViewOpenGL onSurfaceChanged");
        if (lastHeldSurfaceView == this) {
            GraphicsEngine canvas = this.mCanvas;
            if (canvas == null) {
                canvas = getOrCreateRetainedCanvas();
                this.mCanvas = canvas;
            }
            canvas.a(i, i2);
        } else {
            GameEngine.log("GameViewOpenGL onSurfaceChanged - not lastHeldSurfaceView");
        }
    }

    @Override // com.corrodinggames.rts.appFramework.GLSurfaceViewShared$Renderer
    public void onDrawFrame(GL10 gl10) {
        this.gl = gl10;
        GraphicsEngine canvas = this.mCanvas;
        if (canvas == null) {
            GameEngine.log("GameViewOpenGL onDrawFrame - skipping frame without canvas");
            return;
        }
        canvas.c();
        onGLDraw(canvas);
        this.hasCanvasRendered = true;
    }

    protected void onGLDraw(GraphicsEngine graphicsEngine) {
        GameEngine.getInstance();
        if (this.drawPending) {
            synchronized (this.canvasDirectGL.c) {
                OpenGLGraphicsRenderer openGLGraphicsRenderer = this.canvasDirectGL;
                openGLGraphicsRenderer.f771a = graphicsEngine;
                openGLGraphicsRenderer.b = (OpenGLRenderer) openGLGraphicsRenderer.f771a.b();
                OpenGLGraphicsRenderer openGLGraphicsRenderer2 = this.canvasDirectGL;
                OpenGLRenderer openGLRenderer = (OpenGLRenderer) openGLGraphicsRenderer2.f771a.b();
                openGLRenderer.h();
                GLES20.glViewport(0, 0, openGLRenderer.e, openGLRenderer.f);
                openGLRenderer.N = true;
                openGLRenderer.t.f575a = null;
                if (openGLRenderer.f572a != null) {
                    TextureManager textureManager = openGLRenderer.f572a;
                    textureManager.h++;
                    if (textureManager.g && textureManager.h > 600) {
                        textureManager.i = true;
                        textureManager.e.clear();
                    }
                }
                if (OpenGLGraphicsRenderer.g) {
                    OpenGLGraphicsRenderer.g = false;
                    openGLGraphicsRenderer2.f771a.d();
                }
                DeferredGraphicsInterface deferredGraphicsInterface = this.canvasProxy;
                deferredGraphicsInterface.f773a = this.canvasDirectGL;
                GLDrawCommand[] gLDrawCommandArr = deferredGraphicsInterface.k.b;
                int i = deferredGraphicsInterface.l;
                for (int i2 = 0; i2 < i; i2++) {
                    GLDrawCommand gLDrawCommand = gLDrawCommandArr[i2];
                    gLDrawCommand.f753a.a(gLDrawCommand.g.f773a, gLDrawCommand);
                }
                this.canvasProxy.a();
                OpenGLGraphicsRenderer openGLGraphicsRenderer3 = this.canvasDirectGL;
                OpenGLRenderer openGLRenderer2 = openGLGraphicsRenderer3.b;
                if (OpenGLGraphicsRenderer.f) {
                    openGLRenderer2.a("GL - #tex: " + OpenGLRenderer.G + " tex size:" + LogicNumberFuntion.d(OpenGLRenderer.H), 70.0f, 90.0f, openGLRenderer2.F);
                }
                openGLGraphicsRenderer3.b.b((C0009fo) null);
                openGLRenderer2.g();
                openGLRenderer2.b((C0009fo) null);
                if (openGLRenderer2.k.b != 0 || openGLRenderer2.l.b != 0) {
                    synchronized (openGLRenderer2.k) {
                        BlendModeState blendModeState = openGLRenderer2.k;
                        if (openGLRenderer2.k.b > 0) {
                            OpenGLRenderer.m.a(blendModeState.b, blendModeState.f556a);
                            blendModeState.a();
                        }
                        BlendModeState blendModeState2 = openGLRenderer2.l;
                        if (blendModeState2.b > 0) {
                            OpenGLRenderer.m.b(blendModeState2.b, blendModeState2.f556a);
                            blendModeState2.a();
                        }
                    }
                }
                if (openGLRenderer2.f572a != null) {
                    openGLRenderer2.f572a.a();
                }
                if (OpenGLGraphicsRenderer.g) {
                    OpenGLGraphicsRenderer.g = false;
                    openGLGraphicsRenderer3.f771a.d();
                }
                if (openGLRenderer2.u != 0) {
                    GameEngine.log("endFrame: currentTransformIndex=" + openGLRenderer2.u);
                }
                openGLGraphicsRenderer3.e++;
                if (openGLGraphicsRenderer3.e > 60) {
                    openGLGraphicsRenderer3.e = 0;
                    openGLGraphicsRenderer3.f771a.f();
                    openGLGraphicsRenderer3.f771a.e();
                }
                this.canvasProxy.m = false;
            }
            this.drawPending = false;
            synchronized (this.drawDone) {
                this.drawDone.notifyAll();
            }
        }
    }
}
