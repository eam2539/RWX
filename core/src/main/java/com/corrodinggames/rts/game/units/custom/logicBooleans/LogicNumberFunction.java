package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.ConfigException;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;

import java.util.ArrayList;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction.class */
public abstract class LogicNumberFunction extends LogicBoolean.LogicNumberOnly {

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$LogicNumberFunctionRawArgs.class */
    abstract static class LogicNumberFunctionRawArgs extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number)
        public LogicBoolean value;

        public abstract float doFunction(float f);

        LogicNumberFunctionRawArgs() {
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
            if (str == null || VariableScope.nullOrMissingString.equals(str)) {
                validateNumberOfArguments(0);
                return;
            }
            ArrayList arrayListA = StringUtils.a(str, ",", false);
            validateNumberOfArguments(arrayListA.size());
            this.value = LogicBooleanLoader.parseNumberBlock(customUnitConfig, (String) arrayListA.get(0));
            if (this.value == null) {
                throw new BooleanParseException("Expected non-null argument");
            }
        }

        public void validateNumberOfArguments(int i) {
            if (i != 1) {
                throw new BooleanParseException("Expected 1 argument");
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            validate(str, str2, str3, logicBooleanContext, z);
            if (this.value instanceof LogicBoolean.StaticValueBoolean) {
                return new StaticValueBoolean(doFunction(((StaticValueBoolean) this.value).getStaticValue()));
            }
            return this;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this.value == null) {
                throw new BooleanParseException("Expected parameters missing");
            }
            if (z) {
                throw new BooleanParseException("Expected function:" + str + " to return a boolean, but it returns a number");
            }
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return getName() + "(" + this.value.getMatchFailReasonForPlayer(orderableUnit) + ")";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionInt.class */
    public static class FunctionInt extends LogicNumberFunctionRawArgs {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return super.getMatchFailReasonForPlayer(orderableUnit);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs
        public /* bridge */ /* synthetic */ void validateNumberOfArguments(int i) {
            super.validateNumberOfArguments(i);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
            super.setArgumentsRaw(str, customUnitConfig, str2);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "Int";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return doFunction(this.value.readNumber(orderableUnit));
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs
        public float doFunction(float f) {
            return (int) f;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionSquareRoot.class */
    public static class FunctionSquareRoot extends LogicNumberFunctionRawArgs {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return super.getMatchFailReasonForPlayer(orderableUnit);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs
        public /* bridge */ /* synthetic */ void validateNumberOfArguments(int i) {
            super.validateNumberOfArguments(i);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
            super.setArgumentsRaw(str, customUnitConfig, str2);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "SquareRoot";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return doFunction(this.value.readNumber(orderableUnit));
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs
        public float doFunction(float f) {
            return Utility.squareRoot(f);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionCos.class */
    public static class FunctionCos extends LogicNumberFunctionRawArgs {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return super.getMatchFailReasonForPlayer(orderableUnit);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs
        public /* bridge */ /* synthetic */ void validateNumberOfArguments(int i) {
            super.validateNumberOfArguments(i);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
            super.setArgumentsRaw(str, customUnitConfig, str2);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "cos";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return doFunction(this.value.readNumber(orderableUnit));
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs
        public float doFunction(float f) {
            return Utility.fastCos(f);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionSin.class */
    public static class FunctionSin extends LogicNumberFunctionRawArgs {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return super.getMatchFailReasonForPlayer(orderableUnit);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs
        public /* bridge */ /* synthetic */ void validateNumberOfArguments(int i) {
            super.validateNumberOfArguments(i);
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public /* bridge */ /* synthetic */ void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
            super.setArgumentsRaw(str, customUnitConfig, str2);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "sin";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return doFunction(this.value.readNumber(orderableUnit));
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicNumberFunction.LogicNumberFunctionRawArgs
        public float doFunction(float f) {
            return Utility.fastSin(f);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionDistanceSquared.class */
    public static class FunctionDistanceSquared extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0, required = true)
        public LogicBoolean x1;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 1, required = true)
        public LogicBoolean y1;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 2, required = true)
        public LogicBoolean x2;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 3, required = true)
        public LogicBoolean y2;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "DistanceSquared";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            float number = this.x1.readNumber(orderableUnit);
            float number2 = this.y1.readNumber(orderableUnit);
            float number3 = this.x2.readNumber(orderableUnit);
            float number4 = this.y2.readNumber(orderableUnit);
            return ((number - number3) * (number - number3)) + ((number2 - number4) * (number2 - number4));
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionDistanceBetweenSquared.class */
    public static class FunctionDistanceBetweenSquared extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.unit, positional = 0, required = true)
        public LogicBoolean unit1;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.unit, positional = 1, required = true)
        public LogicBoolean unit2;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "DistanceBetweenSquared";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            BaseUnit unit = this.unit1.readUnit(orderableUnit);
            if (unit == null) {
                return 0.0f;
            }
            float f = unit.posX;
            float f2 = unit.posY;
            BaseUnit unit2 = this.unit2.readUnit(orderableUnit);
            if (unit2 == null) {
                return 0.0f;
            }
            return Utility.distanceSq(f, f2, unit2.posX, unit2.posY);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionDistance.class */
    public static class FunctionDistance extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0, required = true)
        public LogicBoolean x1;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 1, required = true)
        public LogicBoolean y1;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 2, required = true)
        public LogicBoolean x2;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 3, required = true)
        public LogicBoolean y2;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "Distance";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return Utility.distance(this.x1.readNumber(orderableUnit), this.y1.readNumber(orderableUnit), this.x2.readNumber(orderableUnit), this.y2.readNumber(orderableUnit));
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionDirection.class */
    public static class FunctionDirection extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0, required = true)
        public LogicBoolean x1;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 1, required = true)
        public LogicBoolean y1;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 2, required = true)
        public LogicBoolean x2;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 3, required = true)
        public LogicBoolean y2;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "Direction";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            return Utility.getAngleBetweenPoints(this.x1.readNumber(orderableUnit), this.y1.readNumber(orderableUnit), this.x2.readNumber(orderableUnit), this.y2.readNumber(orderableUnit));
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionDistanceBetween.class */
    public static class FunctionDistanceBetween extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.unit, positional = 0, required = true)
        public LogicBoolean unit1;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.unit, positional = 1, required = true)
        public LogicBoolean unit2;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "DistanceBetween";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            BaseUnit unit = this.unit1.readUnit(orderableUnit);
            if (unit == null) {
                return 0.0f;
            }
            float f = unit.posX;
            float f2 = unit.posY;
            BaseUnit unit2 = this.unit2.readUnit(orderableUnit);
            if (unit2 == null) {
                return 0.0f;
            }
            return Utility.distance(f, f2, unit2.posX, unit2.posY);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionDirectionBetween.class */
    public static class FunctionDirectionBetween extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.unit, positional = 0, required = true)
        public LogicBoolean unit1;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.unit, positional = 1, required = true)
        public LogicBoolean unit2;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "DirectionBetween";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            BaseUnit unit = this.unit1.readUnit(orderableUnit);
            if (unit == null) {
                return 0.0f;
            }
            float f = unit.posX;
            float f2 = unit.posY;
            BaseUnit unit2 = this.unit2.readUnit(orderableUnit);
            if (unit2 == null) {
                return 0.0f;
            }
            return Utility.getAngleBetweenPoints(f, f2, unit2.posX, unit2.posY);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$CreateMarker.class */
    public static class CreateMarker extends UnitReference {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0, required = true)
        public LogicBoolean x;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 1, required = true)
        public LogicBoolean y;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 2, required = false)
        public LogicBoolean height;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number)
        public LogicBoolean teamId;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number)
        public LogicBoolean dir;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void validate(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            super.validate(str, str2, str3, logicBooleanContext, z);
            if (this.height == null) {
                this.height = LogicBoolean.StaticValueBoolean.static_0;
            }
            if (this.dir == null) {
                this.dir = LogicBoolean.StaticValueBoolean.static_0;
            }
            if (this.teamId == null) {
                this.teamId = LogicBoolean.StaticValueBoolean.static_neg1;
            }
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public BaseUnit getSingleRaw(OrderableUnit orderableUnit) {
            float number = this.x.readNumber(orderableUnit);
            float number2 = this.y.readNumber(orderableUnit);
            float number3 = this.height.readNumber(orderableUnit);
            float number4 = this.dir.readNumber(orderableUnit);
            PlayerTeam playerTeamK = PlayerTeam.k((int) this.teamId.readNumber(orderableUnit));
            if (playerTeamK == null) {
                playerTeamK = PlayerTeam.TEAM_ALL;
            }
            OrderableUnit orderableUnit2 = playerTeamK.teamPrimaryUnit;
            orderableUnit2.rotationSpeed = number4;
            orderableUnit2.posX = number;
            orderableUnit2.posY = number2;
            orderableUnit2.posZ = number3;
            return orderableUnit2;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.UnitReference
        public String getClassDebugName() {
            return "createMarker";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionRnd.class */
    public static class FunctionRnd extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0, required = true)
        public LogicBoolean min;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 1, required = true)
        public LogicBoolean max;
        int randomIndex;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public void forMeta(CustomUnitConfig customUnitConfig) {
            if (customUnitConfig == null) {
                throw new ConfigException("FunctionRnd requires metadata");
            }
            customUnitConfig.number++;
            this.randomIndex = customUnitConfig.number;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "Rnd";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            float number = this.min.readNumber(orderableUnit);
            float number2 = this.max.readNumber(orderableUnit);
            int i = 0;
            if (orderableUnit != null) {
                i = orderableUnit.unitCounter;
            }
            return Utility.getRandomFloat(number, number2, this.randomIndex + i);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionMax.class */
    public static class FunctionMax extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0, required = true)
        public LogicBoolean a;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 1, required = true)
        public LogicBoolean b;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "Max";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            float number = this.a.readNumber(orderableUnit);
            float number2 = this.b.readNumber(orderableUnit);
            if (number > number2) {
                return number;
            }
            return number2;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionMin.class */
    public static class FunctionMin extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 0, required = true)
        public LogicBoolean a;

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number, positional = 1, required = true)
        public LogicBoolean b;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "Min";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            float number = this.a.readNumber(orderableUnit);
            float number2 = this.b.readNumber(orderableUnit);
            if (number < number2) {
                return number;
            }
            return number2;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicNumberFunction$FunctionLength.class */
    public static class FunctionLength extends LogicNumberFunction {

        @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.string, positional = 0, required = true)
        public LogicBoolean a;

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly
        public String getName() {
            return "Length";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.LogicNumberOnly, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            String readString = this.a.readString(orderableUnit);
            if (readString == null) {
                return 0.0f;
            }
            return readString.length();
        }
    }
}
