package com.corrodinggames.rts.gameFramework.ui.widgets;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.ui.TextUtils;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.a.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/a/j.class */
public class TextLabel extends UIElement {

    /* JADX INFO: renamed from: a */
    String text;
    Paint b = new GamePaint();
    UIStyle c = UIStyle.l;
    /* JADX INFO: renamed from: d */
    ArrayList<String> lines;

    public TextLabel() {
        this.b.a(Paint.Align.CENTER);
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
        return super.a() + " (text:" + this.text + ")";
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.widgets.UIElement
    public void a(final float float1, final float float2) {
        super.a(float1, float2);
        final GraphicsEngine d = this.d();
        final RectF a = this.a(new RectF(), float1, float2);
        this.c.a(d, a);
        if (this.text == null) {
            return;
        }
        if (this.lines == null) {
            d.a(this.text, a.d(), a.d - this.l, this.b);
        }
        else {
            int n = 0;
            for (final String string : this.lines) {
                final Paint b = this.b;
                final int lineHeight = TextUtils.getLineHeight(b);
                d.a(string, a.d(), a.b + this.k + lineHeight + n * lineHeight, b);
                ++n;
            }
        }
    }

    public void a(String str) {
        this.text = str;
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
        this.lines = new ArrayList(TextUtils.wrapText(this.text, c, this.b, this.b, true));
        this.i = (float)c.b();
        this.j = (float)c.c();
        this.i += this.m + this.n;
        this.j += this.k + this.l;
    }
}
