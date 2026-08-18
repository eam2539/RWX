package com.corrodinggames.rts.game.units.buildings.turrets;

import android.graphics.Color;
import android.graphics.PointF;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.actions.PopupQueueAction;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/a/a.class */
public class AntiAirTurret extends TurretFactory {

    /* JADX INFO: renamed from: a */
    static Texture level1Texture = null;

    /* JADX INFO: renamed from: b */
    static Texture level2Texture = null;

    /* JADX INFO: renamed from: c */
    static Texture unitIconTexture = null;

    /* JADX INFO: renamed from: d */
    static Texture[] sharedTeamIconTextures = new Texture[10];

    /* JADX INFO: renamed from: e */
    public static AbstractUnitAction upgradeToLevel2Action = new PopupQueueAction(102) { // from class: com.corrodinggames.rts.game.units.d.a.a.1
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: g */
        public boolean isHighPriority() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public String getDescription() {
            return "-Increases HP, attack damage, and range";
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public String getDisplayName() {
            return "Upgrade";
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: c */
        public int getCostAmount() {
            return 1200;
        }

        @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction
        public float K() {
            return 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean canAfford(BaseUnit baseUnit, boolean z) {
            TurretFactory turretFactory = (TurretFactory) baseUnit;
            if (turretFactory.isUpgraded || turretFactory.a(getActionId(), z) > 0) {
                return false;
            }
            return super.canAfford(baseUnit, z);
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        public boolean b(BaseUnit baseUnit) {
            if (((TurretFactory) baseUnit).isUpgraded) {
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
        public ActionDisplayType getActionDisplayType() {
            return ActionDisplayType.upgrade;
        }
    };

    /* JADX INFO: renamed from: f */
    static ArrayList availableActions = new ArrayList();

    static {
        availableActions.add(upgradeToLevel2Action);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        return sharedTeamIconTextures[this.team.getTeamColorIndex()];
    }

    /* JADX INFO: renamed from: b */
    public static void loadTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        level1Texture = gameEngine.renderGraphicsEngine.a(R.drawable.anti_air_top);
        level2Texture = gameEngine.renderGraphicsEngine.a(R.drawable.anti_air_top_l2);
        unitIconTexture = gameEngine.renderGraphicsEngine.a(R.drawable.unit_icon_building_air_turrent);
        sharedTeamIconTextures = PlayerTeam.getTeamColorTextures(unitIconTexture);
    }

    public AntiAirTurret(boolean z) {
        super(z);
        this.maxHealth = 800.0f;
        this.currentHealth = this.maxHealth;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        if (!this.isUpgraded) {
            return 250.0f;
        }
        return 320.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        if (!this.isUpgraded) {
            return 80.0f;
        }
        return 70.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        if (!this.isUpgraded) {
            return super.e(i);
        }
        if (i == 2) {
            return 25.0f;
        }
        return super.e(i);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.OrderableUnit
    public PointF E(int i) {
        if (!this.isUpgraded || i == 0) {
            return super.E(i);
        }
        float f = E() ? this.rotationSpeed : this.movementLevels[i].targetX;
        PointF pointFG = G(i);
        float f2 = f + (i == 1 ? -30.0f : 30.0f);
        tempPointF2.a(pointFG.x + (Utility.fastCos(f2) * 10.0f), pointFG.y + (Utility.fastSin(f2) * 10.0f));
        return tempPointF2;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        PointF pointFE = E(i);
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y);
        PointF pointFK = getShadowOffsetForLevel(i);
        projectileA.K = pointFK.x;
        projectileA.L = pointFK.y;
        projectileA.t = 0.3f;
        projectileA.r = 6.0f;
        if (!this.isUpgraded) {
            projectileA.ar = Color.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 50);
            projectileA.U = 60.0f;
            projectileA.h = 220.0f;
        } else {
            projectileA.ar = Color.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 50, 50);
            projectileA.U = 60.0f;
            projectileA.h = 250.0f;
            projectileA.t = 0.5f;
            projectileA.r = 7.0f;
        }
        projectileA.P = (short) 4;
        projectileA.x = 1.0f;
        projectileA.l = baseUnit;
        projectileA.aH = false;
        projectileA.aI = 0.0f;
        projectileA.aJ = 0.0f;
        projectileA.aM = true;
        projectileA.aQ = true;
        projectileA.aG = true;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.soundEngine.playSoundAt(SoundEngine.missileFireSound, 0.3f, 1.0f + Utility.randomFloatInRange(-0.07f, 0.07f), pointFE.x, pointFE.y);
        gameEngine.effectManager.createLightEffect(projectileA, -1118720);
        gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1127220);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: K */
    public UnitTypeEnum r() {
        if (this.isUpgraded) {
            return UnitTypeEnum.antiAirTurretT2;
        }
        return UnitTypeEnum.antiAirTurret;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        if (!this.isUpgraded) {
            return level1Texture;
        }
        return level2Texture;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: af */
    public boolean canAttackFlyingUnits() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: ag */
    public boolean canAttackSurfaceUnits() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory
    /* JADX INFO: renamed from: s */
    public void updateTurretRotation(float f) {
        if (this.movementLevels[0].a()) {
            this.movementLevels[0].targetX += c(0) * f * 0.1f;
        }
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 9.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 6.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean u(int i) {
        if (!this.isUpgraded) {
            return super.u(i);
        }
        if (i == 0) {
            return false;
        }
        return super.u(i);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: v */
    public int getLinkedTurretIndex(int i) {
        if (!this.isUpgraded) {
            return -1;
        }
        if (i == 1 || i == 2) {
            return 0;
        }
        return -1;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bl */
    public int getTechLevel() {
        return 3;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean r(int i) {
        if (!this.isUpgraded && i > 1) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void a(com.corrodinggames.rts.game.units.buildings.Projectile projectile) {
        if (projectile.j.equals(upgradeToLevel2Action.getActionId())) {
            a(2);
            W();
        }
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.BaseUnit
    public ActionId cm() {
        if (!this.isUpgraded) {
            return upgradeToLevel2Action.getActionId();
        }
        return AbstractUnitAction.NONE_ACTION_ID;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public void clearAndAddAction(ArrayList arrayList) {
        arrayList.clear();
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
        if (i == 1) {
            this.isUpgraded = false;
        } else if (i == 2 && !this.isUpgraded) {
            this.isUpgraded = true;
            this.maxHealth += 600.0f;
            this.currentHealth += 600.0f;
        }
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretFactory, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return availableActions;
    }
}
