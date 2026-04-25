package com.corrodinggames.rts.gameFramework.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.opengl.GraphicsUtils;
import com.corrodinggames.rts.gameFramework.graphics.opengl.TextureWrapper;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.io.File;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.x */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/x.class */
public class SoftwareGraphicsInterface implements GraphicsEngine {
    static SoftwareGraphicsInterface a;
    static Texture b;
    static int[] c;
    static Bitmap d;
    private Context x;
    private GraphicsInterface y;
    private GraphicsInterface z;
    private AudioRenderer A;
    public boolean h;
    public Bitmap i;
    public Texture j;
    public int k;
    public int l;
    private SoftwareGraphicsInterface B;
    Texture s;
    static IntBuffer e = IntBuffer.allocate(0);
    static IntBuffer f = IntBuffer.allocate(0);
    static final RectF t = new RectF();
    static final RectF u = new RectF();
    static Rect w = new Rect();
    boolean g = false;
    final Rect m = new Rect();
    final Rect n = new Rect();
    final RectF o = new RectF();
    final RectF p = new RectF();
    final Matrix q = new Matrix();
    final RectF r = new RectF();
    FastArrayList v = new FastArrayList();

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public GraphicsEngine a(Texture texture) {
        SoftwareGraphicsInterface softwareGraphicsInterface = (SoftwareGraphicsInterface) b(texture);
        softwareGraphicsInterface.h = true;
        return softwareGraphicsInterface;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public GraphicsEngine b(Texture texture) {
        if (this.B != null) {
            return this.B.b(texture);
        }
        SoftwareGraphicsInterface softwareGraphicsInterface = new SoftwareGraphicsInterface();
        softwareGraphicsInterface.a(this.x);
        Canvas canvas = new Canvas();
        Bitmap bitmapB = texture.b();
        canvas.a(bitmapB);
        softwareGraphicsInterface.y = new CanvasGraphicsRenderer(canvas);
        softwareGraphicsInterface.z = softwareGraphicsInterface.y;
        softwareGraphicsInterface.i = bitmapB;
        softwareGraphicsInterface.j = texture;
        softwareGraphicsInterface.B = this;
        if (texture != null) {
            softwareGraphicsInterface.k = texture.m();
            softwareGraphicsInterface.l = texture.l();
        }
        return softwareGraphicsInterface;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public boolean a() {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Context context) {
        this.x = context;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void b() {
        this.s = new TextureWrapper(a(R.drawable.error_outmem));
    }

    private void t() {
        SoftwareGraphicsInterface softwareGraphicsInterface;
        if (a != this) {
            a = this;
            if (this.B != null) {
                softwareGraphicsInterface = this.B;
            } else {
                softwareGraphicsInterface = this;
            }
            if (softwareGraphicsInterface.c()) {
                GraphicsInterface graphicsInterface = this.y;
                if (this.B != null) {
                    graphicsInterface = this.B.y;
                }
                if (!this.h) {
                    if (b != null) {
                        graphicsInterface.a((Texture) null);
                        b = null;
                    }
                    if (this.B != null) {
                        this.y = this.z;
                        return;
                    }
                    return;
                }
                if (this.B != null) {
                    this.y = graphicsInterface;
                }
                if (this.j != b) {
                    this.y.a(this.j);
                    b = this.j;
                    return;
                }
                return;
            }
            if (b != null) {
                GraphicsInterface graphicsInterface2 = this.y;
                if (this.B != null) {
                    graphicsInterface2 = this.B.y;
                }
                graphicsInterface2.a((Texture) null);
                b = null;
            }
            if (this.B != null) {
                this.y = this.z;
            }
        }
    }

    private void a(Paint paint, String str) {
        a(paint, true, str, (Texture) null);
    }

    private void b(Paint paint) {
        a(paint, false, (String) null, (Texture) null);
    }

    private void a(Paint paint, Texture texture) {
        a(paint, false, (String) null, texture);
    }

    public boolean c() {
        if ((this.y instanceof DeferredGraphicsInterface) || (this.y instanceof OpenGLGraphicsRenderer)) {
            return true;
        }
        return false;
    }

    private void a(Paint paint, boolean z, String str, Texture texture) {
        t();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public GraphicsInterface d() {
        return this.y;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(GraphicsInterface graphicsInterface) {
        this.y = graphicsInterface;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(AudioRenderer audioRenderer) {
        this.A = audioRenderer;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public Texture a(int i) {
        return a(i, true);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public Texture a(int i, boolean z) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (z) {
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        } else {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        options.inScaled = false;
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(this.x.e(), i, options);
        if (bitmapDecodeResource == null) {
            throw new RuntimeException("Could not load image with resId:" + i);
        }
        return a(bitmapDecodeResource);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public Texture a(InputStream inputStream, boolean z) {
        if (inputStream == null) {
            throw new RuntimeException("loadImage: steam is null");
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (z) {
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        } else {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        options.inScaled = false;
        try {
            Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStream, null, options);
            if (bitmapDecodeStream == null) {
                GameEngine.logWarningAndStack("Could not load image from steam");
                return null;
            }
            Log.d("RustedWarfare", "load a:" + z + " as " + bitmapDecodeStream.d());
            return a(bitmapDecodeStream);
        } catch (OutOfMemoryError e2) {
            GameEngine.reportOOM(AssetType.gameImage, e2);
            if (this.s == null) {
                throw new RuntimeException("outOfMemoryErrorImage==null", e2);
            }
            return this.s;
        }
    }

    public Texture a(Bitmap bitmap) {
        Texture texture = new Texture();
        texture.a(bitmap);
        return texture;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public Texture a(int i, int i2, boolean z) {
        return b(i, i2, z);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public Texture b(int i, int i2, boolean z) {
        Bitmap.Config config;
        if (z) {
            config = Bitmap.Config.ARGB_8888;
        } else {
            config = Bitmap.Config.ARGB_8888;
        }
        try {
            Bitmap bitmapA = Bitmap.a(i, i2, config);
            if (bitmapA != null && !z && config == Bitmap.Config.ARGB_8888 && Build.VERSION.SDK_INT >= 12) {
                bitmapA.a(false);
            }
            if (bitmapA == null) {
                OutOfMemoryError outOfMemoryError = new OutOfMemoryError("createBitmap returned null, possible out of memory");
                GameEngine.reportOOM(AssetType.gameImageCreate, outOfMemoryError);
                if (this.s == null) {
                    throw new RuntimeException("outOfMemoryErrorImage==null", outOfMemoryError);
                }
                return this.s;
            }
            return a(bitmapA);
        } catch (OutOfMemoryError e2) {
            GameEngine.reportOOM(AssetType.gameImageCreate, e2);
            if (this.s == null) {
                throw new RuntimeException("outOfMemoryErrorImage==null", e2);
            }
            return this.s;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void e() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, float f2, float f3, float f4, Paint paint) {
        GraphicsInterface graphicsInterface = this.y;
        graphicsInterface.b();
        graphicsInterface.a(f4 + 90.0f, f2, f3);
        b(texture, f2 - texture.t, f3 - texture.u, paint);
        graphicsInterface.a();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, Rect rect, float f2, float f3, float f4, Paint paint) {
        GraphicsInterface graphicsInterface = this.y;
        graphicsInterface.b();
        int iB = rect.b() >> 1;
        int iC = rect.c() >> 1;
        this.p.a(f2 - iB, f3 - iC, f2 + iB, f3 + iC);
        graphicsInterface.a(f4 + 90.0f, f2, f3);
        a(texture, rect, this.p, paint);
        graphicsInterface.a();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, Rect rect, Rect rect2, Paint paint) {
        if (c(texture) != null) {
            this.y.a(texture, rect, rect2, paint);
            return;
        }
        throw new RuntimeException("bitmap was not drawn");
    }

    public Bitmap c(Texture texture) {
        Bitmap bitmapB = texture.b();
        if (texture.f != texture.e) {
            texture.f = texture.e;
            d().a(bitmapB);
        }
        return bitmapB;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void b(Texture texture, Rect rect, Rect rect2, Paint paint) {
        if (!this.g) {
            a(texture, rect, rect2, paint);
            return;
        }
        Bitmap bitmapC = c(texture);
        boolean zF = texture.f();
        if (paint.f() < 255) {
            zF = true;
        }
        int iB = this.i.b();
        int iC = this.i.c();
        int iB2 = bitmapC.b();
        bitmapC.c();
        texture.i();
        int[] iArr = texture.j;
        if (d != this.i) {
            a(false);
        }
        int[] iArr2 = c;
        int i = rect2.b;
        int i2 = rect2.d;
        int i3 = rect2.a;
        int i4 = rect2.c;
        int i5 = rect.b;
        int i6 = rect.d;
        int i7 = rect.a;
        float f2 = 1.0f;
        float f3 = 1.0f;
        int i8 = rect.c - i7;
        int i9 = i6 - i5;
        int i10 = i4 - i3;
        int i11 = i2 - i;
        if (i11 != 0) {
            f2 = i9 / i11;
        }
        if (i10 != 0) {
            f3 = i8 / i10;
        }
        if (i < 0) {
            i5 = (int) (i5 + ((-i) * f2));
            i = 0;
        }
        if (i2 > iC - 1) {
            i2 = iC - 1;
        }
        if (i > i2) {
            return;
        }
        if (i3 < 0) {
            i7 = (int) (i7 + ((-i3) * f3));
            i3 = 0;
        }
        if (i4 > iB - 1) {
            i4 = iB - 1;
        }
        if (i3 > i4) {
            return;
        }
        int i12 = i * iB;
        int i13 = i5 * iB2;
        int i14 = i2 * iB;
        float f4 = 0.0f;
        int i15 = i3;
        int i16 = i7;
        int i17 = i4;
        if (!zF) {
            while (i12 < i14) {
                int i18 = i12 + i15;
                int i19 = i13 + (((int) f4) * iB2) + i16;
                float f5 = 0.0f;
                int i20 = i12 + i17;
                int i21 = i20 - 4;
                while (i18 < i21) {
                    iArr2[i18] = iArr[i19 + ((int) f5)];
                    float f6 = f5 + f3;
                    int i22 = i18 + 1;
                    iArr2[i22] = iArr[i19 + ((int) f6)];
                    float f7 = f6 + f3;
                    int i23 = i22 + 1;
                    iArr2[i23] = iArr[i19 + ((int) f7)];
                    float f8 = f7 + f3;
                    int i24 = i23 + 1;
                    iArr2[i24] = iArr[i19 + ((int) f8)];
                    f5 = f8 + f3;
                    i18 = i24 + 1;
                }
                while (i18 < i20) {
                    iArr2[i18] = iArr[i19 + ((int) f5)];
                    i18++;
                    f5 += f3;
                }
                i12 += iB;
                f4 += f2;
            }
            return;
        }
        int iE = paint.e() >>> 24;
        while (i12 < i14) {
            int i25 = i12 + i15;
            int i26 = i13 + (((int) f4) * iB2) + i16;
            float f9 = 0.0f;
            int i27 = i12 + i17;
            int i28 = -1;
            int i29 = 0;
            int i30 = 0;
            int i31 = 0;
            while (i25 < i27) {
                int i32 = i26 + ((int) f9);
                if (i32 != i28) {
                    int i33 = iArr[i32];
                    i29 = ((i33 >>> 24) * iE) >> 8;
                    i30 = i33 & 16711935;
                    i31 = i33 & 65280;
                    if (i29 == 0) {
                        i25++;
                        f9 += f3;
                    } else {
                        i28 = i32;
                    }
                }
                int i34 = iArr2[i25];
                int i35 = i34 & 16711935;
                int i36 = i34 & 65280;
                iArr2[i25] = (-16777216) | ((i35 + (((i30 - i35) * i29) >> 8)) & 16711935) | ((i36 + (((i31 - i36) * i29) >> 8)) & 65280);
                i25++;
                f9 += f3;
            }
            i12 += iB;
            f4 += f2;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Rect rect, Paint paint) {
        if (!this.g) {
            b(rect, paint);
            return;
        }
        if (d != this.i) {
            a(false);
        }
        int iB = this.i.b();
        int iC = this.i.c();
        int[] iArr = c;
        int i = rect.b;
        int i2 = rect.d;
        int i3 = rect.a;
        int i4 = rect.c;
        if (i < 0) {
            i = 0;
        }
        if (i2 > iC - 1) {
            i2 = iC - 1;
        }
        if (i > i2) {
            return;
        }
        if (i3 < 0) {
            i3 = 0;
        }
        if (i4 > iB - 1) {
            i4 = iB - 1;
        }
        if (i3 > i4) {
            return;
        }
        int i5 = i * iB;
        int i6 = i2 * iB;
        int i7 = i3;
        int i8 = i4;
        int iE = paint.e();
        int i9 = iE >>> 24;
        if (!(i9 < 255)) {
            while (i5 < i6) {
                int i10 = i5 + i8;
                for (int i11 = i5 + i7; i11 < i10; i11++) {
                    iArr[i11] = iE;
                }
                i5 += iB;
            }
            return;
        }
        int i12 = (iE >> 16) & 255;
        int i13 = (iE >> 8) & 255;
        int i14 = iE & 255;
        float f2 = i9 * 0.003921569f;
        int i15 = (int) (i12 * f2);
        int i16 = (int) (i13 * f2);
        int i17 = (int) (i14 * f2);
        int i18 = 255 - i9;
        int i19 = (-16777216) | (i15 << 16) | (i16 << 8) | i17;
        while (i5 < i6) {
            int i20 = i5 + i8;
            for (int i21 = i5 + i7; i21 < i20; i21++) {
                int i22 = iArr[i21];
                iArr[i21] = (((((i22 & 16711935) * i18) >> 8) & 16711935) | ((((i22 & 65280) * i18) >> 8) & 65280)) + i19;
            }
            i5 += iB;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(boolean z) {
        this.g = true;
        if (d == this.i) {
            return;
        }
        int iB = this.i.b();
        int iC = this.i.c();
        int i = iB * iC;
        if (c == null || c.length < i) {
            c = new int[i];
        }
        if (!z) {
            this.i.a(c, 0, iB, 0, 0, iB, iC);
        }
        d = this.i;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void f() {
        this.g = false;
        if (d == this.i) {
            int iB = this.i.b();
            this.i.b(c, 0, iB, 0, 0, iB, this.i.c());
            d = null;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, Rect rect, RectF rectF, Paint paint) {
        if (c(texture) != null) {
            a(paint, texture);
            this.y.a(texture, rect, rectF, paint);
            return;
        }
        throw new RuntimeException("bitmap was not drawn");
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, float f2, float f3, Paint paint) {
        b(texture, f2 - texture.t, f3 - texture.u, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, float f2, float f3, Paint paint, float f4, float f5) {
        GraphicsInterface graphicsInterface = this.y;
        graphicsInterface.b();
        if (f4 != 0.0f) {
            graphicsInterface.a(f4 + 90.0f, f2, f3);
        }
        graphicsInterface.a(f5, f5, f2, f3);
        c(texture);
        graphicsInterface.a(texture, f2, f3, paint);
        graphicsInterface.a();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void b(Texture texture, float f2, float f3, Paint paint) {
        if (c(texture) != null) {
            this.y.a(texture, f2, f3, paint);
            return;
        }
        throw new RuntimeException("bitmap was not drawn");
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, Rect rect, Paint paint) {
        a(texture, rect, paint, 0, 0, 0, 0);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, Rect rect, Paint paint, int i, int i2, int i3, int i4) {
        GraphicsUtils.a(this, texture, rect, paint, i, i2, i3, i4);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, RectF rectF, Paint paint, float f2, float f3, int i, int i2) {
        GraphicsUtils.a(this, texture, rectF, paint, f2, f3, i, i2);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void b(int i) {
        t();
        this.y.a(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(int i, PorterDuff.Mode mode) {
        t();
        this.y.a(i, mode);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(String str, float f2, float f3, Paint paint, Paint paint2, float f4) {
        float fA = paint.a(str);
        u.a(f2, f3, f2 + fA, f3 + a(str, paint));
        t.a(u);
        if (paint.j() == Paint.Align.CENTER) {
            t.a(-(fA / 2.0f), 0.0f);
        }
        Utility.grow(t, f4);
        b(paint2);
        this.y.a(t, paint2);
        a(str, u.a + (f4 / 2.0f), u.d - (f4 / 2.0f), paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(String str, float f2, float f3, Paint paint) {
        a(paint, str);
        this.y.a(str, f2, f3, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void b(Rect rect, Paint paint) {
        b(paint);
        this.y.a(rect, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(RectF rectF, Paint paint) {
        b(paint);
        this.y.a(rectF, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void g() {
        if (this.v.size() > 0) {
            synchronized (this.v) {
                Iterator it = this.v.iterator();
                while (it.hasNext()) {
                    this.y.a((ShaderProgram) it.next());
                }
                this.v.clear();
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void h() {
        t();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void c(Rect rect, Paint paint) {
        this.n.a(rect.a, rect.b, rect.a + rect.c, rect.b + rect.d);
        b(paint);
        d().a(this.n, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Rect rect) {
        if (this.i != null) {
            rect = new Rect(rect);
            int iC = this.i.c() - this.l;
        }
        d().a(rect);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(RectF rectF) {
        if (this.i != null) {
            rectF = new RectF(rectF);
            int iC = this.i.c() - this.l;
        }
        d().a(rectF);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(float f2, float f3, float f4, Paint paint) {
        if (f4 < 50.0f) {
            GraphicsUtils.a(this, f2, f3, f4, paint, 1.0f);
        } else {
            b(paint);
            this.y.a(f2, f3, f4, paint);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void b(float f2, float f3, float f4, Paint paint) {
        b(paint);
        this.y.a(f2, f3, f4, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(float[] fArr, int i, int i2, Paint paint) {
        b(paint);
        d().a(fArr, i, i2, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void i() {
        t();
        this.y.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void j() {
        t();
        this.y.a();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void k() {
        t();
        this.y.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void l() {
        t();
        this.y.a();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(float f2, float f3, float f4) {
        this.y.a(f2, f3, f4);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(float f2, float f3) {
        this.y.a(f2, f3);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(float f2, float f3, float f4, float f5) {
        this.y.a(f2, f3, f4, f5);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void b(float f2, float f3) {
        this.y.b(f2, f3);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(float f2, float f3, float f4, float f5, Paint paint) {
        b(paint);
        d().a(f2, f3, f4, f5, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(GraphicsOperation graphicsOperation) {
        d().a(graphicsOperation);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public int m() {
        if (this.i != null) {
            return this.k;
        }
        return (int) GameEngine.getInstance().screenWidth;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public int n() {
        if (this.i != null) {
            return this.l;
        }
        return (int) GameEngine.getInstance().viewpointWidthRaw;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(int i, int i2) {
        this.k = i;
        this.l = i2;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void o() {
        b((Paint) null);
        a(0, PorterDuff.Mode.CLEAR);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Paint paint) {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(ShaderProgram shaderProgram) {
        if (shaderProgram != null) {
            synchronized (this.v) {
                this.v.add(shaderProgram);
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void p() {
        t();
        if (this.i != null && (this.y instanceof CanvasGraphicsRenderer)) {
            d().a(this.i);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void q() {
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public int a(String str, Paint paint) {
        return (int) paint.k();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public int b(String str, Paint paint) {
        paint.a(str, 0, str.length(), w);
        return w.a + w.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public Texture r() {
        return this.s;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Texture texture, File file) {
        throw new RuntimeException("writeImageToFile not yet supported");
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void a(Lock lock) {
        t();
        this.y.a(lock);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public void b(Lock lock) {
        t();
        this.y.b(lock);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine
    public float s() {
        return 1.0f;
    }
}
