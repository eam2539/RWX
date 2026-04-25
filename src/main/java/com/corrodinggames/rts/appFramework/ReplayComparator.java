package com.corrodinggames.rts.appFramework;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/k.class */
class ReplayComparator implements Comparator<String> {

    /* JADX INFO: renamed from: a */
    Pattern datePattern = Pattern.compile(".*\\((.*)\\).*");

    ReplayComparator() {
    }

    @Override // java.util.Comparator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compare(String str, String str2) {
        Date date = parseDate(str);
        Date date2 = parseDate(str2);
        if (date == null && date2 == null) {
            return str.compareTo(str2);
        }
        if (date != null && date2 != null) {
            return date2.compareTo(date);
        }
        if (date == null && date2 != null) {
            return -1;
        }
        if (date != null && date2 == null) {
            return 1;
        }
        return 0;
    }

    /* JADX INFO: renamed from: a */
    public Date parseDate(String str) {
        Matcher matcher = this.datePattern.matcher(str);
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
                                GameEngine.isInSpace("Failed to parse date:" + strGroup);
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
