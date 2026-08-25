package com.corrodinggames.rts.gameFramework.gl;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.gl.font.FontRenderer;
import com.corrodinggames.rts.gameFramework.gl.renderer.VertexBatchRenderer;
import com.corrodinggames.rts.gameFramework.graphics.OpenGLGraphicsRenderer;
import com.corrodinggames.rts.gameFramework.graphics.ShaderProgram;
import com.corrodinggames.rts.gameFramework.graphics.ShaderUniform;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/n.class */
public class OpenGLRenderer implements IGraphicsEngine {
    public TextureAtlas a;
    private Map J;
    private float[] K;
    private int L;
    int b;
    int c;
    public float[] d;
    private int M;
    private int N;
    private int O;
    private int P;
    private int Q;
    private int R;
    private IShaderProgram S;
    private ShaderParameter T;
    ShaderVariable[] e;
    ShaderVariable[] f;
    ShaderVariable[] g;
    private  IntArrayBuffer U;
    private int[] V;
    private ArrayList W;
    private  float[] X;
    private  float[] Y;
    private  RectF Z;
    private  RectF aa;
    private  int[] ab;
    boolean h;
    boolean i;
    TextureLayer j;
    VertexBatchRenderer k;
    TextureManager l;
    float[] m;
    TransformState n;
    public int o;
    FastArrayList p;
    static RectF q;
    int r;
    boolean s;
    boolean t;
    int u;
    ShaderProgram v;
    public int w;
    static int x;
    static int y;
    IRenderCallback z;
    FastArrayList<ShaderContext> C;
    ShaderContext D;
    public static OpenGLGraphicsRenderer E;
    FloatBuffer F;
    static final String G = OpenGLRenderer.class.getSimpleName();
    private static final float[] H = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};
    private static final float[] I = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    private static final ITextureResource ac = new TextureResource();
    public static boolean A = false;
    static int B = 0;

    private int a(int i, int i2, ShaderVariable[] shaderVariableArr, int[] iArr) {
        int iGlCreateProgram = GLES20.glCreateProgram();
        r();
        if (iGlCreateProgram == 0) {
            throw new RuntimeException("Cannot create GL program: " + GLES20.glGetError());
        }
        GLES20.glAttachShader(iGlCreateProgram, i);
        r();
        GLES20.glAttachShader(iGlCreateProgram, i2);
        r();
        GLES20.glLinkProgram(iGlCreateProgram);
        r();
        GLES20.glGetProgramiv(iGlCreateProgram, 35714, iArr, 0);
        if (iArr[0] != 1) {
            Log.d(G, "======= ERROR =========");
            Log.d(G, "Could not link program: ");
            String strGlGetProgramInfoLog = GLES20.glGetProgramInfoLog(iGlCreateProgram);
            Log.d(G, strGlGetProgramInfoLog);
            GLES20.glDeleteProgram(iGlCreateProgram);
            throw new RuntimeException("Cannot link GL program: " + strGlGetProgramInfoLog);
        }
        a(shaderVariableArr, iGlCreateProgram);
        return iGlCreateProgram;
    }

    private static void a(ShaderVariable[] shaderVariableArr, int i) {
        for (ShaderVariable shaderVariable : shaderVariableArr) {
            shaderVariable.a(i);
        }
    }

    private static String a(int i, String str) {
        StringBuffer stringBuffer = new StringBuffer();
        boolean z = false;
        for (String str2 : str.split("\n")) {
            if (!z && str2.contains("version")) {
                z = true;
            } else {
                if (z) {
                }
                if (!str2.equals(str2)) {
                    GameEngine.log("Changing: " + str2 + " to " + str2);
                }
                stringBuffer.append(str2);
                stringBuffer.append("\n");
            }
        }
        if (!z) {
        }
        return stringBuffer.toString();
    }

    private static int b(int i, String str) {
        int iGlCreateShader = GLES20.glCreateShader(i);
        if (str == null) {
            throw new RuntimeException("Shader Compilation Failed: shaderCode==null");
        }
        GLES20.glShaderSource(iGlCreateShader, a(i, str));
        q();
        GLES20.glCompileShader(iGlCreateShader);
        q();
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(iGlCreateShader, 35713, iArr, 0);
        if (iArr[0] == 0) {
            String str2 = "Shader Compilation Failed: " + GLES20.glGetShaderInfoLog(iGlCreateShader);
            Log.d(G, str2);
            throw new RuntimeException(str2);
        }
        return iGlCreateShader;
    }

    public void a(int i, int i2) {
        this.b = i;
        this.c = i2;
        q();
        Matrix.setIdentityM(this.K, this.L);
        Matrix.orthoM(this.d, 0, 0.0f, i, 0.0f, i2, -1.0f, 1.0f);
        q();
        if (t() == null) {
            this.M = i;
            this.N = i2;
            Matrix.translateM(this.K, this.L, 0.0f, i2, 0.0f);
            Matrix.scaleM(this.K, this.L, 1.0f, -1.0f, 1.0f);
            q();
        }
        GLES20.glViewport(0, 0, this.b, this.c);
        g();
    }

    public void a(int i, ShaderVariable[] shaderVariableArr) {
        q();
        f();
        q();
        a(i);
        GLES20.glUniformMatrix4fv(shaderVariableArr[5].a, 1, false, this.d, 0);
        q();
    }

    public void g() {
        if (this.O != 0) {
            a(this.O, this.e);
        }
        if (this.P != 0) {
            a(this.P, this.f);
        }
        if (this.Q != 0) {
            a(this.Q, this.g);
        }
    }

    public void a(float[] fArr) {
        GLES20.glClearColor(fArr[1], fArr[2], fArr[3], fArr[0]);
        q();
        GLES20.glClear(16384);
        q();
    }

    public float h() {
        return 1.0f;
    }

    public void a(float f, float f2) {
        int i = this.L;
        float[] fArr = this.K;
        int i2 = i + 12;
        fArr[i2] = fArr[i2] + (fArr[i + 0] * f) + (fArr[i + 4] * f2);
        int i3 = i + 13;
        fArr[i3] = fArr[i3] + (fArr[i + 1] * f) + (fArr[i + 5] * f2);
        int i4 = i + 14;
        fArr[i4] = fArr[i4] + (fArr[i + 2] * f) + (fArr[i + 6] * f2);
        int i5 = i + 15;
        fArr[i5] = fArr[i5] + (fArr[i + 3] * f) + (fArr[i + 7] * f2);
    }

    public void a(float f, float f2, float f3) {
        Matrix.scaleM(this.K, this.L, f, f2, f3);
    }

    public void a(float f) {
        if (f == 0.0f) {
            return;
        }
        a(this.K, this.L, f);
    }

    public final void a(float[] fArr, int i, float f) {
        float fFastSin = Utility.fastSin(f);
        float fFastCos = Utility.fastCos(f);
        if (f == 90.0f) {
            fFastSin = 1.0f;
            fFastCos = 0.0f;
        }
        float f2 = fArr[0 + i];
        float f3 = fArr[1 + i];
        float f4 = fArr[2 + i];
        float f5 = fArr[3 + i];
        float f6 = fArr[4 + i];
        float f7 = fArr[5 + i];
        float f8 = fArr[6 + i];
        float f9 = fArr[7 + i];
        float f10 = (f2 * fFastCos) + (f6 * fFastSin);
        float f11 = (f3 * fFastCos) + (f7 * fFastSin);
        float f12 = (f4 * fFastCos) + (f8 * fFastSin);
        float f13 = (f5 * fFastCos) + (f9 * fFastSin);
        int i2 = 0 + i;
        fArr[i2 + 0] = f10;
        fArr[i2 + 1] = f11;
        fArr[i2 + 2] = f12;
        fArr[i2 + 3] = f13;
        float f14 = (f2 * (-fFastSin)) + (f6 * fFastCos);
        float f15 = (f3 * (-fFastSin)) + (f7 * fFastCos);
        float f16 = (f4 * (-fFastSin)) + (f8 * fFastCos);
        float f17 = (f5 * (-fFastSin)) + (f9 * fFastCos);
        int i3 = 4 + i;
        fArr[i3 + 0] = f14;
        fArr[i3 + 1] = f15;
        fArr[i3 + 2] = f16;
        fArr[i3 + 3] = f17;
    }

    public float[] i() {
        float[] fArr = this.K;
        int i = this.L;
        float[] fArr2 = this.m;
        for (int i2 = 0; i2 < 16; i2++) {
            fArr2[i2] = fArr[i + i2];
        }
        return fArr2;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void b() {
        if (1 != 0) {
            int i = this.L;
            this.L += 16;
            if (this.K.length <= this.L + 16) {
                this.K = Arrays.copyOf(this.K, this.K.length * 2);
            }
            for (int i2 = 0; i2 < 16; i2++) {
                this.K[this.L + i2] = this.K[i + i2];
            }
        }
        j();
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void c() {
        this.L -= 16;
        if (this.L < 0) {
            GameEngine.logWarningAndStack("restore: error mCurrentMatrixIndex: " + this.L);
            this.L = 0;
        }
        k();
    }

    public void j() {
        FastArrayList fastArrayList = this.p;
        this.o++;
        if (this.o >= fastArrayList.size) {
            fastArrayList.add(new TransformState());
        }
        TransformState transformState = (TransformState) fastArrayList.get(this.o);
        this.n.a(transformState);
        this.n = transformState;
    }

    public void k() {
        FastArrayList fastArrayList = this.p;
        this.o--;
        if (this.o < 0) {
            GameEngine.logWarningAndStack("popTransformStack: error currentTransformIndex: " + this.o);
            fastArrayList.set(0, new TransformState());
            this.o = 0;
        }
        this.n = (TransformState) fastArrayList.get(this.o);
        a(false);
    }

    public void a(boolean z) {
        RectF rectF = this.n.a;
        if (q == rectF && !z) {
            return;
        }
        e();
        if (rectF != null) {
            GLES20.glEnable(3089);
            GLES20.glScissor((int) rectF.a, (int) (this.N - rectF.d), (int) rectF.b(), (int) rectF.c());
        } else {
            GLES20.glDisable(3089);
        }
        q = rectF;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(float f, float f2, float f3, PaintStyle paintStyle, IShaderProgram iShaderProgram) {
        a(iShaderProgram);
        a(5, 0, 4, f, f2, 2.0f * f3, 2.0f * f3, paintStyle.a(), 0.0f);
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(float f, float f2, float f3, float f4, PaintStyle paintStyle, IShaderProgram iShaderProgram) {
        n();
        q();
        this.k.b(1);
        if (paintStyle == null) {
            this.k.a(-1);
        } else {
            this.k.a(paintStyle.a());
        }
        float fB = paintStyle.b();
        if (fB > 0.0f) {
            this.k.a(fB * this.K[this.L]);
        } else {
            this.k.a(1.0f);
        }
        q();
        float[] fArrI = i();
        this.k.a(f, f2, fArrI);
        this.k.a(f3, f4, fArrI);
        q();
    }

    private void a(int i, int i2, int i3, float f, float f2, float f3, float f4, int i4, float f5) {
        a(i2, i4, f5);
        if (this.T != null) {
            this.T.a(this.O, this.S);
        }
        a(this.e, i, i3, f, f2, f3, f4, null);
    }

    public void a(int i) {
        if (this.r != i) {
            GLES20.glUseProgram(i);
            q();
            this.t = false;
            this.r = i;
        }
    }

    private void a(int i, int i2, float f) {
        f();
        a(this.O);
        if (f > 0.0f) {
            GLES20.glLineWidth(f * this.K[this.L]);
            q();
        } else {
            GLES20.glLineWidth(1.0f);
            q();
        }
        float[] fArrB = b(i2);
        b(true);
        GLES20.glBlendColor(fArrB[0], fArrB[1], fArrB[2], fArrB[3]);
        q();
        GLES20.glUniform4fv(this.e[2].a, 1, fArrB, 0);
        b(this.e, i);
        q();
    }

    float[] b(int i) {
        float fH = ((i >>> 24) & 255) * 0.003921569f * h();
        float f = ((i >>> 16) & 255) * 0.003921569f * fH;
        this.Y[0] = f;
        this.Y[1] = ((i >>> 8) & 255) * 0.003921569f * fH;
        this.Y[2] = (i & 255) * 0.003921569f * fH;
        this.Y[3] = fH;
        return this.Y;
    }

    private void b(boolean z) {
        if (z) {
            if (!this.s) {
                GLES20.glEnable(3042);
                q();
                this.s = true;
                return;
            }
            return;
        }
        if (this.s) {
            GLES20.glDisable(3042);
            q();
            this.s = false;
        }
    }

    public void l() {
        b((ShaderProgram) null);
        this.t = false;
        this.r = -1;
        this.z = null;
        this.s = false;
        r();
        f();
    }

    private void b(ShaderVariable[] shaderVariableArr, int i) {
        if (!this.t || this.u != i) {
            GLES20.glBindBuffer(34962, this.R);
            q();
            GLES20.glVertexAttribPointer(shaderVariableArr[0].a, 2, 5126, false, 8, i * 8);
            q();
            GLES20.glBindBuffer(34962, 0);
            q();
            this.t = true;
            this.u = i;
        }
    }

    private void a(ShaderVariable[] shaderVariableArr, int i, int i2, float f, float f2, float f3, float f4, MatrixCalculator matrixCalculator) {
        a(shaderVariableArr, f, f2, f3, f4, matrixCalculator);
        int i3 = shaderVariableArr[0].a;
        GLES20.glEnableVertexAttribArray(i3);
        q();
        GLES20.glDrawArrays(i, 0, i2);
        q();
        GLES20.glDisableVertexAttribArray(i3);
        q();
    }

    private void a(ShaderVariable[] shaderVariableArr, float f, float f2, float f3, float f4, MatrixCalculator matrixCalculator) {
        if (matrixCalculator != null) {
            GLES20.glUniformMatrix4fv(shaderVariableArr[1].a, 1, false, matrixCalculator.a(this.M, this.N, f, f2, f3, f4), 0);
            q();
        } else {
            Matrix.translateM(this.X, 0, this.K, this.L, f, f2, 0.0f);
            Matrix.scaleM(this.X, 0, f3, f4, 1.0f);
            GLES20.glUniformMatrix4fv(shaderVariableArr[1].a, 1, false, this.X, 0);
            q();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(Texture texture, int i, int i2, int i3, int i4, ITextureFilter iTextureFilter, MatrixCalculator matrixCalculator) {
        if (i3 <= 0 || i4 <= 0) {
            return;
        }
        a(texture.g(), iTextureFilter);
        TextureUtils.a(texture, this.Z);
        this.aa.a(i, i2, i + i3, i2 + i4);
        texture.a(this.Z);
        texture.a(this.Z, this.aa);
        a(texture, this.Z, this.aa, matrixCalculator);
    }

    public void a(ShaderProgram shaderProgram) {
        if (this.v == shaderProgram) {
            return;
        }
        if (shaderProgram == null && this.v != null && this.v.a()) {
            if (this.v.b()) {
                e();
                o();
                return;
            }
            return;
        }
        b(shaderProgram);
    }

    public void b(ShaderProgram shaderProgram) {
        if (this.v == shaderProgram) {
            return;
        }
        f();
        if (shaderProgram != null) {
            if (shaderProgram.n == 0) {
                d(shaderProgram);
            }
            if (shaderProgram.n == 0) {
                this.v = null;
                return;
            }
            this.j.j = (ShaderLocations) shaderProgram.q;
        }
        if (shaderProgram == null) {
            this.j.j = this.j.i;
        }
        this.v = shaderProgram;
    }

    public void c(ShaderProgram shaderProgram) {
        for (ShaderUniform shaderUniform : shaderProgram.uniforms) {
            if (shaderUniform.isDirty || shaderUniform.texture != null) {
                shaderUniform.isDirty = false;
                if (shaderUniform.b == -1) {
                    shaderUniform.b = GLES20.glGetUniformLocation(shaderProgram.n, shaderUniform.name);
                    if (shaderUniform.b == -1 && !shaderUniform.d) {
                        shaderUniform.d = true;
                        shaderProgram.b("Unknown parameter: " + shaderUniform.name);
                        return;
                    }
                }
                if (shaderUniform.texture != null) {
                    Texture textureA = this.l.a(shaderUniform.texture.b(), shaderUniform.texture);
                    if (shaderUniform.g) {
                        GLES20.glUniform2f(shaderUniform.b, textureA.e, textureA.f);
                    } else {
                        int iA = textureA.a();
                        GLES20.glActiveTexture(33985);
                        GLES20.glBindTexture(3553, iA);
                        GLES20.glUniform1i(shaderUniform.b, 1);
                        GLES20.glActiveTexture(33984);
                    }
                } else if (shaderUniform.floatValues.length == 1) {
                    GLES20.glUniform1f(shaderUniform.b, shaderUniform.floatValues[0]);
                } else if (shaderUniform.floatValues.length == 2) {
                    GLES20.glUniform2f(shaderUniform.b, shaderUniform.floatValues[0], shaderUniform.floatValues[1]);
                } else if (shaderUniform.floatValues.length == 4) {
                    GLES20.glUniform4f(shaderUniform.b, shaderUniform.floatValues[0], shaderUniform.floatValues[1], shaderUniform.floatValues[2], shaderUniform.floatValues[3]);
                } else {
                    shaderProgram.b("Unhandled parameter size: " + shaderUniform.name + " - " + shaderUniform.floatValues.length);
                }
            }
        }
    }

    public boolean d(ShaderProgram shaderProgram) {
        if (shaderProgram.programStatus != 0) {
            return false;
        }
        if (shaderProgram.n != 0 && !shaderProgram.m) {
            return true;
        }
        r();
        shaderProgram.m = false;
        shaderProgram.b("== Compiling shader ==");
        ShaderLocations shaderLocations = new ShaderLocations();
        shaderProgram.q = shaderLocations;
        try {
            shaderProgram.n = a(shaderLocations.f, this.j.h.a(), shaderProgram.fragmentSource);
        } catch (RuntimeException e) {
            shaderProgram.c("Failed to compile shader: " + e.getMessage());
            e.printStackTrace();
            shaderProgram.programStatus = 1;
        }
        if (shaderProgram.programStatus != 0 && shaderProgram.n == 0) {
            shaderProgram.c("Shader program_handle == 0");
            shaderProgram.programStatus = 1;
        }
        r();
        return true;
    }

    public void a(Texture texture, RectF rectF, RectF rectF2, ITextureFilter iTextureFilter, MatrixCalculator matrixCalculator) {
        if (rectF2.b() <= 0.0f || rectF2.c() <= 0.0f) {
            return;
        }
        a(texture.g(), iTextureFilter);
        this.Z.a(rectF);
        this.aa.a(rectF2);
        texture.a(this.Z);
        texture.a(this.Z, this.aa);
        a(texture, this.Z, this.aa, matrixCalculator);
    }

    private void a(Texture texture, RectF rectF, RectF rectF2, MatrixCalculator matrixCalculator) {
        m();
        this.j.a(this.w);
        this.j.a(texture, rectF, rectF2, i());
    }

    public void m() {
        if (!this.h) {
            if (this.i) {
                f();
            }
            b(true);
            this.j.a(this.v);
            this.h = true;
        }
    }

    public void n() {
        if (!this.i) {
            if (this.h) {
                f();
            }
            b(true);
            this.k.a((ShaderProgram) null);
            this.i = true;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void f() {
        if (this.h) {
            this.j.d();
            this.h = false;
        }
        if (this.i) {
            this.k.c();
            this.i = false;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void e() {
        if (this.h) {
            this.j.c();
        }
        if (this.i) {
            this.k.b();
        }
    }

    public void o() {
        this.j.b();
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public boolean a(Texture texture) {
        boolean zI = texture.i();
        if (zI) {
            c(texture.e, texture.f);
            synchronized (this.U) {
                this.U.a(texture.a());
            }
        }
        return zI;
    }

    public static void b(int i, int i2) {
        x++;
        y += i * i2 * 4;
    }

    public static void c(int i, int i2) {
        x--;
        y -= (i * i2) * 4;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void d() {
        a((Texture) this.W.remove(this.W.size() - 1), t());
        c();
        l();
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void c(Texture texture) {
        b();
        Texture textureT = t();
        this.W.add(texture);
        a(textureT, texture);
    }

    private Texture t() {
        return (Texture) this.W.get(this.W.size() - 1);
    }

    private void a(Texture texture, Texture texture2) {
        f();
        if (texture == null && texture2 != null) {
            if (texture2.g() == 3553) {
                GLES20.glGenFramebuffers(1, this.V, 0);
                q();
                GLES20.glBindFramebuffer(36160, this.V[0]);
                q();
            } else {
                GLES11Ext.glGenFramebuffersOES(1, this.V, 0);
                q();
                GLES11Ext.glBindFramebufferOES(36160, this.V[0]);
                q();
            }
        } else if (texture != null && texture2 == null) {
            if (texture.g() == 3553) {
                GLES20.glBindFramebuffer(36160, 0);
                q();
                GLES20.glDeleteFramebuffers(1, this.V, 0);
                q();
            } else {
                GLES11Ext.glBindFramebufferOES(36160, 0);
                q();
                GLES11Ext.glDeleteFramebuffersOES(1, this.V, 0);
                q();
            }
        }
        if (texture2 == null) {
            a(this.M, this.N);
            return;
        }
        a(texture2.b(), texture2.c());
        if (!texture2.i()) {
            texture2.b(this);
        }
        if (texture2.g() == 3553) {
            GLES20.glFramebufferTexture2D(36160, 36064, texture2.g(), texture2.a(), 0);
            q();
            u();
        } else {
            GLES11Ext.glFramebufferTexture2DOES(36160, 36064, texture2.g(), texture2.a(), 0);
            q();
            v();
        }
    }

    private static void u() {
        int iGlCheckFramebufferStatus = GLES20.glCheckFramebufferStatus(36160);
        if (iGlCheckFramebufferStatus != 36053) {
            String str = VariableScope.nullOrMissingString;
            switch (iGlCheckFramebufferStatus) {
                case 36054:
                    str = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
                    break;
                case 36055:
                    str = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
                    break;
                case 36057:
                    str = "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS";
                    break;
                case 36061:
                    str = "GL_FRAMEBUFFER_UNSUPPORTED";
                    break;
            }
            throw new RuntimeException(str + ":" + Integer.toHexString(iGlCheckFramebufferStatus));
        }
    }

    private static void v() {
        int iGlCheckFramebufferStatusOES = GLES11Ext.glCheckFramebufferStatusOES(36160);
        if (iGlCheckFramebufferStatusOES != 36053) {
            String str = VariableScope.nullOrMissingString;
            switch (iGlCheckFramebufferStatusOES) {
                case 36054:
                    str = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
                    break;
                case 36055:
                    str = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
                    break;
                case 36057:
                    str = "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS";
                    break;
                case 36061:
                    str = "GL_FRAMEBUFFER_UNSUPPORTED";
                    break;
            }
            throw new RuntimeException(str + ":" + Integer.toHexString(iGlCheckFramebufferStatusOES));
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void d(Texture texture) {
        int iG = texture.g();
        b(texture);
        q();
        GLES20.glTexParameteri(iG, 10242, 33071);
        GLES20.glTexParameteri(iG, 10243, 33071);
        int iH = texture.h();
        GLES20.glTexParameterf(iG, 10241, iH);
        GLES20.glTexParameterf(iG, 10240, iH);
    }

    public void a(Texture texture, int i) {
        if (texture.h() != i && texture.a() != -1) {
            b(texture);
            texture.b(i);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void b(Texture texture) {
        if (this.z == texture) {
        }
        e();
        GLES20.glBindTexture(texture.g(), texture.a());
        this.z = texture;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(Texture texture, int i, int i2, int i3) {
        int iG = texture.g();
        b(texture);
        q();
        GLES20.glTexImage2D(iG, 0, i3, texture.d(), texture.e(), 0, i, i2, null);
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(Texture texture, Bitmap bitmap, int i) {
        int iG = texture.g();
        b(texture);
        q();
        if (i == 0) {
            GLUtils.texImage2D(iG, 0, bitmap, 0);
        } else {
            GLUtils.texImage2D(iG, 0, i, bitmap, 0);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(Texture texture, int i, int i2, Bitmap bitmap, int i3, int i4) {
        int iG = texture.g();
        b(texture);
        q();
        GLUtils.texSubImage2D(iG, 0, i, i2, bitmap, i3, i4);
    }

    public static void a(String str, Throwable th) {
        if (B > 1000) {
            return;
        }
        B++;
        if (th != null) {
            Log.b(G, str, th);
        } else {
            Log.d(G, str);
        }
    }

    public static void p() {
        int i;
        int i2 = 255;
        int iGlGetError = GLES20.glGetError();
        while (true) {
            i = iGlGetError;
            if (i == 0 || i2 <= 0) {
                break;
            }
            i2--;
            iGlGetError = GLES20.glGetError();
        }
        if (i != 0) {
            a("clearGlError: Failed to clear", (Throwable) null);
        }
    }

    public static void q() {
        int iGlGetError;
        if (A && (iGlGetError = GLES20.glGetError()) != 0) {
            a("GL error: " + iGlGetError, new Throwable());
            p();
        }
    }

    public static void r() {
        int iGlGetError = GLES20.glGetError();
        if (iGlGetError != 0) {
            a("GL error: " + iGlGetError, new Throwable());
            p();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public ITextureResource a() {
        return ac;
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(int i, int i2, int i3, int i4) {
        float f = this.K[this.L];
        this.n.a = new RectF();
        this.n.a.a = i;
        this.n.a.c = i3;
        this.n.a.b = i2;
        this.n.a.d = i4;
        this.n.a.a *= f;
        this.n.a.c *= f;
        this.n.a.b *= f;
        this.n.a.d *= f;
        a(false);
    }

    private void a(IShaderProgram iShaderProgram) {
        if (iShaderProgram == null) {
            throw new NullPointerException("draw shape filter is null.");
        }
        this.S = iShaderProgram;
        if (this.J.containsKey(iShaderProgram)) {
            int i = this.O;
            this.O = ((Integer) this.J.get(iShaderProgram)).intValue();
            a(this.e, this.O);
            if (i != this.O) {
                a(this.O, this.e);
                return;
            }
            return;
        }
        this.O = a(this.e, iShaderProgram.a(), iShaderProgram.b());
        a(this.O, this.e);
        this.J.put(iShaderProgram, Integer.valueOf(this.O));
    }

    private void a(int i, ITextureFilter iTextureFilter) {
        if (iTextureFilter == null) {
            throw new NullPointerException("Texture filter is null.");
        }
    }

    public int a(ShaderVariable[] shaderVariableArr, String str, String str2) {
        return a(b(35633, str), b(35632, str2), shaderVariableArr, this.ab);
    }

    public ShaderContext a(int i, boolean z, boolean z2) {
        GameEngine.log("Loading new font size:" + i + " bold:" + z + " unicode:" + z2);
        try {
            ShaderContext shaderContext = new ShaderContext(this);
            shaderContext.a = i;
            shaderContext.b = z;
            Paint paint = new Paint();
            paint.c(true);
            paint.a(true);
            if (!z) {
                paint.a(Typeface.a(Typeface.c, 0));
            } else {
                paint.a(Typeface.a(Typeface.c, 1));
            }
            shaderContext.c = new FontRenderer(null);
            shaderContext.c.a(paint, i, 3, 2);
            shaderContext.c.a(true);
            shaderContext.c.a(12);
            this.C.add(shaderContext);
            return shaderContext;
        } catch (OutOfMemoryError e) {
            GameEngine.reportOOM(AssetType.gameFont, e);
            ShaderContext shaderContext2 = new ShaderContext(this);
            shaderContext2.a = i;
            shaderContext2.b = z;
            if (this.D != null) {
                shaderContext2.c = this.D.c;
            }
            this.C.add(shaderContext2);
            return shaderContext2;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(String str, float f, float f2, Paint paint) {
        f();
        boolean zA = false;
        Typeface typefaceI = paint.i();
        if (typefaceI != null) {
            zA = typefaceI.a();
        }
        int iK = (int) paint.k();
        if (iK > 42) {
            iK = 42;
        }
        if (iK < 10) {
            iK = 10;
        }
        if (iK % 2 != 0) {
        }
        boolean zRemoveBadChars = Utility.containsNonAscii(str);
        if (zRemoveBadChars) {
            iK = 24;
        }
        if (this.D == null) {
            this.D = a(24, false, true);
        }
        ShaderContext shaderContextA = null;
        for (ShaderContext shaderContext : this.C) {
            if (shaderContext.a == iK && shaderContext.b == zA) {
                shaderContextA = shaderContext;
            }
        }
        if (shaderContextA == null) {
            shaderContextA = a(iK, zA, zRemoveBadChars);
        }
        if (shaderContextA.c == null) {
            a("font.glText==null", (Throwable) null);
            return;
        }
        FontRenderer fontRenderer = shaderContextA.c;
        r();
        int iE = paint.e();
        float fA = Color.a(iE) * 0.003921569f;
        float fB = Color.b(iE) * 0.003921569f * fA;
        float fC = Color.c(iE) * 0.003921569f * fA;
        float fD = Color.d(iE) * 0.003921569f * fA;
        float fK = paint.k();
        if (fK != shaderContextA.a) {
        }
        fontRenderer.a(fB, fC, fD, fA, this.d);
        fontRenderer.a(fK / shaderContextA.a);
        r();
        if (paint.j() == Paint.Align.CENTER) {
            fontRenderer.a(str, f - ((int) (fontRenderer.a(str) * 0.5f)), this.c - f2);
        } else {
            fontRenderer.a(str, f, this.c - f2, 0.0f);
        }
        r();
        fontRenderer.d();
        l();
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(float[] fArr, int i, int i2, PaintStyle paintStyle, IShaderProgram iShaderProgram) {
        if (this.F == null || this.F.capacity() < i2 * 4) {
            ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect((i2 * 4) + 10);
            byteBufferAllocateDirect.order(ByteOrder.nativeOrder());
            this.F = byteBufferAllocateDirect.asFloatBuffer();
        }
        this.F.clear();
        this.F.put(fArr, i, i2);
        this.F.flip();
        this.F.position(0);
        float fB = paintStyle.b();
        int iA = paintStyle.a();
        if (fB == 0.0f) {
            fB = 1.0f;
        }
        a(iShaderProgram);
        a(0, iA, fB);
        if (this.T != null) {
            this.T.a(this.O, this.S);
        }
        ShaderVariable[] shaderVariableArr = this.e;
        int i3 = shaderVariableArr[0].a;
        GLES20.glEnableVertexAttribArray(i3);
        q();
        GLES20.glVertexAttribPointer(i3, 2, 5126, false, 0, (Buffer) this.F);
        a(shaderVariableArr, 0.0f, 0.0f, 1.0f, 1.0f, (MatrixCalculator) null);
        GLES20.glDrawArrays(0, 0, i2 / 2);
        q();
        GLES20.glDisableVertexAttribArray(i3);
        q();
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IGraphicsEngine
    public void a(Bitmap bitmap) {
        if (this.a != null) {
            this.a.b(bitmap);
        }
    }
}
