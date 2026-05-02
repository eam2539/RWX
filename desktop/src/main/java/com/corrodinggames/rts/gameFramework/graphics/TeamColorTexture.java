package com.corrodinggames.rts.gameFramework.graphics;

import android.graphics.Bitmap;
import com.corrodinggames.rts.game.ColorMode;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/h.class */
public class TeamColorTexture extends Texture {
    public static ShaderProgram x;
    public static ShaderProgram y;
    public static ShaderProgram z;
    public static boolean A;
    boolean B = false;
    boolean C = false;
    private Texture H;
    private Texture I;
    int D;
    int E;
    ColorMode F;
    public static float G;

    public static synchronized void C() {
        if (A) {
            return;
        }
        try {
            GameEngine.log("Loading team shaders...");
            x = new TeamColorShader("assets/shaders/pureGreenTeamColor.frag", true);
            x.a("teamColor", -1);
            x.c();
            y = new TeamColorShader("assets/shaders/hueAddTeamColor.frag", false);
            y.a("teamColorAmount", 0.15f);
            y.a("teamColor", -1);
            y.c();
            z = new TeamColorShader("assets/shaders/hueShiftTeamColor.frag", false);
            z.a("teamColor", -1);
            z.c();
            A = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void D() {
        if (!A) {
            C();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public String a() {
        if (this.H == null) {
            return "LazyColoring (error sourceBitmap==null)";
        }
        return "LazyColoring(" + this.E + "):" + this.H.a();
    }

    public TeamColorTexture(Texture texture, int i, ColorMode colorMode, int i2) {
        if (texture == null) {
            throw new RuntimeException("baseImage==null");
        }
        this.H = texture;
        this.D = i;
        this.F = colorMode;
        this.E = i2;
        this.H.a(this);
        this.k = null;
    }

    public void c(boolean z2) {
        if (GameEngine.areShadersSupported()) {
            if (z2) {
            }
            D();
            if (this.F == ColorMode.hueAdd) {
                a(y);
            } else if (this.F == ColorMode.hueShift) {
                a(z);
            } else {
                a(x);
            }
            this.I = this.H;
            this.C = true;
            return;
        }
        if (this.H.A()) {
            GameEngine.log("Lazy loaded bitmap using errored image: " + this.H.a());
            this.I = this.H;
            return;
        }
        if (z2) {
            try {
                GameEngine.log("Loading in lazy loaded bitmap:" + this.H.a() + " team:" + this.E);
            } catch (OutOfMemoryError e) {
                GameEngine.log("Colouring failed with OOM");
                GameEngine.reportOOM(AssetType.gameImageColor, e);
                this.I = GameEngine.getInstance().graphicsEngine2.r();
                return;
            }
        }
        long jA = PerformanceProfiler.a();
        this.H.i();
        this.I = this.H.clone();
        this.I.j();
        Texture[] textureArr = {this.I};
        int[] iArr = {this.D};
        int[] iArr2 = {this.E};
        long jA2 = PerformanceProfiler.a();
        if (this.F == ColorMode.hueAdd) {
            PlayerTeam.b(this.H, textureArr, iArr);
        } else if (this.F == ColorMode.hueShift) {
            PlayerTeam.a(this.H, textureArr, iArr, iArr2);
        } else {
            PlayerTeam.a(this.H, textureArr, iArr);
        }
        double dA = PerformanceProfiler.a(jA2);
        this.I.p();
        this.I.s();
        this.H.q();
        this.H = null;
        double dA2 = PerformanceProfiler.a(jA);
        if (dA2 > 1.0d) {
            GameEngine.log((this.F == ColorMode.pureGreen ? "Standard " : "Hue ") + "Colouring took:" + PerformanceProfiler.a(dA2) + " (" + PerformanceProfiler.a(dA) + ")");
        }
        G = (float) (((double) G) + dA2);
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public Bitmap b() {
        if (this.C && !GameEngine.areShadersSupported()) {
            GameEngine.log("Team shader coloring now disabled. Recoloring image: " + this.H.a());
            this.B = false;
            this.C = false;
            a((ShaderProgram) null);
        }
        if (!this.B) {
            c(true);
            this.B = true;
        }
        return this.I.k;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public Texture c() {
        if (this.C && !GameEngine.areShadersSupported()) {
            GameEngine.log("Team shader coloring now disabled. Recoloring image: " + this.H.a());
            this.B = false;
            this.C = false;
            a((ShaderProgram) null);
        }
        if (!this.B) {
            if (G > 60.0f) {
            }
            c(true);
            this.B = true;
        }
        if (this.I == null) {
            throw new RuntimeException("coloredBitmap==null");
        }
        return this.I;
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public void w() {
        if (!this.B) {
            c(false);
            this.B = true;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.graphics.Texture
    public int u() {
        if ((!this.B) & (this.H != null)) {
            return this.H.u();
        }
        return super.u();
    }
}
