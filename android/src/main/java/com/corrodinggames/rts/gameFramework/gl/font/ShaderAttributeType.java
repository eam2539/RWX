package com.corrodinggames.rts.gameFramework.gl.font;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.a */
/* JADX INFO: loaded from: classes.dex */
public enum ShaderAttributeType {
    A_Position(1, "a_Position"),
    A_TexCoordinate(2, "a_TexCoordinate");

    int c;
    String d;

    ShaderAttributeType(int i, String str) {
        this.c = i;
        this.d = str;
    }
}
