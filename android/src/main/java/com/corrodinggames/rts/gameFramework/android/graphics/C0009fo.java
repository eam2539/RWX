package com.corrodinggames.rts.gameFramework.android.graphics;

import android.graphics.Paint;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.FileChangeEngine;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.LogicNumberFuntion;
import com.corrodinggames.rts.gameFramework.e.FileSystem;
import com.corrodinggames.rts.gameFramework.m.TextureSettings;
import com.corrodinggames.rts.gameFramework.m.UnitTexture;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;

import java.io.IOException;
import java.util.Arrays;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.fo */
/* JADX INFO: loaded from: classes.dex */
public class C0009fo {
    public String c;
    public String d;
    public String e;
    public String f;
    String g;
    String h;
    long i;
    long j;
    public boolean k;
    public int l;
    public int m;
    public TextureSettings[] n;
    public Object o;
    public int p;

    public final void a(String str, float f) {
        c(str).a(f);
    }

    public final void a(String str, float f, float f2) {
        c(str).a(f, f2);
    }

    public final void a(String str, float f, float f2, float f3, float f4) {
        c(str).a(f, f2, f3, f4);
    }

    public final void a(String str, int i) {
        c(str).a(((i >> 16) & 255) * 0.003921569f, ((i >> 8) & 255) * 0.003921569f, (i & 255) * 0.003921569f, (i >>> 24) * 0.003921569f);
    }

    public final void a(String str, UnitTexture unitTexture) {
        c(str).a(unitTexture);
    }

    public final void b(String str, UnitTexture unitTexture) {
        c(str).b(unitTexture);
    }

    private TextureSettings c(String str) {
        for (TextureSettings textureSettings : this.n) {
            if (textureSettings.f767a.equals(str)) {
                return textureSettings;
            }
        }
        TextureSettings textureSettings2 = new TextureSettings();
        textureSettings2.f767a = str;
        TextureSettings[] textureSettingsArr = (TextureSettings[]) Arrays.copyOf(this.n, this.n.length + 1);
        textureSettingsArr[textureSettingsArr.length - 1] = textureSettings2;
        this.n = textureSettingsArr;
        return textureSettings2;
    }

    public C0009fo(String str) throws IOException {
        this.d = VariableScope.nullOrMissingString;
        this.n = new TextureSettings[0];
        String str2 = "assets/shaders/plain.vert";
        this.c = LogicNumberFuntion.i(str);
        this.g = str2;
        this.h = str;
        AssetInputStream assetInputStreamK = FileSystem.k(this.g);
        if (assetInputStreamK == null) {
            throw new IOException("Cannot find: " + this.g);
        }
        this.e = LogicNumberFuntion.a(assetInputStreamK);
        AssetInputStream assetInputStreamK2 = FileSystem.k(this.h);
        if (assetInputStreamK2 == null) {
            throw new IOException("Cannot find: " + this.h);
        }
        this.f = LogicNumberFuntion.a(assetInputStreamK2);
        long jA = FileChangeEngine.a(this.g, false);
        long jA2 = FileChangeEngine.a(this.h, false);
        this.i = jA;
        this.j = jA2;
    }

    public C0009fo() {
        this.d = VariableScope.nullOrMissingString;
        this.n = new TextureSettings[0];
        this.c = "Invalid";
        this.m = 1;
    }

    public final void a(String str) {
        GameEngine.log("shader(" + this.c + "): " + str);
    }

    public final void b(String str) {
        if (this.p < 3) {
            this.p++;
            GameEngine.logColored("shader(" + this.c + "): " + str);
        }
        GameEngine.log("shader(" + this.c + "): " + str);
        this.m = 1;
    }

    public boolean a() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public boolean a(Paint paint, UnitTexture unitTexture) {
        return false;
    }

    public void c() {
        this.k = true;
    }
}
