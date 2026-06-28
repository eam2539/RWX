package com.corrodinggames.rts.game.units.land;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolArgbColor;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/l.class */
public class MammothTank extends LandUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture[] c = new Texture[10];
    static Texture d = null;
    public static Texture e = null;
    int f;
    float g;
    Rect h;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.mammothTank;
    }

    public static void f() {
        GameEngine gameEngine = GameEngine.getInstance();
        Texture textureA = gameEngine.renderGraphicsEngine.a(R.drawable.mammoth_tank);
        c = PlayerTeam.getTeamColorTextures(textureA);
        a = gameEngine.renderGraphicsEngine.a(R.drawable.mammoth_tank_dead);
        b = gameEngine.renderGraphicsEngine.a(R.drawable.mammoth_tank_turret);
        e = gameEngine.renderGraphicsEngine.a(R.drawable.lighting_charge);
        d = attackUnit(textureA, textureA.m() / 2, textureA.l());
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return a;
        }
        return c[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return d;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return b;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && this.posZ > -2.0f && !this.isDead;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: G */
    public float getShadowOffsetX() {
        return 3.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: H */
    public float getShadowOffsetY() {
        return 3.0f;
    }

    public MammothTank(boolean z) {
        super(z);
        this.h = new Rect();
        a(c[7], 2);
        this.radius = 21.0f;
        this.displayRadius = this.radius + 1.0f;
        this.maxHealth = 2900.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = c[7];
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance();
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        a(UnitSize.largeUnit);
        return true;
    }

    @Override
    // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (this.isMoving) {
            this.g += f;
            if (this.g > 3.0f) {
                this.g = 0.0f;
                this.f = 1 - this.f;
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 14000.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        PointF pointFE = E(i);
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y);
        projectileA.ar = KoolArgbColor.a(255, 247, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_EISU, 129);
        projectileA.U = 260.0f;
        projectileA.l = baseUnit;
        projectileA.h = 20.0f;
        projectileA.t = 4.0f;
        projectileA.x = 2.0f;
        projectileA.aQ = true;
        projectileA.A = true;
        projectileA.M = true;
        projectileA.ai = 0.5f;
        projectileA.ak = 1.0f;
        projectileA.al = 0.0f;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1118482);
        gameEngine.soundEngine.playSound(SoundEngine.lightingBurstSound, 0.2f, this.posX, this.posY);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 210.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 140.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float bc() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float w(int i) {
        return 0.08f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 2.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.04f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.08f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        if (this.isDead && !z) {
            return super.a_(z);
        }
        return super.a(z, this.f);
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        if (!super.c(f)) {
            return false;
        }
        GameViewUtils.a((OrderableUnit) this);
        GameViewUtils.a(this, e, this.movementLevels[0].speed / e(0), 0);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: af */
    public boolean canAttackFlyingUnits() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 22.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        return 60.0f;
    }
}
