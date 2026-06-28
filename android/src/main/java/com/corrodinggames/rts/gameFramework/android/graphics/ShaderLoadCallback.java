package com.corrodinggames.rts.gameFramework.android.graphics;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.i */
/* JADX INFO: loaded from: classes.dex */
final class ShaderLoadCallback implements ShaderLoadInterface {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ GraphicsEngine f567a;

    ShaderLoadCallback(GraphicsEngine graphicsEngine) {
        this.f567a = graphicsEngine;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ShaderLoadInterface
    public final void a(int i, ShaderInterface shaderInterface) {
        shaderInterface.a(i, this.f567a);
    }
}
