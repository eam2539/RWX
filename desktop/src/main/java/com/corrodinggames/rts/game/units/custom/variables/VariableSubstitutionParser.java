package com.corrodinggames.rts.game.units.custom.variables;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.f.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/f/a.class */
public class VariableSubstitutionParser {
    static final Pattern a = Pattern.compile("\\$\\{([^\\}]*)\\}");
    static final Pattern b = Pattern.compile("[A-Za-z_][A-Za-z_.0-9]*");
    static ExpressionEvaluator c = new ExpressionEvaluator();

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile) throws ConfigParseException {
        int i = 0;
        c.a();
        for (String str : iniFile.getSectionsWithKeyStartingWith("@global ")) {
            for (String str2 : iniFile.getKeysStartingWith(str, "@global ")) {
                String strTrim = str2.substring("@global ".length()).trim();
                try {
                    VariableNameValidator.a(strTrim);
                    if (iniFile.hasSection(strTrim)) {
                        throw new ConfigParseException("[" + str + "]" + str2 + ": A section already has that name");
                    }
                    String valueStrict = iniFile.getValueStrict(str, str2);
                    if (valueStrict.contains("${")) {
                        throw new ConfigParseException("[" + str + "]" + str2 + " has dynamic value: '" + valueStrict + "', this is not yet supported");
                    }
                    c.a.a(strTrim, valueStrict);
                } catch (ConfigParseException e) {
                    throw new ConfigParseException("[" + str + "]" + str2 + ": " + e.getMessage());
                }
            }
        }
        ArrayList<ParsedValue> arrayList = new ArrayList();
        LinkedHashMap<String,Map<String,String>> sectionsMap = iniFile.getSectionsMap();
        for (String str3 : sectionsMap.keySet()) {
            if (str3 != null && !str3.startsWith("comment_") && !str3.startsWith("template_")) {
                ExpressionEvaluator expressionEvaluatorB = c.b();
                for (String str4 : iniFile.getKeysStartingWith(str3, "@define ")) {
                    String strTrim2 = str4.substring("@define ".length()).trim();
                    try {
                        VariableNameValidator.a(strTrim2);
                        if (iniFile.hasSection(strTrim2)) {
                            throw new ConfigParseException("[" + str3 + "]" + str4 + ": A section already has that name");
                        }
                        String valueStrict2 = iniFile.getValueStrict(str3, str4);
                        if (valueStrict2.contains("${")) {
                            throw new ConfigParseException("[" + str3 + "]" + str4 + " has dynamic value: '" + valueStrict2 + "', this is not yet supported");
                        }
                        expressionEvaluatorB.b.a(strTrim2, valueStrict2);
                    } catch (ConfigParseException e2) {
                        throw new ConfigParseException("[" + str3 + "]" + str4 + ": " + e2.getMessage());
                    }
                }
                Map<String,String> map =  sectionsMap.get(str3);
                for (String str5 : map.keySet()) {
                    String str6 = (String) map.get(str5);
                    if (str6 != null && str6.contains("${")) {
                        int i2 = 0;
                        StringBuffer stringBuffer = new StringBuffer();
                        Matcher matcher = a.matcher(str6);
                        while (matcher.find()) {
                            i2++;
                            if (i2 > 100) {
                                throw new ConfigParseException("[" + str3 + "]" + str5 + ": Too many loops while parsing");
                            }
                            String strGroup = matcher.group(1);
                            i++;
                            try {
                                String strA = expressionEvaluatorB.a(customUnitConfig, iniFile, str3, strGroup);
                                if (!strGroup.equals(strA)) {
                                }
                                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(strA));
                            } catch (ConfigParseException e3) {
                                e3.printStackTrace();
                                throw new ConfigParseException("[" + str3 + "]" + str5 + ": " + e3.getMessage());
                            }
                        }
                        matcher.appendTail(stringBuffer);
                        arrayList.add(new ParsedValue(str3, str5, stringBuffer.toString()));
                    }
                }
            }
        }
        for (ParsedValue parsedValue : arrayList) {
            iniFile.setValue(parsedValue.a, parsedValue.b, parsedValue.c);
        }
        arrayList.clear();
    }
}
