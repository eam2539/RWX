package com.corrodinggames.rts.appFramework;

import com.corrodinggames.rts.gameFramework.graphics.AudioRenderer;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/f.class */
public interface GameView {
    /* JADX INFO: renamed from: a */
    void pause();

    /* JADX INFO: renamed from: a */
    void resume(boolean z);

    /* JADX INFO: renamed from: b */
    boolean isPaused();

    /* JADX INFO: renamed from: c */
    boolean isSurfaceViewReady();

    /* JADX INFO: renamed from: d */
    AudioRenderer getAudioRenderer();

    /* JADX INFO: renamed from: e */
    boolean isContinuousRendering();

    /* JADX INFO: renamed from: f */
    boolean isRendering();

    /* JADX INFO: renamed from: g */
    Object getHolder();

    /* JADX INFO: renamed from: h */
    void onPause();

    /* JADX INFO: renamed from: i */
    InGameActivity getSurfaceHolder();

    /* JADX INFO: renamed from: j */
    void onResume();

    /* JADX INFO: renamed from: k */
    MultiTouchPointerState getSettings();

    /* JADX INFO: renamed from: a */
    void onDraw(float f, int i);

    /* JADX INFO: renamed from: b */
    void onUpdate(float f, int i);

    /* JADX INFO: renamed from: l */
    void onSizeChanged();

    /* JADX INFO: renamed from: b */
    GraphicsInterface lockCanvas(boolean z);

    /* JADX INFO: renamed from: a */
    void unlockCanvasAndPost(GraphicsInterface graphicsInterface, boolean z);

    /* JADX INFO: renamed from: m */
    void stopRender();

    /* JADX INFO: renamed from: n */
    boolean hasBeenStarted();
}
