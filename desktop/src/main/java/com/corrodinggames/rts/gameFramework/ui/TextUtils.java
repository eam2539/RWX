package com.corrodinggames.rts.gameFramework.ui;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/d.class */
public class TextUtils {

    /* JADX INFO: renamed from: a */
    static Rect tempRect = new Rect();

    /* JADX INFO: renamed from: b */
    static ArrayList lines = new ArrayList();

    /* JADX INFO: renamed from: c */
    static final RectF backgroundRect = new RectF();

    /* JADX INFO: renamed from: d */
    static final RectF textBounds = new RectF();

    /* JADX INFO: renamed from: a */
    public static int getLineHeight(Paint paint) {
        return GameEngine.getInstance().renderGraphicsEngine.a("abcABC123!|", paint) + 4;
    }

    /* JADX INFO: renamed from: b */
    public static int getCharWidth(Paint paint) {
        int iA = GameEngine.getInstance().renderGraphicsEngine.a("abcABC123!|", paint);
        if (GameEngine.isGDXVersion) {
            return iA + 2;
        }
        return iA;
    }

    /* JADX INFO: renamed from: a */
    public static ArrayList wrapText(String str, Rect rect, Paint paint, Paint paint2, boolean z) {
        int iLastIndexOf;
        lines.clear();
        String str2 = VariableScope.nullOrMissingString;
        int size = 0;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= str.length()) {
                break;
            }
            int iA = paint2.a((CharSequence) str, i2, str.length(), true, rect.b() - 5, (float[]) null);
            if (iA == 0) {
                break;
            }
            int iIndexOf = str.indexOf("\n", i2 + 1);
            if (iIndexOf != -1 && iIndexOf < i2 + iA) {
                iA = iIndexOf - i2;
            } else if (i2 + iA < str.length() && (iLastIndexOf = str.substring(i2, i2 + iA).lastIndexOf(" ")) != -1 && iLastIndexOf != 0) {
                iA = iLastIndexOf;
            }
            String strReplaceAll = str.substring(i2, i2 + iA).replaceAll("(\\n)", VariableScope.nullOrMissingString);
            if (strReplaceAll.length() > str2.length()) {
                str2 = strReplaceAll;
                size = lines.size();
            }
            lines.add(strReplaceAll);
            i = i2 + iA;
        }
        rect.d = rect.b + (lines.size() * getLineHeight(paint2));
        if (z) {
            float fD = rect.d();
            Paint paint3 = paint2;
            if (size == 0) {
                paint3 = paint;
            }
            float fB = GameEngine.getInstance().renderGraphicsEngine.b(str2, paint3);
            if (fB < rect.b()) {
                rect.a = (int) (fD - (fB / 2.0f));
                rect.c = (int) (fD + (fB / 2.0f));
            }
        }
        return lines;
    }

    /* JADX INFO: renamed from: a */
    public static void drawTextWithBackground(String str, float f, float f2, Paint paint, Paint paint2, float f3, float f4, float f5, float f6) {
        GraphicsEngine graphicsEngine = GameEngine.getInstance().renderGraphicsEngine;
        float fB = graphicsEngine.b(str, paint);
        textBounds.a(f, f2, f + fB, f2 + graphicsEngine.a(str, paint));
        backgroundRect.a(textBounds);
        if (paint.j() == Paint.Align.CENTER) {
            backgroundRect.a(-(fB / 2.0f), 0.0f);
        }
        backgroundRect.a -= f3;
        backgroundRect.b -= f4;
        backgroundRect.c += f5;
        backgroundRect.d += f6;
        graphicsEngine.a(backgroundRect, paint2);
        graphicsEngine.a(str, textBounds.a, textBounds.d, paint);
    }

    /* JADX INFO: renamed from: a */
    public static float getScale(Texture texture, float f, float f2) {
        return getScaleWithBounds(texture, f, f2, f, f2);
    }

    /* JADX INFO: renamed from: a */
    public static float getScaleWithBounds(Texture texture, float f, float f2, float f3, float f4) {
        float f5 = texture.p;
        float f6 = texture.q;
        float f7 = 1.0f;
        if (f5 * 1.0f < f) {
            float f8 = f / f5;
            if (f8 > 1.0f) {
                f7 = f8;
            }
        }
        if (f6 * f7 < f2) {
            float f9 = f2 / f6;
            if (f9 > f7) {
                f7 = f9;
            }
        }
        if (f5 * f7 > f3) {
            float f10 = f3 / f5;
            if (f10 < f7) {
                f7 = f10;
            }
        }
        if (f6 * f7 > f4) {
            float f11 = f4 / f6;
            if (f11 < f7) {
                f7 = f11;
            }
        }
        return f7;
    }
}
