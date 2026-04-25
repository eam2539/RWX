package com.corrodinggames.rts.gameFramework.graphics.opengl;

import com.corrodinggames.rts.gameFramework.graphics.DeferredGraphicsInterface;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.s */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/s.class */
public final class GraphicsCommand {
    public GraphicsOperation a;
    public Object[] b = new Object[8];
    public float c;
    public float d;
    public float e;
    public float f;
    final /* synthetic */ DeferredGraphicsInterface g;

    public GraphicsCommand(DeferredGraphicsInterface deferredGraphicsInterface) {
        this.g = deferredGraphicsInterface;
    }
}
