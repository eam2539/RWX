package com.corrodinggames.rts.game.units.custom.logic.actions;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import io.github.rwx.geometry.PointF;

import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/h.class */
public class MemoryAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    VariableScope.CachedWriter setUnitMemory;

    /* JADX INFO: renamed from: b */
    boolean swapCustomTarget1And2;

    /* JADX INFO: renamed from: c */
    LogicBoolean setCustomTarget1;

    /* JADX INFO: renamed from: d */
    LogicBoolean setCustomTarget2;

    /* JADX INFO: renamed from: e */
    VariableScope.MemoryNames shrinkArrays;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) throws ConfigParseException {
        boolean zBooleanValue = iniFile.getBoolean(str, str2 + "swapCustomTarget1And2", (Boolean) false).booleanValue();
        LogicBoolean logicBoolean = iniFile.getInt(customUnitConfig, str, str2 + "setCustomTarget1", null);
        LogicBoolean logicBoolean2 = iniFile.getInt(customUnitConfig, str, str2 + "setCustomTarget2", null);
        VariableScope.MemoryWriter memoryWriterCreateMemoryWriter = null;
        String string = iniFile.getString(str, str2 + "setUnitMemory", (String) null);
        if (string != null) {
            memoryWriterCreateMemoryWriter = VariableScope.createMemoryWriter(string, customUnitConfig, str, str2 + "setUnitMemory");
        }
        String string2 = iniFile.getString(str, str2 + "shrinkArrays", (String) null);
        VariableScope.MemoryNames memoryNamesCreateMemoryNameList = null;
        if (string2 != null) {
            memoryNamesCreateMemoryNameList = VariableScope.createMemoryNameList(string2, customUnitConfig, null, str, str2 + "shrinkArrays");
            for (VariableScope.VariableName variableName : memoryNamesCreateMemoryNameList.names) {
                VariableScope.VariableDefinition variableDefinition = customUnitConfig.variableMapping.get(variableName);
                if (variableDefinition == null) {
                    throw new ConfigParseException("Failed to find defined memory: " + variableName, str, str2 + "shrinkArrays");
                }
                if (!LogicBoolean.ReturnType.isArrayType(variableDefinition.type)) {
                    throw new ConfigParseException("Memory: " + variableName + " is type: " + variableDefinition.type + " expected an array type", str, str2 + "shrinkArrays");
                }
                if (variableDefinition.type != LogicBoolean.ReturnType.numberArray && variableDefinition.type != LogicBoolean.ReturnType.unitArray) {
                    throw new ConfigParseException("Memory: " + variableName + " is type: " + variableDefinition.type + " only number and unit arrays are supported by shrinkArrays", str, str2 + "shrinkArrays");
                }
            }
        }
        if (zBooleanValue || logicBoolean != null || logicBoolean2 != null || memoryWriterCreateMemoryWriter != null || memoryNamesCreateMemoryNameList != null) {
            MemoryAction memoryAction = new MemoryAction();
            memoryAction.setUnitMemory = memoryWriterCreateMemoryWriter;
            memoryAction.swapCustomTarget1And2 = zBooleanValue;
            memoryAction.setCustomTarget1 = logicBoolean;
            memoryAction.setCustomTarget2 = logicBoolean2;
            memoryAction.shrinkArrays = memoryNamesCreateMemoryNameList;
            customActionDef.logicActions.add(memoryAction);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        if (this.setUnitMemory != null) {
            this.setUnitMemory.writeToUnit(customUnit);
        }
        if (this.swapCustomTarget1And2) {
            BaseUnit baseUnit2 = customUnit.unitTarget2;
            customUnit.unitTarget2 = customUnit.unitTarget3;
            customUnit.unitTarget3 = baseUnit2;
        }
        if (this.setCustomTarget1 != null) {
            customUnit.unitTarget2 = VariableScope.getSafeUnitReferenceForStorage(this.setCustomTarget1.readUnit(customUnit));
        }
        if (this.setCustomTarget2 != null) {
            customUnit.unitTarget3 = VariableScope.getSafeUnitReferenceForStorage(this.setCustomTarget2.readUnit(customUnit));
        }
        if (this.shrinkArrays != null) {
            a(customUnit, this.shrinkArrays);
            return true;
        }
        return true;
    }

    public static void a(CustomUnit customUnit, VariableScope.MemoryNames memoryNames) {
        if (customUnit.unitVariables == null) {
            return;
        }
        Iterator it = memoryNames.names.iterator();
        while (it.hasNext()) {
            VariableScope.VariableData dataObjectRaw = customUnit.unitVariables.getDataObjectRaw((VariableScope.VariableName) it.next());
            if (dataObjectRaw != null && (dataObjectRaw instanceof VariableScope.VariableDataArray)) {
                ((VariableScope.VariableDataArray) dataObjectRaw).shrink();
            }
        }
    }
}
