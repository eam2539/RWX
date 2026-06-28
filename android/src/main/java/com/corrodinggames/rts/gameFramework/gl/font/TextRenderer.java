package com.corrodinggames.rts.gameFramework.gl.font;

import android.opengl.GLES20;
import com.corrodinggames.rts.gameFramework.gl.font.shaders.ShaderProgramBase;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.d */
/* JADX INFO: loaded from: classes.dex */
public final class TextRenderer {

    /* JADX INFO: renamed from: a */
    FontRenderer f551a;
    public float[] g;
    public int h;
    private int i;
    float[] c = new float[480];
    TextMesh b = new TextMesh();
    public int d = 0;
    int e = 24;
    public int f = 0;

    public TextRenderer(ShaderProgramBase shaderProgramBase, FontRenderer fontRenderer) {
        this.f551a = fontRenderer;
        short[] sArr = new short[144];
        int i = 0;
        short s = 0;
        while (i < 144) {
            sArr[i + 0] = (short) (s + 0);
            sArr[i + 1] = (short) (s + 1);
            sArr[i + 2] = (short) (s + 2);
            sArr[i + 3] = (short) (s + 2);
            sArr[i + 4] = (short) (s + 3);
            sArr[i + 5] = (short) (s + 0);
            i += 6;
            s = (short) (s + 4);
        }
        TextMesh textMesh = this.b;
        textMesh.e.clear();
        textMesh.e.put(sArr, 0, 144);
        textMesh.e.flip();
        textMesh.g = 144;
        this.i = GLES20.glGetUniformLocation(shaderProgramBase.f548a, "u_MVPMatrix");
    }

    public final void a() {
        if (this.f > 0) {
            GLES20.glUniformMatrix4fv(this.i, 1, false, this.g, 0);
            this.b.a(this.c, this.d);
            this.b.a();
            this.b.a(this.f * 6);
            GLES20.glDisableVertexAttribArray(this.b.i);
            this.f = 0;
            this.d = 0;
        }
    }
}
