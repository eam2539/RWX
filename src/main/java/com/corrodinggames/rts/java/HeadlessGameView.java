package com.corrodinggames.rts.java;

import com.corrodinggames.rts.appFramework.GameView;
import com.corrodinggames.rts.appFramework.InGameActivity;
import com.corrodinggames.rts.appFramework.MultiTouchPointerState;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.AudioRenderer;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface;
import com.corrodinggames.rts.gameFramework.graphics.NullGraphicsRenderer;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/d.class */
public class HeadlessGameView implements GameView {
    public int a;
    public int b;
    MultiTouchPointerState d;
    public Object e = new Object();
    public Object f = new Object();
    NullGraphicsRenderer g = new NullGraphicsRenderer();
    JavaInGameActivity c = new JavaInGameActivity();

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: j */
    public void onResume() {
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: a */
    public void pause() {
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: b */
    public boolean isPaused() {
        return true;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: c */
    public boolean isSurfaceViewReady() {
        return false;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: d */
    public AudioRenderer getAudioRenderer() {
        return null;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: e */
    public boolean isContinuousRendering() {
        return false;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: f */
    public boolean isRendering() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine != null && gameEngine.settingsEngine.slick2dFullScreen) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: g */
    public Object getHolder() {
        return null;
    }

    public int o() {
        return this.a;
    }

    public int p() {
        return this.b;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: h */
    public void onPause() {
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: i */
    public InGameActivity getSurfaceHolder() {
        return this.c;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: k */
    public MultiTouchPointerState getSettings() {
        return this.d;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: a */
    public void onDraw(float f, int i) {
        synchronized (this.f) {
            this.f.notifyAll();
        }
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: b */
    public void onUpdate(float f, int i) {
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: l */
    public void onSizeChanged() {
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: b */
    public GraphicsInterface lockCanvas(boolean z) {
        return this.g;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: a */
    public void unlockCanvasAndPost(GraphicsInterface graphicsInterface, boolean z) {
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: m */
    public void stopRender() {
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: n */
    public boolean hasBeenStarted() {
        return true;
    }

    @Override // com.corrodinggames.rts.appFramework.GameView
    /* JADX INFO: renamed from: a */
    public void resume(boolean z) {
    }
}
