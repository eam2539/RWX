package com.corrodinggames.rts.gameFramework.android.graphics;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.k */
/* JADX INFO: loaded from: classes.dex */
final class ImageLoadCallback implements ImageLoadInterface {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    final /* synthetic */ GraphicsEngine f569a;

    ImageLoadCallback(GraphicsEngine graphicsEngine) {
        this.f569a = graphicsEngine;
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ImageLoadInterface
    public final void a(ImageBase imageBase, GraphicsOption graphicsOption) {
        this.f569a.f566a.a(imageBase, imageBase.b(), imageBase.c(), graphicsOption);
    }
}
