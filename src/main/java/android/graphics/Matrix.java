package android.graphics;

/* JADX INFO: loaded from: game-lib.jar:android/graphics/Matrix.class */
public class Matrix {
    public static Matrix a = new Matrix() { // from class: android.graphics.Matrix.1
    };
    public int b = a(0);

    public boolean equals(Object obj) {
        if (obj instanceof Matrix) {
            return a(this.b, ((Matrix) obj).b);
        }
        return false;
    }

    public int hashCode() {
        return 44;
    }

    /* JADX INFO: loaded from: game-lib.jar:android/graphics/Matrix$ScaleToFit.class */
    public enum ScaleToFit {
        FILL(0),
        START(1),
        CENTER(2),
        END(3);

        final int e;

        ScaleToFit(int i) {
            this.e = i;
        }
    }

    public void a(float[] fArr) {
        if (fArr.length < 9) {
            throw new ArrayIndexOutOfBoundsException();
        }
        a(this.b, fArr);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("Matrix{");
        a(sb);
        sb.append('}');
        return sb.toString();
    }

    public void a(StringBuilder sb) {
        float[] fArr = new float[9];
        a(fArr);
        sb.append('[');
        sb.append(fArr[0]);
        sb.append(", ");
        sb.append(fArr[1]);
        sb.append(", ");
        sb.append(fArr[2]);
        sb.append("][");
        sb.append(fArr[3]);
        sb.append(", ");
        sb.append(fArr[4]);
        sb.append(", ");
        sb.append(fArr[5]);
        sb.append("][");
        sb.append(fArr[6]);
        sb.append(", ");
        sb.append(fArr[7]);
        sb.append(", ");
        sb.append(fArr[8]);
        sb.append(']');
    }

    protected void finalize() throws Throwable {
        try {
            b(this.b);
        } finally {
            super.finalize();
        }
    }

    private static int a(int i) {
        return 0;
    }

    private static void a(int i, float[] fArr) {
    }

    private static boolean a(int i, int i2) {
        return false;
    }

    private static void b(int i) {
    }
}
