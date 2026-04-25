package android.graphics;

import java.util.Locale;

/* JADX INFO: loaded from: game-lib.jar:android/graphics/Paint.class */
public class Paint {
    public int a;
    private ColorFilter r;
    private MaskFilter s;
    private PathEffect t;
    private Rasterizer u;
    private Shader v;
    private Typeface w;
    private Xfermode x;
    private boolean y;
    private float z;
    private float A;
    private Locale B;
    public boolean b;
    public float c;
    public float d;
    public float e;
    public int f;
    public int g;
    static final Style[] h = {Style.FILL, Style.STROKE, Style.FILL_AND_STROKE};
    static final Cap[] i = {Cap.BUTT, Cap.ROUND, Cap.SQUARE};
    static final Join[] j = {Join.MITER, Join.ROUND, Join.BEVEL};
    static final Align[] k = {Align.LEFT, Align.CENTER, Align.RIGHT};
    int l;
    Style m;
    int n;
    float o;
    Align p;
    float q;

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Paint$FontMetrics.class */
    public class FontMetrics {
        public float a;
        public float b;
        public float c;
        public float d;
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Paint$Style.class */
    public enum Style {
        FILL(0),
        STROKE(1),
        FILL_AND_STROKE(2);

        final int d;

        Style(int i) {
            this.d = i;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Paint$Cap.class */
    public enum Cap {
        BUTT(0),
        ROUND(1),
        SQUARE(2);

        final int d;

        Cap(int i) {
            this.d = i;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Paint$Join.class */
    public enum Join {
        MITER(0),
        ROUND(1),
        BEVEL(2);

        final int d;

        Join(int i) {
            this.d = i;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Paint$Align.class */
    public enum Align {
        LEFT(0),
        CENTER(1),
        RIGHT(2);

        final int d;

        Align(int i) {
            this.d = i;
        }
    }

    public Paint() {
        this(0);
    }

    public Paint(int i2) {
        this.g = 2;
        this.m = Style.FILL;
        this.o = 0.0f;
        this.q = 16.0f;
        this.a = o();
        a();
        a(i2 | 1280);
        this.A = 1.0f;
        this.z = 1.0f;
        a(Locale.getDefault());
    }

    public Paint(Paint paint) {
        this.g = 2;
        this.m = Style.FILL;
        this.o = 0.0f;
        this.q = 16.0f;
        this.a = d(paint.a);
        b(paint);
    }

    public void a() {
        e(this.a);
        a(1280);
        this.n = -1;
        this.m = Style.FILL;
        this.q = 16.0f;
        this.p = Align.LEFT;
        this.r = null;
        this.s = null;
        this.t = null;
        this.u = null;
        this.v = null;
        this.w = null;
        this.x = null;
        this.y = false;
        this.z = 1.0f;
        this.A = 1.0f;
        this.b = false;
        this.c = 0.0f;
        this.d = 0.0f;
        this.e = 0.0f;
        this.f = 0;
        this.g = 2;
        a(Locale.getDefault());
    }

    public void a(Paint paint) {
        if (this != paint) {
            a(this.a, paint.a);
            b(paint);
        }
    }

    private void b(Paint paint) {
        this.m = paint.m;
        this.n = paint.n;
        this.q = paint.q;
        this.p = paint.p;
        this.r = paint.r;
        this.s = paint.s;
        this.t = paint.t;
        this.u = paint.u;
        this.w = paint.w;
        this.x = paint.x;
        this.y = paint.y;
        this.z = paint.z;
        this.A = paint.A;
        this.b = paint.b;
        this.c = paint.c;
        this.d = paint.d;
        this.e = paint.e;
        this.f = paint.f;
        this.g = paint.g;
        this.B = paint.B;
        this.o = paint.o;
    }

    public int b() {
        return this.l;
    }

    public void a(int i2) {
        this.l = i2;
    }

    public final boolean c() {
        return (b() & 1) != 0;
    }

    public void a(boolean z) {
        if (z) {
            a(this.l | 1);
        } else {
            a(this.l & (-2));
        }
    }

    public void b(boolean z) {
    }

    public void c(boolean z) {
    }

    public void d(boolean z) {
    }

    public Style d() {
        return this.m;
    }

    public void a(Style style) {
        this.m = style;
    }

    public int e() {
        return this.n;
    }

    public void b(int i2) {
        this.n = i2;
    }

    public int f() {
        return Color.a(this.n);
    }

    public void c(int i2) {
        this.n = Color.a(i2, Color.b(this.n), Color.c(this.n), Color.d(this.n));
    }

    public void a(int i2, int i3, int i4, int i5) {
        b((i2 << 24) | (i3 << 16) | (i4 << 8) | i5);
    }

    public float g() {
        return this.o;
    }

    public void a(float f) {
        this.o = f;
    }

    public void a(Cap cap) {
        b(this.a, cap.d);
    }

    public ColorFilter h() {
        return this.r;
    }

    public ColorFilter a(ColorFilter colorFilter) {
        c(this.a, 0);
        this.r = colorFilter;
        return colorFilter;
    }

    public Xfermode a(Xfermode xfermode) {
        d(this.a, 0);
        this.x = xfermode;
        return xfermode;
    }

    public Typeface i() {
        return this.w;
    }

    public Typeface a(Typeface typeface) {
        this.w = typeface;
        return typeface;
    }

    public Align j() {
        return this.p;
    }

    public void a(Align align) {
        this.p = align;
    }

    public void a(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null");
        }
        if (locale.equals(this.B)) {
            return;
        }
        this.B = locale;
        a(this.a, locale.toString());
    }

    public float k() {
        return this.q;
    }

    public void b(float f) {
        this.q = f;
    }

    public float l() {
        return -this.q;
    }

    public float m() {
        return 0.0f;
    }

    public float a(FontMetrics fontMetrics) {
        return 0.0f;
    }

    public FontMetrics n() {
        FontMetrics fontMetrics = new FontMetrics();
        a(fontMetrics);
        return fontMetrics;
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Paint$FontMetricsInt.class */
    public class FontMetricsInt {
        public int a;
        public int b;
        public int c;
        public int d;
        public int e;

        public String toString() {
            return "FontMetricsInt: top=" + this.a + " ascent=" + this.b + " descent=" + this.c + " bottom=" + this.d + " leading=" + this.e;
        }
    }

    public float a(String str) {
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (str.length() == 0) {
            return 0.0f;
        }
        if (!this.y) {
            return (float) Math.ceil(a(str, this.g));
        }
        float fK = k();
        b(fK * this.z);
        float fA = a(str, this.g);
        b(fK);
        return (float) Math.ceil(fA * this.A);
    }

    private float a(String str, int i2) {
        return str.length() * k();
    }

    public int a(char[] cArr, int i2, int i3, float f, float[] fArr) {
        if (cArr == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (i2 < 0 || cArr.length - i2 < Math.abs(i3)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (cArr.length == 0 || i3 == 0) {
            return 0;
        }
        if (!this.y) {
            return a(cArr, i2, i3, f, this.g, fArr);
        }
        float fK = k();
        b(fK * this.z);
        int iA = a(cArr, i2, i3, f * this.z, this.g, fArr);
        b(fK);
        if (fArr != null) {
            fArr[0] = fArr[0] * this.A;
        }
        return iA;
    }

    private int a(char[] cArr, int i2, int i3, float f, int i4, float[] fArr) {
        float fK = k();
        if (f > fK * i3) {
            return i3;
        }
        if (f == 0.0f) {
            return 1;
        }
        int i5 = (int) (f / fK);
        if (i5 < 1) {
            i5 = 1;
        }
        return i5;
    }

    private int a(String str, boolean z, float f, int i2, float[] fArr) {
        return str.length();
    }

    public int a(CharSequence charSequence, int i2, int i3, boolean z, float f, float[] fArr) {
        int iA;
        if (charSequence == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((i2 | i3 | (i3 - i2) | (charSequence.length() - i3)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (charSequence.length() == 0 || i2 == i3) {
            return 0;
        }
        if (i2 == 0 && (charSequence instanceof String) && i3 == charSequence.length()) {
            return a((String) charSequence, z, f, fArr);
        }
        char[] cArrA = TemporaryBuffer.a(i3 - i2);
        a(charSequence, i2, i3, cArrA, 0);
        if (z) {
            iA = a(cArrA, 0, i3 - i2, f, fArr);
        } else {
            iA = a(cArrA, 0, -(i3 - i2), f, fArr);
        }
        TemporaryBuffer.a(cArrA);
        return iA;
    }

    public static void a(CharSequence charSequence, int i2, int i3, char[] cArr, int i4) {
        Class<?> cls = charSequence.getClass();
        if (cls == String.class) {
            ((String) charSequence).getChars(i2, i3, cArr, i4);
            return;
        }
        if (cls == StringBuffer.class) {
            ((StringBuffer) charSequence).getChars(i2, i3, cArr, i4);
            return;
        }
        if (cls == StringBuilder.class) {
            ((StringBuilder) charSequence).getChars(i2, i3, cArr, i4);
            return;
        }
        for (int i5 = i2; i5 < i3; i5++) {
            int i6 = i4;
            i4++;
            cArr[i6] = charSequence.charAt(i5);
        }
    }

    public int a(String str, boolean z, float f, float[] fArr) {
        if (str == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (str.length() == 0) {
            return 0;
        }
        if (!this.y) {
            return a(str, z, f, this.g, fArr);
        }
        float fK = k();
        b(fK * this.z);
        int iA = a(str, z, f * this.z, this.g, fArr);
        b(fK);
        if (fArr != null) {
            fArr[0] = fArr[0] * this.A;
        }
        return iA;
    }

    public int a(char[] cArr, int i2, int i3, float[] fArr) {
        if (cArr == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((i2 | i3) < 0 || i2 + i3 > cArr.length || i3 > fArr.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (cArr.length == 0 || i3 == 0) {
            return 0;
        }
        if (!this.y) {
            return a(this.a, cArr, i2, i3, this.g, fArr);
        }
        float fK = k();
        b(fK * this.z);
        int iA = a(this.a, cArr, i2, i3, this.g, fArr);
        b(fK);
        for (int i4 = 0; i4 < iA; i4++) {
            int i5 = i4;
            fArr[i5] = fArr[i5] * this.A;
        }
        return iA;
    }

    public void a(String str, int i2, int i3, Rect rect) {
        if ((i2 | i3 | (i3 - i2) | (str.length() - i3)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (rect == null) {
            throw new NullPointerException("need bounds Rect");
        }
        rect.a(0, 0, 0, (int) this.q);
    }

    protected void finalize() throws Throwable {
        try {
            f(this.a);
        } finally {
            super.finalize();
        }
    }

    private static int o() {
        return 0;
    }

    private static int d(int i2) {
        return 0;
    }

    private static void e(int i2) {
    }

    private static void a(int i2, int i3) {
    }

    private static void b(int i2, int i3) {
    }

    private static int c(int i2, int i3) {
        return 0;
    }

    private static int d(int i2, int i3) {
        return 0;
    }

    private static void a(int i2, String str) {
    }

    private static int a(int i2, char[] cArr, int i3, int i4, int i5, float[] fArr) {
        return 0;
    }

    private static void f(int i2) {
    }
}
