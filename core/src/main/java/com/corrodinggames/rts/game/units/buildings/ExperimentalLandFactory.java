package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.*;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/f.class */
public class ExperimentalLandFactory extends FactoryWithQueue {

    /* JADX INFO: renamed from: f */
    boolean isUpgraded;

    /* JADX INFO: renamed from: a */
    static Texture baseTexture = null;

    /* JADX INFO: renamed from: b */
    static Texture backgroundTexture = null;

    /* JADX INFO: renamed from: c */
    static Texture[] teamTextures = new Texture[10];

    /* JADX INFO: renamed from: d */
    static Texture[] upgradedTeamTextures = new Texture[10];

    /* JADX INFO: renamed from: e */
    static Texture deadTexture = null;

    /* JADX INFO: renamed from: g */
    static AbstractUnitAction upgradeAction = new PopupQueueAction(110) { // from class: com.corrodinggames.rts.game.units.d.f.1
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: g */
        public boolean isHighPriority() {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public String getDescription() {
            return "-Allows factory to build Tech 2 units";
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public String getDisplayName() {
            return Locale.get("gui.actions.upgradeT2", new Object[0]);
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: c */
        public int getCostAmount() {
            return 1500;
        }

        @Override // com.corrodinggames.rts.game.units.actions.PopupQueueAction
        public float K() {
            return 5.0E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean canAfford(BaseUnit baseUnit, boolean z) {
            ExperimentalLandFactory experimentalLandFactory = (ExperimentalLandFactory) baseUnit;
            if (experimentalLandFactory.isUpgraded || experimentalLandFactory.a(getActionId(), z) > 0) {
                return false;
            }
            return super.canAfford(baseUnit, z);
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

    @Override
    // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(this.isUpgraded);
        gameOutputStream.writeByte(0);
        super.a(gameOutputStream);
    }

    @Override
    // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        if (gameInputStream.readBoolean()) {
            M();
        }
        gameInputStream.readByte();
        super.a(gameInputStream);
    }

    public static void b() {
        GameEngine gameEngine = GameEngine.getInstance();
        baseTexture = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_unit_factory_front);
        backgroundTexture = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_unit_factory_base);
        deadTexture = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_unit_factory_dead);
        teamTextures = PlayerTeam.getTeamColorTextures(baseTexture);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: K, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.experimentalLandFactory;
    }

    public ExperimentalLandFactory(boolean z) {
        super(z);
        super.baseTexture = baseTexture;
        this.overlayTexture = backgroundTexture;
        b(super.baseTexture);
        this.radius = 55.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 3200.0f;
        this.currentHealth = this.maxHealth;
        S(4);
        this.buildingTargetRect.a(-2, -2, 2, 2);
        this.buildingVelocityRect.a(-2, -2, 2, 4);
    }

    @Override
    // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return deadTexture;
        }
        if (this.team == null) {
            return teamTextures[teamTextures.length - 1];
        }
        if (!this.isUpgraded) {
            return teamTextures[this.team.getTeamColorIndex()];
        }
        return upgradedTeamTextures[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void S() {
        super.S();
        if (this.isDead) {
            this.overlayTexture = null;
        } else {
            this.overlayTexture = backgroundTexture;
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding
    /* JADX INFO: renamed from: L */
    public boolean onDeath() {
        GameEngine.getInstance();
        this.overlayTexture = null;
        super.baseTexture = deadTexture;
        S(0);
        this.isAlive = false;
        a(UnitSize.verylargeBuilding);
        return true;
    }

    @Override
    // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void a(Projectile projectile) {
        if (projectile.j.equals(upgradeAction.getActionId())) {
            M();
        } else {
            super.a(projectile);
        }
    }

    public void M() {
        if (!this.isUpgraded) {
            this.isUpgraded = true;
            S();
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public ActionId cm() {
        return AbstractUnitAction.NONE_ACTION_ID;
    }

    public static void a(ArrayList arrayList, int i) {
        arrayList.add(new SetRallyAction());
        arrayList.add(new QueueUnitAction(UnitTypeEnum.experimentalTank, 2.0f));
        arrayList.add(new QueueUnitAction(UnitTypeEnum.experimentalHoverTank, 3.0f));
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

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: V */
    public int getUpgradeLevel() {
        return 2;
    }
}
