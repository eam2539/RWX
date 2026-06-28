package com.corrodinggames.rts.game.map;

import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.RenderTargetMode;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.Rect;
import io.github.rwx.geometry.RectF;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.b.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/b/d.class */
public class LayerBufferCell {

    /* JADX INFO: renamed from: a */
    public GraphicsEngine cellGraphicsCopy;

    /* JADX INFO: renamed from: b */
    int gridLinearIndex;

    /* JADX INFO: renamed from: c */
    int redrawVersion;

    /* JADX INFO: renamed from: d */
    public Texture cellLayerTexture;

    /* JADX INFO: renamed from: e */
    public Texture fadeOutTexture;

    /* JADX INFO: renamed from: f */
    public GraphicsEngine fadeOutGraphics;

    /* JADX INFO: renamed from: g */
    public float fadeProgressRatio;

    /* JADX INFO: renamed from: i */
    public int gridX;

    /* JADX INFO: renamed from: j */
    public int gridY;

    /* JADX INFO: renamed from: s */
    final /* synthetic */ LayerBufferManager manager;

    /* JADX INFO: renamed from: h */
    public KoolPaint fadeBlendPaint = new GamePaint();

    /* JADX INFO: renamed from: k */
    public boolean needsRedraw = true;

    /* JADX INFO: renamed from: l */
    public boolean enableSmoothFade = true;

    /* JADX INFO: renamed from: m */
    public int fadeFrameCount = 0;

    /* JADX INFO: renamed from: n */
    public boolean preRendered = false;

    /* JADX INFO: renamed from: o */
    public final Rect tileSrcRect = new Rect();

    /* JADX INFO: renamed from: p */
    public final Rect screenDstRect = new Rect();

    /* JADX INFO: renamed from: q */
    public final RectF screenDstRectF = new RectF();

    /* JADX INFO: renamed from: r */
    public final Rect worldBoundsRect = new Rect();

    /* JADX INFO: renamed from: a */
    public void initFadeBufferTexture() {
        GraphicsEngine graphicsBackend = this.manager.resourceBackend();
        this.fadeOutTexture = graphicsBackend.a(this.cellLayerTexture.p, this.cellLayerTexture.q, true);
        if (this.fadeOutTexture != null && !this.fadeOutTexture.A()) {
            this.fadeOutTexture.a("fadeOutBitmap");
        }
        if (this.fadeOutTexture == null || this.fadeOutTexture.A()) {
            throw new OutOfMemoryError("Failed to create fade out bitmap");
        }
        this.fadeOutTexture.b(true);
        this.fadeOutGraphics = graphicsBackend.b(this.fadeOutTexture, RenderTargetMode.IMMEDIATE);
    }

    /* JADX INFO: renamed from: b */
    public Rect getWorldBoundsRect() {
        this.worldBoundsRect.a(getWorldLeft(), getWorldTop(), getWorldLeft() + this.manager.cellWorldExtent, getWorldTop() + this.manager.cellWorldExtent);
        return this.worldBoundsRect;
    }

    public LayerBufferCell(LayerBufferManager layerBufferManager, int i, int i2) {
        this.manager = layerBufferManager;
        this.gridX = i;
        this.gridY = i2;
    }

    /* JADX INFO: renamed from: c */
    public int getWorldLeft() {
        return this.manager.gridOriginWorldX + (this.gridX * this.manager.cellWorldStepSize);
    }

    /* JADX INFO: renamed from: d */
    public int getWorldTop() {
        return this.manager.gridOriginWorldY + (this.gridY * this.manager.cellWorldStepSize);
    }
}
