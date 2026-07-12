package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.actions.QueueUnitAction;
import com.corrodinggames.rts.game.units.actions.SetRallyAction;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/m.class */
public class LandFactory extends FactoryWithQueue {

    /* JADX INFO: renamed from: g */
    boolean isUpgraded;

    /* JADX INFO: renamed from: a */
    static Texture level1Texture = null;

    /* JADX INFO: renamed from: b */
    static Texture level2Texture = null;

    /* JADX INFO: renamed from: c */
    static Texture backgroundTexture = null;

    /* JADX INFO: renamed from: d */
    static Texture[] level1TeamTextures = new Texture[10];

    /* JADX INFO: renamed from: e */
    static Texture[] level2TeamTextures = new Texture[10];

    /* JADX INFO: renamed from: f */
    static Texture deadTexture = null;

    /* JADX INFO: renamed from: h */
    static final ActionId upgradeActionId = ActionId.isSameInstance(String.valueOf(110));

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(this.isUpgraded);
        gameOutputStream.writeByte(0);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        if (gameInputStream.readBoolean()) {
            a(2);
        }
        gameInputStream.readByte();
        super.a(gameInputStream);
    }

    /* JADX INFO: renamed from: b */
    public static void loadTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        level1Texture = gameEngine.renderGraphicsEngine.a(R.drawable.land_factory_front);
        level2Texture = gameEngine.renderGraphicsEngine.a(R.drawable.land_factory_front_t2);
        backgroundTexture = gameEngine.renderGraphicsEngine.a(R.drawable.land_factory_back);
        deadTexture = gameEngine.renderGraphicsEngine.a(R.drawable.land_factory_dead);
        level1TeamTextures = PlayerTeam.getTeamColorTextures(level1Texture);
        level2TeamTextures = PlayerTeam.getTeamColorTextures(level2Texture);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: K, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.landFactory;
    }

    public LandFactory(boolean z) {
        super(z);
        this.baseTexture = level1Texture;
        this.overlayTexture = backgroundTexture;
        b(this.baseTexture);
        this.radius = 30.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 1200.0f;
        this.currentHealth = this.maxHealth;
        S(3);
        this.buildingTargetRect.a(-1, -1, 1, 1);
        this.buildingVelocityRect.a(-1, -1, 1, 3);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void S() {
        super.S();
        if (this.isDestroyed) {
            this.overlayTexture = null;
        } else {
            this.overlayTexture = backgroundTexture;
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return deadTexture;
        }
        if (this.team == null) {
            return level1TeamTextures[level1TeamTextures.length - 1];
        }
        if (!this.isUpgraded) {
            return level1TeamTextures[this.team.getTeamColorIndex()];
        }
        return level2TeamTextures[this.team.getTeamColorIndex()];
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
        this.overlayTexture = null;
        this.baseTexture = deadTexture;
        S(0);
        this.isAlive = false;
        gameEngine.soundEngine.playSound(SoundEngine.buildingExplodeSound, 0.8f, this.posX, this.posY);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void a(Projectile projectile) {
        if (upgradeActionId.fromString(projectile.j)) {
            PlayerTeam.b((BaseUnit) this);
            a(2);
            PlayerTeam.c(this);
            W();
            return;
        }
        super.a(projectile);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
        if (i == 1) {
            this.isUpgraded = false;
        } else if (i == 2 && !this.isUpgraded) {
            this.isUpgraded = true;
        }
        S();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public ActionId cm() {
        if (!this.isUpgraded) {
            return upgradeActionId;
        }
        return AbstractUnitAction.NONE_ACTION_ID;
    }

    /* JADX INFO: renamed from: a */
    public static void addAvailableActions(ArrayList arrayList, int i) {
        arrayList.add(new SetRallyAction());
        if (i == 1) {
            arrayList.add(new LandFactoryUpgradeAction());
        }
        arrayList.add(new QueueUnitAction(UnitTypeEnum.builder, 1.0f));
        arrayList.add(new QueueUnitAction(UnitTypeEnum.tank, 2.0f));
        arrayList.add(new QueueUnitAction(UnitTypeEnum.hoverTank, 3.0f));
        arrayList.add(new QueueUnitAction(UnitTypeEnum.artillery, 4.0f));
        if (i >= 2) {
            arrayList.add(new QueueUnitAction(UnitTypeEnum.hovercraft, 5.0f));
            arrayList.add(new QueueUnitAction(UnitTypeEnum.heavyTank, 6.0f));
            arrayList.add(new QueueUnitAction(UnitTypeEnum.heavyHoverTank, 7.0f));
            arrayList.add(new QueueUnitAction(UnitTypeEnum.laserTank, 8.0f));
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return r().a(getUpgradeLevel());
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: V */
    public int getUpgradeLevel() {
        if (this.isUpgraded) {
            return 2;
        }
        return 1;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue
    public FactoryQueueManager du() {
        return new LandFactoryQueueManager(this);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bJ() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: db */
    public float getSelectionRadius() {
        return super.getSelectionRadius() - 8.0f;
    }
}
