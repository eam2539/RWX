package com.corrodinggames.rts.appFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.aa */
/* JADX INFO: loaded from: classes.dex */
final class GLSurfaceViewShared$DepthComponentSizeChooser extends GLSurfaceViewShared$ComponentSizeChooser {
    final /* synthetic */ GLSurfaceViewShared j;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public GLSurfaceViewShared$DepthComponentSizeChooser(GLSurfaceViewShared gLSurfaceViewShared, boolean z) {
        super(gLSurfaceViewShared, 8, 8, 8, 0, z ? 16 : 0, 0);
        this.j = gLSurfaceViewShared;
    }
}
