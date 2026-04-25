package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.Paint;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/i.class */
public class TeamColorShader extends ShaderProgram {
    int a;
    boolean b;

    public TeamColorShader(String str, boolean z) throws IOException {
        super(str);
        this.a = -99;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.ShaderProgram
    public boolean a() {
        return this.b;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.ShaderProgram
    public boolean b() {
        boolean z = false;
        if (-16711936 != this.a) {
            a("teamColor", -16711936);
            z = true;
            this.a = -16711936;
        }
        return z;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.ShaderProgram
    public boolean a(Paint paint, Texture texture) {
        boolean z = false;
        if (texture instanceof TeamColorTexture) {
            TeamColorTexture teamColorTexture = (TeamColorTexture) texture;
            if (teamColorTexture.D != this.a) {
                a("teamColor", teamColorTexture.D);
                z = true;
                this.a = teamColorTexture.D;
            }
        }
        super.a(paint, texture);
        return z;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.ShaderProgram
    public void c() {
        super.c();
    }
}
