package com.corrodinggames.rts.game.map;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.RenderTargetMode;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolArgbColor;
import io.github.rwx.render.canvas.KoolCanvasBlendMode;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.b.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/b/h.class */
public class TileAtlasCache {

    private GraphicsEngine graphicsBackend;

    /* JADX INFO: renamed from: b */
    public Texture atlasTexture;

    /* JADX INFO: renamed from: c */
    GraphicsEngine atlasGraphics;

    /* JADX INFO: renamed from: e */
    int slotPixelWidth;

    /* JADX INFO: renamed from: f */
    int slotPixelHeight;

    /* JADX INFO: renamed from: g */
    int slotStridePixelsX;

    /* JADX INFO: renamed from: h */
    int slotStridePixelsY;

    /* JADX INFO: renamed from: i */
    float scale;

    /* JADX INFO: renamed from: j */
    boolean isAlphaAtlas;

    /* JADX INFO: renamed from: k */
    TileAtlasCache alphaAtlasCache;

    /* JADX INFO: renamed from: l */
    static final Rect tmpRectA = new Rect();

    /* JADX INFO: renamed from: m */
    static final Rect tmpRectB = new Rect();

    /* JADX INFO: renamed from: n */
    static final Rect tmpRectC = new Rect();

    /* JADX INFO: renamed from: a */
    int allocatedSlotCount = 0;

    /* JADX INFO: renamed from: d */
    KoolPaint paddingBlitPaint = new KoolPaint();

    /* JADX INFO: renamed from: o */
    Rect cachedSlotRect = new Rect();

    /* JADX INFO: renamed from: p */
    int cachedSlotIndex = -1;

    public TileAtlasCache(float f, boolean z) {
        this(f, z, null);
    }

    public TileAtlasCache(float f, boolean z, GraphicsEngine graphicsEngine) {
        this.scale = 1.0f;
        this.scale = f;
        this.isAlphaAtlas = z;
        this.graphicsBackend = graphicsEngine;
        this.slotPixelWidth = (int) (20.0f * f);
        this.slotPixelHeight = (int) (20.0f * f);
        this.slotStridePixelsX = this.slotPixelWidth + 2;
        this.slotStridePixelsY = this.slotPixelHeight + 2;
        if (!z) {
            this.alphaAtlasCache = new TileAtlasCache(f, true, graphicsEngine);
        }
    }

    public void bindGraphicsBackend(GraphicsEngine graphicsEngine) {
        if (graphicsEngine == null) {
            throw new IllegalArgumentException("graphicsEngine cannot be null");
        }
        if (this.graphicsBackend == graphicsEngine) {
            return;
        }
        release();
        this.graphicsBackend = graphicsEngine;
        if (this.alphaAtlasCache != null) {
            this.alphaAtlasCache.bindGraphicsBackend(graphicsEngine);
        }
    }

    /* JADX INFO: renamed from: a */
    public void initializeAtlasTexture() {
        GraphicsEngine graphicsEngine = this.graphicsBackend;
        if (graphicsEngine == null) {
            throw new IllegalStateException("TileAtlasCache graphics backend is not bound");
        }
        this.atlasTexture = graphicsEngine.b(20 * this.slotStridePixelsX, 20 * this.slotStridePixelsY, this.isAlphaAtlas);
        this.atlasGraphics = graphicsEngine.b(this.atlasTexture, RenderTargetMode.IMMEDIATE);
        if (this.isAlphaAtlas) {
            this.atlasTexture.m = true;
        }
        if (this.alphaAtlasCache != null) {
            this.alphaAtlasCache.initializeAtlasTexture();
        }
    }

    public void release() {
        if (this.atlasGraphics != null) {
            this.atlasGraphics.q();
            this.atlasGraphics = null;
        }
        if (this.atlasTexture != null) {
            this.atlasTexture.o();
            this.atlasTexture = null;
        }
        this.allocatedSlotCount = 0;
        this.cachedSlotIndex = -1;
        if (this.alphaAtlasCache != null) {
            this.alphaAtlasCache.release();
        }
    }

    /* JADX INFO: renamed from: b */
    public void clearAtlas() {
        this.allocatedSlotCount = 0;
        if (this.isAlphaAtlas) {
            this.atlasGraphics.a(0, KoolCanvasBlendMode.Clear);
        } else {
            this.atlasGraphics.b(-16777216);
        }
        this.atlasTexture.r();
        if (this.alphaAtlasCache != null) {
            this.alphaAtlasCache.clearAtlas();
        }
    }

    /* JADX INFO: renamed from: a */
    public int allocateSlotForTile(Tileset tileset, int i) {
        int iAllocateSlotForTile;
        tileset.computeTileRect(i, tmpRectA);
        if (this.allocatedSlotCount >= 400) {
            return -1;
        }
        boolean z = true;
        if (!this.isAlphaAtlas) {
            z = !hasTransparencyInRect(tileset.tilesetBitmap, tmpRectA);
        }
        if (z) {
            int i2 = this.allocatedSlotCount;
            this.allocatedSlotCount++;
            blitWithPadding(i2, tileset.tilesetBitmap, tmpRectA);
            return i2;
        }
        if (this.alphaAtlasCache != null && (iAllocateSlotForTile = this.alphaAtlasCache.allocateSlotForTile(tileset, i)) != -1) {
            return iAllocateSlotForTile + 500;
        }
        return -1;
    }

    /* JADX INFO: renamed from: a */
    public static boolean hasTransparencyInRect(Texture texture, Rect rect) {
        int iM = rect.a;
        int iM2 = rect.c;
        int iL = rect.b;
        int iL2 = rect.d;
        if (iM < 0) {
            iM = 0;
        }
        if (iM2 < 0) {
            iM2 = 0;
        }
        if (iL < 0) {
            iL = 0;
        }
        if (iL2 < 0) {
            iL2 = 0;
        }
        if (iM > texture.m()) {
            iM = texture.m();
        }
        if (iM2 > texture.m()) {
            iM2 = texture.m();
        }
        if (iL > texture.l()) {
            iL = texture.l();
        }
        if (iL2 > texture.l()) {
            iL2 = texture.l();
        }
        if (!texture.k()) {
            GameEngine.log("hasImageAlpha: Cannot get pixel data for: " + texture.a());
            return true;
        }
        texture.x();
        for (int i = iL; i < iL2; i++) {
            for (int i2 = iM; i2 < iM2; i2++) {
                if (KoolArgbColor.a(texture.a(i2, i)) != 255) {
                    return true;
                }
            }
        }
        texture.y();
        return false;
    }

    /* JADX INFO: renamed from: c */
    public void finalizeAtlas() {
        this.atlasGraphics.p();
        if (this.alphaAtlasCache != null) {
            this.alphaAtlasCache.finalizeAtlas();
        }
    }

    /* JADX INFO: renamed from: a */
    public void blitWithPadding(int i, Texture texture, Rect rect) {
        Rect rect2 = new Rect();
        computeAtlasSlotRect(i, rect2);
        boolean z = false;
        if (this.scale < 1.0f) {
            z = true;
        }
        this.paddingBlitPaint.a(z);
        this.paddingBlitPaint.d(z);
        this.paddingBlitPaint.b(z);
        blitPaddingEdges(this.atlasGraphics, texture, rect, rect2, this.paddingBlitPaint);
    }

    /* JADX INFO: renamed from: a */
    public static void blitPaddingEdges(GraphicsEngine graphicsEngine, Texture texture, Rect rect, Rect rect2, KoolPaint paint) {
        for (int i = 0; i <= 3; i++) {
            getPixelCopyRectForCorner(rect, tmpRectB, i, 0);
            getPixelCopyRectForCorner(rect2, tmpRectC, i, 1);
            graphicsEngine.a(texture, tmpRectB, tmpRectC, paint);
        }
        for (int i2 = 0; i2 <= 3; i2++) {
            getPaddingCornerRect(rect, tmpRectB, i2, 1, -1);
            getPaddingCornerRect(rect2, tmpRectC, i2, 1, 0);
            graphicsEngine.a(texture, tmpRectB, tmpRectC, paint);
        }
        graphicsEngine.a(texture, rect, rect2, paint);
    }

    /* JADX INFO: renamed from: a */
    public static Rect getPaddingCornerRect(Rect rect, Rect rect2, int i, int i2, int i3) {
        if (i == 0) {
            rect2.a = rect.a - 0;
            rect2.c = rect.c + 0;
            rect2.b = (rect.b - i2) - i3;
            rect2.d = rect.b - i3;
        } else if (i == 1) {
            rect2.a = rect.c + i3;
            rect2.c = rect.c + i2 + i3;
            rect2.b = rect.b - 0;
            rect2.d = rect.d + 0;
        } else if (i == 2) {
            rect2.a = rect.a - 0;
            rect2.c = rect.c + 0;
            rect2.b = rect.d + i3;
            rect2.d = rect.d + i2 + i3;
        } else {
            rect2.a = rect.a - i3;
            rect2.c = (rect.a - i2) - i3;
            rect2.b = rect.b - 0;
            rect2.d = rect.d + 0;
        }
        return rect2;
    }

    /* JADX INFO: renamed from: a */
    public static Rect getPixelCopyRectForCorner(Rect rect, Rect rect2, int i, int i2) {
        if (i == 0) {
            rect2.a = (rect.c - 1) + i2;
            rect2.b = rect.b - i2;
        } else if (i == 1) {
            rect2.a = (rect.c - 1) + i2;
            rect2.b = (rect.d - 1) + i2;
        } else if (i == 2) {
            rect2.a = rect.a - i2;
            rect2.b = (rect.d - 1) + i2;
        } else {
            rect2.a = rect.a - i2;
            rect2.b = rect.b - i2;
        }
        rect2.c = rect2.a + 1;
        rect2.d = rect2.b + 1;
        return rect2;
    }

    /* JADX INFO: renamed from: a */
    public final Texture getAtlasTextureForIndex(int i) {
        if (i >= 500) {
            return this.alphaAtlasCache.getAtlasTextureForIndex(i - 500);
        }
        return this.atlasTexture;
    }

    /* JADX INFO: renamed from: b */
    public final Rect getRectForIndex(int i) {
        if (i >= 500) {
            return this.alphaAtlasCache.getRectForIndex(i - 500);
        }
        if (this.cachedSlotIndex == i) {
            return this.cachedSlotRect;
        }
        this.cachedSlotIndex = i;
        computeAtlasSlotRect(i, this.cachedSlotRect);
        return this.cachedSlotRect;
    }

    /* JADX INFO: renamed from: a */
    public void computeAtlasSlotRect(int i, Rect rect) {
        int i2 = ((i % 20) * this.slotStridePixelsX) + 1;
        int i3 = (((int) (i * 0.05f)) * this.slotStridePixelsY) + 1;
        rect.a = i2;
        rect.b = i3;
        rect.c = i2 + this.slotPixelWidth;
        rect.d = i3 + this.slotPixelHeight;
    }
}
