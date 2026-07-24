package com.corrodinggames.rts.game.units.custom.logic.actions;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.game.units.custom.price.DynamicResourcePrice;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/a.class */
public class ResourceAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    Resource resourceAmount;

    /* JADX INFO: renamed from: c */
    Resource addResourceAmount;

    /* JADX INFO: renamed from: e */
    DynamicResourcePrice addResourcesWithLogic;

    /* JADX INFO: renamed from: f */
    DynamicResourcePrice setResourcesWithLogic;

    /* JADX INFO: renamed from: b */
    double resourceSetValue = -1.7976931348623157E308d;

    /* JADX INFO: renamed from: d */
    float resourceMultiplyBy = 1.0f;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) throws ConfigParseException {
        Resource attachmentData = iniFile.getAttachmentData(customUnitConfig, str, str2 + "resourceAmount", (Resource) null, true);
        if (attachmentData != null) {
            ResourceAction resourceAction = new ResourceAction();
            resourceAction.resourceAmount = attachmentData;
            resourceAction.resourceSetValue = iniFile.getDouble(str, str2 + "resourceAmount_setValue", -1.7976931348623157E308d);
            resourceAction.addResourceAmount = iniFile.getAttachmentData(customUnitConfig, str, str2 + "resourceAmount_addOtherResource", (Resource) null, true);
            resourceAction.resourceMultiplyBy = iniFile.getFloat(str, str2 + "resourceAmount_multiplyBy", Float.valueOf(1.0f)).floatValue();
            customActionDef.logicActions.add(resourceAction);
        }
        DynamicResourcePrice dynamicResourcePriceA = DynamicResourcePrice.a(customUnitConfig, iniFile, str, str2 + "addResourcesWithLogic", null);
        DynamicResourcePrice dynamicResourcePriceA2 = DynamicResourcePrice.a(customUnitConfig, iniFile, str, str2 + "setResourcesWithLogic", null);
        if (dynamicResourcePriceA != null || dynamicResourcePriceA2 != null) {
            ResourceAction resourceAction2 = new ResourceAction();
            resourceAction2.setResourcesWithLogic = dynamicResourcePriceA2;
            resourceAction2.addResourcesWithLogic = dynamicResourcePriceA;
            customActionDef.logicActions.add(resourceAction2);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        double dA;
        if (this.resourceAmount != null) {
            if (this.resourceSetValue != -1.7976931348623157E308d) {
                dA = this.resourceSetValue;
            } else {
                dA = this.resourceAmount.a(customUnit);
            }
            if (this.addResourceAmount != null) {
                dA += this.addResourceAmount.a(customUnit);
            }
            this.resourceAmount.a(customUnit, dA * ((double) this.resourceMultiplyBy));
        }
        if (this.setResourcesWithLogic != null) {
            this.setResourcesWithLogic.d(customUnit);
        }
        if (this.addResourcesWithLogic != null) {
            this.addResourcesWithLogic.e(customUnit);
            return true;
        }
        return true;
    }
}
