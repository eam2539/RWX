package com.corrodinggames.rts.gameFramework.android.graphics;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ar */
/* JADX INFO: loaded from: classes.dex */
public final class LineShaderAttributes {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    AttributeHandle f563a = new AttributeHandle("aPosition");
    AttributeHandle b = new AttributeHandle("aColor");
    UniformHandle c = new UniformHandle("uProjection");
    UniformHandle d = new UniformHandle("u_texture");
    ShaderHandleBase[] e = {this.f563a, this.b, this.c, this.d};
}
