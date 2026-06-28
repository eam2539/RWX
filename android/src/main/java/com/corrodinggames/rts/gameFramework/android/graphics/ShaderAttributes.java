package com.corrodinggames.rts.gameFramework.android.graphics;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.ah */
/* JADX INFO: loaded from: classes.dex */
public final class ShaderAttributes {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    AttributeHandle f558a = new AttributeHandle("aPosition");
    AttributeHandle b = new AttributeHandle("aTextureCoordinate");
    AttributeHandle c = new AttributeHandle("aColor");
    UniformHandle d = new UniformHandle("uProjection");
    UniformHandle e = new UniformHandle("u_texture");
    ShaderHandleBase[] f = {this.f558a, this.b, this.c, this.d, this.e};
}
