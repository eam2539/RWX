package com.corrodinggames.rts.appFramework;

import android.util.Log;
import android.view.MotionEvent;

import java.lang.reflect.Method;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.en */
/* JADX INFO: loaded from: classes.dex */
public final class MotionEventCompat {
    private static Method A;
    private static Method B;
    private static Method C;
    private static Method D;
    private static Method E;
    private static Method F;
    private static int G;
    private static int H;
    private static final float[] I;
    private static final float[] J;
    private static final float[] K;
    private static final int[] L;
    static final int[] M;
    public static final boolean b;
    public static final boolean c;
    private static Method x;
    private static Method y;
    private static Method z;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    MultiTouchController$MultiTouchObjectCanvas f147a;
    private CurrTouchPoint d;
    private CurrTouchPoint e;
    private float f;
    private float g;
    private float h;
    private float i;
    private float j;
    private float k;
    private boolean l;
    private Object m;
    private MultiTouchController$PositionAndScale n;
    private long o;
    private long p;
    private float q;
    private float r;
    private float s;
    private float t;
    private float u;
    private float v;
    private int w;

    private void b() {
        float f;
        float f2;
        float f3 = 0.0f;
        this.f = this.d.f;
        this.g = this.d.g;
        if (this.n.g) {
            CurrTouchPoint currTouchPoint = this.d;
            if (!currTouchPoint.s) {
                if (!currTouchPoint.o) {
                    currTouchPoint.k = 0.0f;
                } else {
                    if (!currTouchPoint.r) {
                        currTouchPoint.l = currTouchPoint.o ? (currTouchPoint.i * currTouchPoint.i) + (currTouchPoint.j * currTouchPoint.j) : 0.0f;
                        currTouchPoint.r = true;
                    }
                    float f4 = currTouchPoint.l;
                    if (f4 == 0.0f) {
                        f2 = 0.0f;
                    } else {
                        int i = (int) (f4 * 256.0f);
                        int i2 = 15;
                        int i3 = 32768;
                        int i4 = 0;
                        while (true) {
                            int i5 = i2 - 1;
                            int i6 = ((i4 << 1) + i3) << i2;
                            if (i >= i6) {
                                i4 += i3;
                                i -= i6;
                            }
                            i3 >>= 1;
                            if (i3 <= 0) {
                                break;
                            } else {
                                i2 = i5;
                            }
                        }
                        f2 = i4 / 16.0f;
                    }
                    currTouchPoint.k = f2;
                    if (currTouchPoint.k < currTouchPoint.i) {
                        currTouchPoint.k = currTouchPoint.i;
                    }
                    if (currTouchPoint.k < currTouchPoint.j) {
                        currTouchPoint.k = currTouchPoint.j;
                    }
                }
                currTouchPoint.s = true;
            }
            f = currTouchPoint.k;
        } else {
            f = 0.0f;
        }
        this.h = Math.max(21.3f, f);
        this.i = Math.max(30.0f, !this.n.h ? 0.0f : this.d.x());
        this.j = Math.max(30.0f, !this.n.h ? 0.0f : this.d.y());
        if (this.n.i) {
            CurrTouchPoint currTouchPoint2 = this.d;
            if (!currTouchPoint2.t) {
                if (!currTouchPoint2.o) {
                    currTouchPoint2.m = 0.0f;
                } else {
                    currTouchPoint2.m = (float) Math.atan2(currTouchPoint2.c[1] - currTouchPoint2.c[0], currTouchPoint2.b[1] - currTouchPoint2.b[0]);
                }
                currTouchPoint2.t = true;
            }
            f3 = currTouchPoint2.m;
        }
        this.k = f3;
    }

    public MotionEventCompat(MultiTouchController$MultiTouchObjectCanvas multiTouchController$MultiTouchObjectCanvas) {
        this(multiTouchController$MultiTouchObjectCanvas, (byte) 0);
    }

    private MotionEventCompat(MultiTouchController$MultiTouchObjectCanvas multiTouchController$MultiTouchObjectCanvas, byte b2) {
        this.m = null;
        this.n = new MultiTouchController$PositionAndScale();
        this.w = 0;
        this.d = new CurrTouchPoint();
        this.e = new CurrTouchPoint();
        this.l = true;
        this.f147a = multiTouchController$MultiTouchObjectCanvas;
    }

    static {
        boolean z2;
        boolean z3 = true;
        G = 6;
        H = 8;
        try {
            y = MotionEvent.class.getMethod("getPointerCount", new Class[0]);
            z = MotionEvent.class.getMethod("findPointerIndex", Integer.TYPE);
            A = MotionEvent.class.getMethod("getPressure", Integer.TYPE);
            B = MotionEvent.class.getMethod("getHistoricalX", Integer.TYPE, Integer.TYPE);
            C = MotionEvent.class.getMethod("getHistoricalY", Integer.TYPE, Integer.TYPE);
            D = MotionEvent.class.getMethod("getHistoricalPressure", Integer.TYPE, Integer.TYPE);
            E = MotionEvent.class.getMethod("getX", Integer.TYPE);
            F = MotionEvent.class.getMethod("getY", Integer.TYPE);
            z2 = true;
        } catch (Exception e) {
            Log.e("MultiTouchController", "static initializer failed", e);
            z2 = false;
        }
        b = z2;
        if (z2) {
            try {
                G = MotionEvent.class.getField("ACTION_POINTER_UP").getInt(null);
                H = MotionEvent.class.getField("ACTION_POINTER_INDEX_SHIFT").getInt(null);
            } catch (Exception e2) {
            }
        }
        try {
            x = MotionEvent.class.getMethod("getButtonState", new Class[0]);
            try {
                Log.d("MultiTouchController", "--- Mouse API succeeded");
            } catch (Exception e3) {
                Log.e("MultiTouchController", "static initializer for mouse failed", e3);
            }
        } catch (Exception e4) {
            z3 = false;
            Log.e("MultiTouchController", "static initializer for mouse failed", e4);
        }
        c = z3;
        I = new float[10];
        J = new float[10];
        K = new float[10];
        L = new int[10];
        M = new int[10];
    }

    public final boolean a(MotionEvent motionEvent) {
        try {
            int iIntValue = b ? ((Integer) y.invoke(motionEvent, new Object[0])).intValue() : 1;
            if (this.w == 0 && !this.l && iIntValue == 1) {
                return false;
            }
            int action = motionEvent.getAction();
            int historySize = motionEvent.getHistorySize() / iIntValue;
            int i = 0;
            while (i <= historySize) {
                boolean z2 = i < historySize;
                if (!b || iIntValue == 1) {
                    M[0] = 0;
                    if (c) {
                        try {
                            M[0] = ((Integer) x.invoke(motionEvent, new Object[0])).intValue();
                        } catch (Exception e) {
                            Log.e("MultiTouchController", "onTouchEvent() mouse failed", e);
                        }
                    }
                    I[0] = z2 ? motionEvent.getHistoricalX(i) : motionEvent.getX();
                    J[0] = z2 ? motionEvent.getHistoricalY(i) : motionEvent.getY();
                    K[0] = z2 ? motionEvent.getHistoricalPressure(i) : motionEvent.getPressure();
                } else {
                    int iMin = Math.min(iIntValue, 10);
                    for (int i2 = 0; i2 < iMin; i2++) {
                        int iIntValue2 = ((Integer) z.invoke(motionEvent, Integer.valueOf(i2))).intValue();
                        L[i2] = iIntValue2;
                        if (iIntValue2 == -1) {
                            Log.i("MultiTouch", "ptrIdx is -1");
                        }
                        M[i2] = 0;
                        if (c) {
                            try {
                                M[i2] = ((Integer) x.invoke(motionEvent, new Object[0])).intValue();
                            } catch (Exception e2) {
                                Log.e("MultiTouchController", "onTouchEvent() mouse failed", e2);
                            }
                        }
                        I[i2] = ((Float) (z2 ? B.invoke(motionEvent, Integer.valueOf(iIntValue2), Integer.valueOf(i)) : E.invoke(motionEvent, Integer.valueOf(iIntValue2)))).floatValue();
                        J[i2] = ((Float) (z2 ? C.invoke(motionEvent, Integer.valueOf(iIntValue2), Integer.valueOf(i)) : F.invoke(motionEvent, Integer.valueOf(iIntValue2)))).floatValue();
                        K[i2] = ((Float) (z2 ? D.invoke(motionEvent, Integer.valueOf(iIntValue2), Integer.valueOf(i)) : A.invoke(motionEvent, Integer.valueOf(iIntValue2)))).floatValue();
                    }
                }
                float[] fArr = I;
                float[] fArr2 = J;
                float[] fArr3 = K;
                int[] iArr = L;
                int i3 = z2 ? 2 : action;
                boolean z3 = z2 ? true : (action == 1 || (((1 << H) + (-1)) & action) == G || action == 3) ? false : true;
                long historicalEventTime = z2 ? motionEvent.getHistoricalEventTime(i) : motionEvent.getEventTime();
                CurrTouchPoint currTouchPoint = this.e;
                this.e = this.d;
                this.d = currTouchPoint;
                CurrTouchPoint currTouchPoint2 = this.d;
                currTouchPoint2.v = historicalEventTime;
                currTouchPoint2.u = i3;
                currTouchPoint2.f148a = iIntValue;
                for (int i4 = 0; i4 < iIntValue; i4++) {
                    currTouchPoint2.b[i4] = fArr[i4];
                    currTouchPoint2.c[i4] = fArr2[i4];
                    currTouchPoint2.d[i4] = fArr3[i4];
                    currTouchPoint2.e[i4] = iArr[i4];
                }
                currTouchPoint2.n = z3;
                currTouchPoint2.o = iIntValue >= 2;
                if (currTouchPoint2.n) {
                    currTouchPoint2.p = currTouchPoint2.n;
                }
                if (currTouchPoint2.f148a > 0) {
                    currTouchPoint2.q = currTouchPoint2.f148a;
                }
                if (currTouchPoint2.o) {
                    currTouchPoint2.f = (fArr[0] + fArr[1]) * 0.5f;
                    currTouchPoint2.g = (fArr2[0] + fArr2[1]) * 0.5f;
                    currTouchPoint2.h = (fArr3[0] + fArr3[1]) * 0.5f;
                    currTouchPoint2.i = Math.abs(fArr[1] - fArr[0]);
                    currTouchPoint2.j = Math.abs(fArr2[1] - fArr2[0]);
                } else {
                    currTouchPoint2.f = fArr[0];
                    currTouchPoint2.g = fArr2[0];
                    currTouchPoint2.h = fArr3[0];
                    currTouchPoint2.j = 0.0f;
                    currTouchPoint2.i = 0.0f;
                }
                currTouchPoint2.t = false;
                currTouchPoint2.s = false;
                currTouchPoint2.r = false;
                switch (this.w) {
                    case 0:
                        if (this.d.n) {
                            this.m = this.f147a.getDraggableObjectAtPoint(this.d);
                            if (this.m != null) {
                                this.w = 1;
                                this.f147a.selectObject(this.m, this.d);
                                c();
                                long j = this.d.v;
                                this.p = j;
                                this.o = j;
                            }
                        }
                        break;
                    case 1:
                        if (!this.d.n) {
                            this.w = 0;
                            MultiTouchController$MultiTouchObjectCanvas multiTouchController$MultiTouchObjectCanvas = this.f147a;
                            this.m = null;
                            multiTouchController$MultiTouchObjectCanvas.selectObject(null, this.d);
                        } else if (this.d.o) {
                            this.w = 2;
                            c();
                            this.o = this.d.v;
                            this.p = this.o + 20;
                        } else if (this.d.v < this.p) {
                            c();
                        } else {
                            d();
                        }
                        break;
                    case 2:
                        if (this.d.o && this.d.n) {
                            if (Math.abs(this.d.f - this.e.f) > 30.0f || Math.abs(this.d.g - this.e.g) > 30.0f || Math.abs(this.d.x() - this.e.x()) * 0.5f > 40.0f || Math.abs(this.d.y() - this.e.y()) * 0.5f > 40.0f) {
                                c();
                                this.o = this.d.v;
                                this.p = this.o + 20;
                            } else if (this.d.v < this.p) {
                                c();
                            } else {
                                d();
                            }
                        } else if (!this.d.n) {
                            this.w = 0;
                            MultiTouchController$MultiTouchObjectCanvas multiTouchController$MultiTouchObjectCanvas2 = this.f147a;
                            this.m = null;
                            multiTouchController$MultiTouchObjectCanvas2.selectObject(null, this.d);
                        } else {
                            this.w = 1;
                            c();
                            this.o = this.d.v;
                            this.p = this.o + 20;
                        }
                        break;
                }
                i++;
            }
            return true;
        } catch (Exception e3) {
            Log.e("MultiTouchController", "onTouchEvent() failed", e3);
            return false;
        }
    }

    private void c() {
        if (this.m == null) {
            return;
        }
        this.f147a.getPositionAndScale(this.m, this.n);
        float f = (this.n.g && this.n.c != 0.0f) ? this.n.c : 1.0f;
        float f2 = 1.0f / f;
        b();
        this.q = (this.f - this.n.f149a) * f2;
        this.r = f2 * (this.g - this.n.b);
        this.s = this.n.c / this.h;
        this.u = this.n.d / this.i;
        this.v = this.n.e / this.j;
        this.t = this.n.f - this.k;
    }

    private void d() {
        float f = 1.0f;
        if (this.m == null) {
            return;
        }
        if (this.n.g && this.n.c != 0.0f) {
            f = this.n.c;
        }
        b();
        this.n.a(this.f - (this.q * f), this.g - (f * this.r), this.h * this.s, this.i * this.u, this.j * this.v, this.k + this.t);
        this.f147a.setPositionAndScale(this.m, this.n, this.d);
    }
}
