package com.corrodinggames.rts.appFramework;

import com.corrodinggames.rts.gameFramework.android.graphics.AndroidGLRenderer;
import com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.ab */
/* JADX INFO: loaded from: classes.dex */
public interface InputContext {
    void drawCompleted(float f, int i);

    void drawStarting(float f, int i);

    void flushCanvas();

    void forceSurfaceUnlockWorkaround();

    CurrTouchPoint getCurrTouchPoint();

    boolean getDirectSurfaceRendering();

    Object getGameThreadSync();

    InGameActivity getInGameActivity();

    GraphicsInterface getNewCanvasLock(boolean z);

    AndroidGLRenderer getRenderer();

    boolean getSurfaceExists();

    boolean isFullscreen();

    boolean isPaused();

    void onParentPause();

    void onParentResume();

    void onParentStart();

    void onParentStop();

    void onParentWindowFocusChanged(boolean z);

    void onReplacedByAnotherView();

    void setInGameActivity(InGameActivity inGameActivity);

    void unlockAndReturnCanvas(GraphicsInterface graphicsInterface, boolean z);

    void updateResolution();

    boolean usingBasicDraw();
}
