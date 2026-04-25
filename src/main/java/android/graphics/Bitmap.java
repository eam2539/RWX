package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import java.io.OutputStream;

/* JADX INFO: loaded from: game-lib.jar:android/graphics/Bitmap.class */
public final class Bitmap implements Parcelable {
    public  int a;
    private  boolean d;
    private boolean e;
    private int f;
    private int g;
    private boolean h;
    int b;
    private static volatile int i = -1;
    public static final Parcelable.Creator c = new Parcelable.Creator() { // from class: android.graphics.Bitmap.1
        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public Bitmap createFromParcel(Parcel parcel) {
            Bitmap bitmapNativeCreateFromParcel = Bitmap.nativeCreateFromParcel(parcel);
            if (bitmapNativeCreateFromParcel == null) {
                throw new RuntimeException("Failed to unparcel Bitmap");
            }
            return bitmapNativeCreateFromParcel;
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public Bitmap[] newArray(int i2) {
            return new Bitmap[i2];
        }
    };

    private static native Bitmap nativeCreate(int[] iArr, int i2, int i3, int i4, int i5, int i6, boolean z);

    private static native Bitmap nativeCopy(int i2, int i3, boolean z);

    private static native boolean nativeCompress(int i2, int i3, int i4, OutputStream outputStream, byte[] bArr);

    private static native void nativeErase(int i2, int i3);

    private static native int nativeConfig(int i2);

    private static native int nativeGetPixel(int i2, int i3, int i4, boolean z);

    private static native void nativeGetPixels(int i2, int[] iArr, int i3, int i4, int i5, int i6, int i7, int i8, boolean z);

    private static native void nativeSetPixel(int i2, int i3, int i4, int i5, boolean z);

    private static native void nativeSetPixels(int i2, int[] iArr, int i3, int i4, int i5, int i6, int i7, int i8, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public static native Bitmap nativeCreateFromParcel(Parcel parcel);

    private static native boolean nativeWriteToParcel(int i2, boolean z, int i3, Parcel parcel);

    private static native void nativeSetHasAlpha(int i2, boolean z);

    private void a(String str) {
        if (this.h) {
            throw new IllegalStateException(str);
        }
    }

    private static void b(int i2, int i3) {
        if (i2 < 0) {
            throw new IllegalArgumentException("x must be >= 0");
        }
        if (i3 < 0) {
            throw new IllegalArgumentException("y must be >= 0");
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Bitmap$Config.class */
    public enum Config {
        ALPHA_8(2),
        RGB_565(4),
        ARGB_4444(5),
        ARGB_8888(6);

        final int e;
        private static Config[] f = {null, null, ALPHA_8, null, RGB_565, ARGB_4444, ARGB_8888};

        Config(int i) {
            this.e = i;
        }

        static Config a(int i) {
            return f[i];
        }
    }

    public Bitmap a(Config config, boolean z) {
        a("Can't copy a recycled bitmap");
        Bitmap bitmapNativeCopy = nativeCopy(this.a, config.e, z);
        if (bitmapNativeCopy != null) {
            bitmapNativeCopy.e = this.e;
            bitmapNativeCopy.b = this.b;
        }
        return bitmapNativeCopy;
    }

    public static Bitmap a(int i2, int i3, Config config) {
        return a(i2, i3, config, true);
    }

    private static Bitmap a(int i2, int i3, Config config, boolean z) {
        return a(null, i2, i3, config, z);
    }

    private static Bitmap a(DisplayMetrics displayMetrics, int i2, int i3, Config config, boolean z) {
        if (i2 <= 0 || i3 <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        Bitmap bitmapNativeCreate = nativeCreate(null, 0, i2, i2, i3, config.e, true);
        if (displayMetrics != null) {
            bitmapNativeCreate.b = displayMetrics.densityDpi;
        }
        if (config == Config.ARGB_8888 && !z) {
            nativeErase(bitmapNativeCreate.a, -16777216);
            nativeSetHasAlpha(bitmapNativeCreate.a, z);
        }
        return bitmapNativeCreate;
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Bitmap$CompressFormat.class */
    public enum CompressFormat {
        JPEG(0),
        PNG(1),
        WEBP(2);

        final int d;

        CompressFormat(int i) {
            this.d = i;
        }
    }

    public boolean a(CompressFormat compressFormat, int i2, OutputStream outputStream) {
        a("Can't compress a recycled bitmap");
        if (outputStream == null) {
            throw new NullPointerException();
        }
        if (i2 < 0 || i2 > 100) {
            throw new IllegalArgumentException("quality must be 0..100");
        }
        return nativeCompress(this.a, compressFormat.d, i2, outputStream, new byte[4096]);
    }

    public final boolean a() {
        return this.d;
    }

    public final int b() {
        return this.f;
    }

    public final int c() {
        return this.g;
    }

    public final Config d() {
        return Config.a(nativeConfig(this.a));
    }

    public void a(boolean z) {
        nativeSetHasAlpha(this.a, z);
    }

    public void a(int i2) {
        a("Can't erase a recycled bitmap");
        if (!a()) {
            throw new IllegalStateException("cannot erase immutable bitmaps");
        }
        nativeErase(this.a, i2);
    }

    public int a(int i2, int i3) {
        a("Can't call getPixel() on a recycled bitmap");
        c(i2, i3);
        return nativeGetPixel(this.a, i2, i3, this.e);
    }

    public void a(int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7) {
        a("Can't call getPixels() on a recycled bitmap");
        if (i6 == 0 || i7 == 0) {
            return;
        }
        a(i4, i5, i6, i7, i2, i3, iArr);
        nativeGetPixels(this.a, iArr, i2, i3, i4, i5, i6, i7, this.e);
    }

    private void c(int i2, int i3) {
        b(i2, i3);
        if (i2 >= b()) {
            throw new IllegalArgumentException("x must be < bitmap.width()");
        }
        if (i3 >= c()) {
            throw new IllegalArgumentException("y must be < bitmap.height()");
        }
    }

    private void a(int i2, int i3, int i4, int i5, int i6, int i7, int[] iArr) {
        b(i2, i3);
        if (i4 < 0) {
            throw new IllegalArgumentException("width must be >= 0");
        }
        if (i5 < 0) {
            throw new IllegalArgumentException("height must be >= 0");
        }
        if (i2 + i4 > b()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        }
        if (i3 + i5 > c()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        }
        if (Math.abs(i7) < i4) {
            throw new IllegalArgumentException("abs(stride) must be >= width");
        }
        int i8 = i6 + ((i5 - 1) * i7);
        int length = iArr.length;
        if (i6 < 0 || i6 + i4 > length || i8 < 0 || i8 + i4 > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void a(int i2, int i3, int i4) {
        a("Can't call setPixel() on a recycled bitmap");
        if (!a()) {
            throw new IllegalStateException();
        }
        c(i2, i3);
        nativeSetPixel(this.a, i2, i3, i4, this.e);
    }

    public void b(int[] iArr, int i2, int i3, int i4, int i5, int i6, int i7) {
        a("Can't call setPixels() on a recycled bitmap");
        if (!a()) {
            throw new IllegalStateException();
        }
        if (i6 == 0 || i7 == 0) {
            return;
        }
        a(i4, i5, i6, i7, i2, i3, iArr);
        nativeSetPixels(this.a, iArr, i2, i3, i4, i5, i6, i7, this.e);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i2) {
        a("Can't parcel a recycled bitmap");
        if (!nativeWriteToParcel(this.a, this.d, this.b, parcel)) {
            throw new RuntimeException("native writeToParcel failed");
        }
    }
}
