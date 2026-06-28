package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bo */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bo.class */
public class ConfigParseException extends Exception {

    /* JADX INFO: renamed from: b */
    public String messageDetail;

    /* JADX INFO: renamed from: c */
    public String filePath;

    /* JADX INFO: renamed from: d */
    public String errorContext;

    public ConfigParseException(String str, String str2) {
        super(str);
        this.messageDetail = str2;
    }

    public ConfigParseException(String str) {
        super(str);
    }

    public ConfigParseException(String str, String str2, String str3) {
        super(str);
        this.filePath = str2;
        this.errorContext = str3;
    }

    public ConfigParseException(String str, Exception exc) {
        super(str, exc);
    }
}
