package com.corrodinggames.rts.gameFramework.utility;

import java.io.Serializable;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/b.class */
class CachedStackTrace implements Serializable {

    /* JADX INFO: renamed from: a */
    final String stackTrace;
    final StackTraceElement[] b;

    CachedStackTrace(String str, StackTraceElement[] stackTraceElementArr) {
        this.stackTrace = str;
        this.b = stackTraceElementArr;
    }
}
