package com.corrodinggames.rts.gameFramework.utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.am */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/am.class */
public class ConfigException extends RuntimeException {
    public ConfigException(String str, Throwable th) {
        super(str, th);
    }

    public ConfigException(String str) {
        super(str);
    }
}
