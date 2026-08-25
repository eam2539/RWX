package com.corrodinggames.rts.gameFramework.utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.ac */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/ac.class */
public final class ConfigKeyValue {

    /* JADX INFO: renamed from: a */
    String key;

    /* JADX INFO: renamed from: b */
    String value;

    public ConfigKeyValue(String str, String str2) {
        this.key = str;
        this.value = str2;
    }

    public String toString() {
        return "[" + this.key + "]" + this.value;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigKeyValue)) {
            return false;
        }
        ConfigKeyValue configKeyValue = (ConfigKeyValue) obj;
        return this.value.equals(configKeyValue.value) && this.key.equals(configKeyValue.key);
    }

    public String a() {
        return this.key;
    }

    public String b() {
        return this.value;
    }
}
