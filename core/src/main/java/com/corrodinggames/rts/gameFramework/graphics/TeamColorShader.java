package com.corrodinggames.rts.gameFramework.graphics;

import io.github.rwx.render.canvas.KoolPaint;

import java.io.IOException;

public class TeamColorShader extends ShaderProgram {
    /* JADX INFO: renamed from: a */
    int lastAppliedColor;
    /* JADX INFO: renamed from: b */
    boolean isEnabled;

    public TeamColorShader(String str, boolean z) throws IOException {
        super(str);
        this.lastAppliedColor = -99;
        this.isEnabled = z;
    }

    @Override
    public boolean a() {
        return this.isEnabled;
    }

    @Override
    public boolean b() {
        boolean z = false;
        if (-16711936 != this.lastAppliedColor) {
            a("teamColor", -16711936);
            z = true;
            this.lastAppliedColor = -16711936;
        }
        return z;
    }

    @Override
    public boolean a(KoolPaint paint, Texture texture) {
        boolean z = false;
        if (texture instanceof TeamColorTexture) {
            TeamColorTexture teamColorTexture = (TeamColorTexture) texture;
            if (teamColorTexture.getTeamColor() != this.lastAppliedColor) {
                a("teamColor", teamColorTexture.getTeamColor());
                z = true;
                this.lastAppliedColor = teamColorTexture.getTeamColor();
            }
        }
        super.a(paint, texture);
        return z;
    }
}
