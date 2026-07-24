package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.DummyNonUnitWithTeam;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.CustomUnitEventData;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope.class */
public class VariableScope {
    public static final String nullOrMissingString = "";
    VariableName[] variableNames = emptyNames;
    VariableData[] variableData = emptyData;
    public static final VariableScope emptyVariableScope = new EmptyVariableScope();
    static final VariableName[] emptyNames = new VariableName[0];
    static final VariableData[] emptyData = new VariableData[0];
    public static final VariableDataNull variableDataNull = new VariableDataNull();

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$MemoryNames.class */
    public static class MemoryNames {
        public FastArrayList<VariableScope.VariableName> names = new FastArrayList();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDefinition.class */
    public static class VariableDefinition {
        public VariableName name;
        public LogicBoolean.ReturnType type;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$EmptyVariableScope.class */
    public static class EmptyVariableScope extends VariableScope {
        EmptyVariableScope() {
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope
        public void setDataRaw(VariableName variableName, VariableData variableData) {
            throw new RuntimeException("Not allowed");
        }
    }

    public boolean isEmpty() {
        for (VariableData variableDatum : this.variableData) {
            if (variableDatum != null) {
                return false;
            }
        }
        return true;
    }

    public String debugMemory(boolean z, boolean z2) {
        String str = nullOrMissingString;
        for (int i = 0; i < this.variableData.length; i++) {
            VariableData variableData = this.variableData[i];
            if (variableData != null) {
                String str2 = str + this.variableNames[i].id + "=" + variableData.valueToStringDebug(null);
                if (z2) {
                    str2 = str2 + " (" + variableData.getReturnType().name() + ")";
                }
                if (z) {
                    str = str2 + "\n";
                } else {
                    str = str2 + "|";
                }
            }
        }
        return str;
    }

    public VariableData getDataObjectRaw(VariableName variableName) {
        for (int i = 0; i < this.variableData.length; i++) {
            if (this.variableNames[i] == variableName) {
                return this.variableData[i];
            }
        }
        return variableDataNull;
    }

    public void setArrayDataRaw(VariableName variableName, VariableData variableData, int i) {
        VariableDataArray variableDataUnitArray = null;
        LogicBoolean.ReturnType returnType = LogicBoolean.ReturnType.undefined;
        if (variableData != null) {
            returnType = variableData.getReturnType();
        }
        for (int i2 = 0; i2 < this.variableData.length; i2++) {
            if (this.variableNames[i2] == variableName && (this.variableData[i2] instanceof VariableDataArray)) {
                VariableDataArray variableDataArray = (VariableDataArray) this.variableData[i2];
                if (returnType == LogicBoolean.ReturnType.undefined || variableDataArray.getElementReturnType() == returnType) {
                    variableDataUnitArray = variableDataArray;
                }
            }
        }
        if (variableDataUnitArray == null && variableData == null) {
            return;
        }
        if (variableDataUnitArray == null) {
            if (returnType == LogicBoolean.ReturnType.number) {
                variableDataUnitArray = new VariableDataNumberArray();
            } else if (returnType == LogicBoolean.ReturnType.bool) {
                variableDataUnitArray = new VariableDataBoolArray();
            } else if (returnType == LogicBoolean.ReturnType.unit) {
                variableDataUnitArray = new VariableDataUnitArray();
            } else {
                GameEngine.logColored("Unhandled array type: " + returnType);
                return;
            }
            setDataRaw(variableName, variableDataUnitArray);
        }
        variableDataUnitArray.setDataAtIndex(variableData, i);
    }

    public void setDataRaw(VariableName variableName, VariableData variableData) {
        if (variableData == null) {
            variableData = variableDataNull;
        }
        for (int i = 0; i < this.variableData.length; i++) {
            if (this.variableNames[i] == variableName) {
                this.variableData[i] = variableData;
                return;
            }
        }
        VariableName[] variableNameArr = new VariableName[this.variableData.length + 1];
        VariableData[] variableDataArr = new VariableData[this.variableData.length + 1];
        for (int i2 = 0; i2 < this.variableData.length; i2++) {
            variableDataArr[i2] = this.variableData[i2];
            variableNameArr[i2] = this.variableNames[i2];
        }
        variableDataArr[variableDataArr.length - 1] = variableData;
        variableNameArr[variableNameArr.length - 1] = variableName;
        this.variableData = variableDataArr;
        this.variableNames = variableNameArr;
    }

    public void clearAllData() {
        this.variableData = emptyData;
        this.variableNames = emptyNames;
    }

    public void setUnit(VariableDefinition variableDefinition, BaseUnit baseUnit) {
        if (variableDefinition.type != LogicBoolean.ReturnType.unit) {
        }
        setDataRaw(variableDefinition.name, new VariableDataUnit(baseUnit));
    }

    BaseUnit getUnit(VariableName variableName) {
        return getDataObjectRaw(variableName).readUnit(null);
    }

    LogicBoolean getAsLogicBoolean(VariableName variableName) {
        return getDataObjectRaw(variableName);
    }

    public void setFromLogicBoolean(VariableName variableName, OrderableUnit orderableUnit, LogicBoolean logicBoolean, LogicBoolean logicBoolean2) {
        VariableData variableDataString = null;
        if (logicBoolean != null) {
            LogicBoolean.ReturnType returnType = logicBoolean.getReturnType();
            if (returnType == LogicBoolean.ReturnType.bool) {
                variableDataString = new VariableDataBoolean(logicBoolean.read(orderableUnit));
            } else if (returnType == LogicBoolean.ReturnType.unit) {
                variableDataString = new VariableDataUnit(getSafeUnitReferenceForStorage(logicBoolean.readUnit(orderableUnit)));
            } else if (returnType == LogicBoolean.ReturnType.number) {
                variableDataString = new VariableDataNumber(logicBoolean.readNumber(orderableUnit));
            } else if (returnType == LogicBoolean.ReturnType.string) {
                variableDataString = new VariableDataString(logicBoolean.readString(orderableUnit));
            }
        }
        if (logicBoolean2 != null) {
            setArrayDataRaw(variableName, variableDataString, (int) logicBoolean2.readNumber(orderableUnit));
        } else {
            setDataRaw(variableName, variableDataString);
        }
    }

    double getNumber(VariableName variableName) {
        return getDataObjectRaw(variableName).readNumber(null);
    }

    String getString(VariableName variableName) {
        return getDataObjectRaw(variableName).readString(null);
    }

    boolean getBoolean(VariableName variableName) {
        return getDataObjectRaw(variableName).read(null);
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableMapping.class */
    public static class VariableMapping {
        CustomUnitConfig meta;
        FastArrayList<VariableDefinition> mapping = new FastArrayList<>();

        public VariableDefinition create(String str, LogicBoolean.ReturnType returnType) {
            VariableName variableName = VariableName.get(str);
            if (get(variableName) != null) {
                throw new RuntimeException("A variable already exists with the name: " + str);
            }
            VariableDefinition variableDefinition = new VariableDefinition();
            variableDefinition.name = variableName;
            variableDefinition.type = returnType;
            this.mapping.add(variableDefinition);
            return variableDefinition;
        }

        public VariableDefinition get(String str) {
            VariableName variableName = VariableName.get(str.toLowerCase(Locale.ROOT).trim());
            for (VariableDefinition variableDefinition : this.mapping) {
                if (variableDefinition.name == variableName) {
                    return variableDefinition;
                }
            }
            return null;
        }

        public VariableDefinition get(VariableName variableName) {
            for (VariableDefinition variableDefinition : this.mapping) {
                if (variableDefinition.name == variableName) {
                    return variableDefinition;
                }
            }
            return null;
        }

        public boolean hasArrays() {
            for (Object o : this.mapping) {
                if (LogicBoolean.ReturnType.isArrayType(((VariableDefinition) o).type)) {
                    return true;
                }
            }
            return false;
        }

        public String getListOfPossibleNames() {
            String str = null;
            for (VariableDefinition variableDefinition : this.mapping) {
                if (str == null) {
                    str = VariableScope.nullOrMissingString + variableDefinition.name;
                } else {
                    str = str + ", " + variableDefinition.name;
                }
            }
            return str;
        }

        public void addDefineKey(IniFile iniFile, CustomUnitConfig customUnitConfig, String str, String str2, String str3) {
            String string = iniFile.getString(str2, str3, (String) null);
            if (string != null && !string.equals(VariableScope.nullOrMissingString)) {
                throw new RuntimeException("[" + str2 + "]" + str + ": Unexpected format");
            }
            defineVariablesRaw(str, str2, str3);
        }

        public void addDefineValue(CustomUnitConfig customUnitConfig, String str, String str2, String str3) {
            defineVariablesRaw(str2, str, str3);
        }

        public void defineVariables(CustomUnitConfig customUnitConfig, String str) {
            defineVariablesRaw("define", VariableScope.nullOrMissingString, str);
        }

        public void addSingleDefine(CustomUnitConfig customUnitConfig, String str, String str2, String str3, String str4) {
            String lowerCase = str2.trim().toLowerCase(Locale.ROOT);
            String strTrim = str.toLowerCase(Locale.ROOT).trim();
            LogicBoolean.ReturnType userType = VariableScope.getUserType(lowerCase);
            if (userType == null) {
                throw new RuntimeException("[" + str3 + "]" + str4 + ": Unknown type: " + lowerCase);
            }
            checkNameReserved(strTrim, str3, str4);
            VariableDefinition variableDefinition = get(strTrim);
            if (variableDefinition != null) {
                if (variableDefinition.type == userType) {
                } else {
                    throw new RuntimeException("[" + str3 + "]" + str4 + ": A memory variable already exists with the name: " + strTrim + " and is a different type: " + variableDefinition.type.name());
                }
            } else {
                create(strTrim, userType);
            }
        }

        public void defineVariablesRaw(String str, String str2, String str3) {
            for (String str4 : Utility.splitByChar(str3, ',')) {
                String strTrim = str4.trim();
                if (!strTrim.equals(VariableScope.nullOrMissingString)) {
                    int iIndexOf = strTrim.indexOf(" ");
                    if (iIndexOf == -1) {
                        throw new RuntimeException("[" + str2 + "]" + str + ": Expected 'type name' in each section, got: " + strTrim);
                    }
                    addSingleDefine(this.meta, strTrim.substring(iIndexOf, strTrim.length()).toLowerCase(Locale.ROOT).trim(), strTrim.substring(0, iIndexOf).toLowerCase(Locale.ROOT).trim(), str2, str);
                }
            }
        }

        public void checkNameReserved(String str, String str2, String str3) {
            boolean z = false;
            if (str.equals(VariableScope.nullOrMissingString)) {
                z = true;
            }
            if (str.equals("com/corrodinggames/rts/game") || str.equals("parent") || str.equals("self") || str.equals("this")) {
                z = true;
            }
            if (str.equals("boolean") || str.equals("bool") || str.equals("unit") || str.equals("void") || str.equals("null") || str.equals("number") || str.equals("float")) {
                z = true;
            }
            if (z) {
                throw new RuntimeException("[" + str2 + "]" + str3 + ": Variable cannot be named: '" + str + "'");
            }
            if (str.contains(".") || str.contains("=") || str.contains("(") || str.contains(")") || str.contains("'") || str.contains("\"") || str.contains("?") || str.contains("|") || str.contains("\\") || str.contains("/") || str.contains("[") || str.contains("]") || str.contains(":") || str.contains(";")) {
                throw new RuntimeException("[" + str2 + "]" + str3 + ": Variable name has reserved symbols: '" + str + "'");
            }
            if (str.contains(" ")) {
                throw new RuntimeException("[" + str2 + "]" + str3 + ": Variable name cannot have a space: '" + str + "'");
            }
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableName.class */
    public static class VariableName {
        private static HashMap existingVariableName = new HashMap();
        private String id;

        public static VariableName getExistingOrNull(String str) {
            synchronized (existingVariableName) {
                VariableName variableName = (VariableName) existingVariableName.get(str);
                if (variableName != null) {
                    return variableName;
                }
                return null;
            }
        }

        public static VariableName get(String str) {
            synchronized (existingVariableName) {
                VariableName variableName = (VariableName) existingVariableName.get(str);
                if (variableName != null) {
                    return variableName;
                }
                VariableName variableName2 = new VariableName();
                variableName2.id = str;
                existingVariableName.put(str, variableName2);
                return variableName2;
            }
        }

        public String getId() {
            return this.id;
        }

        public String toString() {
            return this.id;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableData.class */
    public static abstract class VariableData extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public abstract LogicBoolean.ReturnType getReturnType();

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "Data(" + valueToStringDebug(null) + ")";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return 0.0f;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return VariableScope.nullOrMissingString;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDataNull.class */
    public static class VariableDataNull extends VariableData {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.voidReturn;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "null";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDataUnit.class */
    public static class VariableDataUnit extends VariableData {
        BaseUnit unit;

        public VariableDataUnit(BaseUnit baseUnit) {
            this.unit = baseUnit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.unit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public BaseUnit readUnit(OrderableUnit orderableUnit) {
            return this.unit;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDataBoolean.class */
    public static class VariableDataBoolean extends VariableData {
        boolean bool;

        public VariableDataBoolean(boolean z) {
            this.bool = z;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.bool;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return this.bool;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDataString.class */
    public static class VariableDataString extends VariableData {
        String text;

        public VariableDataString(String str) {
            this.text = str;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.string;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return this.text;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDataNumber.class */
    public static class VariableDataNumber extends VariableData {
        double number;

        public VariableDataNumber(double d) {
            this.number = d;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.number;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return (float) this.number;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDataArray.class */
    public abstract static class VariableDataArray extends VariableData {
        int size;

        public abstract LogicBoolean.ReturnType getElementReturnType();

        public abstract void setDataAtIndex(VariableData variableData, int i);

        public abstract VariableData readDataAtIndex(int i);

        public abstract void shrink();

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.voidReturn;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public int getArraySize(OrderableUnit orderableUnit) {
            return this.size;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean readArrayElement(OrderableUnit orderableUnit, int i) {
            return readDataAtIndex(i);
        }

        public boolean readBooleanIndex(int i) {
            return false;
        }

        public float readNumberIndex(int i) {
            return 0.0f;
        }

        public BaseUnit readUnitIndex(int i) {
            return null;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDataNumberArray.class */
    public static class VariableDataNumberArray extends VariableDataArray {
        float[] dataArray;
        public static final boolean trace = false;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray, com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.numberArray;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public LogicBoolean.ReturnType getElementReturnType() {
            return LogicBoolean.ReturnType.number;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public float readNumberIndex(int i) {
            if (i < 0 || i >= this.size) {
                return 0.0f;
            }
            return this.dataArray[i];
        }

        public void setNumberIndex(int i, float f) {
            if (i < 0 || i > 10000) {
                return;
            }
            if (this.dataArray == null) {
                this.dataArray = new float[i + 1];
            }
            if (i >= this.dataArray.length) {
                int length = this.dataArray.length;
                int i2 = length + (length < 12 / 2 ? 12 : length >> 1);
                if (i2 < i + 1) {
                    i2 = i + 1;
                }
                float[] fArr = new float[i2];
                System.arraycopy(this.dataArray, 0, fArr, 0, length);
                this.dataArray = fArr;
            }
            if (this.size < i + 1) {
                this.size = i + 1;
                if (this.size > this.dataArray.length) {
                    throw new RuntimeException("size:" + this.size + ", dataArray.length:" + this.dataArray.length);
                }
            }
            this.dataArray[i] = f;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public void shrink() {
            int i = 0;
            while (i < this.size) {
                if (this.dataArray[i] == 0.0f) {
                    for (int i2 = i + 1; i2 < this.size; i2++) {
                        this.dataArray[i2 - 1] = this.dataArray[i2];
                    }
                    this.dataArray[this.size - 1] = 0.0f;
                    this.size--;
                    i--;
                }
                i++;
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public void setDataAtIndex(VariableData variableData, int i) {
            setNumberIndex(i, variableData.readNumber(null));
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public VariableData readDataAtIndex(int i) {
            return new VariableDataNumber(readNumberIndex(i));
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDataBoolArray.class */
    public static class VariableDataBoolArray extends VariableDataArray {
        boolean[] dataArray;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray, com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.boolArray;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public LogicBoolean.ReturnType getElementReturnType() {
            return LogicBoolean.ReturnType.bool;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public boolean readBooleanIndex(int i) {
            if (i < 0 || i >= this.size) {
                return false;
            }
            return this.dataArray[i];
        }

        public void setBooleanIndex(int i, boolean z) {
            if (i >= 0 && i <= 10000) {
                if (this.dataArray == null) {
                    this.dataArray = new boolean[i + 1];
                }
                if (i >= this.dataArray.length) {
                    int length = this.dataArray.length;
                    int i2 = length + (length < 12 / 2 ? 12 : length >> 1);
                    if (i2 < i + 1) {
                        i2 = i + 1;
                    }
                    boolean[] zArr = new boolean[i2];
                    System.arraycopy(this.dataArray, 0, zArr, 0, length);
                    this.dataArray = zArr;
                }
                if (this.size < i + 1) {
                    this.size = i + 1;
                    if (this.size > this.dataArray.length) {
                        throw new RuntimeException("size:" + this.size + ", dataArray.length:" + this.dataArray.length);
                    }
                }
                this.dataArray[i] = z;
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public void shrink() {
            int i = 0;
            while (i < this.size) {
                if (!this.dataArray[i]) {
                    for (int i2 = i + 1; i2 < this.size; i2++) {
                        this.dataArray[i2 - 1] = this.dataArray[i2];
                    }
                    this.dataArray[this.size - 1] = false;
                    this.size--;
                    i--;
                }
                i++;
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public void setDataAtIndex(VariableData variableData, int i) {
            setBooleanIndex(i, variableData.read(null));
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public VariableData readDataAtIndex(int i) {
            return new VariableDataBoolean(readBooleanIndex(i));
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$VariableDataUnitArray.class */
    public static class VariableDataUnitArray extends VariableDataArray {
        BaseUnit[] dataArray;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray, com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableData, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.unitArray;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public LogicBoolean.ReturnType getElementReturnType() {
            return LogicBoolean.ReturnType.unit;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public BaseUnit readUnitIndex(int i) {
            if (i < 0 || i >= this.size) {
                return null;
            }
            return this.dataArray[i];
        }

        public void setUnitIndex(int i, BaseUnit baseUnit) {
            if (i >= 0 && i <= 10000) {
                if (this.dataArray == null) {
                    this.dataArray = new BaseUnit[i + 1];
                }
                if (i >= this.dataArray.length) {
                    int length = this.dataArray.length;
                    int i2 = length + (length < 12 / 2 ? 12 : length >> 1);
                    if (i2 < i + 1) {
                        i2 = i + 1;
                    }
                    BaseUnit[] baseUnitArr = new BaseUnit[i2];
                    System.arraycopy(this.dataArray, 0, baseUnitArr, 0, length);
                    this.dataArray = baseUnitArr;
                }
                if (this.size < i + 1) {
                    this.size = i + 1;
                    if (this.size > this.dataArray.length) {
                        throw new RuntimeException("size:" + this.size + ", dataArray.length:" + this.dataArray.length);
                    }
                }
                this.dataArray[i] = baseUnit;
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public void setDataAtIndex(VariableData variableData, int i) {
            setUnitIndex(i, variableData.readUnit(null));
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public VariableData readDataAtIndex(int i) {
            return new VariableDataUnit(readUnitIndex(i));
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.VariableDataArray
        public void shrink() {
            int i = 0;
            while (i < this.size) {
                BaseUnit baseUnit = this.dataArray[i];
                if (baseUnit == null || (!VariableScope.isMarker(baseUnit) && baseUnit.isDead)) {
                    for (int i2 = i + 1; i2 < this.size; i2++) {
                        this.dataArray[i2 - 1] = this.dataArray[i2];
                    }
                    this.dataArray[this.size - 1] = null;
                    this.size--;
                    i--;
                }
                i++;
            }
        }
    }

    public static void writeOut(GameOutputStream gameOutputStream, VariableScope variableScope) throws IOException {
        if (variableScope == null) {
            gameOutputStream.writeByte(-2);
            return;
        }
        if (variableScope.variableData.length == 0) {
            gameOutputStream.writeByte(-1);
            return;
        }
        gameOutputStream.writeByte(0);
        gameOutputStream.writeShort((short) variableScope.variableData.length);
        int length = variableScope.variableData.length;
        for (int i = 0; i < length; i++) {
            VariableData variableData = variableScope.variableData[i];
            gameOutputStream.writeStringUTF(variableScope.variableNames[i].id);
            gameOutputStream.writeBoolean(false);
            if (0 == 0) {
                writeOutDynamicData(gameOutputStream, variableData);
            }
        }
    }

    public static VariableScope readIn(GameInputStream gameInputStream) throws IOException {
        byte b = gameInputStream.readByte();
        if (b == -2 || b == -1) {
            return null;
        }
        short shortValue = gameInputStream.readShortValue();
        VariableScope variableScope = new VariableScope();
        for (int i = 0; i < shortValue; i++) {
            VariableName variableName = VariableName.get(gameInputStream.readUTF());
            if (!gameInputStream.readBoolean()) {
                variableScope.setDataRaw(variableName, readInDynamicData(gameInputStream));
            }
        }
        return variableScope;
    }

    public static void writeOutUnitOrPlaceholder(GameOutputStream gameOutputStream, BaseUnit baseUnit) throws IOException {
        if (baseUnit instanceof DummyNonUnitWithTeam) {
            gameOutputStream.writeByte(1);
            gameOutputStream.writeFloat(baseUnit.posX);
            gameOutputStream.writeFloat(baseUnit.posY);
            gameOutputStream.writeFloat(baseUnit.posZ);
            gameOutputStream.writeFloat(baseUnit.rotationSpeed);
            gameOutputStream.writeTeamIdByte(baseUnit.team);
            return;
        }
        gameOutputStream.writeByte(0);
        gameOutputStream.writeUnitIdIfAlive(baseUnit);
    }

    public static void writeOutDynamicData(GameOutputStream gameOutputStream, VariableData variableData) throws IOException {
        if (variableData == null) {
            gameOutputStream.writeEnumOrdinal((Enum) null);
            return;
        }
        LogicBoolean.ReturnType returnType = variableData.getReturnType();
        gameOutputStream.writeEnumOrdinal(returnType);
        if (variableData instanceof VariableDataUnit) {
            writeOutUnitOrPlaceholder(gameOutputStream, ((VariableDataUnit) variableData).unit);
            return;
        }
        if (variableData instanceof VariableDataBoolean) {
            gameOutputStream.writeBoolean(((VariableDataBoolean) variableData).bool);
            return;
        }
        if (variableData instanceof VariableDataString) {
            gameOutputStream.writeStringNullable(((VariableDataString) variableData).text);
            return;
        }
        if (variableData instanceof VariableDataNumber) {
            gameOutputStream.writeDouble(((VariableDataNumber) variableData).number);
            return;
        }
        if (variableData instanceof VariableDataArray) {
            VariableDataArray variableDataArray = (VariableDataArray) variableData;
            gameOutputStream.writeInt(variableDataArray.size);
            if (variableDataArray instanceof VariableDataBoolArray) {
                VariableDataBoolArray variableDataBoolArray = (VariableDataBoolArray) variableDataArray;
                for (int i = 0; i < variableDataBoolArray.size; i++) {
                    gameOutputStream.writeBoolean(variableDataBoolArray.dataArray[i]);
                }
                return;
            }
            if (variableDataArray instanceof VariableDataNumberArray) {
                VariableDataNumberArray variableDataNumberArray = (VariableDataNumberArray) variableDataArray;
                for (int i2 = 0; i2 < variableDataNumberArray.size; i2++) {
                    gameOutputStream.writeFloat(variableDataNumberArray.dataArray[i2]);
                }
                return;
            }
            if (variableDataArray instanceof VariableDataUnitArray) {
                VariableDataUnitArray variableDataUnitArray = (VariableDataUnitArray) variableDataArray;
                for (int i3 = 0; i3 < variableDataUnitArray.size; i3++) {
                    writeOutUnitOrPlaceholder(gameOutputStream, variableDataUnitArray.dataArray[i3]);
                }
                return;
            }
            throw new RuntimeException("Unhandled array type: " + returnType.name());
        }
        if (returnType != LogicBoolean.ReturnType.undefined) {
            throw new RuntimeException("Unhandled type: " + returnType.name());
        }
    }

    public static BaseUnit readInUnitOrPlaceholder(GameInputStream gameInputStream) throws IOException {
        BaseUnit baseUnit;
        byte b = gameInputStream.readByte();
        if (b == 1) {
            float f = gameInputStream.readFloat();
            float f2 = gameInputStream.readFloat();
            float f3 = gameInputStream.readFloat();
            float f4 = gameInputStream.readFloat();
            baseUnit = DummyNonUnitWithTeam.a(gameInputStream.readOptionalPlayerTeam());
            baseUnit.posX = f;
            baseUnit.posY = f2;
            baseUnit.posZ = f3;
            baseUnit.rotationSpeed = f4;
        } else if (b == 0) {
            baseUnit = gameInputStream.readBaseUnit();
        } else {
            throw new IOException("Unhandled unit type: " + ((int) b));
        }
        return baseUnit;
    }

    public static VariableData readInDynamicData(GameInputStream gameInputStream) throws IOException {
        LogicBoolean.ReturnType returnType = (LogicBoolean.ReturnType) gameInputStream.readEnumOrdinalOrNull(LogicBoolean.ReturnType.class);
        if (returnType == null) {
            return null;
        }
        if (returnType == LogicBoolean.ReturnType.unit) {
            return new VariableDataUnit(readInUnitOrPlaceholder(gameInputStream));
        }
        if (returnType == LogicBoolean.ReturnType.bool) {
            return new VariableDataBoolean(gameInputStream.readBoolean());
        }
        if (returnType == LogicBoolean.ReturnType.string) {
            return new VariableDataString(gameInputStream.readNullableString());
        }
        if (returnType == LogicBoolean.ReturnType.number) {
            return new VariableDataNumber(gameInputStream.readDouble());
        }
        if (returnType == LogicBoolean.ReturnType.boolArray || returnType == LogicBoolean.ReturnType.numberArray || returnType == LogicBoolean.ReturnType.unitArray) {
            int i = gameInputStream.readInt();
            if (returnType == LogicBoolean.ReturnType.boolArray) {
                VariableDataBoolArray variableDataBoolArray = new VariableDataBoolArray();
                for (int i2 = 0; i2 < i; i2++) {
                    variableDataBoolArray.setBooleanIndex(i2, gameInputStream.readBoolean());
                }
                return variableDataBoolArray;
            }
            if (returnType == LogicBoolean.ReturnType.numberArray) {
                VariableDataNumberArray variableDataNumberArray = new VariableDataNumberArray();
                for (int i3 = 0; i3 < i; i3++) {
                    variableDataNumberArray.setNumberIndex(i3, gameInputStream.readFloat());
                }
                return variableDataNumberArray;
            }
            if (returnType == LogicBoolean.ReturnType.unitArray) {
                VariableDataUnitArray variableDataUnitArray = new VariableDataUnitArray();
                for (int i4 = 0; i4 < i; i4++) {
                    variableDataUnitArray.setUnitIndex(i4, readInUnitOrPlaceholder(gameInputStream));
                }
                return variableDataUnitArray;
            }
            throw new RuntimeException("Unhandled array type: " + returnType.name());
        }
        if (returnType == LogicBoolean.ReturnType.undefined) {
            throw new RuntimeException("Undefined type: " + returnType.name());
        }
        throw new RuntimeException("Unhandled type: " + returnType.name());
    }

    public static LogicBoolean.ReturnType getUserType(String str) {
        LogicBoolean.ReturnType returnType = null;
        if (str.equals("boolean") || str.equals("bool")) {
            returnType = LogicBoolean.ReturnType.bool;
        } else if (str.equals("unit")) {
            returnType = LogicBoolean.ReturnType.unit;
        } else if (str.equals("number") || str.equals("float")) {
            returnType = LogicBoolean.ReturnType.number;
        } else if (str.equals("text") || str.equals("string")) {
            returnType = LogicBoolean.ReturnType.string;
        } else if (str.equals("number[]") || str.equals("float[]")) {
            returnType = LogicBoolean.ReturnType.numberArray;
        } else if (str.equals("bool[]") || str.equals("boolean[]")) {
            returnType = LogicBoolean.ReturnType.boolArray;
        } else if (str.equals("unit[]")) {
            returnType = LogicBoolean.ReturnType.unitArray;
        }
        return returnType;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$AbstractMemoryLogicBoolean.class */
    public abstract static class AbstractMemoryLogicBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBooleanLoader.LogicBooleanContext createContext() {
            LogicBoolean.ReturnType returnType = getReturnType();
            if (returnType == LogicBoolean.ReturnType.unit) {
                return UnitReference.unitContextChangingContext;
            }
            if (LogicBoolean.ReturnType.isArrayType(returnType)) {
                if (returnType == LogicBoolean.ReturnType.numberArray) {
                    return LogicBooleanLoader.numberArrayContextReader;
                }
                if (returnType == LogicBoolean.ReturnType.boolArray) {
                    return LogicBooleanLoader.boolArrayContextReader;
                }
                if (returnType == LogicBoolean.ReturnType.unitArray) {
                    return LogicBooleanLoader.unitArrayContextReader;
                }
                GameEngine.logColored("Unhandled array context type: " + returnType);
                return LogicBooleanLoader.voidContextReader;
            }
            return super.createContext();
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean setChild(LogicBoolean logicBoolean) {
            if (LogicBoolean.ReturnType.isArrayType(getReturnType())) {
                return logicBoolean;
            }
            return UnitReference.UnitContextChangingBooleanByLogic.create(this, logicBoolean);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$KnownMemoryReadLogicBoolean.class */
    public static class KnownMemoryReadLogicBoolean extends AbstractMemoryLogicBoolean {
        VariableName name;
        LogicBoolean.ReturnType type;

        public KnownMemoryReadLogicBoolean(VariableDefinition variableDefinition) {
            this.name = variableDefinition.name;
            this.type = variableDefinition.type;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            if (orderableUnit.unitVariables == null) {
                return false;
            }
            return orderableUnit.unitVariables.getBoolean(this.name);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            if (orderableUnit.unitVariables == null) {
                return 0.0f;
            }
            return (float) orderableUnit.unitVariables.getNumber(this.name);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return orderableUnit.unitVariables == null ? VariableScope.nullOrMissingString : orderableUnit.unitVariables.getString(this.name);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public BaseUnit readUnit(OrderableUnit orderableUnit) {
            if (orderableUnit.unitVariables == null) {
                return null;
            }
            return orderableUnit.unitVariables.getUnit(this.name);
        }

        public LogicBoolean readAsLogicBoolean(OrderableUnit orderableUnit) {
            if (orderableUnit.unitVariables == null) {
                return null;
            }
            return orderableUnit.unitVariables.getAsLogicBoolean(this.name);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public int getArraySize(OrderableUnit orderableUnit) {
            if (orderableUnit.unitVariables == null) {
                return 0;
            }
            return orderableUnit.unitVariables.getDataObjectRaw(this.name).getArraySize(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean readArrayElement(OrderableUnit orderableUnit, int i) {
            if (orderableUnit.unitVariables == null) {
                return null;
            }
            return orderableUnit.unitVariables.getDataObjectRaw(this.name).readArrayElement(orderableUnit, i);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return this.type;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            LogicBoolean asLogicBoolean = readAsLogicBoolean(orderableUnit);
            if (asLogicBoolean == null) {
                return "memory(" + this.name.id + "=null)";
            }
            String str = VariableScope.nullOrMissingString;
            if (this.type != asLogicBoolean.getReturnType() && asLogicBoolean.getReturnType() != LogicBoolean.ReturnType.voidReturn) {
                str = "(TYPE MISMATCH GOT: " + asLogicBoolean.getReturnType().name() + ")";
            }
            return "memory(" + this.name.id + "=" + asLogicBoolean.getMatchFailReasonForPlayer(orderableUnit) + str + ")";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$ReadEventMemoryLogicBoolean.class */
    public static class ReadEventMemoryLogicBoolean extends ReadUnitMemoryLogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.ReadUnitMemoryLogicBoolean
        public LogicBoolean getUnitMemory(OrderableUnit orderableUnit) {
            CustomUnitEventData customUnitEventData = LogicBoolean.currentEventContext;
            VariableScope variableScope = null;
            if (customUnitEventData != null) {
                variableScope = customUnitEventData.variableScope;
            }
            if (variableScope == null) {
                return this.defaultValue;
            }
            LogicBoolean asLogicBoolean = variableScope.getAsLogicBoolean(this._name);
            if (asLogicBoolean == null) {
                return this.defaultValue;
            }
            return asLogicBoolean;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$ReadUnitMemoryLogicBoolean.class */
    public static class ReadUnitMemoryLogicBoolean extends AbstractMemoryLogicBoolean {
        VariableName _name;
        LogicBoolean.ReturnType _type;

        @LogicBoolean.Parameter(key = "default")
        public LogicBoolean defaultValue;

        @LogicBoolean.Parameter(key = "index")
        public LogicBoolean index;

        @LogicBoolean.Parameter(required = true, positional = 0)
        public void name(String str) {
            this._name = VariableName.get(str.toLowerCase(Locale.ROOT).trim());
        }

        @LogicBoolean.Parameter(required = true)
        public void type(String str) {
            this._type = VariableScope.getUserType(str);
            if (this._type == null) {
                throw new RuntimeException("Unknown type: " + str);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this.defaultValue != null && this.defaultValue.getReturnType() != this._type) {
                throw new BooleanParseException("defaultValue type:" + this.defaultValue.getReturnType() + " does not match requested type: " + this._type);
            }
            if (this.defaultValue == null) {
                this.defaultValue = VariableScope.variableDataNull;
            }
        }

        public LogicBoolean getUnitMemory(OrderableUnit orderableUnit) {
            if (orderableUnit.unitVariables == null) {
                return this.defaultValue;
            }
            LogicBoolean asLogicBoolean = orderableUnit.unitVariables.getAsLogicBoolean(this._name);
            if (asLogicBoolean == null) {
                return this.defaultValue;
            }
            if (this.index != null) {
                LogicBoolean arrayElement = asLogicBoolean.readArrayElement(orderableUnit, (int) this.index.readNumber(orderableUnit));
                if (arrayElement == null) {
                    return this.defaultValue;
                }
                asLogicBoolean = arrayElement;
            }
            return asLogicBoolean;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return getUnitMemory(orderableUnit).read(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return getUnitMemory(orderableUnit).readNumber(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            LogicBoolean unitMemory = getUnitMemory(orderableUnit);
            return LogicString.StringCast.castToString(unitMemory.getReturnType(), unitMemory, orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public BaseUnit readUnit(OrderableUnit orderableUnit) {
            return getUnitMemory(orderableUnit).readUnit(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public int getArraySize(OrderableUnit orderableUnit) {
            return getUnitMemory(orderableUnit).getArraySize(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean readArrayElement(OrderableUnit orderableUnit, int i) {
            return getUnitMemory(orderableUnit).readArrayElement(orderableUnit, i);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return this._type;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            if (this._type == null || this._name == null) {
                return "<memory with type/name == null>";
            }
            LogicBoolean unitMemory = getUnitMemory(orderableUnit);
            if (unitMemory == null) {
                return "memory(" + this._name.id + " as " + this._type.name() + ")";
            }
            String str = VariableScope.nullOrMissingString;
            if (this._type != unitMemory.getReturnType() && unitMemory.getReturnType() != LogicBoolean.ReturnType.voidReturn) {
                str = "(TYPE MISMATCH GOT: " + unitMemory.getReturnType().name() + ")";
            }
            return "memory(" + this._name.id + " as " + this._type.name() + "=" + unitMemory.getMatchFailReasonForPlayer(orderableUnit) + str + ")";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$KnownMemoryScopeLogicBoolean.class */
    public static class KnownMemoryScopeLogicBoolean extends LogicBooleanLoader.LogicBooleanScopeOnly {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.LogicBooleanContext
        public LogicBoolean parseNextElementInChain(String str, CustomUnitConfig customUnitConfig, String str2, boolean z, String str3, String str4, LogicBoolean logicBoolean) {
            VariableDefinition variableDefinition = customUnitConfig.variableMapping.get(str2.toLowerCase(Locale.ROOT));
            if (variableDefinition == null) {
                throw new RuntimeException("Unknown variable:'" + str2 + "' in '" + str4 + "'");
            }
            return new KnownMemoryReadLogicBoolean(variableDefinition);
        }
    }

    public static MemoryWriter createGenericKeyValueWriter(String str, CustomUnitConfig customUnitConfig, String str2, String str3) {
        try {
            MemoryWriter memoryWriter = new MemoryWriter();
            memoryWriter.addWriterElements(str, new MemoryWriterFactory(customUnitConfig, null));
            return memoryWriter;
        } catch (ConfigParseException e) {
            throw new RuntimeException("[" + str2 + "]" + str3 + ": " + e.getMessage(), e);
        }
    }

    public static MemoryWriter createMemoryWriter(String str, CustomUnitConfig customUnitConfig, String str2, String str3) {
        try {
            MemoryWriter memoryWriter = new MemoryWriter();
            memoryWriter.addWriterElements(str, new MemoryWriterFactory(customUnitConfig));
            return memoryWriter;
        } catch (ConfigParseException e) {
            throw new RuntimeException("[" + str2 + "]" + str3 + ": " + e.getMessage(), e);
        }
    }

    public static MemoryNames createMemoryNameList(String str, CustomUnitConfig customUnitConfig, LogicBoolean.ReturnType returnType, String str2, String str3) {
        try {
            MemoryWriter memoryWriter = new MemoryWriter();
            MemoryWriterFactory memoryWriterFactory = new MemoryWriterFactory(customUnitConfig);
            memoryWriterFactory.noValues = true;
            memoryWriter.addWriterElements(str, memoryWriterFactory);
            MemoryNames memoryNames = new MemoryNames();
            for (CachedWriter.WriterElement writerElement : memoryWriter.writers) {
                if (!(writerElement instanceof MemoryWriterFactory.MemoryWriterElement)) {
                    throw new ConfigParseException("Unexpected element reading: " + str, str2, str3);
                }
                MemoryWriterFactory.MemoryWriterElement memoryWriterElement = (MemoryWriterFactory.MemoryWriterElement) writerElement;
                if (memoryWriterElement instanceof MemoryWriterFactory.MemoryWriterElementIndex) {
                    throw new ConfigParseException("Expected memory name without an index got: " + str, str2, str3);
                }
                if (returnType != null) {
                    VariableDefinition variableDefinition = customUnitConfig.variableMapping.get(memoryWriterElement.name);
                    if (variableDefinition == null) {
                        throw new ConfigParseException("Failed to find defined memory: " + str, str2, str3);
                    }
                    if (variableDefinition.type != returnType) {
                        throw new ConfigParseException("Memory: " + str + " is type: " + variableDefinition.type + " expected: " + returnType, str2, str3);
                    }
                }
                memoryNames.names.add(memoryWriterElement.name);
            }
            return memoryNames;
        } catch (ConfigParseException e) {
            throw new RuntimeException("[" + str2 + "]" + str3 + ": " + e.getMessage(), e);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$MemoryWriter.class */
    public static class MemoryWriter extends CachedWriter {
        public void writeToMemory(VariableScope variableScope, OrderableUnit orderableUnit) {
            Iterator it = this.writers.iterator();
            while (it.hasNext()) {
                ((MemoryWriterFactory.MemoryWriterElement) ((CachedWriter.WriterElement) it.next())).writeToMemory(variableScope, orderableUnit);
            }
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$MemoryWriterFactory.class */
    public static class MemoryWriterFactory extends CachedWriter.WriterFactory {
        CustomUnitConfig meta;
        VariableMapping target;
        boolean noValues;

        public MemoryWriterFactory(CustomUnitConfig customUnitConfig, VariableMapping variableMapping) {
            this.meta = customUnitConfig;
            this.target = variableMapping;
        }

        public MemoryWriterFactory(CustomUnitConfig customUnitConfig) {
            this.meta = customUnitConfig;
            if (customUnitConfig != null) {
                this.target = customUnitConfig.variableMapping;
            }
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$MemoryWriterFactory$MemoryWriterElement.class */
        public static class MemoryWriterElement extends CachedWriter.WriterElement {
            public VariableName name;
            public LogicBoolean value;

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.CachedWriter.WriterElement
            public void writeToUnit(OrderableUnit orderableUnit) {
                if (orderableUnit.unitVariables == null) {
                    orderableUnit.unitVariables = new VariableScope();
                }
                orderableUnit.unitVariables.setFromLogicBoolean(this.name, orderableUnit, this.value, null);
            }

            public void writeToMemory(VariableScope variableScope, OrderableUnit orderableUnit) {
                variableScope.setFromLogicBoolean(this.name, orderableUnit, this.value, null);
            }
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$MemoryWriterFactory$MemoryWriterElementIndex.class */
        public static class MemoryWriterElementIndex extends MemoryWriterElement {
            public LogicBoolean nameIndex;

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.MemoryWriterFactory.MemoryWriterElement, com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.CachedWriter.WriterElement
            public void writeToUnit(OrderableUnit orderableUnit) {
                if (orderableUnit.unitVariables == null) {
                    orderableUnit.unitVariables = new VariableScope();
                }
                orderableUnit.unitVariables.setFromLogicBoolean(this.name, orderableUnit, this.value, this.nameIndex);
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.MemoryWriterFactory.MemoryWriterElement
            public void writeToMemory(VariableScope variableScope, OrderableUnit orderableUnit) {
                variableScope.setFromLogicBoolean(this.name, orderableUnit, this.value, this.nameIndex);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope.CachedWriter.WriterFactory
        public CachedWriter.WriterElement createWriterElement(String str, String str2, String str3, String str4) throws ConfigParseException {
            VariableName variableName;
            LogicBoolean.ReturnType returnType;
            MemoryWriterElement memoryWriterElement;
            if (!str2.equals("=")) {
                throw new ConfigParseException("Only '=' is supported here, got:" + str2);
            }
            if (!this.noValues) {
                if (str3 == null) {
                    throw new ConfigParseException("Expected a value for: " + str + " (likely missing '=')");
                }
            } else if (str3 != null) {
                throw new ConfigParseException("Expected no value for: " + str + " (Remove '=" + str3 + "')");
            }
            LogicBoolean booleanBlock = null;
            if (str3 != null) {
                try {
                    booleanBlock = LogicBooleanLoader.parseBooleanBlock(this.meta, str3, false);
                } catch (RuntimeException e) {
                    throw new RuntimeException("LogicBoolean - Error: " + e.getMessage() + ", [parsing: '" + str3 + "']", e);
                }
            }
            if (this.target != null) {
                VariableDefinition variableDefinition = this.target.get(str);
                if (variableDefinition == null) {
                    throw new ConfigParseException("Unknown variable: " + str + " (has it been defined in this unit?)");
                }
                variableName = variableDefinition.name;
                returnType = variableDefinition.type;
            } else {
                variableName = VariableName.get(str);
                returnType = LogicBoolean.ReturnType.undefined;
            }
            LogicBoolean.ReturnType arrayBaseType = returnType;
            LogicBoolean booleanBlock2 = null;
            if (str4 != null) {
                if (!LogicBoolean.ReturnType.isArrayType(returnType)) {
                    if (returnType != LogicBoolean.ReturnType.undefined) {
                        throw new ConfigParseException("Variable: " + str + " is not an array type " + returnType + " cannot use [] index on it.");
                    }
                } else {
                    arrayBaseType = LogicBoolean.ReturnType.getArrayBaseType(returnType);
                }
                try {
                    booleanBlock2 = LogicBooleanLoader.parseBooleanBlock(this.meta, str4, false);
                    if (booleanBlock2 != null && booleanBlock2.getReturnType() != LogicBoolean.ReturnType.number) {
                        throw new RuntimeException("Expected " + str + "[] array index to be a number got " + booleanBlock2.getReturnType() + " type, [parsing: '" + str4 + "']");
                    }
                    if (booleanBlock2 == null) {
                        throw new RuntimeException("Missing " + str + "[] array index");
                    }
                } catch (RuntimeException e2) {
                    throw new RuntimeException("Error reading " + str + "[] array index: " + e2.getMessage() + ", [parsing: '" + str4 + "']", e2);
                }
            } else if (!this.noValues && LogicBoolean.ReturnType.isArrayType(returnType) && (str3 == null || !"null".equalsIgnoreCase(str3.trim()))) {
                throw new ConfigParseException("Variable " + str + " is an array type. Expected: NAME[INDEX]=VALUE format (or NAME=null)");
            }
            if (booleanBlock2 == null) {
                memoryWriterElement = new MemoryWriterElement();
                memoryWriterElement.name = variableName;
                memoryWriterElement.value = booleanBlock;
            } else {
                MemoryWriterElementIndex memoryWriterElementIndex = new MemoryWriterElementIndex();
                memoryWriterElement = memoryWriterElementIndex;
                memoryWriterElementIndex.name = variableName;
                memoryWriterElementIndex.value = booleanBlock;
                memoryWriterElementIndex.nameIndex = booleanBlock2;
            }
            if (arrayBaseType != LogicBoolean.ReturnType.undefined && booleanBlock != null && booleanBlock.getReturnType() != arrayBaseType) {
                if (LogicBoolean.isStaticNull(booleanBlock)) {
                    if (!LogicBoolean.ReturnType.canBeNull(arrayBaseType)) {
                        throw new ConfigParseException("Variable: " + str + " of type " + arrayBaseType + " cannot be set to null.");
                    }
                } else {
                    throw new ConfigParseException("Variable: " + str + " expects " + LogicBoolean.ReturnType.toUserString(arrayBaseType) + " type getting: " + LogicBoolean.ReturnType.toUserString(booleanBlock.getReturnType()) + " from: " + str3);
                }
            }
            return memoryWriterElement;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$CachedWriter.class */
    public static class CachedWriter {
        FastArrayList<CachedWriter.WriterElement> writers = new FastArrayList();

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$CachedWriter$Operator.class */
        public enum Operator {
            set,
            add,
            subtract
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$CachedWriter$WriterElement.class */
        public abstract static class WriterElement {
            public abstract void writeToUnit(OrderableUnit orderableUnit);
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/VariableScope$CachedWriter$WriterFactory.class */
        public abstract static class WriterFactory {
            public abstract WriterElement createWriterElement(String str, String str2, String str3, String str4) throws ConfigParseException;
        }

        public void writeToUnit(OrderableUnit orderableUnit) {
            Iterator it = this.writers.iterator();
            while (it.hasNext()) {
                ((WriterElement) it.next()).writeToUnit(orderableUnit);
            }
        }

        public static CachedWriter create(String str, WriterFactory writerFactory) throws ConfigParseException {
            CachedWriter cachedWriter = new CachedWriter();
            cachedWriter.addWriterElements(str, writerFactory);
            return cachedWriter;
        }

        public void addWriterElements(String str, WriterFactory writerFactory) throws ConfigParseException {
            String str2;
            String str3;
            for (String str4 : StringUtils.a(str, ",", false, false)) {
                String[] strArrC = StringUtils.c(str4, "=");
                if (strArrC == null) {
                    str2 = str4;
                    str3 = null;
                } else {
                    str2 = strArrC[0];
                    str3 = strArrC[1];
                }
                String strSubstring = null;
                if (Utility.containsSubstring(str2, "[")) {
                    int iIndexOf = str2.indexOf(91);
                    int iB = StringUtils.b(str2, "]", iIndexOf);
                    if (iIndexOf == -1 || iB == -1) {
                        throw new ConfigParseException("Unexpected array[] format for: " + str2);
                    }
                    strSubstring = str2.substring(iIndexOf + 1, iB);
                    if (strSubstring.trim().equals(VariableScope.nullOrMissingString)) {
                        throw new ConfigParseException("Array [] index in: " + str2 + " is empty");
                    }
                    String strSubstring2 = str2.substring(iB + 1, str2.length());
                    for (int i = 0; i < strSubstring2.length(); i++) {
                        char cCharAt = strSubstring2.charAt(i);
                        if (cCharAt != '+' && cCharAt != '=' && cCharAt != '-' && cCharAt != '*' && cCharAt != '/' && cCharAt != ' ') {
                            throw new ConfigParseException("Unexpected text:'" + strSubstring2 + "' after [] index of: " + str2);
                        }
                        if (cCharAt == '=') {
                            break;
                        }
                    }
                    str2 = str2.substring(0, iIndexOf) + strSubstring2;
                }
                String strTrim = str2.toLowerCase(Locale.ROOT).trim();
                String str5 = "=";
                if (strTrim.endsWith("+") || strTrim.endsWith("-") || strTrim.endsWith("*") || strTrim.endsWith("/")) {
                    str5 = strTrim.substring(strTrim.length() - 1, strTrim.length()) + "=";
                    strTrim = strTrim.substring(0, strTrim.length() - 1).trim();
                }
                if (strTrim.contains(" ")) {
                    throw new ConfigParseException("Key cannot contain spaces for: " + str4);
                }
                if (strTrim.contains("[")) {
                    throw new ConfigParseException("Key cannot contain [ for: " + str4);
                }
                if (strTrim.contains("]")) {
                    throw new ConfigParseException("Key cannot contain ] for: " + str4);
                }
                if (strTrim.contains("(")) {
                    throw new ConfigParseException("Key cannot contain ( for: " + str4);
                }
                if (strTrim.contains(")")) {
                    throw new ConfigParseException("Key cannot contain ) for: " + str4);
                }
                if (strTrim.contains(".")) {
                    throw new ConfigParseException("Key cannot contain . for: " + str4);
                }
                this.writers.add(writerFactory.createWriterElement(strTrim, str5, str3, strSubstring));
            }
        }
    }

    public static boolean isMarker(BaseUnit baseUnit) {
        if (baseUnit == null) {
            return false;
        }
        return baseUnit instanceof DummyNonUnitWithTeam;
    }

    public static BaseUnit getSafeUnitReferenceForStorage(BaseUnit baseUnit) {
        if (baseUnit == null) {
            return null;
        }
        if (baseUnit instanceof DummyNonUnitWithTeam) {
            DummyNonUnitWithTeam dummyNonUnitWithTeamA = DummyNonUnitWithTeam.a(baseUnit.team);
            dummyNonUnitWithTeamA.posX = baseUnit.posX;
            dummyNonUnitWithTeamA.posY = baseUnit.posY;
            dummyNonUnitWithTeamA.posZ = baseUnit.posZ;
            dummyNonUnitWithTeamA.rotationSpeed = baseUnit.rotationSpeed;
            return dummyNonUnitWithTeamA;
        }
        return baseUnit;
    }
}
