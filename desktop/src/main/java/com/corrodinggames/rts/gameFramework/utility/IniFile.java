package com.corrodinggames.rts.gameFramework.utility;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.game.units.custom.*;
import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.game.units.custom.logicBooleans.BooleanParseException;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.local.Locale;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.ab */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/ab.class */
public class IniFile {

    /* JADX INFO: renamed from: g */
    private static final Pattern controlCharPattern = Pattern.compile("\\p{C}");

    /* JADX INFO: renamed from: h */
    private static final Pattern sectionPattern = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");

    /* JADX INFO: renamed from: i */
    private static final Pattern keyValuePattern = Pattern.compile("\\s*([^=:]*)(?:=|:)(.*)");

    /* JADX INFO: renamed from: a */
    String fileName;

    /* JADX INFO: renamed from: k */
    private boolean strict;

    /* JADX INFO: renamed from: f */
    public String path;

    /* JADX INFO: renamed from: j */
    private LinkedHashMap<String,LinkedHashMap<String,String>> sections = new LinkedHashMap();

    /* JADX INFO: renamed from: b */
    boolean trackReads = true;

    /* JADX INFO: renamed from: c */
    LinkedHashSet usedKeys = new LinkedHashSet();

    /* JADX INFO: renamed from: d */
    public ArrayList<ConfigKeyValue> duplicateKeys = new ArrayList();

    /* JADX INFO: renamed from: e */
    public ArrayList errors = new ArrayList();

    /* JADX INFO: renamed from: a */
    public void enableStrict() {
        this.strict = true;
        this.trackReads = false;
    }

    /* JADX INFO: renamed from: a */
    public void trackRead(String str, String str2) {
        trackReadWithSource(str, str2, "Unknown");
    }

    /* JADX INFO: renamed from: a */
    public void trackReadWithSource(String str, String str2, String str3) {
        if (this.trackReads) {
            this.usedKeys.add(str + ":" + str2);
        }
    }

    /* JADX INFO: renamed from: b */
    public void checkForUnusedKeys() {
        if (!this.trackReads) {
            throw new RuntimeException("Not tracking reads");
        }
        for (String str : this.sections.keySet()) {
            if (str == null || !str.startsWith("template_")) {
                boolean z = false;
                String str2 = null;
                Map<String,String> map = this.sections.get(str);
                for (String str3 : map.keySet()) {
                    if (str3 == null || (!str3.startsWith("@define ") && !str3.startsWith("@global "))) {
                        if (!this.usedKeys.contains(str + ":" + str3)) {
                            if (!"IGNORE".equals((String) map.get(str3)) && str2 == null) {
                                if (str3 != null && str3.trim().equals(VariableScope.nullOrMissingString)) {
                                    str2 = this.fileName + " Found line in [" + str + "] with no key name.";
                                } else {
                                    str2 = this.fileName + ": The key '[" + str + "]" + str3 + "' was not used. (hint: make sure it's valid and in the right section)";
                                }
                            }
                        } else {
                            z = true;
                        }
                    }
                }
                if (str2 != null) {
                    if (z || this.sections.size() == 1) {
                        throw new RuntimeException(str2);
                    }
                    throw new RuntimeException(this.fileName + ": No keys in section: [" + str + "] were used (is this section named correctly?)");
                }
            }
        }
    }

    /* JADX INFO: renamed from: c */
    public int getHash() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            for (String str : this.sections.keySet()) {
                Map<String,String> map = this.sections.get(str);
                for (String str2 : map.keySet()) {
                    messageDigest.update((str + ":" + str2 + ":" + ((String) map.get(str2))).getBytes("UTF-8"));
                }
            }
            return new BigInteger(1, messageDigest.digest()).intValue();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    public IniFile(String str) throws IOException {
        this.fileName = "ini";
        this.fileName = str;
        this.path = str;
        loadFile(str);
    }

    public IniFile(InputStream inputStream, String str) throws IOException {
        this.fileName = "ini";
        this.fileName = str;
        loadStream(inputStream);
    }

    /* JADX INFO: renamed from: a */
    public void loadFile(String str) throws IOException {
        parse(new BufferedReader(new FileReader(str)));
    }

    /* JADX INFO: renamed from: a */
    public void loadStream(InputStream inputStream) throws IOException {
        parse(new BufferedReader(new InputStreamReader(inputStream, "UTF-8")));
    }

    /* JADX INFO: renamed from: a */

    public void parse(BufferedReader bufferedReader) throws IOException {
        try {
            int var2 = 0;
            String var4 = null;
            boolean var5 = false;
            String var6 = "\"\"\"";
            String var7 = "";
            boolean var8 = false;

            String var3;
            label168:
            while ((var3 = bufferedReader.readLine()) != null) {
                var2++;
                if (var3.startsWith("\ufeff")) {
                    var3 = var3.substring(1);
                }

                String var9 = var3.trim();
                boolean var10 = false;
                if (var5 || !var9.startsWith("#")) {
                    if (Utility.containsSubstring(var3, "\"\"\"")) {
                        int var11 = 0;
                        if (!var5 && var3.trim().startsWith("\"\"\"")) {
                            var8 = true;
                        }

                        while (true) {
                            int var12 = var3.indexOf("\"\"\"", var11);
                            if (var12 == -1) {
                                var7 = var7 + var3.substring(var11, var3.length());
                                if (var5) {
                                    continue label168;
                                }

                                if (var8) {
                                    var7 = "";
                                    var8 = false;
                                    continue label168;
                                }

                                var3 = var7;
                                var7 = "";
                                var8 = false;
                                var10 = true;
                                break;
                            }

                            var7 = var7 + var3.substring(var11, var12);
                            var11 = var12 + 3;
                            var5 = !var5;
                        }
                    } else if (var5) {
                        var7 = var7 + var3;
                        continue;
                    }

                    if (var9.length() != 0) {
                        if (Utility.containsSubstring(var3, "[")) {
                            Matcher var19 = sectionPattern.matcher(var3);
                            if (var19.matches()) {
                                var4 = var19.group(1).trim();
                                continue;
                            }
                        }

                        if (var4 == null || !var4.startsWith("comment_")) {
                            Matcher var20 = keyValuePattern.matcher(var3);
                            if (var20.matches()) {
                                if (var4 == null) {
                                    GameEngine.logColored("IniFile: " + this.fileName + "  line " + var2 + " is not in a [section]:'" + var3 + "'");
                                    this.errors.add("Line " + var2 + " is not in a [section]: '" + var3 + "'");
                                } else {
                                    String var22 = var20.group(1).trim();
                                    String var13 = var20.group(2);
                                    if (!var10) {
                                        var13 = var13.trim();
                                    }

                                    if (var22.equals("")) {
                                        String var25 = this.fileName + ": Unexpected format on line " + var2 + ": Key cannot be empty for line '" + var3 + "'";
                                        throw new IOException(var25);
                                    }

                                    LinkedHashMap var14 = this.sections.get(var4);
                                    if (var14 == null) {
                                        this.sections.put(var4, var14 = new LinkedHashMap());
                                    }

                                    if (var14.get(var22) != null) {
                                        this.duplicateKeys.add(new ConfigKeyValue(var4, var22));
                                    }

                                    var14.put(var22, var13);
                                }
                            } else {
                                var20 = controlCharPattern.matcher(var3);
                                if (var20.find()) {
                                    if (var3.length() != 1) {
                                        String var23 = var3.replaceAll("\\p{C}", "?");
                                        String var24 = this.fileName + ": Unexpected format on line:" + var2 + ": '" + var23 + "' in ini file (hint: This line might have hidden unicode)";
                                        throw new IOException(var24);
                                    }
                                } else {
                                    GameEngine.logColored(this.fileName + ": Unexpected format on line:" + var2 + ": '" + var3 + "' in ini file");
                                    this.errors.add(var3);
                                }
                            }
                        }
                    }
                }
            }

            if (var5) {
                String var18 = this.fileName + ": End of file while in multi-line string (hint: You are likely missing a closing \"\"\")";
                throw new IOException(var18);
            }
        } finally {
            bufferedReader.close();
        }
    }

    /* JADX INFO: renamed from: a */
    private String getValueInternal(String str, String str2, boolean z, String str3) {
        String valueOrThrow = getValueOrThrow(str, str2, z);
        if (valueOrThrow != null) {
            trackReadWithSource(str, str2, str3);
        }
        return valueOrThrow;
    }

    /* JADX INFO: renamed from: b */
    public String getValue(String str, String str2) {
        Map map = (Map) this.sections.get(str);
        if (map == null) {
            return null;
        }
        return (String) map.get(str2);
    }

    /* JADX INFO: renamed from: a */
    private String getValueOrThrow(String str, String str2, boolean z) {
        Map map = (Map) this.sections.get(str);
        if (map == null) {
            if (!z) {
                throw new RuntimeException("Could not find section: [" + str + "] in configuration file");
            }
            return null;
        }
        String str3 = (String) map.get(str2);
        if (str3 == null) {
            if (!z) {
                throw new RuntimeException("Could not find: " + str2 + " in configuration file under [" + str + "]");
            }
            return null;
        }
        if (str3.equals("IGNORE")) {
            if (!z) {
                throw new RuntimeException("Key: " + str2 + " under [" + str + "], is set to IGNORE but is required and has no default");
            }
            return null;
        }
        return str3;
    }

    /* JADX INFO: renamed from: a */
    public String getValueFromTwoKeys(String str, String str2, String str3, String str4) {
        String string = getString(str, str2, (String) null);
        String string2 = getString(str, str3, (String) null);
        if (string == null || string2 == null) {
            return string != null ? string : string2 != null ? string2 : str4;
        }
        throw new RuntimeException("[" + str + "]Cannot set " + str2 + " and " + str3 + " at the same time");
    }

    /* JADX INFO: renamed from: a */
    public Boolean getBooleanFromTwoKeys(String str, String str2, String str3, Boolean bool) {
        String valueFromTwoKeys = getValueFromTwoKeys(str, str2, str3, (String) null);
        if (valueFromTwoKeys == null) {
            return bool;
        }
        if (valueFromTwoKeys.equalsIgnoreCase("true")) {
            return true;
        }
        if (valueFromTwoKeys.equalsIgnoreCase("false")) {
            return false;
        }
        if (valueFromTwoKeys.equalsIgnoreCase("1")) {
            return true;
        }
        if (valueFromTwoKeys.equalsIgnoreCase("0")) {
            return false;
        }
        throw new RuntimeException(str2 + ": unexpected boolean value:'" + valueFromTwoKeys + "' in section:" + str);
    }

    /* JADX INFO: renamed from: a */
    public Boolean getBoolean(String str, String str2, Boolean bool) {
        String string = getString(str, str2, (String) null);
        if (string == null) {
            return bool;
        }
        if (string.equalsIgnoreCase("true")) {
            return true;
        }
        if (string.equalsIgnoreCase("false")) {
            return false;
        }
        if (string.equalsIgnoreCase("1")) {
            return true;
        }
        if (string.equalsIgnoreCase("0")) {
            return false;
        }
        throw new RuntimeException(str2 + ": unexpected boolean value:'" + string + "' in section:" + str);
    }

    /* JADX INFO: renamed from: c */
    public void throwFailedToFind(String str, String str2) {
        throw new RuntimeException("Could not find " + str2 + " in configuration file in section:" + str);
    }

    /* JADX INFO: renamed from: d */
    public boolean getBooleanStrict(String str, String str2) {
        Boolean bool = getBoolean(str, str2, (Boolean) null);
        if (bool == null) {
            throwFailedToFind(str, str2);
        }
        return bool.booleanValue();
    }

    /* JADX INFO: renamed from: e */
    public String getValueStrict(String str, String str2) {
        String string = getString(str, str2, (String) null);
        if (string == null) {
            throwFailedToFind(str, str2);
        }
        return string;
    }

    /* JADX INFO: renamed from: b */
    public String getString(String str, String str2, String str3) {
        String valueInternal = getValueInternal(str, str2, true, "string");
        if (valueInternal == null) {
            return str3;
        }
        if (valueInternal.contains("%{") && valueInternal.contains("}")) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Doesn't support dynamic %{} sections");
        }
        return valueInternal;
    }

    /* JADX INFO: renamed from: c */
    public String getStringRaw(String str, String str2, String str3) {
        String valueInternal = getValueInternal(str, str2, true, "string");
        if (valueInternal == null) {
            return str3;
        }
        return valueInternal;
    }

    /* JADX INFO: renamed from: f */
    public String getStringRawStrict(String str, String str2) {
        String stringRaw = getStringRaw(str, str2, (String) null);
        if (stringRaw == null) {
            throwFailedToFind(str, str2);
        }
        return stringRaw;
    }

    /* JADX INFO: renamed from: b */
    public static String unescape(String str) {
        if (str == null) {
            return null;
        }
        return Utility.replaceSubstring(str, "\\n", "\n");
    }

    /* JADX INFO: renamed from: a */
    public LocalizedText getUnitReference(CustomUnitConfig customUnitConfig, String str, String str2, String str3) throws ConfigParseException {
        LocaleString localeString = getLocaleString(str, str2, str3, true);
        if (localeString == null) {
            return null;
        }
        try {
            return new LocalizedText(customUnitConfig, localeString);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ConfigParseException("[" + str + "]" + str2 + ": " + e.getMessage());
        }
    }

    /* JADX INFO: renamed from: a */
    public LocaleString getLocaleString(String str, String str2, String str3, boolean z) {
        String string;
        String valueStrict;
        if (z) {
            string = getStringRaw(str, str2, (String) null);
        } else {
            string = getString(str, str2, (String) null);
        }
        if (string == null) {
            if (str3 == null) {
                return null;
            }
            string = str3;
        }
        String strUnescape = unescape(string);
        LocaleString localeString = new LocaleString();
        if (strUnescape != null && strUnescape.startsWith("i:")) {
            localeString.text = strUnescape.substring("i:".length());
            localeString.text = localeString.text.trim();
            Locale.get(localeString.text, new Object[0]);
            return localeString;
        }
        ArrayList arrayList = new ArrayList();
        LanguagePart languagePart = new LanguagePart();
        languagePart.locale = null;
        languagePart.text = strUnescape;
        arrayList.add(languagePart);
        String str4 = str2 + "_";
        for (String str5 : getKeysStartingWith(str, str4)) {
            String lowerCase = str5.substring(str4.length()).toLowerCase(java.util.Locale.ROOT);
            if (z) {
                valueStrict = getStringRawStrict(str, str5);
            } else {
                valueStrict = getValueStrict(str, str5);
            }
            String strUnescape2 = unescape(valueStrict);
            LanguagePart languagePart2 = new LanguagePart();
            languagePart2.locale = lowerCase;
            languagePart2.text = strUnescape2;
            arrayList.add(languagePart2);
        }
        localeString.localizedItems = (LanguagePart[]) arrayList.toArray(new LanguagePart[0]);
        localeString.resolveText();
        return localeString;
    }

    /* JADX INFO: renamed from: a */
    public LogicBoolean getLogicBoolean(CustomUnitConfig customUnitConfig, String str, String str2) {
        try {
            return LogicBoolean.create(customUnitConfig, getValueStrict(str, str2), null);
        } catch (RuntimeException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": " + e.getMessage(), e);
        }
    }

    /* JADX INFO: renamed from: a */
    public LogicBoolean getLogicBoolean(CustomUnitConfig customUnitConfig, String str, String str2, LogicBoolean logicBoolean) {
        try {
            return LogicBoolean.create(customUnitConfig, getString(str, str2, (String) null), logicBoolean);
        } catch (RuntimeException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": " + e.getMessage(), e);
        }
    }

    /* JADX INFO: renamed from: b */
    public LogicBoolean getInt(CustomUnitConfig customUnitConfig, String str, String str2, LogicBoolean logicBoolean) {
        return getLogicBooleanWithReturnType(customUnitConfig, str, str2, logicBoolean, LogicBoolean.ReturnType.unit);
    }

    /* JADX INFO: renamed from: c */
    public LogicBoolean getLogicBooleanNumber(CustomUnitConfig customUnitConfig, String str, String str2, LogicBoolean logicBoolean) {
        return getLogicBooleanWithReturnType(customUnitConfig, str, str2, logicBoolean, LogicBoolean.ReturnType.number);
    }

    /* JADX INFO: renamed from: a */
    public LogicBoolean getLogicBooleanWithReturnType(CustomUnitConfig customUnitConfig, String str, String str2, LogicBoolean logicBoolean, LogicBoolean.ReturnType returnType) {
        return getLogicBooleanBlockWithReturnType(getString(str, str2, (String) null), customUnitConfig, str, str2, logicBoolean, returnType);
    }

    /* JADX INFO: renamed from: a */
    public static LogicBoolean getLogicBooleanBlock(String str, CustomUnitConfig customUnitConfig, String str2, String str3, LogicBoolean logicBoolean) {
        return getLogicBooleanBlockWithReturnType(str, customUnitConfig, str2, str3, logicBoolean, LogicBoolean.ReturnType.unit);
    }

    /* JADX INFO: renamed from: a */
    public static LogicBoolean getLogicBooleanBlockWithReturnType(String str, CustomUnitConfig customUnitConfig, String str2, String str3, LogicBoolean logicBoolean, LogicBoolean.ReturnType returnType) {
        if (str == null) {
            return logicBoolean;
        }
        try {
            if (returnType == LogicBoolean.ReturnType.number && Utility.isNumeric(str)) {
                return LogicBoolean.StaticValueBoolean.getStaticNumber(str);
            }
            if (str.toLowerCase(java.util.Locale.ROOT).startsWith("unitref ")) {
                str = str.substring("unitref ".length()).trim();
            }
            LogicBoolean booleanBlock = LogicBooleanLoader.parseBooleanBlock(customUnitConfig, str, false);
            if (booleanBlock == null) {
                return null;
            }
            LogicBoolean.ReturnType returnType2 = booleanBlock.getReturnType();
            if (returnType2 != returnType) {
                throw new RuntimeException("[" + str2 + "]" + str3 + ": Type mismatch. Expected type:" + returnType + " got:" + returnType2);
            }
            return booleanBlock;
        } catch (RuntimeException e) {
            throw new RuntimeException("[" + str2 + "]" + str3 + ": " + e.getMessage(), e);
        }
    }

    /* JADX INFO: renamed from: a */
    public AnimationTag getAnimationTag(String str, String str2, AnimationTag animationTag) throws ConfigParseException {
        String string = getString(str, str2, (String) null);
        if (string == null) {
            return animationTag;
        }
        if (string.trim().equals(VariableScope.nullOrMissingString)) {
            return animationTag;
        }
        if (string.contains(",")) {
            throw new ConfigParseException("[" + str + "]" + str2 + ": Expected single tag, got:" + string);
        }
        return AnimationTag.c(string);
    }

    /* JADX INFO: renamed from: a */
    public AnimationSet getAnimationSet(CustomUnitConfig customUnitConfig, String str, String str2, AnimationSet animationSet) {
        return AnimationTag.a(getString(str, str2, (String) null), animationSet);
    }

    /* JADX INFO: renamed from: a */
    public CustomUnitActionHandler getCustomUnitAction(CustomUnitConfig customUnitConfig, String str, String str2, CustomUnitActionHandler customUnitActionHandler) {
        String string = getString(str, str2, (String) null);
        if (string == null) {
            return customUnitActionHandler;
        }
        return customUnitConfig.addActionHandler(string, str2, str);
    }

    /* JADX INFO: renamed from: a */
    public Resource getAttachmentData(CustomUnitConfig customUnitConfig, String str, String str2, Resource resource, boolean z) {
        Resource resourceA;
        String string = getString(str, str2, (String) null);
        if (string == null) {
            return resource;
        }
        if (z && (resourceA = Resource.a(string)) != null) {
            return resourceA;
        }
        Resource resourceFindCustomResourceInList = customUnitConfig.findCustomResourceInList(string);
        if (resourceFindCustomResourceInList == null) {
            throw new BooleanParseException("[" + str + "]" + str2 + ": Could not find custom resource type of:" + string);
        }
        return resourceFindCustomResourceInList;
    }

    /* JADX INFO: renamed from: a */
    public Integer getColorAsInt(String str, String str2, Integer num) {
        String string = getString(str, str2, (String) null);
        if (string == null) {
            return num;
        }
        if (string.equals(VariableScope.nullOrMissingString)) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Unknown color: ''");
        }
        try {
            return Integer.valueOf(Color.a(string));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Unknown color:" + string);
        }
    }

    /* JADX INFO: renamed from: g */
    public int getIntStrict(String str, String str2) {
        String valueInternal = getValueInternal(str, str2, false, "int");
        try {
            return Integer.parseInt(valueInternal);
        } catch (NumberFormatException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Not a static integer: " + valueInternal);
        }
    }

    /* JADX INFO: renamed from: a */
    public Short getShort(String str, String str2, Short sh) {
        String valueInternal = getValueInternal(str, str2, true, "short");
        if (valueInternal == null) {
            return sh;
        }
        try {
            return Short.valueOf(Short.parseShort(valueInternal));
        } catch (NumberFormatException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Not a static integer: " + valueInternal);
        }
    }

    /* JADX INFO: renamed from: b */
    public Integer getInt(String str, String str2, Integer num) {
        String valueInternal = getValueInternal(str, str2, true, "int");
        if (valueInternal == null) {
            return num;
        }
        try {
            return Integer.valueOf(Integer.parseInt(valueInternal));
        } catch (NumberFormatException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Not a static integer: " + valueInternal);
        }
    }

    /* JADX INFO: renamed from: a */
    public Float getFloat(String str, String str2, Float f) {
        String valueInternal = getValueInternal(str, str2, true, "float");
        if (valueInternal == null) {
            return f;
        }
        try {
            return Float.valueOf(Float.parseFloat(valueInternal));
        } catch (NumberFormatException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Not a static float: " + valueInternal);
        }
    }

    /* JADX INFO: renamed from: a */
    public PointF getPointF(String str, String str2, PointF pointF) {
        String valueInternal = getValueInternal(str, str2, true, "point");
        if (valueInternal == null) {
            return pointF;
        }
        if (valueInternal.equalsIgnoreCase("NONE")) {
            return null;
        }
        try {
            String[] strArrSplit = valueInternal.split(",");
            if (strArrSplit.length != 2) {
                throw new NumberFormatException("Got:" + strArrSplit.length + " elements expected 2");
            }
            PointF pointF2 = new PointF();
            pointF2.x = Float.parseFloat(strArrSplit[0]);
            pointF2.y = Float.parseFloat(strArrSplit[1]);
            return pointF2;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to read point:" + valueInternal + " in key:" + str2 + " section:" + str + " expected format: x,y");
        }
    }

    /* JADX INFO: renamed from: a */
    public Vector3D getVector3D(String str, String str2, Vector3D vector3D) {
        String valueInternal = getValueInternal(str, str2, true, "point3d");
        if (valueInternal == null) {
            return vector3D;
        }
        if (valueInternal.equalsIgnoreCase("NONE")) {
            return null;
        }
        try {
            String[] strArrSplit = valueInternal.split(",");
            if (strArrSplit.length != 2 && strArrSplit.length != 3) {
                throw new NumberFormatException("Got:" + strArrSplit.length + " elements expected 2 or 3");
            }
            Vector3D vector3D2 = new Vector3D();
            vector3D2.x = Float.parseFloat(strArrSplit[0]);
            vector3D2.y = Float.parseFloat(strArrSplit[1]);
            if (strArrSplit.length > 2) {
                vector3D2.z = Float.parseFloat(strArrSplit[2]);
            }
            return vector3D2;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to read point:" + valueInternal + " in key:" + str2 + " section:" + str + " expected format: x,y,[height]");
        }
    }

    /* JADX INFO: renamed from: h */
    public Float getFloatStrict(String str, String str2) {
        Float time = getTime(str, str2, (Float) null);
        if (time == null) {
            throw new RuntimeException("Could not find key:" + str2 + " in section:" + str);
        }
        return time;
    }

    /* JADX INFO: renamed from: b */
    public Float getTime(String str, String str2, Float f) {
        return getFloatOrTime(str, str2, f, false);
    }

    /* JADX INFO: renamed from: c */
    public Float getTimeAsFrames(String str, String str2, Float f) {
        Float floatOrTime = getFloatOrTime(str, str2, (Float) null, false);
        if (floatOrTime == null) {
            return f;
        }
        return Float.valueOf(floatOrTime.floatValue() * 16.666666f);
    }

    /* JADX INFO: renamed from: a */
    public Float getFloatOrTime(String str, String str2, Float f, boolean z) {
        String valueInternal = getValueInternal(str, str2, true, "time");
        if (valueInternal == null) {
            return f;
        }
        try {
            return Float.valueOf(parseUnitValue(valueInternal, z, str, str2));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to read time:" + valueInternal + " in key:" + str2 + " section:" + str + " expected a float with optional 's' or 'ms' postfix");
        }
    }

    /* JADX INFO: renamed from: d */
    public Float getInvertedTime(String str, String str2, Float f) {
        return getFloatOrTime(str, str2, f, true);
    }

    /* JADX INFO: renamed from: a */
    public static float parseUnitValue(String str, boolean z, String str2, String str3) {
        float f;
        boolean z2 = false;
        if (str.endsWith("s")) {
            str = str.substring(0, str.length() - 1);
            f = 60.0f;
            z2 = true;
        } else {
            f = 1.0f;
        }
        try {
            float f2 = Float.parseFloat(str) * f;
            if (z) {
                if (z2) {
                    return 1.0f / f2;
                }
                return f2;
            }
            return f2;
        } catch (NumberFormatException e) {
            throw new RuntimeException("[" + str2 + "]" + str3 + ": Failed to read time:" + str + " expected a float with optional 's' postfix");
        }
    }

    /* JADX INFO: renamed from: i */
    public float getFloatStrictRaw(String str, String str2) {
        String valueInternal = getValueInternal(str, str2, false, "float");
        try {
            return Float.parseFloat(valueInternal);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to read float:" + valueInternal + " in key:" + str2 + " section:" + str);
        }
    }

    /* JADX INFO: renamed from: j */
    public double getDoubleStrictRaw(String str, String str2) {
        String valueInternal = getValueInternal(str, str2, false, "double");
        try {
            return Double.parseDouble(valueInternal);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to read float:" + valueInternal + " in key:" + str2 + " section:" + str);
        }
    }

    /* JADX INFO: renamed from: a */
    public double getDouble(String str, String str2, double d) {
        String valueInternal = getValueInternal(str, str2, true, "double");
        if (valueInternal == null) {
            return d;
        }
        try {
            return Double.parseDouble(valueInternal);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to read float:" + valueInternal + " in key:" + str2 + " section:" + str);
        }
    }

    /* JADX INFO: renamed from: a */
    public long getLong(String str, String str2, long j) {
        String valueInternal = getValueInternal(str, str2, true, "long");
        if (valueInternal == null) {
            return j;
        }
        try {
            return Long.parseLong(valueInternal);
        } catch (NumberFormatException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Failed to read long:" + valueInternal + " in key:" + str2 + " section:" + str);
        }
    }

    /* JADX INFO: renamed from: d */
    public void setValueIfMissing(String str, String str2, String str3) {
        if (this.strict) {
            throw new RuntimeException("locked changes");
        }
        LinkedHashMap linkedHashMap = (LinkedHashMap) this.sections.get(str);
        if (linkedHashMap == null) {
            linkedHashMap = new LinkedHashMap();
            this.sections.put(str, linkedHashMap);
        }
        if (linkedHashMap.get(str2) == null) {
            linkedHashMap.put(str2, str3);
        }
    }

    /* JADX INFO: renamed from: e */
    public void setValue(String str, String str2, String str3) {
        if (this.strict) {
            throw new RuntimeException("locked changes");
        }
        LinkedHashMap linkedHashMap = (LinkedHashMap) this.sections.get(str);
        if (linkedHashMap == null) {
            linkedHashMap = new LinkedHashMap();
            this.sections.put(str, linkedHashMap);
        }
        linkedHashMap.put(str2, str3);
    }

    /* JADX INFO: renamed from: a */
    public void merge(IniFile iniFile) {
        if (this.strict) {
            throw new RuntimeException("locked changes");
        }
        for (String str : iniFile.sections.keySet()) {
            LinkedHashMap<String,String> linkedHashMap = (LinkedHashMap) iniFile.sections.get(str);
            if (!getBoolean(str, "@copyFrom_skipThisSection", (Boolean) false).booleanValue()) {
                LinkedHashMap linkedHashMap2 = (LinkedHashMap) this.sections.get(str);
                if (linkedHashMap2 == null) {
                    linkedHashMap2 = new LinkedHashMap();
                    this.sections.put(str, linkedHashMap2);
                }
                for (String str2 : linkedHashMap.keySet()) {
                    if (linkedHashMap2.get(str2) == null) {
                        linkedHashMap2.put(str2, linkedHashMap.get(str2));
                    }
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public Rect getRect(String str, String str2, Rect rect) {
        String string = getString(str, str2, (String) null);
        if (string == null) {
            return rect;
        }
        String[] strArrSplit = string.split(",");
        if (strArrSplit.length != 4) {
            throw new RuntimeException("[" + str + "]" + str2 + ": getRect: expected 4 ints, not:" + strArrSplit.length);
        }
        try {
            return new Rect(Integer.valueOf(strArrSplit[0].trim()).intValue(), Integer.valueOf(strArrSplit[1].trim()).intValue(), Integer.valueOf(strArrSplit[2].trim()).intValue(), Integer.valueOf(strArrSplit[3].trim()).intValue());
        } catch (NumberFormatException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": getRect expected ints got: " + string);
        }
    }

    /* JADX INFO: renamed from: a */
    public Enum getEnum(String str, String str2, Enum r8, Class cls) {
        try {
            return parseEnum(getString(str, str2, (String) null), r8, cls);
        } catch (ConfigParseException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": " + e.getMessage(), e);
        }
    }

    /* JADX INFO: renamed from: a */
    public static Enum parseEnum(String str, Enum r4, Class cls) throws ConfigParseException {
        if (str == null) {
            return r4;
        }
        String strTrim = str.trim();
        for (Object obj : cls.getEnumConstants()) {
            Enum r0 = (Enum) obj;
            if (r0.name().equalsIgnoreCase(strTrim)) {
                return r0;
            }
        }
        throw createEnumParseException(strTrim, cls);
    }

    /* JADX INFO: renamed from: a */
    private static RuntimeException createEnumParseException(String str, Class cls) throws ConfigParseException {
        String str2 = VariableScope.nullOrMissingString;
        for (Object obj : cls.getEnumConstants()) {
            Enum r0 = (Enum) obj;
            if (!str2.equals(VariableScope.nullOrMissingString)) {
                str2 = str2 + ",";
            }
            str2 = str2 + r0.name();
        }
        throw new ConfigParseException("Unknown value: " + str + " (Expected: " + Utility.truncateWithEllipsis(str2, 100) + ")");
    }

    /* JADX INFO: renamed from: c */
    public FastArrayList<String> getSectionsWithKey(String str) {
        FastArrayList fastArrayList = new FastArrayList();
        for (String str2 : this.sections.keySet()) {
            if (((Map) this.sections.get(str2)).get(str) != null) {
                fastArrayList.add(str2);
            }
        }
        return fastArrayList;
    }

    /* JADX INFO: renamed from: d */
    public FastArrayList<String> getSectionsWithKeyStartingWith(String str) {
        FastArrayList fastArrayList = new FastArrayList();
        for (String str2 : this.sections.keySet()) {
            Map map = (Map) this.sections.get(str2);
            Iterator it = map.keySet().iterator();
            while (true) {
                if (it.hasNext()) {
                    String str3 = (String) it.next();
                    if (str3.startsWith(str) && !"IGNORE".equals(map.get(str3))) {
                        fastArrayList.add(str2);
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return fastArrayList;
    }

    /* JADX INFO: renamed from: d */
    public LinkedHashMap getSectionsMap() {
        return this.sections;
    }

    /* JADX INFO: renamed from: k */
    public FastArrayList<String> getKeysStartingWith(String str, String str2) {
        FastArrayList fastArrayList = new FastArrayList();
        Map<String,String> map = this.sections.get(str);
        if (map != null) {
            for (String str3 : map.keySet()) {
                if (str3.startsWith(str2) && !"IGNORE".equals(map.get(str3))) {
                    fastArrayList.add(str3);
                }
            }
        }
        return fastArrayList;
    }

    /* JADX INFO: renamed from: f */
    public FastArrayList getKeysStartingWithTwoPrefixes(String str, String str2, String str3) {
        FastArrayList fastArrayList = new FastArrayList();
        Map<String,String> map = (Map) this.sections.get(str);
        if (map != null) {
            for (String str4 : map.keySet()) {
                if (str4.startsWith(str2) || str4.startsWith(str3)) {
                    fastArrayList.add(str4);
                }
            }
        }
        return fastArrayList;
    }

    /* JADX INFO: renamed from: l */
    public boolean hasKeyStartingWith(String str, String str2) {
        Map map = (Map) this.sections.get(str);
        if (map != null) {
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                if (((String) it.next()).startsWith(str2)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: renamed from: e */
    public FastArrayList<String> getSectionsStartingWith(String str) {
        FastArrayList fastArrayList = new FastArrayList();
        for (String str2 : this.sections.keySet()) {
            if (str2.startsWith(str) && isSectionNotEmpty(str2)) {
                fastArrayList.add(str2);
            }
        }
        return fastArrayList;
    }

    /* JADX INFO: renamed from: m */
    public FastArrayList<String> getSectionsStartingWithOr(String str, String str2) {
        FastArrayList fastArrayList = new FastArrayList();
        for (String str3 : this.sections.keySet()) {
            if (str3.startsWith(str) || str3.startsWith(str2)) {
                if (isSectionNotEmpty(str3)) {
                    fastArrayList.add(str3);
                }
            }
        }
        return fastArrayList;
    }

    /* JADX INFO: renamed from: f */
    public boolean hasSection(String str) {
        if (this.sections.get(str) != null) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: g */
    public boolean isSectionNotEmpty(String str) {
        Map<String,String> map = this.sections.get(str);
        if (map == null) {
            return false;
        }
        for (String str2 : map.keySet()) {
            if (str2 != null && !str2.startsWith("@")) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: renamed from: n */
    public boolean hasKey(String str, String str2) {
        if (getValueOrThrow(str, str2, true) != null) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: g */
    public static boolean parseBoolean(String str, String str2, String str3) {
        if (str3.equalsIgnoreCase("true")) {
            return true;
        }
        if (str3.equalsIgnoreCase("false")) {
            return false;
        }
        throw new RuntimeException("[" + str + "]" + str2 + ": Unexpected boolean value:'" + str3 + "'");
    }

    /* JADX INFO: renamed from: h */
    public static float parseFloat(String str, String str2, String str3) {
        try {
            return Float.parseFloat(str3);
        } catch (NumberFormatException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Failed to read float:" + str3);
        }
    }

    /* JADX INFO: renamed from: i */
    public static int parseInt(String str, String str2, String str3) {
        try {
            return Integer.parseInt(str3);
        } catch (NumberFormatException e) {
            throw new RuntimeException("[" + str + "]" + str2 + ": Failed to read int:" + str3);
        }
    }

    /* JADX INFO: renamed from: j */
    public static AnimationTag parseAnimationTag(String str, String str2, String str3) throws ConfigParseException {
        String strTrim = str3.trim();
        if (strTrim.contains(",")) {
            throw new ConfigParseException("[" + str + "]" + str2 + ": Unexpected single tag, got:'" + strTrim + "'");
        }
        if (strTrim.contains("\"")) {
            throw new ConfigParseException("[" + str + "]" + str2 + ": tag cannot contain quote, got:'" + strTrim + "'");
        }
        if (strTrim.contains("'")) {
            throw new ConfigParseException("[" + str + "]" + str2 + ": tag cannot contain quote, got:'" + strTrim + "'");
        }
        if (strTrim.contains(" ")) {
            throw new ConfigParseException("[" + str + "]" + str2 + ": tag cannot contain space, got:'" + strTrim + "'");
        }
        return AnimationTag.c(strTrim);
    }
}
