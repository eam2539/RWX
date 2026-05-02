package com.corrodinggames.rts.appFramework;

import android.util.Log;
import android.view.MotionEvent;
import java.lang.reflect.Method;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/l.class */
public class MultiTouchController {

    /* JADX INFO: renamed from: a */
    public static final boolean isSupported;

    /* JADX INFO: renamed from: b */
    public static final boolean isMouseSupported;

    /* JADX INFO: renamed from: c */
    private static Method getButtonStateMethod;

    /* JADX INFO: renamed from: d */
    private static Method getPointerCountMethod;

    /* JADX INFO: renamed from: e */
    private static Method findPointerIndexMethod;

    /* JADX INFO: renamed from: f */
    private static Method getPressureMethod;

    /* JADX INFO: renamed from: g */
    private static Method getHistoricalXMethod;

    /* JADX INFO: renamed from: h */
    private static Method getHistoricalYMethod;

    /* JADX INFO: renamed from: i */
    private static Method getHistoricalPressureMethod;

    /* JADX INFO: renamed from: j */
    private static Method getXMethod;

    /* JADX INFO: renamed from: k */
    private static Method getYMethod;

    /* JADX INFO: renamed from: l */
    private static int ACTION_POINTER_UP;

    /* JADX INFO: renamed from: m */
    private static int ACTION_POINTER_INDEX_SHIFT;

    /* JADX INFO: renamed from: n */
    private static final float[] xCoords;

    /* JADX INFO: renamed from: o */
    private static final float[] yCoords;

    /* JADX INFO: renamed from: p */
    private static final float[] pressures;

    /* JADX INFO: renamed from: q */
    private static final int[] pointerIds;

    /* JADX INFO: renamed from: r */
    static final int[] pointerIndices;

    static {
        ACTION_POINTER_UP = 6;
        ACTION_POINTER_INDEX_SHIFT = 8;
        boolean z = false;
        try {
            getPointerCountMethod = MotionEvent.class.getMethod("getPointerCount", new Class[0]);
            findPointerIndexMethod = MotionEvent.class.getMethod("findPointerIndex", Integer.TYPE);
            getPressureMethod = MotionEvent.class.getMethod("getPressure", Integer.TYPE);
            getHistoricalXMethod = MotionEvent.class.getMethod("getHistoricalX", Integer.TYPE, Integer.TYPE);
            getHistoricalYMethod = MotionEvent.class.getMethod("getHistoricalY", Integer.TYPE, Integer.TYPE);
            getHistoricalPressureMethod = MotionEvent.class.getMethod("getHistoricalPressure", Integer.TYPE, Integer.TYPE);
            getXMethod = MotionEvent.class.getMethod("getX", Integer.TYPE);
            getYMethod = MotionEvent.class.getMethod("getY", Integer.TYPE);
            z = true;
        } catch (Exception e) {
            Log.b("MultiTouchController", "static initializer failed", e);
        }
        isSupported = z;
        if (isSupported) {
            try {
                ACTION_POINTER_UP = MotionEvent.class.getField("ACTION_POINTER_UP").getInt(null);
                ACTION_POINTER_INDEX_SHIFT = MotionEvent.class.getField("ACTION_POINTER_INDEX_SHIFT").getInt(null);
            } catch (Exception e2) {
            }
        }
        boolean z2 = false;
        try {
            getButtonStateMethod = MotionEvent.class.getMethod("getButtonState", new Class[0]);
            z2 = true;
            Log.b("MultiTouchController", "--- Mouse API succeeded");
        } catch (Exception e3) {
            Log.b("MultiTouchController", "static initializer for mouse failed", e3);
        }
        isMouseSupported = z2;
        xCoords = new float[10];
        yCoords = new float[10];
        pressures = new float[10];
        pointerIds = new int[10];
        pointerIndices = new int[10];
    }
}
