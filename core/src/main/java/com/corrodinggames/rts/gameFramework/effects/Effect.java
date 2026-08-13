package com.corrodinggames.rts.gameFramework.effects;

import com.corrodinggames.rts.game.units.custom.EffectTemplate;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.ShaderProgram;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;
import io.github.rwx.geometry.RectF;
import io.github.rwx.mod.registry.RenderRegistry;
import io.github.rwx.render.canvas.*;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.d.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/d/e.class */
public final class Effect {
    private final EffectManager ay;
    public GameObject b;
    public boolean c;
    public boolean d;
    public int g;
    public boolean o;
    public boolean p;
    public boolean r;
    public boolean s;
    public float t;
    public boolean u;
    public boolean v;
    public int x;
    public int y;
    public short A;
    public float E;
    public float F;
    public float G;
    public float scaleXFrom;
    public float scaleXTo;
    public float scaleYFrom;
    public float scaleYTo;
    public float imageAnchorY;
    public boolean H;
    public float I;
    public float J;
    public float K;
    public boolean L;
    public float M;
    public float N;
    public float O;
    public float P;
    public float Q;
    public float R;
    public float S;
    public float T;
    public float U;
    public float V;
    public float W;
    public float Y;
    public float Z;
    public String aa;
    public KoolPaint ab;
    public float ac;
    public float ad;
    public boolean ae;
    public int af;
    public int ag;
    public boolean ah;
    public boolean ai;
    public float aj;
    public float ak;
    public boolean al;
    public boolean am;
    public int ap;
    public int aq;
    public float au;
    public int av;
    public boolean aw;
    public static int h = 1;
    public static int i = 2;
    public static int j = 3;
    public static int k = 4;
    public static int l = 5;
    public static int m = 6;
    public static int n = 7;
    public static KoolMultiplyAddColorFilter C = null;
    public static int D = 0;
    public static GamePaint[] ax = new GamePaint[128];
    public EffectTemplate a = EffectTemplate.defaultEffectTemplate;
    public boolean e = true;
    public boolean f = false;
    public EffectQuality q = EffectQuality.verylow;
    public float w = 1.0f;
    public float z = -1.0f;
    public KoolMultiplyAddColorFilter B = null;
    public float X = 0.0f;
    public boolean an = false;
    public float ao = 0.0f;
    public short ar = 2;
    public boolean as = false;
    public GamePaint at = getFreshTexture();

    protected Effect(EffectManager effectManager) {
        this.ay = effectManager;
    }

    static {
        for (int i2 = 0; i2 < ax.length; i2++) {
            ax[i2] = getFreshTexture();
            ax[i2].c((int) ((i2 / (ax.length - 1)) * 255.0f));
        }
    }

    /* JADX INFO: renamed from: a */
    public static GamePaint getFreshTexture() {
        return GameViewUtils.b();
    }

    /* JADX INFO: renamed from: a */
    public GamePaint getTexture(float f) {
        int length = (int) (f * (ax.length - 1));
        if (length < 0) {
            length = 0;
        }
        if (length > ax.length - 1) {
            length = ax.length - 1;
        }
        return ax[length];
    }

    /* JADX INFO: renamed from: b */
    public void reset() {
        if (this.o) {
            this.o = false;
            this.ay.activeEffectsCount--;
            EffectManager.useStrictCounting = true;
            if (this.a.alsoEmitEffectsOnDeath != null && this.A < 20) {
                float f = this.I;
                float f2 = this.J;
                float f3 = this.K;
                if (this.b != null) {
                    f += this.b.posX;
                    f2 += this.b.posY;
                    f3 += this.b.posZ;
                }
                this.a.alsoEmitEffectsOnDeath.a(f, f2, f3, this.Y, this.b, 0, this.A);
            }
        }
    }

    /* JADX INFO: renamed from: c */
    public void free() {
        this.a = EffectTemplate.defaultEffectTemplate;
        this.q = EffectQuality.verylow;
        this.b = null;
        this.c = false;
        this.d = false;
        this.e = true;
        this.f = false;
        this.g = 0;
        this.p = false;
        this.I = 0.0f;
        this.J = 0.0f;
        this.L = false;
        this.M = 0.0f;
        this.N = 0.0f;
        this.O = 0.0f;
        this.K = 0.0f;
        this.ar = (short) 2;
        this.an = false;
        this.ao = 0.0f;
        this.ae = false;
        this.ak = 0.0f;
        this.aj = 0.0f;
        this.ag = 0;
        this.ah = false;
        this.ai = false;
        this.al = false;
        this.am = false;
        this.ap = 0;
        this.aq = 0;
        this.U = 0.0f;
        this.V = 15.0f;
        this.W = this.V;
        this.X = 0.0f;
        this.r = false;
        this.s = false;
        this.t = 0.0f;
        this.F = 1.0f;
        this.G = 1.0f;
        this.scaleXFrom = Float.NaN;
        this.scaleXTo = Float.NaN;
        this.scaleYFrom = Float.NaN;
        this.scaleYTo = Float.NaN;
        this.imageAnchorY = 0.5f;
        this.H = false;
        this.u = false;
        this.v = false;
        this.w = 1.0f;
        this.E = 1.0f;
        this.Y = 0.0f;
        this.Z = 0.0f;
        this.P = 0.0f;
        this.Q = 0.0f;
        this.R = 0.0f;
        this.S = 0.0f;
        this.T = 0.0f;
        this.aa = null;
        this.ab = null;
        this.ac = 0.0f;
        this.ad = 0.0f;
        this.A = (short) 0;
        this.x = -1;
        this.B = null;
        this.y = -1;
        this.z = -1.0f;
        this.at.a((KoolColorFilter) null);
        this.at.a((io.github.rwx.render.canvas.KoolCanvasBlendMode) null);
        this.aw = false;
        this.at.a((KoolDisplacementEffect) null);
        this.at.a((ShaderProgram) null);
        this.as = false;
    }

    /* JADX INFO: renamed from: a */
    public void recycle(Effect effect) {
        this.a = effect.a;
        this.q = effect.q;
        this.g = effect.g;
        this.b = effect.b;
        this.c = effect.c;
        this.d = effect.d;
        this.e = effect.e;
        this.p = effect.p;
        this.I = effect.I;
        this.J = effect.J;
        this.L = effect.L;
        this.M = effect.M;
        this.N = effect.N;
        this.O = effect.O;
        this.K = effect.K;
        this.ar = effect.ar;
        this.an = effect.an;
        this.ao = effect.ao;
        this.ae = effect.ae;
        this.ak = effect.ak;
        this.aj = effect.aj;
        this.ag = effect.ag;
        this.ah = effect.ah;
        this.ai = effect.ai;
        this.al = effect.ah;
        this.am = effect.am;
        this.ap = effect.ap;
        this.aq = effect.aq;
        this.U = effect.U;
        this.V = effect.V;
        this.W = effect.W;
        this.X = effect.X;
        this.r = effect.r;
        this.s = effect.s;
        this.t = effect.t;
        this.F = effect.F;
        this.G = effect.G;
        this.scaleXFrom = effect.scaleXFrom;
        this.scaleXTo = effect.scaleXTo;
        this.scaleYFrom = effect.scaleYFrom;
        this.scaleYTo = effect.scaleYTo;
        this.imageAnchorY = effect.imageAnchorY;
        this.H = effect.H;
        this.u = effect.u;
        this.v = effect.v;
        this.w = effect.w;
        this.E = effect.E;
        this.Y = effect.Y;
        this.Z = effect.Z;
        this.P = effect.P;
        this.Q = effect.Q;
        this.R = effect.R;
        this.S = effect.S;
        this.T = effect.T;
        this.aa = effect.aa;
        this.ab = effect.ab;
        this.ac = effect.ac;
        this.ad = effect.ad;
        this.A = effect.A;
        this.x = effect.x;
        this.y = effect.y;
        this.z = effect.z;
        this.B = effect.B;
        this.as = effect.as;
        this.at.a(effect.at.getBlendMode());
    }

    /* JADX INFO: renamed from: b */
    public void draw(float f) {
        this.U = Utility.moveTowardsZero(this.U, f);
        if (this.U > 0.0f) {
            return;
        }
        this.V -= f;
        if (this.b != null && this.b.isDestroyed && !this.a.liveAfterAttachedDies) {
            this.V = -1.0f;
        }
        if (this.V < 0.0f) {
            reset();
            return;
        }
        if (this.ae) {
            if (this.al) {
                this.ak -= this.aj * f;
            } else {
                this.ak += this.aj * f;
            }
            if (this.ah) {
                if (this.al) {
                    if (this.ak < this.af) {
                        if (!this.ai) {
                            reset();
                            return;
                        } else {
                            this.al = false;
                            this.ak = this.af;
                        }
                    }
                } else if (this.ak >= this.ag + 1) {
                    this.al = true;
                    this.ak = this.ag;
                }
            } else if (this.ak >= this.ag + 1) {
                if (!this.ai) {
                    reset();
                    return;
                }
                this.ak = this.af;
            }
            this.ap = (int) this.ak;
        }
        if (this.u) {
            this.R -= (this.R * 0.002f) * f;
            this.P -= f * 0.0015f;
        }
        if (this.v) {
            if (this.K > 0.0f) {
                this.R -= (0.1f * this.w) * f;
            } else {
                if (this.R < 0.0f) {
                    this.R = -this.R;
                    this.R *= 0.5f;
                    this.R = Utility.moveTowardsZero(this.R, 1.3f);
                }
                if (this.K < 0.0f) {
                    this.K = 0.0f;
                }
                if (this.R < 0.2d) {
                    this.ar = (short) 1;
                }
                this.P = Utility.moveTowardsZero(this.P, 0.15f * f);
                this.Q = Utility.moveTowardsZero(this.Q, 0.15f * f);
                this.Z = Utility.moveTowardsZero(this.Z, 1.0f * f);
            }
        }
        this.I += this.P * f;
        this.J += this.Q * f;
        this.K += this.R * f;
        this.Y += this.Z * f;
        if (this.a.trailEffect != null) {
            this.X += f;
            if (this.X > this.a.trailEffectRate) {
                this.X = 0.0f;
                if (this.A < 20) {
                    float f2 = this.I;
                    float f3 = this.J;
                    float f4 = this.K;
                    if (this.b != null) {
                        f2 += this.b.posX;
                        f3 += this.b.posY;
                        f4 += this.b.posZ;
                    }
                    this.a.trailEffect.a(f2, f3, f4, this.Y, this.b, 0, this.A);
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public static void getEffectSprite(int i2, SpriteSheet spriteSheet, Rect rect) {
        int i3 = 0;
        if (i2 >= spriteSheet.h) {
            i3 = 0 + (i2 / spriteSheet.h);
            i2 %= spriteSheet.h;
        }
        int i4 = spriteSheet.d + (i2 * spriteSheet.f);
        int i5 = spriteSheet.e + (i3 * spriteSheet.g);
        rect.a = i4;
        rect.b = i5;
        rect.c = i4 + spriteSheet.b;
        rect.d = i5 + spriteSheet.c;
    }

    /* JADX INFO: renamed from: a */
    public boolean update(GameEngine gameEngine, boolean z) {
        SpriteSheet spriteSheet;
        PointF pointFCreatePointWithOffset;
        float fClampTo255;
        GamePaint texture;
        Rect rect = EffectManager.rect;
        RectF rectF = EffectManager.rectF;
        if (this.U > 0.0f) {
            return false;
        }
        if (z && this.K < 1.0f) {
            return false;
        }
        if (RenderRegistry.drawEffect(this, gameEngine, z)) {
            return true;
        }
        if (this.a.imageStrip != null) {
            spriteSheet = this.a.imageStrip;
        } else {
            spriteSheet = EffectManager.effectTemplates[this.aq];
        }
        if (!spriteSheet.k) {
            getEffectSprite(this.ap, spriteSheet, rect);
        } else {
            rect.a(0, 0, spriteSheet.i.m(), spriteSheet.i.l());
        }
        if (!z) {
            pointFCreatePointWithOffset = Utility.createPointWithOffset(this.I, this.J, this.K);
        } else {
            pointFCreatePointWithOffset = Utility.createPointWithOffset(this.I, this.J, 0.0f);
        }
        boolean z2 = this.ar == 4;
        float fFromHexString = 1.0f;
        if (this.G != 1.0f || this.F != 1.0f || this.H) {
            fFromHexString = Utility.lerp(this.G, this.F, 1.0f - (this.V / this.W));
            boolean z3 = this.ar != 4;
            if (this.H && z3) {
                fFromHexString = fFromHexString * (1.0f / gameEngine.zoom) * gameEngine.screenScale;
            }
        }
        float scaleProgress = 1.0f - (this.V / this.W);
        float scaleX = Float.isNaN(this.scaleXFrom)
                ? fFromHexString
                : Utility.lerp(this.scaleXFrom, this.scaleXTo, scaleProgress);
        float scaleY = Float.isNaN(this.scaleYFrom)
                ? fFromHexString
                : Utility.lerp(this.scaleYFrom, this.scaleYTo, scaleProgress);
        rectF.a(pointFCreatePointWithOffset.x, pointFCreatePointWithOffset.y, pointFCreatePointWithOffset.x + rect.b(), pointFCreatePointWithOffset.y + rect.c());
        if (this.an) {
            rectF.a((-rectF.b()) / 2.0f, (-rectF.c()) / 2.0f);
        }
        if (this.ao != 0.0f) {
            rectF.a(0.0f, rectF.c() * this.ao * fFromHexString);
        }
        if (this.imageAnchorY != 0.5f) {
            rectF.a(0.0f, rectF.c() * (0.5f - this.imageAnchorY));
        }
        if (this.b != null) {
            if (!z && !this.c) {
                rectF.a(this.b.posX, this.b.posY - this.b.posZ);
            } else {
                rectF.a(this.b.posX, this.b.posY);
            }
        }
        if ((!z2 || this.L) && !Utility.rectanglesOverlap(gameEngine.bufferedVisibleWorldRect, rectF)) {
            return false;
        }
        if (!this.e && !z2 && !this.f) {
            if (!gameEngine.tileMap.isWorldPointVisibleForTeam(rectF.d(), rectF.e(), gameEngine.playerTeam)) {
                return false;
            }
            this.f = true;
        }
        if (!z2) {
            rectF.a(-gameEngine.viewpointXSnapped, -gameEngine.viewpointYSnapped);
        }
        if (this.S != 0.0f) {
            rectF.a(0.0f, Utility.fastSin(((this.W - this.V) / this.T) * 360.0f) * this.S);
        }
        float f = this.W - this.V;
        float fA = 1.0f;
        float f2 = 1.0f;
        float f3 = 1.0f;
        float f4 = 1.0f;
        boolean z4 = this.at.getBlendMode() != null;
        if (this.x != -1) {
            fA = KoolArgbColor.a(this.x) * 0.003921569f;
            int iB = KoolArgbColor.b(this.x);
            int iC = KoolArgbColor.c(this.x);
            int iD = KoolArgbColor.d(this.x);
            if (iB != 255 || iC != 255 || iD != 255) {
                z4 = true;
                f2 = iB * 0.003921569f;
                f3 = iC * 0.003921569f;
                f4 = iD * 0.003921569f;
            }
        }
        if (this.z >= 0.0f) {
            float fA2 = KoolArgbColor.a(this.y) * 0.003921569f;
            float fB = KoolArgbColor.b(this.y) * 0.003921569f;
            float fC = KoolArgbColor.c(this.y) * 0.003921569f;
            float fD = KoolArgbColor.d(this.y) * 0.003921569f;
            if (this.z <= f) {
                fA = fA2;
                z4 = true;
                f2 = fB;
                f3 = fC;
                f4 = fD;
            } else {
                float f5 = f / this.z;
                float f6 = 1.0f - f5;
                fA = (fA * f6) + (fA2 * f5);
                z4 = true;
                f2 = (f2 * f6) + (fB * f5);
                f3 = (f3 * f6) + (fC * f5);
                f4 = (f4 * f6) + (fD * f5);
            }
        }
        if (this.r && f >= this.t) {
            fClampTo255 = fA * (this.V / (this.W - this.t)) * this.E;
        } else if (this.s && f < this.t) {
            fClampTo255 = fA * (f / this.t) * this.E;
        } else {
            fClampTo255 = fA * this.E;
        }
        if (fClampTo255 > 1.0f) {
            fClampTo255 = 1.0f;
        }
        if (fClampTo255 < 0.0f) {
            fClampTo255 = 0.0f;
        }
        boolean z5 = false;
        GraphicsEngine graphicsEngine = gameEngine.renderGraphicsEngine;
        if (this.Y != 0.0f) {
            if (0 == 0) {
                z5 = true;
                graphicsEngine.k();
            }
            graphicsEngine.a(this.Y + 90.0f, rectF.d(), rectF.e());
        }
        if (scaleX != 1.0f || scaleY != 1.0f) {
            if (!z5) {
                z5 = true;
                graphicsEngine.k();
            }
            graphicsEngine.a(scaleX, scaleY, rectF.d(), rectF.e());
        }
        if (z) {
            fClampTo255 = Utility.clampTo255(fClampTo255 / 3.0f, 0.0f, 1.0f);
            f2 = 0.0f;
            f3 = 0.0f;
            f4 = 0.0f;
            z4 = true;
        }
        if (z4 && graphicsEngine.backendCapabilities().getRequiresImageTintColorFilter() && !z && this.B == null) {
            int iLongToIntArray = Utility.packArgb(255, (int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f));
            if (C != null && D == iLongToIntArray) {
                this.B = C;
            } else {
                C = new KoolMultiplyAddColorFilter(iLongToIntArray, 0);
                D = iLongToIntArray;
                this.B = C;
            }
        }
        KoolMultiplyAddColorFilter multiplyAddColorFilter = this.B;
        if (multiplyAddColorFilter != null) {
            if (!this.aw) {
                this.at.a(multiplyAddColorFilter);
                this.aw = true;
            }
            z4 = true;
        } else if (this.aw) {
            this.at.a((KoolColorFilter) null);
            this.aw = false;
        }
        if (this.ar == 3) {
            if (EffectManager.displacementEffect == null) {
                GameEngine.log("Loading displacement effect");
                EffectManager.displacementEffect = new KoolDisplacementEffect();
            }
            if (EffectManager.shader == null) {
                try {
                    EffectManager.shader = new ShaderProgram("assets/shaders/post_displacement.frag");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (this.ay.texture != null) {
                KoolDisplacementEffect koolDisplacementEffect = EffectManager.displacementEffect;
                koolDisplacementEffect.configure(this.ay.texture, 0.12f * gameEngine.zoom);
                this.at.a(koolDisplacementEffect);
                ShaderProgram shaderProgram = EffectManager.shader;
                shaderProgram.a("screenBase", this.ay.texture);
                shaderProgram.b("screenBaseSize", this.ay.texture);
                shaderProgram.a("u_resolution", gameEngine.screenWidth, gameEngine.screenHeight);
                shaderProgram.a("u_offsetBy", 0.12f * gameEngine.zoom);
                shaderProgram.a("u_uiScaling", 1.0f);
                this.at.a(shaderProgram);
                z4 = true;
            }
        }
        if (!z4) {
            texture = getTexture(fClampTo255);
        } else {
            texture = this.at;
            int iLongToIntArray2 = Utility.packArgb(255, (int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f));
            float f7 = this.au - fClampTo255;
            if (f7 < -0.01f || f7 > 0.01f || this.av != iLongToIntArray2) {
                this.au = fClampTo255;
                this.av = iLongToIntArray2;
                this.at.b(Utility.packArgb((int) (fClampTo255 * 255.0f), (int) (f2 * 255.0f), (int) (f3 * 255.0f), (int) (f4 * 255.0f)));
            }
        }
        if (this.aa != null) {
            KoolPaint paint = texture;
            if (this.ab != null) {
                paint = this.ab;
            }
            graphicsEngine.a(this.aa, rectF.d() + this.ac, rectF.e() + this.ad, paint);
        }
        if (this.L) {
            PointF pointFCreatePointWithOffset2 = Utility.createPointWithOffset(this.M, this.N, this.O);
            graphicsEngine.a(rectF.a, rectF.b, pointFCreatePointWithOffset2.x - gameEngine.viewpointXSnapped, pointFCreatePointWithOffset2.y - gameEngine.viewpointYSnapped, this.ay.linePaint);
        } else if (z) {
            if (spriteSheet.j != null) {
                graphicsEngine.a(spriteSheet.j, rect, rectF, texture);
            }
        } else {
            graphicsEngine.a(spriteSheet.i, rect, rectF, texture);
        }
        if (z5) {
            graphicsEngine.l();
            return true;
        }
        return true;
    }
}
