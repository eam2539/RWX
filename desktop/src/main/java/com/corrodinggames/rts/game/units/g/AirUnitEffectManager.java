package com.corrodinggames.rts.game.units.g;

import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.g.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/g/c.class */
public final class AirUnitEffectManager {
    public static void a(OrderableUnit orderableUnit, float f) {
        FastArrayList fastArrayList = orderableUnit.activeStatusEffects;
        if (fastArrayList == null) {
            return;
        }
        int i = GameEngine.getInstance().lastTick;
        Object[] objArrA = fastArrayList.a();
        for (int i2 = fastArrayList.size - 1; i2 >= 0; i2--) {
            AirUnitEffect airUnitEffect = (AirUnitEffect) objArrA[i2];
            if (airUnitEffect.effectId <= i) {
                fastArrayList.remove(i2);
            } else {
                airUnitEffect.a(orderableUnit, f);
            }
        }
    }

    public static void a(OrderableUnit orderableUnit, AirUnitEffect airUnitEffect) {
        if (orderableUnit.activeStatusEffects == null) {
            orderableUnit.activeStatusEffects = new FastArrayList();
        }
        if (orderableUnit.activeStatusEffects.size() > 1000) {
            orderableUnit.a("status effect limit reached");
        } else {
            orderableUnit.activeStatusEffects.add(airUnitEffect);
        }
    }

    public static void a(OrderableUnit orderableUnit, GameOutputStream gameOutputStream) throws IOException {
        int size;
        FastArrayList fastArrayList = orderableUnit.activeStatusEffects;
        if (fastArrayList == null) {
            size = 0;
        } else {
            size = fastArrayList.size();
        }
        gameOutputStream.writeShort((short) size);
        if (size == 0) {
            return;
        }
        gameOutputStream.startBlock("s");
        Object[] objArrA = fastArrayList.a();
        for (int i = 0; i < fastArrayList.size; i++) {
            AirUnitEffect airUnitEffect = (AirUnitEffect) objArrA[i];
            gameOutputStream.writeEnumOrdinal(airUnitEffect.b());
            airUnitEffect.a(orderableUnit, gameOutputStream);
        }
        gameOutputStream.endBlock("s");
    }

    public static void a(OrderableUnit orderableUnit, GameInputStream gameInputStream) throws IOException {
        short shortValue = gameInputStream.readShortValue();
        if (shortValue <= 0) {
            orderableUnit.activeStatusEffects = null;
            return;
        }
        if (orderableUnit.activeStatusEffects == null) {
            orderableUnit.activeStatusEffects = new FastArrayList();
        } else {
            orderableUnit.activeStatusEffects.clear();
        }
        FastArrayList fastArrayList = orderableUnit.activeStatusEffects;
        gameInputStream.startBlockNamed("s");
        for (int i = 0; i < shortValue; i++) {
            AirUnitEffectType airUnitEffectType = (AirUnitEffectType) gameInputStream.readEnumOrdinalOrNull(AirUnitEffectType.class);
            if (airUnitEffectType == null) {
                throw new IOException("Unknown status effect type");
            }
            AirUnitEffect airUnitEffectA = airUnitEffectType.a();
            airUnitEffectA.a(orderableUnit, gameInputStream);
            fastArrayList.add(airUnitEffectA);
        }
        gameInputStream.d("s");
    }
}
