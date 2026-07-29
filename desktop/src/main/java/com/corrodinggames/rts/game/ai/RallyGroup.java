package com.corrodinggames.rts.game.ai;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.a.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/a/l.class */
public class RallyGroup extends AIUnitGroupBase {
    float a;

    @Override // com.corrodinggames.rts.game.ai.AIStrategyNode, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(this.F.size());
        Iterator it = this.F.iterator();
        while (it.hasNext()) {
            gameOutputStream.writeOrderableUnit((OrderableUnit) it.next());
        }
        gameOutputStream.writeByte(1);
        gameOutputStream.writeInt(this.G.size());
        Iterator it2 = this.G.iterator();
        while (it2.hasNext()) {
            gameOutputStream.writeOrderableUnit((OrderableUnit) it2.next());
        }
        gameOutputStream.writeFloat(this.a);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.ai.AIStrategyNode
    /* JADX INFO: renamed from: a */
    public void readFromInputStream(GameInputStream gameInputStream) throws IOException {
        q();
        int i = gameInputStream.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            OrderableUnit unitEntity = gameInputStream.readOrderableUnit();
            if (unitEntity != null) {
                a(unitEntity);
            }
        }
        if (gameInputStream.readByte() >= 1) {
            this.G.clear();
            int i3 = gameInputStream.readInt();
            for (int i4 = 0; i4 < i3; i4++) {
                OrderableUnit unitEntity2 = gameInputStream.readOrderableUnit();
                if (unitEntity2 != null) {
                    this.G.add(unitEntity2);
                }
            }
            this.a = gameInputStream.readFloat();
        }
        super.readFromInputStream(gameInputStream);
    }

    public RallyGroup(AIController aIController) {
        super(aIController);
        this.a = 0.0f;
    }

    @Override // com.corrodinggames.rts.game.ai.AIUnitGroupBase
    public void c(float f) {
        n();
        if (!m()) {
            this.a += f;
        }
        Iterator it = this.F.iterator();
        while (it.hasNext()) {
            OrderableUnit orderableUnit = (OrderableUnit) it.next();
            if (getDistanceSqToUnit((BaseUnit) orderableUnit) < 3600.0f && orderableUnit.unitTransportTarget == null) {
                if (orderableUnit.aB == this) {
                    orderableUnit.aB = null;
                }
                it.remove();
            }
        }
        if (this.F.size() == 0 || this.a > 5000.0f) {
            destroy();
        }
    }

    public void c(OrderableUnit orderableUnit) {
        a(orderableUnit);
        this.G.add(orderableUnit);
    }
}
