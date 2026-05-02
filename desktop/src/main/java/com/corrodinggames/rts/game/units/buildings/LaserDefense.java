package com.corrodinggames.rts.game.units.buildings;

import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.actions.PopupQueueAction;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.effects.Effect;
import com.corrodinggames.rts.gameFramework.effects.EffectQuality;
import com.corrodinggames.rts.gameFramework.effects.EffectType;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/p.class */
public class LaserDefense extends FactoryWithQueue {

    /* JADX INFO: renamed from: d */
    boolean isUpgraded;

    /* JADX INFO: renamed from: e */
    boolean isRecharging;

    /* JADX INFO: renamed from: f */
    float laserEffectTimer;

    /* JADX INFO: renamed from: i */
    PointF laserOriginPoint;

    /* JADX INFO: renamed from: j */
    Rect drawRect;

    /* JADX INFO: renamed from: a */
    static Texture[] level1Textures = new Texture[10];

    /* JADX INFO: renamed from: b */
    static Texture[] level2Textures = new Texture[10];

    /* JADX INFO: renamed from: c */
    static Texture deadTexture = null;

    /* JADX INFO: renamed from: g */
    static Texture iconTexture = null;

    /* JADX INFO: renamed from: h */
    static Texture[] shadowTexture = new Texture[10];

    /* JADX INFO: renamed from: k */
    static AbstractUnitAction upgradeAction = new PopupQueueAction(102) { // from class: com.corrodinggames.rts.game.units.d.p.1
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: g */
        public boolean isHighPriority() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public String isLocked() {
            return Locale.get("units.laserDefence.upgrade.description", new Object[0]);
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public String getCostForUnit() {
            return Locale.get("units.laserDefence.upgrade.name", new Object[0]);
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: c */
        public int isConfirmed() {
            return UnitTypeEnum.laserDefence.getUpgradeCost(2);
        }

        @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction
        public float K() {
            return 3.0E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
            LaserDefense laserDefense = (LaserDefense) baseUnit;
            if (laserDefense.isUpgraded || laserDefense.a(getActionId(), z) > 0) {
                return false;
            }
            return super.drawTooltip(baseUnit, z);
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        public boolean b(BaseUnit baseUnit) {
            if (((LaserDefense) baseUnit).isUpgraded) {
                return false;
            }
            return true;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: L, reason: merged with bridge method [inline-methods] */
        public UnitTypeEnum getUnitType() {
            return null;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: f */
        public ActionDisplayType isAlsoSelected() {
            return ActionDisplayType.upgrade;
        }
    };

    /* JADX INFO: renamed from: l */
    static ArrayList availableActions = new ArrayList();

    static {
        availableActions.add(upgradeAction);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(this.isUpgraded);
        gameOutputStream.writeFloat(this.f0cB);
        gameOutputStream.writeBoolean(this.isRecharging);
        gameOutputStream.writeFloat(this.laserEffectTimer);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.isUpgraded = gameInputStream.readBoolean();
        this.f0cB = gameInputStream.readFloat();
        this.isRecharging = gameInputStream.readBoolean();
        if (gameInputStream.getProtocolVersion() >= 38) {
            this.laserEffectTimer = gameInputStream.readFloat();
        }
        super.a(gameInputStream);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        return shadowTexture[this.team.getTeamColorIndex()];
    }

    /* JADX INFO: renamed from: b */
    public static void initializeTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        deadTexture = gameEngine.graphicsEngine2.a(R.drawable.laser_defence_dead);
        Texture textureA = gameEngine.graphicsEngine2.a(R.drawable.laser_defence);
        Texture textureA2 = gameEngine.graphicsEngine2.a(R.drawable.laser_defence_t2);
        level1Textures = PlayerTeam.getUnitCountByType(textureA);
        level2Textures = PlayerTeam.getUnitCountByType(textureA2);
        textureA.n();
        textureA2.n();
        iconTexture = gameEngine.graphicsEngine2.a(R.drawable.unit_icon_building_turrent);
        shadowTexture = PlayerTeam.getUnitCountByType(iconTexture);
    }

    public LaserDefense(boolean z) {
        super(z);
        this.laserOriginPoint = new PointF();
        this.drawRect = new Rect();
        a(level1Textures[0], 2);
        this.f0cB = 1.0f;
        this.radius = 19.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 500.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = level1Textures[level1Textures.length - 1];
        this.buildingTargetRect.a(0, 0, 1, 1);
        this.buildingVelocityRect.a(0, 0, 1, 1);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return deadTexture;
        }
        if (this.team == null) {
            return level1Textures[level1Textures.length - 1];
        }
        if (!this.isUpgraded) {
            return level1Textures[this.team.getTeamColorIndex()];
        }
        return level2Textures[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding
    /* JADX INFO: renamed from: L */
    public boolean onDeath() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = deadTexture;
        S(0);
        this.isAlive = false;
        gameEngine.soundEngine.playSound(SoundEngine.buildingExplodeSound, 0.8f, this.posX, this.posY);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        float f2;
        super.update(f);
        if (!isAlive() || this.isDestroyed) {
            return;
        }
        GameEngine.getInstance();
        float f3 = 4.0E-4f * f;
        if (this.isUpgraded) {
            f3 += 2.0E-4f * f;
        }
        this.f0cB = Utility.distanceSq(this.f0cB, 1.0f, f3);
        if (this.f0cB >= 1.0f) {
            this.isRecharging = false;
        }
        this.laserEffectTimer -= f;
        this.laserOriginPoint.a(E(0));
        if (this.f0cB > 0.0f && !this.isRecharging) {
            if (!this.isUpgraded) {
                f2 = 0.11f;
            } else {
                f2 = 0.05f;
            }
            if (attackTarget(this, this.laserOriginPoint.x, this.laserOriginPoint.y, this.posZ, m(), f2)) {
                this.laserEffectTimer = 3.0f;
            }
            if (this.f0cB < 0.0f) {
                this.f0cB = 0.0f;
                this.isRecharging = true;
            }
        }
        if (this.isRecharging) {
            this.currentAnimationFrame = 1;
        } else {
            this.currentAnimationFrame = 0;
        }
    }

    /* JADX INFO: renamed from: a */
    public static boolean attackTarget(OrderableUnit orderableUnit, float f, float f2, float f3, float f4, float f5) {
        GameEngine gameEngine = GameEngine.getInstance();
        float f6 = f4 * f4;
        Object[] objArrA = Projectile.a.a();
        int i = Projectile.a.size;
        for (int i2 = 0; i2 < i; i2++) {
            Projectile projectile = (Projectile) objArrA[i2];
            if (!projectile.A && !projectile.C && ((projectile.J > 7.0f || (projectile.J > 2.0f && projectile.t > 8.0f)) && !projectile.aS && ((projectile.posX - f) * (projectile.posX - f)) + ((projectile.posY - f2) * (projectile.posY - f2)) < f6 && projectile.posZ >= -1.0f)) {
                boolean z = false;
                if (projectile.l != null && orderableUnit.team.d(projectile.l.team)) {
                    z = true;
                }
                if (!z && projectile.j != null && orderableUnit.team.c(projectile.j.team)) {
                    z = true;
                }
                if (z) {
                    Effect effectCreateLaserEffect = gameEngine.effectManager.createLaserEffect(f, f2, f3, projectile.posX, projectile.posY, projectile.posZ);
                    if (effectCreateLaserEffect != null) {
                        effectCreateLaserEffect.V = 10.0f;
                        effectCreateLaserEffect.W = effectCreateLaserEffect.V;
                    }
                    Effect effectCreateEffectInternal = gameEngine.effectManager.createEffectInternal(f, f2, f3, EffectType.custom, false, EffectQuality.high);
                    if (effectCreateEffectInternal != null) {
                        effectCreateEffectInternal.P = 0.0f;
                        effectCreateEffectInternal.Q = 0.0f;
                        effectCreateEffectInternal.ap = 4;
                        effectCreateEffectInternal.V = 39.0f;
                        effectCreateEffectInternal.W = effectCreateEffectInternal.V;
                        effectCreateEffectInternal.r = true;
                        effectCreateEffectInternal.E = 1.3f;
                        effectCreateEffectInternal.G = 1.1f;
                        effectCreateEffectInternal.F = 0.7f;
                    }
                    projectile.H -= 1.01f;
                    if (projectile.H <= 0.0f) {
                        projectile.d();
                        Effect effectCreateEffectInternal2 = gameEngine.effectManager.createEffectInternal(projectile.posX, projectile.posY, projectile.posZ, EffectType.custom, false, EffectQuality.high);
                        if (effectCreateEffectInternal2 != null) {
                            effectCreateEffectInternal2.P = 0.0f;
                            effectCreateEffectInternal2.Q = 0.0f;
                            effectCreateEffectInternal2.ap = 4;
                            effectCreateEffectInternal2.V = 23.0f;
                            effectCreateEffectInternal2.W = effectCreateEffectInternal2.V;
                            effectCreateEffectInternal2.r = true;
                            effectCreateEffectInternal2.E = 0.9f;
                            effectCreateEffectInternal2.G = 0.5f;
                            effectCreateEffectInternal2.F = 0.2f;
                        }
                        gameEngine.soundEngine.playSoundAt(SoundEngine.laserDeflect2Sound, 0.2f, 1.0f + Utility.randomFloatInRange(-0.07f, 0.07f), projectile.posX, projectile.posY);
                    }
                    orderableUnit.f0cB -= f5;
                    return true;
                }
            }
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF E(int i) {
        tempPointF2.a(this.posX, this.posY - 13.0f);
        return tempPointF2;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        if (!this.isUpgraded) {
            return 160.0f;
        }
        return 210.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean b(int i, float f) {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        return super.c(f);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: K, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.laserDefence;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bW */
    public float getUnitTeamData() {
        if (this.f0cB != 1.0f) {
            return this.f0cB;
        }
        return super.getUnitTeamData();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bX */
    public boolean isUnitAtPositionX() {
        return this.isRecharging;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public float bd() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void a(com.corrodinggames.rts.game.units.buildings.Projectile projectile) {
        if (projectile.j.equals(upgradeAction.getActionId())) {
            PlayerTeam.b((BaseUnit) this);
            a(2);
            PlayerTeam.c(this);
            W();
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public ActionId cm() {
        if (!this.isUpgraded) {
            return upgradeAction.getActionId();
        }
        return AbstractUnitAction.NONE_ACTION_ID;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: V */
    public int getUpgradeLevel() {
        if (this.isUpgraded) {
            return 2;
        }
        return 1;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
        if (i == 1) {
            this.isUpgraded = false;
        } else if (i == 2 && !this.isUpgraded) {
            this.isUpgraded = true;
            this.maxHealth += 900.0f;
            this.currentHealth += 900.0f;
        }
        S();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return availableActions;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
        super.e(f);
        GameViewUtils.a(this, m());
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cZ */
    public float getUnitAIState() {
        return GameEngine.getInstance().tileMap.tileWorldSizeX;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: da */
    public float getUnitAIPathfindStatus() {
        return GameEngine.getInstance().tileMap.tileWorldSizeY;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: db */
    public float getUnitAIPathfindError() {
        return super.getUnitAIPathfindError() - 8.0f;
    }
}
