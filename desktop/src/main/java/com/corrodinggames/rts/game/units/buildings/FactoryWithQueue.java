package com.corrodinggames.rts.game.units.buildings;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/i.class */
public abstract class FactoryWithQueue extends BaseBuilding implements FactoryQueueInterface {
    public static final Paint y = new Paint();
    FactoryQueueManager z;
    Rect A;
    Rect B;

    static {
        y.a(255, 0, 255, 0);
        y.a(1.5f);
        y.a(true);
    }

    public FactoryWithQueue(boolean z) {
        super(z);
        this.z = du();
        this.A = new Rect();
        this.B = new Rect();
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(this.buildingAnimationState);
        this.z.a(gameOutputStream);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        if (gameInputStream.getProtocolVersion() >= 69) {
            setBuildingAnimationState(gameInputStream.readInt());
        }
        this.z.a(gameInputStream);
        super.a(gameInputStream);
    }

    public FactoryQueueManager du() {
        return new FactoryQueueManager(this);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void b(Projectile projectile) {
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public boolean c(Projectile projectile) {
        return true;
    }

    public void a(Projectile projectile) {
        float f;
        if (this.z.b != null) {
            f = this.radius * 2.0f;
        } else {
            f = this.radius * 3.0f;
        }
        BaseUnit baseUnitA = this.z.a(projectile, f, false, 0.0f);
        if (baseUnitA != null) {
            if (baseUnitA.posY - baseUnitA.radius < this.posY + dv()) {
                baseUnitA.posY = this.posY + dv() + baseUnitA.radius;
            }
            PlayerTeam.c(baseUnitA);
        }
    }

    public int dv() {
        return -100;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public int f(boolean z) {
        return this.z.a(AbstractUnitAction.NONE_ACTION_ID, z, true);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public final int a(ActionId actionId, boolean z) {
        return this.z.a(actionId, z);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public Projectile dw() {
        return this.z.b();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bD */
    public UnitPrice getQueuedActionPriceDelta() {
        return this.z.c();
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public FastArrayList dx() {
        return this.z.c;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public int h(UnitType unitType) {
        return this.z.a(unitType);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public boolean dy() {
        return this.z.a();
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void dz() {
        this.z.e = 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public void a(PointF pointF) {
        this.z.b = pointF;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.FactoryQueueInterface
    public boolean dA() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public float bV() {
        if (isAlive() && !this.z.a()) {
            return this.z.e;
        }
        return super.bV();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: e */
    public AbstractUnitAction getUnitAction(UnitType unitType) {
        return this.z.b(unitType);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public void performUnitAction(AbstractUnitAction abstractUnitAction, boolean z) {
        this.z.a(abstractUnitAction, z, (PointF) null, (BaseUnit) null);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b */
    public void stopMoving(AbstractUnitAction abstractUnitAction, boolean z) {
        this.z.a(abstractUnitAction, z);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void a(AbstractUnitAction abstractUnitAction) {
        this.z.a(abstractUnitAction);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!isAlive() || this.isDestroyed) {
            return;
        }
        this.z.a(f);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        return super.c(f);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public void bv() {
        PlayerTeam.a((BaseUnit) this);
        this.z.a(true);
        super.bv();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.SizedObject, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void remove() {
        PlayerTeam.a((BaseUnit) this);
        this.z.a(true);
        super.remove();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        throw new RuntimeException("Unit cannot shoot");
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: ca */
    public void getUnitAISettings() {
        if (this.z.b != null) {
            final GameEngine instance = GameEngine.getInstance();
            instance.graphicsEngine2.a((float)(int)(this.posX - instance.viewpointXSnapped), (float)(int)(this.posY - instance.viewpointYSnapped), (float)(int)(this.z.b.x - instance.viewpointXSnapped), (float)(int)(this.z.b.y - instance.viewpointYSnapped), FactoryWithQueue.y);
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public int a(AnimationTag animationTag) {
        return this.z.a(animationTag);
    }
}
