package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.Paint;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.FileChangeEngine;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import java.io.IOException;
import java.util.Arrays;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.ae */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/ae.class */
public class ShaderProgram {
    /* JADX INFO: renamed from: c */
    public String name;
    public String d;
    /* JADX INFO: renamed from: e */
    public String vertexSource;
    /* JADX INFO: renamed from: f */
    public String fragmentSource;
    /* JADX INFO: renamed from: g */
    public int vertexShaderId;
    /* JADX INFO: renamed from: h */
    public int fragmentShaderId;
    /* JADX INFO: renamed from: o */
    public int programStatus;
    /* JADX INFO: renamed from: p */
    public ShaderUniform[] uniforms;
    /* JADX INFO: renamed from: r */
    public int errorCount;
    /* JADX INFO: renamed from: i */
    String vertexPath;
    public boolean m;
    public int n;
    /* JADX INFO: renamed from: j */
    String fragmentPath;
    /* JADX INFO: renamed from: k */
    long vertexFileTimestamp;
    public Object q;
    /* JADX INFO: renamed from: l */
    long fragmentFileTimestamp;
    /* JADX INFO: renamed from: s */
    int compileCount;

    public void a(String str, float f) {
        a(str).a(f);
    }

    public void a(String str, float f, float f2) {
        a(str).a(f, f2);
    }

    public void a(String str, int i) {
        a(str).a(((i >> 16) & 255) * 0.003921569f, ((i >> 8) & 255) * 0.003921569f, (i & 255) * 0.003921569f, (i >>> 24) * 0.003921569f);
    }

    public void a(String str, Texture texture) {
        a(str).a(texture);
    }

    public void b(String str, Texture texture) {
        a(str).b(texture);
    }

    public ShaderProgram(String str) throws IOException {
        this.d = VariableScope.nullOrMissingString;
        this.uniforms = new ShaderUniform[0];
        a(GameEngine.isGDXVersion ? "assets/shaders/plainGDX.vert" : "assets/shaders/plain.vert", str);
    }

    public ShaderProgram() {
        this.d = VariableScope.nullOrMissingString;
        this.uniforms = new ShaderUniform[0];
        this.name = "Invalid";
        this.programStatus = 1;
    }

    public ShaderUniform a(String str) {
        for (ShaderUniform shaderUniform : this.uniforms) {
            if (shaderUniform.name.equals(str)) {
                return shaderUniform;
            }
        }
        ShaderUniform shaderUniform2 = new ShaderUniform();
        shaderUniform2.name = str;
        ShaderUniform[] shaderUniformArr = (ShaderUniform[]) Arrays.copyOf(this.uniforms, this.uniforms.length + 1);
        shaderUniformArr[shaderUniformArr.length - 1] = shaderUniform2;
        this.uniforms = shaderUniformArr;
        return shaderUniform2;
    }

    public void a(String str, String str2) throws IOException {
        this.name = Utility.getFileNameWithoutExtension(str2);
        this.vertexPath = str;
        this.fragmentPath = str2;
        d();
        e();
    }

    public void d() throws IOException {
        AssetInputStream assetInputStreamOpenFileByPath = FileHelper.openFileByPath(this.vertexPath);
        if (assetInputStreamOpenFileByPath == null) {
            throw new IOException("Cannot find: " + this.vertexPath);
        }
        this.vertexSource = Utility.readStreamToString(assetInputStreamOpenFileByPath);
        AssetInputStream assetInputStreamOpenFileByPath2 = FileHelper.openFileByPath(this.fragmentPath);
        if (assetInputStreamOpenFileByPath2 == null) {
            throw new IOException("Cannot find: " + this.fragmentPath);
        }
        this.fragmentSource = Utility.readStreamToString(assetInputStreamOpenFileByPath2);
    }

    public void b(String str) {
        GameEngine.log("shader(" + this.name + "): " + str);
    }

    public void c(String str) {
        if (this.errorCount < 3) {
            this.errorCount++;
            GameEngine.reportNonFatalError("shader(" + this.name + "): " + str);
        }
        GameEngine.logErrorColored("shader(" + this.name + "): " + str);
        this.programStatus = 1;
    }

    public boolean a() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public boolean a(Paint paint, Texture texture) {
        return false;
    }

    public boolean e() {
        long jA = FileChangeEngine.a(this.vertexPath, false);
        long jA2 = FileChangeEngine.a(this.fragmentPath, false);
        boolean z = (jA == this.vertexFileTimestamp && jA2 == this.fragmentFileTimestamp) ? false : true;
        this.vertexFileTimestamp = jA;
        this.fragmentFileTimestamp = jA2;
        return z;
    }

    public void f() {
        this.compileCount++;
        if (this.compileCount < 100) {
            return;
        }
        this.compileCount = 0;
        if (e()) {
            b("Reloading shader");
            try {
                d();
                this.m = true;
                this.programStatus = 0;
                for (ShaderUniform shaderUniform : this.uniforms) {
                    shaderUniform.isDirty = true;
                    shaderUniform.b = -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void c() {
        GameEngine.getInstance().renderGraphicsEngine.a(this);
    }
}
