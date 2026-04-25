package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/g.class */
public final class AnimationTag {

    /* JADX INFO: renamed from: a */
    final String tagName;
    public static ArrayList<AnimationTag> b = new ArrayList();
    public static final AnimationTag[] c = new AnimationTag[0];
    public static final AnimationSet d = new AnimationSet(c);

    private AnimationTag(String str) {
        this.tagName = str;
    }

    public String toString() {
        return this.tagName;
    }

    public static AnimationSet a(String str) {
        return a(str, (AnimationSet) null);
    }

    public static AnimationSet a(String str, AnimationSet animationSet) {
        if (str == null) {
            return animationSet;
        }
        if (str.trim().equals(VariableScope.nullOrMissingString)) {
            return animationSet;
        }
        ArrayList arrayList = new ArrayList();
        for (String str2 : str.split(",")) {
            String strTrim = str2.trim();
            if (!strTrim.equals(VariableScope.nullOrMissingString)) {
                AnimationTag animationTagC = c(strTrim);
                if (!arrayList.contains(animationTagC)) {
                    arrayList.add(animationTagC);
                }
            }
        }
        if (arrayList.size() == 0) {
            return animationSet;
        }
        return new AnimationSet((AnimationTag[]) arrayList.toArray(new AnimationTag[0]));
    }

    public static AnimationTag b(String str) throws ConfigParseException {
        String strTrim = str.trim();
        if (strTrim.contains(",")) {
            throw new ConfigParseException("Expected single tag, got:" + strTrim);
        }
        return c(strTrim);
    }

    public static AnimationTag c(String str) {
        String lowerCase = str.trim().toLowerCase(Locale.ROOT);
        for (AnimationTag animationTag : b) {
            if (animationTag.tagName.equals(lowerCase)) {
                return animationTag;
            }
        }
        AnimationTag animationTag2 = new AnimationTag(lowerCase);
        b.add(animationTag2);
        return animationTag2;
    }

    public static void a(AnimationSet animationSet, GameOutputStream gameOutputStream) throws IOException {
        if (animationSet == null) {
            gameOutputStream.writeStringNullable((String) null);
            return;
        }
        if (animationSet.a.length == 0) {
            gameOutputStream.writeStringNullable(VariableScope.nullOrMissingString);
            return;
        }
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (AnimationTag animationTag : animationSet.a) {
            if (!z) {
                sb.append(",");
            }
            z = false;
            sb.append(animationTag.tagName);
        }
        gameOutputStream.writeStringNullable(sb.toString());
    }

    public static AnimationSet a(GameInputStream gameInputStream) throws IOException {
        String nullableString = gameInputStream.readNullableString();
        if (nullableString == null) {
            return null;
        }
        return a(nullableString, d);
    }

    public static boolean a(AnimationSet animationSet, AnimationSet animationSet2) {
        if (animationSet2 == null) {
            return false;
        }
        AnimationTag[] animationTagArr = animationSet.a;
        AnimationTag[] animationTagArr2 = animationSet2.a;
        for (AnimationTag animationTag : animationTagArr) {
            for (AnimationTag animationTag2 : animationTagArr2) {
                if (animationTag == animationTag2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean a(AnimationTag animationTag, AnimationSet animationSet) {
        if (animationSet == null) {
            return false;
        }
        for (AnimationTag animationTag2 : animationSet.a) {
            if (animationTag2 == animationTag) {
                return true;
            }
        }
        return false;
    }

    public static boolean b(AnimationSet animationSet, AnimationSet animationSet2) {
        if (animationSet2 == null) {
            if (animationSet == null || animationSet.b() == 0) {
                return true;
            }
            return false;
        }
        AnimationTag[] animationTagArr = animationSet.a;
        AnimationTag[] animationTagArr2 = animationSet2.a;
        int length = animationTagArr2.length;
        for (AnimationTag animationTag : animationTagArr) {
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                if (animationTag != animationTagArr2[i]) {
                    i++;
                } else {
                    z = true;
                    break;
                }
            }
            if (!z) {
                return false;
            }
        }
        return true;
    }
}
