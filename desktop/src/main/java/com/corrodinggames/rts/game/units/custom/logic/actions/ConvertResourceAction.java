package com.corrodinggames.rts.game.units.custom.logic.actions;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/g.class */
public class ConvertResourceAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    Resource convertResourceFrom;

    /* JADX INFO: renamed from: b */
    Resource convertResourceTo;

    /* JADX INFO: renamed from: c */
    double convertResourceMinAmount;

    /* JADX INFO: renamed from: d */
    double convertResourceMaxAmount;

    /* JADX INFO: renamed from: e */
    float convertResourceMultiplyAmountBy;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) throws ConfigParseException {
        Resource attachmentData = iniFile.getAttachmentData(customUnitConfig, str, "convertResource_from", (Resource) null, true);
        Resource attachmentData2 = iniFile.getAttachmentData(customUnitConfig, str, "convertResource_to", (Resource) null, true);
        if ((attachmentData != null || attachmentData2 != null) && (attachmentData == null || attachmentData2 == null)) {
            throw new ConfigParseException("[" + str + "] Both convertResource_from and convertResource_to are required together");
        }
        if (attachmentData != null && attachmentData2 != null) {
            ConvertResourceAction convertResourceAction = new ConvertResourceAction();
            convertResourceAction.convertResourceFrom = attachmentData;
            convertResourceAction.convertResourceTo = attachmentData2;
            convertResourceAction.convertResourceMinAmount = iniFile.getDouble(str, "convertResource_minAmount", 0.0d);
            convertResourceAction.convertResourceMaxAmount = iniFile.getDoubleStrictRaw(str, "convertResource_maxAmount");
            if (convertResourceAction.convertResourceMinAmount < 0.0d) {
                throw new ConfigParseException("[" + str + "] convertResource_minAmount cannot be < 0");
            }
            if (convertResourceAction.convertResourceMaxAmount < 0.0d) {
                throw new ConfigParseException("[" + str + "] convertResource_maxAmount cannot be < 0");
            }
            if (convertResourceAction.convertResourceMaxAmount < convertResourceAction.convertResourceMinAmount) {
                throw new ConfigParseException("[" + str + "] convertResource_maxAmount cannot be < convertResource_minAmount");
            }
            convertResourceAction.convertResourceMultiplyAmountBy = iniFile.getFloat(str, "convertResource_multiplyAmountBy", Float.valueOf(1.0f)).floatValue();
            customActionDef.ac.add(convertResourceAction);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        double dA = this.convertResourceFrom.a(customUnit);
        if (dA > this.convertResourceMinAmount) {
            double dMin = Utility.min(dA, this.convertResourceMaxAmount);
            this.convertResourceFrom.b(customUnit, -dMin);
            this.convertResourceTo.b(customUnit, dMin * ((double) this.convertResourceMultiplyAmountBy));
            return true;
        }
        return true;
    }
}
