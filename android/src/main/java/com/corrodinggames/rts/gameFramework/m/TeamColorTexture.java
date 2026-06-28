package com.corrodinggames.rts.gameFramework.m;

import android.graphics.Bitmap;
import com.corrodinggames.rts.game.teamColorsHueType;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.android.graphics.C0009fo;
import com.corrodinggames.rts.gameFramework.android.graphics.opengl.AtlasRegion;
import com.corrodinggames.rts.gameFramework.graphics.TeamColoring;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.f */
/* JADX INFO: loaded from: classes.dex */
public final class TeamColorTexture extends UnitTexture {
    public static boolean A;
    public static float G;
    public static C0009fo x;
    public static C0009fo y;
    public static C0009fo z;
    boolean B = false;
    boolean C = false;
    public int D;
    int E;
    teamColorsHueType F;
    private UnitTexture H;
    private UnitTexture I;

    private static synchronized void o() {
        if (!A) {
            try {
                GameEngine.log("Loading team shaders...");
                AtlasRegion atlasRegion = new AtlasRegion("assets/shaders/pureGreenTeamColor.frag");
                x = atlasRegion;
                atlasRegion.a("teamColor", -1);
                x.c();
                AtlasRegion atlasRegion2 = new AtlasRegion("assets/shaders/hueAddTeamColor.frag");
                y = atlasRegion2;
                atlasRegion2.a("teamColorAmount", 0.15f);
                y.a("teamColor", -1);
                y.c();
                AtlasRegion atlasRegion3 = new AtlasRegion("assets/shaders/hueShiftTeamColor.frag");
                z = atlasRegion3;
                atlasRegion3.a("teamColor", -1);
                z.c();
                A = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final String a() {
        return this.H == null ? "LazyColoring (error sourceBitmap==null)" : "LazyColoring(" + this.E + "):" + this.H.a();
    }

    public TeamColorTexture(UnitTexture unitTexture, int i, teamColorsHueType teamcolorshuetype, int i2) {
        if (unitTexture == null) {
            throw new RuntimeException("baseImage==null");
        }
        this.H = unitTexture;
        this.D = i;
        this.F = teamcolorshuetype;
        this.E = i2;
        this.H.a(this);
        this.k = null;
    }

    private void b(boolean z2) {
        if (GameEngine.areTeamShadersSupported()) {
            if (!A) {
                o();
            }
            if (this.F == teamColorsHueType.hueAdd) {
                a(y);
            } else if (this.F == teamColorsHueType.hueShift) {
                a(z);
            } else {
                a(x);
            }
            this.I = this.H;
            this.C = true;
            return;
        }

        try {
            if (z2) {
                GameEngine.log("Loading in lazy loaded bitmap:" + this.H.a() + " team:" + this.E);
            }
            long startTime = PerformanceProfiler.a();
            this.H.e();
            this.I = this.H.clone();
            this.I.e();
            if (this.F == teamColorsHueType.hueAdd) {
                colorHueAdd(this.H, this.I, this.D);
            } else if (this.F == teamColorsHueType.hueShift) {
                colorHueShift(this.H, this.I, this.D);
            } else {
                colorPureGreen(this.H, this.I, this.D);
            }
            this.I.j();
            this.I.k();
            this.H.k();
            this.H = null;
            double elapsed = PerformanceProfiler.a(startTime);
            if (elapsed > 1.0d) {
                GameEngine.log((this.F == teamColorsHueType.pureGreen ? "Standard " : "Hue ")
                        + "Colouring took:" + PerformanceProfiler.a(elapsed));
            }
            G = (float) (G + elapsed);
        } catch (OutOfMemoryError error) {
            GameEngine.log("Colouring failed with OOM");
            GameEngine.reportOOM(AssetType.gameImageColor, error);
            this.I = this.H;
            this.H = null;
        }
    }

    private static void colorPureGreen(UnitTexture source, UnitTexture target, int teamColor) {
        for (int y2 = 0; y2 < source.height(); y2++) {
            for (int x2 = 0; x2 < source.width(); x2++) {
                target.a(x2, y2, TeamColoring.pureGreen(source.b(x2, y2), teamColor));
            }
        }
    }

    private static void colorHueShift(UnitTexture source, UnitTexture target, int teamColor) {
        for (int y2 = 0; y2 < source.height(); y2++) {
            for (int x2 = 0; x2 < source.width(); x2++) {
                target.a(x2, y2, TeamColoring.hueShift(source.b(x2, y2), teamColor));
            }
        }
    }

    private static void colorHueAdd(UnitTexture source, UnitTexture target, int teamColor) {
        for (int x2 = 0; x2 < source.width(); x2++) {
            for (int y2 = 0; y2 < source.height(); y2++) {
                target.a(x2, y2, TeamColoring.hueAdd(source.b(x2, y2), teamColor));
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final Bitmap b() {
        if (this.C && !GameEngine.areTeamShadersSupported()) {
            GameEngine.log("Team shader coloring now disabled. Recoloring image: " + this.H.a());
            this.B = false;
            this.C = false;
            a((C0009fo) null);
        }
        if (!this.B) {
            b(true);
            this.B = true;
        }
        return this.I.k;
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final void m() {
        if (!this.B) {
            b(false);
            this.B = true;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.m.UnitTexture
    public final int l() {
        if ((!this.B) & (this.H != null)) {
            return this.H.l();
        }
        return super.l();
    }
}
