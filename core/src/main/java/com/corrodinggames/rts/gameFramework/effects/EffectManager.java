package com.corrodinggames.rts.gameFramework.effects;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.map.MapTile;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.ShaderProgram;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.Rect;
import io.github.rwx.geometry.RectF;
import io.github.rwx.render.canvas.KoolArgbColor;
import io.github.rwx.render.canvas.KoolDisplacementEffect;
import io.github.rwx.render.canvas.KoolMultiplyAddColorFilter;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.d.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/d/c.class */
public final class EffectManager {

    /* JADX INFO: renamed from: h */
    public static boolean useStrictCounting;

    /* JADX INFO: renamed from: i */
    public int maxParticlesVeryLow;

    /* JADX INFO: renamed from: j */
    public int maxParticlesLow;

    /* JADX INFO: renamed from: k */
    public static KoolDisplacementEffect displacementEffect;

    public static ShaderProgram shader;

    /* JADX INFO: renamed from: l */
    public Texture texture;

    /* JADX INFO: renamed from: m */
    public Texture texture2;

    /* JADX INFO: renamed from: s */
    public static SpriteSheet[] effectTemplates;

    /* JADX INFO: renamed from: f */
    public static Effect[] effects = new Effect[0];

    /* JADX INFO: renamed from: g */
    public static int nextFreeEffect = 0;

    /* JADX INFO: renamed from: n */
    public static final RectF rectF = new RectF();

    /* JADX INFO: renamed from: o */
    public static final Rect rect = new Rect();

    /* JADX INFO: renamed from: p */
    public static final Rect rect2 = new Rect();

    /* JADX INFO: renamed from: q */
    public static final KoolPaint paint = new KoolPaint();

    /* JADX INFO: renamed from: r */
    public static final KoolPaint paint2 = new KoolPaint();

    /* JADX INFO: renamed from: a */
    public int activeEffectsCount = 0;

    /* JADX INFO: renamed from: b */
    public int maxEffectsVeryLow = 80;

    /* JADX INFO: renamed from: c */
    public int maxEffectsLow = 100;

    /* JADX INFO: renamed from: d */
    public int maxEffectsHigh = 110;

    /* JADX INFO: renamed from: e */
    public int maxEffectsVeryHigh = 120;

    /* JADX INFO: renamed from: y */
    private boolean[] activeEffectLayerFlags = new boolean[5];

    /* JADX INFO: renamed from: t */
    EffectQuality overrideEffectQuality = null;

    /* JADX INFO: renamed from: u */
    boolean onlyOnScreen = false;

    /* JADX INFO: renamed from: v */
    boolean forceHighQuality = false;

    /* JADX INFO: renamed from: w */
    public final KoolPaint linePaint = new KoolPaint();

    /* JADX INFO: renamed from: x */
    float lastUpdate = 0.0f;

    /* JADX INFO: renamed from: a */
    public Effect getNewEffect(EffectQuality effectQuality) {
        int i = 0;
        int fps = GameEngine.getInstance().getFps();
        if (fps < 13) {
            i = -this.maxParticlesLow;
        } else if (fps < 28) {
            i = -this.maxParticlesVeryLow;
        }
        int i2 = this.activeEffectsCount;
        if (effectQuality == EffectQuality.verylow && i2 > this.maxEffectsVeryLow + i) {
            return null;
        }
        if (effectQuality == EffectQuality.low && i2 > this.maxEffectsLow + i) {
            return null;
        }
        if (effectQuality == EffectQuality.high && i2 > this.maxEffectsHigh + i) {
            return null;
        }
        if (effectQuality == EffectQuality.veryhigh && i2 > this.maxEffectsVeryHigh + i) {
            return null;
        }
        Effect effectFindFreeEffect = findFreeEffect(true, (EffectQuality) null);
        if (effectFindFreeEffect == null && (effectQuality == EffectQuality.critical || effectQuality == EffectQuality.veryhigh)) {
            effectFindFreeEffect = findFreeEffect(false, EffectQuality.high);
        }
        if (effectFindFreeEffect != null) {
            if (!effectFindFreeEffect.o) {
                effectFindFreeEffect.o = true;
                this.activeEffectsCount++;
            }
            return effectFindFreeEffect;
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    private Effect findFreeEffect(boolean z, EffectQuality effectQuality) {
        Effect[] effectArr = effects;
        int length = effectArr.length;
        if (z && effectQuality == null) {
            for (int i = 0; i < length; i++) {
                Effect effect = effectArr[i];
                if (!effect.o) {
                    if (nextFreeEffect == i) {
                        nextFreeEffect++;
                    }
                    return effect;
                }
            }
            return null;
        }
        for (Effect effect2 : effectArr) {
            if ((!z || !effect2.o) && (effectQuality == null || effect2.q.isLowerThan(effectQuality))) {
                return effect2;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    public void createExplosion(float f, float f2, float f3) {
        createExplosionWithVelocity(f, f2, f3, 0.0f, 20.0f);
    }

    /* JADX INFO: renamed from: a */
    public void createExplosionWithVelocity(float f, float f2, float f3, float f4, float f5) {
        for (int i = 0; i < 7; i++) {
            Effect effectCreateSmallExplosion = createSmallExplosion(f + Utility.randomFloatInRange(-20.0f, 20.0f), f2 + Utility.randomFloatInRange(-20.0f, 20.0f), f3);
            if (effectCreateSmallExplosion != null) {
                effectCreateSmallExplosion.U = f4 + Utility.randomFloatInRange(0.0f, f5);
                effectCreateSmallExplosion.aj = Utility.randomFloatInRange(0.3f, 0.6f);
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public float random(float f, float f2) {
        return Utility.randomFloatInRange(f, f2);
    }

    /* JADX INFO: renamed from: b */
    public Effect createSmallExplosion(float f, float f2, float f3) {
        setOnlyOnScreen();
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.high);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.aq = 1;
            effectCreateEffectInternal.ae = true;
            effectCreateEffectInternal.ak = 0.0f;
            effectCreateEffectInternal.aj = 0.5f;
            effectCreateEffectInternal.ag = 12;
            effectCreateEffectInternal.ap = 0;
            effectCreateEffectInternal.V = 35.0f;
            effectCreateEffectInternal.W = effectCreateEffectInternal.V - 10.0f;
            effectCreateEffectInternal.r = true;
            effectCreateEffectInternal.E = 0.7f;
            effectCreateEffectInternal.Y = random(-180.0f, 180.0f);
            float fRandom = random(0.8f, 1.0f);
            effectCreateEffectInternal.G = fRandom;
            effectCreateEffectInternal.F = fRandom;
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: c */
    public Effect createLargeExplosion(float f, float f2, float f3) {
        setOnlyOnScreen();
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.high);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.aq = 13;
            effectCreateEffectInternal.ae = true;
            effectCreateEffectInternal.ak = 3.0f;
            effectCreateEffectInternal.aj = 0.5f;
            effectCreateEffectInternal.ag = 7;
            effectCreateEffectInternal.ap = 0;
            effectCreateEffectInternal.V = 35.0f;
            effectCreateEffectInternal.W = effectCreateEffectInternal.V - 10.0f;
            effectCreateEffectInternal.r = true;
            effectCreateEffectInternal.E = 1.0f;
            effectCreateEffectInternal.G = 0.5f;
            effectCreateEffectInternal.F = 0.5f;
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: a */
    public Effect createLaserEffect(float f, float f2, float f3, float f4, float f5, float f6) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!gameEngine.tileMap.isWorldPointVisibleForTeam(f, f2, gameEngine.playerTeam) && !gameEngine.tileMap.isWorldPointVisibleForTeam(f4, f5, gameEngine.playerTeam)) {
            return null;
        }
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, true, EffectQuality.high);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.an = false;
            effectCreateEffectInternal.V = 5.0f;
            effectCreateEffectInternal.W = effectCreateEffectInternal.V;
            effectCreateEffectInternal.r = true;
            effectCreateEffectInternal.E = 1.0f;
            effectCreateEffectInternal.L = true;
            effectCreateEffectInternal.M = f4;
            effectCreateEffectInternal.N = f5;
            effectCreateEffectInternal.O = f6;
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: a */
    public Effect createFlameEffect(float f, float f2, float f3, float f4) {
        return createFlameEffect(f, f2, f3, f4, 0);
    }

    /* JADX INFO: renamed from: a */
    public Effect createFlameEffect(float f, float f2, float f3, float f4, int i) {
        return createFlameEffectInternal(f, f2, f3, f4, i, 0);
    }

    /* JADX INFO: renamed from: b */
    public Effect createFlameEffect2(float f, float f2, float f3, float f4, int i) {
        return createFlameEffectInternal(f, f2, f3, f4, i, 1);
    }

    /* JADX INFO: renamed from: a */
    public Effect createFlameEffectInternal(float f, float f2, float f3, float f4, int i, int i2) {
        setOnlyOnScreen();
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.high);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.g = Effect.j;
            effectCreateEffectInternal.ae = true;
            if (i2 == 1) {
                effectCreateEffectInternal.aq = 3;
                effectCreateEffectInternal.ak = 1.0f;
                effectCreateEffectInternal.aj = 0.4f;
                effectCreateEffectInternal.ag = 4;
            } else {
                effectCreateEffectInternal.aq = 3;
                effectCreateEffectInternal.ak = 0.0f;
                effectCreateEffectInternal.aj = 0.5f;
                effectCreateEffectInternal.ag = 3;
            }
            effectCreateEffectInternal.Y = f4;
            effectCreateEffectInternal.ap = 0;
            effectCreateEffectInternal.V = 20.0f;
            effectCreateEffectInternal.W = effectCreateEffectInternal.V;
            effectCreateEffectInternal.r = false;
            if (i != 0) {
                effectCreateEffectInternal.B = new KoolMultiplyAddColorFilter(i, 0);
            }
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: c */
    public Effect createMuzzleFlash(float f, float f2, float f3, float f4, int i) {
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.verylow);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.aq = 4;
            effectCreateEffectInternal.g = Effect.i;
            effectCreateEffectInternal.ap = Utility.getRandomIntInRange(0, 2);
            effectCreateEffectInternal.Y = f4;
            effectCreateEffectInternal.an = true;
            effectCreateEffectInternal.P = Utility.fastCos(f4) * 0.15f;
            effectCreateEffectInternal.Q = Utility.fastSin(f4) * 0.15f;
            effectCreateEffectInternal.V = 30.0f;
            effectCreateEffectInternal.W = effectCreateEffectInternal.V;
            effectCreateEffectInternal.r = true;
            effectCreateEffectInternal.ar = (short) 1;
            effectCreateEffectInternal.G = 0.8f;
            effectCreateEffectInternal.F = 2.3f;
            if (i != 0) {
                effectCreateEffectInternal.B = new KoolMultiplyAddColorFilter(i, 0);
            }
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: a */
    public static void attachEffectToGameObject(Effect effect, GameObject gameObject) {
        if (effect == null) {
            return;
        }
        effect.b = gameObject;
        effect.I -= gameObject.posX;
        effect.J -= gameObject.posY;
        effect.K -= gameObject.posZ;
    }

    /* JADX INFO: renamed from: a */
    public Effect createLightEffect(GameObject gameObject, int i) {
        return createLightEffect(gameObject, i, 0.5f);
    }

    /* JADX INFO: renamed from: a */
    public Effect createLightEffect(GameObject gameObject, int i, float f) {
        setForceHighQuality();
        Effect effectCreateLightEffectInternal = createLightEffectInternal(gameObject.posX, gameObject.posY, gameObject.posZ, i);
        if (effectCreateLightEffectInternal != null) {
            effectCreateLightEffectInternal.I = 0.0f;
            effectCreateLightEffectInternal.J = 0.0f;
            effectCreateLightEffectInternal.K = 0.0f;
            effectCreateLightEffectInternal.V = 400.0f;
            effectCreateLightEffectInternal.W = effectCreateLightEffectInternal.V;
            effectCreateLightEffectInternal.E = 0.3f;
            effectCreateLightEffectInternal.G = f;
            effectCreateLightEffectInternal.b = gameObject;
        }
        return effectCreateLightEffectInternal;
    }

    /* JADX INFO: renamed from: a */
    public Effect createLightEffect(float f, float f2, float f3, int i) {
        if (this.overrideEffectQuality == null && !this.forceHighQuality) {
            setOnlyOnScreen();
        }
        return createLightEffectInternal(f, f2, f3, i);
    }

    /* JADX INFO: renamed from: b */
    public Effect createLightEffectInternal(float f, float f2, float f3, int i) {
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, true, EffectQuality.low);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.e = false;
            effectCreateEffectInternal.g = Effect.h;
            effectCreateEffectInternal.aq = 2;
            effectCreateEffectInternal.V = 10.0f;
            effectCreateEffectInternal.W = effectCreateEffectInternal.V;
            effectCreateEffectInternal.r = true;
            effectCreateEffectInternal.E = 0.5f;
            effectCreateEffectInternal.ar = (short) 2;
            effectCreateEffectInternal.d = true;
            if (i != 0) {
                effectCreateEffectInternal.x = i;
                effectCreateEffectInternal.B = new KoolMultiplyAddColorFilter(i, 0);
            }
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: b */
    public Effect createSmokeEffect(float f, float f2, float f3, float f4) {
        setOnlyOnScreen();
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.low);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.g = Effect.l;
            effectCreateEffectInternal.aq = 0;
            effectCreateEffectInternal.ap = 13;
            effectCreateEffectInternal.ar = (short) 1;
            effectCreateEffectInternal.r = true;
            effectCreateEffectInternal.E = 0.8f;
            effectCreateEffectInternal.W = 80.0f;
            effectCreateEffectInternal.V = effectCreateEffectInternal.W;
            effectCreateEffectInternal.Y = Utility.randomFloatInRange(-180.0f, 180.0f);
            effectCreateEffectInternal.G = Utility.randomFloatInRange(0.6f, 0.8f);
            effectCreateEffectInternal.F = 1.5f;
            effectCreateEffectInternal.P = (Utility.fastCos(f4) * 0.13f * Utility.randomFloatInRange(1.0f, 1.5f)) + Utility.randomFloatInRange(-0.01f, 0.01f);
            effectCreateEffectInternal.Q = (Utility.fastSin(f4) * 0.13f * Utility.randomFloatInRange(1.0f, 1.5f)) + Utility.randomFloatInRange(-0.01f, 0.01f);
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: a */
    public Effect createRedLaserEffect(float f, float f2, float f3, int i, float f4, float f5) {
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.high);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.g = Effect.l;
            effectCreateEffectInternal.aq = 6;
            effectCreateEffectInternal.V = 120.0f;
            effectCreateEffectInternal.W = effectCreateEffectInternal.V;
            effectCreateEffectInternal.r = true;
            effectCreateEffectInternal.G = 0.2f;
            effectCreateEffectInternal.F = 0.9f;
            effectCreateEffectInternal.ar = (short) 1;
            effectCreateEffectInternal.E = 0.5f;
            effectCreateEffectInternal.P = f4;
            effectCreateEffectInternal.Q = f5;
            if (i != 0) {
                i = KoolArgbColor.a(255, 0, 0, 200);
            }
            if (i != 0) {
                effectCreateEffectInternal.B = new KoolMultiplyAddColorFilter(i, 0);
            }
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: a */
    public void createDirectedExplosion(float f, float f2, float f3, int i, float f4, float f5, float f6) {
        createRedLaserEffect(f, f2, 0.0f, 0, 0.0f, 0.0f);
        for (int i2 = -180; i2 < 180; i2 += 45) {
            float f7 = f6 + i2;
            Effect effectCreateSmokeEffect = createSmokeEffect(f + (Utility.fastCos(f7) * (-5.0f)), f2 + (Utility.fastSin(f7) * (-5.0f)), 0.0f, f7);
            if (effectCreateSmokeEffect != null) {
                effectCreateSmokeEffect.ar = (short) 2;
                effectCreateSmokeEffect.s = true;
                effectCreateSmokeEffect.t = 7.0f;
            }
        }
    }

    /* JADX INFO: renamed from: c */
    public Effect createSmallExplosion(float f, float f2, float f3, int i) {
        Effect effectCreateSmallExplosionInternal = createSmallExplosionInternal(f, f2, f3, i);
        if (effectCreateSmallExplosionInternal != null) {
            effectCreateSmallExplosionInternal.aq = 11;
        }
        return effectCreateSmallExplosionInternal;
    }

    /* JADX INFO: renamed from: d */
    public Effect createSmallExplosionInternal(float f, float f2, float f3, int i) {
        setOnlyOnScreen();
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.high);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.aq = 6;
            effectCreateEffectInternal.V = 30.0f;
            effectCreateEffectInternal.W = effectCreateEffectInternal.V;
            effectCreateEffectInternal.r = true;
            effectCreateEffectInternal.G = 0.2f;
            effectCreateEffectInternal.F = 1.3f;
            effectCreateEffectInternal.ar = (short) 1;
            if (i != 0) {
                effectCreateEffectInternal.B = new KoolMultiplyAddColorFilter(i, 0);
            }
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: d */
    public Effect createBloodEffect(float f, float f2, float f3) {
        Effect effectCreateBloodEffectInternal = createBloodEffectInternal(f, f2, f3, 0.3f, 0.7f);
        if (effectCreateBloodEffectInternal != null) {
            effectCreateBloodEffectInternal.aq = 14;
            effectCreateBloodEffectInternal.ap = Utility.getRandomIntInRange(0, 5);
            effectCreateBloodEffectInternal.w = 0.5f;
        }
        return effectCreateBloodEffectInternal;
    }

    /* JADX INFO: renamed from: e */
    public Effect createBloodEffect2(float f, float f2, float f3) {
        Effect effectCreateBloodEffectInternal = createBloodEffectInternal(f, f2, f3, 1.0f, 1.0f);
        if (effectCreateBloodEffectInternal != null) {
        }
        return effectCreateBloodEffectInternal;
    }

    /* JADX INFO: renamed from: b */
    public Effect createBloodEffectInternal(float f, float f2, float f3, float f4, float f5) {
        setForceHighQuality();
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.high);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.g = Effect.m;
            effectCreateEffectInternal.aq = 12;
            effectCreateEffectInternal.ap = Utility.getRandomIntInRange(0, 7);
            effectCreateEffectInternal.V = Utility.randomFloatInRange(400.0f, 800.0f);
            effectCreateEffectInternal.W = effectCreateEffectInternal.V - 150.0f;
            effectCreateEffectInternal.r = true;
            float fRandomFloatInRange = Utility.randomFloatInRange(0.6f, 1.0f);
            effectCreateEffectInternal.G = fRandomFloatInRange;
            effectCreateEffectInternal.F = fRandomFloatInRange;
            effectCreateEffectInternal.ar = (short) 2;
            effectCreateEffectInternal.v = true;
            effectCreateEffectInternal.as = true;
            float fRandomFloatInRange2 = Utility.randomFloatInRange(-180.0f, 180.0f);
            float fRandomFloatInRange3 = Utility.randomFloatInRange(0.4f, 1.2f) * f4;
            effectCreateEffectInternal.P = Utility.fastCos(fRandomFloatInRange2) * fRandomFloatInRange3;
            effectCreateEffectInternal.Q = Utility.fastSin(fRandomFloatInRange2) * fRandomFloatInRange3;
            effectCreateEffectInternal.R = Utility.randomFloatInRange(0.6f, 2.7f) * f5;
            effectCreateEffectInternal.Y = Utility.randomFloatInRange(-180.0f, 180.0f);
            effectCreateEffectInternal.K += 1.0f;
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: f */
    public Effect createShockwaveEffect(float f, float f2, float f3) {
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.low);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.aq = 8;
            effectCreateEffectInternal.V = 480.0f;
            effectCreateEffectInternal.W = effectCreateEffectInternal.V;
            effectCreateEffectInternal.r = false;
            effectCreateEffectInternal.ar = (short) 1;
            effectCreateEffectInternal.ae = true;
            effectCreateEffectInternal.ak = 0.0f;
            effectCreateEffectInternal.G = 0.5f;
            effectCreateEffectInternal.G = 1.0f;
            int randomIntInRange = Utility.getRandomIntInRange(0, 100);
            if (randomIntInRange > 80) {
                effectCreateEffectInternal.aj = Utility.randomFloatInRange(0.1f, 0.15f);
                effectCreateEffectInternal.ag = 15;
            } else if (randomIntInRange > 60) {
                effectCreateEffectInternal.aj = Utility.randomFloatInRange(0.06f, 0.16f);
                effectCreateEffectInternal.ah = true;
                effectCreateEffectInternal.ag = 6;
                effectCreateEffectInternal.r = true;
            } else {
                effectCreateEffectInternal.aj = Utility.randomFloatInRange(0.06f, 0.16f);
                effectCreateEffectInternal.ah = true;
                effectCreateEffectInternal.ag = 3;
                effectCreateEffectInternal.r = true;
            }
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: b */
    public void setOverrideEffectQuality(EffectQuality effectQuality) {
        this.overrideEffectQuality = effectQuality;
    }

    /* JADX INFO: renamed from: a */
    public void setOnlyOnScreen() {
        this.onlyOnScreen = true;
    }

    /* JADX INFO: renamed from: b */
    public void setForceHighQuality() {
        this.forceHighQuality = true;
    }

    /* JADX INFO: renamed from: a */
    public Effect createEffect(float f, float f2, float f3, EffectType effectType, boolean z, EffectQuality effectQuality) {
        Effect effectCreateEffectInternal = createEffectInternal(f, f2, f3, effectType, z, effectQuality);
        if (effectCreateEffectInternal != null) {
            effectCreateEffectInternal.p = true;
        }
        return effectCreateEffectInternal;
    }

    /* JADX INFO: renamed from: b */
    public Effect createEffectInternal(float f, float f2, float f3, EffectType effectType, boolean z, EffectQuality effectQuality) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.overrideEffectQuality != null) {
            effectQuality = this.overrideEffectQuality;
            this.overrideEffectQuality = null;
        }
        boolean z2 = this.forceHighQuality;
        this.forceHighQuality = false;
        if (this.onlyOnScreen) {
            this.onlyOnScreen = false;
            if (!gameEngine.extendedVisibleWorldRect.b(f, f2)) {
                return null;
            }
        }
        if (!z && gameEngine.tileMap != null && !gameEngine.tileMap.isWorldPointVisibleForTeam(f, f2, gameEngine.playerTeam)) {
            return null;
        }
        if (gameEngine.bufferedVisibleWorldRectF.b(f, f2)) {
            if (effectQuality == EffectQuality.verylow) {
                effectQuality = EffectQuality.low;
            } else if (effectQuality == EffectQuality.low) {
                effectQuality = EffectQuality.high;
            } else if (effectQuality == EffectQuality.high) {
                effectQuality = EffectQuality.veryhigh;
            }
        } else if (z2 || gameEngine.extendedVisibleWorldRect.b(f, f2)) {
        }
        Effect newEffect = getNewEffect(effectQuality);
        if (newEffect == null) {
            return null;
        }
        newEffect.free();
        newEffect.q = effectQuality;
        newEffect.aq = 0;
        newEffect.an = true;
        newEffect.I = f;
        newEffect.J = f2;
        newEffect.K = f3;
        newEffect.E = 1.0f;
        if (effectType == EffectType.hitGround || effectType == EffectType.playerLand || effectType == EffectType.playerJump) {
            newEffect.ap = 7;
            newEffect.V = 12.0f;
            newEffect.r = true;
            newEffect.Q = -0.3f;
            newEffect.E = 0.7f;
            if (effectType == EffectType.playerJump) {
                newEffect.ap = 3;
                newEffect.Q = -0.7f;
                newEffect.V = 24.0f;
                newEffect.E = 0.7f;
            }
            if (effectType == EffectType.playerLand) {
                newEffect.ap = 4;
                newEffect.V = 15.0f;
                newEffect.E = 0.4f;
            }
        }
        if (effectType == EffectType.teleport) {
            newEffect.ap = 1;
            newEffect.V = 25.0f;
            newEffect.r = true;
        }
        if (effectType == EffectType.gemCollect) {
            newEffect.ap = 5;
            newEffect.V = 42.0f;
            newEffect.r = true;
            newEffect.Q = 0.1f;
            newEffect.E = 2.0f;
        }
        if (effectType == EffectType.keyDoorOpen) {
            newEffect.ap = 6;
            newEffect.V = 39.0f;
            newEffect.r = true;
            newEffect.Q = 0.1f;
            newEffect.E = 2.0f;
        }
        if (effectType == EffectType.blood) {
            newEffect.ap = 14;
            newEffect.V = 39.0f;
            newEffect.r = true;
            newEffect.Q = 0.1f;
            newEffect.E = 0.7f;
        }
        newEffect.W = newEffect.V;
        return newEffect;
    }

    /* JADX INFO: renamed from: a */
    public void loadContent() {
        int i;
        GameEngine gameEngine = GameEngine.getInstance();
        this.linePaint.a(130, 200, 0, 0);
        this.linePaint.a(true);
        this.linePaint.a(2.0f);
        this.linePaint.a(KoolPaint.Cap.ROUND);
        if (GameEngine.isPCOrIOSVersion) {
            this.linePaint.a(3.0f);
        }
        effectTemplates = new SpriteSheet[20];
        SpriteSheet spriteSheet = new SpriteSheet();
        spriteSheet.b = 25;
        spriteSheet.c = 25;
        spriteSheet.d = 1;
        spriteSheet.e = 1;
        spriteSheet.f = 26;
        spriteSheet.g = 26;
        spriteSheet.i = gameEngine.renderGraphicsEngine.a(R.drawable.effects, true);
        spriteSheet.a = "effects";
        spriteSheet.createOutline();
        effectTemplates[0] = spriteSheet;
        SpriteSheet spriteSheet2 = new SpriteSheet();
        spriteSheet2.b = 39;
        spriteSheet2.c = 40;
        spriteSheet2.d = 1;
        spriteSheet2.e = 1;
        spriteSheet2.f = 40;
        spriteSheet2.g = 41;
        spriteSheet2.i = gameEngine.renderGraphicsEngine.a(R.drawable.explode_big, true);
        spriteSheet2.a = "explode_big";
        effectTemplates[1] = spriteSheet2;
        SpriteSheet spriteSheet3 = new SpriteSheet();
        spriteSheet3.k = true;
        spriteSheet3.i = gameEngine.renderGraphicsEngine.a(R.drawable.light_50, true);
        spriteSheet3.a = "light_50";
        effectTemplates[2] = spriteSheet3;
        SpriteSheet spriteSheet4 = new SpriteSheet();
        spriteSheet4.b = 20;
        spriteSheet4.c = 25;
        spriteSheet4.d = 0;
        spriteSheet4.e = 0;
        spriteSheet4.f = 20;
        spriteSheet4.g = 25;
        spriteSheet4.i = gameEngine.renderGraphicsEngine.a(R.drawable.flame, true);
        spriteSheet4.a = "flame";
        effectTemplates[3] = spriteSheet4;
        SpriteSheet spriteSheet5 = new SpriteSheet();
        spriteSheet5.b = 20;
        spriteSheet5.c = 25;
        spriteSheet5.d = 0;
        spriteSheet5.e = 0;
        spriteSheet5.f = spriteSheet5.b;
        spriteSheet5.g = spriteSheet5.c;
        spriteSheet5.i = gameEngine.renderGraphicsEngine.a(R.drawable.dust, true);
        spriteSheet5.a = "dust";
        effectTemplates[4] = spriteSheet5;
        SpriteSheet spriteSheet6 = new SpriteSheet();
        spriteSheet6.b = 50;
        spriteSheet6.c = 40;
        spriteSheet6.d = 0;
        spriteSheet6.e = 0;
        spriteSheet6.f = spriteSheet6.b;
        spriteSheet6.g = spriteSheet6.c;
        spriteSheet6.i = gameEngine.renderGraphicsEngine.a(R.drawable.smoke_black, true);
        spriteSheet6.a = "smoke_black";
        spriteSheet6.createOutline();
        effectTemplates[5] = spriteSheet6;
        SpriteSheet spriteSheet7 = new SpriteSheet();
        spriteSheet7.b = 50;
        spriteSheet7.c = 50;
        spriteSheet7.d = 0;
        spriteSheet7.e = 0;
        spriteSheet7.f = spriteSheet7.b;
        spriteSheet7.g = spriteSheet7.c;
        spriteSheet7.i = gameEngine.renderGraphicsEngine.a(R.drawable.shockwave, true);
        spriteSheet7.a = "shockwave";
        effectTemplates[6] = spriteSheet7;
        SpriteSheet spriteSheet8 = new SpriteSheet();
        spriteSheet8.b = 20;
        spriteSheet8.c = 20;
        spriteSheet8.d = 0;
        spriteSheet8.e = 0;
        spriteSheet8.f = spriteSheet8.b;
        spriteSheet8.g = spriteSheet8.c;
        spriteSheet8.i = gameEngine.renderGraphicsEngine.a(R.drawable.fire, true);
        spriteSheet8.a = "fire";
        effectTemplates[7] = spriteSheet8;
        SpriteSheet spriteSheet9 = new SpriteSheet();
        spriteSheet9.b = 20;
        spriteSheet9.c = 30;
        spriteSheet9.f = spriteSheet9.b + 2;
        spriteSheet9.g = spriteSheet9.c;
        spriteSheet9.i = gameEngine.renderGraphicsEngine.a(R.drawable.lava_bubble, true);
        spriteSheet9.a = "lava_bubble";
        effectTemplates[8] = spriteSheet9;
        SpriteSheet spriteSheet10 = new SpriteSheet();
        spriteSheet10.b = 28;
        spriteSheet10.c = 28;
        spriteSheet10.d = 0;
        spriteSheet10.e = 0;
        spriteSheet10.f = spriteSheet10.b + 1;
        spriteSheet10.g = spriteSheet10.c + 1;
        spriteSheet10.i = gameEngine.renderGraphicsEngine.a(R.drawable.effects2, true);
        spriteSheet10.a = "effects2";
        effectTemplates[9] = spriteSheet10;
        SpriteSheet spriteSheet11 = new SpriteSheet();
        spriteSheet11.b = 20;
        spriteSheet11.c = 25;
        spriteSheet11.d = 0;
        spriteSheet11.e = 0;
        spriteSheet11.f = 20;
        spriteSheet11.g = 25;
        spriteSheet11.i = gameEngine.renderGraphicsEngine.a(R.drawable.plasma_shot, true);
        spriteSheet11.a = "plasma_shot";
        effectTemplates[10] = spriteSheet11;
        SpriteSheet spriteSheet12 = new SpriteSheet();
        spriteSheet12.b = 104;
        spriteSheet12.c = 104;
        spriteSheet12.d = 0;
        spriteSheet12.e = 0;
        spriteSheet12.f = spriteSheet12.b;
        spriteSheet12.g = spriteSheet12.c;
        spriteSheet12.i = gameEngine.renderGraphicsEngine.a(R.drawable.shockwave_large, true);
        spriteSheet12.a = "shockwave_large";
        effectTemplates[11] = spriteSheet12;
        SpriteSheet spriteSheet13 = new SpriteSheet();
        spriteSheet13.b = 20;
        spriteSheet13.c = 20;
        spriteSheet13.d = 0;
        spriteSheet13.e = 0;
        spriteSheet13.f = spriteSheet13.b;
        spriteSheet13.g = spriteSheet13.c;
        spriteSheet13.i = gameEngine.renderGraphicsEngine.a(R.drawable.explode_bits, true);
        spriteSheet13.a = "explode_bits";
        spriteSheet13.createOutline();
        effectTemplates[12] = spriteSheet13;
        SpriteSheet spriteSheet14 = new SpriteSheet();
        spriteSheet14.b = 39;
        spriteSheet14.c = 40;
        spriteSheet14.d = 1;
        spriteSheet14.e = 1;
        spriteSheet14.f = 40;
        spriteSheet14.g = 41;
        spriteSheet14.i = gameEngine.renderGraphicsEngine.a(R.drawable.explode_big2, true);
        spriteSheet14.a = "explode_big2";
        effectTemplates[13] = spriteSheet14;
        SpriteSheet spriteSheet15 = new SpriteSheet();
        spriteSheet15.b = 20;
        spriteSheet15.c = 20;
        spriteSheet15.d = 0;
        spriteSheet15.e = 0;
        spriteSheet15.f = spriteSheet15.b;
        spriteSheet15.g = spriteSheet15.c;
        spriteSheet15.i = gameEngine.renderGraphicsEngine.a(R.drawable.explode_bits_bug, true);
        spriteSheet15.a = "explode_bits_bug";
        spriteSheet15.createOutline();
        effectTemplates[14] = spriteSheet15;
        SpriteSheet spriteSheet16 = new SpriteSheet();
        spriteSheet16.b = 20;
        spriteSheet16.c = 20;
        spriteSheet16.d = 0;
        spriteSheet16.e = 0;
        spriteSheet16.f = spriteSheet16.b;
        spriteSheet16.g = spriteSheet16.c;
        spriteSheet16.i = gameEngine.renderGraphicsEngine.a(R.drawable.projectiles, true);
        spriteSheet16.a = "projectiles";
        spriteSheet16.createOutline();
        effectTemplates[15] = spriteSheet16;
        SpriteSheet spriteSheet17 = new SpriteSheet();
        spriteSheet17.b = 20;
        spriteSheet17.c = 20;
        spriteSheet17.d = 0;
        spriteSheet17.e = 0;
        spriteSheet17.f = spriteSheet17.b;
        spriteSheet17.g = spriteSheet17.c;
        spriteSheet17.i = gameEngine.renderGraphicsEngine.a(R.drawable.projectiles2, true);
        spriteSheet17.a = "projectiles2";
        spriteSheet17.createOutline();
        effectTemplates[16] = spriteSheet17;
        SpriteSheet spriteSheet18 = new SpriteSheet();
        spriteSheet18.b = 30;
        spriteSheet18.c = 30;
        spriteSheet18.d = 0;
        spriteSheet18.e = 0;
        spriteSheet18.f = spriteSheet18.b + 1;
        spriteSheet18.g = spriteSheet18.c + 1;
        spriteSheet18.i = gameEngine.renderGraphicsEngine.a(R.drawable.effects3, true);
        spriteSheet18.a = "effects3";
        effectTemplates[17] = spriteSheet18;
        SpriteSheet spriteSheet19 = new SpriteSheet();
        spriteSheet19.b = 50;
        spriteSheet19.c = 40;
        spriteSheet19.d = 0;
        spriteSheet19.e = 0;
        spriteSheet19.f = spriteSheet19.b;
        spriteSheet19.g = spriteSheet19.c;
        spriteSheet19.i = gameEngine.renderGraphicsEngine.a(R.drawable.smoke_white, true);
        spriteSheet19.a = "smoke_white";
        spriteSheet19.createOutline();
        effectTemplates[18] = spriteSheet19;
        SpriteSheet spriteSheet20 = new SpriteSheet();
        spriteSheet20.b = 56;
        spriteSheet20.c = 56;
        spriteSheet20.d = 0;
        spriteSheet20.e = 0;
        spriteSheet20.f = spriteSheet20.b;
        spriteSheet20.g = spriteSheet20.c;
        spriteSheet20.i = gameEngine.renderGraphicsEngine.a(R.drawable.shockwave2, true);
        spriteSheet20.a = "shockwave2";
        spriteSheet20.createOutline();
        effectTemplates[19] = spriteSheet20;
        if (GameEngine.isPC()) {
            i = 500;
            this.maxParticlesVeryLow = 90;
            this.maxParticlesLow = 210;
        } else {
            i = 350;
            this.maxParticlesVeryLow = 90;
            this.maxParticlesLow = 170;
        }
        effects = new Effect[i];
        this.maxEffectsVeryLow = i - 60;
        this.maxEffectsLow = i - 30;
        this.maxEffectsHigh = i - 20;
        this.maxEffectsVeryHigh = i - 10;
        for (int i2 = 0; i2 < effects.length; i2++) {
            effects[i2] = new Effect(this);
        }
    }

    /* JADX INFO: renamed from: a */
    public int a_string_method(String str) {
        for (int i = 0; i < effectTemplates.length; i++) {
            if (effectTemplates[i] != null) {
                if (effectTemplates[i].a != null && effectTemplates[i].a.equalsIgnoreCase(str)) {
                    return i;
                }
                if ((VariableScope.nullOrMissingString + i).equals(str)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /* JADX INFO: renamed from: a */
    public void update(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        Effect[] effectArr = effects;
        for (int i = 0; i < nextFreeEffect; i++) {
            Effect effect = effectArr[i];
            if (effect.o && !effect.p) {
                effect.draw(f);
            }
        }
        if (useStrictCounting) {
            while (nextFreeEffect > 0 && !effectArr[nextFreeEffect - 1].o) {
                nextFreeEffect--;
            }
        }
        this.lastUpdate += f;
        if (this.lastUpdate > 10.0f) {
            this.lastUpdate = 0.0f;
            gameEngine.tileMap.setCursorTileIndexFromWorldPoint(gameEngine.viewpointXInt + Utility.getRandomIntInRange(0, (int) gameEngine.visibleWorldWidth), gameEngine.viewpointYInt + Utility.getRandomIntInRange(0, (int) gameEngine.visibleWorldHeight));
            int i2 = gameEngine.tileMap.cursorTileX;
            int i3 = gameEngine.tileMap.cursorTileY;
            MapTile tileAt = gameEngine.tileMap.getTileAt(i2, i3);
            if (tileAt != null && tileAt.isLava && !tileAt.isCliff) {
                gameEngine.tileMap.setCursorTileIndexFromTileIndex(i2, i3);
                createShockwaveEffect(gameEngine.tileMap.cursorTileX + 10, (gameEngine.tileMap.cursorTileY - 10) + 10, 0.0f);
            }
        }
    }

    /* JADX INFO: renamed from: b */
    public int getEffectCount(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        int i = 0;
        for (int i2 = 0; i2 < this.activeEffectLayerFlags.length; i2++) {
            this.activeEffectLayerFlags[i2] = false;
        }
        for (int i3 = 0; i3 < nextFreeEffect; i3++) {
            Effect effect = effects[i3];
            if (effect.o) {
                if (!this.activeEffectLayerFlags[effect.ar]) {
                    this.activeEffectLayerFlags[effect.ar] = true;
                }
                if (effect.p) {
                    effect.draw(f);
                }
                if (effect.as && effect.update(gameEngine, true)) {
                    i++;
                }
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: a */
    public int drawEffect(float f, int i) {
        if (!this.activeEffectLayerFlags[i]) {
            return 0;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        int i2 = 0;
        Effect[] effectArr = effects;
        for (int i3 = 0; i3 < nextFreeEffect; i3++) {
            Effect effect = effectArr[i3];
            if (effect.o && effect.ar == i && effect.update(gameEngine, false)) {
                i2++;
            }
        }
        return i2;
    }

    /* JADX INFO: renamed from: a */
    public void setBitmapQuality(boolean z) {
        if (z) {
            return;
        }
        for (int i = 0; i < effects.length; i++) {
            Effect effect = effects[i];
            if (effect.o) {
                effect.o = false;
                this.activeEffectsCount--;
            }
        }
        if (this.activeEffectsCount != 0) {
            GameEngine.logErrorColored("EffectEngine::removeAll: effectListActiveSize == " + this.activeEffectsCount);
        }
        nextFreeEffect = 0;
    }
}
