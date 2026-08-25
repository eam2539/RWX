package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.Paint;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/i.class */
public class TeamColorShader extends ShaderProgram {
    /* JADX INFO: renamed from: a */
    int lastAppliedColor;
    /* JADX INFO: renamed from: b */
    boolean isEnabled;

    public TeamColorShader(String str, boolean z) throws IOException {
        super(str);
        this.lastAppliedColor = -99;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.ShaderProgram
    public boolean a() {
        return this.isEnabled;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.ShaderProgram
    public boolean b() {
        boolean z = false;
        if (-16711936 != this.lastAppliedColor) {
            a("teamColor", -16711936);
            z = true;
            this.lastAppliedColor = -16711936;
        }
        return z;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.ShaderProgram
    public boolean a(Paint paint, Texture texture) {
        boolean z = false;
        if (texture instanceof TeamColorTexture) {
            TeamColorTexture teamColorTexture = (TeamColorTexture) texture;
            if (teamColorTexture.D != this.lastAppliedColor) {
                a("teamColor", teamColorTexture.D);
                z = true;
                this.lastAppliedColor = teamColorTexture.D;
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
