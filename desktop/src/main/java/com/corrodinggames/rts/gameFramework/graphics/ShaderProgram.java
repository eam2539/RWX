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
    public String c;
    public String d;
    public String e;
    public String f;
    public int g;
    public int h;
    String i;
    String j;
    long k;
    long l;
    public boolean m;
    public int n;
    public int o;
    public ShaderUniform[] p;
    public Object q;
    public int r;
    int s;

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

    public ShaderUniform a(String str) {
        for (ShaderUniform shaderUniform : this.p) {
            if (shaderUniform.a.equals(str)) {
                return shaderUniform;
            }
        }
        ShaderUniform shaderUniform2 = new ShaderUniform();
        shaderUniform2.a = str;
        ShaderUniform[] shaderUniformArr = (ShaderUniform[]) Arrays.copyOf(this.p, this.p.length + 1);
        shaderUniformArr[shaderUniformArr.length - 1] = shaderUniform2;
        this.p = shaderUniformArr;
        return shaderUniform2;
    }

    public ShaderProgram(String str) throws IOException {
        this.d = VariableScope.nullOrMissingString;
        this.p = new ShaderUniform[0];
        a(GameEngine.isGDXVersion ? "assets/shaders/plainGDX.vert" : "assets/shaders/plain.vert", str);
    }

    public void a(String str, String str2) throws IOException {
        this.c = Utility.getFileNameFromPath(str2);
        this.i = str;
        this.j = str2;
        d();
        e();
    }

    public ShaderProgram() {
        this.d = VariableScope.nullOrMissingString;
        this.p = new ShaderUniform[0];
        this.c = "Invalid";
        this.o = 1;
    }

    public void d() throws IOException {
        AssetInputStream assetInputStreamOpenFileByPath = FileHelper.openFileByPath(this.i);
        if (assetInputStreamOpenFileByPath == null) {
            throw new IOException("Cannot find: " + this.i);
        }
        this.e = Utility.readStreamToString(assetInputStreamOpenFileByPath);
        AssetInputStream assetInputStreamOpenFileByPath2 = FileHelper.openFileByPath(this.j);
        if (assetInputStreamOpenFileByPath2 == null) {
            throw new IOException("Cannot find: " + this.j);
        }
        this.f = Utility.readStreamToString(assetInputStreamOpenFileByPath2);
    }

    public void b(String str) {
        GameEngine.log("shader(" + this.c + "): " + str);
    }

    public void c(String str) {
        if (this.r < 3) {
            this.r++;
            GameEngine.reportNonFatalError("shader(" + this.c + "): " + str);
        }
        GameEngine.logErrorColored("shader(" + this.c + "): " + str);
        this.o = 1;
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
        long jA = FileChangeEngine.a(this.i, false);
        long jA2 = FileChangeEngine.a(this.j, false);
        boolean z = (jA == this.k && jA2 == this.l) ? false : true;
        this.k = jA;
        this.l = jA2;
        return z;
    }

    public void f() {
        this.s++;
        if (this.s < 100) {
            return;
        }
        this.s = 0;
        if (e()) {
            b("Reloading shader");
            try {
                d();
                this.m = true;
                this.o = 0;
                for (ShaderUniform shaderUniform : this.p) {
                    shaderUniform.c = true;
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
