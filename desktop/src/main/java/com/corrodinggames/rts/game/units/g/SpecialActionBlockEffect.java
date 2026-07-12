package com.corrodinggames.rts.game.units.g;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.actions.ActionId;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.g.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/g/e.class */
public class SpecialActionBlockEffect extends AirUnitEffect {

    /* JADX INFO: renamed from: b */
    int startTime;

    /* JADX INFO: renamed from: c */
    ActionId blockedAction;

    public SpecialActionBlockEffect() {
        this.blockedAction = ActionId.NONE;
    }

    public SpecialActionBlockEffect(int i, ActionId actionId) {
        super(i);
        this.blockedAction = ActionId.NONE;
        this.blockedAction = actionId;
        this.startTime = GameEngine.getInstance().gameTimeMillis;
    }

    @Override // com.corrodinggames.rts.game.units.g.AirUnitEffect
    public AirUnitEffectType b() {
        return AirUnitEffectType.specialActionBlock;
    }

    public boolean a(ActionId actionId) {
        return this.blockedAction == ActionId.NONE || this.blockedAction == actionId;
    }

    public float c() {
        int i = this.effectId - this.startTime;
        if (i <= 0) {
            return 1.0f;
        }
        return (this.effectId - GameEngine.getInstance().gameTimeMillis) / i;
    }

    public static void a(OrderableUnit orderableUnit, ActionId actionId, int i) {
        AirUnitEffectManager.a(orderableUnit, new SpecialActionBlockEffect(GameEngine.getInstance().gameTimeMillis + i, actionId));
    }

    public static int a(BaseUnit baseUnit, ActionId actionId) {
        SpecialActionBlockEffect specialActionBlockEffectB;
        if (!(baseUnit instanceof OrderableUnit) || ((OrderableUnit) baseUnit).activeStatusEffects == null || (specialActionBlockEffectB = b(baseUnit, actionId)) == null) {
            return 0;
        }
        return specialActionBlockEffectB.d();
    }

    public int d() {
        return this.effectId - GameEngine.getInstance().gameTimeMillis;
    }

    public static SpecialActionBlockEffect b(BaseUnit baseUnit, ActionId actionId) {
        FastArrayList fastArrayList;
        if (!(baseUnit instanceof OrderableUnit) || (fastArrayList = ((OrderableUnit) baseUnit).activeStatusEffects) == null) {
            return null;
        }
        int i = GameEngine.getInstance().gameTimeMillis;
        SpecialActionBlockEffect specialActionBlockEffect = null;
        Object[] objArrA = fastArrayList.a();
        for (int i2 = fastArrayList.size - 1; i2 >= 0; i2--) {
            AirUnitEffect airUnitEffect = (AirUnitEffect) objArrA[i2];
            if (airUnitEffect instanceof SpecialActionBlockEffect) {
                SpecialActionBlockEffect specialActionBlockEffect2 = (SpecialActionBlockEffect) airUnitEffect;
                if (specialActionBlockEffect2.a(actionId) && specialActionBlockEffect2.effectId > i) {
                    i = specialActionBlockEffect2.effectId;
                    specialActionBlockEffect = specialActionBlockEffect2;
                }
            }
        }
        if (specialActionBlockEffect == null) {
            return null;
        }
        return specialActionBlockEffect;
    }

    @Override // com.corrodinggames.rts.game.units.g.AirUnitEffect
    public void a(OrderableUnit orderableUnit, GameOutputStream gameOutputStream) throws IOException {
        ActionId.serialize(gameOutputStream, this.blockedAction);
        gameOutputStream.writeInt(this.startTime);
        super.a(orderableUnit, gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.g.AirUnitEffect
    public void a(OrderableUnit orderableUnit, GameInputStream gameInputStream) throws IOException {
        this.blockedAction = ActionId.deserialize(gameInputStream);
        if (this.blockedAction == null) {
            this.blockedAction = ActionId.NONE;
        }
        this.startTime = gameInputStream.readInt();
        super.a(orderableUnit, gameInputStream);
    }

    public static void c(BaseUnit baseUnit, ActionId actionId) {
        FastArrayList fastArrayList;
        if (!(baseUnit instanceof OrderableUnit) || (fastArrayList = ((OrderableUnit) baseUnit).activeStatusEffects) == null) {
            return;
        }
        int i = GameEngine.getInstance().gameTimeMillis;
        Object[] objArrA = fastArrayList.a();
        for (int i2 = fastArrayList.size - 1; i2 >= 0; i2--) {
            AirUnitEffect airUnitEffect = (AirUnitEffect) objArrA[i2];
            if (airUnitEffect instanceof SpecialActionBlockEffect) {
                SpecialActionBlockEffect specialActionBlockEffect = (SpecialActionBlockEffect) airUnitEffect;
                if (actionId == ActionId.NONE || specialActionBlockEffect.a(actionId)) {
                    specialActionBlockEffect.effectId = i - 1;
                }
            }
        }
    }
}
