package com.corrodinggames.rts.gameFramework.gl;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/w.class */
public class IntArrayBuffer {
    private int[] a;
    private int b;

    public void a(int i) {
        if (this.a.length == this.b) {
            int[] iArr = new int[this.b + this.b];
            System.arraycopy(this.a, 0, iArr, 0, this.b);
            this.a = iArr;
        }
        int[] iArr2 = this.a;
        int i2 = this.b;
        this.b = i2 + 1;
        iArr2[i2] = i;
    }
}
