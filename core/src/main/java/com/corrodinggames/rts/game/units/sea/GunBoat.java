package com.corrodinggames.rts.game.units.sea;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolArgbColor;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.h.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/h/c.class */
public class GunBoat extends WaterUnit {

    /* JADX INFO: renamed from: a */
    static Texture gunBoatTexture_dead = null;

    /* JADX INFO: renamed from: b */
    static Texture gunBoatTexture = null;

    /* JADX INFO: renamed from: c */
    static Texture gunBoatShadowTexture = null;

    /* JADX INFO: renamed from: d */
    static Texture[] gunBoatTextures_teamColors = new Texture[10];
    Rect e;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.gunBoat;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 1500.0f;
    }

    public static void f() {
        GameEngine gameEngine = GameEngine.getInstance();
        gunBoatTexture = gameEngine.renderGraphicsEngine.a(R.drawable.gun_boat);
        gunBoatTexture_dead = gameEngine.renderGraphicsEngine.a(R.drawable.gun_boat_dead);
        gunBoatShadowTexture = attackUnit(gunBoatTexture, gunBoatTexture.m(), gunBoatTexture.l());
        gunBoatTextures_teamColors = PlayerTeam.getTeamColorTextures(gunBoatTexture);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return gunBoatTexture_dead;
        }
        return gunBoatTextures_teamColors[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return gunBoatShadowTexture;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && this.posZ > -2.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: G */
    public float getShadowOffsetX() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: H */
    public float getShadowOffsetY() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    public GunBoat(boolean z) {
        super(z);
        this.e = new Rect();
        T(15);
        U(27);
        this.radius = 12.0f;
        this.displayRadius = this.radius - 2.0f;
        this.maxHealth = 170.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = gunBoatTexture;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance().effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = gunBoatTexture_dead;
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
    public float q(int i) {
        return 12.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        PointF pointFE = E(i);
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y);
        PointF pointFK = getShadowOffsetForLevel(i);
        projectileA.K = pointFK.x;
        projectileA.L = pointFK.y;
        projectileA.posZ = this.posZ;
        projectileA.U = q(i);
        projectileA.l = baseUnit;
        projectileA.h = 30.0f;
        projectileA.t = 8.0f;
        projectileA.S = false;
        projectileA.ar = KoolArgbColor.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 0);
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.soundEngine.playSound(SoundEngine.gunFireSound, 0.2f, pointFE.x, pointFE.y);
        gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
        gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1118720);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 120.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 60.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 1.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 2.8f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.35f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 99.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.06f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.2f;
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
    /* JADX INFO: renamed from: af */
    public boolean canAttackFlyingUnits() {
        return false;
    }
}
