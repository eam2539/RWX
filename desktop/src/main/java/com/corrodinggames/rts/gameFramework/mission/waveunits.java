package com.corrodinggames.rts.gameFramework.mission;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.ai.AIController;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/i.class */
class waveunits {
    boolean a;
    FastArrayList<WaveUnitEntry> b = new FastArrayList();
    final /* synthetic */ MissionEngine c;

    waveunits(MissionEngine missionEngine) {
        this.c = missionEngine;
    }

    public void a(UnitType unitType, int i) {
        UnitType unitTypeC = CustomUnitConfig.c(unitType);
        if (unitTypeC != null) {
            unitType = unitTypeC;
        }
        b(unitType, i);
    }

    public void b(UnitType unitType, int i) {
        for (WaveUnitEntry waveUnitEntry : this.b) {
            if (waveUnitEntry.a == unitType) {
                waveUnitEntry.b += i;
                return;
            }
        }
        WaveUnitEntry waveUnitEntry2 = new WaveUnitEntry(this);
        waveUnitEntry2.a = unitType;
        waveUnitEntry2.b = i;
        this.b.add(waveUnitEntry2);
    }

    public void a(float f, float f2) {
        GameEngine gameEngine = GameEngine.getInstance();
        int i = 0;
        PlayerTeam playerTeamK = PlayerTeam.k(1);
        if (playerTeamK == null) {
            GameEngine.log("Warning: Creating missing wave team AI");
            playerTeamK = new AIController(1);
            playerTeamK.teamColorId = 100;
            playerTeamK.isTeamObserver = true;
        }
        for (WaveUnitEntry waveUnitEntry : this.b) {
            for (int i2 = 0; i2 < waveUnitEntry.b; i2++) {
                BaseUnit baseUnitA = waveUnitEntry.a.a();
                baseUnitA.posX = f + Utility.getDeterministicRandomIntInRange(-85, 85, i + 0);
                baseUnitA.posY = f2 + Utility.getDeterministicRandomIntInRange(-85, 85, i + 1);
                baseUnitA.rotationSpeed = Utility.getDeterministicRandomIntInRange(-180, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT, i + 2);
                i += 3;
                baseUnitA.setUnitTeam(playerTeamK);
                if (baseUnitA.posX < 0.0f) {
                    baseUnitA.posX = 0.0f;
                }
                if (baseUnitA.posY < 0.0f) {
                    baseUnitA.posY = 0.0f;
                }
                if (baseUnitA.posX > gameEngine.tileMap.getWorldWidth()) {
                    baseUnitA.posX = gameEngine.tileMap.getWorldWidth();
                }
                if (baseUnitA.posY > gameEngine.tileMap.getWorldHeight()) {
                    baseUnitA.posY = gameEngine.tileMap.getWorldHeight();
                }
                if (i2 == 0) {
                    gameEngine.minimap.addGraphicsOperation(baseUnitA);
                }
            }
        }
    }

    public String toString() {
        if (this.b.size() == 0) {
            return "No units";
        }
        String str = VariableScope.nullOrMissingString;
        boolean z = true;
        for (WaveUnitEntry waveUnitEntry : this.b) {
            if (!z) {
                str = str + ", ";
            }
            z = false;
            str = (str + waveUnitEntry.b + "x ") + waveUnitEntry.a.getUnitName();
        }
        return str;
    }
}
