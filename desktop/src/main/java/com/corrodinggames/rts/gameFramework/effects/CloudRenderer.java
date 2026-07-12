package com.corrodinggames.rts.gameFramework.effects;

import android.graphics.Paint;
import android.graphics.RectF;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.d.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/d/b.class */
public class CloudRenderer {

    /* JADX INFO: renamed from: a */
    boolean isInitialized = false;

    /* JADX INFO: renamed from: b */
    Texture noiseTexture = null;

    /* JADX INFO: renamed from: c */
    Paint paint = new GamePaint();

    /* JADX INFO: renamed from: d */
    RectF drawRect = new RectF();

    /* JADX INFO: renamed from: e */
    float scrollX = 0.0f;

    /* JADX INFO: renamed from: f */
    float scrollY = 0.0f;

    /* JADX INFO: renamed from: a */
    public boolean shouldRender() {
        return GameEngine.getInstance().settingsEngine.renderClouds;
    }

    /* JADX INFO: renamed from: b */
    public void initialize() {
        this.noiseTexture = GameEngine.getInstance().renderGraphicsEngine.a(R.drawable.noise, false);
        this.isInitialized = true;
    }

    /* JADX INFO: renamed from: a */
    public void update(float f) {
        if (!shouldRender()) {
            return;
        }
        if (!this.isInitialized) {
            initialize();
        }
        this.scrollX += 0.2f * f;
        this.scrollY += 0.07f * f;
        this.scrollX %= this.noiseTexture.m();
        this.scrollY %= this.noiseTexture.l();
    }

    /* JADX INFO: renamed from: b */
    public void draw(float f) {
        if (!shouldRender()) {
            return;
        }
        if (!this.isInitialized) {
            initialize();
        }
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.renderGraphicsEngine.i();
        gameEngine.renderGraphicsEngine.a(3.0f, 3.0f);
        float fMax = (int) Utility.max((-gameEngine.viewpointXSnapped) / 3.0f, 0.0f);
        float fMax2 = (int) Utility.max((-gameEngine.viewpointYSnapped) / 3.0f, 0.0f);
        this.drawRect.a(fMax, fMax2, ((int) (gameEngine.visibleWorldWidth / 3.0f)) + 1, ((int) (gameEngine.visibleWorldHeight / 3.0f)) + 1);
        this.paint.b(-16777216);
        this.paint.c(40);
        gameEngine.renderGraphicsEngine.a(this.noiseTexture, this.drawRect, this.paint, (gameEngine.viewpointXSnapped / 3.0f) + fMax + this.scrollX, (gameEngine.viewpointYSnapped / 3.0f) + fMax2 + this.scrollY, 0, 0);
        gameEngine.renderGraphicsEngine.j();
    }
}
