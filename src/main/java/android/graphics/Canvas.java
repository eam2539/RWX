package android.graphics;

import android.graphics.PorterDuff;

/* JADX INFO: loaded from: game-lib.jar:android/graphics/Canvas.class */
public class Canvas {
    public void a(Bitmap bitmap) {
    }

    public int a() {
        return 0;
    }

    public void b() {
    }

    public void a(float f, float f2) {
    }

    public void b(float f, float f2) {
    }

    public void a(float f, float f2, float f3, float f4) {
    }

    public void a(float f, float f2, float f3) {
    }

    public boolean a(RectF rectF) {
        return false;
    }

    public boolean a(Rect rect) {
        return false;
    }

    public void a(int i) {
    }

    public void a(int i, PorterDuff.Mode mode) {
    }

    public void a(float[] fArr, int i, int i2, Paint paint) {
    }

    public void a(float f, float f2, float f3, float f4, Paint paint) {
    }

    public void a(RectF rectF, Paint paint) {
    }

    public void a(Rect rect, Paint paint) {
    }

    public void a(float f, float f2, float f3, Paint paint) {
    }

    public void a(Bitmap bitmap, float f, float f2, Paint paint) {
    }

    public void a(Bitmap bitmap, Rect rect, RectF rectF, Paint paint) {
    }

    public void a(Bitmap bitmap, Rect rect, Rect rect2, Paint paint) {
    }

    public void a(char[] cArr, int i, int i2, float f, float f2, Paint paint) {
    }

    public void a(String str, float f, float f2, Paint paint) {
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Canvas$EdgeType.class */
    public enum EdgeType {
        BW(0),
        AA(1);

        public final int c;

        EdgeType(int i) {
            this.c = i;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Canvas$VertexMode.class */
    public enum VertexMode {
        TRIANGLES(0),
        TRIANGLE_STRIP(1),
        TRIANGLE_FAN(2);

        public final int d;

        VertexMode(int i) {
            this.d = i;
        }
    }
}
