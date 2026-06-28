package com.corrodinggames.rts.gameFramework.android.graphics;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.os.Debug;
import android.util.Log;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.appFramework.android.AndroidSAF;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.LogicNumberFuntion;
import com.corrodinggames.rts.gameFramework.m.GraphicsUtils;
import com.corrodinggames.rts.gameFramework.m.TextureProxy;
import com.corrodinggames.rts.gameFramework.m.UnitTexture;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.fh */
/* JADX INFO: loaded from: classes.dex */
public final class AndroidGraphicsContext implements com.corrodinggames.rts.gameFramework.m.GraphicsContext {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    static AndroidGraphicsContext f762a;
    static UnitTexture b;
    static int[] c;
    static Bitmap d;
    static IntBuffer e = IntBuffer.allocate(0);
    static IntBuffer f = IntBuffer.allocate(0);
    private static final Map<Integer, String> legacyDrawableNamesById = createLegacyDrawableNamesById();
    static final RectF t = new RectF();
    static final RectF u = new RectF();
    static Rect w = new Rect();
    private AndroidGLRenderer A;
    private AndroidGraphicsContext B;
    public boolean h;
    public Bitmap i;
    public UnitTexture j;
    public int k;
    public int l;
    UnitTexture s;
    private Context x;
    private GraphicsInterface y;
    private GraphicsInterface z;
    boolean g = false;
    final Rect m = new Rect();
    final Rect n = new Rect();
    final RectF o = new RectF();
    final RectF p = new RectF();
    final Matrix q = new Matrix();
    final RectF r = new RectF();
    FastArrayList v = new FastArrayList();

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final com.corrodinggames.rts.gameFramework.m.GraphicsContext a(UnitTexture unitTexture) {
        AndroidGraphicsContext androidGraphicsContext = (AndroidGraphicsContext) b(unitTexture);
        androidGraphicsContext.h = true;
        return androidGraphicsContext;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final com.corrodinggames.rts.gameFramework.m.GraphicsContext b(UnitTexture unitTexture) {
        AndroidGraphicsContext root = this;
        while (root.B != null) {
            root = root.B;
        }
        AndroidGraphicsContext androidGraphicsContext = new AndroidGraphicsContext();
        androidGraphicsContext.x = root.x;
        Canvas canvas = new Canvas();
        Bitmap bitmapB = unitTexture.b();
        canvas.setBitmap(bitmapB);
        androidGraphicsContext.y = new CanvasGraphicsRenderer(canvas);
        androidGraphicsContext.z = androidGraphicsContext.y;
        androidGraphicsContext.i = bitmapB;
        androidGraphicsContext.j = unitTexture;
        androidGraphicsContext.B = root;
        if (unitTexture != null) {
            androidGraphicsContext.k = unitTexture.width();
            androidGraphicsContext.l = unitTexture.height();
        }
        return androidGraphicsContext;
    }

    public final com.corrodinggames.rts.gameFramework.m.GraphicsContext a() {
        AndroidGraphicsContext androidGraphicsContext = new AndroidGraphicsContext();
        androidGraphicsContext.x = this.x;
        return androidGraphicsContext;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(Context context) {
        this.x = context;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b() {
        UnitTexture fallbackTexture = new UnitTexture();
        fallbackTexture.a(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        this.s = new TextureProxy(fallbackTexture);
    }

    private void q() {
        if (f762a != this) {
            f762a = this;
            AndroidGraphicsContext androidGraphicsContext = this.B != null ? this.B : this;
            boolean z = (androidGraphicsContext.y instanceof DeferredGraphicsInterface) || (androidGraphicsContext.y instanceof OpenGLGraphicsRenderer);
            if (z) {
                GraphicsInterface graphicsInterface = this.y;
                if (this.B != null) {
                    graphicsInterface = this.B.y;
                }
                if (!this.h) {
                    if (b != null) {
                        graphicsInterface.a((UnitTexture) null);
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
                graphicsInterface2.a((UnitTexture) null);
                b = null;
            }
            if (this.B != null) {
                this.y = this.z;
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final GraphicsInterface c() {
        return this.y;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(GraphicsInterface graphicsInterface) {
        this.y = graphicsInterface;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(AndroidGLRenderer androidGLRenderer) {
        this.A = androidGLRenderer;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture a(int i) {
        return a(i, true);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture a(int i, boolean z) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (z) {
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        } else {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        options.inScaled = false;
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(this.x.getResources(), i, options);
        if (bitmapDecodeResource == null) {
            bitmapDecodeResource = decodeLegacyDrawableAsset(i, options);
        }
        if (bitmapDecodeResource == null) {
            throw new RuntimeException("Could not load image with resId:".concat(String.valueOf(i)));
        }
        return a(bitmapDecodeResource);
    }

    private Bitmap decodeLegacyDrawableAsset(int resourceId, BitmapFactory.Options options) {
        String drawableName = legacyDrawableNamesById.get(resourceId);
        if (drawableName == null) {
            return null;
        }
        String[] candidates = new String[]{
                "drawable/" + drawableName + ".png",
                "drawable/" + drawableName + ".9.png",
                "drawable/" + drawableName + ".jpg",
                "drawable/" + drawableName + ".jpeg",
                "drawable/" + drawableName + ".webp"
        };
        for (String assetPath : candidates) {
            try (InputStream inputStream = this.x.getAssets().open(assetPath)) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                if (bitmap != null) {
                    return bitmap;
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    private static Map<Integer, String> createLegacyDrawableNamesById() {
        Map<Integer, String> namesById = new HashMap<>();
        for (Field field : R.drawable.class.getFields()) {
            try {
                namesById.put(field.getInt(null), field.getName());
            } catch (IllegalAccessException ignored) {
            }
        }
        return namesById;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture a(InputStream inputStream, boolean z) {
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
                GameEngine.logColored("Could not load image from steam");
                return null;
            }
            Log.e(AndroidSAF.TAG, "load a:" + z + " as " + bitmapDecodeStream.getConfig());
            return a(bitmapDecodeStream);
        } catch (OutOfMemoryError e2) {
            GameEngine.reportOOM(AssetType.gameImage, e2);
            Runtime runtime = Runtime.getRuntime();
            String source = inputStream instanceof AssetInputStream
                    ? ((AssetInputStream) inputStream).getPath()
                    : inputStream.getClass().getName();
            throw new RuntimeException(
                    "Out of memory decoding image: " + source
                            + ", javaHeap=" + runtime.totalMemory() + "/" + runtime.maxMemory()
                            + ", javaFree=" + runtime.freeMemory()
                            + ", nativeHeap=" + Debug.getNativeHeapAllocatedSize(),
                    e2
            );
        }
    }

    private static UnitTexture a(Bitmap bitmap) {
        UnitTexture unitTexture = new UnitTexture();
        unitTexture.a(bitmap);
        return unitTexture;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture a(int i, int i2, boolean z) {
        return b(i, i2, z);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture b(int i, int i2, boolean z) {
        Bitmap.Config config;
        if (z) {
            config = Bitmap.Config.ARGB_8888;
        } else {
            config = Bitmap.Config.ARGB_8888;
        }
        try {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i2, config);
            if (bitmapCreateBitmap != null && !z && config == Bitmap.Config.ARGB_8888 && Build.VERSION.SDK_INT >= 12) {
                bitmapCreateBitmap.setHasAlpha(false);
            }
            if (bitmapCreateBitmap == null) {
                OutOfMemoryError outOfMemoryError = new OutOfMemoryError("createBitmap returned null, possible out of memory");
                GameEngine.reportOOM(AssetType.gameImageCreate, outOfMemoryError);
                if (this.s == null) {
                    throw new RuntimeException("outOfMemoryErrorImage==null", outOfMemoryError);
                }
                return this.s;
            }
            return a(bitmapCreateBitmap);
        } catch (OutOfMemoryError e2) {
            GameEngine.reportOOM(AssetType.gameImageCreate, e2);
            if (this.s == null) {
                throw new RuntimeException("outOfMemoryErrorImage==null", e2);
            }
            return this.s;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, float f2, float f3, float f4, Paint paint) {
        q();
        GraphicsInterface graphicsInterface = this.y;
        graphicsInterface.b();
        graphicsInterface.a(90.0f + f4, f2, f3);
        b(unitTexture, f2 - unitTexture.t, f3 - unitTexture.u, paint);
        graphicsInterface.a_();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect, float f2, float f3, float f4, Paint paint) {
        q();
        GraphicsInterface graphicsInterface = this.y;
        graphicsInterface.b();
        int iWidth = rect.width() >> 1;
        int iHeight = rect.height() >> 1;
        this.p.set(f2 - iWidth, f3 - iHeight, iWidth + f2, iHeight + f3);
        graphicsInterface.a(90.0f + f4, f2, f3);
        a(unitTexture, rect, this.p, paint);
        graphicsInterface.a_();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint) {
        q();
        if (c(unitTexture) != null) {
            this.y.a(unitTexture, rect, rect2, paint);
            return;
        }
        throw new RuntimeException("bitmap was not drawn");
    }

    private Bitmap c(UnitTexture unitTexture) {
        q();
        Bitmap bitmapB = unitTexture.b();
        if (unitTexture.f != unitTexture.e) {
            unitTexture.f = unitTexture.e;
            this.y.a(bitmapB);
        }
        return bitmapB;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(UnitTexture unitTexture, Rect rect, Rect rect2, Paint paint) {
        q();
        float f2;
        float f3;
        int i;
        int i2;
        if (!this.g) {
            a(unitTexture, rect, rect2, paint);
            return;
        }
        Bitmap bitmapC = c(unitTexture);
        boolean z = unitTexture.m;
        if (paint.getAlpha() < 255) {
            z = true;
        }
        int width = this.i.getWidth();
        int height = this.i.getHeight();
        int width2 = bitmapC.getWidth();
        bitmapC.getHeight();
        unitTexture.e();
        int[] iArr = unitTexture.j;
        if (d != this.i) {
            a(false);
        }
        int[] iArr2 = c;
        int i3 = rect2.top;
        int i4 = rect2.bottom;
        int i5 = rect2.left;
        int i6 = rect2.right;
        int i7 = rect.top;
        int i8 = rect.bottom;
        int i9 = rect.left;
        int i10 = rect.right - i9;
        int i11 = i8 - i7;
        int i12 = i6 - i5;
        int i13 = i4 - i3;
        if (i13 == 0) {
            f2 = 1.0f;
        } else {
            f2 = ((float) i11) / i13;
        }
        if (i12 == 0) {
            f3 = 1.0f;
        } else {
            f3 = ((float) i10) / i12;
        }
        if (i3 < 0) {
            i7 = (int) (i7 + ((-i3) * f2));
            i3 = 0;
        }
        if (i4 > height - 1) {
            i4 = height - 1;
        }
        if (i3 <= i4) {
            if (i5 < 0) {
                i = (int) (i9 + ((-i5) * f3));
                i2 = 0;
            } else {
                i = i9;
                i2 = i5;
            }
            int i14 = i6 > width + (-1) ? width - 1 : i6;
            if (i2 <= i14) {
                int i15 = i3 * width;
                int i16 = i7 * width2;
                int i17 = i4 * width;
                float f4 = 0.0f;
                if (!z) {
                    while (i15 < i17) {
                        int i18 = i15 + i2;
                        int i19 = (((int) f4) * width2) + i16 + i;
                        float f5 = 0.0f;
                        int i20 = i15 + i14;
                        int i21 = i20 - 4;
                        while (i18 < i21) {
                            iArr2[i18] = iArr[((int) f5) + i19];
                            float f6 = f5 + f3;
                            int i22 = i18 + 1;
                            iArr2[i22] = iArr[((int) f6) + i19];
                            float f7 = f6 + f3;
                            int i23 = i22 + 1;
                            iArr2[i23] = iArr[((int) f7) + i19];
                            float f8 = f7 + f3;
                            int i24 = i23 + 1;
                            iArr2[i24] = iArr[((int) f8) + i19];
                            f5 = f8 + f3;
                            i18 = i24 + 1;
                        }
                        while (i18 < i20) {
                            iArr2[i18] = iArr[((int) f5) + i19];
                            i18++;
                            f5 += f3;
                        }
                        i15 += width;
                        f4 += f2;
                    }
                    return;
                }
                int color = paint.getColor() >>> 24;
                float f9 = 0.0f;
                for (int i25 = i15; i25 < i17; i25 += width) {
                    int i26 = i25 + i2;
                    int i27 = (((int) f9) * width2) + i16 + i;
                    int i28 = i25 + i14;
                    int i29 = -1;
                    int i30 = 0;
                    int i31 = 0;
                    int i32 = 0;
                    float f10 = 0.0f;
                    while (i26 < i28) {
                        int i33 = ((int) f10) + i27;
                        if (i33 != i29) {
                            int i34 = iArr[i33];
                            i30 = ((i34 >>> 24) * color) >> 8;
                            i31 = 16711935 & i34;
                            i32 = i34 & 65280;
                            if (i30 == 0) {
                                i26++;
                                f10 += f3;
                                continue;
                            }
                        } else {
                            i33 = i29;
                        }
                        int i35 = iArr2[i26];
                        int i36 = 16711935 & i35;
                        int i37 = i35 & 65280;
                        iArr2[i26] = ((i37 + (((i32 - i37) * i30) >> 8)) & 65280) | ((i36 + (((i31 - i36) * i30) >> 8)) & 16711935) | (-16777216);
                        i26++;
                        f10 += f3;
                        i29 = i33;
                    }
                    f9 += f2;
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(Rect rect, Paint paint) {
        if (!this.g) {
            b(rect, paint);
            return;
        }
        if (d != this.i) {
            a(false);
        }
        int width = this.i.getWidth();
        int height = this.i.getHeight();
        int[] iArr = c;
        int i = rect.top;
        int i2 = rect.bottom;
        int i3 = rect.left;
        int i4 = rect.right;
        int i5 = i < 0 ? 0 : i;
        int i6 = i2 > height + (-1) ? height - 1 : i2;
        if (i5 <= i6) {
            int i7 = i3 < 0 ? 0 : i3;
            int i8 = i4 > width + (-1) ? width - 1 : i4;
            if (i7 <= i8) {
                int i9 = i5 * width;
                int i10 = i6 * width;
                int color = paint.getColor();
                int i11 = color >>> 24;
                if (!(i11 < 255)) {
                    for (int i12 = i9; i12 < i10; i12 += width) {
                        int i13 = i12 + i8;
                        for (int i14 = i12 + i7; i14 < i13; i14++) {
                            iArr[i14] = color;
                        }
                    }
                    return;
                }
                float f2 = i11 * 0.003921569f;
                int i15 = 255 - i11;
                int i16 = ((int) ((color & 255) * f2)) | (((int) (((color >> 16) & 255) * f2)) << 16) | (-16777216) | (((int) (((color >> 8) & 255) * f2)) << 8);
                while (i9 < i10) {
                    int i17 = i9 + i8;
                    for (int i18 = i9 + i7; i18 < i17; i18++) {
                        int i19 = iArr[i18];
                        iArr[i18] = (((((i19 & 65280) * i15) >> 8) & 65280) | ((((16711935 & i19) * i15) >> 8) & 16711935)) + i16;
                    }
                    i9 += width;
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(boolean z) {
        this.g = true;
        if (d != this.i) {
            int width = this.i.getWidth();
            int height = this.i.getHeight();
            int i = width * height;
            if (c == null || c.length < i) {
                c = new int[i];
            }
            if (!z) {
                this.i.getPixels(c, 0, width, 0, 0, width, height);
            }
            d = this.i;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void d() {
        this.g = false;
        if (d == this.i) {
            int width = this.i.getWidth();
            this.i.setPixels(c, 0, width, 0, 0, width, this.i.getHeight());
            d = null;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect, RectF rectF, Paint paint) {
        q();
        if (c(unitTexture) == null) {
            throw new RuntimeException("bitmap was not drawn");
        }
        this.y.a(unitTexture, rect, rectF, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, float f2, float f3, Paint paint) {
        b(unitTexture, f2 - unitTexture.t, f3 - unitTexture.u, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, float f2, float f3, Paint paint, float f4) {
        q();
        GraphicsInterface graphicsInterface = this.y;
        graphicsInterface.b();
        graphicsInterface.b(f4, f4, f2, f3);
        c(unitTexture);
        graphicsInterface.a(unitTexture, f2, f3, paint);
        graphicsInterface.a_();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(UnitTexture unitTexture, float f2, float f3, Paint paint) {
        q();
        if (c(unitTexture) != null) {
            this.y.a(unitTexture, f2, f3, paint);
            return;
        }
        throw new RuntimeException("bitmap was not drawn");
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect) {
        a(unitTexture, rect, null, 0, 0, 0, 0);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, Rect rect, Paint paint, int i, int i2, int i3, int i4) {
        GraphicsUtils.a(this, unitTexture, rect, paint, i, i2, i3, i4);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(UnitTexture unitTexture, RectF rectF, Paint paint, float f2, float f3) {
        GraphicsUtils.a(this, unitTexture, rectF, paint, f2, f3);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(int i) {
        q();
        this.y.a(i);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(PorterDuff.Mode mode) {
        q();
        this.y.a(0, mode);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(String str, float f2, float f3, Paint paint, Paint paint2, float f4) {
        float fMeasureText = paint.measureText(str);
        u.set(f2, f3, f2 + fMeasureText, ((int) paint.getTextSize()) + f3);
        t.set(u);
        if (paint.getTextAlign() == Paint.Align.CENTER) {
            t.offset(-(fMeasureText / 2.0f), 0.0f);
        }
        LogicNumberFuntion.a(t, f4);
        q();
        this.y.b(t, paint2);
        a(str, u.left + (f4 / 2.0f), u.bottom - (f4 / 2.0f), paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void e() {
        if (this.v.size() > 0) {
            synchronized (this.v) {
                Iterator it = this.v.iterator();
                while (it.hasNext()) {
                    this.y.a((C0009fo) it.next());
                }
                this.v.clear();
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void f() {
        q();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void c(Rect rect, Paint paint) {
        this.n.set(rect.left, rect.top, rect.left + rect.right, rect.top + rect.bottom);
        q();
        this.y.a(this.n, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(Rect rect) {
        q();
        if (this.i != null) {
            Rect rect2 = new Rect(rect);
            this.i.getHeight();
            rect = rect2;
        }
        this.y.a(rect);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(RectF rectF) {
        q();
        if (this.i != null) {
            RectF rectF2 = new RectF(rectF);
            this.i.getHeight();
            rectF = rectF2;
        }
        this.y.a(rectF);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f2, float f3, float f4, Paint paint) {
        if (f4 < 50.0f) {
            GraphicsUtils.b(this, f2, f3, f4, paint);
        } else {
            q();
            this.y.a(f2, f3, f4, paint);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void g() {
        q();
        this.y.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void h() {
        q();
        this.y.a_();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void i() {
        q();
        this.y.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void j() {
        q();
        this.y.a_();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f2, float f3, float f4) {
        q();
        this.y.a(f2, f3, f4);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f2, float f3) {
        q();
        this.y.a(f2, f3);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f2, float f3, float f4, float f5) {
        q();
        this.y.b(f2, f3, f4, f5);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(float f2, float f3) {
        q();
        this.y.c(f2, f3);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final int k() {
        if (this.i != null) {
            return this.k;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        return gameEngine != null ? (int) gameEngine.screenWidth : this.k;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final int l() {
        if (this.i != null) {
            return this.l;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        return gameEngine != null ? (int) gameEngine.screenHeight : this.l;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(int i, int i2) {
        this.k = i;
        this.l = i2;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(C0009fo c0009fo) {
        synchronized (this.v) {
            this.v.add(c0009fo);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void n() {
        q();
        if (this.i != null && (this.y instanceof CanvasGraphicsRenderer)) {
            this.y.a(this.i);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final int a(Paint paint) {
        return (int) paint.getTextSize();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final int a(String str, Paint paint) {
        paint.getTextBounds(str, 0, str.length(), w);
        return w.left + w.width();
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final UnitTexture o() {
        return this.s;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void p() {
        throw new RuntimeException("writeImageToFile not yet supported");
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(Lock lock) {
        q();
        this.y.a(lock);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(Lock lock) {
        q();
        this.y.b(lock);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(String str, float f2, float f3, Paint paint) {
        q();
        this.y.a(str, f2, f3, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(Rect rect, Paint paint) {
        q();
        this.y.a(rect, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(RectF rectF, Paint paint) {
        q();
        this.y.b(rectF, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void b(float f2, float f3, float f4, Paint paint) {
        q();
        this.y.a(f2, f3, f4, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float[] fArr, int i, Paint paint) {
        q();
        this.y.b(fArr, 0, i, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(float f2, float f3, float f4, float f5, Paint paint) {
        q();
        this.y.a(f2, f3, f4, f5, paint);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void a(GraphicsOperation graphicsOperation) {
        this.y.a(graphicsOperation);
    }

    @Override // com.corrodinggames.rts.gameFramework.m.GraphicsContext
    public final void m() {
        q();
        a(PorterDuff.Mode.CLEAR);
    }
}
