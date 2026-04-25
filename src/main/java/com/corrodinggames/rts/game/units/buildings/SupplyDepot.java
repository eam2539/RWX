package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionDisplayType;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.actions.PopupQueueAction;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.v */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/v.class */
public class SupplyDepot extends FactoryWithQueue {

    /* JADX INFO: renamed from: f */
    int depotLevel;

    /* JADX INFO: renamed from: g */
    float animationTimer;

    /* JADX INFO: renamed from: h */
    int supplyCount;

    /* JADX INFO: renamed from: a */
    static Texture baseTexture = null;

    /* JADX INFO: renamed from: b */
    static Texture tier2Texture = null;

    /* JADX INFO: renamed from: c */
    static Texture[] baseTeamTextures = new Texture[10];

    /* JADX INFO: renamed from: d */
    static Texture[] tier2TeamTextures = new Texture[10];

    /* JADX INFO: renamed from: e */
    static Texture deadTexture = null;
    public static int i = 0;

    /* JADX INFO: renamed from: j */
    static AbstractUnitAction upgradeAction = new PopupQueueAction(102) { // from class: com.corrodinggames.rts.game.units.d.v.1
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: g */
        public boolean isHighPriority() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public String isLocked() {
            return Locale.get("units.supplyDepot.upgrade.description", new Object[0]);
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public String getCostForUnit() {
            return Locale.get("units.supplyDepot.upgrade.name", new Object[0]);
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: c */
        public int isConfirmed() {
            return 1000;
        }

        @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction
        public float K() {
            return 4.0E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
            SupplyDepot supplyDepot = (SupplyDepot) baseUnit;
            if (supplyDepot.depotLevel != 1 || supplyDepot.a(getActionId(), z) > 0) {
                return false;
            }
            return super.drawTooltip(baseUnit, z);
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

    /* JADX INFO: renamed from: k */
    static ArrayList availableActions = new ArrayList();

    static {
        availableActions.add(upgradeAction);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(this.depotLevel);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        a(gameInputStream.readInt());
        super.a(gameInputStream);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.supplyDepot;
    }

    public static void K() {
        GameEngine gameEngine = GameEngine.getInstance();
        baseTexture = gameEngine.graphicsEngine2.a(R.drawable.supply_depot);
        tier2Texture = gameEngine.graphicsEngine2.a(R.drawable.supply_depot_t2);
        baseTeamTextures = PlayerTeam.getUnitCountByType(baseTexture);
        tier2TeamTextures = PlayerTeam.getUnitCountByType(tier2Texture);
        deadTexture = gameEngine.graphicsEngine2.a(R.drawable.supply_depot_dead);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding
    /* JADX INFO: renamed from: L */
    public boolean onDeath() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = deadTexture;
        S(0);
        this.isAttacking = false;
        gameEngine.soundEngine.playSound(SoundEngine.buildingExplodeSound, 0.8f, this.posX, this.posY);
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return deadTexture;
        }
        if (this.team == null) {
            return baseTeamTextures[baseTeamTextures.length - 1];
        }
        if (this.depotLevel == 1) {
            return baseTeamTextures[this.team.getTeamColorIndex()];
        }
        return tier2TeamTextures[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    public SupplyDepot(boolean z) {
        super(z);
        this.depotLevel = 1;
        this.animationTimer = 0.0f;
        this.supplyCount = 0;
        this.baseTexture = baseTexture;
        a(this.baseTexture, 1);
        this.speed = 20.0f;
        this.maxSpeed = this.speed;
        this.maxHealth = 800.0f;
        this.currentHealth = this.maxHealth;
        this.buildingTargetRect.a(-1, -1, 0, 0);
        this.buildingVelocityRect.a(this.buildingTargetRect);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!isAlive() || this.isDestroyed) {
        }
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void a(Projectile projectile) {
        if (projectile.j.equals(upgradeAction.getActionId())) {
            upgradeToTier2();
            W();
        }
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i2) {
        this.depotLevel = i2;
    }

    /* JADX INFO: renamed from: M */
    public void upgradeToTier2() {
        if (this.depotLevel == 1) {
            this.depotLevel = 2;
            S();
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public ActionId cm() {
        if (this.depotLevel == 1) {
            return upgradeAction.getActionId();
        }
        return AbstractUnitAction.NONE_ACTION_ID;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return availableActions;
    }
}
