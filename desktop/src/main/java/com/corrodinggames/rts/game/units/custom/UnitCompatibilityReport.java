package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.mod.ModInfo;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.ab */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/ab.class */
public class UnitCompatibilityReport {

    /* JADX INFO: renamed from: a */
    String modIdentifier;

    /* JADX INFO: renamed from: b */
    String unitName;

    /* JADX INFO: renamed from: c */
    int serverChecksum;

    /* JADX INFO: renamed from: d */
    int clientChecksum;

    /* JADX INFO: renamed from: e */
    String clientUnitConfig;

    /* JADX INFO: renamed from: f */
    CustomUnitConfig serverUnitConfig;

    /* JADX INFO: renamed from: a */
    public String generateErrorMessage() {
        String str = "from internal units";
        if (this.modIdentifier != null) {
            str = "from mod:'" + this.modIdentifier + "'";
        }
        ModInfo modByName = GameEngine.getInstance().modManager.getModByName(this.modIdentifier);
        if (modByName != null) {
            if (!modByName.isEnabled()) {
                str = str + " (You seem to have this mod but it is not enabled)";
            } else {
                str = str + " (You seem to have this mod but it might be a different version)";
            }
        }
        String str2 = VariableScope.nullOrMissingString;
        if (this.serverUnitConfig != null && this.clientUnitConfig != null) {
            if (this.serverUnitConfig.overrideAndReplace == null) {
                str2 = " (Extra debug not enabled)";
            } else {
                String[] strArrSplit = this.clientUnitConfig.split("\n");
                String[] strArrSplit2 = this.serverUnitConfig.overrideAndReplace.split("\n");
                int iMin = Utility.min(strArrSplit.length, strArrSplit2.length);
                if (strArrSplit.length != strArrSplit2.length) {
                    str2 = str2 + "Line length difference: " + strArrSplit.length + " vs " + strArrSplit2.length;
                }
                int i = 0;
                while (true) {
                    if (i >= iMin) {
                        break;
                    }
                    if (strArrSplit[i].equals(strArrSplit2[i])) {
                        i++;
                    } else {
                        str2 = str2 + "Difference on line " + i + ": '" + strArrSplit[i] + "' vs '" + strArrSplit2[i] + "'";
                        break;
                    }
                }
            }
        }
        if (this.clientChecksum == -1) {
            return "The server requires the unit:" + this.unitName + " that was not found " + str + str2;
        }
        return "Found unit:" + this.unitName + " but it does not match the server's copy " + str + str2 + " (checksum c:" + this.clientChecksum + " s:" + this.serverChecksum + ")";
    }
}
