package com.corrodinggames.rts.gameFramework.m;

import android.graphics.Paint;
import android.graphics.Typeface;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.android.graphics.C0009fo;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.fq */
/* JADX INFO: loaded from: classes.dex */
public final class UniquePaint extends Paint {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final UniquePaint f768a;
    public C0009fo c;
    public boolean b = false;
    public boolean d = false;

    static {
        UniquePaint uniquePaint = new UniquePaint();
        f768a = uniquePaint;
        uniquePaint.setColor(-1);
        f768a.d = true;
    }

    public final void a(float f) {
        super.setTextSize(f);
    }

    @Override // android.graphics.Paint
    public final void setTextSize(float f) {
        if (this.d) {
            GameEngine.logColored("UniquePaint changed when locked down:");
            GameEngine.logColored("from:" + getTextSize() + " to: " + f);
        }
        super.setTextSize(f);
    }

    @Override // android.graphics.Paint
    public final Typeface setTypeface(Typeface typeface) {
        if (this.d) {
            GameEngine.logColored("UniquePaint changed when locked down:");
        }
        return super.setTypeface(typeface);
    }

    public static void a(Paint paint) {
        ((UniquePaint) paint).d = true;
    }

    @Override // android.graphics.Paint
    public final void setAntiAlias(boolean z) {
        this.b = z;
        super.setAntiAlias(z);
    }
}
