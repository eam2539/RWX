package com.corrodinggames.rts.gameFramework.ui.widgets;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/h.class */
public class UIStyle {
    Texture p;
    public UIStyle r;
    public int u;
    public UIStyle v;
    public static final UIStyle j = new UIStyle();
    public static final UIStyle k = new UIStyle();
    public static final UIStyle l = new UIStyle();
    public static final UIStyle m = new UIStyle();
    public static final UIStyle n = new UIStyle();
    static Rect w = new Rect();
    static Rect x = new Rect();
    static Rect y = new Rect();
    Paint o = new GamePaint();
    Paint q = new GamePaint();
    public int s = 3;
    public int t = 3;

    public void a(Texture texture) {
        this.p = texture;
    }

    public void a(UIStyle uIStyle) {
        this.p = uIStyle.p;
        if (uIStyle.o != null) {
            this.o = new Paint(uIStyle.o);
        } else {
            this.o = null;
        }
        if (uIStyle.q != null) {
            this.q = new Paint(uIStyle.q);
        } else {
            this.q = null;
        }
    }

    public static void b() {
        UIStyle uIStyle = j;
        uIStyle.o.b(Color.a(140, 100, 100, 100));
        uIStyle.q.b(-16777216);
        uIStyle.q.a(Paint.Style.STROKE);
        UIStyle uIStyle2 = k;
        uIStyle2.o.b(Color.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 100, 100, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_BUTTON_3));
        uIStyle2.q.b(-16777216);
        uIStyle2.q.a(Paint.Style.STROKE);
        UIStyle uIStyle3 = l;
        uIStyle3.o = null;
        uIStyle3.q = null;
        UIStyle uIStyle4 = m;
        uIStyle4.o = null;
        uIStyle4.q.b(-65536);
        uIStyle4.q.c(127);
        uIStyle4.q.a(Paint.Style.STROKE);
        UIStyle uIStyle5 = n;
        uIStyle5.o.c(255);
        uIStyle5.p = GameEngine.getInstance().gameUI.bl;
        uIStyle5.q.b(-7829368);
        uIStyle5.q.c(255);
        uIStyle5.q.a(Paint.Style.STROKE);
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
        if (this.u > 0) {
            y.a(rect);
            rect = y;
            Utility.grow(rect, this.u);
        }
        if (this.r != null) {
            w.a(rect);
            w.a(this.s, this.t);
            this.r.a(graphicsEngine, w);
        }
        if (uIState == UIState.hovered && this.v != null) {
            this.v.a(graphicsEngine, rect);
        } else {
            a(graphicsEngine, rect);
        }
    }

    public void a(GraphicsEngine graphicsEngine, Rect rect) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.p != null) {
            gameEngine.graphicsEngine2.a(this.p, rect, this.o, 0, 0, 0, 0);
        } else if (this.o != null) {
            graphicsEngine.b(rect, this.o);
        }
        if (this.q != null) {
            graphicsEngine.b(rect, this.q);
        }
    }
}
