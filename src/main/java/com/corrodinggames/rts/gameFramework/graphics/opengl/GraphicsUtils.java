package com.corrodinggames.rts.gameFramework.graphics.opengl;

import android.graphics.Bitmap;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.ui.TextUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.aa */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/aa.class */
public final class GraphicsUtils {
    static float b;
    static float c;
    static float d;
    static ShapeCache[] f;
    static Paint g;
    static int a = -1;
    public static final Rect e = new Rect();
    static final Rect h = new Rect();
    static final Rect i = new Rect();
    static final RectF j = new RectF();

    public static final Bitmap a(Texture texture) {
        return texture.b();
    }

    public static void a(String str, float f2, float f3, Paint paint) {
        GameEngine gameEngine = GameEngine.getInstance();
        String[] strArrSplitByChar = Utility.splitByChar(str, '\n');
        float charWidth = TextUtils.getCharWidth(paint);
        float length = (strArrSplitByChar.length - 1) * charWidth;
        int i2 = 0;
        for (String str2 : strArrSplitByChar) {
            gameEngine.graphicsEngine2.a(str2, f2, (f3 - (length / 2.0f)) + (i2 * charWidth) + (charWidth / 2.0f), paint);
            i2++;
        }
    }

    public static void a(GraphicsEngine graphicsEngine, float f2, float f3, float f4, Paint paint) {
        if (GameEngine.isDesktop()) {
            a(graphicsEngine, f2, f3, f4, 40, paint, GameEngine.getInstance().cameraSmoothing);
        } else {
            graphicsEngine.a(f2, f3, f4, paint);
        }
    }

    public static ShapeCache a(float f2, float f3, boolean z, GraphicsEngine graphicsEngine) {
        int i2;
        int i3;
        int i4 = (int) f3;
        int i5 = 0;
        if (((int) f2) > 20) {
            i5 = 1;
            i2 = 60;
        } else {
            i2 = 30;
        }
        int i6 = 0;
        if (i4 >= 2) {
            i6 = 1;
            i3 = 2;
        } else {
            i3 = 1;
        }
        int i7 = i5 + (i6 * 2) + (z ? 0 : 2 + (2 * 2));
        if (f == null) {
            f = new ShapeCache[((2 + (2 * 2)) * 2) + 1];
        }
        if (f[i7] != null) {
            if (f[i7].b != i2) {
                GameEngine.isInSpace("Mismatch on index: " + i7 + " size:" + i2);
            }
            return f[i7];
        }
        ShapeCache shapeCache = new ShapeCache();
        shapeCache.d = a(i2, i3, z, graphicsEngine);
        shapeCache.b = i2;
        shapeCache.a = i3;
        shapeCache.c = z;
        f[i7] = shapeCache;
        return shapeCache;
    }

    public static Texture a(int i2, int i3, boolean z, GraphicsEngine graphicsEngine) {
        Paint paint = new Paint();
        paint.b(-1);
        paint.a(z ? Paint.Style.FILL : Paint.Style.STROKE);
        paint.a(i3);
        Texture textureB = graphicsEngine.b((i2 * 2) + 4, (i2 * 2) + 4, true);
        GraphicsEngine graphicsEngineB = graphicsEngine.b(textureB);
        graphicsEngineB.b(i2 + 2, i2 + 2, i2, paint);
        graphicsEngineB.p();
        textureB.p();
        graphicsEngineB.q();
        return textureB;
    }

    public static void a(final GraphicsEngine y, final float float2, final float float3, final float float4, final Paint paint, final float float6) {
        if (GraphicsUtils.g == null) {
            (GraphicsUtils.g = new Paint()).a(true);
            GraphicsUtils.g.b(true);
        }
        final int e = paint.e();
        if (GameEngine.isDesktop()) {
            GraphicsUtils.g.a(new LightingColorFilter(e, 0));
        }
        GraphicsUtils.g.b(e);
        final ShapeCache a = a(float4 * float6, paint.g(), paint.d() == Paint.Style.FILL, y);
        final float float7 = float4 / a.b;
        final float n = -float4 - float7 * 2.0f;
        y.a(a.d, float2 + n, float3 + n, GraphicsUtils.g, 0.0f, float7);
    }

    public static void a(GraphicsEngine graphicsEngine, float f2, float f3, float f4, int i2, Paint paint, Rect rect) {
        if (a != i2) {
            a = i2;
            b = 6.283185f / i2;
            c = Utility.fastCos(b);
            d = Utility.fastSin(b);
        }
        float f5 = c;
        float f6 = d;
        int i3 = ((int) (b * f4 * 0.5f)) + 50;
        e.a = rect.a - i3;
        e.b = rect.b - i3;
        e.c = rect.c + i3;
        e.d = rect.d + i3;
        float f7 = f4;
        float f8 = 0.0f;
        int i4 = i2 + 1;
        float f9 = 0.0f;
        float f10 = 0.0f;
        float f11 = 0.0f;
        float f12 = 0.0f;
        boolean z = true;
        for (int i5 = 0; i5 < i4; i5++) {
            float f13 = f7 + f2;
            float f14 = f8 + f3;
            if (z) {
                z = false;
                f9 = f13;
                f10 = f14;
            } else if (e.b((int) f13, (int) f14) || e.b((int) f11, (int) f12)) {
                graphicsEngine.a(f13, f14, f11, f12, paint);
            }
            f11 = f13;
            f12 = f14;
            float f15 = f7;
            f7 = (f5 * f7) - (f6 * f8);
            f8 = (f6 * f15) + (f5 * f8);
        }
        if (e.b((int) f9, (int) f10) || e.b((int) f11, (int) f12)) {
            graphicsEngine.a(f9, f10, f11, f12, paint);
        }
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

    public static void a(GraphicsEngine graphicsEngine, Texture texture, Rect rect, Paint paint, int i2, int i3, int i4, int i5) {
        int iM = texture.m();
        int iL = texture.l();
        if (i2 != 0) {
            i2 %= texture.m();
            if (i2 < 0) {
                i2 += texture.m();
            }
        }
        if (i3 != 0) {
            i3 %= texture.l();
            if (i3 < 0) {
                i3 += texture.l();
            }
        }
        int i6 = rect.a - i2;
        int i7 = rect.b - i3;
        int i8 = iM - i4;
        int i9 = iL - i5;
        if (i8 <= 0 || i9 <= 0) {
            return;
        }
        int i10 = 0;
        while (i6 < rect.c) {
            while (i7 < rect.d) {
                i10++;
                if (i10 > 2000) {
                    GameEngine.isInSpace("tileImage hit limit");
                    return;
                }
                int i11 = rect.c - i6;
                if (i11 > iM) {
                    i11 = iM;
                }
                int i12 = rect.d - i7;
                if (i12 > iL) {
                    i12 = iL;
                }
                if (i12 > 0 && i11 > 0) {
                    h.a(0, 0, i11, i12);
                    i.a(i6, i7, i6 + i11, i7 + i12);
                    int i13 = i.a - rect.a;
                    if (i13 < 0) {
                        h.a -= i13;
                        i.a -= i13;
                    }
                    int i14 = i.b - rect.b;
                    if (i14 < 0) {
                        h.b -= i14;
                        i.b -= i14;
                    }
                    graphicsEngine.a(texture, h, i, paint);
                    i7 += i9;
                }
            }
            i6 += i8;
            i7 = rect.b - i3;
        }
    }

    public static void a(final GraphicsEngine y, final Texture e, final RectF rectF, final Paint paint, float float5, float float6, final int integer7, final int integer8) {
        final int m = e.m();
        final int l = e.l();
        if (float5 != 0.0f) {
            float5 %= m;
            if (float5 < 0.0f) {
                float5 += m;
            }
        }
        if (float6 != 0.0f) {
            float6 %= l;
            if (float6 < 0.0f) {
                float6 += l;
            }
        }
        float float7 = rectF.a - float5;
        float float8 = rectF.b - float6;
        int n = 0;
        final int n2 = m - integer7;
        final int n3 = l - integer8;
        if (n2 <= 0 || n3 <= 0) {
            return;
        }
        while (float7 < rectF.c) {
            while (float8 < rectF.d) {
                if (++n > 2000) {
                    GameEngine.isInSpace("tileImage hit limit");
                    return;
                }
                float n4 = rectF.c - float7;
                if (n4 > m) {
                    n4 = (float)m;
                }
                float n5 = rectF.d - float8;
                if (n5 > l) {
                    n5 = (float)l;
                }
                if (n5 <= 0.0f) {
                    break;
                }
                if (n4 <= 0.0f) {
                    break;
                }
                GraphicsUtils.h.a(0, 0, (int)n4, (int)n5);
                GraphicsUtils.j.a(float7, float8, float7 + n4, float8 + n5);
                final float n6 = GraphicsUtils.j.a - rectF.a;
                if (n6 < 0.0f) {
                    final Rect h = GraphicsUtils.h;
                    h.a -= (int)n6;
                    final RectF j = GraphicsUtils.j;
                    j.a -= n6;
                }
                final float n7 = GraphicsUtils.j.b - rectF.b;
                if (n7 < 0.0f) {
                    final Rect h2 = GraphicsUtils.h;
                    h2.b -= (int)n7;
                    final RectF i = GraphicsUtils.j;
                    i.b -= n7;
                }
                y.a(e, GraphicsUtils.h, GraphicsUtils.j, paint);
                float8 += n3;
            }
            float7 += n2;
            float8 = rectF.b - float6;
        }
    }

    public static void a(GraphicsEngine graphicsEngine, Texture texture, Rect rect, Rect rect2, Paint paint, int i2, int i3, int i4, int i5, float f2) {
        int iB = rect.b();
        int iC = rect.c();
        if (i2 != 0) {
            i2 %= iB;
            if (i2 < 0) {
                i2 += iB;
            }
        }
        if (i3 != 0) {
            i3 %= iC;
            if (i3 < 0) {
                i3 += iC;
            }
        }
        int i6 = rect2.a - i2;
        int i7 = rect2.b - i3;
        int i8 = rect2.c - i6;
        int i9 = rect2.d - i7;
        int i10 = (int) ((i8 / iB) + 0.5f);
        int i11 = (int) ((i9 / iC) + 0.5f);
        if (i10 < 1) {
            i10 = 1;
        }
        if (i11 < 1) {
            i11 = 1;
        }
        float f3 = i8 / (i10 * iB);
        float f4 = i9 / (i11 * iC);
        float fFromHexString = Utility.fromHexString(1.0f, f3, f2);
        float fFromHexString2 = Utility.fromHexString(1.0f, f4, f2);
        int i12 = (int) (iB * fFromHexString);
        int i13 = (int) (iC * fFromHexString2);
        float f5 = 1.0f / fFromHexString;
        float f6 = 1.0f / fFromHexString2;
        int i14 = i12 - i4;
        int i15 = i13 - i5;
        if (i14 <= 0 || i15 <= 0) {
            return;
        }
        int i16 = 0;
        while (i6 < rect2.c) {
            while (i7 < rect2.d) {
                i16++;
                if (i16 > 2000) {
                    GameEngine.isInSpace("tileImage hit limit");
                    return;
                }
                int i17 = rect2.c - i6;
                if (i17 > i12) {
                    i17 = i12;
                }
                int i18 = rect2.d - i7;
                if (i18 > i13) {
                    i18 = i13;
                }
                if (i18 > 0 && i17 > 0) {
                    h.a(0, 0, (int) (i17 * f5), (int) (i18 * f6));
                    h.a(rect.a, rect.b);
                    i.a(i6, i7, i6 + i17, i7 + i18);
                    int i19 = i.a - rect2.a;
                    if (i19 < 0) {
                        h.a -= i19;
                        i.a -= i19;
                    }
                    int i20 = i.b - rect2.b;
                    if (i20 < 0) {
                        h.b -= i20;
                        i.b -= i20;
                    }
                    graphicsEngine.a(texture, h, i, paint);
                    i7 += i15;
                }
            }
            i6 += i14;
            i7 = rect2.b - i3;
        }
    }

    public static void a(Paint paint) {
        if (GameEngine.isDesktop()) {
            int iE = paint.e();
            paint.a(new LightingColorFilter(Utility.longToIntArray(255, (iE >> 16) & 255, (iE >> 8) & 255, iE & 255), 0));
        }
    }
}
