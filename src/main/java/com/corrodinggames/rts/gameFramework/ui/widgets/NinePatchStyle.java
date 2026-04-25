package com.corrodinggames.rts.gameFramework.ui.widgets;

import android.graphics.Paint;
import android.graphics.Rect;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GraphicsUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/e.class */
public class NinePatchStyle extends UIStyle {
    int a;
    int b;
    float c;
    float d;
    public boolean e = true;
    public boolean f = false;
    public float g = 1.0f;
    static Rect h = new Rect();
    static Rect i = new Rect();

    public NinePatchStyle() {
    }

    public NinePatchStyle(Texture texture, int i2, int i3) {
        a(texture);
        a(texture, i2, i3);
    }

    public void a(Texture texture, int i2, int i3) {
        this.a = i2;
        this.b = i3;
        this.c = i2 / texture.p;
        this.d = i3 / texture.q;
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
        this.a = ninePatchStyle.a;
        this.b = ninePatchStyle.b;
        this.c = ninePatchStyle.c;
        this.d = ninePatchStyle.d;
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
        a(graphicsEngine, this.p, this.o, rect);
    }

    private boolean c() {
        return true;
    }

    private void a(GraphicsEngine graphicsEngine, Texture texture, Paint paint, Rect rect) {
        int i2 = rect.a;
        int i3 = rect.b;
        int iB = rect.b();
        int iC = rect.c();
        int i4 = this.a;
        int i5 = this.b;
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
                f = i6 / i4;
            }
            if (i5 * f > i7) {
                f = i7 / i5;
            }
            i4 = (int) (this.a * f);
            i5 = (int) (this.b * f);
        }
        int i8 = iB - (2 * i4);
        int i9 = iC - (2 * i5);
        float f2 = this.c;
        float f3 = this.d;
        if (c()) {
            a(graphicsEngine, texture, paint, i2 + i4, i3 + 0, i8, i5, f2, 0.0f, 1.0f - f2, f3, this.f);
            a(graphicsEngine, texture, paint, i2 + 0, i3 + i5, i4, i9, 0.0f, f3, f2, 1.0f - f3, this.f);
            a(graphicsEngine, texture, paint, i2 + i4, (i3 + iC) - i5, i8, i5, f2, 1.0f - f3, 1.0f - f2, 1.0f, this.f);
            a(graphicsEngine, texture, paint, (i2 + iB) - i4, i3 + i5, i4, i9, 1.0f - f2, f3, 1.0f, 1.0f - f3, this.f);
            a(graphicsEngine, texture, paint, i2 + 0, i3 + 0, i4, i5, 0.0f, 0.0f, this.c, this.d);
            a(graphicsEngine, texture, paint, (i2 + iB) - i4, i3 + 0, i4, i5, 1.0f - this.c, 0.0f, 1.0f, this.d);
            a(graphicsEngine, texture, paint, i2 + 0, (i3 + iC) - i5, i4, i5, 0.0f, 1.0f - this.d, this.c, 1.0f);
            a(graphicsEngine, texture, paint, (i2 + iB) - i4, (i3 + iC) - i5, i4, i5, 1.0f - this.c, 1.0f - this.d, 1.0f, 1.0f);
        }
        a(graphicsEngine, texture, paint, i2 + i4, i3 + i5, i8, i9, f2, f3, 1.0f - f2, 1.0f - f3, this.f);
    }

    public void a(GraphicsEngine graphicsEngine, Texture texture, Paint paint, int i2, int i3, int i4, int i5, float f, float f2, float f3, float f4) {
        a(graphicsEngine, texture, paint, i2, i3, i4, i5, f, f2, f3, f4, false);
    }

    public void a(GraphicsEngine graphicsEngine, Texture texture, Paint paint, int i2, int i3, int i4, int i5, float f, float f2, float f3, float f4, boolean z) {
        Rect rect = h;
        Rect rect2 = i;
        rect.a((int) (f * texture.p), (int) (f2 * texture.q), (int) (f3 * texture.p), (int) (f4 * texture.q));
        rect2.a(i2, i3, i2 + i4, i3 + i5);
        if (!z) {
            graphicsEngine.a(texture, rect, rect2, paint);
        } else {
            GraphicsUtils.a(graphicsEngine, texture, rect, rect2, paint, 0, 0, 0, 0, this.g);
        }
    }
}
