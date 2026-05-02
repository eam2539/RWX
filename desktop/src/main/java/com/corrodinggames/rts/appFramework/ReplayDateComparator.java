package com.corrodinggames.rts.appFramework;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.r */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/r.class */
class ReplayDateComparator implements Comparator<String> {
    Pattern a = Pattern.compile(".*\\((.*)\\).*");

    ReplayDateComparator() {
    }

    @Override // java.util.Comparator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compare(String str, String str2) {
        Date dateA = a(str);
        Date dateA2 = a(str2);
        if (dateA == null && dateA2 == null) {
            return str.compareTo(str2);
        }
        if (dateA != null && dateA2 != null) {
            return dateA2.compareTo(dateA);
        }
        if (dateA == null && dateA2 != null) {
            return -1;
        }
        if (dateA != null && dateA2 == null) {
            return 1;
        }
        return 0;
    }

    public Date a(String str) {
        Matcher matcher = this.a.matcher(str);
        if (matcher.matches()) {
            String strGroup = matcher.group(1);
            try {
                return new SimpleDateFormat("d MMM yyyy HH.mm.ss", Locale.ENGLISH).parse(strGroup);
            } catch (ParseException e) {
                try {
                    return new SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.ENGLISH).parse(strGroup);
                } catch (ParseException e2) {
                    try {
                        return new SimpleDateFormat("d MMM yyyy HH_mm_ss", Locale.ENGLISH).parse(strGroup);
                    } catch (ParseException e3) {
                        try {
                            return new SimpleDateFormat("d MMM yyyy HH-mm-ss", Locale.ENGLISH).parse(strGroup);
                        } catch (ParseException e4) {
                            try {
                                return new SimpleDateFormat("d MMM. yyyy HH.mm.ss", Locale.ENGLISH).parse(strGroup);
                            } catch (ParseException e5) {
                                return null;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
