package com.corrodinggames.rts.gameFramework.ui.widgets;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/e.class */
public class NinePatchStyle extends UIStyle {
    /* JADX INFO: renamed from: a */
    int patchWidth;

    /* JADX INFO: renamed from: b */
    int patchHeight;

    /* JADX INFO: renamed from: c */
    float normalizedWidth;

    /* JADX INFO: renamed from: d */
    float normalizedHeight;
    public boolean e = true;
    public boolean f = false;
    public float g = 1.0f;
    private static final int TILE_IMAGE_LIMIT = 2000;
    static Rect h = new Rect();
    static Rect i = new Rect();

    public NinePatchStyle() {
    }

    public NinePatchStyle(Texture texture, int i2, int i3) {
        a(texture);
        a(texture, i2, i3);
    }

    public void a(Texture texture, int i2, int i3) {
        this.patchWidth = i2;
        this.patchHeight = i3;
        this.normalizedWidth = i2 / (float) texture.p;
        this.normalizedHeight = i3 / (float) texture.q;
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public NinePatchStyle clone() {
        NinePatchStyle ninePatchStyle = new NinePatchStyle();
        ninePatchStyle.a(this);
        return ninePatchStyle;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIStyle
    public void a(UIStyle uIStyle) {
        NinePatchStyle ninePatchStyle = (NinePatchStyle) uIStyle;
        this.patchWidth = ninePatchStyle.patchWidth;
        this.patchHeight = ninePatchStyle.patchHeight;
        this.normalizedWidth = ninePatchStyle.normalizedWidth;
        this.normalizedHeight = ninePatchStyle.normalizedHeight;
        this.e = ninePatchStyle.e;
        super.a(ninePatchStyle);
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIStyle
    public void a(Texture texture) {
        super.a(texture);
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIStyle
    public void a(GraphicsEngine graphicsEngine, Rect rect) {
        b(graphicsEngine, rect);
        if (this.q != null) {
        }
    }

    public void b(GraphicsEngine graphicsEngine, Rect rect) {
        a(graphicsEngine, this.backgroundTexture, this.o, rect);
    }

    private boolean c() {
        return true;
    }

    private void a(GraphicsEngine graphicsEngine, Texture texture, KoolPaint paint, Rect rect) {
        int i2 = rect.a;
        int i3 = rect.b;
        int iB = rect.b();
        int iC = rect.c();
        int i4 = this.patchWidth;
        int i5 = this.patchHeight;
        if (!this.e) {
            if (i4 > iB / 2) {
                i4 = iB / 2;
            }
            if (i5 > iC / 2) {
                i5 = iC / 2;
            }
        } else {
            float f = 1.0f;
            int i6 = iB / 2;
            int i7 = iC / 2;
            if (i4 * 1.0f > i6) {
                f = i6 / (float) i4;
            }
            if (i5 * f > i7) {
                f = i7 / (float) i5;
            }
            i4 = (int) (this.patchWidth * f);
            i5 = (int) (this.patchHeight * f);
        }
        int i8 = iB - (2 * i4);
        int i9 = iC - (2 * i5);
        float f2 = this.normalizedWidth;
        float f3 = this.normalizedHeight;
        if (c()) {
            a(graphicsEngine, texture, paint, i2 + i4, i3 + 0, i8, i5, f2, 0.0f, 1.0f - f2, f3, this.f);
            a(graphicsEngine, texture, paint, i2 + 0, i3 + i5, i4, i9, 0.0f, f3, f2, 1.0f - f3, this.f);
            a(graphicsEngine, texture, paint, i2 + i4, (i3 + iC) - i5, i8, i5, f2, 1.0f - f3, 1.0f - f2, 1.0f, this.f);
            a(graphicsEngine, texture, paint, (i2 + iB) - i4, i3 + i5, i4, i9, 1.0f - f2, f3, 1.0f, 1.0f - f3, this.f);
            a(graphicsEngine, texture, paint, i2 + 0, i3 + 0, i4, i5, 0.0f, 0.0f, this.normalizedWidth, this.normalizedHeight);
            a(graphicsEngine, texture, paint, (i2 + iB) - i4, i3 + 0, i4, i5, 1.0f - this.normalizedWidth, 0.0f, 1.0f, this.normalizedHeight);
            a(graphicsEngine, texture, paint, i2 + 0, (i3 + iC) - i5, i4, i5, 0.0f, 1.0f - this.normalizedHeight, this.normalizedWidth, 1.0f);
            a(graphicsEngine, texture, paint, (i2 + iB) - i4, (i3 + iC) - i5, i4, i5, 1.0f - this.normalizedWidth, 1.0f - this.normalizedHeight, 1.0f, 1.0f);
        }
        a(graphicsEngine, texture, paint, i2 + i4, i3 + i5, i8, i9, f2, f3, 1.0f - f2, 1.0f - f3, this.f);
    }

    public void a(GraphicsEngine graphicsEngine, Texture texture, KoolPaint paint, int i2, int i3, int i4, int i5, float f, float f2, float f3, float f4) {
        a(graphicsEngine, texture, paint, i2, i3, i4, i5, f, f2, f3, f4, false);
    }

    public void a(GraphicsEngine graphicsEngine, Texture texture, KoolPaint paint, int i2, int i3, int i4, int i5, float f, float f2, float f3, float f4, boolean z) {
        Rect rect = h;
        Rect rect2 = i;
        rect.a((int) (f * texture.p), (int) (f2 * texture.q), (int) (f3 * texture.p), (int) (f4 * texture.q));
        rect2.a(i2, i3, i2 + i4, i3 + i5);
        if (!z) {
            graphicsEngine.a(texture, rect, rect2, paint);
        } else {
            drawRepeatedSubRect(graphicsEngine, texture, new Rect(rect), new Rect(rect2), paint, this.g);
        }
    }

    private static void drawRepeatedSubRect(GraphicsEngine graphicsEngine, Texture texture, Rect source, Rect destination, KoolPaint paint, float scaleBias) {
        int sourceWidth = source.b();
        int sourceHeight = source.c();
        int tileLeft = destination.a;
        int tileTop = destination.b;
        int drawWidth = destination.c - tileLeft;
        int drawHeight = destination.d - tileTop;
        if (sourceWidth == 0 || sourceHeight == 0) {
            return;
        }
        int tileCountX = (int) ((drawWidth / (float) sourceWidth) + 0.5f);
        int tileCountY = (int) ((drawHeight / (float) sourceHeight) + 0.5f);
        if (tileCountX < 1) {
            tileCountX = 1;
        }
        if (tileCountY < 1) {
            tileCountY = 1;
        }
        if (tileCountX == 0 || tileCountY == 0) {
            return;
        }

        float tileScaleX = drawWidth / (float) (tileCountX * sourceWidth);
        float tileScaleY = drawHeight / (float) (tileCountY * sourceHeight);
        float adjustedScaleX = Utility.lerp(1.0f, tileScaleX, scaleBias);
        float adjustedScaleY = Utility.lerp(1.0f, tileScaleY, scaleBias);
        if (Math.abs(adjustedScaleX) < 0.0001f) {
            adjustedScaleX = 1.0f;
        }
        if (Math.abs(adjustedScaleY) < 0.0001f) {
            adjustedScaleY = 1.0f;
        }

        int tileWidth = (int) (sourceWidth * adjustedScaleX);
        int tileHeight = (int) (sourceHeight * adjustedScaleY);
        float sourceScaleX = 1.0f / adjustedScaleX;
        float sourceScaleY = 1.0f / adjustedScaleY;
        if (tileWidth <= 0 || tileHeight <= 0) {
            return;
        }

        int tileCount = 0;
        int repeatTop = tileTop;
        while (tileLeft < destination.c) {
            while (tileTop < destination.d) {
                tileCount++;
                if (tileCount > TILE_IMAGE_LIMIT) {
                    GameEngine.log("tileImage hit limit");
                    return;
                }
                int clippedTileWidth = destination.c - tileLeft;
                if (clippedTileWidth > tileWidth) {
                    clippedTileWidth = tileWidth;
                }
                int clippedTileHeight = destination.d - tileTop;
                if (clippedTileHeight > tileHeight) {
                    clippedTileHeight = tileHeight;
                }
                if (clippedTileHeight > 0 && clippedTileWidth > 0) {
                    h.a(0, 0, (int) (clippedTileWidth * sourceScaleX), (int) (clippedTileHeight * sourceScaleY));
                    h.a(source.a, source.b);
                    i.a(tileLeft, tileTop, tileLeft + clippedTileWidth, tileTop + clippedTileHeight);
                    int clipLeftDelta = i.a - destination.a;
                    if (clipLeftDelta < 0) {
                        h.a -= clipLeftDelta;
                        i.a -= clipLeftDelta;
                    }
                    int clipTopDelta = i.b - destination.b;
                    if (clipTopDelta < 0) {
                        h.b -= clipTopDelta;
                        i.b -= clipTopDelta;
                    }
                    graphicsEngine.a(texture, h, i, paint);
                    tileTop += tileHeight;
                }
            }
            tileLeft += tileWidth;
            tileTop = repeatTop;
        }
    }
}
