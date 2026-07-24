package com.corrodinggames.rts.game.units.custom.logic.actions;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.UnitEventType;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/i.class */
public class SendMessageAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    VariableScope.MemoryWriter sendMessageWithData;

    /* JADX INFO: renamed from: b */
    LogicBoolean sendMessageTo;

    /* JADX INFO: renamed from: c */
    AnimationSet sendMessageWithTags;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) {
        LogicBoolean logicBoolean = iniFile.getInt(customUnitConfig, str, str2 + "sendMessageTo", null);
        VariableScope.MemoryWriter memoryWriterCreateGenericKeyValueWriter = null;
        String string = iniFile.getString(str, str2 + "sendMessageWithData", (String) null);
        if (string != null) {
            memoryWriterCreateGenericKeyValueWriter = VariableScope.createGenericKeyValueWriter(string, customUnitConfig, str, str2 + "sendMessageWithData");
        }
        AnimationSet animationSet = iniFile.getAnimationSet(customUnitConfig, str, str2 + "sendMessageWithTags", (AnimationSet) null);
        if (logicBoolean != null) {
            SendMessageAction sendMessageAction = new SendMessageAction();
            sendMessageAction.sendMessageTo = logicBoolean;
            sendMessageAction.sendMessageWithData = memoryWriterCreateGenericKeyValueWriter;
            sendMessageAction.sendMessageWithTags = animationSet;
            customActionDef.logicActions.add(sendMessageAction);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        BaseUnit unit;
        if (this.sendMessageTo != null && (unit = this.sendMessageTo.readUnit(customUnit)) != null) {
            VariableScope variableScope = null;
            if (this.sendMessageWithData != null) {
                variableScope = new VariableScope();
                this.sendMessageWithData.writeToMemory(variableScope, customUnit);
            }
            unit.a(UnitEventType.newMessage, customUnit, this.sendMessageWithTags, variableScope);
            return true;
        }
        return true;
    }
}
