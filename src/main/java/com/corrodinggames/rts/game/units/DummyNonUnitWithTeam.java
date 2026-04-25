package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.PlayerTeam;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/t.class */
public class DummyNonUnitWithTeam extends DummyUnit {
    public static DummyNonUnitWithTeam a(PlayerTeam playerTeam) {
        DummyNonUnitWithTeam dummyNonUnitWithTeam = new DummyNonUnitWithTeam(true);
        dummyNonUnitWithTeam.setUnitTeam(playerTeam);
        dummyNonUnitWithTeam.isDestroyed = true;
        return dummyNonUnitWithTeam;
    }

    DummyNonUnitWithTeam(boolean z) {
        super(z);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public UnitType r() {
        return UnitTypeEnum.dummyNonUnitWithTeam;
    }

    public static void b() {
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: c */
    public String getUnitShortName() {
        String str = r().getUnitTypeDescriptionShort() + "(pos:" + ((int) this.posX) + "," + ((int) this.posY);
        if (this.team != null) {
            str = str + " t:" + this.team.teamId;
        }
        return str + ")";
    }
}
