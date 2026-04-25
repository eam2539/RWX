package com.corrodinggames.rts.game.units.land;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/m.class */
public class MegaTankUnit extends LandUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture[] d = new Texture[10];
    Rect e;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.megaTank;
    }

    public static void f() {
        GameEngine gameEngine = GameEngine.getInstance();
        b = gameEngine.graphicsEngine2.a(R.drawable.mega_tank);
        a = gameEngine.graphicsEngine2.a(R.drawable.mega_tank_dead);
        c = gameEngine.graphicsEngine2.a(R.drawable.mega_tank_turret);
        d = PlayerTeam.getUnitCountByType(b);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return a;
        }
        return d[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return c;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = a;
        S(0);
        this.isAttacking = false;
        gameEngine.soundEngine.playSound(SoundEngine.unitExplodeSound, 0.8f, this.posX, this.posY);
        bq();
        return true;
    }

    public MegaTankUnit(boolean z) {
        super(z);
        this.e = new Rect();
        T(20);
        U(25);
        this.speed = 12.0f;
        this.maxSpeed = this.speed + 1.0f;
        this.maxHealth = 550.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = b;
    }

    @Override // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 7000.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        if (!baseUnit.i()) {
            PointF pointFE = E(i);
            Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y);
            projectileA.ar = Color.a(255, 150, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 40);
            projectileA.U = 50.0f;
            projectileA.l = baseUnit;
            projectileA.h = 60.0f;
            projectileA.t = 3.0f;
            projectileA.x = 2.0f;
            projectileA.aQ = true;
            GameEngine gameEngine = GameEngine.getInstance();
            gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1127220);
            gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
            gameEngine.soundEngine.playSound(SoundEngine.gunFire4Sound, 0.3f, this.posX, this.posY);
            return;
        }
        Projectile projectileA2 = Projectile.a(this, this.posX, this.posY);
        projectileA2.ar = Color.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 50);
        projectileA2.U = 40.0f;
        projectileA2.l = baseUnit;
        projectileA2.h = 190.0f;
        projectileA2.t = 4.0f;
        projectileA2.aH = true;
        projectileA2.aI = 10.0f;
        projectileA2.aJ = 15.0f;
        projectileA2.aM = true;
        projectileA2.aQ = true;
        GameEngine.getInstance().soundEngine.playSound(SoundEngine.missileFireSound, 0.2f, this.posX, this.posY);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 140.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 70.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.8f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.2f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 2.0f;
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

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        if (!super.c(f)) {
            return false;
        }
        GameViewUtils.a((OrderableUnit) this);
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
        return 12.0f;
    }
}
