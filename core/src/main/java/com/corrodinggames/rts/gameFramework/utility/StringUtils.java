package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.gameFramework.GameEngine;

import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.al */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/al.class */
public class StringUtils {
    public static ArrayList<String> a(String string1, String string2, String string3, boolean boolean4) {
        int var4 = 0;
        StringBuffer var5 = new StringBuffer();
        ArrayList var6 = new ArrayList();
        char var7 = string2.charAt(0);
        char var8 = string3.charAt(0);
        int var9 = string2.length();
        int var10 = string3.length();
        int var11 = string1.length();

        for (int var12 = 0; var12 < var11; var12++) {
            char var13 = string1.charAt(var12);
            if (var13 == '(') {
                var4++;
            } else if (var13 == ')') {
                var4--;
            }

            if (var4 == 0) {
                if (var7 == var13
                        && (var9 == 1 || string1.indexOf(string2, var12) == var12)
                        && (!boolean4 || !b(string1, var12 - 1) && !b(string1, var12 + string2.length()))) {
                    var6.add(var5.toString());
                    var5 = new StringBuffer();
                    var12 += string2.length() - 1;
                    continue;
                }

                if (var8 == var13
                        && (var10 == 1 || string1.indexOf(string3, var12) == var12)
                        && (!boolean4 || !b(string1, var12 - 1) && !b(string1, var12 + string3.length()))) {
                    var6.add(var5.toString());
                    var5 = new StringBuffer();
                    var12 += string3.length() - 1;
                    continue;
                }
            }

            var5.append(var13);
        }

        var6.add(var5.toString());
        return var6;
    }

    public static ArrayList<String> a(String string1, String string2, boolean boolean3, boolean boolean4) {
        int var4 = 0;
        StringBuffer var5 = new StringBuffer();
        ArrayList var6 = new ArrayList();
        char var7 = string2.charAt(0);
        int var8 = string2.length();
        int var9 = string1.length();
        boolean var10 = false;
        boolean var11 = false;
        boolean var12 = false;
        boolean var13 = false;
        if (string2.equals("-")) {
            var13 = true;
        }

        char var14 = 0;
        char var15 = 0;
        String var16 = string1;
        if (boolean4) {
            var16 = string1.toLowerCase(Locale.ROOT);
        }

        for (int var17 = 0; var17 < var9; var17++) {
            char var18 = var16.charAt(var17);
            char var19 = string1.charAt(var17);
            if (var15 != ' ') {
                var14 = var15;
            }

            var15 = var18;
            boolean var20 = var10;
            var10 = false;
            if (!var20) {
                if (var18 == '\\') {
                    var10 = true;
                }

                if (!var12 && var18 == '\'') {
                    var11 = !var11;
                }

                if (!var11 && var18 == '"') {
                    var12 = !var12;
                }
            }

            boolean var21 = var11 || var12;
            if (!var21) {
                if (var18 == '(') {
                    var4++;
                } else if (var18 == ')') {
                    var4--;
                }

                if (var4 == 0
                        && var7 == var18
                        && (var8 == 1 || var16.indexOf(string2, var17) == var17)
                        && (!boolean3 || !b(string1, var17 - 1) && !b(string1, var17 + string2.length()))
                        && (!var13 || var14 != '*' && var14 != '/' && var14 != '+')) {
                    var6.add(var5.toString());
                    var5.setLength(0);
                    var17 += string2.length() - 1;
                    continue;
                }
            }

            var5.append(var19);
        }

        var6.add(var5.toString());
        return var6;
    }

    public static ArrayList<String> a(String string1, String string2, boolean boolean3) {
        int var3 = 0;
        char[] var4 = new char[5];
        StringBuffer var5 = new StringBuffer();
        ArrayList var6 = new ArrayList();
        char var7 = string2.charAt(0);
        int var8 = string2.length();
        int var9 = string1.length();

        for (int var10 = 0; var10 < var9; var10++) {
            char var11 = string1.charAt(var10);
            byte var12 = 0;
            char var13 = 0;
            if (var11 == '(') {
                var12 = 40;
            } else if (var11 == ')') {
                var13 = '(';
            } else if (var11 == '[') {
                var12 = 91;
            } else if (var11 == ']') {
                var13 = '[';
            }

            if (var12 != 0) {
                if (++var3 >= var4.length) {
                    int var14 = var4.length;
                    int var15 = var14 + 5;
                    char[] var16 = new char[var15];
                    System.arraycopy(var4, 0, var16, 0, var14);
                    var4 = var16;
                }

                var4[var3] = (char) var12;
            } else if (var13 != 0) {
                if (var4[var3] == var13) {
                    var3--;
                } else {
                    GameEngine.log("Bad bracket order: '" + string1 + "' at index:" + var10 + " got " + var13 + " type expected: " + var4[var3]);
                }
            }

            if (var3 == 0) {
                boolean var17 = false;
                if (var7 == var11 && (var8 == 1 || string1.indexOf(string2, var10) == var10)) {
                    var17 = true;
                }

                if (var17 && (!boolean3 || !b(string1, var10 - 1) && !b(string1, var10 + string2.length()))) {
                    var6.add(var5.toString());
                    var5 = new StringBuffer();
                    var10 += string2.length() - 1;
                    continue;
                }
            }

            var5.append(var11);
        }

        var6.add(var5.toString());
        return var6;
    }

    public static String[] b(String str, String str2, boolean z) {
        if (!str.contains(str2)) {
            return new String[]{str};
        }
        return (String[]) a(str, str2, z).toArray(new String[0]);
    }

    public static int a(String str, int i) {
        if (str.charAt(i) != '(') {
            GameEngine.logColored("getBracketEnd: Did not start on a bracket");
            return -1;
        }
        if (i + 1 >= str.length()) {
            return -1;
        }
        int i2 = 1;
        for (int i3 = i + 1; i3 < str.length(); i3++) {
            char cCharAt = str.charAt(i3);
            if (cCharAt == '(') {
                i2++;
            } else if (cCharAt == ')') {
                i2--;
            }
            if (i2 == 0) {
                return i3;
            }
        }
        return -1;
    }

    public static int a(String str) {
        int i = 0;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        for (char c : str.toCharArray()) {
            boolean z4 = z;
            z = false;
            if (!z4) {
                if (c == '\\') {
                    z = true;
                }
                if (!z3 && c == '\'') {
                    z2 = !z2;
                }
                if (!z2 && c == '\"') {
                    z3 = !z3;
                }
            }
            if (!z2 && !z3) {
                if (c == '(') {
                    i++;
                } else if (c == ')') {
                    i--;
                }
            }
        }
        return i;
    }

    public static int b(String str) {
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt == '(') {
                i++;
            } else if (cCharAt == ')') {
                i--;
            }
        }
        return i;
    }

    public static String[] a(String str, char c) {
        ArrayList arrayList = new ArrayList();
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        StringBuilder sb = new StringBuilder();
        for (char c2 : str.toCharArray()) {
            boolean z4 = z;
            z = false;
            if (!z4) {
                if (c2 == '\\') {
                    z = true;
                }
                if (!z3 && c2 == '\'') {
                    z2 = !z2;
                }
                if (!z2 && c2 == '\"') {
                    z3 = !z3;
                }
            }
            if (c2 == c && !z2 && !z3 && !z2) {
                arrayList.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c2);
            }
        }
        if (sb.length() != 0) {
            arrayList.add(sb.toString());
            sb.setLength(0);
        }
        return (String[]) arrayList.toArray(new String[0]);
    }


    public static String[] b(String string, char character) {
        ArrayList<String> var2 = new ArrayList();
        boolean var3 = false;
        StringBuilder var4 = new StringBuilder();

        for (char var8 : string.toCharArray()) {
            boolean var9 = var3;
            var3 = false;
            if (!var9) {
                if (var8 == '\\') {
                    var3 = true;
                    continue;
                }

                if (var8 == character) {
                    var2.add(var4.toString());
                    var4.setLength(0);
                    continue;
                }
            }

            var4.append(var8);
        }

        if (var4.length() != 0) {
            var2.add(var4.toString());
            var4.setLength(0);
        }

        if (var2.size() == 0) {
            var2.add("");
        }

        return var2.toArray(new String[0]);
    }

    public static String a(String[] strArr) {
        StringBuilder stringBuffer = new StringBuilder();
        boolean z = true;
        int length = strArr.length;
        for (String s : strArr) {
            String strReplace = s;
            if (z) {
                z = false;
            } else {
                stringBuffer.append(",");
            }
            if (strReplace.contains("\\")) {
                strReplace = strReplace.replace("\\", "\\\\");
            }
            if (strReplace.contains(",")) {
                strReplace = strReplace.replace(",", "\\,");
            }
            stringBuffer.append(strReplace);
        }
        return stringBuffer.toString();
    }

    public static int a(String str, String str2) {
        return a(str, str2, 0);
    }

    public static int a(String str, String str2, int i) {
        int i2 = 0;
        char cCharAt = str2.charAt(0);
        int length = str2.length();
        for (int i3 = i; i3 < str.length(); i3++) {
            char cCharAt2 = str.charAt(i3);
            if (cCharAt2 == '(') {
                i2++;
            } else if (cCharAt2 == ')') {
                i2--;
            }
            if (i2 == 0 && cCharAt == cCharAt2 && (length == 1 || str.indexOf(str2, i3) == i3)) {
                return i3;
            }
        }
        return -1;
    }

    public static int b(String str, String str2, int i) {
        int i2 = 0;
        char[] cArr = new char[5];
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        char cCharAt = str2.charAt(0);
        int length = str2.length();
        for (int i3 = i; i3 < str.length(); i3++) {
            char cCharAt2 = str.charAt(i3);
            boolean z4 = z;
            if (!z4) {
                z = cCharAt2 == '\\';
                if (!z3 && cCharAt2 == '\'') {
                    z2 = !z2;
                }
                if (!z2 && cCharAt2 == '\"') {
                    z3 = !z3;
                }
            }
            if (!(z2 || z3)) {
                int i4 = i2;
                char c = 0;
                char c2 = 0;
                if (cCharAt2 == '(') {
                    c = '(';
                } else if (cCharAt2 == ')') {
                    c2 = '(';
                } else if (cCharAt2 == '[') {
                    c = '[';
                } else if (cCharAt2 == ']') {
                    c2 = '[';
                }
                if (c != 0) {
                    i2++;
                    if (i2 >= cArr.length) {
                        int length2 = cArr.length;
                        char[] cArr2 = new char[length2 + 5];
                        System.arraycopy(cArr, 0, cArr2, 0, length2);
                        cArr = cArr2;
                    }
                    cArr[i2] = c;
                } else if (c2 != 0) {
                    if (cArr[i2] == c2) {
                        i2--;
                    } else {
                        GameEngine.log("Bad bracket order: '" + str + "' at index:" + i3 + " got " + c2 + " type expected: " + cArr[i2]);
                    }
                }
                if ((i4 == 0 || i2 == 0) && cCharAt == cCharAt2 && (length == 1 || str.indexOf(str2, i3) == i3)) {
                    return i3;
                }
            }
        }
        return -1;
    }

    public static boolean b(String str, int i) {
        if (i < 0 || i >= str.length()) {
            return false;
        }
        char cCharAt = str.charAt(i);
        return Character.isLetter(cCharAt) || Character.isDigit(cCharAt);
    }

    public static int a(String str, String str2, String str3) {
        int iIndexOf = str.indexOf(str2);
        int iIndexOf2 = str.indexOf(str3);
        if (iIndexOf == -1) {
            return iIndexOf2;
        }
        if (iIndexOf2 != -1 && iIndexOf >= iIndexOf2) {
            return iIndexOf2;
        }
        return iIndexOf;
    }

    public static int a(String str, int i, String[] strArr) {
        int i2 = -1;
        for (String str2 : strArr) {
            int iA = a(str, str2, i);
            if (iA != -1 && (i2 > iA || i2 == -1)) {
                i2 = iA;
            }
        }
        return i2;
    }

    public static String c(String str) {
        boolean z = false;
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '-') {
                z = !z;
            } else if (cCharAt != '+' && cCharAt != ' ') {
                if (z) {
                    return "-" + str.substring(i);
                }
                if (i == 0) {
                    return str;
                }
                return str.substring(i);
            }
        }
        return str;
    }

    public static String[] b(String str, String str2) {
        int iIndexOf = str.indexOf(str2);
        if (iIndexOf == -1) {
            return null;
        }
        return new String[]{str.substring(0, iIndexOf), str.substring(iIndexOf + str2.length())};
    }

    public static String[] c(String str, String str2) {
        int iB = b(str, str2, 0);
        if (iB == -1) {
            return null;
        }
        return new String[]{str.substring(0, iB), str.substring(iB + str2.length())};
    }

    public static final String d(String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT);
    }

    public static String[] e(String str) {
        return b(str, ',');
    }
}
