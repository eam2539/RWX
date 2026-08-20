package com.corrodinggames.rts.gameFramework;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f.class */
public final class Utility {
    static final Random a = new Random();
    static final Random b = new Random();
    public static final PointF c = new PointF();
    private static final byte[] j = new byte[1001];
    static final PointF d;
    static final PointF e;
    static final PointF f;
    static final PointF g;
    static final PointF h;
    private static final char[] k;
    private static final float[] l;
    private static final float[] m;
    private static final float[] n;
    private static final float[] o;
    private static final float[] p;
    private static final float[] q;
    private static final float[] r;
    private static final float[] s;
    static int i;
    private static final float[] t;
    private static final float[] u;

    static {
        for (int i2 = 0; i2 < j.length; i2++) {
            j[i2] = (byte) StrictMath.round(squareRoot(i2));
        }
        d = new PointF();
        e = new PointF();
        f = new PointF();
        g = new PointF();
        h = new PointF();
        k = new char[36];
        for (int i3 = 0; i3 < 10; i3++) {
            k[i3] = (char) (48 + i3);
        }
        for (int i4 = 10; i4 < 36; i4++) {
            k[i4] = (char) ((97 + i4) - 10);
        }
        l = new float[1025];
        m = new float[1025];
        n = new float[1025];
        o = new float[1025];
        p = new float[1025];
        q = new float[1025];
        r = new float[1025];
        s = new float[1025];
        for (int i5 = 0; i5 <= 1024; i5++) {
            l[i5] = (float) ((StrictMath.atan(i5 / 1024.0f) * 3.1415927410125732d) / 3.141592653589793d);
            m[i5] = 1.5707964f - l[i5];
            n[i5] = -l[i5];
            o[i5] = l[i5] - 1.5707964f;
            p[i5] = 3.1415927f - l[i5];
            q[i5] = l[i5] + 1.5707964f;
            r[i5] = l[i5] - 3.1415927f;
            s[i5] = (-1.5707964f) - l[i5];
        }
        i = 0;
        t = new float[8192];
        u = new float[8192];
        for (int i6 = 0; i6 < 8192; i6++) {
            t[i6] = (float) StrictMath.sin(((i6 + 0.5f) / 8192.0f) * 6.2831855f);
            u[i6] = (float) StrictMath.cos(((i6 + 0.5f) / 8192.0f) * 6.2831855f);
        }
    }

    /* JADX INFO: renamed from: a */
    public static final void resetSharedRandomSeed() {
        b.setSeed(0L);
    }

    /* JADX INFO: renamed from: a */
    public static final int getDeterministicRandomInt(BaseUnit baseUnit, int i2, int i3) {
        return getDeterministicRandomInt((GameObject) baseUnit, i2, i3, 0);
    }

    /* JADX INFO: renamed from: a */
    public static final float getDeterministicRandomFloat(BaseUnit baseUnit, float f2, float f3, int i2) {
        if (baseUnit == null) {
            return getDeterministicRandomIntInRange((int) (f2 * 1000.0f), (int) (f3 * 1000.0f), i2) * 0.001f;
        }
        return getDeterministicRandomInt((GameObject) baseUnit, (int) (f2 * 1000.0f), (int) (f3 * 1000.0f), i2) * 0.001f;
    }

    /* JADX INFO: renamed from: b */
    public static final float getDeterministicRandomFloatForUnit(BaseUnit baseUnit, float f2, float f3, int i2) {
        return getDeterministicRandomInt((GameObject) baseUnit, (int) (f2 * 1000.0f), (int) (f3 * 1000.0f), i2) * 0.001f;
    }

    /* JADX INFO: renamed from: a */
    public static final int getDeterministicRandomInt(GameObject gameObject, int i2, int i3, int i4) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (i2 >= i3) {
            if (i2 > i3) {
                GameEngine.logColored("min>max");
            }
            return i2;
        }
        int i5 = gameEngine.currentTick + 1;
        int i6 = (int) (((int) (((int) (((int) (((int) (((long) gameEngine.globalSeed) + (gameObject.objectId * 1313))) + (gameObject.posX * 13.0f))) + (gameObject.posY * 13.0f))) + (gameObject.posX * 130.0f))) + (gameObject.posY * 130.0f));
        if (gameObject instanceof BaseUnit) {
            int i7 = ((BaseUnit) gameObject).unitCounter;
            i6 = i6 + (i7 * 13131) + (i7 * i5);
        }
        int i8 = ((((int) (((long) (i6 + ((i4 * 133) * i3))) + ((((long) i4) * gameObject.objectId) + ((long) i4)))) + (i4 * (i5 * 1313))) + ((i5 * 13) + (i5 % 10))) % (i3 - i2);
        if (i8 < 0) {
            i8 = -i8;
        }
        return i8 + i2;
    }

    /* JADX INFO: renamed from: a */
    public static final float getDeterministicRandomFloat(float f2, float f3, int i2) {
        return getDeterministicRandomIntInRange((int) (f2 * 100.0f), (int) (f3 * 100.0f), i2) / 100.0f;
    }

    /* JADX INFO: renamed from: b */
    public static final float getRandomFloat(float f2, float f3, int i2) {
        return getDeterministicRandomIntInRange((int) (f2 * 1000.0f), (int) (f3 * 1000.0f), i2) / 1000.0f;
    }

    /* JADX INFO: renamed from: a */
    public static final int getDeterministicRandomIntInRange(int i2, int i3, int i4) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (i2 >= i3) {
            if (i2 > i3) {
                GameEngine.logColored("min>max");
            }
            return i2;
        }
        int i5 = i3 - i2;
        int i6 = ((((gameEngine.globalSeed + ((i4 * 133333333) * i5)) + (i4 * 13131313)) + (i4 * (gameEngine.currentTick * 13131313))) + ((gameEngine.currentTick * 1313131313) + (gameEngine.currentTick % 10))) % i5;
        if (i6 < 0) {
            i6 = -i6;
        }
        int i7 = i6 + i2;
        if (i7 < i2 || i7 > i3) {
            GameEngine.logColored("notRandInt number not in range: " + i7 + " min:" + i2 + " max:" + i3);
        }
        return i7;
    }

    /* JADX INFO: renamed from: a */
    public static String formatCurrentDate(String str) {
        return new SimpleDateFormat(str).format(Calendar.getInstance().getTime());
    }

    /* JADX INFO: renamed from: a */
    public static final void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[8192];
        while (true) {
            int i2 = inputStream.read(bArr);
            if (i2 != -1) {
                outputStream.write(bArr, 0, i2);
            } else {
                return;
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public static final String readStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[8192];
        while (true) {
            try {
                int i2 = inputStream.read(bArr);
                if (i2 == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, i2);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return byteArrayOutputStream.toString();
        }
        byteArrayOutputStream.close();
        inputStream.close();
        return byteArrayOutputStream.toString();
    }

    /* JADX INFO: renamed from: a */
    public static final float squareRoot(float f2) {
        return (float) StrictMath.sqrt(f2);
    }

    /* JADX INFO: renamed from: a */
    public static final int fastSquareRootInt(int i2) {
        if (i2 > 1000 || i2 < 0) {
            return StrictMath.round(squareRoot(i2));
        }
        return j[i2];
    }

    /* JADX INFO: renamed from: a */
    public static final float moveTowardsZero(float f2, float f3) {
        if (f2 > f3) {
            return f2 - f3;
        }
        if (f2 < (-f3)) {
            return f2 + f3;
        }
        return 0.0f;
    }

    /* JADX INFO: renamed from: a */
    public static final float distanceSq(float f2, float f3, float f4) {
        if (f2 > f3 + f4) {
            return f2 - f4;
        }
        if (f2 < f3 - f4) {
            return f2 + f4;
        }
        return f3;
    }

    /* JADX INFO: renamed from: b */
    public static final float clamp(float f2, float f3) {
        if (f2 > f3) {
            return f3;
        }
        if (f2 < (-f3)) {
            return -f3;
        }
        return f2;
    }

    /* JADX INFO: renamed from: b */
    public static final float clampTo255(float f2, float f3, float f4) {
        if (f2 > f4) {
            return f4;
        }
        if (f2 < f3) {
            return f3;
        }
        return f2;
    }

    /* JADX INFO: renamed from: b */
    public static final int distance(int i2, int i3, int i4) {
        if (i2 > i4) {
            return i4;
        }
        if (i2 < i3) {
            return i3;
        }
        return i2;
    }

    /* JADX INFO: renamed from: b */
    public static final int clampTo255(int i2) {
        if (i2 > 255) {
            return 255;
        }
        if (i2 < 0) {
            return 0;
        }
        return i2;
    }

    /* JADX INFO: renamed from: a */
    public static final void rotatePoint(float f2, float f3, float f4, PointF pointF) {
        float fFastSin = fastSin(f4);
        float fFastCos = fastCos(f4);
        pointF.x -= f2;
        pointF.y -= f3;
        float f5 = (pointF.x * fFastCos) - (pointF.y * fFastSin);
        float f6 = (pointF.x * fFastSin) + (pointF.y * fFastCos);
        pointF.x = f5 + f2;
        pointF.y = f6 + f3;
    }

    /* JADX INFO: renamed from: a */
    public static final float distanceSq(float f2, float f3, float f4, float f5) {
        return ((f2 - f4) * (f2 - f4)) + ((f3 - f5) * (f3 - f5));
    }

    /* JADX INFO: renamed from: b */
    public static final float distance(float f2, float f3, float f4, float f5) {
        return (float) StrictMath.sqrt(((f2 - f4) * (f2 - f4)) + ((f3 - f5) * (f3 - f5)));
    }

    /* JADX INFO: renamed from: c */
    public static final int distanceInt(float f2, float f3, float f4, float f5) {
        return fastSquareRootInt((int) (((f2 - f4) * (f2 - f4)) + ((f3 - f5) * (f3 - f5))));
    }

    /* JADX INFO: renamed from: a */
    public static final int chebyshevDistance(int i2, int i3, int i4, int i5) {
        int i6 = i2 - i4;
        int i7 = i3 - i5;
        if (i6 < 0) {
            i6 = -i6;
        }
        if (i7 < 0) {
            i7 = -i7;
        }
        return i6 > i7 ? i6 : i7;
    }

    /* JADX INFO: renamed from: a */
    public static final float normalizeAngle(float f2, boolean z) {
        if (!z) {
            while (true) {
                if (f2 <= 180.0f && f2 >= -180.0f) {
                    break;
                }
                if (f2 > 180.0f) {
                    f2 -= 360.0f;
                }
                if (f2 < -180.0f) {
                    f2 += 360.0f;
                }
            }
        } else {
            while (true) {
                if (f2 <= 360.0f && f2 >= 0.0f) {
                    break;
                }
                if (f2 > 360.0f) {
                    f2 -= 360.0f;
                }
                if (f2 < 0.0f) {
                    f2 += 360.0f;
                }
            }
        }
        return f2;
    }

    /* JADX INFO: renamed from: c */
    public static final float rotateTowardsAngle(float f2, float f3, float f4) {
        float f5 = (f3 % 360.0f) - (f2 % 360.0f);
        if (f5 > 180.0f) {
            f5 -= 360.0f;
        }
        if (f5 < -180.0f) {
            f5 += 360.0f;
        }
        return f5 > f4 ? f4 : f5 < (-f4) ? -f4 : f5;
    }

    /* JADX INFO: renamed from: d */
    public static final float getAngleBetweenPoints(float f2, float f3, float f4, float f5) {
        return radiansToDegrees(fastAtan2(f5 - f3, f4 - f2));
    }

    /* JADX INFO: renamed from: a */
    public static final boolean lineSegmentsIntersect(PointF pointF, PointF pointF2, PointF pointF3, PointF pointF4) {
        float f2 = ((pointF4.y - pointF3.y) * (pointF2.x - pointF.x)) - ((pointF4.x - pointF3.x) * (pointF2.y - pointF.y));
        float f3 = ((pointF4.x - pointF3.x) * (pointF.y - pointF3.y)) - ((pointF4.y - pointF3.y) * (pointF.x - pointF3.x));
        float f4 = ((pointF2.x - pointF.x) * (pointF.y - pointF3.y)) - ((pointF2.y - pointF.y) * (pointF.x - pointF3.x));
        if (f2 != 0.0f) {
            float f5 = f3 / f2;
            float f6 = f4 / f2;
            return f5 >= 0.0f && f5 <= 1.0f && f6 >= 0.0f && f6 <= 1.0f;
        }
        if (f3 != 0.0f || f4 == 0.0f) {
            return false;
        }
        return false;
    }

    /* JADX INFO: renamed from: c */
    public static final float randomFloatInRange(float f2, float f3) {
        return (a.nextFloat() * (f3 - f2)) + f2;
    }

    /* JADX INFO: renamed from: d */
    public static final float randomRepairTargetOffset(float f2, float f3) {
        return (a.nextFloat() * (f3 - f2)) + f2;
    }

    /* JADX INFO: renamed from: c */
    public static final int getRandomInt(int i2) {
        if (i2 == 0) {
            return 0;
        }
        return a.nextInt(i2);
    }

    /* JADX INFO: renamed from: a */
    public static int getRandomIntInRange(int i2, int i3) {
        int iNextInt;
        if (i3 == i2) {
            iNextInt = 0;
        } else {
            iNextInt = a.nextInt((i3 - i2) + 1);
        }
        return i2 + iNextInt;
    }

    /* JADX INFO: renamed from: a */
    public static final void normalizeRect(Rect rect) {
        if (rect.c < rect.a) {
            int i2 = rect.c;
            rect.c = rect.a;
            rect.a = i2;
        }
        if (rect.d < rect.b) {
            int i3 = rect.d;
            rect.d = rect.b;
            rect.b = i3;
        }
    }

    /* JADX INFO: renamed from: a */
    public static final void normalizeRect(RectF rectF) {
        if (rectF.c < rectF.a) {
            float f2 = rectF.c;
            rectF.c = rectF.a;
            rectF.a = f2;
        }
        if (rectF.d < rectF.b) {
            float f3 = rectF.d;
            rectF.d = rectF.b;
            rectF.b = f3;
        }
    }

    /* JADX INFO: renamed from: d */
    public static final PointF createPointWithOffset(float f2, float f3, float f4) {
        h.a(f2, f3 - f4);
        return h;
    }

    /* JADX INFO: renamed from: b */
    public static final float radiansToDegrees(float f2) {
        return f2 * 57.29578f;
    }

    /* JADX INFO: renamed from: e */
    public static final float pow(float f2, float f3) {
        return (float) StrictMath.pow(f2, f3);
    }

    /* JADX INFO: renamed from: a */
    public static final double abs(double d2) {
        return d2 < 0.0d ? -d2 : d2;
    }

    /* JADX INFO: renamed from: c */
    public static final float abs(float f2) {
        return f2 < 0.0f ? -f2 : f2;
    }

    /* JADX INFO: renamed from: d */
    public static final int abs(int i2) {
        return i2 < 0 ? -i2 : i2;
    }

    /* JADX INFO: renamed from: b */
    public static final int max(int i2, int i3) {
        return i2 > i3 ? i2 : i3;
    }

    /* JADX INFO: renamed from: c */
    public static final int min(int i2, int i3) {
        return i2 < i3 ? i2 : i3;
    }

    /* JADX INFO: renamed from: f */
    public static final float max(float f2, float f3) {
        return f2 > f3 ? f2 : f3;
    }

    /* JADX INFO: renamed from: g */
    public static final float min(float f2, float f3) {
        return f2 < f3 ? f2 : f3;
    }

    /* JADX INFO: renamed from: h */
    public static final boolean isClose(float f2, float f3) {
        return abs(f2 - f3) < 0.05f;
    }

    /* JADX INFO: renamed from: a */
    public static final double min(double d2, double d3) {
        return d2 < d3 ? d2 : d3;
    }

    /* JADX INFO: renamed from: e */
    public static boolean isDifferenceWithinTolerance(float f2, float f3, float f4) {
        if (abs(abs(f2) - abs(f3)) < f4) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: d */
    public static float round(float f2) {
        return (int) (f2 + 0.5f);
    }

    /* JADX INFO: renamed from: e */
    public static float ceil(float f2) {
        return (float) StrictMath.ceil(f2);
    }

    /* JADX INFO: renamed from: f */
    public static final int max(float f2) {
        if (f2 > 0.0f) {
            return (int) f2;
        }
        if (f2 < 0.0f) {
            return ((int) f2) - 1;
        }
        return 0;
    }

    /* JADX INFO: renamed from: a */
    public static void grow(RectF rectF, float f2) {
        rectF.a -= f2;
        rectF.b -= f2;
        rectF.c += f2;
        rectF.d += f2;
    }

    /* JADX INFO: renamed from: a */
    public static void grow(Rect rect, float f2) {
        rect.a = (int) (rect.a - f2);
        rect.b = (int) (rect.b - f2);
        rect.c = (int) (rect.c + f2);
        rect.d = (int) (rect.d + f2);
    }

    /* JADX INFO: renamed from: b */
    public static void expandRectForTouchTarget(Rect rect, float f2) {
        rect.a = (int) (rect.a - f2);
        rect.b = (int) (rect.b - f2);
        rect.c = (int) (rect.c + (f2 * 2.0f));
        rect.d = (int) (rect.d + (f2 * 2.0f));
    }

    /* JADX INFO: renamed from: e */
    public static String getRandomAlphanumericString(int i2) {
        StringBuilder sb = new StringBuilder();
        for (int i3 = 0; i3 < i2; i3++) {
            sb.append(k[a.nextInt(k.length)]);
        }
        return sb.toString();
    }

    /* JADX INFO: renamed from: b */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /* JADX INFO: renamed from: a */
    public static String booleanToString(boolean z) {
        return z ? "true" : "false";
    }

    /* JADX INFO: renamed from: b */
    public static String formatNumber(double d2) {
        if (d2 == ((int) d2)) {
            return VariableScope.nullOrMissingString + ((int) d2);
        }
        return VariableScope.nullOrMissingString + d2;
    }

    /* JADX INFO: renamed from: g */
    public static String padString(float f2) {
        return padString(f2, 2);
    }

    /* JADX INFO: renamed from: c */
    public static String formatNumberWithTwoDecimals(double d2) {
        if (d2 == ((int) d2)) {
            return VariableScope.nullOrMissingString + ((int) d2);
        }
        return formatDouble(d2, 2);
    }

    /* JADX INFO: renamed from: a */
    public static String padString(float f2, int i2) {
        if (f2 == ((int) f2)) {
            return VariableScope.nullOrMissingString + ((int) f2);
        }
        return formatDouble(f2, i2);
    }

    /* JADX INFO: renamed from: a */
    public static String padString(double d2, int i2) {
        if (d2 == ((int) d2)) {
            return VariableScope.nullOrMissingString + ((int) d2);
        }
        return formatDouble(d2, i2);
    }

    /* JADX INFO: renamed from: h */
    public static String formatSeconds(float f2) {
        if (((int) (f2 * 10.0f)) == ((int) f2) * 10) {
            return VariableScope.nullOrMissingString + ((int) f2) + "s";
        }
        return formatDouble(f2, 1) + "s";
    }

    /* JADX INFO: renamed from: b */
    public static String formatDouble(double d2, int i2) {
        String str = VariableScope.nullOrMissingString + d2;
        int iIndexOf = str.indexOf(".");
        if (iIndexOf == -1) {
            return str;
        }
        if (str.indexOf("E") != -1) {
            return String.format("%." + i2 + "f", Double.valueOf(d2));
        }
        int length = iIndexOf + i2 + 1;
        if (length > str.length()) {
            length = str.length();
        }
        return str.substring(0, length);
    }

    /* JADX INFO: renamed from: a */
    public static String truncateToLength(String str, int i2) {
        if (str == null) {
            return null;
        }
        if (str.length() < i2) {
            return str;
        }
        return str.substring(0, Math.min(str.length(), i2));
    }

    /* JADX INFO: renamed from: b */
    public static String truncateWithEllipsis(String str, int i2) {
        if (str == null) {
            return null;
        }
        if (str.length() < i2) {
            return str;
        }
        int i3 = i2 - 3;
        if (i3 < 1) {
            i3 = 1;
        }
        return str.substring(0, Math.min(str.length(), i3)) + "...";
    }

    /* JADX INFO: renamed from: b */
    public static String md5Hex(String str) {
        try {
            byte[] bArrDigest = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(bArrDigest.length * 2);
            for (byte b2 : bArrDigest) {
                int i2 = b2 & 255;
                if (i2 < 16) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(i2));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e2) {
            throw new RuntimeException("UTF-8 should be supported", e2);
        } catch (NoSuchAlgorithmException e3) {
            throw new RuntimeException("MD5 should be supported", e3);
        }
    }

    /* JADX INFO: renamed from: c */
    public static String sha256ShortHash(String str) {
        return truncateToLength(toHexString(sha256Bytes(str)), 14);
    }

    /* JADX INFO: renamed from: d */
    public static String sha256Fingerprint(String str) {
        return truncateToLength(toHexString(sha256Bytes(str)), 4);
    }

    /* JADX INFO: renamed from: c */
    public static String repeatHash(String str, int i2) {
        String hexString = toHexString(sha256Bytes(str));
        for (int i3 = 0; i3 < i2; i3++) {
            hexString = toHexString(sha256Bytes(hexString));
        }
        return hexString;
    }

    /* JADX INFO: renamed from: e */
    public static String sha256Hex(String str) {
        return toHexString(sha256Bytes(str));
    }

    /* JADX INFO: renamed from: f */
    static byte[] sha256Bytes(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            return messageDigest.digest(str.getBytes());
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    /* JADX INFO: renamed from: a */
    static String toHexString(byte[] bArr) {
        return String.format("%0" + (bArr.length * 2) + "X", new BigInteger(1, bArr));
    }

    /* JADX INFO: renamed from: b */
    public static String sha256HexString(byte[] bArr) {
        return toHexString(sha256Bytes(bArr));
    }

    /* JADX INFO: renamed from: c */
    static byte[] sha256Bytes(byte[] bArr) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            return messageDigest.digest(bArr);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    /* JADX INFO: renamed from: c */
    public static int getCpuCoreCount() {
        int iAvailableProcessors;
        int length = 1;
        try {
            File file = new File("/sys/devices/system/cpu/");
            if (file.exists()) {
                length = file.listFiles(new a()).length;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            length = 1;
        }
        if (length == 1 && (iAvailableProcessors = Runtime.getRuntime().availableProcessors()) > 1) {
            length = iAvailableProcessors;
        }
        return length;
    }

    /* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f$a */
    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f$a.class */
    static class a implements FileFilter {
        a() {
        }

        @Override // java.io.FileFilter
        public boolean accept(File file) {
            if (file != null && Pattern.matches("cpu[0-9]+", file.getName())) {
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: renamed from: a */
    public static void copyByteArray(byte[] bArr, byte[] bArr2) {
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
    }

    /* JADX INFO: renamed from: f */
    public static float lerp(float f2, float f3, float f4) {
        return f2 + ((f3 - f2) * f4);
    }

    /* JADX INFO: renamed from: i */
    public static float easeInOutQuad(float f2) {
        float f3 = f2 - 1.0f;
        float f4 = f2 * 2.0f;
        return f4 < 1.0f ? f2 * f4 : 1.0f - ((f3 * f3) * 2.0f);
    }

    /* JADX INFO: renamed from: a */
    public static int lerpColor(int i2, int i3, float f2) {
        return Color.a((int) lerp(Color.a(i2), Color.a(i3), f2), (int) lerp(Color.b(i2), Color.b(i3), f2), (int) lerp(Color.c(i2), Color.c(i3), f2), (int) lerp(Color.d(i2), Color.d(i3), f2));
    }

    /* JADX INFO: renamed from: d */
    public static String repeat(String str, int i2) {
        String str2 = VariableScope.nullOrMissingString;
        for (int i3 = 0; i3 <= i2; i3++) {
            str2 = str2 + str;
        }
        return str2;
    }

    /* JADX INFO: renamed from: e */
    public static String padRightWithSpaces(String str, int i2) {
        for (int length = str.length(); length < i2; length++) {
            str = str + " ";
        }
        return str;
    }

    /* JADX INFO: renamed from: a */
    public static String padLeft(String str, int i2, String str2) {
        for (int length = str.length(); length < i2; length++) {
            str = str2 + str;
        }
        return str;
    }

    /* JADX INFO: renamed from: f */
    public static String padRight(String str, int i2) {
        return String.format("%1$-" + i2 + "s", str);
    }

    /* JADX INFO: renamed from: a */
    public static String getFieldNameByValue(Class cls, int i2) {
        try {
            for (Field field : cls.getFields()) {
                if (field.getInt(null) == i2) {
                    return field.getName();
                }
            }
            return null;
        } catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        } catch (IllegalArgumentException e3) {
            throw new RuntimeException(e3);
        }
    }

    /* JADX INFO: renamed from: f */
    public static String getResourcePath(int i2) {
        String strCountChars = getFieldNameByValue(R.drawable.class, i2);
        if (strCountChars != null) {
            return FileHelper.findFileExtension("res/drawable", strCountChars);
        }
        String strCountChars2 = getFieldNameByValue(R.raw.class, i2);
        if (strCountChars2 != null) {
            return FileHelper.findFileExtension("res/raw", strCountChars2);
        }
        return null;
    }

    /* JADX INFO: renamed from: g */
    public static final String formatByteSize(int i2) {
        if (-1000 < i2 && i2 < 1000) {
            return i2 + " B";
        }
        int i3 = 0;
        while (i3 < "kMGTPE".length() && (i2 <= -999950 || i2 >= 999950)) {
            i2 /= 1000;
            i3++;
        }
        return String.format("%.1f %cB", Double.valueOf(((double) i2) / 1000.0d), Character.valueOf("kMGTPE".charAt(i3)));
    }

    /* JADX INFO: renamed from: h */
    public static final String toHexString(int i2) {
        return String.format("#%06X", Integer.valueOf(16777215 & i2));
    }

    /* JADX INFO: renamed from: g */
    public static final String getFileNameWithoutExtension(String str) {
        if (str == null) {
            return null;
        }
        return new File(str).getName().replaceFirst("[.][^.]+$", VariableScope.nullOrMissingString);
    }

    /* JADX INFO: renamed from: h */
    public static final String getParentPath(String str) {
        if (str.contains("\\")) {
            str = str.replace('\\', '/');
        }
        return new File(str).getParent();
    }

    /* JADX INFO: renamed from: a */
    public static final boolean rectanglesOverlap(Rect rect, RectF rectF) {
        return ((float) rect.a) < rectF.c && rectF.a < ((float) rect.c) && ((float) rect.b) < rectF.d && rectF.b < ((float) rect.d);
    }

    /* JADX INFO: renamed from: a */
    public static final boolean rectanglesOverlap(RectF rectF, RectF rectF2) {
        return rectF.a < rectF2.c && rectF2.a < rectF.c && rectF.b < rectF2.d && rectF2.b < rectF.d;
    }

    /* JADX INFO: renamed from: b */
    public static final int packArgb(int i2, int i3, int i4, int i5) {
        return (i2 << 24) | (i3 << 16) | (i4 << 8) | i5;
    }

    /* JADX INFO: renamed from: a */
    public static final long elapsedMilliseconds(long j2, long j3) {
        return (j3 - j2) / 1000000;
    }

    /* JADX INFO: renamed from: a */
    public static final int countOccurrences(String str, char c2) {
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            if (str.charAt(i3) == c2) {
                i2++;
            }
        }
        return i2;
    }

    /* JADX INFO: renamed from: i */
    public static final String escapeHtml(String str) {
        return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("${", "$ {");
    }

    /* JADX INFO: renamed from: a */
    public static String readFileToString(File file) {
        int i2;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                byte[] bArr = new byte[(int) file.length()];
                int length = bArr.length;
                int i3 = 0;
                while (i3 < length && (i2 = fileInputStream.read(bArr, i3, length - i3)) != -1) {
                    i3 += i2;
                }
                String str = new String(bArr, Charset.forName("UTF-8"));
                fileInputStream.close();
                return str;
            } catch (Throwable th) {
                fileInputStream.close();
                throw th;
            }
        } catch (FileNotFoundException e2) {
            throw new RuntimeException(e2);
        } catch (IOException e3) {
            throw new RuntimeException(e3);
        }
    }

    /* JADX INFO: renamed from: b */
    public static String readStreamToStringUtf8(InputStream inputStream) throws IOException {
        int i2;
        try {
            try {
                byte[] bArr = new byte[inputStream.available()];
                int length = bArr.length;
                int i3 = 0;
                while (i3 < length && (i2 = inputStream.read(bArr, i3, length - i3)) != -1) {
                    i3 += i2;
                }
                String str = new String(bArr, Charset.forName("UTF-8"));
                inputStream.close();
                return str;
            } catch (FileNotFoundException e2) {
                throw new RuntimeException(e2);
            } catch (IOException e3) {
                throw new RuntimeException(e3);
            }
        } catch (Throwable th) {
            inputStream.close();
            throw th;
        }
    }

    /* JADX INFO: renamed from: a */
    public static final String stackTraceToString(Exception exc) {
        StringWriter stringWriter = new StringWriter();
        exc.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /* JADX INFO: renamed from: b */
    public static final String formatExceptionMessage(Exception exc) {
        return formatExceptionMessage(exc, false);
    }

    /* JADX INFO: renamed from: a */
    public static final String formatExceptionMessage(Exception exc, boolean z) {
        Throwable th;
        String message = exc.getMessage();
        if (message == null) {
            message = exc.getClass().getName();
        } else {
            boolean z2 = false;
            if (exc instanceof NumberFormatException) {
                z2 = true;
            }
            if (exc instanceof ArrayIndexOutOfBoundsException) {
                z2 = true;
            }
            if (z2 || z) {
                message = exc.getClass().getName() + " - " + message;
            }
        }
        if (message != null && message.startsWith("java.io.IOException")) {
            message = message.substring("java.io.".length());
        }
        Throwable th2 = exc;
        while (true) {
            th = th2;
            if (th == null) {
                break;
            }
            Throwable cause = th.getCause();
            if (cause == null || cause == exc || cause == th) {
                break;
            }
            th2 = cause;
        }
        if (th != null && th != exc) {
            String message2 = th.getMessage();
            if (message2 == null) {
                message2 = th.getClass().getName();
            }
            boolean z3 = true;
            if (message2.equals(message)) {
                z3 = false;
            }
            if (message != null && message.contains(message2)) {
                z3 = false;
            }
            if (z3) {
                message = message + " caused by (" + message2 + ")";
            }
        }
        return message;
    }

    /* JADX INFO: renamed from: j */
    public static String removeTrailingNewline(String str) {
        if (str.endsWith("\n")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    /* JADX INFO: renamed from: a */
    public static String removeSuffix(String str, String str2) {
        if (str.endsWith(str2)) {
            return str.substring(0, str.length() - str2.length());
        }
        return str;
    }

    /* JADX INFO: renamed from: k */
    public static String getFileName(String str) {
        return new File(str).getName();
    }

    /* JADX INFO: renamed from: b */
    public static String joinPath(String str, String str2) {
        if (str2.startsWith("/") || str2.startsWith("\\")) {
            str2 = str2.substring(1);
        }
        if (str.endsWith("/")) {
            return str + str2;
        }
        if (str.endsWith("\\")) {
            str = str.substring(0, str.length() - 1);
        }
        return str + "/" + str2;
    }

    /* JADX INFO: renamed from: a */
    public static String joinStrings(CharSequence charSequence, Iterable iterable) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        Iterator it = iterable.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (z) {
                z = false;
            } else {
                sb.append(charSequence);
            }
            sb.append(str);
        }
        return sb.toString();
    }

    /* JADX INFO: renamed from: l */
    public static Integer parseIntOrNull(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e2) {
            GameEngine.log(e2.toString());
            return null;
        }
    }

    /* JADX INFO: renamed from: m */
    public static Long parseLongOrNull(String str) {
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e2) {
            GameEngine.log(e2.toString());
            return null;
        }
    }

    /* JADX INFO: renamed from: n */
    public static boolean containsNonAscii(String str) {
        int length = str.length();
        int iCharCount = 0;
        while (true) {
            int i2 = iCharCount;
            if (i2 < length) {
                int iCodePointAt = str.codePointAt(i2);
                if (iCodePointAt > 128) {
                    return true;
                }
                iCharCount = i2 + Character.charCount(iCodePointAt);
            } else {
                return false;
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public static String formatDuration(long j2) {
        String str;
        int[] timeParts = secondsToTimeParts(j2);
        if (timeParts[0] == 0) {
            str = padLeft(VariableScope.nullOrMissingString + timeParts[1], 2, "0") + ":" + padLeft(VariableScope.nullOrMissingString + timeParts[2], 2, "0");
        } else {
            str = padLeft(VariableScope.nullOrMissingString + timeParts[0], 2, "0") + ":" + padLeft(VariableScope.nullOrMissingString + timeParts[1], 2, "0") + ":" + padLeft(VariableScope.nullOrMissingString + timeParts[2], 2, "0");
        }
        return str;
    }

    /* JADX INFO: renamed from: b */
    public static int[] secondsToTimeParts(long j2) {
        int i2 = ((int) j2) / 3600;
        int i3 = ((int) j2) - (i2 * 3600);
        int i4 = i3 / 60;
        return new int[]{i2, i4, i3 - (i4 * 60)};
    }

    /* JADX INFO: renamed from: i */
    public static final float fastAtan2(float f2, float f3) {
        try {
            if (f3 >= 0.0f) {
                if (f2 >= 0.0f) {
                    if (f3 >= f2) {
                        return l[(int) (((double) ((1024.0f * f2) / f3)) + 0.5d)];
                    }
                    return m[(int) (((double) ((1024.0f * f3) / f2)) + 0.5d)];
                }
                if (f3 >= (-f2)) {
                    return n[(int) (((double) (((-1024.0f) * f2) / f3)) + 0.5d)];
                }
                return o[(int) (((double) (((-1024.0f) * f3) / f2)) + 0.5d)];
            }
            if (f2 >= 0.0f) {
                if ((-f3) >= f2) {
                    return p[(int) (((double) (((-1024.0f) * f2) / f3)) + 0.5d)];
                }
                return q[(int) (((double) (((-1024.0f) * f3) / f2)) + 0.5d)];
            }
            if (f3 <= f2) {
                return r[(int) (((double) ((1024.0f * f2) / f3)) + 0.5d)];
            }
            return s[(int) (((double) ((1024.0f * f3) / f2)) + 0.5d)];
        } catch (ArrayIndexOutOfBoundsException e2) {
            if (i < 100) {
                GameEngine.log("atan2 slow fallback for y:" + f2 + " x:" + f3);
                i++;
            }
            return (float) StrictMath.atan2(f2, f3);
        }
    }

    /* JADX INFO: renamed from: j */
    public static final float fastSin(float f2) {
        return t[((int) (f2 * 22.755556f)) & 8191];
    }

    /* JADX INFO: renamed from: k */
    public static final float fastCos(float f2) {
        return u[((int) (f2 * 22.755556f)) & 8191];
    }

    /* JADX INFO: renamed from: o */
    public static String unescapeHtml(String str) {
        if (str.contains("&")) {
            str = str.replace("&lt;", "<").replace("&gt;", ">").replace("&apos;", "'").replace("&quot;", "\"").replace("&amp;", "&");
        }
        return str;
    }

    /* JADX INFO: renamed from: p */
    public static String stripQuotes(String str) {
        if (str == null || str.length() < 2) {
            return null;
        }
        char cCharAt = str.charAt(0);
        if ((cCharAt != '\"' && cCharAt != '\'') || str.charAt(str.length() - 1) != cCharAt) {
            return null;
        }
        boolean z = false;
        StringBuilder sb = new StringBuilder();
        for (int i2 = 1; i2 < str.length() - 1; i2++) {
            char cCharAt2 = str.charAt(i2);
            boolean z2 = z;
            z = false;
            if (!z2) {
                if (cCharAt2 == '\\') {
                    z = true;
                } else {
                    if (cCharAt2 == cCharAt) {
                        return null;
                    }
                    sb.append(cCharAt2);
                }
            } else {
                sb.append(cCharAt2);
            }
        }
        return sb.toString();
    }

    /* JADX INFO: renamed from: q */
    public static String removeEscapeCharacters(String str) {
        boolean z = false;
        StringBuilder sb = new StringBuilder();
        for (char c2 : str.toCharArray()) {
            boolean z2 = z;
            z = false;
            if (!z2 && c2 == '\\') {
                z = true;
            } else {
                sb.append(c2);
            }
        }
        return sb.toString();
    }

    /* JADX INFO: renamed from: a */
    public static final String replaceSubstring(String str, String str2, String str3) {
        if (!containsSubstring(str, str2)) {
            return str;
        }
        return str.replace(str2, str3);
    }

    /* JADX INFO: renamed from: c */
    public static final boolean containsSubstring(String str, String str2) {
        return str.indexOf(str2) > -1;
    }

    /* JADX INFO: renamed from: b */
    public static final boolean containsChar(String str, char c2) {
        return str.indexOf(c2) > -1;
    }

    /* JADX INFO: renamed from: c */
    public static String[] splitByChar(String str, char c2) {
        int i2;
        if (str.length() == 0) {
            return new String[]{VariableScope.nullOrMissingString};
        }
        int length = 0;
        int i3 = 0;
        while (true) {
            i2 = i3;
            int iIndexOf = str.indexOf(c2, i2);
            if (iIndexOf == -1) {
                break;
            }
            length++;
            i3 = iIndexOf + 1;
        }
        if (length == 0) {
            return new String[]{str};
        }
        int length2 = str.length();
        if (i2 == length2) {
            if (length == length2) {
                return new String[0];
            }
            do {
                i2--;
            } while (str.charAt(i2 - 1) == c2);
            length -= str.length() - i2;
            length2 = i2;
        }
        String[] strArr = new String[length + 1];
        int i4 = 0;
        for (int i5 = 0; i5 != length; i5++) {
            int iIndexOf2 = str.indexOf(c2, i4);
            strArr[i5] = str.substring(i4, iIndexOf2);
            i4 = iIndexOf2 + 1;
        }
        strArr[length] = str.substring(i4, length2);
        return strArr;
    }

    /* JADX INFO: renamed from: r */
    public static boolean isNumeric(String str) {
        for (int i2 = 0; i2 < str.length(); i2++) {
            char cCharAt = str.charAt(i2);
            if (!Character.isDigit(cCharAt) && cCharAt != '.' && (cCharAt != '-' || i2 != 0)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: renamed from: s */
    public static boolean isValidNumber(String str) {
        boolean z = false;
        for (int i2 = 0; i2 < str.length(); i2++) {
            char cCharAt = str.charAt(i2);
            if (!Character.isDigit(cCharAt) && (cCharAt != '-' || i2 != 0)) {
                if (!z && cCharAt == '.') {
                    z = true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /* JADX INFO: renamed from: j */
    public static final boolean approximatelyEqual(float f2, float f3) {
        return abs(f2 - f3) < 1.0E-4f;
    }

    /* JADX INFO: renamed from: k */
    public static final boolean approximatelyEqualStrict(float f2, float f3) {
        return abs(f2 - f3) < 1.0E-7f;
    }

    public static boolean b(double d2, double d3) {
        return abs(d2 - d3) < 1.0000000116860974E-7d;
    }

    /* JADX INFO: renamed from: d */
    public static final boolean stringsEqual(String str, String str2) {
        if (str == null) {
            return str2 == null;
        }
        return str.equals(str2);
    }

    /* JADX INFO: renamed from: a */
    public static final boolean nullableIntegersEqual(Integer num, Integer num2) {
        if (num == null) {
            return num2 == null;
        }
        return num.equals(num2);
    }
}
