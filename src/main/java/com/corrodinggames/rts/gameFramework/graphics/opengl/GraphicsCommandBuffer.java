package com.corrodinggames.rts.gameFramework.graphics.opengl;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/t.class */
public final class GraphicsCommandBuffer {
    public int a;
    public GraphicsCommand[] b;

    public GraphicsCommandBuffer(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("capacity < 0: " + i);
        }
        this.b = i == 0 ? new GraphicsCommand[0] : new GraphicsCommand[i];
    }

    public final boolean a(GraphicsCommand graphicsCommand) {
        GraphicsCommand[] graphicsCommandArr = this.b;
        int i = this.a;
        if (i == graphicsCommandArr.length) {
            GraphicsCommand[] graphicsCommandArr2 = new GraphicsCommand[i + (i < 6 ? 12 : i >> 1)];
            System.arraycopy(graphicsCommandArr, 0, graphicsCommandArr2, 0, i);
            graphicsCommandArr = graphicsCommandArr2;
            this.b = graphicsCommandArr2;
        }
        graphicsCommandArr[i] = graphicsCommand;
        this.a = i + 1;
        return true;
    }
}
