package com.corrodinggames.rts.gameFramework.utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.ac */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/ac.class */
public final class ConfigKeyValue {
    String a;
    String b;

    public ConfigKeyValue(String str, String str2) {
        this.a = str;
        this.b = str2;
    }

    public String toString() {
        return "[" + this.a + "]" + this.b;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigKeyValue)) {
            return false;
        }
        ConfigKeyValue configKeyValue = (ConfigKeyValue) obj;
        return this.b.equals(configKeyValue.b) && this.a.equals(configKeyValue.a);
    }

    public String a() {
        return this.a;
    }

    public String b() {
        return this.b;
    }
}
