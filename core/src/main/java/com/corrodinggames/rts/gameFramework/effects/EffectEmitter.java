package com.corrodinggames.rts.gameFramework.effects;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.d.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/d/f.class */
public class EffectEmitter extends GameObject {
    public float a;
    float c;
    float d;
    float e;
    float f;
    float g;
    float h;
    Effect i;
    float m;
    float n;
    float o;
    float p;
    float q;
    float r;
    EffectQuality s;
    public float t;
    static Effect v;
    static Effect w;
    private final EffectManager x;
    public boolean b = true;
    public int j = 0;
    public int k = 0;
    public int l = -1;
    public boolean u = false;

    public static void b() {
        EffectManager effectManager = GameEngine.getInstance().effectManager;
        Effect effect = new Effect(effectManager);
        a(effect, false);
        effect.aq = 18;
        effect.t = 15.0f;
        v = effect;
        Effect effect2 = new Effect(effectManager);
        b(effect2, false);
        w = effect2;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeFloat(this.posX);
        gameOutputStream.writeFloat(this.posY);
        gameOutputStream.writeFloat(this.a);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.posX = gameInputStream.readFloat();
        this.posY = gameInputStream.readFloat();
        this.a = gameInputStream.readFloat();
        this.b = false;
        super.a(gameInputStream);
    }

    public EffectEmitter(EffectManager effectManager) {
        this.x = effectManager;
    }

    public static void a(Effect effect, boolean z) {
        effect.free();
        effect.aq = 5;
        if (z) {
            effect.ap = Utility.getRandomIntInRange(0, 1);
        } else {
            effect.ap = 0;
        }
        effect.Y = 0.0f;
        effect.an = true;
        effect.P = 0.1f;
        effect.R = 0.5f;
        effect.u = true;
        effect.V = 300.0f;
        effect.W = effect.V;
        effect.r = true;
        effect.s = true;
        effect.t = 40.0f;
        effect.as = false;
        effect.ar = (short) 2;
        effect.G = 0.4f;
        effect.F = 1.5f;
        effect.g = Effect.k;
    }

    public static void b(Effect effect, boolean z) {
        effect.free();
        effect.aq = 7;
        if (z) {
            effect.ap = Utility.getRandomIntInRange(0, 3);
        } else {
            effect.ap = 0;
        }
        effect.Y = 0.0f;
        effect.an = true;
        effect.P = 0.0f;
        effect.R = 0.2f;
        effect.u = true;
        effect.V = 50.0f;
        effect.W = effect.V;
        effect.r = true;
        effect.s = true;
        effect.t = 10.0f;
        effect.as = false;
        effect.ar = (short) 2;
        effect.g = Effect.n;
    }

    public static EffectEmitter a(float f, float f2) {
        EffectEmitter effectEmitterA = a(f, f2, v);
        effectEmitterA.a = 280.0f;
        effectEmitterA.f = 10.0f;
        effectEmitterA.c = 10.0f;
        effectEmitterA.m = 0.03f;
        effectEmitterA.n = 0.03f;
        effectEmitterA.p = 6.0f;
        effectEmitterA.q = 6.0f;
        effectEmitterA.s = EffectQuality.verylow;
        effectEmitterA.r = 180.0f;
        effectEmitterA.j = -16777216;
        return effectEmitterA;
    }

    public static EffectEmitter b(float f, float f2) {
        EffectEmitter effectEmitterA = a(f, f2, w);
        effectEmitterA.a = 330.0f;
        effectEmitterA.f = 10.0f;
        effectEmitterA.c = 10.0f;
        effectEmitterA.m = 0.1f;
        effectEmitterA.n = 0.03f;
        effectEmitterA.p = 4.0f;
        effectEmitterA.q = 4.0f;
        effectEmitterA.s = EffectQuality.verylow;
        return effectEmitterA;
    }

    public static EffectEmitter a(float f, float f2, Effect effect) {
        EffectManager effectManager = GameEngine.getInstance().effectManager;
        EffectEmitter effectEmitter = new EffectEmitter(effectManager);
        effectEmitter.posX = f;
        effectEmitter.posY = f2;
        effectEmitter.a = 100.0f;
        effectEmitter.f = 10.0f;
        effectEmitter.i = effect;
        if (effect == null) {
            effectEmitter.i = new Effect(effectManager);
            GameEngine.logColored("Error: Emitter create srcEffect==null");
        }
        return effectEmitter;
    }

    public boolean c() {
        return GameEngine.getInstance().extendedVisibleWorldRect.b(this.posX, this.posY);
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        Effect effectCreateEffectInternal;
        this.t = Utility.moveTowardsZero(this.t, f);
        if (this.t > 0.0f) {
            return;
        }
        if (this.b) {
            this.c += f;
            if (this.c > this.f) {
                this.d += f;
                if (this.d > this.g) {
                    this.d = 0.0f;
                    this.e += 1.0f;
                    if (this.e > this.h) {
                        this.c = 0.0f;
                        this.e = 0.0f;
                    }
                    if ((this.u || c()) && (effectCreateEffectInternal = this.x.createEffectInternal(this.posX, this.posY, 0.0f, EffectType.custom, false, this.s)) != null) {
                        effectCreateEffectInternal.recycle(this.i);
                        effectCreateEffectInternal.P += Utility.randomFloatInRange(-this.m, this.m);
                        effectCreateEffectInternal.Q += Utility.randomFloatInRange(-this.n, this.n);
                        effectCreateEffectInternal.R += Utility.randomFloatInRange(-this.o, this.o);
                        effectCreateEffectInternal.Y = Utility.randomFloatInRange(-this.r, this.r);
                        effectCreateEffectInternal.I = this.posX;
                        effectCreateEffectInternal.J = this.posY;
                        effectCreateEffectInternal.I += Utility.randomFloatInRange(-this.p, this.p);
                        effectCreateEffectInternal.J += Utility.randomFloatInRange(-this.q, this.q);
                        if (this.j != 0) {
                            effectCreateEffectInternal.x = this.j;
                        }
                        if (this.l >= 0) {
                            effectCreateEffectInternal.y = this.k;
                            effectCreateEffectInternal.z = this.l;
                        }
                    }
                }
            }
        }
        this.a -= f;
        if (this.a < 0.0f) {
            remove();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean a(GameEngine gameEngine) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void a(float f, boolean z) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public void d(float f) {
    }

    @Override // com.corrodinggames.rts.gameFramework.GameObject
    public boolean f(float f) {
        return false;
    }
}
