package com.corrodinggames.rts.game.units.custom.variables;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

import java.util.regex.Matcher;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.f.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/f/b.class */
public class ExpressionEvaluator {
    public VariableMap a;
    public VariableMap b;

    public ExpressionEvaluator() {
        this.a = new VariableMap();
        this.b = new VariableMap();
    }

    public ExpressionEvaluator(VariableMap variableMap, VariableMap variableMap2) {
        this.a = variableMap;
        this.b = variableMap2;
    }

    public void a() {
        this.a.a();
        this.b.a();
    }

    public ExpressionEvaluator b() {
        return new ExpressionEvaluator(this.a, new VariableMap());
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.corrodinggames.rts.game.units.custom.f.b$1] */
    public static double a(final String str) {
        return new Object() { // from class: com.corrodinggames.rts.game.units.custom.f.b.1
            int a = -1;
            int b;

            void a() {
                int i = this.a + 1;
                this.a = i;
                this.b = i < str.length() ? str.charAt(this.a) : -1;
            }

            boolean a(int i) {
                while (this.b == 32) {
                    a();
                }
                if (this.b == i) {
                    a();
                    return true;
                }
                return false;
            }

            double b() {
                a();
                double dC = c();
                if (this.a < str.length()) {
                    throw new RuntimeException("Unexpected: " + ((char) this.b));
                }
                return dC;
            }

            double c() {
                double d = d();
                while (true) {
                    double d2 = d;
                    if (a(43)) {
                        d = d2 + d();
                    } else {
                        if (!a(45)) {
                            return d2;
                        }
                        d = d2 - d();
                    }
                }
            }

            double d() {
                double dE = e();
                while (true) {
                    double d = dE;
                    if (a(42)) {
                        dE = d * e();
                    } else if (a(47)) {
                        dE = d / e();
                    } else {
                        if (!a(37)) {
                            return d;
                        }
                        dE = d % e();
                    }
                }
            }

            double e() {
                double dTan;
                if (a(43)) {
                    return e();
                }
                if (a(45)) {
                    return -e();
                }
                int i = this.a;
                if (a(40)) {
                    dTan = c();
                    a(41);
                } else if ((this.b >= 48 && this.b <= 57) || this.b == 46) {
                    while (true) {
                        if ((this.b < 48 || this.b > 57) && this.b != 46) {
                            break;
                        }
                        a();
                    }
                    dTan = Double.parseDouble(str.substring(i, this.a));
                } else if (this.b >= 97 && this.b <= 122) {
                    while (this.b >= 97 && this.b <= 122) {
                        a();
                    }
                    String strSubstring = str.substring(i, this.a);
                    double dE = e();
                    if (strSubstring.equals("sqrt")) {
                        dTan = Math.sqrt(dE);
                    } else if (strSubstring.equals("sin")) {
                        dTan = Math.sin(Math.toRadians(dE));
                    } else if (strSubstring.equals("cos")) {
                        dTan = Math.cos(Math.toRadians(dE));
                    } else if (strSubstring.equals("tan")) {
                        dTan = Math.tan(Math.toRadians(dE));
                    } else {
                        if (!strSubstring.equals("int")) {
                            throw new RuntimeException("Unknown function: " + strSubstring);
                        }
                        dTan = (int) dE;
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + ((char) this.b));
                }
                if (a(94)) {
                    dTan = Math.pow(dTan, e());
                }
                return dTan;
            }
        }.b();
    }

    public boolean b(String str) {
        return str.contains("*") || str.contains("/") || str.contains("+") || str.contains("-") || str.contains("(") || str.contains(")") || str.contains("^") || str.contains("%");
    }

    public String a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2) throws ConfigParseException {
        String strTrim = str2.trim();
        boolean zB = b(strTrim);
        int i = 0;
        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = VariableSubstitutionParser.b.matcher(strTrim);
        while (matcher.find()) {
            i++;
            if (i > 100) {
                throw new ConfigParseException("Too many loops while parsing");
            }
            String strGroup = matcher.group(0);
            if (!Utility.isNumeric(strGroup) && !strGroup.equals("int") && !strGroup.equals("cos") && !strGroup.equals("sin") && !strGroup.equals("sqrt")) {
                String strB = b(customUnitConfig, iniFile, str, strGroup);
                if (zB && !Utility.isNumeric(strB)) {
                    throw new ConfigParseException("Cannot do maths on '" + strB + "' from " + strGroup + " (not a number)");
                }
                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(strB));
            }
        }
        matcher.appendTail(stringBuffer);
        String string = stringBuffer.toString();
        if (zB) {
            string = Utility.formatNumber(a(string));
        }
        return string;
    }

    public String b(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2) throws ConfigParseException {
        if (str2.contains(".")) {
            String[] strArrSplitByChar = Utility.splitByChar(str2, '.');
            if (strArrSplitByChar.length != 2) {
                throw new ConfigParseException("Unexpected key format: " + str2);
            }
            String str3 = strArrSplitByChar[0];
            String str4 = strArrSplitByChar[1];
            if (str3.equals("section")) {
                str3 = str;
            }
            String string = iniFile.getString(str3, str4, (String) null);
            if (string == null) {
                if (str3.equalsIgnoreCase("self")) {
                    throw new ConfigParseException("Static $ block: Could not find: [" + str3 + "]" + str4 + " in this conf file. Hint: You might have wanted % instead of $ for a dynamic string");
                }
                throw new ConfigParseException("Static $ block: Could not find: [" + str3 + "]" + str4 + " in this conf file");
            }
            if (string.contains("${")) {
                throw new ConfigParseException("Reference [" + str3 + "]" + str4 + " is dynamic, chaining is not yet supported");
            }
            return string;
        }
        String strA = this.b.a(str2);
        if (strA != null) {
            return strA;
        }
        String strA2 = this.a.a(str2);
        if (strA2 != null) {
            return strA2;
        }
        throw new ConfigParseException("Could not find variable with name: " + str2);
    }
}
