package com.corrodinggames.rts.game;

import android.graphics.Color;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.CustomProjectileTemplate;
import com.corrodinggames.rts.game.units.custom.CustomUnitSpawnList;
import com.corrodinggames.rts.game.units.custom.UnitSpawnList;
import com.corrodinggames.rts.game.units.custom.UnitSpawner;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/g.class */
public class ProjectileTemplate {
    public static final ProjectileTemplate a = new ProjectileTemplate();
    public int b;
    public int c;
    public boolean d;
    public boolean e;
    public boolean f;
    public boolean g;
    public boolean h;
    public float u;
    public float v;
    public short y;
    public boolean A;
    public Texture B;
    public Texture C;
    public float D;
    public float E;
    public float F;
    public float G;
    public float H;
    public float Q;
    public float R;
    public float S;
    public boolean T;
    public Texture Y;
    public Texture Z;
    public boolean aa;
    public Texture ab;
    public boolean ac;
    public float ad;
    public CustomUnitSpawnList ah;
    public CustomUnitSpawnList ai;
    public UnitSpawnList aj;
    public UnitSpawnList ak;
    public UnitSpawnList al;
    public float am;
    public boolean aq;
    public boolean az;
    public AnimationSet aD;
    public boolean aI;
    public float aK;
    public float aL;
    public float aM;
    public boolean aN;
    public boolean aO;
    public float aP;
    public float aQ;
    public float aV;
    public CustomUnitSpawnList aX;
    public CustomUnitSpawnList aY;
    public UnitSpawner aZ;
    public int ba;
    public boolean bb;
    public boolean bc;
    public AnimationSet bd;
    public int i = 35;
    public float j = -1.0f;
    public boolean k = false;
    public boolean l = false;
    public boolean m = false;
    public boolean n = false;
    public float o = 1.0f;
    public boolean p = false;
    public boolean q = false;
    public boolean r = false;
    public boolean s = false;
    public boolean t = false;
    public float w = 5.0f;
    public short x = -1;
    public short z = -1;
    public boolean I = false;
    public boolean J = false;
    public float K = -1.0f;
    public boolean L = false;
    public boolean M = false;
    public boolean N = false;
    public float O = -1.0f;
    public float P = -1.0f;
    public boolean U = false;
    public boolean V = false;
    public boolean W = false;
    public boolean X = false;
    public boolean ae = false;
    public boolean af = false;
    public float ag = 3.0f;
    public float an = 5.0f;
    public int ao = -1;
    public float ap = 0.5f;
    public boolean ar = false;
    public float as = -1.0f;
    public float at = -1.0f;
    public float au = -1.0f;
    public float av = 0.1f;
    public boolean aw = false;
    public float ax = 120.0f;
    public float ay = 15.0f;
    public float aA = 5.0f;
    public float aB = 120.0f;
    public float aC = 15.0f;
    public int aE = Color.a(255, 255, 255, 255);
    public float aF = 1.0f;
    public float aG = 0.0f;
    public float aH = 1.0f;
    public boolean aJ = true;
    public float aR = 1.0f;
    public float aS = 1.0f;
    public float aT = 1.0f;
    public float aU = 1.0f;
    public float aW = -1.0f;
    public FastArrayList be = null;
    public FastArrayList bf = null;
    public FastArrayList bg = null;

    public CustomUnitSpawnList a(BaseUnit baseUnit) {
        FastArrayList<UnitFilter> fastArrayList = this.bg;
        if (fastArrayList != null && fastArrayList.size > 0) {
            for (UnitFilter unitFilter : fastArrayList) {
                if (unitFilter.a(baseUnit) && unitFilter.g != null) {
                    return unitFilter.g;
                }
            }
            return null;
        }
        return null;
    }

    public float a(BaseUnit baseUnit, float f, boolean z) {
        FastArrayList<UnitFilter> fastArrayList;
        float f2;
        if (!z) {
            fastArrayList = this.be;
        } else {
            fastArrayList = this.bf;
        }
        if (fastArrayList != null && fastArrayList.size > 0) {
            for (UnitFilter unitFilter : fastArrayList) {
                if (unitFilter.a(baseUnit)) {
                    if (!z) {
                        if (unitFilter.e != null) {
                            unitFilter.e.h(baseUnit);
                        }
                        f2 = unitFilter.c;
                    } else {
                        if (unitFilter.f != null) {
                            unitFilter.f.h(baseUnit);
                        }
                        f2 = unitFilter.d;
                    }
                    f *= f2;
                }
            }
        }
        return f;
    }

    public static void a(ProjectileTemplate projectileTemplate, GameOutputStream gameOutputStream) throws IOException {
        if (projectileTemplate == a) {
            gameOutputStream.writeByte(0);
        } else if (projectileTemplate instanceof CustomProjectileTemplate) {
            gameOutputStream.writeByte(1);
            CustomProjectileTemplate.a((CustomProjectileTemplate) projectileTemplate, gameOutputStream);
        } else {
            GameEngine.logWarningAndStack("writeOutLink: Unhandled projectile type");
            gameOutputStream.writeByte(0);
        }
    }

    public static ProjectileTemplate a(GameInputStream gameInputStream) throws IOException {
        byte b = gameInputStream.readByte();
        if (b == 0) {
            return a;
        }
        if (b == 1) {
            ProjectileTemplate projectileTemplateB = CustomProjectileTemplate.b(gameInputStream);
            if (projectileTemplateB == null) {
                return a;
            }
            return projectileTemplateB;
        }
        throw new IOException("Unknown projectile type:" + ((int) b));
    }
}
