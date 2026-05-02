package android.util;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: game-lib.jar:android/util/Log.class */
public final class Log {
    private static final ThreadLocal a = new ThreadLocal() { // from class: android.util.Log.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
    };

    /* JADX INFO: loaded from: game-lib.jar:android/util/Log$TerribleFailureHandler.class */
    public interface TerribleFailureHandler {
    }

    public static native boolean isLoggable(String str, int i);

    private Log() {
    }

    public static int a(String str, String str2) {
        return a(0, 2, str, str2);
    }

    public static int b(String str, String str2) {
        return a(0, 3, str, str2);
    }

    public static int c(String str, String str2) {
        return a(0, 5, str, str2);
    }

    public static int a(String str, String str2, Throwable th) {
        return a(0, 5, str, str2 + '\n' + a(th));
    }

    public static int d(String str, String str2) {
        return a(0, 6, str, str2);
    }

    public static int b(String str, String str2, Throwable th) {
        return a(0, 6, str, str2 + '\n' + a(th));
    }

    public static int c(String str, String str2, Throwable th) {
        return a(0, str, str2, th, false);
    }

    static int a(int i, String str, String str2, Throwable th, boolean z) {
        throw new RuntimeException("removed");
    }

    public static String a(Throwable th) {
        StringWriter stringWriter = new StringWriter();
        for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
            stringWriter.write(stackTraceElement.toString() + "\n");
        }
        return stringWriter.toString();
    }

    public static int a(int i, int i2, String str, String str2) {
        a(i, str, str2);
        return 0;
    }

    public static int a(int i, String str, String str2) {
        System.out.println(((SimpleDateFormat) a.get()).format(new Date()) + ": " + str2);
        return 0;
    }
}
