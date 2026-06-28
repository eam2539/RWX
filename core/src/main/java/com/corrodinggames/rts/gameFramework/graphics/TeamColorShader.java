package com.corrodinggames.rts.gameFramework.graphics;

import io.github.rwx.render.canvas.KoolPaint;

import java.io.IOException;

public class TeamColorShader extends ShaderProgram {
    int a;
    boolean b;

    public TeamColorShader(String str, boolean z) throws IOException {
        super(str);
        this.a = -99;
        this.b = z;
    }

    @Override
    public boolean a() {
        return this.b;
    }

    @Override
    public boolean b() {
        boolean z = false;
        if (-16711936 != this.a) {
            a("teamColor", -16711936);
            z = true;
            this.a = -16711936;
        }
        return z;
    }

    @Override
    public boolean a(KoolPaint paint, Texture texture) {
        boolean z = false;
        if (texture instanceof TeamColorTexture) {
            TeamColorTexture teamColorTexture = (TeamColorTexture) texture;
            if (teamColorTexture.getTeamColor() != this.a) {
                a("teamColor", teamColorTexture.getTeamColor());
                z = true;
                this.a = teamColorTexture.getTeamColor();
            }
        }
        super.a(paint, texture);
        return z;
    }
}
