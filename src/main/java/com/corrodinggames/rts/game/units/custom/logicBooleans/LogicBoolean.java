package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.CustomUnitEventData;
import com.corrodinggames.rts.game.units.custom.logicBooleans.CompareJoinerBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicString;
import com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean.class */
public abstract class LogicBoolean implements Cloneable {
    public static final boolean not = false;

    /* JADX INFO: renamed from: activeEvent */
    static CustomUnitEventData currentEventContext;

    /* JADX INFO: renamed from: outerUnitParameterContext */
    static OrderableUnit externalUnitContext;
    public static final StaticBoolean trueBoolean = new StaticBooleanTrue();
    public static final StaticBoolean falseBoolean = new StaticBooleanFalse();
    static CallContext_self callContext_self = new CallContext_self();
    static CallContext_selfAndTarget callContext_selfAndTarget = new CallContext_selfAndTarget();

    /* JADX INFO: renamed from: parameterMappings */
    static final HashMap parameterCache = new HashMap();

    /* JADX INFO: renamed from: booleans */
    static HashMap booleanRegistry = new HashMap();

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$CallContext.class */
    public static class CallContext {
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$CallContext_self.class */
    public static class CallContext_self extends CallContext {
        public CustomUnit self;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$CallContext_selfAndTarget.class */
    public static class CallContext_selfAndTarget extends CallContext_self {
        public BaseUnit target;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$Parameter.class */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Parameter {
        ReturnType type() default ReturnType.undefined;

        boolean required() default false;

        int positional() default -1;

        String key() default "";
    }

    public abstract boolean read(OrderableUnit orderableUnit);

    public abstract String getMatchFailReasonForPlayer(OrderableUnit orderableUnit);

    static {
        LogicBooleanGameFunctions.loadTypes();
        addBooleanType(new VariableScope.KnownMemoryScopeLogicBoolean(), "memory");
        addBooleanType(new VariableScope.ReadUnitMemoryLogicBoolean(), "self.readUnitMemory");
        addBooleanType(new VariableScope.ReadEventMemoryLogicBoolean(), "eventData");
        addBooleanType(new LogicString.Playername(), "self.playername");
        addBooleanType(new LogicString.TeamName(), "self.teamname");
        addBooleanType(new LogicString.Debug(), "debug");
        addBooleanType(new LogicString.DebugPassthrough(), "debugPassthrough");
        addBooleanType(new LogicString.Select(), "select");
        addBooleanType(new LogicString.StringCast(), "str");
        addBooleanType(new LogicString.Substring(), "substring");
        addBooleanType(new LogicString.LowerString(), "lowercase");
        addBooleanType(new LogicString.UpperString(), "uppercase");
        addBooleanType(new LogicNumberFunction.FunctionInt(), "int");
        addBooleanType(new LogicNumberFunction.FunctionRnd(), "rnd");
        addBooleanType(new LogicNumberFunction.FunctionMax(), "max");
        addBooleanType(new LogicNumberFunction.FunctionMin(), "min");
        addBooleanType(new LogicNumberFunction.FunctionLength(), "length");
        addBooleanType(new LogicNumberFunction.FunctionSquareRoot(), "squareRoot");
        addBooleanType(new LogicNumberFunction.FunctionDistanceSquared(), "distanceSquared");
        addBooleanType(new LogicNumberFunction.FunctionCos(), "cos");
        addBooleanType(new LogicNumberFunction.FunctionSin(), "sin");
        addBooleanType(new LogicNumberFunction.FunctionDistance(), "distance");
        addBooleanType(new LogicNumberFunction.FunctionDirection(), "direction");
        addBooleanType(new LogicNumberFunction.FunctionDistanceBetweenSquared(), "distanceBetweenSquared");
        addBooleanType(new LogicNumberFunction.FunctionDistanceBetween(), "distanceBetween");
        addBooleanType(new LogicNumberFunction.FunctionDirectionBetween(), "directionBetween");
        addBooleanType(new LogicNumberFunction.CreateMarker(), "createMarker");
        addBooleanType(trueBoolean, "true");
        addBooleanType(falseBoolean, "false");
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$ReturnType.class */
    public enum ReturnType {
        undefined,
        voidReturn,
        bool,
        number,
        unit,
        string,
        point,
        boolArray,
        numberArray,
        unitArray;

        public static boolean canBeNull(ReturnType returnType) {
            boolean z = false;
            if (returnType == string) {
                z = true;
            }
            if (returnType == point) {
                z = true;
            }
            if (returnType == unit) {
                z = true;
            }
            if (returnType == numberArray) {
                z = true;
            }
            if (returnType == boolArray) {
                z = true;
            }
            if (returnType == unitArray) {
                z = true;
            }
            return z;
        }

        public static boolean isArrayType(ReturnType returnType) {
            return returnType == numberArray || returnType == boolArray || returnType == unitArray;
        }

        public static ReturnType getArrayBaseType(ReturnType returnType) {
            if (returnType == boolArray) {
                return bool;
            }
            if (returnType == numberArray) {
                return number;
            }
            if (returnType == unitArray) {
                return unit;
            }
            return null;
        }

        public static ReturnType getArrayTypeFromBase(ReturnType returnType) {
            if (returnType == bool) {
                return boolArray;
            }
            if (returnType == number) {
                return numberArray;
            }
            if (returnType == unit) {
                return unitArray;
            }
            return null;
        }

        public static String toUserString(ReturnType returnType) {
            return returnType == null ? "<NULL TYPE>" : returnType == numberArray ? "number[]" : returnType == boolArray ? "bool[]" : returnType == unitArray ? "unit[]" : returnType.name();
        }
    }

    public static final OrderableUnit getParameterContext(OrderableUnit orderableUnit) {
        OrderableUnit orderableUnit2 = externalUnitContext;
        if (orderableUnit2 != null) {
            return orderableUnit2;
        }
        return orderableUnit;
    }

    public static final void setOuterUnitParameterContext(OrderableUnit orderableUnit) {
        externalUnitContext = orderableUnit;
    }

    public static final void clearOuterUnitParameterContext() {
        externalUnitContext = null;
    }

    public static void enableContextEventSource() {
    }

    public static void setContextEventSource(CustomUnitEventData customUnitEventData) {
        currentEventContext = customUnitEventData;
    }

    public static void clearContext() {
        currentEventContext = null;
    }

    static void addBooleanType(LogicBoolean logicBoolean, String... strArr) {
        for (String str : strArr) {
            String lowerCase = str.toLowerCase(Locale.ROOT);
            if (booleanRegistry.get(lowerCase) != null) {
                throw new RuntimeException("logicBoolean: " + lowerCase + " already exists");
            }
            booleanRegistry.put(lowerCase, logicBoolean);
        }
    }

    public void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
        LogicBooleanLoader.setArgumentsWithMapping(getParameters(), this, str, customUnitConfig, str2);
    }

    public LogicBooleanLoader.ParameterMapping getParameters() {
        LogicBooleanLoader.ParameterMapping parameterMapping = (LogicBooleanLoader.ParameterMapping) parameterCache.get(getClass());
        if (parameterMapping == null) {
            parameterMapping = new LogicBooleanLoader.ParameterMapping(getClass());
            parameterCache.put(parameterMapping.type, parameterMapping);
        }
        return parameterMapping;
    }

    public boolean isLocked() {
        return false;
    }

    public LogicBoolean createLocked() {
        throw new RuntimeException("Not implemented");
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$LogicBooleanCommon.class */
    public abstract static class LogicBooleanCommon extends LogicBoolean {
        public abstract String getName();

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return getName() + "=" + (read(orderableUnit) ? "true" : "false") + VariableScope.nullOrMissingString;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$LogicBooleanCommonLocking.class */
    public abstract static class LogicBooleanCommonLocking extends LogicBoolean {
        boolean locked = false;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean createLocked() {
            try {
                LogicBooleanCommonLocking logicBooleanCommonLocking = (LogicBooleanCommonLocking) clone();
                logicBooleanCommonLocking.locked = true;
                return logicBooleanCommonLocking;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean isLocked() {
            return this.locked;
        }
    }

    public void forMeta(CustomUnitConfig customUnitConfig) {
    }

    public LogicBoolean with(String str) {
        return with(null, str, null);
    }

    public LogicBoolean with(CustomUnitConfig customUnitConfig, String str, String str2) {
        try {
            LogicBoolean logicBoolean = (LogicBoolean) clone();
            logicBoolean.forMeta(customUnitConfig);
            if (isLocked()) {
                if (str != null && !str.trim().equals(VariableScope.nullOrMissingString)) {
                    throw new BooleanParseException("No parameters accepted for " + getClass().getSimpleName());
                }
            } else {
                logicBoolean.setArgumentsRaw(str, customUnitConfig, str2);
            }
            return logicBoolean;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static LogicBoolean convertAlwaysTrueToNull(LogicBoolean logicBoolean) {
        if (logicBoolean == trueBoolean) {
            return null;
        }
        return logicBoolean;
    }

    public static boolean isStaticFalse(LogicBoolean logicBoolean) {
        return logicBoolean == falseBoolean;
    }

    public static boolean isStaticTrue(LogicBoolean logicBoolean) {
        return logicBoolean == trueBoolean;
    }

    public static boolean isStaticNull(LogicBoolean logicBoolean) {
        return logicBoolean instanceof UnitReference.NullUnitReference;
    }

    public static boolean isStaticNumber(LogicBoolean logicBoolean) {
        return logicBoolean instanceof StaticValueBoolean;
    }

    public static Float getStaticNumber(LogicBoolean logicBoolean) {
        if (logicBoolean instanceof StaticValueBoolean) {
            return Float.valueOf(((StaticValueBoolean) logicBoolean).staticNumber);
        }
        return null;
    }

    public static float getKnownStaticNumber(LogicBoolean logicBoolean) {
        return ((StaticValueBoolean) logicBoolean).staticNumber;
    }

    public static LogicBoolean create(CustomUnitConfig customUnitConfig, String str) {
        return create(customUnitConfig, str, null);
    }

    public static LogicBoolean create(CustomUnitConfig customUnitConfig, String str, LogicBoolean logicBoolean) {
        if (str == null) {
            return logicBoolean;
        }
        try {
            String lowerCase = str.toLowerCase(Locale.ENGLISH);
            if (lowerCase.equals("true")) {
                return trueBoolean;
            }
            if (lowerCase.equals("false")) {
                return falseBoolean;
            }
            if (lowerCase.startsWith("if ")) {
                return LogicBooleanLoader.parseBooleanBlock(customUnitConfig, str.substring("if ".length()), true);
            }
            throw new BooleanParseException("Cannot parse:'" + str + "' expected true, false or statement starting with 'if'");
        } catch (RuntimeException e) {
            throw new RuntimeException("LogicBoolean - Error: " + e.getMessage() + ", [parsing: '" + str + "']", e);
        }
    }

    public ReturnType getReturnType() {
        return ReturnType.bool;
    }

    public String valueToStringDebug(OrderableUnit orderableUnit) {
        ReturnType returnType = getReturnType();
        if (returnType == ReturnType.number) {
            return Utility.padString(readNumber(orderableUnit), 2);
        }
        if (returnType == ReturnType.unit) {
            return BaseUnit.serialize(readUnit(orderableUnit));
        }
        if (returnType == ReturnType.string) {
            return readString(orderableUnit);
        }
        if (ReturnType.isArrayType(returnType)) {
            return LogicString.arraySummaryToString(orderableUnit, this);
        }
        return read(orderableUnit) ? "true" : "false";
    }

    public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
    }

    public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
        validate(str, str2, str3, logicBooleanContext, z);
        return this;
    }

    public float readNumber(OrderableUnit orderableUnit) {
        return -9999.0f;
    }

    public String readString(OrderableUnit orderableUnit) {
        return null;
    }

    public BaseUnit readUnit(OrderableUnit orderableUnit) {
        return null;
    }

    public int getArraySize(OrderableUnit orderableUnit) {
        return 0;
    }

    public LogicBoolean readArrayElement(OrderableUnit orderableUnit, int i) {
        return null;
    }

    public String getDebugDetails(CustomUnit customUnit) {
        return getMatchFailReasonForPlayer(customUnit) + "==" + (read(customUnit) ? "true" : "false");
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$StaticBoolean.class */
    public abstract static class StaticBoolean extends LogicBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean with(String str) {
            return this;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$StaticBooleanTrue.class */
    public static final class StaticBooleanTrue extends StaticBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "true";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$StaticBooleanFalse.class */
    public static final class StaticBooleanFalse extends StaticBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "false";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return false;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$JoinerBoolean.class */
    public static abstract class JoinerBoolean extends LogicBoolean {
        LogicBoolean[] children;

        public abstract String type();

        public static JoinerBoolean getNewJoiner(String str) {
            JoinerBoolean mathDivideJoinerBoolean;
            if (str.equals("or")) {
                mathDivideJoinerBoolean = new OrBoolean();
            } else if (str.equals("and")) {
                mathDivideJoinerBoolean = new AndBoolean();
            } else if (str.equals("==")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.CompareEqualBoolean();
            } else if (str.equals("!=")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.CompareNotEqualBoolean();
            } else if (str.equals(">")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.CompareGreaterThanNumbers();
            } else if (str.equals(">=")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.CompareGreaterThanOrEqualNumbers();
            } else if (str.equals("<")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.CompareLessThanNumbers();
            } else if (str.equals("<=")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.CompareLessThanOrEqualNumbers();
            } else if (str.equals("%")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.MathModulusJoinerBoolean();
            } else if (str.equals("+")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.MathAddJoinerBoolean();
            } else if (str.equals("-")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.MathSubtractJoinerBoolean();
            } else if (str.equals("*")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.MathMultiplyJoinerBoolean();
            } else if (str.equals("/")) {
                mathDivideJoinerBoolean = new CompareJoinerBoolean.MathDivideJoinerBoolean();
            } else {
                throw new BooleanParseException("Unknown joiner:'" + str + "'");
            }
            return mathDivideJoinerBoolean;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            String str = "(";
            boolean z = true;
            for (LogicBoolean logicBoolean : this.children) {
                if (z) {
                    z = false;
                } else {
                    str = str + " " + type() + " ";
                }
                str = str + logicBoolean.getMatchFailReasonForPlayer(orderableUnit);
            }
            return str + ")";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getDebugDetails(CustomUnit customUnit) {
            String str = "(";
            boolean z = true;
            for (LogicBoolean logicBoolean : this.children) {
                if (z) {
                    z = false;
                } else {
                    str = str + " " + type() + " ";
                }
                str = str + logicBoolean.getDebugDetails(customUnit);
            }
            return str + ")";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public final void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            return this;
        }

        public boolean requireBooleanChildren() {
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$OrBoolean.class */
    public static final class OrBoolean extends JoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "or";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            for (LogicBoolean logicBoolean : this.children) {
                if (logicBoolean.read(orderableUnit)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$AndBoolean.class */
    public static final class AndBoolean extends JoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "and";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            for (LogicBoolean logicBoolean : this.children) {
                if (!logicBoolean.read(orderableUnit)) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$NotBoolean.class */
    public static final class NotBoolean extends LogicBoolean {
        LogicBoolean child;

        public NotBoolean(LogicBoolean logicBoolean) {
            this.child = logicBoolean;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return !this.child.read(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "not(" + this.child.getMatchFailReasonForPlayer(orderableUnit) + ")";
        }
    }

    public static String getAllParametersDebug(LogicBoolean logicBoolean, OrderableUnit orderableUnit) {
        String str = VariableScope.nullOrMissingString;
        LogicBooleanLoader.ParameterMapping parameters = logicBoolean.getParameters();
        for (String str2 : parameters.parameters.keySet()) {
            LogicBooleanLoader.ParameterMapping.FieldOrMethod fieldOrMethod = (LogicBooleanLoader.ParameterMapping.FieldOrMethod) parameters.parameters.get(str2);
            if (fieldOrMethod.field != null) {
                if (!str.equals(VariableScope.nullOrMissingString)) {
                    str = str + ",";
                }
                String strValueToStringDebug = null;
                Object argumentTextWithMapping = LogicBooleanLoader.getArgumentTextWithMapping(fieldOrMethod, logicBoolean);
                if (argumentTextWithMapping instanceof String) {
                    strValueToStringDebug = argumentTextWithMapping.toString();
                }
                if (argumentTextWithMapping instanceof LogicBoolean) {
                    strValueToStringDebug = ((LogicBoolean) argumentTextWithMapping).valueToStringDebug(orderableUnit);
                }
                str = str + str2 + "=" + strValueToStringDebug;
            }
        }
        return str;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$LogicNumberOnly.class */
    public abstract static class LogicNumberOnly extends LogicBoolean {
        public abstract String getName();

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public abstract float readNumber(OrderableUnit orderableUnit);

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public ReturnType getReturnType() {
            return ReturnType.number;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return getName() + "(" + getAllParametersDebug(this, orderableUnit) + ")=" + Utility.padString(readNumber(orderableUnit), 3) + VariableScope.nullOrMissingString;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$AbstractNumberBoolean.class */
    public abstract static class AbstractNumberBoolean extends LogicBoolean {

        @Parameter
        public boolean full;

        @Parameter
        public boolean empty;

        @Parameter
        public float greaterThan = -1.0f;

        @Parameter
        public float lessThan = -1.0f;

        public abstract String getName();

        public abstract float getValue(OrderableUnit orderableUnit);

        public abstract float getMaxValue(OrderableUnit orderableUnit);

        @Parameter
        public void equalTo(float f) {
            this.greaterThan = f - 1.0E-4f;
            this.lessThan = f + 1.0E-4f;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public ReturnType getReturnType() {
            if (this.greaterThan == -1.0f && this.lessThan == -1.0f && !this.full && !this.empty) {
                return ReturnType.number;
            }
            return ReturnType.bool;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (z && this.greaterThan == -1.0f && this.lessThan == -1.0f && !this.full && !this.empty) {
                throw new BooleanParseException("Expected greaterThan, lessThan, full, and/or empty to be set for function:" + str + " to return a boolean");
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return getValue(orderableUnit);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            String str = getName() + "=" + Utility.padString(getValue(orderableUnit), 3) + VariableScope.nullOrMissingString;
            if (this.full) {
                str = str + "(full)";
            }
            if (this.empty) {
                str = str + "(empty)";
            }
            if (this.greaterThan != -1.0f) {
                str = str + ">" + Utility.padString(this.greaterThan, 3);
            }
            if (this.lessThan != -1.0f) {
                str = str + "<" + Utility.padString(this.lessThan, 3);
            }
            return str;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            float value = getValue(orderableUnit);
            boolean z = true;
            if (this.full && value < getMaxValue(orderableUnit)) {
                z = false;
            }
            if (this.empty && value > 0.0f) {
                z = false;
            }
            if (this.greaterThan != -1.0f && value <= this.greaterThan) {
                z = false;
            }
            if (this.lessThan != -1.0f && value >= this.lessThan) {
                z = false;
            }
            return z;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$TimeBoolean.class */
    public abstract static class TimeBoolean extends LogicBoolean {

        @Parameter
        public float laterThanSeconds = -1.0f;

        @Parameter
        public float withinSeconds = -1.0f;

        public abstract String getName();

        public abstract int getTime(OrderableUnit orderableUnit);

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public ReturnType getReturnType() {
            if (this.laterThanSeconds == -1.0f && this.withinSeconds == -1.0f) {
                return ReturnType.number;
            }
            return ReturnType.bool;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (z && this.laterThanSeconds == -1.0f && this.withinSeconds == -1.0f) {
                throw new BooleanParseException("Expended laterThanSeconds, or withinSeconds argument for function:" + str + " to return a boolean");
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            String str = getName() + "=" + msToSecondsString(GameEngine.getInstance().lastTick - getTime(orderableUnit));
            if (this.laterThanSeconds != -1.0f) {
                str = str + ">" + msToSecondsString(this.laterThanSeconds * 1000.0f);
            }
            if (this.withinSeconds != -1.0f) {
                str = str + "<" + msToSecondsString(this.withinSeconds * 1000.0f);
            }
            return str;
        }

        private String msToSecondsString(float f) {
            return Utility.min(f / 1000.0f) + "s";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return (GameEngine.getInstance().lastTick - getTime(orderableUnit)) * 0.001f;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            int time = getTime(orderableUnit);
            boolean z = true;
            GameEngine gameEngine = GameEngine.getInstance();
            if (this.withinSeconds > 0.0f && gameEngine.lastTick - (this.withinSeconds * 1000.0f) > time) {
                z = false;
            }
            if (this.laterThanSeconds > 0.0f && gameEngine.lastTick - (this.laterThanSeconds * 1000.0f) < time) {
                z = false;
            }
            return z;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBoolean$StaticValueBoolean.class */
    public static final class StaticValueBoolean extends LogicNumberOnly {
        public static final StaticValueBoolean static_0 = new StaticValueBoolean(0.0f);
        public static final StaticValueBoolean static_neg1 = new StaticValueBoolean(-1.0f);
        public static final StaticValueBoolean static_1 = new StaticValueBoolean(1.0f);
        float staticNumber;

        public static StaticValueBoolean getStaticNumber(String str) {
            try {
                return getStaticNumber(Float.parseFloat(str));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Error reading number: " + str, e);
            }
        }

        public static StaticValueBoolean getStaticNumber(float f) {
            return f == 0.0f ? static_0 : f == 1.0f ? static_1 : f == -1.0f ? static_neg1 : new StaticValueBoolean(f);
        }

        StaticValueBoolean(float f) {
            this.staticNumber = f;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return VariableScope.nullOrMissingString + this.staticNumber;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public final float readNumber(OrderableUnit orderableUnit) {
            return this.staticNumber;
        }

        public float getStaticValue() {
            return this.staticNumber;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return Utility.padString(this.staticNumber, 3);
        }
    }

    public LogicBooleanLoader.LogicBooleanContext createContext() {
        return LogicBooleanLoader.voidContextReader;
    }

    public void throwVoidReturnError(String str) {
        throw new RuntimeException("Function or object:'" + str + "' cannot be returned");
    }

    public LogicBoolean setChild(LogicBoolean logicBoolean) {
        throw new RuntimeException("setChild not supported on: " + getClass().getSimpleName());
    }

    public void setParent(LogicBoolean logicBoolean) {
    }
}
