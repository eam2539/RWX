package com.corrodinggames.rts.gameFramework.android.graphics.opengl;

import android.graphics.*;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.android.graphics.AndroidGraphicsContext;
import com.corrodinggames.rts.gameFramework.android.graphics.CanvasGraphicsRenderer;
import com.corrodinggames.rts.gameFramework.android.graphics.DeferredGraphicsRenderer;
import com.corrodinggames.rts.gameFramework.m.CanvasDrawCommand;
import com.corrodinggames.rts.gameFramework.m.FloatHolder;

import java.util.concurrent.locks.Lock;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.cp */
/* JADX INFO: loaded from: classes.dex */
public abstract class GraphicsOperation {
    public static final GraphicsOperation A;
    public static final GraphicsOperation B;
    public static final GraphicsOperation C;
    public static final GraphicsOperation D;
    public static final GraphicsOperation E;
    public static final GraphicsOperation F;
    public static final GraphicsOperation G;
    public static final GraphicsOperation H;
    public static final GraphicsOperation I;
    public static final GraphicsOperation J;
    public static final GraphicsOperation K;
    public static final GraphicsOperation L;
    public static final GraphicsOperation M;
    public static final GraphicsOperation N;
    public static final GraphicsOperation O;
    public static final GraphicsOperation P;
    public static final GraphicsOperation Q;
    public static final GraphicsOperation R;
    public static final GraphicsOperation S;
    public static final GraphicsOperation T;
    public static final GraphicsOperation U;
    public static final GraphicsOperation V;
    public static final GraphicsOperation W;
    public static final GraphicsOperation X;
    public static final GraphicsOperation Y;
    public static final GraphicsOperation Z;

    /* JADX INFO: renamed from: a */
    public static final GraphicsOperation f756a;
    public static final GraphicsOperation aa;
    public static final GraphicsOperation ab;
    public static final GraphicsOperation ac;
    public static final GraphicsOperation ad;
    public static final GraphicsOperation ae;
    public static final GraphicsOperation af;
    public static final GraphicsOperation ag;
    public static final GraphicsOperation ah;
    public static final GraphicsOperation ai;
    public static final GraphicsOperation aj;
    public static final GraphicsOperation ak;
    public static final GraphicsOperation al;
    private static final /* synthetic */ GraphicsOperation[] am;
    public static final GraphicsOperation b;
    public static final GraphicsOperation c;
    public static final GraphicsOperation d;
    public static final GraphicsOperation e;
    public static final GraphicsOperation f;
    public static final GraphicsOperation g;
    public static final GraphicsOperation h;
    public static final GraphicsOperation i;
    public static final GraphicsOperation j;
    public static final GraphicsOperation k;
    public static final GraphicsOperation l;
    public static final GraphicsOperation m;
    public static final GraphicsOperation n;
    public static final GraphicsOperation o;
    public static final GraphicsOperation p;
    public static final GraphicsOperation q;
    public static final GraphicsOperation r;
    public static final GraphicsOperation s;
    public static final GraphicsOperation t;
    public static final GraphicsOperation u;
    public static final GraphicsOperation v;
    public static final GraphicsOperation w;
    public static final GraphicsOperation x;
    public static final GraphicsOperation y;
    public static final GraphicsOperation z;
    private final String name;

    public abstract void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand);

    private GraphicsOperation(String str) {
        this.name = str;
    }

    protected /* synthetic */ GraphicsOperation(String str, int i2, byte b2) {
        this(str);
    }

    public static GraphicsOperation valueOf(String str) {
        for (GraphicsOperation graphicsOperation : am) {
            if (graphicsOperation.name.equals(str)) {
                return graphicsOperation;
            }
        }
        throw new IllegalArgumentException(str);
    }

    public static GraphicsOperation[] values() {
        return (GraphicsOperation[]) am.clone();
    }

    static {
        final String str = "clipPath_Path_Op";
        f756a = new GraphicsOperation(str) { // from class: com.corrodinggames.rts.gameFramework.m.cq
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.clipPath((Path) objArr[0], (Region.Op) objArr[1]);
            }
        };
        final String str2 = "clipPath_Path";
        b = new GraphicsOperation(str2) { // from class: com.corrodinggames.rts.gameFramework.m.db
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.clipPath((Path) canvasDrawCommand.b[0]);
            }
        };
        final String str3 = "clipRect_float_float_float_float_Op";
        c = new GraphicsOperation(str3) { // from class: com.corrodinggames.rts.gameFramework.m.dm
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.clipRect(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue(), (Region.Op) objArr[4]);
            }
        };
        final String str4 = "clipRect_float_float_float_float";
        d = new GraphicsOperation(str4) { // from class: com.corrodinggames.rts.gameFramework.m.dx
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.clipRect(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue());
            }
        };
        final String str5 = "clipRect_int_int_int_int";
        e = new GraphicsOperation(str5) { // from class: com.corrodinggames.rts.gameFramework.m.ei
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.clipRect(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Integer) objArr[3]).intValue());
            }
        };
        final String str6 = "clipRect_Rect_Op";
        f = new GraphicsOperation(str6) { // from class: com.corrodinggames.rts.gameFramework.m.et
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.clipRect((Rect) objArr[0], (Region.Op) objArr[1]);
            }
        };
        final String str7 = "clipRect_Rect";
        g = new GraphicsOperation(str7) { // from class: com.corrodinggames.rts.gameFramework.m.ez
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.clipRect((Rect) canvasDrawCommand.b[0]);
            }
        };
        final String str8 = "clipRect_RectF_Op";
        h = new GraphicsOperation(str8) { // from class: com.corrodinggames.rts.gameFramework.m.fa
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.clipRect((RectF) objArr[0], (Region.Op) objArr[1]);
            }
        };
        final String str9 = "clipRect_RectF";
        i = new GraphicsOperation(str9) { // from class: com.corrodinggames.rts.gameFramework.m.fb
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.clipRect((RectF) canvasDrawCommand.b[0]);
            }
        };
        final String str10 = "concat_Matrix";
        j = new GraphicsOperation(str10) { // from class: com.corrodinggames.rts.gameFramework.m.cr
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.concat((Matrix) canvasDrawCommand.b[0]);
            }
        };
        final String str11 = "drawARGB_int_int_int_int";
        k = new GraphicsOperation(str11) { // from class: com.corrodinggames.rts.gameFramework.m.cs
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawARGB(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Integer) objArr[3]).intValue());
            }
        };
        final String str12 = "drawArc_RectF_float_float_boolean_Paint";
        l = new GraphicsOperation(str12) { // from class: com.corrodinggames.rts.gameFramework.m.ct
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawArc((RectF) objArr[0], ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Boolean) objArr[3]).booleanValue(), (Paint) objArr[4]);
            }
        };
        final String str13 = "drawBitmap_Bitmap_float_float_Paint";
        m = new GraphicsOperation(str13) { // from class: com.corrodinggames.rts.gameFramework.m.cu
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawBitmap((Bitmap) objArr[0], canvasDrawCommand.c, canvasDrawCommand.d, (Paint) objArr[1]);
            }
        };
        final String str14 = "drawBitmap_Bitmap_Matrix_Paint";
        n = new GraphicsOperation(str14) { // from class: com.corrodinggames.rts.gameFramework.m.cv
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawBitmap((Bitmap) objArr[0], (Matrix) objArr[1], (Paint) objArr[2]);
            }
        };
        final String str15 = "drawBitmap_Bitmap_Rect_Rect_Paint";
        o = new GraphicsOperation(str15) { // from class: com.corrodinggames.rts.gameFramework.m.cw
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawBitmap((Bitmap) objArr[0], (Rect) objArr[1], (Rect) objArr[2], (Paint) objArr[3]);
            }
        };
        final String str16 = "drawBitmap_Bitmap_Rect_RectF_Paint";
        p = new GraphicsOperation(str16) { // from class: com.corrodinggames.rts.gameFramework.m.cx
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawBitmap((Bitmap) objArr[0], (Rect) objArr[1], (RectF) objArr[2], (Paint) objArr[3]);
            }
        };
        final String str17 = "drawCircle_float_float_float_Paint";
        q = new GraphicsOperation(str17) { // from class: com.corrodinggames.rts.gameFramework.m.cy
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawCircle(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), (Paint) objArr[3]);
            }
        };
        final String str18 = "drawColor_int_Mode";
        r = new GraphicsOperation(str18) { // from class: com.corrodinggames.rts.gameFramework.m.cz
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawColor(((Integer) objArr[0]).intValue(), (PorterDuff.Mode) objArr[1]);
            }
        };
        final String str19 = "drawColor_int";
        s = new GraphicsOperation(str19) { // from class: com.corrodinggames.rts.gameFramework.m.da
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.drawColor(((Integer) canvasDrawCommand.b[0]).intValue());
            }
        };
        final String str20 = "drawLine_float_float_float_float_Paint";
        t = new GraphicsOperation(str20) { // from class: com.corrodinggames.rts.gameFramework.m.dc
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.drawLine(canvasDrawCommand.c, canvasDrawCommand.d, canvasDrawCommand.e, canvasDrawCommand.f, (Paint) canvasDrawCommand.b[0]);
            }
        };
        final String str21 = "drawLines_floatarray_int_int_Paint";
        u = new GraphicsOperation(str21) { // from class: com.corrodinggames.rts.gameFramework.m.dd
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawLines((float[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), (Paint) objArr[3]);
            }
        };
        final String str22 = "drawLines_floatarray_Paint";
        v = new GraphicsOperation(str22) { // from class: com.corrodinggames.rts.gameFramework.m.de
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawLines((float[]) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str23 = "drawOval_RectF_Paint";
        w = new GraphicsOperation(str23) { // from class: com.corrodinggames.rts.gameFramework.m.df
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawOval((RectF) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str24 = "drawPaint_Paint";
        x = new GraphicsOperation(str24) { // from class: com.corrodinggames.rts.gameFramework.m.dg
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.drawPaint((Paint) canvasDrawCommand.b[0]);
            }
        };
        final String str25 = "drawPath_Path_Paint";
        y = new GraphicsOperation(str25) { // from class: com.corrodinggames.rts.gameFramework.m.dh
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawPath((Path) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str26 = "drawPicture_Picture_Rect";
        z = new GraphicsOperation(str26) { // from class: com.corrodinggames.rts.gameFramework.m.di
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawPicture((Picture) objArr[0], (Rect) objArr[1]);
            }
        };
        final String str27 = "drawPicture_Picture_RectF";
        A = new GraphicsOperation(str27) { // from class: com.corrodinggames.rts.gameFramework.m.dj
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawPicture((Picture) objArr[0], (RectF) objArr[1]);
            }
        };
        final String str28 = "drawPicture_Picture";
        B = new GraphicsOperation(str28) { // from class: com.corrodinggames.rts.gameFramework.m.dk
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.drawPicture((Picture) canvasDrawCommand.b[0]);
            }
        };
        final String str29 = "drawPoint_float_float_Paint";
        C = new GraphicsOperation(str29) { // from class: com.corrodinggames.rts.gameFramework.m.dl
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawPoint(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), (Paint) objArr[2]);
            }
        };
        final String str30 = "drawPoints_floatarray_int_int_Paint";
        D = new GraphicsOperation(str30) { // from class: com.corrodinggames.rts.gameFramework.m.dn
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawPoints((float[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), (Paint) objArr[3]);
            }
        };
        final String str31 = "drawPoints_floatarray_Paint";
        E = new GraphicsOperation(str31) { // from class: com.corrodinggames.rts.gameFramework.m.do
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawPoints((float[]) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str32 = "drawPosText_chararray_int_int_floatarray_Paint";
        F = new GraphicsOperation(str32) { // from class: com.corrodinggames.rts.gameFramework.m.dp
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawPosText((char[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), (float[]) objArr[3], (Paint) objArr[4]);
            }
        };
        final String str33 = "drawPosText_String_floatarray_Paint";
        G = new GraphicsOperation(str33) { // from class: com.corrodinggames.rts.gameFramework.m.dq
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawPosText((String) objArr[0], (float[]) objArr[1], (Paint) objArr[2]);
            }
        };
        final String str34 = "drawRGB_int_int_int";
        H = new GraphicsOperation(str34) { // from class: com.corrodinggames.rts.gameFramework.m.dr
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawRGB(((Integer) objArr[0]).intValue(), ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
            }
        };
        final String str35 = "drawRect_float_float_float_float_Paint";
        I = new GraphicsOperation(str35) { // from class: com.corrodinggames.rts.gameFramework.m.ds
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawRect(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue(), (Paint) objArr[4]);
            }
        };
        final String str36 = "drawRect_Rect_Paint";
        J = new GraphicsOperation(str36) { // from class: com.corrodinggames.rts.gameFramework.m.dt
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawRect((Rect) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str37 = "drawRect_RectF_Paint";
        K = new GraphicsOperation(str37) { // from class: com.corrodinggames.rts.gameFramework.m.du
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawRect((RectF) objArr[0], (Paint) objArr[1]);
            }
        };
        final String str38 = "drawRoundRect_RectF_float_float_Paint";
        L = new GraphicsOperation(str38) { // from class: com.corrodinggames.rts.gameFramework.m.dv
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawRoundRect((RectF) objArr[0], ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), (Paint) objArr[3]);
            }
        };
        final String str39 = "drawText_chararray_int_int_float_float_Paint";
        M = new GraphicsOperation(str39) { // from class: com.corrodinggames.rts.gameFramework.m.dw
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawText((char[]) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Float) objArr[3]).floatValue(), ((Float) objArr[4]).floatValue(), (Paint) objArr[5]);
            }
        };
        final String str40 = "drawText_CharSequence_int_int_float_float_Paint";
        N = new GraphicsOperation(str40) { // from class: com.corrodinggames.rts.gameFramework.m.dy
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawText((CharSequence) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Float) objArr[3]).floatValue(), ((Float) objArr[4]).floatValue(), (Paint) objArr[5]);
            }
        };
        final String str41 = "drawText_String_float_float_Paint";
        O = new GraphicsOperation(str41) { // from class: com.corrodinggames.rts.gameFramework.m.dz
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawText((String) objArr[0], ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), (Paint) objArr[3]);
            }
        };
        final String str42 = "drawText_String_int_int_float_float_Paint";
        P = new GraphicsOperation(str42) { // from class: com.corrodinggames.rts.gameFramework.m.ea
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawText((String) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue(), ((Float) objArr[3]).floatValue(), ((Float) objArr[4]).floatValue(), (Paint) objArr[5]);
            }
        };
        final String str43 = "drawTextOnPath_String_Path_float_float_Paint";
        Q = new GraphicsOperation(str43) { // from class: com.corrodinggames.rts.gameFramework.m.eb
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.drawTextOnPath((String) objArr[0], (Path) objArr[1], ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue(), (Paint) objArr[4]);
            }
        };
        final String str44 = "restore";
        R = new GraphicsOperation(str44) { // from class: com.corrodinggames.rts.gameFramework.m.ec
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.restore();
            }
        };
        final String str45 = "restoreToCount_int";
        S = new GraphicsOperation(str45) { // from class: com.corrodinggames.rts.gameFramework.m.ed
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.restoreToCount(((Integer) canvasDrawCommand.b[0]).intValue());
            }
        };
        final String str46 = "rotate_float";
        T = new GraphicsOperation(str46) { // from class: com.corrodinggames.rts.gameFramework.m.ee
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.rotate(canvasDrawCommand.c);
            }
        };
        final String str47 = "rotate_float_float_float";
        U = new GraphicsOperation(str47) { // from class: com.corrodinggames.rts.gameFramework.m.ef
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.rotate(canvasDrawCommand.c, canvasDrawCommand.d, canvasDrawCommand.e);
            }
        };
        final String str48 = "save";
        V = new GraphicsOperation(str48) { // from class: com.corrodinggames.rts.gameFramework.m.eg
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.save();
            }
        };
        final String str49 = "saveLayer_float_float_float_float_Paint_int";
        W = new GraphicsOperation(str49) { // from class: com.corrodinggames.rts.gameFramework.m.eh
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.saveLayer(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue(), (Paint) objArr[4], ((Integer) objArr[5]).intValue());
            }
        };
        final String str50 = "saveLayer_RectF_Paint_int";
        X = new GraphicsOperation(str50) { // from class: com.corrodinggames.rts.gameFramework.m.ej
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.saveLayer((RectF) objArr[0], (Paint) objArr[1], ((Integer) objArr[2]).intValue());
            }
        };
        final String str51 = "saveLayerAlpha_float_float_float_float_int_int";
        Y = new GraphicsOperation(str51) { // from class: com.corrodinggames.rts.gameFramework.m.ek
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.saveLayerAlpha(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue(), ((Float) objArr[2]).floatValue(), ((Float) objArr[3]).floatValue(), ((Integer) objArr[4]).intValue(), ((Integer) objArr[5]).intValue());
            }
        };
        final String str52 = "saveLayerAlpha_RectF_int_int";
        Z = new GraphicsOperation(str52) { // from class: com.corrodinggames.rts.gameFramework.m.el
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.saveLayerAlpha((RectF) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
            }
        };
        final String str53 = "scale_float_float";
        aa = new GraphicsOperation(str53) { // from class: com.corrodinggames.rts.gameFramework.m.em
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.scale(canvasDrawCommand.c, canvasDrawCommand.d);
            }
        };
        final String str54 = "scale_float_float_float_float";
        ab = new GraphicsOperation(str54) { // from class: com.corrodinggames.rts.gameFramework.m.en
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.scale(((FloatHolder) objArr[0]).f760a, ((FloatHolder) objArr[1]).f760a, ((FloatHolder) objArr[2]).f760a, ((FloatHolder) objArr[3]).f760a);
            }
        };
        final String str55 = "setBitmap_Bitmap";
        ac = new GraphicsOperation(str55) { // from class: com.corrodinggames.rts.gameFramework.m.eo
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.setBitmap((Bitmap) canvasDrawCommand.b[0]);
            }
        };
        final String str56 = "setDensity_int";
        ad = new GraphicsOperation(str56) { // from class: com.corrodinggames.rts.gameFramework.m.ep
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.setDensity(((Integer) canvasDrawCommand.b[0]).intValue());
            }
        };
        final String str57 = "setDrawFilter_DrawFilter";
        ae = new GraphicsOperation(str57) { // from class: com.corrodinggames.rts.gameFramework.m.eq
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.setDrawFilter((DrawFilter) canvasDrawCommand.b[0]);
            }
        };
        final String str58 = "setMatrix_Matrix";
        af = new GraphicsOperation(str58) { // from class: com.corrodinggames.rts.gameFramework.m.er
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                canvas.setMatrix((Matrix) canvasDrawCommand.b[0]);
            }
        };
        final String str59 = "skew_float_float";
        ag = new GraphicsOperation(str59) { // from class: com.corrodinggames.rts.gameFramework.m.es
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.skew(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue());
            }
        };
        final String str60 = "translate_float_float";
        ah = new GraphicsOperation(str60) { // from class: com.corrodinggames.rts.gameFramework.m.eu
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                canvas.translate(((Float) objArr[0]).floatValue(), ((Float) objArr[1]).floatValue());
            }
        };
        final String str61 = "runDrawTimeCallback_DrawTimeCallback";
        ai = new GraphicsOperation(str61) { // from class: com.corrodinggames.rts.gameFramework.m.ev
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                DeferredGraphicsRenderer deferredGraphicsRenderer = (DeferredGraphicsRenderer) objArr[0];
                if (deferredGraphicsRenderer.b == null) {
                    deferredGraphicsRenderer.b = new AndroidGraphicsContext().a();
                }
                deferredGraphicsRenderer.b.a(new CanvasGraphicsRenderer(canvas));
                ((com.corrodinggames.rts.gameFramework.android.graphics.GraphicsOperation) objArr[1]).a(deferredGraphicsRenderer.b);
            }
        };
        final String str62 = "runDrawTimeCallback_DrawTimeCallback_float_float_float_paint";
        aj = new GraphicsOperation(str62) { // from class: com.corrodinggames.rts.gameFramework.m.ew
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                DeferredGraphicsRenderer deferredGraphicsRenderer = (DeferredGraphicsRenderer) objArr[0];
                if (deferredGraphicsRenderer.b == null) {
                    deferredGraphicsRenderer.b = new AndroidGraphicsContext().a();
                }
                deferredGraphicsRenderer.b.a(new CanvasGraphicsRenderer(canvas));
                ((Float) objArr[2]).floatValue();
                ((Float) objArr[3]).floatValue();
                ((Float) objArr[4]).floatValue();
            }
        };
        final String str63 = "enterLock_proxy_object";
        ak = new GraphicsOperation(str63) { // from class: com.corrodinggames.rts.gameFramework.m.ex
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                DeferredGraphicsRenderer deferredGraphicsRenderer = (DeferredGraphicsRenderer) objArr[0];
                Lock lock = (Lock) objArr[1];
                lock.lock();
                deferredGraphicsRenderer.i.add(lock);
            }
        };
        final String str64 = "leaveLock_proxy_object";
        al = new GraphicsOperation(str64) { // from class: com.corrodinggames.rts.gameFramework.m.ey
            @Override // com.corrodinggames.rts.gameFramework.android.graphics.opengl.GraphicsOperation
            public final void a(Canvas canvas, CanvasDrawCommand canvasDrawCommand) {
                Object[] objArr = canvasDrawCommand.b;
                DeferredGraphicsRenderer deferredGraphicsRenderer = (DeferredGraphicsRenderer) objArr[0];
                Lock lock = (Lock) objArr[1];
                lock.unlock();
                if (deferredGraphicsRenderer.i.remove(lock)) {
                    return;
                }
                GameEngine.logColored("removeLock lock was not active");
            }
        };
        am = new GraphicsOperation[]{f756a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, aa, ab, ac, ad, ae, af, ag, ah, ai, aj, ak, al};
    }
}
