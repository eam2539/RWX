package com.corrodinggames.rts.game.units.custom.logic.actions;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.UnitSpawner;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/k.class */
public class SpawnUnitAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    public UnitSpawner produceUnits;

    /* JADX INFO: renamed from: b */
    public UnitSpawner spawnUnits;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) throws ConfigParseException {
        UnitSpawner unitSpawnerA = UnitSpawner.a(customUnitConfig, iniFile, str, str2 + "produceUnits");
        if (!unitSpawnerA.b()) {
            SpawnUnitAction spawnUnitAction = new SpawnUnitAction();
            spawnUnitAction.produceUnits = unitSpawnerA;
            customActionDef.logicActions.add(spawnUnitAction);
        }
        UnitSpawner unitSpawnerA2 = UnitSpawner.a(customUnitConfig, iniFile, str, str2 + "spawnUnits");
        if (!unitSpawnerA2.b()) {
            SpawnUnitAction spawnUnitAction2 = new SpawnUnitAction();
            spawnUnitAction2.spawnUnits = unitSpawnerA2;
            customActionDef.logicActions.add(spawnUnitAction2);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        if (this.produceUnits != null) {
            FastArrayList<BaseUnit> fastArrayList = new FastArrayList();
            this.produceUnits.a(fastArrayList, customUnit.team, (BaseUnit) customUnit, false);
            for (BaseUnit baseUnit2 : fastArrayList) {
                customUnit.ejectUnit(baseUnit2);
                customUnit.F(baseUnit2);
            }
        }
        if (this.spawnUnits != null) {
            this.spawnUnits.a(customUnit.posX, customUnit.posY, customUnit.posZ, customUnit.rotationSpeed, customUnit.team, false, customUnit);
            return true;
        }
        return true;
    }
}
