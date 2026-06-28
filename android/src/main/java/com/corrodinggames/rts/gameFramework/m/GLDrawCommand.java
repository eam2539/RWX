package com.corrodinggames.rts.gameFramework.m;

import com.corrodinggames.rts.gameFramework.android.graphics.DeferredGraphicsInterface;
import com.corrodinggames.rts.gameFramework.android.graphics.opengl.CanvasDrawOperation;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.cm */
/* JADX INFO: loaded from: classes.dex */
public final class GLDrawCommand {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public CanvasDrawOperation f753a;
    public Object[] b = new Object[8];
    public float c;
    public float d;
    public float e;
    public float f;
    public final /* synthetic */ DeferredGraphicsInterface g;

    public GLDrawCommand(DeferredGraphicsInterface deferredGraphicsInterface) {
        this.g = deferredGraphicsInterface;
    }
}
