package com.corrodinggames.rts.gameFramework.graphics.opengl;

import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.gameFramework.graphics.AudioRenderer;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

import javax.microedition.khronos.opengles.GL10;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/b.class */
public class DrawCommand {
    ShapeType a;
    Texture b;
    float c;
    float d;
    Rect e;
    RectF f;
     /* synthetic */ AudioRenderer g;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void a(GL10 gl10) {
        if (this.g.i != this.b.h.intValue()) {
            gl10.glBindTexture(3553, this.b.h.intValue());
            this.g.i = this.b.h.intValue();
        }
        gl10.glPushMatrix();
        gl10.glLoadIdentity();
        if (this.a == ShapeType.Rect) {
            gl10.glTranslatef(this.f.a, (this.g.c - this.f.b) - this.e.c(), 0.0f);
            GLMeshBuffer gLMeshBuffer = this.g.h;
            float fM = this.e.a / this.b.m();
            float fM2 = this.e.c / this.b.m();
            float fL = this.e.b / this.b.l();
            float fL2 = this.e.d / this.b.l();
            if (this.g.j == this.e.c() && this.g.k == this.e.b()) {
                gLMeshBuffer.a(0, 0, fM, fL2);
                gLMeshBuffer.a(1, 0, fM2, fL2);
                gLMeshBuffer.a(0, 1, fM, fL);
                gLMeshBuffer.a(1, 1, fM2, fL);
            } else {
                this.g.j = this.e.c();
                this.g.k = this.e.b();
                gLMeshBuffer.a(0, 0, 0.0f, 0.0f, 0.0f, fM, fL2, null);
                gLMeshBuffer.a(1, 0, this.e.b(), 0.0f, 0.0f, fM2, fL2, null);
                gLMeshBuffer.a(0, 1, 0.0f, this.e.c(), 0.0f, fM, fL, null);
                gLMeshBuffer.a(1, 1, this.e.b(), this.e.c(), 0.0f, fM2, fL, null);
            }
            gLMeshBuffer.b(gl10, true, false);
            gl10.glPopMatrix();
            return;
        }
        gl10.glTranslatef(this.c, (this.g.c - this.d) - this.b.l(), 0.0f);
        throw new RuntimeException("Not supported");
    }
}
