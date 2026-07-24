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
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/a.class */
public class AirFactory extends FactoryWithQueue {

    /* JADX INFO: renamed from: f */
    int factoryLevel;

    /* JADX INFO: renamed from: g */
    float animationTiming;

    /* JADX INFO: renamed from: a */
    static Texture level1Texture = null;

    /* JADX INFO: renamed from: b */
    static Texture level2Texture = null;

    /* JADX INFO: renamed from: c */
    static Texture[] level1Textures = new Texture[10];

    /* JADX INFO: renamed from: d */
    static Texture[] level2Textures = new Texture[10];

    /* JADX INFO: renamed from: e */
    static Texture deadTexture = null;

    /* JADX INFO: renamed from: h */
    static final ActionId upgradeActionId = ActionId.intern(String.valueOf(110));

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(this.factoryLevel);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        if (gameInputStream.getProtocolVersion() >= 17) {
            a(gameInputStream.readInt());
        }
        super.a(gameInputStream);
    }

    /* JADX INFO: renamed from: b */
    public static void loadTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        level1Texture = gameEngine.renderGraphicsEngine.a(R.drawable.air_factory);
        level2Texture = gameEngine.renderGraphicsEngine.a(R.drawable.air_factory_t2);
        deadTexture = gameEngine.renderGraphicsEngine.a(R.drawable.air_factory_dead);
        level1Textures = PlayerTeam.getTeamColorTextures(level1Texture);
        level2Textures = PlayerTeam.getTeamColorTextures(level2Texture);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: K, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.airFactory;
    }

    public AirFactory(boolean z) {
        super(z);
        this.factoryLevel = 1;
        this.animationTiming = 0.0f;
        this.baseTexture = level1Texture;
        T(40);
        U(61);
        this.radius = 30.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 1000.0f;
        this.currentHealth = this.maxHealth;
        this.buildingTargetRect.a(-1, -1, 1, 1);
        this.buildingVelocityRect.a(-1, -1, 1, 2);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return deadTexture;
        }
        if (this.team == null) {
            return level1Textures[level1Textures.length - 1];
        }
        if (this.factoryLevel == 1) {
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
        this.baseTexture = deadTexture;
        S(0);
        this.isAlive = false;
        a(UnitSize.large);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryWithQueue, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!isAlive() || this.isDead) {
            return;
        }
        this.animationTiming = Utility.moveTowardsZero(this.animationTiming, f);
        if (this.animationTiming == 0.0f) {
            this.animationTiming = 27.0f;
            this.currentAnimationFrame++;
            if (this.currentAnimationFrame > 4) {
                this.currentAnimationFrame = 0;
            }
        }
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

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: V */
    public int getUpgradeLevel() {
        return this.factoryLevel;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
        if (i == 1) {
            this.factoryLevel = 1;
        } else if (i == 2 && this.factoryLevel == 1) {
            this.factoryLevel = 2;
        }
        S();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public ActionId cm() {
        if (this.factoryLevel == 1) {
            return upgradeActionId;
        }
        return AbstractUnitAction.NONE_ACTION_ID;
    }

    /* JADX INFO: renamed from: a */
    public static void addAvailableActions(ArrayList arrayList, int i) {
        arrayList.add(new SetRallyAction());
        if (i == 1) {
            arrayList.add(new AirFactoryUpgradeT2());
        }
        if (i > 1) {
            arrayList.add(new QueueUnitAction(UnitTypeEnum.dropship, 3.2f));
            arrayList.add(new QueueUnitAction(UnitTypeEnum.gunShip, 4.0f));
            arrayList.add(new QueueUnitAction(UnitTypeEnum.amphibiousJet, 5.0f));
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
