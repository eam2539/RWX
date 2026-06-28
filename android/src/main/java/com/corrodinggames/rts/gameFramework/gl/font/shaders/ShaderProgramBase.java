package com.corrodinggames.rts.gameFramework.gl.font.shaders;

import com.corrodinggames.rts.gameFramework.gl.font.ShaderAttributeType;
import com.corrodinggames.rts.gameFramework.gl.font.ShaderUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.a.b */
/* JADX INFO: loaded from: classes.dex */
public abstract class ShaderProgramBase {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f548a;
    private int b;
    private int c;
    private boolean d = false;

    public void a() {
        a(null, null, null);
    }

    public final void a(String str, String str2, ShaderAttributeType[] shaderAttributeTypeArr) {
        this.b = ShaderUtils.a(35633, str);
        this.c = ShaderUtils.a(35632, str2);
        this.f548a = ShaderUtils.a(this.b, this.c, shaderAttributeTypeArr);
        this.d = true;
    }
}
