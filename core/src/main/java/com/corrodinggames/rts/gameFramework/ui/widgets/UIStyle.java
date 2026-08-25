package com.corrodinggames.rts.gameFramework.ui.widgets;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.geometry.Rect;
import io.github.rwx.geometry.RectF;
import io.github.rwx.render.canvas.KoolArgbColor;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/h.class */
public class UIStyle {
    /* JADX INFO: renamed from: p */
    Texture backgroundTexture;

    /* JADX INFO: renamed from: r */
    public UIStyle normalStyle;

    /* JADX INFO: renamed from: u */
    public int paddingSize;

    /* JADX INFO: renamed from: v */
    public UIStyle hoverStyle;
    public static final UIStyle j = new UIStyle();
    public static final UIStyle k = new UIStyle();
    public static final UIStyle l = new UIStyle();
    public static final UIStyle m = new UIStyle();
    public static final UIStyle n = new UIStyle();
    static Rect w = new Rect();
    static Rect x = new Rect();
    static Rect y = new Rect();
    KoolPaint o = new GamePaint();
    KoolPaint q = new GamePaint();
    public int s = 3;
    public int t = 3;

    public void a(Texture texture) {
        this.backgroundTexture = texture;
    }

    public void a(UIStyle uIStyle) {
        this.backgroundTexture = uIStyle.backgroundTexture;
        if (uIStyle.o != null) {
            this.o = new KoolPaint(uIStyle.o);
        } else {
            this.o = null;
        }
        if (uIStyle.q != null) {
            this.q = new KoolPaint(uIStyle.q);
        } else {
            this.q = null;
        }
    }

    public static void b() {
        UIStyle uIStyle = j;
        uIStyle.o.b(KoolArgbColor.a(140, 100, 100, 100));
        uIStyle.q.b(-16777216);
        uIStyle.q.a(KoolPaint.Style.STROKE);
        UIStyle uIStyle2 = k;
        uIStyle2.o.b(KoolArgbColor.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 100, 100, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_BUTTON_3));
        uIStyle2.q.b(-16777216);
        uIStyle2.q.a(KoolPaint.Style.STROKE);
        UIStyle uIStyle3 = l;
        uIStyle3.o = null;
        uIStyle3.q = null;
        UIStyle uIStyle4 = m;
        uIStyle4.o = null;
        uIStyle4.q.b(-65536);
        uIStyle4.q.c(127);
        uIStyle4.q.a(KoolPaint.Style.STROKE);
        UIStyle uIStyle5 = n;
        uIStyle5.o.c(255);
        uIStyle5.backgroundTexture = GameEngine.getInstance().gameUI.bl;
        uIStyle5.q.b(-7829368);
        uIStyle5.q.c(255);
        uIStyle5.q.a(KoolPaint.Style.STROKE);
    }

    public void a(GraphicsEngine graphicsEngine, RectF rectF) {
        x.a = (int) rectF.a;
        x.b = (int) rectF.b;
        x.c = (int) rectF.c;
        x.d = (int) rectF.d;
        a(graphicsEngine, x, UIState.normal);
    }

    public void c(GraphicsEngine graphicsEngine, Rect rect) {
        a(graphicsEngine, rect, UIState.normal);
    }

    public void a(GraphicsEngine graphicsEngine, Rect rect, UIState uIState) {
        if (this.paddingSize > 0) {
            y.a(rect);
            rect = y;
            Utility.grow(rect, this.paddingSize);
        }
        if (this.normalStyle != null) {
            w.a(rect);
            w.a(this.s, this.t);
            this.normalStyle.a(graphicsEngine, w);
        }
        if (uIState == UIState.hovered && this.hoverStyle != null) {
            this.hoverStyle.a(graphicsEngine, rect);
        } else {
            a(graphicsEngine, rect);
        }
    }

    public void a(GraphicsEngine graphicsEngine, Rect rect) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.backgroundTexture != null) {
            gameEngine.renderGraphicsEngine.a(this.backgroundTexture, rect, this.o, 0, 0, 0, 0);
        } else if (this.o != null) {
            graphicsEngine.b(rect, this.o);
        }
        if (this.q != null) {
            graphicsEngine.b(rect, this.q);
        }
    }
}
