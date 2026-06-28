package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString.class */
public abstract class LogicString extends LogicBoolean {
    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public LogicBoolean.ReturnType getReturnType() {
        return LogicBoolean.ReturnType.string;
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
        super.validate(str, str2, str3, logicBooleanContext, z);
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
        return "TEXT";
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public boolean read(OrderableUnit orderableUnit) {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
    public float readNumber(OrderableUnit orderableUnit) {
        return 0.0f;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$StaticString.class */
    public static class StaticString extends LogicString {
        String text;

        public StaticString(String str) {
            this.text = str;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return this.text;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$Playername.class */
    public static class Playername extends LogicString {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return orderableUnit.team.teamName;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$TeamName.class */
    public static class TeamName extends LogicString {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return orderableUnit.team.getTeamSlotLabel();
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$WrappingLogicString.class */
    public static class WrappingLogicString extends LogicString {
        LogicBoolean[] children;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
            if (str == null || VariableScope.nullOrMissingString.equals(str)) {
                validateNumberOfArguments(0);
                return;
            }
            ArrayList arrayListA = StringUtils.a(str, ",", false);
            validateNumberOfArguments(arrayListA.size());
            this.children = new LogicBoolean[arrayListA.size()];
            for (int i = 0; i < arrayListA.size(); i++) {
                this.children[i] = LogicBooleanLoader.parseBooleanBlock(customUnitConfig, (String) arrayListA.get(i), false);
                if (this.children == null) {
                    throw new BooleanParseException("Expected non-null argument");
                }
            }
        }

        public void validateNumberOfArguments(int i) {
            if (i != 1) {
                throw new BooleanParseException("Expected 1 argument");
            }
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$Debug.class */
    public static class Debug extends WrappingLogicString {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return this.children[0].getMatchFailReasonForPlayer(orderableUnit);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$DebugPassthrough.class */
    public static class DebugPassthrough extends WrappingLogicString {
        public void addMessage(OrderableUnit orderableUnit) {
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine.isGameStarted && gameEngine.isDebugTempMode) {
                String str = VariableScope.nullOrMissingString;
                if (orderableUnit != null) {
                    str = orderableUnit.r().getUnitTypeDescriptionShort() + "(" + orderableUnit.objectId + ") ";
                }
                NetworkEngine.a((String) null, str + "DebugPassthrough: " + this.children[0].getMatchFailReasonForPlayer(orderableUnit));
            }
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return this.children[0].getReturnType();
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            addMessage(orderableUnit);
            return this.children[0].read(orderableUnit);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            addMessage(orderableUnit);
            return this.children[0].readNumber(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            addMessage(orderableUnit);
            return this.children[0].readString(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public BaseUnit readUnit(OrderableUnit orderableUnit) {
            addMessage(orderableUnit);
            return this.children[0].readUnit(orderableUnit);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$Substring.class */
    public static class Substring extends LogicString {

        @LogicBoolean.Parameter(required = true, positional = 0)
        public LogicBoolean text;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, required = true, positional = 1)
        public LogicBoolean start;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, required = true, positional = 2)
        public LogicBoolean end;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            if (this.text == null) {
                throw new BooleanParseException("Expected argument text");
            }
            if (this.text.getReturnType() != LogicBoolean.ReturnType.string) {
                this.text = StringCast.createStringCast(this.text);
            }
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            String string = this.text.readString(orderableUnit);
            int number = (int) this.start.readNumber(orderableUnit);
            int number2 = (int) this.end.readNumber(orderableUnit);
            if (number < 0) {
                number = 0;
            }
            if (number2 < 0) {
                number2 = 0;
            }
            if (number > string.length()) {
                number = string.length();
            }
            if (number2 > string.length()) {
                number2 = string.length();
            }
            if (number2 < number) {
                number2 = number;
            }
            return string.substring(number, number2);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$LowerString.class */
    public static class LowerString extends LogicString {

        @LogicBoolean.Parameter(required = true, positional = 0)
        public LogicBoolean text;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            if (this.text == null) {
                throw new BooleanParseException("Expected argument text");
            }
            if (this.text.getReturnType() != LogicBoolean.ReturnType.string) {
                throw new BooleanParseException("Expected string argument");
            }
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return this.text.readString(orderableUnit).toLowerCase(Locale.ROOT);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$UpperString.class */
    public static class UpperString extends LogicString {

        @LogicBoolean.Parameter(required = true, positional = 0)
        public LogicBoolean text;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            if (this.text == null) {
                throw new BooleanParseException("Expected argument text");
            }
            if (this.text.getReturnType() != LogicBoolean.ReturnType.string) {
                throw new BooleanParseException("Expected string argument");
            }
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return this.text.readString(orderableUnit).toUpperCase(Locale.ROOT);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$StringCast.class */
    static class StringCast extends LogicString {
        LogicBoolean child;
        LogicBoolean.ReturnType sourceType;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
            if (str == null || VariableScope.nullOrMissingString.equals(str)) {
                validateNumberOfArguments(0);
                return;
            }
            ArrayList arrayListA = StringUtils.a(str, ",", false);
            validateNumberOfArguments(arrayListA.size());
            this.child = LogicBooleanLoader.parseBooleanBlock(customUnitConfig, (String) arrayListA.get(0), false);
            if (this.child == null) {
                throw new BooleanParseException("Expected non-null argument");
            }
        }

        public void validateNumberOfArguments(int i) {
            if (i != 1) {
                throw new BooleanParseException("Expected 1 argument");
            }
        }

        public static LogicBoolean createStringCast(LogicBoolean logicBoolean) {
            StringCast stringCast = new StringCast();
            stringCast.child = logicBoolean;
            return stringCast.validateAndOptimize("cast", VariableScope.nullOrMissingString, VariableScope.nullOrMissingString, null, false);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            this.sourceType = this.child.getReturnType();
            if (this.child instanceof StaticString) {
                return this.child;
            }
            if (this.child instanceof LogicBoolean.StaticBoolean) {
                return new StaticString(readString(null));
            }
            if (this.child instanceof LogicBoolean.StaticValueBoolean) {
                return new StaticString(readString(null));
            }
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            return castToString(this.sourceType, this.child, orderableUnit);
        }

        public static String castToString(LogicBoolean.ReturnType returnType, LogicBoolean logicBoolean, OrderableUnit orderableUnit) {
            if (returnType == LogicBoolean.ReturnType.number) {
                return Utility.padString(logicBoolean.readNumber(orderableUnit), 2);
            }
            if (returnType == LogicBoolean.ReturnType.unit) {
                return BaseUnit.serialize(logicBoolean.readUnit(orderableUnit));
            }
            if (returnType == LogicBoolean.ReturnType.string) {
                return logicBoolean.readString(orderableUnit);
            }
            if (returnType == LogicBoolean.ReturnType.numberArray) {
                return arrayToString(orderableUnit, logicBoolean);
            }
            if (returnType == LogicBoolean.ReturnType.boolArray) {
                return arrayToString(orderableUnit, logicBoolean);
            }
            return logicBoolean.read(orderableUnit) ? "true" : "false";
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$StringCast$NumberToStringCast.class */
        public class NumberToStringCast extends StringCast {
            @Override
            // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString.StringCast, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public /* bridge */ /* synthetic */ LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
                return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString.StringCast
            public /* bridge */ /* synthetic */ void validateNumberOfArguments(int i) {
                super.validateNumberOfArguments(i);
            }

            @Override
            // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString.StringCast, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public /* bridge */ /* synthetic */ void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
                super.setArgumentsRaw(str, customUnitConfig, str2);
            }

            @Override
            // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString.StringCast, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public String readString(OrderableUnit orderableUnit) {
                return Utility.padString(this.child.readNumber(orderableUnit), 2);
            }
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicString$Select.class */
    public static class Select extends LogicString {
        LogicBoolean.ReturnType commonType;
        LogicBoolean selector;
        LogicBoolean childA;
        LogicBoolean childB;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
            if (str == null || VariableScope.nullOrMissingString.equals(str)) {
                validateNumberOfArguments(0);
                return;
            }
            ArrayList arrayListA = StringUtils.a(str, ",", false);
            validateNumberOfArguments(arrayListA.size());
            this.selector = LogicBooleanLoader.parseBooleanBlock(customUnitConfig, (String) arrayListA.get(0), true);
            if (this.selector == null) {
                throw new BooleanParseException("Expected non-null argument");
            }
            this.childA = LogicBooleanLoader.parseBooleanBlock(customUnitConfig, (String) arrayListA.get(1), false);
            if (this.childA == null) {
                throw new BooleanParseException("Expected non-null argument");
            }
            this.childB = LogicBooleanLoader.parseBooleanBlock(customUnitConfig, (String) arrayListA.get(2), false);
            if (this.childB == null) {
                throw new BooleanParseException("Expected non-null argument");
            }
            this.commonType = this.childA.getReturnType();
            if (this.commonType != this.childB.getReturnType()) {
                throw new BooleanParseException("Select() expected 2 and 3 argument to FastArrayList the same type, got: " + this.commonType.name() + " and " + this.childB.getReturnType().name());
            }
        }

        public void validateNumberOfArguments(int i) {
            if (i != 3) {
                throw new BooleanParseException("Expected 3 arguments");
            }
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return this.commonType;
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "(selector if:(" + this.selector.getMatchFailReasonForPlayer(orderableUnit) + ") then:(" + this.childA.getMatchFailReasonForPlayer(orderableUnit) + ") ) else:(" + this.childB.getMatchFailReasonForPlayer(orderableUnit) + ") )";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            if (this.selector.read(orderableUnit)) {
                return this.childA.readString(orderableUnit);
            }
            return this.childB.readString(orderableUnit);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            if (this.selector.read(orderableUnit)) {
                return this.childA.read(orderableUnit);
            }
            return this.childB.read(orderableUnit);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            if (this.selector.read(orderableUnit)) {
                return this.childA.readNumber(orderableUnit);
            }
            return this.childB.readNumber(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public BaseUnit readUnit(OrderableUnit orderableUnit) {
            if (this.selector.read(orderableUnit)) {
                return this.childA.readUnit(orderableUnit);
            }
            return this.childB.readUnit(orderableUnit);
        }
    }

    public static String arrayToString(OrderableUnit orderableUnit, LogicBoolean logicBoolean) {
        int arraySize = logicBoolean.getArraySize(orderableUnit);
        String str = "[";
        int i = 0;
        while (true) {
            if (i >= arraySize) {
                break;
            }
            if (i > 12) {
                str = str + ",...";
                break;
            }
            if (i != 0) {
                str = str + ",";
            }
            LogicBoolean arrayElement = logicBoolean.readArrayElement(orderableUnit, i);
            if (arrayElement != null) {
                str = str + arrayElement.valueToStringDebug(orderableUnit);
            }
            i++;
        }
        return str + "]";
    }

    public static String arraySummaryToString(OrderableUnit orderableUnit, LogicBoolean logicBoolean) {
        return LogicBoolean.ReturnType.toUserString(LogicBoolean.ReturnType.getArrayBaseType(logicBoolean.getReturnType())) + "[" + logicBoolean.getArraySize(orderableUnit) + "]";
    }
}
