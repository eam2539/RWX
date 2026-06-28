package com.corrodinggames.rts.gameFramework.m;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.cn */
/* JADX INFO: loaded from: classes.dex */
public final class DrawCommandList {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f754a;
    public GLDrawCommand[] b = new GLDrawCommand[200];

    public final boolean a(GLDrawCommand gLDrawCommand) {
        GLDrawCommand[] gLDrawCommandArr;
        GLDrawCommand[] gLDrawCommandArr2 = this.b;
        int i = this.f754a;
        if (i == gLDrawCommandArr2.length) {
            gLDrawCommandArr = new GLDrawCommand[(i < 6 ? 12 : i >> 1) + i];
            System.arraycopy(gLDrawCommandArr2, 0, gLDrawCommandArr, 0, i);
            this.b = gLDrawCommandArr;
        } else {
            gLDrawCommandArr = gLDrawCommandArr2;
        }
        gLDrawCommandArr[i] = gLDrawCommand;
        this.f754a = i + 1;
        return true;
    }
}
