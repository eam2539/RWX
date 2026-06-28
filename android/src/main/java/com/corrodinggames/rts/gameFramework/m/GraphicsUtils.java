package com.corrodinggames.rts.gameFramework.m;

import android.graphics.*;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.LogicNumberFuntion;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.fk */
/* JADX INFO: loaded from: classes.dex */
public final class GraphicsUtils {
    static float b;
    static float c;
    static float d;
    static TextureRegion[] f;
    static Paint g;

    /* JADX INFO: renamed from: a */
    static int f764a = -1;
    public static final Rect e = new Rect();
    static final Rect h = new Rect();
    static final Rect i = new Rect();
    static final RectF j = new RectF();

    public static final Bitmap a(UnitTexture unitTexture) {
        return unitTexture.b();
    }

    public static void a(String str, float f2, float f3, Paint paint) {
    }

    public static void a(GraphicsContext graphicsContext, float f2, float f3, float f4, Paint paint) {
        graphicsContext.a(f2, f3, f4, paint);
    }

    public static void b(GraphicsContext graphicsContext, float f2, float f3, float f4, Paint paint) {
        int i2;
        int i3;
        int i4;
        TextureRegion textureRegion;
        int i5 = 2;
        if (g == null) {
            Paint paint2 = new Paint();
            g = paint2;
            paint2.setAntiAlias(true);
            g.setDither(true);
        }
        int color = paint.getColor();
        if (GameEngine.isAndroidPlatform()) {
            g.setColorFilter(new LightingColorFilter(color, 0));
        }
        g.setColor(color);
        float f5 = 1.0f * f4;
        float strokeWidth = paint.getStrokeWidth();
        boolean z = paint.getStyle() == Paint.Style.FILL;
        int i6 = (int) strokeWidth;
        if (((int) f5) > 20) {
            i2 = 1;
            i3 = 60;
        } else {
            i2 = 0;
            i3 = 30;
        }
        if (i6 >= 2) {
            i4 = 1;
        } else {
            i4 = 0;
            i5 = 1;
        }
        int i7 = (i4 * 2) + i2 + (z ? 0 : 6);
        if (f == null) {
            f = new TextureRegion[13];
        }
        if (f[i7] != null) {
            if (f[i7].b != i3) {
                GameEngine.log("Mismatch on index: " + i7 + " size:" + i3);
            }
            textureRegion = f[i7];
        } else {
            TextureRegion textureRegion2 = new TextureRegion();
            Paint paint3 = new Paint();
            paint3.setColor(-1);
            paint3.setStyle(z ? Paint.Style.FILL : Paint.Style.STROKE);
            paint3.setStrokeWidth(i5);
            UnitTexture unitTextureB = graphicsContext.b((i3 * 2) + 4, (i3 * 2) + 4, true);
            GraphicsContext graphicsContextB = graphicsContext.b(unitTextureB);
            graphicsContextB.b(i3 + 2, i3 + 2, i3, paint3);
            graphicsContextB.n();
            unitTextureB.j();
            textureRegion2.d = unitTextureB;
            textureRegion2.b = i3;
            textureRegion2.f765a = i5;
            textureRegion2.c = z;
            f[i7] = textureRegion2;
            textureRegion = textureRegion2;
        }
        float f6 = f4 / textureRegion.b;
        float f7 = (-f4) - (2.0f * f6);
        graphicsContext.a(textureRegion.d, f2 + f7, f7 + f3, g, f6);
    }

    public static final int a(int i2) {
        return i2 >>> 24;
    }

    public static final int b(int i2) {
        return (i2 >> 16) & 255;
    }

    public static final int c(int i2) {
        return (i2 >> 8) & 255;
    }

    public static final int d(int i2) {
        return i2 & 255;
    }

    public static void a(GraphicsContext graphicsContext, UnitTexture unitTexture, Rect rect, Paint paint, int i2, int i3, int i4, int i5) {
        int iWidth = unitTexture.width();
        int iHeight = unitTexture.height();
        if (i2 != 0 && (i2 = i2 % unitTexture.width()) < 0) {
            i2 += unitTexture.width();
        }
        if (i3 != 0 && (i3 = i3 % unitTexture.height()) < 0) {
            i3 += unitTexture.height();
        }
        int i6 = rect.left - i2;
        int i7 = rect.top - i3;
        int i8 = iWidth - i4;
        int i9 = iHeight - i5;
        if (i8 > 0 && i9 > 0) {
            int i10 = 0;
            while (i6 < rect.right) {
                while (i7 < rect.bottom) {
                    i10++;
                    if (i10 > 2000) {
                        GameEngine.log("tileImage hit limit");
                        return;
                    }
                    int i11 = rect.right - i6;
                    if (i11 > iWidth) {
                        i11 = iWidth;
                    }
                    int i12 = rect.bottom - i7;
                    if (i12 > iHeight) {
                        i12 = iHeight;
                    }
                    if (i12 <= 0 || i11 <= 0) {
                        break;
                    }
                    h.set(0, 0, i11, i12);
                    i.set(i6, i7, i11 + i6, i12 + i7);
                    int i13 = i.left - rect.left;
                    if (i13 < 0) {
                        h.left -= i13;
                        i.left -= i13;
                    }
                    int i14 = i.top - rect.top;
                    if (i14 < 0) {
                        h.top -= i14;
                        i.top -= i14;
                    }
                    graphicsContext.a(unitTexture, h, i, paint);
                    i7 += i9;
                }
                i6 += i8;
                i7 = rect.top - i3;
            }
        }
    }

    public static void a(GraphicsContext graphicsContext, UnitTexture unitTexture, RectF rectF, Paint paint, float f2, float f3) {
        int iWidth = unitTexture.width();
        int iHeight = unitTexture.height();
        if (f2 != 0.0f) {
            f2 %= iWidth;
            if (f2 < 0.0f) {
                f2 += iWidth;
            }
        }
        if (f3 != 0.0f) {
            f3 %= iHeight;
            if (f3 < 0.0f) {
                f3 += iHeight;
            }
        }
        float f4 = rectF.left - f2;
        float f5 = rectF.top - f3;
        int i2 = 0;
        int i3 = iWidth + 0;
        int i4 = iHeight + 0;
        if (i3 <= 0 || i4 <= 0) {
            return;
        }
        while (f4 < rectF.right) {
            while (f5 < rectF.bottom) {
                i2++;
                if (i2 > 2000) {
                    GameEngine.log("tileImage hit limit");
                    return;
                }
                float f6 = rectF.right - f4;
                if (f6 > iWidth) {
                    f6 = iWidth;
                }
                float f7 = rectF.bottom - f5;
                if (f7 > iHeight) {
                    f7 = iHeight;
                }
                if (f7 <= 0.0f || f6 <= 0.0f) {
                    break;
                }
                h.set(0, 0, (int) f6, (int) f7);
                j.set(f4, f5, f6 + f4, f7 + f5);
                float f8 = j.left - rectF.left;
                if (f8 < 0.0f) {
                    h.left = (int) (h.left - f8);
                    j.left -= f8;
                }
                float f9 = j.top - rectF.top;
                if (f9 < 0.0f) {
                    h.top = (int) (h.top - f9);
                    j.top -= f9;
                }
                graphicsContext.a(unitTexture, h, j, paint);
                f5 += i4;
            }
            f4 += i3;
            f5 = rectF.top - f3;
        }
    }

    public static void a(GraphicsContext graphicsContext, UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint, float f2) {
        int iWidth = rect.width();
        int iHeight = rect.height();
        int i2 = rect2.left + 0;
        int i3 = rect2.top + 0;
        int i4 = rect2.right - i2;
        int i5 = rect2.bottom - i3;
        int i6 = (int) ((i4 / iWidth) + 0.5f);
        int i7 = (int) (0.5f + (i5 / iHeight));
        if (i6 <= 0) {
            i6 = 1;
        }
        if (i7 <= 0) {
            i7 = 1;
        }
        float fE = LogicNumberFuntion.e(1.0f, i4 / (i6 * iWidth), f2);
        float fE2 = LogicNumberFuntion.e(1.0f, i5 / (i7 * iHeight), f2);
        int i8 = (int) (iWidth * fE);
        int i9 = (int) (iHeight * fE2);
        float f3 = 1.0f / fE;
        float f4 = 1.0f / fE2;
        int i10 = i8 + 0;
        int i11 = i9 + 0;
        if (i10 > 0 && i11 > 0) {
            int i12 = 0;
            while (i2 < rect2.right) {
                while (i3 < rect2.bottom) {
                    i12++;
                    if (i12 > 2000) {
                        GameEngine.log("tileImage hit limit");
                        return;
                    }
                    int i13 = rect2.right - i2;
                    if (i13 > i8) {
                        i13 = i8;
                    }
                    int i14 = rect2.bottom - i3;
                    if (i14 > i9) {
                        i14 = i9;
                    }
                    if (i14 <= 0 || i13 <= 0) {
                        break;
                    }
                    h.set(0, 0, (int) (i13 * f3), (int) (i14 * f4));
                    h.offset(rect.left, rect.top);
                    i.set(i2, i3, i13 + i2, i14 + i3);
                    int i15 = i.left - rect2.left;
                    if (i15 < 0) {
                        h.left -= i15;
                        i.left -= i15;
                    }
                    int i16 = i.top - rect2.top;
                    if (i16 < 0) {
                        h.top -= i16;
                        i.top -= i16;
                    }
                    graphicsContext.a(unitTexture, h, i, paint);
                    i3 += i11;
                }
                i2 += i10;
                i3 = rect2.top + 0;
            }
        }
    }

    public static void a(Paint paint) {
    }
}
