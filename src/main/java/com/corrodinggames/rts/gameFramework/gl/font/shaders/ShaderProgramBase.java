package com.corrodinggames.rts.gameFramework.gl.font.shaders;

import com.corrodinggames.rts.gameFramework.gl.font.ShaderAttributeType;
import com.corrodinggames.rts.gameFramework.gl.font.ShaderUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/a/a/b.class */
public abstract class ShaderProgramBase {
    private int a;
    private int b;
    private int c;
    private boolean d = false;

    public void a() {
        a(null, null, null);
    }

    public void a(String str, String str2, ShaderAttributeType[] shaderAttributeTypeArr) {
        this.b = ShaderUtils.a(35633, str);
        this.c = ShaderUtils.a(35632, str2);
        this.a = ShaderUtils.a(this.b, this.c, shaderAttributeTypeArr);
        this.d = true;
    }

    public int b() {
        return this.a;
    }
}
