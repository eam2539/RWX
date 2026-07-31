package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.*;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.LogicNumberFuntion;
import com.corrodinggames.rts.gameFramework.gl.font.FontRenderer;
import com.corrodinggames.rts.gameFramework.gl.font.GlyphInfo;
import com.corrodinggames.rts.gameFramework.gl.font.TextRenderer;
import com.corrodinggames.rts.gameFramework.m.TextureSettings;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.t */
/* JADX INFO: loaded from: classes.dex */
public class OpenGLRenderer implements GraphicsRenderer {
    private static int debugPointDrawCount;
    public static int G;
    public static int H;
    public static OpenGLGraphicsRenderer O;
    static RectF x;
    TextureInterface I;
    RenderState M;
    public boolean N;
    FloatBuffer P;
    private int Z;

    /* JADX INFO: renamed from: a */
    public TextureManager f572a;
    private int aa;
    private int ab;
    private int ac;
    private int ad;
    private ShaderInterface ae;
    private ShaderCompileInterface af;
    private ShaderLoadInterface ag;
    int b;
    int c;
    public int e;
    public int f;
    boolean n;
    boolean o;
    BatchRenderer p;
    LineBatchRenderer q;
    GraphicsEngine r;
    public static final String Q = OpenGLRenderer.class.getSimpleName();
    private static final float[] R = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};
    private static final float[] S = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    public static final ResourceManagerInterface m = new OpenGLResourceManager();
    public static boolean J = false;
    static int K = 0;
    private Map T = new HashMap();
    private Map U = new HashMap();
    private Map V = new HashMap();
    private float[] W = new float[128];
    private int X = 0;
    private int Y = 0;
    float[] d = new float[16];
    ShaderHandleBase[] g = {new AttributeHandle("aPosition"), new UniformHandle("uMatrix"), new UniformHandle("uColor"), new EmptyHandle(), new EmptyHandle(), new UniformHandle("uProjection")};
    ShaderHandleBase[] h = {new AttributeHandle("aPosition"), new UniformHandle("uMatrix"), new UniformHandle("uTextureMatrix"), new UniformHandle("u_texture"), new UniformHandle("uColor"), new UniformHandle("uProjection")};
    ShaderHandleBase[] i = {new AttributeHandle("aPosition"), new UniformHandle("uMatrix"), new UniformHandle("uTextureMatrix"), new UniformHandle("u_texture"), new UniformHandle("uColor"), new UniformHandle("uProjection")};
    ShaderHandleBase[] j = {new AttributeHandle("aPosition"), new UniformHandle("uMatrix"), new AttributeHandle("v_texCoords"), new UniformHandle("u_texture"), new UniformHandle("uColor"), new UniformHandle("uProjection")};
    public final BlendModeState k = new BlendModeState();
    public final BlendModeState l = new BlendModeState();
    private int ah = 0;
    private int ai = 0;
    private int aj = 0;
    private int ak = 0;
    private int[] al = new int[1];
    private ArrayList am = new ArrayList();
    private final float[] an = new float[32];
    private final float[] ao = new float[4];
    private final RectF ap = new RectF();
    private final RectF aq = new RectF();
    private final float[] ar = new float[16];
    private final int[] as = new int[1];
    float[] s = new float[16];
    public ClipRegion t = new ClipRegion();
    public int u = 0;
    FastArrayList v = new FastArrayList();
    FastArrayList w = new FastArrayList();
    int y = -1;
    boolean z = false;
    boolean A = false;
    int B = 0;
    int C = -1;
    C0009fo D = null;
    public int E = -1;
    public Paint F = new Paint();
    FastArrayList L = new FastArrayList();
    private int blendMode = BlendPaint.BLEND_NORMAL;

    public OpenGLRenderer(GraphicsEngine graphicsEngine) {
        this.r = graphicsEngine;
        Matrix.setIdentityM(this.ar, 0);
        Matrix.setIdentityM(this.W, this.Y);
        this.am.add(null);
        this.v.add(new ClipRegion());
        float[] fArr = R;
        FloatBuffer floatBufferAsFloatBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBufferAsFloatBuffer.put(fArr, 0, fArr.length).position(0);
        m.a(this.as);
        j();
        int i = this.as[0];
        GLES20.glBindBuffer(34962, i);
        j();
        GLES20.glBufferData(34962, floatBufferAsFloatBuffer.capacity() * 4, floatBufferAsFloatBuffer, 35044);
        j();
        this.ad = i;
        this.Z = a(a(35633, "uniform mat4 uMatrix;\nuniform mat4 uProjection;\nattribute vec2 aPosition;\nvoid main() {\n  vec4 pos = vec4(aPosition, 0.0, 1.0);\n  gl_Position = uProjection * uMatrix * pos;\n}\n"), a(35632, "precision mediump float;\nuniform vec4 uColor;\nvoid main() {\n  gl_FragColor = uColor;\n}\n"), this.g, this.as);
        a(this.Z, this.g);
        this.ac = a(a(35633, "uniform mat4 uMatrix;\nuniform mat4 uProjection;\nattribute vec2 aPosition;\nattribute vec2 v_texCoords;\nvarying vec2 vTextureCoord;\nvoid main() {\n  vec4 pos = vec4(aPosition, 0.0, 1.0);\n  gl_Position = uMatrix * pos;\n  vTextureCoord = v_texCoords;\n}\n"), a(35632, "precision mediump float;\nvarying vec2 vTextureCoord;\nuniform lowp vec4 uColor;\nuniform sampler2D u_texture;\nvoid main() {\n  gl_FragColor = texture2D(u_texture, vTextureCoord) * uColor;\n}\n"), this.j, this.as);
        a(this.ac, this.j);
        GLES20.glBlendFunc(1, 771);
        j();
        this.p = new BatchRenderer(this);
        this.q = new LineBatchRenderer(this);
    }

    public final void applyBlendMode(int blendMode) {
        if (this.blendMode == blendMode) {
            return;
        }
        g();
        this.blendMode = blendMode;
        switch (blendMode) {
            case BlendPaint.BLEND_LIGHTING_ADD:
            case BlendPaint.BLEND_ADD:
                GLES20.glBlendFunc(770, 1);
                break;
            case BlendPaint.BLEND_TEAM_COPY:
                GLES20.glBlendFunc(1, 1);
                break;
            case BlendPaint.BLEND_TEAM_ADDITIVE:
                GLES20.glBlendFunc(774, 771);
                break;
            case BlendPaint.BLEND_SOURCE:
                GLES20.glBlendFunc(1, 0);
                break;
            case BlendPaint.BLEND_MULTIPLY:
                GLES20.glBlendFunc(769, 768);
                break;
            case BlendPaint.BLEND_SCREEN:
                GLES20.glBlendFunc(1, 769);
                break;
            default:
                GLES20.glBlendFunc(1, 771);
                break;
        }
        j();
    }

    private static int a(int i, int i2, ShaderHandleBase[] shaderHandleBaseArr, int[] iArr) {
        int iGlCreateProgram = GLES20.glCreateProgram();
        k();
        if (iGlCreateProgram == 0) {
            throw new RuntimeException("Cannot create GL program: " + GLES20.glGetError());
        }
        GLES20.glAttachShader(iGlCreateProgram, i);
        k();
        GLES20.glAttachShader(iGlCreateProgram, i2);
        k();
        GLES20.glLinkProgram(iGlCreateProgram);
        k();
        GLES20.glGetProgramiv(iGlCreateProgram, 35714, iArr, 0);
        if (iArr[0] != 1) {
            Log.e(Q, "======= ERROR =========");
            Log.e(Q, "Could not link program: ");
            String strGlGetProgramInfoLog = GLES20.glGetProgramInfoLog(iGlCreateProgram);
            Log.e(Q, strGlGetProgramInfoLog);
            GLES20.glDeleteProgram(iGlCreateProgram);
            throw new RuntimeException("Cannot link GL program: ".concat(String.valueOf(strGlGetProgramInfoLog)));
        }
        a(shaderHandleBaseArr, iGlCreateProgram);
        return iGlCreateProgram;
    }

    private static void a(ShaderHandleBase[] shaderHandleBaseArr, int i) {
        for (ShaderHandleBase shaderHandleBase : shaderHandleBaseArr) {
            shaderHandleBase.a(i);
        }
    }

    private static String a(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        boolean z = false;
        for (String str2 : str.split("\n")) {
            if (!z && str2.contains("version")) {
                z = true;
            } else {
                if (!str2.equals(str2)) {
                    GameEngine.log("Changing: " + str2 + " to " + str2);
                }
                stringBuffer.append(str2);
                stringBuffer.append("\n");
            }
        }
        return stringBuffer.toString();
    }

    private static int a(int i, String str) {
        int iGlCreateShader = GLES20.glCreateShader(i);
        if (str == null) {
            throw new RuntimeException("Shader Compilation Failed: shaderCode==null");
        }
        GLES20.glShaderSource(iGlCreateShader, a(str));
        j();
        GLES20.glCompileShader(iGlCreateShader);
        j();
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(iGlCreateShader, 35713, iArr, 0);
        if (iArr[0] == 0) {
            String strConcat = "Shader Compilation Failed: ".concat(String.valueOf(GLES20.glGetShaderInfoLog(iGlCreateShader)));
            Log.e(Q, strConcat);
            throw new RuntimeException(strConcat);
        }
        return iGlCreateShader;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(int i, int i2) {
        this.b = i;
        this.c = i2;
        j();
        Matrix.setIdentityM(this.W, this.Y);
        Matrix.orthoM(this.d, 0, 0.0f, i, 0.0f, i2, -1.0f, 1.0f);
        j();
        if (q() == null) {
            this.e = i;
            this.f = i2;
            Matrix.translateM(this.W, this.Y, 0.0f, i2, 0.0f);
            Matrix.scaleM(this.W, this.Y, 1.0f, -1.0f, 1.0f);
            j();
        }
        GLES20.glViewport(0, 0, this.b, this.c);
        if (this.Z != 0) {
            a(this.Z, this.g);
        }
        if (this.aa != 0) {
            a(this.aa, this.h);
        }
        if (this.ab == 0) {
            return;
        }
        a(this.ab, this.i);
    }

    private void a(int i, ShaderHandleBase[] shaderHandleBaseArr) {
        j();
        g();
        j();
        a(i);
        GLES20.glUniformMatrix4fv(shaderHandleBaseArr[5].f574a, 1, false, this.d, 0);
        j();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void b() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        j();
        GLES20.glClear(16384);
        j();
    }

    public static void a(float[] fArr) {
        GLES20.glClearColor(fArr[1], fArr[2], fArr[3], fArr[0]);
        j();
        GLES20.glClear(16384);
        j();
    }

    public final void a(float f, float f2) {
        int i = this.Y;
        float[] fArr = this.W;
        int i2 = i + 12;
        fArr[i2] = fArr[i2] + (fArr[i + 0] * f) + (fArr[i + 4] * f2);
        int i3 = i + 13;
        fArr[i3] = fArr[i3] + (fArr[i + 1] * f) + (fArr[i + 5] * f2);
        int i4 = i + 14;
        fArr[i4] = fArr[i4] + (fArr[i + 2] * f) + (fArr[i + 6] * f2);
        int i5 = i + 15;
        fArr[i5] = (fArr[i + 7] * f2) + (fArr[i + 3] * f) + fArr[i5];
    }

    public final void b(float f, float f2) {
        Matrix.scaleM(this.W, this.Y, f, f2, 1.0f);
    }

    public final void a(float f) {
        if (f != 0.0f) {
            float[] fArr = this.W;
            int i = this.Y;
            float fSin = LogicNumberFuntion.sin(f);
            float fCos = LogicNumberFuntion.cos(f);
            if (f == 90.0f) {
                fSin = 1.0f;
                fCos = 0.0f;
            }
            float f2 = fArr[i + 0];
            float f3 = fArr[i + 1];
            float f4 = fArr[i + 2];
            float f5 = fArr[i + 3];
            float f6 = fArr[i + 4];
            float f7 = fArr[i + 5];
            float f8 = fArr[i + 6];
            float f9 = fArr[i + 7];
            int i2 = i + 0;
            fArr[i2 + 0] = (f2 * fCos) + (f6 * fSin);
            fArr[i2 + 1] = (f3 * fCos) + (f7 * fSin);
            fArr[i2 + 2] = (f4 * fCos) + (f8 * fSin);
            fArr[i2 + 3] = (f5 * fCos) + (f9 * fSin);
            float f10 = (f2 * (-fSin)) + (f6 * fCos);
            float f11 = (f3 * (-fSin)) + (f7 * fCos);
            float f12 = (f4 * (-fSin)) + (f8 * fCos);
            float f13 = (fCos * f9) + ((-fSin) * f5);
            int i3 = i + 4;
            fArr[i3 + 0] = f10;
            fArr[i3 + 1] = f11;
            fArr[i3 + 2] = f12;
            fArr[i3 + 3] = f13;
        }
    }

    private float[] m() {
        float[] fArr = this.W;
        int i = this.Y;
        float[] fArr2 = this.s;
        for (int i2 = 0; i2 < 16; i2++) {
            fArr2[i2] = fArr[i + i2];
        }
        return fArr2;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void c() {
        int i = this.Y;
        this.Y += 16;
        if (this.W.length <= this.Y + 16) {
            this.W = Arrays.copyOf(this.W, this.W.length * 2);
        }
        for (int i2 = 0; i2 < 16; i2++) {
            this.W[this.Y + i2] = this.W[i + i2];
        }
        FastArrayList fastArrayList = this.v;
        this.u++;
        if (this.u >= fastArrayList.size) {
            fastArrayList.add(new ClipRegion());
        }
        ClipRegion clipRegion = (ClipRegion) fastArrayList.get(this.u);
        clipRegion.f575a = this.t.f575a;
        this.t = clipRegion;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void d() {
        this.Y -= 16;
        if (this.Y < 0) {
            GameEngine.logColored("restore: error mCurrentMatrixIndex: " + this.Y);
            this.Y = 0;
        }
        FastArrayList fastArrayList = this.v;
        this.u--;
        if (this.u < 0) {
            GameEngine.logColored("popTransformStack: error currentTransformIndex: " + this.u);
            fastArrayList.set(0, new ClipRegion());
            this.u = 0;
        }
        this.t = (ClipRegion) fastArrayList.get(this.u);
        n();
    }

    private void n() {
        RectF rectF = this.t.f575a;
        if (x != rectF) {
            f();
            if (rectF != null) {
                GLES20.glEnable(3089);
                GLES20.glScissor((int) rectF.left, (int) (this.f - rectF.bottom), (int) rectF.width(), (int) rectF.height());
            } else {
                GLES20.glDisable(3089);
            }
            x = rectF;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(float f, float f2, float f3, DrawStyle drawStyle, ShaderInterface shaderInterface) {
        a(shaderInterface);
        float f4 = 2.0f * f3;
        float f5 = 2.0f * f3;
        a(drawStyle.b, 0.0f);
        if (this.ag != null) {
            this.ag.a(this.Z, this.ae);
        }
        ShaderHandleBase[] shaderHandleBaseArr = this.g;
        a(shaderHandleBaseArr, f, f2, f4, f5);
        int i = shaderHandleBaseArr[0].f574a;
        GLES20.glEnableVertexAttribArray(i);
        j();
        GLES20.glDrawArrays(5, 0, 4);
        j();
        GLES20.glDisableVertexAttribArray(i);
        j();
    }

    public final void a(int i) {
        if (this.y != i) {
            GLES20.glUseProgram(i);
            j();
            this.A = false;
            this.y = i;
        }
    }

    private void a(int i, float f) {
        g();
        a(this.Z);
        if (f > 0.0f) {
            GLES20.glLineWidth(this.W[this.Y] * f);
            j();
        } else {
            GLES20.glLineWidth(1.0f);
            j();
        }
        if (this.q != null) {
            this.q.w = Float.NaN;
        }
        float[] fArrB = b(i);
        o();
        GLES20.glBlendColor(fArrB[0], fArrB[1], fArrB[2], fArrB[3]);
        j();
        GLES20.glUniform4fv(this.g[2].f574a, 1, fArrB, 0);
        a(this.g);
        j();
    }

    private float[] b(int i) {
        float f = ((i >>> 24) & 255) * 0.003921569f * 1.0f;
        this.ao[0] = ((i >>> 16) & 255) * 0.003921569f * f;
        this.ao[1] = ((i >>> 8) & 255) * 0.003921569f * f;
        this.ao[2] = (i & 255) * 0.003921569f * f;
        this.ao[3] = f;
        return this.ao;
    }

    private void o() {
        if (!this.z) {
            GLES20.glEnable(3042);
            j();
            this.z = true;
        }
    }

    public final void h() {
        b((C0009fo) null);
        this.A = false;
        this.y = -1;
        this.I = null;
        this.z = false;
        k();
        g();
    }

    private void a(ShaderHandleBase[] shaderHandleBaseArr) {
        if (!this.A || this.B != 0) {
            GLES20.glBindBuffer(34962, this.ad);
            j();
            GLES20.glVertexAttribPointer(shaderHandleBaseArr[0].f574a, 2, 5126, false, 8, 0);
            j();
            GLES20.glBindBuffer(34962, 0);
            j();
            this.A = true;
            this.B = 0;
        }
    }

    private void a(ShaderHandleBase[] shaderHandleBaseArr, float f, float f2, float f3, float f4) {
        Matrix.translateM(this.an, 0, this.W, this.Y, f, f2, 0.0f);
        Matrix.scaleM(this.an, 0, f3, f4, 1.0f);
        GLES20.glUniformMatrix4fv(shaderHandleBaseArr[1].f574a, 1, false, this.an, 0);
        j();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(ImageBase imageBase, int i, int i2, GraphicsOption graphicsOption) {
        int i3;
        int i4;
        if (i > 0 && i2 > 0) {
            imageBase.f();
            a(graphicsOption);
            RectF rectF = this.ap;
            int iB = imageBase.b();
            int iC = imageBase.c();
            if (imageBase.i) {
                iB--;
                iC--;
                i3 = 1;
                i4 = 1;
            } else {
                i3 = 0;
                i4 = 0;
            }
            rectF.set(i4, i3, iB, iC);
            this.aq.set(0.0f, 0.0f, i + 0, i2 + 0);
            imageBase.a(this.ap);
            imageBase.a(this.ap, this.aq);
            a(imageBase, this.ap, this.aq);
        }
    }

    public final void a(C0009fo c0009fo) {
        if (this.D != c0009fo) {
            if (c0009fo == null && this.D != null && this.D.a()) {
                if (this.D.b()) {
                    f();
                    this.p.m = true;
                    return;
                }
                return;
            }
            b(c0009fo);
        }
    }

    public final void b(C0009fo c0009fo) {
        if (this.D != c0009fo) {
            g();
            if (c0009fo != null) {
                if (c0009fo.l == 0) {
                    d(c0009fo);
                }
                if (c0009fo.l == 0) {
                    this.D = null;
                    return;
                }
                this.p.j = (ShaderAttributes) c0009fo.o;
            }
            if (c0009fo == null) {
                this.p.j = this.p.i;
            }
            this.D = c0009fo;
        }
    }

    public final void c(C0009fo c0009fo) {
        for (TextureSettings textureSettings : c0009fo.n) {
            if (textureSettings.c || textureSettings.f != null) {
                textureSettings.c = false;
                if (textureSettings.b == -1) {
                    textureSettings.b = GLES20.glGetUniformLocation(c0009fo.l, textureSettings.f767a);
                    if (textureSettings.b == -1 && !textureSettings.d) {
                        textureSettings.d = true;
                        c0009fo.a("Unknown parameter: " + textureSettings.f767a);
                        return;
                    }
                }
                if (textureSettings.f != null) {
                    ImageBase imageBaseA = this.r.a(textureSettings.f.b(), textureSettings.f);
                    if (textureSettings.g) {
                        GLES20.glUniform2f(textureSettings.b, imageBaseA.e, imageBaseA.f);
                    } else {
                        int iA = imageBaseA.a();
                        GLES20.glActiveTexture(33985);
                        GLES20.glBindTexture(3553, iA);
                        GLES20.glUniform1i(textureSettings.b, 1);
                        GLES20.glActiveTexture(33984);
                    }
                } else if (textureSettings.e.length == 1) {
                    GLES20.glUniform1f(textureSettings.b, textureSettings.e[0]);
                } else if (textureSettings.e.length == 2) {
                    GLES20.glUniform2f(textureSettings.b, textureSettings.e[0], textureSettings.e[1]);
                } else if (textureSettings.e.length == 4) {
                    GLES20.glUniform4f(textureSettings.b, textureSettings.e[0], textureSettings.e[1], textureSettings.e[2], textureSettings.e[3]);
                } else {
                    c0009fo.a("Unhandled parameter size: " + textureSettings.f767a + " - " + textureSettings.e.length);
                }
            }
        }
    }

    public final boolean d(C0009fo c0009fo) {
        if (c0009fo.m != 0) {
            return false;
        }
        if (c0009fo.l != 0 && !c0009fo.k) {
            return true;
        }
        k();
        c0009fo.k = false;
        c0009fo.a("== Compiling shader ==");
        ShaderAttributes shaderAttributes = new ShaderAttributes();
        c0009fo.o = shaderAttributes;
        try {
            c0009fo.l = a(shaderAttributes.f, "#version 100;\nuniform mat4 uProjection;\nattribute vec2 aPosition;\nattribute vec2 aTextureCoordinate;\nattribute vec4 aColor;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\nvoid main() {\n  vec4 pos = vec4(aPosition, 0.0, 1.0);\n  gl_Position = uProjection * pos;\n  v_texCoords = aTextureCoordinate;\n  v_color = aColor;\n}\n", c0009fo.f);
        } catch (RuntimeException e) {
            c0009fo.b("Failed to compile shader: " + e.getMessage());
            e.printStackTrace();
            c0009fo.m = 1;
        }
        if (c0009fo.m != 0 && c0009fo.l == 0) {
            c0009fo.b("Shader program_handle == 0");
            c0009fo.m = 1;
        }
        k();
        return true;
    }

    public final void a(ImageBase imageBase, RectF rectF, RectF rectF2, GraphicsOption graphicsOption) {
        if (rectF2.width() > 0.0f && rectF2.height() > 0.0f) {
            imageBase.f();
            a(graphicsOption);
            this.ap.set(rectF);
            this.aq.set(rectF2);
            imageBase.a(this.ap);
            imageBase.a(this.ap, this.aq);
            a(imageBase, this.ap, this.aq);
        }
    }

    private void a(ImageBase imageBase, RectF rectF, RectF rectF2) {
        p();
        this.p.a(this.E);
        this.p.a(imageBase, rectF, rectF2, m());
    }

    private void p() {
        if (!this.n) {
            if (this.o) {
                g();
            }
            o();
            this.p.a(this.D);
            this.n = true;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void g() {
        if (this.n) {
            this.p.b();
            this.n = false;
        }
        if (this.o) {
            this.q.b();
            this.o = false;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void f() {
        if (this.n) {
            this.p.a();
        }
        if (this.o) {
            this.q.a();
        }
    }

    public final void i() {
        this.p.m = true;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final boolean a(ImageBase imageBase) {
        boolean zH = imageBase.h();
        if (zH) {
            G--;
            H -= (imageBase.e * imageBase.f) * 4;
            synchronized (this.k) {
                BlendModeState blendModeState = this.k;
                int iA = imageBase.a();
                if (blendModeState.f556a.length == blendModeState.b) {
                    int[] iArr = new int[blendModeState.b + blendModeState.b];
                    System.arraycopy(blendModeState.f556a, 0, iArr, 0, blendModeState.b);
                    blendModeState.f556a = iArr;
                }
                int[] iArr2 = blendModeState.f556a;
                int i = blendModeState.b;
                blendModeState.b = i + 1;
                iArr2[i] = iA;
            }
        }
        return zH;
    }

    public static void b(int i, int i2) {
        G++;
        H += i * i2 * 4;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void e() {
        a((ImageBase) this.am.remove(this.am.size() - 1), q());
        d();
        h();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void c(ImageBase imageBase) {
        c();
        ImageBase imageBaseQ = q();
        this.am.add(imageBase);
        a(imageBaseQ, imageBase);
    }

    private ImageBase q() {
        return (ImageBase) this.am.get(this.am.size() - 1);
    }

    private void a(ImageBase imageBase, ImageBase imageBase2) {
        g();
        if (imageBase == null && imageBase2 != null) {
            if (imageBase2.f() == 3553) {
                GLES20.glGenFramebuffers(1, this.al, 0);
                j();
                GLES20.glBindFramebuffer(36160, this.al[0]);
                j();
            } else {
                GLES11Ext.glGenFramebuffersOES(1, this.al, 0);
                j();
                GLES11Ext.glBindFramebufferOES(36160, this.al[0]);
                j();
            }
        } else if (imageBase != null && imageBase2 == null) {
            if (imageBase.f() == 3553) {
                GLES20.glBindFramebuffer(36160, 0);
                j();
                GLES20.glDeleteFramebuffers(1, this.al, 0);
                j();
            } else {
                GLES11Ext.glBindFramebufferOES(36160, 0);
                j();
                GLES11Ext.glDeleteFramebuffersOES(1, this.al, 0);
                j();
            }
        }
        if (imageBase2 == null) {
            a(this.e, this.f);
            return;
        }
        a(imageBase2.b(), imageBase2.c());
        if (!imageBase2.h()) {
            imageBase2.b(this);
        }
        if (imageBase2.f() == 3553) {
            GLES20.glFramebufferTexture2D(36160, 36064, imageBase2.f(), imageBase2.a(), 0);
            j();
            r();
        } else {
            GLES11Ext.glFramebufferTexture2DOES(36160, 36064, imageBase2.f(), imageBase2.a(), 0);
            j();
            s();
        }
    }

    private static void r() {
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

    private static void s() {
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

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void d(ImageBase imageBase) {
        int iF = imageBase.f();
        b(imageBase);
        j();
        GLES20.glTexParameteri(iF, 10242, 33071);
        GLES20.glTexParameteri(iF, 10243, 33071);
        int iG = imageBase.g();
        GLES20.glTexParameterf(iF, 10241, iG);
        GLES20.glTexParameterf(iF, 10240, iG);
    }

    public final void a(ImageBase imageBase, int i) {
        if (imageBase.g() != i && imageBase.a() != -1) {
            b(imageBase);
            imageBase.a(i);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void b(ImageBase imageBase) {
        f();
        GLES20.glBindTexture(imageBase.f(), imageBase.a());
        this.I = imageBase;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(ImageBase imageBase, int i, int i2, int i3) {
        int iF = imageBase.f();
        b(imageBase);
        j();
        GLES20.glTexImage2D(iF, 0, i3, imageBase.d(), imageBase.e(), 0, i, i2, null);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(ImageBase imageBase, Bitmap bitmap) {
        int iF = imageBase.f();
        b(imageBase);
        j();
        GLUtils.texImage2D(iF, 0, bitmap, 0);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(ImageBase imageBase, int i, int i2, Bitmap bitmap, int i3, int i4) {
        int iF = imageBase.f();
        b(imageBase);
        j();
        GLUtils.texSubImage2D(iF, 0, i, i2, bitmap, i3, i4);
    }

    private static void a(String str, Throwable th) {
        if (K <= 1000) {
            K++;
            if (th != null) {
                Log.e(Q, str, th);
            } else {
                Log.e(Q, str);
            }
        }
    }

    private static void t() {
        int i = 255;
        int iGlGetError = GLES20.glGetError();
        while (iGlGetError != 0 && i > 0) {
            i--;
            iGlGetError = GLES20.glGetError();
        }
        if (iGlGetError != 0) {
            a("clearGlError: Failed to clear", (Throwable) null);
        }
    }

    public static void j() {
        int iGlGetError;
        if (J && (iGlGetError = GLES20.glGetError()) != 0) {
            a("GL error: ".concat(String.valueOf(iGlGetError)), new Throwable());
            t();
        }
    }

    public static void k() {
        int iGlGetError = GLES20.glGetError();
        if (iGlGetError != 0) {
            a("GL error: ".concat(String.valueOf(iGlGetError)), new Throwable());
            t();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final ResourceManagerInterface a() {
        return m;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(int i, int i2, int i3, int i4) {
        float f = this.W[this.Y];
        this.t.f575a = new RectF();
        this.t.f575a.left = i;
        this.t.f575a.right = i3;
        this.t.f575a.top = i2;
        this.t.f575a.bottom = i4;
        this.t.f575a.left *= f;
        this.t.f575a.right *= f;
        this.t.f575a.top *= f;
        RectF rectF = this.t.f575a;
        rectF.bottom = f * rectF.bottom;
        n();
    }

    private void a(ShaderInterface shaderInterface) {
        if (shaderInterface == null) {
            throw new NullPointerException("draw shape filter is null.");
        }
        this.ae = shaderInterface;
        if (this.T.containsKey(shaderInterface)) {
            int i = this.Z;
            this.Z = ((Integer) this.T.get(shaderInterface)).intValue();
            a(this.g, this.Z);
            if (i != this.Z) {
                a(this.Z, this.g);
                return;
            }
            return;
        }
        this.Z = a(this.g, shaderInterface.a(), shaderInterface.b());
        a(this.Z, this.g);
        this.T.put(shaderInterface, Integer.valueOf(this.Z));
    }

    private static void a(GraphicsOption graphicsOption) {
        if (graphicsOption == null) {
            throw new NullPointerException("Texture filter is null.");
        }
    }

    public final int a(ShaderHandleBase[] shaderHandleBaseArr, String str, String str2) {
        return a(a(35633, str), a(35632, str2), shaderHandleBaseArr, this.as);
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(ShaderCompileInterface shaderCompileInterface) {
        this.af = shaderCompileInterface;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(ShaderLoadInterface shaderLoadInterface) {
        this.ag = shaderLoadInterface;
    }

    private RenderState a(int i, boolean z, boolean z2) {
        RenderState renderState;
        GameEngine.log("Loading new font size:" + i + " bold:" + z + " unicode:" + z2);
        try {
            renderState = new RenderState(this);
            renderState.f573a = i;
            renderState.b = z;
            Paint paint = new Paint();
            paint.setSubpixelText(true);
            paint.setAntiAlias(true);
            if (!z) {
                paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, 0));
            } else {
                paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, 1));
            }
            renderState.c = new FontRenderer((byte) 0);
            renderState.c.a(paint, i);
            renderState.c.x = true;
            renderState.c.y = 12;
            this.L.add(renderState);
        } catch (OutOfMemoryError e) {
            GameEngine.reportOOM(AssetType.gameFont, e);
            renderState = new RenderState(this);
            renderState.f573a = i;
            renderState.b = z;
            if (this.M != null) {
                renderState.c = this.M.c;
            }
            this.L.add(renderState);
        }
        return renderState;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(String str, float f, float f2, Paint paint) {
        g();
        Typeface typeface = paint.getTypeface();
        boolean zIsBold = typeface != null ? typeface.isBold() : false;
        int textSize = (int) paint.getTextSize();
        if (textSize > 42) {
            textSize = 42;
        }
        if (textSize < 10) {
            textSize = 10;
        }
        boolean zR = LogicNumberFuntion.r(str);
        int i = zR ? 24 : textSize;
        if (this.M == null) {
            this.M = a(24, false, true);
        }
        RenderState renderStateA = null;
        for (Object renderStateObject : this.L) {
            RenderState renderState = (RenderState) renderStateObject;
            if (renderState.f573a != i || renderState.b != zIsBold) {
                renderState = renderStateA;
            }
            renderStateA = renderState;
        }
        if (renderStateA == null) {
            renderStateA = a(i, zIsBold, zR);
        }
        if (renderStateA.c == null) {
            a("font.glText==null", (Throwable) null);
            return;
        }
        FontRenderer fontRenderer = renderStateA.c;
        k();
        int color = paint.getColor();
        float fAlpha = Color.alpha(color) * 0.003921569f;
        float textSize2 = paint.getTextSize() / renderStateA.f573a;
        float[] fArr = this.d;
        GLES20.glUseProgram(fontRenderer.r.f548a);
        GLES20.glUniform4fv(fontRenderer.s, 1, new float[]{Color.red(color) * 0.003921569f * fAlpha, Color.green(color) * 0.003921569f * fAlpha, Color.blue(color) * 0.003921569f * fAlpha, fAlpha}, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glUniform1i(fontRenderer.t, 0);
        FontRenderer.a();
        TextRenderer textRenderer = fontRenderer.b;
        textRenderer.f = 0;
        textRenderer.d = 0;
        textRenderer.g = fArr;
        textRenderer.h = -1;
        fontRenderer.o = textSize2;
        fontRenderer.n = textSize2;
        k();
        if (paint.getTextAlign() == Paint.Align.CENTER) {
            int length = str.length();
            float f3 = 0.0f;
            for (int i2 = 0; i2 < length; i2++) {
                GlyphInfo glyphInfoA = fontRenderer.a(str.charAt(i2));
                if (glyphInfoA != null) {
                    f3 += glyphInfoA.c * fontRenderer.n;
                }
            }
            fontRenderer.a(str, f - ((int) ((f3 + (length > 1 ? ((length - 1) * fontRenderer.p) * fontRenderer.n : 0.0f)) * 0.5f)), this.c - f2);
        } else {
            fontRenderer.a(str, f, this.c - f2);
        }
        k();
        fontRenderer.b.a();
        h();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(float[] fArr, int i, int i2, DrawStyle drawStyle, ShaderInterface shaderInterface) {
        if (this.P == null || this.P.capacity() < i2 * 4) {
            ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect((i2 * 4) + 10);
            byteBufferAllocateDirect.order(ByteOrder.nativeOrder());
            this.P = byteBufferAllocateDirect.asFloatBuffer();
        }
        this.P.clear();
        this.P.put(fArr, i, i2);
        this.P.flip();
        this.P.position(0);
        float f = drawStyle.f555a;
        int i3 = drawStyle.b;
        if (f == 0.0f) {
            f = 1.0f;
        }
        a(shaderInterface);
        a(i3, f);
        if (this.ag != null) {
            this.ag.a(this.Z, this.ae);
        }
        ShaderHandleBase[] shaderHandleBaseArr = this.g;
        int i4 = shaderHandleBaseArr[0].f574a;
        GLES20.glEnableVertexAttribArray(i4);
        j();
        GLES20.glVertexAttribPointer(i4, 2, 5126, false, 0, (Buffer) this.P);
        a(shaderHandleBaseArr, 0.0f, 0.0f, 1.0f, 1.0f);
        if (debugPointDrawCount < 16) {
            Log.d("MinimapGL", "glDrawArrays mode=POINTS floats=" + i2 + " vertices=" + (i2 / 2));
            debugPointDrawCount++;
        }
        GLES20.glDrawArrays(0, 0, i2 / 2);
        j();
        GLES20.glDisableVertexAttribArray(i4);
        j();
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(Bitmap bitmap) {
        if (this.f572a != null) {
            TextureManager textureManager = this.f572a;
            if (((SubTexture) textureManager.d.get(bitmap)) == null) {
                return;
            }
            textureManager.d.remove(bitmap);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.GraphicsRenderer
    public final void a(float f, float f2, float f3, float f4, DrawStyle drawStyle) {
        if (!this.o) {
            if (this.n) {
                g();
            }
            o();
            LineBatchRenderer lineBatchRenderer = this.q;
            lineBatchRenderer.c = 0;
            lineBatchRenderer.d = 0;
            lineBatchRenderer.l = null;
            lineBatchRenderer.g.a(lineBatchRenderer.h);
            lineBatchRenderer.m = null;
            lineBatchRenderer.k.c.a(lineBatchRenderer.g.d);
            j();
            LineVertexBuffer lineVertexBuffer = lineBatchRenderer.f562a;
            GLES20.glEnableVertexAttribArray(lineVertexBuffer.g.k.f563a.f574a);
            GLES20.glEnableVertexAttribArray(lineVertexBuffer.g.k.b.f574a);
            j();
            this.o = true;
        }
        j();
        LineBatchRenderer lineBatchRenderer2 = this.q;
        if (lineBatchRenderer2.o != 1) {
            lineBatchRenderer2.a();
            lineBatchRenderer2.o = 1;
        }
        if (drawStyle == null) {
            this.q.a(-1);
        } else {
            this.q.a(drawStyle.b);
        }
        float f5 = drawStyle.f555a;
        if (f5 > 0.0f) {
            this.q.a(f5 * this.W[this.Y]);
        } else {
            this.q.a(1.0f);
        }
        j();
        float[] fArrM = m();
        this.q.a(f, f2, fArrM);
        this.q.a(f3, f4, fArrM);
        j();
    }
}
