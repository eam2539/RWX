package com.corrodinggames.rts.game.units.custom.logicBooleans;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader.class */
public class LogicBooleanLoader {
    public static final boolean TRACE = false;

    /* JADX INFO: renamed from: patternSingleQuote */
    static Pattern singleQuotePattern = Pattern.compile("'(.*)'");

    /* JADX INFO: renamed from: patternDoubleQuote */
    static Pattern doubleQuotePattern = Pattern.compile("\"(.*)\"");

    /* JADX INFO: renamed from: patternInteger */
    static Pattern integerPattern = Pattern.compile("(-?\\d*)");

    /* JADX INFO: renamed from: patternFloat */
    static Pattern floatPattern = Pattern.compile("(-?\\d*\\.\\d*)");
    static final LogicBooleanContext defaultContextReader = new DefaultContextReader();
    static final LogicBooleanContext voidContextReader = new VoidContextReader(null);
    static final LogicBooleanContext voidNumberContextReader = new VoidContextReader("Number");
    static final LogicBooleanContext voidBoolContextReader = new VoidContextReader("Bool");
    static final LogicBooleanContext voidArrayContextReader = new VoidContextReader("Array element");
    static final LogicBooleanContext numberArrayContextReader = new ArrayContextReader(LogicBoolean.ReturnType.numberArray);
    static final LogicBooleanContext boolArrayContextReader = new ArrayContextReader(LogicBoolean.ReturnType.boolArray);
    static final LogicBooleanContext unitArrayContextReader = new ArrayContextReader(LogicBoolean.ReturnType.unitArray);

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$DefaultContextReader.class */
    public static final class DefaultContextReader extends LogicBooleanContextWithDefault {
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$LogicBooleanContext.class */
    public interface LogicBooleanContext {
        LogicBoolean parseNextElementInChain(String str, CustomUnitConfig customUnitConfig, String str2, boolean z, String str3, String str4, LogicBoolean logicBoolean);
    }

    public static LogicBoolean parseBooleanBlock(CustomUnitConfig customUnitConfig, String str, boolean z) {
        String str2;
        int iB;
        ArrayList<String> arrayListA;
        int iA = StringUtils.a(str);
        if (iA != 0) {
            if (iA > 0) {
                throw new RuntimeException("Brackets unbalanced for: '" + str + "'. A '(' was not closed.");
            }
            if (iA < 0) {
                throw new RuntimeException("Brackets unbalanced for: '" + str + "'. Too many ')'.");
            }
        }
        String strBreakOuterLayerBrackets = breakOuterLayerBrackets(str.trim());
        if (strBreakOuterLayerBrackets.length() > 1 && strBreakOuterLayerBrackets.charAt(0) == '-') {
            boolean z2 = false;
            for (int i = 1; i < strBreakOuterLayerBrackets.length(); i++) {
                char cCharAt = strBreakOuterLayerBrackets.charAt(i);
                if (cCharAt != ' ') {
                    z2 = !Character.isDigit(cCharAt);
                }
            }
            if (z2) {
                strBreakOuterLayerBrackets = "0" + strBreakOuterLayerBrackets;
            }
        }
        String lowerCase = strBreakOuterLayerBrackets.toLowerCase(Locale.ROOT);
        String[] strArr = {"==", "!=", "<=", ">=", "<", ">"};
        String[] strArr2 = {"or", "and", "==", "!=", "<=", ">=", "<", ">", "%", "-", "+", "*", "/", "="};
        for (String s : strArr2) {
            String str3 = s;
            if (Utility.containsSubstring(lowerCase, str3)) {
                boolean z3 = false;
                boolean z4 = false;
                if (str3.equals("and") || str3.equals("or")) {
                    z3 = true;
                    z4 = true;
                }
                if (str3.equals("<>")) {
                    int iA2 = StringUtils.a(strBreakOuterLayerBrackets, 0, strArr);
                    arrayListA = new ArrayList<>();
                    arrayListA.add(strBreakOuterLayerBrackets.substring(0, iA2));
                    int length2 = iA2 + 2;
                    if (length2 > strBreakOuterLayerBrackets.length() - 1) {
                        length2 = strBreakOuterLayerBrackets.length() - 1;
                    }
                    String strSubstring = strBreakOuterLayerBrackets.substring(iA2, length2);
                    if (!strSubstring.endsWith("=")) {
                        strSubstring = strSubstring.substring(0, 1);
                    }
                    arrayListA.add(strBreakOuterLayerBrackets.substring(iA2 + strSubstring.length()));
                    str3 = strSubstring;
                } else if (z4) {
                    arrayListA = StringUtils.a(strBreakOuterLayerBrackets, str3, z3, true);
                } else {
                    arrayListA = StringUtils.a(strBreakOuterLayerBrackets, str3, z3, false);
                }
                if (arrayListA.size() == 1) {
                    continue;
                } else {
                    if (str3.equals("=")) {
                        throw new RuntimeException("Unexpected assignment operator: '=', use '==' for comparison");
                    }
                    if (!arrayListA.get(0).equals(VariableScope.nullOrMissingString) || arrayListA.size() != 2 || (!str3.equals("+") && !str3.equals("-"))) {
                        ArrayList<LogicBoolean> arrayList = new ArrayList();
                        LogicBoolean.JoinerBoolean newJoiner = LogicBoolean.JoinerBoolean.getNewJoiner(str3);
                        boolean zRequireBooleanChildren = newJoiner.requireBooleanChildren();
                        if (zRequireBooleanChildren && ((newJoiner instanceof CompareJoinerBoolean.CompareNotEqualBoolean) || (newJoiner instanceof CompareJoinerBoolean.CompareEqualBoolean))) {
                            GameEngine.log(newJoiner.type() + " was set to require boolean. Workaround triggered. requireBooleanChildren:" + newJoiner.requireBooleanChildren());
                            zRequireBooleanChildren = false;
                        }
                        int i3 = -1;
                        if (str3.equals("+") || str3.equals("-")) {
                            boolean z5 = false;
                            Iterator<String> it = arrayListA.iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                if (isEmptyIgnoringPlusMinus((String) it.next())) {
                                    z5 = true;
                                    break;
                                }
                            }
                            if (z5) {
                                ArrayList<String> arrayList2 = new ArrayList();
                                String str4 = VariableScope.nullOrMissingString;
                                for (String str5 : arrayListA) {
                                    if (isEmptyIgnoringPlusMinus(str5)) {
                                        str4 = str4 + str5 + str3;
                                    } else {
                                        if (!str4.equals(VariableScope.nullOrMissingString)) {
                                            str5 = str4 + str5;
                                            str4 = VariableScope.nullOrMissingString;
                                        }
                                        arrayList2.add(str5);
                                    }
                                }
                                if (!str4.equals(VariableScope.nullOrMissingString)) {
                                    throw new RuntimeException("Unexpected empty last element using: " + str3);
                                }
                                arrayListA = arrayList2;
                            }
                        }
                        if (arrayListA.size() != 1) {
                            for (String str6 : arrayListA) {
                                i3++;
                                if (str6.equals(VariableScope.nullOrMissingString)) {
                                    if (i3 == 0) {
                                        throw new RuntimeException("Unexpected empty element before: " + str3);
                                    }
                                    throw new RuntimeException("Unexpected empty element after: " + str3);
                                }
                                try {
                                    LogicBoolean booleanBlock = parseBooleanBlock(customUnitConfig, str6, zRequireBooleanChildren);
                                    if (booleanBlock == null) {
                                        throw new RuntimeException("null on:'" + strBreakOuterLayerBrackets + "'");
                                    }
                                    arrayList.add(booleanBlock);
                                } catch (BooleanParseException e) {
                                    throw e;
                                }
                            }
                            newJoiner.children = arrayList.toArray(new LogicBoolean[0]);
                            return newJoiner.validateAndOptimize(str3, VariableScope.nullOrMissingString, strBreakOuterLayerBrackets, null, z);
                        }
                    }
                }
            }
        }
        if (lowerCase.startsWith("not ")) {
            String strSubstring2 = strBreakOuterLayerBrackets.substring("not ".length());
            return new LogicBoolean.NotBoolean(parseBooleanBlock(customUnitConfig, strSubstring2, true)).validateAndOptimize("not", VariableScope.nullOrMissingString, strSubstring2, null, z);
        }
        if (strBreakOuterLayerBrackets.length() > 0) {
            String strTrim = strBreakOuterLayerBrackets;
            if (strTrim.startsWith("+")) {
                strTrim = strTrim.substring(1).trim();
            }
            String strC = StringUtils.c(strTrim);
            if (Utility.isNumeric(strC)) {
                if (z) {
                    throw new RuntimeException("Expected a boolean type here, not number: " + strC);
                }
                return LogicBoolean.StaticValueBoolean.getStaticNumber(strC);
            }
            String strSplit = Utility.stripQuotes(strBreakOuterLayerBrackets);
            if (strSplit != null) {
                if (z) {
                    throw new RuntimeException("Expected a boolean type here, not string: " + strBreakOuterLayerBrackets);
                }
                return new LogicString.StaticString(strSplit);
            }
        }
        boolean z6 = false;
        boolean z7 = false;
        if (strBreakOuterLayerBrackets.startsWith("self.")) {
            strBreakOuterLayerBrackets = strBreakOuterLayerBrackets.substring("self.".length());
            z6 = true;
        }
        String[] strArrB = StringUtils.b(strBreakOuterLayerBrackets, ".", false);
        FastArrayList fastArrayList = new FastArrayList();
        LogicBoolean logicBoolean = null;
        String str7 = null;
        LogicBooleanContext logicBooleanContextCreateContext = defaultContextReader;
        String strSubstring3 = null;
        int i4 = 0;
        while (i4 < strArrB.length) {
            if (strSubstring3 != null) {
                str2 = strSubstring3;
                strSubstring3 = null;
            } else {
                str2 = strArrB[i4];
            }
            if (Utility.containsChar(str2, '[') && (iB = StringUtils.b(str2, "[", 0)) != -1) {
                if (iB == 0) {
                    int iB2 = StringUtils.b(str2, "]", 0);
                    if (iB2 == -1 || iB >= iB2) {
                        throw new RuntimeException("Unexpected use of square brankets:'" + str2 + "'");
                    }
                    if (iB2 < str2.length() - 1 && iB2 > 0) {
                        String strSubstring4 = str2.substring(iB, iB2 + 1);
                        strSubstring3 = str2.substring(iB2 + 1);
                        i4--;
                        str2 = strSubstring4;
                    }
                    if (iB != 0 || iB2 != str2.length() - 1 || str2.length() < 2) {
                        throw new RuntimeException("Error reading square brankets:'" + str2 + "'");
                    }
                    str2 = "get(" + str2.substring(1, str2.length() - 1) + ")";
                } else {
                    String strSubstring5 = str2.substring(0, iB);
                    strSubstring3 = str2.substring(iB);
                    i4--;
                    str2 = strSubstring5;
                }
            }
            if (str2.equalsIgnoreCase("self")) {
                if (z7) {
                    throw new RuntimeException("No field:'" + str2 + "' globals");
                }
                if (strArrB.length == 1) {
                    return UnitReference.selfUnitReference;
                }
                z6 = true;
            } else if (i4 == 0 && str2.equalsIgnoreCase("game")) {
                z7 = true;
            } else {
                boolean z8 = i4 == strArrB.length - 1;
                String str8 = null;
                if (z6) {
                    str8 = "self.";
                }
                if (z7) {
                    str8 = "game.";
                }
                boolean z9 = z;
                if (!z8) {
                    z9 = false;
                }
                if (logicBoolean != null) {
                    logicBooleanContextCreateContext = logicBoolean.createContext();
                }
                LogicBoolean nextElementInChain = logicBooleanContextCreateContext.parseNextElementInChain(str8, customUnitConfig, str2, z9, strBreakOuterLayerBrackets, str7, logicBoolean);
                if (nextElementInChain == null) {
                    throw new RuntimeException("Null function or field:'" + str2 + "'");
                }
                logicBoolean = nextElementInChain;
                str7 = str2;
                z6 = true;
                fastArrayList.add(nextElementInChain);
            }
            i4++;
        }
        if (fastArrayList.size() == 0) {
            throw new RuntimeException("Unknown function:'" + strBreakOuterLayerBrackets + "'");
        }
        LogicBoolean logicBoolean2 = null;
        for (int i5 = fastArrayList.size - 1; i5 >= 0; i5--) {
            LogicBoolean child = (LogicBoolean) fastArrayList.get(i5);
            if (logicBoolean2 != null) {
                child = child.setChild(logicBoolean2);
            }
            logicBoolean2 = child;
        }
        LogicBoolean.ReturnType returnType = logicBoolean2.getReturnType();
        if (returnType == LogicBoolean.ReturnType.voidReturn) {
            logicBoolean2.throwVoidReturnError(strBreakOuterLayerBrackets);
            throw new RuntimeException("throwVoidReturnError");
        }
        if (z && returnType != LogicBoolean.ReturnType.bool) {
            throw new BooleanParseException("Function:'" + strBreakOuterLayerBrackets + "' is expected to return a boolean type but it returns type: " + returnType);
        }
        return logicBoolean2;
    }

    public static boolean isEmptyIgnoringPlusMinus(String str) {
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt != '-' && cCharAt != '+' && cCharAt != ' ') {
                return false;
            }
        }
        return true;
    }

    public static LogicBoolean parseNumberBlock(CustomUnitConfig customUnitConfig, String str) {
        LogicBoolean booleanBlock = parseBooleanBlock(customUnitConfig, str, false);
        if (booleanBlock != null && booleanBlock.getReturnType() != LogicBoolean.ReturnType.number) {
            throw new RuntimeException("Expected number for: '" + str + "' got a " + booleanBlock.getReturnType() + " type");
        }
        return booleanBlock;
    }

    public static void setArgumentsWithMapping(ParameterMapping parameterMapping, Object obj, String str, CustomUnitConfig customUnitConfig, String str2) {
        String strSubstring;
        String strSubstring2;
        if (str2 == null) {
            str2 = obj.getClass().getSimpleName();
        }
        ArrayList<String> arrayList = new ArrayList();
        if (str != null && !VariableScope.nullOrMissingString.equals(str)) {
            int i = 0;
            boolean z = false;
            for (String str3 : StringUtils.a(str, ",", false)) {
                int iA = StringUtils.a(str3, "=");
                if (iA > 0) {
                    strSubstring = str3.substring(0, iA);
                    strSubstring2 = str3.substring(iA + 1);
                    z = true;
                } else {
                    if (z) {
                        throw new BooleanParseException(str2 + "(): SyntaxError: Cannot use non-keyword arg after keyword arg");
                    }
                    if (parameterMapping.numberOfPositionalParameters == 0) {
                        throw new BooleanParseException(str2 + "(): Function doesn't accept any non-keyword arguments.");
                    }
                    if (parameterMapping.numberOfPositionalParameters <= i) {
                        throw new BooleanParseException(str2 + "(): Too many non-keyword arguments. Only " + parameterMapping.numberOfPositionalParameters + " accepted.");
                    }
                    strSubstring = null;
                    Iterator<String> it = parameterMapping.parameters.keySet().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        String str4 = it.next();
                        if (((ParameterMapping.FieldOrMethod) parameterMapping.parameters.get(str4)).positionalOffset == i) {
                            strSubstring = str4;
                            break;
                        }
                    }
                    if (strSubstring == null) {
                        throw new BooleanParseException("Error failed to find non-keyword argument index: " + i);
                    }
                    strSubstring2 = str3;
                }
                String str5 = strSubstring;
                String lowerCase = strSubstring.trim().toLowerCase(Locale.ROOT);
                if (arrayList.contains(lowerCase)) {
                    throw new BooleanParseException("SyntaxError: Argument '" + str5 + "' has been listed more than once");
                }
                arrayList.add(lowerCase);
                try {
                    setArgumentWithMapping(parameterMapping, obj, lowerCase, strSubstring2, customUnitConfig);
                    i++;
                } catch (BooleanParseException e) {
                    throw e;
                }
            }
        }
        for (String str6 : parameterMapping.parameters.keySet()) {
            if (((ParameterMapping.FieldOrMethod) parameterMapping.parameters.get(str6)).required && !arrayList.contains(str6)) {
                throw new BooleanParseException(str2 + "(): SyntaxError: Missing required argument: '" + str6 + "'");
            }
        }
    }

    public static String fixArguments(String str) {
        String strTrim = str.trim();
        if (strTrim.equals(VariableScope.nullOrMissingString)) {
            return VariableScope.nullOrMissingString;
        }
        if (strTrim.startsWith("(") && strTrim.endsWith(")")) {
            return strTrim.substring(1, strTrim.length() - 1).trim();
        }
        throw new RuntimeException("Failed to parse function arguments:'" + strTrim + "'");
    }

    public static Matcher match(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return matcher;
        }
        return null;
    }

    public static Object getArgumentTextWithMapping(ParameterMapping.FieldOrMethod fieldOrMethod, Object obj) {
        if (fieldOrMethod != null && fieldOrMethod.field != null) {
            try {
                Object obj2 = fieldOrMethod.field.get(obj);
                if (obj2 == null) {
                    return null;
                }
                return obj2;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "<error>";
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                return "<error>";
            }
        }
        if (fieldOrMethod != null && fieldOrMethod.method != null) {
            GameEngine.log("getArgumentTextWithMapping: method not supported");
            return "<method>";
        }
        GameEngine.log("getArgumentTextWithMapping: No method or field");
        return "<error>";
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$ParameterMapping.class */
    public static class ParameterMapping {
        public HashMap<String, ParameterMapping.FieldOrMethod> parameters = new HashMap<>();
        int numberOfPositionalParameters = 0;
        public Class type;
        public String allParametersString;

        public ParameterMapping(Class cls) {
            this.type = cls;
            ArrayList<Field> arrayList = new ArrayList<>();
            LogicBooleanLoader.getAllFieldsInherited(arrayList, cls);
            this.allParametersString = VariableScope.nullOrMissingString;
            for (Field field : arrayList) {
                if (field.isAnnotationPresent(LogicBoolean.Parameter.class)) {
                    addParameter(field.getName().toLowerCase(Locale.ROOT), new FieldOrMethod(field), field.getAnnotation(LogicBoolean.Parameter.class));
                }
            }
            for (Method method : cls.getMethods()) {
                if (method.isAnnotationPresent(LogicBoolean.Parameter.class)) {
                    addParameter(method.getName().toLowerCase(Locale.ROOT), new FieldOrMethod(method), method.getAnnotation(LogicBoolean.Parameter.class));
                }
            }
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$ParameterMapping$FieldOrMethod.class */
        public static class FieldOrMethod {
            Field field;
            Method method;
            Class type;
            LogicBoolean.ReturnType returnType;
            int positionalOffset = -1;
            boolean required;

            public FieldOrMethod(Field field) {
                this.field = field;
                this.type = field.getType();
            }

            public FieldOrMethod(Method method) {
                this.method = method;
                this.type = method.getParameterTypes()[0];
            }
        }

        public void addParameter(String str, FieldOrMethod fieldOrMethod, LogicBoolean.Parameter parameter) {
            if (parameter.type() != LogicBoolean.ReturnType.undefined) {
                fieldOrMethod.returnType = parameter.type();
            }
            if (parameter.positional() != -1) {
                fieldOrMethod.positionalOffset = parameter.positional();
                this.numberOfPositionalParameters++;
            }
            if (parameter.required()) {
                fieldOrMethod.required = true;
            }
            if (parameter.key() != null && !parameter.key().equals(VariableScope.nullOrMissingString)) {
                str = parameter.key();
            }
            this.parameters.put(str, fieldOrMethod);
            if (!this.allParametersString.equals(VariableScope.nullOrMissingString)) {
                this.allParametersString += ", ";
            }
            this.allParametersString += str;
        }
    }

    public static void setArgumentWithMapping(ParameterMapping parameterMapping, Object obj, String str, String str2, CustomUnitConfig customUnitConfig) {
        ParameterMapping.FieldOrMethod fieldOrMethod = (ParameterMapping.FieldOrMethod) parameterMapping.parameters.get(str);
        if (fieldOrMethod != null && fieldOrMethod.field != null) {
            Object objConvertParameterData = convertParameterData(str2, fieldOrMethod.type, customUnitConfig, fieldOrMethod.returnType, str);
            if (objConvertParameterData == null && fieldOrMethod.required) {
                throw new BooleanParseException("SyntaxError: Cannot set required argument: '" + str + "' to null");
            }
            try {
                fieldOrMethod.field.set(obj, objConvertParameterData);
                return;
            } catch (IllegalAccessException e) {
                throw new BooleanParseException("Error setting parameter:'" + str + "' on " + obj.getClass().getSimpleName(), e);
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                String message = e2.getMessage();
                if (message == null) {
                    throw new BooleanParseException("Error parameter:'" + str + "' on " + obj.getClass().getSimpleName(), e2);
                }
                throw new BooleanParseException("Error parameter:'" + str + "': " + message.replace("com.corrodinggames.rts.game.units.custom.logicBooleans.", VariableScope.nullOrMissingString).replace("java.lang.", VariableScope.nullOrMissingString));
            }
        }
        if (fieldOrMethod != null && fieldOrMethod.method != null) {
            try {
                fieldOrMethod.method.invoke(obj, convertParameterData(str2, fieldOrMethod.type, customUnitConfig, fieldOrMethod.returnType, str));
                return;
            } catch (IllegalAccessException e3) {
                throw new BooleanParseException("Error setting parameter:'" + str + "' on " + obj.getClass().getSimpleName(), e3);
            } catch (IllegalArgumentException e4) {
                e4.printStackTrace();
                String message2 = e4.getMessage();
                if (message2 == null) {
                    throw new BooleanParseException("Error parameter:'" + str + "' on " + obj.getClass().getSimpleName(), e4);
                }
                throw new BooleanParseException("Error setting parameter:'" + str + "': " + message2.replace("com.corrodinggames.rts.game.units.custom.logicBooleans.", VariableScope.nullOrMissingString).replace("java.lang.", VariableScope.nullOrMissingString));
            } catch (InvocationTargetException e5) {
                Throwable cause = e5.getCause();
                String str3 = VariableScope.nullOrMissingString;
                if (cause != null) {
                    str3 = " - " + cause.getMessage();
                }
                throw new BooleanParseException("Error setting parameter:'" + str + "' on " + obj.getClass().getSimpleName() + str3, e5);
            }
        }
        throw new BooleanParseException("No parameter:'" + str + "' on " + obj.getClass().getSimpleName() + " (Possible parameters:" + parameterMapping.allParametersString + ")");
    }

    public static List getAllFieldsInherited(List list, Class cls) {
        list.addAll(Arrays.asList(cls.getFields()));
        return list;
    }

    public static Object convertParameterData(String str, Class cls, CustomUnitConfig customUnitConfig, LogicBoolean.ReturnType returnType, String str2) {
        LogicBoolean.ReturnType returnType2;
        if (str == null) {
            return null;
        }
        String strTrim = str.trim();
        if (strTrim.length() == 0 || strTrim.equals("null")) {
            return null;
        }
        if (cls == LogicBoolean.class) {
            LogicBoolean booleanBlock = parseBooleanBlock(customUnitConfig, strTrim, false);
            if (booleanBlock != null && returnType != null && returnType != (returnType2 = booleanBlock.getReturnType())) {
                throw new BooleanParseException("Wrong type. Expected type: '" + returnType + "' for dynamic parameter '" + str2 + "' instead got type:'" + returnType2 + "' (parsing: " + strTrim + ")");
            }
            return booleanBlock;
        }
        if (Utility.isValidNumber(strTrim)) {
            if (!Utility.containsSubstring(strTrim, ".")) {
                if (cls == String.class) {
                    return strTrim;
                }
                return Integer.valueOf(Integer.parseInt(strTrim));
            }
            if (cls == String.class) {
                return strTrim;
            }
            return Float.valueOf(Float.parseFloat(strTrim));
        }
        String lowerCase = strTrim.toLowerCase(Locale.ENGLISH);
        if ("false".equals(lowerCase)) {
            return Boolean.FALSE;
        }
        if ("true".equals(lowerCase)) {
            return Boolean.TRUE;
        }
        Matcher matcherMatch = match(singleQuotePattern, strTrim);
        if (matcherMatch != null) {
            return Utility.removeEscapeCharacters(matcherMatch.group(1));
        }
        Matcher matcherMatch2 = match(doubleQuotePattern, strTrim);
        if (matcherMatch2 != null) {
            return Utility.removeEscapeCharacters(matcherMatch2.group(1));
        }
        Matcher matcherMatch3 = match(integerPattern, strTrim);
        if (matcherMatch3 != null) {
            if (cls == String.class) {
                return matcherMatch3.group(1);
            }
            return Integer.valueOf(Integer.parseInt(matcherMatch3.group(1)));
        }
        Matcher matcherMatch4 = match(floatPattern, strTrim);
        if (matcherMatch4 != null) {
            if (cls == String.class) {
                return matcherMatch4.group(1);
            }
            return Float.valueOf(Float.parseFloat(matcherMatch4.group(1)));
        }
        String str3 = "null";
        if (cls != null) {
            str3 = "data of " + cls.getSimpleName();
            if (cls == String.class) {
                str3 = "string";
            }
            if (cls == Float.TYPE) {
                str3 = "number";
            }
            if (cls == Integer.TYPE) {
                str3 = "integer";
            }
            if (cls == Boolean.TYPE) {
                str3 = "boolean";
            }
        }
        String str4 = "Failed to read parameter '" + str2 + "' expected non-dynamic " + str3 + " got: " + strTrim + VariableScope.nullOrMissingString;
        if (cls == String.class) {
            str4 = str4 + " (A quoted string was expected)";
        }
        throw new BooleanParseException(str4);
    }

    public static String breakOuterLayerBrackets(String str) {
        if (str.startsWith("(") && str.endsWith(")")) {
            int iA = StringUtils.a(str, 0);
            if (iA == -1) {
                throw new RuntimeException("Brackets unbalanced. Starting '(' in '" + str + "' was not closed.");
            }
            if (iA == str.length() - 1) {
                str = breakOuterLayerBrackets(str.substring(1, str.length() - 1).trim());
            }
        }
        return str;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$LogicBooleanScopeOnly.class */
    public abstract static class LogicBooleanScopeOnly extends LogicBoolean implements LogicBooleanContext {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBooleanContext createContext() {
            return this;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean setChild(LogicBoolean logicBoolean) {
            return logicBoolean;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public boolean read(OrderableUnit orderableUnit) {
            return false;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public LogicBoolean.ReturnType getReturnType() {
            return LogicBoolean.ReturnType.voidReturn;
        }

        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
        public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
            return "<scope>";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$LogicBooleanContextWithDefault.class */
    public abstract static class LogicBooleanContextWithDefault implements LogicBooleanContext {
        @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.LogicBooleanContext
        public LogicBoolean parseNextElementInChain(String str, CustomUnitConfig customUnitConfig, String str2, boolean z, String str3, String str4, LogicBoolean logicBoolean) {
            return defaultParseNextElementInChain(this, str, customUnitConfig, str2, z, str3, str4, logicBoolean, LogicBoolean.booleanRegistry);
        }

        public static LogicBoolean defaultParseNextElementInChain(LogicBooleanContext logicBooleanContext, String str, CustomUnitConfig customUnitConfig, String str2, boolean z, String str3, String str4, LogicBoolean logicBoolean, HashMap<String, LogicBoolean> map) {
            String lowerCase;
            String strSubstring;
            UnitReference singleUnitReferenceElement = UnitReference.parseSingleUnitReferenceElement(customUnitConfig, str2);
            if (singleUnitReferenceElement != null) {
                return singleUnitReferenceElement;
            }
            int iIndexOf = str2.indexOf("(");
            if (iIndexOf == -1) {
                lowerCase = str2.toLowerCase(Locale.ROOT);
                strSubstring = VariableScope.nullOrMissingString;
            } else {
                lowerCase = str2.substring(0, iIndexOf).trim().toLowerCase(Locale.ROOT);
                strSubstring = str2.substring(iIndexOf);
            }
            if (str != null) {
                lowerCase = str + lowerCase;
            }
            LogicBoolean logicBoolean2 = map.get(lowerCase);
            if (logicBoolean2 != null) {
                String strFixArguments = LogicBooleanLoader.fixArguments(strSubstring);
                return logicBoolean2.with(customUnitConfig, strFixArguments, lowerCase).validateAndOptimize(lowerCase, strFixArguments, str3, logicBooleanContext, z);
            }
            String str5 = VariableScope.nullOrMissingString;
            if (map.size() < 8 && !map.isEmpty()) {
                StringBuilder str6 = new StringBuilder(" (Allowed functions: ");
                boolean z2 = true;
                for (String str7 : map.keySet()) {
                    if (!z2) {
                        str6.append(", ");
                    }
                    z2 = false;
                    str6.append(str7);
                }
                str5 = str6 + ")";
            }
            if (str4 != null) {
                throw new RuntimeException("Unknown function or field:'" + str2 + "' in '" + str4 + "'" + str5);
            }
            throw new RuntimeException("Unknown function or field:'" + str2 + "'" + str5);
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$VoidContextReader.class */
    public static final class VoidContextReader extends LogicBooleanContextWithDefault {
        String debugType;

        VoidContextReader(String str) {
            this.debugType = str;
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.LogicBooleanContextWithDefault, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.LogicBooleanContext
        public LogicBoolean parseNextElementInChain(String str, CustomUnitConfig customUnitConfig, String str2, boolean z, String str3, String str4, LogicBoolean logicBoolean) {
            if (str4 != null) {
                if (this.debugType != null) {
                    throw new RuntimeException("No field:'" + str2 + "' in '" + str4 + "' (" + this.debugType + ")");
                }
                throw new RuntimeException("No field:'" + str2 + "' in '" + str4 + "'");
            }
            throw new RuntimeException("No field:'" + str2 + "'");
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$ArrayContextReader.class */
    public static final class ArrayContextReader extends LogicBooleanContextWithDefault {
        LogicBoolean.ReturnType arrayType;
        static HashMap arrayFunctions = new HashMap();
        static HashMap arrayFunctionsUnit;

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$ArrayContextReader$ArrayFunction.class */
        public abstract static class ArrayFunction extends LogicBoolean {
            public abstract void setArrayTarget(LogicBoolean logicBoolean);
        }

        public ArrayContextReader(LogicBoolean.ReturnType returnType) {
            this.arrayType = returnType;
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$ArrayContextReader$ArrayGetUnit.class */
        public static class ArrayGetUnit extends ArrayGet {
            @Override
            // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.ArrayContextReader.ArrayGet, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public LogicBooleanContext createContext() {
                return UnitReference.unitContextChangingContext;
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public LogicBoolean setChild(LogicBoolean logicBoolean) {
                return UnitReference.UnitContextChangingBooleanByLogic.create(this, logicBoolean);
            }

            @Override
            // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.ArrayContextReader.ArrayGet, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public LogicBoolean.ReturnType getReturnType() {
                return LogicBoolean.ReturnType.unit;
            }
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$ArrayContextReader$ArrayGet.class */
        public static class ArrayGet extends ArrayFunction {

            @LogicBoolean.Parameter(type = LogicBoolean.ReturnType.number)
            public LogicBoolean index;
            LogicBoolean targetArray;
            public LogicBoolean.ReturnType elementType;

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public LogicBooleanContext createContext() {
                return LogicBooleanLoader.voidArrayContextReader;
            }

            @Override
            // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.ArrayContextReader.ArrayFunction
            public void setArrayTarget(LogicBoolean logicBoolean) {
                this.targetArray = logicBoolean;
                this.elementType = LogicBoolean.ReturnType.getArrayBaseType(logicBoolean.getReturnType());
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
                if (str == null || VariableScope.nullOrMissingString.equals(str)) {
                    validateNumberOfArguments(0);
                    return;
                }
                ArrayList arrayListA = StringUtils.a(str, ",", false);
                validateNumberOfArguments(arrayListA.size());
                this.index = LogicBooleanLoader.parseNumberBlock(customUnitConfig, (String) arrayListA.get(0));
                if (this.index == null) {
                    throw new BooleanParseException("Expected non-null argument");
                }
            }

            public void validateNumberOfArguments(int i) {
                if (i != 1) {
                    throw new BooleanParseException("Expected 1 argument");
                }
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public void validate(String str, String str2, String str3, LogicBooleanContext logicBooleanContext, boolean z) {
                super.validate(str, str2, str3, logicBooleanContext, z);
                if (this.index == null) {
                    throw new BooleanParseException("No array index");
                }
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public LogicBoolean.ReturnType getReturnType() {
                return this.elementType;
            }

            LogicBoolean readElement(OrderableUnit orderableUnit) {
                int number = (int) this.index.readNumber(orderableUnit);
                if (this.targetArray == null) {
                    GameEngine.logColored("ArrayGet readElement targetArray==null");
                    return null;
                }
                return this.targetArray.readArrayElement(orderableUnit, number);
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public boolean read(OrderableUnit orderableUnit) {
                LogicBoolean element = readElement(orderableUnit);
                if (element == null) {
                    return false;
                }
                return element.read(orderableUnit);
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public float readNumber(OrderableUnit orderableUnit) {
                LogicBoolean element = readElement(orderableUnit);
                if (element == null) {
                    return 0.0f;
                }
                return element.readNumber(orderableUnit);
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public BaseUnit readUnit(OrderableUnit orderableUnit) {
                LogicBoolean element = readElement(orderableUnit);
                if (element == null) {
                    return null;
                }
                return element.readUnit(orderableUnit);
            }

            public String getName() {
                return "get";
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
                String str;
                LogicBoolean element = readElement(orderableUnit);
                int number = (int) this.index.readNumber(orderableUnit);
                String str2 = VariableScope.nullOrMissingString;
                if (this.targetArray != null) {
                    str2 = str2 + this.targetArray.getMatchFailReasonForPlayer(orderableUnit);
                }
                String str3 = str2 + "." + getName() + "(" + number + ")";
                if (element == null) {
                    str = str3 + "=null";
                } else {
                    str = str3 + "=" + element.getMatchFailReasonForPlayer(orderableUnit);
                }
                return str;
            }
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$ArrayContextReader$ArraySize.class */
        public static class ArraySize extends ArrayFunction {
            LogicBoolean targetArray;

            @Override
            // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.ArrayContextReader.ArrayFunction
            public void setArrayTarget(LogicBoolean logicBoolean) {
                this.targetArray = logicBoolean;
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public LogicBoolean.ReturnType getReturnType() {
                return LogicBoolean.ReturnType.number;
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public boolean read(OrderableUnit orderableUnit) {
                return false;
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public float readNumber(OrderableUnit orderableUnit) {
                return this.targetArray.getArraySize(orderableUnit);
            }

            public String getName() {
                return "size";
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
                String str = VariableScope.nullOrMissingString;
                if (this.targetArray != null) {
                    str = str + this.targetArray.getMatchFailReasonForPlayer(orderableUnit);
                }
                return str + getName() + "(=" + readNumber(orderableUnit) + ")";
            }
        }

        /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/logicBooleans/LogicBooleanLoader$ArrayContextReader$ArrayContains.class */
        public static class ArrayContains extends ArrayFunction {
            public LogicBoolean value;
            LogicBoolean targetArray;
            public LogicBoolean.ReturnType elementType;

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public LogicBooleanContext createContext() {
                return LogicBooleanLoader.voidNumberContextReader;
            }

            @Override
            // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.ArrayContextReader.ArrayFunction
            public void setArrayTarget(LogicBoolean logicBoolean) {
                this.targetArray = logicBoolean;
                this.elementType = LogicBoolean.ReturnType.getArrayBaseType(logicBoolean.getReturnType());
                if (this.value.getReturnType() != this.elementType) {
                    throw new BooleanParseException("Expected value of type: " + this.elementType + " (got:" + this.value.getReturnType() + ")");
                }
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public void setArgumentsRaw(String str, CustomUnitConfig customUnitConfig, String str2) {
                if (str == null || VariableScope.nullOrMissingString.equals(str)) {
                    validateNumberOfArguments(0);
                    return;
                }
                ArrayList arrayListA = StringUtils.a(str, ",", false);
                validateNumberOfArguments(arrayListA.size());
                this.value = LogicBooleanLoader.parseBooleanBlock(customUnitConfig, (String) arrayListA.get(0), false);
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
            public void validate(String str, String str2, String str3, LogicBooleanContext logicBooleanContext, boolean z) {
                super.validate(str, str2, str3, logicBooleanContext, z);
                if (this.value == null) {
                    throw new BooleanParseException("No value");
                }
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public LogicBoolean.ReturnType getReturnType() {
                return LogicBoolean.ReturnType.bool;
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public boolean read(OrderableUnit orderableUnit) {
                return indexOf(orderableUnit, this.targetArray, this.value) != -1;
            }

            public static final int indexOf(OrderableUnit orderableUnit, LogicBoolean logicBoolean, LogicBoolean logicBoolean2) {
                int arraySize = logicBoolean.getArraySize(orderableUnit);
                LogicBoolean.ReturnType returnType = logicBoolean2.getReturnType();
                if (returnType == LogicBoolean.ReturnType.bool) {
                    boolean z = logicBoolean2.read(orderableUnit);
                    for (int i = 0; i < arraySize; i++) {
                        if (logicBoolean.readArrayElement(orderableUnit, i).read(orderableUnit) == z) {
                            return i;
                        }
                    }
                    return -1;
                }
                if (returnType == LogicBoolean.ReturnType.number) {
                    float number = logicBoolean2.readNumber(orderableUnit);
                    for (int i2 = 0; i2 < arraySize; i2++) {
                        if (Utility.approximatelyEqual(number, logicBoolean.readArrayElement(orderableUnit, i2).readNumber(orderableUnit))) {
                            return i2;
                        }
                    }
                    return -1;
                }
                if (returnType == LogicBoolean.ReturnType.unit) {
                    BaseUnit unit = logicBoolean2.readUnit(orderableUnit);
                    if (VariableScope.isMarker(unit)) {
                        if (unit == null) {
                            return -1;
                        }
                        float f = unit.posX;
                        float f2 = unit.posY;
                        int i3 = unit.team.teamId;
                        for (int i4 = 0; i4 < arraySize; i4++) {
                            BaseUnit unit2 = logicBoolean.readArrayElement(orderableUnit, i4).readUnit(orderableUnit);
                            if (unit2 != null && Utility.approximatelyEqual(f, unit2.posX) && Utility.approximatelyEqual(f2, unit2.posY) && i3 == unit2.team.teamId) {
                                return i4;
                            }
                        }
                        return -1;
                    }
                    for (int i5 = 0; i5 < arraySize; i5++) {
                        if (unit == logicBoolean.readArrayElement(orderableUnit, i5).readUnit(orderableUnit)) {
                            return i5;
                        }
                    }
                    return -1;
                }
                return -1;
            }

            public String getName() {
                return "contains";
            }

            @Override // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean
            public String getMatchFailReasonForPlayer(OrderableUnit orderableUnit) {
                String str = VariableScope.nullOrMissingString;
                if (this.targetArray != null) {
                    str = str + this.targetArray.getMatchFailReasonForPlayer(orderableUnit);
                }
                String matchFailReasonForPlayer = null;
                if (0 != 0) {
                    matchFailReasonForPlayer = this.value.getMatchFailReasonForPlayer(orderableUnit);
                }
                return (str + "." + getName() + "(" + matchFailReasonForPlayer + ")") + "=" + valueToStringDebug(orderableUnit);
            }
        }

        public static void addContextFunction(HashMap map, LogicBoolean logicBoolean, String... strArr) {
            for (String str : strArr) {
                String lowerCase = str.toLowerCase(Locale.ROOT);
                if (map.get(lowerCase) != null) {
                    throw new RuntimeException("logicBoolean: " + lowerCase + " already exists");
                }
                map.put(lowerCase, logicBoolean);
            }
        }

        static {
            addContextFunction(arrayFunctions, new ArrayGet(), "get");
            addContextFunction(arrayFunctions, new ArraySize(), "size");
            addContextFunction(arrayFunctions, new ArraySize(), "length");
            addContextFunction(arrayFunctions, new ArrayContains(), "contains");
            arrayFunctionsUnit = new HashMap();
            addContextFunction(arrayFunctionsUnit, new ArrayGetUnit(), "get");
            addContextFunction(arrayFunctionsUnit, new ArraySize(), "size");
            addContextFunction(arrayFunctionsUnit, new ArraySize(), "length");
            addContextFunction(arrayFunctionsUnit, new ArrayContains(), "contains");
        }

        @Override
        // com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.LogicBooleanContextWithDefault, com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader.LogicBooleanContext
        public LogicBoolean parseNextElementInChain(String str, CustomUnitConfig customUnitConfig, String str2, boolean z, String str3, String str4, LogicBoolean logicBoolean) {
            LogicBoolean logicBooleanDefaultParseNextElementInChain;
            if (this.arrayType == LogicBoolean.ReturnType.unitArray) {
                logicBooleanDefaultParseNextElementInChain = defaultParseNextElementInChain(this, null, customUnitConfig, str2, z, str3, str4, logicBoolean, arrayFunctionsUnit);
            } else {
                logicBooleanDefaultParseNextElementInChain = defaultParseNextElementInChain(this, null, customUnitConfig, str2, z, str3, str4, logicBoolean, arrayFunctions);
            }
            if (logicBooleanDefaultParseNextElementInChain == null) {
                return null;
            }
            if (!(logicBooleanDefaultParseNextElementInChain instanceof ArrayFunction)) {
                throw new RuntimeException("Expected array function.");
            }
            ArrayFunction arrayFunction = (ArrayFunction) logicBooleanDefaultParseNextElementInChain;
            arrayFunction.setArrayTarget(logicBoolean);
            return arrayFunction;
        }
    }
}
