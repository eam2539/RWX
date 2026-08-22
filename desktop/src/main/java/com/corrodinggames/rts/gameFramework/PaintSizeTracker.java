package com.corrodinggames.rts.gameFramework;

import android.graphics.Paint;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m.class */
class PaintSizeTracker {
    float textSize;
    Paint paint;
    final /* synthetic */ GameEngine c;

    PaintSizeTracker(GameEngine gameEngine) {
        this.c = gameEngine;
    }

    void a() {
        float screenPixels = this.c.toScreenPixels(this.textSize);
        if (this.paint.k() != screenPixels) {
            if (this.paint instanceof GamePaint) {
                ((GamePaint) this.paint).c(screenPixels);
            } else {
                this.paint.b(screenPixels);
            }
        }
    }
}
