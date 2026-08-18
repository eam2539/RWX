package com.corrodinggames.rts.game.units.custom.hooks;

import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.b.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/b/l.class */
public class RandomMovementHook extends CustomUnitRenderHook {
    LogicBoolean a;
    float b;
    float c;
    int d;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile) {
        if (iniFile.isSectionNotEmpty("movement_random")) {
            RandomMovementHook randomMovementHook = new RandomMovementHook();
            randomMovementHook.a(customUnitConfig, iniFile, "movement_random", "movement_random");
            if (!LogicBoolean.isStaticFalse(randomMovementHook.a)) {
                customUnitConfig.a(randomMovementHook);
            }
        }
    }

    public void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2) {
        this.a = iniFile.getLogicBoolean(customUnitConfig, str, "enabled");
        this.b = iniFile.getFloatStrictRaw(str, "speed");
        this.c = iniFile.getFloat(str, "maxSpeed", Float.valueOf(5.0f)).floatValue();
        this.d = iniFile.getInt(str, "awayFromEdge", (Integer) 75).intValue();
    }

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void b(CustomUnit customUnit, float f) {
        if (!this.a.read(customUnit)) {
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (customUnit.isSlidingMovement()) {
            if (Utility.abs(customUnit.velocityX) < this.c) {
                customUnit.velocityX += Utility.clamp(customUnit, -this.b, this.b, 1);
            }
            if (Utility.abs(customUnit.velocityY) < this.c) {
                customUnit.velocityY += Utility.clamp(customUnit, -this.b, this.b, 2);
            }
        } else {
            if (Utility.abs(customUnit.rotation) < this.c) {
                customUnit.rotation += Utility.clamp(customUnit, -this.b, this.b, 1);
            }
            customUnit.rotationSpeed += Utility.clamp(customUnit, -1.0f, 1.0f, 2);
        }
        if (this.d > 0) {
            if (customUnit.posY > gameEngine.tileMap.getWorldHeight() - this.d) {
                customUnit.velocityY -= Utility.clamp(customUnit, 0.0f, this.b * 0.25f, 10);
            }
            if (customUnit.posY < this.d) {
                customUnit.velocityY += Utility.clamp(customUnit, 0.0f, this.b * 0.25f, 11);
            }
            if (customUnit.posX > gameEngine.tileMap.getWorldWidth() - this.d) {
                customUnit.velocityX -= Utility.clamp(customUnit, 0.0f, this.b * 0.25f, 12);
            }
            if (customUnit.posX < this.d) {
                customUnit.velocityX += Utility.clamp(customUnit, 0.0f, this.b * 0.25f, 13);
            }
        }
        customUnit.movementActiveThisFrame = true;
    }
}
