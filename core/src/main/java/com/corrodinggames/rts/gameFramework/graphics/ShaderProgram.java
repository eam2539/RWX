package com.corrodinggames.rts.gameFramework.graphics;

import com.corrodinggames.rts.gameFramework.FileChangeEngine;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import io.github.rwx.render.canvas.KoolPaint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

    public ShaderProgram(String str) throws IOException {
        this();
        a(GameEngine.isGDXVersion ? "assets/shaders/plainGDX.vert" : "assets/shaders/plain.vert", str);
    }

    public ShaderProgram() {
        this.d = "";
        this.p = new ShaderUniform[0];
        this.c = "Invalid";
        this.o = 1;
    }

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
        ShaderUniform shaderUniform = new ShaderUniform();
        shaderUniform.a = str;
        ShaderUniform[] shaderUniformArr = Arrays.copyOf(this.p, this.p.length + 1);
        shaderUniformArr[shaderUniformArr.length - 1] = shaderUniform;
        this.p = shaderUniformArr;
        return shaderUniform;
    }

    public void a(String str, String str2) throws IOException {
        this.c = Utility.getFileNameWithoutExtension(str2);
        this.i = str;
        this.j = str2;
        d();
        e();
        this.o = 0;
    }

    public void d() throws IOException {
        this.e = readShaderSource(this.i);
        this.f = readShaderSource(this.j);
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

    public boolean a(KoolPaint paint, Texture texture) {
        return false;
    }

    public boolean e() {
        long jA = FileChangeEngine.a(this.i, false);
        long jA2 = FileChangeEngine.a(this.j, false);
        boolean z = jA != this.k || jA2 != this.l;
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
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine != null && gameEngine.renderGraphicsEngine != null) {
            gameEngine.renderGraphicsEngine.a(this);
        }
    }

    private static String readShaderSource(String str) throws IOException {
        AssetInputStream assetInputStream = FileHelper.openFileByPath(str);
        if (assetInputStream == null && str.startsWith("assets/")) {
            assetInputStream = FileHelper.openFileByPath(str.substring("assets/".length()));
        }
        if (assetInputStream == null) {
            throw new IOException("Cannot find: " + str);
        }
        try (AssetInputStream inputStream = assetInputStream) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);
            }
            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
