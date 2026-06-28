package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean.class */
public abstract class CompareJoinerBoolean extends LogicBoolean.JoinerBoolean {
    @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
    public boolean requireBooleanChildren() {
        return false;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareBooleanOnly.class */
    public abstract static class CompareBooleanOnly extends CompareJoinerBoolean {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.CompareJoinerBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public boolean requireBooleanChildren() {
            return false;
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            if (this.children.length < 2) {
                throw new BooleanParseException("Expected 2 or more elements while using " + type() + " have: " + this.children.length);
            }
            boolean z2 = false;
            LogicBoolean.ReturnType returnType = null;
            LogicBoolean[] logicBooleanArr = this.children;
            int length = logicBooleanArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                LogicBoolean logicBoolean = logicBooleanArr[i];
                if (!LogicBoolean.isStaticNull(logicBoolean)) {
                    returnType = logicBoolean.getReturnType();
                    break;
                }
                z2 = true;
                i++;
            }
            if (returnType == null) {
                returnType = LogicBoolean.ReturnType.unit;
            }
            if (z2 && returnType != LogicBoolean.ReturnType.string && returnType != LogicBoolean.ReturnType.unit) {
                throw new BooleanParseException(returnType + " cannot FastArrayList compared to null using " + type());
            }
            for (LogicBoolean logicBoolean2 : this.children) {
                LogicBoolean.ReturnType returnType2 = logicBoolean2.getReturnType();
                if (returnType != returnType2 && !LogicBoolean.isStaticNull(logicBoolean2)) {
                    throw new BooleanParseException("Mixing types: " + returnType + " and " + returnType2 + " while using " + type());
                }
            }
            if (returnType == LogicBoolean.ReturnType.number) {
                if (this instanceof CompareEqualBoolean) {
                    CompareEqualNumbers compareEqualNumbers = new CompareEqualNumbers();
                    compareEqualNumbers.children = this.children;
                    return compareEqualNumbers.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
                }
                if (this instanceof CompareNotEqualBoolean) {
                    CompareNotEqualNumbers compareNotEqualNumbers = new CompareNotEqualNumbers();
                    compareNotEqualNumbers.children = this.children;
                    return compareNotEqualNumbers.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
                }
                throw new BooleanParseException("Unhandled compare type: " + type() + " while using numbers");
            }
            if (returnType == LogicBoolean.ReturnType.bool) {
                LogicBoolean.StaticBoolean staticBoolean = null;
                for (LogicBoolean logicBoolean3 : this.children) {
                    if (logicBoolean3 instanceof LogicBoolean.StaticBoolean) {
                        staticBoolean = (LogicBoolean.StaticBoolean) logicBoolean3;
                    }
                }
                if (staticBoolean == null || (this instanceof CompareEqualBoolean)) {
                }
                return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
            }
            if (returnType == LogicBoolean.ReturnType.string) {
                if (this instanceof CompareEqualBoolean) {
                    CompareEqualStrings compareEqualStrings = new CompareEqualStrings();
                    compareEqualStrings.children = this.children;
                    return compareEqualStrings.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
                }
                if (this instanceof CompareNotEqualBoolean) {
                    CompareNotEqualStrings compareNotEqualStrings = new CompareNotEqualStrings();
                    compareNotEqualStrings.children = this.children;
                    return compareNotEqualStrings.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
                }
                throw new BooleanParseException("Unhandled compare type: " + type() + " while using numbers");
            }
            if (returnType == LogicBoolean.ReturnType.unit) {
                if (this instanceof CompareEqualBoolean) {
                    CompareEqualUnits compareEqualUnits = new CompareEqualUnits();
                    compareEqualUnits.children = this.children;
                    return compareEqualUnits.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
                }
                if (this instanceof CompareNotEqualBoolean) {
                    CompareNotEqualUnits compareNotEqualUnits = new CompareNotEqualUnits();
                    compareNotEqualUnits.children = this.children;
                    return compareNotEqualUnits.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
                }
                throw new BooleanParseException("Unhandled compare type: " + type() + " while using numbers");
            }
            throw new BooleanParseException("Unhandled type: " + returnType + " while using " + type());
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareEqualBoolean.class */
    public static final class CompareEqualBoolean extends CompareBooleanOnly {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "==";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = this.children[0].read(orderableUnit);
            for (int i = 1; i < this.children.length; i++) {
                if (this.children[i].read(orderableUnit) != z) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareNotEqualBoolean.class */
    public static final class CompareNotEqualBoolean extends CompareBooleanOnly {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "!=";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            boolean z = this.children[0].read(orderableUnit);
            for (int i = 1; i < this.children.length; i++) {
                boolean z2 = this.children[i].read(orderableUnit);
                if (z2 == z) {
                    return false;
                }
                z = z2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareNumbers.class */
    public abstract static class CompareNumbers extends CompareJoinerBoolean {
        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            boolean z2 = true;
            for (LogicBoolean logicBoolean : this.children) {
                if (logicBoolean.getReturnType() != LogicBoolean.ReturnType.number) {
                    throw new BooleanParseException("Non-number type while using " + type());
                }
                if (!(logicBoolean instanceof LogicBoolean.StaticValueBoolean)) {
                    z2 = false;
                }
            }
            if (z2) {
            }
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareEqualNumbers.class */
    public final class CompareEqualNumbers extends CompareNumbers {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "==";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            float number = logicBooleanArr[0].readNumber(orderableUnit);
            for (int i = 1; i < logicBooleanArr.length; i++) {
                float number2 = logicBooleanArr[i].readNumber(orderableUnit);
                if (number2 - 1.0E-4f > number || number2 + 1.0E-4f < number) {
                    return false;
                }
                number = number2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareNotEqualNumbers.class */
    public final class CompareNotEqualNumbers extends CompareNumbers {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "!=";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            float number = logicBooleanArr[0].readNumber(orderableUnit);
            for (int i = 1; i < logicBooleanArr.length; i++) {
                float number2 = logicBooleanArr[i].readNumber(orderableUnit);
                if (number2 - 1.0E-4f <= number && number2 + 1.0E-4f >= number) {
                    return false;
                }
                number = number2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareEqualStrings.class */
    public final class CompareEqualStrings extends CompareJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "==";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            String string = logicBooleanArr[0].readString(orderableUnit);
            if (string == null) {
                string = VariableScope.nullOrMissingString;
            }
            for (int i = 1; i < logicBooleanArr.length; i++) {
                String string2 = logicBooleanArr[i].readString(orderableUnit);
                if (string2 == null) {
                    string2 = VariableScope.nullOrMissingString;
                }
                if (!string.equals(string2)) {
                    return false;
                }
                string = string2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareNotEqualStrings.class */
    public final class CompareNotEqualStrings extends CompareJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "!=";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            String string = logicBooleanArr[0].readString(orderableUnit);
            if (string == null) {
                string = VariableScope.nullOrMissingString;
            }
            for (int i = 1; i < logicBooleanArr.length; i++) {
                String string2 = logicBooleanArr[i].readString(orderableUnit);
                if (string2 == null) {
                    string2 = VariableScope.nullOrMissingString;
                }
                if (string.equals(string2)) {
                    return false;
                }
                string = string2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareEqualUnits.class */
    public final class CompareEqualUnits extends CompareJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "==";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            BaseUnit unit = logicBooleanArr[0].readUnit(orderableUnit);
            for (int i = 1; i < logicBooleanArr.length; i++) {
                BaseUnit unit2 = logicBooleanArr[i].readUnit(orderableUnit);
                if (unit != unit2) {
                    return false;
                }
                unit = unit2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareNotEqualUnits.class */
    public final class CompareNotEqualUnits extends CompareJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "!=";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            BaseUnit unit = logicBooleanArr[0].readUnit(orderableUnit);
            for (int i = 1; i < logicBooleanArr.length; i++) {
                BaseUnit unit2 = logicBooleanArr[i].readUnit(orderableUnit);
                if (unit == unit2) {
                    return false;
                }
                unit = unit2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareGreaterThanNumbers.class */
    public static final class CompareGreaterThanNumbers extends CompareNumbers {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return ">";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            float number = logicBooleanArr[0].readNumber(orderableUnit);
            for (int i = 1; i < logicBooleanArr.length; i++) {
                float number2 = logicBooleanArr[i].readNumber(orderableUnit);
                if (number <= number2) {
                    return false;
                }
                number = number2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareLessThanNumbers.class */
    public static final class CompareLessThanNumbers extends CompareNumbers {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "<";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            float number = logicBooleanArr[0].readNumber(orderableUnit);
            for (int i = 1; i < logicBooleanArr.length; i++) {
                float number2 = logicBooleanArr[i].readNumber(orderableUnit);
                if (number >= number2) {
                    return false;
                }
                number = number2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareGreaterThanOrEqualNumbers.class */
    public static final class CompareGreaterThanOrEqualNumbers extends CompareNumbers {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return ">=";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            float number = logicBooleanArr[0].readNumber(orderableUnit);
            for (int i = 1; i < logicBooleanArr.length; i++) {
                float number2 = logicBooleanArr[i].readNumber(orderableUnit);
                if (number < number2) {
                    return false;
                }
                number = number2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$CompareLessThanOrEqualNumbers.class */
    public static final class CompareLessThanOrEqualNumbers extends CompareNumbers {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "<=";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            LogicBoolean[] logicBooleanArr = this.children;
            float number = logicBooleanArr[0].readNumber(orderableUnit);
            for (int i = 1; i < logicBooleanArr.length; i++) {
                float number2 = logicBooleanArr[i].readNumber(orderableUnit);
                if (number > number2) {
                    return false;
                }
                number = number2;
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$MathJoinerBoolean.class */
    public abstract static class MathJoinerBoolean extends CompareJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.number;
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            if (this instanceof MathAddJoinerBoolean) {
                boolean z2 = false;
                for (LogicBoolean logicBoolean : this.children) {
                    if (logicBoolean.getReturnType() == LogicBoolean.ReturnType.string) {
                        z2 = true;
                    }
                }
                if (z2) {
                    StringJoinerBoolean stringJoinerBoolean = new StringJoinerBoolean();
                    stringJoinerBoolean.children = this.children;
                    return stringJoinerBoolean.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
                }
            }
            boolean z3 = true;
            for (LogicBoolean logicBoolean2 : this.children) {
                if (!(logicBoolean2 instanceof LogicBoolean.StaticValueBoolean)) {
                    z3 = false;
                }
                if (logicBoolean2.getReturnType() != LogicBoolean.ReturnType.number) {
                    throw new BooleanParseException("Unexpected type while using " + type() + " got: " + logicBoolean2.getReturnType().name());
                }
            }
            if (z3) {
                return new StaticValueBoolean(readNumber(null));
            }
            if (z) {
                throw new BooleanParseException("Cannot return number here, expected boolean");
            }
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$StringJoinerBoolean.class */
    public final class StringJoinerBoolean extends CompareJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "+";
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean validateAndOptimize(String str, String str2, String str3, LogicBooleanLoader.LogicBooleanContext logicBooleanContext, boolean z) {
            for (int i = 0; i < this.children.length; i++) {
                if (this.children[i].getReturnType() != LogicBoolean.ReturnType.string) {
                    this.children[i] = LogicString.StringCast.createStringCast(this.children[i]);
                }
            }
            boolean z2 = true;
            for (LogicBoolean logicBoolean : this.children) {
                if (!(logicBoolean instanceof LogicString.StaticString)) {
                    z2 = false;
                }
            }
            if (z2) {
                return new LogicString.StaticString(readString(null));
            }
            return super.validateAndOptimize(str, str2, str3, logicBooleanContext, z);
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String readString(OrderableUnit orderableUnit) {
            String string = this.children[0].readString(orderableUnit);
            for (int i = 1; i < this.children.length; i++) {
                string = string + this.children[i].readString(orderableUnit);
            }
            return string;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.string;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$MathAddJoinerBoolean.class */
    public static final class MathAddJoinerBoolean extends MathJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "+";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            float number = this.children[0].readNumber(orderableUnit);
            for (int i = 1; i < this.children.length; i++) {
                number += this.children[i].readNumber(orderableUnit);
            }
            return number;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$MathSubtractJoinerBoolean.class */
    public static final class MathSubtractJoinerBoolean extends MathJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "-";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            float number = this.children[0].readNumber(orderableUnit);
            for (int i = 1; i < this.children.length; i++) {
                number -= this.children[i].readNumber(orderableUnit);
            }
            return number;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$MathMultiplyJoinerBoolean.class */
    public static final class MathMultiplyJoinerBoolean extends MathJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "*";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            float number = this.children[0].readNumber(orderableUnit);
            for (int i = 1; i < this.children.length; i++) {
                number *= this.children[i].readNumber(orderableUnit);
            }
            return number;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$MathModulusJoinerBoolean.class */
    public static final class MathModulusJoinerBoolean extends MathJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "%";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            float number = this.children[0].readNumber(orderableUnit);
            for (int i = 1; i < this.children.length; i++) {
                number %= this.children[i].readNumber(orderableUnit);
            }
            return number;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/CompareJoinerBoolean$MathDivideJoinerBoolean.class */
    public static final class MathDivideJoinerBoolean extends MathJoinerBoolean {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean.JoinerBoolean
        public String type() {
            return "/";
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public float readNumber(OrderableUnit orderableUnit) {
            float number = this.children[0].readNumber(orderableUnit);
            for (int i = 1; i < this.children.length; i++) {
                number /= this.children[i].readNumber(orderableUnit);
            }
            return number;
        }
    }
}
