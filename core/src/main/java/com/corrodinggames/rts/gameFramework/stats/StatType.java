package com.corrodinggames.rts.gameFramework.stats;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.g.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/g/f.class */
public enum StatType {
    none,
    income,
    armyValue,
    buildingValue,
    totalValue,
    credits;

    /* JADX INFO: renamed from: a */
    public int calculate(final PlayerTeam n) {
        switch (this) {
            default:
            case none:
                return 0;
            case income: {
                int scaledIncomeRate = n.getScaledIncomeRate();
                for (final Resource a : Resource.f()) {
                    if (a.d()) {
                        final float b = a.b();
                        if (b == 0.0f) {
                            continue;
                        }
                        scaledIncomeRate += (int) (b * n.b(a));
                    }
                }
                return scaledIncomeRate;
            }
            case armyValue: {
                return n.teamStatistics.n;
            }
            case buildingValue: {
                return n.teamStatistics.o;
            }
            case totalValue: {
                return n.teamStatistics.n + n.teamStatistics.o;
            }
            case credits: {
                return (int) n.credits;
            }
        }
    }
}
