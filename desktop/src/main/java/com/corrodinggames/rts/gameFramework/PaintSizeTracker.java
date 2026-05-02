package com.corrodinggames.rts.gameFramework;

import android.graphics.Paint;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m.class */
class PaintSizeTracker {
    float a;
    Paint b;
    final /* synthetic */ GameEngine c;

    PaintSizeTracker(GameEngine gameEngine) {
        this.c = gameEngine;
    }

    void a() {
        float screenPixels = this.c.toScreenPixels(this.a);
        if (this.b.k() != screenPixels) {
            if (this.b instanceof GamePaint) {
                ((GamePaint) this.b).c(screenPixels);
            } else {
                this.b.b(screenPixels);
            }
        }
    }
}
