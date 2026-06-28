package com.corrodinggames.rts.gameFramework.ui.widgets;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.ui.TextUtils;
import io.github.rwx.geometry.Rect;
import io.github.rwx.geometry.RectF;
import io.github.rwx.render.canvas.KoolPaint;

import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/j.class */
public class TextLabel extends UIElement {
    String a;
    KoolPaint b = new GamePaint();
    UIStyle c = UIStyle.l;
    ArrayList<String> d;

    public TextLabel() {
        this.b.a(KoolPaint.Align.CENTER);
        this.b.b(-16777216);
        a(18.0f);
    }

    public void a(float f) {
        GameEngine.getInstance().setScaledTextSize(this.b, f);
        e();
    }

    public void a(int i) {
        this.b.b(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIElement
    public String a() {
        return super.a() + " (text:" + this.a + ")";
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIElement
    public void a(final float float1, final float float2) {
        super.a(float1, float2);
        final GraphicsEngine d = this.d();
        final RectF a = this.a(new RectF(), float1, float2);
        this.c.a(d, a);
        if (this.a == null) {
            return;
        }
        if (this.d == null) {
            d.a(this.a, a.d(), a.d - this.l, this.b);
        } else {
            int n = 0;
            for (final String string : this.d) {
                final KoolPaint b = this.b;
                final int lineHeight = TextUtils.getLineHeight(b);
                d.a(string, a.d(), a.b + this.k + lineHeight + n * lineHeight, b);
                ++n;
            }
        }
    }

    public void a(String str) {
        this.a = str;
        e();
    }

    public Rect c() {
        RectF rectFA = a(new RectF(), 0.0f, 0.0f);
        Rect rect = new Rect();
        rect.d = (int) rectFA.d;
        rect.b = (int) rectFA.b;
        rect.a = (int) rectFA.a;
        rect.c = (int) rectFA.c;
        rect.c = 10000;
        return rect;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIElement
    public void b() {
        super.b();
        this.d();
        final Rect c = this.c();
        this.d = new ArrayList(TextUtils.wrapText(this.a, c, this.b, this.b, true));
        this.i = (float) c.b();
        this.j = (float) c.c();
        this.i += this.m + this.n;
        this.j += this.k + this.l;
    }
}
