package com.corrodinggames.rts.gameFramework.ui.widgets;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.RectF;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/l.class */
public abstract class UIElement {
    float g;
    float h;
    float k;
    float l;
    float m;
    float n;
    float o;
    float p;
    float q;
    float r;
    boolean u;
    UIElement v;
    float y;
    float z;
    UIEventHandler B;
    static final PointF e = new PointF();
    static final RectF f = new RectF();
    static final PointF A = new PointF();
    float i = 50.0f;
    float j = 50.0f;
    boolean s = false;
    boolean t = false;
    FastArrayList<UIElement> w = new FastArrayList();
    LayoutDirection x = LayoutDirection.vertical;

    public String a() {
        return getClass().getSimpleName();
    }

    public GraphicsEngine d() {
        return GameEngine.getInstance().renderGraphicsEngine;
    }

    public RectF a(RectF rectF, float f2, float f3) {
        rectF.a = 0.0f + f2;
        rectF.b = 0.0f + f3;
        rectF.c = 0.0f + this.i + f2;
        rectF.d = 0.0f + this.j + f3;
        return rectF;
    }

    public RectF a(RectF rectF) {
        A.x = this.g;
        A.y = this.h;
        if (this.v != null) {
            this.v.a(A);
        }
        rectF.a = 0.0f + A.x;
        rectF.b = 0.0f + A.y;
        rectF.c = 0.0f + this.i + A.x;
        rectF.d = 0.0f + this.j + A.y;
        return rectF;
    }

    public void b() {
        Iterator it = this.w.iterator();
        while (it.hasNext()) {
            ((UIElement) it.next()).b();
        }
        this.y = 0.0f;
        this.z = 0.0f;
        if (this.x != LayoutDirection.none) {
            if (this.x == LayoutDirection.vertical) {
                float fG = 0.0f;
                float fH = 0.0f;
                for (UIElement uIElement : this.w) {
                    if (uIElement.i > fG) {
                        fG = uIElement.g();
                    }
                    fH += uIElement.h();
                }
                this.y = fH;
                this.z = fG;
                b(this.z * 0.5f, this.y * 0.5f, this.w);
            } else if (this.x == LayoutDirection.horizontal) {
                float fH2 = 0.0f;
                float fG2 = 0.0f;
                for (UIElement uIElement2 : this.w) {
                    if (uIElement2.j > fH2) {
                        fH2 = uIElement2.h();
                    }
                    fG2 += uIElement2.g();
                }
                this.y = fH2;
                this.z = fG2;
                a(this.z * 0.5f, this.y * 0.5f, this.w);
            } else {
                throw new RuntimeException("Unknown layout style:" + this.x);
            }
        }
        this.s = false;
    }

    public static void a(float f2, float f3, FastArrayList fastArrayList) {
        float fG = 0.0f;
        Iterator it = fastArrayList.iterator();
        while (it.hasNext()) {
            fG += ((UIElement) it.next()).g();
        }
        float f4 = f2 - (fG * 0.5f);
        Iterator it2 = fastArrayList.iterator();
        while (it2.hasNext()) {
            UIElement uIElement = (UIElement) it2.next();
            float f5 = f4 + uIElement.q;
            uIElement.g = f5;
            f4 = f5 + uIElement.i + uIElement.r;
            uIElement.d(f3);
        }
    }

    public static void b(float f2, float f3, FastArrayList fastArrayList) {
        float fH = 0.0f;
        Iterator it = fastArrayList.iterator();
        while (it.hasNext()) {
            fH += ((UIElement) it.next()).h();
        }
        float f4 = f3 - (fH * 0.5f);
        Iterator it2 = fastArrayList.iterator();
        while (it2.hasNext()) {
            UIElement uIElement = (UIElement) it2.next();
            float f5 = f4 + uIElement.o;
            uIElement.h = f5;
            f4 = f5 + uIElement.j + uIElement.p;
            uIElement.c(f2);
        }
    }

    public void a(PointF pointF) {
        if (this.v != null) {
            this.v.a(pointF);
        }
        pointF.x += this.g;
        pointF.y += this.h;
    }

    public void a(UIElement uIElement) {
        uIElement.b(this);
    }

    public void b(UIElement uIElement) {
        a(uIElement, false);
    }

    public void a(UIElement uIElement, boolean z) {
        if (this.v == uIElement) {
            return;
        }
        if (this.v != null) {
            this.v.w.remove(this);
        }
        this.v = uIElement;
        if (uIElement != null) {
            if (!z) {
                uIElement.w.add(this);
            } else {
                uIElement.w.add(0, this);
            }
        }
        e();
    }

    public void e() {
        this.s = true;
        if (this.v != null) {
            this.v.e();
        }
    }

    public void b(float f2) {
        if (this.w.size() > 0) {
            Iterator it = this.w.iterator();
            while (it.hasNext()) {
                ((UIElement) it.next()).b(f2);
            }
        }
    }

    public void f() {
        A.x = this.g;
        A.y = this.h;
        if (this.v != null) {
            this.v.a(A);
        }
        a(A.x, A.y);
        if (this.w.size() > 0) {
            Iterator it = this.w.iterator();
            while (it.hasNext()) {
                ((UIElement) it.next()).f();
            }
        }
    }

    public void a(float f2, float f3) {
        if (this.t) {
            UIStyle.m.a(d(), a(new RectF(), f2, f3));
        }
    }

    public void a(UIEventHandler uIEventHandler) {
        this.B = uIEventHandler;
    }

    public boolean a(UIEvent uIEvent) {
        if (uIEvent.a() && c(uIEvent)) {
            GameEngine.log("UI click " + a());
            if (this.B != null) {
                return this.B.a(uIEvent);
            }
            return false;
        }
        if (uIEvent.b()) {
            if (c(uIEvent)) {
                this.u = true;
                return false;
            }
            this.u = false;
            return false;
        }
        return false;
    }

    public boolean b(UIEvent uIEvent) {
        if (this.w.size() > 0) {
            Iterator it = this.w.iterator();
            while (it.hasNext()) {
                if (((UIElement) it.next()).b(uIEvent)) {
                    return true;
                }
            }
        }
        if (a(uIEvent)) {
            return true;
        }
        return false;
    }

    public boolean c(UIEvent uIEvent) {
        a(f);
        return f.b(uIEvent.a, uIEvent.b);
    }

    public void c(float f2) {
        this.g = f2 - (this.i * 0.5f);
    }

    public void d(float f2) {
        this.h = f2 - (this.j * 0.5f);
    }

    public void e(float f2) {
        this.o = f2;
        this.p = f2;
        this.q = f2;
        this.r = f2;
    }

    public void f(float f2) {
        this.k = f2;
        this.l = f2;
        this.m = f2;
        this.n = f2;
    }

    public float g() {
        return this.q + this.i + this.r;
    }

    public float h() {
        return this.o + this.j + this.p;
    }

    public void i() {
        b((UIElement) null);
    }
}
