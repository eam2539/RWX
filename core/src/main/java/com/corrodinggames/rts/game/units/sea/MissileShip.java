package com.corrodinggames.rts.game.units.sea;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolArgbColor;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.h.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/h/d.class */
public class MissileShip extends WaterUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture[] d = new Texture[10];
    static PointF e = new PointF();
    Rect f;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.missileShip;
    }

    public static void f() {
        GameEngine gameEngine = GameEngine.getInstance();
        b = gameEngine.renderGraphicsEngine.a(R.drawable.scout_ship);
        a = gameEngine.renderGraphicsEngine.a(R.drawable.scout_ship_dead);
        c = attackUnit(b, b.m(), b.l());
        d = PlayerTeam.getTeamColorTextures(b);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return a;
        }
        return d[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return c;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && this.posZ > -2.0f;
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

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    public MissileShip(boolean z) {
        super(z);
        this.f = new Rect();
        T(17);
        U(31);
        this.radius = 15.0f;
        this.displayRadius = this.radius - 2.0f;
        this.maxHealth = 350.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = b;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance().effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        return true;
    }

    @Override
    // com.corrodinggames.rts.game.units.sea.WaterUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF E(int i) {
        float f = this.rotationSpeed;
        e.a(this.posX + (Utility.fastCos(f) * 6.0f), this.posY + (Utility.fastSin(f) * 6.0f));
        return e;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float q(int i) {
        return 62.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        PointF pointFE = E(i);
        if (!baseUnit.Q()) {
            Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y, this.posZ, i);
            PointF pointFK = getShadowOffsetForLevel(i);
            projectileA.K = pointFK.x;
            projectileA.L = pointFK.y;
            projectileA.ar = KoolArgbColor.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 50);
            projectileA.U = 62.0f;
            projectileA.l = baseUnit;
            projectileA.h = 190.0f;
            projectileA.t = 2.0f;
            projectileA.aH = true;
            projectileA.aM = true;
            projectileA.aQ = true;
            gameEngine.soundEngine.playSound(SoundEngine.missileFireSound, 0.8f, this.posX, this.posY);
            gameEngine.effectManager.createLightEffect(this.posX, this.posY, this.posZ, -1118720);
            gameEngine.effectManager.createLightEffect(projectileA, -1118720);
            return;
        }
        Projectile projectileA2 = Projectile.a(this, pointFE.x, pointFE.y, this.posZ - 1.0f, i);
        projectileA2.ar = KoolArgbColor.a(255, 0, 0, 150);
        projectileA2.x = 1.0f;
        projectileA2.U = 42.0f;
        projectileA2.l = baseUnit;
        projectileA2.h = 220.0f;
        projectileA2.t = 1.9f;
        projectileA2.aM = true;
        projectileA2.aQ = true;
        gameEngine.soundEngine.playSound(SoundEngine.missileFireSound, 0.8f, this.posX, this.posY);
        gameEngine.effectManager.createLightEffect(this.posX, this.posY, this.posZ, -1118720);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 200.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 170.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 1.2f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float bc() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.9f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.2f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 99.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.05f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.1f;
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        return super.c(f);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: ae */
    public boolean canAttackSubmergedUnits() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
        super.e(f);
        GameViewUtils.a(this, m());
    }
}
