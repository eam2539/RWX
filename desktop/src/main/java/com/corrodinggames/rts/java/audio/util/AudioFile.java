package com.corrodinggames.rts.java.audio.util;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.audio.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/a/a.class */
public class AudioFile {
    protected InputStream a;
    protected File b;
    protected String c;

    public AudioFile(String str) {
        this.b = new File(str);
        this.c = this.b.getName();
    }

    public AudioFile(InputStream inputStream, String str) {
        this.a = inputStream;
        this.c = str;
        if (this.a == null) {
            throw new RuntimeException("inputStream==null");
        }
    }

    public InputStream a() {
        if (this.a != null) {
            return this.a;
        }
        try {
            return new FileInputStream(this.b);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String b() {
        String str = this.c;
        int iLastIndexOf = str.lastIndexOf(46);
        return iLastIndexOf == -1 ? VariableScope.nullOrMissingString : str.substring(iLastIndexOf + 1);
    }
}
