package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.actions.QueueUnitAction;
import com.corrodinggames.rts.game.units.actions.SetRallyAction;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/t.class */
public class SeaFactory extends FactoryWithQueue {

    /* JADX INFO: renamed from: a */
    static Texture baseFactoryTexture = null;

    /* JADX INFO: renamed from: b */
    static Texture tier2FactoryTexture = null;

    /* JADX INFO: renamed from: c */
    static Texture shadowTexture = null;

    /* JADX INFO: renamed from: d */
    static Texture[] baseFactoryTeamTextures = new Texture[10];

    /* JADX INFO: renamed from: e */
    static Texture[] tier2FactoryTeamTextures = new Texture[10];

    /* JADX INFO: renamed from: f */
    static Texture deadFactoryTexture = null;

    /* JADX INFO: renamed from: g */
    static final ActionId upgradeActionId = ActionId.isSameInstance(String.valueOf(110));

    /* JADX INFO: renamed from: b */
    public static void loadTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        baseFactoryTexture = gameEngine.renderGraphicsEngine.a(R.drawable.sea_factory);
        tier2FactoryTexture = gameEngine.renderGraphicsEngine.a(R.drawable.sea_factory_t2);
        deadFactoryTexture = gameEngine.renderGraphicsEngine.a(R.drawable.sea_factory_dead);
        baseFactoryTeamTextures = PlayerTeam.getTeamColorTextures(baseFactoryTexture);
        tier2FactoryTeamTextures = PlayerTeam.getTeamColorTextures(tier2FactoryTexture);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: K, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.seaFactory;
    }

    public SeaFactory(boolean z) {
        super(z);
        this.baseTexture = baseFactoryTexture;
        b(baseFactoryTexture);
        this.radius = 45.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 1000.0f;
        this.currentHealth = this.maxHealth;
        S(2);
        this.buildingTargetRect.a(-1, -1, 1, 2);
        this.buildingVelocityRect.a(-2, -1, 2, 4);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return deadFactoryTexture;
        }
        if (this.team == null) {
            return baseFactoryTeamTextures[baseFactoryTeamTextures.length - 1];
        }
        if (this.buildingAnimationState == 1) {
            return baseFactoryTeamTextures[this.team.getTeamColorIndex()];
        }
        return tier2FactoryTeamTextures[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding
    /* JADX INFO: renamed from: L */
    public boolean onDeath() {
        this.overlayTexture = null;
        this.baseTexture = deadFactoryTexture;
        S(0);
        this.isAlive = false;
        a(UnitSize.large);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void a(Projectile projectile) {
        if (projectile.j.equals(upgradeActionId)) {
            PlayerTeam.b((BaseUnit) this);
            a(2);
            PlayerTeam.c(this);
            W();
            return;
        }
        super.a(projectile);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue
    public int dv() {
        return -20;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: V */
    public int getUpgradeLevel() {
        return this.buildingAnimationState;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
        if (i == 1) {
            this.buildingAnimationState = 1;
        } else if (i == 2 && this.buildingAnimationState == 1) {
            this.buildingAnimationState = 2;
        }
        S();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public ActionId cm() {
        if (this.buildingAnimationState == 1) {
            return upgradeActionId;
        }
        return AbstractUnitAction.NONE_ACTION_ID;
    }

    /* JADX INFO: renamed from: a */
    public static void addAvailableActions(ArrayList arrayList, int i) {
        arrayList.add(new SetRallyAction());
        arrayList.add(new SeaFactoryT2Upgrade());
        arrayList.add(new QueueUnitAction(UnitTypeEnum.builderShip, 1.0f));
        arrayList.add(new QueueUnitAction(UnitTypeEnum.gunBoat, 2.0f));
        arrayList.add(new QueueUnitAction(UnitTypeEnum.missileShip, 3.0f));
        arrayList.add(new QueueUnitAction(UnitTypeEnum.hovercraft, 4.0f));
        arrayList.add(new QueueUnitAction(UnitTypeEnum.battleShip, 5.0f));
        arrayList.add(new QueueUnitAction(UnitTypeEnum.attackSubmarine, 6.0f));
        if (i > 1) {
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return r().a(getUpgradeLevel());
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bJ() {
        return true;
    }
}
