package com.corrodinggames.rts.game.units.sea;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.effects.Effect;
import com.corrodinggames.rts.gameFramework.effects.EffectManager;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolArgbColor;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.h.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/h/a.class */
public class BattleShip extends WaterUnit {

    /* JADX INFO: renamed from: a */
    static Texture battleShipTexture = null;

    /* JADX INFO: renamed from: b */
    static Texture battleShipTexture_alive = null;

    /* JADX INFO: renamed from: c */
    static Texture battleShipTurretTexture = null;

    /* JADX INFO: renamed from: d */
    static Texture battleShipShadowTexture = null;

    /* JADX INFO: renamed from: e */
    static Texture[] battleShipTextures_teamColors = new Texture[10];

    /* JADX INFO: renamed from: f */
    Rect boundingRect;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.battleShip;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 9000.0f;
    }

    /* JADX INFO: renamed from: f */
    public static void loadTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        battleShipTexture_alive = gameEngine.renderGraphicsEngine.a(R.drawable.battle_ship_t2);
        battleShipTexture = gameEngine.renderGraphicsEngine.a(R.drawable.battle_ship_t2_dead);
        battleShipTurretTexture = gameEngine.renderGraphicsEngine.a(R.drawable.battle_ship_t2_turret);
        battleShipTextures_teamColors = PlayerTeam.getTeamColorTextures(battleShipTexture_alive);
        battleShipShadowTexture = attackUnit(battleShipTexture_alive, battleShipTexture_alive.m(), battleShipTexture_alive.l());
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return battleShipTexture;
        }
        return battleShipTextures_teamColors[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return battleShipTurretTexture;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return battleShipShadowTexture;
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

    public BattleShip(boolean z) {
        super(z);
        this.boundingRect = new Rect();
        b(battleShipTexture_alive);
        this.radius = 20.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 1200.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = battleShipTexture_alive;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance().effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = battleShipTexture;
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
        return 65.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        PointF pointFE = E(i);
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y, this.posZ, i);
        PointF pointFK = getShadowTexture(i);
        projectileA.K = pointFK.x;
        projectileA.L = pointFK.y;
        projectileA.U = q(i);
        projectileA.l = baseUnit;
        projectileA.h = 80.0f;
        projectileA.x = 2.0f;
        projectileA.t = 4.0f;
        projectileA.S = true;
        projectileA.ar = KoolArgbColor.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, 0);
        projectileA.aQ = true;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.soundEngine.playSound(SoundEngine.cannonFiringSound, 0.2f, pointFE.x, pointFE.y);
        gameEngine.effectManager.createLightEffect(projectileA, -1118720);
        Effect effectCreateFlameEffect = gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
        if (effectCreateFlameEffect != null) {
            EffectManager.attachEffectToGameObject(effectCreateFlameEffect, this);
        }
        gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1118720);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 240.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.8f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float bc() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float C(int i) {
        if (this.isRotating && bc() > 0.95d) {
            if (i == 0) {
                return this.rotationSpeed + 140.0f;
            }
            return this.rotationSpeed - 140.0f;
        }
        return this.rotationSpeed;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.8f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.08f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 2.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float w(int i) {
        return 0.08f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.03f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.1f;
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
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
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 15.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bl */
    public int getTechLevel() {
        return 2;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF G(int i) {
        float f;
        PointF pointFG = super.G(i);
        float f2 = pointFG.x;
        float f3 = pointFG.y;
        if (i == 0) {
            f = 22.0f;
        } else {
            f = 4.0f;
        }
        tempPointF3.a(f2 + (Utility.fastCos(this.rotationSpeed) * f), f3 + (Utility.fastSin(this.rotationSpeed) * f));
        return tempPointF3;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 120 - (i * 28);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        return i * 30;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
        super.e(f);
        GameViewUtils.a(this, m());
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float H(int i) {
        return -2.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float I(int i) {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float J(int i) {
        return 12.0f;
    }
}
