package com.corrodinggames.rts.game.units.air;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.b.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/b/f.class */
public class Helicopter extends AirUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture d = null;
    static Texture e = null;
    static Texture[] f = new Texture[10];
    boolean g;
    float o;
    float p;
    float q;
    Rect r;
    Rect s;

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeFloat(this.p);
        gameOutputStream.writeFloat(this.o);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        if (gameInputStream.getProtocolVersion() >= 9) {
            this.p = gameInputStream.readFloat();
            this.o = gameInputStream.readFloat();
            if (gameInputStream.getProtocolVersion() == 8) {
                this.g = gameInputStream.readBoolean();
            }
        } else {
            this.o = 0.5f;
        }
        super.a(gameInputStream);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.helicopter;
    }

    public static void f() {
        GameEngine gameEngine = GameEngine.getInstance();
        b = gameEngine.graphicsEngine2.a(R.drawable.helicopter);
        c = gameEngine.graphicsEngine2.a(R.drawable.helicopter_blades);
        d = gameEngine.graphicsEngine2.a(R.drawable.helicopter_shadow);
        e = gameEngine.graphicsEngine2.a(R.drawable.helicopter_shadow_blades);
        a = gameEngine.graphicsEngine2.a(R.drawable.helicopter_dead);
        f = PlayerTeam.getUnitCountByType(b);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return a;
        }
        return f[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return d;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    public Helicopter(boolean z) {
        super(z);
        this.g = false;
        this.p = 0.0f;
        this.r = new Rect();
        this.s = new Rect();
        T(26);
        U(46);
        this.radius = 13.0f;
        this.displayRadius = this.radius + 2.0f;
        this.maxHealth = 150.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = b;
        this.shadowTexture = d;
        this.posZ = 0.0f;
        this.o = 0.14f;
        this.q = 0.0f;
        S(5);
    }

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance().effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void n() {
        super.n();
        this.posZ = 20.0f;
        this.o = 0.5f;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.OrderableUnit
    public boolean I() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean i() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f2) {
        super.update(f2);
        if (this.isDestroyed) {
            return;
        }
        this.o = Utility.distanceSq(this.o, 0.5f, 0.003f * f2);
        this.q += 70.0f * this.o * f2;
        if (this.q >= 360.0f) {
            this.q -= 360.0f;
            this.q += Utility.readStreamToString(this, 0, 4);
        }
        if (this.o > 0.4f) {
            this.p += 2.0f * f2;
            if (this.p > 360.0f) {
                this.p -= 360.0f;
            }
            this.posZ = Utility.distanceSq(this.posZ, 20.0f + (Utility.fastSin(this.p) * 1.5f), 0.1f * f2);
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        PointF pointFE = E(i);
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y, this.posZ, i);
        PointF pointFK = getShadowTexture(i);
        projectileA.K = pointFK.x;
        projectileA.L = pointFK.y;
        projectileA.U = 17.0f;
        projectileA.l = baseUnit;
        projectileA.h = 30.0f;
        projectileA.t = 8.0f;
        projectileA.S = false;
        projectileA.ar = Color.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 0);
        projectileA.A = true;
        projectileA.aR = false;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.soundEngine.playSoundAt(SoundEngine.gunFireSound, 0.2f, 1.0f + Utility.randomFloatInRange(-0.08f, 0.08f), pointFE.x, pointFE.y);
        gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
        gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1118720);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 130.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 60.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        if (this.posZ < 15.0f) {
            return 0.0f;
        }
        return 2.2f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float bc() {
        return 0.1f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 6.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.4f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bi */
    public boolean isSlidingMovement() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bj */
    public boolean isIgnoreMoveOrders() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 16.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        return super.a_(z);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f2) {
        if (!super.c(f2)) {
            return false;
        }
        if (!this.isDestroyed) {
            Paint paintAN = getRenderPaint();
            GameEngine gameEngine = GameEngine.getInstance();
            this.s.a(0, 0, c.m(), c.l());
            float f3 = this.q;
            if (this.isUnitStunned) {
            }
            gameEngine.graphicsEngine2.a(c, this.s, this.posX - GameEngine.getInstance().viewpointXSnapped, (this.posY - GameEngine.getInstance().viewpointYSnapped) - this.posZ, f3, paintAN);
            return true;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.07f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.1f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 7.0f;
    }
}
