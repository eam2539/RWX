package com.corrodinggames.rts.gameFramework.android.graphics.opengl;

import android.graphics.*;
import com.corrodinggames.rts.gameFramework.android.graphics.AndroidGraphicsContext;
import com.corrodinggames.rts.gameFramework.android.graphics.C0009fo;
import com.corrodinggames.rts.gameFramework.android.graphics.DeferredGraphicsInterface;
import com.corrodinggames.rts.gameFramework.android.graphics.GraphicsInterface;
import com.corrodinggames.rts.gameFramework.m.GLDrawCommand;
import com.corrodinggames.rts.gameFramework.m.UnitTexture;

import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.p */
/* JADX INFO: loaded from: classes.dex */
public abstract class CanvasDrawOperation {
    public static final CanvasDrawOperation A;
    public static final CanvasDrawOperation B;
    public static final CanvasDrawOperation C;
    public static final CanvasDrawOperation D;
    public static final CanvasDrawOperation E;
    public static final CanvasDrawOperation F;
    public static final CanvasDrawOperation G;
    public static final CanvasDrawOperation H;
    public static final CanvasDrawOperation I;
    public static final CanvasDrawOperation J;
    public static final CanvasDrawOperation K;
    public static final CanvasDrawOperation L;
    public static final CanvasDrawOperation M;
    public static final CanvasDrawOperation N;
    public static final CanvasDrawOperation O;
    public static final CanvasDrawOperation P;
    public static final CanvasDrawOperation Q;
    public static final CanvasDrawOperation R;
    public static final CanvasDrawOperation S;
    public static final CanvasDrawOperation T;
    public static final CanvasDrawOperation U;
    public static final CanvasDrawOperation V;
    public static final CanvasDrawOperation W;
    public static final CanvasDrawOperation X;
    public static final CanvasDrawOperation Y;
    public static final CanvasDrawOperation Z;

    /* JADX INFO: renamed from: a */
    public static final CanvasDrawOperation f774a;
    public static final CanvasDrawOperation aa;
    public static final CanvasDrawOperation ab;
    public static final CanvasDrawOperation ac;
    public static final CanvasDrawOperation ad;
    public static final CanvasDrawOperation ae;
    public static final CanvasDrawOperation af;
    public static final CanvasDrawOperation ag;
    public static final CanvasDrawOperation ah;
    public static final CanvasDrawOperation ai;
    public static final CanvasDrawOperation aj;
    public static final CanvasDrawOperation ak;
    public static final CanvasDrawOperation al;
    public static final CanvasDrawOperation am;
    public static final CanvasDrawOperation an;
    public static final CanvasDrawOperation ao;
    public static final CanvasDrawOperation ap;
    public static final CanvasDrawOperation aq;
    public static final CanvasDrawOperation ar;
    public static final CanvasDrawOperation as;
    public static final CanvasDrawOperation at;
    private static final /* synthetic */ CanvasDrawOperation[] au;
    public static final CanvasDrawOperation b;
    public static final CanvasDrawOperation c;
    public static final CanvasDrawOperation d;
    public static final CanvasDrawOperation e;
    public static final CanvasDrawOperation f;
    public static final CanvasDrawOperation g;
    public static final CanvasDrawOperation h;
    public static final CanvasDrawOperation i;
    public static final CanvasDrawOperation j;
    public static final CanvasDrawOperation k;
    public static final CanvasDrawOperation l;
    public static final CanvasDrawOperation m;
    public static final CanvasDrawOperation n;
    public static final CanvasDrawOperation o;
    public static final CanvasDrawOperation p;
    public static final CanvasDrawOperation q;
    public static final CanvasDrawOperation r;
    public static final CanvasDrawOperation s;
    public static final CanvasDrawOperation t;
    public static final CanvasDrawOperation u;
    public static final CanvasDrawOperation v;
    public static final CanvasDrawOperation w;
    public static final CanvasDrawOperation x;
    public static final CanvasDrawOperation y;
    public static final CanvasDrawOperation z;
    private final String name;

    public abstract void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand);

    private CanvasDrawOperation(String str) {
        this.name = str;
    }

    protected /* synthetic */ CanvasDrawOperation(String str, int i2, byte b2) {
        this(str);
    }

    public static CanvasDrawOperation valueOf(String str) {
        for (CanvasDrawOperation canvasDrawOperation : au) {
            if (canvasDrawOperation.name.equals(str)) {
                return canvasDrawOperation;
            }
        }
        throw new IllegalArgumentException(str);
    }

    public static CanvasDrawOperation[] values() {
        return (CanvasDrawOperation[]) au.clone();
    }

    static {
        final String str = "clipPath_Path_Op";
        f774a = new CanvasDrawOperation(str) { // from class: com.corrodinggames.rts.gameFramework.m.q
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
            }
        };
        final String str2 = "clipPath_Path";
        b = new CanvasDrawOperation(str2) { // from class: com.corrodinggames.rts.gameFramework.m.ab
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
            }
        };
        final String str3 = "clipRect_float_float_float_float_Op";
        c = new CanvasDrawOperation(str3) { // from class: com.corrodinggames.rts.gameFramework.m.am
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue(), (Region.Op) objArr[4]);
            }
        };
        final String str4 = "clipRect_float_float_float_float";
        d = new CanvasDrawOperation(str4) { // from class: com.corrodinggames.rts.gameFramework.m.ax
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue());
            }
        };
        final String str5 = "clipRect_int_int_int_int";
        e = new CanvasDrawOperation(str5) { // from class: com.corrodinggames.rts.gameFramework.m.bi
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Integer) objArr[3]).intValue());
            }
        };
        final String str6 = "clipRect_Rect_Op";
        f = new CanvasDrawOperation(str6) { // from class: com.corrodinggames.rts.gameFramework.m.bt
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((Rect) objArr[0], (Region.Op) objArr[1]);
            }
        };
        final String str7 = "clipRect_Rect";
        g = new CanvasDrawOperation(str7) { // from class: com.corrodinggames.rts.gameFramework.m.ce
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((Rect) gLDrawCommand.b[0]);
            }
        };
        final String str8 = "clipRect_RectF_Op";
        h = new CanvasDrawOperation(str8) { // from class: com.corrodinggames.rts.gameFramework.m.ci
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((RectF) objArr[0], (Region.Op) objArr[1]);
            }
        };
        final String str9 = "clipRect_RectF";
        i = new CanvasDrawOperation(str9) { // from class: com.corrodinggames.rts.gameFramework.m.cj
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((RectF) gLDrawCommand.b[0]);
            }
        };
        final String str10 = "concat_Matrix";
        j = new CanvasDrawOperation(str10) { // from class: com.corrodinggames.rts.gameFramework.m.r
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((Matrix) gLDrawCommand.b[0]);
            }
        };
        final String str11 = "drawARGB_int_int_int_int";
        k = new CanvasDrawOperation(str11) { // from class: com.corrodinggames.rts.gameFramework.m.s
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.b(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Integer) objArr[3]).intValue());
            }
        };
        final String str12 = "drawArc_RectF_float_float_boolean_Paint";
        l = new CanvasDrawOperation(str12) { // from class: com.corrodinggames.rts.gameFramework.m.t
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((RectF) objArr[0], ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Boolean) objArr[3]).booleanValue(), (Paint) objArr[4]);
            }
        };
        final String str13 = "drawBitmap_Bitmap_float_float_Paint";
        m = new CanvasDrawOperation(str13) { // from class: com.corrodinggames.rts.gameFramework.m.u
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((UnitTexture) objArr[0], gLDrawCommand.c, gLDrawCommand.d, (Paint) objArr[1]);
            }
        };
        final String str14 = "drawBitmap_Bitmap_Matrix_Paint";
        n = new CanvasDrawOperation(str14) { // from class: com.corrodinggames.rts.gameFramework.m.v
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((UnitTexture) objArr[0], (Matrix) objArr[1], (Paint) objArr[2]);
            }
        };
        final String str15 = "drawBitmap_Bitmap_Rect_Rect_Paint";
        o = new CanvasDrawOperation(str15) { // from class: com.corrodinggames.rts.gameFramework.m.w
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((UnitTexture) objArr[0], (Rect) objArr[1], (Rect) objArr[2], (Paint) objArr[3]);
            }
        };
        final String str16 = "drawBitmap_Bitmap_Rect_RectF_Paint";
        p = new CanvasDrawOperation(str16) { // from class: com.corrodinggames.rts.gameFramework.m.x
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((UnitTexture) objArr[0], (Rect) objArr[1], (RectF) objArr[2], (Paint) objArr[3]);
            }
        };
        final String str17 = "drawBitmap_intarray_int_int_float_float_int_int_boolean_Paint";
        q = new CanvasDrawOperation(str17) { // from class: com.corrodinggames.rts.gameFramework.m.y
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((int[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), gLDrawCommand.c, gLDrawCommand.d, ((Integer) objArr[3]).intValue(), ((Integer) objArr[4]).intValue(), ((Boolean) objArr[5]).booleanValue(), (Paint) objArr[6]);
            }
        };
        final String str18 = "drawBitmap_intarray_int_int_int_int_int_int_boolean_Paint";
        r = new CanvasDrawOperation(str18) { // from class: com.corrodinggames.rts.gameFramework.m.z
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((int[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), (int) gLDrawCommand.c, (int) gLDrawCommand.d, ((Integer) objArr[5]).intValue(), ((Integer) objArr[6]).intValue(), ((Boolean) objArr[7]).booleanValue(), (Paint) objArr[8]);
            }
        };
        final String str19 = "drawBitmapMesh_Bitmap_int_int_floatarray_int_intarray_int_Paint";
        s = new CanvasDrawOperation(str19) { // from class: com.corrodinggames.rts.gameFramework.m.aa
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((UnitTexture) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), (float[]) objArr[3], ((Integer) objArr[4]).intValue(), (int[]) objArr[5], ((Integer) objArr[6]).intValue(), (Paint) objArr[7]);
            }
        };
        final String str20 = "drawCircle_float_float_float_Paint";
        t = new CanvasDrawOperation(str20) { // from class: com.corrodinggames.rts.gameFramework.m.ac
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), (Paint) objArr[3]);
            }
        };
        final String str21 = "drawColor_int_Mode";
        u = new CanvasDrawOperation(str21) { // from class: com.corrodinggames.rts.gameFramework.m.ad
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a(((Integer) objArr[0]).intValue(), (PorterDuff.Mode) objArr[1]);
            }
        };
        final String str22 = "drawColor_int";
        v = new CanvasDrawOperation(str22) { // from class: com.corrodinggames.rts.gameFramework.m.ae
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a(((Integer) gLDrawCommand.b[0]).intValue());
            }
        };
        final String str23 = "drawLine_float_float_float_float_Paint";
        w = new CanvasDrawOperation(str23) { // from class: com.corrodinggames.rts.gameFramework.m.af
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a(gLDrawCommand.c, gLDrawCommand.d, gLDrawCommand.e, gLDrawCommand.f, (Paint) gLDrawCommand.b[0]);
            }
        };
        final String str24 = "drawLines_floatarray_int_int_Paint";
        x = new CanvasDrawOperation(str24) { // from class: com.corrodinggames.rts.gameFramework.m.ag
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((float[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), (Paint) objArr[3]);
            }
        };
        final String str25 = "drawLines_floatarray_Paint";
        y = new CanvasDrawOperation(str25) { // from class: com.corrodinggames.rts.gameFramework.m.ah
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((float[]) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str26 = "drawOval_RectF_Paint";
        z = new CanvasDrawOperation(str26) { // from class: com.corrodinggames.rts.gameFramework.m.ai
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((RectF) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str27 = "drawPaint_Paint";
        A = new CanvasDrawOperation(str27) { // from class: com.corrodinggames.rts.gameFramework.m.aj
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((Paint) gLDrawCommand.b[0]);
            }
        };
        final String str28 = "drawPath_Path_Paint";
        B = new CanvasDrawOperation(str28) { // from class: com.corrodinggames.rts.gameFramework.m.ak
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((Path) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str29 = "drawPicture_Picture_Rect";
        C = new CanvasDrawOperation(str29) { // from class: com.corrodinggames.rts.gameFramework.m.al
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((Picture) objArr[0], (Rect) objArr[1]);
            }
        };
        final String str30 = "drawPicture_Picture_RectF";
        D = new CanvasDrawOperation(str30) { // from class: com.corrodinggames.rts.gameFramework.m.an
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((Picture) objArr[0], (RectF) objArr[1]);
            }
        };
        final String str31 = "drawPicture_Picture";
        E = new CanvasDrawOperation(str31) { // from class: com.corrodinggames.rts.gameFramework.m.ao
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((Picture) gLDrawCommand.b[0]);
            }
        };
        final String str32 = "drawPoint_float_float_Paint";
        F = new CanvasDrawOperation(str32) { // from class: com.corrodinggames.rts.gameFramework.m.ap
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), (Paint) objArr[2]);
            }
        };
        final String str33 = "drawPoints_floatarray_int_int_Paint";
        G = new CanvasDrawOperation(str33) { // from class: com.corrodinggames.rts.gameFramework.m.aq
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.b((float[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), (Paint) objArr[3]);
            }
        };
        final String str34 = "drawPoints_floatarray_Paint";
        H = new CanvasDrawOperation(str34) { // from class: com.corrodinggames.rts.gameFramework.m.ar
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.b((float[]) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str35 = "drawPosText_chararray_int_int_floatarray_Paint";
        I = new CanvasDrawOperation(str35) { // from class: com.corrodinggames.rts.gameFramework.m.as
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((char[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), (float[]) objArr[3], (Paint) objArr[4]);
            }
        };
        final String str36 = "drawPosText_String_floatarray_Paint";
        J = new CanvasDrawOperation(str36) { // from class: com.corrodinggames.rts.gameFramework.m.at
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((String) objArr[0], (float[]) objArr[1], (Paint) objArr[2]);
            }
        };
        final String str37 = "drawRGB_int_int_int";
        K = new CanvasDrawOperation(str37) { // from class: com.corrodinggames.rts.gameFramework.m.au
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
            }
        };
        final String str38 = "drawRect_float_float_float_float_Paint";
        L = new CanvasDrawOperation(str38) { // from class: com.corrodinggames.rts.gameFramework.m.av
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.b(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue(), (Paint) objArr[4]);
            }
        };
        final String str39 = "drawRect_Rect_Paint";
        M = new CanvasDrawOperation(str39) { // from class: com.corrodinggames.rts.gameFramework.m.aw
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((Rect) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str40 = "drawRect_RectF_Paint";
        N = new CanvasDrawOperation(str40) { // from class: com.corrodinggames.rts.gameFramework.m.ay
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.b((RectF) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str41 = "drawRoundRect_RectF_float_float_Paint";
        O = new CanvasDrawOperation(str41) { // from class: com.corrodinggames.rts.gameFramework.m.az
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((RectF) objArr[0], ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), (Paint) objArr[3]);
            }
        };
        final String str42 = "drawText_chararray_int_int_float_float_Paint";
        P = new CanvasDrawOperation(str42) { // from class: com.corrodinggames.rts.gameFramework.m.ba
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((char[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Float) objArr[3]).floatValue(), ((Float) objArr[4]).floatValue(), (Paint) objArr[5]);
            }
        };
        final String str43 = "drawText_CharSequence_int_int_float_float_Paint";
        Q = new CanvasDrawOperation(str43) { // from class: com.corrodinggames.rts.gameFramework.m.bb
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((CharSequence) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Float) objArr[3]).floatValue(), ((Float) objArr[4]).floatValue(), (Paint) objArr[5]);
            }
        };
        final String str44 = "drawText_String_float_float_Paint";
        R = new CanvasDrawOperation(str44) { // from class: com.corrodinggames.rts.gameFramework.m.bc
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((String) objArr[0], ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), (Paint) objArr[3]);
            }
        };
        final String str45 = "drawText_String_int_int_float_float_Paint";
        S = new CanvasDrawOperation(str45) { // from class: com.corrodinggames.rts.gameFramework.m.bd
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((String) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Float) objArr[3]).floatValue(), ((Float) objArr[4]).floatValue(), (Paint) objArr[5]);
            }
        };
        final String str46 = "drawTextOnPath_chararray_int_int_Path_float_float_Paint";
        T = new CanvasDrawOperation(str46) { // from class: com.corrodinggames.rts.gameFramework.m.FastArrayList
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((char[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), (Path) objArr[3], ((Float) objArr[4]).floatValue(), ((Float) objArr[5]).floatValue(), (Paint) objArr[6]);
            }
        };
        final String str47 = "drawTextOnPath_String_Path_float_float_Paint";
        U = new CanvasDrawOperation(str47) { // from class: com.corrodinggames.rts.gameFramework.m.bf
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((String) objArr[0], (Path) objArr[1], ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue(), (Paint) objArr[4]);
            }
        };
        final String str48 = "drawVertices_VertexMode_int_floatarray_int_floatarray_int_intarray_int_shortarray_int_int_Paint";
        V = new CanvasDrawOperation(str48) { // from class: com.corrodinggames.rts.gameFramework.m.bg
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.a((Canvas.VertexMode) objArr[0], ((Integer) objArr[1]).intValue(), (float[]) objArr[2], ((Integer) objArr[3]).intValue(), (float[]) objArr[4], ((Integer) objArr[5]).intValue(), (int[]) objArr[6], ((Integer) objArr[7]).intValue(), (short[]) objArr[8], ((Integer) objArr[9]).intValue(), ((Integer) objArr[10]).intValue(), (Paint) objArr[11]);
            }
        };
        final String str49 = "restore";
        W = new CanvasDrawOperation(str49) { // from class: com.corrodinggames.rts.gameFramework.m.bh
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a_();
            }
        };
        final String str50 = "restoreToCount_int";
        X = new CanvasDrawOperation(str50) { // from class: com.corrodinggames.rts.gameFramework.m.bj
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.b(((Integer) gLDrawCommand.b[0]).intValue());
            }
        };
        final String str51 = "rotate_float";
        Y = new CanvasDrawOperation(str51) { // from class: com.corrodinggames.rts.gameFramework.m.bk
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a(gLDrawCommand.c);
            }
        };
        final String str52 = "rotate_float_float_float";
        Z = new CanvasDrawOperation(str52) { // from class: com.corrodinggames.rts.gameFramework.m.bl
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a(gLDrawCommand.c, gLDrawCommand.d, gLDrawCommand.e);
            }
        };
        final String str53 = "save";
        aa = new CanvasDrawOperation(str53) { // from class: com.corrodinggames.rts.gameFramework.m.bm
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.b();
            }
        };
        final String str54 = "saveLayer_float_float_float_float_Paint_int";
        ab = new CanvasDrawOperation(str54) { // from class: com.corrodinggames.rts.gameFramework.m.bn
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
            }
        };
        final String str55 = "saveLayer_RectF_Paint_int";
        ac = new CanvasDrawOperation(str55) { // from class: com.corrodinggames.rts.gameFramework.m.bo
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
            }
        };
        final String str56 = "saveLayerAlpha_float_float_float_float_int_int";
        ad = new CanvasDrawOperation(str56) { // from class: com.corrodinggames.rts.gameFramework.m.bp
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
            }
        };
        final String str57 = "saveLayerAlpha_RectF_int_int";
        ae = new CanvasDrawOperation(str57) { // from class: com.corrodinggames.rts.gameFramework.m.bq
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
            }
        };
        final String str58 = "scale_float_float";
        af = new CanvasDrawOperation(str58) { // from class: com.corrodinggames.rts.gameFramework.m.br
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a(gLDrawCommand.c, gLDrawCommand.d);
            }
        };
        final String str59 = "scale_float_float_float_float";
        ag = new CanvasDrawOperation(str59) { // from class: com.corrodinggames.rts.gameFramework.m.bs
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.b(gLDrawCommand.c, gLDrawCommand.d, gLDrawCommand.e, gLDrawCommand.f);
            }
        };
        final String str60 = "setBitmap_Bitmap";
        ah = new CanvasDrawOperation(str60) { // from class: com.corrodinggames.rts.gameFramework.m.bu
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((UnitTexture) gLDrawCommand.b[0]);
            }
        };
        final String str61 = "setDensity_int";
        ai = new CanvasDrawOperation(str61) { // from class: com.corrodinggames.rts.gameFramework.m.bv
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.c(((Integer) gLDrawCommand.b[0]).intValue());
            }
        };
        final String str62 = "setDrawFilter_DrawFilter";
        aj = new CanvasDrawOperation(str62) { // from class: com.corrodinggames.rts.gameFramework.m.bw
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((DrawFilter) gLDrawCommand.b[0]);
            }
        };
        final String str63 = "setMatrix_Matrix";
        ak = new CanvasDrawOperation(str63) { // from class: com.corrodinggames.rts.gameFramework.m.bx
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.b((Matrix) gLDrawCommand.b[0]);
            }
        };
        final String str64 = "skew_float_float";
        al = new CanvasDrawOperation(str64) { // from class: com.corrodinggames.rts.gameFramework.m.by
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                graphicsInterface.b(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue());
            }
        };
        final String str65 = "translate_float_float";
        am = new CanvasDrawOperation(str65) { // from class: com.corrodinggames.rts.gameFramework.m.bz
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.c(gLDrawCommand.c, gLDrawCommand.d);
            }
        };
        final String str66 = "runDrawTimeCallback_DrawTimeCallback";
        an = new CanvasDrawOperation(str66) { // from class: com.corrodinggames.rts.gameFramework.m.ca
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                DeferredGraphicsInterface deferredGraphicsInterface = (DeferredGraphicsInterface) objArr[0];
                if (deferredGraphicsInterface.c == null) {
                    deferredGraphicsInterface.c = new AndroidGraphicsContext().a();
                }
                deferredGraphicsInterface.c.a(graphicsInterface);
                ((com.corrodinggames.rts.gameFramework.android.graphics.GraphicsOperation) objArr[1]).a(deferredGraphicsInterface.c);
            }
        };
        final String str67 = "runDrawTimeCallback_DrawTimeCallback_float_float_float_paint";
        ao = new CanvasDrawOperation(str67) { // from class: com.corrodinggames.rts.gameFramework.m.cb
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                Object[] objArr = gLDrawCommand.b;
                DeferredGraphicsInterface deferredGraphicsInterface = (DeferredGraphicsInterface) objArr[0];
                if (deferredGraphicsInterface.c == null) {
                    deferredGraphicsInterface.c = new AndroidGraphicsContext().a();
                }
                deferredGraphicsInterface.c.a(graphicsInterface);
                ((Float) objArr[2]).floatValue();
                ((Float) objArr[3]).floatValue();
                ((Float) objArr[4]).floatValue();
            }
        };
        final String str68 = "flushBitmap";
        ap = new CanvasDrawOperation(str68) { // from class: com.corrodinggames.rts.gameFramework.m.cc
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((Bitmap) gLDrawCommand.b[0]);
            }
        };
        final String str69 = "enterLock_object";
        aq = new CanvasDrawOperation(str69) { // from class: com.corrodinggames.rts.gameFramework.m.cd
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((Lock) gLDrawCommand.b[0]);
            }
        };
        final String str70 = "leaveLock_object";
        ar = new CanvasDrawOperation(str70) { // from class: com.corrodinggames.rts.gameFramework.m.cf
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.b((Lock) gLDrawCommand.b[0]);
            }
        };
        final String str71 = "compileShader_object";
        as = new CanvasDrawOperation(str71) { // from class: com.corrodinggames.rts.gameFramework.m.cg
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.a((C0009fo) gLDrawCommand.b[0]);
            }
        };
        final String str72 = "setShader_object";
        at = new CanvasDrawOperation(str72) { // from class: com.corrodinggames.rts.gameFramework.m.ch
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation
            public final void a(GraphicsInterface graphicsInterface, GLDrawCommand gLDrawCommand) {
                graphicsInterface.b((C0009fo) gLDrawCommand.b[0]);
            }
        };
        au = new CanvasDrawOperation[]{f774a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, aa, ab, ac, ad, ae, af, ag, ah, ai, aj, ak, al, am, an, ao, ap, aq, ar, as, at};
    }
}
